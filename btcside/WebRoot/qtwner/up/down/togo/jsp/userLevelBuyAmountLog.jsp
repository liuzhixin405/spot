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
		<title>用户购买VIP等级记录</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript">
			function postsure(qr) {
				if (confirm(qr)) {
					return true;
				}
	
				return false;
			}
	
			function gourl(qr,url) {
				if (confirm(qr)) {
					document.location.href = url;
				}
			}
		</script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						用户购买VIP等级记录
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerUserLevelRule/loadLevelBuyAmountLog.do" method="post">
									用户ID
									<input name="uid" type="text" value="${uid}" />
									用户邮箱
									<input name="email" type="text" value="${email}" />
									<button type="submit" class="button">查 询</button>
								</form>
							</div>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										日志ID
									</th>
									<th>
										用户ID
									</th>
									<th>
										用户邮箱
									</th>
									<th>
										花费币种ID
									</th>
									<th>
										花费金额
									</th>
									<th>
										原等级
									</th>
									<th>
										新等级
									</th>
									<th>
										购买时间
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas }" var="one" varStatus="status">
									<tr>
										<td>
<%-- 											${one.log_id }"> --%>
											<c:out value="${one[0] }"></c:out>
										</td>
										<td>
<%-- 											${one.f_user_id } --%>
											<c:out value="${one[5] }"></c:out>
										</td>
										<td>
<%-- 											${one.f_user_email } --%>
											<c:out value="${one[6] }"></c:out>
											
<%-- 											${one.f_user_id } --%>
											<input type="hidden" name="f_user_id" value="${one[5] }" />
										</td>
										<td>
<%-- 											${one.type_name } --%>
											<c:out value="${one[2] }"></c:out>
											
<%-- 											${one.currency_type_id } --%>
											<input type="hidden" name="currency_type_id" value="${one[1] }" />
										</td>
										<td>
<%-- 											${one.change_amount } --%>
											<c:out value="${one[3] }"></c:out>
										</td>
										<td>
<%-- 											${one.before_level_name } --%>
											<c:out value="${one[9] }"></c:out>
										</td>
										<td>
<%-- 											${one.after_level_name } --%>
											<c:out value="${one[10] }"></c:out>
										</td>
										<td>
<%-- 											${one.dateline} --%>
											<ut:formatDate dateline="${one[4]}"></ut:formatDate>
										</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="12">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerUserLevelRule/loadLevelBuyAmountLog.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="uid" value="${uid}"/>
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
											共 ${pm.total} 条
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
