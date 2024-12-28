<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.inviteMag"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
		
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userCenter.thirdMember"/>
						</h2>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.userCenter.username"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.userLev"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.memberPoint"/>
									</th>
									<c:if test="${identity == 1}">
									<th>
										<fmt:message key="msg.pg.trade.option"/>
									</th>
									</c:if>
								</tr>
							</thead>
							<c:if test="${pm.datas == null||empty pm.datas}">
					              <tr>
						            <c:if test="${sessionScope.userinfo!=null}">
					            		<td colspan="4"> <fmt:message key="msg.pg.activety.none"/> </td>
					            	</c:if>
						          </tr>
						    </c:if>
						    <c:if test="${pm.datas != null && pm.datas != ''}">
							<tbody>
								<c:forEach items="${pm.datas }" var="one" varStatus="status">
									<tr>
										<td>
											${fn:substring(one[1],0,3)}****@${fn:substringAfter(one[1],'@')}
										</td>
										<td>
											${one[2] == null ? 0 : one[2] }
										</td>
										<td>
											<fmt:formatNumber value="${one[3] }" pattern="#.########"/>
										</td>
										<c:if test="${identity == 1}">
										<td>
											<a href="<%=basePath%>center/positionInformation.do?inviteeId=${one[0] }"><fmt:message key="msg.pg.userCenter.positionView"/></a>
										</td>
										</c:if>
									</tr>
								</c:forEach>
							</tbody>
							</c:if>
						</table>
						<div class="pagination">
							<pg:pager url="center/inviteThirdMember.do" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
								<pg:param name="inviteeId" value="${inviteeId}" />
								<pg:prev>
									<li class="disabled">
										<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>
									</li>
								</pg:prev>
								<pg:pages>
									<c:choose>
										<c:when test="${currentPageNumber eq pageNumber }">
											<li class="active">
												<a href="javascript:void(0)">${pageNumber }</a>
											</li>
										</c:when>
										<c:otherwise>
											<li>
												<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a>
											</li>
										</c:otherwise>
									</c:choose>
								</pg:pages>

								<pg:next>
									<li>
										<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a>
									</li>
								</pg:next>
							</pg:pager>
						</div>
						<div class="clear"></div>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
</body>
</html>