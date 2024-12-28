<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.title.index"/></title>
		<meta name="keywords" content="<fmt:message key='msg.pg.keywords.index'/>">
		<meta name="description" content="<fmt:message key='msg.pg.description.idnex'/>">
		<meta name="robots" content="index,follow">
	    <meta name="googlebot" content="index,follow">
        <meta name="baidu-site-verification" content="code-vKIS4t7fWR" />
	    <meta property="og:title" content="<fmt:message key='msg.pg.title.index'/>">
	    <meta property="og:description" content="<fmt:message key='msg.pg.description.idnex'/>">
	    <meta property="twitter:title" content="<fmt:message key='msg.pg.title.index'/>">
	    <meta property="og:site_name" content="Btcside">
	    <meta property="og:type" content="article">
	    <meta property="og:url" content="<%=basePath%>">
	    <meta property="og:image" content="<%=basePath%>common/images/LOGO%20Copy@3x.png">
	    <meta name="twitter:card" content="summary">
	    <meta name="twitter:site" content="@<%=serverName%>">
	    <meta property="twitter:image" content="<%=basePath%>common/images/LOGO%20Copy@3x.png">
		<jsp:include page="_common.jsp"></jsp:include>
		<link href="<%=basePath%>favicon.ico" rel="SHORTCUT ICON" />
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/swiper.min.css?<%=rjs%>" />
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/swiper.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script>
			var partionsIndex0 = "${partitions[0].codes}";
			var partionsUsdrIndex0 = "${usdrPartitions[0].codes}";
			var data = ${daysAndAmount};
			var domilyVals = "${newUser }";
			var usdtFavorite = "";
			var usdrFavorite = "";
			var favorite = "";
			var spotFavorite = "";
			if (isLogin) {
				usdtFavorite = "${favorite }";
				favorite = usdtFavorite;
				usdrFavorite = "${usdrFavorite }";
				spotFavorite = "${spotFavorite }";
				if (usdrFavorite.length > 0) {
					usdrFavorite += ",";
				}
			}
			
			var from = "${from}";
		</script>
		<script type="text/javascript" src="<%=basePath%>common/js/index.js?<%=rjs%>"></script>
	</head>
	<body class="index-bg">
    	<c:if test="${newUser }">
    	<div class="add-windows" id="alert-pwd">
    		<div id="tb-overlay-duiping" class="tb-overlay"></div>
    		<div style="position: absolute;width: 100%;height: 100%; z-index:100;">
	    		<div class="risk-box" style="width: 380px">
		    		<form style="margin: 0 30px 0 20px;" autocomplete="off"
						action="<%=basePath%>doBcexLogin.do"
						class="" method="post" onsubmit="return checkParam();">
						<div class="form-title">
							<h2>
								<fmt:message key="msg.pg.index.setPwd"/>
							</h2>
							<a class='close-icon icons-close' id='close-alert' href='javascript:cancelClick()'></a>
						</div>
						<div class="form-group">
							<label for="">
								<fmt:message key="msg.pg.index.loginPwd"/>
							</label>
							<input type="password" autocomplete="new-password" class="form-control" placeholder="<fmt:message key="msg.pg.register.validPwd"/>"
								name="password" id="password" onblur="checkPwd();" />
							<span class="error" id="error2"></span>
						</div>
						<div class="form-group">
							<label for="">
								<fmt:message key="msg.pg.register.againPwd"/>
							</label>
							<input type="password" autocomplete="new-password" class="form-control" placeholder="<fmt:message key="msg.pg.register.againAndPwd"/>" name="password2"
								id="password2" onblur="checkPwd2();" />
							<span class="error" id="error3"></span>
						</div>
						<div class="form-group-btn text-center">
							<button type="submit" class="btn sub-btn" style="margin: 0 auto;" onclick="return checkParam();"><fmt:message key="msg.pg.index.loginSure"/></button>
						</div>
					</form>
				</div>
			</div>
    	</div>
		</c:if>
		<jsp:include page="_top.jsp"></jsp:include>
		<!-- banner部分 -->
		<div class="banner-part">
			<div class="swiper-container">
				<div class="swiper-wrapper">
					<div class="swiper-slide">
						<div class="croscal" style="cursor: auto; background: url(../common/images/banner/${locale}/Btcside-web.png) no-repeat center 0px;"></div>
					</div>
				</div>
<%--				<div class="swiper-pagination"></div>--%>
<%--				<div class="swiper-button-prev"></div>--%>
<%--				<div class="swiper-button-next"></div>--%>
			</div>
		    <div class="banner-part-notice">
		    	<div class="banner-part-content">
		    		<div class="banner-part-content-right">
				        <div class="part-notice-list notice-news" id="news"></div>
			        </div>
			        <a href="<%=basePath%>news/notice.do" target="_blank" style="color: #4980E3;" class="more-old-news position-site"><fmt:message key="msg.pg.index.more"/><img class="pointtoNew" alt="pointto" src="../common/images/pointTo.png"></a>
		        </div>
		    </div>
		</div>
		<div class="clear"></div>
		<div class="contract-type-btn">
