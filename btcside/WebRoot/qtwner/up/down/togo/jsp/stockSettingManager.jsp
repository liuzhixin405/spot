<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>股指交易时间管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<style type="text/css">
			.clear {
				clear: both;
			}
			input {
				width: 400px;
			}
		</style>
		<script type="text/javascript">
			function sure(qr, url) {
				if (qr != null) {
					if (confirm(qr)) {
						document.location.href = url;
					}
				} else {
					document.location.href = url;
				}
		
			}
		</script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						股指交易时间管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerStockSetting/add.do" method="post">
							<input name="id" type="hidden" value="${setting.id}"></input>
							<label for="transaction_id">合约号：</label>
							<select name="transaction_id" id="transaction_id" onchange="changeTrans(this)">
								<c:forEach items="${transList}" var="trans">
									<c:if test="${setting.transaction_id == trans.transaction_id}">
										<option value="${trans.transaction_id}" selected="selected">${trans.transaction_code}</option>
									</c:if>
									<c:if test="${setting.transaction_id != trans.transaction_id}">
										<option value="${trans.transaction_id}">${trans.transaction_code}</option>
									</c:if>
								</c:forEach>
							</select>
							<div class="clear"></div>
							
							<label for="trade_time">设置交易时间说明</label>
							格式：00:00-04:30,07:00-14:15,15:30-24:00（多个时间段用","隔开）
							<br>
							请将时间段保持在一整天内，例如15:30-4:30，应设置为当天的15:30-24:00，第二天的00:00-04:30
							<div class="clear"></div>
							
							<label for="mon_state">周一是否交易：</label>
							<select name="mon_state" id="mon_state">
								<option value="true">是</option>
								<option <c:if test='${setting.mon_state != null && setting.mon_state == false }'>selected='selected'</c:if> value="false">否</option>
							</select>
							交易时间：<input name="mon_trade_time" value="${setting.mon_trade_time}"></input>
							<div class="clear"></div>
							<label for="tue_state">周二是否交易：</label>
							<select name="tue_state" id="tue_state">
								<option value="true">是</option>
								<option <c:if test='${setting.tue_state != null && setting.tue_state == false }'>selected='selected'</c:if> value="false">否</option>
							</select>
							交易时间：<input name="tue_trade_time" value="${setting.tue_trade_time}"></input>
							<div class="clear"></div>
							<label for="wed_state">周三是否交易：</label>
							<select name="wed_state" id="wed_state">
								<option value="true">是</option>
								<option <c:if test='${setting.wed_state != null && setting.wed_state == false }'>selected='selected'</c:if> value="false">否</option>
							</select>
							交易时间：<input name="wed_trade_time" value="${setting.wed_trade_time}"></input>
							<div class="clear"></div>
							<label for="thu_state">周四是否交易：</label>
							<select name="thu_state" id="thu_state">
								<option value="true">是</option>
								<option <c:if test='${setting.thu_state != null && setting.thu_state == false }'>selected='selected'</c:if> value="false">否</option>
							</select>
							交易时间：<input name="thu_trade_time" value="${setting.thu_trade_time}"></input>
							<div class="clear"></div>
							<label for="fri_state">周五是否交易：</label>
							<select name="fri_state" id="fri_state">
								<option value="true">是</option>
								<option <c:if test='${setting.fri_state != null && setting.fri_state == false }'>selected='selected'</c:if> value="false">否</option>
							</select>
							交易时间：<input name="fri_trade_time" value="${setting.fri_trade_time}"></input>
							<div class="clear"></div>
							<label for="tue_state">周六是否交易：</label>
							<select name="sat_state" id="sat_state">
								<option value="true">是</option>
								<option <c:if test='${setting.sat_state != null && setting.sat_state == false }'>selected='selected'</c:if> value="false">否</option>
							</select>
							交易时间：<input name="sat_trade_time" value="${setting.sat_trade_time}"></input>
							<div class="clear"></div>
							<label for="tue_state">周日是否交易：</label>
							<select name="sun_state" id="sun_state">
								<option value="true">是</option>
								<option <c:if test='${setting.sun_state != null && setting.sun_state == false }'>selected='selected'</c:if> value="false">否</option>
							</select>
							交易时间：<input name="sun_trade_time" value="${setting.sun_trade_time}"></input>
							<div class="clear"></div>
							
							<label for="holiday_url">假期判断URL：</label>
							<input name="holiday_url" value="${setting.holiday_url}"></input>
							<div class="clear"></div>
							
							<label for="holiday_url_using">假期判断开启状态：</label>
							<select name="holiday_url_using" id="holiday_url_using">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
							</select>
							<div class="clear"></div>
							
							<button type="submit" class="button">提交</button>
						</form>
					</div>
				</div>
				
				<table id="changecolor">
					<thead>
						<tr>
							<th>
								合约号
							</th>
							<th>
								周一是否交易
							</th>
							<th>
								周二是否交易
							</th>
							<th>
								周三是否交易
							</th>
							<th>
								周四是否交易
							</th>
							<th>
								周无是否交易
							</th>
							<th>
								周六是否交易
							</th>
							<th>
								周日是否交易
							</th>
							<th>
								假期判断url
							</th>
							<th>
								假期判断开启状态
							</th>
							<th>
								操作
							</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${pm.datas}" var="l" varStatus="status">
							<tr>
								<TD>
									<c:forEach items="${transList}" var="trans">
										<c:if test="${trans.transaction_id == l.transaction_id}">${trans.transaction_code}</c:if>
									</c:forEach>
								</TD>
								<TD>
									<c:choose>
										<c:when test="${l.mon_state}">是</c:when>
										<c:otherwise>否</c:otherwise>
									</c:choose>
									<br />
									${l.mon_trade_time}
								</TD>
								<TD>
									<c:choose>
										<c:when test="${l.tue_state}">是</c:when>
										<c:otherwise>否</c:otherwise>
									</c:choose>
									<br />
									${l.tue_trade_time}
								</TD>
								<TD>
									<c:choose>
										<c:when test="${l.wed_state}">是</c:when>
										<c:otherwise>否</c:otherwise>
									</c:choose>
									<br />
									${l.wed_trade_time}
								</TD>
								<TD>
									<c:choose>
										<c:when test="${l.thu_state}">是</c:when>
										<c:otherwise>否</c:otherwise>
									</c:choose>
									<br />
									${l.thu_trade_time}
								</TD>
								<TD>
									<c:choose>
										<c:when test="${l.fri_state}">是</c:when>
										<c:otherwise>否</c:otherwise>
									</c:choose>
									<br />
									${l.fri_trade_time}
								</TD>
								<TD>
									<c:choose>
										<c:when test="${l.sat_state}">是</c:when>
										<c:otherwise>否</c:otherwise>
									</c:choose>
									<br />
									${l.sat_trade_time}
								</TD>
								<TD>
									<c:choose>
										<c:when test="${l.sun_state}">是</c:when>
										<c:otherwise>否</c:otherwise>
									</c:choose>
									<br />
									${l.sun_trade_time}
								</TD>
								<TD>
									${l.holiday_url}
								</TD>
								<TD>
									<c:choose>
										<c:when test="${l.holiday_url_using}">开启</c:when>
										<c:otherwise>关闭</c:otherwise>
									</c:choose>
								</TD>
								<TD style="width: 80px">
									<a class="link" href="javascript:sure('删除该设置？','<%=actionPath %>managerStockSetting/delete.do?id=${l.id}')" >删除</a>
									<a href="<%=actionPath %>managerStockSetting/load.do?transaction_id=${l.transaction_id}">编辑</a>
								</TD>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="11">
								<div class="pagination">
									<!-- ********** 分页开始 **********	-->
									<pg:pager url="managerStockSetting/load.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">

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
								<div class="clear"></div>
							</td>
						</tr>
					</tfoot>
				</table>
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
		var basePath = '<%=actionPath%>';
		function changeTrans(obj) {
			window.location.href= basePath + "managerStockSetting/load.do?transaction_id=" + obj.value;
		}
	</script>
</html>