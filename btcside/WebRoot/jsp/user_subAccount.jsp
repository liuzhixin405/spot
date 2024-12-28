<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.inviteMag"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
		<!-- 子账户提示 -->
		<c:if test="${isSubUser == true}">
			<div id="add-windows" class="add-windows">
				<div id="tb-overlay" class="tb-overlay"></div>
				<div class="add-wrap" style="display:block;">
					<div class="dialogs-title">
						<h2 class="lt"><fmt:message key="msg.pg.userCenter.subAccount"/></h2>
						<a href="javascript:history.go(-1)" class="close-icon icons-close" id="close-alert"></a>
					</div>
					<div class="dialogs-body">
						<p style="margin-bottom:30px; text-align:center;font-size:16px;">
							很抱歉！该功能子账号未开放
						</p>
						<div class="form-group-btn" style="display: flex; justify-content: center">
							<a href="javascript:history.go(-1)" class="btn sub-btn-return"><fmt:message key="msg.pg.trade.goBack"/></a>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		<!-- 安全提示 -->
		<c:if test="${applayerror==true}">
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
		<!-- 新增子账号弹框 -->
		<div class="add-windows" id="alert-pwd">
    		<div id="tb-overlay-duiping" class="tb-overlay display-none"></div>
    		<div class="add-wrap dp-alert-wrap invite-BFX" id="regsubAccount-alert">
	    		<div class="risk-box" style="width: 429px;padding:0;border:none;">
					<form id="regsubAccountForm" name="regsubAccountForm" method="post" autocomplete="off">
						<div class="regsubAccount-wrap">
							<h2><fmt:message key="msg.pg.userCenter.newSubaccount"/></h2>
							<a class="close-icon-btn" href="javascript:closeRegsubAccount()"><i class="iconfont icon-guanbi"></i></a>
							<div class="regsubAccount-input">
								<span><fmt:message key="msg.pg.userCenter.SubaccountloginName"/></span>
								<input name="email" id="email" autocomplete="off" onblur="checkLoginName();" autocomplete="off" placeholder="<fmt:message key="msg.pg.userCenter.writeSubloginName"/>">
								<span class="error" id="loginNameError"></span>
							</div>
							<div class="regsubAccount-input">
								<span><fmt:message key="msg.pg.index.loginPwd"/></span>
								<input name="password" id="password" autocomplete="new-password" type="password" onblur="checkPassword();" placeholder="<fmt:message key="msg.pg.userCenter.writeloginPassword"/>">
								<span class="error" id="error2"></span>
							</div>
							<div class="regsubAccount-input">
								<span><fmt:message key="msg.pg.register.againPwd"/></span>
								<input name="password2" id="password2" autocomplete="new-password" type="password" onblur="checkPassword2();" placeholder="<fmt:message key="msg.pg.userCenter.writePasswordAgain"/>">
								<span class="error" id="error3"></span>
							</div>
							<div class="regsubAccount-input" style="${showtel==1 ? '' : 'display:none'}">
								<span><fmt:message key="msg.pg.userCenter.applySMScode"/></span>
								<input type="hidden" id="mobile" name="mobile" value="${tel}"/>
								<input type="text" autocomplete="off" class="regsubAccount-input-msg" name="code" id="code" placeholder="<fmt:message key="msg.pg.userCenter.writeapplySMScode"/>">
								<input class="btn regsubAccount-btn" type="button" id="btnCodeNew" name="btnCodeNew" value="<fmt:message key="msg.pg.userCenter.sendapplySMScode"/>"/>
								<span class="error" id="error1"></span>
							</div>
							<div class="regsubAccount-input" style="${showgoogle==1 ? '' : 'display:none' }">
								<span><fmt:message key="msg.pg.userCenter.googleCode"/></span>
								<input type="text" autocomplete="off" name="google" id="google" onblur="checkGoogle();"  placeholder="<fmt:message key="msg.pg.userCenter.sendgoogelcode"/>"> 
								<span class="error" id="googleerror"></span>
							</div>
							<div class="regsubAccount-createAccount">
								<button type="submit" onclick="addSubUser(); return false;" class="btn createAccount-btn"><fmt:message key="msg.pg.userCenter.createSubaccount"/></button>
								<p class="error display-none" id="regsubAccountError" style="color: red; margin-top: 5px;">212313</p>
							</div>
						</div>
					</form>
				</div>
			</div>
    	</div>
		<!-- 资金划转弹框 -->
		<div id="fundsTtransfer" class="fundsTtransfer" >
			<form id="fundsTtransferForm" name="fundsTtransferForm" method="post">
				<div class="fundsTtransfer-wrap">
					<h2><fmt:message key="msg.pg.trade.transfer"/></h2>
					<a class="close-icon-btn" href="javascript:closeFundsTtransfer()"><i class="iconfont icon-guanbi"></i></a>
					<div class="fundsTtransfer-input">
						<span><fmt:message key="msg.pg.userCenter.currentSubaccount"/></span>
						<input  readonly="readonly" type="text" style="background-color: #f8f8f8;" name="sub-tranfer-name" id="sub-tranfer-name" >
						<input type="hidden" name="subUserId" id="sub-tranfer-id" >
						<input type="hidden" name="direction" id="sub-tranfer-direction" >
					</div>
					<div class="fundsTtransfer-input">
						<span><fmt:message key="msg.pg.common.currency"/></span>
						<select name="currencyId" id="currencyId" class="subcurrency" onchange="getAmount()">
							<c:forEach var="ta" items="${currencyTypes}">
								<c:if test="${fn:toUpperCase(ta.type_name) ne 'BFC'}">
									<option value="${ta.type_id}" selected="selected">
												${fn:toUpperCase(ta.type_name)}
									</option>
								</c:if>
							</c:forEach>
						</select>
					</div>
					<div class="fundsTtransfer-input">
						<span><fmt:message key="msg.pg.userCenter.transferDirection"/></span>
						<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-l" style="display: none;" name="fundsTtransferInputSub" id="fundsTtransferInputSub" placeholder="<fmt:message key="msg.pg.userCenter.subAccount"/>">
						<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-s" name="fundsTtransferInputSub2" id="fundsTtransferInputSub2" placeholder="<fmt:message key="msg.pg.userCenter.motherAccount"/>">
						<!-- <img id="changeTtransfer" class="changeTtransfer" src="../common/images/transfer-normal.png"> -->
						<i id="changeTtransfer" class="iconfont icon-huazhuan changeTtransfer"></i>
						<%-- <em id="changeTtransfer" class="fundsTtransfer-input-text"><fmt:message key="msg.pg.userCenter.transferredTo"/></em> --%>
						<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-l" style="display: none;" name="fundsTtransferInputSub3" id="fundsTtransferInputSub3" placeholder="<fmt:message key="msg.pg.userCenter.motherAccount"/>">
						<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-s" name="fundsTtransferInputSub4" id="fundsTtransferInputSub4" placeholder="<fmt:message key="msg.pg.userCenter.subAccount"/>">
					</div>
					<div class="fundsTtransfer-input">
						<span><fmt:message key="msg.pg.userCenter.transferNumber"/></span>
						<input name="amount" id="amount" placeholder="<fmt:message key="msg.pg.userCenter.writetransferNumber"/>"> 
					</div>
					<div class="fundsTtransfer-input">
						<span class="total-num"><fmt:message key="msg.pg.userCenter.convertibleNumber"/><em id="sub-balance"></em></span>
						<span class="all-fundsTtransfer" onclick="tranferAll()"><fmt:message key="msg.pg.userCenter.allTransfer"/></span>
					</div>
					<div class="fundsTtransfer-createAccount">
						<button type="submit" onclick="fundsTtransferSubUser(); return false;" class="fundsTtransfer-btn"><fmt:message key="msg.pg.userCenter.affirm"/></button>
						<p class="error display-none" id="fundsTtransferError" style="color: red; margin-top: 5px;">212313</p>
					</div>
				</div>
			</form>
		</div>
		<!-- 重置登录密码 -->
