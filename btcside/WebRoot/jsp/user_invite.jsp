<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.inviteMag"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
		<div class="add-windows" id="alert-windows"></div>
		<%-- <c:if test="${applayerror!=''}">
			<div id="add-windows" class="add-windows">
				<div id="tb-overlay" class="tb-overlay"></div>
				<div class="add-wrap" style="display:block;">
				<div class="dialogs-title">
            <h2 class="lt">客户管理</h2>
            <a href="javascript:history.go(-1)" class="close-icon icons-close" id="close-alert"></a>
        </div>
					<div class="dialogs-body">
						<p style="margin-bottom:30px; text-align:center;font-size:16px;">
							对不起  您尚未认证经纪人资格，无法查看该页面!
						</p>
					<div class="form-group-btn">
					<a href="<%=basePath%>broker/broker.do" class="btn sub-btn">去认证</a>
						<a href="javascript:history.go(-1)" class="btn sub-btn-return">返回</a>
						</div>
					</div>			
				</div>
			 </div>
		 </c:if> --%>
		 <div class="add-windows">
			<div id="tb-overlay-duiping" class="tb-overlay display-none"></div>
			<div class="add-wrap dp-alert-wrap invite-BFX" id="invite-BFX">
				<a href="javascript:" class="close-icon icons-close dp-close-alert" style="position: absolute;top: 18px;right: 20px;width: 18px;height: 18px;display: block;"></a>
				<div class="loading-pic"><span class="loding-img"></span></div>
				<a class="invimg-link" id="invimg-link" target="_blank"><img alt="" src="" id="inv-img"></a>
				<div style="text-align: center;" id="down-lav">
				<button class="download-btn"><fmt:message key="msg.pg.userCenter.inviteDownload"/></button>
				</div>
			</div>	
		</div>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container center-cont">
			<jsp:include page="_user_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userCenter.inviteMag"/>
						</h2>
					</div>
					<div class="box">
						<div class="link-url" style="padding-bottom: 10px">
							<p class="link-title"><fmt:message key="msg.pg.userCenter.promotionLink"/>：</p>
							<p class="link-pro">
								<b><%=basePath%>home/s${id}.do</b>
								<span class="icon-copyLinks" onclick="copyInviteLink()"></span>
							</p>
							<a class="cvs-btn" onclick="getAlert()"><fmt:message key="msg.pg.userCenter.picturesInvite"/></a>
						</div>
						<textarea class="link-area"  id="inviteCodeLinkArea"><%=basePath%>home/s${id}.do</textarea>
						<div class="link-url" style="padding-top: 10px">
							<p class="link-title"><fmt:message key="msg.pg.userCenter.InviteCode"/>：</p>
							<p class="link-pro" style="margin-left: 14px;">
								<b>${id}</b>
								<span class="icon-copyLinks" onclick="copyInviteCode()"></span>
							</p>
						</div>
						<textarea class="link-area"  id="inviteCodeArea">${id}</textarea>
						<div class="vip-info">
							<p>
								<span><fmt:message key="msg.pg.userCenter.clikToday"/>：<b>${invite.todayClick == null ? 0 : invite.todayClick }</b><fmt:message key="msg.pg.userCenter.dasMal"/> </span>
								<span><fmt:message key="msg.pg.userCenter.hasClikTotal"/>：<b>${invite.sumClick == null ? 0 : invite.sumClick }</b><fmt:message key="msg.pg.userCenter.dasMal"/> </span>
							</p>
							<p>
								<span><fmt:message key="msg.pg.userCenter.PromoteToday"/>：<b>${invite.todaySuccess == null ? 0 : invite.todaySuccess }</b><fmt:message key="msg.pg.userCenter.nit"/> </span>
								<span><fmt:message key="msg.pg.userCenter.PromoteTotal"/>：<b>${invite.sumSuccess == null ? 0 : invite.sumSuccess }</b><fmt:message key="msg.pg.userCenter.nit"/></span>
							</p>
						</div>
						<%-- <div>
							经纪人管理群： <a target="_blank"
								href="http://shang.qq.com/wpa/qunwpa?idkey=70ada63459303516e61e5c645ec9cbcde19c786ade39248dd1d955bdcdd7e172"
								rel="nofollow"> <img border="0"
								src="<%=basePath%>common/images/group.png?<%=rpic%>"
								alt="Btcside经纪人管理群" title="Btcside经纪人管理群"
								style="vertical-align:middle;">
							</a>（申请入群时请在验证信息输入您的用户名）
						</div> --%>
				</div>
				</div>
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userCenter.extractStatistics"/>
						</h2>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.userCenter.extractType"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.weekComsion"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.monthComsion"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.totalComsion"/>
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="allRebate" varStatus="status" items="${invite.allRebate }">
									<tr>
										<td>
											${allRebate.rebateName }
										</td>
										<td>
											<fmt:formatNumber value="${allRebate.weekRebatel }" pattern="#.########"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${allRebate.monthRebatel }" pattern="#.########"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${allRebate.totalRebatel }" pattern="#.########"></fmt:formatNumber>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<div class="main-wrap">
					<div class="block-title">
						<h2><fmt:message key="msg.pg.userCenter.memberMag"/>
						</h2>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.userCenter.username"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.userLev"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.memberPoint"/>
									</th>
									<c:if test="${identity == 1}">
									<th>
										<fmt:message key="msg.pg.trade.option"/>
									</th>
									</c:if>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas }" var="one" varStatus="status">
									<tr>
										<td>
											<a class="btn-blue" href="<%=basePath%>center/inviteMember.do?inviteeId=${one[0] }">${fn:substring(one[1],0,3)}****@${fn:substringAfter(one[1],'@')}</a>
										</td>
										<td>
											${one[2] == null ? 0 : one[2] }
										</td>
										<td>
											<fmt:formatNumber value="${one[3] }" pattern="#.########"/>
										</td>
										<c:if test="${identity == 1}">
										<td>
											<a class="btn-blue" href="<%=basePath%>center/positionInformation.do?inviteeId=${one[0] }"><fmt:message key="msg.pg.userCenter.positionView"/></a>
										</td>
										</c:if>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="4">
										<div class="pagination">
											<pg:pager url="center/invite.do" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:prev>
													<li class="disabled">
														<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>
													</li>
												</pg:prev>
												<pg:pages>
													<c:choose>
														<c:when test="${currentPageNumber eq pageNumber }">
															<li class="active">
																<a href="javascript:void(0)">${pageNumber }</a>
															</li>
														</c:when>
														<c:otherwise>
															<li>
																<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a>
															</li>
														</c:otherwise>
													</c:choose>
												</pg:pages>

												<pg:next>
													<li>
														<a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a>
													</li>
												</pg:next>
											</pg:pager>
										</div>
										<div class="clear"></div>
									</td>
								</tr>
							</tfoot>
						</table>
					</div>
				</div>
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
			<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
		<script type="text/javascript">
			//复制邀请链接
			function copyInviteLink() {
				var linkUrl = document.getElementById("inviteCodeLinkArea");
				linkUrl.select();
				document.execCommand("Copy");
				tipAlert("<fmt:message key='msg.pg.userCenter.copySuccess'/>");
			}
			//复制邀请码
			function copyInviteCode() {
				var linkUrl = document.getElementById("inviteCodeArea");
				linkUrl.select();
				document.execCommand("Copy");
				tipAlert("<fmt:message key='msg.pg.userCenter.copySuccess'/>");
			}
		</script>
	<%--<script type="text/javascript">
		 $(window).load(function() {
		 	var err=$("#error").val();
		 	if(err!=""){
		 	    $("#add-wind").show();
		 	 }else{		 	 
		        $("#add-wind").hide();
		      }
	
		  })
		</script>
		
	--%></body>
