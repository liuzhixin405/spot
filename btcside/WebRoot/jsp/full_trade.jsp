<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!DOCTYPE HTML>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${lastPrice}${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}|${typeCode}<fmt:message key="msg.pg.keywords.trade"/></title>
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
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/slidingProgressBar/jquery.range.css?<%=rjs%>"/>
<c:if test="${skinType eq 'white' }"><link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/skin/white.css?<%=rjs%>" /></c:if>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.cookie.js?<%=rjs%>"></script>
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

<jsp:include page="jisuan.jsp"></jsp:include>
<jsp:include page="fundsTransfer.jsp"></jsp:include>
<jsp:include page="sliderMultiple.jsp"></jsp:include>

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
				<p style="text-align: center; font-size: 16px; margin-bottom: 30px;">
					<fmt:message key="msg.pg.trade.waitMoment"/>！
				</p>
				<div class="form-group-btn text-center">
					<a class="btn sub-btn center" href="javascript:history.go(-1)"><fmt:message key="msg.pg.trade.goBack"/></a>
				</div>
			</div>
		</div>
	</div>
</c:if>
<%--full-trade-manage-一键平仓确认弹框--%>
<div id="add-windows-close" class="add-windows"></div>
<!-- 全屏 -->
	<div class="full-wrap">
		<jsp:include page="full_top.jsp"></jsp:include>
		<div class="full-cont">
			<div class="right-cont-box">
				<div class="right-cont-top">
				<!-- 合并盘口和交易历史-strat -->
					<div class="right-cont-top-item hide">
						<div class="item_data">
							<div class="block-title-trade full-title-color">
																						
								<div class="tab tab-order lt" style="z-index: 1;position: relative;">
																																							
								<ul id="trade-all">																		
									<li class="tapeReading current trade-all-active" id="tapeReading" style="cursor: pointer;">									
									<fmt:message key="msg.pg.trade.tapeReading"/>
									</li>																		
									<li id="history" class="history-show trade-all-active" style="cursor: pointer;">
									<fmt:message key="msg.pg.trade.history"/>
									</li>									
								</ul>	
								 		    											
								</div>							
							</div>
                            <div class="show-contract-detail"><i class="iconfont icon-xiangqing"></i>
                            	<div class="show-contract-box hide" style="bottom: auto;">
                            		<div class='triangle'></div>
									<div class='content-detail'>
										<p class="scroll_div" id="introduce-content"></p>
									</div>
                            	</div>
                            </div>
							     
                            <!-- 盘口内容开始 -->
							<div class="trade-rt-table trade-tape-Content"  style="">
								<table cellpadding="0" cellspacing="0" style="position: absolute; top: 34px; z-index: 1;">
									<thead>
										<tr>
											<th><fmt:message key="msg.pg.trade.price" />(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
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
														<th><fmt:message key="msg.pg.trade.price" /></th>
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
																																			
									<div class="lastPriceFull" id="lastPriceFull" style="font-size: 16px;"><span class="buy" id="lastPriceSpan">${currentTransaction.latest_transaction_price.doubleValue() }</span></div>
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
							<c:if test="${homeName!='spotTrade'}">
							<!-- 合并交易历史内容 -->
							<div class="trade-rt-table trade-history-Content hide">
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
											<th>
												<fmt:message key="msg.pg.market.addAmount"/>
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
						<!-- 合并交易历史结束 -->
						</c:if>
						</div>						
					</div>
                   <!-- 合并盘口和交易历史-end -->
                   <!-- 单独显示盘口-strat -->
					<div class="right-cont-top-item-alone">
						<div class="item_data">
							<div class="block-title-trade full-title-color">
																						
								<div class="tab tab-order lt" style="z-index: 1;position: relative;">
																																							
								<ul id="trade-all">																		
									<li class="tapeReading" id="tapeReadingonly">									
									<fmt:message key="msg.pg.trade.tapeReading"/>
									</li>																		
								</ul>	
								 		    											
								</div>							
							</div>
                            <div class="show-contract-detail"><i class="iconfont icon-xiangqing"></i>
                            	<div class="show-contract-box hide" style="bottom: auto;">
                            		<div class='triangle'></div>
									<div class='content-detail'>
										<p class="scroll_div" id="introduce-content-only"></p>
									</div>
                            	</div>
                            </div>
							     
                            <!-- 盘口内容开始 -->
							<div class="trade-rt-table trade-tape-Content">
								<table cellpadding="0" cellspacing="0" style="position: absolute; top: 34px; z-index: 1;">
									<thead>
										<tr>
											<th><fmt:message key="msg.pg.trade.price" />(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
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
														<th><fmt:message key="msg.pg.trade.price" /></th>
														<th><fmt:message key="msg.pg.index.amount" /></th>
														<th><fmt:message key="msg.pg.market.sum" /></th>
													</tr>
												</thead>
												<tbody id="sellTbodyOnly">
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
																																			
									<div class="lastPriceFull" id="lastPriceFullOnly" style="font-size: 16px;"><span class="buy" id="lastPriceSpanOnly">${currentTransaction.latest_transaction_price.doubleValue() }</span></div>
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
												<tbody id="buyTbodyOnly">
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
						</div>						
					</div>
                   <!-- 单独显示盘口-end -->
                   <!-- 交易历史内容开始 -->
					<div class="right-cont-top-itemm">
						<div class="item_data">						
							<div class="block-title-trade full-title-color">			
						    	<div class="tab tab-order lt" style="z-index: 1;position: relative;width: 100%;">																														
							        <ul id="trade-all">						        																			
								        <li class="">
										<fmt:message key="msg.pg.trade.history"/>
										</li>
							        </ul>
								</div>
							</div>
							<!-- 交易历史内容 -->
							<div class="trade-rt-table trade-history-Content-full">
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
											<th>
												<fmt:message key="msg.pg.market.addAmount"/>
											</th>
										</tr>
									</thead>
								</table>
								<div class="trade-history-data">
									<div class="scroll_div">
										<table class="trade-log-table" cellpadding="0" cellspacing="0">
											<tbody id="ordertbodyonly">
											</tbody>
										</table>
									</div>
								</div>
							</div>
						<!-- 交易历史结束 -->	
						</div>
					</div>
					
				 <!-- 交易历史内容结束 -->
					
				</div>
				<div class="trade-manager-tabb">
					<div class="order-panel-parent">
						<div class="order-panel-cont">	
							<div class="block-title-trade">
						      <div class="tab tab-order lt tab-stop-loss">
						      	 <span class="current"><fmt:message key="msg.pg.trade.fulloOpen"/></span> 
						      	 <span><fmt:message key="msg.pg.trade.closePosition"/></span> 
						      	 <span><fmt:message key="msg.pg.trade.fullProlitLoss"/></span>
						      </div>
						      <div class="rt calculator-icon" onClick="openJisuan();"><i class="iconfont icon-jisuanqi"></i></div>
						    </div>
							<jsp:include page="full_order.jsp"></jsp:include>
							<jsp:include page="full_plan_entrust.jsp"></jsp:include>
							<jsp:include page="full_market_order.jsp"></jsp:include>
							<jsp:include page="close_position.jsp"></jsp:include>
							<%-- <jsp:include page="close_buy_position.jsp"></jsp:include> --%>
							<jsp:include page="full_stop_loss.jsp"></jsp:include>
						</div>
					</div>
				</div>
			</div>
			
			<div class="left-cont-box">
				<!-- 侧导航 -->
				<div class="right-cont-top-item-aside">
					<div class="item_data">
						 <div class="trade-rt-tabled  trade-history-seft">
							<div class="scroll_div">
								<div id="favoritePartition" class="side-navigation-li" onclick="indexTabClickAside(this, '${favorite}', 1, true)"><span><fmt:message key="msg.pg.trade.OptionalMarket"/></span></div>
								<div id="sideNavigationLi" onclick="indexTabClickAside(this, null, 0, false)" class="side-navigation-li"><span><fmt:message key="msg.pg.userCenter.exchangeAll"/></span></div>
								<c:forEach items="${partitions}" var="partition" varStatus="st">
									<div id="sideNavigation${st.index}" onclick="indexTabClickAside(this, '${partition.codes}', 1, false)" class="${(st.index == 0 && favorite.length() == 0) ? 'curActive side-navigation-li' : 'side-navigation-li'}">
										<span>${locale == 'zh_HK' ? partition.name_hk : (locale == 'ko_KR' ? partition.name_kr : (locale == 'ja_JP' ? partition.name_jp : (locale == 'en_US' ? partition.name_en : partition.name_cn))) }</span>
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
				</div>
				<!-- 侧导航 -->
				<!-- 合约市场&自选市场开始 -->
					<div class="right-cont-top-itemd" style="padding-left: 5px;">
						<div class="item_data">
							<div class="block-title-traded full-title-color">			
						    	<div class="tab tab-order lt" style="z-index: 1;position: relative;">																														
							        <ul id="trade-all">
								        <div id="search-box-wrap-top" >				
											<input id="search-box-top" onkeyup="searchChange()" class="trade-search-box-top" placeholder="<fmt:message key="msg.pg.index.search"/>">
											<i class="iconfont icon-sousuo"></i>
										</div>
							        </ul>
								</div>
							</div>
							 <div class="trade-rt-table  trade-history-seft" style="padding-top:40px;padding-bottom: 30px;">
								
									<ul class="contract-list-ul">
										<li class="contract-list-li contract-list-header">
											<div class="contract-list-cell contract-list-header">
												<fmt:message key="msg.pg.trade.ContractName"/>
											</div>
											<div class="contract-list-cell contract-list-header">
												<fmt:message key="msg.pg.trade.latestPrice"/>
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
								<div class="scroll_div">
									<ul class="contract-list-ul" id="tickerTbody">
									</ul>
								</div>
							</div> 
							</div>
						</div>
				 <!-- 合约市场&自选市场结束 -->
				 
				 <!-- K线框 -->
				<div class="canver-parent" style="padding-left: 360px;">
				    <div class="chose-canvas-kd" id="chose-canvas">
				    	<div class="theme-wrap rt">
				    		<i class="iconfont icon-huanfu relative rt"></i>
				    		<div class="show-theme-box hide" id="themeOptions">
				    			<div class="triangle"></div>
					    		<div class="show-theme-item">
					    			<a class="theme-img-box ${skinType eq 'white' ? 'active' : '' }" href="trade.do?skin=white&transactionCode=${currentTransaction.transaction_code}">
					    				<img class="theme-img themeLight" alt="theme-light" src="<%=basePath%>common/images/theme-light@2x.png"><p class="theme-text text-center"><fmt:message key="msg.pg.trade.skin.white"/></p>
					    			</a>
					    			<a class="theme-img-box ${skinType ne 'white' ? 'active' : '' }" href="trade.do?skin=black&transactionCode=${currentTransaction.transaction_code}">
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
					<%--风险提示--%>
					<c:if test="${currentTransaction.transaction_kind == 2}">
						<div class="test-site-wraning">
							<div class="test-site-wrap">
								<p class="test-site-content">
									<i class="iconfont icon-tishi2 icon-tishi-tips"></i>
									<fmt:message key="msg.pg.trade.pilotSiteTips1"/>${typeCode}<fmt:message key="msg.pg.trade.pilotSiteTips2"/>
									<a href="<%=basePath%>news/aboutdo.do?id=500" target="_blank" class="test-site-datail">(<fmt:message key="msg.pg.trade.pilotSiteDetail"/>)</a>
								</p>
								<i class="iconfont icon-guanbi icon-guanbi-tips" onclick="closeWraningTips();"></i>
							</div>
						</div>
					</c:if>
				</div>
				<div class="trade-manager-tab">
					<jsp:include page="full_trade_manage.jsp"></jsp:include>
				</div>
			</div>
		</div>
	</div>
	<div class="show-msg-box" id="showBox"><div id="showBoxText"></div></div>

<script type="text/javascript" src="<%=basePath%>common/js/trade.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/contractTrade.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/contractFavorite.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/commonData.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/webType.js?<%=rjs%>"></script>
<script type="text/javascript">
	var fmtFulloBuyMore = '<fmt:message key="msg.pg.trade.fulloBuyMore"/>';
	var fmtFulloSellMore = '<fmt:message key="msg.pg.trade.fulloSellMore"/>';
	var fmtFullOpen = '<fmt:message key="msg.pg.trade.fulloOpen"/>';
	var fmtFullProlitLoss = '<fmt:message key="msg.pg.trade.fullProlitLoss"/>';
	var fmtDepth = '<fmt:message key="msg.pg.market.depth"/>';
	var fmtKLine = '<fmt:message key="msg.pg.market.kLine"/>';
	var fmtFullPlanEntrust = '<fmt:message key="msg.pg.trade.fmtFullPlanEntrust"/>';
	var fmtFullLimitOrder = '<fmt:message key="msg.pg.trade.fmtFullLimitOrder"/>';
	var fmtFullPlanEntrust = '<fmt:message key="msg.pg.trade.fmtFullPlanEntrust"/>';
	var fmtClosePosition = '<fmt:message key="msg.pg.trade.closePosition"/>';
	var fmtTapeReading = '<fmt:message key="msg.pg.trade.tapeReading"/>';
	var fmtHistory = '<fmt:message key="msg.pg.trade.history"/>';
	var fmtFullMarketOrder = '<fmt:message key="msg.pg.trade.fmtFullMarketOrder"/>';
	var fmtFullMarketClose = '<fmt:message key="msg.pg.trade.fmtFullMarketClose"/>';
	var fmtLimitedBuyMore = '<fmt:message key="msg.pg.trade.limitedBuyMore"/>';
	var fmtBestPriceOrder = '<fmt:message key="msg.pg.trade.bestPriceOrder"/>';
	var fmtCloseValue = '<fmt:message key="msg.pg.trade.closevalue"/>';
</script>
</body>
</html>
