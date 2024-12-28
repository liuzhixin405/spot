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
		.full-cont .trade-detail {
			margin-bottom: 2px;
		}
		.underline {
			top: 114px;
		}
		.market-underline {
			top: 92px;
		}
	</style>
</c:if>

<div class="lt-wrap order-panel" id="full-plan-entrust"  style="display:none;" <c:if test="${isMarket }"> style="height: auto;"</c:if>>
	<form action="" name="topPlanSearchForm" id="topPlanSearchForm" autocomplete="off">
		<input type="hidden" name="btc_index_prices" id="btc_index_prices" value="${btc_index_price}">
		<div class="trade-main" <c:if test="${isMarket }"></c:if>>
			<div class="trade-box">
				<c:if test="${sessionScope.userinfo!=null}">
					<input type="hidden" id="planUserEmail" name="planUserEmail" value="${sessionScope.userinfo.f_user_email}">
				</c:if>
				<input type="hidden" name="planOrderType" id="planOrder" value="" />
	  			<input type="hidden" id="planCode" name="planCode" value="${currentTransaction.transaction_code}" />
				<input type="hidden" id="planMinKcNums" name="planMinKcNums" value="${minKcNums}">
	  			<input type="hidden" id="planMinFloatPrice" name="planMinFloatPrice" value="${minFloatPrice}">
				<input type="hidden" id="planMyRemainBtc" name="planMyRemainBtc" value="${userAccountBtc}">
				<input type="hidden" id="planMyRemainOther" name="planMyRemainOther" value="0">
				<input type="hidden" id="planMyRemainGainBtc" name="myRemainBtc" value="0">
				<input type="hidden" id="planMyDigits" name="planMyDigits" value="${digits}">
	  			<input type="hidden" id="myCommissionRates" name="myCommissionRate" value="${myCommissionRate}">
	  			<input type="hidden" id="planPendingRate" name="pendingRate" value="${pendingRate}">
	  			<input type="hidden" id="planAmountDigits" name="planAmountDigits" value="${amountDigits}">
	  			<input type="hidden" id="planSuspend" name="planSuspend" value="${currentTransaction.suspend}">
	  			<input type="hidden" id="planCoeffcient" name="planCoeffcient" value="${coeffcient}">
	  			<!-- 可用提示框 -->
				<div class="show-usetip-box">
				    <div class="triangle"></div>
					<div class="index-info-detail"><fmt:message key="msg.pg.trade.useTips"/></div> 
				</div>
				<!-- 杠杆倍数提示框 -->
				<div class="show-lever-box">
				    <div class="triangle"></div>
					<div class="index-info-detail"><fmt:message key="msg.pg.trade.leverTips"/></div> 
				</div>
				<!-- 市价委托提示 -->
<%--				<div class="show-marketTip-box">--%>
<%--				    <div class="triangle"></div>--%>
<%--					<div class="index-info-detail"><fmt:message key="msg.pg.trade.marketTips"/></div> --%>
<%--				</div>--%>
<%--				<span class="underline" <c:if test="${locale == 'en_US'}">style="width: 74px;left: 200px;"</c:if>></span>--%>

				<!-- 杠杆 -->
				<div class="trade-detail relative ie-bug">
					<div class="lt" onmouseover="showOrderOptions();" onmouseout="hideOrderOptions();">
						<div class="type-order-input">
							<input type="text" readonly="readonly" unselectable="on" class="type-order-inner input-inner-new"
								   value="<fmt:message key="msg.pg.trade.fmtFullPlanEntrust"/>"/>
							<em class="triger-arrow-select triger-arrow-order"></em>
						</div>
						<div class="type-options-order">
							<div class="type-order-box">
								<span class="type-option type-order-limit"
									  onclick="typeOrderBtnChange(this, 0)"><fmt:message
										key="msg.pg.trade.fmtFullLimitOrder"/></span>
								<span class="type-option type-order-plan active"
									  onclick="typeOrderBtnChange(this, 1)"><fmt:message
										key="msg.pg.trade.fmtFullPlanEntrust"/></span>
								<span class="type-option type-order-market"
									  onclick="typeOrderBtnChange(this, 2)"><fmt:message
										key="msg.pg.trade.fmtFullMarketOrder"/></span>
							</div>
						</div>
					</div>
					<c:if test="${homeName ne 'usdr'}">
					<div class="rt" onmouseover="showLeverOptions();" onmouseout="hideLeverOptions();">
						<input type="hidden" name="planMultiple" id="planMultiple" value=""/>
						<div class="lever-order-input">
							<label><fmt:message key="msg.pg.trade.lever" /></label>
							<input type="text" readonly="readonly"  unselectable="on" class="order-input-inner input-inner-new" value=""/>
							<em class="triger-arrow-select triger-arrow-header"></em>
						</div>
						<div class="select-order-item">
							<div class="select-options-box" id="selectPlanOrderBox">
								<c:forEach items="${multiple}" varStatus="l" var="m">
									<c:if test="${m.state==2}"><span class="option-multiple">x${m.multiple}</span></c:if>
									<c:if test="${m.state==1}"><span class="option-multiple" id="planMultiple${m.multiple}" onclick="multiplePlanClick(${m.multiple},this);">x${m.multiple}</span></c:if>
								</c:forEach>
							</div>
						</div>
						<input type="hidden" name="token" id="plan_token" value="${cookie._token.value}"/>
						<div class="que-help que-ggbs" id="ggbs-txt">
							<div class="arrow-top"></div>
							<p><fmt:message key="msg.pg.trade.leverageClose"/></p>
						</div>
					</div>
					</c:if>
					<c:if test="${homeName eq 'usdr'}">
						<div class="rt" onclick="showSliderBlock();">
							<input type="hidden" name="planMultiple" id="planMultiple" value=""/>
							<div class="lever-order-input">
								<label><fmt:message key="msg.pg.trade.lever" /></label>
								<input type="text" readonly="readonly"  unselectable="on" class="order-input-inner input-inner-new" value=""/>
							</div>
							<input type="hidden" name="token" id="plan_token" value="${cookie._token.value}"/>
						</div>
					</c:if>
				</div>
				<!-- 切换计划委托和限价委托按钮start-->
