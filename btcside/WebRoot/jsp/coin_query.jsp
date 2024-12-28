<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.tradeQuery.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/jQueryDateRangePicker/css/daterangepicker.min.css">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/jQueryDateRangePicker/datePicker.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/jQueryDateRangePicker/js/moment.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/jQueryDateRangePicker/js/jquery.daterangepicker.min.js"></script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont" style="padding: 10px;">
			<div class="main-wrap">
				<div class="block-title">
					<h2 style="margin: 0 20px;"><fmt:message key="msg.pg.tradeQuery.title"/></h2>
					
					<form class="trade-title-lt" action="<%=basePath%>spotTrade/spotTradeQuery.do?direction=${currentDirection }&coinPairName=${currentCoinPair.coin_pair_name }" name="tiaojian">
						
						<div class="selected rt" style="width: 150px; margin-right: 15px; margin-top: 17px;">
							<select name="direction" id="direction" onchange="javascript:document.tiaojian.submit();">
							    <c:forEach var="direction" items="${directionMap}">
							    	<c:if test="${direction.key eq currentDirection}">
							        	<option value="${direction.key}" selected="selected">${direction.value}</option>
							        </c:if>
							        <c:if test="${direction.key ne currentDirection}">
							        	<option value="${direction.key}">${direction.value}</option>
							        </c:if>
							    </c:forEach>
							</select>
						</div>
							
						<div class="selected rt" style="width: 150px; margin-right: 15px;margin-top: 17px;">
							<select name="coinPairName" id="coinPairName" onchange="javascript:document.tiaojian.submit();">
								<c:forEach var="ta" items="${coinPairs}">
									<c:if test="${ta.coin_pair_name eq currentCoinPair.coin_pair_name}">
										<option value="${ta.coin_pair_name}" selected="selected">
												${fn:toUpperCase(ta.coin_pair_name)}
											</option>
									</c:if>
									<c:if test="${ta.coin_pair_name ne currentCoinPair.coin_pair_name}">
										<option value="${ta.coin_pair_name}">
												${fn:toUpperCase(ta.coin_pair_name)}
											</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
						<a href="javascript:void(0)" <c:if test="${currentNearlyDays eq '7'}"> class="current"</c:if> onclick="set_currentNearlyDays('7')"><fmt:message key="msg.pg.trade.nearly7days"/></a> 
						<a href="javascript:void(0)" <c:if test="${currentNearlyDays eq '30'}"> class="current"</c:if> onclick="set_currentNearlyDays('30')"><fmt:message key="msg.pg.trade.nearly30days"/></a> 
						<a href="javascript:void(0)" <c:if test="${currentNearlyDays eq '90'}"> class="current"</c:if> onclick="set_currentNearlyDays('90')"><fmt:message key="msg.pg.trade.nearly90days"/></a> 
						<fmt:message key="msg.pg.trade.queryDate"/>
						<div id="two-inputs" style="display: inline-block;line-height: 50px;">
							<input name="startTimeStr" type="text" style="width:88px;margin-left: 0px;padding: 0 5px;" id="startTimeStr"
								value="${startTimeStr }" class="selected" readonly="readonly" />
							&nbsp;-&nbsp;
							<input name="endTimeStr" type="text" style="width:88px;margin-left: 0px;padding: 0 5px;" id="endTimeStr"
								value="${endTimeStr }" class="selected" readonly="readonly" />
							&nbsp;
						</div>
						<input type="hidden" id="currentNearlyDays" name="currentNearlyDays" value="${currentNearlyDays}" />
						<button type="submit" class="btn sub-btn" style="padding-right: 10px; padding-left: 10px; padding-top: 5px; padding-bottom: 5px;"><fmt:message key="msg.pg.trade.queryStr"/></button>
					</form>
				</div>
				<div class="trade-manage-main">
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
						<tbody>
							<c:forEach var="tl" items="${tradeLogList.datas}">
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
										<fmt:formatNumber value="${tl.trade_bfx_fee > 0 ? tl.trade_bfx_fee : tl.trade_fee}" pattern="#0.########"
										/>(${tl.trade_bfx_fee > 0 ? 'bfx' : (tl.trade_trans_dirction == 1 ? (currentCoinPair.coin_name.toLowerCase()) : (currentCoinPair.market_coin_name.toLowerCase()))})
									</td>
									<td>
										<fmt:message key="msg.pg.tradeQuery.done"/>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<ul class="pagination">
						<pg:pager url="spotTrade/spotTradeQuery.do" items="${tradeLogList.total}" maxIndexPages="10" maxPageItems="${tradeLogList.maxitems}" export="currentPageNumber=pageNumber">
							<pg:param name="direction" value="${currentDirection}" />
							<pg:param name="coinPairName" value="${currentCoinPair.coin_pair_name }" />
							<pg:param name="currentNearlyDays" value="${currentNearlyDays}" />
							<pg:param name="startTimeStr" value="${startTimeStr}" />
							<pg:param name="endTimeStr" value="${endTimeStr}" />
							<pg:prev>
								<li class="disabled">
									<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>
								</li>
							</pg:prev>
							<pg:pages>
								<c:choose>
									<c:when test="${currentPageNumber eq pageNumber }">
										<li class="active">
											<a href="javascript:void(0)">${pageNumber }</a>
										</li>
									</c:when>
									<c:otherwise>
										<li>
											<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a>
										</li>
									</c:otherwise>
								</c:choose>
							</pg:pages>
							<pg:next>
								<li>
									<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a>
								</li>
							</pg:next>
						</pg:pager>
					</ul>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
	
		<script type="text/javascript">
	
		function set_currentNearlyDays(currentNearlyDays) {
			document.getElementById('currentNearlyDays').value = currentNearlyDays;
			document.getElementById('startTimeStr').value = "";
			document.getElementById('endTimeStr').value = "";
			document.tiaojian.submit();
		}
		
		var locale = "${locale}";
		if (locale == "zh_CN") {
        	locale = "cn";
		} else if (locale == "en_US") {
			locale = "en";
		} else if (locale == "ja_JP") {
			locale = "ja";
		} else if (locale == "ko_KR") {
			locale = "kr";
		} else if (locale == "zh_HK") {
			locale = "hk";
		} else {
			locale = "en";
		}
		
		var startDate = "${startTimeForStartDate}";
		
		// 获取当前日期
		var date = new Date();
		
		// 获取当前月份
		var nowMonth = date.getMonth() + 1;
		
		// 获取当前是几号
		var strDate = date.getDate();
		
		// 添加分隔符“-”
		var seperator = "-";
		
		// 对月份进行处理，1-9月在前面添加一个“0”
		if (nowMonth >= 1 && nowMonth <= 9) {
		   nowMonth = "0" + nowMonth;
		}
		
		// 对月份进行处理，1-9号在前面添加一个“0”
		if (strDate >= 0 && strDate <= 9) {
		   strDate = "0" + strDate;
		}
		
		// 最后拼接字符串，得到一个格式为(yyyy-MM-dd)的日期
		var nowDate = date.getFullYear() + seperator + nowMonth + seperator + strDate;
		
		$.noConflict();
		
		$(function()
	    {
	        $('#two-inputs').dateRangePicker(
	        {
	            language: locale,
	    
	            startDate: startDate,
	            endDate: nowDate,
	    
	            separator : ' - ',
	            getValue: function()
	            {
	                if ($('#startTimeStr').val() && $('#endTimeStr').val() )
	                    return $('#startTimeStr').val() + ' to ' + $('#endTimeStr').val();
	                else
	                    return '';
	            },
	            setValue: function(s,s1,s2)
	            {
	                $('#startTimeStr').val(s1);
	                $('#endTimeStr').val(s2);
	            }
	        });
	    });
	</script>
	
</html>
