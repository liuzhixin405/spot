package com.btc100.btc_q.intercept;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.btc100.btc_q.util.Utils;

/**
 * 分别实现预处理、后处理（调用了Service并返回ModelAndView，但未进行页面渲染）、返回处理（已经渲染了页面） 
 * 在preHandle中，可以进行编码、安全控制等处理； 
 * 在postHandle中，有机会修改ModelAndView； 
 * 在afterCompletion中，可以根据ex是否为null判断是否发生了异常，进行日志记录。 
 * @author Administrator 
 *
 */
public class CommonInterceptor extends HandlerInterceptorAdapter {
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		request.setAttribute("locale", Utils.getLocale(request));
		request.setAttribute("actionName", initCurrentPath(request));
		request.setAttribute("homeName", initCurrentHomePath(request));
		
		return true;
	}
	
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
	
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, null);
	}
	
	/**
	 * 初始化访问路径，获取trade.do的trade
	 * 
	 * @param request
	 * @return
	 */
	private String initCurrentPath(HttpServletRequest request) {
		String actionName = request.getRequestURI(); // /btcq/trade/trade.do
		actionName = actionName.substring(actionName.lastIndexOf("/") + 1);
		actionName = actionName.replace(".do", "");
		return actionName;
	}

	/**
	 * 初始化访问路径，获取/btcq/trade的trade
	 * 
	 * @param request
	 * @return
	 */
	private String initCurrentHomePath(HttpServletRequest request) {
		String homeName = request.getRequestURI(); // /btcq/trade/trade.do
		if(homeName.endsWith("/"))
			return "home";
		homeName = homeName.substring(0, homeName.lastIndexOf("/"));
		homeName = homeName.substring(homeName.lastIndexOf("/") + 1);
		return homeName;
	}

}
