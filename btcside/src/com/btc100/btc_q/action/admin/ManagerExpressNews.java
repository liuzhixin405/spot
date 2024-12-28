package com.btc100.btc_q.action.admin;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQExpressNews;
import com.btc100.btc_q.service.BQExpressNewsService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerExpressNews")
public class ManagerExpressNews extends BaseAction {

	@Autowired
	private BQExpressNewsService bqExpressNewsService;

	@RequestMapping(value = "/loadNews")
	public String loadNews(Model mo) {
		PagerModel pm = bqExpressNewsService.readModel();
		mo.addAttribute("pm", pm);

		return super.url_JSP("expressNewsList");
	}

	@RequestMapping(value = "/loadAddNews")
	public String loadAddNews(String id, Model mo) {
		int newsId = ConvertUtil.toInt(id, 0);
		if (newsId > 0) {
			BQExpressNews news = bqExpressNewsService.readById(newsId);
			mo.addAttribute("news", news);
		}

		return super.url_JSP("expressNewsDetails");
	}

	@RequestMapping(value = "/addNews", method = RequestMethod.POST)
	public String addNews(Model mo, String id, String zhContent, String enContent, String jaContent, String koContent, String hkContent) {

		if (zhContent == null || zhContent.isEmpty()) {
			return super.errorPage(mo, "中文内容不能为空");
		}

		if (enContent == null || enContent.isEmpty()) {
			return super.errorPage(mo, "英文内容不能为空");
		}

		if (jaContent == null || jaContent.isEmpty()) {
			return super.errorPage(mo, "日文内容不能为空");
		}
		
		if (koContent == null || koContent.isEmpty()) {
			return super.errorPage(mo, "韩文内容不能为空");
		}
		
		if (hkContent == null || hkContent.isEmpty()) {
			return super.errorPage(mo, "繁体内容不能为空");
		}

		long now = (long) DateUtil.DateToUnix(new Date());
		BQExpressNews news = new BQExpressNews();

		int newsId = ConvertUtil.toInt(id, 0);
		if (newsId > 0) {
			news = bqExpressNewsService.readById(newsId);
			news.setEn_content(enContent);
			news.setZh_content(zhContent);
			news.setJa_content(jaContent);
			news.setKo_content(koContent);
			news.setHk_content(hkContent);
			bqExpressNewsService.update(news);
		} else {
			news.setDateline(now);
			news.setEn_content(enContent);
			news.setZh_content(zhContent);
			news.setJa_content(jaContent);
			news.setKo_content(koContent);
			news.setHk_content(hkContent);
			bqExpressNewsService.add(news);
		}

		return "redirect:/managerExpressNews/loadNews.do";
	}

	@RequestMapping(value = "/deleteNews")
	public String deleteNews(String id) {
		int newsId = ConvertUtil.toInt(id, 0);
		if (newsId > 0) {
			bqExpressNewsService.delById(newsId);
		}
		return "redirect:/managerExpressNews/loadNews.do";
	}

}
