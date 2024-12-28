<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userChangePwd.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script>	
		<script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userChangePwd.title"/>
						</h2>
					</div>
					<div class="box">
						<div class="control">
							<div class="alert">
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
				            </div>
							
							<form method="post" action="<%=basePath%>password/changePwddo.do"
								onsubmit="return checkPasswd();" class="form-wrap" id="mobileform"  autocomplete="off"
								name="mobileform">
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userChangePwd.currentlyPwd"/><input type="hidden" name="email" id="email" value="${email}">
									</label>
									<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userBindGoogle.inputLoginPwd"/>" id="password"  autocomplete="new-password"
										name="password" onblur="checkPassword();" />
									<span class="error" id="passworderror"></span>
								</div>
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userChangePwd.newPwd"/>
									</label>
									<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userChangePwd.inputNewPwd"/>" id="newpasswd1"  autocomplete="new-password"
										name="newpasswd1" onblur="checknewPwd();" />
									<span class="error" id="error2"></span>
									<span><fmt:message key="msg.pg.userChangePwd.note"/></span>

								</div>
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userChangePwd.confirmNewPwd"/>
									</label>
									<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userChangePwd.againInputNewPwd"/>" id="newpasswd2"  autocomplete="new-password"
										name="newpasswd2" onblur="checknewPwd2();" />
									<span class="error" id="error3"></span>
								</div>
								<div class="form-group" style="${showtel==1 ? '' : 'display:none' }">
									<label for="">
										<fmt:message key="msg.pg.userCenter.phoneVaidcode"/><input type="hidden" name="mobile" id="mobile" value="${tel}"/>
									</label>
									<input type="text" class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entercellCode"/>" onblur="checkCode();" id="code" name="code" />
										<span class="error" id="error1"></span>
										<div class="send-btn-box">
										<input type="button" class="btn send-btn" onclick="send(1)" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/>
										</div>
								</div>
								<div class="form-group-btn">
									<button type="submit" class="btn sub-btn" style="width: 187px;height: 42px;"><fmt:message key="msg.pg.userChange.confirmChange"/></button>
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
						//	alert("<fmt:message key='msg.pg.userBindPhone.sendFail'/>");
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
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		codeNull : "验证码不能为空！",
		codeError : "请输入正确的验证码",
		pwdError : "请输入正确的密码",
		loginPwdNull : "登录密码不能为空",
		loginPwdLtEightChar : "登录密码小于8个字符",
		loginPwdGtTwentyChar : "登录密码超过20个字符",
		pwdNull : "密码不能为空",
		pwdLtEightChar : "密码小于8个字符",
		pwdGtTwentyChar : "密码超过20个字符",
		twicePwdNotEq : "密码不一致",
		twicePwdEq : "密码一致",
		pwdAvailable : "密码可用",
		confirmPwdNull : "确认密码不能为空",
		confirmPwdAvailable : "确认密码可用"
	};
	message["zh_HK"] = {
		codeNull : "驗證碼不能為空！",
		codeError : "請輸入正確的驗證碼",
		pwdError : "請輸入正確的密碼",
		loginPwdNull : "登錄密碼不能為空",
		loginPwdLtEightChar : "登錄密碼小於8個字元",
		loginPwdGtTwentyChar : "登錄密碼超過20個字元",
		pwdNull : "密碼不能為空",
		pwdLtEightChar : "密碼小於8個字元",
		pwdGtTwentyChar : "密碼超過20個字元",
		twicePwdNotEq : "密碼不一致",
		twicePwdEq : "密碼一致",
		pwdAvailable : "密碼可用",
		confirmPwdNull : "確認密碼不能為空",
		confirmPwdAvailable : "確認密碼可用"
	};
	message["en_US"] = {
		codeNull : "Verification code cannot be empty",
		codeError : "Please enter correct verify code",
		pwdError : "Please enter correct password",
		loginPwdNull : "Login password can not be empty",
		loginPwdLtEightChar : "The login password is less than 8 characters",
		loginPwdGtTwentyChar : "The login Password is more than 20 characters",
		pwdNull : "Password cannot be blank",
		pwdLtEightChar : "The password is less than 8 characters",
		pwdGtTwentyChar : "The Password is more than 20 characters",
		twicePwdNotEq : "Inconsistent passwords",
		twicePwdEq : "Consistent passwords",
		pwdAvailable : "Available Password",
		confirmPwdNull : "Confirm Password cannot be empty",
		confirmPwdAvailable : "Available Confirm Password"
	};
	message["ja_JP"] = {
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
		pwdError : "正しいパスワードを入力してください",
		loginPwdNull : "ログインパスワードは空欄になれません",
		loginPwdLtEightChar : "ログインパスワードが8文字以下です",
		loginPwdGtTwentyChar : "ログインパスワードが20文字以上です",
		pwdNull : "パスワードは空欄になれません",
		pwdLtEightChar : "パスワードが8文字未満です",
		pwdGtTwentyChar : "パスワードが20文字を超えています",
		twicePwdNotEq : "パスワードが一致しません",
		twicePwdEq : "パスワードが一致しています",
		pwdAvailable : "利用可能なパスワード",
		confirmPwdNull : "パスワードを空欄にすることはできません",
		confirmPwdAvailable : "このパスワードは利用可能です"
	};
	message["ko_KR"] = {
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "올바른 인증 코드를 입력하십시오",
		pwdError : "올바른 비밀번호를 입력하십시오",
		loginPwdNull : "로그인 비밀번호는 비워 둘 수 없습니다",
		loginPwdLtEightChar : "로그인 비밀번호가 8 자 미만입니다",
		loginPwdGtTwentyChar : "로그인 비밀번호가 20자를 초과합니다",
		pwdNull : "비밀번호는 비워 둘 수 없습니다",
		pwdLtEightChar : "비밀번호가 8 자 미만입니다",
		pwdGtTwentyChar : "비밀번호가 20자를 초과합니다",
		twicePwdNotEq : "일치하지 않는 비밀번호",
		twicePwdEq : "일관된 비밀번호",
		pwdAvailable : "사용 가능한 비밀번호",
		confirmPwdNull : "비밀번호 확인은 비워 둘 수 없습니다",
		confirmPwdAvailable : "비밀번호가 사용 가능한지 확인"
	};
	message["ru_RU"] = {
		codeNull : "Код подтверждения не может быть пустым ",
		codeError : "Пожалуйста, введите правильный код подтверждения ",
		pwdError : "Пожалуйста, введите правильный пароль ",
		loginPwdNull : "Пароль для входа не может быть пустым ",
		loginPwdLtEightChar : "Пароль для входа менее чем 8 символов",
		loginPwdGtTwentyChar : "Пароль для входа более чем 20 символов",
		pwdNull : "Пароль не может быть пустым",
		pwdLtEightChar : "Пароль менее чем 8 символов",
		pwdGtTwentyChar : "Пароль более чем 20 символов",
		twicePwdNotEq : "Пароли не соответствуют",
		twicePwdEq : "Пароли соответствуют",
		pwdAvailable : "Доступный пароль",
		confirmPwdNull : "Подтверждение пароля не может быть пустым",
		confirmPwdAvailable : "Доступное подтверждение пароля"
	};
	var MSG = message[locale];
</script>
</html>
