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
<div class="lt-wrap order-panel" id="full-order" <c:if test="${isMarket }"> style="height: auto;"</c:if>>

  <form action="" name="topSearchForm" id="topSearchForm" autocomplete="off">
	<input type="hidden" name="btc_index_price" id="btc_index_price" value="${btc_index_price}">
		<div class="trade-main" <c:if test="${isMarket }"> style="height: auto;"</c:if>>
			<div class="trade-box">
				<c:if test="${sessionScope.userinfo!=null}">
					<input type="hidden" id="userEmail" name="userEmail" value="${sessionScope.userinfo.f_user_email}">
				</c:if>
				
				<input type="hidden" name="orderType" value="" />
				<input type="hidden" id="transaction_code" name="transaction_code" value="${currentTransaction.transaction_code}" />
				<input type="hidden" id="transaction_type_code" name="transaction_type_code" value="${currentTransaction.transaction_type_code}" />
				<input type="hidden" id="minKcNums" name="minKcNums" value="${minKcNums}">
	  			<input type="hidden" id="minFloatPrice" name="minFloatPrice" value="${minFloatPrice}">
				<input type="hidden" id="myRemainBtc" name="myRemainBtc" value="${userAccountBtc}">
				<input type="hidden" id="myRemainOther" name="myRemainOther" value="0">
				<input type="hidden" id="myRemainGainBtc" name="myRemainBtc" value="0">
				<input type="hidden" id="myDigits" name="myDigits" value="${digits}">
	  			<input type="hidden" id="myCommissionRate" name="myCommissionRate" value="${myCommissionRate}">
	  			<input type="hidden" id="pendingRate" name="pendingRate" value="${pendingRate}">
	  			<input type="hidden" id="amountDigits" name="amountDigits" value="${amountDigits}">
	  			<input type="hidden" id="suspend" name="suspend" value="${currentTransaction.suspend}">
	  			<input type="hidden" id="tradePath" name="tradePath" value="${homeName}">
	  			<input type="hidden" id="coeffcient" name="coeffcient" value="${coeffcient}">
				<input type="hidden" id="sellOneNum"  value="${selltop5List.size() > 0 ? selltop5List.get(selltop5List.size() - 1).price : ''}">
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
				<!-- 杠杆start -->
				<div class="trade-detail relative ie-bug">
					<div class="lt" onmouseover="showOrderOptions();" onmouseout="hideOrderOptions();">
						<div class="type-order-input">
							<input type="text" readonly="readonly" unselectable="on" class="type-order-inner input-inner-new"
								   value="<fmt:message key="msg.pg.trade.fmtFullLimitOrder"/>"/>
							<em class="triger-arrow-select triger-arrow-order"></em>
						</div>
						<div class="type-options-order">
							<div class="type-order-box">
								<span class="type-option type-order-limit active"
									  onclick="typeOrderBtnChange(this, 0)"><fmt:message
										key="msg.pg.trade.fmtFullLimitOrder"/></span>
								<span class="type-option type-order-plan"
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
							<input type="hidden" name="multiple" id="multiple" value=""/>
							<div class="lever-order-input">
								<label><fmt:message key="msg.pg.trade.lever" /></label>
								<input type="text" readonly="readonly"  unselectable="on" class="order-input-inner input-inner-new" value=""/>
								<em class="triger-arrow-select triger-arrow-header"></em>
							</div>
							<div class="select-order-item">
								<div class="select-options-box" id="selectOrderBox">
									<c:forEach items="${multiple}" varStatus="l" var="m">
										<c:if test="${m.state==2}"><span class="option-multiple">x${m.multiple}</span></c:if>
										<c:if test="${m.state==1}"><span class="option-multiple" id="multiple${m.multiple}" onclick="multipleClick(${m.multiple},this);">x${m.multiple}</span></c:if>
									</c:forEach>
								</div>
							</div>
							<input type="hidden" name="token" id="token" value="${cookie._token.value}"/>
							<div class="que-help que-ggbs" id="ggbs-txt">
								<div class="arrow-top"></div>
								<p><fmt:message key="msg.pg.trade.leverageClose"/></p>
							</div>
						</div>
					</c:if>
					<c:if test="${homeName eq 'usdr'}">
						<div class="rt slider-block" onclick="showSliderBlock();">
							<input type="hidden" name="multiple" id="multiple" value=""/>
							<div class="lever-order-input">
								<label><fmt:message key="msg.pg.trade.lever" /></label>
								<input type="text" readonly="readonly"  unselectable="on" class="order-input-inner input-inner-new" value=""/>
							</div>
							<input type="hidden" name="token" id="token" value="${cookie._token.value}"/>
						</div>
					</c:if>
				</div>
		<!-- 杠杆end -->
		<!-- 切换计划委托和限价委托按钮start-->
