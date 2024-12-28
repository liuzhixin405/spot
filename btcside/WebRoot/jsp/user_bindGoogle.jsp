<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userBindGoogle.title"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		
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
          <h2>
           <fmt:message key="msg.pg.userBindGoogle.title"/></h2>
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
		 <form action="<%=basePath%>center/bindGoogledo.do" class="form-wrap" method="post" id="mobileform" 
		  		onsubmit="return ${tyle == 0 ? 'checkEmailGoog()' : 'checkCodeGoog()'}" name="mobileform">
              <p style="font-size:16px;font-weight:700;margin-bottom:15px;">1.<fmt:message key="msg.pg.userBindGoogle.note1"/></p>
              <div style="margin-bottom:15px;">
				<img src="<%=basePath%>Captcha/google.do?email=${email}&secret=${secretKey}" width="200px" height="200px"/>
       			<p style="margin-top:5px"><fmt:message key="msg.pg.userBindGoogle.secretKey"/>: <b class="sell">${secretKey}</b>
				<input type="hidden" id="secretKey" name="secretKey" value="${secretKey}"/>
              </div>
             
              <div class="form-group">
                <label for=""><fmt:message key="msg.pg.userBindGoogle.device"/></label>
                <input type="text" placeholder="<fmt:message key="msg.pg.userBindGoogle.inputLoginPwd"/>" readonly="true" value="Btcside:${email}"/>
                <span class="error"></span>
              </div>
              <c:if test="${tyle==1||tyle==3}">
	              <div class="form-group">
	                <label for=""><fmt:message key="msg.pg.userCenter.phoneVaidcode"/></label>
	                <input type="hidden" id="mobile" name="mobile" value="${mobile}"/>
	                <input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entercellCode"/>" onblur="checkCode();"  id="code" name="code"/>
	                <span class="error"  id="error1"></span>
	                <div class="send-btn-box">
	                  <input type="button" class="btn send-btn" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"></input>
	                </div>
	              </div>
              </c:if>
              <c:if test="${tyle==0}">
	              <div class="form-group">
	                <label for=""><fmt:message key="msg.pg.userCenter.emailCode"/></label>
	                <input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.inputEmailCode"/>" onblur="checkEmailCode();"  id="emailCode" name="emailCode"/>
	                <span class="error"  id="error2"></span>
	                <div class="send-btn-box">
	                  <input type="button" class="btn send-btn"  id="sendEmailCode" name="sendEmailCode" value="<fmt:message key="msg.pg.userCenter.getEmailCode"/>"></input>
	                </div>
	              </div>
              </c:if>
              <p style="font-size:16px;font-weight:700;margin-bottom:15px;">2.<fmt:message key="msg.pg.userBindGoogle.note2"/></p>
              <div class="form-group">
                <label for=""><fmt:message key="msg.pg.userCenter.googleCode"/></label>
                <input type="text" class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entergoogleCode"/>" onblur="checkGoogle();"  id="google" name="google"/>
                <span class="error" id="googleerror"></span>
              </div>
              <div class="form-group-btn">
                <button type="submit" class="btn sub-btn"><fmt:message key="msg.pg.userBind.confirmBind"/></button>
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
	$("#btnCode").click(function() {
		document.forms.mobileform.btnCode.disabled = true;
		$.post("<%=basePath%>center/sendOldTelCode.do", {}, function(n){
			if(n == "success"){
				for (i = 1; i <= secs; i++) {
					window.setTimeout("doUpdate(" + i + ")", i * 1000);
				}
			} else {
				tipAlert(n);
				document.forms.mobileform.btnCode.disabled = false;
			}
		}, "text");
	});
	
	$("#sendEmailCode").click(function(){
		document.forms.mobileform.sendEmailCode.disabled = true;
		$.post("<%=basePath%>center/sendEmailCode.do", {}, function(n){
			if(n == "success"){
				for (i = 1; i <= secs; i++) {
					window.setTimeout("doUpdateEmail(" + i + ")", i * 1000);
				}
			} else {
				tipAlert(n);
				document.forms.mobileform.sendEmailCode.disabled = false;
			}
		}, "text");
	});
    
    var secs = 60;
    function doUpdate(num) {
		if (num == '60') {
			document.forms.mobileform.btnCode.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
			document.forms.mobileform.btnCode.disabled = false;
		} else {
			document.forms.mobileform.btnCode.disabled = true;
			var wut = secs - num;
			document.forms.mobileform.btnCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
		}
	}
    
    function doUpdateEmail(num) {
		if (num == '60') {
			document.forms.mobileform.sendEmailCode.value = "发送邮箱验证码";
			document.forms.mobileform.sendEmailCode.disabled = false;
		} else {
			document.forms.mobileform.sendEmailCode.disabled = true;
			var wut = secs - num;
			document.forms.mobileform.sendEmailCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";		
		}
	}
</script> 
</body>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		codeNull : "验证码不能为空！",
		codeError : "请输入正确的验证码",
		warmTip : "温馨提示",
		sureTip : "确定"
	};
	message["zh_HK"] = {
		codeNull : "驗證碼不能為空！",
		codeError : "請輸入正確的驗證碼",
		warmTip : "溫馨提示",
		sureTip : "確定"
	};
	message["en_US"] = {
		codeNull : "Verification code cannot be empty",
		codeError : "Please enter correct verify code",
		warmTip : "Tips",
		sureTip : "OK"
	};
	message["ja_JP"] = {
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
		warmTip : "ヒント",
		sureTip : "OK"
	};
	message["ko_KR"] = {
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "올바른 인증 코드를 입력하십시오",
		warmTip : "팁",
		sureTip : "결정"
	};
	message["ru_RU"] = {
		codeNull : "Код подтверждения не может быть пустым",
		codeError : "Пожалуйста, введите правильный код подтверждения ",
		warmTip : "Подсказки",
		sureTip : "Ок"
	};
	var MSG = message[locale];
</script>
</html>
