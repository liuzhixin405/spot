<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>币币交易刷单管理</title>
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
						币币交易刷单管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
							<c:forEach items="${transList}" var="l" varStatus="status">
								<a href="<%=actionPath%>managerSpotArbitrage/scalpingManager.do?code=${l.coin_pair_name }">
									<font color="${current.coin_pair_name == l.coin_pair_name ? '#ff0000' : ''}">${l.coin_pair_name.toUpperCase() }</font>
								</a> &nbsp;
							</c:forEach>
							<div class="clear"></div>
							<form class="notification depth-form" action="managerSpotArbitrage/updateScalpingSetting.do" method="post">
								<input type="hidden" name="id" value="${setting.id}"></input>
								<input type="hidden" name="f_code" value="${setting.f_code}"></input>
								<input type="hidden" name="running" value="${setting.running}"></input>
								<input type="hidden" name="depth_uid" value="${setting.depth_uid}"></input>
								<input type="hidden" name="depth_buy_range" value="${setting.depth_buy_range}"></input>
								<input type="hidden" name="depth_sell_range" value="${setting.depth_sell_range}"></input>
								<input type="hidden" name="depth_amount_min" value="${setting.depth_amount_min}"></input>
								<input type="hidden" name="depth_amount_max" value="${setting.depth_amount_max}"></input>
								<input type="hidden" name="depth_url" value="${setting.depth_url}"></input>
								<input type="hidden" name="spare_depth_url" value="${setting.spare_depth_url}"></input>
								<input type="hidden" name="hedging" value="${setting.hedging}"></input>
								<input type="hidden" name="hedging_buy_range" value="${setting.hedging_buy_range}"></input>
								<input type="hidden" name="hedging_sell_range" value="${setting.hedging_sell_range}"></input>
								<input type="hidden" name="curr_use_state" value="${setting.curr_use_state}"></input>
								<input type="hidden" name="risk_manage" value="${setting.risk_manage}"></input>
								
								<input type="hidden" name="depth_self" value="${setting.depth_self}"></input>
								<input type="hidden" name="depth_buy_price_gap" value="${setting.depth_buy_price_gap}"></input>
								<input type="hidden" name="depth_sell_price_gap" value="${setting.depth_sell_price_gap}"></input>
								<input type="hidden" name="depth_buy_amount_min" value="${setting.depth_buy_amount_min}"></input>
								<input type="hidden" name="depth_sell_amount_min" value="${setting.depth_sell_amount_min}"></input>
								<input type="hidden" name="depth_buy_amount_max" value="${setting.depth_buy_amount_max}"></input>
								<input type="hidden" name="depth_sell_amount_max" value="${setting.depth_sell_amount_max}"></input>
								<input type="hidden" name="depth_buy_backhand_gap" value="${setting.depth_buy_backhand_gap}"></input>
								<input type="hidden" name="depth_sell_backhand_gap" value="${setting.depth_sell_backhand_gap}"></input>
								<input type="hidden" name="depth_buy_amount_other_min" value="${setting.depth_buy_amount_other_min}"></input>
								<input type="hidden" name="depth_sell_amount_other_min" value="${setting.depth_sell_amount_other_min}"></input>
								<input type="hidden" name="depth_buy_amount_other_max" value="${setting.depth_buy_amount_other_max}"></input>
								<input type="hidden" name="depth_sell_amount_other_max" value="${setting.depth_sell_amount_other_max}"></input>
								<input type="hidden" name="open_price" value="${setting.open_price}"></input>
								<input type="hidden" name="depth_sell" value="${setting.depth_self}"></input>
								<input type="hidden" name="depth_backhand" value="${setting.depth_backhand}"></input>
								<input type="hidden" name="depth_increasing" value="${setting.depth_increasing}"></input>
								<input type="hidden" name="depth_reset_open_price" value="${setting.depth_reset_open_price}"></input>
								<input type="hidden" name="can_trade_self" value="${setting.can_trade_self}"></input>
								<input type="hidden" name="price_digits" value="${setting.price_digits}"></input>
								<input type="hidden" name="amount_digits" value="${setting.amount_digits}"></input>
								<input type="hidden" name="depth_size" value="${setting.depth_size}"></input>
								
								<label for="scalping">刷单系统状态：</label>
								<select name="scalping" id="scalping">
									<c:if test="${setting.scalping}">
									<option value="true" selected="selected">开启</option>
									<option value="false">关闭</option>
									</c:if>
									<c:if test="${!setting.scalping}">
									<option value="true">开启</option>
									<option value="false" selected="selected">关闭</option>
									</c:if>
								</select>
								<div class="clear"></div>
							
								<label for="scalping_frequency">刷单评率（每分钟笔数）：</label>
								<input name="scalping_frequency_min" value="${setting.scalping_frequency_min}" style="width: 60px;"></input>
								~
								<input name="scalping_frequency_max" value="${setting.scalping_frequency_max}" style="width: 60px;"></input>
								<div class="clear"></div>
							
								<label for="scalping_min_each">每笔交易量：</label>
								<input name="scalping_min_each" value="${setting.scalping_min_each.doubleValue()}" style="width: 60px;"></input>
								~
								<input name="scalping_max_each" value="${setting.scalping_max_each.doubleValue()}" style="width: 60px;"></input>
								<div class="clear"></div>
								
								<label for="scalping_index_range">指数范围内（幅度，如0.02）：</label>
								<input name="scalping_index_range" value="${setting.scalping_index_range.doubleValue()}"></input>
								<div class="clear"></div>
								
								<label for="scalping_depth_range">盘口宽度过滤（卖一买一差价/买一，如0.01）：</label>
								<input name="scalping_depth_range" value="${setting.scalping_depth_range.doubleValue()}"></input>
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