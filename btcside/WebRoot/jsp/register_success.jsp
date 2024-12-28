<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.registerSuccess.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>
	<body>
		<div class="add-windows" id="alert-windows"></div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->

		<div class="container-bg-none">
			<div class="login-box">
				<!--<form action="" class="form-wrap">
					--><div class="form-title">
						<h2>
							<fmt:message key="msg.pg.registerSuccess.title"/>
						</h2>
						<span><fmt:message key="msg.pg.registerSuccess.note1"/>？<a href="<%=basePath%>register/register.do"><fmt:message key="msg.pg.registerSuccess.note2"/></a> </span>
					</div>

					<div class="form-group">
						<p style="line-height: 1.5em;">
							<fmt:message key="msg.pg.userFindLoginPwdSuccess.note1"/>
							<br />
							<fmt:message key="msg.pg.userFindLoginPwdSuccess.note2"/>：
							<b>${email}</b> <fmt:message key="msg.pg.userFindLoginPwdSuccess.note3"/>
						</p>
					</div>

					<div class="form-group-btn">
						<input type="submit" value="<fmt:message key="msg.pg.userFindLoginPwdSuccess.note4"/>" class="btn sub-btn sub-btn-click" onclick="navige('${inemail}',true);"/>
						<p class="sendAngin"><fmt:message key="msg.pg.userFindLoginPwdSuccess.note6"/><a href="javascript:void(0)" onclick="post('<%=basePath%>register/resend.do');"><fmt:message key="msg.pg.userFindLoginPwdSuccess.note7"/></a></p>
					</div>
				<!--</form>
			--></div>

		</div>

		<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript">

	function navige(url, newwindow) {
		if (newwindow){
			window.open(url);
		}else{
			document.location.href = url;
		}
	}
	function post(url) {
		$.post(url, null, function(data) {
			if (data == 1)
				tipAlert(MSG['findPwdsendSuccess']);
			else
				tipAlert(MSG['findPwdsendFail']);
		}, "text");
	}
	
	var message = {};
	message["zh_CN"] = {
		findPwdsendSuccess : "发送成功",
		findPwdsendFail : "超时，请重新提交！",
		warmTip : "温馨提示",
		sureTip : "确定",
	};
	message["zh_HK"] = {
		findPwdsendSuccess : "發送成功",
		findPwdsendFail : "超時，請重新提交！",
		warmTip : "溫馨提示",
		sureTip : "確定",
	};
	message["en_US"] = {
		findPwdsendSuccess : "send successfully",
		findPwdsendFail : "Time out, please re-submit!",
		warmTip : "Warm prompt",
		sureTip : "Sure"	
	};
	message["ja_JP"] = {
		findPwdsendSuccess : "送信成功",
		findPwdsendFail : "タイムアウトしました、再送信してください！",
		warmTip : "ヒント",
		sureTip : "確定",
	};
	message["ko_KR"] = {
		findPwdsendSuccess : "성공적으로 보냈습니다",
		findPwdsendFail : "시간 종료, 다시 제출하십시오!",
		warmTip : "팁",
		sureTip : "결정",
	};
	message["ru_RU"] = {
		findPwdsendSuccess : "успешно отправлено",
		findPwdsendFail : " Тайм-аут, пожалуйста, отправьте повторно!",
		warmTip : " Важное напоминание",
		sureTip : "Конечно"
	};
	var MSG = message[locale];
	</script>
	</body>
</html>
