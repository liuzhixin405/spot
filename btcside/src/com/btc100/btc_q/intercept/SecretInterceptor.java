package com.btc100.btc_q.intercept;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.util.MD5Service;

/**
 * 拦截账户管理
 */
public class SecretInterceptor extends HandlerInterceptorAdapter {

	Logger logger = Logger.getLogger(SecretInterceptor.class);
	
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
		String surl = request.getRequestURL().toString();
		String password = request.getParameter("password");
		
		password = password == null ? "" : password;
		
		BQSurecome bqSure = (BQSurecome) request.getSession().getAttribute("owner");
		
		if (surl.contains("/managerFinancial/") && !(MD5Service.encryptString(bqSure.getUser_salt() + MD5Service.encryptString(password)).equals(bqSure.getUser_thpwd()))) {
			request.getRequestDispatcher("/qtwner/blue/btcqserver/go/jsp/secretpassword.jsp").forward(request, response);
			return false;
		}

		return true;
	}
}
