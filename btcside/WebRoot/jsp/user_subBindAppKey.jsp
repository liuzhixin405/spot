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
		
		<!-- 安全提示 -->
		<c:if test="${showtel != 1 && showgoogle != 1}">
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
		
		<div class="black_overlay" id="black_overlay"></div>
		<!-- 编辑弹框 -->
		<div id="editShowBox" class="editShowBox" >
			<form id="editShowBoxForm" name="editShowBoxForm" method="post">
				<div class="editShowBox-wrap">
					<h2><fmt:message key="msg.pg.userCenter.modificationApi"/></h2>
					<input type="hidden" id="mobile2" name="mobile2" value="${tel}"/>
					<img onclick="closeEditBox()" alt="closesubAccount" src="../common/images/close_pages.png">
					<div class="editShowBox-input">
						<span><fmt:message key="msg.pg.userCenter.subAccount"/></span>
						<input readonly="readonly" type="text" autocomplete="off" style="background-color: #f8f8f8;" value="${subUser.f_user_email}">
						<span class="error" id="error2"></span>
					</div>
					<div class="editShowBox-input-ipAddress">
						<span><fmt:message key="msg.pg.userCenter.applyIpAddr"/></span>
						<textarea class="ipAddress-input" onblur="checkIpRightEdit();" id="testIp" name="testIp" placeholder="<fmt:message key="msg.pg.userBindPhone.inputIpAddr"/>"></textarea>
						<%-- <input type="text" autocomplete="off" class="ipAddress-input" placeholder="<fmt:message key="msg.pg.userBindPhone.inputIpAddr"/>"> --%>
						<span class="error" style="font-size: 12px;font-weight: normal;padding-bottom: 0;" id="iperrorEdit"></span>
					</div>
				<%-- 	style="${showtel==1 ? '' : 'display:none' }" --%>
					<div class="editShowBox-input" style="${showtel==1 ? '' : 'display:none'}">
						<span><fmt:message key="msg.pg.userCenter.applySMScode"/></span>
						<input class="editShowBox-input-msg" autocomplete="off" type="text" onblur="checkCode1();" id="telCode" name="telCode" placeholder="<fmt:message key="msg.pg.userCenter.writeapplySMScode"/>">
						<input type="button" id="btnCodeEdit" name="btnCodeEdit" class="btn editShowBox-btn"  value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>">
						<span class="error" id="error-edit-showBox"></span>
					</div>
					<div class="editShowBox-input" style="${showgoogle==1 ? '' : 'display:none'}">
						<span><fmt:message key="msg.pg.userCenter.googleCode"/></span>
						<input type="text" name="googleCode" autocomplete="off" id="googleCode" onblur="checkGoogle1();" placeholder="<fmt:message key="msg.pg.userCenter.sendgoogelcode"/>">
						<span class="error" id="googleError-edit"></span>
					</div>
					<div class="editShowBox-createAccount" style="margin-top: 5px;">
						<input type="hidden" name="subUserId" id="sub-edit-id" >
						<button type="submit" onclick="changeSubaccountApi(); return false;" class="btn editShowBox-btn"><fmt:message key="msg.pg.userCenter.affirm"/></button>
						<p class="error display-none" id="editShowBoxError" style="color: red; margin-top: 5px;">212313</p>
					</div>
				</div>
			</form>
		</div>
		<!-- 主体内容 -->
		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userCenter.subAccount"/>-<fmt:message key="msg.pg.userCenter.applyApi"/></h2>
					</div>
					<div class="api-notice">
						<fmt:message key="msg.pg.userCenter.applyApiTip"/>
					</div>
					<c:if test="${appkey eq null}">
					<div class="box">
						<div class="control">
							<!--提示框,成功是class是success,失败时class是warning;-->
							<c:if test="${success!=null && success!='' && success != 'success'}">
								<div class="success">
									${success}
								</div>
							</c:if>
							<c:if test="${error!=null&&error!=''}">
								<div class="warning">
									${error}
								</div>
							</c:if>
							<form action="<%=subUserPath%>doBindAppKey.do" onsubmit="return checkSubApiFlag();" class="form-wrap" method="post" id="mobileform" name="mobileform">
								<input type="hidden" id="subUserId" name="subUserId" value="${subUser.f_user_id}"/>
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
								<!-- 增加 -->
								<div class="verify-wrap-api">
									<div class="form-group form-marbb" style="${showtel==1 ? '' : 'display:none'}">
										<label for="" class="form-lab">
											<fmt:message key="msg.pg.userCenter.applySMScode"/>：
										</label>
										<input type="hidden" id="mobile" name="mobile" value="${tel}"/>
										<input type="text" onblur="checkCode();" class="form-control form-inpt" autocomplete="off" name="code" id="code" placeholder="<fmt:message key="msg.pg.userCenter.writeapplySMScode"/>">
										
										<div class="send-btn-box">
											<label for="" class="form-lab"></label>
											<input type="button" class="btn send-btn" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/><br>
											<label for="" class="form-lab"></label>
											<span class="error" id="error1"></span>
										</div>
									</div>
									<div class="form-group form-marbb" style="${showgoogle==1 ? '' : 'display:none'}">
										<label for="" class="form-lab">
											<fmt:message key="msg.pg.userCenter.googleCode"/>：
										</label>
										<input class="form-control form-inpt"  onblur="checkGoogle();"  autocomplete="off" name="google" id="google" placeholder="<fmt:message key="msg.pg.userCenter.sendgoogelcode"/>">
										<span class="error" style="display: block;margin-left: 150px;" id="googleerror"></span>
									</div>
								</div>
								<!-- 结束 -->
								<div class="form-group-btnn">
									<input type="submit" class="btn sub-btnn api-btnn" value="<fmt:message key="msg.pg.userCenter.applyRequestApi"/>">
									<span style="display: block;margin-left: 150px;margin-top:5px;font-size: 12px;" class="error" id="errorApi"></span>
								</div>
							</form>
						</div>
					</div>
					</c:if>
				</div>
				<c:if test="${appkey ne null}">
				<div class="main-rt-box mt10">
					<div class="trade-manage-main">
						<input type="hidden" id="delSubApi" name="subUserId"/>
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
							<tr>
								<td><ut:formatDate dateline="${appkey.update_time}"></ut:formatDate></td>
								<td>${fn:substring(appkey.app_key,0,5)}*******</td>
								<td style="word-break: break-word;">${appkey.allow_ip }</td>
								<td>
									<c:if test="${appkey.allow_ip == '' || appkey.allow_ip == null}"><ut:formatDate dateline="${appkey.over_time}"></ut:formatDate></c:if>
									<c:if test="${appkey.allow_ip != '' && appkey.allow_ip != null}"><fmt:message key="msg.pg.userCenter.applyNeverExpires"/></c:if>
								</td>
								<td>
									<c:if test="${appkey.state eq 1 }"><fmt:message key="msg.pg.userCenter.applyuseAble"/></c:if>
									<c:if test="${appkey.state eq 0 }"><fmt:message key="msg.pg.userCenter.applyuseUnable"/></c:if>
								</td>
								<td>
									<a class="edit-btn" onclick="showEditBox('${appkey.f_user_id}');"><fmt:message key="msg.pg.userCenter.editApi"/></a>
									<a class="edit-btn showapidetile"><fmt:message key="msg.pg.userCenter.look"/></a>
									<a class="edit-del" onclick="deleteTip('${appkey.f_user_id}')"><fmt:message key="msg.pg.userCenter.applyDeleted"/></a>
									<%-- <button onclick="deleteTip('${appkey.f_user_id}')" class="btn btn-mal"><fmt:message key="msg.pg.userCenter.applyDeleted"/></button>
									     <button class="btn btn-mal showapi"><fmt:message key="msg.pg.userCenter.look"/></button> --%>
								</td>
							</tr>
					        <tr class="display-none showInfo">
					        	<td colspan="6" class="apply-api-detail">
					        		<em><fmt:message key="msg.pg.userCenter.applyApiPublic"/>：${appkey.app_key }</em><br>
					        		<em><fmt:message key="msg.pg.userCenter.applyBindIp"/>：	${appkey.allow_ip }</em>
					        	</td>
					        </tr>				
				          </tbody>
				        </table>
      				</div>	
				</div>
				</c:if>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
		<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/subBindAppKey.js?<%=rjs%>"></script>
		<script type="text/javascript">
		$(function() {
			var appkey = "${appkey.app_key}";
			var success = "${success}";
			var appsecret = "${appkey.app_secret}";
			var allowIPaddr = "${appkey.allow_ip}";
			if (success != null && success != '' && success == 'success' && appsecret != null && appsecret != ""){
				showapi(appkey, appsecret, allowIPaddr);			
			}
			var valuenull="";	
			//申请API发送验证码
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
			//编辑弹框发送验证码
			$("#btnCodeEdit").click(function(){
				var tel = $("#mobile2").val();
				document.forms.editShowBoxForm.btnCodeEdit.disabled = true;
				$.post("<%=basePath%>center/sendOldTelCode.do", {Tel:tel}, function(n){
					if(n == "success"){
						for (i = 1; i <= secs; i++) {
							window.setTimeout("doUpdateEdit(" + i + ")", i * 1000);
						}
					} else {
						tipAlert("<fmt:message key='msg.pg.userBindPhone.sendFail'/>");
						document.forms.editShowBoxForm.btnCodeEdit.disabled = false;
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
		function doUpdateEdit(num) {
			if (num == '60') {
				document.forms.editShowBoxForm.btnCodeEdit.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
				document.forms.editShowBoxForm.btnCodeEdit.disabled = false;
			} else {
				document.forms.editShowBoxForm.btnCodeEdit.disabled = true;
				var wut = secs - num;
				document.forms.editShowBoxForm.btnCodeEdit.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";		
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
			tipsApi: "手机号和谷歌都未绑定，请先绑定后在操作！"
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
			tipsApi: "手機號和穀歌都未綁定，請先綁定後在操作！"
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
			tipsApi: "The phone number and Google are not bound, please bind and operate!"
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
			tipsApi: "電話番号とGoogleはバインドされていません。バインドして操作してください！"
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
			tipsApi: "전화 번호와 Google이 연결되어 있지 않습니다. 바인딩하고 조작하십시오!"
		};
		message["ru_RU"] = {
			codeNull : "Код подтверждения не может быть пустым",
			codeError : "Пожалуйста, введите правильный код подтверждения ",
			lookOver: "К просмотру",
			applyPickup: "Забрать",
			applyDelete: "Удалить",
			codeCorrect: "код правильный",
			warmTip : "подсказка",
			sureTip : "Конечно",
			cancel: "Отменить",
			sureDelete: "Уверен(а) в удалении",
			ipCorrect: "Правильный IP формат",
			ipEorror: "Неправильный IP формат",
			applySuc: " заявка утверждена！",
			dateTmNover: "1. Вы подали заявку на пару секретных ключей, которые будут действовать постоянно.",
			dateTmNinty: "1. Вы подали заявку на пару секретных ключей, срок действия которых истекает через 90 дней. Затем вы можете повторно подать заявку.",
			apisecrete: "2. Закрытый ключ отображается только после успешного завершения и не может быть восстановлен после потери. Пожалуйста, держите его в безопасности.",
			replyapisecrete: "3. Если вы забыли свой закрытый ключ, удалите пару ключей и повторно примените.",
			apiPublic: "Публичный ключ API:",
			apiPerson: "Приватный ключ API:",
			tradePwd: "Пароль транзакции не может быть пустым",
			arrayIp: " Пара ключей имеет не более 10 IP-адресов ",
			tipsApi: " Номер телефона и Google не привязаны, пожалуйста, привяжите их и продолжайте работать!"
		};
		var MSG = message[locale];
		
		function deleteTip(subUserId) {
			$("#delSubApi").val(subUserId);
			$("#alert-windows").show();
			var insidehtm = 
						"<div id='tb-overlay-duiping' class='tb-overlay'></div><div class='add-wrap alert-wrap' style='display: block;' id='invite-BFX'>"
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
							+"<a class='btn sub-btn center' style='margin-right: 0px;' href='"+"<%=basePath%>userSubAccount/deleteSubAppKey.do?subUserId=" + subUserId +"'>"+MSG['sureTip']+"</a>"
						+"</div>"
						+"</div>"
						+"</div>";
			$("#alert-windows").html(insidehtm);
			center($('.alert-wrap'));
		}
		</script>
	</body>
</html>
