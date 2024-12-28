<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.workerOrderDetail.detail"/></title>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>
<body>
	<div class="add-windows" id="alert-windows"></div>
	<jsp:include page="_top.jsp"></jsp:include>
	<jsp:include page="_head.jsp"></jsp:include>
	<div class="container center-cont online-server" style="background: #FFFFFF;box-shadow: 0px 2px 6px 1px rgba(148,171,192,0.1);padding: 20px 20px;">
		<div class="bkg-white order-intrd">
			<div class="order-auther">
				<span><fmt:message key="msg.pg.workerOrderList.orderCode"/>&emsp;&nbsp;&nbsp;&nbsp;：</span><span class="order-content">${order.f_order_code}</span>		
			</div>
			<div class="order-auther">
				<span><fmt:message key="msg.pg.workerOrderList.orderType"/>&nbsp;&nbsp;&nbsp;：</span>
				<span class="order-content">
					<c:choose>
						<c:when test="${order.f_order_type == 'ACCOUNT'}"><fmt:message key="msg.pg.workerOrderAdd.account"/></c:when>
						<c:when test="${order.f_order_type == 'TRADE'}"><fmt:message key="msg.pg.workerOrderAdd.trade"/></c:when>
						<c:when test="${order.f_order_type == 'COMPLAIN'}"><fmt:message key="msg.pg.workerOrderAdd.Complaint"/></c:when>
						<c:when test="${order.f_order_type == 'RW'}"><fmt:message key="msg.pg.workerOrderAdd.rw"/></c:when>
						<c:when test="${order.f_order_type == 'COOP'}"><fmt:message key="msg.pg.workerOrderAdd.COOP"/></c:when>
					</c:choose>
				</span>
			</div>
			<div class="order-auther">
				<span><fmt:message key="msg.pg.workerOrderList.orderTheme"/>&nbsp;&nbsp;&nbsp;：</span>
				<span class="order-content">${order.f_theme}</span>		
			</div>
			<div class="order-auther"><span><fmt:message key="msg.pg.workerOrderList.orderTime"/>&emsp;&emsp;&nbsp;&nbsp;&nbsp;：</span>
				<span class="order-content">
					<ut:formatDate dateline="${order.f_create_time }"></ut:formatDate>
				</span>
			</div>
			<div class="order-auther">
				<span><fmt:message key="msg.pg.workerOrderList.orderContent"/>&nbsp;&nbsp;&nbsp;：</span>
				<span class="order-content">${order.f_reply_content}</span>
			</div>
		</div>
		<div class="chat-info col-sx-12  col-md-9 col-lg-9">
			<div class="order-inline">
				<div class="online-service">
					<span class="online-circle"></span><span><fmt:message key="msg.pg.workerOrderDetail.onlineService"/></span>	
				</div>
				<div id="message-reply" class="chat-tainer">
				
				</div>
				<!-- 填写内容 -->
				<c:if test="${order.f_from_order_state != 'CLOSE'}"> 
				<input type="file" id="fileOpen" name="fileOpen" accept=".jpg,.jpeg,.png,image/png,image/jpeg" class="text-input conmiss" value="" />
					<div class="" style="width: 100%;">
						<form class="order-wids" action="">
							<table style="width: 100%;">
								<tr class="order-wids">
									<td class="order-reply">
										<a href="javascript:void(0)" onclick="uploadFile('front')"><span class="order-p" title=""></span></a>
										<input type="hidden" id="file" name="file" class="order-input conmiss" value="" />
										<!-- <div id="content" class="reply-content" contenteditable="true"></div> -->
										<textarea name="content" id="content" rows="1" class="reply-content" style="overflow-y:hidden; resize: vertical;"></textarea>
										<c:if test="${order.f_from_order_state != 'CLOSE'}"> 
											<button type="button" onclick="messageReply()" class="btn reply-btn"><fmt:message key="msg.pg.workerOrderDetail.reply"/></button>
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
	
<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/file-upload.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/lrz.bundle.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.upload.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript">
	
	var basePath = '<%=basePath%>';
	var orderId = '${order.id}';
	var orderState = '${order.f_from_order_state}';
	var lastDetailTime = 0;
	var isScorll = true;
	
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
			tipAlert('<fmt:message key="msg.pg.workerOrderAdd.contentNull"/>');
			return false;
		}
		$.ajax({
			type : "POST",
			dataType : "json",
			url : basePath + "order/orderReply.do",
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
					$("#message-reply").load(basePath + "order/loadReply.do?scroll=true&orderId=" + orderId);
				} else {
					tipAlert(data.msg);
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

	function loadReply() {
		if (orderId.length == 0) {
			return;
		}
		$("#message-reply").load(basePath + "order/loadReply.do?scroll=true&orderId=" + orderId);
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
	var message = {};
	message["zh_CN"] = {
		fileError : "文件类型不支持（jpg或png，最大2M）,请重新选择！",
		warmTip : "温馨提示",
		sureTip : "确定",
	};
	message["zh_HK"] = {
		fileError : "檔類型不支持（jpg或png，最大2M）,請重新選擇！",
		warmTip : "溫馨提示",
		sureTip : "確定",
	};
	message["en_US"] = {
		fileError : "File type is not supported (jpg or PNG, maximum 2M).  Please rechoose! ",
		warmTip : "Warm prompt",
		sureTip : "Sure",
	};
	message["ja_JP"] = {
		fileError : "このファイルフォーマットは利用できません（jpgまたはpng、最大2M）、別のファイル形式を選択してください！",
		warmTip: "ヒント",
		sureTip: "確定",
	};
	message["ko_KR"] = {
		fileError : "파일 형식이 지원되지 않습니다 (jpg 또는 png, 최대 2M). 다시 선택하십시오!",
		warmTip : "팁",
		sureTip : "결정"
	};
	message["ru_RU"] = {
		fileError : " Тип файла не поддерживается (JPG или PNG, максимум 2M). Пожалуйста, выберите другой тип файла! ",
		warmTip : "Важное напоминание",
		sureTip : "Конечно"
	};
	var MSG = message[locale];
</script>
</body>	
</html>