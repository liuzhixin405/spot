<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!DOCTYPE HTML>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${lastPrice}USDT|${coinName}/${marketName}<fmt:message key="msg.pg.keywords.trade"/></title>
<meta name="keywords" content="${typeCode}<fmt:message key='msg.pg.keywords.trade'/>">
<meta name="description" content="<fmt:message key='msg.pg.description.trade1'/>${typeCode}<fmt:message key='msg.pg.description.trade2'/>">
<meta name="robots" content="index,follow">
<meta name="googlebot" content="index,follow">
<meta property="og:title" content="<fmt:message key='msg.pg.title.index'/>">
<meta property="og:description" content="<fmt:message key='msg.pg.description.idnex'/>">
<meta property="twitter:title" content="<fmt:message key='msg.pg.title.index'/>">
<meta property="og:site_name" content="Btcside">
<meta property="og:type" content="article">
<meta property="og:url" content="<%=basePath%>">
<meta property="og:image" content="<%=basePath%>common/images/LOGO%20Copy@3x.png">
<meta name="twitter:card" content="summary">
<meta name="twitter:site" content="@<%=serverName%>">
<meta property="twitter:image" content="<%=basePath%>common/images/LOGO%20Copy@3x.png">
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/kline.css?<%=rjs%>" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script> 
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<jsp:include page="_common.jsp"></jsp:include>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/fullScreen.css?<%=rjs%>" />
<c:if test="${skinType eq 'white' }"><link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/skin/white.css?<%=rjs%>" /></c:if>
<script>
var isLogin = ${sessionScope.userinfo != null };
var favorite = "";
if (isLogin) {
	favorite = "${favorite }";
}
var IsWhite = ${skinType == 'white' };
</script>
</head>

<body style="width:100%;height:100%;">

<c:if test="${applayerror==true}">
<div id="add-windows-safe-notice" class="add-windows" style="display: block;">
	<div id="tb-overlay" class="tb-overlay"></div>
	<div class="add-wrap" style="display:block;">
	<div class="dialogs-title">
         		<h2 class="lt"><fmt:message key="msg.pg.userCenter.safePromat"/></h2>
         		<a href="javascript:void(0);" onClick="closeSafeAlert()" class="close-icon icons-close"></a>
     		</div>
		<div class="dialogs-body">
			<p style="margin-bottom:30px; text-align:center;font-size:16px;">
				<fmt:message key="msg.pg.userCenter.gobind"/>
			</p>
			<div class="form-group-btn text-center">
				<a href="<%=basePath%>center/accountSafe.do" class="btn sub-btn"><fmt:message key="msg.pg.userCenter.letbind"/></a>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
	function closeSafeAlert() {
		$("#add-windows-safe-notice").css("display", "none");
	}
	</script>
 </div>
</c:if>

<c:if test="${hashy}">
	<div class="add-windows" id="alert-windows"></div>
</c:if>
<!-- 若无合约 -->
<c:if test="${!hashy}">
	<div class="add-windows" id="add-windows">
		<div class="tb-overlay" id="tb-overlay"></div>
		<div class="add-wrap" style="display: block;">
			<div class="dialogs-title">
				<h2 class="lt">
					${typeCode}<fmt:message key="msg.pg.index.contract"/>
				</h2>
				<a class="close-icon icons-close" id="close-alert"
					href="javascript:history.go(-1)"></a>
			</div>
			<div class="dialogs-body">
				<p
					style="text-align: center; font-size: 16px; margin-bottom: 30px;">
					<fmt:message key="msg.pg.trade.waitMoment"/>！
				</p>
				<div class="form-group-btn text-center">
					<a class="btn sub-btn center" href="javascript:history.go(-1)"><fmt:message key="msg.pg.trade.goBack"/></a>
				</div>
			</div>
		</div>
	</div>
