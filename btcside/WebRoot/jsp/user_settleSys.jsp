<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Btcside区块链资产合约交易平台-结算记录</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont">
			<div class="left-menu">
				<jsp:include page="_trade_left.jsp"></jsp:include>
			</div>
			<div class="main-rt-box">
				<!-- 系统结算记录 -->
				<div class="main-wrap">
					<div class="tab">
							<a href="<%=basePath%>trade/settleSys.do" class="current">系统交割记录</a>
							<a href="<%=basePath%>trade/settleSelf.do">个人交割记录</a>
							<a href="<%=basePath%>trade/settle.do">盈利结算记录</a>
					</div>

					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										合约名称
									</th>
									<th>
										结算价格
									</th>
									<th>
										结算成交量
									</th>
									<th>
										系统亏损额
									</th>
									<th>
										净盈利部分分摊比例
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${syslist.datas}" var="bq">
									<tr>
										<td>
											<c:out value="${fn:toUpperCase(bq.transaction_code)}"></c:out>
										</td>
										<td>
											<fmt:formatNumber value="${bq.price}" pattern="#0.00"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${bq.trans_num}" pattern="#0.00"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${bq.system_profit_loss}" pattern="#0.00000000"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${bq.rate}" type="percent" pattern="#.##%"></fmt:formatNumber>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<ul class="pagination">
							<pg:pager url="trade/settleSys.do" items="${syslist.total}" maxIndexPages="10" maxPageItems="${syslist.maxitems}"
								export="currentPageNumber=pageNumber">

								<pg:prev>
									<li class="disabled">
										<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}">上一页</a>
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
										<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">下一页</a>
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

