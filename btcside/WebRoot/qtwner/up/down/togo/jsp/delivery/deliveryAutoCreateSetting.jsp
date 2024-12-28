<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>交割合约-自动创建日合约</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
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
						交割合约-自动创建日合约管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="delivery/managerTrans/updateCreateSetting.do" method="post">
							<label for="is_open">是否开启：</label>
							<select name="is_open" id="is_open">
								<c:if test="${setting.is_open}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.is_open}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<label for="delivery_type">交割类型：</label>
							<select name="delivery_type" id="delivery_type">
								<option value="d" selected="selected">日交割</option>
								<!-- <option value="w">周交割</option> -->
							</select>
							<div class="clear"></div>
							
							<label for="transaction_type_id">合约类型：</label>
							<select name="transaction_type_id" id="transaction_type_id">
								<c:forEach items="${transTypeList}" var="transType">
									<c:if test="${setting.transaction_type_id == transType.type_id}">
										<option value="${transType.type_id}" selected="selected">${transType.type_name}</option>
									</c:if>
									<c:if test="${setting.transaction_type_id != transType.type_id}">
										<option value="${transType.type_id}">${transType.type_name}</option>
									</c:if>
								</c:forEach>
							</select>
							<div class="clear"></div>
							
							<label for="depth_uid">杠杆：</label>
							<input type="text" name="multiple" class="text-input" value="${setting.multiple}"></input>
							<div class="clear"></div>
						
							<label for="depth_uid">杠杆状态：</label>
							<input type="text" name="multiple_state" class="text-input" value="${setting.multiple_state}"></input>
							<div class="clear"></div>
							
							<label for="depth_uid">交易系数：</label>
							<input type="text" name="trade_coefficient" id="trade_coefficient" class="text-input" value="${setting.trade_coefficient }"></input>
							<div class="clear"></div>
							
							<label for="depth_uid">交割时间：</label>
							<input type="text" name="end_time" id="end_time" class="text-input" value="${setting.end_time }" ></input>
							<span style="color: red">(说明：每日交割时间点,时分,eg:16:00)</span>
							<div class="clear"></div>
							
							<input type="hidden" name="id" value="${setting.id}"></input>
							
							<button type="submit" id="updateUid" class="button">提交</button>
						</form>
					</div>
					</div>
					<font color="red">${msg}</font>
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