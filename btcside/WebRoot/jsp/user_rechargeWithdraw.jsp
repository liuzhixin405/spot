<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.billDetails"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
		<script type="text/javascript">
			function changeTransaction(name, type) {
				window.location.href = 'transferRecord.do?type=' + type + '&bqCode=' + name;
			}
		</script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="clearfix">
						<div class="tab lt">
							<a href="<%=basePath%>center/record.do?type=1" class="${empty transactionList && type != 2 ? 'current' : ''}"><fmt:message key="msg.pg.userCenter.recharged"/></a>
							<a href="<%=basePath%>center/record.do?type=2" class="${empty transactionList && type == 2 ? 'current' : ''}"><fmt:message key="msg.pg.userCenter.withdrawals"/></a>
							<a href="<%=basePath%>transferAccount/transferRecord.do?type=1" class="${!empty transactionList && type != 2 ? 'current' : ''}"><fmt:message key="msg.pg.trade.rollIn"/></a>
							<a href="<%=basePath%>transferAccount/transferRecord.do?type=2" class="${!empty transactionList && type == 2 ? 'current' : ''}"><fmt:message key="msg.pg.trade.rollOut"/></a>
							<%--<a href="<%=basePath%>center/transferMore.do?type=4">转账到BTC100</a>
							<a href="<%=basePath%>center/transferMore.do?type=3">转账到Btcside</a> --%>
						</div>
						<c:if test="${!empty transactionList}">
							<div class="trade-title-lt rt">
								<span><fmt:message key="msg.pg.trade.choseContract"/>：</span>
								<c:forEach var="transaction" items="${transactionList}">
									<a href="javascript:;" name='${transaction.transaction_code}' onclick="changeTransaction(this.name, ${type });"
										<c:if test="${transaction.transaction_code eq currentTransaction.transaction_code}" > class="current" </c:if>>
										${fn:toUpperCase(transaction.transaction_code)} </a>
								</c:forEach>
								<input type="hidden" id="transaction_code" name="transaction_code" value="${currentTransaction.transaction_code}" />
							</div>
						</c:if>
					</div>
					
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.index.recordTime"/>
									</th>
									<th>
										<c:if test="${empty transactionList == true}"><fmt:message key="msg.pg.userCenter.address"/></c:if>
										<c:if test="${empty transactionList == false}"><fmt:message key="msg.pg.trade.contractNO"/></c:if>
										<%-- ${empty transactionList ? '地址' : '合约号'} --%>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.methods"/>
									</th>
									<th>
										<fmt:message key="msg.pg.market.amount"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.status"/>
									</th>
								</tr>
							</thead>
							<tbody>
								
								<c:forEach var="li" items="${pm.datas}" varStatus="st">
									<tr>
										<td>
											<span><ut:formatDate dateline="${li.dateline}"></ut:formatDate></span>
										</td>
										<td>
											<c:out value="${empty transactionList ? li.receice_card_no : li.f_code }"></c:out>
										</td>
										<td>
											<c:choose>
												<c:when test="${empty transactionList && li.operation==1}"><fmt:message key="msg.pg.userCenter.recharged"/></c:when>
												<c:when test="${empty transactionList && li.operation==2}"><fmt:message key="msg.pg.userCenter.withdrawals"/></c:when>
												<c:when test="${empty transactionList && li.operation==3}"><fmt:message key="msg.pg.trade.transferAccount"/></c:when>
												<c:when test="${!empty transactionList && li.operation==1}"><fmt:message key="msg.pg.trade.rollIn"/></c:when>
												<c:when test="${!empty transactionList && li.operation==2}"><fmt:message key="msg.pg.trade.rollOut"/></c:when>
											</c:choose>
										</td>
										<td>
											<c:out value="${empty transactionList ? li.amount : li.change_amount }"></c:out>
										</td>
										<td>
											<c:choose>
												<c:when test="${empty transactionList && li.state==0}"><span class="sell"><fmt:message key="msg.pg.userCenter.notAccept"/></span></c:when>
												<c:when test="${empty transactionList && li.state==1}"><span class="buy"><fmt:message key="msg.pg.userCenter.hadAccept"/></span></c:when>
												<c:when test="${empty transactionList && li.state==2}"><span class="sell"><fmt:message key="msg.pg.userCenter.refuse"/>(<c:out value="${li.remark}"></c:out>)</span></c:when>
												<c:when test="${!empty transactionList}"><span class="buy"><fmt:message key="msg.pg.userCenter.succ"/></span></c:when>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<ul class="pagination">
							<pg:pager url="${pageN}" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
								
								<pg:param name="type" value="${type != 2 ? 1 : 2 }" />
								
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
	</body>
</html>