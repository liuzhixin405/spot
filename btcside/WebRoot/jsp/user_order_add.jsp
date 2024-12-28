<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>

<!DOCTYPE HTML>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.workerOrderAdd.add"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/file-upload.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/lrz.bundle.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.upload.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
	<jsp:include page="_top.jsp"></jsp:include>
	<jsp:include page="_head.jsp"></jsp:include>
	
	<div class="container order-main center-cont" style="background: #FFFFFF;box-shadow: 0px 2px 6px 1px rgba(148,171,192,0.1);">
		<div class="order-row">
			<a class="btn sub-btn-return order-btn-back" href="javascript:history.go(-1)"><fmt:message key="msg.pg.trade.goBack"/></a>
			<div class="order-title"><fmt:message key="msg.pg.workerOrderAdd.add"/></div>
			<hr>
			<div class="order-note">
				<p><fmt:message key="msg.pg.workerOrderAdd.title1"/></p>
				<p><fmt:message key="msg.pg.workerOrderAdd.title2"/></p>
				<p><fmt:message key="msg.pg.workerOrderAdd.title3"/></p>
				<p><fmt:message key="msg.pg.workerOrderAdd.title4"/></p>
				<p><fmt:message key="msg.pg.workerOrderAdd.title5"/></p>
				<p><fmt:message key="msg.pg.workerOrderAdd.title6"/></p>
			</div>
		</div>
	
		<div class="order-row">
			<form action="<%=orderPath %>doAddOrder.do" method="post" onsubmit="return checkParams()">
				<div class="">
					<div class="order-item">
						<label><fmt:message key="msg.pg.workerOrderList.orderType"/></label>
						<select id="f_order_type" name="f_order_type">
							<option value="ACCOUNT"><fmt:message key="msg.pg.workerOrderAdd.account"/></option>
							<option value="TRADE"><fmt:message key="msg.pg.workerOrderAdd.trade"/></option>
							<option value="RW"><fmt:message key="msg.pg.workerOrderAdd.rw"/></option>
							<option value="COMPLAIN"><fmt:message key="msg.pg.workerOrderAdd.Complaint"/></option>
							<option value="COOP"><fmt:message key="msg.pg.workerOrderAdd.COOP"/></option>
						</select>
					</div>
					<div class="order-item">
						<label><fmt:message key="msg.pg.workerOrderList.orderTheme"/></label>
						<input type="text" class="order-input" id="f_theme" name="f_theme" value="${theme}" placeholder="<fmt:message key="msg.pg.workerOrderAdd.themeDescription"/>" onfocus="removeMsg()" maxlength="120"/>
					</div>
					
					<div class="order-item">
						<label class="form-textarea-title"><fmt:message key="msg.pg.workerOrderList.orderContent"/></label>
						<textarea rows="8" cols="80" id="f_reply_content" class="order-input" name="f_reply_content" onfocus="removeMsg()" placeholder="<fmt:message key="msg.pg.workerOrderAdd.contentDescription"/>" maxlength="1200"></textarea>
					</div>
					<div class="order-item">
						<label><fmt:message key="msg.pg.workerOrderAdd.file"/></label>
						<button type="button" class="btn form-btn sub-btn" onclick="uploadFile('front')"><fmt:message key="msg.pg.workerOrderAdd.selectFile"/></button>
						<input type="hidden" id="file" name="f_file" class="order-input conmiss" value="" />
						<input type="file" id="fileOpen" name="fileOpen" accept="image/png,image/jpeg" class="text-input conmiss" value="" />
						<input value="" readonly="readonly" type="text" id="fileShow" name="fileShow" class="file-note"  placeholder="<fmt:message key="msg.pg.workerOrderAdd.fileNote"/>"/>
						<span class="file-progress" id="progressid"></span>
						<span id="time"></span>
						<img id="showCardFrontImg" class="file-show" alt="" src="">
					</div>
					<div id="message" class="message center">${error}</div>
					<div class="order-item center">
						<input type="submit" class="btn sub-btn order-btn-sub" id="" value="<fmt:message key="msg.pg.common.submit"/>"></input>
					</div>
				</div>
			</form>
		</div>
	</div>
	
	<jsp:include page="_footer.jsp" />
</body>
<script type="text/javascript">
	
	function checkParams() {
		if ($.trim($("#f_theme").val()).length == 0) {
			$("#message").html("<fmt:message key='msg.pg.workerOrderAdd.themeNull'/>");
			return false;
		}
		
		if ($.trim($("#f_reply_content").val()).length == 0) {
			$("#message").html("<fmt:message key='msg.pg.workerOrderAdd.contentNull'/>");
			return false;
		}
		return true;
	}
	
	var bothfile = '';
	
	var myfile = document.getElementById('fileOpen');
	myfile.addEventListener('change',readFile,true);

	function uploadFile() {
		document.getElementById('fileOpen').click();
	}
	
	function readFile() {
		var File = this.files;
		var bothfile = File;
		var dataurl = window.URL.createObjectURL(this.files.item(0));
		bothurl = dataurl;
		$("#showCardFrontImg").attr("src", bothurl);
		$("#fileShow")[0].type = 'hidden';
		var img = myfile.value;
		var listener = new UploadListener();
		var uploader = new Uploader(this.files.item(0), listener, 'user'); 
	}
	
	function UploadListener() {
	    this.onProgress = function(e) {
	    	$("#progressid").css("display", "block");
	    	$("#progressid").html("<fmt:message key='msg.pg.workerOrderAdd.fileProgress'/>：" + e + "%");
	    }
	    
	    this.uploadComplete = function(data) {
	    	$("#file").val(data);
			$("#fileShow").val(data);
			$("#fileShow")[0].type = 'hidden';
	    	$("#showCardFrontImg").css("display", "block");
	    	$("#showCardFrontImg").css('width', '200px important');
			$("#showCardFrontImg").css('height', '120px important');
	    	$("#progressid").css("display", "none");
	    }
	    
	    this.uploadFailed = function() {
	    	$("#progressid").css("display", "block");
	    	$("#progressid").html("<fmt:message key='msg.pg.workerOrderAdd.fileFail'/>");
	    }
	}
	
	function removeMsg(){
		$("#message").html("");
	}
	var message = {};
	message["zh_CN"] = {
		fileError : "文件类型不支持（jpg或png，最大2M）,请重新选择！"
	};
	message["zh_HK"] = {
		fileError : "檔類型不支持（jpg或png，最大2M）,請重新選擇！"
	};
	message["en_US"] = {
		fileError : "File type is not supported (jpg or PNG, maximum 2M).  Please rechoose! "
	};
	message["ja_JP"] = {
		fileError : "このファイルフォーマットは利用できません（jpgまたはpng、最大2M）、別のファイル形式を選択してください！"
	};
	message["ko_KR"] = {
		fileError : "파일 형식이 지원되지 않습니다 (jpg 또는 png, 최대 2M). 다시 선택하십시오!"
	};
	message["ru_RU"] = {
		fileError : "Тип файла не поддерживается (JPG или PNG, максимум 2M). Пожалуйста, выберите другой тип файла!"
	};
	var MSG = message[locale];
</script>
</html>
