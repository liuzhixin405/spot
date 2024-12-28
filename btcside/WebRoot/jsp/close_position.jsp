<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<div class="lt-wrap order-panel" style="display:none;" id="close-sell-position">
	<form action="" id="closePlanForm">
		<div class="trade-main">
			<div class="trade-box">
				<!-- 持仓均价 -->
				<%-- <div class="trade-detail tog-ht">
					<div class="toggle-wrap relative clearfix">
						<div class="lt">
						  <span class="label"> <em id="toggle-em"><i class="icon icons-show"></i><fmt:message key="msg.pg.tradeQuery.holdAvgPrice"/> : </em> </span>
						  <b id="holdSellAvgPrice">
						  	<c:if test="${sellMemberTransaction == null || empty sellMemberTransaction}">
						  		0
						  	</c:if>
						  	<c:if test="${sellMemberTransaction != null && sellMemberTransaction.size() > 0}">
						  		${sellMemberTransaction[0].averagePrice}
						  	</c:if>
						  </b><b> ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b>
						</div>
					  <div class="clear"></div>
					</div>
				</div> --%>
				<input type="hidden" name="orderType" id="closeOrderType" value="" />
				<input type="hidden" id="closePlanCode" name="planCode" value="${currentTransaction.transaction_code}" />
				<input type="hidden" name="planOrderType" id="closePlanOrder" value="" />
				<!-- 市价平仓提示 -->
				<div class="show-marketCloseTip-box hide">
				    <div class="triangle"></div>
					<div class="index-info-detail"><fmt:message key="msg.pg.trade.marketCloseTips"/></div>
				</div>
				<span class="market-underline" <c:if test="${locale == 'en_US' || locale == 'ja_JP'}">style="width: 74px;left: 183px;"</c:if>></span>
				<input type="hidden" id="sellCloseMId" 
					<c:if test="${sellMemberTransaction == null || empty sellMemberTransaction}">
				  		value="0"
				  	</c:if>
				  	<c:if test="${sellMemberTransaction != null && sellMemberTransaction.size() > 0}">
				  		value = "${sellMemberTransaction[0].mt_id}"
				  	</c:if>/>
				  	
				<input type="hidden" id="sellCloseMultiple"
					<c:if test="${sellMemberTransaction == null || empty sellMemberTransaction}">
				  		value="0"
				  	</c:if>
				  	<c:if test="${sellMemberTransaction != null && sellMemberTransaction.size() > 0}">
				  		value="${sellMemberTransaction[0].multiple}"
				  	</c:if>/>
				  	
				<input type="hidden" id="buyCloseMId" 
					<c:if test="${buyMemberTransaction == null || empty buyMemberTransaction}">
				  		value="0"
				  	</c:if>
				  	<c:if test="${buyMemberTransaction != null && buyMemberTransaction.size() > 0}">
				  		value="${buyMemberTransaction[0].mt_id}"
				  	</c:if>/>
				  	
				<input type="hidden" id="buyCloseMultiple" 
					<c:if test="${buyMemberTransaction == null || empty buyMemberTransaction}">
				  		value="0"
				  	</c:if>
				  	<c:if test="${buyMemberTransaction != null && buyMemberTransaction.size() > 0}">
				  		value="${buyMemberTransaction[0].multiple}"
				  	</c:if>/>
				<!-- 杠杆倍数 -->
				<div class="trade-detail relative ie-bug">
					<div class="lt" onmouseover="showCloseTypeOptions();" onmouseout="hideCloseTypeOptions();">
						<div class="type-close-input">
							<input type="text" readonly="readonly" unselectable="on" class="type-close-inner input-inner-new"
								   value="<fmt:message key="msg.pg.trade.limitedBuyMore"/>"/>
							<em class="triger-arrow-chose triger-chose-order"></em>
						</div>
						<div class="type-close-order">
							<div class="type-close-box">
								<span class="type-option type-order-limit active"
									  onclick="typeCloseBtnChange(this, 0)"><fmt:message
										key="msg.pg.trade.limitedBuyMore"/></span>
								<span class="type-option type-order-plan"
									  onclick="typeCloseBtnChange(this, 1)"><fmt:message
										key="msg.pg.trade.planEntrustClose"/></span>
								<span class="type-option type-order-market"
									  onclick="typeCloseBtnChange(this, 2)"><fmt:message
										key="msg.pg.trade.fmtFullMarketClose"/></span>
							</div>
						</div>
					</div>
					<div class="rt" onmouseover="showCloseOptions();" onmouseout="hideCloseOptions();">
						<input type="hidden" name="planMultiple" id="closePlanMultiple" value=""/>
						<div class="lever-close-input">
							<label><fmt:message key="msg.pg.trade.lever" /></label>
							<input type="text" readonly="readonly"  unselectable="on" class="close-input-inner input-inner-new" value=""/>
							<em class="triger-close-select triger-arrow-close"></em>
						</div>
						<div class="select-close-item">
							<div class="select-options-box" id="selectCloseOrderBox">
								<c:if test="${multiples != null && multiples.size() > 0}">
									<c:forEach items="${multiples}" var="m" varStatus="l">
										<span class="option-multiple-close <c:if test="${l.index == 0}">active</c:if>" id="sellCloseMultiple-${m.multiple}" onclick="closeMultipleClick(${m.multiple},this);">x${m.multiple}</span>
									</c:forEach>
								</c:if>
							</div>
						</div>
						<input type="hidden" name="token" id="closeToken" value="${cookie._token.value}"/>
						<div class="que-help que-ggbs" id="ggbs-txt">
							<div class="arrow-top"></div>
							<p><fmt:message key="msg.pg.trade.leverageClose"/></p>
						</div>
					</div>
				</div>
				<%--限价&市价--%>
				<div class="trade-detail relative ie-bug1 " id="closePriceInput">
					<input type="text" class="validate[required,number] form-control order-input" 
						value="" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.closevalue"/>"
						id="closeSellPrice" name="closeSellPrice" onblur="" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});" />
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;" />
					<div class="unit closeUnitid">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
				</div>
				
				<div class="trade-detail relative" id="closeAmountInput">
					<input type="text" style="display: none;"/>
					<input type="text" class="validate[required,number] form-control order-input" value="" placeholder="<fmt:message key="msg.pg.trade.closevolumn"/>"
						id="closeSellAmount" name="closeSellAmount" maxlength="8" onblur="" autocomplete="off" onkeyup="checkInputDigits(this, ${amountDigits});" />
					<div class="unit"><fmt:message key="msg.pg.trade.ge"/></div> 
				</div>
				
				<div class="trade-detail relative trade-percent-sell" id="closePercentBtn">
					<span class="percent-sell">25%</span>
					<span class="percent-sell">50%</span>
					<span class="percent-sell">75%</span>
					<span class="percent-sell">100%</span>
					<div class="clear"></div>
				</div>
				<%--计划--%>
				<!-- 触发价格 -->
				<div class="trade-detail relative ie-bug1 hide " id="triPriceInput">
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
					<input type="text" class="validate[required,number] form-control order-input" value="" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.triPrice"/>"
						   id="closePlanTriPrice" name="planTriPrice" onblur="" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});" />
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
					<div class="unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
				</div>
				<!-- 委托价格 -->
				<div class="trade-detail relative ie-bug1 hide " id="entruePriceInput">
					<span class="label order-label"><fmt:message key="msg.pg.trade.openPrice"/> : </span>
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
					<input type="text" class="validate[required,number] form-control order-input" style="width: 197px;<c:if test="${locale == 'en_US'}">width: 140px;</c:if>" value="" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.entruePrice"/>"
					id="closePlanPrice" name="planPrice" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});" />
					<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
					<em class="market-price-btn" style="padding: 4px 14px;float: right;" onclick="closeUseMarketPrice(this);"><fmt:message key="msg.pg.trade.planMarketPrice"/></em>
				</div>
				<!-- 数量 -->
				<div class="trade-detail relative hide" id="planAmountInput">
					<span class="label order-label"><fmt:message key="msg.pg.trade.openPosition"/> : </span>
					<input type="text" style="display: none;"/>
					<input type="text" class="validate[required,number] form-control order-input" value="" placeholder="<fmt:message key="msg.pg.market.amount"/>"
						   id="closePlanAmount" name="amount" maxlength="8" autocomplete="off" onkeyup="checkInputDigits(this, ${amountDigits});" />
					<input type="text" style="position: absolute; top: -10000px; left: -10000px; width: 0px; height: 0px;border: 0px; padding: 0;"/>
					<div class="unit"><fmt:message key="msg.pg.trade.ge"/></div>
				</div>
				<!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
				<div class="trade-detail hide" style="${payPwd ? 'display: block;' : 'display: none;' }" id="payPwdwrap">
					<span class="label order-label"><fmt:message key="msg.pg.trade.assetPwd"/> : </span>
					<input type="password" autocomplete="new-password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
					<input type="password" autocomplete="new-password" placeholder="<fmt:message key="msg.pg.trade.assetPwd"/>" class="form-control order-input" value="" id="closePlanPayPwd" name="planPayPwd" maxlength="20" autocomplete="off"/>
					<input type="password" autocomplete="new-password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
				</div>
				<!-- 空仓可平 -->
				<div class="trade-detail relative half-width-label" style="margin-bottom: 5px;">
					<div class="lt"> <span class="label"><fmt:message key="msg.pg.trade.fullCanFlat"/> : </span>
					<b id="holdSellAmount">
						<c:if test="${sellMemberTransaction == null || empty sellMemberTransaction}">
					  		0
					  	</c:if>
					  	<c:if test="${sellMemberTransaction != null && sellMemberTransaction.size() > 0}">
					  		${sellMemberTransaction[0].holdCount}
					  	</c:if>
				  	</b>
					<b> <fmt:message key="msg.pg.trade.ge"/></b> </div>
				</div>
				<!-- 多仓可平 -->
				<div class="trade-detail relative half-width-label" style="margin-bottom: 5px;">
					<div class="lt" style="padding-left: 5px;"> <span class="label"><fmt:message key="msg.pg.trade.fullCanFlatMore"/> : </span>
					<b id="holdBuyAmount">
						<c:if test="${buyMemberTransaction == null || empty buyMemberTransaction}">
					  		0
					  	</c:if>
					  	<c:if test="${buyMemberTransaction != null && buyMemberTransaction.size() > 0}">
					  		${buyMemberTransaction[0].holdCount}
					  	</c:if>
					</b>
					<b> <fmt:message key="msg.pg.trade.ge"/></b> </div>
				</div>
		
				<div class="trade-alert suspend-div" <c:if test="${!currentTransaction.suspend && isTradeTime}">style='display:none;'</c:if>>
					<div class="warning suspend-text">
						<c:if test="${currentTransaction.suspend}"><fmt:message key='msg.trade.pause'/></c:if>
						<c:if test="${!currentTransaction.suspend && !isTradeTime}"><fmt:message key='msg.trade.noTradeTime'/></c:if>
					</div>
				</div>
				<div class="normal-trade-div" <c:if test="${currentTransaction.suspend || !isTradeTime}">style='display:none;'</c:if>>
					<div class="trade-btn-box" id="closeTradeBtn">
						<div class="trade-btn-wrap" style="padding-right: 5px;">
							<button type="button" class="btn trade-btn buy-bg" onclick="return pc_k_from_closePos('${currentTransaction.transaction_code.toLowerCase()}');">
							<fmt:message key="msg.pg.trade.fulloBuyMore"/></button>
						</div>
						<div class="trade-btn-wrap" style="padding-left: 5px;">
							<button type="button" class="btn trade-btn sell-bg" onclick="return pc_d_from_closePos('${currentTransaction.transaction_code.toLowerCase()}');">
							<fmt:message key="msg.pg.trade.fulloSellMore"/></button>
						</div>
					</div>
					<div class="trade-btn-box hide" id="closePlanTradeBtn">
						<input type="hidden" id="closePlanDirection" name="planDirection" value="">
						<div class="trade-btn-wrap" style="padding-right: 5px;">
							<button type="button" id="closePlanBuy" class="btn trade-btn buy-bg" onclick="return del_sure_closePlantrade('${currentTransaction.transaction_code.toLowerCase()}', 1);">
								<fmt:message key="msg.pg.trade.fulloBuyMore"/></button>
						</div>
						<div class="trade-btn-wrap" style="padding-left: 5px;">
							<button type="button" id="closePlanSell" class="btn trade-btn sell-bg" onclick="return del_sure_closePlantrade('${currentTransaction.transaction_code.toLowerCase()}', 2);">
								<fmt:message key="msg.pg.trade.fulloSellMore"/></button>
						</div>
					</div>
					<div class="price-item clearfix half-width-label"> 
						<span><fmt:message key="msg.pg.trade.maxPriceIs"/><b class="buy maxBuyText">${maxBuy }</b><b class="buy"> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() :'USDT')}</b></span> 
					</div>
					<div class="price-item clearfix half-width-label">
						<span class="sell-span"><fmt:message key="msg.pg.trade.minPriceIs"/><b class="sell minSellText">${minSell }</b><b class="sell"> ${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</b></span>
					</div>
				</div>
				
			</div>
		</div>
	</form>
</div>

<script type="text/javascript">
	$(".trade-percent-sell span").on('click', function() {
		 if(!$(this).hasClass("active")){
			 $(".trade-percent-sell span").removeClass("active");
			 $(this).addClass("active");
		 }else {
			 $(this).removeClass("active");
		 }
		 
		var percent = $(this).html();
		var float = percent.replace("%","");
		float = float / 100;
		var holdSellAmount = parseFloat($("#holdSellAmount").html());
		var holdBuyAmount = parseFloat($("#holdBuyAmount").html());
		
		if(holdSellAmount < holdBuyAmount && holdSellAmount != 0 && holdBuyAmount != 0) {
			var canOpen = holdSellAmount;
		} else if(holdSellAmount > holdBuyAmount && holdSellAmount != 0 && holdBuyAmount != 0){
			var canOpen = holdBuyAmount;
		} else if(holdSellAmount == 0 && holdBuyAmount != 0) {
			var canOpen = holdBuyAmount;
		} else if(holdSellAmount != 0 && holdBuyAmount == 0) {
			var canOpen = holdSellAmount;
		} else {
			var canOpen = holdSellAmount;
		}
		
		if (!isNaN(canOpen)) {
			var amountDigits = Number($('#amountDigits').val());
			var amount = float * canOpen;
			var maxStr = amount + "";
			if (maxStr.indexOf('.') != -1 && maxStr.length - maxStr.indexOf('.') > amountDigits) {
				maxStr = maxStr.substring(0, maxStr.indexOf('.') + amountDigits + 1);
			}
			$("#closeSellAmount").val(maxStr);
		}
	});
</script>