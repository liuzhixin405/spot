<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-交易查询</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container">
			<jsp:include page="_trade_left_sz.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2>
							交易查询
						</h2>
						<div class="trade-title-lt rt">
							<a href="<%=tradePath%>sztradeQuery.do?czlx=all&pendingcode=${currentTransaction.transaction_code }" <c:if test="${currentCzlx eq 'all'}"> class="current"</c:if>>全部类型</a>
							<a href="<%=tradePath%>sztradeQuery.do?czlx=1&pendingcode=${currentTransaction.transaction_code }" <c:if test="${currentCzlx eq '1'}"> class="current"</c:if>>多单开仓</a>
							<a href="<%=tradePath%>sztradeQuery.do?czlx=2&pendingcode=${currentTransaction.transaction_code }" <c:if test="${currentCzlx eq '2'}"> class="current"</c:if>>多单平仓</a>
							<a href="<%=tradePath%>sztradeQuery.do?czlx=3&pendingcode=${currentTransaction.transaction_code }" <c:if test="${currentCzlx eq '3'}"> class="current"</c:if>>空单开仓</a>
							<a href="<%=tradePath%>sztradeQuery.do?czlx=4&pendingcode=${currentTransaction.transaction_code }" <c:if test="${currentCzlx eq '4'}"> class="current"</c:if>>空单平仓</a>
							<a href="<%=tradePath%>sztradeQuery.do?czlx=5&pendingcode=${currentTransaction.transaction_code }" <c:if test="${currentCzlx eq '5'}"> class="current"</c:if>>多单爆仓</a>
							<a href="<%=tradePath%>sztradeQuery.do?czlx=6&pendingcode=${currentTransaction.transaction_code }" <c:if test="${currentCzlx eq '6'}"> class="current"</c:if>>空单爆仓</a>
						</div>
						<form action="<%=basePath%>trade/sztradeQuery.do?czlx=${currentCzlx }&pendingcode=${currentTransaction.transaction_code }" name="tiaojian">
							<div class="selected rt" style="margin-right: 15px;">
								<select name="pendingcode" id="transactionCode" onchange="javascript:document.tiaojian.submit();">
									<c:forEach var="ta" items="${transactionList}">
										<option value="${ta.transaction_code}" <c:if test="${ta.transaction_code eq currentTransaction.transaction_code}"> selected</c:if>>
											${fn:toUpperCase(ta.transaction_code)}
										</option>
									</c:forEach>
								</select>
							</div>
						</form>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										成交时间
									</th>
									<th>
										合约号
									</th>
									<th>
										成交类型
									</th>
									<th>
										<c:choose>
											<c:when test="${issz}">
												成交点位
											</c:when>
											<c:otherwise>
												成交均价
											</c:otherwise>
										</c:choose>
									</th>
									<th>
										成交数量
									</th>
									<th>
										保证金
									</th>
									<th>
										盈亏额
									</th>
									<th>
										佣金
									</th>
									<th>
										状态
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="tl" items="${tradeLogList.datas}">
									<tr>
										<td>
											<ut:formatDate dateline="${tl.trade_time}"></ut:formatDate>
										</td>
										<td>
											${fn:toUpperCase(tl.transaction_code)}
										</td>
										<td>
											<c:choose>
												<c:when test="${tl.trade_type_id eq 1}">
													<b class="buy">多单开仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 2}">
													<b class="buy">多单平仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 3}">
													<b class="sell">空单开仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 4}">
													<b class="sell">空单平仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 5}">
													<b class="loss-all">多单爆仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 6}">
													<b class="loss-all">空单爆仓</b>
												</c:when>
												<c:otherwise>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.#" />
										</td>
										<td>
											<fmt:formatNumber value="${tl.trade_count}" pattern="#0" />张
										</td>
										<td>
											<fmt:formatNumber value="${tl.trade_deposit}" pattern="#0.####" />USDT
										</td>
										<td <c:if test="${tl.trade_income lt 0}"> class="sell"</c:if>
											<c:if test="${tl.trade_income gt 0}"> class="buy"</c:if>>
											<c:choose>
												<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
												</c:when>
												<c:otherwise>
													<fmt:formatNumber value="${tl.trade_income }" pattern="#0.########" />USDT 
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<fmt:formatNumber value="${tl.trade_fee}" pattern="#0.######" />USDT
										</td>
										<td>
											完成
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<ul class="pagination">
							<pg:pager url="trade/sztradeQuery.do" items="${tradeLogList.total}" maxIndexPages="10" maxPageItems="${tradeLogList.maxitems}" export="currentPageNumber=pageNumber">
								<pg:param name="czlx" value="${currentCzlx}" />
								<pg:param name="pendingcode" value="${currentTransaction.transaction_code }" />
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
									<li>
										<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">下一页</a>
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
