<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<title>${map.lastPrice }$ <fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.market.title"/></title>
		<link href="<%=basePath%>favicon.ico" rel="SHORTCUT ICON" />

		<style type="text/css">
			/* actGotop */
			.actGotop{position:fixed;right:20px;bottom:10px;width:45px;height:45px;display:none;}
			*html .actGotop{position:absolute;top:expression(eval(document.documentElement.scrollTop));}
			.actGotop a,.actGotop a:link{width:45px;height:45px;display:inline-block;background:url(<%=basePath%>common/images/goup.png) no-repeat;_background:url(<%=basePath%>common/images/goup.png) no-repeat;outline:none;}
			.actGotop a:hover{width:45px;height:45px;background:url(<%=basePath%>common/images/goup.png) no-repeat;outline:none;}
		</style>
		
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/kline.css?<%=rjs%>" />
		<%-- <script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script> --%>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
	<div class="add-windows" id="alert-windows"></div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container">
			<div style="margin: 0 10px 10px 10px;position: relative;">
				<div class="sz-item" id="trade-ticker">
						<span class="sz-span"><b>${currentCoinPair.coin_name }/${currentCoinPair.market_coin_name }</b></span>
				        <span class="sz-span"><fmt:message key="msg.pg.trade.tallest"/>：<b class="buy" id="todayHighCoin">${todayHigh} USDT</b></span>
				        <span class="sz-span"><fmt:message key="msg.pg.trade.lowest"/>：<b class="sell" id="todayLowCoin">${todayLow} USDT</b></span>
				        <span class="sz-span"><fmt:message key="msg.pg.index.dealAmounts"/>：<b id="tradevolsCoin"><fmt:formatNumber value="${tradevols}" pattern="#.##"></fmt:formatNumber> <c:if test="${locale == 'zh_CN'}"><fmt:message key="msg.pg.trade.ge"/></c:if></b></span>
				        <span class="sz-span"><fmt:message key="msg.pg.trade.dealVol"/>：<b id="tradesCoin"><fmt:formatNumber value="${trades}" pattern="#.##"></fmt:formatNumber> USDT</b></span>
				        <!-- <span class="sz-span"><fmt:message key="msg.pg.index.inventory"/>：<b id="holdvols"><fmt:formatNumber value="${holdvols}" pattern="#.##"></fmt:formatNumber> <fmt:message key="msg.pg.trade.ge"/></b></span> -->
				        <div class="market-status rt">
         					<span>
         	  					<b><fmt:message key="msg.pg.market.kLineState"/>：</b><a href="javascript:;" class="toggle-icon open-bgc" id="kline-a"><fmt:message key="msg.pg.market.hadBeOpen"/></a>
         					</span>
         				</div>
	    			</div>
	    			<input type="hidden" id="coinPairName" name="coinPairName" value="${currentCoinPair.coin_pair_name }" />
				    <div class="chose-canvas chose-canvas-us" id="chose-canvas">
				     	<input type="button" class="btn-canvas clik" value="<fmt:message key="msg.pg.market.kLine"/>">
				     	&emsp;<input type="button" class="btn-canvas" value="<fmt:message key="msg.pg.market.depth"/>">
				    </div>
				    <div class="main-wrap margin-chart" id="depathChart">
						<jsp:include page="coindepathdraw.jsp"></jsp:include>
					</div>
				    <div class="main-wrap margin-chart">
						<jsp:include page="kdraw.jsp"></jsp:include>
					</div>
				</div>
			<div class="trade-rt-box">
				<div class="main-wrap" style="margin-top: 20px;">
				<!-- 
					<div class="trade-pair-wrap lt trade-manage-main" style="width: 280px;">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr style="height: 41px;">
									<th>
										<fmt:message key="msg.pg.trade.tradeON"/>
									<th>
										<fmt:message key="msg.pg.index.newDeal"/>
									</th>
									<th>
										<fmt:message key="msg.pg.index.upToDown"/>
									</th>
									<th>
										<fmt:message key="msg.pg.index.dealAmounts"/>
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="map" items="${listMap}">
									<tr class="tradesA" onclick="gotoTrade(${map.pairId });">
										<td>${fn:toUpperCase(map.pairName)}</td>
										<td>${map.lastPrice}</td>
										<c:choose>
											<c:when test="${ map.lastPrice >= map.firstPrice }">
											<td class="buy">
												${map.fudu }
											</td>
											</c:when>
											<c:otherwise>
											<td class="sell">
												${map.fudu }
											</td>
											</c:otherwise>
										</c:choose>
										<td><fmt:formatNumber value="${map.vol }" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div> -->
				    
				
				    <div class="rt-wrap lt" style="margin-left: 0; width: 385px;">
				        <div class="trade-rt-table">
				          <table id="selldiv">
				            <thead>
				              <tr>
				                <th> <fmt:message key="msg.pg.trade.direction"/> </th>
				                <th> <fmt:message key="msg.pg.trade.price"/> </th>
				                <th> <fmt:message key="msg.pg.index.amount"/> </th>
				              </tr>
				            </thead>
				            <tbody id="sellTbodyCoin">
				              <c:forEach var="sell" items="${selltop5List}">
				                <tr onclick="trade_price_market(2,'${sell.price}', '${sell.countNum}');" >
				                  <td><span class="sell">${sell.name}</span></td>
				                  <td> ${sell.price} </td>
				                  <td> ${sell.countNum} </td>
				                </tr>
				              </c:forEach>
				            </tbody>
				            <tbody id="buyTbodyCoin">
				              <c:forEach var="buy" items="${buytop5List}">
				                <tr onclick="trade_price_market(1,'${buy.price}', '${buy.countNum}');" >
				                  <td><span class="buy">${buy.name}</span></td>
				                  <td> ${buy.price} </td>
				                  <td> ${buy.countNum} </td>
				                </tr>
				              </c:forEach>
				            </tbody>
				          </table>
				        </div>
				        <div class="trade-rt-data" style="width: 100%;">
				          <span style="width: 100%;"><fmt:message key="msg.pg.trade.newDealprice"/>： <em id="lastPrice">${currentCoinPair.latest_price.doubleValue() }</em>
				          </span> 
				        </div>
				    </div>
					<!-- 下单页面 -->
				    <jsp:include page="coin_order.jsp"></jsp:include>
				    
				    <input type="hidden" id="buy1Price" value="${buy1Price }">
      				<input type="hidden" id="sell1Price" value="${sell1Price }">	
				</div>
				<%-- <div class="main-wrap" style="margin-top: 20px;">
					<jsp:include page="spotTable.jsp"></jsp:include>	
				</div> --%>
			</div>
		</div>
		<div class="clear"></div>
		<div class="container-trade" style="padding-top: 10px;">
			<jsp:include page="coinTrade_manage.jsp"></jsp:include> 
		</div>
		<div class="clear"></div>
		<div class="container-trade" style="padding-top: 10px;">
			<div class="main-wrap">
				<div class="main-data-block1">
					<div class="block-title">
						<h2>
							<fmt:message key="msg.pg.market.dependingTrades"/>
						</h2>
						<div style="float: left; padding-left: 40px;">
							<span class="fpt_left"><fmt:message key="msg.pg.market.flushTime"/></span>
							<select id="updateSecond" onchange="javascript:updateMarketSecond('spot');">
									<option value="2000">
										1s
									</option>
									<option value="3000">
										2s
									</option>
									<option value="6000" selected="selected">
										5s
									</option>
									<option value="11000">
										10s
									</option>
									<option value="31000">
										30s
									</option>
									<option value="61000">
										60s
									</option>
							</select>
							<span class="fpt_right"><fmt:message key="msg.pg.market.countDown"/>：<span class="fr" id="secondNumber" style="color: red;">5s</span></span>
						</div>
					</div>
					<div class="trade-manage-main1">
						<table cellpadding="0" cellspacing="0" class="lt" style="border-right: 1px solid #f0f0f0;">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.market.duo"/>
									</th>
									<th>
										<fmt:message key="msg.pg.market.buyPrice"/>
									</th>
									<th>
										<fmt:message key="msg.pg.market.amount"/>
									</th>
									<th>
										<fmt:message key="msg.pg.market.sum"/>
									</th>
								</tr>
							</thead>
							<tbody id="buytbody">
								<c:forEach var="trans" items="${lstBtcPendingBuy}" varStatus="status">
									<tr style="cursor: pointer; background-color: rgb(255, 255, 255);" onmouseout="this.style.backgroundColor='#fff';" 
										onmouseover="this.style.cursor='pointer';this.style.backgroundColor='#FFFFAA';">
			
										<td class="fc_02">
											<c:out value="${status.index + 1}"></c:out>
										</td>
										<td>
											<span id="buyPriceSpan<c:out value="${status.index + 1}"></c:out>" class="buy">
												<fmt:formatNumber value="${trans.price }" />
											</span>
										</td>
										<td>
											<span id="buyAmountSpan<c:out value="${status.index + 1}"></c:out>" style="${trans.priority == 3 ? 'color:#ff7f01;' : '' }">
												<fmt:formatNumber value="${trans.countNum }" pattern="#0.0000" />
											</span>
										</td>
										<td>
											<span id="buySumSpan<c:out value="${status.index + 1}"></c:out>">
												<c:set value="${total+trans.countNum}" var="total"/>
												<fmt:formatNumber value="${total }" pattern="#0.0000" />
											</span>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<table cellpadding="0" cellspacing="0" class="lt">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.market.kong"/>
									</th>
									<th>
										<fmt:message key="msg.pg.market.sellPrice"/>
									</th>
									<th>
										<fmt:message key="msg.pg.market.amount"/>
									</th>
									<th>
										<fmt:message key="msg.pg.market.sum"/>
									</th>
								</tr>
							</thead>
							<tbody id="selltbody">
								<c:forEach var="userPending" items="${lstBtcPendingSell }" varStatus="status">
									<tr style="cursor: pointer; background-color: rgb(255, 255, 255);" onmouseout="this.style.backgroundColor='#fff';" 
										onmouseover="this.style.cursor='pointer';this.style.backgroundColor='#FFFFAA';">
										<td class="fc_02">
											<c:out value="${status.index + 1}"></c:out>
										</td>
										<td>
											<span id="sellPriceSpan<c:out value="${status.index + 1}"></c:out>" class="sell">
												<fmt:formatNumber value="${userPending.price }" />
											</span>
										</td>
										<td>
											<span id="sellAmountSpan<c:out value="${status.index + 1}"></c:out>" style="${userPending.priority == 3 ? 'color:#ff7f01;' : '' }">
												<fmt:formatNumber value="${userPending.countNum }" pattern="#0.0000" />
											</span>
										</td>
										<td>
											<span id="sellSumSpan<c:out value="${status.index + 1}"></c:out>">
												<c:set value="${selltotal+userPending.countNum}" var="selltotal"/>
												<fmt:formatNumber value="${selltotal }" pattern="#0.0000" />
											</span>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			
				<div class="main-data-block" style="width: 34%;">
					<div class="block-title">
						<h2>
							<fmt:message key="msg.pg.market.latestDeal"/>
						</h2>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
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
										<fmt:message key="msg.pg.userCenter.money"/>
									</th>
								</tr>
							</thead>
							<tbody id="ordertbody">
								<c:forEach var="trades" items="${lstBtcTrades }" varStatus="status">
									<tr class="tradesA">
										<td id="createdDateTd<c:out value="${status.index + 1}"></c:out>">
											<ut:formatDate dateline="${trades[0] }" pattern="HH:mm:ss"></ut:formatDate>
										</td>
										<td class="${trades[4] == 1 ? 'buy' : 'sell'}" id="fontPriceSpan<c:out value="${status.index + 1}"></c:out>">
											<fmt:formatNumber value="${trades[1] }" />
										</td>
										<td id="colorAmount<c:out value="${status.index + 1}"></c:out>">
											<fmt:formatNumber value="${trades[2] }" pattern="#0.0000" />
										</td>
										<td id="colorAdd<c:out value="${status.index + 1}"></c:out>">
											<c:choose>
												<c:when test="${(trades[5] == 1 || trades[5] == 3) && (trades[6] == 1 || trades[6] == 3)}">
														+<fmt:formatNumber value="${0 + trades[2] * 2 }" pattern="#0.0000" />
												</c:when>
												<c:when test="${(trades[5] == 2 || trades[5] == 4) && (trades[6] == 2 || trades[6] == 4)}">
													<fmt:formatNumber value="${0 - trades[2] * 2 }" pattern="#0.0000" />
												</c:when>
												<c:otherwise>
													0
												</c:otherwise>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>	
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>

		<div class="actGotop"><a href="javascript:;" title="Top"></a></div>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/tradeKline.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/kline.js?<%=rjs%>"></script>
		<script src="https://cdn.bootcss.com/echarts/4.0.4/echarts.js"></script>
	</body>
	<script type="text/javascript">
		$(function(){	
			$(window).scroll(function() {		
				if($(window).scrollTop() >= 100){
					$('.actGotop').fadeIn(100); 
				}else{    
					$('.actGotop').fadeOut(100);    
				}  
			});
			$('.actGotop').click(function(){$('html,body').animate({scrollTop: '0px'}, 200);});
				
			// 控制表格隔行换色
			$(".table-bg table tr:odd").css("background", "#fff");
			$(".table-bg table tr:even").css("background", "#fafafa");
			
	        $("#kline-a").toggle(
	          function() {
	          	$(this).html('<fmt:message key="msg.pg.market.hadBeClose"/>');
				$(".margin-chart").hide();
				$(".chose-canvas").hide();
				$("#kline-a").removeClass('open-bgc').addClass('close-bgc');
	          },
	          function() {
	          	$(this).html('<fmt:message key="msg.pg.market.hadBeOpen"/>');
				$(".margin-chart").show();
				$(".chose-canvas").show();
				$("#kline-a").removeClass('close-bgc').addClass('open-bgc');
				//判断#toggle-main显示隐藏；
	          });
		});
		
		function gotoTrade(id) {
			document.location.href = 'spotTrade.do?coinPairId=' + id;
		}
	
		//var afds = setInterval(getCommonData_ajax, 2000);
		updateMarketSecond('spot');
	</script>				
</html>

