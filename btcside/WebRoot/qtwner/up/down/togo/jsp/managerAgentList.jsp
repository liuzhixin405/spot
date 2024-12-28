<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>代理人管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		
		<script type="text/javascript">
			function sure(qr, url) {
				if (qr != null) {
					if (confirm(qr)) {
						document.location.href = url;
					}
				} else {
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
						代理人管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerAgent/add.do" method="post">
									邮箱：
									<input type="text" name="useremail" class="text-input " value=""></input>
									登录密码：
									<input type="password" name="pwd1" class="text-input " value=""></input>
									确认密码：
									<input type="password" name="pwd2" class="text-input " value=""></input>
									<button type="submit" class="button">
										添加代理人
									</button>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>managerAgent/userToAgent.do" method="post">
									用户ID：
									<input type="text" name="userid" class="text-input " value=""></input>
									邮箱：
									<input type="text" name="useremail" class="text-input " value=""></input>
									<button type="submit" class="button">
										普通用户转为代理人
									</button>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>managerAgent/agentToUser.do" method="post">
									用户ID：
									<input type="text" name="userid" class="text-input " value=""></input>
									邮箱：
									<input type="text" name="useremail" class="text-input " value=""></input>
									<button type="submit" class="button">
										取消代理人身份
									</button>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>managerAgent/load.do">
									用户ID：
									<input type="text" name="userid" class="text-input " value="${uid}"></input>
									邮箱：
									<input type="text" name="useremail" class="text-input " value="${uemail}"></input>
									<button type="submit" class="button">
										查 询
									</button>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>managerAgent/loadInviter.do">
									用户ID：
									<input type="text" name="userid" class="text-input " value="${userId}"></input>
									邮箱：
									<input type="text" name="useremail" class="text-input " value="${userEmail}"></input>
									<button type="submit" class="button">
										查询该用户的代理人
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
										用户ID
									</th>
									<th>
										邮箱
									</th>
									<th>
										积分及等级
									</th>
									<th>
										注册时间
									</th>
									<th>
										状态
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${status.index+1}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_user_id}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_user_email}"></c:out>
										</TD>
										<TD>
											<c:out value="VIP${l.f_user_level_id-1}"></c:out>
											<br />
											<c:out value="${l.f_user_credits}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.f_user_time}"></ut:formatDate>
										</TD>
										<td>
											<c:choose>
												<c:when test="${l.f_user_state==-1}">冻结</c:when>
												<c:when test="${l.f_user_state==0}">无效</c:when>
												<c:when test="${l.f_user_state==1}">有效</c:when>
												<c:otherwise>未知${l.f_user_state}</c:otherwise>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="9">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="${pgN }" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="userid" value="${userid}" />
												<pg:param name="useremail" value="${useremail}" />
												
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
