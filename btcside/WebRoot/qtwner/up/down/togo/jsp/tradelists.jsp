<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>交易信息</title>
		<!-- Reset Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<!-- Main Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<!-- Invalid Stylesheet. This makes stuff look pretty. Remove it if you want the CSS completely valid -->
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript">
		   function sure(qr,url)
		   {
			   if(qr!=null)
			   {
				   if(confirm(qr))
				   {
					   document.location.href = url;
				   }
			   }
			   else{
				   document.location.href = url;
				   }
				   
		   }
		   function postform(url) {
			   document.select.action=url;
			   $('#tag').attr("value", "");
			   document.select.submit();
		   }
		   function postquantform(url) {
			   document.select.action=url;
			   $('#tag').attr("value", "quant");
			   var quantId = $('#quantitativeId').val();
			   if (quantId == '') {
			   		confirm("请输入量化账户Id");
			   }else {
			   		document.select.submit();
			   }
		   }
		</script>

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
						交易信息管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
 								<form name="select" action="#" method="get">
									选择合约:
									<c:forEach items="${lstTrans}" var="l" varStatus="status">
										<a href="<%=actionPath%>managerTrade/loadTrades.do?tcode=${l.transaction_code }&buyuser=${buyuser}&selluser=${selluser}&startTimeStr=${startTime}&endTimeStr=${endTime}">
											<font color="${current eq l.transaction_code ? '#ff0000' : ''}">${l.transaction_code }</font>
										</a> &nbsp;
									</c:forEach>
	 								&nbsp; &nbsp; &nbsp;
									或者可以输入合约号:
									<input type="text" name="tcode" class="text-input small-input" value="${current}"></input>
									<button type="button" class="button" onclick="postform('<%=actionPath%>managerTrade/loadTrades.do')">
										查 询
									</button>
									<br />
									<br />
									开始时间：
									 <input name="startTimeStr" type="text" id="startTimeStr"
										value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
										class="Wdate" readonly="readonly" /> 
									结束时间：
									<input name="endTimeStr" type="text" id="endTimeStr"
										value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
										class="Wdate" readonly="readonly" />
									<button type="button" class="button" onclick="postform('<%=actionPath%>managerTrade/loadTrades.do')">
										查 询
									</button>
									<br>
									<br>
									买多用户ID
									<input type="text" name="buyuser" class="text-input small-input" value="${buyuser}"></input>
									卖空用户ID
									<input type="text" name="selluser" class="text-input small-input" value="${selluser}"></input>
									<button type="button" class="button" onclick="postform('<%=actionPath%>managerTrade/loadTrades.do')">
										查 询
									</button>
									<br />
									<br />
									量化账户ID(增加量化账户请以‘,’隔开)：
									<input type="text" name="quantitativeId" class="text-input small-input" value="${quantitativeId}" id="quantitativeId"></input>
									<input type="hidden" name="tag" value="" id="tag"></input>
									<button type="button" class="button" onclick="postquantform('<%=actionPath%>managerTrade/loadTrades.do')">
										剔除量化自成交查询
									</button>
								</form>
							</div>
							<br>
						</div>
						<div class="notification success png_bg">
							<div>
								多单持仓量：
								<span style="color: #57a000;">${dc+dd+db eq null ? 0 : dc+dd+db}</span>
								&nbsp;&nbsp;多单持仓可用：
								<span style="color: #57a000;">${dc eq null ? 0 : dc}</span>
								&nbsp;&nbsp;多单持仓冻结：
								<span style="color: #57a000;">${dd eq null ? 0 : dd}</span>
								&nbsp;&nbsp;多单爆仓量：
								<span style="color: #57a000;">${db eq null ? 0 : db}</span>
								<br>空单持仓量：
								<span style="color: #57a000;">${kc+kd+kb eq null ? 0 : kc+kd+kb}</span>
								&nbsp;&nbsp;空单持仓可用：
								<span style="color: #57a000;">${kc eq null ? 0 : kc}</span>
								&nbsp;&nbsp;空单持仓冻结：
								<span style="color: #57a000;">${kd eq null ? 0 : kd}</span>
								&nbsp;&nbsp;空单爆仓量：
								<span style="color: #57a000;">${kb eq null ? 0 : kb }</span>
							</div>
							<br>
							<div>
							总交易量:  ${total }
							</div>
							<br>
						</div>
						<button type="button" class="button" style="margin-top: 10px;"
								onclick="postform('<%=actionPath%>managerTrade/export.do')">导出Excel</button>
						<br>
						<button type="button" class="button" style="margin-top: 10px;"
								onclick="postquantform('<%=actionPath%>managerTrade/export.do')">导出Excel(剔除量化账户)</button>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										合约号
									</th>
									<th>
										买多方
									</th>
									<th>
										卖空方
									</th>
									<th>
										成交价
									</th>
									<th>
										成交数量
									</th>
									<th>
										成交时间
									</th>
									
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${pm.datas}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${status.index+1}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.transaction_code}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_buy_uid}"></c:out>---
											<c:choose>
												<c:when test="${l.trade_buy_type_id==1}">
													<font color="green">多单开仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==2}">
													<font color="green">多单平仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==3}">
													<font color="red">空头开仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==4}">
													<font color="red">空头平仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==5}">
													<font color="#FF0800">多单爆仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==6}">
													<font color="#FF0800">空单爆仓</font>
												</c:when>
												<c:otherwise>
													${l.trade_buy_type_id}
												</c:otherwise>
											</c:choose>
											<br/>
											<c:out value="${l.f_buy_user_email}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_sell_uid}"></c:out>---
											<c:choose>
												<c:when test="${l.trade_sell_type_id==1}">
													<font color="green">多单开仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==2}">
													<font color="green">多单平仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==3}">
													<font color="red">空头开仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==4}">
													<font color="red">空头平仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==5}">
													<font color="#FF0800">多单爆仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==6}">
													<font color="#FF0800">空单爆仓</font>
												</c:when>
												<c:otherwise>
													${l.trade_sell_type_id}
												</c:otherwise>
											</c:choose>
											<br/>
											<c:out value="${l.f_sell_user_email}"></c:out>
										</TD>
										<TD>
										<c:out value="${l.trade_price}"></c:out>
										</TD>
										<TD>
										<c:out value="${l.trade_count}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.trade_time}"></ut:formatDate>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerTrade/loadTrades.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="buyuser" value="${buyuser}"/>
											<pg:param name="tcode" value="${current}"/>
											<pg:param name="selluser" value="${selluser}"/>
											<pg:param name="startTimeStr" value="${startTime}"/>
											<pg:param name="endTimeStr" value="${endTime}"/>
											<pg:param name="tag" value="${tag}"/>
											<pg:param name="quantitativeId" value="${quantitativeId}"/>
											
											<pg:first>
												<a href="${pageUrl}" mce_href="${pageUrl}">首页</a>
											</pg:first>
											
											<pg:prev>
												<a href="${pageUrl }" mce_href="${pageUrl }">前页</a>
											</pg:prev>
											<pg:pages>
												<c:choose>
													<c:when test="${currentPageNumber eq pageNumber }">
														<font color="red">${pageNumber }</font>
													</c:when>
													<c:otherwise>
														<a href="${pageUrl }" mce_href="${pageUrl }">${pageNumber}</a>
													</c:otherwise>
												</c:choose>
											</pg:pages>
											<pg:next>
												<a href="${pageUrl }" mce_href="${pageUrl }">后页</a>
											</pg:next>

											<pg:last>
												<a href="${pageUrl }" mce_href="${pageUrl }">尾页</a>
											</pg:last>
										</pg:pager>
										
									</div>
									<!-- End .pagination -->
									<div class="clear"></div>
								</td>
							</tr>
						</tfoot>
						<script language="javascript" type="text/javascript">
<!--
//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
--></script>


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
				'width: 150px;' +
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
