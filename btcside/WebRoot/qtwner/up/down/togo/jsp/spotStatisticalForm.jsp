<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
<base href="<%=actionPath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>币币-每日统计报表</title>
<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css"
	media="screen" />
<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css"
	media="screen" />
<link rel="stylesheet" href="<%=jspPath%>css/invalid.css"
	type="text/css" media="screen" />

</head>
<body>
	<div id="main-content">
		<div class="clear"></div>
		<div class="content-box">
			<div class="content-box-header">
				<h3>币币-每日统计报表</h3>
				<div class="clear"></div>
			</div>

			<div class="content-box-content">
				<div class="tab-content default-tab" id="tab1">
					<div class="notification success png_bg">
						<br>
						<div>
							<form action="<%=actionPath%>statisticalForm/daySpotDatas.do"
								method="post" id="selectForm" name="selectForm">
								开始时间 <input name="startTimeStr" type="text" id="startTimeStr"
									value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
									class="Wdate" readonly="readonly" /> 
								&nbsp;&nbsp;&nbsp;&nbsp;结束时间  <input name="endTimeStr" type="text" id="endTimeStr"
									value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
									class="Wdate" readonly="readonly" /> 
								<button type="submit" class="button">查询</button>
							</form>
							<button type="button" class="button" style="margin-top: 10px;"
								onclick="exportExcel('data-table')">导出Excel</button>
						</div>
						<br>
					</div>
					<br>
					<table id="data-table" class="statistical-table">
						<thead>
							<tr>
								<th>时间</th>
								<th>总交易人数</th>
								<th>Btcside抵扣手续费(Btcside)</th>
								<th>Btcside返佣(Btcside)</th>
								<th>手续费(usdt)</th>
								<th>返佣(usdt)</th>
								<th>交易对</th>
								<th>真实交易额</th>
								<th>交易人数</th>
								<th>手续费(本币)</th>
								<th>返佣(本币)</th>
								<th>手续费(usdt)</th>
								<th>返佣(usdt)</th>
								<th>Btcside抵扣手续费(Btcside)</th>
								<th>Btcside返佣(Btcside)</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${reportForms }" var="one" varStatus="status">
								<tr>
									<td class="sing-td"><c:out value="${one.time }"></c:out></td>
									<td class="sing-td"><c:out value="${one.totalTradeUser }"></c:out></td>
									<td class="sing-td">${one.totalBfxFee }</td>
									<td class="sing-td">${one.totalBfxRebate }</td>
									<td class="sing-td">${one.totalFee }</td>
									<td class="sing-td">${one.totalMarketRebate }</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm1" varStatus="status">
													<tr>
														<td>${conForm1.code }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm2" varStatus="status">
													<tr>
														<td>${conForm2.tradeAmount }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm3" varStatus="status">
													<tr>
														<td><c:out value="${conForm3.tradeUser }"></c:out></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm4" varStatus="status">
													<tr>
														<td>${conForm4.buyTradeFee }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm5" varStatus="status">
													<tr>
														<td>${conForm5.coinRebate }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm6" varStatus="status">
													<tr>
														<td>${conForm6.sellTradeFee }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm7" varStatus="status">
													<tr>
														<td>${conForm7.marketRebate }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm8" varStatus="status">
													<tr>
														<td>${conForm8.buyTradeBfxFee + conForm8.sellTradeBfxFee}</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.spotFormVos }" var="conForm9" varStatus="status">
													<tr>
														<td>${conForm9.bfxRebate }</td>
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
		</div>
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
