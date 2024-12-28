<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!DOCTYPE HTML>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${lastPrice }USDT <fmt:message key="msg.pg.systemName"/></title>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/kline.css?<%=rjs%>" />
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script> 
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<jsp:include page="_common.jsp"></jsp:include>

<style type="text/css">
	.trade-rt-table table td:last-child {
		padding-right: 45px
	}
</style>
</head>

<body>
<jsp:include page="duiping.jsp"></jsp:include>

<c:if test="${hashy}">
	<div class="add-windows" id="alert-windows"></div>
</c:if>
<!-- 若无合约 -->
<c:if test="${!hashy}">
	<div class="add-windows" id="add-windows">
		<div class="tb-overlay" id="tb-overlay"></div>
		<div class="add-wrap" style="display: block;">
			<div class="dialogs-title">
				<h2 class="lt">
					${typeCode}<fmt:message key="msg.pg.index.contract"/>
				</h2>
				<a class="close-icon icons-close" id="close-alert"
					href="javascript:history.go(-1)"></a>
			</div>
			<div class="dialogs-body">
				<p
					style="text-align: center; font-size: 16px; margin-bottom: 30px;">
					<fmt:message key="msg.pg.trade.waitMoment"/>！
				</p>
				<div class="form-group-btn text-center">
					<a class="btn sub-btn center" href="javascript:history.go(-1)"><fmt:message key="msg.pg.trade.goBack"/></a>
				</div>
			</div>
		</div>
	</div>
</c:if>

<jsp:include page="jisuan.jsp"></jsp:include>
<jsp:include page="_top.jsp"></jsp:include>
<jsp:include page="_head.jsp"></jsp:include>

