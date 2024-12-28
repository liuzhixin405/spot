<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>用户盈亏额排名</title>
		<!-- Reset Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<!-- Main Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<!-- Invalid Stylesheet. This makes stuff look pretty. Remove it if you want the CSS completely valid -->
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						用户盈亏额排名
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerQuantRisk/quantIncomeRanking.do" method="post" id="selectForm" name="selectForm">
									合约号：
									<select name="code" id="code" onchange="javascript:document.selectForm.submit();">
										<c:forEach items="${lstTrans}" var="l">
											<c:if test="${l.transaction_code eq code}">
												<option value="${l.transaction_code}" selected="selected">${l.transaction_code}</option>
											</c:if>
											<c:if test="${l.transaction_code ne code}">
												<option value="${l.transaction_code}">${l.transaction_code}</option>
											</c:if>
										</c:forEach>
									</select>
									排序方式：
									<select id="orderBy" name="orderBy" class="small-input" onchange="javascript:document.selectForm.submit();">
										<c:if test ="${orderBy == 'ASC'}">
											<option value="ASC" selected="selected">盈利(由低到高)</option>
											<option value="DES" >盈利(由高到低)</option>
										</c:if>
										<c:if test ="${orderBy eq 'DES'}">
											<option value="DES" selected="selected">盈利(由高到低)</option>
											<option value="ASC" >盈利(由低到高)</option>
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
										用户id
									</th>
									<th>
										用户邮箱
									</th>
									<th>
										盈亏额
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${pm.datas}" var="tl" varStatus="status">
									<tr>
										<td>
											${status.index + 1 }
										</td>
										<td>
											${tl.userId }
										</td>
										<td>
											${tl.userName }
										</td>
										<td>
											${tl.rebate }
										</td>
										<td>
											<a href="<%=actionPath%>managerTrade/loadTradeLogs.do?id=${tl.userId}&code=${code}">明细</a>
										</td>
										
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerQuantRisk/quantIncomeRanking.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="code" value="${code}"/>
											<pg:param name="orderBy" value="${orderBy}"/>
											
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
									<!-- End .pagination -->
									<div class="clear"></div>
								</td>
							</tr>
						</tfoot>
						
						<script language="javascript" type="text/javascript">
							senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						</script>
					</div>
				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>