<!-- 		<div id="resetPasswords" class="resetPasswords" >
		<div class="resetPasswords-add"> -->
			<div class="add-windows" id="alert-pwd">
				<div id="tb-overlay-duiping" class="tb-overlay display-none"></div>
		   		<div class="add-wrap dp-alert-wrap invite-BFX" id="resetPasswords-alert">
		    		<div class="risk-box" style="width: 429px;padding:0;border:none;">
					<form id="resetPasswordsForm" name="resetPasswordsForm" method="post" autocomplete="off">
						<div class="resetPasswords-wrap">
							<h2><fmt:message key="msg.pg.userFindPwd2.title"/></h2>
							<a class="close-icon-btn" href="javascript:closeResetPasswords()"><i class="iconfont icon-guanbi"></i></a>
							<input type="hidden" name="subUserId" id="sub-reset-pwd" >
							<div class="regsubAccount-input">
								<span><fmt:message key="msg.pg.userChangePwd.newPwd"/></span>
								<input type="password" autocomplete="new-password"  name="newpasswd1" id="newpasswd1" onblur="checknewPwd();" placeholder="<fmt:message key="msg.pg.userChangePwd.inputNewPwd"/>">
								<span class="error" id="error2"></span>
							</div>
							<div class="regsubAccount-input">
								<span><fmt:message key="msg.pg.userCenter.affirmNewloginPw"/></span>
								<input type="password" autocomplete="new-password"  name="newpasswd2" id="newpasswd2" onblur="checknewPwd2();" placeholder="<fmt:message key="msg.pg.userChangePwd.againInputNewPwd"/>">
								<span class="error" id="error3"></span>
							</div>
							<div class="regsubAccount-input" style="${showtel==1 ? '' : 'display:none' }">
								<span><fmt:message key="msg.pg.userCenter.applySMScode"/></span>
								<input class="regsubAccount-input-msg" autocomplete="off" type="text" onblur="checkCodereset();" id="telCode" name="telCode" placeholder="<fmt:message key="msg.pg.userCenter.writeapplySMScode"/>">
								<button type="button" id="btnCodeNew1" name="btnCodeNew1" class="btn regsubAccount-btn"><fmt:message key="msg.pg.userCenter.sendapplySMScode"/></button>
								<span class="error" id="errorReset"></span>
							</div>
							<div class="regsubAccount-input" style="${showgoogle==1 ? '' : 'display:none' }">
								<span><fmt:message key="msg.pg.userCenter.googleCode"/></span>
								<input type="text" name="googleCode" autocomplete="off" id="googleCode" onblur="checkGooglereset();" placeholder="<fmt:message key="msg.pg.userCenter.sendgoogelcode"/>">
								<span class="error" id="googleerrorReset"></span>
							</div>
							<div class="regsubAccount-createAccount">
								<button type="submit" onclick="resetPasswordsSubUser(); return false;" class="btn resetPasswords-btn"><fmt:message key="msg.pg.userCenter.affirm"/></button>
								<p class="error display-none" id="resetPasswordsError" style="color: red; margin-top: 5px;">212313</p>
							</div>
						</div>
					</form>
				</div>
			</div>
	  	</div>
		<!-- 冻结子账户 -->
		<div id="freezeSubAccount" class="delSubAccount" >
			<div class="delSubAccount-wrap">
				<div class="warning-tips">
					<img alt="warning" src="../common/images/warning-tips.png">
				</div>
				<img class="closesubAccount" onclick="closefreezeSubAccount()" alt="closesubAccount" src="../common/images/close_pages.png">
				<p><fmt:message key="msg.pg.trade.optionStatusSuc1"/></p>
				<div class="delSubAccount-btn">
					<button type="button" onclick="closefreezeSubAccount()" class="btn delSubAccount-btn-cancel"><fmt:message key="msg.pg.trade.cancel"/></button>
					<button type="submit" class="btn delSubAccount-btn-ok"><fmt:message key="msg.pg.userCenter.affirm"/></button>
				</div>
			</div>
		</div>
		<!-- 删除子账户 -->
		<div id="delSubAccount" class="delSubAccount" >
			<div class="delSubAccount-wrap">
				<div class="warning-tips">
					<img alt="warning" src="../common/images/warning-tips.png">
				</div>
				<a class="close-icon-btn" href="javascript:closeDelSubAccount()"><i class="iconfont icon-guanbi"></i></a>
				<p><fmt:message key="msg.pg.userCenter.delSubAccount1"/><span id="user-account-id"></span><fmt:message key="msg.pg.userCenter.delSubAccount2"/></p>
				<div class="delSubAccount-btn">
					<button type="submit" onclick="deleteSubUser()" class="btn delSubAccount-btn-ok"><fmt:message key="msg.pg.userCenter.affirm"/></button>
					<button type="button" onclick="closeDelSubAccount()" class="btn delSubAccount-btn-cancel"><fmt:message key="msg.pg.trade.cancel"/></button>
					<input type="hidden" id="del-sub-id">
				</div>
			</div>
		</div>
		<div class="add-windows" id="alert-windows"></div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userCenter.subAccount"/></h2>
						<span class="subhead-title">
							<img alt="bulkEdit" src="../common/images/bulk_edit.png" class="subhead-img">
							<a href="<%=subUserPath%>toSubUserBatchCreate.do" class="subhead-text"><fmt:message key="msg.pg.userCenter.subheadSubAccount"/></a>
						</span>
						<span onclick="showRegsubAccount()" class="subhead-title">
							<img alt="subAccount" src="../common/images/new_subaccount.png" class="subhead-img">
							<a class="subhead-text"><fmt:message key="msg.pg.userCenter.createSubaccount"/></a>
						</span>
					</div>
				</div>
				<div class="main-wrap">
					<div class="block-title-top" style="border: none;margin-top: 20px;">
						<form action="<%=subUserPath%>subUser.do" method="post">
							<input name="loginName" id="loginName" value="${loginName }" placeholder="<fmt:message key="msg.pg.userCenter.loginName"/>">
							<input name="remark" id="remark" value="${remark }" placeholder="<fmt:message key="msg.pg.userCenter.remark"/>">
							<button type="submit" class="btn inquire-btn" ><fmt:message key="msg.pg.userCenter.inquire"/></button>
						</form>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th style="text-align: left;padding-left: 15px;">
										<fmt:message key="msg.pg.userCenter.loginName"/>
									</th>
									<th>
										<fmt:message key="msg.pg.tradeQuery.state"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.remark"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.option"/>
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas }" var="one" varStatus="l">
								<tr>
									<td style="text-align: left;padding-left: 15px;">
										<c:out value="${one[1]}"></c:out>
									</td>
									<td>
										<c:if test="${one[2] == 1}"><fmt:message key="msg.pg.userCenter.transfernormal"/></c:if>
										<c:if test="${one[2] == -1}"><fmt:message key="msg.pg.trade.freezen"/></c:if>
									</td>
									<td class="edit-remark-td">
										<div class="edit-remark-box">
											<input class="edit-remark" onfocus="addactive(${l.index})" onblur="addNormal(${one[0]}, ${l.index})" type="text" name="edit-remark" id="edit-remark${l.index}" value="${one[3]}">
											<!-- <img class="edit-remark-img" alt="edit" src="../common/images/edit.png"> -->
										</div>
									</td>
									<td>
										<c:if test="${one[4] == 0 }">
											<a href="<%=subUserPath%>bindGoogle.do?subUserId=${one[0]}" class="operate-text"><fmt:message key="msg.pg.subUser.bindGoogle"/></a>
										</c:if>
										<c:if test="${one[4] == 2 }">
											<a href="javascript:void(0);" class="operate-text-yat" style="cursor: default;"><fmt:message key="msg.pg.subUser.bindGoogleYet"/></a>
										</c:if>
										<a href="<%=subUserPath%>subUserAssets.do?subUserId=${one[0]}" class="operate-text"><fmt:message key="msg.pg.userCenter.assetsDetails"/></a>
										<a onclick="fundsTtransfer(${one[0]},'${one[1]}')" class="operate-text"><fmt:message key="msg.pg.trade.transfer"/></a>
										<a onclick="resetPasswords(${one[0]})" class="operate-text"><fmt:message key="msg.pg.userCenter.resetPw"/></a>
										<c:if test="${one[2] == 1}"><a onclick="updateFreeze(${one[0]})" class="operate-text"><fmt:message key="msg.pg.userCenter.frozeAssets"/></a></c:if>
										<c:if test="${one[2] == -1}"><a onclick="updateFreeze(${one[0]})" class="operate-text"><fmt:message key="msg.pg.userCenter.unfreeze"/></a></c:if>
										<a href="<%=subUserPath%>bindSubAppKey.do?subUserId=${one[0]}" class="operate-text"><fmt:message key="msg.pg.userCenter.applyApiRequest"/></a>
										<a onclick="delSubAccount(${one[0]},'${one[1]}')" class="operate-del"><fmt:message key="msg.pg.userCenter.delete"/></a>
									</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<ul class="pagination">
						<pg:pager url="${pageN}" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
							
							<pg:param name="loginName" value="${loginName }" />
							<pg:param name="remark" value="${remark }" />
							
							<pg:prev>
							<li class="disabled">
								<a href="<%=basePath%>${pageUrl}"
									mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>
									</li>
							</pg:prev>
							
							<pg:pages>
								<c:choose>
									<c:when test="${currentPageNumber eq pageNumber }">
										<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
									</c:when>
									<c:otherwise>
									<li>
										<a href="<%=basePath%>${pageUrl }"
											mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
									</c:otherwise>
								</c:choose>
							</pg:pages>

							<pg:next>
							<li>
								<a href="<%=basePath%>${pageUrl }"
									mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a>
									</li>
							</pg:next>
						</pg:pager>
					</ul>
				</div>
				<div class="main-wrap">
					<div class="block-title-top">
						<h2><fmt:message key="msg.pg.userCenter.motherAccountList"/></h2>
						<a href="<%=basePath%>userSubAccount/transferLogs.do" class="rt" style="color: #4980E3;"><fmt:message key="msg.pg.userCenter.view"/><fmt:message key="msg.pg.index.more"/></a>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th style="text-align: left;padding-left: 15px;width: 20%;">
										<fmt:message key="msg.pg.userCenter.transferTime"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.genre"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.subAccount"/>
									</th>
									<th>
										<fmt:message key="msg.pg.common.currency"/>
									</th>
									<th>
										<fmt:message key="msg.pg.index.amount"/>
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="li" items="${logs}" >
								<tr>
									<td style="text-align: left;padding-left: 15px;">
										<span><ut:formatDate dateline="${li.dateline}"></ut:formatDate></span>
									</td>
									<td>
										<c:if test="${li.operation == 1 }"><fmt:message key="msg.pg.userCenter.transferSubaccount"/></c:if>
										<c:if test="${li.operation == 2 }"><fmt:message key="msg.pg.userCenter.subAccountTransfer"/></c:if>
									</td>
									<td>
										<c:out value="${li.sub_user_email}" />
									</td>
									<td>
										<c:out value="${li.currency_name}" />
									</td>
									<td>
										<fmt:formatNumber value="${li.amount }" pattern="#0.##"/>
									</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/subAccount.js?<%=rjs%>"></script>
