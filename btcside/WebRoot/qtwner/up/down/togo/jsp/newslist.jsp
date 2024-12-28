<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>公告资讯管理</title>
		<!-- Reset Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath %>css/reset.css" type="text/css" media="screen" />
		<!-- Main Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath %>css/style.css" type="text/css" media="screen" />
		<!-- Invalid Stylesheet. This makes stuff look pretty. Remove it if you want the CSS completely valid -->
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
						公告资讯管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerNotice/selectpage.do" method="post">
									标题：
									<input type="text" name="title" class="text-input small-input" value="${title}"></input>
									类型
									<select name="type" class="small-input" >
										<c:choose>
											<c:when test="${type==1}">
												<option value="1" selected="selected">
													站内公告
												</option>
												<option value="2">
													信息披露
												</option>
												<option value="3">
													帮助
												</option>
												<option value="4">
													新闻资讯
												</option>
											</c:when>
											<c:when test="${type==2}">
												<option value="1">
													站内公告
												</option>
												<option value="2" selected="selected">
													信息披露
												</option>
												<option value="3">
													帮助
												</option>
												<option value="4">
													新闻资讯
												</option>
											</c:when>
											<c:when test="${type==3}">
												<option value="1">
													站内公告
												</option>
												<option value="2">
													信息披露
												</option>
												<option value="3" selected="selected">
													帮助
												</option>
												<option value="4">
													新闻资讯
												</option>
											</c:when>
											<c:when test="${type==4}">
												<option value="1">
													站内公告
												</option>
												<option value="2">
													信息披露
												</option>
												<option value="3">
													帮助
												</option>
												<option value="4" selected="selected">
													新闻资讯
												</option>
											</c:when>
											<c:otherwise>
												<option value="1">
													站内公告
												</option>
												<option value="2">
													信息披露
												</option>
												<option value="3">
													帮助
												</option>
												<option value="4">
													新闻资讯
												</option>
											</c:otherwise>
										</c:choose>
									</select>
									<button type="submit" class="button" >查 询</button>
								</form>
								<br />
								<form action="<%=actionPath %>managerNotice/loadaddnews.do" method="post">
									<input type="hidden" value="0" name="newsid"/>
									<button type="submit" class="button" >新增</button>
								</form>
							</div>
							<br>
						</div>
						<font color="red">${msg}</font>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										标题
									</th>
									<th>
										类型
									</th>
									<th>
										语言
									</th>
									<th>
										创建日期
									</th>
									<th>
										置顶
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
											<c:out value="${l.title}"></c:out>
										</TD>

										<TD>
											<c:choose>
												<c:when test="${l.category_id==1}">
												站内公告
												</c:when>
												<c:when test="${l.category_id==2}">
												信息披露
												</c:when>
												<c:when test="${l.category_id==3}">
												帮助
												</c:when>
												<c:when test="${l.category_id==4}">
												新闻资讯
												</c:when>
											</c:choose>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.language=='zh_CN'}">
												简体中文
												</c:when>
												<c:when test="${l.language=='en_US'}">
												English
												</c:when>
												<c:when test="${l.language=='ja_JP'}">
													日语
												</c:when>
												<c:when test="${l.language=='ko_KR'}">
													韩文
												</c:when>
												<c:when test="${l.language=='zh_HK'}">
													繁体
												</c:when>
											</c:choose>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.dateline}"></ut:formatDate>
										</TD>
										<td>
										<a class="link" href="javascript:sure(null,'<%=actionPath %>managerNotice/topnews.do?newsid=${l.article_id}&top=${l.istop}')" >
											<c:choose>
												<c:when test="${l.istop==1}">
												置顶
												</c:when>
												<c:otherwise>
												普通
												</c:otherwise>
											</c:choose>										
										</a>
										</td>
										<TD>
											<a class="link" href="javascript:sure(null,'<%=actionPath %>managerNotice/loadaddnews.do?newsid=${l.article_id}')" >修改</a>
											&nbsp;&nbsp;
											<a class="link" href="javascript:sure('确实要删除该条信息吗?','<%=actionPath %>managerNotice/delnews.do?newsid=${l.article_id}')" >删除</a>
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
										<pg:pager url="managerNotice/selectpage.do" items="${pm.total}" maxIndexPages="10" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="title" value="${title}"/>
											<pg:param name="type" value="${type}"/>
											
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