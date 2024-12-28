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
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script>
<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
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
      <input type="text" class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entergoogleCode"/>" id="google" name="google">
      <span class="error" id="googleerror"></span>
      </div>
      <div class="form-group">
        <label for=""><fmt:message key="msg.pg.trade.assetPwd"/></label>
        <input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.enterassetPwd"/>" name="password" id="password" autocomplete="new-password" onblur="checkTradePwd();" />
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
		<div class="block-title" style="border-bottom: 0;">
		  <h2><fmt:message key="msg.pg.userCenter.myAseets"/> </h2>
		</div>
		<div class="block-title-combine">
			<ul class="combine-menu-ul">
				<li class="${actionName=='assets'  ? 'current' : ''}">
					<a href="<%=userPath %>assets.do"> <span></span><fmt:message key="msg.pg.head.bibiAcount"/></a>
				</li>
<%--				<li class="${actionName=='otc'  ? 'current' : ''}">--%>
<%--						<a href="<%=userPath %>otc.do"> <span>OTC</span><fmt:message key="msg.pg.workerOrderAdd.account"/></a>--%>
<%--				</li>--%>
<%--				<li class="${actionName=='center'  ? 'current' : ''}">--%>
<%--					<a href="<%=userPath %>center.do"> <span></span>USDT<fmt:message key="msg.pg.head.AccountContract"/></a>--%>
<%--				</li>--%>
<%--				<li class="${actionName=='usdr'  ? 'current' : ''}">--%>
<%--					<a href="<%=userPath %>nCenter/usdr.do"> <span>USDR</span><fmt:message key="msg.pg.head.AccountContract"/></a>--%>
<%--				</li>--%>
				<%--<li class="${actionName=='dCenter'  ? 'current' : ''}">
					<a href="<%=userPath %>dCenter.do"> <span></span><fmt:message key="msg.pg.head.AccountDContract"/></a>
				</li>--%>
				<%--<li class="${actionName=='vCenter'  ? 'current' : ''}">
					<a href="<%=userPath %>vCenter.do"> <span></span><fmt:message key="msg.pg.userCenter.simulAseets"/></a>
				</li>--%>
				
			</ul>
		</div>
      <div class="info-box">
      	<input type="hidden" id="actionName" name="actionName" value="${actionName}">
        <ul class="info-list">
			<li style="width: 100%;">
	          <div> 
	          	<b><fmt:message key="msg.pg.userCenter.acountTalassets"/><fmt:message key="msg.pg.userCenter.zhehe"/></b> <br>
	          	<span> 
		          	<em id="acountTalassetsShow">
			            <fmt:formatNumber value="${depositAndYk + freez + availableBtc + gift + floatGainLoss}" pattern="#0.0000">
			            </fmt:formatNumber>
		            </em>
		            <c:if test="${actionName eq 'vCenter'}">
		            BFC
		            </c:if>
		            <c:if test="${actionName eq 'center'}">
		            USDT
		            </c:if>
		            <c:if test="${actionName eq 'usdr'}">
		            USDT
		            </c:if>
	            </span>
	            <span style="font-size: 18px;"> ≈<em><fmt:formatNumber value="${totalCny}" pattern="#0.00"></fmt:formatNumber> </em>CNY</span>
	          </div> 
          </li>
          <li>
	          <b><fmt:message key="msg.pg.head.bibiAcountSubtitle"/>
	          <c:if test="${actionName eq 'vCenter'}">
	            BFC
	            </c:if>
	            <c:if test="${actionName eq 'center'}">
	            USDT
	            </c:if>
	            <c:if test="${actionName eq 'usdr'}">
	            USDR
	            </c:if>
	          </b> <br>
	          <span> <em>
	            <fmt:formatNumber value="${account}" pattern="#0.0000">
	            </fmt:formatNumber>
	            </em>
	            <c:if test="${actionName eq 'vCenter'}">
	            BFC
	            </c:if>
	            <c:if test="${actionName eq 'center'}">
	            USDT
	            </c:if>
	            <c:if test="${actionName eq 'usdr'}">
	            USDR
	            </c:if>
	          </span>
          </li>
          <li> 
	          <b><fmt:message key="msg.pg.userCenter.areFrozee"/></b> <br>
	          <span> <em>
	            <fmt:formatNumber value="${freez}" pattern="#0.0000">
	            </fmt:formatNumber>
	            </em>
	            <c:if test="${actionName eq 'vCenter'}">
	            BFC
	            </c:if>
	            <c:if test="${actionName eq 'center'}">
	            USDT
	            </c:if>
	            <c:if test="${actionName eq 'usdr'}">
	            USDR
	            </c:if>
	          </span> 
          </li>
          <li> 
	          <b><fmt:message key="msg.pg.userCenter.holeDeposte"/></b><br> 
	          <span> <em>
	            <fmt:formatNumber value="${deposit}" pattern="#0.0000">
	            </fmt:formatNumber>
	            </em>
	            <c:if test="${actionName eq 'vCenter'}">
	            BFC
	            </c:if>
	            <c:if test="${actionName eq 'center'}">
	            USDT
	            </c:if>
	            <c:if test="${actionName eq 'usdr'}">
	            USDR
	            </c:if>
	          </span> 
          </li>
          <li> 
	          <b><fmt:message key="msg.pg.trade.floatProfit"/></b> <br>
	          <span class="${floatGainLoss < 0 ? 'sell' : 'buy' }"> <em class="${floatGainLoss < 0 ? 'sell' : 'buy' }">
	            <fmt:formatNumber value="${floatGainLoss}" pattern="#0.0000">
	            </fmt:formatNumber>
	            </em>
	            <c:if test="${actionName eq 'vCenter'}">
	            BFC
	            </c:if>
	            <c:if test="${actionName eq 'center'}">
	            USDT
	            </c:if>
	            <c:if test="${actionName eq 'usdr'}">
	            USDR
	            </c:if>
	          </span> 
          </li>
          <li> 
	          <b><fmt:message key="msg.pg.userCenter.totalOfContract"/></b> <br>
	          <span> <em>
	            <fmt:formatNumber value="${availableBtc}" pattern="#0.0000">
	            </fmt:formatNumber>
	            </em>
	            <c:if test="${actionName eq 'vCenter'}">
	            BFC
	            </c:if>
	            <c:if test="${actionName eq 'center'}">
	            USDT
	            </c:if>
	            <c:if test="${actionName eq 'usdr'}">
	            USDR
	            </c:if>
	          </span> 
          </li>
        </ul>
      </div>
    </div>
    <div class="main-wrap">
     <%--  <div class="block-title" style="border: none;">
        <h2><fmt:message key="msg.pg.userCenter.assetsDetails"/> </h2>
      </div> --%>
      <div class="block-hidden-search">
	      <div class="hidden-search-item">
	        <input type="checkbox" class="hidden-asset-detail" id="hidden-asset-detail">
	      	<div class="hidden-asset-detail-text"><fmt:message key="msg.pg.userCenter.assetsDetailsHidden"/></div>
	      </div>
	      <i class="iconfont icon-sousuo asset-detail-search-icon"></i>	
	      <input class="asset-detail-search" id="asset-detail-search" onkeyup="searchChange()" placeholder="<fmt:message key="msg.pg.index.search"/>">
      </div>
      <div class="trade-manage-main">
        <table cellpadding="0" cellspacing="0" id="table-details">
          <thead>
            <tr>
              <th><fmt:message key="msg.pg.userCenter.nameContract"/></th>
              <%-- <th><fmt:message key="msg.pg.userCenter.totalHoldpos"/></th> --%>
              <th><fmt:message key="msg.pg.userCenter.muchHoldpos"/></th>
              <th><fmt:message key="msg.pg.userCenter.emptyHoldpos"/></th>
              <%-- <th><fmt:message key="msg.pg.userCenter.delegateTol"/></th> --%>
              <th><fmt:message key="msg.pg.userCenter.muchEntrue"/></th>
              <th><fmt:message key="msg.pg.userCenter.emptyEntrue"/></th>
              <th><fmt:message key="msg.pg.userCenter.curtAviableContract"/></th>
              <%-- <th><fmt:message key="msg.pg.userCenter.contractValue"/></th> --%>
              <th width="150px"><fmt:message key="msg.pg.trade.option"/></th>
            </tr>
          </thead>
          <tbody id="tickerTbody">
            
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
<jsp:include page="user_transfer.jsp"></jsp:include>
<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script> 
<script type="text/javascript" src="<%=basePath%>common/js/userCenter.js?<%=rjs%>"></script> 
<script type="text/javascript">
		// 点击不输入弹出身份确认窗口
  $(function() {
		var sfyz = $("#add-wrap-sfyz");
		var sfyzzz = $("#tb-overlay-sfyz");
    $("#no-input").click(function() {
	     show(sfyz);
	     sfyzzz.show();
      $(".close-alert").click(function() {
       hide(sfyz);
	     sfyzzz.hide();
      });
    });


  });
  </script> 
