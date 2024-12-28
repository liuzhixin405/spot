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
		<title>综合指数类型编辑</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
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
						综合指数类型编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerCompositeIndex/updateCompositeType.do" method="post" onsubmit="return postsubmit();">
									<input type="hidden" name="typeId" id="typeId" class="text-input " value="${type.type_id}"></input>
									
									<div>
									<div class="text-label">合约类型号：</div>
									<input type="text" name="typeCode" id="typeCode" class="text-input " value="${type.transaction_type_code}" required="required"></input>
									</div>
									
									<div>
									<div class="text-label">指数类型名称:</div>
									<input type="text" name="typeName" id="typeName" class="text-input " value="${type.type_name}" required="required"></input>
									</div>
									
									<div>
									<div class="text-label">基准开盘指数:</div>
									<input type="text" name="benchmarkPrice" id="benchmarkPrice" class="text-input " value="${type.benchmark_index.doubleValue()}" required="required"></input>
									</div>
									
									<div>
									<div class="text-label">是否使用：</div>
									<select name="isUsing" id="isUsing">
										<c:if test="${type.is_using}">
											<option value="1" selected="selected">使用中</option>
											<option value="0">不使用</option>
										</c:if>
										<c:if test="${!type.is_using}">
											<option value="1">使用中</option>
											<option value="0" selected="selected">不使用</option>
										</c:if>
									</select>
									</div>
									
									<div>
									<div class="text-label">安全密码：</div>
									<input class="text-input " name="password" type="password" value="" />
									</div>
									<div>
									<div class="text-label"></div>
									<button type="submit" class="button">
										提交
									</button>
									</div>
									<div class="clear"></div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
