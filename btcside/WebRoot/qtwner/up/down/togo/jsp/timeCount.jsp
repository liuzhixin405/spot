<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>查询统计</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						查询统计管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification information png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerFinancial/loadCount.do" method="post">
									开始时间
									<input name="startTime" type="text" value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
									结束时间
									<input name="endTime" type="text" value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
									<button type="submit" class="button">查询</button>
								</form>
							</div>
						</div>
						<table>
							<thead>
								<tr>
									<th>
										合约号
									</th>
									<th>
										成交量
									</th>
									<th>
										类型
									</th>
									<th>
										均价
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${lstCounts }" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${l.bqCode }"></c:out>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.countAmount }" pattern="#.##"></fmt:formatNumber>
										</TD>
										<TD>
											<c:out value="${l.type eq 1 ? '买' : '卖'}"></c:out>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.averagePrice }" pattern="#.########"></fmt:formatNumber>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
