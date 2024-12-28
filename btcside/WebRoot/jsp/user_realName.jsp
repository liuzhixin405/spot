<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userSetTradePwd.title"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath %>common/css/style.css?<%=rjs%>" />

		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js"
			type="text/javascript"></script>
			<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>

	</head>

	<body>
		<c:if test="${noCertificate}">
    	<div class="add-windows" id="alert-pwd">
    		<div id="tb-overlay-duiping" class="tb-overlay"></div>
    		<div class='add-wrap alert-wrap' style='display: block;' id='invite-BFX'>
				<div class='dialogs-title'>
					<h2 class='lt'>
						<fmt:message key="msg.pg.userHon.tip"/>
					</h2>
					<a class='close-icon icons-close' id='close-alert' href='javascript:closealert()'></a>
				</div>
				<div class='dialogs-body'>
					<p style='text-align: center; font-size: 16px; margin-bottom: 30px;'><fmt:message key="msg.pg.userHon.getCert"/></p>
					<div class='form-group-btn text-center'>"
						<a class='btn sub-btn center' style='margin-right: 0px;' href='javascript:closealert();'><fmt:message key="msg.pg.userHon.sure"/></a>
					</div>
				</div>
			</div>
    	</div>
    	</c:if>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->

		<div class="container">
			
			<jsp:include page="_user_left.jsp"></jsp:include>
			<c:if test="${noRealName}">
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userHon.writerealName"/>
						</h2>
					</div>
					<div class="box">
						<div class="control">
							<form method="post" action="<%=basePath%>center/getRealName.do" class="form-wrap" 
							      id="mobileform"
								name="mobileform">
								<!--提示框,成功是class是success,失败时class是warning;-->
								<c:if test="${success!=null&&success!=''}">
								<div class="success">
									${success}
								</div>
							</c:if>
							<c:if test="${error!=null&&error!=''}">
								<div class="warning">
									${error}
								</div>
							</c:if>
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userHon.realName"/>
									</label>
									<input type="text" class="form-control" placeholder="<fmt:message key="msg.pg.userHon.writerealName"/>" id="realName"
										name="realName" onblur="cheakrealName()" />
									<span class="error" id="error2"></span>
									<span><fmt:message key="msg.pg.userHon.realNamenotchange"/></span>

								</div>
								<div class="form-group">
									<label for="">
										<fmt:message key="msg.pg.userHon.chifmrealName"/>
									</label>
									<input type="text" class="form-control" placeholder="<fmt:message key="msg.pg.userHon.chifmrealNameAgain"/>" id="realName2"
										name="realName2" onblur="cheakrealName2()" />
									<span class="error" id="error3"></span>
								</div>
								<div class="form-group-btn">
									<button type="submit" class="btn sub-btn" onclick="return realNameFit();"><fmt:message key="msg.pg.userCenter.confirmSubmit"/></button>
								</div>
							</form>
						</div>

					</div>
				</div>

			</div>
			</c:if>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript">
	var message = {};
	var realName1_bol = false;
	var realName2_bol = false;
	message["zh_CN"] = {
		warmTip : "温馨提示",
		sureTip : "确定",
		realNameNull : "真实姓名不能为空",
		realNameChar : "名字不能含有字符",
		realNameAvailable : "真实姓名可用",
		confirmrealNameNull : "确认姓名不能为空",
		confirmrealNameRight : "确认姓名正确",
		twicerealNameNotEq : "俩次姓名不一致",
	};
	message["zh_HK"] = {
		warmTip : "溫馨提示",
		sureTip : "確定",
		realNameNull : "真實姓名不能為空",
		realNameChar : "名字不能含有字元",
		realNameAvailable : "真實姓名可用",
		confirmrealNameNull : "確認姓名不能為空",
		confirmrealNameRight : "確認姓名正確",
		twicerealNameNotEq : "倆次姓名不一致",
	};
	message["en_US"] = {
		warmTip : "Warm prompt",
		sureTip : "Sure",
		realNameNull: "Real names cannot be empty",
		realNameChar: "The name cannot contain characters",
		realNameAvailable : "Real name available",
		confirmrealNameNull : "Verify that the name cannot be empty",
		confirmrealNameRight : "Verify the name is correct",
		twicerealNameNotEq : "Two names are inconsistent",
	};
	message["ja_JP"] = {
		warmTip: "ヒント",
		sureTip: "確定",
		realNameNull : "本名は空欄にすることはできません",
		realNameChar : "本名は空欄にすることはできません",
		realNameAvailable : "本名の利用は可能です",
		confirmrealNameNull : "本名は空欄にすることはできません",
		confirmrealNameRight : "確認用名前は正しくありません",
		twicerealNameNotEq : "本名は空欄にすることはできません",
	};
	message["ko_KR"] = {
		warmTip : "팁",
		sureTip : "결정",
		realNameNull : "실명은 비워 둘 수 없습니다",
		realNameChar : "이름은 문자를 포함 할 수 없습니다",
		realNameAvailable : "실명 사용 가능",
		confirmrealNameNull : "이름을 비워 둘 수 없는지 확인",
		confirmrealNameRight : "이름이 정확한지 확인하십시오",
		twicerealNameNotEq : "두 이름이 일치하지 않습니다",
	};
	message["ru_RU"] = {
		warmTip : "подсказка",
		sureTip : "Конечно",
		realNameNull: "Реальные имена не могут быть пустыми",
		realNameChar: "Имя не может содержать символы",
		realNameAvailable : "Настоящее имя доступны",
		confirmrealNameNull : "Убедитесь, что имя не пустое",
		confirmrealNameRight : "Проверьте правильность имени",
		twicerealNameNotEq : "Два имени не соответствуют",
	};
	var MSG = message[locale];
	
	function closealert() {
		window.history.back();	
	}
	
	//检查真实姓名
	function cheakrealName() {
		var realName = $("#realName").val();
		var regs = /[@#\$%\^&\*?!,.<>{}]+/g;
		var unLen = realName.length;
		if (realName.trim() == '') {
			document.getElementById("error2").style.color = "red";
			$("#error2").html(MSG['realNameNull']);
			realName1_bol1 = false;
			return false;
		}
		if (regs.test(realName)) {
			document.getElementById("error2").style.color = "red";
			$("#error2").html(MSG['realNameChar']);
			realName1_bol1 = false;
			return false;
		}
		
		document.getElementById("error2").style.color = "green";
		$("#error2").html(MSG['realNameAvailable']);
		realName1_bol1 = true;
		return true;
	}
	//确认真实姓名
	function cheakrealName2() {
		var realName1 = $("#realName").val();
		var realName2 = $("#realName2").val();
		
		if(realName2 == ""){
			document.getElementById("error3").style.color = "red";
			$("#error3").html(MSG['confirmrealNameNull']);
			realName2_bol1 = false;
			return false;
		}
		if (realName1 == realName2) {
			document.getElementById("error3").style.color = "green";
			$("#error3").html(MSG['confirmrealNameRight']);
			realName2_bol1 = true;
			return true;
		} else {
			document.getElementById("error3").style.color = "red";
			$("#error3").html(MSG['twicerealNameNotEq']);
			realName2_bol1 = false;
			return false;
		}
	}
	
	function realNameFit() {
		if(realName2_bol1 && realName1_bol1) {
			return ture;
		}
		return false;	
	}
</script>
</html>
