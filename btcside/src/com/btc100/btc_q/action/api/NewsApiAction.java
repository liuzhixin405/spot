package com.btc100.btc_q.action.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQArticle;
import com.btc100.btc_q.entity.BQArticleContent;
import com.btc100.btc_q.entity.BQExpressNews;
import com.btc100.btc_q.service.BQArticleContentService;
import com.btc100.btc_q.service.BQArticleService;
import com.btc100.btc_q.service.BQExpressNewsService;
import com.btc100.btc_q.util.StringUtil;
import com.google.gson.Gson;

/**
 * 公告、信息披露、帮助API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/newsApi")
public class NewsApiAction extends BaseApiAction {

	Logger logger = Logger.getLogger(NewsApiAction.class);
	
	@Autowired
	private BQArticleService bqArticleService;
	@Autowired
	private BQArticleContentService bqArticleContentService;
	@Autowired
	private BQExpressNewsService bqExpressNewsService;
	
	/**
	 * 公告资讯列表接口
	 * categoryId (1:站内公告，2：信息披露，3：帮助，4：新闻资讯)
	 * 
	 */
	@RequestMapping("/list")
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		String categoryIdStr = request.getParameter("categoryId");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		int categoryId = Convert.toInt(categoryIdStr, -1);
		if (categoryId < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，参数categoryId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size < 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		int nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		JSONArray array = new JSONArray();
		
		List<BQArticle> list = bqArticleService.readByCategoryAndTime(categoryId, nFrom, direct, start, end, size);
		for (BQArticle article : list) {
			Map<String,Object> newsMap = new HashMap<String, Object>();
			newsMap.put("article_id", article.getArticle_id()); // 记录id
			newsMap.put("category_id", article.getCategory_id()); // 记录id
			newsMap.put("title", article.getTitle()); // 标题
			newsMap.put("dateline", article.getDateline()); // 时间
			newsMap.put("description", article.getDescription()); // 描述
			newsMap.put("keyword", article.getKeyword()); // 关键字
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(newsMap)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}

	/**
	 * 获取公告资讯详情接口(站内公告、新闻资讯、信息披露、帮助)
	 * 
	 */
	@RequestMapping("/detail")
	public String detail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String id = request.getParameter("articleId");

		int articleId = Convert.toInt(id, -1);
		if (articleId < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，参数articleId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQArticleContent content = bqArticleContentService.readById(Integer.parseInt(id));
		Gson gson = new Gson();
		Map<String,Object> newsMap = new HashMap<String, Object>();
		newsMap.put("title", content.getTitle());
		newsMap.put("content", content.getContent());
		newsMap.put("dateline", content.getDateline());
		userJson.put("data", new JSONObject(gson.toJson(newsMap)));
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 获取快讯新闻列表
	 * 
	 */
	@RequestMapping("/expressNewsList")
	public String expressNewsList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		
		if (size < 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		JSONArray array = new JSONArray();
		
		List<BQExpressNews> list = bqExpressNewsService.readByTime(nFrom, direct, start, end, size);
		for (BQExpressNews news : list) {
			Map<String,Object> newsMap = new HashMap<String, Object>();
			newsMap.put("recordId", news.getId()); // 记录id
			newsMap.put("content", news.getZh_content()); // 内容
			newsMap.put("dateline", news.getDateline()); // 时间
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(newsMap)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
}
