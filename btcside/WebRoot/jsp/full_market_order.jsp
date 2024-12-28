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
<div class="lt-wrap order-panel" id="full-market-order" style="display:none;" <c:if test="${isMarket }"> style="height: auto;"</c:if>>

  <form action="" name="topMarketSearchForm" id="topMarketSearchForm" autocomplete="off">
	<input type="hidden" name="btc_index_price" id="btc_index_price_val" value="${btc_index_price}">
		<div class="trade-main" <c:if test="${isMarket }"> style="height: auto;"</c:if>>
			<div class="trade-box">
				<c:if test="${sessionScope.userinfo!=null}">
					<input type="hidden" id="marketUserEmail" name="userEmail" value="${sessionScope.userinfo.f_user_email}">
				</c:if>
				
				<input type="hidden" name="orderType" value="1" />
				<input type="hidden" id="market_transaction_code" name="transaction_code" value="${currentTransaction.transaction_code}" />
				<input type="hidden" id="market_transaction_type_code" name="transaction_type_code" value="${currentTransaction.transaction_type_code}" />
				<input type="hidden" id="market_minKcNums" name="minKcNums" value="${minKcNums}">
	  			<input type="hidden" id="market_minFloatPrice" name="minFloatPrice" value="${minFloatPrice}">
				<input type="hidden" id="market_myRemainBtc" name="myRemainBtc" value="${userAccountBtc}">
				<input type="hidden" id="market_myRemainOther" name="myRemainOther" value="0">
				<input type="hidden" id="market_myRemainGainBtc" name="myRemainGainBtc" value="0">
				<input type="hidden" id="market_myDigits" name="myDigits" value="${digits}">
	  			<input type="hidden" id="market_myCommissionRate" name="myCommissionRate" value="${myCommissionRate}">
	  			<input type="hidden" id="market_pendingRate" name="pendingRate" value="${pendingRate}">
	  			<input type="hidden" id="market_amountDigits" name="amountDigits" value="${amountDigits}">
	  			<input type="hidden" id="market_suspend" name="suspend" value="${currentTransaction.suspend}">
	  			<input type="hidden" id="market_tradePath" name="tradePath" value="${homeName}">
	  			<input type="hidden" id="market_coeffcient" name="coeffcient" value="${coeffcient}">
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
				<div class="show-marketTip-box">
				    <div class="triangle"></div>
					<div class="index-info-detail"><fmt:message key="msg.pg.trade.marketTips"/></div>
				</div>
				<span class="underline"></span>
				<c:if test="${locale == 'en_US'}">
					<span class="underline" style="width: 74px;left: 200px;border-top: 1px dashed #4980e3;"></span>
				</c:if>

				<!-- 杠杆start -->
				<div class="trade-detail relative ie-bug">
					<div class="lt" onmouseover="showOrderOptions();" onmouseout="hideOrderOptions();">
						<div class="type-order-input">
							<input type="text" readonly="readonly" unselectable="on" class="type-order-inner market-order-inner input-inner-new"
								   value="<fmt:message key="msg.pg.trade.fmtFullMarketOrder"/>"/>
							<em class="triger-arrow-select triger-arrow-order"></em>
						</div>
						<div class="type-options-order">
							<div class="type-order-box">
								<span class="type-option type-order-limit"
									  onclick="typeOrderBtnChange(this, 0)"><fmt:message
										key="msg.pg.trade.fmtFullLimitOrder"/></span>
								<span class="type-option type-order-plan"
									  onclick="typeOrderBtnChange(this, 1)"><fmt:message
										key="msg.pg.trade.fmtFullPlanEntrust"/></span>
								<span class="type-option type-order-market active"
									  onclick="typeOrderBtnChange(this, 2)"><fmt:message
										key="msg.pg.trade.fmtFullMarketOrder"/></span>
							</div>
						</div>
					</div>
					<c:if test="${homeName ne 'usdr'}">
						<div class="rt" onmouseover="showLeverOptions();" onmouseout="hideLeverOptions();">
							<div class="lever-order-input">
								<label><fmt:message key="msg.pg.trade.lever" /></label>
								<input type="text" readonly="readonly"  unselectable="on" class="order-input-inner input-inner-new" value=""/>
								<em class="triger-arrow-select triger-arrow-header"></em>
							</div>
							<div class="select-order-item">
								<div class="select-options-box" id="selectMarketOrderBox">
									<c:forEach items="${multiple}" varStatus="l" var="m">
										<c:if test="${m.state==2}"><span class="option-multiple">x${m.multiple}</span></c:if>
										<c:if test="${m.state==1}"><span class="option-multiple" id="market_multiple${m.multiple}" onclick="multipleMarketClick(${m.multiple},this);">x${m.multiple}</span></c:if>
									</c:forEach>
								</div>
							</div>
							<input type="hidden" name="token" id="market_token" value="${cookie._token.value}"/>
							<div class="que-help que-ggbs" id="ggbs-txt">
								<div class="arrow-top"></div>
								<p><fmt:message key="msg.pg.trade.leverageClose"/></p>
							</div>
						</div>
					</c:if>
					<c:if test="${homeName eq 'usdr'}">
						<div class="rt" onclick="showSliderBlock();">
							<input type="hidden" name="multiple" id="market_multiple" value=""/>
							<div class="lever-order-input">
								<label><fmt:message key="msg.pg.trade.lever" /></label>
								<input type="text" readonly="readonly"  unselectable="on" class="order-input-inner input-inner-new" value=""/>
							</div>
							<input type="hidden" name="token" id="market_token" value="${cookie._token.value}"/>
						</div>
					</c:if>
				</div>
				<!-- 杠杆end -->
				<!-- 切换计划委托和限价委托按钮start-->
