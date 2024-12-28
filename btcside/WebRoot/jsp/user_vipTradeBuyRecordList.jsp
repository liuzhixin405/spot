<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.memberLevelBuyAmount.vipTradeBuyRecord"/> </title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>">
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.memberLevelBuyAmount.vipTradeBuyRecord"/> 
						</h2>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.index.recordTime"/>
									</th>
									<th>
										<fmt:message key="msg.memberLevelBuyAmount.spend"/>(Btcside)
									</th>
									<th>
										<fmt:message key="msg.memberLevelBuyAmount.originalLevel"/>
									</th>
									<th>
										<fmt:message key="msg.memberLevelBuyAmount.newLevel"/>
									</th>
								</tr>
							</thead>
							<tbody>
			                    <c:forEach var="li" items="${pm.datas}" varStatus="st">
									<tr>
										<td style="width: 25%;">
											<span><ut:formatDate dateline="${li.dateline}"></ut:formatDate></span>
										</td>
										<td style="width: 25%;">
											<fmt:formatNumber value="${li.change_amount}" pattern="#0.00000000"></fmt:formatNumber>
										</td>
										<td style="width: 25%;">
											${li.before_level_name}
										</td>
										<td style="width: 25%;">
											${li.after_level_name}
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<ul class="pagination">
	
							<pg:pager url="center/vipTradeBuyRecordList.do" items="${pm.total}"
								maxIndexPages="10" maxPageItems="${pm.maxitems}"
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
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
	</body>

</html>