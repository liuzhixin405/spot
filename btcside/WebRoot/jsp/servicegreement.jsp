<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.register.userRegister"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>" />
			<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container">
			<jsp:include page="_help_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<jsp:include page="${locale}/register_serviceContent.jsp" />
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>
