package com.btc100.btc_q.common;

public class DATA_SessionKeys {

	/**
	 * 重新发送邮件在session中存的
	 */
	public final static String REGISTER_EMAIL_STRING = "re_send_emailcontent";

	/**
	 * 用户在Session中存的Email
	 */
	public final static String REGISTER_EMAIL = "re_send_email";

	/**
	 * 注册验证码
	 */
	public final static String REGISTER_VALIDCODE = "validCode";

	/**
	 * 用户登录后的用户
	 */
	public final static String USERINFO = "userinfo";
	
	/** 找回登录密码重新验证邮箱 */
	public static final String FIND_LOGINPWD = "find_loginpwd";
	/** 找回登录密码重新验证邮箱_的邮箱号 */
	public static final String FIND_LOGINPWD_EMAIL = "find_loginpwd_email";
	
}
