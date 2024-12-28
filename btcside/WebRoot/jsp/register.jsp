<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.register.userRegisterTitle"/> | <fmt:message key="msg.pg.systemName"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/intlTelInput/intlTelInput.css"/>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/register.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/city.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->

		<div class="container-bg-none" style="margin: 60px auto;width: 995px">
			
			<div class="shadow">
				<div class="sign-lt lt">
					<form action="<%=basePath%>register/registerdo.do?action=register" autocomplete="off"
						class="form-wrap" method="post" onsubmit="return checkFlag();">
						<div class="form-title register-tab">
							<a class="register-tab-item ${isEmail ? '' : 'active-color'}" href="register.do">
								<fmt:message key="msg.pg.register.phoneRegister"/>
							</a>
							
							<a style="margin-left: 20px;" class="register-tab-item ${isEmail ? 'active-color' : ''}" href="register.do?type=email">
								<fmt:message key="msg.pg.register.userRegister"/>
							</a>
							<span><fmt:message key="msg.pg.register.hasAccount"/>？<a href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.register.goL"/></a>
							</span>
						</div>
						<div class="alert">
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
						<div class="form-group form-group-list" style="margin-bottom: 5px;">
							<input type="hidden" name="countryCode" id="countryCode" value="">
							<input type="hidden" name="currCountryName" id="currCountryName" value="" />
							<label for="countryOrregion" class="label-item label-country"><fmt:message key="msg.pg.userCenter.choseCountry"/></label>
							<input type="text" class="form-control form-control-city" data-value="0" value="" name="country" id="countryOrregion" readonly style="padding: 17px 30px 17px 8px;border: none;border-radius: 4px;text-indent: 40px;"/>
							<i class="select-country-arrow"></i>
