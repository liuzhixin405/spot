<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>管理员权限编辑</title>
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
						管理员权限编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerAdmin/addOrUpdateManager.do" method="post" >
									<input type="hidden" name="id" value="${manager.user_id}"></input>
									<c:if test="${manager.user_id ne null}">
										<label for="userName">管理员名称：</label>
										<div>
											<input name="userName" value="${manager.user_name}" readonly="readonly"></input>
										</div>
										<div class="clear"></div>
									</c:if>
									
									<c:if test="${manager.user_id eq null}">
										<label for="userName">管理员名称：</label>
										<div>
											<input name="userName" value=""></input>
										</div>
										<div class="clear"></div>
										
										<label for="loginPwd">登录密码：</label>
										<div>
											<input name="loginPwd" value=""></input>
										</div>
										<div class="clear"></div>
										
										<label for="safePwd">安全密码：</label>
										<div>
											<input name="safePwd" value=""></input>
										</div>
										<div class="clear"></div>
									</c:if>
									
									<label for="menus">菜单权限：</label>
									<div>
										<input type="hidden" value="${manager.user_role }" id="caidan" />
										<c:forEach var="menu" varStatus="stat" items="${menus}">
											<span style="width: 300px;display: inline-block;">
												<input type="checkbox" value="${menu.menu_no }" name="menus" />${menu.menu_name }
											</span>
										</c:forEach>
									</div>
									
									<div class="clear"></div>
									
									<!-- <label for="password">安全密码：</label>
									<input name="password" value=""></input>
									<div class="clear"></div> -->
									
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
<script type="text/javascript" src="<%=actionPath %>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript">
	$(function() {
		var checkeds = $("#caidan").val();
		var checkArray = checkeds.split(",");
		var checkBoxAll = $("input[name='menus']");
		for (var i = 0; i < checkArray.length; i++) {
			$.each(checkBoxAll, function(j, checkbox) {
				var checkValue = $(checkbox).val();
				if (checkArray[i] == checkValue) {
					$(checkbox).attr("checked", true);
				}
			})
		}
	});
</script>
</html>