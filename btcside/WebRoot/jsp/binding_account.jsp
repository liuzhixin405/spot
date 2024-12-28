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
						<div class="alert" style="display: ${msg eq null ? 'none' : 'block'}">
							<div class="warning">
								${msg }
							</div>
						</div>
						<div class="control">
							<form action="<%=basePath %>center/usersafe.do" class="form-wrap" method="post" autocomplete="off">
								<div class="form-group">
									<label for="">
										账户名
									</label>
									<input type="text" class="form-control" placeholder="请输入账户名" name="username" id="username" />
									<span class="error" id="errorname"></span>
								</div>
								<div class="form-group">
									<label for="">
										登录密码
									</label>
									<input type="password" class="form-control" placeholder="请输登录密码" name="password" id="password" />
									<span class="error" id="errorpwd"></span>
								</div>
								<div class="form-group-btn">
									<button type="submit" class="btn sub-btn">
										下一步
									</button>
									<a href="https://www.coinbankotc.com/toBuyBtc?currency=USDT">没有BTC100账号？去注册</a>
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
