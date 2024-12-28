<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userChangePhone.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/intlTelInput/intlTelInput.css"/>
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
						<h2><fmt:message key="msg.pg.userChangePhone.title"/>
						</h2>
					</div>
					<div class="box">

						<div class="control">
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
							<form action="<%=basePath %>center/changePhonedo.do" class="form-wrap"
								method="post" onsubmit="return checkChangePhone();" id="registerform" autocomplete="off"
								name="registerform">
								<p style="font-size: 16px; font-weight: 700; margin-bottom: 15px; color: #f00;">
									<fmt:message key="msg.pg.userChangePhone.note1"/>
									<a href="<%=orderPath %>orderList.do" rel="nofollow" target="_blank" style="color: #0171c7;"><fmt:message key="msg.pg.userChangePhone.onlineOrder"/></a>
									<input type="hidden" name="mobile" id="mobile" value="${tel}"/>
								</p>
								<div class="form-group" style="margin-bottom: 35px;">
									<label for="">
										<fmt:message key="msg.pg.userChangePhone.oldPhoneCode"/>
									</label>
									<div>
										<input type="text" style="float: left;" class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entercellCode"/>" onblur="checkCode(true);" id="code" name="code" />
										<span class="error" style="float: left; line-height: 37px; margin-left: 10px;" id="error1"></span>
										<div class="clear"></div>
									</div>
									<div class="send-btn-box">
										<input type="button" class="btn send-btn" id="btnCode1" name="btnCode1" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/>
									</div>
								</div>

								<div class="form-group" style="margin-bottom: 35px;">
									<label for="">
										<fmt:message key="msg.pg.userBindPhone.phoneNumber"/>
									</label>
									<div>
										<input type="text" autocomplete="off" style="width: 120px !important;height: 50px !important;float: left;margin-right: 20px;display: inline-block;transition: none;cursor: default;border: 1px solid #F1F3F4;text-align: center;padding: 0;text-indent: 0;" data-value="0" value="" id="areaCode" name="zone" class="form-control-zone inputElem" placeholder="<fmt:message key="msg.pg.userCenter.choseCode"/>"/>
										<input type="text" style="float: left;width: 256px;display: inline-block;" class="form-control" placeholder="<fmt:message key="msg.pg.userBindPhone.inputNumber"/>" id="mobilenew" name="mobilenew" onblur="checkTel(true);"/>
										<span class="error" style="float: left; line-height: 37px; margin-left: 10px;" id="mobileError"></span>
										<div class="clear"></div>
									</div>
								</div>
								<div class="form-group" style="margin-bottom: 35px;">
									<label for="">
										<fmt:message key="msg.pg.userCenter.phoneVaidcode"/>
									</label>
									<div>
										<input type="text" style="float: left;" class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entercellCode"/>"
											onblur="checkCodeNew();" id="codenew" name="codenew" />
										<span class="error" style="float: left; line-height: 37px; margin-left: 10px;" id="errornew"></span>
									<div class="clear"></div>
									</div>
									<div class="send-btn-box">
										<input type="button" class="btn send-btn" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/>
									</div>
								</div>
								<div class="form-group-btn">
									<button type="submit" id="find_btn" class="btn sub-btn" style="width: 191px;"><fmt:message key="msg.pg.userChange.confirmChange"/></button>
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
			<%-- 旧手机--%>
			$(function(){   
				$("#btnCode1").click(function(){
					document.forms.registerform.btnCode1.disabled = true;
					$.post("<%=basePath%>center/sendOldTelCode.do", {}, function(n){
						if(n == "success"){
							window.setTimeout("doUpdate1(1)", 1000);
						} else {
							tipAlert(n);
							document.forms.registerform.btnCode1.disabled = false;
						}
					}, "text");
				});
			});
			var secs = 60;
			var wait = secs * 1000;

			function doUpdate1(num) {
				if (num == '60') {
					document.forms.registerform.btnCode1.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
					document.forms.registerform.btnCode1.disabled = false;
				} else {
					document.forms.registerform.btnCode1.disabled = true;
					var wut = secs - num;
					document.forms.registerform.btnCode1.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";	
					window.setTimeout("doUpdate1(" + (num + 1) +  ")", 1000);
				}
			}

			<%-- 新手机--%>
			$(function(){
				$("#btnCode").click(function(){
					var tel = $("#mobilenew").val();
					var zone = $("#areaCode").val();

					if (checkTel(true)) {
						document.forms.registerform.btnCode.disabled = true;
						$.post("<%=basePath%>center/sendTelCodeBeforeBindTel.do", {Tel:tel, zone: zone}, function(n){
							if(n == "success"){
								window.setTimeout("doUpdate(1)", 1000);
							} else {
								tipAlert(n);
								document.forms.registerform.btnCode.disabled = false;
							}
						}, "text");
					}
				});
			});
			
			function doUpdate(num) {
				if (num == '60') {
					document.forms.registerform.btnCode.disabled = false;
					document.forms.registerform.btnCode.value.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
				} else {
					document.forms.registerform.btnCode.disabled = true;
					var wut = secs - num;
					document.forms.registerform.btnCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
					window.setTimeout("doUpdate(" + (num + 1) +  ")", 1000);
				}
			}
			
			if ('${success}' !='') {
				window.setTimeout("redirectToCenter()", 3000);
			}
			
			function redirectToCenter() {
				window.location.href="/center/accountSafe.do";
			}
			
		</script>
	</body>
	<script type="text/javascript" charset="utf-8" src="<%=basePath%>common/intlTelInput/intlTelInput.js"></script>
