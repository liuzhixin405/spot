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
		<title>高级经纪人管理</title>
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
						高级经纪人管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerBroker/loadUserByEmail.do" method="post">
									ID
									<input name="uid" type="text" value="${user.f_user_id}" />
									用户邮箱
									<input name="uemail" type="text" value="${user.f_user_email}" />
									<button type="submit" class="button">查 询</button>
								</form>
							</div>
						</div>
						<div class="notification information png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerBroker/appendBroker.do" method="post">
									用户ID<input name="suserID" value="${user.f_user_id}"/>
									用户电话<input name="userTel" value="${user.f_user_tel}"/>
									用户地址<input name="userAddr" value=""/>
									用户身份证号<input name="userNo" value="888888888888888888"/>
									
									<button type="submit" class="button" onclick="return confirm('确认将该用户添加为高级经纪人吗?');">成为高级经纪人</button>
								</form>
							</div>
						</div>
							<table>
								<thead>
									<tr>
										<th>
											用户ID
										</th>
										<th>
											身份证号码
										</th>
										<th>
											手机号码
										</th>
										<th>
											通讯地址
										</th>
										<th>
											申请时间
										</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${pm.datas }" var="one" varStatus="status">
										<tr>
											<td>
												<c:out value="${one.f_user_id }"></c:out>
												<input type="hidden" name="id" value="${one.f_user_id }" />
											</td>
											<td>
												<c:out value="${one.idcard }"></c:out>
											</td>
											<td>
												<c:out value="${one.mobile }"></c:out>
											</td>
											<td>
												<c:out value="${one.address }"></c:out>
											</td>
											<td>
												<ut:formatDate dateline="${one.dateline }"></ut:formatDate>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							<tfoot>
								<tr>
									<td colspan="5">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerBroker/loadAdvancedBroker.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												
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
