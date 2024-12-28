<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="msg.pg.userFindPwd1.title"/> | <fmt:message key="msg.pg.systemName" /></title>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
<script src="<%=basePath%>common/js/jquery.1.7.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<jsp:include page="_common.jsp"></jsp:include>
</head>
<body>
	<div class="add-windows" id="alert-windows"></div>

	<jsp:include page="_top.jsp"></jsp:include>
	<jsp:include page="_head.jsp"></jsp:include>
	<!-- 主体内容 -->
	<div class="container-bg-none">
		<div class="login-box">
			<c:if test="${error != null && error != ''}">
				<div class="warning" id="msg">${error}</div>
			</c:if>
			<span id="loginheader"></span>
			<form action="<%=basePath%>password/phoneChangePwddo.do" method="post"
				class="ui-form" id="loginform" name="loginform"
				onsubmit="return login();">
				<div class="form-title">
					<h2><fmt:message key="msg.pg.userCenter.phoneVaide"/></h2>
        		</div>
				<div class="form-group email-select">
					<div class="send-btn-box">
						<label for=""><fmt:message key="msg.pg.userBindPhone.phoneNumber"/>：${email}</label>
					</div>
					
					<div class="send-btn-box" style="position: relative;height: 46px;">
						<input style="width: 100%; padding-right: 120px; box-sizing: border-box; position: absolute; left: 0;" type="text" id="code" name="code" tabindex="1" value=""
							autocomplete="off" autofocus="autofocus" class="form-control inputElem" onfocus="removeMsg()"/>
		                	<div style="position: absolute;right: 0;top: 0;line-height:46px;padding: 0 10px; color: #4980E3; cursor: pointer;" id="sendTelCode"><fmt:message key="msg.pg.userCenter.availSMSCode"/></div>
	                	<div class="clear"></div>
					</div>
					
				</div>

				<div class="form-group-btn">
					<input type="submit" class="btn sub-btn sub-btn-click" id="" value="<fmt:message key="msg.pg.userCenter.confirmSubmit"/>"></input>
				</div>
			</form>
		</div>
	</div>
	<div class="clear"></div>
	<jsp:include page="_footer.jsp"></jsp:include>
</body>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		codeNull : "验证码不能为空！",
		login : "立即登录",
		warmTip : "温馨提示",
		sureTip : "确定",
		regain: "重新获取"
	};
	message["zh_HK"] = {
		codeNull : "驗證碼不能為空！",
		login : "立即登錄",
		warmTip : "溫馨提示",
		sureTip : "確定",
		regain: "重新獲取"
	};
	message["en_US"] = {
		codeNull : "The verifying code can not be empty !",
		login : "Log in",
		warmTip : "Tips",
		sureTip : "OK",
		regain: "Regain"
	};
	message["ja_JP"] = {
		codeNull : "認証コードを必ず入力してください！",
		login : "すぐにログイン",
		warmTip : "ヒント",
		sureTip : "OK",
		regain: "再取得"
	};
	message["ko_KR"] = {
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		login : "지금 로그인",
		warmTip : "팁",
		sureTip : "결정",
		regain: "다시 가져오기"
	};
	message["ru_RU"] = {
		codeNull : "Код подтверждения не может быть пустым !",
		login : "Войти",
		warmTip : "Подсказки",
		sureTip : "Ок",
		regain: "восстановить"
	};
	var MSG = message[locale];

	function login() {
		if ($("#code").val() == "") {
			seterror(-2);
			$("#code").focus();
			return false;
		}
		return true;
	}
	
	var secs = 60;
	var nowSec = 0;
	var canSend = true;
	var interval = null;
	
	function doUpdateEmail() {
		if (interval == null) return;
		
		if (nowSec == 60) {
			canSend = true;
			$("#sendEmailCode").html(MSG['regain']);
			$("#sendEmailCode").css("cursor", "pointer");
			
			clearInterval(interval);
			interval = null;
		} else {
			var wut = secs - nowSec;
			$("#sendEmailCode").html(" <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...");
			nowSec++;
		}
	}
	
	$("#sendEmailCode").click(function(){
		if (!canSend) return;
		canSend = false;
		$("#sendEmailCode").css("cursor", "auto");
		
		nowSec = 0;
		doUpdateEmail();
		interval = setInterval('doUpdateEmail()', 1000);
		
		$.post("<%=basePath%>password/sendEmailCode.do", {}, function(n){
			if(n == "success"){
			} else {
				tipAlert(n);
				
				canSend = true;
				$("#sendEmailCode").html(MSG['regain']);
				$("#sendEmailCode").css("cursor", "pointer");
				
				clearInterval(interval);
				interval = null;
			}
		}, "text");
	});

	function doUpdateTel() {
		if (interval == null) return;
		
		if (nowSec == 60) {
			canSend = true;
			$("#sendTelCode").html(MSG['regain']);
			$("#sendTelCode").css("cursor", "pointer");
			
			clearInterval(interval);
			interval = null;
		} else {
			var wut = secs - nowSec;
			$("#sendTelCode").html(" <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...");
			nowSec++;
		}
	}
	
	$("#sendTelCode").click(function(){
		if (!canSend) return;
		canSend = false;
		$("#sendTelCode").css("cursor", "auto");
		
		nowSec = 0;
		doUpdateTel();
		interval = setInterval('doUpdateTel()', 1000);
		
		$.post("<%=basePath%>password/sendTelCode.do", {}, function(n){
			if(n == "success"){
			} else {
				tipAlert(n);
				
				canSend = true;
				$("#sendTelCode").html(MSG['regain']);
				$("#sendTelCode").css("cursor", "pointer");
				
				clearInterval(interval);
				interval = null;
			}
		}, "text");
	});

	function seterror(tsy) {
		$(".warning").remove();
		var vhtml = '<div class="warning">';
		var vhtml2 = '';
		if (tsy == -2) {
			vhtml2 = MSG['codeNull'];
		}
		var vhtml3 = '</div>';
		document.getElementById("loginheader").innerHTML = vhtml + vhtml2 + vhtml3;
	}
	
	function removeMsg() {
		$(".warning").remove();
	}
	
	$(function() {
		if (${error == null || error == ''}) {
			canSend = false;
			$("#sendTelCode").css("cursor", "auto");
			
			nowSec = 0;
			interval = setInterval('doUpdateTel()', 1000);
			doUpdateTel();
		}
	});
</script>
</html>
