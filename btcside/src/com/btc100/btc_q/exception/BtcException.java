package com.btc100.btc_q.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class BtcException implements HandlerExceptionResolver {

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception ex) {
		// TODO Auto-generated method stub
		if (ex != null) {
			System.out.println("========异常拦截========");
			ex.printStackTrace();
			String msg = "系统繁忙, 请稍后重试! ";
			String info = ex.getMessage();

			if (ex.getClass() != null) {
				if ("com.btc100.btc_q.exception.ServiceException".equalsIgnoreCase(ex.getClass().getName())) {
					if (info != null && !info.isEmpty())
						msg = info;
				}
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", 2);
			map.put("msg", msg);
			map.put("ref", "javascript:history.go(-1);");
			map.put("info", ex.toString() + "<br /><br />===Message:" + info);

			return new ModelAndView("/error/SystemInfo", map);
		}

		return null;
	}

}
