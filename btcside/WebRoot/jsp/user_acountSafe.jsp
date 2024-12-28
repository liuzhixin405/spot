<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Pragma" content="no-cache"/>
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Expires" content= "0"/>
<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.center"/></title>
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>"/>
<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
<div class="add-windows" id="alert-windows"></div>
<!-- 身份验证 -->
<div class="add-windows">
  <div id="tb-overlay-sfyz" class="tb-overlay display-none"></div>
  <div class="add-wrap" style="top:200px;" id="add-wrap-sfyz">
    <div class="dialogs-title">
      <h2 class="lt"><fmt:message key="msg.pg.userCenter.authenticate"/></h2>
      <a href="javascript:" class="close-icon icons-close close-alert" id=""></a> </div>
    <div class="dialogs-body" >
    <form action="javascript:" class="form-wrap" id="mobileform" method="post" name="mobileform" autocomplete="off">
      <div class="form-group" style="${tel!=0? '' : 'display:none' }">
        <label for=""><fmt:message key="msg.pg.userCenter.phoneVaidcode"/>
          <input type="hidden" name="mobile" id="mobile" value="${tel}" />
        </label>
        <input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entercellCode"/>" onblur="checkCode();" id="code" name="code" >
        <span class="error" id="error1"></span>
        <div class="send-btn-box">
          <input type="button" class="btn send-btn" onclick="send(1)" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/>
        </div>
      </div>
      <div class="form-group"  class="form-group" style="${google!=0 ? '' : 'display:none' }">
      <label for=""><fmt:message key="msg.pg.userCenter.googleCode"/></label>
      <input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entergoogleCode"/>" id="google" name="google">
      <span class="error" id="googleerror"></span>
      </div>
      <div class="form-group">
        <label for=""><fmt:message key="msg.pg.trade.assetPwd"/></label>
        <input type="password" class="form-control" autocomplete="new-password" placeholder="<fmt:message key="msg.pg.userCenter.enterassetPwd"/>" name="password" id="password" onblur="checkTradePwd();" />
        <span class="error" id="error3"></span> </div>
      <div class="form-group-btn">
        <button type="submit"  class="btn sub-btn" onclick="return changTradeNoInput();"><fmt:message key="msg.pg.userCenter.confirmSubmit"/></button>
      </div>
    </form>
  </div>
