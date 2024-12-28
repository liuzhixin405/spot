<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.registerSuccess.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->

		<div class="container-bg-none">
			<div class="login-box">
				<table width="95%" border="0" align="center" cellpadding="0"
					cellspacing="5">
					<tr>
						<td height="190" valign="top" class="tit6">
							<input id="loginurl" type="hidden" value="<%=loginPath%>/login.do" />
							<p class="mt10">
								<fmt:message key="msg.pg.registerSuccess.beBTCQMember"/>！
							</p>
							<br />
							<a href="<%=loginPath%>/login.do"> <fmt:message key="msg.pg.registerSuccess.LikeLogin"/> </a>

							<div id="redirectionMsg"
								style="color: #999999; margin: 12px 0px;">
								<a href="<%=loginPath%>/login.do" style="color: #000000; text-decoration: none;"> <span id="spanSeconds" style="color: #009900; font-size: 15px;">5</span>&nbsp;<fmt:message key="msg.pg.registerSuccess.waitLogin"/>...
								</a>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div class="clear"></div>

		<jsp:include page="_footer.jsp"></jsp:include>
		
		<script type="text/javascript">
			var seconds = 5;
			var defaultUrl = document.getElementById("loginurl").value;
			var iID;
			onload = function() {
				if (defaultUrl == 'javascript:history.go(-1)'
						&& window.history.length == 0) {
					document.getElementById('redirectionMsg').innerHTML = '';
					return;
				}
		
				iID = window.setInterval(redirection, 1000);
			}
			function redirection() {
				if (seconds <= 0) {
					window.clearInterval(iID);
					return;
				}
		
				seconds--;
				document.getElementById('spanSeconds').innerHTML = seconds;
		
				if (seconds == 0) {
					window.clearInterval(iID);
					location.href = defaultUrl;
				}
			}
		</script>
	</body>
	
</html>
