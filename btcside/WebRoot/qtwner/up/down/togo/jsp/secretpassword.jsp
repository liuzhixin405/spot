<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.btc100.btc_q.common.UrlConfig"%>
<%
	String scheme = request.getScheme();
	String scheme1 = request.getHeader("X-Forwarded-Proto");
	if (scheme1 != null && scheme1.equals("https")) {
		scheme = "https";
	}
	String port = "";
	if (scheme.equals("http")) {
		port = ":" + request.getServerPort();
	}
	String basePath = scheme + "://" + request.getServerName() + port + "/";
%>
<!DOCTYPE HTML>
<html>
	<head>
		<title>密码填写</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=7">
		<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 8px;
	font-size: 12px;
	font-family: Arial, Helvetica, sans-serif;
	background-color: #ffffff;
}

h1,h2,h3,h4,h5,form,p,ul {
	margin: 0px;
	padding: 0px;
}

.msg a {
	font-size: 16px;
	font-weight: 700;
	text-decoration: none;
	font-family: 微软雅黑, 宋体;
	color: #00CC33;
}

.error a {
	font-size: 16px;
	font-weight: 700;
	text-decoration: none;
	font-family: 微软雅黑, 宋体;
	color: #FF0000;
}

.message {
	font-size: 12px;
	margin-top: 2px;
	line-height: 200%;
	text-align: center;
}

.shadow {
	-moz-box-shadow: 3px 3px 5px #44A8D1;
	-webkit-box-shadow: 3px 3px 5px #44A8D1;
	box-shadow: 3px 3px 5px #44A8D1; /* For IE 8 */
	-ms-filter:
		"progid:DXImageTransform.Microsoft.Shadow(Strength=4, Direction=135, Color='#44A8D1')"
		; /* For IE 5.5 - 7 */
	filter: progid : DXImageTransform.Microsoft.Shadow ( Strength = 4,
		Direction = 135, Color = '#44A8D1' );
}
-->
</style>
	</head>
	<body>
		<table width="600" border="0" align="center" cellpadding="6" cellspacing="0" 
			style="font-size: 14px; margin-top: 160px; border: 1px #44A8D1 solid; margin-bottom: 100px;" class="shadow">
			<tr>
				<td bgcolor="#44A8D1" style="color: #FFF;">
					<strong>密码填写：</strong>
				</td>
			</tr>
			<tr>
				<td height="22" bgcolor="#FFFFFF">
					<form action="" method="post">
						<table width="100%" border="0" align="center" cellpadding="0" cellspacing="20">
							<tr>
								<td style="width: 60px;">
									<img src="<%=basePath%>error/images/right.png"></img>
								</td>
								<td>
									<div class="message">
										<div class="msg">
											<input type="password" value="******" name="password" id="password">
										</div>
									</div>
								</td>
								<td>
									<input type="submit" class="button" value="确 定">
								</td>
							</tr>
						</table>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>