</body>
<script type="text/javascript">
	$("#btnCodeNew").click(function() {
		document.forms.regsubAccountForm.btnCodeNew.disabled = true;
		$.post("<%=basePath%>center/sendOldTelCode.do", {}, function(n){
			if(n == "success"){
				for (i = 1; i <= secs; i++) {
					window.setTimeout("doUpdate(" + i + ")", i * 1000);
				}
			} else {
				tipAlert(n);
				document.forms.regsubAccountForm.btnCodeNew.disabled = false;
			}
		}, "text");
	});
	
	$("#btnCodeNew1").click(function(){
		document.forms.resetPasswordsForm.btnCodeNew1.disabled = true;
		$.post("<%=basePath%>center/sendOldTelCode.do", {}, function(n){
			if(n == "success"){
				for (i = 1; i <= secs; i++) {
					window.setTimeout("doUpdateEmail(" + i + ")", i * 1000);
				}
			} else {
				tipAlert(n);
				document.forms.resetPasswordsForm.btnCodeNew1.disabled = false;
			}
		}, "text");
	});
    
    var secs = 60;
    function doUpdate(num) {
		if (num == '60') {
			document.forms.regsubAccountForm.btnCodeNew.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
			document.forms.regsubAccountForm.btnCodeNew.disabled = false;
			$("#btnCodeNew").css("background-color","#5d8fe9");
		} else {
			document.forms.regsubAccountForm.btnCodeNew.disabled = true;
			$("#btnCodeNew").css("background-color","#cfcfcf");
			var wut = secs - num;
			document.forms.regsubAccountForm.btnCodeNew.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";
		}
	}
    
    function doUpdateEmail(num) {
		if (num == '60') {
			document.forms.resetPasswordsForm.btnCodeNew1.value = " <fmt:message key='msg.pg.userCenter.sendSMSverifcate'/> ";
			document.forms.resetPasswordsForm.btnCodeNew1.disabled = false;
			$("#btnCodeNew1").css("background-color","#5d8fe9");
		} else {
			document.forms.resetPasswordsForm.btnCodeNew1.disabled = true;
			$("#btnCodeNew1").css("background-color","#cfcfcf");
			var wut = secs - num;
			document.forms.resetPasswordsForm.btnCodeNew1.value = " <fmt:message key='msg.pg.userCenter.hasSend'/> (" + wut + ")...";		
		}
	}
