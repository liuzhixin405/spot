<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<div class="rt" style="width: 775px;height: 482px; overflow: hidden;">
	<c:if test="${sessionScope.userinfo!=null}">
		<input type="hidden" id="userEmail" name="userEmail" value="${sessionScope.userinfo.f_user_email}">
	</c:if>
				
	<input type="hidden" id="coinPairName" name="coinPairName" value="${currentCoinPair.coin_pair_name }" />
	<input type="hidden" id="minKcNums" name="minKcNums" value="${minKcNums }" />
	<input type="hidden" id="coinBalance" name="coinBalance" value="${coinAccount.btc }" />
	<input type="hidden" id="marketBalance" name="marketBalance" value="${maeketCoinAccount.btc }" />
	<input type="hidden" id="amountDigits" name="amountDigits" value="${amountDigits }" />
   		
   	<div class="coin-trade-title lt" style="height: 100%; width: 350px; padding: 8px 15px; background: #fafafa;">
   		<h2 class="nav-wids coin-nav">
 			<span id="buyTab" class="coin-to-coin buy-sty" style=""><fmt:message key="msg.pg.trade.buyIn"/>${currentCoinPair.coin_name }</span>
   		</h2>
   		<div class="clear"></div>
   		<form action="" name="" id="topSearchForm1" class="coin-order-form">
			<div class="coin-trade">
   				<span class="label"><fmt:message key="msg.pg.cointrade.available"/>：</span>
   				<span id="bestBuyPrice"><fmt:formatNumber value="${maeketCoinAccount.btc.doubleValue() }" pattern="#0.0000" /></span>
   				<span>${currentCoinPair.market_coin_name }</span>
			</div>
			<div class="clear"></div>
			
			<div class="coin-trade relative">
				<span class="label"><fmt:message key="msg.pg.cointrade.priceBuy"/>：</span>
				<input type="text" class="validate[required,number] form-control" value="${sell1Price }" id="buyPrice" name="price" maxlength="10" 
					onkeyup="checkInputDigits(this, ${digits});checkAmount('buy');" onblur="" autocomplete="off" />
				<input type="text" style="display: none;"/>
				<div class="coinUnit">${currentCoinPair.market_coin_name }</div>
			</div>
			
			<div class="coin-trade" id="progress1">
				<span class="label" style="float"><fmt:message key="msg.pg.cointrade.raidoBuy"/>：</span>	
				<div class="coin-scale" id="progressBg1">
					<div class="barProgress" id="barProgress1"></div>
					<span class="global" id="global1"></span>
				</div>
				<div class="radio"><b id="radio1">0%</b></div>	
				<div class="clear"></div>
			</div>
			
			<div class="coin-trade relative">
				<span class="label"><fmt:message key="msg.pg.cointrade.amountBuy"/>：</span>
				<input type="text" class="validate[required,number] form-control" value="" id="buyAmount" name="amount" maxlength="10" 
				onkeyup="checkInputDigits(this, ${amountDigits});checkRatio(this,'1');checkAmount('buy');" onblur="" autocomplete="off"/>
				<input type="text" style="display: none;"/>
				<div class="coinUnit">${currentCoinPair.coin_name }</div>
				<div class="clear"></div>
			</div>
			
			<div class="coin-trade relative">
				<span class="label"><fmt:message key="msg.pg.cointrade.money"/>：</span>
				<span id="buyMoney">0</span>${currentCoinPair.market_coin_name }
				<div class="clear"></div>
			</div>
			
			<div class="coin-trade relative" style="display:none;">
				<span class="label"><fmt:message key="msg.pg.cointrade.ableBuy"/>：</span>
				<span><b id="buyKinds1">${maxBuyAmount }</b> ${currentCoinPair.coin_name }</span>
				<div class="clear"></div>
			</div>
			
			<!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
			<div class="coin-trade" style="${payPwd ? 'display: block;' : 'display: none;' }">
				<span class="label"><fmt:message key="msg.pg.trade.assetPwd"/>：</span>
  				<input type="password" class="form-control" value="" name="payPwd" maxlength="20" autocomplete="new-password"/>
  				<input type="text" style="display: none;"/>
			</div>
			
			<c:if test="${!currentCoinPair.suspend}">
			<div class="coin-trade" style="text-align: center;">
				<input type="button" class="btn trade-btn buy-bg" value="<fmt:message key="msg.pg.trade.buyIn"/>" 
				onclick="return tcoinSubmit('<%=basePath %>spotTradeAjax/tradeTrans.do', '${coinPairName.toLowerCase()}', 1);">
			</div>
			</c:if>
   		</form>
   		
   		<c:if test="${currentCoinPair.suspend}">
   		<div class="trade-alert">
		  <div class="warning"><fmt:message key="msg.trade.pause"/></div>
		</div>	
   		</c:if>
   		
   		<!-- 下单后提示 -->
		<div class="trade-alert">
		  <div class="success display-none" id="infomsg_kcbuy"></div>
		</div>	
   	</div>
   	
   	<div class="coin-trade-title rt" style="height: 100%; width: 350px; padding: 8px 15px; background: #fafafa;">
   		<h2 class="nav-wids coin-nav">
 			<span id="sellTab" class="coin-to-coin sell-sty"><fmt:message key="msg.pg.cointrade.sell"/>${currentCoinPair.coin_name }</span>	
   		</h2>
   		<div class="clear"></div>
   		<form action="" name="" id="topSearchForm2" class="coin-order-form" autocomplete="off">
			<div class="coin-trade">
				<span class="label"><fmt:message key="msg.pg.cointrade.available"/>：</span>
				<span id="bestSellPrice"><fmt:formatNumber value="${maxSellAmount.doubleValue() }" pattern="#0.0000" /></span>
				<span>${currentCoinPair.coin_name }</span>
				<div class="clear"></div>
			</div>
			
			<div class="coin-trade relative">
				<span class="label"><fmt:message key="msg.pg.cointrade.priceSell"/>：</span>
				<input type="text" class="validate[required,number] form-control" value="${buy1Price }" id="sellPrice" name="price" maxlength="10" 
				onkeyup="checkInputDigits(this, ${digits});checkAmount('sell');" onblur="" autocomplete="off"/>
				<input type="text" style="display: none;"/>
				<div class="coinUnit">${currentCoinPair.market_coin_name }</div>
			</div>
					
			<div class="coin-trade" id="progress2">
				<span class="label" style="float: left;"><fmt:message key="msg.pg.cointrade.radioSell"/>：</span>
				<div class="coin-scale" id="progressBg2">
					<div class="barProgress" id="barProgress2"></div>
					<span class="global" id="global2"></span>
				</div>
				<div class="radio"><b id="radio2">0%</b></div>	
				<div class="clear"></div>
			</div>
			
			<div class="coin-trade relative">
				<span class="label"><fmt:message key="msg.pg.cointrade.amountSell"/>：</span>
				<input type="text" class="validate[required,number] form-control" value="" id="sellAmount" name="amount" maxlength="10" 
				onkeyup="checkInputDigits(this, ${amountDigits});checkRatio(this, '2');checkAmount('sell');" onblur="" autocomplete="off" />
				<input type="text" style="display: none;"/>
				<div class="coinUnit">${currentCoinPair.coin_name }</div>
			</div>
			
			<div class="coin-trade relative">
				<span class="label"><fmt:message key="msg.pg.cointrade.money"/>：</span>
				<span id="sellMoney"></span>${currentCoinPair.market_coin_name }
				<div class="clear"></div>
			</div>
			
			<div class="coin-trade relative" style="display:none;">
				<span class="label"><fmt:message key="msg.pg.cointrade.ableSell"/>：</span>
				<span><b id="buyKinds2">${maxSellAmount }</b> ${currentCoinPair.coin_name }</span>
				<div class="clear"></div>
			</div>
			
			<!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
			<div class="coin-trade" style="${payPwd ? 'display: block;' : 'display: none;' }"> 
				<span class="label"><fmt:message key="msg.pg.trade.assetPwd"/>：</span>
				<input type="password" class="form-control" value="" name="payPwd" maxlength="20" autocomplete="new-password"/>
				<input type="text" style="display: none;"/>
			</div>
			
			<c:if test="${!currentCoinPair.suspend}">
			<div class="coin-trade" style="text-align: center;">
				<input type="button" class="btn trade-btn sell-bg" value="<fmt:message key="msg.pg.cointrade.sell"/>" 
				onclick="return tcoinSubmit('<%=basePath %>spotTradeAjax/tradeTrans.do', '${coinPairName.toLowerCase()}', 2);">
			</div>
			</c:if>
			
   		</form>	
   		
   		<c:if test="${currentCoinPair.suspend}">
   		<div class="trade-alert">
		  <div class="warning"><fmt:message key="msg.trade.pause"/></div>
		</div>	
   		</c:if>
   		<!-- 下单后提示 -->
		<div class="trade-alert">
		  <div class="success display-none" id="infomsg_kcsell"></div>
		</div>	
   	</div>
   	