<script type="text/javascript">
	var imgdata = '';
	function getAlert() {
		$.ajax({
			type : "POST",
			url : "<%=basePath%>center/inviteImage.do",
			data : {
				
			},
			dataType : "json",
			success : function(data) {
				$(".loading-pic").hide();
				imgdata = data.path;
				var matPath = "<%=basePath %>" + data.path;
				var cvswids = $("body").height() - 100;
				var imgwids = cvswids-200;
				$("#down-lav").css("top",cvswids);
				$("#inv-img").css("width","477px");
				$("#inv-img").css("height","850px");
				$("#inv-img").attr("src", matPath);
				$("#invimg-link").attr("href",matPath);
				
			},
			error : function(data) {
					
			}
		}) ;
		
		var inviteBFX = $("#invite-BFX");
		var dpzz = $("#tb-overlay-duiping");
		show(inviteBFX);
		dpzz.show();
	}

	$(".dp-close-alert").click(function() {
		var inviteBFX = $("#invite-BFX");
		var dpzz = $("#tb-overlay-duiping");
		hide(inviteBFX);
		dpzz.hide();
	});
	
	$(".download-btn").click(function() {
		window.open("<%=basePath%>center/downLoadInviteImage.do?filePath=" + imgdata);
	})
	
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
		sureTip : "Sure"
	};
	message["ja_JP"] = {
		warmTip: "ヒント",
		sureTip: "確定",
	};
	message["ko_KR"] = {
		warmTip : "팁",
		sureTip : "결정"
	};
	var MSG = message[locale];
</script>
</html>

