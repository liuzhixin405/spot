<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.VIPintergel"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">

		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/vip.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>
	<body>
    	<div class="add-windows" id="alert-pwd">
    		<div id="tb-overlay-duiping" class="tb-overlay display-none"></div>
    		<div class="add-wrap dp-alert-wrap invite-BFX" id="invite-BFX">
	    		<div class="risk-box" style="width: 380px">
		    		<form style="margin: 0 30px 0 20px;" action="<%=basePath%>center/vipTradeBuy.do" class="" method="post" id="mobileform"  name="mobileform">
						<div class="form-title">
							<h2 style="width:auto;text-align: center;font-size: 18px;color: #2B364B;float: none;">
								<fmt:message key="msg.memberLevelBuyAmount.buyConfirm"/>
							</h2>
							<a href="javascript:" class="close-icon icons-close dp-close-alert" style="position: absolute;top: 18px;right: 20px;width: 18px;height: 18px;display: block;"></a>
						</div>
						
						<div class="form-group">
							<label for="" style="color: #777777;" id="show_level_buy_amount1"></label>
							<label for="" style="color: #777777;" id="show_level_buy_amount2"></label>
						</div>
						<input type="hidden" id="select_level_id" name="select_level_id" value="" />
						
						<div class="form-group" style="${tel==1 || user_safetype==1 ? '' : 'display:none' }">
							<label for="">
								<fmt:message key="msg.pg.userCenter.phoneVaidcode"/>
								<input type="hidden" name="mobile" id="mobile"
									value="${mobile}" />
							</label>
							<input type="text" autocomplete="off" class="form-control form-control-input" placeholder="<fmt:message key="msg.pg.login.validcodePut"/>"
								onblur="checkCode();" id="code" name="code" />
							<span class="error" id="error1"></span>
							<span class="error" id="error"></span>
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
							<input type="text" autocomplete="off" class="form-control form-control-input" placeholder="<fmt:message key="msg.pg.userCenter.entergoogleCode"/>" id="google" name="google" />
							<span class="error" id="googleerror"> </span>
						</div>
						<div class="form-group-btn text-center">
							<button type="submit" class="btn sub-btn form-control-input" style="margin: 0 auto;" onclick="return doVipTradeBuyPost();"><fmt:message key="msg.pg.userHon.sure"/></button>
						</div>
					</form>
				</div>
			</div>
    	</div>
    	<div class="add-windows">
			<div id="tb-overlay-duiping2" class="tb-overlay display-none"></div>
			<div class="add-wrap dp-alert-wrap invite-BFX" id="invite-BFX2">
				<div class="risk-box" style="width: 380px">
					<form style="margin: 0 30px 0 20px;" action="" class="" method="post">
						<div class="form-title">
			          		<h2 style="width:auto;">
			          			<fmt:message key="msg.pg.userCenter.safePromat"/>
			          		</h2>
			          		<a href="javascript:" class="close-icon icons-close dp-close-alert2" style="position: absolute;top: 18px;right: 20px;width: 18px;height: 18px;display: block;" id="close-alert2"></a>
						</div>
						<div class="form-group">
							<label for="" style="color: red;">
								<fmt:message key="msg.pg.userCenter.gobind"/>
							</label>
						</div>
						<p style="text-align:center;">
							<a href="<%=basePath%>center/accountSafe.do" class="btn sub-btn order-btn-sub"><fmt:message key="msg.pg.userCenter.letbind"/></a>
 						</p>
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
						<h2>
							<fmt:message key="msg.pg.userCenter.VIPintergel"/>
						</h2>
					</div>
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
					<div class="box">
						<div class="vip-info">
							<p>
								<span><fmt:message key="msg.memberLevelBuyAmount.currentLevel"/><b>${name}</b>
									<a href="<%=basePath%>center/viplist.do" target="_blank" class="viplist-img">
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
									</a>
								</span>
							</p>
						</div>
					</div>
				</div>

				<br/>
