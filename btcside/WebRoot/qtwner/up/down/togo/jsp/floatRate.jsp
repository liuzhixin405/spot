<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>合约价格浮动比例信息</title>
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
						合约价格浮动比例管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerFloatRate/saveFloatRate.do" method="post">
									合约号
									<input name="bqCode" type="text" value="" />
									上波动比率
									<input name="uprate" type="text" value="" />
									下波动比率
									<input name="downrate" type="text" value="" />
									<button type="submit" class="button">
										保 存
									</button>
									<span>注释：波动比率分为上波动和下波动，上波动限制买价，下波动限制卖价</span>
								</form>
							</div>
							<div>
								<form action="<%=actionPath%>managerFloatRate/modifyFloatRate.do" method="post">
									主键ID
									<input name="id" type="text" value="" />
									合约号
									<input name="bqCode" type="text" value="" />
									上波动比率
									<input name="uprate" type="text" value="" />
									下波动比率
									<input name="downrate" type="text" value="" />
									<button type="submit" class="button">
										修 改
									</button>
									<span>注释：波动比率分为上波动和下波动，上波动限制买价，下波动限制卖价</span>
								</form>
							</div>
							<div>
								<form action="<%=actionPath%>managerFloatRate/delFloatRate.do" method="post">
									主键ID
									<input name="id" type="text" value="" />
									<button type="submit" class="button">
										删 除
									</button>
									<span>注释：波动比率分为上波动和下波动，上波动限制买价，下波动限制卖价</span>
								</form>
							</div>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										主键
									</th>
									<th>
										合约号
									</th>
									<th>
										上波动比率
									</th>
									<th>
										下波动比率
									</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${lstTransRate}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${l.id}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.transaction_code }"></c:out>
											<br />
										</TD>
										<td>
											<c:out value="${l.uprate}"></c:out>
										</td>
										<td>
											<c:out value="${l.downrate}"></c:out>
										</td>
									</tr>
								</c:forEach>
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
