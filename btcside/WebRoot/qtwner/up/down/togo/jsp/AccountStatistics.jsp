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
		<title>账户统计</title>
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
						账户统计
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerFinancial/statisticsData.do?" method="post" id="selectForm" name="selectForm">
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId == 0 ? '' : userId}" id="uid"></input>
									<input type="hidden" value="${userId}" id="userId"></input>
									邮箱：
									<input type="text" name="userName" class="text-input " value="${userName}" id="uemail"></input>
									币种：
									<select id="coinId" name="coinId" class="small-input" onchange="javascript:document.selectForm.submit();">
										<c:forEach var="item" varStatus="stat" items="${allCoin }">
											<c:if test="${item.type_id == coinId}">
												<option value="${item.type_id}" selected="selected">${item.type_name}</option>
											</c:if>
											<c:if test="${item.type_id != coinId}">
												<option value="${item.type_id}">${item.type_name}</option>
											</c:if>
										</c:forEach>
									</select>
									排序方式：
									<select id="orderBy" name="orderBy" class="small-input" onchange="javascript:document.selectForm.submit();">
										<c:if test ="${orderBy == 'ALL'}">
											<option value="ALL" selected="selected">全部</option>
											<option value="AVAILABLEDES" >可用(由高到低)</option>
											<option value="AVAILABLEASC" >可用(由低到高)</option>
											<option value="FROZENDES" >冻结(由高到低)</option>
											<option value="FROZENASC" >冻结(由低到高)</option>
											<option value="TOTALDES" >账户总和(由高到低)</option>
											<option value="TOTALASC" >账户总和(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'AVAILABLEDES'}">
											<option value="ALL" >全部</option>
											<option value="AVAILABLEDES" selected="selected">可用(由高到低)</option>
											<option value="AVAILABLEASC" >可用(由低到高)</option>
											<option value="FROZENDES" >冻结(由高到低)</option>
											<option value="FROZENASC" >冻结(由低到高)</option>
											<option value="TOTALDES" >账户总和(由高到低)</option>
											<option value="TOTALASC" >账户总和(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'AVAILABLEASC'}">
											<option value="ALL" >全部</option>
											<option value="AVAILABLEDES" >可用(由高到低)</option>
											<option value="AVAILABLEASC" selected="selected">可用(由低到高)</option>
											<option value="FROZENDES" >冻结(由高到低)</option>
											<option value="FROZENASC" >冻结(由低到高)</option>
											<option value="TOTALDES" >账户总和(由高到低)</option>
											<option value="TOTALASC" >账户总和(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'FROZENDES'}">
											<option value="ALL" >全部</option>
											<option value="AVAILABLEDES" >可用(由高到低)</option>
											<option value="AVAILABLEASC" >可用(由低到高)</option>
											<option value="FROZENDES" selected="selected">冻结(由高到低)</option>
											<option value="FROZENASC" >冻结(由低到高)</option>
											<option value="TOTALDES" >账户总和(由高到低)</option>
											<option value="TOTALASC" >账户总和(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'FROZENASC'}">
											<option value="ALL" >全部</option>
											<option value="AVAILABLEDES" >可用(由高到低)</option>
											<option value="AVAILABLEASC" >可用(由低到高)</option>
											<option value="FROZENDES" >冻结(由高到低)</option>
											<option value="FROZENASC" selected="selected">冻结(由低到高)</option>
											<option value="TOTALDES" >账户总和(由高到低)</option>
											<option value="TOTALASC" >账户总和(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'TOTALDES'}">
											<option value="ALL" >全部</option>
											<option value="AVAILABLEDES" >可用(由高到低)</option>
											<option value="AVAILABLEASC" >可用(由低到高)</option>
											<option value="FROZENDES" >冻结(由高到低)</option>
											<option value="FROZENASC" >冻结(由低到高)</option>
											<option value="TOTALDES" selected="selected">账户总和(由高到低)</option>
											<option value="TOTALASC" >账户总和(由低到高)</option>
										</c:if>
										<c:if test ="${orderBy eq 'TOTALASC'}">
											<option value="ALL" >全部</option>
											<option value="AVAILABLEDES" >可用(由高到低)</option>
											<option value="AVAILABLEASC" >可用(由低到高)</option>
											<option value="FROZENDES" >冻结(由高到低)</option>
											<option value="FROZENASC" >冻结(由低到高)</option>
											<option value="TOTALDES" >账户总和(由高到低)</option>
											<option value="TOTALASC" selected="selected">账户总和(由低到高)</option>
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
											总可用
										</th>
										<th>
											总冻结
										</th>
										<th>
											待释放总和
										</th>
										<th>
											账户总和
										</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<TD>
											<span><em><fmt:formatNumber value="${bqAccount.btc}" pattern="#0.00000000"></fmt:formatNumber>
											</em></span>
										</TD>
										<TD>
											<span><em><fmt:formatNumber value="${bqAccount.btc_locked}" pattern="#0.00000000"></fmt:formatNumber>
											</em></span>
										</TD>
										<TD>
											<span><em><fmt:formatNumber value="${toUnlock}" pattern="#0.00000000"></fmt:formatNumber>
											</em></span>
										</TD>
										<td>
											<span><em><fmt:formatNumber value="${bqAccount.btc + bqAccount.btc_locked}" pattern="#0.00000000"></fmt:formatNumber>
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
										用户邮箱
									</th>
									<th>
										可用
									</th>
									<th>
										冻结
									</th>
									<th>
										待释放
									</th>
									<th>
										账户总和
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
												<c:out value="${one.userEmail }"></c:out>
											</td>
											<td>
												<fmt:formatNumber value="${one.available }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.frozen }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.bfxToUnlock }" pattern="#0.0000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${one.available + one.frozen }" pattern="#0.0000"></fmt:formatNumber>
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
												<pg:pager url="managerFinancial/statisticsData.do"
													items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
													maxPageItems="${pm.maxitems}"
													export="currentPageNumber=pageNumber">
													<pg:param name="orderBy" value="${orderBy }" />
													<pg:param name="userId" value="${userId }"/>
													<pg:param name="userName" value="${userName }"/>
													<pg:param name="coinId" value="${coinId }"/>
													
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
