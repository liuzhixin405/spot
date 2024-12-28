<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>APP管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<style type="text/css">
			.clear {
				clear: both;
			}
			input {
    			width: 400px;
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
						APP管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div class="notification" style="line-height: 24px;">
						<div class="file" style="padding-left: 133px;">
							安装包上传
							<input type="file" name="file1" id="file1"/>
							<span style="color: red;">（格式支持apk，ipa）</span>
						</div>
					
						<form class="depth-form" action="managerApp/updateApp.do" method="post">
							<input type="hidden" name="id" value="${app.id}"></input>
						
							<label for="name">名称：</label>
							<input name="name" value="${app.name}"></input>
							<div class="clear"></div>
							
							<label for="platform">平台：</label>
							<select name="platform" id="platform">
								<c:if test="${app.platform eq null }">
									<option value="Android" selected="selected">Android</option>
									<option value="IOS">IOS</option>
								</c:if>
								<c:if test="${app.platform eq 'Android'}">
									<option value="Android" selected="selected">Android</option>
								</c:if>
								<c:if test="${app.platform eq 'IOS' }">
									<option value="IOS" selected="selected">IOS</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<label for="version">版本：</label>
							<input name="version" value="${app.version}"></input>
							<div class="clear"></div>
							
							<label for="note">更新日志（'-'号隔开）：</label>
							<input name="note" value="${app.note}"></input>
							<div class="clear"></div>
						
							<label for="download_url">下载路径：</label>
							<input name="download_url" id="download_url" value="${app.download_url}" readonly="readonly"></input>
							<div class="clear"></div>
							
							<label for="force_version">最后一个强制更新的版本：</label>
							<input name="force_version" value="${app.force_version}"></input>
							<div class="clear"></div>
							
							<button type="submit" class="button">提交</button>
						</form>
					</div>
					<font color="red">${msg}</font>
					
					<table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										名称
									</th>
									<th>
										平台
									</th>
									<th>
										版本
									</th>
									<th>
										更新日志
									</th>
									<th>
										下载路径
									</th>
									<th>
										下载次数
									</th>
									<th>
										更新时间
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${list}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${status.index+1}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.name}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.platform}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.version}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.note}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.download_url}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.download_times}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.update_time}"></ut:formatDate>
										</TD>
										<TD>
											<a class="link" href="<%=actionPath%>managerApp/appManager.do?platform=${l.platform}" >修改版本</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
						</table>
				</div>
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
	<script type="text/javascript">
		function upload() {
			var formData = new FormData();
			formData.append("files", $("#file1")[0].files[0]);
			console.log(formData);
			$.ajax({
				type : 'POST',
				url : '<%=actionPath%>managerApp/upload.do',
				data : formData,
				contentType : false,
				processData : false,
				/* mimeType : "multipart/form-data", */
				dataType : "json",
				success : function(data) {
					if ('success' == data.code) {
						console.log(data.data);
						$("#download_url").val(data.data);
						alert("上传成功!");
					} else {
						alert(data.msg);
					}
				}
			});
		}
		
		var myfile = document.getElementById('file1');
		myfile.addEventListener('change',upload,true);
	</script>
</html>