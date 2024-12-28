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
		<div class="container center-cont border-box-div" style="padding: 20px;background: #FFFFFF;box-shadow: 0px 2px 6px 1px rgba(148,171,192,0.1);">
			<div class="main-wrap">
				<div class="block-title">
					<h2 class="lt" style="margin: 0 20px;"><fmt:message key="msg.pg.tradeQuery.title"/></h2>
				</div>
				<div class="block-title">
					<c:if test="${!issz}">
						<form class="trade-title-lt" action="<%=basePath%>${homeName}/tradeQuery.do" name="tiaojian">
							
							<div class="selected lt" style="width: 121px;height: 30px;margin-right: 15px;margin-top: 9px;border:1px solid #DCE4EB;">
								<select name="pendingcode" id="transactionCode" onchange="javascript:document.tiaojian.submit();" style="width: 121px;height: 30px;background: transparent;color: #2B364B;font-size:14px;">
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
								<em class="triger-select"></em>
							</div>
							
							<div class="selected lt" style="width: 121px;height: 30px;margin-right: 15px;margin-top: 9px;border:1px solid #DCE4EB;">
								<select name="czlx" id="czlx" onchange="javascript:document.tiaojian.submit();" style="width: 121px;height: 30px;background: transparent;color: #2B364B;font-size:14px;">
								    <c:forEach var="czlx" items="${czlxMap}">
								    	<c:if test="${czlx.key eq currentCzlx}">
								        	<option value="${czlx.key}" selected="selected">${czlx.value}</option>
								        </c:if>
								        <c:if test="${czlx.key ne currentCzlx}">
								        	<option value="${czlx.key}">${czlx.value}</option>
								        </c:if>
								    </c:forEach>
								</select>
								<em class="triger-select"></em>
							</div>
							<div class="daysChaose-wrap">
								<a href="javascript:void(0)" <c:if test="${currentNearlyDays eq '7'}"> class="current"</c:if> onclick="set_currentNearlyDays('7')"><fmt:message key="msg.pg.trade.nearly7days"/></a> 
								<a href="javascript:void(0)" <c:if test="${currentNearlyDays eq '30'}"> class="current"</c:if> onclick="set_currentNearlyDays('30')"><fmt:message key="msg.pg.trade.nearly30days"/></a> 
								<a href="javascript:void(0)" <c:if test="${currentNearlyDays eq '90'}"> class="current"</c:if> onclick="set_currentNearlyDays('90')"><fmt:message key="msg.pg.trade.nearly90days"/></a> 
							</div>
							
							<div id="two-inputs" style="display: inline-block;line-height: 50px;margin-left: 30px;">
								<%-- <fmt:message key="msg.pg.trade.queryDate"/> --%>
								<input name="startTimeStr" type="text" style="width:100px;height:30px;margin-left: 0px;padding: 0 5px;"" id="startTimeStr"
									value="${startTimeStr }" class="selected" readonly="readonly" />
								&nbsp;-&nbsp;
								<input name="endTimeStr" type="text" style="width:100px;height:30px;margin-left: 0px;padding: 0 5px;" id="endTimeStr"
									value="${endTimeStr }" class="selected" readonly="readonly" />
								&nbsp;
								<input type="hidden" id="currentNearlyDays" name="currentNearlyDays" value="${currentNearlyDays}" />
							</div>
							<div style="display: inline-block;line-height: 50px;margin-left: 20px;">
								<button type="submit" class="btn sub-btn" style="padding: 6px 17px;"><fmt:message key="msg.pg.trade.queryStr"/></button>
							</div>
						</form>
					</c:if>
					
				</div>

				<div class="trade-manage-main">
					<table cellpadding="0" cellspacing="0">
						<thead>
							<tr>
								<th>
									<fmt:message key="msg.pg.tradeQuery.dealTime"/>
								</th>
								<th>
									<fmt:message key="msg.pg.trade.contractNO"/>
								</th>
								<th>
									<fmt:message key="msg.pg.tradeQuery.dealType"/>
								</th>
								<th>
									<fmt:message key="msg.pg.tradeQuery.holdAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})
								</th>
								<th>
									<fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})
								</th>
								<th>
									<fmt:message key="msg.pg.tradeQuery.dealAmount"/>
								</th>
								<th>
									<fmt:message key="msg.pg.trade.depositCash"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})
								</th>
								<th>
									<fmt:message key="msg.pg.tradeQuery.money"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})
								</th>
								<th>
									<fmt:message key="msg.pg.trade.commission"/>${homeName=='vtrade' ? '(BFC)' : ''}
								</th>
								<th>
									<fmt:message key="msg.pg.tradeQuery.state"/>
								</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="tl" items="${tradeLogList.datas}" varStatus="l">
								<tr>
									<td>
										<ut:formatDate dateline="${tl.trade_time}"></ut:formatDate>
									</td>
									<td>
										${fn:toUpperCase(tl.transaction_code)}
									</td>
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
												<div class="addTips" onmouseout="hideOverloadTip(${l.index})" onmouseover="showOverloadTip(${l.index})">
													<div class="addTips-arrow" id="baocangTips_arrow_${l.index}"></div>
													<div class="baocangTips" id="baocangTips_${l.index}" style="${l.index == 4 ? 'top:-43px;' : ''}">
														<fmt:message key="msg.pg.trade.bao.text.your" />${fn:toUpperCase(tl.transaction_code)}<fmt:message key="msg.pg.trade.bao.text.contract" />
														<fmt:message key="msg.pg.trade.bao.text.buy" />${tl.trade_multiple}<fmt:message key="msg.pg.trade.bao.text.note1" />
														<c:if test="${tl.force_price > 0}"><span style="color:red;">${tl.force_price.doubleValue()}${(homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</span></c:if>
														<fmt:message key="msg.pg.trade.bao.text.note12" /><span style="color:red;">${tl.trade_average_price.doubleValue()}</span>
														<fmt:message key="msg.pg.trade.bao.text.note2" />
													</div>
												</div>
											</c:when>
											<c:when test="${tl.trade_type_id eq 6}">
												<b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.kongBaoTrade"/></b>
												<div class="addTips" onmouseout="hideOverloadTip(${l.index})" onmouseover="showOverloadTip(${l.index})">
													<div class="addTips-arrow" id="baocangTips_arrow_${l.index}"></div>
													<div class="baocangTips" id="baocangTips_${l.index}" style="${l.index == 4 ? 'top:-43px;' : ''}">
														<fmt:message key="msg.pg.trade.bao.text.your" />${fn:toUpperCase(tl.transaction_code)}<fmt:message key="msg.pg.trade.bao.text.contract" />
														<fmt:message key="msg.pg.trade.bao.text.sell" />${tl.trade_multiple}<fmt:message key="msg.pg.trade.bao.text.note1" />
														<c:if test="${tl.force_price > 0}"><span style="color:red;">${tl.force_price.doubleValue()}${(homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</span></c:if>
														<span style="color:red;">${tl.trade_average_price.doubleValue()}</span>
														<fmt:message key="msg.pg.trade.bao.text.note2" />
													</div>
												</div>
											</c:when>
											<c:otherwise>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${tl.hold_price }" pattern="#0.########" />
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${tl.trade_type_id eq 5 or tl.trade_type_id eq 6}">
												--
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${issz}">
														<fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.#" />
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.####" />
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${issz}">
												<fmt:formatNumber value="${tl.trade_count}" pattern="#0" /><fmt:message key="msg.pg.tradeEntrust.zhang"/>
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${tl.trade_count}" pattern="#0.####" />
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${tl.trade_type_id eq 5 or tl.trade_type_id eq 6}">
												--
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${issz}">
														<fmt:formatNumber value="${tl.trade_deposit}" pattern="#0" /><fmt:message key="msg.pg.tradeEntrust.zhang"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${tl.trade_deposit}" pattern="#0.######" />
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</td>
									<td <c:if test="${tl.trade_income lt 0}"> class="sell"</c:if>
										<c:if test="${tl.trade_income gt 0}"> class="buy"</c:if>>
										<c:choose>
											<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
											</c:when>
											<c:when test="${tl.trade_type_id eq 5 or tl.trade_type_id eq 6}">
												<fmt:formatNumber value="${tl.trade_income + tl.trade_fee }" pattern="#0.########" />
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${tl.trade_income }" pattern="#0.########" />
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${tl.trade_type_id eq 5 or tl.trade_type_id eq 6}">
												--
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${tl.trade_bfx_fee > 0 ? tl.trade_bfx_fee : tl.trade_fee}" pattern="#0.########" />${homeName == 'vtrade' ? '' : (tl.trade_bfx_fee > 0 ? '(bfx)' : (homeName=='trade' ? '(usdt)' : '('.concat(homeName).concat(')')))}
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<fmt:message key="msg.pg.tradeQuery.done"/>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<ul class="pagination">
						<pg:pager url="${pageN }" items="${tradeLogList.total}" maxIndexPages="10" maxPageItems="${tradeLogList.maxitems}" export="currentPageNumber=pageNumber">
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
