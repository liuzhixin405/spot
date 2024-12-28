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
		<div class="container center-cont" style="padding: 20px;background: #FFFFFF;box-shadow: 0px 2px 6px 1px rgba(148,171,192,0.1);">
			<div class="main-wrap">
				<div class="block-title">
					<h2 style="margin: 0 20px;"><fmt:message key="msg.pg.trade.historyOrderQuery"/></h2>
					
					<c:if test="${!issz}">
						<form class="trade-title-lt" action="<%=basePath%>${homeName}/pendingQuery.do" name="tiaojian">
							
							<div class="selected rt" style="width: 150px; margin-right: 15px; margin-top: 17px;">
								<select name="czlx" id="czlx" onchange="javascript:document.tiaojian.submit();">
								    <c:forEach var="czlx" items="${czlxMap}">
								    	<c:if test="${czlx.key eq currentCzlx}">
								        	<option value="${czlx.key}" selected="selected">${czlx.value}</option>
								        </c:if>
								        <c:if test="${czlx.key ne currentCzlx}">
								        	<option value="${czlx.key}">${czlx.value}</option>
								        </c:if>
								    </c:forEach>
								</select>
							</div>
							
							<div class="selected rt" style="width: 150px; margin-right: 15px; margin-top: 17px;">
								<select name="pendingcode" id="transactionCode" onchange="javascript:document.tiaojian.submit();">
									<c:forEach var="ta" items="${transactionList}">
										<c:if test="${ta.transaction_code eq currentTransaction.transaction_code}">
											<option value="${ta.transaction_code}" selected="selected">
													${fn:toUpperCase(ta.transaction_code)} <fmt:message key="msg.pg.index.contract"/>
												</option>
										</c:if>
										<c:if test="${ta.transaction_code ne currentTransaction.transaction_code}">
											<option value="${ta.transaction_code}">
													${fn:toUpperCase(ta.transaction_code)} <fmt:message key="msg.pg.index.contract"/>
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
								<input type="hidden" id="currentNearlyDays" name="currentNearlyDays" value="${currentNearlyDays}" />
							</div>
							<button type="submit" class="btn sub-btn" style="padding-right: 10px; padding-left: 10px; padding-top: 5px; padding-bottom: 5px;"><fmt:message key="msg.pg.trade.queryStr"/></button>
						</form>
					</c:if>
					
				</div>

				<div class="trade-manage-main">
					<table cellpadding="0" cellspacing="0">
						<thead>
							<tr>
								<th><fmt:message key="msg.pg.trade.entrueTime"/></th>
								<th><fmt:message key="msg.pg.trade.contractNO"/></th>
								<th><fmt:message key="msg.pg.trade.typeOfList"/></th> 
								<th><fmt:message key="msg.pg.trade.leverage"/></th>
								<th><fmt:message key="msg.pg.trade.entruePrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
								<th><fmt:message key="msg.pg.trade.entrueNO"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> 
								<th><fmt:message key="msg.pg.trade.doneDeal"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> 
								<th><fmt:message key="msg.pg.trade.unsettled"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> 
								<th><fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
								<th><fmt:message key="msg.pg.tradeQuery.state"/></th> 
							</tr>
						</thead>
						<tbody>
							<c:forEach var="tl" items="${pendingLogList.datas}" varStatus="l">
								<tr>
									<td><ut:formatDate dateline="${tl.delegation_time}"></ut:formatDate></td>
									<td>${fn:toUpperCase(tl.transaction_code)}</td>
									<td>
										<c:choose>
											<c:when test="${tl.trade_type_id eq 1}">
												<b class="buy"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></b>
											</c:when>
											<c:when test="${tl.trade_type_id eq 2}">
												<b class="buy"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></b>
											</c:when>
											<c:when test="${tl.trade_type_id eq 3}">
												<b class="sell"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></b>
											</c:when>
											<c:when test="${tl.trade_type_id eq 4}">
												<b class="sell"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></b>
											</c:when>
											<c:when test="${tl.trade_type_id eq 5 }">
												<b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.duoBaoTrade"/></b>
											</c:when>
											<c:when test="${tl.trade_type_id eq 6}">
												<b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.kongBaoTrade"/></b>
											</c:when>
											<c:otherwise>
											</c:otherwise>
										</c:choose>
									</td>
									<td>${tl.multiple}</td>
									<td><fmt:formatNumber value="${tl.delegation_price}"></fmt:formatNumber></td>
									<td><fmt:formatNumber value="${tl.delegation_count}"></fmt:formatNumber></td>
									<td><fmt:formatNumber value="${tl.deal_count}"></fmt:formatNumber></td>
									<td><fmt:formatNumber value="${tl.undeal_count}"></fmt:formatNumber></td>
									<td>
										<c:choose>
											<c:when test="${tl.avg_price > 0}"><fmt:formatNumber value="${tl.avg_price}"></fmt:formatNumber></c:when>
											<c:otherwise>--</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${tl.action == 0 and tl.deal_count > 0}"><fmt:message key="msg.pg.trade.partDeal"/></c:when>
											<c:when test="${tl.action == 1}"><fmt:message key="msg.pg.trade.allDeal"/></c:when>
											<c:when test="${tl.action == 4 and tl.deal_count > 0}"><fmt:message key="msg.pg.trade.partCancel"/></c:when>
											<c:when test="${tl.action == 4 and tl.deal_count == 0}"><fmt:message key="msg.pg.trade.allreadyCancel"/></c:when>
											<c:otherwise><fmt:message key="msg.pg.trade.submitted"/></c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<ul class="pagination">
						<pg:pager url="${pageN }" items="${pendingLogList.total}" maxIndexPages="10" maxPageItems="${pendingLogList.maxitems}" export="currentPageNumber=pageNumber">
							<pg:param name="czlx" value="${currentCzlx}" />
							<pg:param name="pendingcode" value="${currentTransaction.transaction_code }" />
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
		function hideOverloadTip(index) {
			$("#baocangTips_" + index).hide();
			$("#baocangTips_arrow_" + index).hide();
		}

		function showOverloadTip(index) {
			$("#baocangTips_" + index).show();
			$("#baocangTips_arrow_" + index).show();
		}
	
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
