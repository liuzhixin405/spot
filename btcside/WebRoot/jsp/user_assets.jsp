<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.center"/></title>
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script>
<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
<jsp:include page="_top.jsp"></jsp:include>
<jsp:include page="_head.jsp"></jsp:include>
<div class="container center-cont">
	<jsp:include page="_user_left.jsp"></jsp:include>
	<div class="main-rt-box assets-main">
		<div class="main-wrap">
		    <div class="block-title" style="border-bottom: 0;">
				<h2><fmt:message key="msg.pg.userCenter.myAseets"/> </h2>
		    </div>
		    <div class="block-title-combine">
			    <ul class="combine-menu-ul">
					<li class="${actionName=='assets'  ? 'current' : ''}">
						<a href="<%=userPath %>assets.do"> <span></span><fmt:message key="msg.pg.head.bibiAcount"/></a>
					</li>
<%--					<li class="${actionName=='otc'  ? 'current' : ''}">--%>
<%--						<a href="<%=userPath %>otc.do"> <span>OTC</span><fmt:message key="msg.pg.workerOrderAdd.account"/></a>--%>
<%--					</li>--%>
<%--					<li class="${actionName=='center'  ? 'current' : ''}">--%>
<%--						<a href="<%=userPath %>center.do"> <span></span>USDT<fmt:message key="msg.pg.head.AccountContract"/></a>--%>
<%--					</li>--%>
<%--					<li class="${actionName=='usdr'  ? 'current' : ''}">--%>
<%--						<a href="<%=userPath %>nCenter/usdr.do"> <span>USDR</span><fmt:message key="msg.pg.head.AccountContract"/></a>--%>
<%--					</li>--%>
					<%--<li class="${actionName=='dCenter'  ? 'current' : ''}">
						<a href="<%=userPath %>dCenter.do"> <span></span><fmt:message key="msg.pg.head.AccountDContract"/></a>
					</li>--%>
			<%--		<li class="${actionName=='vCenter'  ? 'current' : ''}">
						<a href="<%=userPath %>vCenter.do"> <span></span><fmt:message key="msg.pg.userCenter.simulAseets"/></a>
					</li>--%>
					
				</ul>
			</div>
			<div class="info-box">
		        <ul class="info-list">
		          <li style="width: 100%;">
			          <b><fmt:message key="msg.pg.userCenter.totalAccount"/><fmt:message key="msg.pg.userCenter.zhehe"/></b> <br>
			          <span><em id="totalAccountNum">--</em> USDT</span>
			          <span style="font-size: 18px;"> ≈<em id="totalAccountNumCny">--</em> CNY</span>
		          </li>
				</ul>
				
		        <ul class="info-list">
		          <li style="width: 100%;">
		          	  <input type="hidden" id="usdRate" value="${usdRate }">
			          <b><fmt:message key="msg.pg.head.bibiAcount"/><fmt:message key="msg.pg.userCenter.zhehe"/></b> <br>
			          <span><em><fmt:formatNumber value="${usdtAvailable}" pattern="#0.0000"></fmt:formatNumber></em> USDT</span>
			          <span style="font-size: 18px;"> ≈<em><fmt:formatNumber value="${totalCny}" pattern="#0.00"></fmt:formatNumber></em> CNY</span>
		          </li>
				</ul>
			</div>
			<div class="trade-manage-main">
		        <table cellpadding="0" cellspacing="0">
		          <thead>
		            <tr>
		              <th><fmt:message key="msg.pg.userCenter.nameAssets"/></th>
		              <th><fmt:message key="msg.pg.market.chartTotalAmount"/></th>
		              <th><fmt:message key="msg.pg.userCenter.avaibleAssets"/></th>
		              <th><fmt:message key="msg.pg.userCenter.frozeAssets"/></th>
		              <c:if test="${ieoLock != null }">
		              <th>待释放</th>
		              </c:if>
		              <th width="260px"><fmt:message key="msg.pg.trade.option"/></th>
		            </tr>
		          </thead>
		          <tbody>
		          	<c:forEach var="bglists" items="${bqAccounts }">
		          		<c:if test="${bglists.coinName eq 'USDT' }">
				        <tr>
				        	<td class="clik-td">
				        		${bglists.coinName }
			        			<em class="que-icon icons-que td-usd" id="que-usd"></em>
			        			<div class="que-help que-help-usd" id="kcdj-txt">
									<span class="triangle-user"></span>
									<c:if test="${locale == 'zh_CN'}">
										<p id="detail-usd">${bglists.coinDescribleZh }</p>	
									</c:if>
									<c:if test="${locale == 'en_US'}">
										<p id="detail-usd">${bglists.coinDescribleEn }</p>
									</c:if>
									<c:if test="${locale == 'ja_JP'}">
										<p id="detail-usd">${bglists.coinDescribleJp }</p>
									</c:if>
									<c:if test="${locale == 'ko_KR'}">
										<p id="detail-usd">${bglists.coinDescribleKr }</p>
									</c:if>
									<c:if test="${locale == 'zh_HK'}">
										<p id="detail-usd">${bglists.coinDescribleHk }</p>
									</c:if>
								</div>
				        	</td>
				        	<td><fmt:formatNumber value="${bglists.total }" pattern="#0.####"/></td>
				        	<td><span class="buy"><fmt:formatNumber value="${bglists.available }" pattern="#0.####"/></span></td>
				        	<td><span class="sell"><fmt:formatNumber value="${bglists.freeze }" pattern="#0.####"/></span></td>
						  	 <c:if test="${ieoLock != null }">
							  	 <td><fmt:formatNumber value="0" pattern="#0.####"/></td>
						  	 </c:if>
				        	<td class="clik-td">
				        		<c:if test="${isSubUser != true}">
									<c:if test="${bglists.enableRecharge == true}">
										<a href="<%=userPath %>recharge.do?trc20=1&coinId=${bglists.coinId }" class="btn-blue"><fmt:message key="msg.pg.userCenter.recharged"/></a>
									</c:if>
									<c:if test="${bglists.enableWithdraw == true}">
										<a href="<%=userPath %>withdraw.do?trc20=1&coinId=${bglists.coinId }" class="btn-blue"><fmt:message key="msg.pg.top.withdraw"/></a>
									</c:if>
			          			</c:if>
