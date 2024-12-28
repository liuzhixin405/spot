package com.btc100.btc_q.intercept;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.btc100.btc_q.common.DATA_SessionKeys;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.util.Utils;

/**
 * 
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, null);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		request.setAttribute("locale", Utils.getLocale(request));
		
		String sitePart = Utils.getWebRoot(request);
		Object sName = request.getSession().getAttribute(DATA_SessionKeys.USERINFO);
		BQMember user = (BQMember)sName;
		if (sName == null) {
			response.sendRedirect(sitePart + "login/login.do");
			return false;
		}
		
		if (user.getOpen_invite_management() == 1) {
			request.setAttribute("management", true);
		}
		request.setAttribute("isSub", user.getIs_sub_user());
		
		return true;
	}
}
