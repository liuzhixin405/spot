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
		<title>自由经纪人邀请详情</title>
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
						自由经纪人邀请详情管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerBroker/loadDetails.do" method="post">
									<button type="submit" class="button">
										返回
									</button>
								</form>
							</div>
						</div>
						<table>
							<thead>
								<tr>
									<th>
										被邀请人用户id
									</th>
									<th>
										邀请人用户id
									</th>
									<th>
										邀请时间
									</th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${pm!= null }">
									<c:forEach items="${pm.datas }" var="one" varStatus="status">
										<tr>
											<td>
												<c:out value="${one.f_user_id }"></c:out>
											</td>
											<td>
												<c:out value="${one.invite_uid }"></c:out>
											</td>
											<td>
												<ut:formatDate dateline="${one.invite_time }"></ut:formatDate>
											</td>
										</tr>
									</c:forEach>
								</c:if>
							</tbody>
							<c:if test="${pm!=null }">
								<tfoot>
									<tr>
										<td colspan="3">
											<div class="pagination">
												<!-- ********** 分页开始 **********	-->
												<pg:pager url="managerBroker/loadUserDetails.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
													maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
													
													<pg:param name="userId" value="${userId }"/>

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
