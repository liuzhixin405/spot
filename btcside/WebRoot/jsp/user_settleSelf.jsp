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

	<body  >
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont">
			<div class="left-menu">
				<jsp:include page="_trade_left.jsp"></jsp:include>
			</div>
			<div class="main-rt-box">
				<!-- 个人结算记录 -->
      <div class="main-wrap">
				<div class="tab">
		  		<a href="<%=basePath%>trade/settleSys.do">系统交割记录</a>
          <a href="<%=basePath%>trade/settleSelf.do" class="current">个人交割记录</a>
          <a href="<%=basePath%>trade/settle.do" >盈利结算记录</a>
        </div>
			<div class="trade-manage-main">
				<table cellpadding="0" cellspacing="0">
					<thead>
						<tr>
							<th>合约号</th>
							<th>方向</th>
							<th>持仓量</th>
							<th>冻结量</th>
							<th>平均价</th>
							<th>佣金</th>
							<th>杠杆倍数</th>
							<th>保证金</th>
							<th>总盈亏</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${selflist.datas}" var="bq">
							<tr>
								<td>
									<c:out value="${fn:toUpperCase(bq.transaction_code)}"></c:out>
									
								</td>
								<td>
									
									<c:if test="${bq.trade_type_id=='1'}">多单开仓</c:if>
									<c:if test="${bq.trade_type_id=='2'}">多单平仓</c:if>
									<c:if test="${bq.trade_type_id=='3'}">空头开仓</c:if>
									<c:if test="${bq.trade_type_id=='4'}">空头平仓</c:if>
									<c:if test="${bq.trade_type_id=='5'}">多单爆仓</c:if>
									<c:if test="${bq.trade_type_id=='6'}">空单爆仓</c:if>
									
								</td>
								<td>
									<c:out value="${bq.hold_count}"></c:out>
								</td>
								<td>
									<c:out value="${bq.lock_count}"></c:out>
								</td>
								<td>
									<c:out value="${bq.average_price}"></c:out>
								</td>
								<td>
									<c:out value="${bq.trade_fee}"></c:out>
								</td>
								<td>
									<c:out value="${bq.trade_multiple}"></c:out>
								</td>
								<td>
									<c:out value="${bq.deposit}"></c:out>
								</td>
								<td>
									
									<fmt:formatNumber value="${bq.profit_lost}" pattern="#0.######"></fmt:formatNumber>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<ul class="pagination">
					<pg:pager url="trade/settleSelf.do" items="${selflist.total}"
						maxIndexPages="10" maxPageItems="${selflist.maxitems}"
						export="currentPageNumber=pageNumber">

						<pg:prev> <li class="disabled">
							<a href="<%=basePath%>${pageUrl}"
								mce_href="<%=basePath%>${pageUrl}">上一页</a></li>
						</pg:prev>


						<pg:pages>
							<c:choose>
								<c:when test="${currentPageNumber eq pageNumber }">
									<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
								</c:when>
								<c:otherwise><li>
									<a href="<%=basePath%>${pageUrl }"
										mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
								</c:otherwise>
							</c:choose>
						</pg:pages>


						<pg:next><li>
							<a href="<%=basePath%>${pageUrl }"
								mce_href="<%=basePath%>${pageUrl }">下一页</a></li>
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