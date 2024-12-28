<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>借贷周期利率配置</title>
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
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						借贷周期利率配置
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					<div style="line-height: 24px;">
						<h5 style="float:left; margin-right: 10px;">选择交易对（借-押）:</h5>
						<c:forEach items="${loanList}" var="l" varStatus="status">
							<a href="<%=actionPath%>managerLoanSetting/loanRateSettingManager.do?loanId=${l.id }">
								<font color="${setting.loan_setting_id == l.id ? '#ff0000' : ''}">${l.borrow_symbol }-${l.pledge_symbol }</font>
							</a> &nbsp;
						</c:forEach>
						<div class="clear"></div>
						
						<form class="notification depth-form" action="managerLoanSetting/updateLoanRateSetting.do" method="post">
						
							<input name="id" type="hidden" value="${setting.id}"></input>
							<input name="loan_setting_id" type="hidden" value="${setting.loan_setting_id}"></input>
						
							<label for="cycle">周期（天数）：</label>
							<input name="cycle" value="${setting.cycle}"></input>
							<div class="clear"></div>
							
							<label for="day_rate">利率（日）：</label>
							<input name="day_rate" value="<fmt:formatNumber pattern="#0.########" value='${setting.day_rate}'/>"></input>
							<div class="clear"></div>
							
							<button type="submit" id="updateUid" class="button">${setting.id > 0 ? '修改' : '新增'}</button>
						</form>
					</div>
					
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									周期（天数）
								</th>
								<th>
									利率（日）
								</th>
								<th>
									操作
								</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${pm.datas}" var="l" varStatus="status">
								<tr>
									<td>
										${l.cycle}
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.day_rate}'/>
									</td>
									<td>
										<a class="link" href="<%=actionPath%>managerLoanSetting/loanRateSettingManager.do?id=${l.id}&loanId=${setting.loan_setting_id }">编辑</a>
										<a class="red" href="<%=actionPath%>managerLoanSetting/deleteLoanRateSetting.do?id=${l.id}&loanId=${setting.loan_setting_id }">删除</a>
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