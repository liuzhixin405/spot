package com.btc100.btc_q.intercept;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.btc100.btc_q.util.StringUtil;

public class OTCInterceptor extends HandlerInterceptorAdapter {
	
	public static boolean isLocalAddress(String ipAddress) {
		if (StringUtil.isEmpty(ipAddress)) {
			return true;
		}
		
		ipAddress += ",";
		String[] ips = ipAddress.split(",");
    	for (int i = 0; i < ips.length; i++) {
    		ipAddress = ips[0];
    		if (!StringUtil.isEmpty(ipAddress) 
            		&& (!ipAddress.equals("127.0.0.1") && !ipAddress.equals("0:0:0:0:0:0:0:1"))) {
            	return false;
            }
    	}
		
		return true;
	}
	
	public static boolean isLocalRequest(HttpServletRequest request) {
		
		boolean haveAddress = false;
        String ipAddress = request.getHeader("x-forwarded-for");
        if (!StringUtil.isEmpty(ipAddress)) haveAddress = true;
        if (!isLocalAddress(ipAddress)) return false;
        
        ipAddress = request.getHeader("Proxy-Client-IP");
        if (!StringUtil.isEmpty(ipAddress)) haveAddress = true;
        if (!isLocalAddress(ipAddress)) return false;
        
        ipAddress = request.getHeader("WL-Proxy-Client-IP");  
        if (!StringUtil.isEmpty(ipAddress)) haveAddress = true;
        if (!isLocalAddress(ipAddress)) return false;
        
        ipAddress = request.getRemoteAddr();
        if (!StringUtil.isEmpty(ipAddress)) haveAddress = true;
        if (!isLocalAddress(ipAddress)) return false;
        
        return haveAddress;
    }
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return isLocalRequest(request);
	}
	
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (!isLocalRequest(request)) {
			
		} else {
			super.postHandle(request, response, handler, modelAndView);
		}
	}
	
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, null);
	}
}
