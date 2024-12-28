<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>用户风险度排行</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						用户风险度管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<c:forEach items="${lstBQTrans }" var="l">
									<a href="<%=actionPath %>delivery/managerOneProfitloss/loadDegreeRisk.do?tcode=${l.transaction_code}" >
									<c:choose>
										<c:when test="${bqCode==l.transaction_code}">
											<font color="#ff0000">${l.transaction_code}</font>
										</c:when>
										<c:otherwise>
											${l.transaction_code}
										</c:otherwise>
									</c:choose>
									</a>
								</c:forEach>
								<span>注释：风险度计算，买（（最新成交价-合约均价）/杠杆系数）*（持仓量+冻结量）/（保证金+追加保证金）
								卖（（合约均价-最新成交价）/杠杆系数）*（持仓量+冻结量）/（保证金+追加保证金）</span>
							</div>
						</div>

						<div>
							<div class="notification information png_bg" style="overflow: hidden; padding-bottom: 20px;">
								<div style="text-align: center;">
									<c:out value="${bqCode }"></c:out>
								</div>
								<div>
									<table style="width: 49%; float: left;">
										<thead>
											<tr>
												<th>
													ID
												</th>
												<th>
													风险度
												</th>
												<th>
													持仓均价
												</th>
												<th>
													爆仓价
												</th>
												<th>
													持仓量
												</th>
												<th>
													杠杆
												</th>
												<th>
													追加
												</th>
												<th>
													方向
												</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${lstBuyMembTrans }" var="one" varStatus="status">
												<tr>
													<td>
														<c:out value="${one[1] }"></c:out>
													</td>
													<td>
														<fmt:formatNumber value="${one[0] }" pattern="#0.000" />
													</td>
													<td>
														<fmt:formatNumber value="${one[3] }" pattern="#0.000000" />
													</td>
													<td>
														<fmt:formatNumber value="${one[4] }" pattern="#0.000000" />
													</td>
													<td>
														<fmt:formatNumber value="${one[5] }" pattern="#0.000" />
													</td>
													<td>
														<c:out value="${one[6] }"></c:out>
													</td>
													<td>
														<c:out value="${one[7] eq '1' && one[8]>one[9]*0.5 ? '能' : '否' }"></c:out>
													</td>
													<td>
														<c:out value="${one[2] eq '1' ? '买' : '卖' }"></c:out>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<table style="width: 49%; float: right;">
										<thead>
											<tr>
												<th>
													ID
												</th>
												<th>
													风险度
												</th>
												<th>
													持仓均价
												</th>
												<th>
													爆仓价
												</th>
												<th>
													持仓量
												</th>
												<th>
													杠杆
												</th>
												<th>
													追加
												</th>
												<th>
													方向
												</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${lstSellMembTrans }" var="one" varStatus="status">
												<tr>
													<td>
														<c:out value="${one[1] }"></c:out>
													</td>
													<td>
														<fmt:formatNumber value="${one[0] }" pattern="#0.000" />
													</td>
													<td>
														<fmt:formatNumber value="${one[3] }" pattern="#0.000000" />
													</td>
													<td>
														<fmt:formatNumber value="${one[4] }" pattern="#0.000000" />
													</td>
													<td>
														<fmt:formatNumber value="${one[5] }" pattern="#0.000" />
													</td>
													<td>
														<c:out value="${one[6] }"></c:out>
													</td>
													<td>
														<c:out value="${one[7] eq '1' && one[8]>one[9]*0.5 ? '能' : '否' }"></c:out>
													</td>
													<td>
														<c:out value="${one[2] eq '1' ? '买' : '卖' }"></c:out>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
