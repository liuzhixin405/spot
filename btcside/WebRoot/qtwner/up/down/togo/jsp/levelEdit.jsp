<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE HTML>
<html>
<head>
	<base href="<%=actionPath%>">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>积分规则</title>
	<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
	<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	<script type="text/javascript">
		function postsubmit(){
			return true;
		}
	</script>
</head>

<body>
	<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						等级编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<form action="<%=actionPath%>managerUserLevelRule/doUpdateLevel.do" method="post" onsubmit="return postsubmit();">
									<input type="hidden" name="level_id" id="level_id" class="text-input " value="${level.level_id }">
									<label>VIP等级：</label>
									<input type="text" name="level_name" id="level_name" class="text-input " value="${level.level_name}">
									<label>条件：</label>
									<input type="text" name="level_credits" id="level_credits" class="text-input " value="${level.level_credits}">
									<label>吃单：</label>
									<input type="text" name="eat_commission_rate" id="eat_commission_rate" class="text-input " value="${level.eat_commission_rate}">
									<label>挂单：</label>
									<input type="text" name="pending_commission_rate" id="pending_commission_rate" class="text-input " value="${level.pending_commission_rate}">
									<label>提现：</label>
									<input type="text" name="withdraws_cash" id="withdraws_cash" class="text-input " value="${level.withdraws_cash}">
									<label>经纪返佣比例：</label>
									<input type="text" name="broker_commission_rate" id="broker_commission_rate" class="text-input " value="${level.broker_commission_rate}">
									<label>合约交易佣金比例：</label>
									<input type="text" name="transaction_commission_rate" id="transaction_commission_rate" class="text-input " value="${level.transaction_commission_rate }">
									<label>购买VIP金额(Btcside)：</label>
									<input type="text" name="level_buy_amount" id="level_buy_amount" class="text-input " value="${level.level_buy_amount.stripTrailingZeros().toPlainString() }">
									<label>是否隐藏(1：隐藏，0:使用)：</label>
									<input type="text" name="state" id="state" class="text-input " value="${level.state }">
									<button type="submit" class="button">
										修改
									</button>
								</form>
							</div>
						</div>
						
						<script language="javascript" type="text/javascript">
						<!--
						//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
						senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						--></script>
					</div>
				</div>
			</div>
		</div>

</body>
</html>