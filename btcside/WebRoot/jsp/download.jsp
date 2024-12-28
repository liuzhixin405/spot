<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
  <meta name="apple-touch-fullscreen" content="YES">
  <meta name="format-detection" content="telephone=no">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>APP下载</title>
  <link rel="stylesheet" href="./app/css/index.css?v=1002" type="text/css"/>
  
</head>
<body>
  
  <div class="download-page">
	<img class="mobile-wrapper-img" src="./app/images/down.png?v=1007"/>
	
	<div class="bottom-panel">
		<!-- <div class="down-button ios">
			<a href="https://testflight.apple.com/join/y5j3sBsJ">
				中国大陆版
			</a>
		</div>
				
		<div class="down-button ios">
			<a href="https://testflight.apple.com/join/RnuORJoM" style="background:#242630;">
				国际版
			</a>
		</div>
		 -->
		 <div class="down-button">
             <a class="" id="visitFlag" href="javascript:location.href = document.referrer;">
                 返回pc网页
             </a>
         </div>
		<div class="down-button ios">
			暂不支持IOS平台
		</div>
		
		<div class="down-button android">
			<a id="downloadBtn" href="./app/download.do">
				点击下载
			</a>
		</div>
	</div> 
	
    <div class="mobile-wrapper-pop" id="maskDiv" style="display: none;"></div>

  </div>
  <script src="./app/js/jquery.min.js"></script>
  <script src="./app/js/index.js?v=101119"></script>
</body>
</html>