<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>挖矿记录</title>
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
		
		<script type="text/javascript">
			function sure(qr, url) {
				if (qr != null) {
					if (confirm(qr)) {
						document.location.href = url;
					}
				} else {
					document.location.href = url;
				}
		
			}
		</script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						挖矿记录
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>managerMine/mineRecords.do" method="get">
										用户ID
										<input name="userId" type="text" value="${userId }"/>
										&nbsp;&nbsp;&nbsp;&nbsp;用户邮箱
										<input name="email" type="text" value="${email }"/>
<%--										&nbsp;&nbsp;&nbsp;&nbsp;<a id="mode" value = "${mode}">交易模式</a>--%>
										&nbsp;&nbsp;&nbsp;&nbsp;交易模式
										<select name=tradeMode id="tradeMode">
											<c:if test="${mode == 0}">
												<option value="0">全部</option>
												<option value="1">永续合约</option>
												<option value="2">现货</option>
											</c:if>
											<c:if test="${mode == 1}">
												<option value="1">永续合约</option>
												<option value="0">全部</option>
												<option value="2">现货</option>
											</c:if>
											<c:if test="${mode == 2}">
												<option value="2">现货</option>
												<option value="0">全部</option>
												<option value="1">永续合约</option>
											</c:if>
										</select>
										&nbsp;&nbsp;&nbsp;&nbsp;开始时间
										<input name="startTime" type="text" value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										&nbsp;&nbsp;&nbsp;&nbsp;结束时间
										<input name="endTime" type="text" value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查询</button>
										<br>
										用户已挖矿数量:
										<font color="red">${minedAmount}</font>
									</form>
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
									用户ID
								</th>
								<th>
									交易类型
								</th>
								<th>
									交易模式
								</th>
								<th>
									交易ID
								</th>
								<th>
									交易额
								</th>
								<th>
									交易手续费
								</th>
								<th>
									挖矿比例
								</th>
								<th>
									挖币量
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
										<c:out value="${l.f_user_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.trade_type}"></c:out>
									</td>
									<td>
										<c:if test="${l.trade_mode == 1}">
											<c:out value="永续合约"></c:out>
										</c:if>
										<c:if test="${l.trade_mode == 2}">
											<c:out value="现货"></c:out>
										</c:if>
									</td>
									<td>
										<c:out value="${l.trade_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.trade_amount}"></c:out>
									</td>
									<td>
										<c:out value="${l.trade_fee}"></c:out>
									</td>
									<td>
										<c:out value="${l.mine_rate}"></c:out>
									</td>
									<td>
										<c:out value="${l.mine_amount}"></c:out>
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
									<pg:pager url="managerMine/mineRecords.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="userId" value="${userId}"/>
										<pg:param name="email" value="${email}"/>
										<pg:param name="mode" value="${mode}"/>
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