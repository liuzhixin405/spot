package com.btc100.btc_q.action.client;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQArticleContent;
import com.btc100.btc_q.service.BQArticleContentService;
import com.btc100.btc_q.service.BQArticleService;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/news")
public class NewsAction extends BaseAction {
	
	@Autowired
	private BQArticleService bqArticleService;
	@Autowired
	private BQArticleContentService bqArticleContentService;

	@RequestMapping("/notice")
	public String notice(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		PagerModel pm = bqArticleService.readNewsByCategoryId(1);
		model.addAttribute("newslist", pm);
		return "jsp/user_notice";
	}

	@RequestMapping("/noticedo")
	public String noticedo(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		String id = request.getParameter("id");
		BQArticleContent bc = bqArticleContentService.readById(Integer.parseInt(id));
		model.addAttribute("content", bc);
		return "jsp/user_newsdetail";
	}

	@RequestMapping("/about")
	public String about(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		PagerModel pm = bqArticleService.readNewsByCategoryId(2);
		model.addAttribute("newslist", pm);
		return "jsp/user_about";
	}

	@RequestMapping("/aboutdo")
	public String aboutdo(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		String id = request.getParameter("id");
//		if ("44".equals(id)) {
//			List<BQMemberLevel> levelRules = bqMemberLevelService.findAll();// 等级规则制度
//			model.addAttribute("levelRles", levelRules);
//			return "jsp/feed";
//		}

		BQArticleContent bc = bqArticleContentService.readById(Integer.parseInt(id));
		model.addAttribute("content", bc);
		return "jsp/user_newsdetail";
	}
	
	@RequestMapping("/other")
	public String help(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		PagerModel pm = bqArticleService.readNewsByCategoryId(3);
		model.addAttribute("newslist", pm);
		return "jsp/user_other";
	}

	@RequestMapping("/otherdo")
	public String otherdo(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		String id = request.getParameter("id");
		BQArticleContent bc = bqArticleContentService.readById(Integer.parseInt(id));
		model.addAttribute("content", bc);
		return "jsp/user_newsdetail";
	}
	
	@RequestMapping("/news")
	public String news(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		PagerModel pm = bqArticleService.readNewsByCategoryId(4);
		model.addAttribute("newslist", pm);
		return "jsp/user_news";
	}
	
	@RequestMapping("/newsdo")
	public String newsdo(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		String id = request.getParameter("id");
		BQArticleContent bc = bqArticleContentService.readById(Integer.parseInt(id));
		model.addAttribute("content", bc);
		return "jsp/user_newsdetail";
	}
}