</div>
<script type="text/javascript" src="<%=basePath%>common/js/trade.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/coinTrade.js?<%=rjs%>"></script>
<script>

    var tag1 = false, ox1 = 0, left1 = 0;
    var tag2 = false, ox2 = 0, left2 = 0;
    var barw =  $('#progressBg1').width();
    var wids = $(".global").width()/2;
    
	function checkRatio(type, pro) {
		var divied = Number($(type).val());
		var divsor = Number($("#buyKinds" + pro).html());
		var progress =  divied / divsor;
		if (progress > 1) progress = 1;
		var barw =  $('#progressBg' + pro).width();
		var left = progress * barw;

		$("#barProgress" + pro).css("width", left);
		$("#global" + pro).css("left", left - 10);
		$("#radio" + pro).html((progress * 100).toFixed(1) + "%");
		if (pro == 1) {
			left1 = left - 10;
		} else {
			left2 = left - 10;
		}
	}
		
	$(function() {
	    $(document).mouseup(function() {
	        tag1 = false;
	        tag2 = false;
	    });

	    $('#global1').mousedown(function(e) {
	        ox1 = e.pageX - left1;
	        tag1 = true;
	    });	    
	    $('#progress1').mousemove(function(e) {//鼠标移动
	        if (tag1) {
	            left1 = e.pageX - ox1;
	            if (left1 <= -wids) {
	                left1 = -wids;
	            }else if (left1 > barw - wids) {
	                left1 = barw - wids;
	            }
	            
	            $('#global1').css('left', left1);
	            $('#barProgress1').width(left1 + wids);
	            var progress = (left1 + wids) / barw;
	            $('#radio1').html((progress * 100).toFixed(1) + '%');
	            
	            var divsor = Number($("#buyKinds1").html());
	            var amount = (progress * divsor).toFixed(${amountDigits});
	            var price = Number($('#buyPrice').val());
	            $('#buyAmount').val(amount);
				$('#buyMoney').html(amount * price);
	        }
	    });
	    
	    $('#global2').mousedown(function(e) {
	        ox2 = e.pageX - left2;
	        tag2 = true;
	    });
	    $('#progress2').mousemove(function(e) {//鼠标移动
	        if (tag2) {
	            left2 = e.pageX - ox2;
	            if (left2 <= -wids) {
	                left2 = -wids;
	            }else if (left2 > barw - wids) {
	                left2 = barw - wids;
	            }
	            
	            $('#global2').css('left', left2);
	            $('#barProgress2').width(left2 + wids);
	            var progress = (left2 + wids) / barw;
	            $('#radio2').html((progress * 100).toFixed(1) + '%');
	            
	            var divsor = Number($("#buyKinds2").html());
	            var amount = (progress * divsor).toFixed(${amountDigits});
	            var price = Number($('#sellPrice').val());
	            $('#sellAmount').val(amount);
				$('#sellMoney').html(amount * price);
	        }
	    });
	});
	
	/*
	$(".coin-to-coin").click(function() {
		if($(this).html() == "<fmt:message key='msg.pg.trade.buyIn'/>") {
			$(this).addClass("buy-sty").siblings().removeClass("sell-sty");
			$("#topSearchForm1").show();
			$("#topSearchForm2").hide();
		}
		if($(this).html() == "<fmt:message key='msg.pg.cointrade.sell'/>") {
			$(this).addClass("sell-sty").siblings().removeClass("buy-sty");
			$("#topSearchForm1").hide();
			$("#topSearchForm2").show();
		}
	});*/

            
</script>