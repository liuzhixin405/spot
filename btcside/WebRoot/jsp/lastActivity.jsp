<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.title.activity"/></title>
		<meta name="keywords" content="${typeCode}<fmt:message key='msg.pg.keywords.activity'/>">
		<meta name="robots" content="index,follow">
		<meta name="googlebot" content="index,follow">
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
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<jsp:include page="_common.jsp"></jsp:include>
	</head>
	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container-trade">
			<div class="main-wrap" style="overflow: visible;">
				<div class="center rink-title">
					<span class="rank-icon"></span><span><fmt:message key="msg.pg.activity.transactionlist"/></span>	
				</div>
				<div class="block-title-trade" style="position: relative;">
					<div class="tab tab-trade lt" id="tab-rink">
						<%-- <span class="<c:if test="${select ne 'week'}">current</c:if>" onclick="selectDay()"><fmt:message key="msg.pg.activity.dayRank"/></span> --%>
						<span class="current" onclick="selectWeek()"><fmt:message key="msg.pg.activity.weekRank"/></span> 
						<span onclick="selectMonth()"><fmt:message key="msg.pg.activity.monthRank"/></span>
      				</div>
      				<div class="activety-sel" style="display: none;" id="record-btn"><fmt:message key="msg.pg.activety.records"/><i class="avtive-arrow"></i>
						<div class="arrow-sele" id="sel-days"></div>
					</div>	
				</div>
				<div class="clear"></div>
				<div class="trade-manage-main table-bg">
					<%-- <div class="activity-bix" <c:if test="${select eq 'week'}">style="display: none;"</c:if>>
						<table class="activity-table" style="margin-right: 10px;">
							<thead>
								<tr>
									<th style="width: 20%"><fmt:message key="msg.pg.activity.ranking"/></th>
									<th style="width: 45%"><fmt:message key="msg.pg.activity.mailbox"/></th>
									<th style="width: 35%"><fmt:message key="msg.pg.activity.yieldRate"/></th>
								</tr>
							</thead>
							<tbody id="frontday">
								<c:forEach var="item" items="${halfOneDay }" varStatus="status">
									<tr style="<c:if test="${status.index+1 == dayRank}">color: #ffa302;</c:if>">
										<td>
											<c:choose>
												<c:when test="${status.index+1 == 1}">
													<c:if test="${status.index+1 == dayRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${dayRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne dayRank}">
													<span class="rinkicon0"></span>
													</c:if>
												</c:when>
												<c:when test="${status.index+1 == 2}">
													<c:if test="${status.index+1 == dayRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${dayRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne dayRank}">
													<span class="rinkicon1"></span>
													</c:if>
												</c:when>
												<c:when test="${status.index+1 == 3}">
													<c:if test="${status.index+1 == dayRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${dayRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne dayRank}">
													<span class="rinkicon2"></span>
													</c:if>
												</c:when>
												<c:otherwise>
													<c:if test="${status.index+1 == dayRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${dayRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne dayRank}">
													<c:out value="${status.index+1 }"></c:out>
													</c:if>
												</c:otherwise>
											</c:choose>
										</td>
										<td>${item.userName }</td>
										<td><fmt:formatNumber value="${item.sumIncome }" pattern="#0.00"/></td>
									</tr>
								</c:forEach>
								<c:if test="${dayRank == 0 && (halfOneDay.size() < 5 || halfTwoDay.size() == 0)}">
									<tr style="color: #ffa302;">
										<td colspan="3">
											<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/><fmt:message key="msg.pg.activety.none"/></span>
										</td>
									</tr>
								</c:if>
							</tbody>
						</table>
						<table class="activity-table">
							<thead>
								<tr>
									<th style="width: 20%"><fmt:message key="msg.pg.activity.ranking"/></th>
									<th style="width: 45%"><fmt:message key="msg.pg.activity.mailbox"/></th>
									<th style="width: 35%"><fmt:message key="msg.pg.activity.yieldRate"/></th>
								</tr>
							</thead>
							<tbody id="backday">
								<c:forEach var="item" items="${halfTwoDay }" varStatus="status">
									<c:choose>
									<c:when test="${status.last && (dayRank == 0 || dayRank > status.index + 6)}">
										<tr style="color: #ffa302;">
											<td colspan="3">
												<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/><c:if test="${dayRank == 0}"><fmt:message key="msg.pg.activety.none"/></c:if><c:if test="${dayRank != 0}">${dayRank}</c:if>
												</span>
											</td>
										</tr>	
									</c:when>
									<c:otherwise>
										<tr style="<c:if test="${status.index+6 == dayRank}">color: #ffa302;</c:if>">
											<td>
												<c:if test="${status.index+6 == dayRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${dayRank }</span>
												</c:if>
												<c:if test="${status.index+6 ne dayRank}">
													<c:out value="${status.index+6 }"></c:out>	
												</c:if>
											</td>
											<td>${item.userName }</td>
											<td><fmt:formatNumber value="${item.sumIncome }" pattern="#0.00"/></td>
										</tr>
									</c:otherwise>
									</c:choose>
								</c:forEach>
								
							</tbody>
						</table>
					</div> --%>
				
					<div class="activity-bix" >
						<table class="activity-table" style="margin-right: 10px;">
							<thead>
								<tr>
									<th style="width: 20%"><fmt:message key="msg.pg.activity.ranking"/></th>
									<th style="width: 45%"><fmt:message key="msg.pg.activity.mailbox"/></th>
									<th style="width: 35%"><fmt:message key="msg.pg.activity.yieldRate"/></th>
								</tr>
							</thead>
							<tbody id="frontweek">
								<c:forEach var="item" items="${halfOneWeek }" varStatus="status">
									<tr style="<c:if test="${status.index+1 == weekRank}">color: #ffa302;</c:if>">
										<td>
											<c:choose>
												<c:when test="${status.index+1 == 1}">
													<c:if test="${status.index+1 == weekRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${weekRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne weekRank}">
													<span class="rinkicon0"></span>
													</c:if>
												</c:when>
												<c:when test="${status.index+1 == 2}">
													<c:if test="${status.index+1 == weekRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${weekRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne weekRank}">
													<span class="rinkicon1"></span>
													</c:if>
												</c:when>
												<c:when test="${status.index+1 == 3}">
													<c:if test="${status.index+1 == weekRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${weekRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne weekRank}">
													<span class="rinkicon2"></span>
													</c:if>
												</c:when>
												<c:otherwise>
													<c:if test="${status.index+1 == weekRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${weekRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne weekRank}">
													<c:out value="${status.index+1 }"></c:out>
													</c:if>
												</c:otherwise>
											</c:choose>
										</td>
										<td>${item.userName }</td>
										<td><fmt:formatNumber value="${item.sumIncome }" pattern="#0.00"/></td>
									</tr>
								</c:forEach>
								<c:if test="${weekRank == 0 && (halfOneWeek.size() < 5 || halfTwoWeek.size() == 0)}">
									<tr style="color: #ffa302;">
										<td colspan="3">
											<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/><fmt:message key="msg.pg.activety.none"/></span>
										</td>
									</tr>
								</c:if>
							</tbody>
						</table>
						<table class="activity-table">
							<thead>
								<tr>
									<th style="width: 20%"><fmt:message key="msg.pg.activity.ranking"/></th>
									<th style="width: 45%"><fmt:message key="msg.pg.activity.mailbox"/></th>
									<th style="width: 35%"><fmt:message key="msg.pg.activity.yieldRate"/></th>
								</tr>
							</thead>
							<tbody id="backweek">
								<c:forEach var="item" items="${halfTwoWeek }" varStatus="status">
									<c:choose>
									<c:when test="${status.last && (weekRank == 0 || weekRank > status.index + 6)}">
										<tr style="color: #ffa302;">
											<td colspan="3">
												<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/><c:if test="${weekRank == 0}"><fmt:message key="msg.pg.activety.none"/></c:if><c:if test="${weekRank != 0}">${weekRank}</c:if>
												</span>
											</td>
										</tr>	
									</c:when>
									<c:otherwise>
										<tr style="<c:if test="${status.index+6 == weekRank}">color: #ffa302;</c:if>">
											<td>
												<c:if test="${status.index+6 == weekRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${weekRank }</span>
												</c:if>
												<c:if test="${status.index+6 ne weekRank}">
													<c:out value="${status.index+6 }"></c:out>	
												</c:if>
											</td>
											<td>${item.userName }</td>
											<td><fmt:formatNumber value="${item.sumIncome }" pattern="#0.00"/></td>
										</tr>
									</c:otherwise>
									</c:choose>
								</c:forEach>
								
							</tbody>
						</table>
					</div>
					
					<div class="activity-bix" style="display: none;">
						<table class="activity-table" style="margin-right: 10px;">
							<thead>
								<tr>
									<th style="width: 20%"><fmt:message key="msg.pg.activity.ranking"/></th>
									<th style="width: 45%"><fmt:message key="msg.pg.activity.mailbox"/></th>
									<th style="width: 35%"><fmt:message key="msg.pg.activity.yieldRate"/></th>
								</tr>
							</thead>
							<tbody id="frontmonth">
								<c:forEach var="item" items="${halfOneMonth }" varStatus="status">
									<tr style="<c:if test="${status.index+1 == monthRank}">color: #ffa302;</c:if>">
										<td>
											<c:choose>
												<c:when test="${status.index+1 == 1}">
													<c:if test="${status.index+1 == monthRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${monthRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne monthRank}">
													<span class="rinkicon0"></span>
													</c:if>
												</c:when>
												<c:when test="${status.index+1 == 2}">
													<c:if test="${status.index+1 == monthRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${monthRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne monthRank}">
													<span class="rinkicon1"></span>
													</c:if>
												</c:when>
												<c:when test="${status.index+1 == 3}">
													<c:if test="${status.index+1 == monthRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${monthRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne monthRank}">
													<span class="rinkicon2"></span>
													</c:if>
												</c:when>
												<c:otherwise>
													<c:if test="${status.index+1 == monthRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${monthRank }</span>
													</c:if>
													<c:if test="${status.index+1 ne monthRank}">
													<c:out value="${status.index+1 }"></c:out>
													</c:if>
												</c:otherwise>
											</c:choose>
										</td>
										<td>${item.userName }</td>
										<td><fmt:formatNumber value="${item.sumIncome }" pattern="#0.00"/></td>
									</tr>
								</c:forEach>
								<c:if test="${monthRank == 0 && (halfOneMonth.size() < 5 || halfTwoMonth.size() == 0)}">
									<tr style="color: #ffa302;">
										<td colspan="3">
											<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/><fmt:message key="msg.pg.activety.none"/></span>
										</td>
									</tr>
								</c:if>
							</tbody>
						</table>
						<table class="activity-table">
							<thead>
								<tr>
									<th style="width: 20%"><fmt:message key="msg.pg.activity.ranking"/></th>
									<th style="width: 45%"><fmt:message key="msg.pg.activity.mailbox"/></th>
									<th style="width: 35%"><fmt:message key="msg.pg.activity.yieldRate"/></th>
								</tr>
							</thead>
							<tbody id="backmonth">
								<c:forEach var="item" items="${halfTwoMonth }" varStatus="status">
								<c:choose>
									<c:when test="${status.last && (monthRank == 0 || monthRank > status.index + 6)}">
										<tr style="color: #ffa302;">
											<td colspan="3">
												<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/><c:if test="${monthRank == 0}"><fmt:message key="msg.pg.activety.none"/></c:if><c:if test="${monthRank != 0}">${monthRank}</c:if></span>
											</td>
										</tr>	
									</c:when>
									<c:otherwise>
										<tr style="<c:if test="${status.index+6 == monthRank}">color: #ffa302;</c:if>">
											<td>
												<c:if test="${status.index+6 == monthRank}">
													<span class="myrink"><fmt:message key="msg.pg.activity.myRank"/>${monthRank }</span>
												</c:if>
												<c:if test="${status.index+6 ne monthRank}">
													<c:out value="${status.index+6 }"></c:out>
												</c:if>
											</td>
											<td>${item.userName }</td> 
											<td><fmt:formatNumber value="${item.sumIncome }" pattern="#0.00"/></td>
										</tr>
									</c:otherwise>
									</c:choose>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>	
			</div>
			<div class="main-wrap" style="overflow: visible;">
				<div class="center rink-title">
					<span class="rank-icon"></span><span><fmt:message key="msg.pg.activity.particulars"/></span>	
				</div>
				<jsp:include page="${locale}/activityContent.jsp" />
			</div>	
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>	
	</body>
	<script>
		$('#tab-rink span').click(function() {
			if($(this).index() == '1') {
				$(".activety-sel").hide();
			}
			$(this).addClass("current").siblings().removeClass();
			$(".trade-manage-main .activity-bix").hide().eq($('#tab-rink span').index(this)).show();
		});
		
		function selectDay() {
			var nowTime = new Date().getTime() / 1000;
			var startTime = new Date("2019-08-09 15:00:00").getTime() / 1000;
			if (nowTime > startTime) {
				var itemTime = new Date("2019-08-09 15:00:00").getTime() / 1000;
				var staHtmls = "";
				while (itemTime < nowTime) {
					var color = "";
					if (itemTime == "${day}") {
						color = " style='color: #4980E3;'";
					}
					staHtmls = "<a" + color + " href='<%=basePath%>activity/ranking.do?day=" + itemTime + "&&week=${week}&&select=day'>" 
					+ new Date(itemTime * 1000).Format("yyyy.MM.dd") + "</a>" + staHtmls;
					itemTime += 86400;
				}
				
				var color = "";
				if ("" == "${day}" || "0" == "${day}") {
					color = " style='color: #4980E3;'";
				}
				
				staHtmls = "<a" + color + " href='<%=basePath%>activity/ranking.do?day=0&&week=${week}&&select=day'>" + MSG['current'] + "</a>" + staHtmls;
				$("#record-btn").css("display", "block");
				$("#sel-days").html(staHtmls);
				
				if($(".arrow-sele").height() >= '320') {
					$(".arrow-sele").css("overflow-y","scroll");	
				}
				
			} else {
				$("#record-btn").css("display", "none");
			}
		}
		
		function selectMonth() {
			$("#record-btn").css("display", "none");
		}
		
		function selectWeek() {
			var nowTime = new Date().getTime() / 1000;
			var baseTime = new Date("2019-08-09 15:00:00").getTime() / 1000;
			var startTime = new Date("2019-08-09 15:00:00").getTime() / 1000;
			if (nowTime > startTime) {
				var itemTime = baseTime + 7 * 86400;
				var index = 1;
				var staHtmls = "";
				while (itemTime < nowTime) {
					var color = "";
					if (itemTime == "${week}") {
						color = " style='color: #4980E3;'";
					}
					staHtmls = "<a" + color + " href='<%=basePath%>activity/ranking.do?day=${day}&&week=" + itemTime + "&&select=week'>" + MSG['num' + index] + "</a>" + staHtmls;
					itemTime += 7 * 86400;
					index++;
				}
				
				var color = "";
				if ("" == "${week}" || "0" == "${week}") {
					color = " style='color: #4980E3;'";
				}
				
				staHtmls = "<a" + color + " href='<%=basePath%>activity/ranking.do?day=${day}&&week=0&&select=week'>" + MSG['current'] + "</a>" + staHtmls;
				
				$("#record-btn").css("display", "block");
				$("#sel-days").html(staHtmls);
				
				if($(".arrow-sele").height() >= '320') {
					$(".arrow-sele").css("overflow-y","scroll");	
				}
			} else {
				$("#record-btn").css("display", "none");
			}
		}
		
		$(function() {
			selectWeek();	
		})
		
	var message = {};
	message["zh_CN"] = {
		current : "当前",
		num1: "第1周",
		num2: "第2周",
		num3: "第3周",
		num4: "第4周",
		num5: "第5周",
		num6: "第6周",
		num7: "第7周",
		num8: "第8周",
	};
	message["zh_HK"] = {
		current : "當前",
		num1: "第1周",
		num2: "第2周",
		num3: "第3周",
		num4: "第4周",
		num5: "第5周",
		num6: "第6周",
		num7: "第7周",
		num8: "第8周",
	};
	message["en_US"] = {
		current : "Current",
		num1: "First week",
		num2: "Second weeks",
		num3: "Third weeks",
		num4: "Fourth weeks",
		num5: "Fifth weeks",
		num6: "Sixth weeks",
		num7: "Seventh weeks",
		num8: "Eighth weeks",
	};
	message["ja_JP"] = {
		current : "現在",
		num1: "第1週",
		num2: "第2週",
		num3: "第3週",
		num4: "第4週",
		num5: "第5週",
		num6: "第6週",
		num7: "第7週",
		num8: "第8週",
		
	};
	message["ko_KR"] = {
		current : "현재",
		num1: "1 주차",
		num2: "2 주차",
		num3: "3 주차",
		num4: "4 주차",
		num5: "5 주차",
		num6: "6 주차",
		num7: "7 주차",
		num8: "8 주차",
	};
	message["ru_RU"] = {
		current : "Текущая",
		num1: "Первая неделя",
		num2: "Вторая неделя",
		num3: "Третья неделя",
		num4: "Четвертая неделя",
		num5: "Пятая неделя",
		num6: "Шестая неделя",
		num7: "Седьмая неделя",
		num8: "Восьмая неделя",
	};
	var MSG = message[locale];
	</script>
</html>