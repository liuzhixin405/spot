<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>币币交易深度管理</title>
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
						币币交易深度管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<h5 style="float:left; margin-right: 10px;">选择交易对:</h5>
						<c:forEach items="${transList}" var="l" varStatus="status">
							<a href="<%=actionPath%>managerSpotArbitrage/depthManager.do?code=${l.coin_pair_name }">
								<font color="${current.coin_pair_name == l.coin_pair_name ? '#ff0000' : ''}">${l.coin_pair_name.toUpperCase() }</font>
							</a> &nbsp;
						</c:forEach>
						<div class="clear"></div>
						<form class="notification depth-form" action="managerSpotArbitrage/updateDepthSetting.do" method="post">
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
							
							<label for="depth_self">是否引入外部深度：</label>
							<select name="depth_self" id="depth_self">
								<option value="true" ${setting.depth_self ? "selected='selected'" : ""}>否</option>
								<option value="false" ${setting.depth_self ?  "" : "selected='selected'"}>是</option>
							</select>
							<div class="clear"></div>
						
							<div>------- 引入外部深度 --------</div>
							<label for="depth_buy_range">外部深度买单调整幅度(%)：</label>
							<input name="depth_buy_range" value="${setting.depth_buy_range.doubleValue()}"></input>
							<div class="clear"></div>
						
							<label for="depth_sell_range">外部深度卖单调整幅度(%)：</label>
							<input name="depth_sell_range" value="${setting.depth_sell_range.doubleValue()}"></input>
							<div class="clear"></div>
						
							<label for="depth_amount_min">过滤最小外部委托量：</label>
							<input name="depth_amount_min" value="${setting.depth_amount_min.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="depth_amount_max">过滤最大外部委托量：</label>
							<input name="depth_amount_max" value="${setting.depth_amount_max.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="depth_url">外部深度获取路径：</label>
							<input name="depth_url" value="${setting.depth_url}" style="width: 300px;"></input>
							<div class="clear"></div>
							
							<label for="spare_depth_url">备用外部深度获取路径：</label>
							<input name="spare_depth_url" value="${setting.spare_depth_url}" style="width: 300px;"></input>
							<div class="clear"></div>
							
							<label for="depth_url">外部深度最后更新时间：</label>
							<div>${updateTime}</div>
							<div class="clear"></div>
							
							<label for="curr_use_state">当前使用外部深度市场：</label>
							<div><c:if test="${setting.curr_use_state == 0}">主深度市场</c:if>
							<c:if test="${setting.curr_use_state != 0}">备用深度市场</c:if></div>
							<div class="clear"></div>
							
							<div>------- 系统自挂单 --------</div>
							<label for="price_digits">价格精度（必填）：</label>
							<input name="price_digits" value="${setting.price_digits}"></input>
							<div class="clear"></div>
							
							<label for="amount_digits">数量精度（必填）：</label>
							<input name="amount_digits" value="${setting.amount_digits}"></input>
							<div class="clear"></div>
							
							<label for="depth_size">盘口挂单量：</label>
							<input name="depth_size" value="${setting.depth_size}"></input>
							<div class="clear"></div>
							
							<label for="depth_buy_price_gap">买盘价格增量：</label>
							<input name="depth_buy_price_gap" value="<fmt:formatNumber pattern="#0.########" value='${setting.depth_buy_price_gap.doubleValue()}'/>"></input>
							<div class="clear"></div>
							<label for="depth_sell_price_gap">卖盘价格增量：</label>
							<input name="depth_sell_price_gap" value="<fmt:formatNumber pattern="#0.########" value='${setting.depth_sell_price_gap.doubleValue()}'/>"></input>
							<div class="clear"></div>
							
							<label for="depth_buy_amount_min">买盘挂单最小数量：</label>
							<input name="depth_buy_amount_min" value="${setting.depth_buy_amount_min.doubleValue()}"></input>
							<div class="clear"></div>
							<label for="depth_buy_amount_max">买盘挂单最大数量：</label>
							<input name="depth_buy_amount_max" value="${setting.depth_buy_amount_max.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="depth_sell_amount_min">卖盘挂单最小数量：</label>
							<input name="depth_sell_amount_min" value="${setting.depth_sell_amount_min.doubleValue()}"></input>
							<div class="clear"></div>
							<label for="depth_sell_amount_max">卖盘挂单最大数量：</label>
							<input name="depth_sell_amount_max" value="${setting.depth_sell_amount_max.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="depth_buy_backhand_gap">买盘反手挂单差价：</label>
							<input name="depth_buy_backhand_gap" value="<fmt:formatNumber pattern="#0.########" value='${setting.depth_buy_backhand_gap.doubleValue()}'/>"></input>
							<div class="clear"></div>
							<label for="depth_sell_backhand_gap">卖盘反手挂单差价：</label>
							<input name="depth_sell_backhand_gap" value="<fmt:formatNumber pattern="#0.########" value='${setting.depth_sell_backhand_gap.doubleValue()}'/>"></input>
							<div class="clear"></div>
							<label for="depth_backhand">是否开启反手挂单：</label>
							<select name="depth_backhand" id="depth_backhand">
								<option value="true" ${setting.depth_backhand ? "selected='selected'" : ""}>是</option>
								<option value="false" ${setting.depth_backhand ?  "" : "selected='selected'"}>否</option>
							</select>
							<div class="clear"></div>
							
							<label for="depth_buy_amount_other_min">买盘挂单叠加最小数量：</label>
							<input name="depth_buy_amount_other_min" value="${setting.depth_buy_amount_other_min.doubleValue()}"></input>
							<div class="clear"></div>
							<label for="depth_buy_amount_other_max">买盘挂单叠加最大数量：</label>
							<input name="depth_buy_amount_other_max" value="${setting.depth_buy_amount_other_max.doubleValue()}"></input>
							<div class="clear"></div>
							<label for="depth_sell_amount_other_min">卖盘挂单叠加最小数量：</label>
							<input name="depth_sell_amount_other_min" value="${setting.depth_sell_amount_other_min.doubleValue()}"></input>
							<div class="clear"></div>
							<label for="depth_sell_amount_other_max">卖盘挂单叠加最大数量：</label>
							<input name="depth_sell_amount_other_max" value="${setting.depth_sell_amount_other_max.doubleValue()}"></input>
							<div class="clear"></div>
							<label for="depth_increasing">是否递增叠加：</label>
							<select name="depth_increasing" id="depth_increasing">
								<option value="true" ${setting.depth_increasing ? "selected='selected'" : ""}>是</option>
								<option value="false" ${setting.depth_increasing ?  "" : "selected='selected'"}>否</option>
							</select>
							<div class="clear"></div>
							
							<label for="open_price">开盘价格 ：</label>
							<input name="open_price" value="<fmt:formatNumber pattern="#0.########" value='${setting.open_price.doubleValue()}'/>"></input>
							<div class="clear"></div>
							<label for="depth_reset_open_price">是否重置开盘价：</label>
							<select name="depth_reset_open_price" id="depth_reset_open_price">
								<option value="true" ${setting.depth_reset_open_price ? "selected='selected'" : ""}>是</option>
								<option value="false" ${setting.depth_reset_open_price ?  "" : "selected='selected'"}>否</option>
							</select>
							<div class="clear"></div>
							
							<label for="depth_reset_open_price">是否可自成交：</label>
							<select name="can_trade_self" id="can_trade_self">
								<option value="true" ${setting.can_trade_self ? "selected='selected'" : ""}>是</option>
								<option value="false" ${setting.can_trade_self ?  "" : "selected='selected'"}>否</option>
							</select>
							<div class="clear"></div>
							
							<input type="hidden" name="id" value="${setting.id}"></input>
							<input type="hidden" name="f_code" value="${setting.f_code}"></input>
							<input type="hidden" name="scalping" value="${setting.scalping}"></input>
							<input type="hidden" name="scalping_frequency_min" value="${setting.scalping_frequency_min}"></input>
							<input type="hidden" name="scalping_frequency_max" value="${setting.scalping_frequency_max}"></input>
							<input type="hidden" name="scalping_min_each" value="${setting.scalping_min_each}"></input>
							<input type="hidden" name="scalping_max_each" value="${setting.scalping_max_each}"></input>
							<input type="hidden" name="scalping_index_range" value="${setting.scalping_index_range}"></input>
							<input type="hidden" name="scalping_depth_range" value="${setting.scalping_depth_range}"></input>
							<input type="hidden" name="hedging" value="${setting.hedging}"></input>
							<input type="hidden" name="hedging_buy_range" value="${setting.hedging_buy_range}"></input>
							<input type="hidden" name="hedging_sell_range" value="${setting.hedging_sell_range}"></input>
							<input type="hidden" name="curr_use_state" value="${setting.curr_use_state}"></input>
							<input type="hidden" name="risk_manage" value="${setting.risk_manage}"></input>
							
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