<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="msg.pg.workerOrderList.workerOrder"/></title>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
</head>

<body>
	<jsp:include page="_top.jsp"></jsp:include>
	<jsp:include page="_head.jsp"></jsp:include>

	<div class="container center-cont order-contents" style="background: #FFFFFF;box-shadow: 0px 2px 6px 1px rgba(148,171,192,0.1);">
		<div class="center">
			<input type="button" class="btn sub-btn" style="margin-top: 20px;" value="<fmt:message key="msg.pg.workerOrderAdd.add"/>" onclick="window.location.href='<%=orderPath %>addOrder.do'">
		</div>
		<div class="trade-manage-main table-bg margin-b20">
			<div class="order-list">
				<div class="block-title">
					<h2>
						<fmt:message key="msg.pg.workerOrderList.myOrder"/>
					</h2>
				</div>
				<table class="margin-t5">
					<thead>
						<tr>
							<th><fmt:message key="msg.pg.workerOrderList.orderCode"/></th>
							<th><fmt:message key="msg.pg.workerOrderList.orderState"/></th>
							<th><fmt:message key="msg.pg.workerOrderList.orderType"/></th>
							<th><fmt:message key="msg.pg.workerOrderList.orderTheme"/></th>
							<th><fmt:message key="msg.pg.workerOrderList.orderTime"/></th>
							<th><fmt:message key="msg.pg.trade.option"/></th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${pm!= null }">
							<c:forEach items="${pm.datas }" var="orders" varStatus="status">
								<tr>
									<td>
										${orders.f_order_code}
										<c:if test="${orders.f_from_order_state == 'UNREAD'}"><span class="unread-prompt"></span></c:if>	
									</td>
									<td>
										<c:choose>
											<c:when test="${orders.f_from_order_state == 'CLOSE'}"><fmt:message key="msg.pg.workerOrderList.close"/></c:when>
											<c:when test="${orders.f_from_order_state == 'READ'}"><fmt:message key="msg.pg.workerOrderList.read"/></c:when>
											<c:when test="${orders.f_from_order_state == 'SEND'}"><fmt:message key="msg.pg.workerOrderList.send"/></c:when>
											<c:when test="${orders.f_from_order_state == 'UNREAD'}"><font style="color: red"><fmt:message key="msg.pg.workerOrderList.unread"/></font></c:when>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${orders.f_order_type == 'ACCOUNT'}"><fmt:message key="msg.pg.workerOrderAdd.account"/></c:when>
											<c:when test="${orders.f_order_type == 'TRADE'}"><fmt:message key="msg.pg.workerOrderAdd.trade"/></c:when>
											<c:when test="${orders.f_order_type == 'COMPLAIN'}"><fmt:message key="msg.pg.workerOrderAdd.Complaint"/></c:when>
											<c:when test="${orders.f_order_type == 'RW'}"><fmt:message key="msg.pg.workerOrderAdd.rw"/></c:when>
											<c:when test="${orders.f_order_type == 'COOP'}"><fmt:message key="msg.pg.workerOrderAdd.COOP"/></c:when>
										</c:choose>
									</td>
									<td>${orders.f_theme}</td>
									<td><ut:formatDate dateline="${orders.f_create_time }"></ut:formatDate></td>
									<td><a class="gray-a" href="<%=orderPath %>orderDetail.do?id=${orders.id}" class="">
										<c:if test="${orders.f_from_order_state == 'CLOSE'}"><fmt:message key="msg.pg.workerOrderList.check"/></c:if>
										<c:if test="${orders.f_from_order_state != 'CLOSE'}"><fmt:message key="msg.pg.workerOrderList.checkAndreply"/></c:if>
									</a></td>
								</tr>
							</c:forEach>
						</c:if>
					</tbody>
				</table>
				<ul class="pagination">
					<pg:pager url="order/orderList.do" items="${pm.total}"
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
							<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a>
						</li>
						</pg:next>
						
					</pg:pager>
				</ul>
			</div>
		</div>
		
	</div>

	<jsp:include page="_footer.jsp" />
</body>
<script type="text/javascript">
</script>
</html>
