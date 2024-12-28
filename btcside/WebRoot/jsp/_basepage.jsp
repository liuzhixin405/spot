<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.btc100.btc_q.common.UrlConfig"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
	String basePath = scheme + "://" + request.getServerName() + port + "/";
	String serverName = request.getServerName();
	if (serverName == null || serverName.isEmpty()) {
		serverName = "btcside.com";
	} else {
		serverName = serverName.replace("www.", "");
	}
	String otcPort = UrlConfig.getValue("otcPort");
	if (!otcPort.equals("")) {
		otcPort = ":" + otcPort;
	}
	String otcPath = scheme + "://" + request.getServerName() + otcPort + "/otc/";
	// basePath = UrlConfig.getValue("webRoot");
	String homePath = basePath + "home/";
	String tradePath = basePath + "trade/";
	if (request.getSession().getAttribute("homeName") == "vtrade") {
		tradePath = basePath + "vtrade/";
	} else if (request.getSession().getAttribute("homeName") == "dtrade") {
		tradePath = basePath + "dtrade/";
	}
	String spotTradePath = basePath + "spotTrade/";
	String activityPath = basePath + "activity/";
	String userPath = basePath + "center/";
	String subUserPath = basePath + "userSubAccount/";
	String suggestPath = basePath + "suggest/";
	String orderPath = basePath + "order/";
	String helpPath = basePath + "help/";
	String marketPath = basePath + "market/";
	String newsPath = basePath + "news/";
	String loginPath = basePath + "login/";
	String r = "r=" + System.currentTimeMillis();
	String rjs = "r=459997099";
	String rpic = "r=459997079";
%>
