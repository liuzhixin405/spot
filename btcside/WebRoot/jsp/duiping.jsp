<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!-- 对平弹窗 -->
<div class="add-windows">
  <div id="tb-overlay-duiping" class="tb-overlay display-none"></div>
  <div class="add-wrap dp-alert-wrap psab" id="duiping-wrap" style="position: fixed; left: 32%; width: 620px;">
    <div class="dialogs-title">
      <h2 class="lt"><fmt:message key="msg.pg.trade.selectBalance"/>：</h2>
      <a href="javascript:" class="close-icon icons-close dp-close-alert"></a> </div>
    <div class="dialogs-body1">
      <div class="trade-manage-main">
        <form action="" id="" name="">
          <table class="dp-table">
            <thead>
              <tr>
                <th><fmt:message key="msg.pg.trade.genre"/></th>
                <th><fmt:message key="msg.pg.trade.totalHold"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                <th><fmt:message key="msg.pg.trade.aviablePosition"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                <th><fmt:message key="msg.pg.trade.averagePrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
                <th><fmt:message key="msg.pg.trade.floatProfit"/></th>
              </tr>
            </thead>
            <tbody class="dp-tbody" id="duipingBuyTbody">
              <c:if test="${buyMemberTransaction == null||empty buyMemberTransaction}">
                <tr>
                  <td colspan="12"> <fmt:message key="msg.pg.trade.guessBuy"/> </td>
                </tr>
              </c:if>
              <c:forEach items="${buyMemberTransaction}" var="tran" varStatus="l">
                <tr onclick="clickSelectedIcon(1, ${l.index});">
                  <td><input type="hidden" value="${tran.bqCode }" id="bqcodebuy${l.index}">
                    <input type="hidden" value="${tran.holdCount }" id="holdcountbuy${l.index}">
                    <input type="hidden" value="${tran.mt_id }" id="mtidbuy${l.index}">
                    <input type="hidden" value="${tran.qpPrice }" id="baocangbuy${l.index}">
                    <i class="checked" id="selectedIconBuy${l.index}"></i> ${fn:toUpperCase(tran.bqCode)} <b class="buy"><fmt:message key="msg.pg.trade.much"/></b>x${tran.multiple} </td>
                  <td id="buyAll${l.index}"><fmt:formatNumber value="${tran.holdCount + tran.lockCount }" pattern="#.####">
                    </fmt:formatNumber> </td>
                  <td><fmt:formatNumber value="${tran.holdCount}" pattern="#.####">
                    </fmt:formatNumber></td>
                  <td>${tran.averagePrice}</td>
                  <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
                    </fmt:formatNumber></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
          <input type="hidden" value="" id="indexBuy">
          <input type="hidden" value="" id="bqCodeBuy">
          <input type="hidden" value="" id="holdCountBuy">
          <input type="hidden" value="" id="mtIdBuy">
          <input type="hidden" value="" id="baocangBuy">
          <input type="hidden" value="" id="indexSell">
          <input type="hidden" value="" id="bqCodeSell">
          <input type="hidden" value="" id="holdCountSell">
          <input type="hidden" value="" id="mtIdSell">
          <input type="hidden" value="" id="baocangSell">
          <div class="dp-control-box clearfix">
            <div class="dp-control lt relative"> <span><fmt:message key="msg.pg.trade.flatunitPrice"/>：</span>
              <input type="text" class="form-control" id="duipingPrice" value="${currentTransaction.latest_transaction_price.doubleValue()}" onkeyup="checkInputDigits(this, ${digits});">
              <div class="unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div> </div>
            <div class="dp-control lt relative"> <span><fmt:message key="msg.pg.trade.flatNo"/>：</span>
              <input type="text" class="form-control" id="duipingNum" onkeyup="checkInputDigits(this, ${amountDigits});">
              <div class="unit"><fmt:message key="msg.pg.trade.ge"/></div> </div>
            <div class="dp-control button-box lt">
              <input type="button" value="<fmt:message key="msg.pg.trade.alignMent"/>" class="btn sub-btn" onclick="pc_dp();">
              <input type="button" value="<fmt:message key="msg.pg.trade.cancel"/>" class="btn sub-btn-return dp-close-alert">
            </div>
            <div class="clear"></div>
            <div class="status"><span id="msg_duiping" class="buy"></span></div>
          </div>
          <table class="dp-table">
            <thead>
              <tr>
                <th><fmt:message key="msg.pg.trade.genre"/></th>
                <th><fmt:message key="msg.pg.trade.totalHold"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                <th><fmt:message key="msg.pg.trade.aviablePosition"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                <th><fmt:message key="msg.pg.trade.averagePrice"/>(${homeName=='usdr' ? 'BFC' : (homeName=='ntrade' ? homeName.toUpperCase() : 'USDT')})</th>
                <th><fmt:message key="msg.pg.trade.floatProfit"/></th>
              </tr>
            </thead>
            <tbody class="dp-tbody" id="duipingSellTbody">
              <c:if test="${sellMemberTransaction == null||empty sellMemberTransaction}">
                <tr>
                  <td colspan="12"> <fmt:message key="msg.pg.trade.guessSell"/> </td>
                </tr>
              </c:if>
              <c:forEach items="${sellMemberTransaction}" var="tran" varStatus="l">
                <tr onclick="clickSelectedIcon(2, ${l.index});" class="">
                  <td><input type="hidden" value="${tran.bqCode }" id="bqcodesell${l.index}">
                    <input type="hidden" value="${tran.holdCount }" id="holdcountsell${l.index}">
                    <input type="hidden" value="${tran.mt_id }" id="mtidsell${l.index}">
                    <input type="hidden" value="${tran.qpPrice }" id="baocangsell${l.index}">
                    <i class="checked" id="selectedIconSell${l.index}"></i> ${fn:toUpperCase(tran.bqCode)} <em class="sell"><fmt:message key="msg.pg.trade.guessEmpty"/></em>x${tran.multiple} </td>
                  <td><fmt:formatNumber value="${tran.holdCount + tran.lockCount }" pattern="#.##">
                    </fmt:formatNumber></td>
                  <td><fmt:formatNumber value="${tran.holdCount }" pattern="#.##">
                    </fmt:formatNumber></td>
                  <td>${tran.averagePrice }</td>
                  <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
                    </fmt:formatNumber></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </form>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
    // 对平相关
    function clickSelectedIcon(type, n) {
		if(type == 1){//多仓
	         $("#indexBuy").val(n);
	         $("#bqCodeBuy").val($("#bqcodebuy" + n).val());
	         $("#holdCountBuy").val($("#holdcountbuy" + n).val());
	         $("#mtIdBuy").val($("#mtidbuy" + n).val());
	         $("#baocangBuy").val($("#baocangbuy" + n).val());
	         
			$($("#duipingBuyTbody tr")[n]).children().find('.checked').addClass('icons-checked');
			$($("#duipingBuyTbody tr")[n]).siblings().find('.checked').removeClass('icons-checked');
			
		} else {//空仓
	         $("#indexSell").val(n);
	         $("#bqCodeSell").val($("#bqcodesell" + n).val());
	         $("#holdCountSell").val($("#holdcountsell" + n).val());
	         $("#mtIdSell").val($("#mtidsell" + n).val());
	         $("#baocangSell").val($("#baocangsell" + n).val());
	         
			$($("#duipingSellTbody tr")[n]).children().find('.checked').addClass('icons-checked');
			$($("#duipingSellTbody tr")[n]).siblings().find('.checked').removeClass('icons-checked');
		}
		var numBuy = Number($("#holdCountBuy").val());
		var numSell = Number($("#holdCountSell").val());
		// alert($("#holdCountBuy").val() + ", " + $("#holdCountSell").val());
		if (numBuy <= 0 || numSell <= 0) {
			return;
		}
		$("#duipingNum").val(numBuy <= numSell ? numBuy : numSell);
	}
	
</script>