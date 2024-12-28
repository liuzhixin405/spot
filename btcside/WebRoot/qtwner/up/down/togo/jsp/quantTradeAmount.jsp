<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>量化成交额</title>
		<!-- Reset Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<!-- Main Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<!-- Invalid Stylesheet. This makes stuff look pretty. Remove it if you want the CSS completely valid -->
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						量化成交额
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerFinancial/quantTradeAmount.do" method="post" id="" name="">
									合约号：
									<select name="code" id="code" >
										<c:forEach items="${lstTrans}" var="l">
											<c:if test="${l.transaction_code eq current}">
												<option value="${l.transaction_code}" selected="selected">${l.transaction_code}</option>
											</c:if>
											<c:if test="${l.transaction_code ne current}">
												<option value="${l.transaction_code}">${l.transaction_code}</option>
											</c:if>
										</c:forEach>
									</select>
									开始时间：
									 <input name="startTimeStr" type="text" id="startTimeStr"
										value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
										class="Wdate" readonly="readonly" /> 
									结束时间：
									<input name="endTimeStr" type="text" id="endTimeStr"
										value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
										class="Wdate" readonly="readonly" />
									<br>
									<br>
									量化账户ID：
									<input type="text" name="userId" class="text-input " value="${userId}" id="userId"></input>
									邮箱：
									<input type="text" name="userName" class="text-input " value="${userName}" id="userName"></input>
									<button type="submit" class="button">
										查询单个合约
									</button>
									<button type="button" class="button" onclick="stats()">
										查询所有合约
									</button>
								</form>
								<button type="button" class="button" style="margin-top: 10px;"
								onclick="exportExcel('data-table')">导出Excel</button>
							</div>
							<br>
						</div>
						<div class="notification success png_bg">
							<div>
								量化账户：<span style="color: #57a000;">${uId}</span>
							</div>
							<div>
								合约：<span style="color: #57a000;">${current}</span>
							</div>
							<div>
								成交总额（所有与量化账户）：<span style="color: #57a000;">${amount eq null ? 0 : amount}</span>
							</div>
							<div>
								成交额（量化账户maker）：<span style="color: #57a000;">${makerAmount eq null ? 0 : makerAmount}</span>
							</div>
							<div>
								成交额（量化账户taker）：<span style="color: #57a000;">${takerAmount eq null ? 0 : takerAmount}</span>
							</div>
							<br>
						</div>
						
						<br>
						<table id="data-table" class="statistical-table">
							<thead>
								<tr>
									<th>时间</th>
									<th>合约号</th>
									<th>总成交额</th>
									<th>maker</th>
									<th>taker</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${reportForms }" var="one" varStatus="status">
									<tr>
										<td class="sing-td"><c:out value="${one.time }"></c:out></td>
										<td class="mitily-td">
											<table>
												<tbody>
													<c:forEach items="${one.quantContractVos }" var="vo" varStatus="status">
														<tr>
															<td>${vo.code }</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</td>
										<td class="mitily-td">
											<table>
												<tbody>
													<c:forEach items="${one.quantContractVos }" var="vo" varStatus="status">
														<tr>
															<td><fmt:formatNumber value="${vo.amount }"
												pattern="#0.0000"></fmt:formatNumber></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</td>
										<td class="mitily-td">
											<table>
												<tbody>
													<c:forEach items="${one.quantContractVos }" var="vo" varStatus="status">
														<tr>
															<td><fmt:formatNumber value="${vo.makerAmount }"
												pattern="#0.0000"></fmt:formatNumber></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</td>
										<td class="mitily-td">
											<table>
												<tbody>
													<c:forEach items="${one.quantContractVos }" var="vo" varStatus="status">
														<tr>
															<td><fmt:formatNumber value="${vo.takerAmount }"
												pattern="#0.0000"></fmt:formatNumber></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