<%--				<div class="trade-detail relative">--%>
<%--					<button type="button" class="sub-btn-entrust-plan" <c:if test="${locale == 'en_US'}">style="margin-right: 9px;"</c:if> id="limited-price"><fmt:message key="msg.pg.trade.fmtFullLimitOrder"/></button>--%>
<%--					<button type="button" class="sub-btn-entrust-plan currentrust" id="plan-entrust"><fmt:message key="msg.pg.trade.fmtFullPlanEntrust"/></button>--%>
<%--					<button type="button" class="sub-btn-entrust-plan" <c:if test="${locale == 'en_US'}">style="padding: 4px 9px;"</c:if> id="market-order" onmouseover="showMarketTipsBox();" onmouseout="hideMarketTipsBox();"><fmt:message key="msg.pg.trade.fmtFullMarketOrder"/></button>--%>
<%--				</div>--%>
				<!-- 切换计划委托和限价委托按钮end -->
				<!-- 触发价格 -->
				<div class="trade-detail relative ie-bug1 ">
					<%-- <span class="label order-label"><fmt:message key="msg.pg.trade.openPrice"/> : </span> --%>
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				  	<input type="text" class="validate[required,number] form-control order-input" value="" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.triPrice"/>"
						id="planTriPrice" name="planTriPrice" onblur="" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});" />
				  	<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				  	<div class="unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
				  	<div class="que-help" id="kcdj-txtt">
						<div class="arrow-top"></div>
						<p><b><fmt:message key="msg.pg.trade.goLong"/> : </b><fmt:message key="msg.pg.trade.ltExponent"/></p>
						<p><b><fmt:message key="msg.pg.trade.goShort"/> : </b><fmt:message key="msg.pg.trade.gtExponent"/></p>
						<p>（<fmt:message key="msg.pg.trade.adjust"/>）</p>
				  	</div>
				  	<!-- 判断价格是否合理 -->
				  	<div class="alert-money display-none">
					<div class="arrow-top"></div>
					<p> <fmt:message key="msg.pg.trade.quoteRole"/>，<fmt:message key="msg.pg.trade.atNow"/><span id="text_alertt" style="margin:0;"><fmt:message key="msg.pg.trade.longPosition"/></span>
					<fmt:message key="msg.pg.trade.priceIs"/> :  <b id="maxBuyPrice1_alert1">7777</b>
					<br><fmt:message key="msg.pg.trade.asSure"/> <b id="maxBuyPrice2_alert2">7777</b> <b class="buy" id="direct_alertt"><fmt:message key="msg.pg.trade.buyIn"/></b>
					<b id="buyAmount_alert">12</b>${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')} </p>
					 <div class="calculat-sub">
					   <button type="button" class="btn sub-btn" id="submit_alertt"><fmt:message key="msg.pg.index.loginSure"/></button>
					   <button type="button" class="btn sub-btn-return" id="close_alertt"><fmt:message key="msg.pg.trade.close"/></button>
					 </div>
				   </div>
				 </div>
			 	<!-- 委托价格 -->
				<div class="trade-detail relative ie-bug1 ">
					<span class="label order-label"><fmt:message key="msg.pg.trade.openPrice"/> : </span>
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				  	<input type="text" class="validate[required,number] form-control order-input" style="width: 197px;<c:if test="${locale == 'en_US'}">width: 140px;"</c:if>" value="" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.entruePrice"/>"
						id="planPrice" name="planPrice" onblur="trade_plan_amount();" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});" />
				  	<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
					<em class="market-price-btn" style="padding: 4px 14px;float: right;" onclick="useMarketPrice(this);"><fmt:message key="msg.pg.trade.planMarketPrice"/></em>
				  	<div class="unit use-unit" style="left: 135px;<c:if test="${locale == 'en_US'}">left: 80px;"</c:if>">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
				  	<div class="que-help" id="kcdj-txtt">
						<div class="arrow-top"></div>
						<p><b><fmt:message key="msg.pg.trade.goLong"/> : </b><fmt:message key="msg.pg.trade.ltExponent"/></p>
						<p><b><fmt:message key="msg.pg.trade.goShort"/> : </b><fmt:message key="msg.pg.trade.gtExponent"/></p>
						<p>（<fmt:message key="msg.pg.trade.adjust"/>）</p>
				  	</div>
				  	<!-- 判断价格是否合理 -->
				  	<div class="alert-money display-none">
					<div class="arrow-top"></div>
					<p> <fmt:message key="msg.pg.trade.quoteRole"/>，<fmt:message key="msg.pg.trade.atNow"/><span id="text_alertt" style="margin:0;"><fmt:message key="msg.pg.trade.longPosition"/></span>
					<fmt:message key="msg.pg.trade.priceIs"/> :  <b id="maxBuyPrice1_alert1">7777</b>
					<br><fmt:message key="msg.pg.trade.asSure"/> <b id="maxBuyPrice2_alert2">7777</b> <b class="buy" id="direct_alertt"><fmt:message key="msg.pg.trade.buyIn"/></b>
					<b id="buyAmount_alertt">12</b>${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')} </p>
					 <div class="calculat-sub">
					   <button type="button" class="btn sub-btn" id="submit_alerts"><fmt:message key="msg.pg.index.loginSure"/></button>
					   <button type="button" class="btn sub-btn-return" id="close_alerts"><fmt:message key="msg.pg.trade.close"/></button>
					 </div>
				   </div>
				 </div>
				 <!-- 数量 -->
				 <div class="trade-detail relative"> 
					<span class="label order-label"><fmt:message key="msg.pg.trade.openPosition"/> : </span>
					<input type="text" style="display: none;"/>
				  <input type="text" class="validate[required,number] form-control order-input" value="" placeholder="<fmt:message key="msg.pg.market.amount"/>"
					id="planAmount" name="planAmount" maxlength="8" onblur="trade_plan_amount();" autocomplete="off" onkeyup="checkInputDigits(this, ${amountDigits});" />
					<input type="text" style="position: absolute; top: -10000px; left: -10000px; width: 0px; height: 0px;border: 0px; padding: 0;"/>
				  <div class="unit"><fmt:message key="msg.pg.trade.ge"/></div> 
				</div>
				<!-- 百分比 -->
				<!-- <div class="trade-detail relative">
					<span class="percent">25%</span>
					<span class="percent">50%</span>
					<span class="percent">75%</span>
					<span class="percent">100%</span>
					<div class="clear"></div>
				</div> -->
				 <!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
				<div class="trade-detail" style="${payPwd ? 'display: block;' : 'display: none;' }">
				  <span class="label order-label"><fmt:message key="msg.pg.trade.assetPwd"/> : </span>
				  <input type="password" autocomplete="new-password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				  <input type="password" autocomplete="new-password" placeholder="<fmt:message key="msg.pg.trade.assetPwd"/>" class="form-control order-input" value="" id="planPayPwd" name="planPayPwd" maxlength="20" autocomplete="off"/>
				  <input type="password" autocomplete="new-password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				</div>
				<%--可用 & 资金划转 -开始--%>
				<div class="trade-detail tog-ht" style="margin-bottom: 8px;">
					<div class="toggle-wrap relative clearfix">
						<div class="lt">
							<!-- 合约可用 -->
							<span class="label"> <em id="toggle-em" class="show-tips-box" onmouseover="showTipsBox();" onmouseout="hideTipsBox();"><i class="icon icons-show"></i><fmt:message key="msg.pg.trade.useful"/></em> </span>
							<em class="showTipsColon">:</em>
							<b id="planTotalamount">${userAccountBtc }</b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b>&nbsp;&nbsp;
							<a class="a-btn" onclick="fundsTransferBox('${currentTransaction.transaction_code}', '${homeName=='vtrade' ? 'vcontract' : homeName=='dtrade' ? 'delivery' : (homeName=='trade' ? 'contract' : homeName)}');">
								<fmt:message key="msg.pg.trade.transfer"/></a>
						</div>
						<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'br') }">
							<div class="sz-hint-block rt">
								<b class="sell" id="sz-hintt"><fmt:message key="msg.pg.trade.notes"/><i class="icons-hint"></i></b>
								<div class="que-help" id="sz-hint-help">
									<div class="arrow-top"></div>
									<p><fmt:message key="msg.pg.trade.notesBull"/>
									</p>
								</div>
							</div>
						</c:if>
						<div class="clear"></div>
					</div>
					<div class="toggle-main">
						<p><fmt:message key="msg.pg.trade.canRollOut"/> : <b id="planSettleamount">${userAccountBtc } </b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b>
							<a href="<%=basePath %>transferAccount/${homeName=='vtrade' ? 'vtransfer.do' : homeName=='dtrade' ? 'dtransfer.do' : 'transfer.do'}?type=2&bqCode=${currentTransaction.transaction_code}" class="a-btn"><fmt:message key="msg.pg.trade.rollOut"/></a> </p>
						<p style="display: none;"><b id="otherAmount"></b></p>
					</div>
				</div>
				<%--可用 & 资金划转 -结束--%>
				<div class="trade-detail relative half-width-label">
				  <div class="lt"> <span class="label"><fmt:message key="msg.pg.trade.depositCash"/> : </span><b id="cash_deposit">0</b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b> </div>
				</div>
				<div class="trade-detail relative half-width-label">
				  <div class="lt" style="padding-left: 5px;"> <span class="label"><fmt:message key="msg.pg.trade.canOpen"/> : </span><b id="planMaxAmount">0</b><b> <fmt:message key="msg.pg.trade.ge"/></b> </div>
				</div>
				<div class="clear"></div>
			
				<div class="trade-alert suspend-div" <c:if test="${!currentTransaction.suspend && isTradeTime}">style='display:none;'</c:if>>
					<div class="warning suspend-text">
						<c:if test="${currentTransaction.suspend}"><fmt:message key='msg.trade.pause'/></c:if>
						<c:if test="${!currentTransaction.suspend && !isTradeTime}"><fmt:message key='msg.trade.noTradeTime'/></c:if>
					</div>
				</div>
				<!-- 买多卖空按钮 -->
				<div class="normal-trade-div" <c:if test="${currentTransaction.suspend || !isTradeTime}">style='display:none;'</c:if>>
					<div class="trade-btn-box">
						<input type="hidden" id="planDirection" name="planDirection" value="">
					  <div class="trade-btn-wrap" id="buy">
						<button type="button" id="btnBtcPlan" class="btn trade-btn buy-bg" onclick="return del_sure_Plantrade('<%=basePath %>${homeName=='vtrade' ? 'vtradeAjax' : homeName=='dtrade' ? 'dtradeAjax' : (homeName=='trade' ? 'tradeAjax' : ('ntradeAjax/'.concat(homeName)))}/orderPlanPending.do', '${typeCode.toLowerCase()}', 1);"><fmt:message key="msg.pg.trade.buyMore2"/></button>
					  </div>
					  <div class="trade-btn-wrap" id="sell">
						<button type="button" id="sellBtcPlan" class="btn trade-btn sell-bg" onclick="return del_sure_Plantrade('<%=basePath %>${homeName=='vtrade' ? 'vtradeAjax' : homeName=='dtrade' ? 'dtradeAjax' : (homeName=='trade' ? 'tradeAjax' : ('ntradeAjax/'.concat(homeName)))}/orderPlanPending.do', '${typeCode.toLowerCase()}', 2);"><fmt:message key="msg.pg.trade.sellEmpty2"/></button>
					  </div>
					</div>
					<div class="price-item clearfix half-width-label"> 
						<span><fmt:message key="msg.pg.trade.maxPriceIs"/><b class="buy">${maxBuy }</b><b class="buy"> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b></span> 
					</div>
					<div class="price-item clearfix half-width-label">
						<span class="sell-span"><fmt:message key="msg.pg.trade.minPriceIs"/><b class="sell">${minSell }</b><b class="sell"> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b></span>
					</div>
					<div class="clear"></div>
				</div>
			
				<!-- <div class="price-item clearfix"> <span id="prompt_buy">下跌至 <b>0</b> 您将爆仓</span> <span id="prompt_sell">上涨至 <b>0</b> 您将爆仓</span> </div> -->
			</div>
		</div>
	</form>
</div>