<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-BTC转账</title>
		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<link href="<%=basePath%>common/validate/engine.css?<%=rjs%>"
			rel="stylesheet" type="text/css" media="screen" />
		<script type="text/javascript"
			src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript"
			src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
		<script type="text/javascript"
			src="<%=basePath%>common/validate/engine.js?<%=rjs%>"></script>
		<script type="text/javascript"
			src="<%=basePath%>common/validate/engine_zh_cn.js?<%=rjs%>"></script>
		<script type="text/javascript">     
		$(document).ready(function() {
			$("#mobileform").validationEngine( {
				validationEventTriggers : "blur", // 触发的事件 validationEventTriggers:"keyup blur",
				inlineValidation : true,// 是否即时验证，false为提交表单时验证,默认true
				success : false,// 为true时即使有不符合的也提交表单,false表示只有全部通过验证了才能提交表单,默认false
				promptPosition : "topRight"// 提示所在的位置，topLeft, topRight, bottomLeft, centerRight, bottomRight
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
					else
						document.forms.mobileform.btnCodeVoice.value = " 发送语音验证码 ";
				} else {
					document.forms.mobileform.btnCode.disabled = true;
					document.forms.mobileform.btnCodeVoice.disabled = true;
					wut = secs - num;
					if (vtype == 1) {
						document.forms.mobileform.btnCode.value = " 已经发送 (" + wut + ")...";
						document.forms.mobileform.btnCodeVoice.value = " 发送语音验证码 ";
					} else {
						document.forms.mobileform.btnCode.value = " 发送短信验证码";
						document.forms.mobileform.btnCodeVoice.value = " 已经发送 (" + wut + ")...";
					}
				}
			}
			function Timer() {
				document.forms.mobileform.btnCode.disabled = false;
				document.forms.mobileform.btnCodeVoice.disabled = false;
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
					<div class="tab">
						<a href="<%=basePath%>center/transferout.do">Btcside转BTC100</a>
						<a href="javascript:;" class="current">BTC100转Btcside</a>
					</div>
					<div class="box">
						<c:choose>
							<c:when test="${tstate=='1'}">
								<div class="success">
									转账成功
								</div>
							</c:when>
							<c:when test="${fn:length(tstate)>4}">							
								<div class="warning">
									${tstate}
								</div>
							</c:when>
						</c:choose>

						<div class="control">
							<div class="transfer-account">
								<div class="bfx">
									<span class="web-icon sprites-btc100"></span>
								</div>
								<div class="dircetion">
									<!-- 这里需要判断，以及箭头方向，sprites-arrow-rt是默认状态，切换时变成arrow-lt,文字也随之变化 -->
									<b>BTC100 转 Btcside</b>
									<p class="arrow sprites-arrow-rt" id="arrow"
										title="BTC100 转 Btcside"></p>
								</div>
								<div class="btc-100">
									<span class="web-icon sprites-btcq"></span>
								</div>
							</div>

							<form action="<%=basePath%>center/transferindo.do"
								class="form-wrap" method="post" name="mobileform"
								id="mobileform">
								<div class="form-group">
									<label for="">
										转账数额（BTC）
									</label>
									<input type="text"
										class="form-control validate[required,min[0.001]]"
										placeholder="请输转账数额" id="amount" name="amount" />
									<span class="error" id="error2"> </span>
								</div>

								<!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
								<div class="form-group">
									<label for="">
										资金密码（BTC100）
									</label>
									<input type="password"
										class="form-control validate[required,minSize[6]]"
										placeholder="请输资金密码" id="password" name="paypwd"
										maxlength="20" />
									<span class="error" id="error3"></span>
								</div>

								<c:if test="${safeType == 1 || safeType == 3}">
									<div class="form-group" id="telphoneNum">
										<label for="">
											手机验证码（BTC100）
										</label>
										<input type="text"
											class="form-control validate[required,minSize[6]]"
											placeholder="请输入手机验证码" id="code" name="telCode" />
										<span class="error" id="error1"></span>
										<div class="send-btn-box">
											<input type="button" class="btn send-btn" id="btnCode"
												name="btnCode" value="获取短信验证码" />
											<input type="button" class="btn send-btn" name="btnCodeVoice"
												id="btnCodeVoice" value="获取语音验证码" />
										</div>
									</div>
								</c:if>
								<c:if test="${safeType == 2 || safeType == 3}">
									<div class="form-group" id="googleNum">
										<label for="">
											谷歌验证码（BTC100）
										</label>
										<input type="hidden" name="secrety" id="secrety" value="" />
										<input type="text"
											class="form-control validate[required,minSize[6]]"
											placeholder="请输谷歌验证码" id="google" name="googleCode" />
										<span class="error" id="googleerror"></span>
									</div>
								</c:if>
								<div class="form-group-btn">
									<button type="submit" class="btn sub-btn">
										确认转账
									</button>
								</div>
							</form>
						</div>
					</div>
				</div>
				<div class="main-wrap">
					<div class="block-title">
						<h2 class="lt">
							<span></span>最近10笔转账记录
						</h2>
						<a href="<%=basePath%>center/transferMore.do?type=3" class="rt">查看更多</a>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>

									<th>
										订单号
									</th>
									<th>
										转账时间
									</th>
									<th>
										转账地址
									</th>
									<th>
										转账金额
									</th>
									<th>
										转账状态
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${transferList!=null}">
										<c:forEach var="rech" items="${transferList}">
											<tr>
												<td>
													<c:out value="${rech.record_id}" />
												</td>
												<td>

													<span><ut:formatDate dateline="${rech.dateline}"></ut:formatDate>
													</span>
												</td>
												<td>
													<c:out value="${rech.remark}" />
												</td>
												<td>
													<c:out value="${rech.amount}" />
												</td>
												<td>
													<c:choose>
														<c:when test="${rech.state==0}">未受理</c:when>
														<c:when test="${rech.state==1}">受理成功</c:when>
														<c:when test="${rech.state==2}">拒绝(<c:out
																value="${rech.remark}"></c:out>)</c:when>
													</c:choose>
												</td>

											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="5" height="30" align="center" bgcolor="#FFFFFF">
												您暂无历史记录。
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
		<script type="text/javascript">
			$(function(){
				$("#btnCode").click(function(){
					send(1);
					$.post("<%=basePath%>center/sendUserTel.do",{}, function(n){
						if(n == 1){
						//	alert("发送成功，请查收");
						} else {
							alert("发送失败，请稍候再试！");
						}
					}, "text");
				});
				
				$("#btnCodeVoice").click(function(){
					send(2);
					$.post("<%=basePath%>
	center/sendUserVoice.do", {}, function(n) {
				if (n == 1) {
					// alert("发送成功，请查收");
				} else {
					alert("发送失败，请稍候再试！");
				}
			}, "text");
		});
	});
</script>
	</body>

</html>