<div class="container">
	<div style="margin: 0 10px 10px 10px;position: relative;">
		<div class="sz-item" id="trade-ticker">
	        <span class="sz-span"><fmt:message key="msg.pg.trade.tallest"/>：<b class="buy" id="todayHigh">${todayHigh} ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b></span>
	        <span class="sz-span"><fmt:message key="msg.pg.trade.lowest"/>：<b class="sell" id="todayLow">${todayLow} ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b></span>
	        <span class="sz-span"><fmt:message key="msg.pg.index.dealAmounts"/>：<b id="tradevols"><fmt:formatNumber value="${tradevols}" pattern="#.####"></fmt:formatNumber> <c:if test="${locale == 'zh_CN'}"><fmt:message key="msg.pg.trade.ge"/></c:if></b></span>
	        <span class="sz-span"><fmt:message key="msg.pg.trade.dealVol"/>：<b id="trades"><fmt:formatNumber value="${trades}" pattern="#.####"></fmt:formatNumber> ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b></span>
	        <!-- <span class="sz-span"><fmt:message key="msg.pg.index.inventory"/>：<b id="holdvols"><fmt:formatNumber value="${holdvols}" pattern="#.##"></fmt:formatNumber> <fmt:message key="msg.pg.trade.ge"/></b></span> -->
	      	<div class="market-status rt">
         		<span>
         	  	<b><fmt:message key="msg.pg.market.kLineState"/>：</b><a href="javascript:;" class="toggle-icon open-bgc" id="kline-a"><fmt:message key="msg.pg.market.hadBeOpen"/></a>
         		</span>
         	</div>
	    </div>

	    <input type="hidden" id="transaction_code" name="transaction_code" value="${currentTransaction.transaction_code}" />
	    <div class="chose-canvas chose-canvas-us" id="chose-canvas">
	     	<input type="button" class="btn-canvas clik" value="<fmt:message key="msg.pg.market.kLine"/>">
	     	&emsp;<input type="button" class="btn-canvas" value="<fmt:message key="msg.pg.market.depth"/>">
	    </div>
	    <div class="main-wrap margin-chart" id="depathChart">
			<jsp:include page="depathdraw.jsp"></jsp:include>
		</div>
	    <div class="main-wrap margin-chart" id="kdraw">
			<jsp:include page="kdraw.jsp"></jsp:include>
		</div>
	</div>
  <div class="trade-rt-box"> 
    <div class="main-wrap" style="margin-top: 20px;">
		<div class="trade-pair-wrap lt trade-manage-main">
			<table cellpadding="0" cellspacing="0">
				<thead>
					<tr style="height: 41px;">
						<th>
							<fmt:message key="msg.pg.index.contract"/>
						<th>
							<fmt:message key="msg.pg.index.newDeal"/>
						</th>
						<th>
							<fmt:message key="msg.pg.index.upToDown"/>
						</th>
						<th>
							<fmt:message key="msg.pg.index.dealAmounts"/>
						</th>
					</tr>
				</thead>
				<tbody id="tickerTbody">
					<c:forEach var="map" items="${listMap}">
						<tr class="tradesA" onclick="gotoTrade(${map.tranCode });">
							<td>${fn:toUpperCase(map.typeCode)}</td>
							<td>${map.lastPrice}</td>
							<c:choose>
								<c:when test="${ map.lastPrice >= map.firstPrice }">
								<td class="buy">
									${map.fudu }
								</td>
								</c:when>
								<c:otherwise>
								<td class="sell">
									${map.fudu }
								</td>
								</c:otherwise>
							</c:choose>
							<td><fmt:formatNumber value="${map.vol }" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
    
    <div class="rt-wrap lt" style="margin-left: 15px;">
        <div class="trade-rt-table">
          <table id="selldiv">
            <thead>
              <tr>
                <th> <fmt:message key="msg.pg.trade.price"/> </th>
                <th> <fmt:message key="msg.pg.index.amount"/> </th>
                <th><fmt:message key="msg.pg.market.sum" /></th>
              </tr>
            </thead>
            <tbody id="sellTbody">
              <c:forEach var="sell" items="${selltop5List}">
                <tr onclick="trade_price_market(2, '${sell.price}', '${sell.countNum}');" >
                  <td><span class="sell">${sell.price}</span></td>
                  <td> ${sell.countNum} </td>
                  <td>${sell.sumNum.doubleValue()}</td>
                  <td class="progress"></td>
                </tr>
              </c:forEach>
            </tbody>
            <tbody id="buyTbody">
              <c:forEach var="buy" items="${buytop5List}">
                <tr onclick="trade_price_market(1, '${buy.price}', '${buy.countNum}');" >
                  <td><span class="buy">${buy.price}</span></td>
                  <td> ${buy.countNum} </td>
                  <td>${buy.sumNum.doubleValue()}</td>
                  <td class="progress"></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <div class="trade-rt-data">
          <span><fmt:message key="msg.pg.trade.newDealprice"/>： <em id="lastPrice">${currentTransaction.latest_transaction_price.doubleValue() }</em>
          </span> 
          <span class="zs">
            <a href="<%=basePath %>bpiAction/bpiIndex.do?coinId=${currentTransaction.transaction_type}" 
            target="_blank">${typeCode} <fmt:message key="msg.pg.top.exponent"/>： <em id="${typeCode.toLowerCase()}-bpi2">
			<c:forEach items="${indexs}" var="index">
				<c:if test="${index.code eq currentTransaction.transaction_code}">
					${index.price.doubleValue() }
				</c:if>
			</c:forEach>
			</em></a> 
           </span> 
        </div>
      </div>
    
		<!-- 下单界面 -->
		<jsp:include page="order.jsp"></jsp:include>
		
		<input type="hidden" id="buy1" value="${selltop5List[4] }">
     	<input type="hidden" id="sell1" value="${buytop5List[0] }">
    </div>
    <div class="clear"></div>
  </div>
</div>
<div class="clear"></div>
<div class="container-trade" style="padding-top: 10px;">
	<jsp:include page="trade_manage.jsp"></jsp:include> 
