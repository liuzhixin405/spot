<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>合约费率编辑</title>
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
						合约费率编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>delivery/managerTrans/feeRateAdd.do" method="post" onsubmit="return postsubmit();">
									<input type="hidden" name="id" id="id" class="text-input " value="${rate.id}"></input>
									
									<div>
									<div class="text-label">合约号：</div>
									<c:if test='${rate ne null}'>
										<input type="text" name="f_code" id="f_code" class="text-input " value="${rate.f_code}" readonly="readonly" required="required"></input>
									</c:if>
									<c:if test='${rate eq null}'>
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
									<div class="text-label">VIP等级:</div>
									<c:if test='${rate ne null}'>
										<input type="hidden" name="level_id" id="level_id" class="text-input" value="${rate.level_id}"></input>
										<input type="text" name="level_name" id="level_name" class="text-input" value="${rate.level_name}" readonly="readonly" required="required"></input>
									</c:if>
									<c:if test='${rate eq null}'>
										<select name="level_id" id="level_id" >
											<c:forEach items="${levels}" var="l">
												<option value="${l.level_id}">${l.level_name}</option>
											</c:forEach>
										</select>
									</c:if>
									</div>
									
									<div>
									<div class="text-label">挂单费率：</div>
									<input type="text" name="pending_commission_rate" id="pending_commission_rate" class="text-input " value="${rate.pending_commission_rate}" required="required"></input>
									<span>若费率为0，则实行用户等级制度的挂单费率</span>
									</div>
									
									<div>
									<div class="text-label">吃单费率：</div>
									<input type="text" name="eat_commission_rate" id="eat_commission_rate" class="text-input " value="${rate.eat_commission_rate}" required="required"></input>
									<span>若费率为0，则实行用户等级制度的吃单费率</span>
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
