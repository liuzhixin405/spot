<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-经纪人系统</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<link href="<%=basePath%>common/validate/engine.css" rel="stylesheet" type="text/css" media="screen" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
		<script type="text/javascript">
			function checkVip() {	
				$("#error1").html("您的会员等级没有达到vip3")	;
				document.getElementById("error1").style.color = "red";
			}
		</script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>

		<jsp:include page="_head.jsp"></jsp:include>

		<!-- 主体内容 -->
		<div class="container-trade-data">
			<div class="main-wrap">
				<div class="menu-title">
					<h3>
						<span class="icons-i"></span>什么是经纪人？
					</h3>
				</div>
				<div class="box-bg-none">
					<div class="freedom-broker">
						<p>
							经纪人，一般是指以收取
							<b>佣金</b>为目的，为促成他人交易而从事居间，行纪或者代理等经纪业务的公民，法人和其他经济组织。
						</p>

						<p>
							虚拟币合约经纪人，在这里，特指专门从事
							<strong>Btcside</strong>虚拟币合约交易平台提供的各交易品种的中介业务，通过专业服务进行客户开发，通过客户交易的佣金提成得到收入回报的公民、法人或其他经济组织。
						</p>
					</div>
				</div>
			</div>
			<div class="main-wrap">
				<div class="clearfix">
	        <div class="menu-title broker-title">
	          <h3>
	            <span class="icons-zy-user"></span>自由经纪人</h3>
	        </div>
	        <div class="menu-title broker-title">
	          <h3>
	            <span class="icons-borker"></span>高级经纪人</h3>
	        </div>
      	</div>
      	<div class="box freedom-broker">
	        <div class="borker-block bl-lt lt ">
	          <p>自由经纪人是指，通过自己的专业能力，开发客户并通过该经纪业务赚取一定比例交易佣金的自然人。需要经过平台的审核通过，并签订《Btcside虚拟币合约自由经纪业务代理协议》。</p>
	          <h3>成为自由经纪人的条件</h3>
	          <ol>
	            <li>热爱虚拟币行业，愿意从事相关工作；</li>
	            <li>具备一定的虚拟币行业知识，能够胜任相关工作；</li>
	            <li>在站内的会员等级须达到VIP3级以上。</li>
	          </ol>
	          <span class="error" id="error1"></span>
	          <div class="form-group-btn">
	          					<c:if test="${levelid == -1}">
									<a href="javascript:void(0);" class="btn sub-btn" onclick="checkVip();"><i class="borker-icons icons-edit"></i>申请成为自由经纪人</a>
								</c:if>
								<c:if test="${levelid == 0 && type == -1}">
									<a href="<%=basePath%>broker/enter.do" class="btn sub-btn"><i class="borker-icons icons-edit"></i>申请成为自由经纪人</a>
								</c:if>
								<c:if test="${type == 0 }">
									<a href="<%=basePath%>broker/look.do" class="btn sub-btn">查看审核进度</a>
								</c:if>
								<c:if test="${type == 1 }">
									<a href="<%=basePath%>broker/congratulation.do" class="btn sub-btn">审核通过认证</a>
								</c:if>
								<c:if test="${type == 2 }">
									<a href="<%=basePath%>broker/look.do" class="btn sub-btn">查看审核进度</a>
								</c:if>
							</div>
	        </div>
	        <div class="borker-block bl-rt lt">
	          <p>高级经纪人是Btcside专门针对虚拟币相关网站站长、微博大V、活跃QQ群主、以及有固定客户的一些专业经纪人制定的推广返佣策略。 高级经纪人可直接享受VIP10权益，并且获得40%比例的佣金提成。
	          </p>

	          <h3>成为高级经纪人的条件</h3>
	          <ol>
	            <li>虚拟币相关网站站长；</li>
	            <li>虚拟币相关的微博名人账号的所有人；</li>
	            <li>虚拟币投资相关的活跃QQ群群主；</li>
	            <li>有一定投资知识 能为客户提供交易指导以及有固定客户的职业经纪人。</li>
	          </ol>
	          <div class="form-group-btn">
	            <a href="http://wpa.qq.com/msgrd?v=3&amp;uin=786248388&amp;site=qq&amp;menu=yes" rel="nofollow" class="btn sub-btn"><i class="borker-icons icons-oicq"></i>联系客服申请高级经纪人</a>
	          </div>
	        </div>
      	</div>
      	<div class="clear"></div>
			</div>
			<div class="main-wrap">
	      <div class="freedom-broker">
	        <h3>权利</h3>
	        <ol>
	          <li>获得Btcside虚拟币合约自由经纪业务；</li>
	          <li>获得名下所有注册用户交易佣金的20-40%比例不等的提成收入（具体比例根据用户的<a href="<%=basePath%>center/viplist.do" target="_blank">VIP等级</a>确定）；</li>
	          <li>可以自由经纪人名义，开展小范围的线上、线下交流活动；</li>
	          <li>有权参加公司定期不定期举办的各种培训活动或相关会议。</li>
	        </ol>
	        <h3>义务</h3>
	        <ol>
	          <li>配合总公司的统一营销计划；</li>
	          <li>积极地服务好所推广用户；</li>
	          <li>应当积极地学习总公司定期更新的业务资料。</li>
	        </ol>
	        <h3>禁止以下四种行为：</h3>
	        <ol>
	          <li>通过诱骗手段吸引用户注册平台账号；</li>
	          <li>通过高额获利许诺手段进行市场推广活动；</li>
	          <li>以平台名义进行与市场推广活动无关的行为；</li>
	          <li>以平台名义从事任何违反所在地法律的行为。</li>
	        </ol>
	      </div>
    	</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>
