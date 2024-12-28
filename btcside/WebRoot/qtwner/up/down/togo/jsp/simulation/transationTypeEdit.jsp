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
		<title>模拟交易-合约类型编辑</title>
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
						模拟交易-合约类型编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>simulation/managerTrans/updateManagerTransType.do" method="post" onsubmit="return postsubmit();">
									<div>
									<input type="hidden" name="typeId" id="typeId" class="text-input " value="${transType.type_id}"></input>
									<div class="text-label">合约类型名称:</div>
									<input type="text" name="typeName" id="typeName" class="text-input " value="${transType.type_name}" required="required"></input>
									</div>
									<div>
									<div class="text-label">合约类型号：</div>
									<input type="text" name="typeCode" id="typeCode" class="text-input " value="${transType.type_code}" required="required"></input>
									</div>
									<div>
									<div class="text-label">是否使用：</div>
									<select name="isUsing" id="isUsing">
										<c:if test="${transType.is_using}">
											<option value="1" selected="selected">使用中</option>
											<option value="0">不使用</option>
										</c:if>
										<c:if test="${!transType.is_using}">
											<option value="1">使用中</option>
											<option value="0" selected="selected">不使用</option>
										</c:if>
									</select>
									</div>
									<div>
									<div class="text-label">最小开仓数量：</div>
									<input type="text" name="minKcNums" id="minKcNums" class="text-input " value="${transType.min_kc_nums}"></input>
									</div>
									<div>
									<div class="text-label">最小波动：</div>
									<input type="text" name="minFloatPrice" id="minFloatPrice" class="text-input " value="${transType.min_float_price}"></input>
									</div>
									<div>
									<div class="text-label">默认杠杆：</div>
									<input type="text" name="defaultMultiple" id="defaultMultiple" class="text-input " value="${transType.default_multiple}"></input>
									</div>
									<div>
									<div class="text-label">默认杠杆状态：</div>
									<input type="text" name="defaultMultipleState" id="defaultMultipleState" class="text-input " value="${transType.default_multiple_state}"></input>
									</div>
									<div>
									<div class="text-label">默认交易系数：</div>
									<input type="text" name="defaultTradeCoefficient" id="defaultTradeCoefficient" class="text-input " value="${transType.default_coefficient}"></input>
									</div>
									<div>
									<div class="text-label">指数地址：</div>
									<input type="text" class="text-input " name="indexAddress" id="indexAddress" value="${transType.index_address}" />
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
