<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.purseIn"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=r%>">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/jQueryDateRangePicker/css/daterangepicker.min.css">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/jQueryDateRangePicker/datePicker.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script> 
		<!--[if lte IE 6]>
		<script src="js/ie6png.js" type="text/javascript"></script>
		    <script type="text/javascript">
		        DD_belatedPNG.fix('.test, ul, img, li, input , a, div,span,em,i');
		    </script>
		<![endif]-->
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/transfer.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/jQueryDateRangePicker/js/moment.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/jQueryDateRangePicker/js/jquery.daterangepicker.min.js"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
		<div class="add-windows" id="alert-windows"></div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->
		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			
			<div class="main-rt-box center-cont">
				<div class="main-wrap">
					<div class="block-title">
						<h2 class="lt">
						<!-- 资金转出&转入记录 -->
							<c:if test="${ type != 2 == true}"><fmt:message key="msg.pg.userCenter.inTransferRecord"/></c:if>
							<c:if test="${ type != 2 == false}"><fmt:message key="msg.pg.userCenter.outTransferRecord"/></c:if>
						</h2>
					</div>
					<div class="block-title">
						<form class="trade-title-lt" action="<%=basePath%>${homeName}/${actionName}.do" name="tiaojian">
							<input type="hidden" name="partition" value="${partition }">
							<div class="selected lt" style="width: 121px;height: 30px; margin-right: 15px; margin-top: 9px;border:1px solid #DCE4EB;">
								<select name="bqCode" id="bqCode" onchange="javascript:document.tiaojian.submit();" style="width: 121px;height: 30px;background: transparent;color: #2B364B;font-size:14px;">
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
							
							<div class="selected lt" style="width: 121px;height: 30px; margin-right: 15px; margin-top: 9px;border:1px solid #DCE4EB;">
								<select name="type" id="type" onchange="javascript:document.tiaojian.submit();" style="width: 121px;height: 30px;background: transparent;color: #2B364B;font-size:14px;">
									<c:if test="${type != 2}">
										<option selected="selected" value="1"><fmt:message key="msg.pg.trade.rollIn"/></option>
										<option value="2"><fmt:message key="msg.pg.trade.rollOut"/></option>
									</c:if>
									<c:if test="${type == 2}">
										<option value="1"><fmt:message key="msg.pg.trade.rollIn"/></option>
										<option selected="selected" value="2"><fmt:message key="msg.pg.trade.rollOut"/></option>
									</c:if>
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
								<input name="startTimeStr" type="text" style="width:100px;height:30px;margin-left: 0px;padding: 0 5px;" id="startTimeStr"
									value="${startTimeStr }" class="selected" readonly="readonly" />
								&nbsp;-&nbsp;
								<input name="endTimeStr" type="text" style="width:100px;height:30px;margin-left: 0px;padding: 0 5px;" id="endTimeStr"
									value="${endTimeStr }" class="selected" readonly="readonly" />
								&nbsp;
								<input type="hidden" id="currentNearlyDays" name="currentNearlyDays" value="${currentNearlyDays}" />
							</div>
							<div style="display: inline-block;line-height: 50px;margin-left: 20px;">
								<button type="submit" class="btn sub-btn" style="padding-right: 21px; padding-left: 21px; padding-top: 6px; padding-bottom: 6px;"><fmt:message key="msg.pg.trade.queryStr"/></button>
							</div>
						</form>
					</div>
				</div>
				<div class="main-wrap">
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<c:if test="${ type != 2 == true}"><fmt:message key="msg.pg.userCenter.inTransferTime"/></c:if>
										<c:if test="${ type != 2 == false}"><fmt:message key="msg.pg.userCenter.outTransferTime"/></c:if>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.nameContract"/>
									</th>
									<th>
										<c:if test="${ type != 2 == true}"><fmt:message key="msg.pg.userCenter.inTransferMoney"/></c:if>
										<c:if test="${ type != 2 == false}"><fmt:message key="msg.pg.userCenter.outTransferMoney"/></c:if>
										<%-- ${type != 2 ? '转入金额' : '转出金额'} --%>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.originMoney"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.curtMoney"/>
									</th>
								</tr>
							</thead>
							<tbody>
                            	<c:if test="${pm.datas == null || empty pm.datas}">
									<tr>
										<td colspan="5" bgcolor="#ffffff">
											 <fmt:message key="msg.pg.userCenter.noData"/>
										</td>
									</tr>
								</c:if>
								<c:if test="${pm.datas != null}">
									<c:forEach items="${pm.datas}" var="log" varStatus="l">
										<tr>
											<td>
												<ut:formatDate dateline="${log.dateline }"></ut:formatDate>
											</td>
											<td >
												<c:if test="${actionName eq 'transfer'}">${currentTransaction.transaction_show_name }</c:if>
												<c:if test="${actionName ne 'transfer'}">${fn:toUpperCase(currentTransaction.transaction_code) }</c:if>
											</td>
											<td>
												<fmt:formatNumber value="${log.change_amount }" pattern="#.####"></fmt:formatNumber>
												<c:if test="${actionName eq 'ntransfer'}">USDR</c:if>
												<c:if test="${actionName eq 'transfer'}">USDT</c:if>
											</td>
											<td>
												<fmt:formatNumber value="${log.current_amount }" pattern="#.####"></fmt:formatNumber>
												<c:if test="${actionName eq 'ntransfer'}">USDR</c:if>
												<c:if test="${actionName eq 'transfer'}">USDT</c:if>
											</td>
											<td>
												<fmt:formatNumber value="${log.lastest_amount }" pattern="#.####"></fmt:formatNumber>
												<c:if test="${actionName eq 'ntransfer'}">USDR</c:if>
												<c:if test="${actionName eq 'transfer'}">USDT</c:if>
											</td>
										</tr>
									</c:forEach>
								</c:if>
							</tbody>
						</table>
					</div>
					<ul class="pagination">
							<pg:pager url="${pageN}" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
								<pg:param name="partition" value="${partition }" />
								<pg:param name="type" value="${type }" />
								<pg:param name="bqCode" value="${bqCode }" />
								<pg:param name="currentNearlyDays" value="${currentNearlyDays}" />
								<pg:param name="startTimeStr" value="${startTimeStr}" />
								<pg:param name="endTimeStr" value="${endTimeStr}" />
								
								<pg:prev>
								<li class="disabled">
									<a href="<%=basePath%>${pageUrl}"
										mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>
										</li>
								</pg:prev>
								
								<pg:pages>
									<c:choose>
										<c:when test="${currentPageNumber eq pageNumber }">
											<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
										</c:when>
										<c:otherwise>
										<li>
											<a href="<%=basePath%>${pageUrl }"
												mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
										</c:otherwise>
									</c:choose>
								</pg:pages>

								<pg:next>
								<li>
									<a href="<%=basePath%>${pageUrl }"
										mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a>
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
	function checkTransable() {
		var balance = "${btc }";
		$("#amount").val(balance);
		$("#amountmsg").html("");
	}
	var message = {};
	message["zh_CN"] = {
		amountError : "金额不正确",
		amountInsufficient : "余额不足",
		transferNum : "转账数量错误",
		amounttrsNull : "数量不能为0或空",
		warmTip: "温馨提示",
		sureTip: "确定",
		riskPause: "风控系统暂停",
		transferOut: "合约转出功能",
		transferIn: "合约转入功能"
	};
	message["zh_HK"] = {
		amountError : "金額不正確",
		amountInsufficient : "餘額不足",
		transferNum : "轉賬數量錯誤",
		amounttrsNull : "數量不能為0或空",
		warmTip: "溫馨提示",
		sureTip: "確定",
		riskPause: "風控系統暫停",
		transferOut: "合約轉出功能",
		transferIn: "合約轉入功能"
	};
	message["en_US"] = {
		amountError : "incorrect amount",
		amountInsufficient : "Insufficient balance",
		transferNum : "Wrong amount",
		amounttrsNull : "Quantity can't be zero or empty",
		warmTip : "Warm prompt",
		sureTip: "Sure",
		riskPause: "Risk control system paused ",
		transferOut: " contract transfer out",
		transferIn: " contract transfer in"
	};
	message["ja_JP"] = {
		amountError : "金額が正しくありません",
		amountInsufficient : "残高不足",
		transferNum : "振込数が間違っています",
		amounttrsNull : "数量を0または空欄にすることはできません",
		warmTip: "ヒント",
		sureTip: "確定",
		riskPause: "リスト管理システムは一時停止する",
		transferOut: "契約転出機能",
		transferIn: "契約転入機能"
	};
	message["ko_KR"] = {
		amountError : "금액이 잘못되었습니다",
		amountInsufficient : "잔액 부족",
		transferNum : "잘못된 금액",
		amounttrsNull : "수량은 0이거나 비어있을 수 없습니다",
		warmTip: "팁",
		sureTip: "결정",
		riskPause: "에어 컨트롤 시스템 중단",
		transferOut: "계약 롤아웃 기능",
		transferIn: "계약 이체 기능"
	};
	message["ru_RU"] = {
		amountError : "неверное количество",
		amountInsufficient : "Недостаточный баланс",
		transferNum : "Неверное количество",
		amounttrsNull : "Количество не может быть нулевым или пустым",
		warmTip : "Важное напоминание",
		sureTip: "Конечно",
		riskPause: "Система контроля рисков приостановлена ",
		transferOut: "перевод контракта",
		transferIn: "перевод контракта в"
	};
	var MSG = message[locale];
	
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
