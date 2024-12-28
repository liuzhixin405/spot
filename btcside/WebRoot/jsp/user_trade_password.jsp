<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userSetTradePwd.title"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath %>common/css/style.css?<%=rjs%>" />

		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js"
			type="text/javascript"></script>
			<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript"
			src="<%=basePath %>common/js/password.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>

	</head>

	<body>
		<div class="add-windows" id="alert-windows"></div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->

		<div class="container center-cont">

			<jsp:include page="_user_left.jsp"></jsp:include>

			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userSetTradePwd.title"/>
						</h2>
					</div>
					<div class="box">
						<div class="control">
							<form method="post" action="<%=basePath%>password/tradePwddo.do" class="form-wrap"  autocomplete="off"
							      id="mobileform"
								name="mobileform">
								<!--提示框,成功是class是success,失败时class是warning;-->
								<c:if test="${success!=null&&success!=''}">
								<div class="success">
									${success}
								</div>
							</c:if>
							<c:if test="${error!=null&&error!=''}">
								<div class="warning">
									${error}
								</div>
							</c:if>
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userSetTradePwd.tradePwd"/>
									</label>
									<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userSetTradePwd.inputTradePwd"/>" id="newpasswd1" autocomplete="new-password" 
										name="newpasswd1" onblur="checknewPwd();" />
									<span class="error" id="error2"></span>
									<span><fmt:message key="msg.pg.userChangePwd.note"/></span>

								</div>
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userChangeTradePwd.confirmNewPwd"/>
									</label>
									<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userChangeTradePwd.againInputNewPwd"/>" id="newpasswd2" autocomplete="new-password" 
										name="newpasswd2" onblur="checknewPwd2();" />
									<span class="error" id="error3"></span>
								</div>
								<div class="form-group" style="${showtel==1 ? '' : 'display:none'}">
									<label for="">
										<fmt:message key="msg.pg.userCenter.phoneVaidcode"/><input type="hidden" id="mobile" name="mobile" value="${tel}"/>
									</label>
									<input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userSetTradePwd.title"/>" onblur="checkCode();"
										id="code" name="code" />
									<span class="error" id="error1"></span>
									<div class="send-btn-box">
										<input type="button" class="btn send-btn" onclick="send(1)" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/>
									</div>

								</div>
								<div class="form-group" style="${showgoogle==1 ? '' : 'display:none' }">
									<label for="">
										<fmt:message key="msg.pg.userCenter.googleCode"/>
										<input type="hidden" name="secretKey" id="secretKey" value="${secretKey}" />
									</label>
									<input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entergoogleCode"/>"
										onblur="checkGoogle();" id="google" name="google" />
									<span class="error" id="googleerror"> </span>
								</div>
								<div class="form-group-btn">
									<button type="submit" class="btn sub-btn" onclick="return checkPwd();"><fmt:message key="msg.pg.userChange.confirmChange"/></button>
									<a href="<%=userPath %>accountSafe.do" class="btn sub-btn-return"><fmt:message key="msg.pg.trade.goBack"/></a>
								</div>
							</form>
						</div>

					</div>
				</div>

			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
		<script type="text/javascript">
			$(function(){
				$("#btnCode").click(function(){
					var tel = $("#mobile").val();
					$.post("<%=basePath%>center/sendOldTelCode.do",{Tel:tel}, function(n){
						if(n == "success"){
						//	alert("发送成功，请查收");
						} else {
							tipAlert("<fmt:message key='msg.pg.userBindPhone.sendFail'/>");
						}
					}, "text");
				});
			});
		
			var secs = 60;
			var wait = secs * 1000;
			var vtype = 1;
			function send(type) {
				var tel = document.forms.mobileform.mobile.value;
				vtype = type;
				if (tel != "") {
					for (i = 1; i <= secs; i++) {
						window.setTimeout("doUpdate(" + i + ")", i * 1000);
					}
					window.setTimeout("Timer()", wait);
				}
			}
			function doUpdate(num) {
				if (num == '60') {
						if(vtype == 1)
							document.forms.mobileform.btnCode.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
					} else {
						document.forms.mobileform.btnCode.disabled = true;
						wut = secs - num;
						if(vtype == 1){
							document.forms.mobileform.btnCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/>  (" + wut + ")...";
						}else{
							document.forms.mobileform.btnCode.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
						}
					}
				}
				function Timer() {
					document.forms.mobileform.btnCode.disabled = false;
				}
				</script>
	</body>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		codeNull : "验证码不能为空！",
		codeError : "请输入正确的验证码",
		pwdNull : "密码不能为空",
		pwdLtEightChar : "密码小于8个字符",
		pwdGtTwentyChar : "密码超过20个字符",
		twicePwdNotEq : "密码不一致",
		twicePwdEq : "密码一致",
		pwdAvailable : "密码可用",
		confirmPwdNull : "确认密码不能为空",
		confirmPwdAvailable : "确认密码可用",
		warmTip : "温馨提示",
		sureTip : "确定",
	};
	message["zh_HK"] = {
		codeNull : "驗證碼不能為空！",
		codeError : "請輸入正確的驗證碼",
		pwdNull : "密碼不能為空",
		pwdLtEightChar : "密碼小於8個字元",
		pwdGtTwentyChar : "密碼超過20個字元",
		twicePwdNotEq : "密碼不一致",
		twicePwdEq : "密碼一致",
		pwdAvailable : "密碼可用",
		confirmPwdNull : "確認密碼不能為空",
		confirmPwdAvailable : "確認密碼可用",
		warmTip : "溫馨提示",
		sureTip : "確定",
	};
	message["en_US"] = {
		codeNull : "Verification code cannot be empty",
		codeError : "Please enter correct verify code",
		pwdNull : "Password cannot be empty",
		pwdLtEightChar : "The password is less than 8 characters",
		pwdGtTwentyChar : "The Password is more than 20 characters",
		twicePwdNotEq : "Inconsistent passwords",
		twicePwdEq : "Consistent passwords",
		pwdAvailable : "Available Password",
		confirmPwdNull : "Confirm Password cannot be empty",
		confirmPwdAvailable : "Available Confirm Password",
		warmTip : "Warm prompt",
		sureTip : "Sure",
	};
	message["ja_JP"] = {
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
		pwdNull : "パスワードは空欄になれません",
		pwdLtEightChar : "パスワードが8文字未満です",
		pwdGtTwentyChar : "パスワードが20文字を超えています",
		twicePwdNotEq : "パスワードが一致しません",
		twicePwdEq : "パスワードが一致しています",
		pwdAvailable : "利用可能なパスワード",
		confirmPwdNull : "パスワードを空欄にすることはできません",
		confirmPwdAvailable : "このパスワードは利用可能です",
		warmTip: "ヒント",
		sureTip: "確定",
	};
	message["ko_KR"] = {
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "올바른 인증 코드를 입력하십시오",
		pwdNull : "비밀번호는 비워 둘 수 없습니다",
		pwdLtEightChar : "비밀번호가 8 자 미만입니다",
		pwdGtTwentyChar : "비밀번호가 20자를 초과합니다",
		twicePwdNotEq : "일치하지 않는 비밀번호",
		twicePwdEq : "일관된 비밀번호",
		pwdAvailable : "사용 가능한 비밀번호",
		confirmPwdNull : "비밀번호 확인은 비워 둘 수 없습니다",
		confirmPwdAvailable : "비밀번호가 사용 가능한지 확인",
		warmTip : "팁",
		sureTip : "결정"
	};
	message["ru_RU"] = {
		codeNull : "Код подтверждения не может быть пустым",
		codeError : "Пожалуйста, введите корректный код подтверждения",
		pwdNull : "Пароль не может быть пустым",
		pwdLtEightChar : " Пароль менее чем 8 символов ",
		pwdGtTwentyChar : "Пароль более чем 20 символов",
		twicePwdNotEq : "Пароли не соответствуют",
		twicePwdEq : "Пароли соответствуют",
		pwdAvailable : "Доступный пароль",
		confirmPwdNull : "Подтверждение пароля не может быть пустым",
		confirmPwdAvailable : "Доступное подтверждение пароля",
		warmTip : "подсказка",
		sureTip : "Конечно"
	};
	var MSG = message[locale];
</script>
</html>
