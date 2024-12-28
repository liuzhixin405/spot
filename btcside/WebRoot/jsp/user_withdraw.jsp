<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="Pragma" content="no-cache"/>
		<meta http-equiv="Cache-Control" content="no-cache" />
		<meta http-equiv="Expires" content= "0"/>
		<title><fmt:message key="msg.pg.systemName"/>-BTC<fmt:message key="msg.pg.top.withdraw"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
		<jsp:include page="_common.jsp"></jsp:include>
	</head>
	<body>
		<div class="add-windows" id="alert-windows"></div>
		<!-- 子账号不能提币提示 -->
		<c:if test="${isSubUser == true}">
			<div id="add-windows" class="add-windows">
				<div id="tb-overlay" class="tb-overlay"></div>
				<div class="add-wrap" style="display:block;">
					<div class="dialogs-title">
						<h2 class="lt"><fmt:message key="msg.pg.top.withdraw"/></h2>
						<a href="javascript:history.go(-1)" class="close-icon icons-close" id="close-alert"></a>
					</div>
					<div class="dialogs-body">
						<p style="margin-bottom:30px; text-align:center;font-size:16px;">
							子账户不能提币!
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
	
		<!-- 修改提现地址的弹窗 -->
		<div class="add-windows" id="add-windows">
			<div id="tb-overlay-tixian" class="tb-overlay display-none"></div>
			<div class="add-wrap" id="withdraw-wrap">
			<div class="dialogs-title">
				<h2 class="lt"><fmt:message key="msg.pg.userCenter.addWithdrawAddr"/></h2>
            	<a href="javascript:" class="close-icon icons-close close-alert"></a>
        	</div>
				<div class="dialogs-body">
					<form id="addressForm" name="addressForm"
						class="form-wrap"  method="post"> 
						<input type="hidden" name="addid" id="addid" />
						<input type="hidden" name="currencyId" id="currencyId" value="${coinId }"/>
						<div class="form-group">
							<label for="">
								<fmt:message key="msg.pg.userCenter.withdrawalsAd"/>
							</label>

							<input type="text" autocomplete="off" class="form-control withdraw-input" placeholder="<fmt:message key="msg.pg.userCenter.enterwithdrawalsAd"/>" name="address"
								id="address" onblur="checkAddress();" />
						</div>
						<div class="form-group">
							<label for="">
								<fmt:message key="msg.pg.userCenter.label"/>
							</label>
							<input type="text" autocomplete="off" class="form-control withdraw-input" placeholder="<fmt:message key="msg.pg.userCenter.inputParseAd"/>" name="label" id="label" />
							<span class="error" id="labelError"></span>
							<span><fmt:message key="msg.pg.userCenter.existManyAd"/> ${coinName } <fmt:message key="msg.pg.userCenter.address"/><fmt:message key="msg.pg.userCenter.existLabelset"/></span>
						</div>
						<!-- 增加短信和谷歌验证    开始 -->
						<div class="form-group" style="${tel==1 ? '' : 'display:none'}">
							<label for="">
								<fmt:message key="msg.pg.userCenter.applySMScode"/>
							</label><br>
							<input type="hidden" id="mobile2" name="mobile2" value="${mobile}"/>
							<input type="text" autocomplete="off" class="addWithdraw-input" onblur="checkPhoneCode2();" name="phoneCode" id="phoneCode" placeholder="<fmt:message key="msg.pg.userCenter.writeapplySMScode"/>">
							<input class="btn addWithdraw-input-btn" type="button" id="phoneCodebtn" name="phoneCodebtn" value="<fmt:message key="msg.pg.userCenter.sendapplySMScode"/>"/><br>
							<span style="display: block;" class="error" id="phoneCodError"></span>
						</div>
						<div class="form-group" style="${google==2 ? '' : 'display:none' }">
							<label for="">
								<fmt:message key="msg.pg.userCenter.googleCode"/>
							</label>
							<input type="text" class="form-control withdraw-input" autocomplete="off" name="googleCode" id="googleCode" onblur="checkAddoogle2();"  placeholder="<fmt:message key="msg.pg.userCenter.sendgoogelcode"/>"> 
							<span style="display: block;" class="error" id="checkGoogleerror"></span>
						</div>
						<!-- 结束 -->
						<div class="form-group-btn">
							<input type="hidden" name="erc20usdt" id="erc20usdt" value="${erc20usdt }"/>
							<input type="hidden" name="trc20usdt" id="trc20usdt" value="${trc20usdt }"/>
							<button type="submit" class="btn sub-btn" onclick="addWithdrawAddredd(); return false;"><fmt:message key="msg.pg.userCenter.saveAd"/></button>
							<p class="error display-none" id="addressError" style="color: red; margin-top: 5px;">212313</p>
						</div>
					</form>
				</div>
			</div>
		</div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->
		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2>
							${coinName }<fmt:message key="msg.pg.top.withdraw"/>
						</h2>
					</div>
					<div class="api-notice">
						<fmt:message key="msg.pg.userCenter.warmPrompt"/>
					</div>
					<div class="box" >
					
						<c:if test="${coinName eq 'USDT'}">
							<div class="tab" style="margin-bottom: 20px;">
								<a href="<%=basePath %>center/withdraw.do?coinId=${coinId}&trc20=1" class="${trc20 == 1 ? 'current' : ''}" style="border-width: 1px;">TRC20</a>
								<a href="<%=basePath %>center/withdraw.do?coinId=${coinId}&omni=0" class="${trc20 == 0 ? 'current' : ''}" style="border-width: 1px;">ERC20</a>
								<%-- <a href="<%=basePath %>center/withdraw.do?coinId=${coinId}&omni=1" class="${omni == 1 ? 'current' : ''}" style="border-width: 1px;">OMNI</a> --%>
							</div>
						</c:if>
						<c:if test="${coinName eq 'BTC'}">
							<div class="tab" style="margin-bottom: 20px;">
								<a href="<%=basePath %>center/withdraw.do?coinId=${coinId}" class="current" style="border-width: 1px;">HECO</a>
							</div>
						</c:if>
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

						<div class="control">
							<p><fmt:message key="msg.pg.userCenter.myPause"/>：<span><b><fmt:formatNumber value="${accountBtc }" pattern="#0.0000"></fmt:formatNumber> ${coinName}</b></span></p>
							<form action="<%=basePath%>center/withdrawdo.do" id="mobileform" method="post" name="mobileform" class="form-wrap" autocomplete="off">
								
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userCenter.withdrawalsAd"/>
									</label>
									<div class="relative withdraw-input-div">
										
										<input type="text" class="form-control withdraw-input" placeholder="<fmt:message key="msg.pg.userCenter.enterwithdrawalsAd"/>"
											name="withdrawaddress" autocomplete="off" id="withdrawaddress" value=""/>
										<div class="triger-domn-arrow" id="withdrawTips"></div>
										<div class="withdraw-addr-list">
											<c:forEach var="li" items="${addressList}" varStatus="st">
												<div class="addr-list-item">
												 	<div class="addr-list-item-left" onmousedown="preventDown()" onClick="selectWithdrawAddress('${li.wallet_address}')">
														<p>${li.name}</p>
														<p>${li.wallet_address}</p>
													</div>
													<div class="addr-list-item-right" onmousedown="preventDown()" onClick="deleteWithdrawAddress(this, ${li.id})"><i class="iconfont icon-guanbi"></i></div>
													<div class="clear"></div>
												</div>
											</c:forEach>
											<p class="new-addr-btn" id="edit-address" onmousedown="preventDown()" onClick="newAddressClick()" style="line-height: 58px;margin: 0;"><fmt:message key="msg.pg.userCenter.addWithdrawAddr"/></p>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userCenter.withdrawalsLeast"/> ${minWithdraw.doubleValue()} ${coinName},
										<fmt:message key="msg.pg.userCenter.withdrawFee"/>  ${fee.doubleValue()} ${coinName})
										<input type="hidden" name="balance" id="balance" value="${balance}"/>
									</label>
									<input type="text" autocomplete="off" class="form-control withdraw-input" placeholder="<fmt:message key="msg.pg.userCenter.enterNum"/>" onkeyup="checkInputDigits(this, 4);" onblur="checkAmountTibi(${minWithdraw.doubleValue()},${maxWithdraw.doubleValue()},${fee.doubleValue()},'${coinName}');" name="amount" id="amount" />
									<span class="error" id="error2"></span>
									<input type="text" style="display: none;"/>
								</div>
								<div class="form-group" style="${state==1 ? '' : 'display:none' }">
									<label for="">
										<fmt:message key="msg.pg.userSetTradePwd.tradePwd"/>
									</label>
									<input type="hidden" name="trade" id="trade" value="trade" />
									<input type="password" class="form-control withdraw-input" autocomplete="new-password" placeholder="<fmt:message key="msg.pg.userSetTradePwd.inputTradePwd"/>" name="password"
										id="password" onblur="checkTradePwd();" />
									<span class="error" id="error3"> <a
										href="<%=basePath%>password/tradePwd.do"><fmt:message key="msg.pg.userSetTradePwd.recoverAssetPwd"/></a> </span>
								</div>
								<div class="form-group" style="${tel==1? '' : 'display:none' }">
									<label for="">
										<fmt:message key="msg.pg.userCenter.phoneVaidcode"/>
										<input type="hidden" name="mobile" id="mobile"
											value="${mobile}" />
									</label>
									<input type="text" autocomplete="off" class="form-control withdraw-input" placeholder="<fmt:message key="msg.pg.login.validcodePut"/>"
										onblur="checkCode();" id="code" name="code" />
									<span class="error" id="error1"></span>
									<div class="send-btn-box">
										<input type="button" class="btn send-btn" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/>
									</div>
								</div>
								<!-- 谷歌验证码，如果开启双重验证就显示，默认不显示-->
								<div class="form-group" style="${google==2 ? '' : 'display:none' }">
									<label for="">
										<fmt:message key="msg.pg.userCenter.googleCode"/>
										<input type="hidden" name="secretKey" id="secretKey" value="${secretKey}" />
									</label>
									<input type="text" autocomplete="off" class="form-control withdraw-input" placeholder="<fmt:message key="msg.pg.userCenter.entergoogleCode"/>" id="google" name="google" />
									<span class="error" id="googleerror"> </span>
								</div>

								<div class="form-group-btn">
									<input type="hidden" name="coinId" id="coinId" value="${coinId}" />
									<input type="hidden" name="erc20usdt" id="erc20usdt1" value="${erc20usdt }"/>
									<input type="hidden" name="trc20usdt" id="trc20usdt1" value="${trc20usdt }"/>
									<button type="submit" class="btn sub-btn" 
									onclick="return submitopbtc(${minWithdraw.doubleValue()},${maxWithdraw.doubleValue()},${fee.doubleValue()},'${coinName}');">
									<fmt:message key="msg.pg.userCenter.confirmWithdraw"/></button>
								</div>
							</form>
						</div>

					</div>
				</div>
				<div class="main-wrap">
					<div class="block-title">
						<h2 class="lt">
							<fmt:message key="msg.pg.userCenter.withdrawRecord"/>
						</h2>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>

									<th>
										<fmt:message key="msg.pg.userCenter.orderNum"/>
									</th>
									<th>
										<fmt:message key="msg.pg.top.withdraw"/><fmt:message key="msg.pg.market.time"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.withdrawalsAd"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.withdrawalsMoney"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.withdrawalstate"/>
									</th>
									<!--
									<th>
										<fmt:message key="msg.pg.trade.option"/>
									</th>
									 -->
								</tr>
							</thead>
							<tbody>
								<tr>
									<c:choose>
										<c:when test="${pm.datas != null}">
											<c:forEach var="with" items="${pm.datas}">
												<tr>
													<td>
														<c:out value="${with.record_id}" />
													</td>
													<td>
														<span><ut:formatDate dateline="${with.dateline}"></ut:formatDate></span>
													</td>
													<td>
														<c:out value="${with.receice_card_no == '' ? with.remark : with.receice_card_no}" />
													</td>
													<td>
														<c:out value="${with.amount}" />
													</td>
													<td>
														<c:choose>
															<c:when test="${with.state==0}"><span><fmt:message key="msg.pg.userCenter.notAccept"/></span></c:when>
															<c:when test="${with.state==1}"><span style="color: green;"><fmt:message key="msg.pg.userCenter.hadAccept"/></span></c:when>
															<c:when test="${with.state==2}"><span style="color: red;"><fmt:message key="msg.pg.userCenter.refuse"/>(<c:out value="${with.remark}"></c:out>)</span></c:when>
															<c:otherwise>
																<span style="color: orange;"><fmt:message key="msg.pg.userCenter.accepting"/></span>
															</c:otherwise>
														</c:choose>
													</td>
													<!--
													<td>
														<a target="_blank" href="http://www.blockmeta.info/address/${with.receice_card_no}"><fmt:message key="msg.pg.userCenter.quereState"/></a>
													</td>
													 -->
												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<tr>
												<td colspan="5" height="30" align="center" bgcolor="#FFFFFF">
													<fmt:message key="msg.pg.userCenter.noHistory"/>。
												</td>
											</tr>
										</c:otherwise>
									</c:choose>

								</tr>
							</tbody>
						</table>
					</div>
					
					<ul class="pagination">
							<pg:pager url="${pageN}" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
								
								<pg:param name="coinId" value="${coinId }" />
								
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
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/withdraw.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
		
		<script type="text/javascript">
				  function sure(qr,url)
		   {
			   if(qr!=null)
			   {
				   tipAlert(qr);
				   $("#alert-windows").css("z-index","1001");
				   var flag = true;
				   var alertWin = document.getElementById("alert-windows");
						alertWin.addEventListener("click", function(event) {
							var tars = event.target || event.srcElement;
							if(flag) {
								if(tars.innerHTML != '') {
									document.location.href = url;
									flag = false;
								}									
							}	
				   })
			   }
			   else{
				   document.location.href = url;
				   }
				   
		   }
		
		</script>
		<script type="text/javascript">
			$(function(){
				$("#phoneCodebtn").click(function(){
					var tel = $("#mobile2").val();
					document.forms.addressForm.phoneCodebtn.disabled = true;
					$.post("<%=basePath%>center/sendOldTelCode.do",{Tel:tel}, function(n){
						if(n == "success"){
							for (i = 1; i <= secs; i++) {
								window.setTimeout("doUpdate2(" + i + ")", i * 1000);
							}
						} else {
							tipAlert("<fmt:message key='msg.pg.userBindPhone.sendFail'/>");
							document.forms.addressForm.phoneCodebtn.disabled = false;
						}
					}, "text");
				});
			});
		
			var secs = 60;
			var wait = secs * 1000;
			function doUpdate2(num) {
				if (num == '60') {
					if(vtype == 1)
						document.forms.addressForm.phoneCodebtn.value = " 发送短信验证码 ";
				} else {
					document.forms.addressForm.phoneCodebtn.disabled = true;
					wut = secs - num;
					if(vtype == 1){
						document.forms.addressForm.phoneCodebtn.value = " 已经发送 (" + wut + ")...";
					}else{
						document.forms.addressForm.phoneCodebtn.value = " 发送短信验证码";
					}
				}
			}
		</script>
		<script type="text/javascript">
			$(function() {
			    // 点击添加地址弹窗
			    var address = $("#withdraw-wrap");
			    var addzz = $("#tb-overlay-tixian");
			    var form = $("#addressForm");
	
			    $("#edit-add").click(function() {
			      form.show();
			    });
			    //关闭模态框
			    $(".close-alert").click(function() {
			      hide(address);
			      addzz.hide();
			    });
		  	});
		  
			function newAddressClick() {
				$("#withdrawaddress").blur();
				//点击设置提现地址链接弹窗
				var addzz = $("#tb-overlay-tixian");
				var address = $("#withdraw-wrap");
				show(address);
				addzz.show();
				$("#add-links").toggle(function() {
					form.show();
				}, function() {
					form.hide();
				});
			}
		    
		 </script>

		<script type="text/javascript">
			$(function(){
				$("#btnCode").click(function(){
					var tel = $("#mobile").val();
					document.forms.mobileform.btnCode.disabled = true;
					$.post("<%=basePath%>center/sendOldTelCode.do",{Tel:tel}, function(n){
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
				
				$("#withdrawTips").click(function() {
					$("#withdrawaddress").focus();
				});
			});
			var secs = 60;
			var wait = secs * 1000;
			var vtype = 1;
			function doUpdate(num) {
				if (num == '60') {
					if(vtype == 1)
						document.forms.mobileform.btnCode.value = " 发送短信验证码 ";
				} else {
					document.forms.mobileform.btnCode.disabled = true;
					wut = secs - num;
					if(vtype == 1){
						document.forms.mobileform.btnCode.value = " 已经发送 (" + wut + ")...";	
					}else{
						document.forms.mobileform.btnCode.value = " 发送短信验证码";
					}
				}
			}
				
			var message = {};
			message["zh_CN"] = {
				withdrawAm : "提现数额",
				withdrawLeast : "最少提现",
				actualAm : "实际到账金额：",
				notAm : "金额不正确",
				notEnough : "账户余额不足",
				codeNull : "验证码不能为空！",
				codeError : "请输入正确的验证码",
				withdrawNull : "提现地址不能为空",
				labelNull : "标签不能为空",
				warmTip : "温馨提示",
				addressError: "地址格式错误，请勿输入引号等特殊符号",
				sureTip : "确定",
				withdrawMaximum: "单笔最大提现额度为",
				tradePwdNull : "资金密码不能为空",
				tradePwdLtEightChar : "资金密码小于8个字符",
				tradePwdGtTwentyChar : "资金密码超过20个字符",
				pwdError : "请输入正确的密码",
				amountError : "金额不正确",
				dayMaxTips1: "当日最大提额度为",
				dayMaxTips2: ",您今日最多还可提",
			};
			message["zh_HK"] = {
				withdrawAm : "提現數額",
				withdrawLeast : "最少提現",
				actualAm : "實際到賬金額：",
				notAm : "金額不正確",
				notEnough : "帳戶餘額不足",
				codeNull : "驗證碼不能為空！",
				codeError : "請輸入正確的驗證碼",
				withdrawNull : "提現地址不能為空",
				labelNull : "標籤不能為空",
				warmTip : "溫馨提示",
				addressError: "地址格式錯誤，請勿輸入引號等特殊符號",
				sureTip : "確定",
				withdrawMaximum: "单笔最大提现额度为",
				tradePwdNull : "資金密碼不能為空",
				tradePwdLtEightChar : "資金密碼小於8個字元",
				tradePwdGtTwentyChar : "資金密碼超過20個字元",
				pwdError : "請輸入正確的密碼",
				amountError : "金額不正確",
				dayMaxTips1: "当日最大提额度为",
				dayMaxTips2: ",您今日最多还可提",
			};
			message["en_US"] = {
				withdrawAm : "Withdrawal amount",
				withdrawLeast : "Minimum withdrawal",
				actualAm : "Actual amount：",
				notAm : "Incorrect amount",
				notEnough : "Insufficient account balance",
				codeNull : "The verification code cannot be empty！",
				codeError : "Please enter the correct verification code",
				withdrawNull : "The withdrawal address cannot be empty",
				labelNull : "The label cannot be empty",
				addressError: "Address format error",
				warmTip : "Warm prompt",
				sureTip : "Sure",
				withdrawMaximum: "The maximum withdrawal amount for a single withdrawal is",
				tradePwdNull : "The Fund Password cannot be blank",
				tradePwdLtEightChar : "The Fund Password is less than 8 characters",
				tradePwdGtTwentyChar : "The Fund Password is more than 20 characters",
				pwdError : "Please enter correct password",
				amountError : "incorrect amount",
				dayMaxTips1: "The maximum amount to be drawn on that day is",
				dayMaxTips2: ",The most you can say today",
			};
			message["ja_JP"] = {
				withdrawAm : "出金金額",
				withdrawLeast : "最低出金",
				actualAm : "实际到账金额：",
				notAm : "金額が正しくありません",
				notEnough : "残高が不足しています",
				codeNull : "認証コードを必ず入力してください！",
				codeError : "正しい認証コードを入力してください",
				withdrawNull : "引き出しアドレスは空欄にすることはできません",
				labelNull : "タグはは空欄にすることはできません",
				addressError: "アドレスフォーマットエラー",
				warmTip: "ヒント",
				sureTip: "確定",
				withdrawMaximum: "単筆の最大引出し額は",
				tradePwdNull : "資金パスワードを空欄にすることはできません",
				tradePwdLtEightChar : "ファンドパスワードが8文字未満です",
				tradePwdGtTwentyChar : "資金パスワードが20文字を超えています",
				pwdError : "正しいパスワードを入力してください",
				amountError : "金額が正しくありません",
				dayMaxTips1: "当日の最大限度額は",
				dayMaxTips2: ",本日はごもっともです",
			};
			message["ko_KR"] = {
				withdrawAm : "인출 금액",
				withdrawLeast : "최소 인출",
				actualAm : "실제 도착 량：",
				notAm : "금액이 잘못되었습니다",
				notEnough : "계정 잔액 부족",
				codeNull : "인증 코드는 비워 둘 수 없습니다！",
				codeError : "올바른 인증 코드를 입력하십시오",
				withdrawNull : "출금 주소는 비워 둘 수 없습니다",
				labelNull : "라벨은 비워 둘 수 없습니다",
				warmTip : "팁",
				withdrawMaximum: "한 장의 최대 현금 인출 한도액은",
				addressError: "주소 형식이 잘못되었습니다. 따옴표와 같은 특수 기호를 입력하지 마십시오",
				sureTip : "결정",
				amountError : "금액이 잘못되었습니다",
				dayMaxTips1: "당일최대한공제액",
				dayMaxTips2: ",당신은 오늘 기껏해야 언급하실 수 있습니다",
			};
			message["ru_RU"] = {
				withdrawAm : "Сумма вывода",
				withdrawLeast : "Минимальный вывод",
				actualAm : "Актуальное количество：",
				notAm : "Неверное количество",
				notEnough : "Недостаточный остаток на счете ",
				codeNull : "Код подтверждения не может быть пустым！",
				codeError : "Пожалуйста, введите правильный код подтверждения",
				withdrawNull : "Адрес вывода не может быть пустым ",
				labelNull : "Метка не может быть пустой ",
				addressError: "Ошибка формата адреса ",
				warmTip : "подсказка ",
				sureTip : "Конечно",
				withdrawMaximum: "Максимальная сумма снятия с одной ручки",
				tradePwdNull : "Фондовый пароль не может быть пустым ",
				tradePwdLtEightChar : "Фондовый пароль содержит менее чем 8 символов ",
				tradePwdGtTwentyChar : "Фондовый пароль содержит более чем 20 символов ",
				pwdError : "Пожалуйста, введите правильный пароль ",
				amountError : "неверное количество",
				dayMaxTips1: "Максимальная линия была открыта в тот же день",
				dayMaxTips2: ",В лучшем случае, сегодня",
			};
			var MSG = message[locale];
		</script>
	</body>
</html>
