<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.userFindPwd1.title"/> | <fmt:message key="msg.pg.systemName"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<!-- 主体内容 -->
		<div class="container-bg-none">
			<div class="login-box">
				<form class="form-wrap" action="<%=basePath%>password/findPwddo.do" method="post" onsubmit="return checkFindPwd();">
					<div class="form-title">
						<h2>
							<fmt:message key="msg.pg.userFindPwd1.title"/>
						</h2>
						<%-- <span><fmt:message key="msg.pg.login.noAccount"/>？<a href="<%=basePath%>register/register.do"><fmt:message key="msg.pg.login.goResiter"/></a>
						</span> --%>
					</div>
				
					<div class="form-group email-select">
						<%-- <label for="">
							<fmt:message key="msg.pg.userFindPwd1.email"/>
						</label> --%>
						<input type="text" placeholder="<fmt:message key="msg.pg.index.loginEmail"/>" id="email" name="email" class="form-control inputElem"/>
						<span class="error" id="emailerror">${error}</span>
					</div>
					<div class="form-group-btn">
						<input type="submit" id="find_btn" class="btn sub-btn sub-btn-click" value="<fmt:message key="msg.pg.userFindPwd1.next"/>"></input>
					</div>
				</form>
			</div>
		</div>
		<div class="clear"></div>
	<jsp:include page="_footer.jsp"></jsp:include>
	<script>
	var bol_email=false;

	function checkFindPwd(){
		var Email = $('#email').val();
		if(Email == ""){
			$("#emailerror").html("邮箱/手机号不能为空");
			bol_email = false;
			return false;
		} else {
			bol_email = true;
			return true;
		}
	}
	</script>
	</body>
</html>
