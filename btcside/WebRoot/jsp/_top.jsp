<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/withdraw.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>

<script type="text/javascript">
	$(function() {
	    var amount=null;
	   
	    if(amount==null){
		    loadAmount();
		}
		
	});
	
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "https://hm.baidu.com/hm.js?aa18022e0aa56cfad4421d33d82fde7f";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
</script>

<div class="add-windows" id="alert-windows-top"></div>

<!-- top开始 -->
<div class="top-wrap">
	<div class="top">
		<div class="top-lt">
			<a href="<%=basePath%>" class="logo lt" title="<fmt:message key="msg.pg.systemName"/>"></a>
			<ul class="relative">
				<%-- <li class="${(homeName eq 'home') ? 'active' : ''}">
					<a href="<%=homePath%>home.do" class="role-dex"><fmt:message key="msg.pg.head.home"/></a>
				</li> --%>
<%--				<li class="menu-item" id="trans-menu-item-li">--%>
<%--&lt;%&ndash;					href="<%=basePath%>trade/trade.do"&ndash;%&gt;--%>
<%--					<a href="javascript:;" class="role-dex curcolor" onclick="showTipsVerification()">USDT<fmt:message key="msg.pg.head.tradeMark"/></a>--%>
<%--				</li>--%>
<%--				<li class="menu-item" id="trans-menu-item-li">--%>
<%--					<a href="<%=basePath%>ntrade/usdr/trade.do" class="role-dex curcolor">USDR<fmt:message key="msg.pg.head.tradeMark"/></a>--%>
<%--				</li>--%>
<%--				<li class="menu-item ${homeName=='dTrade' ? 'current' : ''} ">--%>
<%--					<a style="cursor:pointer" href="<%=basePath%>dtrade/trade.do" class="role-dex curcolor"><fmt:message key="msg.pg.head.deliveryTrade"/></a>--%>
<%--				</li>--%>
				<li class="menu-item ${homeName=='spotTrade' ? 'current' : ''} ">
					<a style="cursor:pointer" href="<%=basePath%>spotTrade/spotTrade.do" class="role-dex curcolor"><fmt:message key="msg.pg.head.spotTrade"/></a>
				</li>
<%--				<li class="menu-item">--%>
<%--&lt;%&ndash;					href="<%=otcPath%>toBuyBtc"&ndash;%&gt;--%>
<%--					<a style="cursor:pointer" href="javascript:;" class="role-dex curcolor" onclick="showTipsVerification()"><fmt:message key="msg.pg.head.currencyTransaction"/></a>--%>
<%--				</li>--%>
				<%--<li class="menu-item ${homeName=='vtrade' ? 'current' : ''} " >
					<a href="<%=basePath%>vtrade/trade.do" style="cursor:pointer" class="role-dex curcolor"><fmt:message key="msg.pg.head.vTradeMark"/></a>
				</li>--%>
				<c:if test="${vTransTypes.size() == 0}">
				<li class="${homeName=='order' ? 'current' : ''} home-order">
					<a href="<%=orderPath%>orderList.do" class="role-dex curcolor"><fmt:message key="msg.pg.head.orderService"/></a>
				</li>
				</c:if>	
<%--				<li class="menu-item" id="trans-menu-item-li">--%>
<%--					<a href="<%=basePath%>financial/pledge.do" class="role-dex curcolor"><fmt:message key="msg.pg.top.borrow"/></a>--%>
<%--				</li>--%>
				<li class="${(homeName=='news' || homeName=='help') && actionName != 'graphic' ? 'active' : ''}">
					<a href="<%=newsPath%>notice.do" class="role-dex curcolor"><fmt:message key="msg.pg.head.help"/></a>
				</li>
				<!-- 
				<li class="${actionName=='graphic' ? 'active' : ''}">
					<a href="<%=basePath%>help/graphic.do" class="role-dex curcolor" target="_blank"><fmt:message key="msg.pg.head.course"/></a>
				</li>
				
				<li class="${actionName=='activity' ? 'active' : ''}">
					<a href="<%=basePath%>activity/ranking.do" class="role-dex curcolor" target="_blank"><fmt:message key="msg.pg.activity.lastacTity"/></a>
				</li> -->
				<li class="menu-item">
					<a href="javascript:;" class="role-dex curcolor" onclick="goToDownLoad()">APP</a>
					<div class="menu-item-arraw" style="left: 5px; top: 46px;"></div>
					<div class="sub-menu-item app-sub-item" style="width:120px;height: 120px; padding: 5px;">
					<img alt="app" style="width: 100%;height: 100%;" src="<%=basePath%>common/images/qrcode/<%=serverName%>/app.png?v=2">
					</div>
				</li>
			</ul>
		</div>
		<ul class="top-rt">
			<li class="menu-item locale" id="locale-li"  
				<c:if test="${homeName eq '' || homeName eq 'home'}">
	               	 style = "line-height: 19px;"
	            </c:if>>
	            <c:if test="${locale == 'zh_CN'}">
	            	<span class="top-lang">简体中文</span>	
	            </c:if>
	            <c:if test="${locale == 'zh_HK'}">
	            	<span class="top-lang">繁體中文</span>	
	            </c:if>
	            <c:if test="${locale == 'en_US'}">
	            	<span class="top-lang">English</span>	
	            </c:if>
