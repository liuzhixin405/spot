<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.login.userLogin"/> | <fmt:message key="msg.pg.systemName"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>"/>		
		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/login.js?<%=rjs%>"  ></script>
		<script type="text/javascript" src="<%=basePath%>common/js/register.js?<%=rjs%>"  ></script>
		<script type="text/javascript" src="<%=basePath%>common/js/loginemailAutoComplete.js?<%=rjs%>"></script>
		<script type="text/javascript">
			function onByEnter(event){			
     			var lKeyCode = (navigator.appName=="Netscape") ? event.which : window.event.keyCode; //event.keyCode按的建的代码，13表示回车   			
     			 if ( lKeyCode == 13 ){ 		
	  				$(".index-sub").trigger("click");
  				 }
  			}
			var subUserLoign = "${subUserLoign }";
		</script>
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
      		<div class="success">${success}</div>
      </c:if>
      <c:if test="${error!=null&&error!=''}">
      		<div class="warning" >${error}</div>
      </c:if>
      <span id="loginheader" ></span>
		<form action="<%=basePath%>login/logindo.do" method="post" class="ui-form" id="loginform" name="loginform" onsubmit="return login();">
			 <div class="form-title">
		          <h2><fmt:message key="msg.pg.login.userLogin"/></h2>
        	 </div>
			 <div class="form-group email-select">
		         <%--  <label for=""><fmt:message key="msg.pg.index.loginEmail"/></label> --%>
		          	<input type="text" id="email" name="email"  tabindex="1" value="${email}" onkeydown="removeMsg(event)" class="form-control inputElem" autocomplete="off" autofocus="autofocus" placeholder="<fmt:message key="msg.pg.index.loginEmail"/>"/>
		          	<%-- <label for="email" class="label-item"><fmt:message key="msg.pg.index.loginEmail"/></label> --%>
		          	<span class="error" id="emailerror"></span>
             </div>
			 <div class="form-group" style="margin-bottom: 0;">
		         <%--  <label for=""><fmt:message key="msg.pg.index.loginPwd"/></label> --%>
		          	<input type="password" class="form-control" class="form-control" tabindex="2" onkeydown="removeMsg(event)" id="password" name="password" autocomplete="off" onkeyup="onByEnter(event);" placeholder="<fmt:message key="msg.pg.index.loginPwd"/>"/>
					<%-- <label for="password" class="label-item"><fmt:message key="msg.pg.index.loginPwd"/></label> --%>
		          	<span class="error"></span>
        	 </div>	
			 <div class="form-group" style="display:none" id="div_yzm">
		          <%-- <label for="" class="lt" style="line-height:40px;"><fmt:message key="msg.pg.login.validcode"/>：</label> --%>
					<input type="text" placeholder="<fmt:message key="msg.pg.login.validcodePut"/>" class="form-control code" name="validcode" id="validcode" onkeydown="removeMsg(event)"/>
					<label for="validcode" class="label-item"><fmt:message key="msg.pg.login.validcodePut"/></label>
					<img src="<%=basePath%>Captcha/CaptchaAction.do?" id="imgValidcode" onclick="RefreshCaptcha(this);" alt="<fmt:message key="msg.pg.login.changeOther"/>" style="margin-left: 17px;"/>
				  	<span class="error" id="error4"></span>
	         </div>
			 <div class="form-group-btn">
					<a class="forget-pwd" href="<%=basePath%>password/findPwd.do"><fmt:message key="msg.pg.index.forgetPwd"/>？</a>
					<input type="submit" class="btn sub-btn sub-btn-click" id="login_but_submit" value="<fmt:message key="msg.pg.login.goJoin"/>"></input>
					<span class="noAccount"><fmt:message key="msg.pg.login.noAccount"/>？<a href="<%=basePath%>register/register.do"><fmt:message key="msg.pg.login.goResiter"/></a>
		          </span>
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
		emailNull : "邮箱,手机号或子账号不能为空！",
		emailError : "邮箱,手机号或子账号格式错误！",
		emailOrPwdNull: "用户名、密码不能为空！",
		emailOrPwdError: "用户名或登录密码不正确！",
		accountFfrozen: "该账户已冻结, 请联系管理员！",
		codeNull: "验证码不能为空！",
		codeError: "验证码错误！",
		login: "立即登录",
		phoneError : "号码错误",
		phoneRegistered : "该号码已注册,换个号码注册",
		phoneAvailable : "该号码可用",
		phoneNull : "邮箱,手机号或子账号不能为空！！",
		codeNull : "验证码不能为空！",
		codeError : "请输入正确的验证码",
		warmTip : "温馨提示",
		sureTip : "确定",
		nickNameRule: "登录名为4-32个字符(可包含字母,数字和下划线)",
		emailAlreadyRegister : "该登录名已注册,请换一个！",
		emailAvailable : "该登录名可用！",
		hasnoOK:"登录名不符合规则",
		sliderValidation: "请按住滑块，拖动到最右边",
		validationPass: "验证通过",
		errorCkeck: "哎呀，出错了，点击",
		refurbishSlider: "刷新",
		againTry: "再来一次",
		notGelievabl: "网络不给力，请",
		ClickRefresh: "点击刷新",
	};
	message["zh_HK"] = {
		emailNull : "郵箱,手機號或子帳號不能為空！",
		emailError : "郵箱,手機號或子帳號格式錯誤！",
		emailOrPwdNull: "用戶名、密碼不能為空！",
		emailOrPwdError: "用戶名或登錄密碼不正確！",
		accountFfrozen: "該帳戶已凍結, 請聯繫管理員！",
		codeNull: "驗證碼不能為空！",
		codeError: "驗證碼錯誤！",
		login: "立即登錄",
		phoneError : "號碼錯誤",
		phoneRegistered : "該號碼已註冊,換個號碼註冊",
		phoneAvailable : "該號碼可用",
		phoneNull : "郵箱,手機號或子帳號不能為空！！",
		codeNull : "驗證碼不能為空！",
		codeError : "請輸入正確的驗證碼",
		warmTip : "溫馨提示",
		sureTip : "確定",
		nickNameRule: "登錄名為4-32個字元(可包含字母,數字和下劃線)",
		emailAlreadyRegister : "該登錄名已註冊,請換一個！",
		emailAvailable : "該登錄名可用！",
		hasnoOK:"登錄名不符合規則",
		sliderValidation: "請按住滑塊，拖動到最右邊",
		validationPass: "驗證通過",
		errorCkeck: "哎呀，出錯了，點擊",
		refurbishSlider: "刷新",
		againTry: "再來一次",
		notGelievabl: "網路不給力，請",
		ClickRefresh: "點擊刷新",
	};
	message["en_US"] = {
		emailNull : "The email,phone number or sub account login name can not be empty !",
		emailError : "Mailbox,phone number or sub account login name format error !",
		emailOrPwdNull: "The username and password can not be empty !",
		emailOrPwdError: "Incorrect username or login password !",
		accountFfrozen: "The account has been frozen. please contact customer service !",
		codeNull: "The verifying code can not be empty !",
		codeError: "Verification code error !",
		login: "Log in",phoneError : "Incorrect phone number",
		phoneRegistered : "The number has been registered, change another one please",
		phoneAvailable : "Available phone number",
		phoneNull : "The email, or phone number or sub account login name cannot be empty",
		codeNull : "Verification code cannot be empty",
		codeError : "Please enter correct verify code",
		warmTip : "Reminder",
		sureTip : "Confirm",
		nickNameRule: "The login name is 4-32 characters (which can include letters, Numbers, and underscores)",
		emailAlreadyRegister : "This login name has been registered! please change another one !",
		emailAvailable : "Available login name !",
		hasnoOK:"The user name does not follow the rules",
		sliderValidation: "Please hold down the slider and drag to the far right",
		validationPass: "Verification passed",
		errorCkeck: "Oops, something went wrong, click",
		refurbishSlider: "Refresh",
		againTry: "Again",
		notGelievabl: "The network is not strong, please",
		ClickRefresh: "Click refresh",
	};
	message["ja_JP"] = {
		emailNull : "メール、携帯電話番号、またはサブアカウントを空にすることはできません！",
		emailError : "電子メール、携帯電話番号、またはサブアカウントの形式が間違っています！",
		emailOrPwdNull: "ユーザー名、パスワードは空欄にすることはできません！",
		emailOrPwdError: "ユーザー名またはログインパスワードが正しくありません！",
		accountFfrozen: "このアカウントが凍結されています、管理者に連絡してください！",
		codeNull: "認証コードを空欄にすることはできません！",
		codeError: "認証コードが間違っています！",
		login: "今すぐサインイン",
		phoneError : "エラーメール/電話番号",
		phoneRegistered : "この電話番号は登録されています、他の電話番号で登録してください",
		phoneAvailable : "この電話番号は利用可能です",
		phoneNull : "電話番号を必ず入力してください！",
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
		warmTip : "暖かいヒント",
		sureTip : "確認",
		nickNameRule: "登录名为4-32个字符(可包含字母,数字和下划线)",
		emailAlreadyRegister : "このメールアドレスは登録されています、他のメールアドレスで登録してください！",
		emailAvailable : "このメールアドレスは利用可能です！",
		hasnoOK:"ユーザー名がルールに一致しません",
		sliderValidation: "一番右にドラッグ",
		validationPass: "検証に合格しましたs",
		errorCkeck: "間違った、クリック",
		refurbishSlider: "リフレッシュ",
		againTry: "また来て",
		notGelievabl: "ネットワークは強制しません",
		ClickRefresh: "更新をクリック",
	};
	message["ko_KR"] = {
		emailNull : "이메일, 휴대폰 번호 또는 하위 계정은 비워 둘 수 없습니다!",
		emailError : "이메일, 휴대폰 번호 또는 하위 계정 형식이 잘못되었습니다!",
		emailOrPwdNull: "사용자 이름과 비밀번호는 비워 둘 수 없습니다!",
		emailOrPwdError: "사용자 이름 또는 로그인 비밀번호가 잘못되었습니다!",
		accountFfrozen: "계정이 정지되었습니다. 관리자에게 문의하십시오!",
		codeNull: "인증 코드는 비워 둘 수 없습니다!",
		codeError: "인증 코드 오류!",
		login: "지금 로그인",
		phoneError : "잘못된 번호",
		phoneRegistered : "이미 등록 된 번호입니다. 등록 할 번호를 변경하십시오",
		phoneAvailable : "이 번호는 사용할 수 있습니다",
		phoneNull : "이메일, 휴대폰 번호 또는 하위 계정은 비워 둘 수 없습니다! !",
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "올바른 인증 코드를 입력하십시오",
		warmTip : "팁",
		sureTip : "결정",
		nickNameRule: "로그인 이름은 4-32 자입니다 (문자, 숫자 및 밑줄을 포함 할 수 있음)",
		emailAlreadyRegister : "로그인이 이미 등록되었습니다. 변경하십시오!",
		emailAvailable : "이 로그인이 가능합니다!",
		hasnoOK:"로그인 이름이 규칙과 일치하지 않습니다",
		sliderValidation: "슬라이더를 누른 채 오른쪽 끝으로 드래그하십시오",
		validationPass: "확인 통과",
		errorCkeck: "잘못 클릭",
		refurbishSlider: "새로 고침",
		againTry: "다시 오세요",
		notGelievabl: "네트워크는 강제하지 않습니다",
		ClickRefresh: "새로 고침 클릭",
	};
	message["ru_RU"] = {
		emailNull : "Адрес электронной почты, номер телефона или логин суб аккаунта не может быть пустым !",
		emailError : "Ошибка формата имени для входа в почтовый ящик, номер телефона или суб-аккаунта!",
		emailOrPwdNull: "Имя пользователя и пароль не могут быть пустыми!",
		emailOrPwdError: "Неверное имя пользователя или пароль!",
		accountFfrozen: "Аккаунт был заморожен. Пожалуйста, свяжитесь со службой поддержки!",
		codeNull: "Код подтверждения не может быть пустым !",
		codeError: "Ошибка кода подтверждения!",
		login: "Войти",
		phoneError : "Неверный номер мобильного телефона",
		phoneRegistered : "Номер зарегистрирован, замените на другой пожалуйста",
		phoneAvailable : "Доступный номер телефона",
		phoneNull : "Адрес электронной почты, номер телефона или логин суб-аккаунта не могут быть пустыми",
		codeNull : "Код подтверждения не может быть пустым",
		codeError : "Пожалуйста, введите правильный код подтверждения",
		warmTip : "Важное напоминание\подсказка ",
		sureTip : "Подтвердить",
		nickNameRule: "Логин может состоять из 4-32 символов (которые могут включать в себя буквы, цифры и символы подчеркивания)",
		emailAlreadyRegister : "Это имя пользователя уже зарегистрировано! Пожалуйста, выберите другое!",
		emailAvailable : "Доступное имя пользователя !",
		hasnoOK: "Имя пользователя не соответствует правилам ",
		sliderValidation: "Пожалуйста, удерживайте ползунок и перетащите в край в право. ",
		validationPass: "Верификация\проверка пройдена",
		errorCkeck: "Ой, что-то пошло не так, нажмите",
		refurbishSlider: "Обновить",
		againTry: "Еще раз",
		notGelievabl: "Работа сети не стабильна, пожалуйста",
		ClickRefresh: "Нажмите обновить",
	};
	var MSG = message[locale];
	
	function removeMsg(event){
		if (event.keyCode == null || typeof(event.keyCode) == undefined) {
			return;
		}
		
		$(".warning").remove();
	}
</script>
</html>
