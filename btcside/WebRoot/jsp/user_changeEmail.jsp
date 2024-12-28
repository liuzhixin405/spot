<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userChangeEmail.title"/></title>
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
                <h2><fmt:message key="msg.pg.userChangeEmail.title"/>
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
                    <form action="<%=basePath %>center/changeEmaildo.do" class="form-wrap" method="post" onsubmit="return checkChangePhone();" id="changeEmailform" autocomplete="off" name="changeEmailform">
                        <div class="form-group">
                            <label for="">
                                <fmt:message key="msg.changeEmail.request"/>
                            </label>
                            <div>
                                <input type="hidden" name="emali" id="oldEmail" value="${emali}"/>
                                <input type="text" style="float: left;" class="form-control" placeholder="<fmt:message key="msg.changeEmail.inputOldCode"/>" onblur="changeSandsEmailCode();" id="emailCode" name="oldEmailCode" />
                                <span class="error" style="float: left; line-height: 37px; margin-left: 10px;" id="emailCodeError2"></span>
                                <div class="clear"></div>
                            </div>
                            <div class="send-btn-box">
                                <input type="button" class="btn send-btn" id="changeEmailCode" name="changeEmailCode" value="<fmt:message key="msg.changeEmail.catchOldCode"/>"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="">
                                <fmt:message key="msg.changeEmail.newCode"/>
                            </label>
                            <div>
                                <input type="text" style="float: left;" class="form-control" placeholder="<fmt:message key="msg.changeEmail.inputNewCode"/>" id="emailNew" name="newEmail" onblur="changeBindEmail();"/>
                                <span class="error" style="float: left; line-height: 37px; margin-left: 10px;" id="emailNewError"></span>
                                <div class="clear"></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="">
                                <fmt:message key="msg.changeEmail.NewEmailCode"/>
                            </label>
                            <div>
                                <input type="text" style="float: left;" class="form-control" placeholder="<fmt:message key="msg.changeEmail.inputTheNewCode"/>"
                                       onblur="changeSandEmailCode();" id="changeCodeNew" name="newEmailCode" />
                                <span class="error" style="float: left; line-height: 37px; margin-left: 10px;" id="changeErrorNew"></span>
                                <div class="clear"></div>
                            </div>
                            <div class="send-btn-box">
                                <input type="button" class="btn send-btn" id="changeNewEmailCode" name="changeNewEmailCode" value="<fmt:message key="msg.changeEmail.catchNewCode"/>"/>
                            </div>
                        </div>
                        <div class="form-group-btn">
                            <button type="submit" id="find_btn" class="btn sub-btn" style="width: 187px;height: 42px;"><fmt:message key="msg.pg.userChange.confirmChange"/></button>
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
        //发送旧邮箱验证码
        $("#changeEmailCode").click(function(){  //发送邮箱验证码
            document.forms.changeEmailform.changeEmailCode.disabled = true;
            $.post("<%=basePath%>center/sendOldEmailCode.do", {}, function(n){
                if(n == "success"){
                    for (i = 1; i <= secs; i++) {
                        window.setTimeout("doUpdate1(" + i + ")", i * 1000);
                    }
                } else {
                    tipAlert(n);
                    document.forms.changeEmailform.changeEmailCode.disabled = false;
                }
            }, "text");
        });
        //发送新邮箱验证码
        $("#changeNewEmailCode").click(function(){  //发送邮箱验证码
            var Email = $('#emailNew').val();
            document.forms.changeEmailform.changeNewEmailCode.disabled = true;

            $.post("<%=basePath%>center/sendEmailCodeBeforeBindEmail.do", {email:Email}, function(n){
                if(n == "success"){
                    for (i = 1; i <= secs; i++) {
                        window.setTimeout("doUpdate(" + i + ")", i * 1000);
                    }
                } else {
                    tipAlert(n);
                    document.forms.changeEmailform.changeNewEmailCode.disabled = false;
                }
            }, "text");
        });
    });
    var secs = 60;
    var wait = secs * 1000;

    function doUpdate1(num) {
        if (num == '60') {
            document.forms.changeEmailform.changeEmailCode.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
            document.forms.changeEmailform.changeEmailCode.disabled = false;
        } else {
            document.forms.changeEmailform.changeEmailCode.disabled = true;
            var wut = secs - num;
            document.forms.changeEmailform.changeEmailCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
            window.setTimeout("doUpdate1(" + (num + 1) +  ")", 1000);
        }
    }

    function doUpdate(num) {
        if (num == '60') {
            document.forms.changeEmailform.changeNewEmailCode.disabled = false;
            document.forms.changeEmailform.changeNewEmailCode.value.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
        } else {
            document.forms.changeEmailform.changeNewEmailCode.disabled = true;
            var wut = secs - num;
            document.forms.changeEmailform.changeNewEmailCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
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
<script type="text/javascript">
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
        emailNull : "邮箱不能为空！",
        emailError : "邮箱格式错误！",
        login: "立即登录",
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
        emailNull : "郵箱不能為空！",
        emailError : "郵箱格式錯誤！",
        login: "立即登錄",
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
        emailNull : "The email can not be empty !",
        emailError : "Email name format error !",
        login: "Log in",
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
        emailNull : "電子メールを空にすることはできません",
        emailError : "メールフォーマットエラー！",
        login: "今すぐサインイン",
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
        emailNull : "이메일은 비워 둘 수 없습니다!",
        emailError : "이메일 형식 오류!",
        login: "지금 로그인",
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
        emailNull : "E-mail не может быть пустым!",
        emailError : "Ошибка формата электронной почты!",
        login: "Войти",
        sliderValidation: "Пожалуйста, удерживайте ползунок и перетащите в край в право. ",
        validationPass: "Верификация\проверка пройдена",
    };
    var MSG = message[locale];

    //提交修改前调用滑块
    function checkChangePhone() {
        if (email_code_bol && email_change_bol && email_new_bol) {
            return true;
        }
    }
</script>
</html>