<%--			<span class="contract-type-change" onclick="contractTypeChange(this, 1)"><fmt:message key="msg.pg.index.contractPartitionUsdt"/></span>--%>
			<span class="contract-type-change active" onclick="contractTypeChange(this, 2)"><fmt:message key="msg.pg.trade.ExchangeMarket"/></span>
		</div>
		<!-- 主体内容,数据表  -->
        <div class="container-row" style="background: none;">
			<div class="container-bg-none index-table-container" id="data-table">
				<div class="border-box-div">
					<div class="index-tab-title">
						<div id="contract-partition-usdt" style="display: none;">
							<div id="favoritePartition" class="${favorite.length() > 0 ? 'current index-tab-item ' : 'index-tab-item'}" onclick="indexTabClick(this, '${favorite}', true)"><span><fmt:message key="msg.pg.trade.OptionalMarket"/></span></div>
							<c:forEach items="${partitions}" var="partition" varStatus="st">
								<div id="partitions${st.index}" onclick="indexTabClick(this, '${partition.codes}', false, ${st.index})" class="${(st.index == 0 && favorite.length() == 0) ? 'current index-tab-item' : 'index-tab-item'}">
									<span>${locale == 'zh_HK' ? partition.name_hk : (locale == 'ko_KR' ? partition.name_kr : (locale == 'ja_JP' ? partition.name_jp : (locale == 'en_US' ? partition.name_en : partition.name_cn))) }</span>
								</div>
							</c:forEach>
						</div>
						<div id="contract-partition-spot">
							<div id="favoriteSpotPartition" class="${favorite.length() > 0 ? 'current index-tab-item ' : 'index-tab-item'}" onclick="indexTabSpotClick(this, true, 2)"><span><fmt:message key="msg.pg.trade.OptionalMarket"/></span></div>
							<div class="index-tab-item" id="spotIndexPartition" onclick="indexTabSpotClick(this, false, 1)"><span><fmt:message key="msg.pg.trade.ExchangeMarket"/></span></div>
						</div>
						<div class="contract-search">
							<input class="contract-search-input" id="contractSearchInput" onkeyup="contractSearchChange()" placeholder="<fmt:message key="msg.pg.index.search"/>">
							<i class="iconfont icon-sousuo contract-search-icon"></i>
						</div>
						<div class="clear"></div>
					</div>
					<div class="table-bg index-table">
						<div class="index-data-header" id="indexDataHeader" style="display: none;">
							<table cellpadding="0" cellspacing="0">
								<thead>
									<tr>
										<th class="fav-th"></th>
										<th class="name-th">
											<fmt:message key="msg.pg.index.contactName"/>
										</th>
										<th class="index-th">
											<fmt:message key="msg.pg.index.currentIndex"/>${locale == 'ja_JP' ? '' : '(USDT)'}
										</th>
										<th>
											<fmt:message key="msg.pg.index.newDeal"/>
										</th>
										<th>
											<fmt:message key="msg.pg.index.highest"/>
										</th>
										<th>
											<fmt:message key="msg.pg.index.floor"/>
										</th>
										<th>
											<span class="index-change">
												<fmt:message key="msg.pg.index.upToDown"/>
											</span>
											<span class="index-change up-down">
												<em data-indexV class="arcshape-up-arrow"></em>
												<em data-indexVI class="arcshape-down-arrow"></em>
											</span>
										</th>
										<th class="last-th">
											<fmt:message key="msg.pg.index.dealAmounts"/>
										</th>
									</tr>
								</thead>
							</table>
						</div>
						<div class="index-data-header" id="indexSpotDataHeader">
							<table cellpadding="0" cellspacing="0">
								<thead>
								<tr>
									<th class="fav-th"></th>
									<th class="name-th thChangeWidth">
										<fmt:message key="msg.pg.trade.tradeON"/>
									</th>
									<th class="index-th thChangeWidth">
										<fmt:message key="msg.pg.index.newDeal"/>
									</th>
									<th class="index-th thChangeWidth">
										<fmt:message key="msg.pg.index.floor"/>
									</th>
									<th class="index-th thChangeWidth">
										<fmt:message key="msg.pg.index.highest"/>
									</th>
									<th class="thChangeWidth">
										<span class="index-change">
											<fmt:message key="msg.pg.index.upToDown"/>
										</span>
										<span class="index-change up-down">
											<em data-indexV class="arcshape-up-arrow"></em>
											<em data-indexVI class="arcshape-down-arrow"></em>
										</span>
									</th>
									<th class="last-th thChangeWidth">
										<fmt:message key="msg.pg.index.dealAmounts"/>
									</th>
								</tr>
								</thead>
							</table>
						</div>
						<div class="index-data-table">
							<ul id="tickerTbody" class="index-data-ul">
							</ul>
							<div class="center text-center index-table-loading" id="indexTableLoading">加载中...</div>
						</div>
						
					</div>
				</div>
				<div class="clear"></div>
			</div>
	   </div>
	   
	<!--App下载-->
	<div class="app-download">
		<div class="download-area-wrap clearfix">
			<div class="download-area-text"><fmt:message key="msg.pg.index.downApp"/></div>
			<div class="download-area-text-Tagline"><fmt:message key="msg.pg.index.downTagline"/></div>			
            <div class="download-bgimg">
        	    	<img class="ios-btn" src="../common/images/apple-app.png" alt="ios" />
        	    	<img class="android-btn" src="../common/images/andriod-app.png" alt="android" />
        	</div>
			<div class="download-area-DownBtn">
				<div id="iosDownBtn" class="iosDownBtn">
					<span><fmt:message key="msg.pg.index.downIOS"/><img src="../common/images/CombinedShape.png"/></span>
				</div>
				<div id="androidDownBtn"  class="androidDownBtn" onclick="goToDownLoad()">
					<span><fmt:message key="msg.pg.index.downAndroid"/><img src="../common/images/CombinedShape.png"/></span>		
				</div>
				<div id="appQrcode" class="appQrcode">
					<img alt="app" style="width: 100%;height: 100%;" src="<%=basePath%>common/images/qrcode/<%=serverName%>/app.png?v=1">
				</div>
				<div class="clear"></div>
				
			</div>
		</div>
	</div>
<!-- 底部footer -->
<jsp:include page="_footer.jsp"></jsp:include>
</body>
<script>
	if (from && 'otc' == from) {
		logoutClick();
	}
</script>
</html>	    
