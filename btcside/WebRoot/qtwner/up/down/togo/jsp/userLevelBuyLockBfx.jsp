<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>平台币销售查询</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<style type="text/css">
			.clear {
				clear: both;
			}
		</style>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						平台币销售查询 
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<h5 style="margin-bottom:20px;">
						总累计销售:<span style="font-size:14px;"><fmt:formatNumber value="${allLock }" pattern="#.########"/> Btcside</span>
					</h5>
					
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>managerLevelBuyLock/levelBuyLock.do" method="get">
										用户ID
										<input name="userId" type="text" value="${userId }"/>
										&nbsp;&nbsp;&nbsp;&nbsp;用户邮箱
										<input name="email" type="text" value="${email }"/>
										&nbsp;&nbsp;&nbsp;&nbsp;会员类型
										<select name="subuserType" >
											<option value="0" ${subuserType == 0 ? "selected='selected'" : ''}>一级二级会员</option>
											<option value="1" ${subuserType == 1 ? "selected='selected'" : ''}>仅一级会员</option>
											<option value="2" ${subuserType == 2 ? "selected='selected'" : ''}>仅二级会员</option>
										</select>
										<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查询</button>
									</form>
								</div>
								
								<div class="clear"></div>
							</div>
						</div>
					</div>
					<div style="color:red">${msg}</div>
					
					<h5 style="margin-top:20px;">
						基本信息
					</h5>
					<table id="changecolor1">
						<thead>
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
									账户锁仓
								</th>
								<th>
									一级锁仓
								</th>
								<th>
									二级锁仓
								</th>
								<th>
									累计锁仓
								</th>
								<th>
									邀请人
								</th>
							</tr>
						</thead>

						<tbody>
							<tr>
								<td><c:out value="${userId }"></c:out></td>
								<td><c:out value="${userEmail }"></c:out></td>
								<td><c:out value="${reconcilia.vip_name }"></c:out></td>
								<td><fmt:formatNumber value="${reconcilia.self_bfx_lock }" pattern="#.########"/></td>
								<td><fmt:formatNumber value="${subuserLock }" pattern="#.########"/></td>
								<td><fmt:formatNumber value="${subuser2Lock }" pattern="#.########"/></td>
								<td><fmt:formatNumber value="${reconcilia.total_bfx_lock }" pattern="#.########"/></td>
								<td><c:out value="${reconcilia.superior }"></c:out></td>
							</tr>
						</tbody>
					</table>

					
					<h5 style="margin-top:20px;">会员信息</h5>
					<table id="changecolor2">
						<thead>
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
									会员类型
								</th>
								<th>
									Btcside锁仓数量
								</th>
								<th>
									邀请人
								</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${pm.datas}" var="l" varStatus="status">
								<tr>
									<td><c:out value="${l.f_user_id }"></c:out></td>
									<td><c:out value="${l.f_user_email }"></c:out></td>
									<td><c:out value="${l.superior == userId ? '一级会员' : '二级会员' }"></c:out></td>
									<td><c:out value="${l.vip_name }"></c:out></td>
									<td><fmt:formatNumber value="${l.self_bfx_lock }" pattern="#.########"/></td>
									<td><c:out value="${l.superior }"></c:out></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<tfoot>
						<tr>
							<td colspan="9">
								<div class="pagination">
									<!-- ********** 分页开始 **********	-->
									<pg:pager url="managerLevelBuyLock/levelBuyLock.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="userId" value="${userId}"/>
										<pg:param name="email" value="${email}"/>
										<pg:param name="subuserType" value="${subuserType}"/>
										
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
								<!-- End .pagination -->
								<div class="clear"></div>
							</td>
						</tr>
					</tfoot>
					<script language="javascript" type="text/javascript">
						senfe("changecolor1","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						senfe("changecolor2","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
					</script>
				</div>
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
	</script>
</html>