<%--								<c:if test="${bglists.coinName == 'USDT'}">--%>
<%--									<div style="display: inline-block;" class="BTN">--%>
<%--										<a href="javascipt:void(0);" class="btn-exchange select-exchange"><fmt:message key="msg.pg.userCenter.exchangeUp"/><b class="btn-triange"></b></a>--%>
<%--										<div class="sub-exchange sub-exchange-us">--%>
<%--											<a href="<%=userPath %>exchange.do?originCoinName=${bglists.coinName }&targetCoinName=USDR">USDR</a>--%>
<%--										</div>--%>
<%--									</div>--%>
<%--								</c:if>--%>
		          			</td>
				        </tr>	
				        </c:if> 
				        <c:if test="${bglists.coinName eq 'USDR' }">
				        <tr>
				        	<td class="${bglists.coinDescribleZh ne '' ? 'clik-td' : ''}">
				        		${bglists.coinName }
				        		<c:if test="${bglists.coinDescribleZh ne '' }">
				        			<em class="que-icon icons-que td-usd" onmouseout="hideTip(${l.index})" onmouseover="showTip(${l.index})"></em>
				        			<div class="que-help que-help-usd" id="kcdj-txt-${l.index}">
										<span class="triangle-user"></span>
										<c:if test="${locale == 'zh_CN'}">
											<p id="detail-usd">${bglists.coinDescribleZh }</p>	
										</c:if>
										<c:if test="${locale == 'en_US'}">
											<p id="detail-usd">${bglists.coinDescribleEn }</p>
										</c:if>
										<c:if test="${locale == 'ja_JP'}">
											<p id="detail-usd">${bglists.coinDescribleJp }</p>
										</c:if>
										<c:if test="${locale == 'ko_KR'}">
											<p id="detail-usd">${bglists.coinDescribleKr }</p>
										</c:if>
										<c:if test="${locale == 'zh_HK'}">
											<p id="detail-usd">${bglists.coinDescribleHk }</p>
										</c:if>
									</div>	
				        		</c:if>
				        	</td>
				        	<td><fmt:formatNumber value="${bglists.total }" pattern="#0.####"/></td>
				        	<td><span class="buy"><fmt:formatNumber value="${bglists.available }" pattern="#0.####"/></span></td>
				        	<td><span class="sell"><fmt:formatNumber value="${bglists.freeze }" pattern="#0.####"/></span></td>
				        	 <c:if test="${ieoLock != null }">
							  	 <c:if test="${bglists.coinName ne 'BFX' }">
							  	 	<td><fmt:formatNumber value="0"/></td>
							  	 </c:if>
							  	 <c:if test="${bglists.coinName eq 'BFX' }">
							  	 	<td><fmt:formatNumber value="${ieoLock - ieoUnlock }" pattern="#0.####"/></td>
							  	 </c:if>
						  	 </c:if>
				        	<td class="clik-td">
				        		<c:if test="${isSubUser != true}">
				        			<c:if test="${bglists.enableRecharge == true}">
				          			<a href="<%=userPath %>recharge.do?<c:if test="${bglists.coinName eq 'USDT'}">trc20=1&</c:if>coinId=${bglists.coinId }" class="btn-blue"><fmt:message key="msg.pg.userCenter.recharged"/></a>
				          			</c:if>
				          			<c:if test="${bglists.enableWithdraw == true}">
				          			<a href="<%=userPath %>withdraw.do?<c:if test="${bglists.coinName eq 'USDT'}">trc20=1&</c:if>coinId=${bglists.coinId }" class="btn-blue"><fmt:message key="msg.pg.top.withdraw"/></a>
				          			</c:if>
			          			</c:if>
			          			<c:if test="${bglists.coinName ne 'USDT' and bglists.coinName ne 'BFC'}">
			          				<a href="<%=basePath %>spotTrade/spotCenterTrade.do?coinPairName=${bglists.coinName.toLowerCase() }usdt" class="btn-blue"><fmt:message key="msg.pg.userCenter.goTotrade"></fmt:message></a>
			          			</c:if>
