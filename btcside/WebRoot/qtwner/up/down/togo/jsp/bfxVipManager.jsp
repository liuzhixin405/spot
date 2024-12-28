<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<title>持Btcside对应Vip等级管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
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
						持Btcside对应Vip等级管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<form class="notification depth-form" action="managerBfx/updateBfxVipSetting.do" method="post">
								<input type="hidden" name="id" value="${setting.id}"></input>								
								
								<label for="hold_count">Btcside持有量：</label>
								<input name="hold_count" value="${setting.hold_count.doubleValue()}"></input>
								<div class="clear"></div>
								
								<label for="level_id">vip等级：</label>
								<select name="level_id" id="level_id">
									<c:forEach items="${levels}" var="level" varStatus="status">
										<c:if test="${level.level_id == setting.level_id}"><option value="${level.level_id}" selected="selected">${level.level_name}</option></c:if>
										<c:if test="${level.level_id != setting.level_id}"><option value="${level.level_id}">${level.level_name}</option></c:if>
									</c:forEach>
								</select>
								<div class="clear"></div>
								
								<label for="note">备注：</label>
								<input name="note" value="${setting.note}"></input>
								<div class="clear"></div>
									
								<button type="submit" id="submit" class="button">${setting.id == null ? '添加' : '更新'}</button>
							</form>
						</div>
					</div>
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									Btcside持有量
								</th>
								<th>
									vip等级Id
								</th>
								<th>
									vip等级
								</th>
								<th>
									备注
								</th>
								<th>
									 操作
								</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${settings}" var="l" varStatus="status">
								<tr>
									<td>
										<c:out value="${l.hold_count.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.level_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.level_name}"></c:out>
									</td>
									<td>
										<c:out value="${l.note}"></c:out>
									</td>
									<td>
										<a href="<%=actionPath%>managerBfx/bfxVipManager.do?id=${l.id}">编辑</a> 
										<a href="javascript:void(0);" class="red" onclick="deleteBfxVip(${l.id})">删除</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<script language="javascript" type="text/javascript">
						senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
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
	function deleteBfxVip(id) {
		var r = confirm("确定删除?");
		if (r == true) {
			window.location.href = "<%=actionPath%>managerBfx/deleteBfxVip.do?id=" + id;
		}
	}
	</script>
</html>