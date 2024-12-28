<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>用户合约资产管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<style>
			table{
				width:100%;
				border:1px solid #CCC;
				border-spacing:0;
				border-collapse:collapse;
			}
			th,td{border:1px solid #CCC;text-align: center;}
		</style>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						用户合约资产管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<form action="<%=actionPath %>managerUserSearch/sarchAccount.do" method="post">
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId }"></input>
									邮箱
									<input type="text" name="email" class="text-input " value="${email }"></input>
									<button type="submit" class="button" >查 询</button>
								</form>
							</div>
						</div>
						<table>
							<thead>
								<tr>
									<th width="8%">
										用户ID
									</th>
									<c:forEach items="${lstTrans }" var="tran">
										<th colspan="3">
											${tran.transaction_code }
										</th>
									</c:forEach>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>
										用户ID
									</td>
									
									<c:forEach items="${lstTrans }" var="tran">
										<td>
											初始金额
										</td>
										<td>
											可用金额
										</td>
										<td>
											冻结金额
										</td>
									</c:forEach>
								</tr>
								<c:forEach items="${lstUids.datas }" varStatus="status" var="uid">
									<tr>
										<td>
											${uid }
										</td>
										<c:forEach items="${map }" varStatus="status" var="m">
											<c:if test="${fn:containsIgnoreCase(fn:trim(m.key),fn:trim(uid))}" var="flag">
												<c:forEach items="${m.value}" var="acc">
													<td>
														<fmt:formatNumber value="${acc.f_initbtc }" pattern="#0.0000"></fmt:formatNumber>
													</td>
													<td>
														<fmt:formatNumber value="${acc.f_btc }" pattern="#0.0000"></fmt:formatNumber>
													</td>
													<td>
														<fmt:formatNumber value="${acc.f_lockbtc }" pattern="#0.0000"></fmt:formatNumber>
													</td>
												</c:forEach>
											</c:if>
										</c:forEach>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="13">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerUserSearch/${URL }.do" items="${lstUids.total}" maxIndexPages="10" 
											maxPageItems="${lstUids.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="userId" value="${userId}"/>
												<pg:param name="email" value="${email}"/>
												
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
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
