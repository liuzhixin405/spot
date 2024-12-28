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
		<title>SZ非交易时间管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script language="javascript" src="<%=actionPath%>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
		
		<script type="text/javascript">
			
		function add_ajax(url){
			$.post(url + "?t=" + new Date().getTime(), {
				time : $('#time').val(),
	    		type : $('#type').val()
			}, function(data) {
				if (data == "success") {
					document.searchform.submit();
				} else {
					alert(data);
				}
				return;
			});
		}
		</script>
		
		
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						SZ非交易时间管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<form action="<%=actionPath%>noTradeTime/reade.do" method="post" enctype="multipart/form-data">
								<div class="file">
									Excel上传
									<input type="file" name="file" />
									<button type="submit" class="button">
										添加
									</button>
									<span style="color: red;">（上传文件最大支持1024KB，格式支持xls，xlsx）</span>
								</div>
							</form>

							<br>
							<form action="<%=actionPath%>noTradeTime/noTradePage.do" method="post" name="searchform">
							<div>
								时间
								<input type="text" name="time" value="${time }" id="time" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" readonly="readonly"></input>
								类型
								<input type="text" name="type" id="type" value="${type eq null ? 3 : type }">
								<button type="button" class="button" onclick="add_ajax('<%=actionPath%>noTradeTime/add.do')">
									添加
								</button>
								<button type="submit" class="button">
									查 询
								</button>
							</div>
							</form>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										ID
									</th>
									<th>
										时间
									</th>
									<th>
										类别
									</th>
									<th>
										删除
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${status.index+1}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.h_id}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.h_time}"></ut:formatDate>
										</TD>
										<TD>
											<c:out value="${l.h_type}"></c:out>
										</TD>
										<TD>
											<a class="link" href="javascript:sure('您确定要删除吗？','<%=actionPath %>noTradeTime/delete.do?id=${l.h_id }')">删除</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="5">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="noTradeTime/noTradePage.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												
												<pg:param name="time" value="${time }" />
												<pg:param name="type" value="${type }" />
												
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
