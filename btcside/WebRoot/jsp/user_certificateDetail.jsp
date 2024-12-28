<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.center"/></title>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/html2canvas.js"></script>
<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
<div class="add-windows" id="alert-windows"></div>
<!-- 身份验证 -->
<jsp:include page="_top.jsp"></jsp:include>
<jsp:include page="_head.jsp"></jsp:include>
<div class="container center-cont">
  <jsp:include page="_user_left.jsp"></jsp:include>
  <div class="main-rt-box">
  	<div class="main-wrap">
      <div class="block-title">
        <h2> <fmt:message key="msg.pg.userCenter.getCerHonur"/> </h2>
      </div>
      <div class="info-box" id="cont-svg">
      	<div id="honour" class="honour-cvs"> 
            <div style="width:440px;margin:  94px 124px 0;position: absolute;">
                <h2 style="font-size: 20px;color: #333;margin-bottom: 15px;margin-top: 35px;">${validateInfo.real_name }:</h2> 
                <p style="line-height: 30px;">
                    <span>&emsp;&emsp;<c:if test="${locale == 'zh_CN' }">在</c:if><c:if test="${locale == 'en_US' }"></c:if>${certificate.activity_name }<c:if test="${locale == 'zh_CN' }">中</c:if><c:if test="${locale == 'en_US' }"></c:if>，<c:if test="${locale == 'zh_CN' }">荣获</c:if><c:if test="${locale == 'en_US' }">Award</c:if><span style="color: #333;font-weight: 700;">“${certificate.certificate_name }” </span><c:if test="${locale == 'zh_CN' }">荣誉称号。</c:if><c:if test="${locale == 'en_US' }">honorary title.</c:if></span>
                </p>
                <p>&emsp;&emsp;<fmt:message key="msg.pg.userCenter.hadEncourage"/></p>
                <p style="text-align: right;margin-right: 15px;margin-bottom: 0px;margin-top: 20px;color: #333;font-weight: 700;">Btcside TEAM</p>
                <div style="display: inline-block;margin-left: -26px;">
                    <span class="sign-time" id="issueTime"></span>
                    <span class="bfx-sign"></span>
                    <span class="sign-time" style="margin-left: 26px;"><fmt:message key="msg.pg.index.platform"/></span> 
                </div>
            </div>
        </div>
        <div class="text-center margint15"><input type="button" class="button btn sub-btn" value="<fmt:message key="msg.pg.userCenter.honourDownload"/>" id="download" /></div>
      </div>
    </div>
  </div>
</div>
<jsp:include page="_footer.jsp"></jsp:include>
</body>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		warmTip : "温馨提示",
		sureTip : "确定",
	};
	message["zh_HK"] = {
		warmTip : "溫馨提示",
		sureTip : "確定",
	};
	message["en_US"] = {
		warmTip : "Warm prompt",
		sureTip : "Sure",
	};
	message["ja_JP"] = {
		warmTip: "ヒント",
		sureTip: "確定",
	};
	message["ko_KR"] = {
		warmTip : "팁",
		sureTip : "결정",
	};
	var MSG = message[locale];
	
	var id = "${certificate.id }";
	$(function(){
		var stamp = "${certificate.issue_time }";
		var dateStr = datetoString(new Date(stamp * 1000));
		$('#issueTime').html(dateStr);
		
	});
	
	$("#download").click(function() {
		$.ajax({
			url : "<%=basePath%>center/getCertificateImage.do",
			type : 'POST',
			dataType : "json",
			data : {
				id : id,
			},
			success : function(data) {
				var imgPath = data.path;
				if(data.path == '') {
					getImageData();			
				}else {
					getImgPath(imgPath);
				}
			},
			error : function(XMLHttpRequest) {
				alert("Error: " + XMLHttpRequest.responseText);
			}
		});
	})
	
	function getImageData() {
		html2canvas($('#honour'), {
			allowTaint : true,
			taintTest : false,
			onrendered : function(canvas) {
				var dataUrl = canvas.toDataURL("image/png");
				
				$.ajax({
					url : "<%=basePath%>center/cvsToImage.do",
					type : 'POST',
					dataType : "json",
					data : {
						base64ImgData : JSON.stringify(dataUrl),
						id : id
					},
					success : function(data) {
						var imgdata = data.path;
						getImgPath(imgdata);
					},
					error : function(XMLHttpRequest) {
						alert("Error: " + XMLHttpRequest.responseText);
					}
				});
			}
		});	
	}
	
	function getImgPath(imgPath) {
		window.open(imgPath);	
	}
	
	function datetoString(date) {
		var cn = [ "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九" ];
		var s = [];
		var YY = date.getFullYear().toString();
		for (var i = 0; i < YY.length; i++)
			if (cn[YY.charAt(i)])
				s.push(cn[YY.charAt(i)]);
			else
				s.push(YY.charAt(i));
		s.push("年");
		var MM = date.getMonth() + 1;
		if (MM < 10)
			s.push(cn[MM]);
		else if (MM == 10)
			s.push("十");
		else if (MM < 20)
			s.push("十" + cn[MM % 10]);
		s.push("月");
		var DD = date.getDate();
		if (DD < 10)
			s.push(cn[DD]);
		else if (DD == 10)
			s.push("十");
		else if (DD == 20)
			s.push("二十");
		else if (DD < 20)
			s.push("十" + cn[DD % 10]);
		else if (DD == 30)
			s.push("三十");
		else if (DD < 30)
			s.push("二十" + cn[DD % 10]);
		else
			s.push("三十" + cn[DD % 10]);
		s.push("日");
		return s.join('');
	}

</script>
</html>
