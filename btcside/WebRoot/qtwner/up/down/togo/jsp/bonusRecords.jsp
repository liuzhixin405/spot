<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>用户分红记录</title>
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
						用户分红记录
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>managerMine/bonusRecords.do" method="get">
										用户ID
										<input name="userId" type="text" value="${userId }"/>
										&nbsp;&nbsp;&nbsp;&nbsp;用户邮箱
										<input name="email" type="text" value="${email }"/>
										&nbsp;&nbsp;&nbsp;&nbsp;开始时间
										<input name="startTime" type="text" value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										&nbsp;&nbsp;&nbsp;&nbsp;结束时间
										<input name="endTime" type="text" value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查询</button>
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
									分红币种ID
								</th>
								<th>
									1BF分红
								</th>
								<th>
									分红所得
								</th>
								<th>
									当日参与分红总手续费
								</th>
								<th>
									当前平台总挖矿+IEO解冻
								</th>
								<th>
									BF余额
								</th>
								<th>
									BF冻结
								</th>
								<th>
									日期
								</th>
								<th>
									记录时间
								</th>
								<th>
									备注
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
										<c:out value="${l.currency_type_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.rate}"></c:out>
									</td>
									<td>
										<c:out value="${l.amount}"></c:out>
									</td>
									<td>
										<c:out value="${l.total_fee}"></c:out>
									</td>
									<td>
										<c:out value="${l.total_amount}"></c:out>
									</td>
									<td>
										<c:out value="${l.hold_amount}"></c:out>
									</td>
									<td>
										<c:out value="${l.frozen_amount}"></c:out>
									</td>
									<td>
										<c:out value="${l.day}"></c:out>
									</td>
									<td>
										<ut:formatDate dateline="${l.record_time}"></ut:formatDate>
									</td>
									<td>
										<c:out value="${l.note}"></c:out>
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
									<pg:pager url="managerMine/bonusRecords.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="userId" value="${userId}"/>
										<pg:param name="email" value="${email}"/>
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