<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>交易记录</title>
		<!-- Reset Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<!-- Main Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<!-- Invalid Stylesheet. This makes stuff look pretty. Remove it if you want the CSS completely valid -->
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript">
		   function sure(qr,url)
		   {
			   if(qr!=null)
			   {
				   if(confirm(qr))
				   {
					   document.location.href = url;
				   }
			   }
			   else{
				   document.location.href = url;
				   }
				   
		   }
		   function postform(url) {
			   document.select.action=url;
			   document.select.submit();
		   }
		</script>

		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						交易记录
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										成交时间
									</th>
									<th>
										合约号
									</th>
									<th>
										成交类型
									</th>
									<th>
										持仓均价(USDT)
									</th>
									<th>
										<c:choose>
											<c:when test="${issz}">
												成交点位
											</c:when>
											<c:otherwise>
												成交均价(USDT)
											</c:otherwise>
										</c:choose>
									</th>
									<th>
										成交数量(个)
									</th>
									<th>
										保证金(USDT)
									</th>
									<th>
										盈亏额(USDT)
									</th>
									<th>
										佣金(USDT)
									</th>
									<th>
										状态
									</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${pm.datas}" var="tl" varStatus="status">
									<tr>
										<td>
											<ut:formatDate dateline="${tl.trade_time}"></ut:formatDate>
										</td>
										<td>
											${tl.transaction_code.toUpperCase() }
										</td>
										<td>
											<c:choose>
												<c:when test="${tl.trade_type_id eq 1}">
													<b style="color: #5a9d3d;">多单开仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 2}">
													<b style="color: #5a9d3d;">多单平仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 3}">
													<b style="color: #c55555;">空单开仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 4}">
													<b style="color: #c55555;">空单平仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 5}">
													<b style="color: orange;">多单爆仓</b>
												</c:when>
												<c:when test="${tl.trade_type_id eq 6}">
													<b style="color: orange;">空单爆仓</b>
												</c:when>
												<c:otherwise>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											${tl.hold_price }
												
										</td>
										<td>
											${tl.trade_average_price}
										</td>
										<td>
											${tl.trade_count}
										</td>
										<td>
											${tl.trade_deposit}
										</td>
										<td <c:if test="${tl.trade_income lt 0}"> style="color: #c55555;"</c:if>
											<c:if test="${tl.trade_income gt 0}"> style="color: #5a9d3d;"</c:if>>
											<c:choose>
												<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
												</c:when>
												<c:otherwise>
													${tl.trade_income }
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											${tl.trade_fee}
										</td>
										<td>
											完成
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerTrade/loadTradeLogs.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="code" value="${code}"/>
											<pg:param name="id" value="${id}"/>
											
											<pg:first>
												<a href="${pageUrl}" mce_href="${pageUrl}">首页</a>
											</pg:first>
											
											<pg:prev>
												<a href="${pageUrl }" mce_href="${pageUrl }">前页</a>
											</pg:prev>
											<pg:pages>
												<c:choose>
													<c:when test="${currentPageNumber eq pageNumber }">
														<font color="red">${pageNumber }</font>
													</c:when>
													<c:otherwise>
														<a href="${pageUrl }" mce_href="${pageUrl }">${pageNumber}</a>
													</c:otherwise>
												</c:choose>
											</pg:pages>
											<pg:next>
												<a href="${pageUrl }" mce_href="${pageUrl }">后页</a>
											</pg:next>

											<pg:last>
												<a href="${pageUrl }" mce_href="${pageUrl }">尾页</a>
											</pg:last>
										</pg:pager>
										
									</div>
									<!-- End .pagination -->
									<div class="clear"></div>
								</td>
							</tr>
						</tfoot>
						
						<script language="javascript" type="text/javascript">
							senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						</script>
					</div>

				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>