<%--							<span class="label-city"><fmt:message key="msg.pg.register.country"/></span>--%>
							<span class="marked-words"><fmt:message key="msg.pg.register.countryTips"/></span>
							<span class="error" id="countryError"></span>
						</div>
						<div class="form-group email-select form-group-list" style="margin-bottom: 5px;">
							<c:if test="${isEmail == null || !isEmail}">
								<input type="text" autocomplete="off" style="width: 120px !important;float: left;margin-right: 20px;display: inline-block;transition: none;cursor: default;border: 1px solid #F1F3F4;padding: 16px 20px 16px 8px;" data-value="0" 
									value="${zone}" id="areaCode" name="zone" class="form-control-zone inputElem" placeholder="<fmt:message key="msg.pg.userCenter.choseCode"/>" readonly/>
								<i class="select-arrow"></i>
							</c:if>
							<input type="text" autocomplete="off" <c:if test="${isEmail == null || !isEmail}">style="width: 256px;display: inline-block;"</c:if> data-value="${email != null ? email.length() : 0}" value="${email }" id="email" name="email" onblur="${isEmail ? 'checkEmail()' : 'checkPhone()'};" class="form-control inputElem" />
							<label for="email" class="label-item" <c:if test="${isEmail == null || !isEmail}">style="left: 160px;"</c:if>>
							<c:if test="${isEmail == null || !isEmail}">
								<fmt:message key='msg.pg.userBindPhone.phoneNumber'/>
							</c:if>
							<c:if test="${isEmail != null && isEmail}">
								<fmt:message key='msg.pg.register.validEmail'/>
							</c:if>
							</label>
							<span class="error" id="emailerror"></span>
						</div>
						<div class="form-group form-group-list" style="margin-bottom: 5px;">
							<input type="password" autocomplete="new-password" data-value="0" class="form-control" name="password" id="password" onblur="checkPassword();" />
							<label for="password" class="label-item"><fmt:message key="msg.pg.index.loginPwd"/></label>
							<span class="error" id="error2"></span>
						</div>
						<div class="form-group form-group-list" style="margin-bottom: 5px;">
							<input type="password" autocomplete="new-password" data-value="0" class="form-control" name="password2" id="password2" onblur="checkPassword2();" />
							<label for="password2" class="label-item"><fmt:message key="msg.pg.register.againPwd"/></label>
							<span class="error" id="error3"></span>
						</div>
						<div class="form-group form-group-list" style="width: 398px;margin-bottom: 5px;">
							<input type="text" data-value="0" class="form-control code" style="float: left;" name="validcode" id="validcode" onblur="checkValidcode();" />
							<label for="validcode" class="label-item"><fmt:message key="msg.pg.login.validcode"/></label>
							<img src="<%=basePath%>Captcha/CaptchaAction.do?<%=r%>"
								id="imgValidcode" onclick="RefreshCaptcha(this);" alt="<fmt:message key="msg.pg.login.changeOther"/>"/>
							<span class="error" id="error4"></span>
						</div>
						<div class="form-group form-group-list" style="margin-bottom: 5px;">
							<c:if test="${invid != '' && invid != null}">
								<input type="text" data-value="0" id="idd" name="id" class="form-control inputElem"  style="cursor: auto;" onMouseOver="this.style.border='1px solid #F1F3F4'" readonly/>
								<label for="idd" class="label-item" id="invitLabel" style="cursor: default;">邀请码（选填）</label>
							</c:if>
							<c:if test="${invid == '' || invid == null}">
								<input type="text" data-value="0" id="idd" name="id" class="form-control inputElem"/>
								<label for="idd" class="label-item" id="invitLabel">邀请码（选填）</label>
							</c:if>

						</div>
						<div class="form-group" style="margin-bottom: 5px;">
							<input type="checkbox" checked="checked" class="checked"
								name="checkbox" id="servicebox" onclick="isChecked();"/>
							<span class="checkbox-text"><fmt:message key="msg.pg.register.haveRead"/><a href="javascript:void(0)" onclick="window.open('<%=basePath %>register/rechargeService.do')">《<fmt:message key="msg.pg.footer.serviceAgreement"/>》</a> </span>
							<br/>
							<span class="error" id="errorservice"></span>
						</div>
						<div class="form-group-btn">
							<button type="submit" class="btn sub-btn" id="register_button"><fmt:message key="msg.pg.userFindPwd1.next"/></button>
						</div>
					</form>
				</div>
			</div>
		</div>

	<jsp:include page="_footer.jsp"></jsp:include>
	<script type="text/javascript" charset="utf-8" src="<%=basePath%>common/intlTelInput/intlTelInput.js"></script>
	<script type="text/javascript" charset="utf-8" src="<%=basePath%>common/intlTelInput/intlTelInput-city.js"></script>
	<script type="text/javascript">
		var message = {};
		message["zh_CN"] = {
			emailNull : "邮箱不能为空！",
			emailError : "邮箱格式错误！",
			emailAlreadyRegister : "该邮箱已注册,换个邮箱注册！",
			emailAvailable : "该邮箱可用！",
			phoneNull : "手机号不能为空！",
			phoneError : "手机号格式错误！",
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
			login: "立即登录",
			sliderValidation: "请按住滑块，拖动到最右边",
			validationPass: "验证通过",
			errorCkeck: "哎呀，出错了，点击",
			refurbishSlider: "刷新",
			againTry: "再来一次",
			notGelievabl: "网络不给力，请",
			ClickRefresh: "点击刷新",
			invitLabelStr: "邀请码（选填）",
			choseCode: "请选择国际手机区号！",
			choseCityCode: "请选择国家或地区！"
		};
		message["zh_HK"] = {
			emailNull : "郵箱不能為空！",
			emailError : "郵箱格式錯誤！",
			emailAlreadyRegister : "該郵箱已註冊,換個郵箱註冊！",
			emailAvailable : "該郵箱可用！",
			phoneNull : "手机号不能为空！",
			phoneError : "手机号格式错误！",
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
			login: "立即登錄",
			sliderValidation: "請按住滑塊，拖動到最右邊",
			validationPass: "驗證通過",
			errorCkeck: "哎呀，出錯了，點擊",
			refurbishSlider: "刷新",
			againTry: "再來一次",
			notGelievabl: "網路不給力，請",
			ClickRefresh: "點擊刷新",
			invitLabelStr: "邀請碼（選填）",
			choseCode: "請選擇國際手機區號！",
			choseCityCode: "請選擇國家或地區！"
		};
		message["en_US"] = {
			emailNull : "The email can not be empty !",
			emailError : "Mailbox format error !",
			emailAlreadyRegister : "This email has been registered! please change another one !",
			emailAvailable : "Available email !",
			phoneNull : "手机号不能为空！",
			phoneError : "手机号格式错误！",
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
			login: "Log in",
			sliderValidation: "Please hold down the slider and drag to the far right",
			validationPass: "Verification passed",
			errorCkeck: "Oops, something went wrong, click",
			refurbishSlider: "Refresh",
			againTry: "Again",
			notGelievabl: "The network is not strong, please",
			ClickRefresh: "Click refresh",
			invitLabelStr: "Invitation code (optional)",
			choseCode: "Please select an international area code!",
			choseCityCode: "Please select country or region！"
		};
		message["ja_JP"] = {
			emailNull : "メールを空にすることはできません！",
			emailError : "メールアドレスの形式が間違っています！",
			emailAlreadyRegister : "このメールアドレスは登録されています、他のメールアドレスで登録してください！",
			emailAvailable : "このメールアドレスは利用可能です！",
			phoneNull : "手机号不能为空！",
			phoneError : "手机号格式错误！",
			pwdNull : "パスワードは空欄になれません！",
			pwdLtEightChar : "パスワードが8文字以下！",
			pwdGtTwentyChar : "パスワードが20文字以上！",
			twicePwdNotEq : "パスワードが一致しません！",
			pwdAvailable : "パスワードが一致しません！",
			confirmPwdNull : "確認パスワードの欄は空欄にすることはできません！",
			confirmPwdAvailable : "確認パスワードが利用可能です！",
			codeNull : "認証コードが空欄になれません！",
			codeError : "認証コードが正しくありません！",
			agreementService : "先にサービス同意書を同意してから！",
			emailBoxError : "メールアドレスが間違っています！",
			login: "今すぐサインイン",
			sliderValidation: "一番右にドラッグ",
			validationPass: "検証に合格しましたs",
			errorCkeck: "間違った、クリック",
			refurbishSlider: "リフレッシュ",
			againTry: "また来て",
			notGelievabl: "ネットワークは強制しません",
			ClickRefresh: "更新をクリック",
			invitLabelStr: "招待コード(オプション)",
			choseCode: "Please select an international area code!",
			choseCityCode: "Please select country or region！"
		};
		message["ko_KR"] = {
			emailNull : "이메일  번호는 비워 둘 수 없습니다!",
			emailError : "우편함 형식이 잘못되었습니다!",
			emailAlreadyRegister : "메일 박스가 등록되었습니다. 다른 이메일을 등록하십시오!",
			emailAvailable : "이 사서함을 사용할 수 있습니다!",
			phoneNull : "手机号不能为空！",
			phoneError : "手机号格式错误！",
			pwdNull : "비밀번호는 비워 둘 수 없습니다!",
			pwdLtEightChar : "로그인 비밀번호가 8 자 미만입니다!",
			pwdGtTwentyChar : "로그인 비밀번호가 20 자 이상입니다!",
			twicePwdNotEq : "일치하지 않는 비밀번호!",
			pwdAvailable : "비밀번호를 사용할 수 있습니다!",
			confirmPwdNull : "비밀번호는 비워 둘 수 없습니다!",
			confirmPwdAvailable : "비밀번호가 사용 가능한지 확인하십시오!",
			codeNull : "인증 코드는 비워 둘 수 없습니다!",
			codeError : "인증 코드가 잘못되었습니다!",
			agreementService : "먼저 서비스 계약에 동의해야합니다!",
			emailBoxError : "이메일 오류!",
			login: "지금 로그인",
			sliderValidation: "슬라이더를 누른 채 오른쪽 끝으로 드래그하십시오",
			validationPass: "확인 통과",
			errorCkeck: "잘못 클릭",
			refurbishSlider: "새로 고침",
			againTry: "다시 오세요",
			notGelievabl: "네트워크는 강제하지 않습니다",
			ClickRefresh: "새로 고침 클릭",
			invitLabelStr: "요청 코드 (선택)",
			choseCode: "Please select an international area code!",
			choseCityCode: "Please select country or region！"
		};
		message["ru_RU"] = {
			emailNull : "Электронная почта не могут быть пустыми!",
			emailError : "Ошибка формата почтового ящика!",
			emailAlreadyRegister : "Эта электронная почта уже зарегистрирована! Пожалуйста, введите другой э-мейл !",
			emailAvailable : "Доступный э-мейл !",
			phoneNull : "手机号不能为空！",
			phoneError : "手机号格式错误！",
			pwdNull : "Пароль не может быть пустым!",
			pwdLtEightChar : "Пароль менее 8 символов!",
			pwdGtTwentyChar : "Пароль более 20 символов!",
			twicePwdNotEq : "Пароли не соответствуют друг другу !",
			pwdAvailable : "Доступный пароль !",
			confirmPwdNull : "Подтверждение пароля не может быть пустым !",
			confirmPwdAvailable : "Доступное подтверждение пароля !",
			codeNull : "Код аутентификации не может быть пустым!",
			codeError : "Пожалуйста, введите правильный код аутентификации!",
			agreementService : "Вы должны сначала согласиться с соглашением об обслуживании !",
			emailBoxError : "Ошибка электронной почты !",
			login: "Войти",
			sliderValidation: "Пожалуйста, удерживайте ползунок и перетащите в правый край. ",
			validationPass: "Проверка пройдена",
			errorCkeck: "Ой, что-то пошло не так, нажмите",
			refurbishSlider: "Обновить",
			againTry: "Еще раз",
			notGelievabl: "Сеть не стабильна, пожалуйста",
			ClickRefresh: "Нажмите обновить",
			invitLabelStr: "пригласительный код",
			choseCode: "Please select an international area code!",
			choseCityCode: "Please select country or region！"
		};
		var MSG = message[locale];
	</script>
	<script type="text/javascript">
		var rootpath="";
		var zzone = $("#areaCode").val();
		var choseCode = ${isEmail ? true : (zzone != '')};//是否选择国际区号
		var choseCityCode = false;//是否选择国家或者地区
		$(function() {
			loadProvice();
			// getAreaCodeNum();
			// getCuntryOrregion();
		});
		
		$(function() {
		    // 默认隐藏选择区域
		    $("#address-select").hide();
		    $("#address-link").toggle(function() {
		      $("#address-select").show();
		    }, function() {
		      $("#address-select").hide();
		    });
	  	});
	</script>

	</body>
