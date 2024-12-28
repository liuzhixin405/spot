<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<title>${map.lastPrice }$ <fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.market.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/kline.css?<%=rjs%>" />
		<link href="<%=basePath%>favicon.ico" rel="SHORTCUT ICON" />
		<!--[if lte IE 6]>
		<script src="js/ie6png.js" type="text/javascript"></script>
		    <script type="text/javascript">
		        DD_belatedPNG.fix('.test, ul, img, li, input , a, div,span,em,i');
		    </script>
		<![endif]-->
		
		<style type="text/css">
		/* actGotop */
		.actGotop{position:fixed;right:20px;bottom:10px;width:45px;height:45px;display:none;}
		*html .actGotop{position:absolute;top:expression(eval(document.documentElement.scrollTop));}
		.actGotop a,.actGotop a:link{width:45px;height:45px;display:inline-block;background:url(<%=basePath%>common/images/goup.png) no-repeat;_background:url(<%=basePath%>common/images/goup.png) no-repeat;outline:none;}
		.actGotop a:hover{width:45px;height:45px;background:url(<%=basePath%>common/images/goup.png) no-repeat;outline:none;}
		
		</style>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
		<c:if test="${user != null }">
		<jsp:include page="duiping.jsp"></jsp:include>
		<jsp:include page="jisuan.jsp"></jsp:include>
		</c:if>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container-trade-data overflow-visible">
			<div class="main-wrap">
				<div class="select-tab">
					<div class="tab lt" style="width: 20%;">
						<a href="javascript:;" class="current reset"><c:choose>
								<c:when
									test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }"><fmt:message key="msg.pg.index.subcriContact"/></c:when>
									<c:otherwise>
									<fmt:message key="msg.pg.index.subcriContact"/>
									</c:otherwise>
							</c:choose>
						</a>
					</div>
                <div class="market-status rt">
                    <span>  <b><fmt:message key="msg.pg.market.kLineState"/>：</b><a href="javascript:;" class="toggle-icon open-bgc" id="kline-a"><fmt:message key="msg.pg.market.hadBeOpen"/></a></span>
                    <c:if test="${user != null }">
                    <span>  <b><fmt:message key="msg.pg.market.tradeState"/>：</b><a href="javascript:;" class="toggle-icon open-bgc" id="trade-a"><fmt:message key="msg.pg.market.hadBeOpen"/></a></span>
                    </c:if>
                </div>
					<div class="trade-title-lt rt">
						<span><strong><fmt:message key="msg.pg.market.selectTransaction"/>：</strong> </span>
						<c:forEach var="transaction" items="${transactionList}">
							<a href="javascript:;" name='${transaction.transaction_code}' onclick="changeTransaction(this.name);"
								<c:if test="${transaction.transaction_code eq currentTransaction.transaction_code}" > class="current" </c:if>>
								${fn:toUpperCase(transaction.transaction_code)}<fmt:message key="msg.pg.index.contract"/></a>
						</c:forEach>
						<input type="hidden" id="transaction_code" name="transaction_code" value="${currentTransaction.transaction_code}" />
					</div>
				</div>
				
				<div class="market-box">
					<div class="price-box-detail">
						<div class="price-lt lt">
							<b id="last">${map.lastPrice}</b>
							<p>
								<fmt:message key="msg.pg.market.lastPrice"/>
							</p>
						</div>
						<div class="price-rt lt">
							<p>
								<span class="market-block"><fmt:message key="msg.pg.market.highPrice"/>：<b class="buy" id="high">${map.highPrice}</b> </span>
								<!-- 涨追加buy；跌追加sell -->
								<span class="market-block"><fmt:message key="msg.pg.market.fudu"/>：
								<c:if test="${map.lastPrice >= map.firstPrice }">
									<b class="buy" id="fudu">${map.fudu }</b>
								</c:if>
								<c:if test="${map.lastPrice < map.firstPrice }">
									<b class="sell" id="fudu">${map.fudu }</b>
								</c:if>  
								</span>
								<span class="market-block mrbd"><!--<fmt:message key="msg.pg.market.holdCount"/>：<b id="hold">${map.holdCount}</b> --></span>
							</p>
							<p>
								<span class="market-block"><fmt:message key="msg.pg.market.lowPrice"/>：<b class="sell" id="low">${map.lowPrice}</b> </span>
								<span class="market-block"><fmt:message key="msg.pg.market.vol"/>：<b id="vol">${map.vol}</b> </span>
								<span class="market-block mrbd"><fmt:message key="msg.pg.market.addAmount"/>：<b id="amounts">${map.tradeAmount}</b> </span>
							</p>
						</div>
					</div>
				</div>
			</div>
			<div class="chose-canvas chose-canvas-us" id="chose-canvas">
         		<input type="button" class="btn-canvas clik" value="<fmt:message key="msg.pg.market.kLine"/>">
         		&emsp;<input type="button" class="btn-canvas" value="<fmt:message key="msg.pg.market.depth"/>">
    		</div>
			<div class="main-wrap margin-chart" id="depathChart">
				<jsp:include page="depathdraw.jsp"></jsp:include>
			</div>
			<div class="main-wrap margin-chart">
				<jsp:include page="kdraw.jsp"></jsp:include>
			</div>
			<c:if test="${user != null }">
				<!-- 下单界面 -->
				<div class="main-wrap overflow-visible" id="trade-main">
					<div class="lt">
					<jsp:include page="order.jsp"></jsp:include>
					</div>
					<div class="lt" style="width: 565px; margin-left: 10px;">
	      			<jsp:include page="trade_manage.jsp"></jsp:include>
	      			</div>
      			</div> 
      			<input type="hidden" id="buy1" value="${selltop5List[4] }">
      			<input type="hidden" id="sell1" value="${buytop5List[0] }">
			</c:if>
			 <div class="clear"></div>
			
			<div class="main-wrap trades-meta">
				<div class="main-data-block1">
					<div class="block-title">
						<h2>
							<fmt:message key="msg.pg.market.dependingTrades"/>
						</h2>
						<div style="float: left; padding-left: 40px;">
							<span class="fpt_left"><fmt:message key="msg.pg.market.flushTime"/></span>
							<select id="updateSecond" onchange="javascript:updateMarketSecond(0);">
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
												<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${trans.price }" pattern="#0.0" />
												</c:if>
												<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${trans.price }" />
												</c:if>
											</span>
										</td>
										<td>
											<span id="buyAmountSpan<c:out value="${status.index + 1}"></c:out>" style="${trans.priority == 3 ? 'color:#ff7f01;' : '' }">
												<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${trans.countNum }" pattern="#0" />
												</c:if>
												<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${trans.countNum }" pattern="#0.0000" />
												</c:if>
											</span>
										</td>
										<td>
											<span id="buySumSpan<c:out value="${status.index + 1}"></c:out>">
												<c:set value="${total+trans.countNum}" var="total"/>
												<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${total }" pattern="#0" />
												</c:if>
												<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${total }" pattern="#0.0000" />
												</c:if>
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
												<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${userPending.price }" pattern="#0.0" />
												</c:if>
												<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${userPending.price }" />
												</c:if>
											</span>
										</td>
										<td>
											<span id="sellAmountSpan<c:out value="${status.index + 1}"></c:out>" style="${userPending.priority == 3 ? 'color:#ff7f01;' : '' }">
												<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${userPending.countNum }" pattern="#0" />
												</c:if>
												<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${userPending.countNum }" pattern="#0.0000" />
												</c:if>
											</span>
										</td>
										<td>
											<span id="sellSumSpan<c:out value="${status.index + 1}"></c:out>">
												<c:set value="${selltotal+userPending.countNum}" var="selltotal"/>
												<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${selltotal }" pattern="#0" />
												</c:if>
												<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
													<fmt:formatNumber value="${selltotal }" pattern="#0.0000" />
												</c:if>
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
										<fmt:message key="msg.pg.market.addAmount"/>
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
											<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
												<fmt:formatNumber value="${trades[1] }" pattern="#0.0" />
											</c:if>
											<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
												<fmt:formatNumber value="${trades[1] }" />
											</c:if>
										</td>
										<td id="colorAmount<c:out value="${status.index + 1}"></c:out>">
											<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
												<fmt:formatNumber value="${trades[2] }" pattern="#0" />
											</c:if>
											<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
												<fmt:formatNumber value="${trades[2] }" pattern="#0.0000" />
											</c:if>
										</td>
										<td id="colorAdd<c:out value="${status.index + 1}"></c:out>">
											<c:choose>
												<c:when test="${(trades[5] == 1 || trades[5] == 3) && (trades[6] == 1 || trades[6] == 3)}">
													<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
														+<fmt:formatNumber value="${0 + trades[2] * 2 }" pattern="#0.0000" />
													</c:if>
													<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
														+<fmt:formatNumber value="${0 + trades[2] * 2 }" pattern="#0.0000" />
													</c:if>
												</c:when>
												<c:when test="${(trades[5] == 2 || trades[5] == 4) && (trades[6] == 2 || trades[6] == 4)}">
													<c:if test="${fn:startsWith(currentTransaction.transaction_code, 'sz') }">
														<fmt:formatNumber value="${0 - trades[2] * 2 }" pattern="#0.0000" />
													</c:if>
													<c:if test="${!fn:startsWith(currentTransaction.transaction_code, 'sz') }">
														<fmt:formatNumber value="${0 - trades[2] * 2 }" pattern="#0.0000" />
													</c:if>
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
		<div class="clear"></div>
		<jsp:include page="_footer.jsp"></jsp:include>

		<div class="actGotop"><a href="javascript:;" title="Top"></a></div>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/tradeKline.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/kline.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/trade.js?<%=rjs%>"></script> 
		<script type="text/javascript" src="<%=basePath%>common/js/transSelected.js?<%=rjs%>"></script> 
		<script src="https://cdn.bootcss.com/echarts/4.0.4/echarts.js"></script>
		<script type="text/javascript">
			var afds = setInterval(getCommonData_ajax, 2000)
			setTimeout("updateMarketSecond(0)", 5000);
		$(function(){	
			$(window).scroll(function() {		
				if($(window).scrollTop() >= 100){
					$('.actGotop').fadeIn(300); 
				}else{    
					$('.actGotop').fadeOut(300);    
				}  
			});
			$('.actGotop').click(function(){$('html,body').animate({scrollTop: '0px'}, 800);});	
		});
		</script>
		<script type="text/javascript">
    $(function() {
     	// 显示隐藏K线
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
	// 显示隐藏交易状态
    $("#trade-a").toggle(
        function() {
        	$(this).html('<fmt:message key="msg.pg.market.hadBeClose"/>');
			$("#trade-main").hide();
			$("#trade-a").removeClass('open-bgc').addClass('close-bgc');
        },
        function() {
        	$(this).html('<fmt:message key="msg.pg.market.hadBeOpen"/>');
			$("#trade-main").show();
			$("#trade-a").removeClass('close-bgc').addClass('open-bgc');
			//判断#toggle-main显示隐藏；
     });
    function clickSelectedIcon(type, n){
    	if(type == 1){//多仓
             $("#indexBuy").val(n);
             $("#bqCodeBuy").val($("#bqcodebuy" + n).val());
             $("#holdCountBuy").val($("#holdcountbuy" + n).val());
             $("#mtIdBuy").val($("#mtidbuy" + n).val());
             $("#baocangBuy").val($("#baocangbuy" + n).val());
    	} else {//空仓
             $("#indexSell").val(n);
             $("#bqCodeSell").val($("#bqcodesell" + n).val());
             $("#holdCountSell").val($("#holdcountsell" + n).val());
             $("#mtIdSell").val($("#mtidsell" + n).val());
             $("#baocangSell").val($("#baocangsell" + n).val());
    	}
    	var numBuy = Number($("#holdCountBuy").val());
    	var numSell = Number($("#holdCountSell").val());
    	if (numBuy <= 0 || numSell <= 0) {
    		return;
    	}
    	$("#duipingNum").val(numBuy <= numSell ? numBuy : numSell);
    	
    }
    
   	function dateFmt(date) {
		if (isNaN(date)) {
			return "";
		}
		date = new Date(date);
		var year = date.getFullYear();
		var month = date.getMonth();
		month = month < 9 ? "0" + (month + 1) : (month + 1);
		var day = date.getDate();
		day = day < 10 ? "0" + day : day;
		
		var hour = date.getHours();
		hour = hour < 10 ? "0" + hour : hour;
		var min = date.getMinutes();
		min = min < 10 ? "0" + min : min;
		var sec = date.getSeconds();
		sec = sec < 10 ? "0" + sec : sec;
		
		return  year+"-"+ month+"-"+day + " " + hour + ":" + min + ":" + sec; 
	}	

	
    </script>
	</body>
<script type="text/javascript">
		var message = {};
		message["zh_CN"] = {
			day : "日线",
			hour : "小时",
			minute : "分钟",
			everyMinute : "分时",
			time : "时间：",
			open : "开：",
			high : "高：",
			low : "低：",
			close : "收：",
			amplitude : "振幅：",
			volume : "量：",
		};
		message["zh_HK"] = {
			day : "日線",
			hour : "小時",
			minute : "分鐘",
			everyMinute : "分時",
			time : "時間：",
			open : "開：",
			high : "高：",
			low : "低：",
			close : "收：",
			amplitude : "振幅：",
			volume : "量：",
		};
		message["en_US"] = {
			day : "Day line",
			hour : "Hour",
			minute : "Minute",
			everyMinute : "Real-time",
			time : "T:",
			open : " O:",
			high : " H:",
			low : " L:",
			close : " C:",
			amplitude : " Amplitude:",
			volume : " V:",
		};
		message["ja_JP"] = {
			day : "デイリーライン",
			hour : "時",
			minute : "分",
			everyMinute : "時分割",
			time : "時間：",
			open : "開く：",
			high : "高い：",
			low : "低い：",
			close : "受け取る：",
			amplitude : "振幅：",
			volume : "量：",
		};
		message["ko_KR"] = {
			day : "일일 노선",
			hour : "시간",
			minute : "분",
			everyMinute : "시간 공유",
			time : "시간 :",
			open : "열기 :",
			high : "높음 :",
			low : "낮음 :",
			close : "받기 :",
			amplitude : "진폭 :",
			volume : "수량 :",
		};
		message["ru_RU"] = {
			day : "Дневная линия",
			hour : "Час",
			minute : "Минута",
			everyMinute : "Настоящее время",
			time : "T:",
			open : " O:",
			high : " H:",
			low : " L:",
			close : " C:",
			amplitude : " Амплитуда:",
			volume : " V:",
		};
		var MSG = message[locale];
		
</script>
</html>
			
			
			
			
			
			