<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<div class="lt-wrap order-panel" id="close-buy-position">
	<form action="">
		<div class="trade-main">
			<div class="trade-box">
				<div class="trade-detail tog-ht" style="margin-bottom: 15px;">
					<div class="toggle-wrap relative clearfix">
						<!-- 可用 -->
						<div class="lt">
						  <div class="label label-sell"> <em id="toggle-em" style="padding-bottom: 8px;display: inline-block;"><fmt:message key="msg.pg.cointrade.available"/>: </em></div>
						  <b class="best-sell-Price" id="bestSellPrice"><fmt:formatNumber value="${coinAccount.btc.doubleValue() }" pattern="#0.0000" /></b>
						  <b style="font-weight: normal;">${currentCoinPair.coin_name }</b>
						</div>
						
						<div class="rt">
						<!-- 可卖 -->
<%-- 						  <div class="label label-buy"> <em id="toggle-em" style="color: #94ABC0;padding-bottom: 8px;display: inline-block;"><fmt:message key="msg.pg.cointrade.toSell"/>: </em></div>
						  <b class="best-Buy-Price" style="font-weight: normal;" id="canSellPrice">--</b>
						  <b style="font-weight: normal;">${currentCoinPair.market_coin_name }</b> --%>
						  <%-- <a style="color: #94ABC0;" href="spotTradeQuery.do?coinPairName=${currentCoinPair.coin_pair_name}">充币</a> --%>
						  <!-- 充值 -->
						  	<c:if test="${coin.type_name eq 'USDT' }">
								<a class="label-recharged" href="<%=userPath %>recharge.do?omni=0&coinId=${coin.type_id }" ><fmt:message key="msg.pg.userCenter.spotTradeRecharged"/></a>
							</c:if>
							<c:if test="${coin.type_name ne 'USDT' }">
								<a class="label-recharged" href="<%=userPath %>recharge.do?coinId=${coin.type_id }" ><fmt:message key="msg.pg.userCenter.spotTradeRecharged"/></a>
							</c:if>
						</div>
					  <div class="clear"></div>
					</div>
				</div>
                <!-- 卖出价格 -->				
				<div class="trade-detail relative ie-bug1" style="margin-bottom: 15px;">
					<input type="text" class="validate[required,number] form-control order-input add-input"
						value="${buytop5List[0].price}" autocomplete="off" placeholder="<fmt:message key="msg.pg.cointrade.priceSell"/>"
						id="sellPrice" name="price" onblur="" maxlength="10" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});checkAmount('sell');"/>
					<b class="sell-Price-title" style="text-align: right;font-weight: normal;"><em class="sell-Price-unit" style="color: #fff;">${currentCoinPair.market_coin_name }</em></b>
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;" />	
				</div>
				<!-- 卖出数量 -->
				<div class="trade-detail relative" style="margin-bottom: 15px;">
					<input type="text" style="display: none;"/>
					<input type="text" class="validate[required,number] form-control order-input add-input" style="text-align: left;" value="" placeholder="<fmt:message key="msg.pg.cointrade.amountSell"/>"
						id="sellAmount" name="amount" maxlength="10" onblur="" autocomplete="off" onkeyup="checkInputDigits(this, ${amountDigits});checkAmount('sell', 1);" />
					<b class="sell-Price-title" style="text-align: right;color: #fff;font-weight: normal;"><em class="sell-Price-unit" style="color: #fff;">${currentCoinPair.coin_name }</em></b>
				</div>
				<!-- 百分比 -->
				<div class="trade-detail relative spot-percent-sell" style="margin-bottom: 15px;">
					<span class="sell-percent">25%</span>
					<span class="sell-percent">50%</span>
					<span class="sell-percent">75%</span>
					<span class="sell-percent">100%</span>
					<div class="clear"></div>
				</div>
				<!-- 交易金额 -->
				<div class="trade-detail relative" style="margin-bottom: 15px;">
					<div class="lt">
						<span class="label"><fmt:message key="msg.pg.cointrade.money"/>: </span>
						<b style="font-weight: normal;" id="sellMoney">--</b> <b class="sell-Money-unit" style="color:#fff;font-weight: normal;">${currentCoinPair.market_coin_name }</b>
						<div class="clear"></div>
					</div>
				</div>
				
				<!-- 卖出 -->
				<div class="coin-trader normal-trade-div" style="text-align: center;display:${currentCoinPair.suspend ? 'none' : 'block'}">
					<input type="button" class="btn trade-btn sell-bg" value="<fmt:message key="msg.pg.cointrade.sell"/>"
					onclick="return tcoinSubmit('<%=basePath %>spotTradeAjax/tradeTrans.do', '${coinPairName.toLowerCase()}', 2);">
				</div>
			</div>
		</div>
	</form>
	
	<div class="trade-alert suspend-div" style="display:${currentCoinPair.suspend ? 'block' : 'none'};">
		<div class="warning"><fmt:message key="msg.trade.pause"/></div>
	</div>
	
	<!-- 下单后提示 -->
	<div class="trade-alert">
		<div class="success display-none" id="infomsg_kcsell"></div>
	</div>
</div>
