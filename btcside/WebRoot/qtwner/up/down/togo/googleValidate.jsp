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
						<td width="16"></td>
						<td align="middle">
							<form action="qtner/bindGoogle.do" class="form-wrap"
								method="post" id="mobileform" name="mobileform">
								<p style="font-size:16px;font-weight:700;margin-bottom:15px;">
									1.
									<fmt:message key="msg.pg.userBindGoogle.note1" />
								</p>
								<div style="margin-bottom:15px;">
									<img
										src="<%=actionPath%>Captcha/google.do?email=${managerName}&secret=${secretKey}"
										width="200px" height="200px" />
									<p style="margin-top:5px">
										<fmt:message key="msg.pg.userBindGoogle.secretKey" />
										: <b class="sell">${secretKey}</b> <input type="hidden"
											id="secretKey" name="secretKey" value="${secretKey}" />
								</div>

								<div class="form-group" style="margin-bottom:15px;">
									<label for="" style="font-size:14px;"><fmt:message
											key="msg.pg.userBindGoogle.device" /></label> <input type="text"
										placeholder="<fmt:message key="msg.pg.userBindGoogle.inputLoginPwd"/>"
										readonly="readonly" value="Btcside:${managerName}"
										style="height: 20px;" /> <span class="error"></span>
								</div>
								<p style="font-size:16px;font-weight:700;margin-bottom:15px;">
									2.
									<fmt:message key="msg.pg.userBindGoogle.note2" />
								</p>
								<div class="form-group" style="margin-bottom:15px;">
									<label for="" style="font-size:14px;"><fmt:message
											key="msg.pg.userCenter.googleCode" /></label> <input type="text"
										class="form-control"
										placeholder="<fmt:message key="msg.pg.userCenter.entergoogleCode"/>"
										onblur="check()" onfocus="checkKong()" id="google" name="googleCode"
										style="height: 20px;" /> <span id="msg" style="color: red"></span>
								</div>
								<c:if></c:if>
								<div class="form-group-btn" style="margin-bottom:15px;">
									<button type="submit" class="btn btn-blue sub-btn">
										<fmt:message key="msg.pg.userBind.confirmBind" />
									</button>
								</div>
							</form>
						</td>
						<td width="16"></td>
					</tr>
				</table>
				<table cellspacing="0" cellpadding="0" width="468" border="0">
					<tr>
						<td><img height="16" src="<%=jspPath%>images/login_5.jpg"
							width="468"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
<script type="text/javascript">
	function check() {
		var msg = "${msg}";
		if (msg != null) {
			document.getElementById("msg").innerHTML = msg;
		}
	}
	function checkKong() {
		
		document.getElementById("msg").innerHTML = '';

	}
</script>
</html>
