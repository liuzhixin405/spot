<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title></title>
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
						用户合约开仓限制管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>managerDeliveryUserOpenLimit/list.do" method="get" name="selectForm">
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
										用户ID
										<input name="userId" type="text" value="${userId }"/>
										&nbsp;&nbsp;&nbsp;&nbsp;用户邮箱
										<input name="uemail" type="text" value="${uemail }"/>
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
									<a class="button" style="margin-left: 20px; width: 30px;" href="javascript:toAdd('<%=actionPath%>managerDeliveryUserOpenLimit/toAddOrUpdate.do?code=')">新 增</a>
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
									合约号
								</th>
								<th>
									最大开多数量
								</th>
								<th>
									最大开空数量
								</th>
								<th>
									添加时间
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
										<c:out value="${l.f_user_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.f_code}"></c:out>
									</td>
									<td>
										<c:out value="${l.max_kdc_nums}"></c:out>
									</td>
									<td>
										<c:out value="${l.max_kkc_nums}"></c:out>
									</td>
									<td>
										<ut:formatDate dateline="${l.add_time}"></ut:formatDate>
									</td>
									<TD>
										<a class="link" href="javascript:sure(null,'<%=actionPath%>managerDeliveryUserOpenLimit/toAddOrUpdate.do?id=${l.id}')" >修改</a>
										&nbsp;&nbsp;
										<a class="link" href="javascript:sure('确实删除吗?','<%=actionPath%>managerDeliveryUserOpenLimit/delete.do?id=${l.id}')" >删除</a>
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
									<pg:pager url="managerDeliveryUserOpenLimit/list.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="userId" value="${userId}"/>
										<pg:param name="email" value="${email}"/>
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