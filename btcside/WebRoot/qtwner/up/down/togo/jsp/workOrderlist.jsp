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
						用户工单管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerWorkOrder/searchWorkOrder.do" method="post">
									<div>
										用户ID：
										<input name="uid" type="text" value="${uid}" />
										<button type="submit" class="button">查 询</button>
									</div>
									<div>
										工单状态：
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?state=UNREAD">未读</a>
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?state=READ">已读</a>
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?state=SEND">已发送</a>
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?state=CLOSE">关闭</a>
									</div>
									<div>
										工单类型：
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?type=ACCOUNT">账户类</a>
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?type=TRADE">交易类</a>
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?type=RW">充提类</a>
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?type=COMPLAIN">投诉类</a>
										<a style="margin-left: 40px;" href="<%=actionPath%>managerWorkOrder/searchWorkOrder.do?type=COOP">合作类</a>
									</div>
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
										主题
									</th>
									<th>
										创建时间
									</th>
									<th>
										状态
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
											<td>
												<c:out value="${one.f_user_id }"></c:out>
												<input type="hidden" name="id" value="${one.id }" />
											</td>
											<td>
												<c:out value="${one.f_user_email }"></c:out>
											</td>
											<td>
												<c:choose>
													<c:when test="${one.f_order_type == 'ACCOUNT'}">账户</c:when>
													<c:when test="${one.f_order_type == 'TRADE'}">交易</c:when>
													<c:when test="${one.f_order_type == 'COMPLAIN'}">投诉</c:when>
													<c:when test="${one.f_order_type == 'RW'}">充提</c:when>
													<c:when test="${one.f_order_type == 'COOP'}">合作</c:when>
												</c:choose>
											</td>
											<td>
												<c:out value="${one.f_theme }"></c:out>
											</td>
											<td>
												<ut:formatDate dateline="${one.f_create_time }"></ut:formatDate>
											</td>
											<td>
												<c:choose>
													<c:when test="${one.f_to_order_state == 'UNREAD'}">
														<font style="color: red">未读</font>
													</c:when>
													<c:when test="${one.f_to_order_state == 'READ'}">已读</c:when>
													<c:when test="${one.f_to_order_state == 'SEND'}">已发送</c:when>
													<c:when test="${one.f_to_order_state == 'CLOSE'}">关闭</c:when>
												</c:choose>
											</td>
											<td>
												<a href="<%=actionPath%>managerWorkOrder/workOrderDetail.do?id=${one.id}" class="order-btn">
													<c:if test="${one.f_to_order_state != 'CLOSE'}">查看/回复</c:if>
													<c:if test="${one.f_to_order_state == 'CLOSE'}">查看</c:if>
												</a>
												<c:if test="${one.f_to_order_state != 'CLOSE'}">
													<input type="button" class="order-btn" onclick="gourl('确定关闭?','<%=actionPath%>managerWorkOrder/closeOrder.do?id=${one.id}');" value="关闭"></input>
												</c:if>
											</td>
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
												<pg:pager url="managerWorkOrder/searchWorkOrder.do"
													items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
													maxPageItems="${pm.maxitems}"
													export="currentPageNumber=pageNumber">
													<pg:param name="state" value="${state }" />
													<pg:param name="type" value="${type }"/>
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
