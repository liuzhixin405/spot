<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/withdraw.js?<%=rjs%>"></script>

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

<div class="full-head full-title-color">
	<div class="head-lt sub-head-marMin"><a href="<%=basePath%>" class="bfx-logo lt" title="<fmt:message key="msg.pg.systemName"/>"></a></div>
	<div class="head-lt sub-head-marMiddle">
		<div class="menu-item arrow-menu-item ${homeName=='spotTrade' ? 'current' : ''} ">
			<a onClick="menuClick(this.parent)" style="cursor:pointer;" class="role-dex">
			<%--<c:if test="${homeName=='vtrade'}">
				<fmt:message key="msg.pg.head.vTradeMark"/>
			</c:if>--%>
			<%--<c:if test="${homeName=='dtrade'}">
				<fmt:message key="msg.pg.head.deliveryTrade"/>
			</c:if>--%>
			<c:if test="${homeName=='trade'}">
				USDT<fmt:message key="msg.pg.head.tradeMark"/>
			</c:if>
			<c:if test="${homeName=='spotTrade'}">
				<fmt:message key="msg.pg.head.spotTrade"/>
			</c:if>
<%--			<c:if test="${homeName=='usdr'}">--%>
<%--				USDR<fmt:message key="msg.pg.head.tradeMark"/>--%>
<%--			</c:if>--%>
			<em class="triger-arrow triger-arrow-head"></em></a>
			<div class="menu-item-arraw"></div>
			<div class="sub-menu-item">
<%--				href="<%=basePath%>trade/trade.do"--%>
<%--				<a href="javascript:;" onclick="showTipsVerification()">USDT<fmt:message key="msg.pg.head.tradeMark"/></a>--%>
<%--				<a href="<%=basePath%>ntrade/usdr/trade.do">USDR<fmt:message key="msg.pg.head.tradeMark"/></a>--%>
				<%--<a href="<%=basePath%>dtrade/trade.do"><fmt:message key="msg.pg.head.deliveryTrade"/></a>--%>
				<a href="<%=basePath%>spotTrade/spotTrade.do"><fmt:message key="msg.pg.head.spotTrade"/></a>
				<%--<a href="<%=basePath%>vtrade/trade.do"><fmt:message key="msg.pg.head.vTradeMark"/></a>--%>
