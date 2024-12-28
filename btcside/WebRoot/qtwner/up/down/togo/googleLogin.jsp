<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="jsp/_basepage.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<base href="<%=actionPath%>">
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>管理平台</title>
<link href="<%=jspPath%>css/admin.css" type="text/css" rel="stylesheet">
</head>
<body onload="document.form1.name.focus();"
	style="background-color: #002779">
	<div></div>
	<table height="100%" style="height: 100%; margin-top: 100px"
		cellspacing="0" cellpadding="0" width="100%" bgcolor="#002779"
		border="0">
		<tr>
			<td align="middle">
				<table cellspacing="0" cellpadding="0" width="468" border="0">
					<tr>
						<td><img height="23" src="<%=jspPath%>images/login_1.jpg"
							width="468"></td>
					</tr>
					<tr>
						<td><img height="147" src="<%=jspPath%>images/login_2.jpg"
							width="468"></td>
					</tr>
				</table>
				<table cellspacing="0" cellpadding="0" width="468" bgcolor="#ffffff"
					border="0">
					<tr>
						<td width="16"><img height="122"
							src="<%=jspPath%>images/login_3.jpg" width="16"></td>
						<td align="middle">
							<form name="form1" action="qtner/googleLogin.do" method="post">
								<table cellspacing="0" cellpadding="0" width="230" border="0">
									<tr height="5">
										<td width="5"></td>
										<td width="56"></td>
										<td></td>
									</tr>
									<tr height="36">
										<td></td>
										<td width="65">谷歌验证码</td>
										<td><input
											style="border-right: #000000 1px solid; border-top: #000000 1px solid; border-left: #000000 1px solid; border-bottom: #000000 1px solid;width: 160px;"
											maxlength="30" size="24" value="" name="googleCode" onfocus="removeMsg()">
										</td>
									</tr>
									<tr height="5">
										<td colspan="3"></td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td><input type="image" height="18" width="70"
											src="<%=jspPath%>images/bt_login.gif"></td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>
											<span id="msg" style="color: red">${msg }</span>
										</td>
										</tr>
								</table>
							</form>
						</td>
						<td width="16"><img height="122"
							src="<%=jspPath%>images/login_4.jpg" width="16"></td>
					</tr>
				</table>
				<table cellspacing="0" cellpadding="0" width="468" border="0">
					<tr>
						<td><img height="16" src="<%=jspPath%>images/login_5.jpg"
							width="468"></td>
					</tr>
				</table>
<%--				<table cellspacing="0" cellpadding="0" width="468" border="0">--%>
<%--					<tr>--%>
<%--						<td align="right"><a href="<%=actionPath%>" target="_blank"><img--%>
<%--								height=26 src="<%=actionPath%>website-logo.png" width="165" border="0">--%>
<%--						</a></td>--%>
<%--					</tr>--%>
<%--				</table>--%>
			</td>
		</tr>
	</table>
</body>
<script type="text/javascript">
	function removeMsg() {
		document.getElementById("msg").innerHTML = '';
	}
</script>
</html>
