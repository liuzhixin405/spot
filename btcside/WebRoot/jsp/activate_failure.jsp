<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.registerSuccess.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->
		<div class="container-bg-none">
			<div class="login-box">
				<fmt:message key="msg.pg.registerSuccess.activeFailed"/>！${message }
			</div>
		</div>
		<div class="clear"></div>

		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
	
</html>
