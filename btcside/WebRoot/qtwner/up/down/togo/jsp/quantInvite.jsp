<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>邀请关系查询</title>
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
						邀请关系查询
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerQuantRisk/quantInvite.do" method="post" id="selectForm" name="selectForm">
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId}" id="uid"></input>
									邮箱：
									<input type="text" name="userName" class="text-input " value="${userName}" id="uemail"></input>
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
									<th colspan="5" align="center">
										基本信息
									</th>
								</tr>
								<tr>
									<th>
										用户ID
									</th>
									<th>
										用户邮箱
									</th>
									<th>
										VIP等级
									</th>
									<th>
										邀请会员
									</th>
									<th>
										联盟总会员
									</th>
									<th>
										邀请人
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<TD>
										${user.f_user_id > 0 ? user.f_user_id : "" }
									</TD>
									<TD>
										${user.f_user_email}
									</TD>
									<TD>
										${level ne "" and level ne null ? level : ""}
									</TD>
									<TD>
										${inviteNums}
									</TD>
									<TD>
										${allInviteNums}
									</TD>
									<TD>
										<c:if test="${inviter ne '' and inviter ne null }">
											<a href="<%=actionPath %>managerQuantRisk/quantInvite.do?userId=${inviter}">${inviter}</a>
										</c:if>
									</TD>
								</tr>
							</tbody>
						</table>
						<br/>
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
										用户邮箱
									</th>
									<th>
										VIP等级
									</th>
									<th>
										邀请会员
									</th>
									<th>
										联盟总会员
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas}" var="t" varStatus="status">
									<tr>
										<td>
											<c:out value="${status.index+1}"></c:out>
										</td>
										<TD>
											<a href="<%=actionPath %>managerQuantRisk/quantInvite.do?userId=${t.userId}">${t.userId}</a>
										</TD>
										<TD>
											${t.userName}
										</TD>
										<TD>
											${t.level}
										</TD>
										<TD>
											${t.inviteNums}
										</TD>
										<TD>
											${t.allInviteNums}
										</TD>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerQuantRisk/quantInvite.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="userId" value="${userId}"/>
											<pg:param name="userName" value="${userName}"/>
									
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
