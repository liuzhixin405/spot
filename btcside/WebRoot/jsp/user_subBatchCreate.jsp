<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.inviteMag"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
	<!-- 子账户提示 -->
		<c:if test="${isSubUser == true}">
			<div id="add-windows" class="add-windows">
				<div id="tb-overlay" class="tb-overlay"></div>
				<div class="add-wrap" style="display:block;">
					<div class="dialogs-title">
						<h2 class="lt"><fmt:message key="msg.pg.userCenter.subAccount"/></h2>
						<a href="javascript:history.go(-1)" class="close-icon icons-close" id="close-alert"></a>
					</div>
					<div class="dialogs-body">
						<p style="margin-bottom:30px; text-align:center;font-size:16px;">
							很抱歉！该功能子账号未开放
						</p>
						<div class="form-group-btn" style="display: flex; justify-content: center">
							<a href="javascript:history.go(-1)" class="btn sub-btn-return"><fmt:message key="msg.pg.trade.goBack"/></a>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		<!-- 安全提示 -->
		<c:if test="${applayerror==true}">
			<div id="add-windows" class="add-windows">
				<div id="tb-overlay" class="tb-overlay"></div>
				<div class="add-wrap" style="display:block;">
				<div class="dialogs-title">
		          		<h2 class="lt"><fmt:message key="msg.pg.top.withdraw"/></h2>
		          		<a href="javascript:history.go(-1)" class="close-icon icons-close" id="close-alert"></a>
		      		</div>
					<div class="dialogs-body">
						<p style="margin-bottom:30px; text-align:center;font-size:16px;">
							<fmt:message key="msg.pg.userCenter.gobind"/>
						</p>
					<div class="form-group-btn">
					<a href="<%=basePath%>center/accountSafe.do" class="btn sub-btn"><fmt:message key="msg.pg.userCenter.letbind"/></a>
						<a href="javascript:history.go(-1)" class="btn sub-btn-return"><fmt:message key="msg.pg.trade.goBack"/></a>
						</div>
					</div>
				</div>
			 </div>
		 </c:if>
		<div class="add-windows" id="alert-windows"></div>
		 <div class="add-windows">
			<div id="tb-overlay-duiping" class="tb-overlay display-none"></div>
			<div class="add-wrap dp-alert-wrap invite-BFX" id="invite-BFX">
				<a href="javascript:" class="close-icon icons-close dp-close-alert" style="position: absolute;top: 18px;right: 20px;width: 18px;height: 18px;display: block;"></a>
				<div class="loading-pic"><span class="loding-img"></span></div>
				<a class="invimg-link" id="invimg-link" target="_blank"><img alt="" src="" id="inv-img"></a>
				<div style="text-align: center;" id="down-lav">
				<button class="download-btn"><fmt:message key="msg.pg.userCenter.inviteDownload"/></button>
				</div>
			</div>	
		</div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title-all">
						<h2><fmt:message key="msg.pg.userCenter.subheadSubAccount1"/></h2>
						<a class="first-pages" style="font-size: 14px;color: #94abc0;margin-left: 25px;"><fmt:message key="msg.pg.userCenter.subAccount"/>></a>
						<a class="second-pages" style="font-size: 14px;color: #5d8fe9;"><fmt:message key="msg.pg.userCenter.subheadSubAccount1"/></a>
						<%-- <span class="save-drafts">
							<botton type="submit"><fmt:message key="msg.pg.userCenter.SaveAsaDraft"/></botton>
						</span> --%>
					</div>
				</div>
				<div class="main-wrap">
					<span class="suggest-tips"><fmt:message key="msg.pg.userCenter.MultipleBatchCreation"/></span>
					<div class="subAccount-allpage" id="subAccount-allpage">
						<div class="subAccount-allpage-box" id="subAccount-allpage-box">
							<div class="subAccount-allpage-title">
								<h3><fmt:message key="msg.pg.userCenter.subAccount"/>-1</h3>
								<%-- <botton onclick="deleteCreatesub()" type="submit"><fmt:message key="msg.pg.userCenter.delete"/></botton> --%>
							</div>
							<span class="loginPassword-item">
								<fmt:message key="msg.pg.userCenter.SubaccountloginName"/>：<em class="blank-part1"></em>
								<input name="email1" autocomplete="off" id="email1" onblur="checkLoginName(1)" placeholder="<fmt:message key="msg.pg.userCenter.writeSubloginName"/>">
								<span class="error" id="emailError1"></span>
							</span>
							<span class="loginPassword-item">
								<fmt:message key="msg.pg.index.loginPwd"/>：<em class="blank-part2"></em>
								<input class="loginPassword" autocomplete="new-password" type="password" onblur="checkPassword(1)" name="pwd1" id="pwd1" placeholder="<fmt:message key="msg.pg.userCenter.writeloginPassword"/>">
								<span class="error" id="pwdError1"></span>
								<img class="showPsws" id="openPsw1" onclick="changeShowPwd(1)" src="../common/images/open-eyes.png">
								<img class="showPsws pswnone" id="closePsw1" onclick="changeShowPwd(1)" src="../common/images/close-eyes.png">
							</span>
						</div>
					</div>
					<div class="addSubaccount-box">
						<div class="addSubaccount-active">
							<img onclick="addSubaccountList()" class="addSubaccount" id="addSubaccount" src="../common/images/add-subaccount.png">
							<span onclick="addSubaccountList()"><fmt:message key="msg.pg.userCenter.addSubaccount"/></span>
						</div>
					</div>
				</div>
				<div class="verify-wrap">
				<form id="verifywrapForm">
					<span class="verify-item" style="${showtel==1 ? '' : 'display:none'}">
						<fmt:message key="msg.pg.userCenter.applySMScode"/>：<em class="blank-part5"></em>
						<input type="hidden" id="mobile" name="mobile" value="${tel}"/>
						<input class="msg-box" autocomplete="off" name="code" id="code" placeholder="<fmt:message key="msg.pg.userCenter.writeapplySMScode"/>">
						<%-- <button type="button" class="btn sendmsg-btn" id="btnCodeNew"><fmt:message key="msg.pg.userCenter.sendapplySMScode"/></button> --%>
						<input class="btn sendmsg-btn" type="button" id="btnCodeNew" name="btnCodeNew" value="<fmt:message key="msg.pg.userCenter.sendapplySMScode"/>"/>
						
						<span class="error" id="error1"></span>
					</span>
					<span class="verify-item" style="${showgoogle==1 ? '' : 'display:none'}">
						<fmt:message key="msg.pg.userCenter.googleCode"/>：<em class="blank-part5"></em>
						<input class="google-msg-box" autocomplete="off" name="google" id="google" placeholder="<fmt:message key="msg.pg.userCenter.sendgoogelcode"/>">
						<span class="error" id="googleerror"></span>
					</span>
				</form>
						<button type="submit" onclick="addBatchSubUser(); return false;" class="btn affirmmsg-btn"><fmt:message key="msg.pg.userCenter.affirm"/></button>
				</div>
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/subBatchAccount.js?<%=rjs%>"></script>
</body>
<script type="text/javascript">
	var fmtsubAccount = "<fmt:message key='msg.pg.userCenter.subAccount'/>"
	var fmtdelete = "<fmt:message key='msg.pg.userCenter.delete'/>"
	var fmtSubaccountloginName = "<fmt:message key='msg.pg.userCenter.SubaccountloginName'/>"
	var fmtwriteSubloginName = "<fmt:message key='msg.pg.userCenter.writeSubloginName'/>"
	var fmtloginPwd = "<fmt:message key='msg.pg.index.loginPwd'/>"
	var fmtwriteloginPassword = "<fmt:message key='msg.pg.userCenter.writeloginPassword'/>"
	
	$("#btnCodeNew").click(function() {
		document.forms.verifywrapForm.btnCodeNew.disabled = true;
		$.post("<%=basePath%>center/sendOldTelCode.do", {}, function(n){
			if(n == "success"){
				for (i = 1; i <= secs; i++) {
					window.setTimeout("doUpdate(" + i + ")", i * 1000);
				}
			} else {
				tipAlert(n);
				document.forms.verifywrapForm.btnCodeNew.disabled = false;
			}
		}, "text");
	});
	
    var secs = 60;
    function doUpdate(num) {
		if (num == '60') {
			document.forms.verifywrapForm.btnCodeNew.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
			document.forms.verifywrapForm.btnCodeNew.disabled = false;
			$("#btnCodeNew").css("background-color","#5d8fe9");
		} else {
			document.forms.verifywrapForm.btnCodeNew.disabled = true;
			$("#btnCodeNew").css("background-color","#cfcfcf");
			var wut = secs - num;
			document.forms.verifywrapForm.btnCodeNew.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
		}
	}
