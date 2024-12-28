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
		<title>用户管理</title>
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
						用户管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerUser/selectUsers.do" method="post">
									用户ID：
									<input type="text" name="userid" class="text-input " value="${userid}"></input>
									电话：
									<input type="text" name="usertel" class="text-input " value="${usertel}"></input>
									邮箱：
									<input type="text" name="useremail" class="text-input " value="${useremail}"></input>
									区域：
									<input type="text" name="countryName" class="text-input " value="${countryName}"></input>

<%--									<input type="text" name="provinceName" class="text-input " value="${provinceName}"></input>--%>
<%--									----%>
<%--									<input type="text" name="cityName" class="text-input " value="${cityName}"></input>--%>
									邀请人ID：
									<input type="text" name="inviteId" class="text-input " value="${inviteId}"></input>
									VIP等级：
									<input type="text" name="userLevelId" class="text-input " value="${userLevelId}"></input>
									<button type="submit" class="button">
										查 询
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
										是否是子账号
									</th>
									<th>
<%--										&provinceName=${provinceName }&cityName=${cityName }--%>
										<a href="javascript:;" onclick="sure(null,'<%=actionPath%>managerUser/loadUsers.do?userid=${userid}&usertel=${usertel}&useremail=${useremail }&countryName=${countryName }&inviteId=${inviteId }&userLevelId=${userLevelId }&state=1');">
											比特币
										</a>
									</th>
									<th>
										<a href="javascript:;" onclick="sure(null,'<%=actionPath%>managerUser/loadUsers.do?userid=${userid}&usertel=${usertel}&useremail=${useremail }&countryName=${countryName }&inviteId=${inviteId }&userLevelId=${userLevelId }&state=2');">
											合约合计
										</a>
									</th>
									<th>
										<a href="javascript:;" onclick="sure(null,'<%=actionPath%>managerUser/loadUsers.do?userid=${userid}&usertel=${usertel}&useremail=${useremail }&countryName=${countryName }&inviteId=${inviteId }&userLevelId=${userLevelId }&state=3');">
											积分及等级
										</a>
									</th>
									<th>
										时间
									</th>
									<th>
										状态
									</th>
									<th>
										邮箱
									</th>
									<th>
										手机
									</th>
									<th>
										区域
									</th>
									<th>
										邀请人ID
									</th>
									<th>
										操作
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
											<c:if test="${l.is_sub_user == 1}">是</c:if>
											<c:if test="${l.is_sub_user != 1}">否</c:if>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.btc}" pattern="#0.000000"></fmt:formatNumber>
											<br />
											<font color="red">
												<fmt:formatNumber value="${l.btc_locked}" pattern="#0.000000"></fmt:formatNumber>
											</font>
										</TD>
										<td>
											<fmt:formatNumber value="${l.btc_gain}" pattern="#0.000000"></fmt:formatNumber>
										</td>
										<TD>
<%--											<c:out value="VIP${l.f_user_level_id-1}"></c:out>--%>
											<c:out value="${l.level_name}"></c:out>
											<br />
											<c:out value="${l.credits}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.f_user_time}"></ut:formatDate>
										</TD>
										<td>
											<c:choose>
												<c:when test="${l.f_user_state==-1}">
													<a class="link"
														href="javascript:sure('确定要解除该用户冻结吗?','<%=actionPath%>managerUser/freezeUser.do?userid=${l.f_user_id}&istate=${l.f_user_state}')">
														冻结</a>
												</c:when>
												<c:when test="${l.f_user_state==0}">
												无效
												</c:when>
												<c:when test="${l.f_user_state==1}">
													<a class="link"
														href="javascript:sure('确定要冻结该用户吗?','<%=actionPath%>managerUser/freezeUser.do?userid=${l.f_user_id}&istate=${l.f_user_state}')">
														有效</a>
												</c:when>
												<c:otherwise>
												未知${l.f_user_state}
												</c:otherwise>
											</c:choose>

										</td>
										<TD>
											<c:out value="${l.f_user_email}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_user_tel}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.country_name}"></c:out>
<%--											<c:out value="${l.province_name}"></c:out>--%>
<%--											----%>
<%--											<c:out value="${l.city_name}"></c:out>--%>
										</TD>
										<TD>
											<c:out value="${l.invite_uid}"></c:out>
										</TD>
										<TD>
											<a class="link" href="javascript:sure(null,'<%=actionPath%>managerUser/managerUser.do?userid=${l.f_user_id}')">管理</a>
											<a href="<%=actionPath%>managerCheckAccount/loadDetail.do?id=${l.f_user_id}">对账</a>
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
											<pg:pager url="managerUser/selectUsers.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="userid" value="${userid}" />
												<pg:param name="usertel" value="${usertel}" />
												<pg:param name="useremail" value="${useremail}" />
<%--												<pg:param name="provinceName" value="${provinceName}" />--%>
<%--												<pg:param name="cityName" value="${cityName}" />--%>
												<pg:param name="countryName" value="${countryName}" />
												<pg:param name="inviteId" value="${inviteId}" />
												<pg:param name="userLevelId" value="${userLevelId}" />
												<pg:param name="state" value="${state}"/>
												
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