<%--				<div class="main-wrap">--%>
<%--					<div class="block-title">--%>
<%--						<h2>--%>
<%--							<fmt:message key="msg.memberLevelBuyAmount.buyVIPLevel"/>--%>
<%--						</h2>--%>
<%--						<a href="<%=basePath%>center/vipTradeBuyRecordList.do" class="rt"><fmt:message key="msg.pg.userCenter.view"/><fmt:message key="msg.pg.index.more"/></a>--%>
<%--					</div>--%>
<%--					<c:if test="${nameID >= nameIDMax}">--%>
<%--						<div style="padding: 20px; line-height: 24px; margin-top: 20px; text-align: center;"  >--%>
<%--    						<h2>--%>
<%--								<fmt:message key="msg.memberLevelBuyAmount.alreadyTopLevel"/>--%>
<%--							</h2>--%>
<%--						</div>--%>
<%--&lt;%&ndash; 						<input type="text" readonly="readonly" class="order-input" value="" placeholder="<fmt:message key="msg.memberLevelBuyAmount.alreadyTopLevel"/>" maxlength="120"/> &ndash;%&gt;--%>
<%--					</c:if>--%>
<%--					<c:if test="${nameID < nameIDMax}">--%>
<%--						<div class="order-row">--%>
<%--							<form action="<%=basePath%>center/vipTradeBuy.do" method="post">--%>
<%--								<div class="">--%>
<%--									<div class="order-item">--%>
<%--										<label style="width: 150px;"><fmt:message key="msg.memberLevelBuyAmount.vipLevel"/></label>--%>
<%--											<c:forEach items="${levelRles}" var="l" varStatus="status">--%>
<%--												<c:if test="${status.first}">--%>
<%--													<select id="level_id" name="level_id" onchange="javascript:getLevelBuyAmount();">--%>
<%--												</c:if>--%>
<%--												<c:if test="${l.level_name eq name && l.level_buy_amount > 0 && nameID < l.level_id}">--%>
<%--													<option value="${l.level_id}" selected="selected">--%>
<%--													${l.level_name}--%>
<%--												</option>--%>
<%--												</c:if>--%>
<%--												<c:if test="${l.level_name ne name && l.level_buy_amount > 0 && nameID < l.level_id}">--%>
<%--													<option value="${l.level_id}">--%>
<%--													${l.level_name}--%>
<%--												</option>--%>
<%--												</c:if>--%>
<%--												--%>
<%--												<c:if test="${status.last}">--%>
<%--													</select>--%>
<%--												</c:if>--%>
<%--											</c:forEach>--%>
<%--									</div>--%>
<%--									<div class="order-item">--%>
<%--										<label style="width: 150px;"><fmt:message key="msg.pg.userCenter.lockAssets"/>Btcside<fmt:message key="msg.pg.market.amount"/></label>--%>
<%--										<input type="text" readonly="readonly" class="order-input" id="level_buy_amount" name="level_buy_amount" value="" placeholder="<fmt:message key="msg.memberLevelBuyAmount.amountRequired"/>" maxlength="120"/>--%>
<%--									</div>--%>
<%--									<div class="order-item">--%>
<%--										<label style="width: 150px;"><fmt:message key="msg.memberLevelBuyAmount.need"/>${payCoinName}<fmt:message key="msg.pg.market.amount"/></label>--%>
<%--										<input type="text" readonly="readonly" class="order-input" id="level_pay_amount" name="level_pay_amount" value="" placeholder="<fmt:message key="msg.memberLevelBuyAmount.amountRequired"/>" maxlength="120"/>--%>
<%--									</div>--%>
<%--									--%>
<%--									<div class="order-item">--%>
<%--										<label style="width: 150px;"></label>--%>
<%--										<input onclick="getAlert()" class="btn sub-btn order-btn-sub" id="" value="<fmt:message key="msg.memberLevelBuyAmount.toPay"/>"></input>--%>
<%--									</div>--%>
<%--								</div>--%>
<%--							</form>--%>
<%--						</div>--%>
<%--					</c:if>--%>
<%--				</div>--%>
				
				<br/>
