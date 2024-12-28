<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>量化自动撤单管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
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
						量化自动撤单管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerDeliveryQuantSetting/update.do" method="post">
							<input type="hidden" name="id" value="${setting.id}"></input>
						
							<label for="uids">用户id（逗号隔开）：</label>
							<input name="uids" value="${setting.uids}"></input>
							<div class="clear"></div>
							
							<label for="cancel_timeout">超出多少时间撤销(秒)：</label>
							<input name="cancel_timeout" value="${setting.cancel_timeout}"></input>
							<div class="clear"></div>
							
							<label for="keep_nums">保留多少笔：</label>
							<input name="keep_nums" value="${setting.keep_nums}"></input>
							<div class="clear"></div>
							
							<label for="is_auto_cancel">开启状态：</label>
							<select name="is_auto_cancel" id="is_auto_cancel">
								<c:if test="${setting.is_auto_cancel}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.is_auto_cancel}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<label for="auto_duiping">自动对平：</label>
							<select name="auto_duiping" id="auto_duiping">
								<c:if test="${setting.auto_duiping}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.auto_duiping}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<button type="submit" class="button">提交</button>
						</form>
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