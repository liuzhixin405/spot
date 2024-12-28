<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>快讯管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />

		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<script type="text/javascript" charset="utf-8" src="<%=FileUpload%>ueditor/ueditor.config.js?v=1000"></script>
		<script type="text/javascript" charset="utf-8" src="<%=FileUpload%>ueditor/ueditor.all.min.js?v=1000"></script>
		<script type="text/javascript" charset="utf-8" src="<%=FileUpload%>ueditor/lang/zh-cn/zh-cn.js"></script>

		<style type="text/css">
		.clear {
			clear: both;
		}
		form textarea {
			width: 50% !important;
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
						快讯编辑
					</h3>

					<div class="clear"></div>
				</div>
				<div class="tab-content" id="tab2">
					<form action="managerExpressNews/addNews.do" method="post">
						<fieldset>
							<input type="hidden" name="id" value="${news.id}" />
							<p>
								<label>
									中文快讯编辑
								</label>
								<textarea id="editor" name="zhContent" rows="5" cols="5">
								${news.zh_content}
								</textarea>
							</p>
							<p>
								<label>
									英文快讯编辑
								</label>
								<textarea id="enEditor" name="enContent" rows="5" cols="5">
								${news.en_content}
								</textarea>
							</p>
							<p>
								<label>
									日文快讯编辑
								</label>
								<textarea id="jaEditor" name="jaContent" rows="5" cols="5">
								${news.ja_content}
								</textarea>
							</p>
							<p>
								<label>
									韩文快讯编辑
								</label>
								<textarea id="koEditor" name="koContent" rows="5" cols="5">
								${news.ko_content}
								</textarea>
							</p>
							<p>
								<label>
									繁体快讯编辑
								</label>
								<textarea id="hkEditor" name="hkContent" rows="5" cols="5">
								${news.hk_content}
								</textarea>
							</p>
							<p>
								<input class="button" type="submit" value="提交" />
							</p>
						</fieldset>
						<div class="clear"></div>
						<!-- End .clear -->
					</form>
				</div>
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->

	</body>
	<script type="text/javascript">
	
</script>
</html>