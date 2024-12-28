<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!--  计算 -->
<div class="add-windows" id="add-windows">
	<div id="tb-overlay-jisuan" class="tb-overlay display-none"></div>
	<div class="add-wrap calculate-wrap" id="jisuan-wrap">
			<div class="dialogs-title">
          <h2 class="lt">交易计算器</h2>
          <a href="javascript:" class="close-icon icons-close close-alert"></a>
      </div>
      <div class="dialogs-body">
      <form action="" class="jisuan-form lt">
			<div class="trade-box1">
				<div class="trade-detail">
					<span>方向：</span>
					<em style="margin-right: 30px; color: #"> <input type="radio" name="radioName" value="1" checked="checked" onchange="change_direct();">买多 </em>
					<em> <input type="radio" name="radioName" value="2" onchange="change_direct();">卖空 </em>
				</div>
				<div class="trade-detail">
					<span>杠杆倍数：</span>
					<p class="lever-control">
						<a href="javascript:" class="multiple_js current" id="multiple_20" onclick="multipleClick_jisuan(20, this);">x20</a>
						<input id="multiple_select" type="hidden" value="20" />
					</p>
				</div>
				<div class="trade-detail relative">
					<span>开仓数量：</span>
					<input type="text" value="1" id="js_amount" maxlength="9" onkeyup="jisuan_amount(this, 'tinynum', event);" onblur="change_amount();" class="form-control"/>
					<i class="sz-icons-input jisuan icons-ge"></i>
				</div>
				<div class="trade-detail relative">
					<span>开仓单价：</span>
					<input type="text" id="js_price" value="${currentTransaction.latest_transaction_price eq '0.00' ? btc_index_price : currentTransaction.latest_transaction_price }" maxlength="9" onkeyup="jisuan_amount(this, 'price', event);" onblur="change_deal();" class="form-control"/>
					 <i class="sz-icons-input jisuan icons-dian"></i>
				</div>
				<div class="trade-detail relative">
					<span>涨跌幅：</span>
					<input type="text" id="js_zdf" value="+0.5" maxlength="7" onblur="change_zdf();" class="form-control"/>
					<i class="sz-icons-input jisuan icons-dian"></i>
				</div>
				<div class="trade-detail relative">
					<span>了结价格：</span>
					<input type="text" id="js_deal_price" value="${currentTransaction.latest_transaction_price + 0.5}" maxlength="7" onblur="change_deal();" onkeyup="jisuan_amount(this, 'price', event);"  class="form-control"/>
					<i class="sz-icons-input jisuan icons-dian"></i>
				</div>
			</div>
		</form>
		<div class="calculate-rt">
			<div class="trade-detail">
				<span>初始保证金：</span>
				<b id="js_bzj">0.1</b>
				<em>USDT</em>
			</div>
			<div class="trade-detail">
				<span>佣金：</span>
				<b id="js_yj">${userlevel.transaction_commission_rate}</b>
				<em>USDT</em>
			</div>
			<div class="trade-detail">
				<span>盈亏比：</span>
				<c:choose>
					<c:when test="${currentTransaction.latest_transaction_price eq '0.00'}">
						<b id="js_ykb" class="buy"><fmt:formatNumber value="${0.5/btc_index_price}" pattern="#0.00%"/></b>
					</c:when>
					<c:otherwise>
						<b id="js_ykb" class="buy"><fmt:formatNumber value="${0.5/currentTransaction.latest_transaction_price }" pattern="#0.00%"/></b>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="trade-detail">
				<span>最小波动值：</span>
				
				<c:choose>
					<c:when test="${currentTransaction.latest_transaction_price eq '0.00'}">
						<b id="js_min_bdz"><fmt:formatNumber value="${0.01*10*0.5/btc_index_price }" pattern="#0.00000000"/></b>
					</c:when>
					<c:otherwise>
						<b id="js_min_bdz"><fmt:formatNumber value="${0.01*10*0.5/currentTransaction.latest_transaction_price }" pattern="#0.00000000"/></b>
					</c:otherwise>
				</c:choose>
				<em>USDT</em>
			</div>
			<div class="trade-detail">
				<span>净盈亏：</span>
				<c:choose>
					<c:when test="${currentTransaction.latest_transaction_price eq '0.00'}">
						<b id="js_jyk" class="buy"><fmt:formatNumber value="${10*0.5/btc_index_price }" pattern="#0.0000"/></b>
					</c:when>
					<c:otherwise>
						<b id="js_jyk" class="buy"><fmt:formatNumber value="${10*0.5/currentTransaction.latest_transaction_price }" pattern="#0.0000"/></b>
					</c:otherwise>
				</c:choose>
				<em>USDT</em>
			</div>
		</div>
		<div class="clear"></div>
		<div class="calculat-sub">
			<button type="button" class="btn sub-btn" onclick="jisuanBtn();">计算</button>
			<button type="button" class="btn sub-btn-return close-alert">关闭</button>
		</div></div>
		
	</div>