<%-- 				<div class="main-wrap">
					<div class="block-title">
						<h2>
							<fmt:message key="msg.pg.userCenter.lastTenRecoded"/>
						</h2>
						<a href="<%=basePath%>center/vipRecordList.do" class="rt"><fmt:message key="msg.pg.userCenter.view"/><fmt:message key="msg.pg.index.more"/></a>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.index.recordTime"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.genre"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.getIntergel"/>
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="li" items="${list}" >
								<tr>
									<td>
										<span><ut:formatDate dateline="${li.dateline}"></ut:formatDate></span>
									</td>			        
									<td>
										<c:out value="${li.title}" />
									</td>
									<td>
										<c:if test="${li.credit_type_id == 11 }">
											<c:out value="${-li.credits}" />
										</c:if>
										<c:if test="${li.credit_type_id != 11 }">
											<c:out value="${li.credits}" />
										</c:if>
									</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div> --%>
<%--				<div class="main-wrap">--%>
<%--					<div class="block-title">--%>
<%--						<h2>--%>
<%--							<fmt:message key="msg.pg.userCenter.rankSystem"/>（<fmt:message key="msg.pg.userCenter.activeVal"/>）--%>
<%--						</h2>--%>
<%--					</div>--%>
<%--					<div class="trade-manage-main">--%>
<%--						<table cellpadding="0" cellspacing="0">--%>
<%--							<thead>--%>
<%--							  <tr>--%>
<%--							    <th><fmt:message key="msg.pg.userCenter.userLev"/></th>--%>
<%--								<th><fmt:message key="msg.pg.userCenter.putOrder"/></th>--%>
<%--								<th><fmt:message key="msg.pg.userCenter.eatOrder"/></th>--%>
<%--							    <th><fmt:message key="msg.pg.userCenter.withdrawCharge"/></th>--%>
<%--							    <th><fmt:message key="msg.pg.userCenter.returnInvite"/></th>--%>
<%--							    <th><fmt:message key="msg.memberLevelBuyAmount.amount"/></th>--%>
<%--							  </tr>--%>
<%--							</thead>--%>
<%--							<tbody id="levs-amounts">--%>
<%--								<c:forEach items="${levelRles}" var="l" varStatus="status">--%>
<%--									<tr>--%>
<%--										<td>--%>
<%--											<c:if test="${l.level_id==1}"><a class="viplist-img"><em class="icons-vip0"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==2}"><a class="viplist-img"><em class="icons-vip1"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==3}"><a class="viplist-img"><em class="icons-vip2"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==4}"><a class="viplist-img"><em class="icons-vip3"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==5}"><a class="viplist-img"><em class="icons-vip4"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==6}"><a class="viplist-img"><em class="icons-vip5"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==7}"><a class="viplist-img"><em class="icons-vip6"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==8}"><a class="viplist-img"><em class="icons-vip7"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==9}"><a class="viplist-img"><em class="icons-vip8"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==10}"><a class="viplist-img"><em class="icons-vip9"></em></a></c:if>--%>
<%--											<c:if test="${l.level_id==11}"><a class="viplist-img"><em class="icons-vip10"></em></a></c:if>--%>
<%--											<c:out value="${l.level_name }"></c:out>--%>
<%--										</td>--%>
<%--										<td>--%>
<%--											<fmt:formatNumber value="${l.pending_commission_rate }" type="percent" pattern="#.###%"/>--%>
<%--										</td>--%>
<%--										<td>--%>
<%--											<fmt:formatNumber value="${l.eat_commission_rate }" type="percent" pattern="#.###%"/>--%>
<%--										</td>--%>
<%--										<td>--%>
<%--											<fmt:formatNumber value="${l.withdraws_cash }" pattern="#0.##"/>--%>
<%--										</td>--%>
<%--										<td>--%>
<%--											<fmt:formatNumber value="${l.broker_commission_rate }" type="percent" pattern="#.###%"/>--%>
<%--										</td>--%>
<%--										<td>--%>
<%--											<c:if test="${l.level_buy_amount > 0 }">--%>
<%--												<fmt:formatNumber value="${l.level_buy_amount }" type="number"/>--%>
<%--											</c:if>--%>
<%--											<c:if test="${l.level_buy_amount <= 0 }">--%>
<%--												<fmt:message key="msg.memberLevelBuyAmount.null"/>--%>
<%--											</c:if>--%>
<%--										</td>--%>
<%--									</tr>--%>
<%--								</c:forEach>--%>
<%--							</tbody>--%>
<%--						</table>--%>
<%--					</div>--%>
<%--				</div>--%>
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
	window.onload = function() {
		var quans = document.getElementById("levs-amounts").children;
		var colors = ["#83c9ff","#5ab8ff","#3597e1","#2179bb","#12598e","#0b829b",
			"#1da697","#0fa343","#77b819","#cdcf12",];
		var htms = "";
		var ingels = "";
		for(var i = 0; i < quans.length; i++) {
			htms += "<li class=\"vip-proc\" data-before=\"\" data-after=\"\">" + "</li>";
			ingels += "<li class=\"vip-aingels\">" + "|" + "</li>";
		};
		$(".vip-gress").html(htms + "<div class='clear'></div>");
		$(".vip-ingels").html(ingels + "<div class='clear'></div>");
		var lis = $(".vip-gress li");
		var intergels = [];
		$(".lev-tdans").each(function() {
			if($(this)[0].innerText >= 100000) {
				var vans = $(this)[0].innerText/10000 + "万";
				intergels.push(vans);	
			}else {
				intergels.push($(this)[0].innerText);	
			}	
		})
		$(".vip-gress li").each(function(index){
			$(this).width("70");
			$(this).css({"border-right":"1px solid  " + colors[index],"background-color":colors[index],});
			$(this).attr("data-before","V" + [index]);
			$(".vip-gress li::before").css("background-color",colors[index]);	
		})
		$(".vip-gress li:last").width("0");
		$(".vip-gress li:last").css("border-right","none");
		$(".vip-gress li:first").width("76").css({"border-bottom-left-radius": "8px","border-top-left-radius": "8px",});
		$(".vip-gress li").eq(-2).width("76").css({"border-top-right-radius":"8px","border-bottom-right-radius":"8px"});
		$(".vip-ingels li").each(function(index){
			$(this).html(intergels[index]);	
		})
	}
	
	function doVipTradeBuyPost() {
		document.getElementById('select_level_id').value = document.getElementById('level_id').value;
	}

    function getLevelBuyAmount(){
    
    	document.getElementById('level_buy_amount').value = "";
    	
        var level_id = document.getElementById('level_id').value; //获取第一个输入框的值
        if (isNaN(level_id)) {  //如果为非数字，结果为空
            document.getElementById('level_buy_amount').value = "";
        } else {
			var levelRlesJson = eval(${levelRlesJson});
			var name = "${name}";
			var nameID = "${nameID}";
			var coinName = "Btcside";
			var payCoinName = "${payCoinName}";
			var rate = "${exchangeRate}";
			var level_buy_amount = 0;
			var level_buy_amount_for_nameID = 0;
			var level_id_show_name = "";
			for(var i in levelRlesJson){
				if (levelRlesJson[i].level_id == level_id) {
            		level_buy_amount = levelRlesJson[i].level_buy_amount;
					level_id_show_name = levelRlesJson[i].level_name;
				} else if (levelRlesJson[i].level_id == nameID) {
					level_buy_amount_for_nameID = levelRlesJson[i].level_buy_amount;
				} else {
// 					alert(levelRlesJson[i].level_id + levelRlesJson[i].level_buy_amount);
				}
			}
			var amount = level_buy_amount - level_buy_amount_for_nameID
			var payAmount = amount * rate;
			document.getElementById('level_buy_amount').value = amount;
			document.getElementById('level_pay_amount').value = payAmount;
			
			var label1 = document.getElementById("show_level_buy_amount1");
// 			label1.innerText="当前等级：" + name;
			label1.innerText="<fmt:message key='msg.memberLevelBuyAmount.currentLevel'></fmt:message>" + name;
			
			var label2 = document.getElementById("show_level_buy_amount2");
// 			label2.innerText="购买 " + level_id_show_name + " 等级所需支付 平台币 " + payCoinName + " " + amount + "个。";
        	label2.innerText="<fmt:message key='msg.memberLevelBuyAmount.buy'></fmt:message>" + " " 
        	+ level_id_show_name + " " + "<fmt:message key='msg.memberLevelBuyAmount.paymentRequiredPlatformCoin'></fmt:message>" + " " 
        	+ coinName + " " + amount + "<fmt:message key='msg.pg.trade.ge'></fmt:message>" + "，" 
        	+ "<fmt:message key='msg.memberLevelBuyAmount.paymentCoin'></fmt:message>" + " " 
        	+ payCoinName + " " + payAmount + "<fmt:message key='msg.pg.trade.ge'></fmt:message>" + "。";
        }
    }
	
	function getAlert() {
		var applayerror = "${applayerror}";
		if (applayerror) {
			var inviteBFX2 = $("#invite-BFX2");
			var dpzz2 = $("#tb-overlay-duiping2");
			show(inviteBFX2);
			dpzz2.show();
		} else {
			var inviteBFX = $("#invite-BFX");
			var dpzz = $("#tb-overlay-duiping");
			show(inviteBFX);
			dpzz.show();
		}
	}

	$(".dp-close-alert").click(function() {
		var inviteBFX = $("#invite-BFX");
		var dpzz = $("#tb-overlay-duiping");
		hide(inviteBFX);
		dpzz.hide();
	});
	
	$(".dp-close-alert2").click(function() {
		var inviteBFX2 = $("#invite-BFX2");
		var dpzz2 = $("#tb-overlay-duiping2");
		hide(inviteBFX2);
		dpzz2.hide();
	});
	
	getLevelBuyAmount();
	
