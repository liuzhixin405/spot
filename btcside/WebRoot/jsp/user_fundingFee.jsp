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
						<h2><fmt:message key="msg.pg.userCenter.fundingFee"/></h2>
						<form action="<%=basePath%>center/fundingFee.do" name="feeRateForm">
							<div class="selected lt" style="margin-left: 25px; margin-top: 8px;">
								<select name="partition" id="partition" onchange="javascript:document.feeRateForm.submit();">
									<c:forEach var="ta" items="${partitions}">
										<c:if test="${ta.part eq partition}">
											<option value="${ta.part}" selected="selected">
												${fn:toUpperCase(ta.part)} <fmt:message key="msg.pg.index.contract"/>
											</option>
										</c:if>
										<c:if test="${ta.part ne partition}">
											<option value="${ta.part}">
												${fn:toUpperCase(ta.part)} <fmt:message key="msg.pg.index.contract"/>
											</option>
										</c:if>
									</c:forEach>
								</select>
							</div>
							<div class="selected lt" style="margin-left: 25px; margin-top: 8px;">
								<select name="code" id="code" onchange="javascript:document.feeRateForm.submit();">
									<c:forEach var="ta" items="${settings}">
										<c:if test="${ta.f_code eq code}">
											<option value="${ta.f_code}" selected="selected">
													${fn:toUpperCase(ta.f_code)} <fmt:message key="msg.pg.index.contract"/>
												</option>
										</c:if>
										<c:if test="${ta.f_code ne code}">
											<option value="${ta.f_code}">
													${fn:toUpperCase(ta.f_code)} <fmt:message key="msg.pg.index.contract"/>
												</option>
										</c:if>
									</c:forEach>
								</select>
							</div>
						</form>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th><fmt:message key="msg.pg.index.recordTime"/></th>
									<th><fmt:message key="msg.pg.userCenter.nameContract"/></th>
									<th><fmt:message key="msg.pg.userCenter.holdDirect"/></th>
									<th><fmt:message key="msg.pg.userCenter.holdCount"/></th>
									<th><fmt:message key="msg.pg.userCenter.currPrice"/>${partition.toUpperCase()}<fmt:message key="msg.pg.userCenter.currPriceUnit"/></th>
									<th><fmt:message key="msg.pg.userCenter.posValue"/>(${partition.toUpperCase()})</th> 
									<th><fmt:message key="msg.pg.feeRate.fundingRate"/></th>
									<th><fmt:message key="msg.pg.userCenter.Fundfee"/>(${partition.toUpperCase()})</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="li" items="${pm.datas}" varStatus="st">
								<tr>
									<td><span><ut:formatDate dateline="${li.dateline}"></ut:formatDate></span></td>
									<td>${li.f_code.toUpperCase()}</td>
									<td>
										<c:if test="${li.trans_type_id == 1}"><span class="buy"><fmt:message key="msg.pg.trade.much"/></span></c:if>
										<c:if test="${li.trans_type_id == 2}"><span class="sell"><fmt:message key="msg.pg.trade.guessEmpty"/></span></c:if>
									</td>
									<td>${li.hold_count.doubleValue()}</td>
									<td>${li.curr_price.doubleValue()}</td>
									<td>${li.position_value.doubleValue()}</td>
									<td><fmt:formatNumber value="${li.fee_rate.doubleValue() * 100}" pattern="#0.0000"></fmt:formatNumber>%</td>
									<td>${li.funding_fee.doubleValue()}</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
						<ul class="pagination">
							<pg:pager url="center/fundingFee.do" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">							
									<pg:param name="code" value="${code }" />
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