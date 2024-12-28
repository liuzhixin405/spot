<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-交易API</title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>" />
			<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
			<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>


	</head>
	<body>
		
			<div class="add-windows" id="add-windows">
			<div id="tb-overlay"></div>
			<input type="hidden" name="error" id="error" value="${err}"/>
			<div class="add-wrap">
				<div class="block-title">
					<h2 class="lt">
						为了您的账号安全，请在该操作前进行安全设置(绑定手机或谷歌验证)!
					</h2>
				</div>
				<div class="main-wrap">
				<a href="<%=basePath%>center/center.do" class="btn sub-btn">进入安全设置</a>
				<a href="<%=basePath%>center/center.do" class="btn sub-btn-return">返回用户中心</a>
				</div>
			</div>
		</div>
	
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<!-- 主体内容 -->
		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			
			<div class="main-rt-box">
				<div class="main-wrap">
				
					<div class="block-title">
						<h2>
							交易API
						</h2>
					</div>
					<div class="box">
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
						<div class="box-lt lt">
							<h4>
								Btcside交易API
							</h4>
							<p>
								Btcside交易API是Btcside针对用户提供的自动交易接入方案。
								API让您可以快速的获取当前市场最新行情及时的下单交易、查询自己可用和冻结金额、查询自己当前尚未成交的挂单、买进或卖出、撤单。
							</p>
							<p>
								<c:if test="${open==1}">
									<a href="javascript:void(0);" class="lt btn btn-blue" onclick="submitaction('tradegotapi');">获取我的API</a>
								</c:if>
								<c:if test="${show==1}">
									<a href="javascript:void(0);" class="lt btn btn-blue" onclick="submitaction('tradegotapi');">查看我的开发者秘钥</a>
								</c:if>
							</p>
							<a href="" style="margin-left: 15px; color: #333;">查看交易API使用文档</a>
						</div>
						<div class="box-rt rt">
							 <span class="api-icon sprites-api"></span>
						</div>
					</div>
					<form action="<%=basePath%>center/openapi.do" class="form-wrap" name="mobileform" id="mobileform" method="post">
					<div class="box">
						
							<div class="form-group" style="${tel==1? '' : 'display:none' }">
								<label for="">
									手机验证码
									<input type="hidden" name="mobile" id="mobile"
										value="${mobile}" />
								</label>
								<input type="text" class="form-control" placeholder="请输入验证码"
									onblur="checkCode();" id="code" name="code" />
								<span class="error" id="error1"></span>
								<div class="send-btn-box">
									<input type="button" class="btn send-btn" onclick="send(1)" id="btnCode" name="btnCode" value="获取短信验证码"/>
								</div>
							</div>
							<!-- 谷歌验证码，如果开启双重验证就显示，默认不显示-->
							<div class="form-group" style="${google==2 ? '' : 'display:none' }">
								<label for="">
									谷歌验证码<input type="hidden" name="secrec" id="secrec"
										value="${secrec}" />
								</label>
								<input type="text"  class="form-control" placeholder="请输入谷歌验证码"
									onblur="checkGoogle();" id="google" name="google" />
								<span class="error" id="googleerror"></span>
							</div>
							
							<div class="form-group-btn" style="${but==1? '' : 'display:none' }">
									<button type="submit" class="btn sub-btn">确认</button>														
							</div>
					</div>
					<c:if test="${openapi==1}">
						<table>
							<tr>
								<td align="left" valign="middle" colspan="4" bgcolor="#FFFFFF"
									height="50px" style="padding-left: 50px">
									请不要将API的partner和secretKey告诉任何人,包括Btcside的客服。
								</td>
							</tr>

							<tr>
								<td colspan="4" align="left" valign="middle" bgcolor="#FFFFFF"
									height="50px" style="padding-left: 50px">
									partner:&nbsp;
									<font color="red">${spartner}</font>
								</td>
							</tr>
							<tr>
								<td colspan="4" align="left" valign="middle" bgcolor="#FFFFFF"
									height="50px" style="padding-left: 50px">
									secretKey:&nbsp;
									<font color="red">${secretKey}</font>
								</td>
							</tr>
						</table>
					</c:if>
					</form>
				</div>
			</div>
		</div>
				<jsp:include page="_footer.jsp"></jsp:include>
			<script type="text/javascript">
		 $(window).load(function() {
		 	var err=$("#error").val();
		 	if(err==1){
		 	    $("#add-windows").show();
		 	 }else{		 	 
		        $("#add-windows").hide();
		      }
	
		  })
		</script>
		<script type="text/javascript">
			function submitaction(msg){				
						document.mobileform.action="<%=basePath%>center/"+msg+".do";
						document.mobileform.submit();
						return true;
				
			}
		</script>
		<script type="text/javascript">
			$(function(){
				$("#btnCode").click(function(){
					var tel = $("#mobile").val();
					$.post("<%=basePath%>center/sendOldTelCode.do",{Tel:tel}, function(n){
						if(n == "success"){
						//	alert("发送成功，请查收");
						} else {
							alert("发送失败，请稍候再试！");
						}
					}, "text");
				});
	});

	var secs = 60;
	var wait = secs * 1000;
	var vtype = 1;
	function send(type) {
		var tel = document.forms.mobileform.mobile.value;
		vtype = type;
		if (tel != "") {
			for (i = 1; i <= secs; i++) {
				window.setTimeout("doUpdate(" + i + ")", i * 1000);
			}
			window.setTimeout("Timer()", wait);
		}
	}
	function doUpdate(num) {
		if (num == '60') {
			if (vtype == 1)
				document.forms.mobileform.btnCode.value = " 发送短信验证码 ";
		} else {
			document.forms.mobileform.btnCode.disabled = true;
			wut = secs - num;
			if (vtype == 1) {
				document.forms.mobileform.btnCode.value = " 已经发送 (" + wut
						+ ")...";
			} else {
				document.forms.mobileform.btnCode.value = " 发送短信验证码";
			}
		}
	}
	function Timer() {
		document.forms.mobileform.btnCode.disabled = false;
	}
</script>
	</body>
</html>
