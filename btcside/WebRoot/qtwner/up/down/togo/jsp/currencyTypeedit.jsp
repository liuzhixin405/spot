<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>资产币种编辑</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
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
						资产币种编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1" style="line-height: 24px;">
						<form class="notification depth-form"  action="<%=actionPath%>managerCurrencyType/update.do" method="post" onsubmit="return postsubmit();">
							<input type="hidden" name="type_id" id="type_id" value="${currencyType.type_id}"></input>
							<label>币种名称:</label>
							<input type="text" name="type_name" id="type_name"  value="${currencyType.type_name}" required="required"></input>
							<div class="clear"></div>
							
							<label>中文描述：</label>
							<input type="text" name="description_zh" id="description_zh" value="${currencyType.description_zh}"></input>
							<span style="color: red">若中文描述为空时，则前台用户资产列表不显示描述</span>
							<div class="clear"></div>
							
							<label>英文描述：</label>
							<input type="text" name="description_en" id="description_en" value="${currencyType.description_en}"></input>
							<div class="clear"></div>
							
							<label>日文描述：</label>
							<input type="text" name="description_jp" id="description_jp" value="${currencyType.description_jp}"></input>
							<div class="clear"></div>
							
							<label>韩文描述：</label>
							<input type="text" name="description_kr" id="description_kr" value="${currencyType.description_kr}"></input>
							<div class="clear"></div>
							
							<label>繁体描述：</label>
							<input type="text" name="description_hk" id="description_hk" value="${currencyType.description_hk}"></input>
							<div class="clear"></div>
							
							<label>是否使用：</label>
							<select name="state" id="state">
								<c:if test="${currencyType.state == 1}">
									<option value="1" selected="selected">使用中</option>
									<option value="0">不使用</option>
								</c:if>
								<c:if test="${currencyType.state != 1}">
									<option value="1">使用中</option>
									<option value="0" selected="selected">不使用</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<label>充值手续费：</label>
							<input type="text" name="recharge_fee" id="recharge_fee" value="${currencyType.recharge_fee.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>站内充值手续费：</label>
							<input type="text" name="inside_recharge_fee" id="inside_recharge_fee" value="${currencyType.inside_recharge_fee.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>充值手续费(USDT-ERC20)：</label>
							<input type="text" name="recharge_fee_erc20" id="recharge_fee" value="${currencyType.recharge_fee_erc20.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>充值手续费(USDT-TRC20)：</label>
							<input type="text" name="recharge_fee_trc20" id="recharge_fee" value="${currencyType.recharge_fee_trc20.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>提币手续费：</label>
							<input type="text" name="withdraw_fee" id="withdraw_fee" value="${currencyType.withdraw_fee.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>站内提币手续费：</label>
							<input type="text" name="inside_withdraw_fee" id="inside_withdraw_fee" value="${currencyType.inside_withdraw_fee.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>提币手续费(USDT-ERC20)：</label>
							<input type="text" name="withdraw_fee_erc20" id="recharge_fee" value="${currencyType.withdraw_fee_erc20.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>提币手续费(USDT-TRC20)：</label>
							<input type="text" name="withdraw_fee_trc20" id="recharge_fee" value="${currencyType.withdraw_fee_trc20.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>单笔最小提币额：</label>
							<input type="text" name="min_withdraw" id="min_withdraw" value="${currencyType.min_withdraw.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>单笔最大提币额：</label>
							<input type="text" name="max_widthdraw" id="max_widthdraw" value="${currencyType.max_widthdraw.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>单日累计额度转人工：</label>
							<input type="text" name="manul_max_widthdraw" id="manul_max_widthdraw" value="${currencyType.manul_max_widthdraw.doubleValue()}"></input>
							<span style="color: red">说明：单日累计达到该额度需人工，设置额度需大于0，且小于每日最大提币额</span>
							<div class="clear"></div>
							
							<label>每日最大提币额：</label>
							<input type="text" name="day_max_widthdraw" id="day_max_widthdraw" value="${currencyType.day_max_widthdraw.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label>是否是ERC20：</label>
							<select name="erc20" id="erc20">
								<c:if test="${currencyType.erc20}">
									<option value="true" selected="selected">是</option>
									<option value="false">否</option>
								</c:if>
								<c:if test="${!currencyType.erc20}">
									<option value="true">是</option>
									<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<label>是否是TRC20：</label>
							<select name="trc20" id="trc20">
								<c:if test="${currencyType.trc20}">
									<option value="true" selected="selected">是</option>
									<option value="false">否</option>
								</c:if>
								<c:if test="${!currencyType.trc20}">
									<option value="true">是</option>
									<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							<div class="clear"></div>

							<label>是否开启充值：</label>
							<select name="enable_recharge" id="enable_recharge">
								<c:if test="${currencyType.enable_recharge}">
									<option value="true" selected="selected">是</option>
									<option value="false">否</option>
								</c:if>
								<c:if test="${!currencyType.enable_recharge}">
									<option value="true">是</option>
									<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							<div class="clear"></div>

							<label>是否开启提现：</label>
							<select name="enable_withdraw" id="enable_withdraw">
								<c:if test="${currencyType.enable_withdraw}">
									<option value="true" selected="selected">是</option>
									<option value="false">否</option>
								</c:if>
								<c:if test="${!currencyType.enable_withdraw}">
									<option value="true">是</option>
									<option value="false" selected="selected">否</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<%-- <label>语言：</label>
							<input type="text" name="language" id="language" value="${currencyType.language}"></input>
							<div class="clear"></div> --%>
							
							<button type="submit" class="button">
								提交
							</button>
							<div class="clear"></div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
