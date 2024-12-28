<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.assetCaptia"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>">
			<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userCenter.assetCaptia"/>
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
										<fmt:message key="msg.pg.common.currency"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.methods"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.originVal"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.increaseOrPlus"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.curtVal"/>
									</th>
								</tr>
							</thead>
							<tbody>
									<c:forEach var="li" items="${pm.datas}" varStatus="st">
									<tr>
										<td>
										<span><ut:formatDate dateline="${li.dateline}"></ut:formatDate></span>
										</td>
										<td>${coins[li.currency_type_id]}</td>
										<td>
											<%--<c:choose>
														<c:when test="${li.operation==0}">其它</c:when>
														<c:when test="${li.operation==1}">充值</c:when>
														<c:when test="${li.operation==2}">提现</c:when>
														<c:when test="${li.operation==3}">交易</c:when>
														<c:when test="${li.operation==4}">追加保证金</c:when>
														<c:when test="${li.operation==5}">购买积分</c:when>
													</c:choose>
										--%>
											${li.title}
										</td>
										<td>
											
											<fmt:formatNumber value="${li.current_amount}" pattern="#0.00000000"></fmt:formatNumber>
										</td>
										<td>
										${li.direction == 1 ? '+' : '-'}<fmt:formatNumber value="${li.change_amount}" pattern="#0.00000000"></fmt:formatNumber>
										
										</td>
										<td>
										<fmt:formatNumber value="${li.lastest_amount}" pattern="#0.00000000"></fmt:formatNumber>
										
										</td>
										</tr>
									</c:forEach>

								
							</tbody>
						</table>
						<ul class="pagination">

							<pg:pager url="center/money.do" items="${pm.total}"
								maxIndexPages="10" maxPageItems="${pm.maxitems}"
								export="currentPageNumber=pageNumber">							
							
									<pg:prev> <li class="disabled">
										<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>	</li>
									</pg:prev>
							
							
									<pg:pages>
										<c:choose>
											<c:when test="${currentPageNumber eq pageNumber }">
												<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
											</c:when>
											<c:otherwise><li>
												<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
											</c:otherwise>
										</c:choose>
									</pg:pages>
									<pg:next>
									<li><a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a></li>
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