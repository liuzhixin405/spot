<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>挖矿管理</title>
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
						挖矿管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerMine/updateMineSetting.do" method="post">
							<input type="hidden" name="id" id="id" value="${setting.id}"></input>
							<label for="running">挖矿系统状态：</label>
							<select name="running" id="running">
								<c:if test="${setting.running}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.running}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
						
							<label for="currency_type_id">挖矿币种ID：</label>
							<input name="currency_type_id" value="${setting.currency_type_id}"></input>
							<div class="clear"></div>
						
							<label for="total_amount">挖矿总量：</label>
							<input name="total_amount" value="${setting.total_amount.doubleValue()}"></input>
							<div class="clear"></div>
						
							<label for="mined_amount">当前已挖：</label>
							<input name="mined_amount" value="${setting.mined_amount.doubleValue()}" readonly="readonly"></input>
							<div class="clear"></div>

							<label for="mined_amount">个人挖矿上限：</label>
							<input name="personal_max_amount" value="${setting.personal_max_amount.doubleValue()}"></input>
							<div class="clear"></div>
							
							<label for="beginTime">开始时间：</label>
							<input type="hidden" name="begin_time" id="begin_time" value="${setting.begin_time}"></input>
							<input name="beginTime" type="text" value="<ut:formatDate dateline="${setting.begin_time}"></ut:formatDate>" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
								class="Wdate" readonly="readonly"/>
							<div class="clear"></div>
							
							<label for="running">邀请返佣系统状态：</label>
							<select name=rebate id="rebate">
								<c:if test="${setting.rebate}">
								<option value="true" selected="selected">开启</option>
								<option value="false">关闭</option>
								</c:if>
								<c:if test="${!setting.rebate}">
								<option value="true">开启</option>
								<option value="false" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<button type="submit" id="updateUid" class="button">提交</button>
						</form>
					</div>
					
					<table id="changecolor">
							<thead>
								<tr>
									<th>
										阶段
									</th>
									<th>
										阶段起始币量
									</th>
									<th>
										1手续费得平台币
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
											<c:out value="${l.num}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.stage_amount}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.rate}"></c:out>
										</TD>
										<TD>
											<a href="<%=actionPath%>managerMine/getStage.do?id=${l.id}">编辑</a>
										</TD>
										<TD>
											<a href="<%=actionPath%>managerMine/deleteMineStageSetting.do?id=${l.id}" onclick="return confirm('确认删除吗?')">删除</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerMine/updateMineSetting.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
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
						<div class="">
							<br>
							<div><a class="button" href="<%=actionPath%>managerMine/getStage.do?id=0">新增</a></div>
						</div>
						<script language="javascript" type="text/javascript">senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");</script>
					</div>
					<font color="red">${msg}</font>
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