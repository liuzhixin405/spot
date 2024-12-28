<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.title.news"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont" style="background: #FFFFFF;box-shadow: 0px 2px 6px 1px rgba(148,171,192,0.1);">
			<div class="main-center-box">
				<div class="block-title">
					<img alt="title" src="<%=basePath%>common/images/oldNewsTitle.png">
				</div>
				<div>
					<ul class="news-list-wrapp">
						<c:forEach var="li" items="${newslist.datas}" varStatus="st">
							<li>
								<c:if test="${locale == 'zh_CN'}">
					            	<c:out value="${li.zh_content}"></c:out>
					            </c:if>
					            <c:if test="${locale == 'en_US'}">
					            	<c:out value="${li.en_content}"></c:out>
					            </c:if>
					            <c:if test="${locale == 'ja_JP'}">
					            	<c:out value="${li.ja_content}"></c:out>
					            </c:if>
					            <c:if test="${locale == 'ko_KR'}">
					            	<c:out value="${li.ko_content}"></c:out>
					            </c:if>
					            <c:if test="${locale == 'zh_HK'}">
					            	<c:out value="${li.hk_content}"></c:out>
					            </c:if>
					            <br>
								<span class=""><ut:formatDate dateline="${li.dateline}"></ut:formatDate></span>
							</li>
						</c:forEach>
					</ul>
				</div>
				<ul class="pagination">
					<pg:pager url="news/expressNews.do" items="${newslist.total}"
						maxIndexPages="10" maxPageItems="${newslist.maxitems}"
						export="currentPageNumber=pageNumber">
						<pg:prev>
						 <li class="disabled">
							<a href="<%=basePath%>${pageUrl}"
								mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>
								</li>
						</pg:prev>
						<pg:pages>
							<c:choose>
								<c:when test="${currentPageNumber eq pageNumber }">
									<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
								</c:when>
								<c:otherwise>
								<li>
									<a href="<%=basePath%>${pageUrl }"
										mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
								</c:otherwise>
							</c:choose>
						</pg:pages>
						<pg:next>
						<li>
							<a href="<%=basePath%>${pageUrl }"
								mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a>
								</li>
						</pg:next>
					</pg:pager>
				</ul>
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>