<%--								<c:if test="${bglists.coinName == 'USDR'}">--%>
<%--									<div style="display: inline-block;" class="BTN">--%>
<%--										<a href="javascipt:void(0);" class="btn-exchange select-exchange"><fmt:message key="msg.pg.userCenter.exchangeUp"/><b class="btn-triange"></b></a>--%>
<%--										<div class="sub-exchange sub-exchange-us" style="left: 131px;">--%>
<%--											<a href="<%=userPath %>exchange.do?originCoinName=${bglists.coinName }&targetCoinName=USDT">USDT</a>--%>
<%--										</div>--%>
<%--									</div>--%>
<%--								</c:if>--%>
		          			</td> 	
				        </tr>
				        </c:if> 
			        </c:forEach>
			        <c:forEach var="bglists" items="${bqAccounts }" varStatus="l">
		          		<c:if test="${bglists.coinName ne 'USDT' and bglists.coinName ne 'USDR'}">
				        <tr>
				        	<td class="${bglists.coinDescribleZh ne '' ? 'clik-td' : ''}">
				        		${bglists.coinName }
				        		<c:if test="${bglists.coinDescribleZh ne '' }">
				        			<em class="que-icon icons-que td-usd" onmouseout="hideTip(${l.index})" onmouseover="showTip(${l.index})"></em>
				        			<div class="que-help que-help-usd" id="kcdj-txt-${l.index}">
										<span class="triangle-user"></span>
										<c:if test="${locale == 'zh_CN'}">
											<p id="detail-usd">${bglists.coinDescribleZh }</p>	
										</c:if>
										<c:if test="${locale == 'en_US'}">
											<p id="detail-usd">${bglists.coinDescribleEn }</p>
										</c:if>
										<c:if test="${locale == 'ja_JP'}">
											<p id="detail-usd">${bglists.coinDescribleJp }</p>
										</c:if>
										<c:if test="${locale == 'ko_KR'}">
											<p id="detail-usd">${bglists.coinDescribleKr }</p>
										</c:if>
										<c:if test="${locale == 'zh_HK'}">
											<p id="detail-usd">${bglists.coinDescribleHk }</p>
										</c:if>
									</div>	
				        		</c:if>
				        	</td>
				        	<td><fmt:formatNumber value="${bglists.total }" pattern="#0.####"/></td>
				        	<td><span class="buy"><fmt:formatNumber value="${bglists.available }" pattern="#0.####"/></span></td>
				        	<td><span class="sell"><fmt:formatNumber value="${bglists.freeze }" pattern="#0.####"/></span></td>
				        	 <c:if test="${ieoLock != null }">
							  	 <c:if test="${bglists.coinName ne 'BFX' }">
							  	 	<td><fmt:formatNumber value="0"/></td>
							  	 </c:if>
							  	 <c:if test="${bglists.coinName eq 'BFX' }">
							  	 	<td><fmt:formatNumber value="${ieoLock - ieoUnlock }" pattern="#0.####"/></td>
							  	 </c:if>
						  	 </c:if>
				        	<td class="clik-td">
				        		<c:if test="${isSubUser != true}">
				        			<c:if test="${bglists.enableRecharge == true}">
				          			<a href="<%=userPath %>recharge.do?<c:if test="${bglists.coinName eq 'USDT'}">trc20=1&</c:if>coinId=${bglists.coinId }" class="btn-blue"><fmt:message key="msg.pg.userCenter.recharged"/></a>
				          			</c:if>
				          			<c:if test="${bglists.enableWithdraw == true}">
				          			<a href="<%=userPath %>withdraw.do?<c:if test="${bglists.coinName eq 'USDT'}">trc20=1&</c:if>coinId=${bglists.coinId }" class="btn-blue"><fmt:message key="msg.pg.top.withdraw"/></a>
				          			</c:if>
			          			</c:if>
			          			<c:if test="${bglists.coinName ne 'USDT' and bglists.coinName ne 'BFC'}">
			          				<a href="<%=basePath %>spotTrade/spotCenterTrade.do?coinPairName=${bglists.coinName.toLowerCase() }usdt" class="btn-blue"><fmt:message key="msg.pg.userCenter.goTotrade"></fmt:message></a>
			          			</c:if>
		          			</td> 	
				        </tr>
				        </c:if>
			        </c:forEach>
		          </tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script> 
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script> 
<script type="text/javascript">
				  function input()
		 		  {
		 		       var msg="<fmt:message key='msg.pg.userCenter.whichEnterPwd'/>？";
					   if(confirm(msg)==true)
					   {
						changTradeInInput();
					   }
					  
		 		  }
				
		</script> 
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
		setNeedTradePwdFail : "设置资金密码输入失败"
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
		setNeedTradePwdFail : "設置資金密碼輸入失敗"
	};
	message["en_US"] = {
		codeNull : "The auth code cannot be blank",
		codeError : "Please enter correct auth code",
		tradePwdNull : "The Fund Password cannot be blank",
		tradePwdLtEightChar : "The Fund Password is less than 8 characters",
		tradePwdGtTwentyChar : "The Fund Password is more than 20 characters",
		pwdError : "Please enter correct password",
		setNoTradePwdSuccess : "Set no need to enter fund password successfully!",
		setNeedTradePwdSuccess : "Set need to enter fund password successfully!",
		setNeedTradePwdFail : "Set no need to enter fund password failure!"
	};
	message["ja_JP"] = {
		codeNull : "認証コードを空欄にすることはできません！",
		codeError : "正しい認証コードを入力してください",
		tradePwdNull : "資金パスワードを空欄にすることはできません",
		tradePwdLtEightChar : "ファンドパスワードが8文字未満です",
		tradePwdGtTwentyChar : "資金パスワードが20文字を超えています",
		pwdError : "正しいパスワードを入力してください",
		setNoTradePwdSuccess : "資金パスワードが正常に入力されていないことを設定",
		setNeedTradePwdSuccess : "資金パスワードの入力に成功しました",
		setNeedTradePwdFail : "資金パスワードの入力に失敗しました"
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
		setNeedTradePwdFail : "자금 조달 비밀번호 입력 실패"
	};
	message["ru_RU"] = {
		codeNull : "Код аутентификации не может быть пустым",
		codeError : "Пожалуйста, введите правильный код аутентификации",
		tradePwdNull : "Фондовый пароль не может быть пустым",
		tradePwdLtEightChar : "Фондовый пароль содержит менее чем 8 символов",
		tradePwdGtTwentyChar : "Фондовый пароль содержит более чем 20 символов",
		pwdError : "Пожалуйста, введите правильный пароль",
		setNoTradePwdSuccess : "Установка «не нужно вводить фондовый пароль» прошла успешно!",
		setNeedTradePwdSuccess : "Установка «нужно вводить фондовый пароль» прошла успешно!",
		setNeedTradePwdFail : "Произошла ошибка при установке «не нужно вводить фондовый пароль»!"
	};
	var MSG = message[locale];
	
	$(".btn-exchange").mouseover(function() {
		$(this).css("color","#4980E3");
		$(this).css("border","1px solid #f2f2f2");
		$(this).children().css("visibility","visible");
		$(this).next().show();	
	})
	
	$(".BTN").mouseleave(function() {
		$(".sub-exchange").hide();
		$(".btn-triange").css("visibility","hidden");
		$(".btn-exchange").css("border","1px solid white");
		$(".btn-exchange").css("color","#94abc0");
		$(".btn-exchange").css("backgroundColor","transparent");	
	})
	
	$(".sub-exchange").mouseleave(function() {
		$(this).hide();
		$(".btn-triange").css("visibility","hidden");
		$(".btn-exchange").css("border","1px solid white");
		$(".btn-exchange").css("color","#94abc0");
		$(".btn-exchange").css("backgroundColor","transparent");
	})
	
	$(".btn-exchange").click(function() {
		$(this).css("color","#333");
		$(this).css("backgroundColor","#ccc");
		$(this).children().css("visibility","visible");
		$(this).next().show();	
	})
	
	var ld = '${locale}';
	if(ld == 'en_US') {
		$('.sub-exchange-us').css('width','91px');
		$('.sub-exchange-us').css('left','156px');
		$('.singel-us').css("width",'91px');
		$('.singel-us').css("left",'85px');
		$(".td-usd").css('right','50px');
	}
	
	$("#que-usd").hover(
		function() {
			$("#kcdj-txt").show();
		}, function() {
			$("#kcdj-txt").fadeOut("slow");
		}
	);
	
	function hideTip(index) {
		$("#kcdj-txt-" + index).hide();
	}
	
	function showTip(index) {
		$("#kcdj-txt-" + index).show();
	}
</script>
</html>