</c:if>
<!-- 全屏 -->
	<div class="full-wrap">
		<jsp:include page="full_top.jsp"></jsp:include>
		<div class="full-cont">
			<div class="right-cont-box-spot" style="width: 602px;">
				<div class="right-cont-top" style="padding-bottom: 285px;">
					<div class="right-cont-top-item" style="padding-bottom: 0;">
						<div class="item_data">
							<div class="block-title-trade full-title-color">
																						
								<div class="tab tab-order lt" style="z-index: 1;position: relative;">
																																							
								<ul id="trade-all">																		
									<li class="current" id="tapeReading">									
									<fmt:message key="msg.pg.trade.tapeReading"/>
									</li>																		
									<%-- <li id="history">
									<fmt:message key="msg.pg.trade.history"/>
									</li> --%>									
								</ul>	
								 		    											
								</div>							
							</div>
							     
                            <!-- 盘口内容开始 -->
                            
							<div class="trade-rt-table trade-tape-Content-spot">
								<table cellpadding="0" cellspacing="0" style="position: absolute; top: 34px; z-index: 2;">
									<thead>
										<tr>
											<th><fmt:message key="msg.pg.trade.price" />(${currentCoinPair.market_coin_name })</th>
											<th><fmt:message key="msg.pg.index.amount" /></th>
											<th><fmt:message key="msg.pg.market.sum" /></th>
										</tr>
									</thead>
								</table>	
								<div class="trade-depth-all">
									<div class="trade-table-parent sell-table">
										<div class="sell-depth">
											<table class="trade-depth-table" cellpadding="0" cellspacing="0">
												<thead style="display: none;">
													<tr>
														<th><fmt:message key="msg.pg.trade.price" />(USDT)</th>
														<th><fmt:message key="msg.pg.index.amount" /></th>
														<th><fmt:message key="msg.pg.market.sum" /></th>
													</tr>
												</thead>
												<tbody id="sellTbody">
													<c:forEach var="sell" items="${selltop5List}">
														<tr onclick="trade_price_market(2, '${sell.price}', '${sell.countNum}');">
															<td><span class="sell">${sell.price}</span></td>
															<td>${sell.countNum}</td>
															<td>${sell.sumNum.doubleValue()}</td>
															<td class="progress"></td>
														</tr>
													</c:forEach>
												</tbody>	
											</table>
										</div>
									</div>
																																			
									<div class="lastPriceFull" id="lastPriceFull" style="font-size: 16px;"><span class="buy">${currentCoinPair.latest_price.doubleValue() }</span></div>
									<div class="trade-table-parent buy-table">
										<div class="buy-depth">
											<table class="trade-depth-table" cellpadding="0" cellspacing="0">
												<thead style="display: none;">
													<tr>
														<th><fmt:message key="msg.pg.trade.price" /></th>
														<th><fmt:message key="msg.pg.index.amount" /></th>
														<th><fmt:message key="msg.pg.market.sum" /></th>
													</tr>
												</thead>
												<tbody id="buyTbody">
													<c:forEach var="buy" items="${buytop5List}">
														<tr onclick="trade_price_market(1, '${buy.price}', '${buy.countNum}');">
															<td><span class="buy">${buy.price}</span></td>
															<td>${buy.countNum}</td>
															<td>${buy.sumNum.doubleValue()}</td>
															<td class="progress"></td>
														</tr>
													</c:forEach>
												</tbody>	
											</table>
										</div>
									</div>
								</div>
							</div>
							<!-- 盘口内容结束 -->
							
							<!-- 交易历史内容 -->
							<%-- <div class="trade-rt-table trade-history-Content hide">
								<table class="trade-log-table" cellpadding="0" cellspacing="0">
									<thead>
										<tr>
											<th>
												<fmt:message key="msg.pg.market.time"/>
											<th>
												<fmt:message key="msg.pg.market.dealPrice"/>
											</th>
											<th>
												<fmt:message key="msg.pg.market.dealAmount"/>
											</th>
										</tr>
									</thead>
								</table>
								<div class="trade-history-data">
									<div class="scroll_div">
										<table class="trade-log-table" cellpadding="0" cellspacing="0">
											<tbody id="ordertbody">
											</tbody>
										</table>
									</div>
								</div>
							</div> --%>
						<!-- 交易历史结束 -->	
							
							
						</div>						
					</div>
                   
                   <!-- 币币市场&自选市场开始 -->
					<div class="right-cont-top-item" style="display:block;padding-bottom: 0;">
						<div class="item_data">
							<div class="block-title-trade full-title-color">							
						    	<div class="tab tabb-order lt" style="z-index: 1;position: relative;width: 100%;">																																
							        <ul id="trade-all">								        																			
								        <li id="favoritePartition" class="current" onclick="indexTabClick(this, 0);">									
								        <fmt:message key="msg.pg.trade.Optional"/>
								        </li>		
								        <li onclick="indexTabClick(this, 1);">
								        <fmt:message key="msg.pg.trade.Market"/>
								        </li>
								        <li id="history" onclick="indexTabClick(this, 2);">
										<fmt:message key="msg.pg.trade.history"/>
										</li>
								        <div id="search-box-wrapnew" >					
											<input id="search-box" onkeyup="searchChange();" class="trade-search-boxNew" placeholder="<fmt:message key="msg.pg.index.search"/>">
											<i class="iconfont icon-sousuo icon-search-new"></i>
										</div>																
							        </ul>
								</div>
							</div>
							
							<div class="trade-rt-table  trade-history-seft-spot">
								<div class="scroll_div">
									<ul class="contract-list-ul" >
										<li class="contract-list-li contract-list-header">
											<div class="contract-list-cell contract-list-header">
												<fmt:message key="msg.pg.trade.tradeON"/>
											</div>
											<div class="contract-list-cell contract-list-header">
												<fmt:message key="msg.pg.trade.LastPrice"/>
											</div>
											<div id="changeUpDown" class="contract-list-cell contract-list-header" style="cursor:pointer;">
												<span style="color: #94ABC0;"><fmt:message key="msg.pg.trade.Trend"/></span>
												<span class="change-up-down" style="display: inline-block; width: 7px; height: 19px; position: relative">
													<em data-indexV class="arcshape-up-arrow" style="top:6px;right:0;"></em>
													<em data-indexVI class="arcshape-down-arrow"  style="top:10px;right:0;"></em>
												</span>									
											</div>
										</li>
									</ul>
									<ul class="contract-list-ul" id="tickerTbody">
									</ul>
								</div>
							</div>
							
							<!-- 交易历史内容 -->
							<div class="trade-rt-table trade-history-Content-spot hide">
								<table class="trade-log-table" cellpadding="0" cellspacing="0">
									<thead>
										<tr>
											<th>
												<fmt:message key="msg.pg.market.time"/>
											<th>
												<fmt:message key="msg.pg.market.dealPrice"/>
											</th>
											<th>
												<fmt:message key="msg.pg.market.dealAmount"/>
											</th>
										</tr>
									</thead>
								</table>
								<div class="trade-history-data">
									<div class="scroll_div">
										<table class="trade-log-table" cellpadding="0" cellspacing="0">
											<tbody id="ordertbody">
											</tbody>
										</table>
									</div>
								</div>
							</div>
						<!-- 交易历史结束 -->															
						</div>
					</div>
					
				 <!-- 币币市场&自选市场结束 -->
					
				</div>
				
				<div class="trade-manager-tab" style="height: 285px;">
					<!-- 买入 -->
					<div class="order-panel-parent-spot">
						<div class="order-panel-cont">	
							<jsp:include page="buy_spotTrade_order.jsp"></jsp:include>

						</div>
					</div>
					<!-- 卖出 -->
					<div class="order-panel-parent-spot">
						<div class="order-panel-cont">	
							<jsp:include page="sell_spotTrade_order.jsp"></jsp:include>
						</div>
					</div>
				</div>
			</div>
			
			<div class="left-cont-box-spot" style="margin-right: 602px;">
				<div class="canver-parent" style="padding-bottom: 285px;">
					<input type="hidden" id="transaction_code" name="transaction_code" value="${currentCoinPair.coin_pair_name}" />
					<input type="hidden" id="tradePath" name="tradePath" value="${homeName}">
				    <div class="chose-canvas-kd" id="chose-canvas">
				    	<div class="theme-wrap rt">
				    		<i class="iconfont icon-huanfu relative rt"></i>
				    		<div class="show-theme-box hide" id="themeOptions">
				    			<div class="triangle"></div>
					    		<div class="show-theme-item">
					    			<a class="theme-img-box ${skinType eq 'white' ? 'active' : '' }" href="spotTrade.do?skin=white&coinPairId=${currentCoinPair.id}">
					    				<img class="theme-img themeLight" alt="theme-light" src="<%=basePath%>common/images/theme-light@2x.png"><p class="theme-text text-center"><fmt:message key="msg.pg.trade.skin.white"/></p>
					    			</a>
					    			<a class="theme-img-box ${skinType ne 'white' ? 'active' : '' }" href="spotTrade.do?skin=black&coinPairId=${currentCoinPair.id}">
					    				<img class="theme-img themeDark" alt="theme-dark" src="<%=basePath%>common/images/theme-dark@2x.png"><p class="theme-text text-center"><fmt:message key="msg.pg.trade.skin.black"/></p>
					    			</a>
					    		</div>
					    	</div>
				    	</div>
				    	<span name="depth"><fmt:message key="msg.pg.market.depth"/></span>
				    	<span name="tradingView"><fmt:message key="msg.pg.market.kline.major"/></span>
				    	<span name="ori" class=""><fmt:message key="msg.pg.market.kline.base"/></span>
				    </div>
				    <div class="cav-cter cav-cter-depath full-cont-bkg">
				    	<jsp:include page="full_depathdraw.jsp"></jsp:include>	
				    </div>
					<div class="cav-cter cav-cter-kdraw full-cont-bkg">
						<jsp:include page="kdraw.jsp"></jsp:include>
					</div>
				</div>
				<!-- 委托管理&成交记录 -->
				<div class="trade-manager-tab" style="height: 285px;">
					<jsp:include page="full_spotTrade_manage.jsp"></jsp:include>
				</div>
			</div>
		</div>
	</div>
	<div class="show-msg-box" id="showBox"><div id="showBoxText"></div></div>
<script type="text/javascript">
	var fmtFulloBuyMore = '<fmt:message key="msg.pg.trade.fulloBuyMore"/>';
	var fmtFulloSellMore = '<fmt:message key="msg.pg.trade.fulloSellMore"/>';
	var fmtFullOpen = '<fmt:message key="msg.pg.trade.fulloOpen"/>';
	var fmtFullProlitLoss = '<fmt:message key="msg.pg.trade.fullProlitLoss"/>';
	var fmtDepth = '<fmt:message key="msg.pg.market.depth"/>';
	var fmtKLine = '<fmt:message key="msg.pg.market.kLine"/>';
</script>
<script type="text/javascript" src="<%=basePath%>common/js/trade.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/coinTrade.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/coinMarketFavorite.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/coinCommonData.js?<%=rjs%>"></script>
</body>
</html>
