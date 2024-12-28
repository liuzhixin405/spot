<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>量化交易信息</title>
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
						量化交易信息
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
 								<form action="<%=actionPath %>managerQuantRisk/quantTradeMakerTaker.do" method="post" id="" name="">
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
									类型：
									<select name="type" id="type" >
										<c:if test="${type eq 1 or type eq 0}">
											<option value="1" selected="selected">maker</option>
											<option value="2" >taker</option>
										</c:if>
										<c:if test="${type eq 2 }">
											<option value="1" >maker</option>
											<option value="2" selected="selected">taker</option>
										</c:if>
									</select>
									量化用户ID：
									<input type="text" name="userId" class="text-input " value="${userId == 0 ? '' : userId}" id="uid"></input>
									量化用户邮箱：
									<input type="text" name="userName" class="text-input " value="${userName}" id="uemail"></input>
									开始时间：
									 <input name="startTimeStr" type="text" id="startTimeStr"
										value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
										class="Wdate" readonly="readonly" /> 
									结束时间：
									<input name="endTimeStr" type="text" id="endTimeStr"
										value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
										class="Wdate" readonly="readonly" /> 
									
									<button type="submit" class="button">
										查询
									</button>
								</form>
							</div>
							<br>
						</div>
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
											<c:out value="${l[0]}"></c:out>
										</TD>
										<TD>
											<c:out value="${l[1]}"></c:out>---
											<c:choose>
												<c:when test="${l[2]==1}">
													<font color="green">多单开仓</font>
												</c:when>
												<c:when test="${l[2]==2}">
													<font color="green">多单平仓</font>
												</c:when>
												<c:when test="${l[2]==3}">
													<font color="red">空头开仓</font>
												</c:when>
												<c:when test="${l[2]==4}">
													<font color="red">空头平仓</font>
												</c:when>
												<c:when test="${l[2]==5}">
													<font color="#FF0800">多单爆仓</font>
												</c:when>
												<c:when test="${l[2]==6}">
													<font color="#FF0800">空单爆仓</font>
												</c:when>
												<c:otherwise>
													${l[2]}
												</c:otherwise>
											</c:choose>
											<br/>
											<c:out value="${l[3]}"></c:out>
										</TD>
										<TD>
											<c:out value="${l[4]}"></c:out>---
											<c:choose>
												<c:when test="${l[5]==1}">
													<font color="green">多单开仓</font>
												</c:when>
												<c:when test="${l[5]==2}">
													<font color="green">多单平仓</font>
												</c:when>
												<c:when test="${l[5]==3}">
													<font color="red">空头开仓</font>
												</c:when>
												<c:when test="${l[5]==4}">
													<font color="red">空头平仓</font>
												</c:when>
												<c:when test="${l[5]==5}">
													<font color="#FF0800">多单爆仓</font>
												</c:when>
												<c:when test="${l[5]==6}">
													<font color="#FF0800">空单爆仓</font>
												</c:when>
												<c:otherwise>
													${l[5]}
												</c:otherwise>
											</c:choose>
											<br/>
											<c:out value="${l[6]}"></c:out>
										</TD>
										<TD>
										<c:out value="${l[7]}"></c:out>
										</TD>
										<TD>
										<c:out value="${l[8]}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l[9]}"></ut:formatDate>
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
										<pg:pager url="managerQuantRisk/quantTradeMakerTaker.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="code" value="${current}"/>
											<pg:param name="startTimeStr" value="${startTime}"/>
											<pg:param name="endTimeStr" value="${endTime}"/>
											<pg:param name="userId" value="${userId}"/>
											<pg:param name="userName" value="${userName}"/>
											
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
