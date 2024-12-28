<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<title>Btcside抵扣手续费记录</title>
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
						Btcside抵扣手续费记录
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- <div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
							<c:forEach items="${settings}" var="l" varStatus="status">
								<a href="<%=actionPath%>managerBfx/list.do?code=${l.f_code }">
									<font color="${code == l.f_code ? '#ff0000' : ''}">${l.f_code }</font>
								</a> &nbsp;
							</c:forEach>
							<div class="clear"></div>
						</div>  -->
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>managerBfx/records.do" method="get">
										用户ID
										<input type="text" name="userId" value="${userId}" ></input>
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
									用户ID
								</th>
								<th>
									抵扣(USDT)
								</th>
								<th>
									手续费(Btcside)
								</th>
								<th>
									Btcside均价
								</th>
								<th>
									折扣
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
										<c:out value="${l.f_user_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.fee}"></c:out>
									</td>
									<td>
										<c:out value="${l.amount}"></c:out>
									</td>
									<td>
										<c:out value="${l.price}"></c:out>
									</td>
									<td>
										<c:out value="${l.discount}"></c:out>
									</td>
									<td>
										<ut:formatDate dateline="${l.trade_time}"></ut:formatDate>
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
									<pg:pager url="managerBfx/records.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="userId" value="${userId}"/>
										
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
	function cancelHedging(id) {
		var r = confirm("确定取消交易对冲?");
		if (r == true) {
		    $.ajax({
				type : "POST",
				dataType : "json",
				url : "<%=actionPath%>managerArbitrage/cancelHedging.do",
				data : {
					id : id
				},
				success : function(data) {
					if (data.code == 'success') {
						window.location.reload();
					} else {
						alert(data.msg);
					}
				}
			});
		}
	}
	</script>
</html>