<script type="text/javascript">
<%--	$(function() {--%>
<%--		getAreaCodeNum();--%>
<%--	});--%>
	var message = {};
	message["zh_CN"] = {
		phoneError : "号码错误",
		phoneRegistered : "该号码已注册,换个号码注册",
		phoneAvailable : "该号码可用",
		phoneNull : "电话不能为空！",
		codeNull : "验证码不能为空！",
		codeError : "请输入正确的验证码",
		warmTip : "温馨提示",
		sureTip : "确定",
		sliderValidation: "请按住滑块，拖动到最右边",
		validationPass: "验证通过",
	};
	message["zh_HK"] = {
		phoneError : "號碼錯誤",
		phoneRegistered : "該號碼已註冊,換個號碼註冊",
		phoneAvailable : "該號碼可用",
		phoneNull : "電話不能為空！",
		codeNull : "驗證碼不能為空！",
		codeError : "請輸入正確的驗證碼",
		warmTip : "溫馨提示",
		sureTip : "確定",
		sliderValidation: "請按住滑塊，拖動到最右邊",
		validationPass: "驗證通過",
	};
	message["en_US"] = {
		phoneError : "Incorrect phone number",
		phoneRegistered : "The number has been registered, change another one please",
		phoneAvailable : "Available phone number",
		phoneNull : "The phone number cannot be empty",
		codeNull : "Verification code cannot be empty",
		codeError : "Please enter correct verify code",
		warmTip : "Warm prompt",
		sureTip : "Sure",
		sliderValidation: "Please hold down the slider and drag to the far right",
		validationPass: "Verification passed",
	};
	message["ja_JP"] = {
		phoneError : "エラー番号",
		phoneRegistered : "この電話番号は登録されています、他の電話番号で登録してください",
		phoneAvailable : "この電話番号は利用可能です",
		phoneNull : "電話番号を必ず入力してください！",
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
		warmTip: "ヒント",
		sureTip: "確定",
		sliderValidation: "一番右にドラッグ",
		validationPass: "検証に合格しましたs",
	};
	message["ko_KR"] = {
		phoneError : "잘못된 번호",
		phoneRegistered : "이미 등록 된 번호입니다. 등록 할 번호를 변경하십시오",
		phoneAvailable : "이 번호는 사용할 수 있습니다",
		phoneNull : "전화는 비워 둘 수 없습니다!",
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "올바른 인증 코드를 입력하십시오",
		warmTip : "팁",
		sureTip : "결정",
		sliderValidation: "슬라이더를 누른 채 오른쪽 끝으로 드래그하십시오",
		validationPass: "확인 통과",
	};
	message["ru_RU"] = {
		phoneError : "Неверный номер телефона",
		phoneRegistered : "Данный номер уже зарегистрирован, введите другой номер телефона",
		phoneAvailable : "Доступный номер телефона",
		phoneNull : "Номер телефона не может быть пустым",
		codeNull : "Код подтверждения не может быть пустым",
		codeError : "Пожалуйста, введите правильный код подтверждения",
		warmTip : "подсказка ",
		sureTip : "Конечно",
		sliderValidation: "Пожалуйста, удерживайте ползунок и перетащите в край в право. ",
		validationPass: "Верификация\проверка пройдена",
	};
	var MSG = message[locale];

	var choseCode = false;//是否选择国际区号

	function checkChangePhone() {
		if (tel_bol && code_bol) {
			return true;
		}
	}

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
		initialCountry: "cn",
		// 不要插入国际拨号码
		nationalMode: true,
		//数字类型用于占位符
		placeholderNumberType: "MOBILE",
		// display only these countries
		onlyCountries: [],
		// 指定显示在列表顶部的国家。
		preferredCountries: [ "cn", "us", "gb","jp", "kr", "ru"],
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
</script>
</html>
