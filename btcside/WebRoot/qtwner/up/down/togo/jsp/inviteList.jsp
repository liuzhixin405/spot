<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>邀请管理</title>
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
		
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						邀请管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>managerInviteManagement/inviteList.do" method="get">
										用户ID
										<input name="userId" type="text" value="${userId }"/>
										&nbsp;&nbsp;&nbsp;&nbsp;用户邮箱
										<input name="uemail" type="text" value="${uemail }"/>
										<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查询</button>
									</form>
								</div>
								
								<div class="clear"></div>
							</div>
						</div>
					</div>
					
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>managerInviteManagement/updateInvite.do" method="post">
										用户ID
										<input name="userId" type="text" value="${userId }" required="required"/>
										&nbsp;&nbsp;&nbsp;&nbsp;上级用户邮箱
										<input name="parentEmail" type="text" value="${parentEmail }" required="required"/>
										&nbsp;&nbsp;&nbsp;&nbsp;
										<button type="submit" class="button" style="margin-left: 20px; width: 100px;">修改</button>
									</form>
								</div>
								
								<div class="clear"></div>
							</div>
						</div>
					</div>
					
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