<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userFindLoginPwdSuccess.title"/></title>
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
							<fmt:message key="msg.pg.userFindLoginPwdSuccess.title"/>
						</h2>
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
						<button type="button" class="btn sub-btn sub-btn-click" onclick="navige('${inemail}',true);"><fmt:message key="msg.pg.userFindLoginPwdSuccess.note4"/></button>
						<p class="sendAngin"><fmt:message key="msg.pg.userFindLoginPwdSuccess.note6"/>
							<a href="javascript:void(0)" onclick="post('<%=basePath%>password/resend.do');"><fmt:message key="msg.pg.userFindLoginPwdSuccess.note7"/></a>
						</p>
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
				tipAlert("<fmt:message key='msg.pg.userFindPwdSuccess.sendSuccess'/>");
			else
				tipAlert("<fmt:message key='msg.pg.userFindPwdSuccess.sendFail'/>");
		}, "text");
	}
	
	var message = {};
	message["zh_CN"] = {
		warmTip : "温馨提示",
		sureTip : "确定"
	};
	message["zh_HK"] = {
		warmTip : "溫馨提示",
		sureTip : "確定"
	};
	message["en_US"] = {
		warmTip : "Warm prompt",
		sureTip : "Sure"
	};
	message["ja_JP"] = {
		warmTip: "ヒント",
		sureTip: "確定"
	};
	message["ko_KR"] = {
		warmTip : "팁",
		sureTip : "결정"
	};
	var MSG = message[locale];
	</script>
	</body>
</html>