</script>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		emailNull : "登录名不能为空！",
		emailError : "登录名格式错误！",
		emailAlreadyRegister : "该登录名已注册,换个邮箱注册！",
		emailAvailable : "该登录名可用！",
		pwdNull : "密码不能为空！",
		pwdLtEightChar : "登录密码小于8个字符！",
		pwdGtTwentyChar : "登录密码超过20个字符！",
		twicePwdNotEq : "密码不一致！",
		twicePwdEq : "密码一致",
		pwdAvailable : "密码可用！",
		confirmPwdNull : "确认密码不能为空！",
		confirmPwdAvailable : "确认密码可用！",
		codeNull : "验证码不能为空！",
		codeError : "验证码不正确！",
		agreementService : "您需要先同意服务协议！",
		emailBoxError : "邮箱错误！",
		login: "立即登录",
		warmTip: "温馨提示",
		sureTip: "确定",
		nickNameRule: "登录名为4-32个字符(可包含字母,数字和下划线)",
		hasnoOK:"登录名不符合规则"
	};
	message["zh_HK"] = {
		emailNull : "登錄名不能為空！",
		emailError : "登錄名格式錯誤！",
		emailAlreadyRegister : "該登錄名已註冊,換個郵箱註冊！",
		emailAvailable : "該登錄名可用！",
		pwdNull : "密碼不能為空！",
		pwdLtEightChar : "登錄密碼小於8個字元！",
		pwdGtTwentyChar : "登錄密碼超過20個字元！",
		twicePwdNotEq : "密碼不一致！",
		twicePwdEq : "密碼一致",
		pwdAvailable : "密碼可用！",
		confirmPwdNull : "確認密碼不能為空！",
		confirmPwdAvailable : "確認密碼可用！",
		codeNull : "驗證碼不能為空！",
		codeError : "驗證碼不正確！",
		agreementService : "您需要先同意服務協議！",
		emailBoxError : "郵箱錯誤！",
		login: "立即登錄",
		warmTip: "溫馨提示",
		sureTip: "確定",
		nickNameRule: "登錄名為4-32個字元(可包含字母,數字和下劃線)",
		hasnoOK:"登錄名不符合規則"
	};
	message["en_US"] = {
		emailNull : "The login name can not be empty !",
		emailError : "Login name format error !",
		emailAlreadyRegister : "This login name has been registered! please change another one !",
		emailAvailable : "Available login name !",
		pwdNull : "Password cannot be blank !",
		pwdLtEightChar : "The password is less than 8 characters !",
		pwdGtTwentyChar : "The Password is more than 20 characters !",
		twicePwdNotEq : "Inconsistent passwords !",
		twicePwdEq : "Consistent passwords",
		pwdAvailable : "Available Password !",
		confirmPwdNull : "Confirm Password cannot be blank !",
		confirmPwdAvailable : "Available Confirm Password !",
		codeNull : "The auth code cannot be blank !",
		codeError : "Please enter correct auth code !",
		agreementService : "You need to agree with the Service Agreement first !",
		emailBoxError : "The email Error !",
		login: "Log in",
		nickNameRule: "The login name is 4-32 characters (which can include letters, Numbers, and underscores)",
		hasnoOK:"The user name does not follow the rules"
	};
	message["ja_JP"] = {
		emailNull : "メールの欄は空欄にすることはできません！",
		emailError : "メールアドレスの形式が間違っています！",
		emailAlreadyRegister : "このメールアドレスは登録されています、他のメールアドレスで登録してください！",
		emailAvailable : "このメールアドレスは利用可能です！",
		pwdNull : "パスワードは空欄になれません！",
		pwdLtEightChar : "パスワードが8文字以下！",
		pwdGtTwentyChar : "パスワードが20文字以上！",
		twicePwdNotEq : "パスワードが一致しません！",
		twicePwdEq : "パスワードが一致しています",
		pwdAvailable : "パスワードが一致しません！",
		confirmPwdNull : "確認パスワードの欄は空欄にすることはできません！",
		confirmPwdAvailable : "確認パスワードが利用可能です！",
		codeNull : "認証コードが空欄になれません！",
		codeError : "認証コードが正しくありません！",
		agreementService : "先にサービス同意書を同意してから！",
		emailBoxError : "メールアドレスが間違っています！",
		login: "今すぐサインイン",
		nickNameRule: "登录名为4-32个字符(可包含字母,数字和下划线)",
		hasnoOK:"ユーザー名がルールに一致しません"
	};
	message["ko_KR"] = {
		emailNull : "로그인 이름은 비워 둘 수 없습니다!",
		emailError : "로그인 이름이 잘못된 형식입니다!",
		emailAlreadyRegister : "로그인이 이미 등록되었습니다. 변경하십시오!",
		emailAvailable : "이 로그인이 가능합니다!",
		pwdNull : "비밀번호는 비워 둘 수 없습니다!",
		pwdLtEightChar : "로그인 비밀번호가 8 자 미만입니다!",
		pwdGtTwentyChar : "로그인 비밀번호가 20 자 이상입니다!",
		twicePwdNotEq : "일치하지 않는 비밀번호!",
		twicePwdEq : "일관된 비밀번호",
		pwdAvailable : "비밀번호를 사용할 수 있습니다!",
		confirmPwdNull : "비밀번호는 비워 둘 수 없습니다!",
		confirmPwdAvailable : "비밀번호가 사용 가능한지 확인하십시오!",
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "인증 코드가 잘못되었습니다!",
		agreementService : "먼저 서비스 계약에 동의해야합니다!",
		emailBoxError : "로그인 이름이 잘못되었습니다!",
		login: "지금 로그인",
		warmTip: "팁",
		sureTip: "결정",
		nickNameRule: "로그인 이름은 4-32 자입니다 (문자, 숫자 및 밑줄을 포함 할 수 있음)",
		hasnoOK:"로그인 이름이 규칙과 일치하지 않습니다"
	};
	message["ru_RU"] = {
		emailNull : "Имя пользователя(логин) не может быть пустым !",
		emailError : "Ошибка формата имени пользователя!",
		emailAlreadyRegister : "Этот логин (имя пользователя) уже зарегистрирован, пожалуйста, используйте другой логин !",
		emailAvailable : "Доступное имя пользователя !",
		pwdNull : "Пароль не может быть пустым !",
		pwdLtEightChar : "Пароль менее чем 8 символов!",
		pwdGtTwentyChar : "Пароль более чем 20 символов!",
		twicePwdNotEq : "Пароли не соответствуют !",
		twicePwdEq : "Пароли соответствуют",
		pwdAvailable : "Доступный пароль !",
		confirmPwdNull : "Подтверждение пароля не может быть пустым !",
		confirmPwdAvailable : "Доступное подтверждение пароля !",
		codeNull : "Код аутентификации не может быть пустым !",
		codeError : "Пожалуйста, введите правильный код аутентификации !",
		agreementService : "Вы должны сначала согласиться с соглашением об обслуживании !",
		emailBoxError : "Ошибка электронной почты !",
		login: "Войти",
		warmTip : "подсказка",
		sureTip : "Конечно",
		nickNameRule: "Логин может состоять из 4-32 символов (которые могут включать в себя буквы, цифры и символы подчеркивания)",
		hasnoOK: "Имя пользователя не соответствует правилам "
	};
	var MSG = message[locale];
</script>
</html>

