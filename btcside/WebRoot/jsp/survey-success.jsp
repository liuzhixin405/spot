<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="shortcut icon" href="<%=basePath%>common/images/favicon.ico" type="image/x-icon">
		<title>测试结果</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>"/>		
		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
		 <script src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js" type="text/javascript"></script>
	</head>
	<body class="epps-bg">

  <div class="epps-wrap">
    <div class="epps-success">
      <span>测试结果</span>
      <div class="epps-success-main">
        <p>经测试，您的比特币投资情商为 <b>${num}</b> 分！您属于 <b>${num7 }</b>投资者<br/>
        <c:if test="${numarr1!=''||numarr2!=''||numarr3!=''}">
	                    建议您<b>${numarr1} &nbsp ${numarr2} &nbsp ${numarr3}</b><br/></c:if>
	    <c:if test="${num1!=''||num4!=''||num6!=''}">
	                   您懂得<b>${num1}  &nbsp ${num4}  &nbsp ${num6}</b><br/></c:if>
        </p>
        <em>将测试结果分享到新浪微博并@3位好友，立刻获得官方送出的500狗狗币。
          <br>（记得留下您的狗狗币收款地址哟~）
        </em>
          <div class="fx-btn">
           <a class="btn-sina"  href="javascript:void(0)"></a>
          <a href="<%=basePath %>home/home.do" class="btn-home" title="返回首页"></a>
        </div>
      </div>
    </div>
    <div class="epps-btn">
      <p>10秒钟开启您的比特币合约掘金之路！</p>
      <a href="<%=basePath %>register/register.do" style="padding:0 100px;">立即注册</a>
    </div>
  </div>
  <div class="epps-footer"></div>
  <script>
  $(function() {

    $(".btn-sina").click(function() {
      window.open("http://service.weibo.com/share/share.php?url=https://www.bfx.un/survey/survey.do&desc=&title=@BFX比特币合约 我在BFX测试比特币投资情商，快来测一测吧！&pic=http://ww3.sinaimg.cn/mw1024/83834ca6jw1elkw6ce6hmj20kp073ab2.jpg");
    });
  });
  </script>
</body>

</html>
