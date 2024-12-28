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
		<title>综合指数合约配比管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script lang="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
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
						综合指数合约配比详情
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form name="check" action="<%=actionPath%>managerCompositeIndex/loadCompositeIndex.do" method="post" onsubmit="return postsubmit();">
									综合指数类型:
									<select name="type" id="type" onchange="javascript:document.check.submit();" >
										<c:if test="${types == null }">
											<option value="0" selected="selected">暂无综合指数类型</option>
										</c:if>
										<c:if test="${types != null }">
											<c:forEach items="${types}" var="l">
												<c:if test="${type == l.type_id }">
													<option value="${l.type_id}" selected="selected">${l.type_name}</option>
												</c:if>
												<c:if test="${type != l.type_id }">
													<option value="${l.type_id}">${l.type_name}</option>
												</c:if>
											</c:forEach>
										</c:if>
									</select>
									<button type="submit" class="button">
										查 询
									</button>
								</form>
							</div>
							
							<br>
							<div>
								<form action="<%=actionPath %>managerCompositeIndex/loadCompositeIndexEdit.do" method="post">
									<input type="hidden" name="indexType" id="indexType" value="${type}"></input>
									<button type="submit" class="button" >新增</button>
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
										合约号
									</th>
									<th>
										权重（%）
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${list}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${status.index+1}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.transaction_code}"></c:out>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.rate }" type="percent" pattern="0.00%"/>
										</TD>
										<TD>
											<a class="link" href="javascript:sure(null,'<%=actionPath%>managerCompositeIndex/loadCompositeIndexEdit.do?indexId=${l.id}')" >修改</a>
											&nbsp;&nbsp;
											<a class="link" href="javascript:sure('确实移除该配比吗?','<%=actionPath%>managerCompositeIndex/deleteCompositeIndex.do?id=${l.id}')" >删除</a>
										</TD>
									</tr>
								</c:forEach>
								<tr><td></td>
									<td></td>
									<TD>
										总权重：<span style="${totalRate == 1 ? 'color: green' : 'color: red'} "><fmt:formatNumber value="${totalRate }" type="percent" pattern="0.00%"/></span>
									</TD>
									<td></td>
								</tr>
							</tbody>
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