<script type="text/javascript" src="<%=actionPath%>common/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		var dateNow = new Date();
		var dateMon = dateNow.getMonth() + 1;
		dateMon = dateMon < 9 ? "0" + dateMon : dateMon;
		var dateDay = dateNow.getDate();
		var tomdateDay = dateNow.getDate() + 1;
		dateDay = dateDay < 10 ? "0" + dateDay : dateDay;
		tomdateDay = tomdateDay < 10 ? "0" + tomdateDay : tomdateDay;
		var str = dateNow.getFullYear() + "-" + dateMon + "-" + dateDay;
		var tomStr = dateNow.getFullYear() + "-" + dateMon + "-" + (tomdateDay);
		if ("${startTime }" == "") {
			$("#startTimeStr").val(str + " 00:00:00");
		}
		
		if ("${endTime }" == "") {
			$("#endTimeStr").val(tomStr + " 00:00:00");
		}
	});

	function stats() {
		var startTimeStr = $("#startTimeStr").val();
		var endTimeStr = $("#endTimeStr").val();
		var userId = $("#userId").val();
		var userName = $("#userName").val();
		document.location.href = '<%=actionPath%>managerFinancial/quantTradeAmount.do?tag=all&startTimeStr=' + startTimeStr
		 + '&endTimeStr=' + endTimeStr + '&userId=' + userId + '&userName=' + userName;
	}

	var idTmr;
	function getExplorer() {
		var explorer = window.navigator.userAgent;
		//ie  
		if (explorer.indexOf("MSIE") >= 0) {
			return 'ie';
		}
		//firefox  
		else if (explorer.indexOf("Firefox") >= 0) {
			return 'Firefox';
		}
		//Chrome  
		else if (explorer.indexOf("Chrome") >= 0) {
			return 'Chrome';
		}
		//Opera  
		else if (explorer.indexOf("Opera") >= 0) {
			return 'Opera';
		}
		//Safari  
		else if (explorer.indexOf("Safari") >= 0) {
			return 'Safari';
		}
	}
	function exportExcel(tableid) {
		if (getExplorer() == 'ie') {
			var curTbl = document.getElementById(tableid);
			var oXL = new ActiveXObject("Excel.Application");
			var oWB = oXL.Workbooks.Add();
			var xlsheet = oWB.Worksheets(1);
			var sel = document.body.createTextRange();
			sel.moveToElementText(curTbl);
			sel.select();
			sel.execCommand("Copy");
			xlsheet.Paste();
			oXL.Visible = true;

			try {
				var fname = oXL.Application.GetSaveAsFilename("Excel.xls", "Excel Spreadsheets (*.xls), *.xls");
			} catch (e) {
				print("Nested catch caught " + e);
			} finally {
				oWB.SaveAs(fname);
				oWB.Close(savechanges = false);
				oXL.Quit();
				oXL = null;
				idTmr = window.setInterval("Cleanup();", 1);
			}

		} else {
			tableToExcel(tableid)
		}
	}
	function Cleanup() {
		window.clearInterval(idTmr);
		CollectGarbage();
	}
	var tableToExcel = (function() {
		var uri = 'data:application/vnd.ms-excel;base64,',
			template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel"' +
				'xmlns="http://www.w3.org/TR/REC-html40"><head>' +
				' <style type="text/css">' +
				'table td {' +
				'border: 1px solid #0D0D0D;' +
				'width: 100px;' +
				'height: 30px;' +
				'text-align: center;' +
				'background-color: #FFFFFF;' +
				'color: #000000;' +
				' }' +
				'</style>' +
				'</head><body ><table class="excelTable">{table}</table></body></html>',
			base64 = function(s) {
				return window.btoa(unescape(encodeURIComponent(s)))
			},
			format = function(s, c) {
				return s.replace(/{(\w+)}/g,
					function(m, p) {
						return c[p];
					})
		}
		return function(table, name) {
			if (!table.nodeType)
				table = document.getElementById(table)
			var ctx = {
				worksheet : name || 'Worksheet',
				table : table.innerHTML
			}
			window.location.href = uri + base64(format(template, ctx))
		}
	})()
</script>
</html>
