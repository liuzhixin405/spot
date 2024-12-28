<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.bindEmail"/></title>
    <link type="text/css" rel="stylesheet"
          href="<%=basePath%>common/css/style.css?<%=rjs%>" />
    <script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>
    <script type="text/javascript" src="<%=basePath %>common/js/jquery.1.7.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
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
                <h2><fmt:message key="msg.pg.userCenter.bindEmail"/>
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
                    <form action="<%=basePath%>center/bindEmaildo.do" class="form-wrap"
                          method="post" onsubmit="return checkBindPhone();" id="bindEmailform"
                          name="bindEmailform">
                        <div class="form-group">
                            <label for="">
                                <fmt:message key="msg.pg.activity.mailbox"/>
                            </label>
                            <div>
                                <input type="text" style="float: left;" class="form-control" placeholder="<fmt:message key="msg.pg.userBindPhone.inputEmail"/>"
                                       id="bindEmail" name="email" autocomplete="off" onblur="checkbindEmail();"/>
                                <span class="error" style="float: left; line-height: 37px; margin-left: 10px;" id="bindError"></span>
                                <div class="clear"></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="">
                                <fmt:message key="msg.pg.userCenter.emailCode"/>
                            </label>
                            <input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.inputEmailCode"/>" id="emailCode" name="emailCode" onblur="checkSandEmailCode();"/>
                            <span class="error" id="error2"></span>
                            <div class="send-btn-box">
                                <input type="button" class="btn send-btn" id="sendsEmailCode" name="sendsEmailCode" value="<fmt:message key="msg.pg.userCenter.getEmailCode"/>"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="">
                                <fmt:message key="msg.pg.userCenter.phoneVaidcode"/>
                            </label>
                            <input type="hidden" id="mobile" name="mobile" value="${tel}"/>
                            <input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entercellCode"/>"
                                   onblur="checkCode();" id="code" name="code" />
                            <span class="error" id="error1"></span>
                            <div class="send-btn-box">
                                <input type="button" class="btn send-btn" id="sendBtnCode" name="sendBtnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/>
                            </div>
                        </div>
                        <div class="form-group-btn">
                            <button type="submit" id="find_btn" class="btn sub-btn" style="width: 187px;height: 42px;"><fmt:message key="msg.pg.userBind.confirmBind"/></button>
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
    $(function() {
        var valuenull="";
        $("#sendBtnCode").click(function(){  //发送手机验证码
            var tel = $("#mobile").val();
            // if (checkTel(false)) {
                document.forms.bindEmailform.sendBtnCode.disabled = true;
                $.post("<%=basePath%>center/sendOldTelCode.do", {Tel:tel}, function(n){
                    if(n == "success"){
                        for (i = 1; i <= secs; i++) {
                            window.setTimeout("doUpdate(" + i + ")", i * 1000);
                        }
                    } else {
                        tipAlert(n);
                        document.forms.bindEmailform.sendBtnCode.disabled = false;
                    }
                }, "text");
            // }
        });

        $("#sendsEmailCode").click(function(){  //发送邮箱验证码
            var Email = $('#bindEmail').val();
            document.forms.bindEmailform.sendsEmailCode.disabled = true;

            $.post("<%=basePath%>center/sendEmailCodeBeforeBindEmail.do", {email:Email}, function(n){
                if(n == "success"){
                    for (i = 1; i <= secs; i++) {
                        window.setTimeout("doUpdateEmail(" + i + ")", i * 1000);
                    }
                } else {
                    tipAlert(n);
                    document.forms.bindEmailform.sendsEmailCode.disabled = false;
                }
            }, "text");
        });

    });

    var secs = 60;
    var wait = secs * 1000;

    function doUpdate(num) {
        if (num == '60') {
            document.forms.bindEmailform.sendBtnCode.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
            document.forms.bindEmailform.sendBtnCode.disabled = false;
        } else {
            document.forms.bindEmailform.sendBtnCode.disabled = true;
            var wut = secs - num;
            document.forms.bindEmailform.sendBtnCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
        }
    }

    function doUpdateEmail(num) {
        if (num == '60') {
            document.forms.bindEmailform.sendsEmailCode.value = "发送邮箱验证码";
            document.forms.bindEmailform.sendsEmailCode.disabled = false;
        } else {
            document.forms.bindEmailform.sendsEmailCode.disabled = true;
            var wut = secs - num;
            document.forms.bindEmailform.sendsEmailCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
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
        emailNull : "邮箱不能为空！",
        emailError : "邮箱格式错误！",
        phoneError : "号码错误",
        phoneRegistered : "该号码已注册,换个号码注册",
        phoneAvailable : "该号码可用",
        phoneNull : "电话不能为空！",
        codeNull : "验证码不能为空！",
        codeError : "请输入正确的验证码",
        warmTip : "温馨提示",
        sureTip : "确定",
        login: "立即登录",
        sliderValidation: "请按住滑块，拖动到最右边",
        validationPass: "验证通过",
    };
    message["zh_HK"] = {
        emailNull : "郵箱不能為空！",
        emailError : "郵箱格式錯誤！",
        phoneError : "號碼錯誤",
        phoneRegistered : "該號碼已註冊,換個號碼註冊",
        phoneAvailable : "該號碼可用",
        phoneNull : "電話不能為空！",
        codeNull : "驗證碼不能為空！",
        codeError : "請輸入正確的驗證碼",
        warmTip : "溫馨提示",
        sureTip : "確定",
        login: "立即登錄",
        sliderValidation: "請按住滑塊，拖動到最右邊",
        validationPass: "驗證通過",
    };
    message["en_US"] = {
        emailNull : "The email can not be empty !",
        emailError : "Email name format error !",
        phoneError : "Incorrect phone number",
        phoneRegistered : "The number has been registered, change another one please",
        phoneAvailable : "Available phone number",
        phoneNull : "The phone number cannot be empty",
        codeNull : "Verification code cannot be empty",
        codeError : "Please enter correct verify code",
        login: "Log in",
        sliderValidation: "Please hold down the slider and drag to the far right",
        validationPass: "Verification passed",
    };
    message["ja_JP"] = {
        emailNull : "メールの欄は空欄にすることはできません！",
        emailError : "メールアドレスの形式が間違っています！",
        phoneError : "エラー番号",
        phoneRegistered : "この電話番号は登録されています、他の電話番号で登録してください",
        phoneAvailable : "この電話番号は利用可能です",
        phoneNull : "電話番号を必ず入力してください！",
        codeNull : "認証コードを必ず入力してください！",
        codeError : "正しい認証コードを入力してください",
        login: "今すぐサインイン",
        sliderValidation: "一番右にドラッグ",
        validationPass: "検証に合格しましたs",
    };
    message["ko_KR"] = {
        emailNull : "로그인 이름은 비워 둘 수 없습니다!",
        emailError : "로그인 이름이 잘못된 형식입니다!",
        phoneError : "잘못된 번호",
        phoneRegistered : "이미 등록 된 번호입니다. 등록 할 번호를 변경하십시오",
        phoneAvailable : "이 번호는 사용할 수 있습니다",
        phoneNull : "전화는 비워 둘 수 없습니다!",
        codeNull : "인증 코드는 비워 둘 수 없습니다!",
        codeError : "올바른 인증 코드를 입력하십시오",
        warmTip : "팁",
        sureTip : "결정",
        login: "지금 로그인",
        sliderValidation: "슬라이더를 누른 채 오른쪽 끝으로 드래그하십시오",
        validationPass: "확인 통과",
    };
    message["ru_RU"] = {
        emailNull : "Имя пользователя не может быть пустым !",
        emailError : "Ошибка формата имени пользователя !",
        phoneError : "Неверный номер телефона",
        phoneRegistered : "Данный номер уже зарегистрирован, введите другой номер телефона ",
        phoneAvailable : "Доступный номер телефона",
        phoneNull : "Номер телефона не может быть пустым",
        codeNull : "Код подтверждения не может быть пустым",
        codeError : "Пожалуйста, введите правильный код подтверждения",
        warmTip : "Подсказки",
        sureTip : "Ок",
        login: "Войти",
        sliderValidation: "Пожалуйста, удерживайте ползунок и перетащите в край в право. ",
        validationPass: "Верификация\проверка пройдена",
    };
    var MSG = message[locale];

    function checkBindPhone() {
        if (email_bind_bol && code_bol && email_bol) {
            return true;
        }
    }
</script>
</html>
