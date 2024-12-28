package com.btc100.btc_q.action.admin;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.entity.BQArticle;
import com.btc100.btc_q.entity.BQArticleContent;
import com.btc100.btc_q.service.BQArticleContentService;
import com.btc100.btc_q.service.BQArticleService;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.FanYiBaiduUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.google.gson.Gson;

@Controller
@RequestMapping(value = "/managerNotice")
public class Notice extends BaseAction {

	@Autowired
	private BQArticleService bqArticleService;
	@Autowired
	private BQArticleContentService bqArticleContentService;

	private final String slat = "\t\t\t\t\t\t\t";
	private final String CN = "zh_CN";
	private final String US = "en_US";
	private final String JP = "ja_JP";
	private final String KR = "ko_KR";
	private final String HK = "zh_HK";

	@RequestMapping(value = "/inpage")
	public String inpage(HttpServletRequest request, Model mo) {
		String index = request.getParameter("index");
		if (!StringUtil.isEmpty(index)) {
			if (index.equals("success")) {
				mo.addAttribute("msg", "添加成功");
			} else if (index.equals("error")){
				mo.addAttribute("msg", "添加失败");
			} else if (index.equals("success1")) {
				mo.addAttribute("msg", "修改成功");
			} else if (index.equals("error1")){
				mo.addAttribute("msg", "修改失败");
			}
		}
		// List<BQArticle> list = bqArticleService.findList(1, 5);
		// mo.addAttribute("list", list);
		PagerModel m1 = bqArticleService.readlistModel("");
		mo.addAttribute("pm", m1);
		mo.addAttribute("title", "");
		mo.addAttribute("type", 0);

		return super.url_JSP("newslist");
	}

	@RequestMapping(value = "/selectpage")
	public String selectpage(HttpServletRequest request, String title, int type, Model mo) throws UnsupportedEncodingException {
		String ss0= request.getParameter("pager.offset");
		
//		String s1 = ConvertUtil.encodeStr(title);
		if(ss0!=null){
			title = URLEncoder.encode(title, "ISO-8859-1");
			title = URLDecoder.decode(title, "GB2312");
		}
		
		PagerModel list = bqArticleService.readListModel(title, type);
		mo.addAttribute("pm", list);
		mo.addAttribute("title", title);
		mo.addAttribute("type", type);

		return super.url_JSP("newslist");
	}

	@RequestMapping(value = "/updatenews", method = RequestMethod.POST)
	public String updatenews(Model model, int id, String title, int type, String content, String descript, String keyword, String language) {

		boolean index = false;
		index = addOrUpdateNews(id, title, type, content, descript, keyword, language);
		model.addAttribute("index", index ? "success1" : "error1");
		return "redirect:/managerNotice/inpage.do";
	}

	//批量添加公告，多语种添加
	@RequestMapping(value = "/addnews", method = RequestMethod.POST)
	public String addnews(Model model, int type, String title, String content, String descript, String keyword, String enTitle, String enContent, String enDescript, String enKeyword,
							String jpTitle, String jpContent, String jpDescript, String jpKeyword, String korTitle, String korContent, String korDescript, String korKeyword,
						  String chtTitle, String chtContent, String chtDescript, String chtKeyword) {

		model.addAttribute("index", "success");

		try {

			if (!StringUtil.isEmpty(title) && !StringUtil.isEmpty(content)) {
				addOrUpdateNews(0, title, type, content, descript, keyword, CN);
			}

			if (!StringUtil.isEmpty(enTitle) && !StringUtil.isEmpty(enContent)) {
				addOrUpdateNews(0, enTitle, type, enContent, enDescript, enKeyword, US);
			}

			if (!StringUtil.isEmpty(jpTitle) && !StringUtil.isEmpty(jpContent)) {
				addOrUpdateNews(0, jpTitle, type, jpContent, jpDescript, jpKeyword, JP);
			}

			if (!StringUtil.isEmpty(korTitle) && !StringUtil.isEmpty(korContent)) {
				addOrUpdateNews(0, korTitle, type, korContent, korDescript, korKeyword, KR);
			}

			if (!StringUtil.isEmpty(chtTitle) && !StringUtil.isEmpty(chtContent)) {
				addOrUpdateNews(0, chtTitle, type, chtContent, chtDescript, chtKeyword, HK);
			}

		} catch (Exception e) {
			model.addAttribute("index", "error");
		}

		return "redirect:/managerNotice/inpage.do";
	}


