<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<html>
<head>
	<base href="<%=actionPath%>">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>交易统计</title>
	<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
</head>
<body>
	<div id="main-content">
		<div class="clear"></div>
		<div class="content-box">
			<div class="content-box-header">
				<h3>
					交易统计
				</h3>
				<div class="clear"></div>
			</div>

			<div class="content-box-content">
				<div class="tab-content default-tab" id="tab1">
					<div class="notification success png_bg">
						<br>
						<div>
							<form action="<%=actionPath %>managerTradeStat/statsTrades.do" method="post" id="" name="">
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
								用户ID：
								<input type="text" name="userId" class="text-input " value="${userId == 0 ? '' : userId}" id="uid"></input>
								邮箱：
								<input type="text" name="userName" class="text-input " value="${userName}" id="uemail"></input>
								<button type="submit" class="button">
									查询
								</button>
							</form>
						</div>
						<br>
					</div>
					<br>
					<table id="changecolor">
							<thead>
								<tr>
									<th>
										用户
									</th>
									<th>
										合约号
									</th>
									<th>
										买多数量
									</th>
									<th>
										卖空数量
									</th>
									<th>
										平多数量
									</th>
									<th>
										平空数量
									</th>
									<th>
										手续费净收入
									</th>
									<th>
										返佣金额
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>
										<c:out value="${email }"></c:out>
									</td>
									<td>
										<c:out value="${current }"></c:out>
									</td>
									<td>
										<fmt:formatNumber value="${mdVol }" pattern="#0.0000"></fmt:formatNumber>
									</td>
									<td>
										<fmt:formatNumber value="${mkVol }" pattern="#0.0000"></fmt:formatNumber>
									</td>
									<td>
										<fmt:formatNumber value="${pdVol }" pattern="#0.0000"></fmt:formatNumber>
									</td>
									<td>
										<fmt:formatNumber value="${pkVol }" pattern="#0.0000"></fmt:formatNumber>
									</td>
									<td>
										<fmt:formatNumber value="${fee }" pattern="#0.0000"></fmt:formatNumber>
									</td>
									<td>
										<fmt:formatNumber value="${rebate }" pattern="#0.0000"></fmt:formatNumber>
									</td>
								</tr>
							</tbody>
						</table>
						<br>
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
</script>
</html>
