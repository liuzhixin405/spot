<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-认证资料</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<link href="<%=basePath%>common/validate/engine.css" rel="stylesheet" type="text/css" media="screen" />
		
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		
		<script type="text/javascript" src="<%=basePath%>common/js/checkBroker.js?<%=rjs%>"></script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>

		<jsp:include page="_head.jsp"></jsp:include>
		
		<!-- 主体内容 -->
		<div class="container-trade-data">
			<div class="main-wrap">
				<div class="menu-title">
					<h3>
						<span class="icons-rzzl"></span>认证资料
					</h3>
				</div>
				<div class="box">

					<div class="freedom-broker">
						<p>
							<span class="jindu sprites-jindu"></span>
						</p>
						<form action="judge.do" class="form-wrap" method="post" enctype="multipart/form-data" onsubmit="return checkFlag();">
							<div class="form-group">
								<label for="">
									您的姓名
								</label>
								<input type="text"  class="form-control" placeholder="请输入您的姓名" id="userName" onblur="checkUserName_blur();checkUserName_keydown();" name="userName" maxlength="20">
								<span id="validateName"></span>
							</div>
							<div class="form-group">
								<label for="">
									身份证号
								</label>
								<input type="text"  class="form-control" placeholder="请输入您的身份证号" onblur="checkUserNo();" id="userNo" name="userNo" maxlength="50">
								<span id="validateNo"> </span>
							</div>
							<div class="form-group">
								<label for="">
									手机号码
								</label>
								<input type="text"  class="form-control" placeholder="请输入您的手机号" onblur="checkUserTel();" id="userTel" name="userTel" maxlength="20">
								<span id="validateTelephone"></span>
								<span>请保持手机畅通，认证过程中会与您联系。</span>
							</div>
							<div class="form-group">
								<label for="">
									通讯地址（选填）
								</label>
								<input type="text"  class="form-control" placeholder="请输入您通讯地址" id="userAddr" name="userAddr" maxlength="40">
								<span class="error"></span>
							</div>
							<div class="file">
								<label for="">
									手持身份证照片（上传图片最大支持1024KB，图片格式支持jpg，jpeg，png）
								</label>
								<input type="file" name="myfiles"/>
								<span class="error">验证码错误，请重新输入。</span>
								<span>五官可见 证件信息完整清晰无遮挡 </span>
							</div>
							<div class="file">
								<label for="">
									身份证（正面）（上传图片最大支持1024KB，图片格式支持jpg，jpeg，png）
								</label>
								<input type="file" name="myfiles"/>
								<span class="error">验证码错误，请重新输入。</span>
								<span>证件信息完整清晰可识别 </span>
							</div>
							<div class="file">
								<label for="">
									身份证（反面）（上传图片最大支持1024KB，图片格式支持jpg，jpeg，png）
								</label>
								<input type="file" name="myfiles"/>
								<span class="error">验证码错误，请重新输入。</span>
								<span>有效期在一个月以上 </span>
							</div>
							<div class="form-group-btn">
								<input type="submit" class="btn sub-btn" value="已完成，提交资料"></input>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>
