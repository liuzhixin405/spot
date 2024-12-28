<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>挖矿阶段编辑</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript">
			function postsubmit(){
				return true;
			}
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						挖矿阶段编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1" style="line-height: 24px;">
						<form class="notification depth-form"  action="<%=actionPath%>managerMine/updateMineStageSetting.do" method="post" onsubmit="return postsubmit();">
							<input type="hidden" name="id" id="id" value="${setting.id}"></input>
							<label>阶段编号:</label>
							<input type="number" name="num" id="num"  value="${setting.num}" required="required"></input>
							<div class="clear"></div>
							
							<label>该阶段起始币量:</label>
							<input type="text" name="stage_amount" id="stage_amount"  value="${setting.stage_amount}" required="required"></input>
							<div class="clear"></div>
							
							<label>1手续费得平台币：</label>
							<input type="text" name="rate" id="rate" value="${setting.rate}" required="required"></input>
							<div class="clear"></div>
							
							<button type="submit" class="button">
								提交
							</button>
							<div class="clear"></div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