<%--	            <c:if test="${locale == 'ja_JP'}">--%>
<%--	            	<span class="top-lang">日本語</span>	--%>
<%--	            </c:if>--%>
<%--	            <c:if test="${locale == 'ko_KR'}">--%>
<%--	            	<span class="top-lang">한국어</span>	--%>
<%--	            </c:if>--%>
<%--	            <c:if test="${locale == 'ru_RU'}">--%>
<%--	            	<span class="top-lang">Русский язык</span>	--%>
<%--	            </c:if>--%>
	            <em class="triger-domn-arrow triger-up-arrow-head"></em>
				<div class="menu-item-arraw" style="left: 23px; top: 44px;" id="submenu-arraw"></div>
	          	<div class="locale-sub-item sub-menu-color" style="left: -45px; top: 49px;" id="submenu">
					<a class="a" onclick="changeTo('en_US');" style="text-align: center;border-top: 1px solid #EFF1F2;color: #4A5F78 !important;" href="javascript:;">
						<%--<span class="icon-en_US icon-span"></span>--%>English
					</a>
<%--					<a class="a" onclick="changeTo('ja_JP');" style="text-align: center;border-top: 1px solid #EFF1F2;color: #4A5F78 !important;" href="javascript:;">--%>
<%--						&lt;%&ndash;<span class="icon-ja_JP icon-span"></span&ndash;%&gt;日本語--%>
<%--					</a>--%>
<%--					<a class="a" onclick="changeTo('ko_KR');" style="text-align: center;border-top: 1px solid #EFF1F2;color: #4A5F78 !important;" href="javascript:;">--%>
<%--						&lt;%&ndash;<span class="icon-ko_KR icon-span"></span>&ndash;%&gt;한국어--%>
<%--					</a>--%>
<%--					<a class="a" onclick="changeTo('ru_RU');" style="text-align: center;border-top: 1px solid #EFF1F2;color: #4A5F78 !important;" href="javascript:;">--%>
<%--						&lt;%&ndash;<span class="icon-ru_RU icon-span"></span>&ndash;%&gt;Русский язык--%>
<%--					</a>--%>
					<a class="a" onclick="changeTo('zh_CN');" style="text-align: center;border-top: 1px solid #EFF1F2;color: #4A5F78 !important;" href="javascript:;">
						<%--<span class="icon-zh_CN icon-span"></span>--%>简体中文
					</a>
					<a class="a" onclick="changeTo('zh_HK');" style="text-align: center;border-top: 1px solid #EFF1F2;color: #4A5F78 !important;" href="javascript:;">
						<%--<span class="icon-zh_CN icon-span"></span>--%>繁體中文
					</a>
				</div>
			</li>
			<c:if test="${sessionScope.userinfo==null }">
				<li>
					<a href="<%=basePath%>login/login.do" class="top-login"><fmt:message key="msg.pg.top.login"/></a>
				    <a href="<%=basePath%>register/register.do" class="top-register"><fmt:message key="msg.pg.top.register"/></a>
				</li>
				<li style="margin-right: 0;">

				</li>
			</c:if>
			
