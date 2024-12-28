<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<div class="lt-wrap rt" <c:if test="${isMarket }"> style="height: auto;"</c:if>>
  <form action="" name="topSearchForm" id="topSearchForm">
	<input type="hidden" name="btc_index_price" id="btc_index_price" value="${btc_index_price}">
	<div class="trade-title  trade-tt">
		<div class="trade-title-lt">
		  	<span><strong><fmt:message key="msg.pg.trade.everContract"/>：</strong></span>
			<a href="javascript:;" class="current">
			${currentTransaction.transaction_code.toUpperCase()} <fmt:message key="msg.pg.index.contract"/>
			</a>
			<c:if test="${typeCode == 'BTC'}"><a href="<%=helpPath%>btc.do" class="link" target="_blank"><fmt:message key="msg.pg.trade.knowContract"/></a></c:if>
			<input type="hidden" id="transaction_code" name="transaction_code" value="${currentTransaction.transaction_code}" />
			</div>
	   </div>
		<div class="trade-main" <c:if test="${isMarket }"> style="height: auto;"</c:if>>
			<div class="trade-box">
				<c:if test="${sessionScope.userinfo!=null}">
					<input type="hidden" id="userEmail" name="userEmail" value="${sessionScope.userinfo.f_user_email}">
				</c:if>
				<input type="hidden" id="minKcNums" name="minKcNums" value="${minKcNums}">
	  			<input type="hidden" id="minFloatPrice" name="minFloatPrice" value="${minFloatPrice}">
				<input type="hidden" id="myRemainBtc" name="myRemainBtc" value="${userAccountBtc}">
				<input type="hidden" id="myRemainOther" name="myRemainOther" value="0">
				<input type="hidden" id="myRemainGainBtc" name="myRemainBtc" value="0">
				<input type="hidden" id="myDigits" name="myDigits" value="${digits}">
	  			<input type="hidden" id="myCommissionRate" name="myCommissionRate" value="${myCommissionRate}">
	  			<input type="hidden" id="amountDigits" name="amountDigits" value="${amountDigits}">
	  			<input type="hidden" id="suspend" name="suspend" value="${currentTransaction.suspend}">
	  			<input type="hidden" id="tradePath" name="tradePath" value="${homeName}">
				<div class="trade-detail tog-ht">
		  <div class="toggle-wrap relative clearfix">
			<div class="lt">
			  <span class="label"> <em id="toggle-em"><i class="icon icons-show"></i><fmt:message key="msg.pg.trade.usefulContract"/>：</em> </span>
			  <b id="totalamount">${userAccountBtc }</b><b> ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b>&nbsp;&nbsp;&nbsp;&nbsp; 
			  <a href="<%=basePath %>transferAccount/${homeName=='vtrade' ? 'vtransfer.do' : 'transfer.do'}?type=1&bqCode=${currentTransaction.transaction_code}" class="a-btn">
			  <fmt:message key="msg.pg.trade.rollIn"/></a> <%-- <a href="https://www.btc100.com/" target="_blank" class="a-btn">从BTC100买币</a>--%>
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
			<!-- <p>其中本金余额：<b id="remainamount">${userAccountBtc } USDT</b>
				</p> -->
			<p><!-- ${fn:toUpperCase(currentTransaction.transaction_code)}待结算盈利： -->
			<fmt:message key="msg.pg.trade.canRollOut"/>：<b id="settleamount">${userAccountBtc } </b><b> ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b>
			<a href="<%=basePath %>transferAccount/${homeName=='vtrade' ? 'vtransfer.do' : 'transfer.do'}?type=2&bqCode=${currentTransaction.transaction_code}" class="a-btn"><fmt:message key="msg.pg.trade.rollOut"/></a> </p>
			<p style="display: none;"><b id="otherAmount"></b></p>
			<!-- <em class="que-icon icons-que" id="que-icon"></em>
				<div class="que-help" id="que-help">
				  <div class="arrow-top"></div>
				  <p><strong>待结算盈利：</strong>指您在该合约交割前进行平仓所产生的盈利。在该合约交割前，可以用于该合约的继续开仓，但不可以提现，也不可以用于其它合约的开仓。该部分盈利将在按比例参与该合约交割的系统亏损分担后划入您的提现余额账户。
				  </p>
				</div> --> 
		  </div>
		</div>
		<div class="trade-detail relative ie-bug"> <span class="label"><fmt:message key="msg.pg.trade.leverage"/>：</span>
		  <p class="lever-control">
			<c:forEach items="${multiple}" varStatus="l" var="m">
			  <c:if test="${m.state==2}"> <a class="disabled-ggbs" href="javascript:void(0);">x${m.multiple}</a> </c:if>
			  <c:if test="${m.state==1}"> <a href="javascript:void(0);" <c:choose><c:when test="${demultiple eq m.multiple}">class="current multiple" </c:when><c:otherwise>class="multiple" </c:otherwise></c:choose>
				id="multiple${m.multiple}" onclick="multipleClick(${m.multiple},this);">x${m.multiple}</a> </c:if>
			</c:forEach>
		  </p>
		  <input type="hidden" name="multiple" id="multiple"  value="${demultiple}" />
		  <input type="hidden" name="token" id="token"  value="${cookie._token.value}" />
		  <div class="que-help que-ggbs" id="ggbs-txt">
			<div class="arrow-top"></div>
			<p> <fmt:message key="msg.pg.trade.leverageClose"/> </p>
		  </div>
		</div>
		<div class="trade-detail relative"> 
			<span class="label"><fmt:message key="msg.pg.trade.openPosition"/>：</span>
			<input type="text" style="display: none;"/>
		  <input type="text" class="validate[required,number] form-control" value=""
			id="amount" name="amount" maxlength="8" onblur="trade_amount(1);" autocomplete="off" onkeyup="checkInputDigits(this, ${amountDigits});" />
		  <div class="unit"><fmt:message key="msg.pg.trade.ge"/></div> 
		</div>
		<div class="trade-detail relative ie-bug1 "> <span class="label"><fmt:message key="msg.pg.trade.openPrice"/>：</span>
		  <input type="text" class="validate[required,number] form-control" value="" autocomplete="off"
			id="price" name="price" onblur="trade_price();" autocomplete="off" onkeyup="checkInputDigits(this, ${digits});" />
		  <input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
		  <div class="unit">${homeName=='vtrade' ? 'BFC' : 'USDT'}</div>  <em class="que-icon icons-que" id="kcdj-que"></em>
		  <div class="que-help" id="kcdj-txt">
			<div class="arrow-top"></div>
			<p><b><fmt:message key="msg.pg.trade.goLong"/>：</b><fmt:message key="msg.pg.trade.ltExponent"/></p>
			<p><b><fmt:message key="msg.pg.trade.goShort"/>：</b><fmt:message key="msg.pg.trade.gtExponent"/></p>
			<p>（<fmt:message key="msg.pg.trade.adjust"/>）</p>
		  </div>
		  <!-- 判断价格是否合理 -->
		  <div class="alert-money display-none">
			<div class="arrow-top"></div>
			<p> <fmt:message key="msg.pg.trade.quoteRole"/>，<fmt:message key="msg.pg.trade.atNow"/><span id="text_alert" style="margin:0;"><fmt:message key="msg.pg.trade.longPosition"/></span> <fmt:message key="msg.pg.trade.priceIs"/>： <b id="maxBuyPrice1_alert">7777</b> 
			<br><fmt:message key="msg.pg.trade.asSure"/> <b id="maxBuyPrice2_alert">7777</b> <b class="buy" id="direct_alert"><fmt:message key="msg.pg.trade.buyIn"/></b> 
			<b id="buyAmount_alert">12</b>${homeName=='vtrade' ? 'BFC' : 'USDT'} </p>
			 <div class="calculat-sub">
			   <button type="button" class="btn sub-btn" id="submit_alert"><fmt:message key="msg.pg.index.loginSure"/></button>
			   <button type="button" class="btn sub-btn-return" id="close_alert"><fmt:message key="msg.pg.trade.close"/></button>
			 </div>
		   </div>
		 </div>
		 <!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
		<div class="trade-detail" style="${payPwd ? 'display: block;' : 'display: none;' }"> <span class="label"><fmt:message key="msg.pg.trade.assetPwd"/>：</span>
		  <input type="password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
		  <input type="password" class="form-control" value="" id="payPd" name="payPwd" maxlength="20" autocomplete="off"/>
		  <input type="password" style="${payPwd ? 'display: block;' : 'display: none;' } width: 0; height: 0; position: absolute; top: -100px; left: -100px;">
		</div>
		<div class="trade-detail relative">
		  <div class="lt"> <span class="label"><fmt:message key="msg.pg.trade.depositCash"/>：</span><b id="baozhengjin">0</b><b> ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b> </div>
		  <div class="lt pdlt" style="padding-left: 10px;"> <em><a href="javascript:" id="jisuan"><b class="icons-count"></b><fmt:message key="msg.pg.trade.count"/></a> </em> </div>
		</div>
		<div class="trade-detail relative">
		  <div class="lt"> <span class="label"><fmt:message key="msg.pg.trade.canOpen"/>：</span><b id="maxAmount">0</b><b> <fmt:message key="msg.pg.trade.ge"/></b> </div>
		</div>
		
		<c:if test="${currentTransaction.suspend}">
			<div class="trade-alert">
		  <div class="warning"><fmt:message key="msg.trade.pause"/></div>
		</div>
		</c:if>
		<c:if test="${!currentTransaction.suspend}">
		<div class="price-item clearfix"> 
			<span><fmt:message key="msg.pg.trade.maxPriceIs"/> <b class="buy" id="maxBuy">${maxBuy }</b><b class="buy"> ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b></span> 
			<span><fmt:message key="msg.pg.trade.minPriceIs"/> <b class="sell" id="minSell">${minSell }</b><b class="sell"> ${homeName=='vtrade' ? 'BFC' : 'USDT'}</b></span> 
		</div>
		<div class="trade-btn-box">
			<input type="hidden" id="direction" name="direction" value="">
		  <div class="trade-btn-wrap" id="buy">
			<button type="button" id="btnBtc" class="btn trade-btn buy-bg" onclick="return del_sure_trade('<%=basePath %>${homeName=='vtrade' ? 'vtradeAjax' : 'tradeAjax'}/tradeTrans.do', '${typeCode.toLowerCase()}', 1);"><fmt:message key="msg.pg.trade.buyMore"/></button>
		  </div>
		  <div class="trade-btn-wrap" id="sell">
			<button type="button" id="sellBtc" class="btn trade-btn sell-bg" onclick="return del_sure_trade('<%=basePath %>${homeName=='vtrade' ? 'vtradeAjax' : 'tradeAjax'}/tradeTrans.do', '${typeCode.toLowerCase()}', 2);"><fmt:message key="msg.pg.trade.sellEmpty"/></button>
		  </div>
		</div>
		</c:if>
		
		<!-- <div class="price-item clearfix"> <span id="prompt_buy">下跌至 <b>0</b> 您将爆仓</span> <span id="prompt_sell">上涨至 <b>0</b> 您将爆仓</span> </div> -->
		
		<!-- 开仓后提示 -->
		<div class="trade-alert">
		  <div class="success display-none" id="infomsg_kc"></div>
		</div>
	  </div>
	</div>
  </form>
</div>

<script type="text/javascript">
	// 点击计算模态框
	$("#jisuan").on('click', function() {
		var jisuan = $("#jisuan-wrap");
		var jszz = $("#tb-overlay-jisuan");
		show(jisuan);
		jszz.show();
	});

    $(".jisuan_close_btn").on('click', function() {
    	var jisuan = $("#jisuan-wrap");
		var jszz = $("#tb-overlay-jisuan");
        hide(jisuan);
        jszz.hide();
    });
    
	// 交易须知提示
    var sz_hint_block = $("#sz-hint");
    var sz_hint_help = $("#sz-hint-help");
    sz_hint_block.hover(function() {
      sz_hint_help.show();
    }, function() {
      sz_hint_help.fadeOut("slow");
    });
	
	// 50倍杠杆不可用状态 
	$(".disabled-ggbs").hover(function() {
		$("#ggbs-txt").show();
	}, function() {
		$("#ggbs-txt").fadeOut("slow");
	});
	
</script>