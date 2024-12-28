<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="msg.pg.systemName" />-<fmt:message
		key="msg.pg.userCenter.center" /></title>
<script type="text/javascript"
	src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript"
	src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
	<!-- 子账户提示 -->
	<c:if test="${isSubUser == true}">
		<div id="add-windows" class="add-windows">
			<div id="tb-overlay" class="tb-overlay"></div>
			<div class="add-wrap" style="display:block;">
				<div class="dialogs-title">
					<h2 class="lt">
						<fmt:message key="msg.pg.userCenter.subAccount" />
					</h2>
					<a href="javascript:history.go(-1)" class="close-icon icons-close"
						id="close-alert"></a>
				</div>
				<div class="dialogs-body">
					<p style="margin-bottom:30px; text-align:center;font-size:16px;">
						很抱歉！该功能子账号未开放</p>
					<div class="form-group-btn"
						style="display: flex; justify-content: center">
						<a href="javascript:history.go(-1)" class="btn sub-btn-return"><fmt:message
								key="msg.pg.trade.goBack" /></a>
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
					<h2 class="lt">
						<fmt:message key="msg.pg.top.withdraw" />
					</h2>
					<a href="javascript:history.go(-1)" class="close-icon icons-close"
						id="close-alert"></a>
				</div>
				<div class="dialogs-body">
					<p style="margin-bottom:30px; text-align:center;font-size:16px;">
						<fmt:message key="msg.pg.userCenter.gobind" />
					</p>
					<div class="form-group-btn">
						<a href="<%=basePath%>center/accountSafe.do" class="btn sub-btn"><fmt:message
								key="msg.pg.userCenter.letbind" /></a> <a
							href="javascript:history.go(-1)" class="btn sub-btn-return"><fmt:message
								key="msg.pg.trade.goBack" /></a>
					</div>
				</div>
			</div>
		</div>
	</c:if>
	<jsp:include page="_top.jsp"></jsp:include>
	<jsp:include page="_head.jsp"></jsp:include>
	<div class="container center-cont">
		<jsp:include page="_user_left.jsp"></jsp:include>
		<div class="main-rt-box assets-main">

			<div class="main-wrap">
				<div class="block-title">
					<h2>
						<fmt:message key="msg.pg.userCenter.subAccount"/>${subUser.f_user_email }-<fmt:message key="msg.pg.userCenter.assetsDetails" />
					</h2>
				</div>
				<div class="trade-manage-main">
					<table cellpadding="0" cellspacing="0">
						<thead>
							<tr>
								<th><fmt:message key="msg.pg.userCenter.nameAssets" /></th>
								<th><fmt:message key="msg.pg.market.chartTotalAmount" /></th>
								<th><fmt:message key="msg.pg.userCenter.avaibleAssets" /></th>
								<th><fmt:message key="msg.pg.userCenter.frozeAssets" /></th>
								<c:if test="${ieoLock != null }">
									<th><fmt:message key="msg.pg.userCenter.lockAssets" /></th>
									<th><fmt:message key="msg.pg.userCenter.unlockAssets" /></th>
								</c:if>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="bglists" items="${bqAccounts }">
								<c:if test="${bglists.coinName eq 'USDT' }">
									<tr>
										<td class="clik-td">${bglists.coinName } <em
											class="que-icon icons-que td-usd" id="que-usd"></em>
											<div class="que-help que-help-usd" id="kcdj-txt">
												<span class="triangle-user" style="left: 142px;"></span>
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
										<td><fmt:formatNumber value="${bglists.total }"
												pattern="#0.####" /></td>
										<td><span class="buy"><fmt:formatNumber
													value="${bglists.available }" pattern="#0.####" /></span></td>
										<td><span class="sell"><fmt:formatNumber
													value="${bglists.freeze }" pattern="#0.####" /></span></td>
										<c:if test="${ieoLock != null }">
											<td><fmt:formatNumber value="0" pattern="#0.####" /></td>
											<td><fmt:formatNumber value="0" pattern="#0.####" /></td>
										</c:if>
									</tr>
								</c:if>
							</c:forEach>
							<c:forEach var="bglists" items="${bqAccounts }" varStatus="l">
								<c:if test="${bglists.coinName ne 'USDT'}">
									<tr>
										<td class="${bglists.coinDescribleZh ne '' ? 'clik-td' : ''}">
							        		${bglists.coinName }
							        		<c:if test="${bglists.coinDescribleZh ne '' }">
							        			<em class="que-icon icons-que td-usd" onmouseout="hideTip(${l.index})" onmouseover="showTip(${l.index})"></em>
							        			<div class="que-help que-help-usd" id="kcdj-txt-${l.index}">
													<span class="triangle-user" style="left: 142px;"></span>
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
										<td><fmt:formatNumber value="${bglists.total }"
												pattern="#0.####" /></td>
										<td><span class="buy"><fmt:formatNumber
													value="${bglists.available }" pattern="#0.####" /></span></td>
										<td><span class="sell"><fmt:formatNumber
													value="${bglists.freeze }" pattern="#0.####" /></span></td>
										<c:if test="${ieoLock != null }">
											<c:if test="${bglists.coinName ne 'BFX' }">
												<td><fmt:formatNumber value="0" /></td>
												<td><fmt:formatNumber value="0" /></td>
											</c:if>
											<c:if test="${bglists.coinName eq 'BFX' }">
												<td><fmt:formatNumber value="${ieoLock }"
														pattern="#0.####" /></td>
												<td><fmt:formatNumber value="${ieoUnlock }"
														pattern="#0.####" /></td>
											</c:if>
										</c:if>
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
	<script type="text/javascript"
		src="<%=basePath%>common/js/password.js?<%=rjs%>"></script>
	<script type="text/javascript"
		src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
</body>
<script type="text/javascript">
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
