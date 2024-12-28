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
		<title>用户交易API管理</title>
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
						用户交易API管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerUserApi/load.do">
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId}" id="uid"></input>
									邮箱：
									<input type="text" name="userEmail" class="text-input " value="${userEmail}" id="uemail"></input>
									<button type="submit" class="button">
										查询
									</button>
								</form>
								<br>
								<form action="<%=actionPath %>managerUserApi/load.do">
									状态:
									<select name="state" id="state" >
										<c:choose>
											<c:when test="${state eq 1}">
												<option value="1" selected='selected'>使用中</option>	
											</c:when>
											<c:otherwise>
												<option value="1">使用中</option>	
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${state eq 0}">
												<option value="0" selected='selected'>禁用</option>	
											</c:when>
											<c:otherwise>
												<option value="0">禁用</option>	
											</c:otherwise>
										</c:choose>
									</select>
									<button type="submit" class="button">
										查询
									</button>
								</form>
							</div>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										用户id
									</th>
									<th>
										API访问公钥
									</th>
									<th>
										API访问私钥
									</th>
									<th>
										IP地址
									</th>
									<th>
										状态
									</th>
									<th>
										创建时间
									</th>
									<th>
										过期时间
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
											<c:out value="${l.f_user_id}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.app_key}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.app_secret}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.allow_ip}"></c:out>
										</TD>
										<TD>
											<c:if test="${l.state eq 1 }">使用中</c:if>
											<c:if test="${l.state eq 0 }">禁用</c:if>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.update_time}"></ut:formatDate>
										</TD>
										<TD>
											<c:if test="${l.allow_ip == '' || l.allow_ip == null}"><ut:formatDate dateline="${l.over_time}"></ut:formatDate></c:if>
						        			<c:if test="${l.allow_ip != '' && l.allow_ip != null}">永久有效</c:if>
										</TD>
										<TD>
											<a class="link" href="javascript:sure(null,'<%=actionPath%>managerUserApi/get.do?uid=${l.f_user_id}')" >修改</a>
											&nbsp;&nbsp;
											<a class="link" href="javascript:sure('删除该用户交易API?','<%=actionPath %>managerUserApi/delete.do?uid=${l.f_user_id}')" >删除</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerUserApi/load.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												
												<pg:param name="userId" value="${userId}"/>
												<pg:param name="userEmail" value="${userEmail}"/>
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
