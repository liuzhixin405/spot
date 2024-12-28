<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>工单详情</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" src="<%=FileUpload%>js/jquery.1.7.1.min.js?"></script>
	</head>
<body>
	<div id="main-content">
		<div class="clear"></div>
		<!-- End .clear -->
		<div class="content-box">
			<div class="content-box-header">
				<h3>
					工单查看/回复
				</h3>
				<div class="clear"></div>
			</div>
			
			<div class="content-box-content">
				<div class="container online-server">
					<div class="bkg-white order-intrd">
						<div class="order-auther">
							<span>工单编号：</span><span class="order-content">${order.f_order_code}</span>
						</div>
						<div class="order-auther">
							<span>工单类型：</span>
							<span class="order-content">
								<c:choose>
									<c:when test="${order.f_order_type == 'ACCOUNT'}">账户</c:when>
									<c:when test="${order.f_order_type == 'TRADE'}">交易</c:when>
									<c:when test="${order.f_order_type == 'COMPLAIN'}">投诉</c:when>
									<c:when test="${order.f_order_type == 'RW'}">充提</c:when>
									<c:when test="${order.f_order_type == 'COOP'}">合作</c:when>
								</c:choose>
							</span>
						</div>
						<div class="order-auther">
							<span>主题&emsp;&nbsp;&nbsp;&nbsp;：</span><span
								class="order-content">${order.f_theme}</span>
						</div>
						<div class="order-auther">
							<span>时间&emsp;&nbsp;&nbsp;&nbsp;：</span> <span
								class="order-content"> <ut:formatDate
									dateline="${order.f_create_time }"></ut:formatDate>
							</span>
						</div>
						<div class="order-auther">
							内容&emsp;&nbsp;&nbsp;&nbsp;：<span class="order-content">${order.f_reply_content}</span>
						</div>
						<c:if test="${not empty order.f_file}">
						<div class="order-tainer">
							附件&emsp;&nbsp;&nbsp;&nbsp;：
							<span class="order-content" style="color: green;">
								<a href="${order.f_file}" target="_blank" style="color: green;">点击查看</a>
							</span>
						</div>
						</c:if>
					</div>
					<div class="chat-info col-sx-12  col-md-9 col-lg-9">
						<div class="order-inline">
							<div class="online-service" style="max-width: 1000px;">
								<span class="online-circle"></span><span>在线服务</span>
							</div>
							<div id="message-reply" class="chat-tainer" style="max-width: 1000px"></div>
							<input type="file" id="fileOpen" name="fileOpen" accept="image/png,image/jpeg" style="display: none;" value="" />
							<!-- 填写内容 -->
							<c:if test="${order.f_to_order_state != 'CLOSE'}">
								<div class="" style="width: 100%;">
									<form class="order-wids" action="">
										<table style="width: 100%;">
											<tr class="order-wids">
												<td class="order-reply">
													<a href="javascript:void(0)" onclick="uploadFile('front')"><span class="order-p" title="发送图片"></span></a>
													<input type="hidden" id="file" name="file" class="order-input conmiss" value="" />
													<textarea name="content" id="content" rows="1" class="reply-content" style="overflow-y:hidden; resize: vertical;max-width: 930px;"></textarea>
													<c:if test="${order.f_from_order_state != 'CLOSE'}"> 
														<button type="button" onclick="messageReply()" class="button">回复</button>
													</c:if>
												</td>
											</tr>
											<tr>
												<td>
													<span class="file-progress" id="progressid"></span>
												</td>
											</tr>
											<tr class="ht15"></tr>
										</table>
									</form>
								</div>
							</c:if>
						</div>
					</div>
				</div>
				
				<button onclick="javascript:history.go(-1);">返回</button>
			</div>
			
		</div>
		
		
	</div>

<script type="text/javascript" src="<%=FileUpload%>js/file-upload.js"></script>
<script type="text/javascript" src="<%=FileUpload%>js/lrz.bundle.js"></script>
<script type="text/javascript" src="<%=FileUpload%>js/jquery.upload.js"></script>
<script type="text/javascript">
	var basePath = '<%=actionPath%>';
	var orderId = '${order.id}';
	var orderState = '${order.f_to_order_state}';

	if (orderState != 'CLOSE') {
		function makeExpandingArea(el) {
			var setStyle = function(el) {
				el.style.height = 'auto';
				el.style.height = el.scrollHeight - 4 + 'px';
			}
			var delayedResize = function(el) {
				window.setTimeout(function() {
					setStyle(el)
				},
					0);
			}
			if (el.addEventListener) {
				el.addEventListener('input', function() {
					setStyle(el)
				}, false);
				setStyle(el)
			} else if (el.attachEvent) {
				el.attachEvent('onpropertychange', function() {
					setStyle(el)
				});
				setStyle(el)
			}
			if (window.VBArray && window.addEventListener) { //IE9  
				el.attachEvent("onkeydown", function() {
					var key = window.event.keyCode;
					if (key == 8 || key == 46) delayedResize(el);

				});
				el.attachEvent("oncut", function() {
					delayedResize(el);
				}); //处理粘贴  
			}
		}

		makeExpandingArea(document.getElementById("content"));
	}


	function messageReply() {
		var content = $("#content").val();
		var file = $("#file").val();
		
		if (content == '' && file == '') {
			alert('内容不能为空！');
			return false;
		}
		$.ajax({
			type : "POST",
			dataType : "json",
			url : basePath + "managerWorkOrder/orderReply.do",
			data : {
				orderId : orderId,
				content : content,
				file : file
			},
			success : function(data) {
				if (data.code == 'success') {
					$("#content").val('');
					$("#file").val('');
					$("#content").css('height', '24px');
					$("#message-reply").load(basePath + "managerWorkOrder/loadReply.do?scroll=true&orderId=" + orderId);
				} else {
					alert(data.msg);
					return;
				}
			}
		});
	}
	
	function scrollToBottom() {
		$('#message-reply').animate({
            scrollTop: $('#message-reply')[0].scrollHeight
         },300);
	}
	
	var lastDetailTime = 0;
	var isScorll = true;
	function loadReply() {
		if (orderId.length == 0) {
			return;
		}
		$("#message-reply").load(basePath + "managerWorkOrder/loadReply.do?scroll=true&orderId=" + orderId);
		setTimeout(loadReply, 3000);
		if (isScorll) {
			setTimeout(scrollToBottom, 200);
		}
	}
	
	var myfile = document.getElementById('fileOpen');
	myfile.addEventListener('change',readFile,true);

	function uploadFile() {
		document.getElementById('fileOpen').click();
	}
	
	function readFile() {
		var img = myfile.value;
		var listener = new UploadListener();
		var uploader = new Uploader(this.files.item(0), listener, 'user'); 
	}
	
	function UploadListener() {
	    this.onProgress = function(e) {
	    	
	    }
	    
	    this.uploadComplete = function(data) {
	   		$("#file").val(data);
	    	messageReply();
	    }
	    
	    this.uploadFailed = function() {
	    	$("#progressid").css("display", "block");
	    	$("#progressid").html("上传失败!");
	    }
	}
	
	$(loadReply);		
		
</script>
</body>	
</html>