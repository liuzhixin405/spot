<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>等级购买设置</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
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
						等级购买设置
					</h3>
					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<form class="notification depth-form" action="managerLevelBuy/update.do" method="post">
								<input type="hidden" name="id" id="id" value="${setting.id}"></input>
								
								<label for="pay_coin_id">支付币种：</label>
								<select name="pay_coin_id" id=pay_coin_id >
									<c:forEach items="${coins}" var="l">
										<c:if test="${l.type_id eq setting.pay_coin_id}">
											<option value="${l.type_id}" selected="selected">${l.type_name}</option>
										</c:if>
										<c:if test="${l.type_id ne setting.pay_coin_id}">
											<option value="${l.type_id}">${l.type_name}</option>
										</c:if>
									</c:forEach>
								</select>
								<div class="clear"></div>
											
								<label for="exchange_rate">与Btcside汇率（1Btcside等于多少支付币种）：</label>
								<input name="exchange_rate" value="${setting.exchange_rate}"></input>
								<div class="clear"></div>
								
								<label for="back_price">回购指导价：</label>
								<input name="back_price" value="${setting.back_price}"></input>
								<div class="clear"></div>
								
								<label for="start_unlock_gap_day">多少天后开始释放：</label>
								<input name="start_unlock_gap_day" value="${setting.start_unlock_gap_day}"></input>
								<div class="clear"></div>
								
								<label for="unlock_times">分多少次释放完：</label>
								<input name="unlock_times" value="${setting.unlock_times}"></input>
								<div class="clear"></div>
								
								<label for="first_rebate">一级上级返佣比例：</label>
								<input name="first_rebate" value="${setting.first_rebate}"></input>
								<div class="clear"></div>
								
								<label for="second_rebate">二级上级返佣比例：</label>
								<input name="second_rebate" value="${setting.second_rebate}"></input>
								<div class="clear"></div>
								
								<button type="submit" class="button">提交</button>
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