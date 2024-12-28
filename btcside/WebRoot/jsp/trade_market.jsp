<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>${lastPrice }USDT <fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.tradeMarket.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript">
		$(function(){	
			$(window).scroll(function() {		
				if($(window).scrollTop() >= 100){
					$('.actGotop').fadeIn(300); 
				}else{    
					$('.actGotop').fadeOut(300);    
				}  
			});
			$('.actGotop').click(function(){$('html,body').animate({scrollTop: '0px'}, 800);});	
		});
		</script>
		<style type="text/css">
		/* actGotop */
		.actGotop{position:fixed;right:20px;bottom:10px;width:45px;height:45px;display:none;}
		*html .actGotop{position:absolute;top:expression(eval(document.documentElement.scrollTop));}
		.actGotop a,.actGotop a:link{width:45px;height:45px;display:inline-block;background:url(<%=basePath%>common/images/goup.png) no-repeat;_background:url(<%=basePath%>common/images/goup.png) no-repeat;outline:none;}
		.actGotop a:hover{width:45px;height:45px;background:url(<%=basePath%>common/images/goup.png) no-repeat;outline:none;}
		
		</style>
	</head>

	<body>

		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont">
			<div style="padding: 20px;">
			  <div class="main-wrap">
			  	<div class="trade-title-lt">
						<b><fmt:message key="msg.pg.market.selectTransaction"/>：</b>
						<c:forEach var="transaction" items="${transactionList}">
							<a href="javascript:;" name='${transaction.transaction_code}'
								onclick="changeTransaction(this.name);"
								<c:if test="${transaction.transaction_code eq currentTransaction.transaction_code}" > class="current" </c:if>>
								${fn:toUpperCase(transaction.transaction_code)}</a>
						</c:forEach>
						<input type="hidden" id="transaction_code" name="transaction_code" value="${currentTransaction.transaction_code}" />
					</div><%--
					<div class="price-box clearfix mt10">
	          <div class="price-block lt">
	          </div>
        </div>
			  --%></div>
				<div class="main-wrap clearfix">
				 <div class="main-data-block1">
						<div class="block-title">
							<h2><fmt:message key="msg.pg.index.marketPending"/>
							</h2>
							

						</div>
						<div class="trade-manage-main1">
							<table cellpadding="0" cellspacing="0" class="lt">
								<thead>
									<tr>

										<th>
											<fmt:message key="msg.pg.index.total"/>
										</th>
										<th>
											<fmt:message key="msg.pg.index.amount"/>
										</th>
										<th>
											<fmt:message key="msg.pg.index.burPrice"/>
										</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="asks" items="${asks}" varStatus="states">
										<tr>
											<td>
												<c:set value="${sum+asks.countNum}"  var="sum"/>
												<c:choose>
													<c:when test="${issz}">
														<fmt:formatNumber value="${sum}" pattern="#0"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${sum}" pattern="#0.##"/>
													</c:otherwise>
												</c:choose>
											</td>
											<td style="${asks.priority == 3 ? "color:orange" : "" }">
												<c:choose>
													<c:when test="${issz}">
														<fmt:formatNumber value="${asks.countNum}" pattern="#0"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${asks.countNum}" pattern="#0.##"/>
													</c:otherwise>
												</c:choose>
											</td>
											<td class="buy">
												<c:choose>
													<c:when test="${issz}">
														<fmt:formatNumber value="${asks.price}" pattern="#0.######"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${asks.price}" pattern="#0.######"/>
													</c:otherwise>
												</c:choose>
											</td>

										</tr>
									</c:forEach>
								</tbody>
							</table>
							<table cellpadding="0" cellspacing="0" class="rt">
								<thead>
									<tr>

										<th>
											<fmt:message key="msg.pg.index.sellPrice"/>
										</th>
										<th>
											<fmt:message key="msg.pg.index.amount"/>
										</th>
										<th>
											<fmt:message key="msg.pg.index.total"/>
										</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="bids" items="${bids}" varStatus="states">
										<tr>
											<td class="sell">
												<c:choose>
													<c:when test="${issz}">
														<fmt:formatNumber value="${bids.price}" pattern="#0.######"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${bids.price}" pattern="#0.######"/>
													</c:otherwise>
												</c:choose>
											</td>
											<td style="${bids.priority == 3 ? "color:orange;" : "" }">
												<c:choose>
													<c:when test="${issz}">
														<fmt:formatNumber value="${bids.countNum}" pattern="#0"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${bids.countNum}" pattern="#0.##"/>
													</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:set value="${total+bids.countNum}"  var="total"/>
												<c:choose>
													<c:when test="${issz}">
														<fmt:formatNumber value="${total}" pattern="#0"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${total}" pattern="#0.00"/>
													</c:otherwise>
												</c:choose>
											</td>

										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					<div class="main-data-block">
						<div class="block-title">

							<h2>
								<fmt:message key="msg.pg.index.tradeRecode"/>
							</h2>

						</div>
						<div class="trade-manage-main">
							<table cellpadding="0" cellspacing="0">
								<thead>
									<tr>
										<th>
											<fmt:message key="msg.pg.index.recordTime"/>
										</th>
										<th>
											<fmt:message key="msg.pg.index.dealPrice"/>
										</th>
										<th>
											<fmt:message key="msg.pg.index.dealAmounts"/>
										</th>
									</tr>
								</thead>
								<tbody>
									<c:choose>
										<c:when test="${listtrade!=null}">
											<c:forEach var="litr" items="${listtrade}">
												<tr>
													<td >
														<ut:formatDate dateline="${litr.trade_time}" pattern="MM-dd HH:mm"></ut:formatDate>
													</td>
													<td >
														<span class="<c:choose><c:when test="${litr.trade_trans_dirction==1}">buy</c:when><c:when test="${litr.trade_trans_dirction==2}">sell</c:when></c:choose>">
															<c:choose>
																<c:when test="${issz}">
																	<fmt:formatNumber value="${litr.trade_average_price}" pattern="#0.######"/>
																</c:when>
																<c:otherwise>
																	<fmt:formatNumber value="${litr.trade_average_price}" pattern="#0.######"/>
																</c:otherwise>
															</c:choose>
														</span>
													</td>
													<td >
														<c:choose>
															<c:when test="${issz}">
																<fmt:formatNumber value="${litr.trade_count}" pattern="#0"/>
															</c:when>
															<c:otherwise>
																<fmt:formatNumber value="${litr.trade_count}" pattern="#0.##"/>
															</c:otherwise>
														</c:choose>	
													</td>

												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<tr>
												<td colspan="5" height="30" align="center" bgcolor="#FFFFFF">
													<fmt:message key="msg.pg.tradeMarket.noTrade"/>
												</td>
											</tr>
										</c:otherwise>
									</c:choose>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
		<div class="actGotop"><a href="javascript:;" title="Top"></a></div>
		<script type="text/javascript">
		
		function changeTransaction(name) {
			document.location.href = 'market.do?transactionCode=' + name;
		}
		
		</script>
	</body>

</html>