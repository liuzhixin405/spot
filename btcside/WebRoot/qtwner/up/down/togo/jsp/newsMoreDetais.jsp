<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
<head>
    <base href="<%=actionPath%>">
    <title>批量公告资讯管理</title>
    <link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
    <link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />

    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <script type="text/javascript" charset="utf-8" src="<%=FileUpload%>ueditor/ueditor.config.js?v=1000"></script>
    <script type="text/javascript" charset="utf-8" src="<%=FileUpload%>ueditor/ueditor.all.min.js?v=1000"></script>

    <script type="text/javascript" charset="utf-8" src="<%=FileUpload%>ueditor/lang/zh-cn/zh-cn.js"></script>
    <script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>

    <style type="text/css">
        .clear {
            clear: both;
        }
        .title-tabber {
            display: inline-block;
            padding-left: 30px;
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
                编辑
            </h3>

            <div class="clear"></div>
        </div>
        <div class="tab-content" id="tab2">
            <form action="managerNotice/addnews.do" method="post">
                <fieldset>
<%--                    <input type="hidden" name="id" value="${id}" />--%>
                <div class="title-tabber">
                    <p>
                        <label>
                            中文标题
                        </label>
                        <input type="text" name="title" value="${title }"></input>
                    </p>
                    <p>
                        <label>
                            中文描述
                        </label>
                        <input type="text" name="descript" value="${descript }"></input>
                    </p>
                    <p>
                        <label>
                            中文关键字
                        </label>
                        <input type="text" name="keyword" value="${keyword }"></input>
                    </p>
                </div>
                <div class="title-tabber">
                    <p>
                        <label>
                            英文标题
                        </label>
                        <input type="text" name="enTitle" value="${enTitle }"></input>
                    </p>
                    <p>
                        <label>
                            英文描述
                        </label>
                        <input type="text" name="enDescript" value="${enDescript }"></input>
                    </p>
                    <p>
                        <label>
                            英文关键字
                        </label>
                        <input type="text" name="enKeyword" value="${enKeyword }"></input>
                    </p>
                </div>
                <div class="title-tabber">
                    <p>
                        <label>
                            日文标题
                        </label>
                        <input type="text" name="jpTitle" value="${jpTitle }"></input>
                    </p>
                    <p>
                        <label>
                            日文描述
                        </label>
                        <input type="text" name="jpDescript" value="${jpDescript }"></input>
                    </p>
                    <p>
                        <label>
                            日文关键字
                        </label>
                        <input type="text" name="jpKeyword" value="${jpKeyword }"></input>
                    </p>
                </div>
                <div class="title-tabber">
                    <p>
                        <label>
                            韩文标题
                        </label>
                        <input type="text" name="korTitle" value="${korTitle }"></input>
                    </p>
                    <p>
                        <label>
                            韩文描述
                        </label>
                        <input type="text" name="korDescript" value="${korDescript }"></input>
                    </p>
                    <p>
                        <label>
                            韩文关键字
                        </label>
                        <input type="text" name="korKeyword" value="${korKeyword }"></input>
                    </p>
                </div>
                <div class="title-tabber">
                    <p>
                        <label>
                            繁体标题
                        </label>
                        <input type="text" name="chtTitle" value="${chtTitle }"></input>
                    </p>
                    <p>
                        <label>
                            繁体描述
                        </label>
                        <input type="text" name="chtDescript" value="${chtDescript }"></input>
                    </p>
                    <p>
                        <label>
                            繁体关键字
                        </label>
                        <input type="text" name="chtKeyword" value="${chtKeyword }"></input>
                    </p>
                </div>
                    <p>
                        <label>
                            类型
                        </label>
                        <select name="type" class="small-input" >
                            <c:choose>
                                <c:when test="${type==1}">
                                    <option value="1" selected="selected">
                                        站内公告
                                    </option>
                                    <option value="2">
                                        信息披露
                                    </option>
                                    <option value="3">
                                        帮助
                                    </option>
                                    <option value="4">
                                        新闻资讯
                                    </option>
                                </c:when>
                                <c:when test="${type==2}">
                                    <option value="1">
                                        站内公告
                                    </option>
                                    <option value="2" selected="selected">
                                        信息披露
                                    </option>
                                    <option value="3">
                                        帮助
                                    </option>
                                    <option value="4">
                                        新闻资讯
                                    </option>
                                </c:when>
                                <c:when test="${type==3}">
                                    <option value="1">
                                        公告咨询
                                    </option>
                                    <option value="2">
                                        信息披露
                                    </option>
                                    <option value="3" selected="selected">
                                        帮助
                                    </option>
                                    <option value="4">
                                        新闻资讯
                                    </option>
                                </c:when>
                                <c:when test="${type==4}">
                                    <option value="1">
                                        站内公告
                                    </option>
                                    <option value="2">
                                        信息披露
                                    </option>
                                    <option value="3">
                                        帮助
                                    </option>
                                    <option value="4" selected="selected">
                                        新闻资讯
                                    </option>
                                </c:when>
                                <c:otherwise>
                                    <option value="1">
                                        站内公告
                                    </option>
                                    <option value="2">
                                        信息披露
                                    </option>
                                    <option value="3">
                                        帮助
                                    </option>
                                    <option value="4">
                                        新闻资讯
                                    </option>
                                </c:otherwise>
                            </c:choose>
                        </select>
                    </p>
                    <p>
                        <label>
                            中文内容编辑
                        </label>
                        <textarea id="editor" name="content" rows="100" cols="100">
                            ${content}
                        </textarea>
                    </p>
                    <p>
                        <label>
                            英文内容编辑
                        </label>
                        <textarea id="enEditor" name="enContent" rows="100" cols="100">
                            ${data.data.en}
                        </textarea>
                    </p>
                    <p>
                        <label>
                            日文内容编辑
                        </label>
                        <textarea id="jpEditor" name="jpContent" rows="100" cols="100">
                            ${data.data.jp}
                        </textarea>
                    </p>
                    <p>
                        <label>
                            韩文内容编辑
                        </label>
                        <textarea id="korEditor" name="korContent" rows="100" cols="100">
                            ${data.data.kor}
                        </textarea>
                    </p>
                    <p>
                        <label>
                            繁体内容编辑
                        </label>
                        <textarea id="hkEditor" name="chtContent" rows="100" cols="100">
                            ${data.data.cht}
                        </textarea>
                    </p>
                    <p>
                        <input class="button" type="button" value="翻译" onclick="transContent()"/>
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
	UE.getEditor('jpEditor');
	UE.getEditor('korEditor');
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



<%--	var content = "${content}";--%>
	function transContent() {
	var basePath = '<%=actionPath%>';
	var contents = [];
    contents.push(UE.getEditor('editor').getContent());
    var content = contents.toString();
<%--    console.log(content);--%>
	$.ajax({
			type : "POST",
			dataType : "json",
			url : basePath + "managerNotice/transContent.do",
			data : {
				content : content,
			},
			success : function(data) {
				if (data.code == 'success') {
<%--				    console.log(data.data);--%>
<%--				    console.log("英文" + data.data.en);--%>
<%--				    console.log("日文" + data.data.jp);--%>
<%--				    console.log("韩文" + data.data.kor);--%>
<%--				    console.log("繁体" + data.data.cht);--%>
				    UE.getEditor('enEditor').setContent(data.data.en);
				    UE.getEditor('jpEditor').setContent(data.data.jp);
				    UE.getEditor('korEditor').setContent(data.data.kor);
				    UE.getEditor('hkEditor').setContent(data.data.cht);
				} else {
					tipAlert(data.msg);
					return;
				}
			}
		});

	}

</script>
</html>