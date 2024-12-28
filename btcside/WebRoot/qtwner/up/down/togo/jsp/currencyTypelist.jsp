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
		<title>资产币种管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript">
			function postsubmit(){
				return true;
			}
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						资产币种管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div><a class="button" href="<%=actionPath%>managerCurrencyType/get.do?typeId=0">新增</a></div>
						</div>
						<table id="changecolor" class="index-tb">
							<thead>
								<tr>
									<th>
										ID
									</th>
									<th>
										币种名称
									</th>
									<th>
										中文描述
									</th>
									<th>
										英文描述
									</th>
									<th>
										日文描述
									</th>
									<th>
										韩文描述
									</th>
									<th>
										繁体描述
									</th>
									<th>
										开启充币
									</th>
									<th>
										开启提币
									</th>
									<th>
										是否使用
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
											<c:out value="${l.type_id}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.type_name}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.description_zh}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.description_en}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.description_jp}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.description_kr}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.description_hk}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.enable_recharge==true}">
													<a class="link"
													   href="javascript:sure('确定要关闭充值吗?','<%=actionPath%>managerCurrencyType/updateRecharge.do?id=${l.type_id}&type=1&state=0')">
														开启</a>
												</c:when>
												<c:when test="${l.enable_recharge==false}">
													<a class="link"
													   href="javascript:sure('确定要开启充值吗?','<%=actionPath%>managerCurrencyType/updateRecharge.do?id=${l.type_id}&type=1&state=1')">
														关闭</a>
												</c:when>
											</c:choose>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.enable_withdraw==true}">
													<a class="link"
													   href="javascript:sure('确定要关闭提现吗?','<%=actionPath%>managerCurrencyType/updateRecharge.do?id=${l.type_id}&type=2&state=0')">
														开启</a>
												</c:when>
												<c:when test="${l.enable_withdraw==false}">
													<a class="link"
													   href="javascript:sure('确定要开启提现吗?','<%=actionPath%>managerCurrencyType/updateRecharge.do?id=${l.type_id}&type=2&state=1')">
														关闭</a>
												</c:when>
											</c:choose>
										</TD>
										<TD>
											<c:if test="${l.state != 1}">
												<font style="color: red;">不使用</font>
											</c:if>
											<c:if test="${l.state == 1}">
												<font style="color: green;">使用中</font>
											</c:if>
										</TD>
										<TD>
											<a href="<%=actionPath%>managerCurrencyType/get.do?typeId=${l.type_id}">编辑</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerCurrencyType/load.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												
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
