<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.tradeQuery.title"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container center-cont" style="padding: 10px;">
			<div class="main-wrap">
				<div class="block-title">
					<h2>
						${email }---<fmt:message key="msg.pg.userCenter.positionInfo"/>
					</h2>
					<div class="trade-title-lt rt">
						<a href="<%=basePath%>center/positionInformation.do?code=${currentTransaction.transaction_code }&type=hold&inviteeId=${inviteeId}" <c:if test="${type eq 'hold'}"> class="current"</c:if>><fmt:message key="msg.pg.userCenter.positionHowMany"/></a> 
						<%-- <a href="<%=basePath%>center/positionInformation.do?code=${currentTransaction.transaction_code }&type=pcrecord&inviteeId=${inviteeId}" <c:if test="${type eq 'pcrecord'}"> class="current"</c:if>><fmt:message key="msg.pg.userCenter.positionCloseRecode"/></a> --%>
					</div>
					<form action="<%=basePath%>center/positionInformation.do?code=${currentTransaction.transaction_code }" name="tiaojian">
						<input hidden="hidden" name="inviteeId" value="${inviteeId }">
						<input hidden="hidden" name="type" value="${type }">
						<div class="selected rt" style="margin-right: 15px; margin-top: 17px;">
							<select name="code" id="transactionCode" onchange="javascript:document.tiaojian.submit();">
								<c:forEach var="ta" items="${transactionList}">
									<c:if test="${ta.transaction_code eq currentTransaction.transaction_code}">
										<option value="${ta.transaction_code}" selected="selected">
													${fn:toUpperCase(ta.transaction_code)} <fmt:message key="msg.pg.index.contract"/>
												</option>
									</c:if>
									<c:if test="${ta.transaction_code ne currentTransaction.transaction_code}">
										<option value="${ta.transaction_code}">
													${fn:toUpperCase(ta.transaction_code)} <fmt:message key="msg.pg.index.contract"/>
												</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
					</form>
				</div>
				<div class="trade-manage-main">
				  <c:if test="${memberTransaction != null}">
				  
			      <div class="trade-cont">
			        <table>
			          <thead>
			            <tr>
			              <th><fmt:message key="msg.pg.trade.genre"/></th>
			              <th><fmt:message key="msg.pg.trade.aviablePosition"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
			              <c:if test="${isMarket == null }">
			              <th><fmt:message key="msg.pg.trade.freezen"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
			              </c:if>
			              <th><fmt:message key="msg.pg.trade.averagePrice"/></th>
			              <th class="relative"><fmt:message key="msg.pg.trade.triggerPrice"/>
			              <!-- <em class="que-icon icons-que th-help bcj-txt" id="bcj-help"></em> -->
			              </th>
			              <c:if test="${isMarket == null }">
			              <th><fmt:message key="msg.pg.trade.depositCash"/></th>
			              </c:if>
			              <th class="relative"><fmt:message key="msg.pg.trade.floatProfit"/>
			              </th>
			              <c:if test="${isMarket == null }">
			              <th><fmt:message key="msg.pg.trade.profitRatio"/></th>
			               </c:if>
			              <%-- <th style="width:70px;"><fmt:message key="msg.pg.trade.exitPrice"/></th>
			              <th style="width:80px;"><fmt:message key="msg.pg.trade.numberPositions"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> --%>
			            </tr>
			          </thead>
			          <tbody id="depotbuyTbody">
			            <tr id="alert_tr_pc_buy" style="display: none;">
			              <td colspan="12"><div class="trade-alert" style="margin-top: 0;">
			                  <div class="success" id="infomsg_d"></div>
			                </div>
			              </td>
			            </tr>
			            <c:if test="${memberTransaction == null || empty memberTransaction}">
			              <tr>
				            <c:if test="${sessionScope.userinfo==null}">
				            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do" class="full-a-link"><fmt:message key="msg.pg.top.login"/></a> </td>
				            </c:if>
			            	<c:if test="${sessionScope.userinfo!=null}">
			            		<td colspan="12"> <fmt:message key="msg.pg.activety.none"/> </td>
			            	</c:if>
			              </tr>
			            </c:if>
			            <c:forEach items="${memberTransaction}" var="tran" varStatus="l">
			              <tr>
			               <%--  <td><em class="${tran.direct == 1? 'buy':'sell' }"> </em> x${tran.multiple} </td> --%>
			                <td>
			                	<c:choose>
									<c:when test="${tran.trade_type_id eq 1}">
										<b class="buy"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></b>
									</c:when>
									<c:when test="${tran.trade_type_id eq 2}">
										<b class="sell"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></b>
									</c:when>
								</c:choose>
			                </td>
			                <td ><fmt:formatNumber value="${tran.holdCount}"></fmt:formatNumber></td>
			                <c:if test="${isMarket == null }">
			                	<td ><fmt:formatNumber value="${tran.lockCount}"></fmt:formatNumber></td>
			                </c:if>
			                <td ><fmt:formatNumber value="${tran.averagePrice}"></fmt:formatNumber></td>
			                <td ><fmt:formatNumber value="${tran.qpPrice}"></fmt:formatNumber></td>
			                <c:if test="${isMarket == null }">
			                <td ><fmt:formatNumber value="${tran.deposit}" pattern="#.####"></fmt:formatNumber></td>
			                </c:if>
			                <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
			                  </fmt:formatNumber></td>
			                 <c:if test="${isMarket == null }">
			                <td > ${tran.profitLossRatio } </td>
			                 </c:if>
			              </tr>
			            </c:forEach>
			          </tbody>
			        </table>
			      </div>
			      </c:if>
			      <c:if test="${tradeLogList.datas != null && tradeLogList.datas != ''}">
			      <div class="trade-cont">
			       <table>
			          <thead>
			           <tr>
							<th>
								<fmt:message key="msg.pg.tradeQuery.dealTime"/>
							</th>
							<th>
								<fmt:message key="msg.pg.trade.contractNO"/>
							</th>
							<th>
								<fmt:message key="msg.pg.tradeQuery.dealType"/>
							</th>
							<th>
								<fmt:message key="msg.pg.tradeQuery.holdAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
							</th>
							<th>
								<fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
							</th>
							<th>
								<fmt:message key="msg.pg.tradeQuery.dealAmount"/>
							</th>
							<th>
								<fmt:message key="msg.pg.trade.depositCash"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
							</th>
							<th>
								<fmt:message key="msg.pg.tradeQuery.money"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
							</th>
							<th>
								<fmt:message key="msg.pg.trade.commission"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
							</th>
							<th>
								<fmt:message key="msg.pg.tradeQuery.state"/>
							</th>
						</tr>
			          </thead>
			          <tbody id="tradeTbody">
			          	<c:if test="${tradeLogList.datas == null||empty tradeLogList.datas}">
			              <tr>
			          		<c:if test="${sessionScope.userinfo==null}">
				            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a class="full-a-link" href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
				            </c:if>
				            <c:if test="${sessionScope.userinfo!=null}">
			            		<td colspan="12"> <fmt:message key="msg.pg.activety.none"/> </td>
			            	</c:if>
				          </tr>
				        </c:if>
			           <c:forEach var="tl" items="${tradeLogList.datas}">
						<tr>
							<td>
								<ut:formatDate dateline="${tl.trade_time}"></ut:formatDate>
							</td>
							<td>
								${fn:toUpperCase(tl.transaction_code)}
							</td>
							<td>
								<c:choose>
									<c:when test="${tl.trade_type_id eq 1}">
										<b class="buy"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></b>
									</c:when>
									<c:when test="${tl.trade_type_id eq 2}">
										<b class="buy"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></b>
									</c:when>
									<c:when test="${tl.trade_type_id eq 3}">
										<b class="sell"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></b>
									</c:when>
									<c:when test="${tl.trade_type_id eq 4}">
										<b class="sell"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></b>
									</c:when>
									<c:when test="${tl.trade_type_id eq 5}">
										<b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.duoBaoTrade"/></b>
									</c:when>
									<c:when test="${tl.trade_type_id eq 6}">
										<b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.kongBaoTrade"/></b>
									</c:when>
									<c:otherwise>
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:choose>
									<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
									</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${tl.hold_price }" pattern="#0.########" />
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.####" />
							</td>
							<td>
								<fmt:formatNumber value="${tl.trade_count}" pattern="#0.####" />
							</td>
							<td>
								<fmt:formatNumber value="${tl.trade_deposit}" pattern="#0.######" />
							</td>
							<td <c:if test="${tl.trade_income lt 0}"> class="sell"</c:if>
								<c:if test="${tl.trade_income gt 0}"> class="buy"</c:if>>
								<c:choose>
									<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
									</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${tl.trade_income }" pattern="#0.########" />
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<fmt:formatNumber value="${tl.trade_fee}" pattern="#0.########" />
							</td>
							<td>
								<fmt:message key="msg.pg.tradeQuery.done"/>
							</td>
						</tr>
						</c:forEach>
			          </tbody>
			        </table>
			        <c:if test="${!empty tradeLogList.datas}">
						<ul class="pagination">
							<pg:pager url="${pageN }" items="${tradeLogList.total}"
								maxIndexPages="10" maxPageItems="${tradeLogList.maxitems}"
								export="currentPageNumber=pageNumber">
								<pg:param name="inviteeId" value="${inviteeId}" />
								<pg:param name="type" value="${type}" />
								<pg:param name="code"
									value="${currentTransaction.transaction_code }" />
								<pg:prev>
									<li class="disabled"><a href="<%=basePath%>${pageUrl}"
										mce_href="<%=basePath%>${pageUrl}"><fmt:message
												key="msg.pg.paging.previous" /></a></li>
								</pg:prev>
								<pg:pages>
									<c:choose>
										<c:when test="${currentPageNumber eq pageNumber }">
											<li class="active"><a href="javascript:void(0)">${pageNumber }</a>
											</li>
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
					</c:if>
			      </div>
			 	  </c:if>
			    </div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
</html>
