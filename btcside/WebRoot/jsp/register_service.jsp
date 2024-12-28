<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		  <title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.footer.serviceAgreement"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->

  <div class="container-trade-data">
  	<jsp:include page="${locale}/register_serviceContent.jsp" />  
  </div>
  	<jsp:include page="_footer.jsp"></jsp:include>

	</body>
</html>
