<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%
	Long nowTime = System.currentTimeMillis() / 1000;
	request.setAttribute("nowTime", nowTime);
%>

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.top.borrow"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/pledge.css?<%=rjs%>">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<!-- 主体内容 -->
		<div class="pledge-title-content">
			<div class="pledge-record-title">
				<h3 style="margin-right:100px;">理财借出历史订单</h3>
				<a href="orders.do?type=0" class="tab ${type == 0 ? 'active' : ''}">借出订单</a>
				<a href="orders.do?type=1" class="tab ${type == 1 ? 'active' : ''}">还币记录</a>
				<a href="orders.do?type=2" class="tab ${type == 2 ? 'active' : ''}">平仓记录</a>
			</div>
		</div>
		
		<div class="pledge-content" style="padding-bottom:24px;margin:20px auto;">
			<div class="pledge-content-div pledge-border">
				<div class="pledge-content-title">
					<h4>借出订单</h4>
				</div>
				
				<div class="trade-manage-main">
			        <table cellpadding="0" cellspacing="0">
						<thead>
						  <tr>
						    <th>借出时间</th>
						    <th>借出币种</th>
						    <th>借出数量</th>
						    <th>周期</th>
						    <th>还款日期</th>
						    <th>质押币种</th>
						    <th>质押数量</th>
						    <th>代收利息</th>
						    <th>状态</th>
						  </tr>
						</thead>
						<tbody>
							<c:forEach items="${pm.datas}" var="r" varStatus="status">
								<tr>
									<td><ut:formatDate dateline="${r.record_time}"></ut:formatDate></td>
									<td>${r.borrow_symbol}</td>
									<td>${r.received_amount.doubleValue()}</td>
									<td>${r.cycle}</td>
									<td><ut:formatDate dateline="${r.record_time + r.cycle * 24 * 3600}"></ut:formatDate></td>
									<td>${r.pledge_symbol}</td>
									<td>${r.pledge_amount.doubleValue()}</td>
									<td>${r.interest.doubleValue() * (1 - r.fee_rate.doubleValue())}</td>
									<td>
									<c:if test="${r.state == 0}">已提交</c:if>
									<c:if test="${r.state == 1}">${(r.record_time + r.cycle * 24 * 3600) > nowTime ? '计息中' : '已逾期'}</c:if>
									<c:if test="${r.state == 2}">已还款</c:if>
									<c:if test="${r.state == 3}">已平仓</c:if>
									
									</td>
								</tr>
							</c:forEach>
						</tbody>
			          </table>
			          <ul class="pagination">
							<pg:pager url="financial/out/orders.do" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}"
								export="currentPageNumber=pageNumber">
									<pg:param name="type" value="${type}"/>
								
									<pg:prev> <li class="disabled">
										<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a></li>
									</pg:prev>
							
							
									<pg:pages>
										<c:choose>
											<c:when test="${currentPageNumber eq pageNumber }">
												<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
											</c:when>
											<c:otherwise><li>
												<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
											</c:otherwise>
										</c:choose>
									</pg:pages>
									<pg:next>
									<li><a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a></li>
								</pg:next>
							</pg:pager>
						</ul>
						
						<c:if test="${pm.datas.size() == 0}">
							<div class="pledge-no-record">暂无记录</div>
						</c:if>
		          </div>
			</div>
		</div>

		<jsp:include page="_footer.jsp"></jsp:include>
		
	</body>
	
	<script>
	
	</script>
</html>

