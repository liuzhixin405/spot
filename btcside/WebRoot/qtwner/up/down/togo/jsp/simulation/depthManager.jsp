<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>模拟交易-深度管理</title>
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
						模拟交易-深度管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
						<c:forEach items="${transList}" var="l" varStatus="status">
							<a href="<%=actionPath%>simulation/managerArbitrage/depthManager.do?code=${l.transaction_code }">
								<font color="${current.transaction_code == l.transaction_code ? '#ff0000' : ''}">${l.transaction_code.toUpperCase() }</font>
							</a> &nbsp;
						</c:forEach>
						<div class="clear"></div>
						<form class="notification depth-form" action="simulation/managerArbitrage/updateDepthSetting.do" method="post">
							<label for="running">深度系统状态：</label>
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
						
							<label for="depth_uid">深度用户ID：</label>
							<input name="depth_uid" value="${setting.depth_uid}"></input>
							<div class="clear"></div>
						
							<label for="price_digits">价格精度：</label>
							<input name="price_digits" value="${setting.price_digits}"></input>
							<div class="clear"></div>
							
							<label for="amount_digits">数量精度：</label>
							<input name="amount_digits" value="${setting.amount_digits}"></input>
							<div class="clear"></div>
							
							<label for="depth_index_range">指数偏离（小数如，0.005）：</label>
							<input name="depth_index_range" value="<fmt:formatNumber pattern="#0.########" value='${setting.depth_index_range}'/>"></input>
							<div class="clear"></div>

							<label for="depth_buy_range">买一偏离指数(小数如：0.005)：</label>
							<input name="depth_buy_range" value="<fmt:formatNumber pattern="#0.########" value='${setting.depth_buy_range}'/>"></input>
							<div class="clear"></div>
						
							<label for="depth_sell_range">卖一偏离指数(小数如：0.005)：</label>
							<input name="depth_sell_range" value="<fmt:formatNumber pattern="#0.########" value='${setting.depth_sell_range}'/>"></input>
							<div class="clear"></div>
							
							<label for="depth_price_gap">价格增量：</label>
							<input name="depth_price_gap" value="<fmt:formatNumber pattern="#0.########" value='${setting.depth_price_gap}'/>"></input>
							<div class="clear"></div>
						
							<label for="depth_amount_min">买一卖一最小委托量：</label>
							<input name="depth_amount_min" value="${setting.depth_amount_min.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="depth_amount_max">买一卖一最大委托量：</label>
							<input name="depth_amount_max" value="${setting.depth_amount_max.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="depth_amount_gap_min">委托量最小增量：</label>
							<input name="depth_amount_gap_min" value="${setting.depth_amount_gap_min.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="depth_amount_gap_max">委托量最大增量：</label>
							<input name="depth_amount_gap_max" value="${setting.depth_amount_gap_max.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="depth_size">盘口挂单量：</label>
							<input name="depth_size" value="${setting.depth_size}"></input>
							<div class="clear"></div>
							
							<label for="can_trade_self">是否可以自成交：</label>
							<select name="can_trade_self" id="can_trade_self">
								<c:if test="${setting.can_trade_self}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.can_trade_self}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<input type="hidden" name="amount_mutil" value="${setting.amount_mutil}"></input>
							<input type="hidden" name="spare_depth_url" value="${setting.spare_depth_url}"></input>
							<input type="hidden" name="depth_url" value="${setting.depth_url}"></input>
							<input type="hidden" name="curr_use_state" value="${setting.curr_use_state}"></input>
							
							<input type="hidden" name="id" value="${setting.id}"></input>
							<input type="hidden" name="f_code" value="${setting.f_code}"></input>
							<input type="hidden" name="scalping" value="${setting.scalping}"></input>
							<input type="hidden" name="scalping_frequency_min" value="${setting.scalping_frequency_min}"></input>
							<input type="hidden" name="scalping_frequency_max" value="${setting.scalping_frequency_max}"></input>
							<input type="hidden" name="scalping_min_each" value="${setting.scalping_min_each}"></input>
							<input type="hidden" name="scalping_max_each" value="${setting.scalping_max_each}"></input>
							<input type="hidden" name="scalping_index_range" value="${setting.scalping_index_range}"></input>
							<input type="hidden" name="scalping_depth_range" value="${setting.scalping_depth_range}"></input>
							<input type="hidden" name="auto_trade" value="${setting.auto_trade}"></input>
							<input type="hidden" name="hedging" value="${setting.hedging}"></input>
							<input type="hidden" name="hedging_buy_range" value="${setting.hedging_buy_range}"></input>
							<input type="hidden" name="hedging_sell_range" value="${setting.hedging_sell_range}"></input>
							<input type="hidden" name="curr_use_state" value="${setting.curr_use_state}"></input>
							<input type="hidden" name="risk_manage" value="${setting.risk_manage}"></input>
							
							<button type="submit" id="updateUid" class="button">提交</button>
						</form>
					</div>
					<div>注：深度用户持单及委托情况，请在基本功能模块或登录用户前端系统查看！</div>
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