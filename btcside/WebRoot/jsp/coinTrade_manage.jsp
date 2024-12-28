<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>

  <div class="main-wrap" style="overflow: visible;">
    <div class="block-title-trade">
      <div class="tab tab-trade lt" id="tab-trade"> 
      	<span class="current" onclick="refreshPending();"><fmt:message key="msg.pg.trade.mandateManage"/></span> 
      	<span onclick="refreshTradeLog();"><fmt:message key="msg.trade.record"/></span> 
      </div>
      <div class="trade-control relative">
      	<div class="lt"><a href="javascript:;" onclick="coincancelDeal('1','${currentCoinPair.coin_pair_name}');"><fmt:message key="msg.pg.trade.buyBtn"/></a> </div>
        <div class="lt"><a href="javascript:;" onclick="coincancelDeal('2','${currentCoinPair.coin_pair_name}');"><fmt:message key="msg.pg.trade.sellBtn"/></a> </div>
        <div class="lt"><a href="javascript:;" onclick="coincancelDeal('0','${currentCoinPair.coin_pair_name}');"><fmt:message key="msg.pg.trade.alldraw"/></a> </div>	
      </div>
       <div class="trade-control relative" style="display: none;">
      	<a href="spotTradeQuery.do?coinPairName=${currentCoinPair.coin_pair_name}"><fmt:message key="msg.normal.more"/></a>	
      </div>
      <div class="clear"></div>
    </div>
    <div class="clear"></div>
    <div class="trade-manage-main table-bg">
      <div class="trade-cont" id="trade-matter">
        <table>
          <thead>
            <tr>
              <th> <fmt:message key="msg.pg.trade.entrueTime"/></th> 
              <th> <fmt:message key="msg.pg.trade.tradeON"/> </th> 
              <th> <fmt:message key="msg.pg.trade.typeOfList"/> </th>  
              <th> <fmt:message key="msg.pg.trade.entruePrice"/>(USDT)</th>
              <th> <fmt:message key="msg.pg.trade.entrueNO"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> 
              <th> <fmt:message key="msg.pg.trade.doneDeal"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> 
              <th> <fmt:message key="msg.pg.trade.unsettled"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>  
              <th style="width:60px"> <fmt:message key="msg.pg.trade.option"/> </th> 
            </tr>
          </thead>
          <tbody id="pendingTbody">
         	<c:if test="${pdList == null||empty pdList}">
              <tr>
          		<c:if test="${sessionScope.userinfo==null}">
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
	            </c:if>
	          </tr>
	        </c:if>
	        
            <c:forEach var="pd" items="${pdList}" >
              <tr>
                <td><jsp:useBean id="dateValue" class="java.util.Date"/>
                	<jsp:setProperty name="dateValue" property="time" value="${pd.delegation_time * 1000}"/>
                	<fmt:formatDate value="${dateValue}" pattern="MM.dd HH:mm"/></td>
                <td> ${fn:toUpperCase(pd.coin_pair_name)} </td>
                <td>
                 <c:choose>
                 <c:when test="${pd.direction eq 1}"><b class="buy"><fmt:message key="msg.pg.trade.buyIn"/></b></c:when>
                 <c:when test="${pd.direction eq 2}"><b class="sell"><fmt:message key="msg.pg.cointrade.sell"/></b></c:when>
                 </c:choose>
                </td>
                <td> ${pd.delegation_price.setScale(digits, 1)} </td>
                <td><fmt:formatNumber value="${pd.delegation_count}" pattern="#0.0000"/></td>
                <td><fmt:formatNumber value="${pd.deal_count}" pattern="#0.0000"/></td>
                <td><fmt:formatNumber value="${pd.undeal_count}" pattern="#0.0000"/></td>
                <td>
                   <a href="javascript:void(0);"  class="btn btn-blue" 
                    	onclick="coincancelDealAnsyc(${pd.delegation_id},'${pd.coin_pair_name}');"><fmt:message key="msg.pg.trade.withdrawals"/></a>
                 </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    
      <div class="trade-cont" style="display: none;">
       <table cellpadding="0" cellspacing="0">
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
					<fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(USDT)
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealAmount"/>
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
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
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
$('#tab-trade span').click(function() {
	$(this).addClass("current").siblings().removeClass();
	//removeClass就是删除当前其他类；只有当前对象有addClass("current")；siblings()意思就是当前对象的同级元素，removeClass就是删除； 
	$(".trade-manage-main .trade-cont").hide().eq($('#tab-trade span').index(this)).show();
	$(".block-title-trade .trade-control").hide().eq($('#tab-trade span').index(this)).show();
});
</script>