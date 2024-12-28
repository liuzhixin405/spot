<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<title>Btcside抵扣手续费管理</title>
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
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						Btcside抵扣手续费管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<form class="notification depth-form" action="managerBfx/updateBfxFeeSetting.do" method="post">
								<input type="hidden" name="id" value="${setting.id}"></input>								
								
								<label for="vip_state">持Btcside享vip待遇：</label>
								<select name="vip_state" id="vip_state">
									<c:if test="${setting.vip_state != 0}">
									<option value="1" selected="selected">开启</option>
									<option value="0">关闭</option>
									</c:if>
									<c:if test="${setting.vip_state == 0}">
									<option value="1">开启</option>
									<option value="0" selected="selected">关闭</option>
									</c:if>
								</select>
								<div class="clear"></div>
								
								<label for="fee_state">Btcside抵扣手续费：</label>
								<select name="fee_state" id="fee_state">
									<c:if test="${setting.fee_state != 0}">
									<option value="1" selected="selected">开启</option>
									<option value="0">关闭</option>
									</c:if>
									<c:if test="${setting.fee_state == 0}">
									<option value="1">开启</option>
									<option value="0" selected="selected">关闭</option>
									</c:if>
								</select>
								<div class="clear"></div>
							
								<label for="coin_id">Btcside币种Id：</label>
								<input name="coin_id" value="${setting.coin_id}"></input>
								<div class="clear"></div>
								
								<label for="discount">Btcside抵扣手续费折扣：</label>
								<input name="discount" value="${setting.discount}"></input>
								<div class="clear"></div>
								
								<label>昨日Btcside均价：</label>
								<span>${setting.bfx_avg_price}</span>
								<div class="clear"></div>
								
								<input type="hidden" name="bfx_avg_price" value="${setting.bfx_avg_price}"></input>
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
</html>