<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>菜单链接管理</title>
		<link rel="stylesheet" href="<%=jspPath %>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath %>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath %>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript">
		   function sure(qr,url)
		   {
			   if(qr!=null)
			   {
				   if(confirm(qr))
				   {
					   document.location.href = url;
				   }
			   }
			   else{
				   document.location.href = url;
				   }
				   
		   }
		</script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						菜单链接管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerMenu/addOrUpdateMenu.do" method="post">
									<div class="form-item">
										<div class="text-label">菜单名称：</div>
										<input type="text" name="menuName" id="menuName" class="text-input " value="" required="required"></input>
									</div>
									<div class="form-item">
										<div class="text-label">菜单编号:</div>
										<input type="text" name="menuNo" id="menuNo" class="text-input " value="" required="required"></input>
									</div>
									<div class="form-item">
										<div class="text-label">URL链接：</div>
										<input type="text" name="link" id="link" class="text-input " value=""></input>
									</div>
									<div class="form-item">
										<div class="text-label">状态：</div>
										<select name="state" id="state">
											<option value="1">使用中</option>
											<option value="0">未使用</option>
										</select>
									</div>
									<div class="form-item">
										<div class="text-label">安全密码：</div>
										<input class="text-input " name="password" type="password" value="" />
									</div>
									<div class="align-left">
										<button type="submit" class="button">
											新 增
										</button>
									</div>
									<div class="clear"></div>
								</form>
							</div>
							<br>
						</div>
						<table id="changecolor" class="index-tb">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										菜单名称
									</th>
									<th>
										菜单编号
									</th>
									<th>
										URL链接
									</th>
									<th>
										状态
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
											<c:out value="${status.index+1}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.menu_name}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.menu_no}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.link}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.state eq '1'}">使用中</c:when>
												<c:when test="${l.state eq '0'}">未使用</c:when>
												<c:otherwise>其他</c:otherwise>
											</c:choose>
										</TD>
										<TD>
											<a class="link" href="javascript:sure(null,'<%=actionPath %>managerMenu/loadUpdateMenu.do?id=${l.id}')" >修改</a>
											&nbsp;&nbsp;
											<a class="link" href="javascript:sure('确实要删除该条主菜单吗?','<%=actionPath %>managerMenu/delManagerMenu.do?id=${l.id}')" >删除</a>
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
										<pg:pager url="managerMenu/loadManagerMenu.do" items="${pm.total}" maxIndexPages="10" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<%-- <pg:param name="title" value="${title}"/>
											<pg:param name="type" value="${type}"/> --%>
											
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
<!--
//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
--></script>


					</div>

				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>