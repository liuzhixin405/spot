<%@page contentType="text/html;charset=utf-8"%>
<%@ include file="_basepage.jsp"%>

<div class="left-menu">
	<div class="menu-block">
		<div class="menu-title">
			<h3>
				<span class="icon-fund-btn"></span><fmt:message key="msg.pg.userCenter.assetMag"/>
			</h3>
		</div>
		<ul class="left-menu-ul">
			<li class="${actionName=='assets' || actionName=='center' || actionName=='vCenter' ? 'current' : ''}">
				<a href="<%=userPath %>assets.do"> <span></span><fmt:message key="msg.pg.userCenter.myAseets"/></a>
			</li>
		<%-- 	<li class="${actionName=='center'  ? 'current' : ''}">
				<a href="<%=userPath %>center.do"> <span></span><fmt:message key="msg.pg.head.AccountContract"/></a>
			</li>
			<li class="${actionName=='vCenter'  ? 'current' : ''}">
				<a href="<%=userPath %>vCenter.do"> <span></span><fmt:message key="msg.pg.userCenter.simulAseets"/></a>
			</li> --%>
<%--			<li class="${actionName=='money'  ? 'current' : ''}">--%>
<%--				<a href="<%=userPath %>money.do"> <span></span><fmt:message key="msg.pg.userCenter.assetCaptia"/></a>--%>
<%--			</li>--%>
<%--			<li class="${actionName=='fundingFee'  ? 'current' : ''}">--%>
<%--				<a href="<%=userPath%>fundingFee.do"> <span></span><fmt:message key="msg.pg.userCenter.fundingFee"/></a>--%>
<%--			</li>--%>
<%--			<li class="${actionName=='feeRate'  ? 'current' : ''}">--%>
<%--				<a href="<%=basePath%>fundingFee/feeRate.do"> <span></span><fmt:message key="msg.pg.feeRate.historyFeeRate"/></a>--%>
<%--			</li>--%>
			<%--<li class="${actionName=='vFundingFee'  ? 'current' : ''}">
				<a href="<%=userPath%>vFundingFee.do"> <span></span><fmt:message key="msg.pg.userCenter.vFundingFee"/></a>
			</li>
			<li class="${actionName=='vFeeRate'  ? 'current' : ''}">
				<a href="<%=basePath%>vfundingFee/vFeeRate.do"> <span></span><fmt:message key="msg.pg.feeRate.vHistoryFeeRate"/></a>
			</li>--%>
		</ul>
	</div>
	<div class="menu-block">
		<div class="menu-title">
			<h3>
				<span class="icon-user-btn"></span><fmt:message key="msg.pg.userCenter.center"/>
			</h3>
		</div>
		<ul class="left-menu-ul">
			<li class="${actionName=='accountSafe'  ? 'current' : ''}">
				<a href="<%=userPath %>accountSafe.do"> <span></span><fmt:message key="msg.pg.userCenter.acountSafe"/></a>
			</li>
			<c:if test="${isSub != 1 }">
<%--			<li class="${actionName=='subUser'  ? 'current' : ''}">--%>
<%--				<a href="<%=subUserPath %>subUser.do"> <span></span><fmt:message key="msg.pg.userCenter.subAccount"/></a>--%>
<%--			</li>--%>
			</c:if>
			<li class="${actionName=='viplist'  ? 'current' : ''}">
				<a href="<%=userPath %>viplist.do"><span></span><fmt:message key="msg.pg.userCenter.VIPintergel"/></a>
			</li>
			<li class="${actionName=='invite'  ? 'current' : ''}">
				<a href="<%=userPath %>invite.do"> <span></span><fmt:message key="msg.pg.userCenter.inviteMag"/></a>
			</li>
			<c:if test="${management != null }">
			<li class="${actionName=='inviteManagement'  ? 'current' : ''}">
				<a href="<%=userPath %>inviteManagement.do"> <span></span><fmt:message key="msg.pg.userCenter.memberManagement"/></a>
			</li>
			</c:if>
			<li class="${actionName=='myCertificate'  ? 'current' : ''}">
				<a href="<%=userPath %>myCertificate.do"> <span></span><fmt:message key="msg.pg.userCenter.myHonour"/></a>
			</li>
			<c:if test="${isSub != 1 }">
			<li class="${actionName=='bindAppKey'  ? 'current' : ''}">
				<a href="<%=userPath %>bindAppKey.do"> <span></span><fmt:message key="msg.pg.userCenter.applyApiRequest"/></a>
			</li>
			</c:if>
			<%-- <li>
				<a href="<%=userPath %>tradeapi.do" class="${actionName=='tradeapi'  ? 'current' : ''}"> <span class="icons-api${actionName=='tradeapi'  ? '-current' : ''}"></span>交易API</a>
			</li> --%>
		</ul>
	</div>
</div>