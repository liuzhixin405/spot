<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.userFindPwd2.title"/> | <fmt:message key="msg.pg.systemName"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>" />
			<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script>
		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->
		<div class="container-bg-none">
			<div class="login-box">
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
				<form action="<%=basePath%>password/findPwd2do.do" class="form-wrap" method="post" autocomplete="off">
					<div class="form-title">
						<h2>
							<fmt:message key="msg.pg.userFindPwd2.title"/>
						</h2>
					</div>
					<input type="hidden" name="email" value="${email }" />
					<input type="hidden" name="phone" value="${phone }" />
					<div class="form-group">
						<label for="">
							<fmt:message key="msg.pg.userChangePwd.newPwd"/>
						</label>
						<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userChangePwd.inputNewPwd"/>" id="newpasswd1" autocomplete="new-password"
							name="newpasswd1" onblur="checknewPwd();" />
						<span><fmt:message key="msg.pg.userChangePwd.note"/></span>
						<span class="error" id="error2"></span>
					</div>
					<div class="form-group">
						<label for="">
							<fmt:message key="msg.pg.userChangePwd.confirmNewPwd"/>
						</label>
						<input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userChangePwd.againInputNewPwd"/>" id="newpasswd2" autocomplete="new-password"
							name="newpasswd2" onblur="checknewPwd2();" />
						<span class="error" id="error3"></span>
					</div>
					<div class="form-group-btn">
						<button type="submit" class="btn sub-btn" onclick="return checkPwd();"><fmt:message key="msg.pg.userChange.confirmChange"/></button>
					</div>
				</form>
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
	</body>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
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
		pwdNull : "Password cannot be empty",
		pwdLtEightChar : "The password is less than 8 characters",
		pwdGtTwentyChar : "The Password is more than 20 characters",
		twicePwdNotEq : "Inconsistent passwords",
		twicePwdEq : "Consistent passwords",
		pwdAvailable : "Available Password",
		confirmPwdNull : "Confirm Password cannot be empty",
		confirmPwdAvailable : "Available Confirm Password"
	};
	message["ja_JP"] = {
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
		pwdNull : "Пароль не может быть пустым",
		pwdLtEightChar : "Пароль менее чем 8 символов ",
		pwdGtTwentyChar : "Пароль более чем 20 символов ",
		twicePwdNotEq : "Пароли не соответствуют",
		twicePwdEq : "Пароли соответствуют",
		pwdAvailable : "Доступный пароль",
		confirmPwdNull : "Подтверждение пароля не может быть пустым",
		confirmPwdAvailable : "Доступное подтверждение пароля"
	};
	var MSG = message[locale];
</script>
</html>