<%--				<div class="trade-detail relative">--%>
<%--					<button type="button" class="sub-btn-entrust-market" <c:if test="${locale == 'en_US'}">style="margin-right: 9px;"</c:if> id="limitedPriceTitle"><fmt:message key="msg.pg.trade.fmtFullLimitOrder"/></button>--%>
<%--					<button type="button" class="sub-btn-entrust-market" id="planEntrustTitle"><fmt:message key="msg.pg.trade.fmtFullPlanEntrust"/></button>--%>
<%--					<button type="button" <c:if test="${locale == 'en_US'}">style="padding: 4px 9px;"</c:if> class="sub-btn-entrust-market currentrust" id="marketOrderTitle" onmouseover="showMarketTipsBox();" onmouseout="hideMarketTipsBox();"><fmt:message key="msg.pg.trade.fmtFullMarketOrder"/></button>--%>
<%--				</div>--%>
				<!-- 切换计划委托和限价委托按钮end -->
				<div class="trade-detail relative ie-bug1 "> 
					<span class="label order-label"><fmt:message key="msg.pg.trade.openPrice"/> : </span>
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				  	<input type="text" class="validate[required,number] form-control order-input-disabled" style="background-color: #1B2737;" value="" placeholder="<fmt:message key="msg.pg.trade.bestPriceOrder"/>"
						id="marketPrice" name="price" disabled="disabled"/>
				  	<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				  	<%-- <div class="unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div> --%>
				  	<div class="que-help" id="kcdj-txt">
						<div class="arrow-top"></div>
						<p><b><fmt:message key="msg.pg.trade.goLong"/> : </b><fmt:message key="msg.pg.trade.ltExponent"/></p>
						<p><b><fmt:message key="msg.pg.trade.goShort"/> : </b><fmt:message key="msg.pg.trade.gtExponent"/></p>
						<p>（<fmt:message key="msg.pg.trade.adjust"/>）</p>
				  	</div>
				  	<!-- 判断价格是否合理 -->
				  	<div class="alert-money display-none">
					<div class="arrow-top"></div>
					<p> <fmt:message key="msg.pg.trade.quoteRole"/>，<fmt:message key="msg.pg.trade.atNow"/><span id="text_alert" style="margin:0;"><fmt:message key="msg.pg.trade.longPosition"/></span>
					<fmt:message key="msg.pg.trade.priceIs"/> :  <b id="maxBuyPrice1_alert">7777</b> 
					<br><fmt:message key="msg.pg.trade.asSure"/> <b id="maxBuyPrice2_alert">7777</b> <b class="buy" id="direct_alert"><fmt:message key="msg.pg.trade.buyIn"/></b> 
					<b id="buyAmount_alert">12</b>${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')} </p>
					 <div class="calculat-sub">
					   <button type="button" class="btn sub-btn" id="market_submit_alert"><fmt:message key="msg.pg.index.loginSure"/></button>
					   <button type="button" class="btn sub-btn-return" id="market_close_alert"><fmt:message key="msg.pg.trade.close"/></button>
					 </div>
				   </div>
				 </div>
				 <div class="trade-detail relative"> 
					<span class="label order-label"><fmt:message key="msg.pg.trade.openPosition"/> : </span>
					<input type="text" style="display: none;"/>
				  <input type="text" class="validate[required,number] form-control order-input" value="" placeholder="<fmt:message key="msg.pg.market.amount"/>"
					id="marketAmount" name="amount" maxlength="8" onblur="trade_market_price();" autocomplete="off" onkeyup="checkInputDigits(this, ${amountDigits});"/>
					<input type="text" style="position: absolute; top: -10000px; left: -10000px; width: 0px; height: 0px;border: 0px; padding: 0;"/>
				  <div class="unit"><fmt:message key="msg.pg.trade.ge"/></div> 
				</div>
				<input type="hidden" id="percent" name="percent" value="">
				<div class="trade-detail relative trade-market-percent">
					<span class="market-percent">25%</span>
					<span class="market-percent">50%</span>
					<span class="market-percent">75%</span>
					<span class="market-percent">100%</span>
					<div class="clear"></div>
				</div>
				 <!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
				<div class="trade-detail" style="${payPwd ? 'display: block;' : 'display: none;' }">
				  <span class="label order-label"><fmt:message key="msg.pg.trade.assetPwd"/> : </span>
				  <input type="password" autocomplete="new-password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				  <input type="password" autocomplete="new-password" placeholder="<fmt:message key="msg.pg.trade.assetPwd"/>" class="form-control order-input" value="" id="marketPayPd" name="payPwd" maxlength="20" autocomplete="off"/>
				  <input type="password" autocomplete="new-password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				</div>
				<%--可用 & 资金划转 -开始--%>
				<div class="trade-detail tog-ht" style="margin-bottom: 8px;">
					<div class="toggle-wrap relative clearfix">
						<div class="lt">
							<span class="label">
					  			<em id="toggle-em" class="show-tips-box" onmouseover="showTipsBox();" onmouseout="hideTipsBox();">
					  				<i class="icon icons-show"></i>
					 	 			<fmt:message key="msg.pg.trade.useful"/>
					 	 		</em>
							</span>
							<em class="showTipsColon">:</em>
							<b id="marketTotalamount">${userAccountBtc }</b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b>&nbsp;&nbsp;
							<a class="a-btn" onclick="fundsTransferBox('${currentTransaction.transaction_code}', '${homeName=='vtrade' ? 'vcontract' : homeName=='dtrade' ? 'delivery' : (homeName=='trade' ? 'contract' : homeName)}');"><fmt:message key="msg.pg.trade.transfer"/></a>
						</div>

						<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'br') }">
							<div class="sz-hint-block rt">
								<b class="sell" id="sz-hint"><fmt:message key="msg.pg.trade.notes"/><i class="icons-hint"></i></b>
								<div class="que-help" id="sz-hint-help">
									<div class="arrow-top"></div>
									<p><fmt:message key="msg.pg.trade.notesBull"/>
									</p>
								</div>
							</div>
						</c:if>
						<div class="clear"></div>
					</div>
					<div class="toggle-main" id="toggle-main">
						<p><fmt:message key="msg.pg.trade.canRollOut"/> : <b id="settleamount">${userAccountBtc } </b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b>
							<a href="<%=basePath %>transferAccount/${homeName=='vtrade' ? 'vtransfer.do' : homeName=='dtrade' ? 'dtransfer.do' : 'transfer.do'}?type=2&bqCode=${currentTransaction.transaction_code}" class="a-btn"><fmt:message key="msg.pg.trade.rollOut"/></a> </p>
						<p style="display: none;"><b id="otherAmount"></b></p>
					</div>
				</div>
				<%--可用 & 资金划转 -结束--%>
				<div class="trade-detail relative half-width-label">
				  <div class="lt"> <span class="label"><fmt:message key="msg.pg.trade.depositCash"/> : </span><b id="baozhengjinMarket">0</b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b> </div>
				</div>
				<div class="trade-detail relative half-width-label">
				  <div class="lt" style="padding-left: 5px;"> <span class="label"><fmt:message key="msg.pg.trade.canOpen"/> : </span><b id="maxMarketAmount">0</b><b> <fmt:message key="msg.pg.trade.ge"/></b> </div>
				</div>
				<div class="clear"></div>
				
				<div class="trade-alert suspend-div" <c:if test="${!currentTransaction.suspend && isTradeTime}">style='display:none;'</c:if>>
					<div class="warning suspend-text">
						<c:if test="${currentTransaction.suspend}"><fmt:message key='msg.trade.pause'/></c:if>
						<c:if test="${!currentTransaction.suspend && !isTradeTime}"><fmt:message key='msg.trade.noTradeTime'/></c:if>
					</div>
				</div>
				<div class="normal-trade-div" <c:if test="${currentTransaction.suspend || !isTradeTime}">style='display:none;'</c:if>>
					<div class="trade-btn-box">
						<input type="hidden" id="market_direction" name="direction" value="">
						<div class="trade-btn-wrap" id="buy">
							<button type="button" id="marketBtnBtc" class="btn trade-btn buy-bg" onclick="return del_sure_trade('<%=basePath %>${homeName=='vtrade' ? 'vtradeAjax' : homeName=='dtrade' ? 'dtradeAjax' : (homeName=='trade' ? 'tradeAjax' : ('ntradeAjax/'.concat(homeName)))}/tradeTrans.do', '${typeCode.toLowerCase()}', 1, 2);"><fmt:message key="msg.pg.trade.buyMore2"/></button>
						</div>
						<div class="trade-btn-wrap" id="sell">
							<button type="button" id="marketSellBtc" class="btn trade-btn sell-bg" onclick="return del_sure_trade('<%=basePath %>${homeName=='vtrade' ? 'vtradeAjax' : homeName=='dtrade' ? 'dtradeAjax' : (homeName=='trade' ? 'tradeAjax' : ('ntradeAjax/'.concat(homeName)))}/tradeTrans.do', '${typeCode.toLowerCase()}', 2, 2);"><fmt:message key="msg.pg.trade.sellEmpty2"/></button>
						</div>
					</div>
					<div class="price-item clearfix half-width-label"> 
						<span><fmt:message key="msg.pg.trade.maxPriceIs"/><b class="buy" id="maxBuy">${maxBuy }</b><b class="buy"> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b></span> 
					</div>
					<div class="price-item clearfix half-width-label">
						<span class="sell-span"><fmt:message key="msg.pg.trade.minPriceIs"/><b class="sell" id="minSell">${minSell }</b><b class="sell"> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b></span>
					</div>
					<div class="clear"></div>
				</div>
	  		</div>
		</div>
  	</form>
