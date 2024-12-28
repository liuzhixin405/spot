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
<title>创建子账号</title>
<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css"
	media="screen" />
<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css"
	media="screen" />
<link rel="stylesheet" href="<%=jspPath%>css/invalid.css"
	type="text/css" media="screen" />
<script type="text/javascript" charset="utf-8"
	src="<%=jspPath%>js/page.js"></script>
<script type="text/javascript">
			function postform(url) {
				document.select.action=url;
				document.select.submit();
			}
			
		</script>
<style type="text/css">
form label {
	margin: 10px 0;
}

form input {
	margin: 10px 0;
}

.depth-form .check {
	margin: 0;
}
</style>
</head>
<body>
	<div id="main-content">
		<div class="clear"></div>
		<div class="content-box">
			<div class="content-box-header">
				<h3>创建子账号</h3>
				<div class="clear"></div>
			</div>

			<div class="content-box-content">
				<div class="tab-content default-tab" id="tab1">
					<div class="notification success png_bg">
						<br>
						<div>
							<form name="select" class="depth-form" action="#" method="post">
								<label for=userId>母账户id： </label><input type="text"
									name="userid" class="text-input " value="${userid}"></input>
								<button type="button" class="button check"
									onclick="postform('<%=actionPath%>managerSubUserCreate/load.do')">查
									询</button>
								<button type="button" class="button check"
									onclick="postform('<%=actionPath%>managerSubUserCreate/export.do')">导出Excel</button>
								<span>(子账号api)</span>
							</form>
						</div>
						<br>
						<div>
							<form class=" depth-form"
								action="managerSubUserCreate/batchCreateUsers.do" method="post">

								<label for=userId>母账户id：</label> <input name="userId"
									class="text-input "></input>
								<div class="clear"></div>

								<label for="email">子账号登录名范本：</label> <input name="email"
									class="text-input " autocomplete="off"></input> <br> <span>1、例如创建个数为50，范本为：bcoin（子账号登录名：bcoin1，bcoin2.........bcoin50）</span>
								<br> <label for=""></label><span>2、登录名范本不能使用之前使用过的。</span>
								<div class="clear"></div>

								<label for="createCount">子账号创建个数：</label> <input
									name="createCount" class="text-input "></input>
								<div class="clear"></div>

								<label for="allowIp">子账号绑定IP：</label> <input name="allowIp"
									class="text-input "></input> <br> <span>1、多个IP地址用半角逗号隔开，如192.168.1.1,192.168.1.2,192.168.1.3
									暂时不支持修改IP功能</span> <br> <label for=""></label><span>2、若IP不填写绑定，生成的密钥对则会90天后失效。</span>
								<div class="clear"></div>

								<button type="submit" class="button" onclick=""
									style="margin-right: 10px">提交</button>
								<span id="progress" style="display: none;">正在创建：1%</span>
							</form>
						</div>
					</div>
					<table id="changecolor">
						<thead>
							<tr>
								<th>序号</th>
								<th>登录名</th>
								<th>访问公钥</th>
								<th>IP地址</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${pm.datas}" var="l" varStatus="status">
								<tr>
									<TD><c:out value="${status.index+1}"></c:out></TD>
									<TD><c:out value="${l[1]}"></c:out></TD>
									<TD><c:out value="${l[2]}"></c:out></TD>
									<TD><c:out value="${l[3]}"></c:out></TD>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="11">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerSubUserCreate/load.do"
											items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
											maxPageItems="${pm.maxitems}"
											export="currentPageNumber=pageNumber">
											<pg:param name="userid" value="${userid}" />

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
<script type="text/javascript"
	src="<%=FileUpload%>js/jquery.1.7.1.min.js?"></script>
<script type="text/javascript">
	var timer;
	var create = "${create}";
	if (create) {
		function getProcess() {
			$.ajax({
				type : "get",
				url : "<%=actionPath%>managerSubUserCreate/getProcess.do",
				data : {
	
				},
				dataType : "json",
				success : function(data) {
					if (data != null) {
						if (data.rate < 100) {
							$("#progress").html("已创建" + data.rate + "%");
							$("#progress").show();
							timer = setTimeout(getProcess, 500);
						} else {
							$("#progress").html("已创建100%");
						}
					} 
				},
				error : function() {
					console.log("error");
					timer = setTimeout(getProcess, 500);
				}
			});
		}
		getProcess();
	}
	
</script>
</html>

