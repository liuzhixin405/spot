<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.index.newsInfo"/></title>
		<link type="text/css" rel="stylesheet"
			href="<%=basePath%>common/css/style.css?<%=rjs%>" />
			<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont">
			<jsp:include page="_help_left.jsp"></jsp:include>
			<div class="main-rt-box">
				<div class="info-box">
					<div class="news-deatil">
						<h1>
							<fmt:message key="msg.pg.footer.fees"/>
						</h1>
					</div>
				</div>
				<div class="main-wrap">
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th><fmt:message key="msg.pg.userCenter.userLev"/></th>
								    <th><fmt:message key="msg.pg.userCenter.intergelCodi"/></th>
									<th><fmt:message key="msg.pg.userCenter.putOrder"/></th>
								    <th><fmt:message key="msg.pg.userCenter.eatOrder"/></th>
								    <th><fmt:message key="msg.pg.userCenter.withdrawCharge"/></th>
								    <th><fmt:message key="msg.pg.userCenter.returnInvite"/></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${levelRles}" var="l" varStatus="status">
									<tr>
										<td>
											<c:out value="${l.level_name }"></c:out>
										</td>
										<td class="lev-tdans">
											${l.level_credits }
										</td>
										<td>
											<fmt:formatNumber value="${l.pending_commission_rate }" type="percent" pattern="0.000%"/>
										</td>
										<td>
											<fmt:formatNumber value="${l.eat_commission_rate }" type="percent" pattern="0.000%"/>
										</td>
										<td>
											${l.withdraws_cash }
										</td>
										<td>
											<fmt:formatNumber value="${l.broker_commission_rate }" type="percent" pattern="0.000%"/>
										</td>
									</tr>
								</c:forEach>
									
							</tbody>
						</table>
					</div>	
				</div>
				<div class="main-wrap">
					<div class="block-title">
					  <h2><fmt:message key="msg.pg.userCenter.inviteRebate"/></h2>
					</div>
					<div class="box">
					  <div class="vip-info">
					    <p>
					      1.<fmt:message key="msg.pg.userCenter.inviteRebateDirect"/>；
					    </p>
					    <p>2.<fmt:message key="msg.pg.userCenter.inviteRebateIndirect"/>。</p>
					  </div>
					</div>
        		</div>
				<div class="main-wrap">
					<div class="block-title">
					  <h2><fmt:message key="msg.pg.userCenter.ruleIntergel"/></h2>
					</div>
					<div class="box">
					  <div class="vip-info">
					    <p>
					      1.<fmt:message key="msg.pg.userCenter.notReset"/>；
					    </p>
					    <p>2.<fmt:message key="msg.pg.userCenter.Contractcomsion"/>：<fmt:message key="msg.pg.userCenter.actualNO"/>（<fmt:message key="msg.pg.userCenter.money"/>）<fmt:message key="msg.pg.userCenter.collect"/>；</p>
					    <p>3.<fmt:message key="msg.pg.userCenter.activeilyReward"/>；</p>
					    <p>4.<fmt:message key="msg.pg.userCenter.platformAdjust"/>。</p>
					
					  </div>
					</div>
        		</div>
        		<div class="main-wrap">
					<div class="block-title">
						<h2>
							<fmt:message key="msg.pg.userCenter.detailsIntergel"/>
						</h2>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										<fmt:message key="msg.pg.trade.option"/>
									</th>
									<th>
										<fmt:message key="msg.pg.userCenter.cumtrapz"/>
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
								  <td><fmt:message key="msg.pg.userCenter.openPostionNO"/></td>
								  <td><fmt:message key="msg.pg.userCenter.perOpenPostion"/>，<fmt:message key="msg.pg.userCenter.plusIntergel"/></td>
								</tr>
								<tr>
								  <td><fmt:message key="msg.pg.userCenter.netAsset"/></td>
								  <td><fmt:message key="msg.pg.userCenter.retainInAccount"/>，<fmt:message key="msg.pg.userCenter.plusIntergel"/></td>
								</tr>
								<tr>
								<th><fmt:message key="msg.pg.trade.option"/></th>
								<th><fmt:message key="msg.pg.userCenter.oneTime"/></th>
								</tr>
								<tr>
									<td>
										<fmt:message key="msg.pg.userCenter.enroll"/>
									</td>
									<td>
										+20<fmt:message key="msg.pg.userCenter.intergelCried"/>
									</td>
								</tr>
								<tr>
									<td>
										<fmt:message key="msg.pg.userCenter.validEmail"/>
									</td>
									<td>
										+10<fmt:message key="msg.pg.userCenter.intergelCried"/>
									</td>
								</tr>
								<tr>
									<td>
										<fmt:message key="msg.pg.userCenter.googleAuthcate"/>
									</td>
									<td>
										+10<fmt:message key="msg.pg.userCenter.intergelCried"/>
									</td>
								</tr>
								<tr>
									<td>
										<fmt:message key="msg.pg.userCenter.bindCell"/>
									</td>
									<td>
										+10<fmt:message key="msg.pg.userCenter.intergelCried"/>
									</td>
								</tr>
								
								<tr>
									<td>
										<fmt:message key="msg.pg.userCenter.firstCharge"/>
									</td>
									<td>
										+25<fmt:message key="msg.pg.userCenter.intergelCried"/>
									</td>
								</tr>
								<tr>
									<td>
										<fmt:message key="msg.pg.userCenter.firstTrade"/>
									</td>
									<td>
										+40<fmt:message key="msg.pg.userCenter.intergelCried"/>
									</td>
								</tr>
								<tr>
								  <td><fmt:message key="msg.pg.userCenter.useSuggest"/></td>
								  <td>+100<fmt:message key="msg.pg.userCenter.intergelTme"/></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
			<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>

