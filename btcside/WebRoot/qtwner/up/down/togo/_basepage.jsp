<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.btc100.btc_q.common.UrlConfig"%>
<%
	String path = request.getContextPath();
	String scheme = request.getScheme();
	String scheme1 = request.getHeader("X-Forwarded-Proto");
	if (scheme1 != null && scheme1.equals("https")) {
		scheme = "https";
	}
	String port = "";
	if (scheme.equals("http")) {
		port = ":" + request.getServerPort();
	}
	String basePath = scheme + "://" + request.getServerName() + port + "/";
	String strPath = basePath + "qtwner/up/down/togo/";
%>
