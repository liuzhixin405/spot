<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.applyApi"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		
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
						<h2><fmt:message key="msg.pg.userCenter.applyApi"/></h2>
						<%-- <a class="rt"><fmt:message key="msg.pg.userCenter.applyApiDocs"/></a> --%>
					</div>
					<div class="api-notice">
						<fmt:message key="msg.pg.userCenter.applyApiTip"/>
					</div>
					<c:if test="${noAppkey }">
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
							<form action="<%=userPath %>doBindAppKey.do" class="form-wrap"
								method="post" onsubmit="return checkapiFlag();" id="mobileform" autocomplete="off"
								name="mobileform">
								<div class="form-group form-marb">
									<label for="" class="form-lab">
										<fmt:message key="msg.pg.userCenter.applyIpAddr"/>：
									</label>
									<input type="text" class="form-control form-inpt" id="allowIp" name="allowIp" onblur="checkIpRight()"  placeholder="<fmt:message key="msg.pg.userBindPhone.inputIpAddr"/>" />
									<div>
										<div class="lt margin-lt-lab">
											<span class="disblock-sp">1、<fmt:message key="msg.pg.userCenter.applyMuchIpAddr"/></span>
											<span class="disblock-sp">2、<fmt:message key="msg.pg.userCenter.applyWriteIpAddr"/></span>	
										</div>
										<div class="clear"></div>	
									</div>
									<label for="" class="form-lab">
									</label>
									<span class="error" id="iperror"></span>
								</div>
								<div class="form-group">
									<label for="" class="form-lab">
										<fmt:message key="msg.pg.userCenter.applyMothods"/>：
									</label>
									<input type="radio" checked="checked"/><em><fmt:message key="msg.pg.userCenter.applyphoneCode"/></em>
								</div>
								<c:if test="${tel==0}">
								<div class="form-group">
									<label for="" class="form-lab">
										<fmt:message key="msg.pg.userCenter.applyphoneUnb"/>：
									</label>
									<a href="<%=basePath%>center/bindPhone.do"><fmt:message key="msg.pg.userCenter.bind"/></a>
								</div>
								</c:if>
								<c:if test="${tel!=0}">
								<div class="form-group form-marb">
									<label for="" class="form-lab">
										<fmt:message key="msg.pg.userBindPhone.phoneNumber"/>：
									</label>
									<input type="hidden" name="mobile" id="mobile" value="${tel}" />
									<span>${fn:substring(tel,0,4)}*****${fn:substring(tel,9,11)}</span><br><br>
									<input type="text"  class="display-none" name="hidden1"/>
									<label for="" class="form-lab">
										<fmt:message key="msg.pg.userCenter.applySMScode"/>：
									</label>
										<input type="text"  class="form-control form-inpt" placeholder=""
											onblur="checkCode();" id="code" name="code"/>
									<div class="send-btn-box">
									<label for="" class="form-lab">
									</label>
										<input type="button" class="btn send-btn" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/><br>
									<label for="" class="form-lab">
									</label>
										<span class="error" id="error1"></span>
									</div>
								</div>
								</c:if>
								<c:if test="${payword!=1}">
								<div class="form-group">
									<label for="" class="form-lab">
										<fmt:message key="msg.pg.userCenter.applyUnTradePwd"/>：
									</label>
									<a href="<%=basePath%>password/tradePwd.do"><fmt:message key="msg.pg.userCenter.set"/></a>
								</div>
								</c:if>
								<c:if test="${payword==1}">
								<div class="form-group form-marb">
									<label for="" class="form-lab">
										<fmt:message key="msg.pg.trade.assetPwd"/>：
									</label>
										<input type="password"  class="form-control form-inpt" onblur="checkTradePwd()" autocomplete="new-password" placeholder="<fmt:message key="msg.pg.userSetTradePwd.inputTradePwd"/>"
											onblur="" id="paypwd" name="paypwd" /><br>
									<label for="" class="form-lab">
									</label>
										<span class="error" id="error2"></span>
									</div>
								</div>
								</c:if>
								<div class="form-group-btn">
									<button type="submit" 
										<c:if test="${payword!=1 || tel==0 }">
											disabled="disabled"	
										</c:if>
									class="btn sub-btn api-btn"><fmt:message key="msg.pg.userCenter.applyRequestApi"/></button>
								</div>
							</form>
						</div>
					</div>
					</c:if>
				</div>
				<c:if test="${!noAppkey }">
				<div class="main-rt-box mt10">
					<div class="trade-manage-main">
				        <table cellpadding="0" cellspacing="0">
				          <thead>
				            <tr>
				              <th><fmt:message key="msg.pg.userCenter.applyRequestTm"/></th>
				              <th><fmt:message key="msg.pg.userCenter.applyPublicKey"/></th>
				              <th>IP</th>
				              <th><fmt:message key="msg.pg.userCenter.overTime"/></th>
				              <th><fmt:message key="msg.pg.userCenter.status"/></th>
				              <th><fmt:message key="msg.pg.userCenter.option"/></th>
				            </tr>
				          </thead>
				          <tbody>
				          	<c:forEach var="l" items="${pm.datas }" varStatus="">
						        <tr>
						        	<td><ut:formatDate dateline="${l.update_time}"></ut:formatDate></td>
						        	<td>${fn:substring(l.app_key,0,5)}*******</td>
						        	<td style="word-break: break-word;">${l.allow_ip }</td>
						        	<td>
						        		<c:if test="${l.allow_ip == '' || l.allow_ip == null}"><ut:formatDate dateline="${l.over_time}"></ut:formatDate></c:if>
						        		<c:if test="${l.allow_ip != '' && l.allow_ip != null}"><fmt:message key="msg.pg.userCenter.applyNeverExpires"/></c:if>
						        	</td>
						        	<td>
						        		<c:if test="${l.state eq 1 }"><fmt:message key="msg.pg.userCenter.applyuseAble"/></c:if>
										<c:if test="${l.state eq 0 }"><fmt:message key="msg.pg.userCenter.applyuseUnable"/></c:if>
						        	</td>
						        	<td>
						        		<button onclick="deleteTip('${l.f_user_id}')" class="btn btn-mal"><fmt:message key="msg.pg.userCenter.applyDeleted"/></button>
						        		<button class="btn btn-mal showapi"><fmt:message key="msg.pg.userCenter.look"/></button>
						        	</td>
						        </tr>
						        <tr class="display-none showInfo">
						        	<td colspan="6" class="apply-api-detail">
						        		<em><fmt:message key="msg.pg.userCenter.applyApiPublic"/>：${l.app_key }</em><br>
						        		<em><fmt:message key="msg.pg.userCenter.applyBindIp"/>：	${l.allow_ip }</em>
						        	</td>
						        </tr>				
					        </c:forEach>
				          </tbody>
				        </table>
				        <%-- <ul class="pagination">
						  <pg:pager url="${pageN}" items="${pm.total}"
							maxIndexPages="10" maxPageItems="${pm.maxitems}"
							export="currentPageNumber=pageNumber">							
						
								<pg:prev> <li class="disabled">
									<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>	</li>
								</pg:prev>
						
						
								<pg:pages>
									<c:choose>
										<c:when test="${currentPageNumber eq pageNumber }">
											<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
										</c:when>
										<c:otherwise><li>
											<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}">${pageNumber}</a></li>
										</c:otherwise>
									</c:choose>
								</pg:pages>
								<pg:next>
								<li><a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.next"/></a></li>
							</pg:next>
						  </pg:pager>
						</ul> --%>
      				</div>	
				</div>
				</c:if>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
		<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
		<script type="text/javascript">
		$(function() {
			var appkey = "${appkey.app_key}";
			var appsecret = "${appkey.app_secret}";
			var allowIPaddr = "${appkey.allow_ip}";
			if (appsecret != null && appsecret != ""){
				showapi(appkey, appsecret, allowIPaddr);			
			}
			var valuenull="";								
			$("#btnCode").click(function(){
				var tel = $("#mobile").val();
					document.forms.mobileform.btnCode.disabled = true;
					$.post("<%=basePath%>center/sendOldTelCode.do", {Tel:tel}, function(n){
						if(n == "success"){
							for (i = 1; i <= secs; i++) {
								window.setTimeout("doUpdate(" + i + ")", i * 1000);
							}
						} else {
							tipAlert("<fmt:message key='msg.pg.userBindPhone.sendFail'/>");
							document.forms.mobileform.btnCode.disabled = false;
						}
					}, "text");
			});
		});

		var secs = 60;
		var wait = secs * 1000;

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
	
	var message = {};
	message["zh_CN"] = {
		codeNull : "验证码不能为空！",
		codeError : "请输入正确的验证码",
		lookOver: "查看",
		applyPickup: "收起",
		applyDelete: "删除",
		codeCorrect : "验证码正确",
		warmTip : "温馨提示",
		sureTip : "确定",
		cancel: "取消",
		sureDelete: "确定删除",
		ipCorrect: "IP格式正确",
		ipEorror: "IP格式不正确",
		applySuc: "申请成功!",
		dateTmNover: "1、您已申请了秘钥对，该秘钥对会永久生效。",
		dateTmNinty: "1、您已申请了秘钥对，该秘钥对会在90天后失效。到时可重新申请。",
		apisecrete: "2、私钥仅在申请成功后显示，遗失后不可找回，请妥善保管。",
		replyapisecrete: "3、如你忘记私钥，请删除该密钥对重新申请。",
		apiPublic: "API公钥：",
		apiPerson: "API私钥：",
		tradePwd: "交易密码不能为空",
		arrayIp: "一个密钥对对应IP个数不大于10",
	};
	message["zh_HK"] = {
		codeNull : "驗證碼不能為空！",
		codeError : "請輸入正確的驗證碼",
		lookOver: "查看",
		applyPickup: "收起",
		applyDelete: "刪除",
		codeCorrect : "驗證碼正確",
		warmTip : "溫馨提示",
		sureTip : "確定",
		cancel: "取消",
		sureDelete: "確定刪除",
		ipCorrect: "IP格式正確",
		ipEorror: "IP格式不正確",
		applySuc: "申請成功!",
		dateTmNover: "1、您已申請了秘鑰對，該秘鑰對會永久生效。",
		dateTmNinty: "1、您已申請了秘鑰對，該秘鑰對會在90天後失效。到時可重新申請。",
		apisecrete: "2、私鑰僅在申請成功後顯示，遺失後不可找回，請妥善保管。",
		replyapisecrete: "3、如你忘記私鑰，請刪除該密鑰對重新申請。",
		apiPublic: "API公鑰：",
		apiPerson: "API私鑰：",
		tradePwd: "交易密碼不能為空",
		arrayIp: "一個密鑰對對應IP個數不大於10",
	};
	message["en_US"] = {
		codeNull : "Verification code cannot be empty",
		codeError : "Please enter correct verify code",
		lookOver: "To view",
		applyPickup: "Pick up",
		applyDelete: "Delete",
		codeCorrect: "code is correct",
		warmTip : "Warm prompt",
		sureTip : "Sure",
		cancel: "Cancel",
		sureDelete: "Sure to delete",
		ipCorrect: "IP format is correct",
		ipEorror: "IP format is incorrect",
		applySuc: "application approved！",
		dateTmNover: "1. You have applied for a secret key pair, which will take effect permanently.",
		dateTmNinty: "1. You have applied for the secret key pair, which will expire after 90 days.You can then reapply.",
		apisecrete: "2. The private key is only displayed after the application is successful, and cannot be recovered after losing. Please keep it properly.",
		replyapisecrete: "3. If you forget your private key, please delete the key pair and reapply.",
		apiPublic: "API public key:",
		apiPerson: "API private key:",
		tradePwd: "The transaction password cannot be empty",
		arrayIp: "A key pair has no more than 10 IP addresses",
	};
	message["ja_JP"] = {
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
		lookOver: "チェック",
		applyPickup: "折りたたむ",
		applyDelete: "削除",
		codeCorrect : "認証コードは正確です",
		warmTip: "ヒント",
		sureTip: "確定",
		cancel: "キャンセル",
		sureDelete: "削除できました",
		ipCorrect: "IPフォーマットは正確です",
		ipEorror: "IPフォーマットが正しくありません",
		applySuc: "申請に成功しました！",
		dateTmNover: "1、您已申请了秘钥对，该秘钥对会永久生效。",
		dateTmNinty: "1、您已申请了秘钥对，该秘钥对会在90天后失效。到时可重新申请。",
		apisecrete: "2、私钥仅在申请成功后显示，遗失后不可找回，请妥善保管。",
		replyapisecrete: "3、如你忘记私钥，请删除该密钥对重新申请。",
		apiPublic: "APIパブリックキー：",
		apiPerson: "APIプライベートキー：",
		tradePwd: "取引パスワードは空欄になれません",
		arrayIp: "鍵ペアのIPアドレス数が10以下である",
	};
	message["ko_KR"] = {
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "올바른 인증 코드를 입력하십시오",
		lookOver: "보기",
		applyPickup: "접기",
		applyDelete: "삭제",
		codeCorrect : "인증 코드가 정확",
		warmTip : "팁",
		sureTip : "결정",
		cancel: "취소",
		sureDelete: "삭제 확인",
		ipCorrect: "IP 형식이 정확",
		ipEorror: "IP 형식이 잘못되었습니다",
		applySuc: "성공적인 신청!",
		dateTmNover: "1. 비밀 키 페어를 신청했으며 키 페어가 영구적으로 적용됩니다.",
		dateTmNinty: "1. 비밀 키 페어를 신청했으며 90 일 후에 만료됩니다. 도착하면 다시 신청할 수 있습니다.",
		apisecrete: "2. 개인 키는 응용 프로그램이 성공한 후에 만 표시되며, 분실 한 경우 검색 할 수 없으므로 안전하게 보관하십시오.",
		replyapisecrete: "3. 개인 키를 잊어 버린 경우 키 페어를 삭제하고 다시 적용하십시오.",
		apiPublic: "API 공개 키 :",
		apiPerson: "API 개인 키 :",
		tradePwd: "거래 비밀번호는 비워 둘 수 없습니다",
		arrayIp: "키 페어의 IP 주소 수가 10을 넘지 않아야합니다.",
	};
	message["ru_RU"] = {
		codeNull : "Код подтверждения не может быть пустым",
		codeError : "Пожалуйста, введите верный код подтверждения",
		lookOver: "К просмотру",
		applyPickup: "Забрать\Поднять",
		applyDelete: "Удалить",
		codeCorrect: "код правильный",
		warmTip : "Важное напоминание",
		sureTip : "Конечно",
		cancel: "Отменить",
		sureDelete: "Уверен(а) в удалении",
		ipCorrect: "Формат IP правильный",
		ipEorror: "Неверный формат IP",
		applySuc: "заявка утверждена！",
		dateTmNover: "1. Вы подали заявку на пару секретных ключей, которые будут действовать постоянно.",
		dateTmNinty: "1. Вы подали заявку на пару секретных ключей, срок действия которых истекает через 90 дней. Затем вы можете повторно подать заявку.",
		apisecrete: "2. Закрытый ключ отображается только после успешного завершения и не может быть восстановлен после потери. Пожалуйста, держите его в безопасности.",
		replyapisecrete: "3. Если вы забыли свой закрытый ключ, удалите пару ключей и повторно примените.",
		apiPublic: "Публичный ключ API:",
		apiPerson: "Приватный ключ API:",
		tradePwd: "Пароль транзакции не может быть пустым",
		arrayIp: "Пара ключей имеет не более 10 IP-адресов ",
	};
	var MSG = message[locale];
	
	$('.showapi').bind('click', function(){
            if($(this).html() == MSG['lookOver']){
                $('.showapi').html(MSG['lookOver']);
                 $(this).html(MSG['applyPickup']);
                $(".showInfo").hide("slow");
                $(this).parent().parent().next('tr').show("9999");
                return;
            }
            if($(this).html() == MSG['applyPickup']){
                $(this).html(MSG['lookOver']);
                 $(this).parent().parent().next("tr").hide("9999");
            }
        });
    
	var apicode_bol = true;
	var apicode_bol1 = false;
	var apicode_bol2 = false;
	    // 验证手机验证码
	function checkCode() {
		var tel = $("#mobile").val();
		var code = $("#code").val();
		var unLen = code.replace(/[^\x00-\xff]/g, "**").length;
		if (unLen == 0) {
			document.getElementById("error1").style.color = "red";
			$("#error1").html(MSG['codeNull']);
			apicode_bol1 = false;
			return false;
		}
		var path = getRootPath();
		$.post(path + 'center/checkTelCode.do', {code:code,tel:tel}, function(data) {
			if (data == "false") {
				document.getElementById("error1").style.color = "red";
				$("#error1").html(MSG['codeError']);
				apicode_bol1 = false;
				return false;
			} else {
				$("#error1").html(MSG['codeCorrect']);
				document.getElementById("error1").style.color = "green";
				apicode_bol1 = true;
				return true;
			}
		}, "text")
	}
	
	//验证交易密码
	function checkTradePwd() {
		var password = $("#paypwd").val();
		if(password.trim() == "") {
			$("#error2").html(MSG['tradePwd']);
			document.getElementById("error2").style.color = "red";
			apicode_bol2 = false;
			return false;	
		}
		if(password.trim() != "") {
			$("#error2").html("");
			apicode_bol2 = true;
			return true;	
		}
	}
	
	function checkapiFlag() {
		if (apicode_bol && apicode_bol1 && apicode_bol2) {
			return true;
		}
		return false;
	}
	
	function deleteTip(typeId) {
    	$("#alert-windows").show();
		var insidehtm = "<div id='tb-overlay-duiping' class='tb-overlay'></div><div class='add-wrap alert-wrap' style='display: block;' id='invite-BFX'>"
					+"<div class='dialogs-title'>"
					+"<h2 class='lt'>"
						+MSG['warmTip']
					+"</h2>"
					+"<a class='close-icon icons-close' id='close-alert' href='javascript:closealert()'></a>"
					+"</div>"
					+"<div class='dialogs-body'>"
					+"<p style='text-align: center; font-size: 16px; margin-bottom: 30px;'>"+ MSG['sureDelete'] +"</p>"
					+"<div class='form-group-btn text-center'>"
						+"<a class='btn sub-btn center' style='margin-right: 0px;background-color: #c55555;' href='javascript:closealert();'>"+MSG['cancel']+"</a>&emsp;"
						+"<a class='btn sub-btn center' style='margin-right: 0px;' href='"+"<%=basePath%>center/deleteAppKey.do?uid=" + typeId + "'>"+MSG['sureTip']+"</a>"
					+"</div>"
					+"</div>"
				+"</div>";
		$("#alert-windows").html(insidehtm);
		center($('.alert-wrap'));
	}
	
	function showapi(type1,type2,type3) {
    	$("#alert-windows").show();
    	if(type3 == "") {
    		var dateTm = MSG['dateTmNinty']
    	}
    	if(type3 != "") {
    		var dateTm = MSG['dateTmNover']
    	}
		var insidehtm = "<div id='tb-overlay-duiping' class='tb-overlay'></div><div class='add-wrap alert-wrap' style='display: block;' id='invite-BFX'>"
					+"<div class='dialogs-title'>"
					+"<h2 class='text-center'>"
						+MSG['applySuc']
					+"</h2>"
					+"<a class='close-icon icons-close' id='close-alert' href='javascript:closealert()'></a>"
					+"</div>"
					+"<div class='dialogs-body' style='line-height: 20px;padding:20px 0px;'>"
					+"<div style='padding: 20px 0px;background-color: #e5f5f9;'>"
					+"<p style='font-size: 16px;margin-left: 30px;'>" + dateTm + "</p>"
					+"<p style='font-size: 16px;margin-left: 30px;margin-top: 10px;'>" + MSG['apisecrete'] + "</p>"
					+"<p style='font-size: 16px;margin-left: 30px;margin-top: 10px;'>" + MSG['replyapisecrete'] + "</p>"
					+"</div>"
					+"<p style='font-size: 16px;margin-left: 30px;margin-top: 10px;color: #808080;'>"+ MSG['apiPublic'] + type1 +"</p>"
					+"<p style='font-size: 16px;margin-left: 30px;margin-top: 10px;color: #808080;'>"+ MSG['apiPerson'] + type2 +"</p>"
					+"<p style='font-size: 16px;margin: 10px 0px 10px 30px;color: #808080;'>IP："+ type3 +"</p>"
					+"<div class='form-group-btn text-center'>"
						+"<a class='btn sub-btn center' style='margin-right: 0px;' href='javascript:closealert();'>"+MSG['sureTip']+"</a>"
					+"</div>"
					+"</div>"
				+"</div>";
		$("#alert-windows").html(insidehtm);
		center($('.alert-wrap'));
	}
	
	function checkIpRight() {
		var ipRight = $("#allowIp").val();			
		var reg =  /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
		var ableinp = ipRight.split(',');
		if(ipRight == "") {
			apicode_bol = true;
			$("#iperror").html('');
			return true;
		} 
		if(ipRight != "") {
			if(ableinp.length > 10) {
				$("#iperror").html(MSG['arrayIp']);
				document.getElementById("iperror").style.color = "red";
				apicode_bol = false;
				return false;	
			}else {
				for(let i = 0;i < ableinp.length;i++) {
				  if(reg.test(ableinp[i])){
				  	continue;		  
				  }else {
				  	$("#iperror").html(MSG['ipEorror']);
					document.getElementById("iperror").style.color = "red";
					apicode_bol = false;
					return false;	
				  }
				}
				$("#iperror").html(MSG['ipCorrect']);
				document.getElementById("iperror").style.color = "green";
				apicode_bol = true;
				return true;	
			}	
		} 
		$("#iperror").html(MSG['ipEorror']);
		document.getElementById("iperror").style.color = "red";
		apicode_bol = false;
		return false;
	}
	
</script>
	</body>
</html>