</script> 

<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		emailNull : "登录名不能为空！",
		emailError : "登录名格式错误！",
		emailAlreadyRegister : "该登录名已注册,请换一个！",
		emailAvailable : "该登录名可用！",
		pwdNull : "密码不能为空！",
		pwdLtEightChar : "登录密码小于8个字符！",
		pwdGtTwentyChar : "登录密码超过20个字符！",
		twicePwdNotEq : "密码不一致！",
		twicePwdEq : "密码一致",
		pwdAvailable : "密码可用！",
		confirmPwdNull : "确认密码不能为空！",
		confirmPwdAvailable : "确认密码可用！",
		codeNull : "验证码不能为空！",
		codeError : "验证码不正确！",
		agreementService : "您需要先同意服务协议！",
		emailBoxError : "登录名错误！",
		login: "立即登录",
		warmTip: "温馨提示",
		sureTip: "确定",
		nickNameRule: "登录名为4-32个字符(可包含字母,数字和下划线)",
		hasnoOK:"登录名不符合规则"
	};
	message["zh_HK"] = {
		emailNull : "登錄名不能為空！",
		emailError : "登錄名格式錯誤！",
		emailAlreadyRegister : "該登錄名已註冊,請換一個！",
		emailAvailable : "該登錄名可用！",
		pwdNull : "密碼不能為空！",
		pwdLtEightChar : "登錄密碼小於8個字元！",
		pwdGtTwentyChar : "登錄密碼超過20個字元！",
		twicePwdNotEq : "密碼不一致！",
		twicePwdEq : "密碼一致",
		pwdAvailable : "密碼可用！",
		confirmPwdNull : "確認密碼不能為空！",
		confirmPwdAvailable : "確認密碼可用！",
		codeNull : "驗證碼不能為空！",
		codeError : "驗證碼不正確！",
		agreementService : "您需要先同意服務協議！",
		emailBoxError : "登錄名錯誤！",
		login: "立即登錄",
		warmTip: "溫馨提示",
		sureTip: "確定",
		nickNameRule: "登錄名為4-32個字元(可包含字母,數字和下劃線)",
		hasnoOK:"登錄名不符合規則"
	};
	message["en_US"] = {
		emailNull : "The login name can not be empty !",
		emailError : "Login name format error !",
		emailAlreadyRegister : "This login name has been registered! please change another one !",
		emailAvailable : "Available login name !",
		pwdNull : "Password cannot be blank !",
		pwdLtEightChar : "The password is less than 8 characters !",
		pwdGtTwentyChar : "The Password is more than 20 characters !",
		twicePwdNotEq : "Inconsistent passwords !",
		twicePwdEq : "Consistent passwords",
		pwdAvailable : "Available Password !",
		confirmPwdNull : "Confirm Password cannot be blank !",
		confirmPwdAvailable : "Available Confirm Password !",
		codeNull : "The auth code cannot be blank !",
		codeError : "Please enter correct auth code !",
		agreementService : "You need to agree with the Service Agreement first !",
		emailBoxError : "The login name error !",
		login: "Log in",
		nickNameRule: "The login name is 4-32 characters (which can include letters, Numbers, and underscores)",
		hasnoOK:"The user name does not follow the rules"
	};
	message["ja_JP"] = {
		emailNull : "メールの欄は空欄にすることはできません！",
		emailError : "メールアドレスの形式が間違っています！",
		emailAlreadyRegister : "このメールアドレスは登録されています、他のメールアドレスで登録してください！",
		emailAvailable : "このメールアドレスは利用可能です！",
		pwdNull : "パスワードは空欄になれません！",
		pwdLtEightChar : "パスワードが8文字以下！",
		pwdGtTwentyChar : "パスワードが20文字以上！",
		twicePwdNotEq : "パスワードが一致しません！",
		twicePwdEq : "パスワードが一致しています",
		pwdAvailable : "パスワードが一致しません！",
		confirmPwdNull : "確認パスワードの欄は空欄にすることはできません！",
		confirmPwdAvailable : "確認パスワードが利用可能です！",
		codeNull : "認証コードが空欄になれません！",
		codeError : "認証コードが正しくありません！",
		agreementService : "先にサービス同意書を同意してから！",
		emailBoxError : "メールアドレスが間違っています！",
		login: "今すぐサインイン",
		nickNameRule: "登录名为4-32个字符(可包含字母,数字和下划线)",
		hasnoOK:"ユーザー名がルールに一致しません"
	};
	message["ko_KR"] = {
		emailNull : "로그인 이름은 비워 둘 수 없습니다!",
		emailError : "로그인 이름이 잘못된 형식입니다!",
		emailAlreadyRegister : "로그인이 이미 등록되었습니다. 변경하십시오!",
		emailAvailable : "이 로그인이 가능합니다!",
		pwdNull : "비밀번호는 비워 둘 수 없습니다!",
		pwdLtEightChar : "로그인 비밀번호가 8 자 미만입니다!",
		pwdGtTwentyChar : "로그인 비밀번호가 20 자 이상입니다!",
		twicePwdNotEq : "일치하지 않는 비밀번호!",
		twicePwdEq : "일관된 비밀번호",
		pwdAvailable : "비밀번호를 사용할 수 있습니다!",
		confirmPwdNull : "비밀번호는 비워 둘 수 없습니다!",
		confirmPwdAvailable : "비밀번호가 사용 가능한지 확인하십시오!",
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "인증 코드가 잘못되었습니다!",
		agreementService : "먼저 서비스 계약에 동의해야합니다!",
		emailBoxError : "로그인 이름이 잘못되었습니다!",
		login: "지금 로그인",
		warmTip: "팁",
		sureTip: "결정",
		nickNameRule: "로그인 이름은 4-32 자입니다 (문자, 숫자 및 밑줄을 포함 할 수 있음)",
		hasnoOK:"로그인 이름이 규칙과 일치하지 않습니다"
	};
	message["ru_RU"] = {
		emailNull : "Имя пользователя не может быть пустым !",
		emailError : "Ошибка формата имени пользователя !",
		emailAlreadyRegister : "Этот логин (имя пользователя) уже зарегистрирован, пожалуйста, используйте другой логин !",
		emailAvailable : "Доступное имя пользователя !",
		pwdNull : "Пароль не может быть пустым !",
		pwdLtEightChar : "Пароль менее чем 8 символов !",
		pwdGtTwentyChar : "Пароль более чем 20 символов !",
		twicePwdNotEq : "Пароли не соответствуют!",
		twicePwdEq : "Пароли соответствуют",
		pwdAvailable : "Доступный пароль !",
		confirmPwdNull : "Подтверждение пароля не может быть пустым!",
		confirmPwdAvailable : "Доступное подтверждение пароля!",
		codeNull : "Код аутентификации не может быть пустым !",
		codeError : "Пожалуйста, введите правильный код аутентификации !",
		agreementService : "Вы должны сначала согласиться с соглашением об обслуживании !",
		emailBoxError : "Ошибка имени пользователя !",
		login: "Войти",
		warmTip : "подсказка",
		sureTip : "Конечно",
		nickNameRule: "Логин может состоять из 4-32 символов (которые могут включать в себя буквы, цифры и символы подчеркивания)",
		hasnoOK: "Имя пользователя не соответствует правилам"
	};
	var MSG = message[locale];
</script>
</html>

