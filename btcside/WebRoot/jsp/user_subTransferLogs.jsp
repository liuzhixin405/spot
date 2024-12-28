<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.scoreRecoded"/> </title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>">
	</head>

	<body>
		<!-- 子账户不能充值提示 -->
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
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userCenter.motherAccountList"/>
						</h2>
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
 								<c:forEach var="li" items="${pm.datas}" >
			                      <tr>
									<td style="text-align: left;padding-left: 15px;width: 20%;">
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
						<ul class="pagination">
	
							<pg:pager url="userSubAccount/transferLogs.do" items="${pm.total}"
								maxIndexPages="10" maxPageItems="${pm.maxitems}"
								export="currentPageNumber=pageNumber">
								
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
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
	</body>

</html>