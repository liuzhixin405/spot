<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>${lastPrice }$ Btcside区块链资产合约交易平台-委托管理</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath %>common/css/style.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/tradeEntrust.js?<%=rjs%>"></script>
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container">

			<jsp:include page="_trade_left_sz.jsp"></jsp:include>

			<div class="main-rt-box">

				<div class="main-wrap">
					<div class="block-title">
						<h2>委托管理
						</h2>

						<div class="trade-control-box">
							<div class="trade-control lt"
								style="width: 300px; display: inline-block; float: left;">
								<a href="javascript:;" onclick="cancelDeal('1');">一键撤买</a>
								<a href="javascript:;" onclick="cancelDeal('2');">一键撤卖</a>
								<a href="javascript:;" onclick="cancelDeal('0');">全部撤单</a>
								
							</div>
						</div>

						<form action="<%=basePath%>trade/szentrust.do" name="tiaojian">
							<div class="selected rt" id="selected">
								<select required="true" name="pendingtype" id="pendingtype" onchange="javascript:document.tiaojian.submit();">
									<option value="all" <c:if test ="${type eq 'all'}">selected="selected"</c:if>>
										按价格
									</option>
									<option value="htl" <c:if test ="${type eq 'htl'}">selected="selected"</c:if>>
										从高到低
									</option>
									<option value="lth" <c:if test ="${type eq 'lth'}">selected="selected"</c:if>>
										从低到高
									</option>
								</select>
							</div>
							<div class="selected rt" id="selected">
								<select required="true" name="pendingorder" id="pendingorder" onchange="javascript:document.tiaojian.submit();">
									<option value="all" <c:if test ="${order eq 'all'}">selected="selected"</c:if> >
										全部类型
									</option>
									<option value="buy" <c:if test ="${order eq 'buy'}">selected="selected"</c:if> >
										买单
									</option>
									<option value="sell" <c:if test ="${order eq 'sell'}">selected="selected"</c:if>>
										卖单
									</option>
									<option value="buyf" <c:if test ="${order eq 'buyf'}">selected="selected"</c:if>>
										多单开仓
									</option>
									<option value="sells" <c:if test ="${order eq 'sells'}">selected="selected"</c:if>>
										多单平仓
									</option>
									<option value="sellf" <c:if test ="${order eq 'sellf'}">selected="selected"</c:if>>
										空单开仓
									</option>
									<option value="buys" <c:if test ="${order eq 'buys'}">selected="selected"</c:if>>
										空单平仓
									</option>
								</select>
							</div>
							<div class="selected rt">
								<select  name="pendingcode" id="transactionCode" onchange="javascript:document.tiaojian.submit();">
								<option value="all"
									<c:if test="${'all' eq currentTransaction.transaction_code}"> selected</c:if>>
									全部合约
								</option>
								<c:forEach var="ta" items="${transactionList}">
									<option value="${ta.transaction_code}"
										<c:if test="${ta.transaction_code eq currentTransaction.transaction_code}"> selected</c:if>>
										${fn:toUpperCase(ta.transaction_code)}
									</option>
								</c:forEach>
							</select>
							</div>
						</form>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										委托时间
									</th>
									<th>
										合约号
									</th>
									<th>
										挂单类型
									</th>
									<th>
										杠杆倍数
									</th>
									<th>
										<c:if test="${!issz}">委托价格</c:if>
										<c:if test="${issz}">委托点位</c:if>
									</th>
									<th>
										委托数量
									</th>
									<th>
										已成交
									</th>
									<th>
										未成交
									</th>
									<th>
										保证金
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach var="pd" items="${pdList}" >
								<tr>
									<td>
										
										<ut:formatDate dateline="${pd.delegation_time}"></ut:formatDate>
									</td>
									<td>
										${fn:toUpperCase(pd.transaction_code)}
									</td>
									<td>
										<c:choose>
										<c:when test="${pd.direction eq 1 && pd.priority eq 1}"><b class="buy">多单开仓</b></c:when>
										<c:when test="${pd.direction eq 1 && pd.priority eq 2}"><b class="sell">空单平仓</b></c:when>
										<c:when test="${pd.direction eq 1 && pd.priority eq 3}"><b class="loss-all">多单爆仓</b></c:when>
										<c:when test="${pd.direction eq 2 && pd.priority eq 1}"><b class="sell">空单开仓</b></c:when>
										<c:when test="${pd.direction eq 2 && pd.priority eq 2}"><b class="buy">多单平仓</b></c:when>
										<c:when test="${pd.direction eq 2 && pd.priority eq 3}"><b class="loss-all">空单爆仓</b></c:when>
										</c:choose>
									</td>
									<td>
										${pd.multiple}
									</td>
									<td>
										<fmt:formatNumber value="${pd.delegation_price}" pattern="#0.#"/>
									</td>
									<td>
										<fmt:formatNumber value="${pd.delegation_count}" pattern="#0"/>张
									</td>
									<td>
										<fmt:formatNumber value="${pd.deal_count}" pattern="#0"/>张
									</td>
									<td>
										<fmt:formatNumber value="${pd.undeal_count}" pattern="#0"/>张
									</td>
									<td>
										<fmt:formatNumber value="${pd.deposit}" pattern="#0.####"/>
										USDT
									</td>
									<td>
										<c:choose>
										<c:when test="${pd.priority < 3}"><a href="<%=basePath %>tradeAjax/canclePendingDelegation.do?pdId=${pd.delegation_id}&code=${pd.transaction_code}&type=${type}&order=${order}" class="btn btn-blue">撤单</a> </c:when>
										<c:otherwise>已爆仓 </c:otherwise></c:choose>
									</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>

					</div>
				</div>
			</div>
		</div>
		<jsp:include page="_footer.jsp"></jsp:include>
		
	</body>

</html>
