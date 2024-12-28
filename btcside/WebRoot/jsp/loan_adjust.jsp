<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
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
				<h3 style="margin-right:100px;">质押借币历史订单</h3>
				<a href="orders.do?type=1" class="tab ${type == 1 ? 'active' : ''}">还币记录</a>
				<a href="orders.do?type=2" class="tab ${type == 2 ? 'active' : ''}">质押金调整历史</a>
				<a href="orders.do?type=3" class="tab ${type == 3 ? 'active' : ''}">平仓记录</a>
			</div>
		</div>
		
		<div class="pledge-content" style="padding-bottom:24px;margin:20px auto;">
			<div class="pledge-content-div pledge-border">
				<div class="pledge-content-title">
					<h4>质押金调整历史</h4>
				</div>
				
				<div class="trade-manage-main">
			        <table cellpadding="0" cellspacing="0">
						<thead>
						  <tr>
						    <th>时间</th>
						    <th>质押币种</th>
						    <th>质押总额</th>
						    <th>调整方向</th>
						    <th>调整金额</th>
						    <th>调整前质押率</th>
						    <th>调整后质押率</th>
						    <th>借币币种</th>
						  </tr>
						</thead>
						<tbody>
							<c:forEach items="${pm.datas}" var="r" varStatus="status">
								<tr>
									<td><ut:formatDate dateline="${r.record_time}"></ut:formatDate></td>
									<td>${r.pledge_symbol}</td>
									<td>${r.pledge_amount.doubleValue()}</td>
									<td>${r.direction == 0 ? '新增' : (r.direction == 1 ? '追加' : '减少')}</td>
									<td>${r.adjust_amount}</td>
									<td><fmt:formatNumber type="percent" minFractionDigits="2" maxIntegerDigits="3" value="${r.old_pledge_rate}" /></td>
									<td><fmt:formatNumber type="percent" minFractionDigits="2" maxIntegerDigits="3" value="${r.new_pledge_rate}" /></td>
									<td>${r.borrow_symbol}</td>
								</tr>
							</c:forEach>
						</tbody>
			          </table>
			          <ul class="pagination">
							<pg:pager url="financial/orders.do" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}"
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

