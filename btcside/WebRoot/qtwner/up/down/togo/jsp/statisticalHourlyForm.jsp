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
<title>统计报表</title>
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
				<h3>合约-每小时统计报表</h3>
				<div class="clear"></div>
			</div>

			<div class="content-box-content">
				<div class="tab-content default-tab" id="tab1">
					<div class="notification success png_bg">
						<br>
						<div>
							<form action="<%=actionPath%>statisticalForm/hourlyDatas.do"
								method="post" id="selectForm" name="selectForm">
								开始时间 <input name="startTimeStr" type="text" id="startTimeStr"
									value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
									class="Wdate" readonly="readonly" /> 
								&nbsp;&nbsp;&nbsp;&nbsp;结束时间  <input name="endTimeStr" type="text" id="endTimeStr"
									value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
									class="Wdate" readonly="readonly" /> 
								<button type="submit" class="button">查询</button>
								<button type="button" class="button" onclick="stats()">带模拟盘查询</button>
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
								<th>注册用户</th>
								<th>登陆用户</th>
								<th>总注册数量</th>
								<th>总交易人数</th>
								<th>手续费总额</th>
								<th>返佣总额</th>
								<th>Btcside手续费总额</th>
								<th>Btcside返佣总额</th>
								<th>合约号</th>
								<th>持仓数量</th>
								<th>保证金总量</th>
								<th>交易人数</th>
								<th>手续费金额</th>
								<th>返佣金额</th>
								<th>Btcside手续费金额</th>
								<th>Btcside返佣金额</th>
								
								<c:if test="${tag eq 'addSimulate' }">
								<th>模拟盘-总交易人数</th>
								<th>模拟盘-手续费总额</th>
								<th>模拟盘-合约号</th>
								<th>模拟盘-持仓数量</th>
								<th>模拟盘-保证金总量</th>
								<th>模拟盘-合约可用总额</th>
								<th>模拟盘-真实交易总量</th>
								<th>模拟盘-交易人数</th>
								<th>模拟盘-手续费金额</th>
								</c:if>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${reportForms }" var="one" varStatus="status">
								<tr>
									<td class="sing-td"><c:out value="${one.time }"></c:out></td>
									<td class="sing-td"><c:out value="${one.todayRegister }"></c:out></td>
									<td class="sing-td"><c:out value="${one.loginUserCount }"></c:out></td>
									<td class="sing-td"><c:out value="${one.totalRegister }"></c:out></td>
									<td class="sing-td"><c:out value="${one.totalTradeUser }"></c:out></td>
									<td class="sing-td">${(one.time.compareTo('2020-08-24') >= 0 and one.time.compareTo('2021-03-09') < 0) ? one.totalFee/2 : one.totalFee }</td>
									<td class="sing-td">${(one.time.compareTo('2020-08-24') >= 0 and one.time.compareTo('2021-03-09') < 0) ? one.totalRebate/2 : one.totalRebate }</td>
									<td class="sing-td">${(one.time.compareTo('2020-08-24') >= 0 and one.time.compareTo('2021-03-09') < 0) ? one.totalBfxFee/2 : one.totalBfxFee }</td>
									<td class="sing-td">${(one.time.compareTo('2020-08-24') >= 0 and one.time.compareTo('2021-03-09') < 0) ? one.totalBfxRebate/2 : one.totalBfxRebate }</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.conForms }" var="conForm1" varStatus="status">
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
												<c:forEach items="${one.conForms }" var="conForm2" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${conForm2.holdAmount }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.conForms }" var="conForm3" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${conForm3.depositAmount }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.conForms }" var="conForm6" varStatus="status">
													<tr>
														<td><c:out value="${conForm6.tradeUser }"></c:out></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.conForms }" var="conForm7" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${(one.time.compareTo('2020-08-24') >= 0 and one.time.compareTo('2021-03-09') < 0) ? conForm7.feeAmount/2 : conForm7.feeAmount }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.conForms }" var="conForm8" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${(one.time.compareTo('2020-08-24') >= 0 and one.time.compareTo('2021-03-09') < 0) ? conForm8.rebate/2 : conForm8.rebate }" pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.conForms }" var="conForm9" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${(one.time.compareTo('2020-08-24') >= 0 and one.time.compareTo('2021-03-09') < 0) ? conForm9.bfxFeeAmount/2 : conForm9.bfxFeeAmount }" pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.conForms }" var="conForm10" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${(one.time.compareTo('2020-08-24') >= 0 and one.time.compareTo('2021-03-09') < 0) ? conForm10.bfxRebate/2 : conForm10.bfxRebate }" pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									
									<c:if test="${tag eq 'addSimulate' }">
									<td class="sing-td"><c:out value="${one.vtotalTradeUser }"></c:out></td>
									<td class="sing-td"><fmt:formatNumber value="${one.vtotalFee }"
											pattern="#0.0000"></fmt:formatNumber></td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.vconForms }" var="conForm14" varStatus="status">
													<tr>
														<td>${conForm14.code }</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.vconForms }" var="conForm8" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${conForm8.vholdAmount }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.vconForms }" var="conForm9" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${conForm9.vdepositAmount }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.vconForms }" var="conForm10" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${conForm10.vcontractBalance }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.vconForms }" var="conForm11" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${conForm11.vtradeAmount }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.vconForms }" var="conForm12" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${conForm12.vtradeUser }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									<td class="mitily-td">
										<table>
											<tbody>
												<c:forEach items="${one.vconForms }" var="conForm13" varStatus="status">
													<tr>
														<td><fmt:formatNumber value="${conForm13.vfeeAmount }"
											pattern="#0.0000"></fmt:formatNumber></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
									</c:if>
									
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
		dateMon = dateMon < 10 ? "0" + dateMon : dateMon;
		var dateDay = dateNow.getDate();
		dateDay = dateDay < 10 ? "0" + dateDay : dateDay;
		var dateHour = dateNow.getHours();
		var dateNextHour = dateNow.getHours() + 1;
		dateHour = dateHour < 10 ? "0" + dateHour : dateHour;
		dateNextHour = dateNextHour < 10 ? "0" + dateNextHour : dateNextHour;
		var str = dateNow.getFullYear() + "-" + dateMon + "-" + dateDay + " " + dateHour;
		var tomStr = dateNow.getFullYear() + "-" + dateMon + "-" + dateDay + " " + dateNextHour;
		if ("${startTime }" == "") {
			$("#startTimeStr").val(str + ":00:00");
		}
		
		if ("${endTime }" == "") {
			$("#endTimeStr").val(tomStr + ":00:00");
		}
	});
	
	function stats() {
		var startTimeStr = $("#startTimeStr").val();
		var endTimeStr = $("#endTimeStr").val();
		document.location.href = '<%=actionPath%>statisticalForm/hourlyDatas.do?tag=addSimulate&startTimeStr=' + startTimeStr + '&endTimeStr=' + endTimeStr;
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
