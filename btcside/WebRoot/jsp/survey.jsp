<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>问卷调查</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>"/>		
		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
		<script type="text/javascript" src="<%=basePath%>common/js/survey.js?<%=rjs%>"></script>
	</head>
	<body class="epps-bg">
<form action="<%=basePath %>survey/surveydo.do" method="post">
  <div class="epps-wrap">
    <dl>
      <dt>1.您投资比特币行业的资金来源？（以下问题均为单选）</dt>
      <dd id="num1">
        <span onclick="loadStyle('num1','num1span1');" id="num1span1">所有积蓄</span>
        <span onclick="loadStyle('num1','num1span2');" id="num1span2">闲置资金</span>
        <span onclick="loadStyle('num1','num1span3');" id="num1span3">资产的一定比例专门用来投资</span>
      </dd>
      <dt>2.如果可以选择，那么您在交易时更偏好哪种杠杆倍数？</dt>
      <dd id="num2">
        <span onclick="loadStyle('num2','num2span1');" id="num2span1">1倍</span>
        <span onclick="loadStyle('num2','num2span2');" id="num2span2">5倍</span>
        <span onclick="loadStyle('num2','num2span3');" id="num2span3">10倍</span>
        <span onclick="loadStyle('num2','num2span4');" id="num2span4">20倍</span>
        <span onclick="loadStyle('num2','num2span5');" id="num2span5">50倍</span>
      </dd>
      <dt>3.在交易时您偏好的仓位习惯？</dt>
      <dd id="num3">
        <span onclick="loadStyle('num3','num3span1');" id="num3span1">习惯满仓交易</span>
        <span onclick="loadStyle('num3','num3span2');" id="num3span2">习惯部分持仓</span>
        <span onclick="loadStyle('num3','num3span3');" id="num3span3">会根据市场情况做出自己的仓位情况</span>
      </dd>
      <dt>4.QQ群、微博、论坛等信息聚集处所传播的一些所谓能影响行情的“消息”是否会影响您的交易决策？</dt>
      <dd id="num4">
        <span onclick="loadStyle('num4','num4span1');" id="num4span1">会影响</span>
        <span onclick="loadStyle('num4','num4span2');" id="num4span2">我会参考并作出自己的判断</span>
        <span onclick="loadStyle('num4','num4span3');" id="num4span3">不会影响</span>
      </dd>
      <dt>5.您是否喜欢听大神指导交易？</dt>
      <dd id="num5">
        <span onclick="loadStyle('num5','num5span1');" id="num5span1">喜欢</span>
        <span onclick="loadStyle('num5','num5span2');" id="num5span2">选择性听取意见</span>
        <span onclick="loadStyle('num5','num5span3');" id="num5span3">不喜欢</span>
      </dd>
      <dt>6.交易时您的操作习惯？</dt>
      <dd id="num6">
        <span onclick="loadStyle('num6','num6span1');" id="num6span1">跟着感觉走</span>
        <span onclick="loadStyle('num6','num6span2');" id="num6span2">与多数人意见一致</span>
        <span onclick="loadStyle('num6','num6span3');" id="num6span3">有自己的一套交易纪律</span>
      </dd>
      <dt>7.交易时您的获利手段？</dt>
      <dd id="num7">
        <span onclick="loadStyle('num7','num7span1');" id="num7span1">看好趋势长期持有</span>
        <span onclick="loadStyle('num7','num7span2');" id="num7span2">短线波动高频交易</span>
        <span onclick="loadStyle('num7','num7span3');" id="num7span3">讨厌风险 只寻求套利交易机会</span>
      </dd>

    </dl>
    <input type="hidden" name="numarray" id="numarray"/>
    <div class="epps-btn">
      <input type="submit" onclick="return styleText();" value="提交答案查看结果"/>
    </div>
  </div>
  <div class="epps-footer"></div>
  </form>
</body>

</html>
