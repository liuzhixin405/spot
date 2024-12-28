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
		<title>用户资产排序</title>
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
						用户资产排序
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerQuantRisk/quantFundRanking.do?" method="post" id="selectForm" name="selectForm">
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId == 0 ? '' : userId}" id="uid"></input>
									邮箱：
									<input type="text" name="userName" class="text-input " value="${userName}" id="uemail"></input>
									排序方式：
									<select id="orderBy" name="orderBy" class="small-input" onchange="javascript:document.selectForm.submit();">
										<c:if test ="${orderBy == 'ALL'}">
											<option value="ALL" selected="selected">全部</option>
											<option value="ACCOUNTDES" >钱包余额(由高到低)</option>
											<option value="ACCOUNTASC" >钱包余额(由低到高)</option>
											<option value="CONTRACTDES" >合约总计(由高到低)</option>
											<option value="CONTRACTASC" >合约总计(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'ACCOUNTDES'}">
											<option value="ALL" >全部</option>
											<option value="ACCOUNTDES" selected="selected">钱包余额(由高到低)</option>
											<option value="ACCOUNTASC" >钱包余额(由低到高)</option>
											<option value="CONTRACTDES" >合约总计(由高到低)</option>
											<option value="CONTRACTASC" >合约总计(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'ACCOUNTASC'}">
											<option value="ALL" >全部</option>
											<option value="ACCOUNTDES" >钱包余额(由高到低)</option>
											<option value="ACCOUNTASC" selected="selected">钱包余额(由低到高)</option>
											<option value="CONTRACTDES" >合约总计(由高到低)</option>
											<option value="CONTRACTASC" >合约总计(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'CONTRACTDES'}">
											<option value="ALL" >全部</option>
											<option value="ACCOUNTDES" >钱包余额(由高到低)</option>
											<option value="ACCOUNTASC" >钱包余额(由低到高)</option>
											<option value="CONTRACTDES" selected="selected">合约总计(由高到低)</option>
											<option value="CONTRACTASC" >合约总计(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'CONTRACTASC'}">
											<option value="ALL" >全部</option>
											<option value="ACCOUNTDES" >钱包余额(由高到低)</option>
											<option value="ACCOUNTASC" >钱包余额(由低到高)</option>
											<option value="CONTRACTDES" >合约总计(由高到低)</option>
											<option value="CONTRACTASC" selected="selected">合约总计(由低到高)</option>
										</c:if>
									</select>
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
										序号
									</th>
									<th>
										用户Id
									</th>
									<th>
										用户邮箱
									</th>
									<th>
										钱包余额
									</th>
									<th>
										合约总计（永续）
									</th>
									<th>
										合约总计（交割）
									</th>
									<th>
										总计
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${pm!= null }">
									<c:forEach items="${pm.datas }" var="one" varStatus="status">
										<tr>
											<td>
												<c:out value="${status.index+1}"></c:out>
											</td>
											<td>
												<c:out value="${one.userId }"></c:out>
											</td>
											<td>
												<c:out value="${one.userName }"></c:out>
											</td>
											<td>
												<fmt:formatNumber value="${one.accountAll }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.contractAll }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.deliveryContractAll }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.all }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<a href="<%=actionPath%>managerCheckAccount/loadDetail.do?id=${one.userId}">对账</a>
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
												<pg:pager url="managerQuantRisk/quantFundRanking.do"
													items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
													maxPageItems="${pm.maxitems}"
													export="currentPageNumber=pageNumber">
													<pg:param name="userId" value="${userId }"/>
													<pg:param name="userName" value="${userName }"/>
													<pg:param name="orderBy" value="${orderBy }"/>
													
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
