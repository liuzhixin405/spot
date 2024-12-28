<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>用户交易明细</title>
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
						用户交易明细
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerQuantRisk/quantTradeDetail.do" method="post" id="" name="">
									合约号：
									<select name="code" id="code" >
										<c:forEach items="${lstTrans}" var="l">
											<c:if test="${l.transaction_code eq code}">
												<option value="${l.transaction_code}" selected="selected">${l.transaction_code}</option>
											</c:if>
											<c:if test="${l.transaction_code ne code}">
												<option value="${l.transaction_code}">${l.transaction_code}</option>
											</c:if>
										</c:forEach>
									</select>
									成交类型：
									<select name="tradeType" id="tradeType" >
										<c:if test="${tradeType eq 1 or tradeType eq 0}">
											<option value="1" selected="selected">多单开仓</option>
											<option value="2" >多单平仓</option>
											<option value="3" >空单开仓</option>
											<option value="4" >空单平仓</option>
											<option value="5" >多单爆仓</option>
											<option value="6" >空单爆仓</option>
										</c:if>
										<c:if test="${tradeType eq 2 }">
											<option value="1" >多单开仓</option>
											<option value="2" selected="selected">多单平仓</option>
											<option value="3" >空单开仓</option>
											<option value="4" >空单平仓</option>
											<option value="5" >多单爆仓</option>
											<option value="6" >空单爆仓</option>
										</c:if>
										<c:if test="${tradeType eq 3 }">
											<option value="1" >多单开仓</option>
											<option value="2" >多单平仓</option>
											<option value="3" selected="selected">空单开仓</option>
											<option value="4" >空单平仓</option>
											<option value="5" >多单爆仓</option>
											<option value="6" >空单爆仓</option>
										</c:if>
										<c:if test="${tradeType eq 4 }">
											<option value="1" >多单开仓</option>
											<option value="2" >多单平仓</option>
											<option value="3" >空单开仓</option>
											<option value="4" selected="selected">空单平仓</option>
											<option value="5" >多单爆仓</option>
											<option value="6" >空单爆仓</option>
										</c:if>
										<c:if test="${tradeType eq 5 }">
											<option value="1" >多单开仓</option>
											<option value="2" >多单平仓</option>
											<option value="3" >空单开仓</option>
											<option value="4" >空单平仓</option>
											<option value="5" selected="selected">多单爆仓</option>
											<option value="6" >空单爆仓</option>
										</c:if>
										<c:if test="${tradeType eq 6 }">
											<option value="1" >多单开仓</option>
											<option value="2" >多单平仓</option>
											<option value="3" >空单开仓</option>
											<option value="4" >空单平仓</option>
											<option value="5" >多单爆仓</option>
											<option value="6" selected="selected">空单爆仓</option>
										</c:if>
									</select>
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId == 0 ? '' : userId}" id="uid"></input>
									邮箱：
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
						<br>
						<div class="notification success png_bg">
							<div>
								数量：
								<span style="color: #57a000;">${vol eq null ? 0 : vol}</span>
								&nbsp;&nbsp;均价：
								<span style="color: #57a000;">${avgPrice eq null ? 0 : avgPrice}</span>
								&nbsp;&nbsp;盈亏额：
								<span style="color: #57a000;">${income eq null ? 0 : income }</span>
							</div>
							<br>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										成交时间
									</th>
									<th>
										合约号
									</th>
									<th>
										成交类型
									</th>
									<th>
										持仓均价(USDT)
									</th>
									<th>
										<c:choose>
											<c:when test="${issz}">
												成交点位
											</c:when>
											<c:otherwise>
												成交均价(USDT)
											</c:otherwise>
										</c:choose>
									</th>
									<th>
										成交数量(个)
									</th>
									<th>
										保证金(USDT)
									</th>
									<th>
										盈亏额(USDT)
									</th>
									<th>
										佣金(USDT)
									</th>
									<th>
										状态
									</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${pm.datas}" var="tl" varStatus="status">
									<tr>
										<td>
											<ut:formatDate dateline="${tl.trade_time}"></ut:formatDate>
										</td>
										<td>
											${tl.transaction_code.toUpperCase() }
										</td>
										<td>
											<c:choose>
												<c:when test="${tl.trade_type_id eq 1}">
													<b style="color: #5a9d3d;">多单开仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 2}">
													<b style="color: #5a9d3d;">多单平仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 3}">
													<b style="color: #c55555;">空单开仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 4}">
													<b style="color: #c55555;">空单平仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 5}">
													<b style="color: orange;">多单爆仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 6}">
													<b style="color: orange;">空单爆仓</b>
												</c:when>
												<c:otherwise>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											${tl.hold_price }
												
										</td>
										<td>
											${tl.trade_average_price}
										</td>
										<td>
											${tl.trade_count}
										</td>
										<td>
											${tl.trade_deposit}
										</td>
										<td <c:if test="${tl.trade_income lt 0}"> style="color: #c55555;"</c:if>
											<c:if test="${tl.trade_income gt 0}"> style="color: #5a9d3d;"</c:if>>
											<c:choose>
												<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
												</c:when>
												<c:otherwise>
													${tl.trade_income }
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											${tl.trade_fee}
										</td>
										<td>
											完成
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerQuantRisk/quantTradeDetail.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="code" value="${code}"/>
											<pg:param name="userId" value="${userId}"/>
											<pg:param name="tradeType" value="${tradeType}"/>
											<pg:param name="startTimeStr" value="${startTime}"/>
											<pg:param name="endTimeStr" value="${endTime}"/>
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
							senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						</script>
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
			//$("#startTimeStr").val(str + " 00:00:00");
		}
		
		if ("${endTime }" == "") {
			//$("#endTimeStr").val(tomStr + " 00:00:00");
		}
	});
</script>
</html>
