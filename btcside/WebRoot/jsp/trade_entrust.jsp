<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>
			<c:if test ="${not empty lastPrice}">${lastPrice }USDT </c:if>
			<fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.tradeEntrust.title"/>
		</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath %>common/css/style.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/tradeEntrust.js?<%=rjs%>"></script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container">

			<jsp:include page="_trade_left.jsp"></jsp:include>

			<div class="main-rt-box">

				<div class="main-wrap">
					<div class="block-title">
						<h2>
						<c:if test="${locale == 'zh_CN'}"><fmt:message key="msg.pg.tradeEntrust.title"/></c:if>
						<c:if test="${locale == 'en_US'}"><fmt:message key="msg.pg.trade.option"/></c:if>
						</h2>

						<div class="trade-control-box">
							<div class="trade-control lt"
								style="width: 313px; display: inline-block; float: left;">
								<a href="javascript:;" onclick="cancelDeal('1');"><fmt:message key="msg.pg.tradeEntrust.cancelBuy"/></a>
								<a href="javascript:;" onclick="cancelDeal('2');"><fmt:message key="msg.pg.tradeEntrust.cancelSell"/></a>
								<a href="javascript:;" onclick="cancelDeal('0');"><fmt:message key="msg.pg.tradeEntrust.cancelAll"/></a>
							</div>
						</div>

						<form action="<%=basePath%>trade/entrust.do" name="tiaojian">
							<div class="selected rt" id="selected">
								<select required="true" name="pendingtype" id="pendingtype" onchange="javascript:document.tiaojian.submit();">
									<c:if test ="${type eq 'all'}">
										<option value="all" selected="selected"><fmt:message key="msg.pg.tradeEntrust.priceSort"/></option>
										<option value="htl"><fmt:message key="msg.pg.tradeEntrust.priceHTL"/></option>
										<option value="lth"><fmt:message key="msg.pg.tradeEntrust.priceLTH"/></option>
									</c:if>
									<c:if test ="${type eq 'htl'}">
										<option value="htl" selected="selected"><fmt:message key="msg.pg.tradeEntrust.priceHTL"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.priceSort"/></option>
										<option value="lth"><fmt:message key="msg.pg.tradeEntrust.priceLTH"/></option>
									</c:if>
									<c:if test ="${type eq 'lth'}">
										<option value="lth" selected="selected"><fmt:message key="msg.pg.tradeEntrust.priceLTH"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.priceSort"/></option>
										<option value="htl"><fmt:message key="msg.pg.tradeEntrust.priceHTL"/></option>
									</c:if>
								</select>
							</div>
							<div class="selected rt" id="selected">
								<select required="true" name="pendingorder" id="pendingorder" onchange="javascript:document.tiaojian.submit();">
									<c:if test ="${order eq 'all'}">
										<option value="all" selected="selected"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="sell"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
										<option value="buyf"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></option>
										<option value="sells"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></option>
										<option value="sellf"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></option>
										<option value="buys"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></option>
									</c:if>
									<c:if test ="${order eq 'buy'}">
										<option value="buy" selected="selected"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="sell"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
										<option value="buyf"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></option>
										<option value="sells"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></option>
										<option value="sellf"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></option>
										<option value="buys"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></option>
									</c:if>
									<c:if test ="${order eq 'sell'}">
										<option value="sell" selected="selected"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="buyf"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></option>
										<option value="sells"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></option>
										<option value="sellf"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></option>
										<option value="buys"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></option>
									</c:if>
									<c:if test ="${order eq 'buyf'}">
										<option value="buyf" selected="selected"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="sell"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
										<option value="sells"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></option>
										<option value="sellf"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></option>
										<option value="buys"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></option>
									</c:if>
									<c:if test ="${order eq 'sells'}">
										<option value="sells" selected="selected"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="sell"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
										<option value="buyf"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></option>
										<option value="sellf"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></option>
										<option value="buys"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></option>
									</c:if>
									<c:if test ="${order eq 'sellf'}">
										<option value="sellf" selected="selected"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="sell"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
										<option value="buyf"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></option>
										<option value="sells"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></option>
										<option value="buys"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></option>
									</c:if>
									<c:if test ="${order eq 'buys'}">
										<option value="buys" selected="selected"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="sell"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
										<option value="buyf"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></option>
										<option value="sells"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></option>
										<option value="sellf"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></option>
									</c:if>
								</select>
							</div>
							<c:if test="${!issz}">
								<div class="selected rt">
									<select  name="pendingcode" id="transactionCode" onchange="javascript:document.tiaojian.submit();">
									<c:if test="${'all' eq currentTransaction.transaction_code}">
										<option value="all" selected="selected"><fmt:message key="msg.pg.tradeEntrust.allTrans"/></option>
									</c:if>
									<c:if test="${'all' ne currentTransaction.transaction_code}">
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrans"/></option>
									</c:if>
									<c:forEach var="ta" items="${transactionList}">
										<c:if test="${ta.transaction_code ne currentTransaction.transaction_code}">
											<option value="${ta.transaction_code}">
												${fn:toUpperCase(ta.transaction_code)} <fmt:message key="msg.pg.index.contract"/>
											</option>
										</c:if>
										<c:if test="${ta.transaction_code eq currentTransaction.transaction_code}">
											<option value="${ta.transaction_code}" selected="selected">
												${fn:toUpperCase(ta.transaction_code)} <fmt:message key="msg.pg.index.contract"/>
											</option>
										</c:if>
										<%-- <option value="${ta.transaction_code}"
											<c:if test="${ta.transaction_code eq currentTransaction.transaction_code}"> selected</c:if>>
											${fn:toUpperCase(ta.transaction_code)}
										</option> --%>
									</c:forEach>
								</select>
								</div>
							</c:if>
						</form>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.tradeEntrust.dependingTime"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.contractNO"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.typeOfList"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.leverage"/>
									</th>
									<th>
										<c:if test="${!issz}"><fmt:message key="msg.pg.trade.entruePrice"/>(USDT)</c:if>
										<c:if test="${issz}"><fmt:message key="msg.pg.tradeEntrust.dependingPointPosition"/></c:if>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.entrueNO"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.doneDeal"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.unsettled"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.depositCash"/>(USDT)
									</th>
									<th>
										<fmt:message key="msg.pg.trade.option"/>
									</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach var="pd" items="${pdList}" >
								<tr>
									<td>
										
										<ut:formatDate dateline="${pd.delegation_time}"></ut:formatDate>
									</td>
									<td>
										${fn:toUpperCase(pd.transaction_code)}
									</td>
									<td>
										<c:choose>
										<c:when test="${pd.direction eq 1 && pd.priority eq 1}"><b class="buy"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></b></c:when>
										<c:when test="${pd.direction eq 1 && pd.priority eq 2}"><b class="sell"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></b></c:when>
										<c:when test="${pd.direction eq 1 && pd.priority eq 3}"><b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.kongBaoTrade"/></b></c:when>
										<c:when test="${pd.direction eq 2 && pd.priority eq 1}"><b class="sell"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></b></c:when>
										<c:when test="${pd.direction eq 2 && pd.priority eq 2}"><b class="buy"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></b></c:when>
										<c:when test="${pd.direction eq 2 && pd.priority eq 3}"><b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.duoBaoTrade"/></b></c:when>
										</c:choose>
									</td>
									<td>
										${pd.multiple}
									</td>
									<td>
										<c:choose>
											<c:when test="${issz}">
												<fmt:formatNumber value="${pd.delegation_price}" pattern="#0.#"/>
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${pd.delegation_price}" pattern="#0.######"/>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${issz}">
												<fmt:formatNumber value="${pd.delegation_count}" pattern="#0"/><fmt:message key="msg.pg.tradeEntrust.zhang"/>
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${pd.delegation_count}" pattern="#0.##"/>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${issz}">
												<fmt:formatNumber value="${pd.deal_count}" pattern="#0"/><fmt:message key="msg.pg.tradeEntrust.zhang"/>
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${pd.deal_count}" pattern="#0.##"/>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${issz}">
												<fmt:formatNumber value="${pd.undeal_count}" pattern="#0"/><fmt:message key="msg.pg.tradeEntrust.zhang"/>
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${pd.undeal_count}" pattern="#0.##"/>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<fmt:formatNumber value="${pd.deposit}" pattern="#0.####"/>
									</td>
									<td>
										<c:choose>
										<c:when test="${pd.priority < 3}"><a href="<%=basePath %>tradeAjax/canclePendingDelegation.do?pdId=${pd.delegation_id}&code=${pd.transaction_code}&type=${type}&order=${order}" class="btn btn-blue">
										<fmt:message key="msg.pg.tradeEntrust.cancelTrade"/></a></c:when>
										<c:otherwise><fmt:message key="msg.pg.tradeEntrust.alreadyBao"/></c:otherwise></c:choose>
									</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>

					</div>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
		
	</body>

</html>
