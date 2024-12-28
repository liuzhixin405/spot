<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>用户持仓挂单等</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						用户持仓挂单等
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerQuantRisk/quantHoldAndPending.do" method="post" id="selectForm" name="selectForm">
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId}" id="uid"></input>
									邮箱：
									<input type="text" name="userName" class="text-input " value="${userName}" id="uemail"></input>
									<button type="submit" class="button">
										查询
									</button>
								</form>
							</div>
							<br>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th colspan="5" align="center">
										基本信息
									</th>
								</tr>
								<tr>
									<th>
										合约可用（永续）
									</th>
									<th>
										持仓保证金（永续）
									</th>
									<th>
										挂单冻结（永续）
									</th>
									<th>
										合约总计（永续）
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<TD>
										<span><em><fmt:formatNumber value="${sumAvailable}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>					
									</TD>
									<TD>
										<span><em><fmt:formatNumber value="${deposit}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>
									</TD>
									<TD>
										<span><em><fmt:formatNumber value="${sumFreeze}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>
									</TD>
									<TD>
										<span><em><fmt:formatNumber value="${sumContract}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>
									</TD>
								</tr>
							</tbody>
							
							<thead>
								<tr>
									<th>
										合约可用（交割）
									</th>
									<th>
										持仓保证金（交割）
									</th>
									<th>
										挂单冻结（交割）
									</th>
									<th>
										合约总计（交割）
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<TD>
										<span><em><fmt:formatNumber value="${deliveryAvailable}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>
									</TD>
									<TD>
										<span><em><fmt:formatNumber value="${deliveryDeposit}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>
									</TD>
									<TD>
										<span><em><fmt:formatNumber value="${deliveryFreeze}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>
									</TD>
									<TD>
										<span><em><fmt:formatNumber value="${deliveryContract}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>
									</TD>
								</tr>
							</tbody>
							
							<thead>
								<tr>
									<th>
										可用余额/提现冻结
									</th>
									<th>
										账户总资产
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<TD>
										<span> <strong><fmt:formatNumber value="${account}" pattern="#0.00000000"></fmt:formatNumber>
										</strong>USDT</span>
										<span> <strong><fmt:formatNumber value="${freez}" pattern="#0.00000000"></fmt:formatNumber>
										</strong>USDT</span>					
									</TD>
									<TD>
										<span><em><fmt:formatNumber value="${total}" pattern="#0.00000000"></fmt:formatNumber>
										</em>USDT</span>
									</TD>
								</tr>
							</tbody>
						</table>
						
						<br><br>
						<h3 style="text-align: center;">永续合约</h3>
						
						<table id="changecolor">
							<thead>
								<tr>
									<th colspan="4" align="center">
										持仓情况
									</th>
									<th colspan="4" align="center">
										<div align="center" id="divmsg">
											<b id="errormsg" style="color: white;"></b>
										</div>
									</th>
								</tr>
								<tr>
									<th>
										类型
									</th>
									<th>
										可用持仓
									</th>
									<th>
										冻结
									</th>
									<th>
										平均价
									</th>
									<th>
										强平触发价
									</th>
									<th>
										保证金
									</th>
									<th>
										浮动盈亏
									</th>
									<th>
										盈亏比
									</th>
									<th>
										佣金
									</th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${listTransVos == null||empty listTransVos}">
									<tr>
										<td colspan="9">
											暂无持仓
										</td>
									</tr>
								</c:if>
								<c:forEach items="${listTransVos}" var="tran" varStatus="l">
									<tr>
										<td>
											${fn:toUpperCase(tran.bqCode)}
											<font color="${tran.direct eq '买多'?'green':'red' }">${tran.direct}</font>x${tran.multiple}
										</td>
										<td>
											<fmt:formatNumber value="${tran.holdCount}" pattern="#.##"></fmt:formatNumber>
											USDT
										</td>
										<td>
											<fmt:formatNumber value="${tran.lockCount}" pattern="#.##"></fmt:formatNumber>
											USDT
										</td>
										<td>
											<fmt:formatNumber value="${tran.averagePrice}" pattern="#.####"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${tran.qpPrice}" pattern="#.####"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${tran.deposit}" pattern="#.####"></fmt:formatNumber>
										</td>
										<td class="${tran.floatingProfitLoss >0? 'buy':'sell' }">
											<fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########"></fmt:formatNumber>
										</td>
										<td id="floatGainLossRatebuy${l.index}">
											${tran.profitLossRatio }
										</td>
										<td>
											<fmt:formatNumber value="${tran.commission }" pattern="#.######"></fmt:formatNumber>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<table id="changecolor">
							<thead>
								<tr>
									<th colspan="9" align="center">
										挂单情况
									</th>
								</tr>
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
										委托价格
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
								</tr>
							</thead>
							<tbody>
								<c:if test="${pdList == null||empty pdList}">
									<tr>
										<td colspan="9">
											暂无挂单
										</td>
									</tr>
								</c:if>
								<c:forEach var="pd" items="${pdList}">
									<tr>
										<td>
											<ut:formatDate dateline="${pd.createTime}"></ut:formatDate>
										</td>
										<td>
											${fn:toUpperCase(pd.symbol)}
										</td>
										<td>
											<c:choose>
												<c:when test="${pd.direction eq 1 && pd.priority eq 1}">多单开仓 </c:when>
												<c:when test="${pd.direction eq 1 && pd.priority eq 2}">空单平仓 </c:when>
												<c:when test="${pd.direction eq 1 && pd.priority eq 3}">多单爆仓 </c:when>
												<c:when test="${pd.direction eq 2 && pd.priority eq 1}">空单开仓 </c:when>
												<c:when test="${pd.direction eq 2 && pd.priority eq 2}">多单平仓 </c:when>
												<c:when test="${pd.direction eq 2 && pd.priority eq 3}">空单爆仓 </c:when>
												<c:otherwise>class="multiple" </c:otherwise>
											</c:choose>
										</td>
										<td>
											${pd.multiple}
										</td>
										<td>
											${pd.price}
										</td>
										<td>
											<fmt:formatNumber value="${pd.amount}" pattern="#0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${pd.dealAmount}" pattern="#0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${pd.undealAmount}" pattern="#0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${pd.deposit}" pattern="#0.0000" />
											USDT
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						
						<br><br>
						<h3 style="text-align: center;">交割合约</h3>
						
						<table id="changecolor">
							<thead>
								<tr>
									<th colspan="4" align="center">
										持仓情况
									</th>
									<th colspan="4" align="center">
										<div align="center" id="divmsg">
											<b id="errormsg" style="color: white;"></b>
										</div>
									</th>
								</tr>
								<tr>
									<th>
										类型
									</th>
									<th>
										可用持仓
									</th>
									<th>
										冻结
									</th>
									<th>
										平均价
									</th>
									<th>
										强平触发价
									</th>
									<th>
										保证金
									</th>
									<th>
										浮动盈亏
									</th>
									<th>
										盈亏比
									</th>
									<th>
										佣金
									</th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${deliverylistTransVos == null||empty deliverylistTransVos}">
									<tr>
										<td colspan="9">
											暂无持仓
										</td>
									</tr>
								</c:if>
								<c:forEach items="${deliverylistTransVos}" var="tran" varStatus="l">
									<tr>
										<td>
											${fn:toUpperCase(tran.bqCode)}
											<font color="${tran.direct eq '买多'?'green':'red' }">${tran.direct}</font>x${tran.multiple}
										</td>
										<td>
											<fmt:formatNumber value="${tran.holdCount}" pattern="#.##"></fmt:formatNumber>
											USDT
										</td>
										<td>
											<fmt:formatNumber value="${tran.lockCount}" pattern="#.##"></fmt:formatNumber>
											USDT
										</td>
										<td>
											<fmt:formatNumber value="${tran.averagePrice}" pattern="#.####"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${tran.qpPrice}" pattern="#.####"></fmt:formatNumber>
										</td>
										<td>
											<fmt:formatNumber value="${tran.deposit}" pattern="#.####"></fmt:formatNumber>
										</td>
										<td class="${tran.floatingProfitLoss >0? 'buy':'sell' }">
											<fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########"></fmt:formatNumber>
										</td>
										<td id="floatGainLossRatebuy${l.index}">
											${tran.profitLossRatio }
										</td>
										<td>
											<fmt:formatNumber value="${tran.commission }" pattern="#.######"></fmt:formatNumber>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<table id="changecolor">
							<thead>
								<tr>
									<th colspan="9" align="center">
										挂单情况
									</th>
								</tr>
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
										委托价格
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
								</tr>
							</thead>
							<tbody>
								<c:if test="${deliverypdList == null||empty deliverypdList}">
									<tr>
										<td colspan="9">
											暂无挂单
										</td>
									</tr>
								</c:if>
								<c:forEach var="pd" items="${deliverypdList}">
									<tr>
										<td>
											<ut:formatDate dateline="${pd.createTime}"></ut:formatDate>
										</td>
										<td>
											${fn:toUpperCase(pd.symbol)}
										</td>
										<td>
											<c:choose>
												<c:when test="${pd.direction eq 1 && pd.priority eq 1}">多单开仓 </c:when>
												<c:when test="${pd.direction eq 1 && pd.priority eq 2}">空单平仓 </c:when>
												<c:when test="${pd.direction eq 1 && pd.priority eq 3}">多单爆仓 </c:when>
												<c:when test="${pd.direction eq 2 && pd.priority eq 1}">空单开仓 </c:when>
												<c:when test="${pd.direction eq 2 && pd.priority eq 2}">多单平仓 </c:when>
												<c:when test="${pd.direction eq 2 && pd.priority eq 3}">空单爆仓 </c:when>
												<c:otherwise>class="multiple" </c:otherwise>
											</c:choose>
										</td>
										<td>
											${pd.multiple}
										</td>
										<td>
											${pd.price}
										</td>
										<td>
											<fmt:formatNumber value="${pd.amount}" pattern="#0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${pd.dealAmount}" pattern="#0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${pd.undealAmount}" pattern="#0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${pd.deposit}" pattern="#0.0000" />
											USDT
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