<%--	href="<%=otcPath%>toBuyBtc"--%>
<%--				<a href="javascript:;" onclick="showTipsVerification()"><fmt:message key="msg.pg.head.currencyTransaction"/></a>--%>
			</div>
		</div>	
	</div>
	<div class="head-lt sub-head-marMiddle">
		<div id="trans-menu-item-li" class="menu-item arrow-menu-item ${homeName=='spotTrade' ? 'current' : ''} ">
			<a onClick="menuClick(this.parent)" class="<c:if test="${homeName=='trade' || homeName=='vtrade' || homeName=='dtrade' || homeName == 'usdr'}">role-dexx</c:if><c:if test="${homeName == 'spotTrade'}">role-dex</c:if>">
			<c:if test="${homeName == 'spotTrade'}">
				${coinName}/${marketName}
			</c:if>
			<c:if test="${homeName == 'trade' || homeName == 'dtrade' || homeName == 'usdr'}">
				<c:if test="${locale == 'zh_CN'}">${currentTransaction.transaction_show_name }</c:if>
				<c:if test="${locale == 'zh_HK'}">${currentTransaction.transaction_show_name_hk }</c:if>
				<c:if test="${locale == 'en_US'}">${currentTransaction.transaction_show_name_en }</c:if>
				<c:if test="${locale == 'ja_JP'}">${currentTransaction.transaction_show_name_jp }</c:if>
				<c:if test="${locale == 'ko_KR'}">${currentTransaction.transaction_show_name_kr }</c:if>
				<c:if test="${locale == 'ru_RU'}">${currentTransaction.transaction_show_name_en }</c:if>
				<fmt:message key="msg.pg.index.contract"/>
			</c:if>
			<c:if test="${homeName == 'vtrade'}">
				${typeCodeName}<fmt:message key="msg.pg.index.contract"/>
			</c:if>
			<c:if test="${homeName == 'spotTrade'}">
			<em class="triger-arrow triger-arrow-head"></em>
			</c:if>
			</a>
			<div class="menu-item-arraw"></div>
			<c:if test="${homeName=='spotTrade'}">
				<div class="sub-menu-item">
					<c:forEach items="${coinPairs}" var="pair" varStatus="st">
						<c:if test="${pair.coin_pair_name eq 'bfxusdr'}"><a href="<%=basePath%>spotTrade/spotTrade.do?coinPairId=${pair.id}">${pair.coin_name }/${pair.market_coin_name }</a></c:if>
					</c:forEach>
					<c:forEach items="${coinPairs}" var="pair" varStatus="st">
						<c:if test="${pair.coin_pair_name ne 'bfxusdr'}"><a href="<%=basePath%>spotTrade/spotTrade.do?coinPairId=${pair.id}">${pair.coin_name }/${pair.market_coin_name }</a></c:if>
					</c:forEach>
				</div>
			</c:if>
		</div>
	</div>
	<div class="head-lt sub-head-marMax">	
			<c:if test="${homeName=='spotTrade'}">
			    <p class="label"><fmt:message key="msg.pg.trade.latestPrice"/></p>
				<p class="number" id="lastPrice">--</p>
			</c:if>
			<c:if test="${homeName=='vtrade' || homeName=='trade' || homeName=='dtrade' || homeName == 'usdr'}">
			<a href="<%=basePath %>bpiAction/bpiIndex.do?code=${typeCode}" target="_blank">
			<p class="label">${typeCode} <fmt:message key="msg.pg.top.exponent"/></p>
			<p class="number" id="${typeCode.toLowerCase()}-bpi2">
				<c:forEach items="${listMap}" var="index">
					<c:if test="${index.typeCode eq typeCode.toLowerCase()}">
						<span id="${typeCode.toLowerCase()}-bpi2-span" class="${index.indexFudu.startsWith('-') ? 'sell' : 'buy'}">
						<fmt:formatNumber type="number" maxFractionDigits="8" groupingUsed="false" value="${index.indexPrice.doubleValue() }"/></span>
					</c:if>
				</c:forEach>
			</p>
			</a>
			</c:if>
	</div>
	<c:if test="${homeName=='vtrade' || homeName=='trade'  || homeName=='dtrade' || homeName == 'usdr'}">
	<div class="head-lt">
		<p class="label mark-price"><fmt:message key="msg.pg.trade.markPrice"/><i class="iconfont icon-tishi relative">
			<span class="show-index-box hide">
                  	<span class="triangle"></span>
				<span class="index-info-detail">
					<fmt:message key="msg.pg.trade.markDetails"/>
				</span> 
			</span>
			</i>
		</p>
		<p class="number" id="markPrice">${markPrice} </p>
	</div>
	</c:if>
	<div class="head-lt">
		<c:if test="${homeName=='spotTrade'}">
		<p class="label"><fmt:message key="msg.pg.trade.fulldayHight"/></p>
		<p class="number" id="todayHighCoin">--</p>
		</c:if>
		<c:if test="${homeName=='vtrade' || homeName=='trade' || homeName=='dtrade' || homeName == 'usdr'}">
		<p class="label"><fmt:message key="msg.pg.trade.fulldayHight"/></p>
		<p class="number" id="todayHigh">${todayHigh.doubleValue()} </p>
		</c:if>
	</div>
	<div class="head-lt">
		<c:if test="${homeName=='spotTrade'}">
		<p class="label"><fmt:message key="msg.pg.trade.fulldayLow"/></p>
		<p class="number" id="todayLowCoin">--</p>
		</c:if>
		<c:if test="${homeName=='vtrade' || homeName=='trade' || homeName=='dtrade' || homeName == 'usdr'}">
		<p class="label"><fmt:message key="msg.pg.trade.fulldayLow"/></p>
		<p class="number" id="todayLow">${todayLow.doubleValue()} </p>
		</c:if>
	</div>
	<div class="head-lt">
		<c:if test="${homeName=='spotTrade'}">
		<p class="label"><fmt:message key="msg.pg.trade.fulldayMoney"/></p>
		<p class="number" id="tradesCoin">--</p>
		</c:if>
		<c:if test="${homeName=='vtrade' || homeName=='trade' || homeName=='dtrade' || homeName == 'usdr'}">
		<p class="label"><fmt:message key="msg.pg.trade.fulldayMoney"/></p>
		<p class="number" id="volumn"><fmt:formatNumber value="${trades}" pattern="#.##"></fmt:formatNumber>${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? 'USDR' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT'))} </p>
		</c:if>
	</div>
	<c:if test="${fundingFeeRate != null}">
	<div class="head-lt">
		<a href="<%=basePath %>news/otherdo.do?id=254" target="_blank">
			<p class="label funding-ratelist"><fmt:message key="msg.pg.feeRate.fundingRate"/>
				<i class="iconfont icon-tishi relative">
					<span class="show-rate-box hide">
	                   	<span class="triangle"></span>
						<span class="index-info-detail" style="text-align: left;"><fmt:message key="msg.pg.trade.feeRateTips"/><font class="clickDetil"><fmt:message key="msg.pg.trade.particulars"/></font></span>
					</span>
				</i>
			</p>
			<p class="feeRate-time-box">
				<p class="number" id="fundingFeeRate" style="display: inline-block;"><fmt:formatNumber value="${fundingFeeRate * 100}" pattern="#.####"></fmt:formatNumber>%</p>
				<span class="feeRate-time">
				(<em id="time_hour">00</em>:<em id="time_minute">00</em>:<em id="time_second">00</em>)
					<span class="show-feerate-box hide">
		                <span class="triangle"></span>
						<span class="index-info-detail" style="text-align: left;"><fmt:message key="msg.pg.feeRate.feeRateTimeRemaining"/></span>
					</span>
				</span>
			</p>
		</a>
		
	</div>
	</c:if>
	<div class="head-rt" style="position: absolute; right: 25px;">
		<div class="menu-item locale lt" id="locale-li"  
			<c:if test="${homeName eq '' || homeName eq 'home'}">
	              	 style = "line-height: 19px;"
			</c:if>>
			<c:if test="${locale == 'zh_CN'}">
				<span class="full-top-lang">简体中文</span>	
			</c:if>
			<c:if test="${locale == 'zh_HK'}">
				<span class="full-top-lang">繁體中文</span>	
			</c:if>
			<c:if test="${locale == 'en_US'}">
				<span class="full-top-lang">English</span>	
			</c:if>
<%--			<c:if test="${locale == 'ja_JP'}">--%>
<%--				<span class="full-top-lang">日本語</span>	--%>
<%--			</c:if>--%>
<%--			<c:if test="${locale == 'ko_KR'}">--%>
<%--	            <span class="full-top-lang">한국어</span>	--%>
<%--           	</c:if>--%>
<%--           	<c:if test="${locale == 'ru_RU'}">--%>
<%--	            <span class="full-top-lang">Русский язык</span>	--%>
<%--           	</c:if>--%>
			<em class="triger-arrow triger-arrow-head" style="top: 22px;right: 28px;"></em>
			<div class="menu-item-arraw" style="left: 18px; top: 40px;" id="submenu-arraw"></div>
	         	<div class="sub-menu-item locale-sub-item" style="left: -45px; top: 49px;" id="submenu">
					<a class="a" onclick="changeTo('en_US');" style="text-align: center;" href="javascript:;">
						<%--<span class="icon-en_US icon-span"></span>--%>English
					</a>
<%--					<a class="a" onclick="changeTo('ja_JP');" style="text-align: center;" href="javascript:;">--%>
<%--						&lt;%&ndash;<span class="icon-ja_JP icon-span"></span>&ndash;%&gt;日本語--%>
<%--					</a>--%>
<%--					<a class="a" onclick="changeTo('ko_KR');" style="text-align: center;" href="javascript:;">--%>
<%--						&lt;%&ndash;<span class="icon-ko_KR icon-span"></span>&ndash;%&gt;한국어--%>
<%--					</a>--%>
<%--					<a class="a" onclick="changeTo('ru_RU');" style="text-align: center;" href="javascript:;">--%>
<%--						&lt;%&ndash;<span class="icon-ru_RU icon-span"></span>&ndash;%&gt;Русский язык--%>
<%--					</a>--%>
					<a class="a" onclick="changeTo('zh_CN');" style="text-align: center;" href="javascript:;">
						<%--<span class="icon-zh_CN icon-span"></span>--%>简体中文
					</a>
					<a class="a" onclick="changeTo('zh_HK');" style="text-align: center;" href="javascript:;">
						<%--<span class="icon-zh_CN icon-span"></span>--%>繁體中文
					</a>
				</div>
			</div>
		<c:if test="${sessionScope.userinfo==null }">
			<div class="lt">
				<a href="<%=basePath%>login/login.do" class="marginrft"><fmt:message key="msg.pg.top.login"/></a>
			</div>
			<div class="lt">
				<a href="<%=basePath%>register/register.do" class="full-register"><fmt:message key="msg.pg.top.register"/></a>
			</div>
		</c:if>
		<c:if test="${sessionScope.userinfo!=null}">
		<div class="menu-control lt" id="top-user-control"> <strong style="height: 100%;">${sessionScope.userinfo.f_user_email}
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
          <div class="top-menu">
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
        </div>
		</c:if> 		
	</div>
	<div class="tips_verification" id="tipsVerification">
		<div class="verification_operate">
			<div class="title">
				<span>温馨提示</span>
				<img src="../common/images/close_pages.png" alt="关闭" srcset="" onclick="closeTips()">
			</div>
			<div class="content">
				<p>暂未开启，敬请期待!</p>
				<button onclick="closeTips()">确认</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var loadLogin = ${sessionScope.userinfo != null};
	var indexFuduUp = ($("#${typeCode.toLowerCase()}-bpi2-span").attr("class") != "sell");
	
	function hideAllSubTransMenu() {
		for (var i = 0; i < 10; i++) {
			$("#sub-menu-item-" + i).hide();
		}
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
		
		checkLogin();
	});
	function showTipsVerification() {
		$("#tipsVerification").show();
	}
	function closeTips() {
		$("#tipsVerification").hide();
	}
</script>