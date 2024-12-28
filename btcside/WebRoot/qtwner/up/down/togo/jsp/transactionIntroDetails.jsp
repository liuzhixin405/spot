<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>合约详情介绍编辑</title>
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
						合约详情介绍编辑
					</h3>

					<div class="clear"></div>
				</div>
				<div class="tab-content" id="tab2">
					<form action="managerTrans/addTransIntroduce.do" method="post">
						<fieldset>
							<input type="hidden" name="id" value="${introduce.id}" />
							<p>
								<label>
									合约号
								</label>
								<c:if test="${introduce ne null }">
									<input type="text" name="code" value="${introduce.transaction_code }" readonly="readonly">
								</c:if>
								<c:if test="${introduce eq null }">
									<select name="code" id="code">
										<c:forEach items="${transList}" var="trans">
											<c:if test="${introduce.transaction_code == trans.transaction_code}">
												<option value="${trans.transaction_code}" selected="selected">${trans.transaction_code}</option>
											</c:if>
											<c:if test="${introduce.transaction_code != trans.transaction_code}">
												<option value="${trans.transaction_code}">${trans.transaction_code}</option>
											</c:if>
										</c:forEach>
									</select>
								</c:if>
							</p>
							<p>
								<label>
									中文说明内容编辑
								</label>
								<textarea id="editor" name="zhDesc" rows="100" cols="100">
								${introduce.zh_introduce}
								</textarea>
							</p>
							<p>
								<label>
									英文说明内容编辑
								</label>
								<textarea id="enEditor" name="enDesc" rows="100" cols="100">
								${introduce.en_introduce}
								</textarea>
							</p>
							<p>
								<label>
									日文说明内容编辑
								</label>
								<textarea id="jaEditor" name="jaDesc" rows="100" cols="100">
								${introduce.ja_introduce}
								</textarea>
							</p>
							<p>
								<label>
									韩文说明内容编辑
								</label>
								<textarea id="koEditor" name="koDesc" rows="100" cols="100">
								${introduce.ko_introduce}
								</textarea>
							</p>
							<p>
								<label>
									繁体说明内容编辑
								</label>
								<textarea id="hkEditor" name="hkDesc" rows="100" cols="100">
								${introduce.hk_introduce}
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
	//实例化编辑器
	//建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
	UE.getEditor('editor');
	UE.getEditor('enEditor');
	UE.getEditor('jaEditor');
	UE.getEditor('koEditor');
	UE.getEditor('hkEditor');

	function isFocus(e) {
		alert(UE.getEditor('editor').isFocus());
		UE.dom.domUtils.preventDefault(e)
	}
	function setblur(e) {
		UE.getEditor('editor').blur();
		UE.dom.domUtils.preventDefault(e)
	}
	function insertHtml() {
		var value = prompt('插入html代码', '');
		UE.getEditor('editor').execCommand('insertHtml', value)
	}
	function createEditor() {
		enableBtn();
		UE.getEditor('editor');
	}
	function getAllHtml() {
		alert(UE.getEditor('editor').getAllHtml())
	}
	function getContent() {
		var arr = [];
		arr.push("使用editor.getContent()方法可以获得编辑器的内容");
		arr.push("内容为：");
		arr.push(UE.getEditor('editor').getContent());
		alert(arr.join("\n"));
	}
	function getPlainTxt() {
		var arr = [];
		arr.push("使用editor.getPlainTxt()方法可以获得编辑器的带格式的纯文本内容");
		arr.push("内容为：");
		arr.push(UE.getEditor('editor').getPlainTxt());
		alert(arr.join('\n'))
	}
	function setContent(isAppendTo) {
		var arr = [];
		arr.push("使用editor.setContent('欢迎使用ueditor')方法可以设置编辑器的内容");
		UE.getEditor('editor').setContent('欢迎使用ueditor', isAppendTo);
		alert(arr.join("\n"));
	}
	function setDisabled() {
		UE.getEditor('editor').setDisabled('fullscreen');
		disableBtn("enable");
	}

	function setEnabled() {
		UE.getEditor('editor').setEnabled();
		enableBtn();
	}

	function getText() {
		//当你点击按钮时编辑区域已经失去了焦点，如果直接用getText将不会得到内容，所以要在选回来，然后取得内容
		var range = UE.getEditor('editor').selection.getRange();
		range.select();
		var txt = UE.getEditor('editor').selection.getText();
		alert(txt)
	}

	function getContentTxt() {
		var arr = [];
		arr.push("使用editor.getContentTxt()方法可以获得编辑器的纯文本内容");
		arr.push("编辑器的纯文本内容为：");
		arr.push(UE.getEditor('editor').getContentTxt());
		alert(arr.join("\n"));
	}
	function hasContent() {
		var arr = [];
		arr.push("使用editor.hasContents()方法判断编辑器里是否有内容");
		arr.push("判断结果为：");
		arr.push(UE.getEditor('editor').hasContents());
		alert(arr.join("\n"));
	}
	function setFocus() {
		UE.getEditor('editor').focus();
	}
	function deleteEditor() {
		disableBtn();
		UE.getEditor('editor').destroy();
	}
	function disableBtn(str) {
		var div = document.getElementById('btns');
		var btns = domUtils.getElementsByTagName(div, "button");
		for ( var i = 0, btn; btn = btns[i++];) {
			if (btn.id == str) {
				domUtils.removeAttributes(btn, [ "disabled" ]);
			} else {
				btn.setAttribute("disabled", "true");
			}
		}
	}
	function enableBtn() {
		var div = document.getElementById('btns');
		var btns = domUtils.getElementsByTagName(div, "button");
		for ( var i = 0, btn; btn = btns[i++];) {
			domUtils.removeAttributes(btn, [ "disabled" ]);
		}
	}

	function getLocalData() {
		alert(UE.getEditor('editor').execCommand("getlocaldata"));
	}

	function clearLocalData() {
		UE.getEditor('editor').execCommand("clearlocaldata");
		alert("已清空草稿箱")
	}
</script>
</html>