<%--			<c:if test="${sessionScope.userinfo!=null}">--%>
<%--			<li class="message-control" --%>
<%--				<c:if test="${homeName eq '' || homeName eq 'home'}">--%>
<%--               	 style = "line-height: 19px; height: 50px;"--%>
<%--            	</c:if>>--%>
<%--				<a href="<%=orderPath%>orderList.do" class="ring-us" style="display: inline-block;"><strong><span class="icons-ring"></span></strong></a>--%>
<%--				<div id="messageCountDiv" class="msg-prompt">--%>
<%--					<span id="messageCountNum"></span>	--%>
<%--				</div>--%>
<%--				<div class="message-top-menu">	--%>
<%--				</div>							--%>
<%--			</li>--%>
<%--			</c:if>--%>
			<c:if test="${sessionScope.userinfo!=null}">
				<li class="menu-control" id="top-user-control" style="margin-right: 0;"> <strong style="height: 100%;">${sessionScope.userinfo.f_user_email}
				<a href="<%=basePath%>center/viplist.do" target="_blank">
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
          </strong>
          <div class="top-menu-account">
            <div class=""></div>
            <ul>
              <li class="account-mes">
              <a href="<%=userPath %>assets.do" class="first ownColor" style="padding-left: 0px !important;text-align: center;line-height: 40px;">
              	<font id="amountAccount"></font> USDT
              </a>
              </li>
              <li class="account-mes">
                <a href="<%=userPath%>assets.do" class="first ownColor" style="line-height: 40px;">
                  <i class="iconfont icon-icon-safe-light"></i><fmt:message key="msg.pg.userCenter.assetMag"/>
                </a>
              </li>
              <li class="account-mes">
                <a href="<%=userPath %>accountSafe.do" class="first ownColor" style="line-height: 40px;">
                 <i class="iconfont icon-icon-user-light"></i><fmt:message key="msg.pg.userCenter.center"/>
                </a>
              </li>
              <li class="account-mes">
                <a onClick="logoutClick()" href="#" style="line-height: 40px;" class="ownColor">
                  <i class="iconfont icon-icon-out-light"></i><fmt:message key="msg.pg.top.layout"/></a>
              </li>
            </ul>
          </div>
        </li>
		</c:if> 
		</ul>
		<div class="clear"></div>
		<div class="tips_verification" id="tipsVerifications">
			<div class="verification_operate">
				<div class="title">
					<span>温馨提示</span>
					<img src="../common/images/close_pages.png" alt="关闭" srcset="" onclick="closeTips()">
				</div>
				<div class="contents">
					<p>暂未开启，敬请期待!</p>
					<button onclick="closeTips()">确认</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">	
	var loadLogin = ${sessionScope.userinfo != null};
	var btn=document.getElementById('locale-li');
	btn.onclick=function(){
		var subArraw = document.getElementById('submenu-arraw');
		var subMenu = document.getElementById('submenu');
		
		if (subArraw.style.display=="block") {
			subArraw.style.display="none";
			subMenu.style.display="none";
		} else {
			subArraw.style.display="block";
			var subMenu = document.getElementById('submenu');
			subMenu.style.display="block";
		}
	}
	
	function menuClick(menu) {
		var item = $(menu).children(".sub-menu-item");
		var arraw = $(menu).children(".menu-item-arraw");
		item.css("display", "block");
		arraw.css("display", "block");
		
	}
	var ld = '${locale}';
	if(ld == 'en_US' || ld == 'ja_JP') {
		$(".top-lt ul li").addClass("mar-item");
		$(".top-rt li").addClass("mar-item");
	}
	if(ld == 'en_US') {
	}
	
	function hideAllSubTransMenu() {
		for (var i = 0; i < 10; i++) {
			$("#sub-menu-item-" + i).hide();
		}
	}
	
	function spotClick(type) {
		$("#alert-windows-top").show();
		var insidehtm = "<div id='tb-overlay-duiping' class='tb-overlay'></div><div class='add-wrap alert-wrap' style='display: block;' id='invite-BFX'>"
					+"<div class='dialogs-title'>"
					+"<h2 class='lt'>温馨提示</h2>"
					+"<a class='close-icon icons-close' id='close-alert' href='javascript:closeTopAlert()'></a>"
					+"</div>"
					+"<div class='dialogs-body'>"
					+"<p style='text-align: center; font-size: 16px; margin-bottom: 30px;'>"+ type +"</p>"
					+"<div class='form-group-btn text-center'>"
						+"<a class='btn sub-btn center' style='margin-right: 0px;' href='javascript:closeTopAlert();'>确定</a>"
					+"</div>"
					+"</div>"
				+"</div>";
		$("#alert-windows-top").html(insidehtm);
		center($('.alert-wrap'));
	}
	
	function closeTopAlert() {
		$("#alert-windows-top").hide();
	}
	
	$(document).ready(function(){
		$("#trans-menu-item-li").hover(function(){
		},function(){
			hideAllSubTransMenu();
		});
		
		$(".trans-menu-item-a").hover(function(){
			hideAllSubTransMenu();
			$("#sub-" + $(this).attr('id')).show();
		},function(){
		});
	});
	function showTipsVerification() {
		$("#tipsVerifications").show();
	}
	function closeTips() {
		$("#tipsVerifications").hide();
	}
	function goToDownLoad() {
		document.location.href = basePath + "download.do";
	}
</script>
<!-- top结束-->