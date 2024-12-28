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
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
		<!-- 子账户提示 -->
		<c:if test="${notManagement == true}">
			<div id="add-windows" class="add-windows">
				<div id="tb-overlay" class="tb-overlay"></div>
				<div class="add-wrap" style="display:block;">
					<div class="dialogs-title">
						<h2 class="lt"><fmt:message key="msg.pg.userCenter.memberManagement"/></h2>
						<a href="javascript:history.go(-1)" class="close-icon icons-close" id="close-alert"></a>
					</div>
					<div class="dialogs-body">
						<p style="margin-bottom:30px; text-align:center;font-size:16px;">
							<fmt:message key="msg.pg.userCenter.unopen"/>
						</p>
						<div class="form-group-btn" style="display: flex; justify-content: center">
							<a href="javascript:history.go(-1)" class="btn sub-btn-return"><fmt:message key="msg.pg.trade.goBack"/></a>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		
		<div class="gray_overlay" id="gray_overlay"></div>
		<!-- 查看用户邮箱 -->
		<div id="delManagement" class="delManagement" >
			<div class="delManagement-wrap">
				<div class="warning-tips">
					<img alt="warning" src="../common/images/warning-tips.png">
				</div>
				<img class="closeManagement" onclick="closeDelManagement()" alt="closesubAccount" src="../common/images/close_pages.png">
				<p><fmt:message key="msg.pg.userCenter.commissionWarning"/><span id="user-account-id"></span>&nbsp;<a href="<%=basePath%>center/viplist.do"><fmt:message key="msg.pg.userCenter.purchasevip"/></a></p>
			</div>
		</div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title-top">
						<h2><fmt:message key="msg.pg.userCenter.memberManagement"/></h2>
					</div>
				</div>
				<div class="main-wrap">
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.userCenter.userMailbox"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.vipLevel"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.inviteMembers"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.unionMembers"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.inviter"/>
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td style="width: 250px;" id="inviter">
										<c:out value="${email}"></c:out>
									</td>
									<td id="level">
										<c:out value="${level}"></c:out>
									</td>
									<td>
										<c:out value="${inviteNums}"></c:out>
									</td>
									<td>
										<c:out value="${allInviteNums}"></c:out>
									</td>
									<td>
										<c:if test="${inviter eq null }">--</c:if>
										<c:if test="${inviter ne null }"><a class="inviterText" href="<%=userPath%>inviteManagement.do?userName=${inviter }">${inviter }</a></c:if>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<div class="main-wrap" style="margin-top: 50px;margin-bottom:230px;position: relative;z-index: 999;">
					<div class="block-title-top">
						<h2 class="CommiRebatetitle"><fmt:message key="msg.pg.userCenter.DetailsCommiRebate"/></h2>
					</div>
					<div class="trade-manage-mainList">
						<table class="firstRebateBox" cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.userCenter.serialNumber"/>
									</th>
									<th style="width: 20%;">
										<fmt:message key="msg.pg.userCenter.userMailbox"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.vipLevel"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.inviteMembers"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.unionMembers"/>
									</th>
									<th>
										<fmt:message key="msg.pg.trade.commission"/>（USDT）
									</th>
									<th>
										<fmt:message key="msg.pg.trade.commission"/>（Btcside）
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.seeAll"/>
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="li" items="${pm.datas }" varStatus="l">
								<tr class="userDetilListText" id="userDetilListText">
									<td style="text-align: center;height: 54px;border-bottom: solid 1px #f1f3f4;">
										<span>${l.index+1}</span>
									</td>
									<td class="userEmail" id="userEmail${l.index}" onclick="clickGotoUserDel(${l.index})" style="text-align: center;height: 54px;border-bottom: solid 1px #f1f3f4;">
										<c:out value="${li.userName}"></c:out>
									</td>
									<td id="vipGrade${l.index}" style="text-align: center;height: 54px;border-bottom: solid 1px #f1f3f4;">
										<c:out value="${li.level}"></c:out>
									</td>
									<td style="text-align: center;height: 54px;border-bottom: solid 1px #f1f3f4;">
										<c:out value="${li.inviteNums}"></c:out>
									</td>
									<td style="text-align: center;height: 54px;border-bottom: solid 1px #f1f3f4;">
										<c:out value="${li.allInviteNums}"></c:out>
									</td>
									<td style="text-align: center;height: 54px;border-bottom: solid 1px #f1f3f4;">
										<c:out value="${li.usdtRebate}"></c:out>
									</td>
									<td style="text-align: center;height: 54px;border-bottom: solid 1px #f1f3f4;">
										<c:out value="${li.bfxRebate}"></c:out>
									</td>
									<td class="checkDetilbtn" id="checkDetilbtn${l.index}" onclick="checkDetilbtn(${l.index})" style="text-align: center;height: 54px;border-bottom: solid 1px #f1f3f4;">
										<span class="checkDetilText" id="checkDetilText${l.index}"><fmt:message key="msg.pg.workerOrderList.check"/></span>
										<img id="checkDetilList${l.index}" class="checkDetilList" src="../common/images/check_open.png">
										<!-- 下拉的列表 开始 -->
										<div class="trade-manage-mainLists" id="rebateListBox${l.index}">
										</div>
										<!-- 结束 -->
									</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
						
					</div>
					<ul class="pagination">
						<pg:pager url="${pageN}" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
							
							<pg:param name="userName" value="${email }" />
							<pg:param name="inviter" value="${inviter }" />
							
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
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/memberManagement.js?<%=rjs%>"></script>
</body>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		checkOpenList: "查看",
		checkCloseList: "收起"
	};
	message["zh_HK"] = {
		checkOpenList: "查看",
		checkCloseList: "收起"
	};
	message["en_US"] = {
		checkOpenList: "View",
		checkCloseList: "Close"
	};
	message["ja_JP"] = {
		checkOpenList: "表示する",
		checkCloseList: "折りたたむ"
	};
	message["ko_KR"] = {
		checkOpenList: "보기",
		checkCloseList: "접기"
	};
	message["ru_RU"] = {
		checkOpenList: "Посмотреть",
		checkCloseList: "Закрыть"
	};
	var MSG = message[locale];
</script>
<script type="text/javascript">
	var fmtCommissionType = '<fmt:message key="msg.pg.userCenter.commissionType"/>';
	var fmtWeekCommission = '<fmt:message key="msg.pg.userCenter.weekCommission"/>';
	var fmtMonthsCommission = '<fmt:message key="msg.pg.userCenter.monthsCommission"/>';
	var fmtTotalCommission = '<fmt:message key="msg.pg.userCenter.totalCommission"/>';
</script>
</html>

