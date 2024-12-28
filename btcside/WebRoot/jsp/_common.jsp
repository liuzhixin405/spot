<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>"/>

<script type="text/javascript">
	var isLogin = ${sessionScope.userinfo != null };
	var locale = "${locale}";
	if ("" == locale) {
		locale = "en_US";
	}
</script>