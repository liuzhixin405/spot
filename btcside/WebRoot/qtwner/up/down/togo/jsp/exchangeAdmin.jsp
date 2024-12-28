<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>后台兑换功能</title>
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
						后台兑换功能
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerExchange/exchangeAllAccount.do" method="post">
							<label for="oriCoinId">源币种：</label>
							<select name="oriCoinId" id="oriCoinId">
							<c:forEach items="${coins}" var="coin" varStatus="status">
								<option value="${coin.type_id}" selected="selected">${coin.type_name}</option>
							</c:forEach>
							</select>
							<div class="clear"></div>
							
							<label for="targetCoinId">目标币种：</label>
							<select name="targetCoinId" id="targetCoinId">
							<c:forEach items="${coins}" var="coin" varStatus="status">
								<option value="${coin.type_id}" selected="selected">${coin.type_name}</option>
							</c:forEach>
							</select>
							<div class="clear"></div>
							
							<label for="rate">汇率：</label>
							<input name="rate" value="1"></input>
							<div class="clear"></div>
							
							<button type="submit" id="submit" class="button">提交</button>
						</form>
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