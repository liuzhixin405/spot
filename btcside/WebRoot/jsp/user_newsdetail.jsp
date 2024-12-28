<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.index.newsInfo"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>" />
			<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont">
			<jsp:include page="_help_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="info-box">
					<div class="news-deatil">
						<h1>
							${content.title}
						</h1>
						<div class="news-info">
							<span><fmt:message key="msg.pg.selfHelp.from"/>： <strong>Btcside</strong> </span>
							<span><fmt:message key="msg.pg.selfHelp.pubdate"/>： <em><ut:formatDate
										dateline="${content.dateline}"></ut:formatDate>
							</em> </span>
						</div>
						<div class="news-text">
							<p>
								${content.content }
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>
