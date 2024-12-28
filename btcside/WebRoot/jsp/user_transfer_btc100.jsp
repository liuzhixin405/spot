<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-账单明细</title>
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

		<div class="container">

			<jsp:include page="_user_left.jsp"></jsp:include>

			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="clearfix">
						<div class="tab lt">
							<a href="<%=basePath%>center/record.do?type=1">充值</a>
							<a href="<%=basePath%>center/record.do?type=2">提现</a>
							<a href="<%=basePath%>transferAccount/transferRecord.do?type=1">转入</a>
							<a href="<%=basePath%>transferAccount/transferRecord.do?type=2">转出</a>
							<%--<a href="<%=basePath%>center/transferMore.do?type=4" class="${type != 3 ? 'current' : ''}">转账到BTC100</a>
							<a href="<%=basePath%>center/transferMore.do?type=3" class="${type == 3 ? 'current' : ''}">转账到BFX</a>--%>
						</div>
					</div>
					
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										时间
									</th>
									<th>
										地址
									</th>
									<th>
										数量
									</th>
									<th>
										状态
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
											<c:out value="${li.remark}" />
										</td>
										<td>
											<c:out value="${li.amount}" />
										</td>
										<td>
											受理成功
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<ul class="pagination">
							<pg:pager url="center/${pageN}" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
								
								<pg:param name="type" value="${type eq '' ? 4 : 3 }" />
								
								<pg:prev>
								<li class="disabled">
									<a href="<%=basePath%>${pageUrl}"
										mce_href="<%=basePath%>${pageUrl}">上一页</a>
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
										mce_href="<%=basePath%>${pageUrl }">下一页</a>
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