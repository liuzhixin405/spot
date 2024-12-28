package com.btc100.btc_q.intercept;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.aliyuncs.utils.StringUtils;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.util.Utils;

/**
 * 分别实现预处理、后处理（调用了Service并返回ModelAndView，但未进行页面渲染）、返回处理（已经渲染了页面） 
 * 在preHandle中，可以进行编码、安全控制等处理； 
 * 在postHandle中，有机会修改ModelAndView； 
 * 在afterCompletion中，可以根据ex是否为null判断是否发生了异常，进行日志记录。 
 * @author Administrator 
 *
 */
public class ApiInterceptor extends HandlerInterceptorAdapter {
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Locale locale = (Locale) request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		if (null == locale) {
			locale = new Locale("zh", "CN");
			String language = request.getParameter("language");
			if (!StringUtils.isEmpty(language)) {
				String[] langs = language.split("_");
				if (langs.length > 1 && langs[1].equalsIgnoreCase("tw")) langs[1] = "HK"; 
				locale = new Locale(langs[0], langs.length > 1 ? langs[1] : "");
			}
			Contents.Local.LOCALE.set(locale);
			
			request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
			request.setAttribute("locale", Utils.getLocale(request));
		} else {
			Contents.Local.LOCALE.set(locale);
		}
		
		return true;
	}
	
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
	
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, null);
	}
}