</div>
</div>
<jsp:include page="_top.jsp"></jsp:include>
<jsp:include page="_head.jsp"></jsp:include>
<div class="container center-cont">
  <jsp:include page="_user_left.jsp"></jsp:include>
  <div class="main-rt-box">
  	<div class="main-wrap">
      <div class="block-title">
        <h2> <fmt:message key="msg.pg.userCenter.basicInfo"/> </h2>
      </div>
      <div class="info-box" style="margin: 0 auto;text-align: center;padding-top: 30px;">
        <ul class="info-list">
          <li class="li-block" style="float: none;"><img alt="user-acount" src="../common/images/user-acount.png"></li>
          <li class="li-block" style="float: none;"><%-- <b>VIP<fmt:message key="msg.pg.userCenter.VIPrank"/>：</b> --%> <!--这里动态显示vip等级 icons1-vip[序号：随等级发生变化] --> 
            <span><i class="iconfont icon-dizhi"></i>${area}</span>
            <span>${email}</span>
            <span class="vip-status">
             <%-- <em class="active-color">${name}</em> --%>
            <c:if test="${sessionScope.userinfo.f_user_level_id==1}"><em class="icons-vip0"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==2}"><em class="icons-vip1"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==3}"><em class="icons-vip2"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==4}"><em class="icons-vip3"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==5}"><em class="icons-vip4"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==6}"><em class="icons-vip5"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==7}"><em class="icons-vip6"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==8}"><em class="icons-vip7"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==9}"><em class="icons-vip8"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==10}"><em class="icons-vip9"></em></c:if>
			<c:if test="${sessionScope.userinfo.f_user_level_id==11}"><em class="icons-vip10"></em></c:if>
			<em class="active-color">${name}</em>
             <%-- <strong class="vip-width"><i class="vip-icon" style="width:${rate}%;"></i> </strong> <em>${name2}</em> --%>
            </span>
          </li>
          <%-- <li class="li-block"> <b><fmt:message key="msg.pg.index.loginEmail"/>：</b> <span>${email}</span> </li>
          <li class="li-block"> <b><fmt:message key="msg.pg.userCenter.location"/>：</b> <span>${area}</span> </li> --%>
        </ul>
      </div>
    </div>
    <div class="main-wrap">
      <div class="block-title">
        <h2> <fmt:message key="msg.pg.userCenter.acountSafe"/> </h2>
      </div>
      <ul class="safety-box">
        <li>
          <div class="li-bg">
            <b><fmt:message key="msg.pg.index.loginPwd"/>：</b>
            <span>********</span>
            <em><a href="<%=basePath%>password/changePwd.do"><fmt:message key="msg.pg.userCenter.amend"/></a></em>
          </div>
          <div class="bg-status yes"> <em class="yes"><fmt:message key="msg.pg.userCenter.enabled"/></em> </div>
        </li>
        <c:if test="${isSub != 1 }">
        <li>
          <div class="li-bg">
            <b><fmt:message key="msg.pg.userCenter.bindEmail"/>：</b>
            <span><c:if test="${realEmail!=null}">${realEmail}</c:if></span>
            <c:if test="${realEmail==null || realEmail==''}"><em><a href="<%=basePath%>center/bindEmail.do"><fmt:message key="msg.pg.userCenter.bind"/></a></em></c:if>
            <c:if test="${realEmail!=null && realEmail!=''}"><em><a href="<%=basePath%>center/changeEmail.do"><fmt:message key="msg.pg.userCenter.amend"/></a></em></c:if>
          </div>
          <div class="bg-status ${(realEmail!=null && realEmail!='') ? 'yes' : 'no'}">
            <em class="${(realEmail!=null && realEmail!='') ? 'yes' : 'no'}">
          	  <c:if test="${realEmail!=null && realEmail!=''}"><fmt:message key="msg.pg.userCenter.enabled"/></c:if>
          	  <c:if test="${realEmail==null || realEmail==''}"><fmt:message key="msg.pg.userCenter.unabled"/></c:if>
            </em>
          </div>
        </li>
        <li>
          <div class="li-bg"> <b><fmt:message key="msg.pg.userCenter.bindCell"/>：</b> <span>
            <c:if test="${tel!=0}"> ${fn:substring(tel,0,4)}*****${fn:substring(tel,9,11)} <em><a href="<%=basePath%>center/changePhone.do"><fmt:message key="msg.pg.userCenter.amend"/></a> </em> </c:if>
            <c:if test="${tel==0}"> <em><a href="<%=basePath%>center/bindPhone.do"><fmt:message key="msg.pg.userCenter.bind"/></a> </em> </c:if>
            </span> </div>
          <c:if test="${tel!=0}">
            <div class="bg-status yes"> <em class="yes"><fmt:message key="msg.pg.userCenter.enabled"/></em> </div>
          </c:if>
          <c:if test="${tel==0}">
            <div class="bg-status no"> <em class="no"><fmt:message key="msg.pg.userCenter.unabled"/></em> </div>
          </c:if>
        </li>
        </c:if>
        <li>
          <div class="li-bg"> <b><fmt:message key="msg.pg.userCenter.googleAuthcate"/>：</b>
            <c:if test="${google==0 && isSub != 1}"> <em><a href="<%=basePath%>center/bindGoogle.do"><fmt:message key="msg.pg.userCenter.bind"/></a> </em> </c:if>
            <c:if test="${google==0 && isSub == 1}"> <em><fmt:message key="msg.pg.subUser.subBindGoogle"/> </em> </c:if>
          </div>
          <c:if test="${google!=0}">
            <div class="bg-status yes"> <em class="yes"><fmt:message key="msg.pg.userCenter.enabled"/></em> </div>
          </c:if>
          <c:if test="${google==0}">
            <div class="bg-status no"> <em class="no"><fmt:message key="msg.pg.userCenter.unabled"/></em> </div>
          </c:if>
        </li>
        <c:if test="${isSub != 1 }">
        <li>
          <div class="li-bg"> <b><fmt:message key="msg.pg.trade.assetPwd"/>：</b>
            <c:if test="${payword==1}"> <span>********</span> <em><a href="<%=basePath%>password/findTradePwddo.do"><fmt:message key="msg.pg.index.forgetPwd"/>？</a> </em> <em><a href="<%=basePath%>password/changeTradePwd.do"><fmt:message key="msg.pg.userCenter.amend"/></a> </em> </c:if>
            <c:if test="${payword!=1}"> <em><a href="<%=basePath%>password/tradePwd.do"><fmt:message key="msg.pg.userCenter.set"/></a> </em> </c:if>
          </div>
          <c:if test="${payword==1}">
            <div class="bg-status yes"> <em class="yes"><fmt:message key="msg.pg.userCenter.enabled"/></em> </div>
          </c:if>
          <c:if test="${payword!=1}">
            <div class="bg-status no"> <em class="no"><fmt:message key="msg.pg.userCenter.unabled"/></em> </div>
          </c:if>
        </li>
