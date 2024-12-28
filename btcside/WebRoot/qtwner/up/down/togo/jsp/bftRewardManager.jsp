<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>BFC奖励赠送设置</title>
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
						BFC奖励赠送设置
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerBftReward/updateBftRewardSetting.do" method="post">
							<input type="hidden" name="id" id="id" value="${setting.id}"></input>
							<input type="hidden" name="bfx_rewarded" id="bfx_rewarded" value="${setting.bfx_rewarded}"></input>
							
							<label for="currency_type_id">BFC币种ID：</label>
							<input name="currency_type_id" value="${setting.currency_type_id}"></input>
							<div class="clear"></div>
						
							<label for="login_reward">登录奖励：</label>
							<input name="login_reward" value="${setting.login_reward.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="register_reward">注册奖励：</label>
							<input name="register_reward" value="${setting.register_reward.doubleValue()}"></input>
							<div class="clear"></div>
						
							<label for="invite_reward">邀请奖励：</label>
							<input name="invite_reward" value="${setting.invite_reward.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="running">Btcside持有赠送：</label>
							<c:if test="${setting.bfx_rewarded}">
							<span>已赠送</span>
							</c:if>
							<c:if test="${!setting.bfx_rewarded}">
							<span>未赠送</span>
							 <a onClick="bfxReward()" style="background: red !important; border: 0 !important; text-align: center; height: 20px; line-height: 20px;" class="button">赠送</a>
							</c:if>
							<div class="clear"></div>
							
							<label for="old_register_rewarded">老用户赠送：</label>
							<c:if test="${setting.old_register_rewarded}">
							<span>已赠送</span>
							</c:if>
							<c:if test="${!setting.old_register_rewarded}">
							<span>未赠送</span>
							 <a onClick="oldRegisterRewarded()" style="background: red !important; border: 0 !important; text-align: center; height: 20px; line-height: 20px;" class="button">赠送</a>
							</c:if>
							<div class="clear"></div>
							
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
	
	function bfxReward() {
		if (confirm("确定赠送？")) {
			$.ajax({
		        type: "POST",
		        dataType: "json",         
		        url: "<%=actionPath%>managerBftReward/bfxReward.do",
		        data: "",
		        success: function(data) {
		        	window.location.reload();
		        },
		        error : function(XMLHttpRequest, textStatus, errorThrown) {
		        	console.log(textStatus);
		        }
			});
		}
	}
	
	function oldRegisterRewarded() {
	if (confirm("确定赠送？")) {
			$.ajax({
		        type: "POST",
		        dataType: "json",         
		        url: "<%=actionPath%>managerBftReward/oldRegisterReward.do",
		        data: "",
		        success: function(data) {
		        	window.location.reload();
		        },
		        error : function(XMLHttpRequest, textStatus, errorThrown) {
		        	console.log(textStatus);
		        }
			});
		}
	}
	</script>
</html>