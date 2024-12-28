<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="../_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>交割合约-合约分区管理</title>
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
						交割合约-合约分区管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="delivery/managerTrans/updateTransPartition.do" method="post">
						
							<input name="id" type="hidden" value="${partition.id}"></input>
						
							<label for=name_cn>中文名称：</label>
							<input name="name_cn" value="${partition.name_cn}"></input>
							<div class="clear"></div>
							
							<label for="name_en">英文名称：</label>
							<input name="name_en" value="${partition.name_en}"></input>
							<div class="clear"></div>
							
							<label for="name_jp">日文名称：</label>
							<input name="name_jp" value="${partition.name_jp}"></input>
							<div class="clear"></div>
							
							<label for="name_kr">韩文名称：</label>
							<input name="name_kr" value="${partition.name_kr}"></input>
							<div class="clear"></div>
							
							<label for="name_hk">繁体名称：</label>
							<input name="name_hk" value="${partition.name_hk}"></input>
							<div class="clear"></div>
							
							<label for="sort">排序：</label>
							<input name="sort" value="${partition.sort}"></input>
							<div class="clear"></div>
							
							<label for="codes">合约列表（逗号分开）：</label>
							<input name="codes" value="${partition.codes}"></input>
							<div class="clear"></div>
							
							<button type="submit" class="button">提交</button>
						</form>
					</div>
					
					<table id="changecolor">
							<thead>
								<tr>
									<th>
										中文名称
									</th>
									<th>
										英文名称
									</th>
									<th>
										日文名称
									</th>
									<th>
										韩文名称
									</th>
									<th>
										繁体名称
									</th>
									<th>
										排序
									</th>
									<th>
										包含合约
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
											<c:out value="${l.name_cn}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.name_en}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.name_jp}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.name_kr}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.name_hk}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.sort}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.codes}"></c:out>
										</TD>
										<TD>
											<a class="link" href="javascript:sure(null,'<%=actionPath%>delivery/managerTrans/loadTransPartition.do?id=${l.id}')" >修改</a>
											&nbsp;&nbsp;
											<a class="link" href="javascript:sure('删除该分区?','<%=actionPath %>delivery/managerTrans/deleteTransPartition.do?id=${l.id}')" >删除</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="delivery/managerTrans/loadTransPartition.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												
												<pg:param name="id" value="${partition.id}"/>
												
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
						<script language="javascript" type="text/javascript">
						<!--
						//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
						senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						--></script>
						
					<font color="red">${msg}</font>
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