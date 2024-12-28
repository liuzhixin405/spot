<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="msg.pg.systemName" />-BTC<fmt:message
		key="msg.pg.userCenter.recharged" /></title>
<link type="text/css" rel="stylesheet"
	href="<%=basePath%>common/css/style.css?<%=rjs%>" />
<script type="text/javascript"
	src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript"
	src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript"
	src="<%=basePath%>common/js/ZeroClipboard.js?<%=rjs%>"></script>

</head>

<body>
	<div class="add-wind" id="add-wind">
		<div id="tb-overlay"></div>
		<input type="hidden" name="addresserror" id="addresserror"
			value="${addresserror}" />
		<div class="add-wrap">
			<div class="block-title">
				<h2 class="lt">
					<fmt:message key="msg.pg.userCenter.noChargeAddress" />
				</h2>
			</div>
			<div class="main-wrap">
				<a href=""></a>
			</div>
		</div>
	</div>

	<jsp:include page="_top.jsp"></jsp:include>
	<jsp:include page="_head.jsp"></jsp:include>
	<!-- 主体内容 -->
	<div class="container center-cont">
		<jsp:include page="_user_left.jsp"></jsp:include>
		<div class="main-rt-box">
			<c:if test="${mineRecords.datas != null}">
				<div class="main-wrap">
					<div class="block-title">
						<h2 class="lt">
							<fmt:message key="msg.pg.userCenter.mineRecord" />
						</h2>
					</div>

					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th><fmt:message key="msg.pg.index.recordTime" /></th>
									<th><fmt:message key="msg.pg.userCenter.tradeContract" />
									</th>
									<th><fmt:message key="msg.pg.userCenter.tradeFee" /></th>
									<th><fmt:message key="msg.pg.userCenter.mineAmount" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="record" items="${mineRecords.datas}">
									<tr>
										<td><ut:formatDate dateline="${record.record_time}"></ut:formatDate>
										</td>
										<td><c:out value="${record.trade_type}" /></td>
										<td><c:out value="${record.trade_fee}" /></td>
										<td><c:out value="${record.mine_amount}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>

					<ul class="pagination">
						<pg:pager url="${pageN}" items="${mineRecords.total}"
							maxIndexPages="10" maxPageItems="${mineRecords.maxitems}"
							export="currentPageNumber=pageNumber">

							<pg:prev>
								<li class="disabled"><a href="<%=basePath%>${pageUrl}"
									mce_href="<%=basePath%>${pageUrl}"><fmt:message
											key="msg.pg.paging.previous" /></a></li>
							</pg:prev>

							<pg:pages>
								<c:choose>
									<c:when test="${currentPageNumber eq pageNumber }">
										<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
									</c:when>
									<c:otherwise>
										<li><a href="<%=basePath%>${pageUrl }"
											mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
									</c:otherwise>
								</c:choose>
							</pg:pages>

							<pg:next>
								<li><a href="<%=basePath%>${pageUrl }"
									mce_href="<%=basePath%>${pageUrl }"><fmt:message
											key="msg.pg.paging.next" /></a></li>
							</pg:next>
						</pg:pager>
					</ul>
				</div>
			</c:if>

		</div>
	</div>
	<div class="clear"></div>
	<jsp:include page="_footer.jsp"></jsp:include>


</body>

</html>
