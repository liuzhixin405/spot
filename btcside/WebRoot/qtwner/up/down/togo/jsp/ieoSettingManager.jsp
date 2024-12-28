<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>IEO释放配置</title>
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
						IEO释放配置
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerMine/updateIeoSetting.do" method="post">
							<input type="hidden" name="id" id="id" value="${setting.id}"></input>
							
							<label for="time">释放时间：</label>
							<input type="hidden" name="unlock_time" id="unlock_time" value="${setting.unlock_time}"></input>
							<input name="unlockTime" type="text" value="<ut:formatDate dateline="${setting.unlock_time}"></ut:formatDate>" 
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
							<div class="clear"></div>
							
							<label for="unlock_rate">释放比例（小数）：</label>
							<input name="unlock_rate" value="${setting.unlock_rate}"></input>
							<div class="clear"></div>
							
							<label for="time">私募开始时间：</label>
							<input type="hidden" name="lock_time_begin" id="lock_time_begin" value="${setting.lock_time_begin}"></input>
							<input name="beginTime" type="text" value="<ut:formatDate dateline="${setting.lock_time_begin}"></ut:formatDate>" 
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
							<div class="clear"></div>
							
							<label for="time">私募结束时间：</label>
							<input type="hidden" name="lock_time_end" id="lock_time_end" value="${setting.lock_time_end}"></input>
							<input name="endTime" type="text" value="<ut:formatDate dateline="${setting.lock_time_end}"></ut:formatDate>" 
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
							<div class="clear"></div>
							
							<button type="submit" class="button">提交</button>
						</form>
					</div>
					
					<table id="changecolor">
							<thead>
								<tr>
									<th>
										释放时间
									</th>
									<th>
										释放比例
									</th>
									<th>
										私募开始时间
									</th>
									<th>
										私募结束时间
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
											<ut:formatDate dateline="${l.unlock_time}"></ut:formatDate>
										</TD>
										
										<TD>
											<c:out value="${l.unlock_rate}"></c:out>
										</TD>
										
										<TD>
											<ut:formatDate dateline="${l.lock_time_begin}"></ut:formatDate>
										</TD>
										
										<TD>
											<ut:formatDate dateline="${l.lock_time_end}"></ut:formatDate>
										</TD>
										
										<TD>
											<a href="<%=actionPath%>managerMine/ieoSettingManager.do?id=${l.id}">编辑</a>
											<a href="<%=actionPath%>managerMine/deleteIeoSetting.do?id=${l.id}">删除</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerMine/ieoSettingManager.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
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