<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>

<c:if test="${locale == 'en_US' }">
	<style type="text/css">
		.alert-window-full {
			top: -300px;
		}
	</style>
</c:if>

  <div class="main-wrap" style="overflow: visible;height: 100%;">
    <div class="block-title-trade">
      <div class="tab tab-trade lt" id="tab-trade">
      	 <span class="current" onclick="refreshPending();changeTab(0);"><fmt:message key="msg.pg.trade.mandateManage"/></span>
      	 <span onclick="refreshHistoryPending();changeTab(1);"><fmt:message key="msg.pg.trade.historyOrder"/></span>
      	 <span onclick="refreshTradeLog();changeTab(2);"><fmt:message key="msg.trade.record"/> (<fmt:message key="msg.pg.trade.nearly7days"/>)</span>
      </div>
      <div class="trade-control relative">
        <div class="lt"><a href="javascript:;" onclick="coincancelDeal('1','${currentCoinPair.coin_pair_name}');"><fmt:message key="msg.pg.trade.buyBtn"/></a> </div>
        <div class="lt"><a href="javascript:;" onclick="coincancelDeal('2','${currentCoinPair.coin_pair_name}');"><fmt:message key="msg.pg.trade.sellBtn"/></a> </div>
        <div class="lt"><a href="javascript:;" onclick="coincancelDeal('0','${currentCoinPair.coin_pair_name}');"><fmt:message key="msg.pg.trade.alldraw"/></a> </div>
      </div>
      <div class="trade-control relative" style="display: none;">
      	<a href="spotPendingQuery.do?coinPairName=${currentCoinPair.coin_pair_name}"><fmt:message key="msg.normal.more"/></a>
      </div>
      <div class="trade-control relative" style="display: none;">
      	<a href="spotTradeQuery.do?coinPairName=${currentCoinPair.coin_pair_name}"><fmt:message key="msg.normal.more"/></a>
      </div>
      <div class="clear"></div>
    </div>

    <div class="trade-manage-main table-bg" style="height: calc(100% - 34px);">
      <!-- 委托管理 -->
      <div class="trade-cont trade-matter trade-manager" id="trade-matter">
        <table>
          <thead>
            <tr>
				<th> <fmt:message key="msg.pg.trade.entrueTime"/></th>
				<th> <fmt:message key="msg.pg.trade.tradeON"/> </th>
				<th> <fmt:message key="msg.pg.trade.typeOfList"/> </th>
				<th> <fmt:message key="msg.pg.trade.entruePrice"/>(${currentCoinPair.market_coin_name })</th>
				<th> <fmt:message key="msg.pg.trade.entrueNO"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
				<th> <fmt:message key="msg.pg.trade.doneDeal"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
				<th> <fmt:message key="msg.pg.trade.unsettled"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
				<th> <fmt:message key="msg.pg.trade.option"/> </th>
            </tr>
          </thead>
          <tbody id="pendingTbody">
           	<c:if test="${pdList == null||empty pdList}">
              <tr>
          		<c:if test="${sessionScope.userinfo==null}">
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a class="full-a-link" href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
	            </c:if>
	            <c:if test="${sessionScope.userinfo!=null}">
            		<td colspan="12"> <fmt:message key="msg.pg.trade.notorderTicket"/> </td>
            	</c:if>
	          </tr>
	        </c:if>
	            
             <c:forEach var="pd" items="${pdList}" >
              <tr>
                <td><jsp:useBean id="dateValue" class="java.util.Date"/>
                	<jsp:setProperty name="dateValue" property="time" value="${pd.createTime * 1000}"/>
                	<fmt:formatDate value="${dateValue}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td> ${pd.coinPairName }</td>
                <td>
                 <c:choose>
                 <c:when test="${pd.direction eq 1}"><b class="buy"><fmt:message key="msg.pg.trade.buyIn"/></b></c:when>
                 <c:when test="${pd.direction eq 2}"><b class="sell"><fmt:message key="msg.pg.cointrade.sell"/></b></c:when>
                 </c:choose>
                </td>
                <td> ${pd.price} </td>
                <td><fmt:formatNumber value="${pd.amount}" pattern="#0.0000"/></td>
                <td><fmt:formatNumber value="${pd.dealAmount}" pattern="#0.0000"/></td>
                <td><fmt:formatNumber value="${pd.undealAmount}" pattern="#0.0000"/></td>
                <td>
                   <a href="javascript:void(0);"  class="btn btn-blue"
                    	onclick="coincancelDealAnsyc(${pd.orderId},'${pd.symbol}');"><fmt:message key="msg.pg.trade.withdrawals"/></a>
                 </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      
      <!-- 历史委托 -->
      <div class="trade-cont trade-matter" style="display: none;">
        <table>
          <thead>
            <tr>
				<th> <fmt:message key="msg.pg.trade.entrueTime"/></th>
				<th> <fmt:message key="msg.pg.trade.tradeON"/> </th>
				<th> <fmt:message key="msg.pg.trade.typeOfList"/> </th>
				<th> <fmt:message key="msg.pg.trade.entruePrice"/>(${currentCoinPair.market_coin_name })</th>
				<th> <fmt:message key="msg.pg.trade.entrueNO"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
				<th> <fmt:message key="msg.pg.trade.doneDeal"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
				<th> <fmt:message key="msg.pg.trade.unsettled"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
				<th> <fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(${currentCoinPair.market_coin_name })</th>
				<th> <fmt:message key="msg.pg.trade.commission"/></th>
				<th> <fmt:message key="msg.pg.tradeQuery.state"/> </th>
            </tr>
          </thead>
          <tbody id="historyPendingTbody">
              <tr>
          		<c:if test="${sessionScope.userinfo==null}">
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a class="full-a-link" href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
	            </c:if>
	            <c:if test="${sessionScope.userinfo!=null}">
            		<td colspan="12"> <fmt:message key="msg.pg.trade.notorderTicket"/> </td>
            	</c:if>
	          </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 成交记录 -->
      <div class="trade-cont trade-matter trade-manager" id="trade-history" style="display: none;">
       <table>
          <thead>
           <tr>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealTime"/>
				</th>
				<th>
					<fmt:message key="msg.pg.trade.tradeON"/>
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealType"/>
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(${currentCoinPair.market_coin_name })
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealAmount"/>
				</th>
				<th>
					<fmt:message key="msg.pg.trade.dealVol"/>
				</th>
				<th>
					<fmt:message key="msg.pg.userCenter.withdrawFee"/>
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.state"/>
				</th>
			</tr>
          </thead>
          <tbody id="tradeTbody">
          	<c:if test="${tradeLogList == null||empty tradeLogList}">
              <tr>
          		<c:if test="${sessionScope.userinfo==null}">
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a class="full-a-link" href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
	            </c:if>
	          </tr>
	        </c:if>
			<c:forEach var="tl" items="${tradeLogList}">
				<tr>
					<td>
						<ut:formatDate dateline="${tl.trade_time}"></ut:formatDate>
					</td>
					<td>
						${fn:toUpperCase(tl.coin_pair_name)}
					</td>
					<td>
						<c:choose>
							<c:when test="${tl.trade_trans_dirction eq 1}">
								<b class="buy"><fmt:message key="msg.pg.trade.buyIn"/></b>
							</c:when>
							<c:when test="${tl.trade_trans_dirction eq 2}">
								<b class="sell"><fmt:message key="msg.pg.cointrade.sell"/></b>
							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.####" />
					</td>
					<td>
						<fmt:formatNumber value="${tl.trade_count}" pattern="#0.####" />
					</td>
					<td>
						<fmt:formatNumber value="${tl.trade_amount}" pattern="#0.####" />
					</td>
					<td>
						<fmt:formatNumber value="${tl.trade_bfx_fee > 0 ? tl.trade_bfx_fee : tl.trade_fee}" pattern="#0.####" 
						/>(${tl.trade_bfx_fee > 0 ? 'bfx' : (tl.trade_trans_dirction == 1 ? (currentCoinPair.coin_name.toLowerCase()) : (currentCoinPair.market_coin_name.toLowerCase()))})
					</td>
					<td>
						<fmt:message key="msg.pg.tradeQuery.done"/>
					</td>
				</tr>
			</c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