<%--        <c:if test="${payword==1}">
        <li>
            <div class="li-bg" style="width: 96%;" > <b><fmt:message key="msg.pg.userCenter.enterFunPwd"/>：</b>
              <c:if test="${checked==1}">
             	<span style="margin-left: 50px;">
                	<input type="radio" name="only" id="inInput" onclick="input();" checked="checked" />
                	<label for="inInput"> <fmt:message key="msg.pg.userCenter.input"/> </label>
                </span> 
                <span style="margin-left: 50px;">
                	<input type="radio" name="only" id="noInput" onclick="noInput();"/>
                	<label for="noInput"> <fmt:message key="msg.pg.userCenter.inputNull"/> </label>
                </span> 
              </c:if>
              <c:if test="${checked!=1}"> 
              	<span style="margin-left: 50px;">
                	<input type="radio" name="only" id="inInput" onclick="input();"/>
                	<label for="inInput"> <fmt:message key="msg.pg.userCenter.input"/> </label>
                	</span>
                <span style="margin-left: 50px;">
                	<input type="radio" name="only" id="noInput" checked="checked" onclick="noInput();"/>
                	<label for="noInput"> <fmt:message key="msg.pg.userCenter.inputNull"/> </label>
                </span> 
              </c:if>
            </div>
        </li>
        </c:if>--%>
        </c:if>
<%--        <li>--%>
<%--            <div class="li-bg"><b><fmt:message key="msg.pg.userCenter.bfxFeeRate"/>：</b>--%>
<%--            	<c:if test="${feeRateType == 1}"><em><a href="javascript:;" class="account-control" onclick="changeFeeRateType(0)"><fmt:message key="msg.pg.trade.close"/></a></em></c:if>--%>
<%--            	<c:if test="${feeRateType != 1}"><em><a href="javascript:;" class="account-control" onclick="changeFeeRateType(1)"><fmt:message key="msg.pg.market.chartOn"/></a></em></c:if>--%>
<%--            </div>--%>
<%--            <c:if test="${feeRateType == 1}">--%>
<%--            	<div class="bg-status yes"><em class="yes"><fmt:message key="msg.pg.userCenter.enabled"/></em></div>--%>
<%--            </c:if>--%>
<%--            <c:if test="${feeRateType != 1}">--%>
<%--            	<div class="bg-status yes"><em class="no"><fmt:message key="msg.pg.userCenter.unabled"/></em></div>--%>
<%--            </c:if>--%>
<%--        </li>	--%>
      </ul>
    </div>
  </div>
</div>
<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script> 
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script> 
<script type="text/javascript">
	function input() {
		var flag = true;
		var msg="<fmt:message key='msg.pg.userCenter.whichEnterPwd'/>？";
		tipAlert(msg);
		var alertWin = document.getElementById("alert-windows");
		alertWin.addEventListener("click", function(event) {
			var tars = event.target || event.srcElement;
			if(flag) {
				if(tars.innerHTML != '') {
					changTradeInInput();
					flag = false;
				}									
			}
		});
		
		$("#close-alert").click(function() {
			window.location.reload();
		});
	}
	
	function noInput() {
		var sfyz = $("#add-wrap-sfyz");
		var sfyzzz = $("#tb-overlay-sfyz");
		show(sfyz);
		sfyzzz.show();
		$(".close-alert").click(function() {
			hide(sfyz);
			sfyzzz.hide();
			window.location.reload();
		});
		
		return false;
	}
	
	function changeFeeRateType(type) {
		$.post("<%=basePath%>center/changeFeeRateType.do", {type: type}, function(n){
			if(n == "success"){
				tipAlert(MSG['operSuccess']);
				var alertWin = document.getElementById("alert-windows");
				alertWin.addEventListener("click", function(event) {
					window.location.reload();
				});
			} else {
				tipAlert("<fmt:message key='msg.pg.userBindPhone.sendFail'/>");
			}
		}, "text");
	}