	private boolean addOrUpdateNews(int id, String title, int type, String content, String descript, String keyword, String language) {
		int now = (int) DateUtil.DateToUnix(new Date());

		BQArticle article = new BQArticle();
		BQArticleContent bqcontent = new BQArticleContent();

		try {
			if (id > 0) {
				article = bqArticleService.readById(id);
				bqcontent = bqArticleContentService.readById(id);
			}
			article.setCategory_id(type);
			article.setDescription(descript);
			article.setKeyword(keyword);
			article.setTitle(title);
			article.setLanguage(language);

			bqcontent.setContent(content);
			bqcontent.setCredit_type_id(type);
			bqcontent.setDateline(now);
			bqcontent.setTitle(title);

			if (id <= 0) {
				article.setUsername("admin");
				article.setState(0);
				article.setIstop(0);
				article.setDateline(now);

				bqcontent.setF_user_id(0);
				bqcontent.setCredit_log_id(0);

				int aid = bqArticleService.addList(article);
				bqcontent.setArticle_id(aid);
				bqArticleContentService.add(bqcontent);
			} else {
				bqArticleService.update(article);
				bqArticleContentService.update(bqcontent);
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@RequestMapping(value = "/loadaddnews")
	public String loadAddNews(int newsid, Model mo) {
		if (newsid > 0) {
			BQArticleContent aa = bqArticleContentService.readById(newsid);
			BQArticle bq = bqArticleService.readById(newsid);

			mo.addAttribute("content", aa.getContent());
			mo.addAttribute("title", aa.getTitle());
			mo.addAttribute("type", aa.getCredit_type_id());
			mo.addAttribute("id", newsid);
			mo.addAttribute("language", bq.getLanguage());
			mo.addAttribute("descript", bq.getDescription());
			mo.addAttribute("keyword", bq.getKeyword());

			return super.url_JSP("newsdetails");
		}

		mo.addAttribute("id", newsid);
		return super.url_JSP("newsMoreDetais");
	}

	@RequestMapping(value = "/delnews")
	public String delNews(int newsid) {
		bqArticleService.delById(newsid);
		bqArticleContentService.del(newsid);
		return "redirect:/managerNotice/inpage.do";
	}

	@RequestMapping(value = "/topnews")
	public String topNews(int newsid, int top) {
		if (top == 0)
			top = 1;
		else
			top = 0;
		bqArticleService.updateTop(newsid, top);
		return "redirect:/managerNotice/inpage.do";
	}

	/**
	 * 中文翻译，此方法只接受中文原文本
	 * @param content
	 * @return
	 */
	@RequestMapping(value = "/transContent", method = RequestMethod.POST)
	public void transContent(HttpServletResponse response, String content) throws JSONException {
		Gson gson = new Gson();
		org.json.JSONObject json = new org.json.JSONObject();
		json.put("code", "success");
		json.put("msg", "");
		Map<String, Object> nowContent = delHTMLTagGetList(content);	//获取去除html标签的字符串集合
		List<String> oldCons = (List<String>) nowContent.get("strs");	//源文本集合

		String transContentEN = translateNews(content, FanYiBaiduUtil.ZH, FanYiBaiduUtil.EN);	//翻译之后的字符串，英文
		List<String> transConsEN = analysisContent(StringUtil.isEmpty(transContentEN) ? "" : transContentEN);	//翻译之后的字符串集合
		String contentNewEN = afterTranslationText(oldCons, transConsEN, content);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String transContentCHT = translateNews(content, FanYiBaiduUtil.ZH, FanYiBaiduUtil.CHT);
		List<String> transConsCHT = analysisContent(StringUtil.isEmpty(transContentCHT) ? "" : transContentCHT);
		String contentNewCHT = afterTranslationText(oldCons, transConsCHT, content);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String transContentKOR = translateNews(content, FanYiBaiduUtil.ZH, FanYiBaiduUtil.KOR);
		List<String> transConsKOR = analysisContent(StringUtil.isEmpty(transContentKOR) ? "" : transContentKOR);
		String contentNewKOR = afterTranslationText(oldCons, transConsKOR, content);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String transContentJP = translateNews(content, FanYiBaiduUtil.ZH, FanYiBaiduUtil.JP);
		List<String> transConsJP = analysisContent(StringUtil.isEmpty(transContentJP) ? "" : transContentJP);
		String contentNewJP = afterTranslationText(oldCons, transConsJP, content);

		Map<String, String> params = new HashMap<>();
		params.put("en", contentNewEN);
		params.put("cht", contentNewCHT);
		params.put("kor", contentNewKOR);
		params.put("jp", contentNewJP);

		json.put("data", new org.json.JSONObject(gson.toJson(params)));
		toWeb(response, json);
	}


	/**
	 * 去除html标签，此方法并不是一定准确
	 * @param htmlStr	文本
	 * @return	去除html标签并且拼接的字符串，原文本
	 */
	private Map<String, Object> delHTMLTagGetList(String htmlStr){
//		String regEx_script_prefix = "<script[^>]*?>"; 	//script前缀正则表达式
		String regEx_html="<[^>]+>"; 					//定义HTML标签的正则表达式

		String[] htmlStrs = htmlStr.split(regEx_html);
		List<String> strs = new ArrayList<>();
		StringBuilder splicing = new StringBuilder("");
		for (String s : htmlStrs) {
			if (StringUtil.isEmpty(s)) {
				continue;
			}
			strs.add(s);
			splicing.append(s + slat);
		}
		Map<String, Object> params = new HashMap<>();
		params.put("strs", strs);
		params.put("query", splicing);

		return params;
	}

	/**
	 * 字符串翻译成指定语言
	 * @param news	未翻译文本
	 * @param from	源语言
	 * @param to	目标语言
	 * @return
	 */
	private String translateNews(String news, String from, String to) {
		if (StringUtil.isEmpty(news) || StringUtil.isEmpty(to) || StringUtil.isEmpty(from)) return "";

		Map<String, Object> organizedNews = delHTMLTagGetList(news);
		String queryStr = organizedNews.get("query") == null ? "" : organizedNews.get("query").toString();
		String aftTransStr = FanYiBaiduUtil.translate(queryStr, from, to);

		JSONObject jsonObject = JSONObject.parseObject(aftTransStr);
		String transResult = jsonObject.getString("trans_result");
		JSONObject resultOne = (JSONObject) JSONObject.parseArray(transResult).get(0);
		String dst = resultOne.getString("dst");

		return dst;
	}

	/**
	 * 解析文本
	 * @param context 翻译后的文本，进行解析
	 * @return
	 */
	private List<String> analysisContent(String context) {

		if (StringUtil.isEmpty(context)) {
			return new ArrayList<>();
		}

		String[] texts = context.split(slat);
		List<String> cons = new ArrayList<>();
		for (String text : texts) {
			if (StringUtil.isEmpty(text)) {
				continue;
			}
			cons.add(text);
		}
		return cons;
	}


	/**
	 * 翻译后的文本替换源文本，格式不变
	 * @param oldParameters	源文本集合
	 * @param newParameters	目标文本集合
	 * @param oldText	源文本
	 * @return
	 */
	private String afterTranslationText(List<String> oldParameters, List<String> newParameters, String oldText) {
		if (StringUtil.isEmpty(oldText)) return "";
		StringBuilder newText = new StringBuilder(oldText);

		for (int i = 0; i < oldParameters.size(); i++) {
			int index = newText.indexOf(oldParameters.get(i));	//第一次出现的字符串索引
			int strLenth = index + oldParameters.get(i).length();	//字符串长度
			if (newParameters.size() < i + 1) return newText.toString();
			newText = newText.replace(index, strLenth, newParameters.get(i));
		}

		return newText.toString();
	}

}
