// 适配rem
(function(doc, win) {
  var docEl = doc.documentElement
  var resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize'
  var recalc = function() {
    var clientWidth = docEl.clientWidth
    if (!clientWidth) return
    // 根据设备的比例调整初始font-size大小
    if (clientWidth > 750) clientWidth = 750
    docEl.style.fontSize = 20 * (clientWidth / 375) + 'px'
  }
  if (!doc.addEventListener) return
  win.addEventListener(resizeEvt, recalc, false)
  doc.addEventListener('DOMContentLoaded', recalc, false)
})(document, window)

function showMask() {
	$(".mobile-wrapper-pop").html(
	"<img class='mobile-wrapper-pop-arrow' src='./app/images/pop_arrow.png'/>" + 
	"<div class='mobile-wrapper-pop-tips'>" + 
	"<div>请点击右上角复制当前页面链接</div>" + 
	"<div>并在手机自带浏览器中打开</div>" + 
	"</div>");
	$(".mobile-wrapper-pop").show();
}

$(function() {
    goBackPc();
  var isJump = false;
  var jumpTestFlight = false;

    // 机型
  var userAgent = window.navigator.userAgent
   // console.log(userAgent)
   // alert(userAgent)
  if ((userAgent.indexOf('iPhone') >= 0 || userAgent.indexOf('iPod') >= 0 || userAgent.indexOf('iPad') >= 0) 
	  && userAgent.indexOf('MicroMessenger') >= 0) {
	  jumpTestFlight = true;
  } else if ((userAgent.indexOf('iPhone') >= 0 || userAgent.indexOf('iPod') >= 0 || userAgent.indexOf('iPad') >= 0)
	  && userAgent.indexOf(' QQ') >= 0 && userAgent.indexOf('Mobile') >= 0) {
	  jumpTestFlight = true;
  } else if (userAgent.indexOf('Android') >= 0 && userAgent.indexOf('MicroMessenger') >= 0) {
    isJump = true;
  } else if (userAgent.indexOf('Android') >= 0 && userAgent.indexOf(' QQ') >= 0 && userAgent.indexOf('Mobile') >= 0) {
    isJump = true;
  } else {
    isJump = false;
	if ((userAgent.indexOf('iPhone') >= 0 || userAgent.indexOf('iPod') >= 0 || userAgent.indexOf('iPad') >= 0)) {
		jumpTestFlight = true;
	}
  }
  
  if (jumpTestFlight) {
	  $(".android").remove();
  } else {
	  $(".ios").remove();
	  if (isJump) {
		// $(".mobile-wrapper-pop").show();
		//$("#downloadBtn").removeAttr("href");
		$("#downloadBtn").attr("onclick", "showMask()");
		$("#downloadBtn").attr("target", "_blank");
	  } else {
		$(".mobile-wrapper-pop").hide();
	  }
  }
  // if (userAgent.indexOf('iPhone') >= 0) {
  //   // iPhone
  // } else if (userAgent.indexOf('Android') >= 0) {
  //   // Android
  // }
  // if (userAgent.indexOf('iPhone') >= 0 && userAgent.indexOf('MicroMessenger') >= 0) {
  //   // iPhone微信浏览器
  // } else if (userAgent.indexOf('iPhone') >= 0 && userAgent.indexOf(' QQ') >= 0 && userAgent.indexOf('Mobile') >= 0) {
  //   // iPhoneQQ内置浏览器
  // } else if (userAgent.indexOf('iPhone') >= 0 && userAgent.indexOf('MQQBrowser') >= 0 && userAgent.indexOf('Mobile') >= 0) {
  //   // iPhoneQQ浏览器
  // } else if (userAgent.indexOf('iPhone') >= 0 && userAgent.indexOf('QQ') < 0 && userAgent.indexOf('MicroMessenger') < 0 && userAgent.indexOf('Mobile') >= 0) {
  //   // iPhone内置浏览器
  // } else if (userAgent.indexOf('Android') >= 0 && userAgent.indexOf('MicroMessenger') >= 0) {
  //   // Android微信浏览器
  // } else if (userAgent.indexOf('Android') >= 0 && userAgent.indexOf(' QQ') >= 0 && userAgent.indexOf('Mobile') >= 0) {
  //   // AndroidQQ内置浏览器
  // } else if (userAgent.indexOf('Android') >= 0 && userAgent.indexOf('MQQBrowser') >= 0 && userAgent.indexOf('Mobile') >= 0) {
  //   // AndroidQQ浏览器
  // } else if (userAgent.indexOf('Android') >= 0 && userAgent.indexOf('QQ') < 0 && userAgent.indexOf('MicroMessenger') < 0 && userAgent.indexOf('Mobile') >= 0) {
  //   // Android内置浏览器
  // }

})
function goBackPc() {
    window.localStorage.setItem("visit", '1');
    // window.location.href = document.referrer;
    // var referrer = document.referrer;
    // $("#visitFlag").attr("href", referrer);
}