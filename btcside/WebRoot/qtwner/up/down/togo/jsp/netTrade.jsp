<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>净交易</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript">
			function loadCode(){
				var code = document.getElementById('codeSelect').value;
				document.getElementById('bqCode').value = code;
			}
			function loadType(){
				var type = document.getElementById('typeSelect').value;
				document.getElementById('type').value = type;
			}
		</script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						净交易管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification information png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerFinancial/loadNetTrade.do" method="post">
									合约号
									<select id="codeSelect" onchange="loadCode();">
										<c:forEach items="${lstTrans}" var="cou">
											<c:choose>
												<c:when test="${bqCode eq cou.transaction_code}">
													<option value="${cou.transaction_code}" selected='selected'>${cou.transaction_code}</option>	
												</c:when>
												<c:otherwise>
													<option value="${cou.transaction_code}">${cou.transaction_code}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select>
									方向
									<select id="typeSelect" onchange="loadType();">
										<c:choose>
											<c:when test="${type eq 1}">
												<option value="1" selected='selected'>买</option>	
											</c:when>
											<c:otherwise>
												<option value="1">买</option>	
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${type eq 2}">
												<option value="2" selected='selected'>卖</option>	
											</c:when>
											<c:otherwise>
												<option value="2">卖</option>	
											</c:otherwise>
										</c:choose>
									</select>
									<input type="hidden" id="bqCode" value="${bqCode }" name="bqCode">
									<input type="hidden" id="type" value="${type }" name="type">
									<button type="submit" class="button">查询</button>
								</form>
							</div>
						</div>
						<table>
							<thead>
								<tr>
									<th>
										合约号
									</th>
									<th>
										用户ID
									</th>
									<th>
										成交量
									</th>
									<th>
										类型
									</th>
									<th>
										成交均价
									</th>
									<th>
										交易时间
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas }" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${l.bqCode }"></c:out>
										</TD>
										<TD>
											<c:out value="${l.userId }"></c:out>
										</TD>
										<TD>
											<c:out value="${l.countAmount }"></c:out>
										</TD>
										<TD>
											<c:out value="${l.type eq 1 ? '买' : '卖'}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.averagePrice }"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.tradeTime}"></ut:formatDate>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="5">
										<div class="pagination">
											<pg:pager url="managerFinancial/loadNetTrade.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												
												<pg:param name="bqCode" value="${bqCode }" />
												<pg:param name="type" value="${type }" />

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
