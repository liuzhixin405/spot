package com.btc100.base;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  1. 设置国际化Cookie   bind_modifyCookie(request, response, "2");
 *  2. 获取相应配置文件的val  String msg = getMessageValue(request, "welcome");
 * @author Administrator
 *
 */
public class CookieHelper {

	ResourceBundle mbundle = null;

	/**
	 * 获取Cookie
	 * 
	 * @param request
	 * @param skey
	 * @return null:不存在此Cookie
	 */
	protected Cookie bind_findCookie(HttpServletRequest request, String skey) {
		for (Cookie cc : request.getCookies()) {
			if (cc.getName().equals(skey)) {
				return cc;
			}
		}
		return null;
	}

	/**
	 * 操作Cookie
	 * 
	 * @param request
	 * @param response
	 * @param value
	 *            value值
	 */
	protected void bind_modifyCookie(HttpServletRequest request, HttpServletResponse response, String value) {
		String key = "language";
		Cookie cookie = bind_findCookie(request, key);
		if (cookie == null) { // 不存在，添加Cookie
			Cookie co = new Cookie(key, value);
			co.setMaxAge(2592000); // 30*24*60*60 = 一个月
			co.setPath("/"); // 可在同一应用服务器内共享
			response.addCookie(co);
			return;
		}
		if (cookie.getValue().equals(value)) { // Cookie已存在，则修改
			cookie.setValue(value);
			response.addCookie(cookie);
		}
	}

	/**
	 * 获取Cookie Value值
	 * 
	 * @param skey
	 *            Cookie Key值
	 * @return
	 */
	protected String bind_findCookieVal(HttpServletRequest request, String skey) {
		String currentLanguage = "";
		for (Cookie cc : request.getCookies()) {
			if (cc.getName().equals(skey)) {
				currentLanguage = cc.getValue();
				break;
			}
		}
		return currentLanguage;
	}

	/**
	 * 加载语言国际化
	 * @param request
	 * @param key
	 */
	protected void bind_findLanguage(HttpServletRequest request, String key) {
		String currentLanguage = bind_findCookieVal(request, key);
		// 设置语言，汉语1.英语2
		Locale pLocale = Locale.CHINESE;
		if (currentLanguage.equals("1"))
			pLocale = Locale.CHINESE;
		else if (currentLanguage.equals("2"))
			pLocale = Locale.US;

		mbundle = ResourceBundle.getBundle("i18n/messages", pLocale);
	}

	/**
	 * 通过国际化语言获取当前显示的值
	 * 
	 * @param msgVal
	 *            messages.properties文件的key值
	 * @return
	 */
	protected String getMessageValue(HttpServletRequest request, String msgVal) {
		if (mbundle == null)
			bind_findLanguage(request, "language");
		if (mbundle != null)
			return mbundle.getString(msgVal);
		else
			return "";
	}

}
