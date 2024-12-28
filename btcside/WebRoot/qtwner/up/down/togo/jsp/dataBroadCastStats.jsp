<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<title>合约持仓数据播报</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<style type="text/css">
			.clear {
				clear: both;
			}
		</style>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						持仓数据播报
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
							<c:forEach items="${settings}" var="l" varStatus="status">
								<a href="<%=actionPath%>managerDataBroadCast/data.do?code=${l.f_code }">
									<font color="${code == l.f_code ? '#ff0000' : ''}">${l.f_code.toUpperCase() }</font>
								</a> &nbsp;
							</c:forEach>
							<div class="clear"></div>
						</div>
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<br>
								<div>
									据Btcside数据披露，截至<ut:formatDate dateline="${dataStats.record_time}"></ut:formatDate>：
									<br>
									<span style="color: red;"><c:out value="${code.toUpperCase()}"></c:out></span>合约
									持仓总量<span style="color: #0000FF;"><fmt:formatNumber value="${dataStats.buy_hold_count + dataStats.sell_hold_count }" pattern="#0.####"/></span>，
									较上一交易日变化<span style="color: #0000FF;"><fmt:formatNumber value="${dataStats.total_change / 10000 }" pattern="#0.####"/></span>万，
									精英多头持仓比例为<span style="color: #0000FF;"><fmt:formatNumber value="${dataStats.elite_buy_ratio }" type="percent" pattern="#.###%"/></span>，
									精英空头持仓比例为<span style="color: #0000FF;"><fmt:formatNumber value="${dataStats.elite_sell_ratio }" type="percent" pattern="#.###%"/></span>；
								</div>
								<br>
								<div>
									<form action="<%=actionPath%>managerDataBroadCast/data.do" method="get">
										<input type="hidden" name="code" value="${code}" ></input>
										开始时间
										<input name="startTime" type="text" value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										&nbsp;&nbsp;&nbsp;&nbsp;结束时间
										<input name="endTime" type="text" value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查询</button>
									</form>
									<div class="clear"></div>
								</div>
								<div class="clear"></div>
							</div>
						</div>
						
					</div>
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									合约号
								</th>
								<th>
									买多总量
								</th>
								<th>
									卖空总量
								</th>
								<th>
									总持仓量
								</th>
								<th>
									较上一日变化量
								</th>
								<th>
									精英买多总量
								</th>
								<th>
									精英买多持仓比例
								</th>
								<th>
									精英卖空总量
								</th>
								<th>
									精英卖空持仓比例
								</th>
								<th>
									时间
								</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${pm.datas}" var="l" varStatus="status">
								<tr>
									<td>
										<c:out value="${l.f_code}"></c:out>
									</td>
									<td>
										<fmt:formatNumber value="${l.buy_hold_count }" pattern="#0.####"/>
									</td>
									<td>
										<fmt:formatNumber value="${l.sell_hold_count }" pattern="#0.####"/>
									</td>
									<td>
										<fmt:formatNumber value="${l.buy_hold_count + l.sell_hold_count }" pattern="#0.####"/>
									</td>
									<td>
										<fmt:formatNumber value="${l.total_change / 10000 }" pattern="#0.####"/>万
									</td>
									<td>
										<fmt:formatNumber value="${l.elite_buy_hold_count }" pattern="#0.####"/>
									</td>
									<td>
										<fmt:formatNumber value="${l.elite_buy_ratio }" type="percent" pattern="#.###%"/>
									</td>
									<td>
										<fmt:formatNumber value="${l.elite_sell_hold_count }" pattern="#0.####"/>
									</td>
									<td>
										<fmt:formatNumber value="${l.elite_sell_ratio }" type="percent" pattern="#.###%"/>
									</td>
									<td>
										<ut:formatDate dateline="${l.record_time}"></ut:formatDate>
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
									<pg:pager url="managerDataBroadCast/data.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="code" value="${code}"/>
										<pg:param name="startTime" value="${startTime}"/>
										<pg:param name="endTime" value="${endTime}"/>
										
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
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
	
	</script>
</html>