</script> 
<script type="text/javascript">
			$(function(){
				$("#btnCode").click(function(){
					var tel = $("#mobile").val();
					$.post("<%=basePath%>center/sendOldTelCode.do",{Tel:tel}, function(n){
						if(n == "success"){
						//	alert("发送成功，请查收");
						} else {
							tipAlert("<fmt:message key='msg.pg.userCenter.errorSend'/>！");
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
							document.forms.mobileform.btnCode.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
						}else{
							document.forms.mobileform.btnCode.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/>";
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
		tradePwdNull : "资金密码不能为空",
		tradePwdLtEightChar : "资金密码小于8个字符",
		tradePwdGtTwentyChar : "资金密码超过20个字符",
		pwdError : "请输入正确的密码",
		setNoTradePwdSuccess : "设置资金密码不输入成功",
		setNeedTradePwdSuccess : "设置资金密码输入成功",
		setNeedTradePwdFail : "设置资金密码输入失败",
		warmTip : "温馨提示",
		operSuccess : "操作成功！",
		sureTip : "确定",
	};
	message["zh_HK"] = {
		codeNull : "驗證碼不能為空！",
		codeError : "請輸入正確的驗證碼",
		tradePwdNull : "資金密碼不能為空",
		tradePwdLtEightChar : "資金密碼小於8個字元",
		tradePwdGtTwentyChar : "資金密碼超過20個字元",
		pwdError : "請輸入正確的密碼",
		setNoTradePwdSuccess : "設置資金密碼不輸入成功",
		setNeedTradePwdSuccess : "設置資金密碼輸入成功",
		setNeedTradePwdFail : "設置資金密碼輸入失敗",
		warmTip : "溫馨提示",
		operSuccess : "操作成功！",
		sureTip : "確定",
	};
	message["en_US"] = {
		codeNull : "Verification code cannot be empty",
		codeError : "Please enter correct verify code",
		tradePwdNull : "The Fund Password cannot be blank",
		tradePwdLtEightChar : "The Fund Password is less than 8 characters",
		tradePwdGtTwentyChar : "The Fund Password is more than 20 characters",
		pwdError : "Please enter correct password",
		setNoTradePwdSuccess : "Set no need to enter fund password successfully!",
		setNeedTradePwdSuccess : "Set need to enter fund password successfully!",
		setNeedTradePwdFail : "Set no need to enter fund password failure!",
		warmTip : "Warm prompt",
		operSuccess : "Successful!",
		sureTip : "Sure"
	};
	
	message["ja_JP"] = {
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
		tradePwdNull : "資金パスワードを空欄にすることはできません",
		tradePwdLtEightChar : "ファンドパスワードが8文字未満です",
		tradePwdGtTwentyChar : "資金パスワードが20文字を超えています",
		pwdError : "正しいパスワードを入力してください",
		setNoTradePwdSuccess : "ファンドパスワードが正しく入力されていません",
		setNeedTradePwdSuccess : "資金パスワードの入力に成功しました",
		setNeedTradePwdFail : "資金パスワードの入力に失敗しました",
		warmTip: "ヒント",
		operSuccess : "操作成功！",
		sureTip: "確定",
	};
	message["ko_KR"] = {
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "올바른 인증 코드를 입력하십시오",
		tradePwdNull : "자금 비밀번호는 비워 둘 수 없습니다",
		tradePwdLtEightChar : "펀드 비밀번호는 8 자 미만입니다.",
		tradePwdGtTwentyChar : "펀드 비밀번호가 20자를 초과합니다",
		pwdError : "올바른 비밀번호를 입력하십시오",
		setNoTradePwdSuccess : "펀드 비밀번호가 성공적으로 입력되지 않도록 설정",
		setNeedTradePwdSuccess : "펀드 비밀번호를 성공적으로 입력하도록 설정",
		setNeedTradePwdFail : "자금 조달 비밀번호 입력 실패",
		warmTip : "팁",
		operSuccess : "성공적인 운영!",
		sureTip : "결정",
	};
	message["ru_RU"] = {
		codeNull : "Код подтверждения не может быть пустым",
		codeError : "Пожалуйста, введите правильный код подтверждения",
		tradePwdNull : "Фондовый пароль не может быть пустым",
		tradePwdLtEightChar : "Пароль фонда содержит менее 8 символов ",
		tradePwdGtTwentyChar : "Пароль фонда содержит более чем 20 символов ",
		pwdError : "Пожалуйста, введите правильный пароль",
		setNoTradePwdSuccess : "Вход без ввода фондового пароля успешно установлен!",
		setNeedTradePwdSuccess : "Вход с вводом фондового пароля успешно установлен!",
		setNeedTradePwdFail : "Произошла ошибка при установки входа без фондового пароля!",
		warmTip : "Важное напоминание\подсказка ",
		operSuccess : "Успешно!",
		sureTip : "Конечно"
	};
	var MSG = message[locale];
</script>
</html>
