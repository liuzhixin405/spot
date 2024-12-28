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
					<a href="<%=tradePath %>trade.do" class="${actionName=='trade'  ? 'current' : ''}"> <span class="icons-trade${actionName=='trade'  ? '-current' : ''}"></span> <fmt:message key="msg.pg.trade.sales"/></a>
				</li>
				<li>
					<a href="<%=tradePath %>entrust.do" class="${actionName=='entrust'  ? 'current' : ''}"> <span class="icons-order${actionName=='entrust'  ? '-current' : ''}"></span> <fmt:message key="msg.pg.trade.mandate"/> </a>
				</li>
				<li>
					<a href="<%=tradePath %>tradeQuery.do" class="${actionName=='tradeQuery'  ? 'current' : ''}"> <span class="icons-query${actionName=='tradeQuery'  ? '-current' : ''}"></span> <fmt:message key="msg.pg.trade.query"/> </a>
				</li>
				<li>
					<a href="<%=tradePath %>market.do" class="${actionName=='market'  ? 'current' : ''}"> <span class="icons-data${actionName=='market'  ? '-current' : ''}"></span> <fmt:message key="msg.pg.trade.data"/> </a>
				</li>
				<%-- <li>
					<a href="<%=tradePath %>settle.do" class="${(actionName=='settle'||actionName=='settleSelf'||actionName=='settleSys') ? 'current' : ''}"> <span class="icons-record${(actionName=='settle'||actionName=='settleSelf'||actionName=='settleSys') ? '-current' : ''}"></span> 结算记录 </a>
				</li> --%>
			</ul>
		</div>
	</div>
</div>