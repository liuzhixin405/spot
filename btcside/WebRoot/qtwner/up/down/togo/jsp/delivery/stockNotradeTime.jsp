<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>股指休市时间管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
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
						股指休市时间管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
				
				<div class="tab-content default-tab" id="tab1">
					<div class="notification success png_bg">
						<br>
						<div>
							<form action="delivery/managerStockSetting/addNoTradeTime.do" method="post">
								合约号：
								<select name="transId" id="transId" onchange="changeTrans(this)">
									<c:forEach items="${transList}" var="trans">
										<c:if test="${current eq trans.transaction_id}">
											<option value="${trans.transaction_id}" selected="selected">${trans.transaction_code}</option>
										</c:if>
										<c:if test="${current ne trans.transaction_id}">
											<option value="${trans.transaction_id}">${trans.transaction_code}</option>
										</c:if>
									</c:forEach>
								</select>
								&nbsp;&nbsp;
								休市日期：
								<input name="date" type="text" id="date"
										value="" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})"
										class="Wdate" readonly="readonly" /> 
								<button type="submit" class="button">添加</button>
							</form>
						</div>
						<br>
						
					</div>
					
					<font color="red">${msg}</font>
				</div>
				
				<table id="changecolor">
					<thead>
						<tr>
							<th>
								合约号
							</th>
							<th>
								休市日期
							</th>
							<th>
								操作
							</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${pm.datas}" var="l" varStatus="status">
							<tr>
								<TD>
									<c:forEach items="${transList}" var="trans">
										<c:if test="${l.transaction_id == trans.transaction_id}">
											<c:out value="${trans.transaction_code}"></c:out>
										</c:if>
									</c:forEach>
								</TD>
								<TD>
									<ut:formatDate dateline="${l.h_time}"></ut:formatDate>
								</TD>
								<TD>
									<a class="link" href="javascript:sure('删除休市时间','<%=actionPath %>delivery/managerStockSetting/deleteNotradeTime.do?id=${l.h_id}')" >删除</a>
								</TD>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="11">
								<div class="pagination">
									<!-- ********** 分页开始 **********	-->
									<pg:pager url="delivery/managerStockSetting/loadNotradeTime.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">

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
								<div class="clear"></div>
							</td>
						</tr>
					</tfoot>
				</table>
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
		var basePath = '<%=actionPath%>';
		function changeTrans(obj) {
			window.location.href= basePath + "delivery/managerStockSetting/loadNotradeTime.do?transId=" + obj.value;
		}
	</script>
</html>