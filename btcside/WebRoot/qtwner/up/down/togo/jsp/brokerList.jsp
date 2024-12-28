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
		<title>自由经纪人审核</title>
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
						自由经纪人审核管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerBroker/selectBrokers.do" method="post">
									用户ID
									<input name="uid" type="text" value="${uid}" />
									<button type="submit" class="button">查 询</button>
									<a style="margin-left: 80px;" href="<%=actionPath%>managerBroker/selectBrokers.do?ustate=-1">已拒绝</a>
									<a style="margin-left: 80px;" href="<%=actionPath%>managerBroker/selectBrokers.do?ustate=0">待审核</a>
									<a style="margin-left: 80px;" href="<%=actionPath%>managerBroker/selectBrokers.do?ustate=1">已通过</a>
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
										<th>
											状态
										</th>
										<th>
											备注说明
										</th>
										<th>
											操作
										</th>
									</tr>
								</thead>
								<tbody>
									<c:if test="${apply==null && pm!= null }">
										<c:forEach items="${pm.datas }" var="one" varStatus="status">
											<tr>
												<form action="<%=actionPath%>managerBroker/updateBroker.do">
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
												<td>
													<c:choose>
														<c:when test="${one.state==0}">
															<font color="red">未受理</font>
														</c:when>
														<c:when test="${one.state==1}">受理成功</c:when>
														<c:when test="${one.state==-1}">拒绝</c:when>
													</c:choose>
												</td>
												<td>
													<c:choose>
														<c:when test="${one.state==0}">
														<input type="text" name="reason" value="${one.reason}" />
														</c:when>
														<c:otherwise>
															<c:out value="${one.reason }"></c:out>
														</c:otherwise>
													</c:choose>
												</td>
												<td>
													<c:choose>
														<c:when test="${one.state==0}">
															<input type="button" onclick="gourl('确定提交?','<%=actionPath%>managerBroker/updateBroker.do?id=${one.f_user_id}&state=1');" value='确认'"></input>
															<input type="hidden" name="state" value="-1" />
															<input type="submit" value="拒绝" onclick="return postsure('确定要拒绝该请求吗?该操作无法回退');"></input>
															<a href="<%=actionPath%>managerBroker/searchBroker.do?uid=${one.f_user_id }">查 看</a>
														</c:when>
														<c:when test="${one.state==1}">
															<a href="<%=actionPath%>managerBroker/searchBroker.do?uid=${one.f_user_id }">查 看</a>
														</c:when>
														<c:when test="${one.state==-1}">
															<a href="<%=actionPath%>managerBroker/searchBroker.do?uid=${one.f_user_id }">查 看</a> | 
															<a href="<%=actionPath%>managerBroker/delBroker.do?uid=${one.f_user_id }">删 除</a>
														</c:when>
													</c:choose>
												</td>
												</form>
											</tr>
										</c:forEach>
									</c:if>
									<c:if test="${apply!=null && pm==null }">
										<tr>
											<td>
												<c:out value="${apply.f_user_id }"></c:out>
												<input type="hidden" name="id" value="${apply.f_user_id }" />
											</td>
											<td>
												<c:out value="${apply.idcard }"></c:out>
											</td>
											<td>
												<c:out value="${apply.mobile }"></c:out>
											</td>
											<td>
												<c:out value="${apply.address }"></c:out>
											</td>
											<td>
												<ut:formatDate dateline="${apply.dateline }"></ut:formatDate>
											</td>
											<td>
												<c:choose>
													<c:when test="${apply.state==0}">
														<font color="red">未受理</font>
													</c:when>
													<c:when test="${apply.state==1}">受理成功</c:when>
													<c:when test="${apply.state==-1}">拒绝</c:when>
												</c:choose>
											</td>
											<td>
												<c:if test="${apply.state==0}">
													<input type="text" name="reason" value="${apply.reason}" />
												</c:if>
											</td>
											<td>
												<c:choose>
													<c:when test="${apply.state==0}">
														<input type="button" onclick="gourl('确定提交?','<%=actionPath%>managerBroker/updateBroker.do?id=${apply.f_user_id}&state=1');" value='确认'"></input>
														<input type="hidden" name="state" value="-1" />
														<input type="submit" value="拒绝" onclick="return postsure('确定要拒绝该请求吗?该操作无法回退');"></input>
														<a href="<%=actionPath%>managerBroker/searchBroker.do?uid=${one.f_user_id }">查 看</a>
													</c:when>
													<c:when test="${apply.state==1}">
															<a href="<%=actionPath%>managerBroker/searchBroker.do?uid=${one.f_user_id }">查 看</a>
														</c:when>
														<c:when test="${apply.state==-1}">
															<a href="<%=actionPath%>managerBroker/searchBroker.do?uid=${one.f_user_id }">查 看</a> | 
															<a href="<%=actionPath%>managerBroker/delBroker.do?uid=${one.f_user_id }">删 除</a>
														</c:when>
												</c:choose>
											</td>
										</tr>
									</c:if>
									
								</tbody>
								<c:if test="${apply==null && pm!=null }">
									<tfoot>
										<tr>
											<td colspan="9">
												<div class="pagination">
													<!-- ********** 分页开始 **********	-->
													<pg:pager url="managerBroker/selectBrokers.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
														maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
														<pg:param name="ustate" value="${state }" />
	
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
