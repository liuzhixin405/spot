<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<c:if test="${payPwd }">
	<style type="text/css">
		.full-cont .left-cont-box .canver-parent {
			padding-bottom: 390px;
		}
		.full-cont .trade-manager-tab {
			height: 390px;
		}
		.full-cont .right-cont-box .right-cont-top {
			padding-bottom: 0;
		}
	</style>
</c:if>

<div class="lt-wrap order-panel" id="full-order" <c:if test="${isMarket }"> style="height: auto;"</c:if>>
	<c:if test="${sessionScope.userinfo!=null}">
		<input type="hidden" id="userEmail" name="userEmail" value="${sessionScope.userinfo.f_user_email}">
	</c:if>
				
	<input type="hidden" id="coinPairName" name="coinPairName" value="${currentCoinPair.coin_pair_name }" />
	<input type="hidden" id="coinName" name="coinName" value="${currentCoinPair.coin_name.toLowerCase() }" />
	<input type="hidden" id="marketName" name="marketName" value="${currentCoinPair.market_coin_name.toLowerCase() }" />
	<input type="hidden" id="minKcNums" name="minKcNums" value="${minKcNums }" />
	<input type="hidden" id="coinBalance" name="coinBalance" value="${coinAccount.btc }" />
	<input type="hidden" id="marketBalance" name="marketBalance" value="${maeketCoinAccount.btc }" />
	<input type="hidden" id="amountDigits" name="amountDigits" value="${amountDigits }" />  		
   	<form action="" name="" id="topSearchForm1" class="coin-order-form" autocomplete="off">
   		<div class="trade-main" <c:if test="${isMarket }"> style="height: auto;"</c:if>>
			<div class="trade-box">
				<div class="trade-detail tog-ht" style="margin-bottom: 15px;">
		  			<div class="toggle-wrap relative clearfix">
						<!-- 可用 -->
						<div class="lt">
				   			<div class="label label-sell"><em id="toggle-em" style="padding-bottom: 8px;display: inline-block;"><fmt:message key="msg.pg.cointrade.available"/>: </em></div>
				   			<b id="bestBuyPrice" class="best-sell-Price" style="font-weight: normal;"><fmt:formatNumber value="${maeketCoinAccount.btc.doubleValue() }" pattern="#0.0000" /></b>
				   			<b style="font-weight: normal;">${currentCoinPair.market_coin_name }</b>
						</div>
						
						<div class="rt">
						<!-- 可买 -->
<%-- 				   			<div class="label label-buy"> <em id="toggle-em" style="color: #94ABC0;padding-bottom: 8px;display: inline-block;"><fmt:message key="msg.pg.cointrade.buyable"/>: </em></div>
				   			<b id="canBuyPrice" class="best-Buy-Price" style="font-weight: normal;">--</b>
				   			<b style="font-weight: normal;">${currentCoinPair.coin_name }</b> --%>
				   			<%-- <a style="color: #94ABC0;" href="spotTradeQuery.do?coinPairName=${currentCoinPair.coin_pair_name}">充币</a> --%>
				   			<!-- 充值 -->
				   			<c:if test="${marketCoin.type_name eq 'USDT' }">
								<a class="label-recharged" href="<%=userPath %>recharge.do?omni=0&coinId=${marketCoin.type_id }" ><fmt:message key="msg.pg.userCenter.spotTradeRecharged"/></a>
							</c:if>
							<c:if test="${marketCoin.type_name ne 'USDT' }">
								<a class="label-recharged" href="<%=userPath %>recharge.do?coinId=${marketCoin.type_id }" ><fmt:message key="msg.pg.userCenter.spotTradeRecharged"/></a>
							</c:if>
						</div>
						<div class="clear"></div>
			 		</div>
				</div>
				<!-- 买入价格 -->	
				<div class="trade-detail relative ie-bug1 " style="margin-bottom: 15px;">
					<b class="sell-Price-title" style="text-align: right;color: #fff;font-weight: normal;"><em class="sell-Price-unit" style="color: #fff;">${currentCoinPair.market_coin_name }</em></b>
					<input type="text" class="validate[required,number] form-control order-input" value="${selltop5List[selltop5List.size() - 1].price}" id="buyPrice" name="price" maxlength="10" 
						onkeyup="checkInputDigits(this, ${digits});checkAmount('buy');" onblur="" autocomplete="off" placeholder="<fmt:message key="msg.pg.cointrade.priceBuy"/>"/>
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				</div>
					
				<!-- 买入数量 -->
				<div class="trade-detail relative" style="margin-bottom: 15px;">
					<input type="text" style="display: none;"/>
					<b class="sell-Price-title" style="text-align: right;font-weight: normal;"><em class="sell-Price-unit" style="color: #fff;">${currentCoinPair.coin_name }</em></b>
					<input type="text" class="validate[required,number] form-control order-input" value="" id="buyAmount" name="amount" maxlength="10" 
					onkeyup="checkInputDigits(this, ${amountDigits});checkAmount('buy', 1);" onblur="" autocomplete="off" placeholder="<fmt:message key="msg.pg.cointrade.amountBuy"/>"/>
					<input type="text" style="position: absolute; top: -10000px; left: -10000px; width: 0px; height: 0px;border: 0px; padding: 0;"/>
					<div class="clear"></div>
				</div>
				<!-- 百分比 -->
				<div class="trade-detail relative spot-percent-buy" style="margin-bottom: 15px;">
					<span class="percent-buy">25%</span>
					<span class="percent-buy">50%</span>
					<span class="percent-buy">75%</span>
					<span class="percent-buy">100%</span>
					<div class="clear"></div>
				</div>	
				<!-- 交易金额 -->
				<div class="trade-detail relative" style="margin-bottom: 15px;">
					<div class="lt">
						<span class="label"><fmt:message key="msg.pg.cointrade.money"/>：</span>
						<b style="font-weight: normal;" id="buyMoney">--</b> <b style="font-weight: normal;">${currentCoinPair.market_coin_name }</b>
						<div class="clear"></div>
					</div>
				</div>

				<!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
				<div class="coin-trade" style="${payPwd ? 'display: block;' : 'display: none;' }">
					<span class="label"><fmt:message key="msg.pg.trade.assetPwd"/>：</span>
		  			<input type="password" class="form-control" value="" name="payPwd" maxlength="20" autocomplete="new-password"/>
		  			<input type="text" style="display: none;"/>
				</div>
				
				<div class="coin-trader normal-trade-div" style="text-align:center;display:${currentCoinPair.suspend ? 'none' : 'block'}">
					<input type="button" class="btn trade-btn buy-bg" value="<fmt:message key="msg.pg.trade.buyIn"/>" 
					onclick="return tcoinSubmit('<%=basePath %>spotTradeAjax/tradeTrans.do', '${coinPairName.toLowerCase()}', 1);">
				</div>
			</div>
		</div>
   	</form>
   		
	<div class="trade-alert suspend-div" style="display:${currentCoinPair.suspend ? 'block' : 'none'};">
		<div class="warning"><fmt:message key="msg.trade.pause"/></div>
	</div>
  		
	<!-- 下单后提示 -->
	<div class="trade-alert">
		<div class="success display-none" id="infomsg_kcbuy"></div>
	</div>	
	
</div>