</div>
<script type="text/javascript">
	$(".trade-market-percent span").on('click', function() {
		var marketPrice = Number($('#lastPriceSpan').html());
		var marketMultiple = Number($('#market_multiple').val());
		var marketCoeffcient = Number($('#market_coeffcient').val());
		var marketMyRemainAmount = Number($('#market_myRemainBtc').val());
		var marketRemainAmount = marketMyRemainAmount;
		var marketAmountDigits = Number($('#market_amountDigits').val());

		clearInterval(marketInterval);
		marketInterval = null;

		var percent = $(this).html();
		var float = percent.replace("%","");
		float = float / 100;
		$("#percent").val(percent);

		var maxAmount = 0;
		var feeRate = Number($('#market_myCommissionRate').val());						// 佣金（手续费）
		maxAmount = marketRemainAmount / (marketPrice / marketMultiple + marketPrice * feeRate) / marketCoeffcient;	// 最大可开仓数量
		
		var maxStr = maxAmount + "";
		if (maxStr.indexOf('.') != -1 && maxStr.length - maxStr.indexOf('.') > marketAmountDigits) {
			maxStr = maxStr.substring(0, maxStr.indexOf('.') + marketAmountDigits + 1);
		}
		maxAmount = Number(maxStr);
		$('#maxMarketAmount').html(maxAmount);//最大可开数量
		
		var canOpen = parseFloat($("#maxMarketAmount").html());
		if (!isNaN(canOpen)) {
			var amount = float * canOpen;
			var maxStr = amount + "";
			if (maxStr.indexOf('.') != -1 && maxStr.length - maxStr.indexOf('.') > marketAmountDigits) {
				maxStr = maxStr.substring(0, maxStr.indexOf('.') + marketAmountDigits + 1);
			}
			$('#baozhengjinMarket').html((marketPrice * (maxStr / marketMultiple) * marketCoeffcient).toFixed(4));
		}

		if (IsWhite) {
			$("#marketAmount").attr({'value': $("#percent").val()}).css({'background-color' : '#f6fafd'});//'color' : '#94abc0'
		} else {
			$("#marketAmount").attr({'value': $("#percent").val()}).css({'background-color' : '#1B2737'});//'color' : '#4A5F78'
		}

		if(!$(this).hasClass("active")){
			$(".trade-market-percent span").removeClass("active");
			$(this).addClass("active");
		} else {
			$(this).removeClass("active");
			if (IsWhite) {
				$("#marketAmount").attr({'value': ''}).css('background-color', '#ffffff');
			} else {
				$("#marketAmount").attr({'value': ''}).css('background-color', '#0d1923');
			}

			$("#percent").val('');
			return;
		}
	});
	
	$("#marketAmount").hover(function() {
		$(this).css('border', '1px solid #4980E3');
	}, function() {
		$(this).css('border', '1px solid #4A5F78');
	})
	$("#marketAmount").focus(function() {
		if($(".trade-market-percent span").hasClass("active")) {
			$(".trade-market-percent span").removeClass("active");
			if (IsWhite) {
				$(this).attr({'value': ''}).css('background-color', '#ffffff');
			} else {
				$(this).attr({'value': ''}).css('background-color', '#0d1923');
			}
			$("#percent").val('');
			return;
		}
    });
</script>