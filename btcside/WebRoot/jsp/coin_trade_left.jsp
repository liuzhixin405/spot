<%@page contentType="text/html;charset=utf-8"%>
<%@ include file="_basepage.jsp"%>

<div class="left-menu">
	<div class="menu-block">
		<div class="menu-title">
			<h3>
				<span class="icons-trade1"></span><fmt:message key="msg.pg.trade.dealTrade"/>
			</h3>
			<ul>
				<li>
					<a href="<%=spotTradePath%>spotTrade.do" class="${actionName=='spotTrade'  ? 'current' : ''}"> <span class="icons-trade${actionName=='spotTrade'  ? '-current' : ''}"></span> <fmt:message key="msg.pg.trade.sales"/></a>
				</li>
				<li>
					<a href="<%=spotTradePath %>spotEntrust.do" class="${actionName=='spotEntrust'  ? 'current' : ''}"> <span class="icons-order${actionName=='spotEntrust'  ? '-current' : ''}"></span> <fmt:message key="msg.pg.trade.mandate"/> </a>
				</li>
				<li>
					<a href="<%=spotTradePath %>spotTradeQuery.do" class="${actionName=='spotTradeQuery'  ? 'current' : ''}"> <span class="icons-query${actionName=='spotTradeQuery'  ? '-current' : ''}"></span> <fmt:message key="msg.pg.trade.query"/> </a>
				</li>
				<li>
					<a href="<%=spotTradePath %>spotMarket.do" class="${actionName=='spotMarket'  ? 'current' : ''}"> <span class="icons-data${actionName=='spotMarket'  ? '-current' : ''}"></span> <fmt:message key="msg.pg.trade.data"/> </a>
				</li>
			</ul>
		</div>
	</div>
</div>
<script>
	
</script>