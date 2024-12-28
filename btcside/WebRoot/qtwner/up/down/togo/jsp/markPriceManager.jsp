<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>标记价格管理</title>
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
						标记价格管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerTrans/updateMarkPriceSetting.do" method="post">
							<input name="id" type="hidden" value="${setting.id}"></input>
							
							<label for=code>合约号（通用为空）：</label>
							<input name="code" value="${setting.code}" <c:if test="${setting.id > 0 }">readonly</c:if>></input>
							<div class="clear"></div>
						
						    <label for="running">系统状态：</label>
							<select name="running" id="running">
								<c:if test="${setting.running}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.running}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<label for=moving_period>平均移动周期：</label>
							<input name="moving_period" value="${setting.moving_period}"></input>
							<div class="clear"></div>
							
							<label for="open_width_filter">盘口宽度过滤，大于此值将忽略：(卖一 - 买一) / 买一</label>
							<input name="open_width_filter" value="${setting.open_width_filter}"></input>
							<div class="clear"></div>
							
							<label for="index_rate_filter">偏离指数范围限制（如0.01）：</label>
							<input name="index_rate_filter" value="${setting.index_rate_filter}"></input>
							<div class="clear"></div>
							
							<label for="fixed">是否固定价格：</label>
							<select name="fixed" id="fixed">
								<c:if test="${setting.fixed}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.fixed}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<label for="fixed_price">固定价格：</label>
							<input name="fixed_price" value="${setting.fixed_price}"></input>
							<div class="clear"></div>
							
							<c:if test="${setting.id > 0 }"><button type="submit" class="button">修改</button></c:if>
							<c:if test="${setting.id <= 0 }"><button type="submit" class="button">新增</button></c:if>
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
									是否开启
								</th>
								<th>
									移动周期
								</th>
								<th>
									盘口宽度过滤
								</th>
								<th>
									指数范围比例
								</th>
								<th>
									是否固定价格
								</th>
								<th>
									固定价格
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
										<c:out value="${l.code}"></c:out>
									</td>
									<td>
										<c:out value="${l.running}"></c:out>
									</td>
									<td>
										<c:out value="${l.moving_period}"></c:out>
									</td>
									<td>
										<c:out value="${l.open_width_filter}"></c:out>
									</td>
									<td>
										<c:out value="${l.index_rate_filter}"></c:out>
									</td>
									<td>
										<c:out value="${l.fixed}"></c:out>
									</td>
									<td>
										<c:out value="${l.fixed_price}"></c:out>
									</td>
									<td>
										<a href="<%=actionPath%>managerTrans/loadMarkPriceSetting.do?code=${l.code}">编辑</a> 
										<a href="<%=actionPath%>managerTrans/deleteMarkPriceSetting.do?id=${l.id}" class="red">删除</a>
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
									<pg:pager url="managerTrans/loadMarkPriceSetting.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
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