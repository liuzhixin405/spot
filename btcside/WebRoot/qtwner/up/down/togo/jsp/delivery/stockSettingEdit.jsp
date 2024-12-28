<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>股指交易时间编辑</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<style type="text/css">
			.clear {
				clear: both;
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
						股指交易时间编辑
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="delivery/managerStockSetting/update.do" method="post">
							<input type="hidden" name="id" value="${setting.id}"></input>
						
							<label for="transaction_id">合约号：</label>
							<input hidden="hidden" name="transaction_id" value="${setting.transaction_id}">
							<c:forEach items="${transList}" var="trans">
								<c:if test="${trans.transaction_id == setting.transaction_id}">
									<input readonly="readonly" value="${trans.transaction_code}">
								</c:if>
							</c:forEach>
							<div class="clear"></div>
							
							<label for="trade_time">设置交易时间说明</label>
							格式：00:00-04:30,07:00-14:15,15:30-24:00（多个时间段用","隔开）
							<br>
							请将时间段保持在一整天内，例如15:30-4:30，应设置为当天的15:30-24:00，第二天的00:00-04:30
							<div class="clear"></div>
							
							<label for="mon_state">周一是否交易：</label>
							<select name="mon_state" id="mon_state">
								<c:if test="${setting.mon_state}">
								<option value="true" selected="selected">是</option>
								<option value="false">否</option>
								</c:if>
								<c:if test="${!setting.mon_state}">
								<option value="true">是</option>
								<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							交易时间：<input name="mon_trade_time" value="${setting.mon_trade_time}"></input>
							<div class="clear"></div>
							<label for="tue_state">周二是否交易：</label>
							<select name="tue_state" id="tue_state">
								<c:if test="${setting.tue_state}">
								<option value="true" selected="selected">是</option>
								<option value="false">否</option>
								</c:if>
								<c:if test="${!setting.tue_state}">
								<option value="true">是</option>
								<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							交易时间：<input name="tue_trade_time" value="${setting.tue_trade_time}"></input>
							<div class="clear"></div>
							<label for="wed_state">周三是否交易：</label>
							<select name="wed_state" id="wed_state">
								<c:if test="${setting.wed_state}">
								<option value="true" selected="selected">是</option>
								<option value="false">否</option>
								</c:if>
								<c:if test="${!setting.wed_state}">
								<option value="true">是</option>
								<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							交易时间：<input name="wed_trade_time" value="${setting.wed_trade_time}"></input>
							<div class="clear"></div>
							<label for="thu_state">周四是否交易：</label>
							<select name="thu_state" id="thu_state">
								<c:if test="${setting.thu_state}">
								<option value="true" selected="selected">是</option>
								<option value="false">否</option>
								</c:if>
								<c:if test="${!setting.thu_state}">
								<option value="true">是</option>
								<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							交易时间：<input name="thu_trade_time" value="${setting.thu_trade_time}"></input>
							<div class="clear"></div>
							<label for="fri_state">周五是否交易：</label>
							<select name="fri_state" id="fri_state">
								<c:if test="${setting.fri_state}">
								<option value="true" selected="selected">是</option>
								<option value="false">否</option>
								</c:if>
								<c:if test="${!setting.fri_state}">
								<option value="true">是</option>
								<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							交易时间：<input name="fri_trade_time" value="${setting.fri_trade_time}"></input>
							<div class="clear"></div>
							<label for="tue_state">周六是否交易：</label>
							<select name="sat_state" id="sat_state">
								<c:if test="${setting.sat_state}">
								<option value="true" selected="selected">是</option>
								<option value="false">否</option>
								</c:if>
								<c:if test="${!setting.sat_state}">
								<option value="true">是</option>
								<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							交易时间：<input name="sat_trade_time" value="${setting.sat_trade_time}"></input>
							<div class="clear"></div>
							<label for="tue_state">周日是否交易：</label>
							<select name="sun_state" id="sun_state">
								<c:if test="${setting.sun_state}">
								<option value="true" selected="selected">是</option>
								<option value="false">否</option>
								</c:if>
								<c:if test="${!setting.sun_state}">
								<option value="true">是</option>
								<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							交易时间：<input name="sun_trade_time" value="${setting.sun_trade_time}"></input>
							<div class="clear"></div>
							
							<label for="holiday_url">假期判断URL：</label>
							<input name="holiday_url" value="${setting.holiday_url}"></input>
							<div class="clear"></div>
							
							<label for="holiday_url_using">假期判断开启状态：</label>
							<select name="holiday_url_using" id="holiday_url_using">
								<c:if test="${setting.holiday_url_using}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.holiday_url_using}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<button type="submit" class="button">提交</button>
						</form>
					</div>
				</div>
				
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
	</script>
</html>