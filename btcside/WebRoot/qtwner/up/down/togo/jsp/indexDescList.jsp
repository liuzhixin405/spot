<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>指数说明管理</title>
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
						指数说明管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerIndexDesc/loadAddIndexDesc.do" method="post">
									<button type="submit" class="button" >新增指数说明</button>
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
										类型码
									</th>
									<th>
										中文说明
									</th>
									<th>
										英文说明
									</th>
									<th>
										日文说明
									</th>
									<th>
										韩文说明
									</th>
									<th>
										繁体说明
									</th>
									<th>
										创建日期
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
											<c:out value="${l.type_code}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.zh_description}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.en_description}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.ja_description}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.ko_description}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.hk_description}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.dateline}"></ut:formatDate>
										</TD>
										<TD>
											<a class="link" href="javascript:sure(null,'<%=actionPath %>managerIndexDesc/loadAddIndexDesc.do?id=${l.id}')" >修改</a>
											&nbsp;&nbsp;
											<a class="link" href="javascript:sure('确实要删除该条说明吗?','<%=actionPath %>managerIndexDesc/delIndexDesc.do?id=${l.id}')" >删除</a>
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
										<pg:pager url="managerIndexDesc/loadIndexDesc.do" items="${pm.total}" maxIndexPages="10" 
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