<script type="text/javascript">
			$(function(){
				$("#btnCode").click(function(){
					var tel = $("#mobile").val();
					$.post("<%=basePath%>center/sendOldTelCode.do",{Tel:tel}, function(n){
						if(n == "success"){
						//	alert("发送成功，请查收");
						} else {
							alert("<fmt:message key='msg.pg.userCenter.errorSend'/>！");
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
		transfer : "资金划转",
		rollOut : "转出",
		goTotrade: "去交易",
		amountError : "金额不正确",
		amountInsufficient : "余额不足",
		transferNum : "转账数量错误",
		amounttrsNull : "数量不能为0或空",
		warmTip: "温馨提示",
		sureTip: "确定",
		riskPause: "风控系统暂停",
		transferOut: "合约转出功能",
		transferIn: "合约转入功能",
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
		transfer : "資金劃轉",
		rollOut : "轉出",
		goTotrade: "去交易",
		amountError : "金額不正確",
		amountInsufficient : "餘額不足",
		transferNum : "轉賬數量錯誤",
		amounttrsNull : "數量不能為0或空",
		warmTip: "溫馨提示",
		sureTip: "確定",
		riskPause: "風控系統暫停",
		transferOut: "合約轉出功能",
		transferIn: "合約轉入功能",
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
		transfer : "Transfer",
		rollOut : "Transfer out",
		goTotrade: "Go to trade",
		amountError : "incorrect amount",
		amountInsufficient : "Insufficient balance",
		transferNum : "Wrong amount",
		amounttrsNull : "Quantity can't be zero or empty",
		warmTip : "Warm prompt",
		sureTip: "Sure",
		riskPause: "Risk control system paused ",
		transferOut: " contract transfer out",
		transferIn: " contract transfer in",
	};
	message["ja_JP"] = {
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
		tradePwdNull : "資金パスワードを空欄にすることはできません",
		tradePwdLtEightChar : "資金パスワードが8文字以下です",
		tradePwdGtTwentyChar : "資金パスワードが20文字を超えています",
		pwdError : "正しいパスワードを入力してください",
		setNoTradePwdSuccess : "資金パスワードが正常に入力されていないことを設定",
		setNeedTradePwdSuccess : "資金パスワードの入力に成功しました",
		setNeedTradePwdFail : "資金パスワードの入力に失敗しました",
		transfer : "資金の振り替え",
		rollOut : "転出",
		goTotrade: "取引",
		amountError : "金額が正しくありません",
		amountInsufficient : "残高不足",
		transferNum : "振込数が間違っています",
		amounttrsNull : "数量を0または空欄にすることはできません",
		warmTip: "ヒント",
		sureTip: "確定",
		riskPause: "リスト管理システムは一時停止する",
		transferOut: "契約転出機能",
		transferIn: "契約転入機能",
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
		transfer : "자금 조달",
		rollOut : "전송",
		goTotrade: "거래하기",
		amountError : "금액이 잘못되었습니다",
		amountInsufficient : "잔액 부족",
		transferNum : "잘못된 금액",
		amounttrsNull : "수량은 0이거나 비어있을 수 없습니다",
		warmTip: "팁",
		sureTip: "결정",
		riskPause: "에어 컨트롤 시스템 중단",
		transferOut: "계약 롤아웃 기능",
		transferIn: "계약 이체 기능",
	};
	message["ru_RU"] = {
		codeNull : "Код подтверждения не может быть пустым",
		codeError : "Пожалуйста, введите правильный код подтверждения",
		tradePwdNull : "Фондовый пароль не может быть пустым",
		tradePwdLtEightChar : "Фондовый пароль содержит менее чем 8 символов",
		tradePwdGtTwentyChar : "Фондовый пароль содержит более чем 20 символов",
		pwdError : "Пожалуйста, введите правильный пароль",
		setNoTradePwdSuccess : "Вход без ввода фондового пароля успешно установлен!",
		setNeedTradePwdSuccess : "Вход с вводом фондового пароля успешно установлен!",
		setNeedTradePwdFail : "Произошла ошибка при установки входа без фондового пароля!",
		transfer : "Перевод",
		rollOut : "Перевести",
		goTotrade: "Перейти к торговле",
		amountError : "неверное количество",
		amountInsufficient : "Недостаточный баланс",
		transferNum : "Неверное количество",
		amounttrsNull : "Количество не может быть нулевым или пустым",
		warmTip : "подсказка",
		sureTip: "Конечно",
		riskPause:  "Система контроля рисков приостановлена",
		transferOut: "перевод контракта",
		transferIn: "перевод контракта в",
	};
	var MSG = message[locale];
</script>
</html>
