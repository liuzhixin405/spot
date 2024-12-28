<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>持仓成本调整</title>
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
			function sure() {
				var str = "确定给" + $("#code").val() + "合约持仓成本调整：" + $("#value").val() + "？";
				if (confirm(str)) {
					return true;
				}
				
				return false;
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
						持仓成本调整
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					<div style="line-height: 24px;">
						<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
						<c:forEach items="${transList}" var="l" varStatus="status">
							<a href="<%=actionPath%>managerHoldCostPrice/costManager.do?code=${l.transaction_code }">
								<font color="${current.transaction_code == l.transaction_code ? '#ff0000' : ''}">${l.transaction_code.toUpperCase() }</font>
							</a> &nbsp;
						</c:forEach>
						<div class="clear"></div>
							
						<form class="notification depth-form" action="managerHoldCostPrice/updateCost.do" method="post" onsubmit="return sure();">
							<input name="code" type="hidden" id="code" value="${current.transaction_code}"></input>
	
							<label for=value>调整额度：</label>
							<input name="value" id="value" value=""></input>
							<div class="clear"></div>
							
							<button type="submit" class="button">提交</button>
						</form>
					</div>
						
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									合约号
								</th>
								<th>
									用户ID
								</th>
								<th>
									持仓均价
								</th>
								<th>
									持仓个数
								</th>
								<th>
									冻结个数
								</th>
								<th>
									杠杆倍数
								</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${pm.datas}" var="l" varStatus="status">
								<tr>
									<td>
										<c:out value="${l.transaction_code}"></c:out>
									</td>
									<td>
										<c:out value="${l.f_user_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.average_price}"></c:out>
									</td>
									<td>
										<c:out value="${l.hold_count}"></c:out>
									</td>
									<td>
										<c:out value="${l.lock_count}"></c:out>
									</td>
									<td>
										<c:out value="${l.trade_multiple}"></c:out>
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
									<pg:pager url="managerHoldCostPrice/costManager.do?code=${current.transaction_code }" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
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