<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>用户谷歌验证监控</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						用户谷歌验证监控管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<form action="<%=actionPath%>managerUser/loadGoogle.do" method="post">
									ID
									<input name="userId" type="text" value="${userId }" />
									邮箱
									<input name="email" type="text" value="${email }" />
									<button type="submit" class="button">
										查 询
									</button>
								</form>
							</div>
							<div>
								<form action="<%=actionPath%>managerUser/removeGoogle.do" method="post">
									ID
									<input name="userId" type="text" value="${userId }" />
									邮箱
									<input name="email" type="text" value="${email }" />
									资金密码
									<input name="surepwd" type="password" value="${surepwd }" />
									<button type="submit" class="button">
										解 除
									</button>
									<span style="color: red;" >${errormsg }</span>
								</form>
							</div>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										ID
									</th>
									<th>
										手机号码
									</th>
									<th>
										谷歌私钥
									</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${pm.datas}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${l.f_user_id}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_user_tel }"></c:out>
										</TD>
										<td>
											<c:out value="${l.f_user_safegoogle eq '' ? '未绑定' : '已绑定' }"></c:out>
										</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="4">
										<div class="pagination">
											<pg:pager url="managerUser/${pageUrl }.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												
												<pg:param name="userId" value="${userId }" />
												<pg:param name="email" value="${email }" />

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
