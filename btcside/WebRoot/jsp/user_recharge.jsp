<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
        <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title><fmt:message key="msg.pg.systemName"/>-BTC<fmt:message key="msg.pg.userCenter.recharged"/></title>
        <link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
        <script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
        <script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/clipboard.min.js"></script>
		<script type="text/javascript">
		
	  	 function copyInit() {		
				var linkUrl = document.getElementById("rechargeArea");
				linkUrl.select();
				document.execCommand("Copy");
				tipAlert("<fmt:message key='msg.pg.userCenter.copy'/>");
				
				var text= document.getElementById('copyNum').value
				var button="btnCopy";
				var msg="<fmt:message key='msg.pg.userCenter.copy'/>";
				
				var clipboard = new ClipboardJS('#copyNum');
				clipboard.on('success', function(e) {	
				    e.clearSelection();
				    tipAlert(msg);
				});
				
			}
	</script>
	<jsp:include page="_common.jsp"></jsp:include>
	</head>

	<body>
	<div class="add-windows" id="alert-windows"></div>
	<!-- 子账户不能充值提示 -->
	<c:if test="${isSubUser == true}">
		<div id="add-windows" class="add-windows">
			<div id="tb-overlay" class="tb-overlay"></div>
			<div class="add-wrap" style="display:block;">
				<div class="dialogs-title">
					<h2 class="lt"><fmt:message key="msg.pg.top.recharge"/></h2>
					<a href="javascript:history.go(-1)" class="close-icon icons-close" id="close-alert"></a>
				</div>
				<div class="dialogs-body">
					<p style="margin-bottom:30px; text-align:center;font-size:16px;">
						子账户不能充值!
					</p>
					<div class="form-group-btn" style="display: flex; justify-content: center">
						<a href="javascript:history.go(-1)" class="btn sub-btn-return"><fmt:message key="msg.pg.trade.goBack"/></a>
					</div>
				</div>
			</div>
		</div>
	</c:if>
	<div class="add-wind" id="add-wind">
		<div id="tb-overlay"></div>
		<input type="hidden" name="addresserror" id="addresserror"
			value="${addresserror}" />
		<div class="add-wrap">
			<div class="block-title">
				<h2 class="lt">
					<fmt:message key="msg.pg.userCenter.noChargeAddress" />
				</h2>
			</div>
			<div class="main-wrap">
				<a href=""></a>
			</div>
		</div>
	</div>

	<jsp:include page="_top.jsp"></jsp:include>
	<jsp:include page="_head.jsp"></jsp:include>
	<!-- 主体内容 -->
	<div class="container center-cont">
		<jsp:include page="_user_left.jsp"></jsp:include>
		
		<div class="main-rt-box">
			<div class="main-wrap">
				<div class="block-title">

					<h2>
						${coinName }
						<fmt:message key="msg.pg.userCenter.recharged" />
					</h2>
				</div>
				<div class="box">
					<c:if test="${coinName eq 'USDT'}">
						<div class="tab" style="margin-bottom: 20px;">
							<a href="<%=basePath %>center/recharge.do?coinId=${coinId}&trc20=1" class="${trc20 == 1 ? 'current' : ''}" style="border-width: 1px;">TRC20</a>
							<a href="<%=basePath %>center/recharge.do?coinId=${coinId}&omni=0" class="${omni == 0 ? 'current' : ''}" style="border-width: 1px;">ERC20</a>
						</div>
					</c:if>
					<c:if test="${coinName eq 'BTC'}">
						<div class="tab" style="margin-bottom: 20px;">
							<a href="<%=basePath %>center/recharge.do?coinId=${coinId}" class="current" style="border-width: 1px;">HECO</a>
						</div>
					</c:if>
				
					<div class="box-lt lt">
						<p>
							<fmt:message key="msg.pg.userCenter.remitAddressFrt" />${coinName }<fmt:message
								key="msg.pg.userCenter.remitAddressBak" />
							：
						</p>
						
						<p>
							<input type="hidden" id="copyNum" value="${address}" /> <b>${address}</b>
							<a href="javascript:void(0)" class="btn btn-blue" id="btnCopy" style="margin-left: 15px;" onclick="javascript:copyInit();">
								<fmt:message key="msg.pg.userCenter.replcation" />
							</a>
						</p>
						<c:if test="${not empty address}">
							<textarea class="recharge-area" id="rechargeArea">${address}</textarea>
						</c:if>
						<br />
						<p>
							<fmt:message key="msg.pg.userCenter.afterSended" />
							，
							<fmt:message key="msg.pg.userCenter.systemHere" />
							<b>1</b>
							<fmt:message key="msg.pg.userCenter.thisLapire" />
							${coinName }
							<fmt:message key="msg.pg.userCenter.enterYouAccount" />
							。
						</p>

					</div>
					<div class="box-rt rt">
						<img src="<%=basePath%>${pic}" alt="" />
					</div>
				</div>
			</div>
			<div class="main-wrap">
				<div class="block-title">
					<h2 class="lt">
						<fmt:message key="msg.pg.userCenter.lastTenRecord" />
					</h2>
				</div>

				<div class="trade-manage-main">
					<table cellpadding="0" cellspacing="0">
						<thead>
							<tr>
								<th><fmt:message key="msg.pg.userCenter.orderNum" /></th>
								<th><fmt:message key="msg.pg.userCenter.prepaidTim" /></th>
								<th><fmt:message key="msg.pg.userCenter.prepaidAddress" />
								</th>
								<th><fmt:message key="msg.pg.userCenter.prepaidMoney" /></th>
								<th><fmt:message key="msg.pg.userCenter.prepaidState" /></th>
								<!-- 
									<th>
										<fmt:message key="msg.pg.trade.option"/>
									</th>
									-->
							</tr>
						</thead>
						<tbody>
							<tr>
								<c:choose>
									<c:when test="${pm.datas != null}">
										<c:forEach var="rech" items="${pm.datas}">
											<tr>
												<td><c:out value="${rech.record_id}" /></td>
												<td><span> <ut:formatDate
															dateline="${rech.dateline}"></ut:formatDate>
												</span></td>
												<td><c:out value="${rech.receice_card_no}" /></td>
												<td><c:out value="${rech.amount}" /></td>
												<td><c:choose>
														<c:when test="${rech.state==0}">
															<span class="sell"><fmt:message
																	key="msg.pg.userCenter.notAccept" /></span>
														</c:when>
														<c:when test="${rech.state==1}">
															<span class="buy"><fmt:message
																	key="msg.pg.userCenter.hadAccept" /></span>
														</c:when>
														<c:when test="${rech.state==2}">
															<span class="sell"><fmt:message
																	key="msg.pg.userCenter.refuse" />(<c:out
																	value="${rech.remark}"></c:out>)</span>
														</c:when>
													</c:choose></td>
												<!--
													<td>
													<a target="_blank" href="http://www.blockmeta.info/address/${rech.receice_card_no}"><fmt:message key="msg.pg.userCenter.quereState"/> </a>
													</td>
													-->
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="5" height="30" align="center" bgcolor="#FFFFFF">
												<fmt:message key="msg.pg.userCenter.noHistory" />。
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tr>
						</tbody>
					</table>
				</div>

				<ul class="pagination">
					<pg:pager url="${pageN}" items="${pm.total}" maxIndexPages="10"
						maxPageItems="${pm.maxitems}"
						export="currentPageNumber=pageNumber">

						<pg:param name="coinId" value="${coinId }" />

						<pg:prev>
							<li class="disabled"><a href="<%=basePath%>${pageUrl}"
								mce_href="<%=basePath%>${pageUrl}"><fmt:message
										key="msg.pg.paging.previous" /></a></li>
						</pg:prev>

						<pg:pages>
							<c:choose>
								<c:when test="${currentPageNumber eq pageNumber }">
									<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
								</c:when>
								<c:otherwise>
									<li><a href="<%=basePath%>${pageUrl }"
										mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
								</c:otherwise>
							</c:choose>
						</pg:pages>

						<pg:next>
							<li><a href="<%=basePath%>${pageUrl }"
								mce_href="<%=basePath%>${pageUrl }"><fmt:message
										key="msg.pg.paging.next" /></a></li>
						</pg:next>
					</pg:pager>
				</ul>
			</div>

			<c:if test="${mineRecords != null}">
				<div class="main-wrap">
					<div class="block-title">
						<h2 class="lt">
							<fmt:message key="msg.pg.userCenter.mineRecord" />
						</h2>
						<div class="trade-control relative">
							<a href="mineRecordsMore.do"><fmt:message
									key="msg.normal.more" /></a>
						</div>
						<div class="clear"></div>
					</div>
					
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th><fmt:message key="msg.pg.index.recordTime" /></th>
									<th><fmt:message key="msg.pg.userCenter.tradeContract" />
									</th>
									<th><fmt:message key="msg.pg.userCenter.tradeFee" /></th>
									<th><fmt:message key="msg.pg.userCenter.mineAmount" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="record" items="${mineRecords}">
									<tr>
										<td><ut:formatDate dateline="${record.record_time}"></ut:formatDate>
										</td>
										<td><c:out value="${record.trade_type}" /></td>
										<td><c:out value="${record.trade_fee}" /></td>
										<td><c:out value="${record.mine_amount}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</c:if>

			<c:if test="${unlockRecords != null}">
				<div class="main-wrap">
					<div class="block-title">
						<h2 class="lt">
							<fmt:message key="msg.pg.userCenter.unlockRecord" />
						</h2>
					</div>

					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th><fmt:message key="msg.pg.index.recordTime" /></th>
									<th><fmt:message key="msg.pg.userCenter.unlockRate" /></th>
									<th><fmt:message key="msg.pg.userCenter.unlockAmount" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="record" items="${unlockRecords}">
									<tr>
										<td><c:out value="${record.day}" /></td>
										<td><c:out value="${record.unlock_rate}" /></td>
										<td><c:out value="${record.amount}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</c:if>
			
			<c:if test="${rewards != null}">
				<div class="main-wrap">
					<div class="block-title">
						<h2 class="lt">
							<fmt:message key="msg.pg.userCenter.rewardRecord" />
						</h2>
					</div>

					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th><fmt:message key="msg.pg.index.recordTime" /></th>
									<th><fmt:message key="msg.pg.userCenter.money" /></th>
									<th><fmt:message key="msg.pg.trade.stated" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="record" items="${rewards}">
									<tr>
										<td><ut:formatDate dateline="${record.record_time}"></ut:formatDate></td>
										<td><c:out value="${record.amount.doubleValue()}" /></td>
										<td><c:out value="${record.note}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</c:if>

		</div>
	</div>
	<div class="clear"></div>
	<jsp:include page="_footer.jsp"></jsp:include>
	<script type="text/javascript">
		
			$(window).load(function() {		
				var err = $("#addresserror").val();		
				if (err != "") {
					$("#add-wind").show();
			
				} else {
					$("#add-wind").hide();
				}
			
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

	</body>

</html>
