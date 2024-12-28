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
		<title>用户意见审核</title>
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
					document.location.href = url + "&sugReply=" + document.getElementById("sugReply").value;
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
						用户意见审核管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerUserSuggest/searchUserSuggest.do" method="post">
									用户ID
									<input name="uid" type="text" value="${uid}" />
									<button type="submit" class="button">查 询</button>
									<a style="margin-left: 80px;" href="<%=actionPath%>managerUserSuggest/searchUserSuggest.do?ustate=2">已拒绝</a>
									<a style="margin-left: 80px;" href="<%=actionPath%>managerUserSuggest/searchUserSuggest.do?ustate=0">待审核</a>
									<a style="margin-left: 80px;" href="<%=actionPath%>managerUserSuggest/searchUserSuggest.do?ustate=1">已通过</a>
								</form>
							</div>
						</div>

						<table>
							<thead>
								<tr>
									<th>
										建议用户ID
									</th>
									<th>
										建议用户邮箱
									</th>
									<th>
										类型
									</th>
									<th>
										内容
									</th>
									<th>
										提交时间
									</th>
									<th>
										状态
									</th>
									<th>
										采纳回复
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
											<form action="<%=actionPath%>managerUserSuggest/updateUserSuggest.do">
												<td>
													<c:out value="${one.f_user_id }"></c:out>
													<input type="hidden" name="id" value="${one.id }" />
												</td>
												<td>
													<c:out value="${one.f_user_email }"></c:out>
												</td>
												<td>
													<c:out value="${one.f_sug_type }"></c:out>
												</td>
												<td>
													<c:out value="${one.f_sug_content }"></c:out>
												</td>
												<td>
													<ut:formatDate dateline="${one.f_sug_time }"></ut:formatDate>
												</td>
												<td>
													<c:choose>
														<c:when test="${one.f_sug_state==0}">
															<font color="red">未受理</font>
														</c:when>
														<c:when test="${one.f_sug_state==1}">采纳</c:when>
														<c:when test="${one.f_sug_state==2}">拒绝</c:when>
													</c:choose>
												</td>
												<td>
													<c:choose>
														<c:when test="${one.f_sug_reply==''}">
															<input type="text" name="sugReply" id="sugReply" value="${one.f_sug_reply}" />
														</c:when>
														<c:otherwise>
															<c:out value="${one.f_sug_reply }"></c:out>
														</c:otherwise>
													</c:choose>
												</td>
												<td>
													<c:if test="${one.f_sug_state==0}">
														<input type="button" onclick="gourl('确定提交?','<%=actionPath%>managerUserSuggest/updateUserSuggest.do?id=${one.id}&uid=${one.f_user_id }&state=1');" value='一键采纳'"></input>
														<input type="hidden" name="state" value="2" />
														<input type="submit" value="拒绝" onclick="return postsure('确定要拒绝该请求吗?该操作无法回退');"></input>
													</c:if>
												</td>
											</form>
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
												<pg:pager url="managerUserSuggest/searchUserSuggest.do"
													items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
													maxPageItems="${pm.maxitems}"
													export="currentPageNumber=pageNumber">
													<pg:param name="ustate" value="${state }" />
													<pg:param name="uid" value="${id }"/>	
													
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