</div>
<script type="text/javascript">
<!--
	function multipleClick_jisuan(val, obj){
		$(".multiple_js").removeClass("current");
		$(obj).addClass("current");
		$('#multiple_select').val(val);
	}

	function jisuanBtn(){
		var multiple_select = Number($('#multiple_select').val());
		var amount = Number($('#js_amount').val());
		var price = Number($('#js_price').val());
		var zdf = Number($('#js_zdf').val());
		var dealPrice = Number($('#js_deal_price').val());
		var direction = Number($("input[name='radioName']:checked").val());
		var vip = Number($('#myCommissionRate').val());
		var bzj = formatFloat(amount * price * 0.0001 / multiple_select, 4);
		var tradebtc = amount * price * 0.0001;

		if (amount == 0 || price == 0 || dealPrice == 0 || bzj == 0) {
			return;
		}
		
		if (direction == 1) {
			$('#js_bzj').html(bzj);
			$('#js_yj').html((tradebtc * vip).toFixed(8));
			$('#js_ykb').html(formatFloat(0.01 * (dealPrice - price) * amount / bzj, 2) + "%");
			$('#js_min_bdz').html(0.2 * 0.0001);
			$('#js_jyk').html(formatFloat(amount * (dealPrice - price) * 0.0001, 3));
			if (dealPrice - price >= 0) {
				$('#js_ykb').addClass("buy");
				$('#js_ykb').removeClass("sell");
				$('#js_jyk').addClass("buy");
				$('#js_jyk').removeClass("sell");
			} else {
				$('#js_ykb').addClass("sell");
				$('#js_ykb').removeClass("buy");
				$('#js_jyk').addClass("sell");
				$('#js_jyk').removeClass("buy");
			}
		} else {
			$('#js_bzj').html(bzj);
			$('#js_yj').html((tradebtc * vip).toFixed(8));
			$('#js_ykb').html(formatFloat(0.01 * (price - dealPrice) * amount / bzj, 2) + "%");
			$('#js_min_bdz').html(0.2 * 0.0001);
			$('#js_jyk').html(formatFloat(amount * (price - dealPrice) * 0.0001, 3));
			if (price - dealPrice > 0) {
				$('#js_ykb').addClass("buy");
				$('#js_ykb').removeClass("sell");
				$('#js_jyk').addClass("buy");
				$('#js_jyk').removeClass("sell");
			} else {
				$('#js_ykb').addClass("sell");
				$('#js_ykb').removeClass("buy");
				$('#js_jyk').addClass("sell");
				$('#js_jyk').removeClass("buy");
			}
		}
	}

	function change_direct() {
		var direction = Number($("input[name='radioName']:checked").val());
		var price = Number($('#js_price').val());
		var zdf = Number($('#js_zdf').val()); // 可为负

		$('#js_deal_price').val(price + zdf);

	}

	function change_amount() {
		var multiple_select = Number($('#multiple_select').val());
		var amount = Number($('#js_amount').val());
		var vip = Number($('#myCommissionRate').val());
		$('#js_bzj').html(formatFloat(amount / multiple_select, 4));
		$('#js_yj').html((amount * vip).toFixed(8));
	}

	function change_zdf() {
		var direction = Number($("input[name='radioName']:checked").val());
		var price = Number($('#js_price').val());
		var zdf = Number($('#js_zdf').val()); // 可为负

		if (isNaN(zdf)) {
			$('#js_zdf').val(0);
			$('#js_deal_price').val(price);
			return;
		}
		
		if (zdf + price > 0) { // 可以无限涨，不开跌至0		
			$('#js_deal_price').val(price + zdf);
		} else {
			$('#js_zdf').val(-price);
			$('#js_deal_price').val(0);
		}

	}

	function change_deal() {
		var direction = Number($("input[name='radioName']:checked").val());
		var price = Number($('#js_price').val());
		var dealPrice = Number($('#js_deal_price').val());
		
		if (dealPrice > 0) {
			$('#js_zdf').val(formatFloat(dealPrice - price, 3));
		} else {
			$('#js_deal_price').val(0);
			$('#js_zdf').val(-price);
		}
	}

	function jisuan_amount(oInput, type, e) {
		if (type == 'num') {
			var len = 8;
			var exp = /\d{1,8}\.{0,1}\d{0,2}/;
		} else if (type == 'int') {
			var len = 8;
			var exp = /\d{1,8}/;
		} else if (type == 'cash') {
			var len = 16;
			var exp = /\d{1,8}\.{0,1}\d{0,4}/;
		} else if (type == 'tinynum') {
			var len = 6;
			var exp = /\d{1,6}\.{0,1}\d{0,2}/;
		} else if (type == 'price2') {
			var len = 4;
			var exp = /\d{1,4}\.{0,1}\d{0,4}/
		} else {
			var len = 4;
			var exp = /\d{1,4}\.{0,1}\d{0,2}/
		}
		if ('' != oInput.value.replace(exp, '')) {
			oInput.value = oInput.value.match(exp) == null ? '' : oInput.value
					.match(exp);
			if (oInput.value.indexOf('.') == -1) {
				oInput.value = oInput.value.substr(0, len) + '.'
						+ oInput.value.substr(len);
			}
		} else {
			if (oInput.value.indexOf('.') == -1 && oInput.value.length > len) {
				oInput.value = oInput.value.substr(0, len) + '.'
						+ oInput.value.substr(len);
			}
		}

	}
	//-->
</script>