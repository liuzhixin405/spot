<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<jsp:include page="_common.jsp"></jsp:include>

<div class="rt-wrap" style="background: white;border: 1px solid #ccc;">
   	<div class="trade-coin-head">
   		<ul class="lt coin-lis" id="coin-lis">
   			<c:forEach items="${markets}" var="market">
				<li class="lt disor actived">
					<a href="<%=spotTradePath%>spotTrade.do?marketId=${market.id}">${market.market_name}交易区</a>
				</li>
			</c:forEach>
   		</ul>
   		<div class="clear"></div>
   	</div>
   	<!-- TODO,根据交易对获取交易数据 -->
   	<table class="spot-table">
   		<thead>
   			<tr>
   				<th>币种</th>
   				<th>最新价</th>
   				<th>成交量</th>
   			</tr>
   		</thead>
   		<tbody>
   			<tr>
   				<td>BTC</td>
   				<td>7000</td>
   				<td>1200</td>
   			</tr>
   		</tbody>
   	</table>
</div>
<script>
	$(".disor").each(function() {
		$(this).click(function() {
			$(this).addClass("actived").siblings().removeClass("actived");
		})
	})
</script>		
