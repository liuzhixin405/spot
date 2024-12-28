<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-资料审核</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<link href="<%=basePath%>common/validate/engine.css" rel="stylesheet" type="text/css" media="screen" />
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>

		<jsp:include page="_head.jsp"></jsp:include>

		<!-- 主体内容 -->
		<div class="container-trade-data">
			<div class="main-wrap">
				<div class="menu-title">
					<h3>
						<span class="icons-rzzl"></span>资料审核
					</h3>
				</div>
				<div class="box">
					<div class="freedom-broker">
						<p>
							<span class="jindu sprites-jindu1"></span>
						</p>
						<c:if test="${type == 0 }">
							<!-- 审核进行中 -->
							<div class="success" style="padding: 15px 0;">
								您的资料正在审核当中，请耐心等待。
							</div>
						</c:if>
						<c:if test="${type == 2 }">
							<!-- 审核未通过显示 -->
							<div class="no">
								<div class="warning" style="padding: 15px 0;">
									您的资料未完成审核，原因是：
									<b>照片不清晰</b>
								</div>
								<div class="form-group-btn">
									<span style="color:#f00;">资料审核失败,如果您想继续申请经纪人,</span> <a href="<%=basePath%>broker/backenter.do" class="btn sub-btn">返回上一步，重新提交资料</a>
								</div>
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>