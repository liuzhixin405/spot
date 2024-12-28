<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>合约账户统计</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript">
			
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						合约账户统计
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerFinancial/contractAccountStatistics.do?" method="post" id="selectForm" name="selectForm">
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId == 0 ? '' : userId}" id="uid"></input>
									邮箱：
									<input type="text" name="userName" class="text-input " value="${userName}" id="uemail"></input>
									<button type="submit" class="button">
										查询
									</button>
								</form>
							</div>
							<br>
						</div>
						<table id="changecolor">
								<thead>
									
									<tr>
										<th>
											账户总可用
										</th>
										<th>
											合约总和
										</th>
										<th>
											总和
										</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<TD>
											<span><em><fmt:formatNumber value="${allBalance.accountAll}" pattern="#0.00000000"></fmt:formatNumber>
											</em></span>
										</TD>
										<TD>
											<span><em><fmt:formatNumber value="${allBalance.contractAll}" pattern="#0.00000000"></fmt:formatNumber>
											</em></span>
										</TD>
										<td>
											<span><em><fmt:formatNumber value="${allBalance.all}" pattern="#0.00000000"></fmt:formatNumber>
											</em></span>
										</td>
									</tr>
								</tbody>
							</table>
							<br>
							<table>
							<thead>
								<tr>
									<th>
										用户Id
									</th>
									<th>
										账户可用
									</th>
									<th>
										账户冻结
									</th>
									<th>
										账户总计
									</th>
									<th>
										合约可用（永续）
									</th>
									<th>
										合约冻结（永续）
									</th>
									<th>
										持仓保证金+亏损备用（永续）
									</th>
									<th>
										浮动盈亏（永续）
									</th>
									<th>
										合约总计（永续）
									</th>
									<th>
										合约可用（交割）
									</th>
									<th>
										合约冻结（交割）
									</th>
									<th>
										持仓保证金+亏损备用（交割）
									</th>
									<th>
										浮动盈亏（交割）
									</th>
									<th>
										合约总计（交割）
									</th>
									<th>
										总计
									</th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${pm!= null }">
									<c:forEach items="${pm.datas }" var="one" varStatus="status">
										<tr>
											<td>
												<c:out value="${one.userId }"></c:out>
											</td>
											<td>
												<fmt:formatNumber value="${one.accountBalance }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.accountFrozen }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.accountAll }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.contractBalance }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.contractFrozen }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.contractDeposit }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.contractFloatGainLoss }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.contractAll }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.deliveryContractBalance }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.deliveryContractFrozen }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.deliveryContractDeposit }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.deliveryContractFloatGainLoss }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.deliveryContractAll }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.all }" pattern="#0.0000"></fmt:formatNumber>
											</td>
										</tr>
									</c:forEach>
								</c:if>
							</tbody>
							<c:if test="${pm!=null }">
								<tfoot>
									<tr>
										<td colspan="9">
											<div class="pagination">
												<!-- ********** 分页开始 **********	-->
												<pg:pager url="managerFinancial/contractAccountStatistics.do"
													items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
													maxPageItems="${pm.maxitems}"
													export="currentPageNumber=pageNumber">
													<pg:param name="userId" value="${userId }"/>
													<pg:param name="userName" value="${userName }"/>
													
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
												共 ${pm.total} 条
											</div>
											<div class="clear"></div>
										</td>
									</tr>
								</tfoot>
							</c:if>
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