</div>
<div class="container-trade">
	<div class="main-wrap">
		<div class="main-data-block1">
			<div class="block-title">
				<h2>
					<fmt:message key="msg.pg.market.dependingTrades"/>
				</h2>
				<div style="float: left; padding-left: 40px;">
					<span class="fpt_left"><fmt:message key="msg.pg.market.flushTime"/></span>
					<select id="updateSecond" onchange="javascript:updateMarketSecond('${homeName=='vtrade' ? 'vcontract' : (homeName=='trade' ? 'contract' : homeName)}');">
							<option value="2000">
										1s
									</option>
							<option value="3000">
										2s
									</option>
							<option value="6000" selected="selected">
										5s
									</option>
							<option value="11000">
										10s
									</option>
							<option value="31000">
										30s
									</option>
							<option value="61000">
										60s
									</option>
					</select>
					<span class="fpt_right"><fmt:message key="msg.pg.market.countDown"/>：<span class="fr" id="secondNumber" style="color: red;">5s</span></span>
				</div>
			</div>
			<div class="trade-manage-main1">
				<table cellpadding="0" cellspacing="0" class="lt" style="border-right: 1px solid #f0f0f0;">
					<thead>
						<tr>
							<th>
								<fmt:message key="msg.pg.market.duo"/>
							</th>
							<th>
								<fmt:message key="msg.pg.market.buyPrice"/>
							</th>
							<th>
								<fmt:message key="msg.pg.market.amount"/>
							</th>
							<th>
								<fmt:message key="msg.pg.market.sum"/>
							</th>
						</tr>
					</thead>
					<tbody id="buytbody">
						<c:forEach var="trans" items="${lstBtcPendingBuy}" varStatus="status">
							<tr style="cursor: pointer; background-color: rgb(255, 255, 255);" onmouseout="this.style.backgroundColor='#fff';" 
								onmouseover="this.style.cursor='pointer';this.style.backgroundColor='#FFFFAA';">
	
								<td class="fc_02">
									<c:out value="${status.index + 1}"></c:out>
								</td>
								<td>
									<span id="buyPriceSpan<c:out value="${status.index + 1}"></c:out>" class="buy">
										<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${trans.price }" pattern="#0.0" />
										</c:if>
										<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${trans.price }" />
										</c:if>
									</span>
								</td>
								<td>
									<span id="buyAmountSpan<c:out value="${status.index + 1}"></c:out>" style="${trans.priority == 3 ? 'color:#ff7f01;' : '' }">
										<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${trans.countNum }" pattern="#0" />
										</c:if>
										<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${trans.countNum }" pattern="#0.0000" />
										</c:if>
									</span>
								</td>
								<td>
									<span id="buySumSpan<c:out value="${status.index + 1}"></c:out>">
										<c:set value="${total+trans.countNum}" var="total"/>
										<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${total }" pattern="#0" />
										</c:if>
										<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${total }" pattern="#0.0000" />
										</c:if>
									</span>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<table cellpadding="0" cellspacing="0" class="lt">
					<thead>
						<tr>
							<th>
								<fmt:message key="msg.pg.market.kong"/>
							</th>
							<th>
								<fmt:message key="msg.pg.market.sellPrice"/>
							</th>
							<th>
								<fmt:message key="msg.pg.market.amount"/>
							</th>
							<th>
								<fmt:message key="msg.pg.market.sum"/>
							</th>
						</tr>
					</thead>
					<tbody id="selltbody">
						<c:forEach var="userPending" items="${lstBtcPendingSell }" varStatus="status">
							<tr style="cursor: pointer; background-color: rgb(255, 255, 255);" onmouseout="this.style.backgroundColor='#fff';" 
								onmouseover="this.style.cursor='pointer';this.style.backgroundColor='#FFFFAA';">
								<td class="fc_02">
									<c:out value="${status.index + 1}"></c:out>
								</td>
								<td>
									<span id="sellPriceSpan<c:out value="${status.index + 1}"></c:out>" class="sell">
										<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${userPending.price }" pattern="#0.0" />
										</c:if>
										<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${userPending.price }" />
										</c:if>
									</span>
								</td>
								<td>
									<span id="sellAmountSpan<c:out value="${status.index + 1}"></c:out>" style="${userPending.priority == 3 ? 'color:#ff7f01;' : '' }">
										<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${userPending.countNum }" pattern="#0" />
										</c:if>
										<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${userPending.countNum }" pattern="#0.0000" />
										</c:if>
									</span>
								</td>
								<td>
									<span id="sellSumSpan<c:out value="${status.index + 1}"></c:out>">
										<c:set value="${selltotal+userPending.countNum}" var="selltotal"/>
										<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${selltotal }" pattern="#0" />
										</c:if>
										<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
											<fmt:formatNumber value="${selltotal }" pattern="#0.0000" />
										</c:if>
									</span>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	
		<div class="main-data-block" style="width: 34%;">
			<div class="block-title">
				<h2>
					<fmt:message key="msg.pg.market.latestDeal"/>
				</h2>
			</div>
			<div class="trade-manage-main">
				<table cellpadding="0" cellspacing="0">
					<thead>
						<tr>
							<th>
								<fmt:message key="msg.pg.market.time"/>
							<th>
								<fmt:message key="msg.pg.market.dealPrice"/>
							</th>
							<th>
								<fmt:message key="msg.pg.market.dealAmount"/>
							</th>
							<th>
								<fmt:message key="msg.pg.market.addAmount"/>
							</th>
						</tr>
					</thead>
					<tbody id="ordertbody">
						<c:forEach var="trades" items="${lstBtcTrades }" varStatus="status">
							<tr class="tradesA">
								<td id="createdDateTd<c:out value="${status.index + 1}"></c:out>">
									<ut:formatDate dateline="${trades[0] }" pattern="HH:mm:ss"></ut:formatDate>
								</td>
								<td class="${trades[4] == 1 ? 'buy' : 'sell'}" id="fontPriceSpan<c:out value="${status.index + 1}"></c:out>">
									<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
										<fmt:formatNumber value="${trades[1] }" pattern="#0.0" />
									</c:if>
									<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
										<fmt:formatNumber value="${trades[1] }" />
									</c:if>
								</td>
								<td id="colorAmount<c:out value="${status.index + 1}"></c:out>">
									<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
										<fmt:formatNumber value="${trades[2] }" pattern="#0" />
									</c:if>
									<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
										<fmt:formatNumber value="${trades[2] }" pattern="#0.0000" />
									</c:if>
								</td>
								<td id="colorAdd<c:out value="${status.index + 1}"></c:out>">
									<c:choose>
										<c:when test="${(trades[5] == 1 || trades[5] == 3) && (trades[6] == 1 || trades[6] == 3)}">
											<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
												+<fmt:formatNumber value="${0 + trades[2] * 2 }" pattern="#0.0000" />
											</c:if>
											<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
												+<fmt:formatNumber value="${0 + trades[2] * 2 }" pattern="#0.0000" />
											</c:if>
										</c:when>
										<c:when test="${(trades[5] == 2 || trades[5] == 4) && (trades[6] == 2 || trades[6] == 4)}">
											<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
												<fmt:formatNumber value="${0 - trades[2] * 2 }" pattern="#0.0000" />
											</c:if>
											<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
												<fmt:formatNumber value="${0 - trades[2] * 2 }" pattern="#0.0000" />
											</c:if>
										</c:when>
										<c:otherwise>
											0
										</c:otherwise>
									</c:choose>
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
<script type="text/javascript" src="<%=basePath%>common/js/tradeKline.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/trade.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/contractTrade.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script> 
<script type="text/javascript">
	$(function() {
		// 控制表格隔行换色
		$(".table-bg table tr:odd").css("background", "#fff");
		$(".table-bg table tr:even").css("background", "#fafafa");
		
        $("#kline-a").toggle(
          function() {
          	$(this).html('<fmt:message key="msg.pg.market.hadBeClose"/>');
			$(".margin-chart").hide();
			$(".chose-canvas").hide();
			$("#kline-a").removeClass('open-bgc').addClass('close-bgc');
          },
          function() {
          	$(this).html('<fmt:message key="msg.pg.market.hadBeOpen"/>');
			$(".margin-chart").show();
			$(".chose-canvas").show();
			$("#kline-a").removeClass('close-bgc').addClass('open-bgc');
			//判断#toggle-main显示隐藏；
          });
	});
	
	$(".btn-canvas").click(function() {
		$(this).addClass("clik").siblings().removeClass("clik");
		if($(this).val() == '<fmt:message key="msg.pg.market.depth"/>') {
			$("#kdraw").hide();
			$("#chart-depath").show();
			$(".chose-canvas").css("top","56px");
		}
		if($(this).val() == '<fmt:message key="msg.pg.market.kLine"/>') {
			$("#kdraw").show();
			$("#chart-depath").hide();
			$(".chose-canvas").css("top","56px");
		}
	})
	
	function gotoTrade(code) {
		document.location.href = 'trade.do?transactionCode=' + code;
	}
	
	updateMarketSecond('${homeName=='vtrade' ? 'vcontract' : (homeName=='trade' ? 'contract' : homeName)}');
</script>
</body>
</html>