</script>


<script type="text/javascript">
	$(function(){
		$("#btnCode").click(function(){
			var tel = $("#mobile").val();
			if (checkTel(false)) {
				document.forms.mobileform.btnCode.disabled = true;
				$.post("<%=basePath%>center/sendOldTelCode.do",{Tel:tel}, function(n){
					if(n == "success"){
	// 					alert("发送成功，请查收");
						for (i = 1; i <= secs; i++) {
							window.setTimeout("doUpdate(" + i + ")", i * 1000);
						}
					} else {
	// 					alert("发送失败，请稍候再试！");
// 						tipAlert("<fmt:message key='msg.pg.userBindPhone.sendFail'/>");
						tipAlert(n);
						document.forms.mobileform.btnCode.disabled = false;
					}
				}, "text");
			}
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
	
	function Timer() {
		document.forms.mobileform.btnCode.disabled = false;
	}
</script>


<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		phoneError : "号码错误",
		phoneAvailable : "该号码可用",
		phoneNull : "电话不能为空！",
		codeNull : "验证码不能为空！",
		codeError : "请输入正确的验证码",
		warmTip : "温馨提示",
		sureTip : "确定",
	};
	message["zh_HK"] = {
		phoneError : "號碼錯誤",
		phoneAvailable : "該號碼可用",
		phoneNull : "電話不能為空！",
		codeNull : "驗證碼不能為空！",
		codeError : "請輸入正確的驗證碼",
		warmTip : "溫馨提示",
		sureTip : "確定",
	};
	message["en_US"] = {
		phoneError : "Incorrect phone number",
		phoneAvailable : "Available phone number",
		phoneNull : "The phone number cannot be empty",
		codeNull : "Verification code cannot be empty",
		codeError : "Please enter correct verify code",
	};
	message["ja_JP"] = {
		phoneError : "エラー番号",
		phoneAvailable : "この電話番号は利用可能です",
		phoneNull : "電話番号を必ず入力してください！",
		codeNull : "認証コードを必ず入力してください！",
		codeError : "正しい認証コードを入力してください",
	};
	message["ko_KR"] = {
		phoneError : "잘못된 번호",
		phoneAvailable : "이 번호는 사용할 수 있습니다",
		phoneNull : "전화는 비워 둘 수 없습니다!",
		codeNull : "인증 코드는 비워 둘 수 없습니다!",
		codeError : "올바른 인증 코드를 입력하십시오",
		warmTip : "팁",
		sureTip : "결정",
	};
	message["ru_RU"] = {
		phoneError : "Неверный номер телефона",
		phoneAvailable : "Доступный номер телефона",
		phoneNull : "Номер телефона не может быть пустым",
		codeNull : "Код подтверждения не может быть пустым",
		codeError : "Пожалуйста, введите правильный код подтверждения",
		warmTip : "подсказка",
		sureTip : "Конечно"
	};
	var MSG = message[locale];
</script>


</body>
</html>