<%--		<div class="trade-detail relative">--%>
<%--			<button type="button" class="sub-btn-entrust currentrust" <c:if test="${locale == 'en_US'}">style="margin-right: 9px;"</c:if> id="limitedPrice"><fmt:message key="msg.pg.trade.fmtFullLimitOrder"/></button>--%>
<%--			<button type="button" class="sub-btn-entrust" id="planEntrust"><fmt:message key="msg.pg.trade.fmtFullPlanEntrust"/></button>--%>
<%--			<button type="button" class="sub-btn-entrust" <c:if test="${locale == 'en_US'}">style="padding: 4px 9px;"</c:if> id="marketOrder" onmouseover="showMarketTipsBox();" onmouseout="hideMarketTipsBox();"><fmt:message key="msg.pg.trade.fmtFullMarketOrder"/></button>--%>
<%--		</div>--%>
		<!-- 切换计划委托和限价委托按钮end -->
		<div class="trade-detail relative ie-bug1 "> 
			<span class="label order-label"><fmt:message key="msg.pg.trade.openPrice"/> : </span>
			<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
		  	<input type="text" class="validate[required,number] form-control order-input" value="" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.openPrice"/>"
				id="price" name="price" onblur="trade_price();" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});trade_price();" />
		  	<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
		  	<div class="unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
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
			   <button type="button" class="btn sub-btn" id="submit_alert"><fmt:message key="msg.pg.index.loginSure"/></button>
			   <button type="button" class="btn sub-btn-return" id="close_alert"><fmt:message key="msg.pg.trade.close"/></button>
			 </div>
		   </div>
		 </div>
		 <div class="trade-detail relative"> 
			<span class="label order-label"><fmt:message key="msg.pg.trade.openPosition"/> : </span>
			<input type="text" style="display: none;"/>
		  <input type="text" class="validate[required,number] form-control order-input" value="" placeholder="<fmt:message key="msg.pg.trade.openPosition"/>"
			id="amount" name="amount" maxlength="8" onblur="trade_amount();" autocomplete="off" onkeyup="checkInputDigits(this, ${amountDigits});trade_amount();" />
			<input type="text" style="position: absolute; top: -10000px; left: -10000px; width: 0px; height: 0px;border: 0px; padding: 0;"/>
		  <div class="unit"><fmt:message key="msg.pg.trade.ge"/></div> 
		</div>
		<div class="trade-detail relative trade-percent-btn">
			<span class="percent">25%</span>
			<span class="percent">50%</span>
			<span class="percent">75%</span>
			<span class="percent">100%</span>
			<div class="clear"></div>
		</div>
		 <!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
		<div class="trade-detail" style="${payPwd ? 'display: block;' : 'display: none;' }">
		  <span class="label order-label"><fmt:message key="msg.pg.trade.assetPwd"/> : </span>
		  <input type="password" autocomplete="new-password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
		  <input type="password" autocomplete="new-password" placeholder="<fmt:message key="msg.pg.trade.assetPwd"/>" class="form-control order-input" value="" id="payPd" name="payPwd" maxlength="20" autocomplete="off"/>
		  <input type="password" autocomplete="new-password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
		</div>
		<%--可用 & 资金划转 -开始--%>
		<div class="trade-detail tog-ht" style="margin-bottom: 8px;">
			<div class="toggle-wrap relative clearfix">
				<div class="lt">
			  <span class="label"> <em id="toggle-em" class="show-tips-box" onmouseover="showTipsBox();" onmouseout="hideTipsBox();"><i class="icon icons-show"></i>
				 <fmt:message key="msg.pg.trade.useful"/>
				 </em>
			   </span><em class="showTipsColon">:</em>
					<b id="totalamount">${userAccountBtc }</b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b>&nbsp;&nbsp;
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
				<p><!-- ${fn:toUpperCase(currentTransaction.transaction_code)}待结算盈利 :  -->
					<fmt:message key="msg.pg.trade.canRollOut"/> : <b id="settleamount">${userAccountBtc } </b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b>
					<a href="<%=basePath %>transferAccount/${homeName=='vtrade' ? 'vtransfer.do' : homeName=='dtrade' ? 'dtransfer.do' : 'transfer.do'}?type=2&bqCode=${currentTransaction.transaction_code}" class="a-btn"><fmt:message key="msg.pg.trade.rollOut"/></a> </p>
				<p style="display: none;"><b id="otherAmount"></b></p>
			</div>
		</div>
		<%--可用 & 资金划转 -结束--%>
		<div class="trade-detail relative half-width-label">
		  <div class="lt"> <span class="label"><fmt:message key="msg.pg.trade.depositCash"/> : </span><b id="baozhengjin">0</b><b> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b> </div>
		</div>
		<div class="trade-detail relative half-width-label">
		  <div class="lt" style="padding-left: 5px;"> <span class="label"><fmt:message key="msg.pg.trade.canOpen"/> : </span><b id="maxAmount">0</b><b> <fmt:message key="msg.pg.trade.ge"/></b> </div>
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
				<input type="hidden" id="direction" name="direction" value="">
				<div class="trade-btn-wrap" id="buy">
					<button type="button" id="btnBtc" class="btn trade-btn buy-bg" onclick="return del_sure_trade('<%=basePath %>${homeName=='vtrade' ? 'vtradeAjax' : homeName=='dtrade' ? 'dtradeAjax' : (homeName=='trade' ? 'tradeAjax' : ('ntradeAjax/'.concat(homeName)))}/tradeTrans.do', '${typeCode.toLowerCase()}', 1, 1);"><fmt:message key="msg.pg.trade.buyMore2"/></button>
				</div>
				<div class="trade-btn-wrap" id="sell">
					<button type="button" id="sellBtc" class="btn trade-btn sell-bg" onclick="return del_sure_trade('<%=basePath %>${homeName=='vtrade' ? 'vtradeAjax' : homeName=='dtrade' ? 'dtradeAjax' : (homeName=='trade' ? 'tradeAjax' : ('ntradeAjax/'.concat(homeName)))}/tradeTrans.do', '${typeCode.toLowerCase()}', 2, 1);"><fmt:message key="msg.pg.trade.sellEmpty2"/></button>
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
		
		<!-- <div class="price-item clearfix"> <span id="prompt_buy">下跌至 <b>0</b> 您将爆仓</span> <span id="prompt_sell">上涨至 <b>0</b> 您将爆仓</span> </div> -->
	  </div>
	</div>
  </form>
</div>
<script type="text/javascript">
	$(".trade-percent-btn span").on('click', function() {
		if(!$(this).hasClass("active")){
			$(".trade-percent-btn span").removeClass("active");
			$(this).addClass("active");
		}else {
			$(this).removeClass("active");
		}

		var percent = $(this).html();
		var float = percent.replace("%","");
		float = float / 100;

		var canOpen = parseFloat($("#maxAmount").html());
		if (!isNaN(canOpen)) {
			var amountDigits = Number($('#amountDigits').val());
			var amount = float * canOpen;
			var maxStr = amount + "";
			if (maxStr.indexOf('.') != -1 && maxStr.length - maxStr.indexOf('.') > amountDigits) {
				maxStr = maxStr.substring(0, maxStr.indexOf('.') + amountDigits + 1);
			}
			$("#amount").val(maxStr);
		}

		trade_amount(true);
	});
</script>