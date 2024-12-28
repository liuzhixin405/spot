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
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						综合指数合约配比详情编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
								<form name="check" class="notification depth-form" action="<%=actionPath%>managerCompositeIndex/addCompositeIndex.do" method="post" onsubmit="return postsubmit();">
									<input type="hidden" name="id" id="id" value="${index.id}"></input>
									<label>综合指数类型:</label>
									<c:if test="${index.id ne null and index.id > 0}">
										<select name="index_type" id="index_type">
											<c:if test="${types != null }">
												<c:forEach items="${types}" var="l">
													<c:if test="${index.index_type == l.type_id }">
														<option value="${l.type_id}" selected="selected">${l.type_name}</option>
													</c:if>
												</c:forEach>
											</c:if>
										</select>
									</c:if>
									<c:if test="${index.id eq null or index.id == 0}">
										<select name="index_type" id="index_type">
											<c:if test="${types == null }">
												<option value="0" selected="selected">暂无综合指数类型</option>
											</c:if>
											<c:if test="${types != null }">
												<c:forEach items="${types}" var="l">
													<c:if test="${index.index_type == l.type_id }">
														<option value="${l.type_id}" selected="selected">${l.type_name}</option>
													</c:if>
													<c:if test="${index.index_type != l.type_id }">
														<option value="${l.type_id}">${l.type_name}</option>
													</c:if>
												</c:forEach>
											</c:if>
										</select>
									</c:if>
									<div class="clear"></div>
									
									<label>合约号：</label>
									<select name="transaction_code" id="transaction_code">
										<c:if test="${index.transaction_code ne null }">
												<option value="${index.transaction_code}" selected="selected">${index.transaction_code}</option>
											</c:if>
										<c:forEach items="${transList}" var="trans">
											<c:if test="${index.transaction_code eq null or index.transaction_code != trans.transaction_code}">
												<option value="${trans.transaction_code}">${trans.transaction_code}</option>
											</c:if>
										</c:forEach>
									</select>
									<div class="clear"></div>
									
									<label>权重（小数）：</label>
									<input name="rate" id="rate" value="${index.rate}" autocomplete="off"></input>（eg：10%应写为0.1）
									<div class="clear"></div>
									
									<label>安全密码：</label>
									<input name="password" type="password" autocomplete="off" />
									<div class="clear"></div>
									
									<button type="submit" class="button">
										提 交
									</button>
								</form>
							
						</div>
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
