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

			<jsp:include page="coin_trade_left.jsp"></jsp:include>

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

						<form action="<%=basePath%>spotTrade/spotEntrust.do" name="tiaojian">
							<div class="selected rt" id="selected">
								<select required="true" name="price" id="price" onchange="javascript:document.tiaojian.submit();">
									<c:if test ="${price eq 'all'}">
										<option value="all" selected="selected"><fmt:message key="msg.pg.tradeEntrust.priceSort"/></option>
										<option value="htl"><fmt:message key="msg.pg.tradeEntrust.priceHTL"/></option>
										<option value="lth"><fmt:message key="msg.pg.tradeEntrust.priceLTH"/></option>
									</c:if>
									<c:if test ="${price eq 'htl'}">
										<option value="htl" selected="selected"><fmt:message key="msg.pg.tradeEntrust.priceHTL"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.priceSort"/></option>
										<option value="lth"><fmt:message key="msg.pg.tradeEntrust.priceLTH"/></option>
									</c:if>
									<c:if test ="${price eq 'lth'}">
										<option value="lth" selected="selected"><fmt:message key="msg.pg.tradeEntrust.priceLTH"/></option>
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.priceSort"/></option>
										<option value="htl"><fmt:message key="msg.pg.tradeEntrust.priceHTL"/></option>
									</c:if>
								</select>
							</div>
							<div class="selected rt" id="selected">
								<select required="true" name="direction" id="direction" onchange="javascript:document.tiaojian.submit();">
									<c:if test ="${direction eq 'all'}">
										<option value="all" selected="selected"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="sell"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
									</c:if>
									<c:if test ="${direction eq 'buy'}">
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy" selected="selected"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="sell"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
									</c:if>
									<c:if test ="${direction eq 'sell'}">
										<option value="all"><fmt:message key="msg.pg.tradeEntrust.allTrade"/></option>
										<option value="buy"><fmt:message key="msg.pg.tradeEntrust.buyTrade"/></option>
										<option value="sell" selected="selected"><fmt:message key="msg.pg.tradeEntrust.sellTrade"/></option>
									</c:if>
								</select>
							</div>
							<div class="selected rt">
								<select  name="coinPairName" id="coinPairName" onchange="javascript:document.tiaojian.submit();">
									<c:if test="${'all' eq currentCoinPair.coin_pair_name}">
										<option value="all" selected="selected"><fmt:message key="msg.pg.cointrade.allTrans"/></option>
									</c:if>
									<c:if test="${'all' ne currentCoinPair.coin_pair_name}">
										<option value="all"><fmt:message key="msg.pg.cointrade.allTrans"/></option>
									</c:if>
									<c:forEach var="ta" items="${coinPairs}">
										<c:if test="${ta.coin_pair_name ne currentCoinPair.coin_pair_name}">
											<option value="${ta.coin_pair_name}">
												${fn:toUpperCase(ta.coin_pair_name)}
											</option>
										</c:if>
										<c:if test="${ta.coin_pair_name eq currentCoinPair.coin_pair_name}">
											<option value="${ta.coin_pair_name}" selected="selected">
												${fn:toUpperCase(ta.coin_pair_name)}
											</option>
										</c:if>
									</c:forEach>
								</select>
							</div>
						</form>
					</div>
					<!--上面可去掉-->
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.tradeEntrust.dependingTime"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.tradeON"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.typeOfList"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.entruePrice"/>
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
										${fn:toUpperCase(pd.coin_pair_name)}
									</td>
									<td>
										<c:choose>
										<c:when test="${pd.direction eq 1}"><b class="buy"><fmt:message key="msg.pg.trade.buyIn"/></b></c:when>
										<c:when test="${pd.direction eq 2}"><b class="sell"><fmt:message key="msg.pg.cointrade.sell"/></b></c:when>
										</c:choose>
									</td>
									<td>
										<fmt:formatNumber value="${pd.delegation_price}" pattern="#0.######"/>
									</td>
									<td>
										<fmt:formatNumber value="${pd.delegation_count}" pattern="#0.##"/>
									</td>
									<td>
										<fmt:formatNumber value="${pd.deal_count}" pattern="#0.##"/>
									</td>
									<td>
										<fmt:formatNumber value="${pd.undeal_count}" pattern="#0.##"/>
									</td>
									<td>
										<a href="<%=basePath %>spotTradeAjax/canclePendingDelegation.do?pdId=${pd.delegation_id}&code=${pd.coin_pair_name}&price=${price}&direction=${direction}"
										 class="btn btn-blue">
										<fmt:message key="msg.pg.tradeEntrust.cancelTrade"/></a>
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
<script type="text/javascript">
function cancelDeal(direction){
	var code = $("#coinPairName").val();
	$.get(basePath +"spotTradeAjax/cancleDeal.do", {
		direction : direction,
		code : code,
		token : getCookie("_token")
	}, function(data) {
		location.reload();
		return;
	});
}
</script>
</html>
