<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>资金费配置管理</title>
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
						资金费配置管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
						<div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
							<c:forEach items="${transList}" var="l" varStatus="status">
								<a href="<%=actionPath%>simulation/managerFundingFee/fundingFeeSettingManager.do?code=${l.transaction_code }">
									<font color="${current.transaction_code == l.transaction_code ? '#ff0000' : ''}">${l.transaction_code.toUpperCase() }</font>
								</a> &nbsp;
							</c:forEach>
							<div class="clear"></div>
							<form class="notification depth-form" action="simulation/managerFundingFee/updateFundingFeeSetting.do" method="post">
								<label for="running">资金费状态：</label>
								<select name="running" id="running">
									<c:if test="${setting.running}">
									<option value="true" selected="selected">开启</option>
									<option value="false">关闭</option>
									</c:if>
									<c:if test="${!setting.running}">
									<option value="true">开启</option>
									<option value="false" selected="selected">关闭</option>
									</c:if>
								</select>
								<div class="clear"></div>
							
								<label for="running_time">执行时间点（逗号分隔，如10:00,22:00)：</label>
								<input name="running_time" value="${setting.running_time}"></input>
								<div class="clear"></div>
							
								<label for="interest_rate_diff">利率差：</label>
								<input name="interest_rate_diff" value="${setting.interest_rate_diff.doubleValue()}"></input>
								<div class="clear"></div>
							
								<label for="divisor">除数：</label>
								<input name="divisor" value="${setting.divisor.doubleValue()}"></input>
								<div class="clear"></div>
							
								<label for="clamp_up">区间限定上边界（%）：</label>
								<input name="clamp_up" value="${setting.clamp_up.doubleValue()}"></input>
								<div class="clear"></div>
								
								<label for="clamp_down">区间限定下边界（%）：</label>
								<input name="clamp_down" value="${setting.clamp_down.doubleValue()}"></input>
								<div class="clear"></div>
								
								<label for="fixed_fee_open">固定费率是否开启：</label>
								<select name="fixed_fee_open" id="fixed_fee_open">
									<c:if test="${setting.fixed_fee_open}">
									<option value="true" selected="selected">开启</option>
									<option value="false">关闭</option>
									</c:if>
									<c:if test="${!setting.fixed_fee_open}">
									<option value="true">开启</option>
									<option value="false" selected="selected">关闭</option>
									</c:if>
								</select>
								<div class="clear"></div>
								
								<label for="fixed_fee_rate">固定资金费率：</label>
								<input name="fixed_fee_rate" value="${setting.fixed_fee_rate}"></input>
								<div class="clear"></div>
								
								<input type="hidden" name="id" value="${setting.id}"></input>
								<input type="hidden" name="f_code" value="${setting.f_code}"></input>
								
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