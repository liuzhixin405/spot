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
		<title>模拟交易-合约类型管理</title>
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
						模拟交易-合约类型管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>simulation/managerTrans/updateManagerTransType.do" method="post" onsubmit="return postsubmit();">
									<div class="form-item">
									<div class="text-label">合约类型名称:</div>
									<input type="text" name="typeName" id="typeName" class="text-input " value="" required="required"></input>
									</div>
									<div class="form-item">
									<div class="text-label">合约类型号：</div>
									<input type="text" name="typeCode" id="typeCode" class="text-input " value="" required="required"></input>
									</div>
									<div class="form-item">
									<div class="text-label">是否使用：</div>
									<select name="isUsing" id="isUsing">
										<option value="1">使用中</option>
										<option value="0">不使用</option>
									</select>
									</div>
									<div class="form-item">
									<div class="text-label">最小开仓数量：</div>
									<input type="text" name="minKcNums" id="minKcNums" class="text-input " value=""></input>
									</div>
									<div class="form-item">
									<div class="text-label">最小波动：</div>
									<input type="text" name="minFloatPrice" id="minFloatPrice" class="text-input " value=""></input>
									</div>
									<div class="form-item">
									<div class="text-label">默认杠杆：</div>
									<input type="text" name="defaultMultiple" id="defaultMultiple" class="text-input " value="${multiple}"></input>
									</div>
									<div class="form-item">
									<div class="text-label">默认杠杆状态：</div>
									<input type="text" name="defaultMultipleState" id="defaultMultipleState" class="text-input " value="${multipleState}"></input>
									</div>
									<div class="form-item">
									<div class="text-label">默认交易系数：</div>
									<input type="text" name="defaultTradeCoefficient" id="defaultTradeCoefficient" class="text-input " value=""></input>
									</div>
									<div class="form-item">
									<div class="text-label">指数地址：</div>
									<input type="text" name="indexAddress" id="indexAddress" class="text-input " value=""></input>
									</div>
									<div class="form-item">
									<div class="text-label">安全密码：</div>
									<input class="text-input " name="password" type="password" value="" />
									</div>
									<div class="align-left">
									<button type="submit" class="button">
										新 增
									</button>
									</div>
									<div class="clear"></div>
								</form>
							</div>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										ID
									</th>
									<th>
										合约类型名称
									</th>
									<th>
										合约类型号
									</th>
									<th>
										是否使用
									</th>
									<th>
										最小开仓数量
									</th>
									<th>
										最小价格波动
									</th>
									<th>
										默认杠杆
									</th>
									<th>
										默认杠杆状态
									</th>
									<th>
										默认交易系数（分母）
									</th>
									<th>
										指数地址
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${l.type_id}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.type_name}"></c:out>
										</TD>
										<td>
											<c:out value="${l.type_code}"></c:out>
											
										</td>
										<TD>
											<c:choose>
												<c:when test="${!l.is_using}">
													<font style="color: red;">不使用</font>
												</c:when>
												<c:when test="${l.is_using}">
													<font style="color: green;">使用中</font>
												</c:when>
											</c:choose>
										</TD>
										<TD>
											${l.min_kc_nums}
										</TD>
										<td>
											${l.min_float_price}
										</td>
										<TD>
											${l.default_multiple}
										</TD>
										<td>
											${l.default_multiple_state}
										</td>
										<TD>
											<c:out value="${l.default_coefficient}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.index_address}"></c:out>
										</TD>
										<TD>
											<a href="<%=actionPath%>simulation/managerTrans/editManagerTransType.do?typeId=${l.type_id}">编辑</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="simulation/managerTrans/loadManagerTransType.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="bqCode" value="${bqCode}" />
												<pg:param name="state" value="${state}" />
												
												<pg:first>
													<a href="${pageUrl}" mce_href="${pageUrl}">首页</a>
												</pg:first>
	
												<pg:prev>
													<a href="${pageUrl }" mce_href="${pageUrl }">前页</a>
												</pg:prev>
												<pg:pages>
													<c:choose>
														<c:when test="${currentPageNumber eq pageNumber }">
															<font color="red">${pageNumber }</font>
														</c:when>
														<c:otherwise>
															<a href="${pageUrl }" mce_href="${pageUrl }">${pageNumber}</a>
														</c:otherwise>
													</c:choose>
												</pg:pages>
												<pg:next>
													<a href="${pageUrl }" mce_href="${pageUrl }">后页</a>
												</pg:next>
	
												<pg:last>
													<a href="${pageUrl }" mce_href="${pageUrl }">尾页</a>
												</pg:last>
											</pg:pager>
	
										</div>
										<div class="clear"></div>
									</td>
								</tr>
							</tfoot>
						</table>
						<script language="javascript" type="text/javascript">
						<!--
						//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
						senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						--></script>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
