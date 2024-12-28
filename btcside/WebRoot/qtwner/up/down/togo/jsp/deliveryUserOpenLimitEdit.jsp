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
		<title>合约限仓编辑</title>
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
						用户合约限仓编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerDeliveryUserOpenLimit/add.do" method="post" onsubmit="return postsubmit();">
									<div>
									<input type="hidden" name="id" id="id" class="text-input " value="${limit.id}"></input>
									<div class="text-label">用户id:</div>
									<input type="text" name="f_user_id" id="f_user_id" class="text-input " value="${limit.f_user_id}" <c:if test='${limit ne null}'>readonly="readonly"</c:if> required="required"></input>
									</div>
									<div>
									<div class="text-label">合约号：</div>
									<c:if test='${limit ne null}'>
									<input type="text" name="f_code" id="f_code" class="text-input " value="${limit.f_code}" readonly="readonly" required="required"></input>
									</c:if>
									<c:if test='${limit eq null}'>
									<select name="f_code" id="f_code" >
										<c:forEach items="${lstTrans}" var="l">
											<c:if test="${l.transaction_code eq current}">
												<option value="${l.transaction_code}" selected="selected">${l.transaction_code}</option>
											</c:if>
											<c:if test="${l.transaction_code ne current}">
												<option value="${l.transaction_code}">${l.transaction_code}</option>
											</c:if>
										</c:forEach>
									</select>
									</c:if>
									</div>
									
									<div>
									<div class="text-label">最大开多数量：</div>
									<input type="text" name="max_kdc_nums" id="max_kdc_nums" class="text-input " value="${limit.max_kdc_nums}" required="required"></input>
									</div>
									
									<div>
									<div class="text-label">最大开空数量：</div>
									<input type="text" name="max_kkc_nums" id="max_kkc_nums" class="text-input " value="${limit.max_kkc_nums}" required="required"></input>
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
