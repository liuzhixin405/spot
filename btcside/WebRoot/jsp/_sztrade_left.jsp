<%@page contentType="text/html;charset=utf-8"%>
<%@ include file="_basepage.jsp"%>

<div class="left-menu">
	<div class="menu-block">
		<div class="menu-title">
			<h3>
				<span class="icons-trade1"></span>交易
			</h3>
			<ul>
				<li>
					<a href="<%=tradePath %>sztrade.do" class="${actionName=='sztrade' ? 'current' : ''}"> <span class="icons-trade${actionName=='sztrade'  ? '-current' : ''}"></span> 买卖 </a>
				</li>
				<li>
					<a href="<%=tradePath %>entrust.do" class="${actionName=='entrust' ? 'current' : ''}"> <span class="icons-order${actionName=='entrust'  ? '-current' : ''}"></span> 委托管理 </a>
				</li>
				<li>
					<a href="<%=tradePath %>tradeQuery.do" class="${actionName=='tradeQuery' ? 'current' : ''}"> <span class="icons-query${actionName=='tradeQuery'  ? '-current' : ''}"></span> 交易查询 </a>
				</li>
				<li>
					<a href="<%=tradePath %>market.do" class="${actionName=='market' ? 'current' : ''}"> <span class="icons-data${actionName=='market'  ? '-current' : ''}"></span> 市场数据 </a>
				</li>
				<li>
					<a href="<%=tradePath %>settle.do" class="${(actionName=='settle'||actionName=='settleSelf'||actionName=='settleSys') ? 'current' : ''}"> <span class="icons-record${(actionName=='settle'||actionName=='settleSelf'||actionName=='settleSys') ? '-current' : ''}"></span> 结算记录 </a>
				</li>
			</ul>
		</div>
	</div>
</div>