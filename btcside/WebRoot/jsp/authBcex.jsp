<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>授权登入</title>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>  
	</head>
	<jsp:include page="_common.jsp"></jsp:include>
	<body class="">
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		
		<div class="container-bg-none" style="margin: 60px auto;">
			
			<div class="shadow" style="width:350px;">
				<div class="">
					<form autocomplete="off"
						action="<%=basePath%>doBcexLogin.do"
						class="form-wrap" method="post" onsubmit="return checkParam();">
						<div class="form-title">
							<h2>
								是否给Bcex用户授权？
							</h2>
						</div>
						
						<c:if test="${newUser }">
							<div class="form-group">
								<label for="">
									<fmt:message key="msg.pg.index.loginPwd"/>
								</label>
								<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.register.validPwd"/>"
									name="password" id="password" onblur="checkPwd();" />
								<span class="error" id="error2"></span>
							</div>
							<div class="form-group">
								<label for="">
									<fmt:message key="msg.pg.register.againPwd"/>
								</label>
								<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.register.againAndPwd"/>" name="password2"
									id="password2" onblur="checkPwd2();" />
								<span class="error" id="error3"></span>
							</div>
						</c:if>
						<div class="form-group-btn">
							<button type="submit" class="btn sub-btn" onclick="return cancelClick();">取消</button>
							<button type="submit" class="btn sub-btn" onclick="return checkParam();">授权</button>
						</div>
					</form>
				</div>
			</div>
		</div>
		
		<jsp:include page="_footer.jsp"></jsp:include>
		
	</body>
	
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		emailNull : "邮箱不能为空！",
		emailError : "邮箱格式错误！",
		emailAlreadyRegister : "该邮箱已注册,换个邮箱注册！",
		emailAvailable : "该邮箱可用！",
		pwdNull : "密码不能为空！",
		pwdLtEightChar : "登录密码小于8个字符！",
		pwdGtTwentyChar : "登录密码超过20个字符！",
		twicePwdNotEq : "密码不一致！",
		pwdAvailable : "密码可用！",
		confirmPwdNull : "确认密码不能为空！",
		confirmPwdAvailable : "确认密码可用！",
		codeNull : "验证码不能为空！",
		codeError : "验证码不正确！",
		agreementService : "您需要先同意服务协议！",
		emailBoxError : "邮箱错误！",
		login: "立即登录"
	};
	message["zh_HK"] = {
		emailNull : "郵箱不能為空！",
		emailError : "郵箱格式錯誤！",
		emailAlreadyRegister : "該郵箱已註冊,換個郵箱註冊！",
		emailAvailable : "該郵箱可用！",
		pwdNull : "密碼不能為空！",
		pwdLtEightChar : "登錄密碼小於8個字元！",
		pwdGtTwentyChar : "登錄密碼超過20個字元！",
		twicePwdNotEq : "密碼不一致！",
		pwdAvailable : "密碼可用！",
		confirmPwdNull : "確認密碼不能為空！",
		confirmPwdAvailable : "確認密碼可用！",
		codeNull : "驗證碼不能為空！",
		codeError : "驗證碼不正確！",
		agreementService : "您需要先同意服務協議！",
		emailBoxError : "郵箱錯誤！",
		login: "立即登錄"
	};
	message["en_US"] = {
		emailNull : "The email can not be empty !",
		emailError : "Mailbox format error !",
		emailAlreadyRegister : "This email has been registered! please change another one !",
		emailAvailable : "Available email !",
		pwdNull : "Password cannot be blank !",
		pwdLtEightChar : "The password is less than 8 characters !",
		pwdGtTwentyChar : "The Password is more than 20 characters !",
		twicePwdNotEq : "Inconsistent passwords !",
		pwdAvailable : "Available Password !",
		confirmPwdNull : "Confirm Password cannot be blank !",
		confirmPwdAvailable : "Available Confirm Password !",
		codeNull : "The auth code cannot be blank !",
		codeError : "Please enter correct auth code !",
		agreementService : "You need to agree with the Service Agreement first !",
		emailBoxError : "The email Error !",
		login: "Log in"
	};
	message["ja_JP"] = {
		emailNull : "メールの欄は空欄になれません！",
		emailError : "メールアドレスの形式が間違っています！",
		emailAlreadyRegister : "このメールアドレスは登録されています、他のメールアドレスで登録してください！",
		emailAvailable : "このメールアドレスが利用可能です！",
		pwdNull : "パスワードは空欄になれません！",
		pwdLtEightChar : "パスワードが8文字以下！",
		pwdGtTwentyChar : "パスワードが20文字以上！",
		twicePwdNotEq : "パスワードが一致しません！",
		pwdAvailable : "このパスワードが利用可能です！",
		confirmPwdNull : "確認パスワードの欄は空欄にすることはできません！",
		confirmPwdAvailable : "確認パスワードが利用可能です！",
		codeNull : "認証コードが空欄になれません！",
		codeError : "認証コードが正しくありません！",
		agreementService : "先にサービス同意書への同意が必要です！",
		emailBoxError : "メールアドレスが間違っています！",
		login: "今すぐサインイン"
	};
	message["ko_KR"] = {
			emailNull : "사서함은 비워 둘 수 없습니다!",
			emailError : "우편함 형식이 잘못되었습니다!",
			emailAlreadyRegister : "메일 박스가 등록되었습니다. 다른 이메일을 등록하십시오!",
			emailAvailable : "이 사서함을 사용할 수 있습니다!",
			pwdNull : "비밀번호는 비워 둘 수 없습니다!",
			pwdLtEightChar : "로그인 비밀번호가 8 자 미만입니다!",
			pwdGtTwentyChar : "로그인 비밀번호가 20 자 이상입니다!",
			twicePwdNotEq : "일치하지 않는 비밀번호!",
			pwdAvailable : "비밀번호를 사용할 수 있습니다!",
			confirmPwdNull : "비밀번호는 비워 둘 수 없습니다!",
			confirmPwdAvailable : "비밀번호가 사용 가능한지 확인하십시오!",
			codeNull : "인증 코드는 비워 둘 수 없습니다!",
			codeError : "인증 코드가 잘못되었습니다!",
			agreementService : "먼저 서비스 약관에 동의해야합니다!",
			emailBoxError : "이메일 오류!",
			login: "지금 로그인"
		};
	message["ru_RU"] = {
			emailNull : "Электронная почта не может быть пустой!",
			emailError : "Ошибка формата почтового ящика !",
			emailAlreadyRegister : "Эта электронная почта уже была зарегистрирована, пожалуйста, введите другой э-мейл!",
			emailAvailable : "Доступный адрес электронной почты!",
			pwdNull : "Пароль не может быть пустым!",
			pwdLtEightChar : "Пароль менее чем 8 символов !",
			pwdGtTwentyChar : "Пароль более чем 20 символов !",
			twicePwdNotEq : "Пароли не соответствуют друг другу !",
			pwdAvailable : "Доступный пароль !",
			confirmPwdNull : "Подтверждение пароля не может быть пустым !",
			confirmPwdAvailable : "Доступное подтверждение пароля !",
			codeNull : "Код аутентификации не может быть пустым!",
			codeError : "Пожалуйста, введите правильный аутентификационный код!",
			agreementService : "Вы должны сначала согласиться с соглашением об обслуживании !",
			emailBoxError : "Ошибка электронной почты !",
			login: "Войти"
		};		
	var MSG = message[locale];
	
	var password_bol = false;
	var password2_bol = false;
	
	function cancelClick() {
		location.href = basePath;
		return false;
	}
	
	function checkPwd() {
		var password = $("#password").val();
		var unLen = password.length;
		if (unLen == 0) {
			document.getElementById("error2").style.color = "red";
			$("#error2").html(MSG['pwdNull']);
			password_bol = false;
			return false;
		}
		if (unLen < 8) {
			document.getElementById("error2").style.color = "red";
			$("#error2").html(MSG['pwdLtEightChar']);
			password_bol = false;
			return false;
		}
		if (unLen > 20) {
			document.getElementById("error2").style.color = "red";
			$("#error2").html(MSG['pwdGtTwentyChar']);
			password_bol = false;
			return false;
		}
		var repassword = $("#password2").val();
		if (repassword != "") {
			if (password != repassword) {
				document.getElementById("error3").style.color = "red";
				$("#error3").html(MSG['twicePwdNotEq']);
				password2_bol = false;
				return false;
			} else {
				document.getElementById("error3").style.color = "green";
				$("#error3").html(MSG['pwdAvailable']);
				password2_bol = true;
			}
		}
		document.getElementById("error2").style.color = "green";
		$("#error2").html(MSG['pwdAvailable']);
		password_bol = true;
		return true;
	}
	
	function checkPwd2() {
		var password1 = $("#password").val();
		var password2 = $("#password2").val();
		
		if(password2 == ""){
			document.getElementById("error3").style.color = "red";
			$("#error3").html(MSG['confirmPwdNull']);
			password2_bol = false;
			return false;
		}
		if (password1 == password2) {
			document.getElementById("error3").style.color = "green";
			$("#error3").html(MSG['confirmPwdAvailable']);
			password2_bol = true;
			return true;
		} else {
			document.getElementById("error3").style.color = "red";
			$("#error3").html(MSG['twicePwdNotEq']);
			password2_bol = false;
			return false;
		}
	}
	
	function checkParam(){
		if(password_bol && password2_bol) {
			return true;
		}
		
		if(!password_bol){
			document.getElementById("error2").style.color = "red";
			$("#error2").html(MSG['pwdLtEightChar']);
		}else if(!password2_bol){
			document.getElementById("error3").style.color = "red";
			$("#error3").html(MSG['twicePwdNotEq']);
		}
		
		return false;
	}
	
</script>
</html>
