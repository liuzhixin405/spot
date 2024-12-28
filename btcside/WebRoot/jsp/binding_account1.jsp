<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>绑定BTC100账户</title>
		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript">
			var google_bol = false;
			var code_bol = false;
		
			// 验证google验证码
			function checkGoogle() {
				var code = $("#google").val();
				var unLen = code.replace(/[^\x00-\xff]/g, "**").length;
				if (unLen == 0) {
					document.getElementById("googleerror").style.color = "red";
					$("#googleerror").html("验证码不能为空");
					google_bol = false;
					return false;
				}
				google_bol = true;
				return true;
			}
	
			// 验证手机验证码
			function checkCode() {
				var code = $("#code").val();
				var unLen = code.replace(/[^\x00-\xff]/g, "**").length;
				if (unLen == 0) {
					document.getElementById("error1").style.color = "red";
					$("#error1").html("验证码不能为空");
					code_bol = false;
					return false;
				}
				code_bol = true;
				return true;
			}
		</script>
		<script type="text/javascript">
			$(function(){
				$("#btnCode").click(function(){
					$.post("<%=basePath%>center/sendOldTelCode.do",{"username" : $('#username').val(),"password" : $('#password').val()}, function(n){
						if(n == 1){
						//	alert("发送成功，请查收");
						} else {
							alert("发送失败，请稍候再试！");
						}
					}, "text");
				});
			});
		</script>
		<script type="text/javascript">
			var secs = 60;
			var wait = secs * 1000;
			var vtype = 1;
			function send(type) {
				vtype = type;
				for (i = 1; i <= secs; i++) {
					window.setTimeout("doUpdate(" + i + ")", i * 1000);
				}
				window.setTimeout("Timer()", wait);
			}
			function doUpdate(num) {
				if (num == '60') {
					if (vtype == 1)
						document.forms.mobileform.btnCode.value = " 发送短信验证码 ";
				} else {
					document.forms.mobileform.btnCode.disabled = true;
					wut = secs - num;
					if (vtype == 1) {
						document.forms.mobileform.btnCode.value = " 已经发送 (" + wut + ")...";
					} else {
						document.forms.mobileform.btnCode.value = " 发送短信验证码";
					}
				}
			}
			function Timer() {
				document.forms.mobileform.btnCode.disabled = false;
			}
		</script>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2>
							绑定BTC100账户
						</h2>
						<em>&nbsp;&nbsp;&nbsp;&nbsp;实现BTC100现货和Btcside的一键转账</em>
					</div>
					<div class="box">
						<div class="control">
							<!--提示框,成功是class是success,失败时class是warning;-->
							<div class="alert" style="display: ${msg eq null ? 'none' : 'block'}">
								<div class="warning">
									${msg }
								</div>
							</div>
							<form action="<%=basePath %>center/transferBind.do" class="form-wrap" id="mobileform" method="post" >
								<c:if test="${safeType == 1 || safeType == 3}">
									<div class="form-group" id="telphoneNum">
										<label for="">
											手机验证码
										</label>
										<input type="text" class="form-control" placeholder="请输入手机验证码" id="code" name="telCode" onblur="checkCode();"/>
										<span class="error" id="error1"></span>
										<div class="send-btn-box">
											<input type="button" class="btn send-btn" onclick="send(1)" id="btnCode" name="btnCode" value="获取短信验证码" />
										</div>
									</div>
								</c:if>
								<c:if test="${safeType == 2 || safeType == 3}">
									<div class="form-group" id="googleNum">
										<label for="">
											谷歌验证码
										</label>
										<input type="hidden" name="secrety" id="secrety" value="" />
										<input type="text" class="form-control" placeholder="请输谷歌验证码" onblur="checkGoogle();" id="google" name="googleCode" />
										<span class="error" id="googleerror"></span>
									</div>
								</c:if>
								<input type="hidden" name="username" value="${username }" id="username">
								<input type="hidden" name="password" value="${password }" id="password">
								<input type="hidden" name="safeType" value="${safeType }">
								<div class="form-group-btn">
									<button type="submit" class="btn sub-btn">
										确认绑定
									</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="clear"></div>
		<jsp:include page="_footer.jsp"></jsp:include>
		
	</body>
</html>
