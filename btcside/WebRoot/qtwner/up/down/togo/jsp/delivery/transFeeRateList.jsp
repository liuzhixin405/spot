<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>合约费率管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<style type="text/css">
			.clear {
				clear: both;
			}
		</style>
		
		<script type="text/javascript">
			function toAdd(url) {
				var code = $('#code option:selected').val();
				console.log(code);
				document.location.href = url + code;
			}
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
						合约费率管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>delivery/managerTrans/feeRateList.do" method="get" name="selectForm">
										合约号：
										<select name="code" id="code" onchange="javascript:document.selectForm.submit();">
											<c:forEach items="${lstTrans}" var="l">
												<c:if test="${l.transaction_code eq current}">
													<option value="${l.transaction_code}" selected="selected">${l.transaction_code}</option>
												</c:if>
												<c:if test="${l.transaction_code ne current}">
													<option value="${l.transaction_code}">${l.transaction_code}</option>
												</c:if>
											</c:forEach>
										</select>
										<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查 询</button>
									</form>
								</div>
								
								<div class="clear"></div>
							</div>
						</div>
					</div>
					
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<a class="button" style="margin-left: 20px; width: 30px;" href="javascript:toAdd('<%=actionPath%>delivery/managerTrans/feeRateAddOrUpdate.do?code=')">新 增</a>
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
									VIP等级
								</th>
								<th>
									挂单费率
								</th>
								<th>
									吃单费率
								</th>
								<th>
									操作
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
										<c:out value="${l.level_name}"></c:out>
									</td>
									<td>
										<c:out value="${l.pending_commission_rate}"></c:out>
									</td>
									<td>
										<c:out value="${l.eat_commission_rate}"></c:out>
									</td>
									<TD>
										<a class="link" href="javascript:sure(null,'<%=actionPath%>delivery/managerTrans/feeRateAddOrUpdate.do?id=${l.id}')" >修改</a>
										&nbsp;&nbsp;
										<a class="link" href="javascript:sure('确实删除吗?','<%=actionPath%>delivery/managerTrans/feeRateDelete.do?id=${l.id}')" >删除</a>
									</TD>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<tfoot>
						<tr>
							<td colspan="9">
								<div class="pagination">
									<!-- ********** 分页开始 **********	-->
									<pg:pager url="managerTrans/feeRateList.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="code" value="${current}"/>
										
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