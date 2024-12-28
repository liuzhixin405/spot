<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <meta charset="UTF-8">
  <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
  <title>公测活动结束
  </title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>"/>		
		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>

  <!--[if lte IE 6]>
<script src="js/ie6png.js" type="text/javascript"></script>
    <script type="text/javascript">
        DD_belatedPNG.fix('.test, ul, img, li, input , a, div,span,em,i');
    </script>
<![endif]-->

</head>

<body class="hd-end-bg">
  <div class="hd-container">
    <div class="hd-end-wrap">
      <p>参加Btcside公测的小伙伴们大家好，Btcside团队向大家的热心表示由衷的感谢！</p>
      <p>首先恭喜用户 <b style="color:red;">jhs****@163.com</b> 在活动中获得第一名，怒抢IPHONE6手机一部，我们已经通过邮箱向您索要收货地址 收到您的回复后我们将在一周内为您寄出奖品。 同时也要恭喜其他上榜用户，获得Btcside的vip资格。</p>
      <p>公测中系统存在的问题让一些用户受到了不少的困扰，在此我们向您表达深深的歉意。但是也正因为这些问题， 让我们变得更有底气，所有的鼓励和批评，我们都会虚心接受。我们会争取把所有问题解决在正式上线之前，力求为大家提供一个安全稳定的交易平台。
      </p>
      <ul>
        <li>来分享一下我们的战果吧：</li>
        <li>修正了撤单引起的盈利资金冻结问题；</li>
        <li>修正了爆仓单引起的0挂单和负数挂单问题；</li>
        <li>修正了与错误数据成交的用户资产被冻结的问题；</li>
        <li>修复了其他一些用户提交的页面上的一些问题。</li>
      </ul>
      <p>接下来，我们将会停盘进行调整以及系统优化。后续我们将计划第二轮公测或者正式上线。再次感谢，敬请关注！</p>
    </div>
    <div class="hd-wrap" style="padding-top:0;">
      <div class="hd-title">
        <b style="color:#fffb89;">获奖名单</b>
      </div>

      <div class="hd-result">
        <p style="color:#fff;text-align:center;font-size:18px;"><b>此处显示排名仅供参考，由于部分用户存在恶意刷资产现象将被取消资格，具体排名请关注网站公告。
</b><a href="<%=basePath %>home/home.do" class="rt" style="color:#f60;font-size:16px;">返回首页
          </a>
        </p>
        <br>
					<table cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th width="20%">排名</th>
              <th width="30%">邮箱</th>
              <th width="40%">账户资产</th>
              <th width="10%">奖品</th>
            </tr>
          </thead>
          <tbody>
							<c:forEach var="li" items="${pm}" varStatus="status">
								<tr>
									<td>
										${li.num }
									</td>
									<td>
										${fn:substring(li.email,0,3)}****@${fn:substringAfter(li.email,'@')}
									</td>
									<td align="left">
										<span class="zhichan" style="width:${li.rate}%;"></span>
									</td>
									<td>
										<c:if test="${status.index==0}">
											<img src="<%=basePath %>common/images/iphone.png" alt="iphone">
										</c:if>
										<c:if test="${status.index>0 && status.index<5 }">
											 <img src="<%=basePath %>common/images/v9.png" alt="vip9">
										</c:if>
										<c:if test="${status.index>=5}">
											 <img src="<%=basePath %>common/images/v7.png" alt="vip9">
										</c:if>
									</td>
								</tr>
							</c:forEach>
            <tr>
              <td>参与奖</td>
              <td>815****@qq.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>873****@qq.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>540****@qq.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>181****@qq.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>szt****@163.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>554****@qq.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>shu****@163.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>a47****@gmail.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>ruo****@126.com</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>
            <tr>
              <td>参与奖</td>
              <td>bai****@cntv.cn</td>
              <td>
                
              </td>
              <td>
                <img src="<%=basePath %>common/images/v5.png" alt="vip5">
              </td>
            </tr>


          </tbody>
        </table>
      </div>
      <br>
      <p style="color:#3770b3;">
        <a href="<%=basePath %>" class="rt" style="color:#fff;font-size:16px;">返回首页
          </a>
      </p>
    </div>

  </div>
  <!-- 客服代码 -->
  <div class="service-box">
    <div class="service-wrap">
      <div class="kefu-icon service-icon sprites-service" id="service-icon"></div>
      <div class="service-main" id="service-main">
        <div class="se-block" id="se-title">
          <span class="kefu-icon title-icon sprites-service"></span>收起 >></div>
        <div class="se-list">
          <span class="icons-se-qq"></span><a href="http://crm2.qq.com/page/portalpage/wpa.php?uin=800088161&ref=&f=1&ty=1&ap=&as=" rel="nofollow" target="_blank">800088161</a>
        </div>
        <div class="se-list">
          <span class="icons-se-tel"></span>400-88-94100</div>

        <div class="se-list">
          <span class="icons-wb1"></span><a href="http://weibo.com/btcqihuo" rel="nofollow" target="_blank">新浪微博</a>
        </div>
        <div class="se-list">
          <span class="icons-wx1"></span><a href="javascript:void(0)" target="_blank" id="wx1">关注微信</a>
          <img src="images/bfx-weixin.jpg" width="120px" height="120px" alt="Btcside官方微信" style="display:none;" id="wx-img">
        </div>
        <div class="se-list">
          <span class="icons-info"></span><a href="feedback.html" target="_blank">提交反馈</a>
        </div>
        <div class="se-list" style="text-align:center;padding-top:15px;">
          <a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=d8af779649248fb1526d591391818ddd1217353bba25c2d7061213281140be2a" rel="nofollow">
            <img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="Btcside" title="Btcside" style="vertical-align:middle;">
          </a>

        </div>

      </div>
    </div>
  </div>

  <script>
  $(function() {
    $(".hd-result table tr:odd").css("background", "#002b66");
    $(".hd-result table tr:even").css("background", "#032f6a");
    $(".hd-result table tr:eq(1) td").css("color", "#e6e697");
    $(".hd-result table tr:eq(1) td span").css("background", "#e6e697");
    $(".hd-result table tr:gt(1) td").css("color", "#d4d9de");
    $(".hd-result table tr:gt(1) td span").css("background", "#d4d9de");
    $(".hd-result table tr:gt(5) td").css("color", "#bb924a");
    $(".hd-result table tr:gt(5) td span").css("background", "#bb924a");
    $(".hd-result table tr:gt(15) td").css("color", "#326aac");
    $(".hd-result table tr:gt(15) td span").css("background", "#326aac");
    //点击收起,隐藏块
    $("#service-main").show();
    $("#se-title").click(function() {
      $("#service-main").hide();
      $("#service-icon").show();
    });
    // 微信显示切换
    $("#wx1").toggle(function() {
      $("#wx-img").show();
    }, function() {
      $("#wx-img").hide();
    });
    // 点击客服按钮
    $("#service-icon").click(function() {
      $(this).hide();
      $("#service-main").show();
    });
  });
  </script>

</body>

</html>
