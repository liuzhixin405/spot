<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-认证通过</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<link href="<%=basePath%>common/validate/engine.css" rel="stylesheet" type="text/css" media="screen" />
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>

		<jsp:include page="_head.jsp"></jsp:include>
		
		<!-- 主体内容 -->
		<div class="container-trade-data">
			<div class="main-wrap min-height">
				<div class="menu-title">
					<h3>
						<span class="icons-rzzl"></span>认证通过
					</h3>
				</div>
				<div class="box">
					<div class="freedom-broker">
						<p>
							<span class="jindu sprites-jindu2"></span>
						</p>
						<!--提示框,成功是class是success,失败时class是warning;-->
						<div class="success" style="padding: 15px 0;">
							恭喜您！您的资料已经审核通过，您现在是Btcside的自由经纪人啦，快去推广您的客户赚取佣金吧！
						</div>
					</div>
					<div class="form-group-btn">
						<a href="<%=basePath %>center/invite.do" class="btn sub-btn">前往客户管理</a>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>
