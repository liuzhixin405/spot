<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>菜单链接编辑</title>
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
						菜单链接编辑
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
									<input type="hidden" name="id" value="${menu.id}"></input>
									<label for="menuName">菜单名称：</label>
									<input name="menuName" value="${menu.menu_name}" style="width: 200px"></input>
									<div class="clear"></div>
									
									<label for="menuNo">菜单编号：</label>
									<input name="menuNo" value="${menu.menu_no}"></input>
									<div class="clear"></div>
									
									<label for="link">URL链接：</label>
									<input name="link" value="${menu.link}"></input>
									<div class="clear"></div>
									
									<label for="state">状态：</label>
									<select name="state" id="state">
										<c:if test="${menu.state eq '1'}">
											<option value="1" selected="selected">使用中</option>
											<option value="0">未使用</option>
										</c:if>
										<c:if test="${menu.state eq '0'}">
											<option value="0" selected="selected">未使用</option>
											<option value="1">使用中</option>
										</c:if>
									</select>
									<div class="clear"></div>
									
									<label for="password">安全密码：</label>
									<input name="password" value=""></input>
									<div class="clear"></div>
									
									<button type="submit" id="submit" class="button">提交</button>
								</form>
							</div>
							<br>
						</div>
						
					</div>

				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>