<script type="text/javascript">
var fmtTargetProfit = "<fmt:message key='msg.pg.trade.targetProfit'/>";
var fmtReached =  '<fmt:message key="msg.pg.trade.reached"/>';
var fmtHour = '<fmt:message key="msg.pg.trade.hour"/>';
var fmtiWillBe = '<fmt:message key="msg.pg.trade.iWillBe"/>';
var fmtClearance = '<fmt:message key="msg.pg.trade.clearance"/>';
var fmtStopLoss = '<fmt:message key="msg.pg.trade.stopLoss"/>';
var fmtStated = '<fmt:message key="msg.pg.trade.stated"/>';
var fmtStateCon = '<fmt:message key="msg.pg.trade.statedCon"/>';
var fmtThisFun = '<fmt:message key="msg.pg.trade.thisFun"/>';
var fmtLoginSure = '<fmt:message key="msg.pg.userHon.sure"/>';
var fmtClose = '<fmt:message key="msg.pg.trade.close"/>';

</script>
<script type="text/javascript">
$('#tab-trade span').click(function() {
	$(this).addClass("current").siblings().removeClass();
	//removeClass就是删除当前其他类；只有当前对象有addClass("current")；siblings()意思就是当前对象的同级元素，removeClass就是删除； 
	$(".trade-manage-main .trade-cont").hide().eq($('#tab-trade span').index(this)).show();
	$(".block-title-trade .trade-control").hide().eq($('#tab-trade span').index(this)).show();
});
</script>