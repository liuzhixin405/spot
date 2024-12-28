<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>各合约持仓排名  </title>
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
						 各合约持仓排名
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<c:forEach items="${lstBQTrans }" var="l">
									<a href="<%=actionPath %>managerTransHold/loadRankings.do?tcode=${l.transaction_code}&buyOrderBy=${buyOrderBy }&sellOrderBy=${sellOrderBy }" >
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
								<span>注释：持仓量排名（持仓量+冻结量），上证合约的持仓量为（盈亏+合约可用余额）</span>
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
													持仓量
												</th>
												<th>
													杠杆
												</th>
												<th>
													持仓均价
												</th>
												<th>
													<a href="<%=actionPath%>managerTransHold/loadRankings.do?tcode=${bqCode}&buyOrderBy=${buyOrderBy == 'ASC' ? 'DESC' : 'ASC' }&sellOrderBy=${sellOrderBy }">
														<span class="index-change2">
															强平触发价
														</span>
														<span class="index-change2 up-down2">
															<em data-indexV class="arcshape-up-arrow1 <c:if test="${buyOrderBy == 'ASC'}">arcshape-arrow-activate</c:if> <c:if test="${buyOrderBy != 'ASC'}">arcshape-arrow-normal</c:if>"></em>
															<em data-indexVI class="arcshape-down-arrow1 <c:if test="${buyOrderBy == 'DESC'}">arcshape-arrow-activate</c:if> <c:if test="${buyOrderBy != 'DESC'}">arcshape-arrow-normal</c:if>"></em>
														</span>
													</a>
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
														<c:out value="${one[2] }"></c:out>
													</td>
													<td>
														<fmt:formatNumber value="${one[3] }" pattern="#0.000000" />
													</td>
													<td>
														<fmt:formatNumber value="${one[5] }" pattern="#0.000000" />
													</td>
													<td>
														<c:out value="${one[4] eq '1' && one[6]>one[7]*0.5 ? '能' : '否' }"></c:out>
													</td>
													<td>
														买
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
													持仓量
												</th>
												<th>
													杠杆
												</th>
												<th>
													持仓均价
												</th>
												<th>
													<a href="<%=actionPath%>managerTransHold/loadRankings.do?tcode=${bqCode}&sellOrderBy=${sellOrderBy == 'ASC' ? 'DESC' : 'ASC' }&buyOrderBy=${buyOrderBy }">
														<span class="index-change2">
															强平触发价
														</span>
														<span class="index-change2 up-down2">
															<em data-indexV class="arcshape-up-arrow1 <c:if test="${sellOrderBy == 'ASC'}">arcshape-arrow-activate</c:if> <c:if test="${sellOrderBy != 'ASC'}">arcshape-arrow-normal</c:if>"></em>
															<em data-indexVI class="arcshape-down-arrow1 <c:if test="${sellOrderBy == 'DESC'}">arcshape-arrow-activate</c:if> <c:if test="${sellOrderBy != 'DESC'}">arcshape-arrow-normal</c:if>"></em>
														</span>
													</a>
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
														<c:out value="${one[2] }"></c:out>
													</td>
													<td>
														<fmt:formatNumber value="${one[3] }" pattern="#0.000000" />
													</td>
													<td>
														<fmt:formatNumber value="${one[5] }" pattern="#0.000000" />
													</td>
													<td>
														<c:out value="${one[4] eq '1' && one[6]>one[7]*0.5 ? '能' : '否' }"></c:out>
													</td>
													<td>
														卖
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
