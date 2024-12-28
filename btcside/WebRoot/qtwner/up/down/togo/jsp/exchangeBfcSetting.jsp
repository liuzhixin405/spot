<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>兑换参数设置</title>
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
						Btcside与BFC互兑换设置
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<h5 style="float:left; margin-right: 10px;">选择币种:</h5>
						<c:forEach items="${settings}" var="l" varStatus="status">
							<a href="<%=actionPath%>managerExchangeBfc/exchangeSetting.do?id=${l.id }">
								<font color="${setting.id == l.id ? '#ff0000' : ''}">${l.symbol.toUpperCase() }</font>
							</a> &nbsp;
						</c:forEach>
						<div class="clear"></div>
						<form class="notification depth-form" action="managerExchangeBfc/updateSetting.do" method="post">
						
							<input type="hidden" name="id" value="${setting.id}"></input>
							<input type="hidden" name="currency_type_id" value="${setting.currency_type_id}"></input>
							
							<label for="symbol">币种：</label>
							<input name="symbol" value="${setting.symbol}" readonly="readonly"></input>
							<div class="clear"></div>
						
							<label for="min_amount">最小兑换数量：</label>
							<input name="min_amount" value="${setting.min_amount.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="running">状态：</label>
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
							
							<label for="exchange_rate">汇率：</label>
							<input name="exchange_rate" value="${setting.exchange_rate.doubleValue()}"></input>
							<div class="clear"></div>
							
							<button type="submit" id="submit" class="button">提交</button>
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