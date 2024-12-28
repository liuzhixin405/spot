<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.btc100.btc_q.common.UrlConfig"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
	String actionPath = scheme + "://" + request.getServerName() + port + "/";
	//actionPath = UrlConfig.getValue("webRoot");
	String FileUpload = actionPath + "common/";
	String jspPath = actionPath + "qtwner/up/down/togo/";
%>
