<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>汇率比例信息</title>
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
						汇率比例管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerExchangeRate/saveExchangeRate.do" method="post">
									货币类型
									<input name="ex_type" type="text" value="0" id="type_save"/>
									汇率
									<input name="ex_rate" type="text" value="0" id="rate_save"/>
									状态
									<input name="ex_use" type="text" value="0" id="use_save"/>
									<button type="submit" class="button">保 存</button>
									<span>注释：货币类型(1USDT)，汇率（美元对人民币的汇率 1美元=?人民币），状态（当前是否使用汇率 1为使用, 0 为未使用）</span>
								</form>
							</div>
							<div>
								<form action="<%=actionPath%>managerExchangeRate/modifyExchangeRate.do" method="post">
									主键ID
									<input name="id" type="text" value="0" />
									货币类型
									<input name="ex_type" type="text" value="0" />
									汇率
									<input name="ex_rate" type="text" value="0" />
									状态
									<input name="ex_use" type="text" value="0" />
									<button type="submit" class="button">修 改</button>
									<span>注释：货币类型(1USDT)，汇率（美元对人民币的汇率 1美元=?人民币），状态（当前是否使用汇率 1为使用, 0 为未使用）</span>
								</form>
							</div>
							<div>
								<form action="<%=actionPath%>managerExchangeRate/delExchangeRate.do" method="post">
									主键ID
									<input name="id" type="text" value="0" />
									<button type="submit" class="button">删 除</button>
									<span>注释：货币类型(1USDT)，汇率（美元对人民币的汇率 1美元=?人民币），状态（当前是否使用汇率 1为使用, 0 为未使用）</span>
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
										货币类型
									</th>
									<th>
										汇率
									</th>
									<th>
										添加时间
									</th>
									<th>
										状态
									</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${lstUsdRates}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${l.id}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.ex_type}"></c:out><br/>
										</TD>
										<td>
											<c:out value="${l.ex_rate}"></c:out>
										</td>
										<TD>
											<ut:formatDate dateline="${l.ex_time}"></ut:formatDate>
										</TD>
										<TD>
											<c:out value="${l.ex_use == 1 ? '在使用' : '未使用'}"></c:out>
										</TD>
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
