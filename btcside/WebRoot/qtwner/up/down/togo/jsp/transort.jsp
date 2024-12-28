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
		<title>合约排序管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
		<script type="text/javascript">
			function asd(url) {
				// yanzheng
			
			$.post(url + "?t=" + new Date().getTime(),
				$("#selectForm").serialize(), function(data) {
					if (data == "success") {
						document.selectForm.submit();
					} else {
						alert(data);
					}
					return;
				});

	}
</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						合约排序管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerTrans/transort.do" method="post" name="selectForm" id="selectForm">
								<c:forEach items="${currtrans}" var="tran">
									合约号：
									<input type="text" id="bqCode" name="${tran.transaction_code}" class="text-input " value="${tran.transaction_code}" readonly="readonly"></input>
									序号：
									<input type="text" id="bqsort" name="${tran.sort}" class="text-input " value="${tran.sort}"></input>
								<br>
								</c:forEach>
									<button type="button" class="button" onclick="asd('<%=actionPath%>managerTrans/transortsave.do')">
										保存
									</button>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
