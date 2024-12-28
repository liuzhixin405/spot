<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>市价委托设置管理</title>
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
						市价委托设置管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerMarketOrder/update.do" method="post">
							<input type="hidden" name="id" value="${setting.id}"></input>
						
							<label for="price_range">价格阈值范围比例：</label>
							<input name="price_range" value="${setting.price_range}"></input>
							<span>(填写小数，eg：0.05=5%)</span>
							<div class="clear"></div>
							
							<label for="running">状态：</label>
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