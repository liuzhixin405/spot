<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.selfHelp.riskEduate"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>" />
			<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
			<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont">

			<jsp:include page="_help_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<jsp:include page="${locale}/user_safeContent.jsp" />	
			</div>
		</div>

 <jsp:include page="_footer.jsp"></jsp:include>
 <script type="text/javascript">
			$(document).ready(function() {
				//隐藏所有消息主体内容"div.help-text"
					$('.freedom-broker h2').click(function() {
						$(this).toggleClass('bg').next('.help-text').slideToggle(300);
					});

				});
		</script>
	</body>

</html>
