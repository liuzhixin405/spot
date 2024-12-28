<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-结算记录</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
	</head>

	<body>

		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont">
			<div class="left-menu">
				<c:if test="${issz}"><jsp:include page="_trade_left_sz.jsp"></jsp:include></c:if>
				<c:if test="${!issz}"><jsp:include page="_trade_left.jsp"></jsp:include></c:if>
			</div>
			<div class="main-rt-box">
				<!-- 盈利结算记录 -->
				<div class="main-wrap">
					<div class="tab">
							<a href="<%=basePath%>trade/settleSys.do">系统交割记录</a>
							<a href="<%=basePath%>trade/settleSelf.do">个人交割记录</a>
					<a href="<%=basePath%>trade/<c:if test="${issz}">sz</c:if>settle.do" class="current">盈利结算记录</a>
							
					</div>

					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										结算时间
									</th>
									<th>
										合约名称
									</th>
									<th>
										结算合约余额
									</th>
									<th>
										系统亏损均摊比例
									</th>
									<th>
										均摊额
									</th>
									<th>
										实际得到
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="l" items="${pm.datas}" varStatus="st">
									<tr>
										<td>
											<ut:formatDate dateline="${l.f_settle_time}"></ut:formatDate>
										</td>
										<td>
											${l.transaction_code}
										</td>
										<td>
											<fmt:formatNumber value="${l.f_settle_amount+l.f_settle_apportion}" pattern="#.########"></fmt:formatNumber>
											USDT
										</td>
										<td>
											<fmt:formatNumber type="percent" pattern="#.##%" value="${l.f_settle_rate}"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${l.f_settle_apportion}" pattern="#.########"></fmt:formatNumber>
											USDT
										</td>
										<td>
											<fmt:formatNumber value="${l.f_settle_amount}" pattern="#.########"></fmt:formatNumber>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<ul class="pagination">
							<c:if test="${issz}">
								<pg:pager url="trade/szsettle.do" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}"
									export="currentPageNumber=pageNumber">
	
									<pg:prev>
										<li class="disabled">
											<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}">上一页</a>
										</li>
									</pg:prev>
	
									<pg:pages>
										<c:choose>
											<c:when test="${currentPageNumber eq pageNumber }">
												<li class="active">
													<a href="javascript:void(0)">${pageNumber }</a>
												</li>
											</c:when>
											<c:otherwise>
												<li>
													<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a>
												</li>
											</c:otherwise>
										</c:choose>
									</pg:pages>
	
									<pg:next>
										<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">下一页</a>
									</pg:next>
								</pg:pager>
							</c:if>
							<c:if test="${!issz}">
								<pg:pager url="trade/settle.do" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}"
									export="currentPageNumber=pageNumber">
	
									<pg:prev>
										<li class="disabled">
											<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}">上一页</a>
										</li>
									</pg:prev>
	
									<pg:pages>
										<c:choose>
											<c:when test="${currentPageNumber eq pageNumber }">
												<li class="active">
													<a href="javascript:void(0)">${pageNumber }</a>
												</li>
											</c:when>
											<c:otherwise>
												<li>
													<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a>
												</li>
											</c:otherwise>
										</c:choose>
									</pg:pages>
	
									<pg:next>
										<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">下一页</a>
									</pg:next>
								</pg:pager>
							</c:if>
						</ul>
					</div>
				</div>
			</div>
		</div>

		<jsp:include page="_footer.jsp"></jsp:include>

	</body>

</html>