<script type="text/javascript">
	var name = "${currCountryName }";
	var code = "${countryCode }";
	var isEmail = ${isEmail ? true : false};
	var a = false;
	function loadCountry() {
		$.get(basePath + "city/loadCountry.do", function(result){
    		result = eval(result);
    		for(var i = 0; i < result.length-1; i++) {
    			if(code != result[i].code) {
    				a = true;
    			}
    			if(code == result[i].code) {
    				return false;
    			}
    		}
    		if (a) {
    			$("#countrySelect").prepend("<option value="+ code +" >" +name+ "</option>");
    			loadProvice();
    		}
  		});
	}
	
	loadCountry();
	
	function getQueryVariable(variable) {
	       var query = window.location.search.substring(1);
	       var vars = query.split("&");
	       for (var i=0;i<vars.length;i++) {
	               var pair = vars[i].split("=");
	               if(pair[0] == variable){return pair[1];}
	       }
	       return(false);
	}

   $(".form-group .form-control[type=text],.form-group .form-control[type=password]").bind("input propertychange",function(event){
		$(this).attr("data-value",$(this).val().length);
   });
   
   $(function(){
	   var invid = getQueryVariable("invid");
	   if (!invid || invid == "") {
		   invid = localStorage.getItem('INVID_CODE');
	   } else {
		   localStorage.setItem('INVID_CODE', invid);
	   }
	   
	   if (invid && invid != "") {
		   $("#idd").val(invid);
		   $("#idd").attr("data-value", invid.length);
	   }
	   
	   var email = "${email}";
	   if ("" != email) {
		   ${isEmail ? 'checkEmail();' : 'checkPhone();'}
	   }
	   
	   $("#invitLabel").html(MSG['invitLabelStr']);
   });
	$("#areaCode").intlTelInput({
		//是否允许下拉
		allowDropdown: true,
		// if there is just a dial code in the input: remove it on blur, and re-add it on focus
		autoHideDialCode: true,
		// 添加在所选国家例数输入占位符
		autoPlaceholder: "polite",
		// 修改自动占位符
		customPlaceholder: null,
		// 附加菜单到特定元素
		dropdownContainer: "",
		// 不要显示这些国家
		excludeCountries: [],
		// 在初始化过程中格式化的输入的值
		formatOnDisplay: true,
		//更多的查找功能
		geoIpLookup: null,
		//初始国家
		initialCountry: "",
		// 不要插入国际拨号码
		nationalMode: true,
		//数字类型用于占位符
		placeholderNumberType: "MOBILE",
		// display only these countries
		onlyCountries: [],
		// 指定显示在列表顶部的国家。
		preferredCountries: [],
		//在选定的标记旁边显示国家拨号码，因此它不是键入编号的一部分
		separateDialCode: false,
		// 指定要启用验证/格式的一个专门用于脚本的路径
		utilsScript: ""
	});
	var code = $("#areaCode").intlTelInput("getSelectedCountryData")
	function getAreaCodeNum() {
		$("#areaCode").val(code.dialCode);
		choseCode = true;
	}
	// 选择国家或者地区
	$("#countryOrregion").intlTelInputCity({
		//是否允许下拉
		allowDropdown: true,
		// if there is just a dial code in the input: remove it on blur, and re-add it on focus
		autoHideDialCode: true,
		// 添加在所选国家例数输入占位符
		autoPlaceholder: "polite",
		// 修改自动占位符
		customPlaceholder: null,
		// 附加菜单到特定元素
		dropdownContainer: "",
		// 不要显示这些国家
		excludeCountries: ["af", "cn", "my", "us", ],
		// 在初始化过程中格式化的输入的值
		formatOnDisplay: true,
		//更多的查找功能
		geoIpLookup: null,
		//初始国家
		initialCountry: "",
		// 不要插入国际拨号码
		nationalMode: true,
		//数字类型用于占位符
		placeholderNumberType: "MOBILE",
		// display only these countries
		onlyCountries: [],
		// 指定显示在列表顶部的国家。
		preferredCountries: [],
		//在选定的标记旁边显示国家拨号码，因此它不是键入编号的一部分
		separateDialCode: false,
		// 指定要启用验证/格式的一个专门用于脚本的路径
		utilsScript: ""
	});
	var cityCode = $("#countryOrregion").intlTelInputCity("getSelectedCountryData")
	function getCuntryOrregion() {
		// console.log(cityCode.iso2);// 传给后台的值
		// console.log(cityCode.name);// 国家或地区
		$("#countryCodes").val(cityCode.iso2);
		$("#countryOrregion").attr( "placeholder", cityCode.name);
		$("#currCountryName").val(cityCode.name);
		choseCityCode = true;
	}
</script>
</html>
