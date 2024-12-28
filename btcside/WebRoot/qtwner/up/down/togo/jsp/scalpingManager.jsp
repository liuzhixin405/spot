<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>刷单管理</title>
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
						刷单管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
							<c:forEach items="${transList}" var="l" varStatus="status">
								<a href="<%=actionPath%>managerArbitrage/scalpingManager.do?code=${l.transaction_code }">
									<font color="${current.transaction_code == l.transaction_code ? '#ff0000' : ''}">${l.transaction_code.toUpperCase() }</font>
								</a> &nbsp;
							</c:forEach>
							<div class="clear"></div>
							<form class="notification depth-form" action="managerArbitrage/updateScalpingSetting.do" method="post">
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
								<input type="hidden" name="auto_trade" value="${setting.auto_trade}"></input>
								<input type="hidden" name="hedging" value="${setting.hedging}"></input>
								<input type="hidden" name="hedging_buy_range" value="${setting.hedging_buy_range}"></input>
								<input type="hidden" name="hedging_sell_range" value="${setting.hedging_sell_range}"></input>
								<input type="hidden" name="curr_use_state" value="${setting.curr_use_state}"></input>
								<input type="hidden" name="risk_manage" value="${setting.risk_manage}"></input>
								
								<input type="hidden" name="depth_price_gap" value="${setting.depth_price_gap}"></input>
								<input type="hidden" name="depth_amount_gap_min" value="${setting.depth_amount_gap_min}"></input>
								<input type="hidden" name="depth_amount_gap_max" value="${setting.depth_amount_gap_max}"></input>
								<input type="hidden" name="depth_index_range" value="${setting.depth_index_range}"></input>
								<input type="hidden" name="depth_size" value="${setting.depth_size}"></input>
								<input type="hidden" name="price_digits" value="${setting.price_digits}"></input>
								<input type="hidden" name="amount_digits" value="${setting.amount_digits}"></input>
								<input type="hidden" name="can_trade_self" value="${setting.can_trade_self}"></input>
								<input type="hidden" name="speed_type" value="${setting.speed_type}"></input>
								
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
						
						<div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">刷单统计:</h5>
							<div class="clear"></div>
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>managerArbitrage/scalpingManager.do" method="get">
										<input type="hidden" value="${current.transaction_code}" ></input>
										开始时间
										<input name="startTime" type="text" value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										&nbsp;&nbsp;&nbsp;&nbsp;结束时间
										<input name="endTime" type="text" value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查询</button>
									</form>
									<c:if test="${selectSum != null }">
										<p><b>查询结果</b>--刷单量：${selectSum.countAmount.doubleValue()} => 均价：${selectSum.averagePrice.doubleValue()}</p>
									</c:if>
									<div class="clear"></div>
								</div>
								
								<div class="clear"></div>
							</div>
						</div>
						<div>注：刷单详情请在【基本功能】-【交易信息】模块查看，用户ID为1！</div>
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