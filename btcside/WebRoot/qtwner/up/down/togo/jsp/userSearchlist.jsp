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
		<title>用户资产查询</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript">
			function clickBtn(url){
				var id = document.getElementById("uid").value;
				var email = document.getElementById("uemail").value;
				document.location.href = url + "&userId=" + id + "&userEmail=" + email;
			}
			function cancelDealAnsyc(url, id, code, cache) {
				$.post(url + "?t=" + new Date().getTime(), {
					pdId : id,
					code : code,
					uid : $('#userId').val(),
					cache : cache
				}, function(data) {
					document.selectForm.submit();
					return;
				});
			}
			function admin_pc(index, hold_count, mtid, url){
				var pcAmount = Number($("#pcAmount_"+index).val());
				if (isNaN(pcAmount) || pcAmount == 0 || pcAmount > hold_count) {
					alert("请输入合法的平仓数量");
					return false;
				}
				
				var price = Number($("#pcPrice_"+index).val());
				if (isNaN(price) || price == 0) {
					alert("请输入合法的平仓价格");
					return false;
				}
				
				$.ajax({
			        type: "POST",
			        dataType: "json",         
			        url: url,
			        data: {
			    		price : price,
			    		amount : pcAmount,
			    		mtid : mtid,
			    		userId : $('#userId').val()
			    	},
			        success: function(data){
				        if (data.code == "success") {
				    		document.selectForm.submit();
						} else {
							//alert(data.msg);
							$('#errormsg').html(data.msg);
							$('#divmsg').css("background-color","#C55555");
							$("#pcPrice_"+index).val(data.msg.match(/\d+(\.\d+)?/g));
						}
			    		return;
			        }
				});
			}
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						用户资产查询
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerUserSearch/selectUsers.do?type=1" method="post" id="selectForm" name="selectForm">
									用户ID：
									<input type="text" name="userId" class="text-input " value="${userId}" id="uid"></input>
									<input type="hidden" value="${userId}" id="userId"></input>
									邮箱：
									<input type="text" name="userEmail" class="text-input " value="${userEmail}" id="uemail"></input>
									<button type="submit" class="button">
										基本查询
									</button>
									<button type="button" class="button" onclick="clickBtn('<%=actionPath%>managerUserSearch/selectUsers.do?type=2')">
										资金流水查询
									</button>
									<button type="button" class="button" onclick="clickBtn('<%=actionPath%>managerUserSearch/selectUsers.do?type=3')">
										账单明细查询
									</button>
								</form>
							</div>
							<br>
							<c:if test="${selectType == 3}">
								<div>
									总充值：<fmt:formatNumber value="${allRecharge}" pattern="#.######"></fmt:formatNumber>
									<br>总提现：<fmt:formatNumber value="${allWithDraw}" pattern="#.######"></fmt:formatNumber>
									<br>提现冻结：<fmt:formatNumber value="${freezeWithDraw}" pattern="#.######"></fmt:formatNumber>
								</div>
							</c:if>
						</div>
						<c:if test="${selectType == 1}">
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
										<th colspan="8" align="center">
											资产详情
										</th>
									</tr>
									<tr>
										<th>
											合约名称
										</th>
										<th>
											持仓总量
										</th>
										<th>
											做多持仓
										</th>
										<th>
											做空持仓
										</th>
										<th>
											委托总量
										</th>
										<th>
											做多委托
										</th>
										<th>
											做空委托
										</th>
										<th>
											合约余额
										</th>
										<th>
											合约冻结
										</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="vo" items="${listvo}">
										<tr>
											<td>
												${vo.code}
											</td>
											<td>
												${vo.totalAmount}
											</td>
											<td>
												${vo.buyAmount}
											</td>
											<td>
												${vo.sellAmount}
											</td>
											<td>
												${vo.delegationAmount}
											</td>
											<td>
												${vo.buydelegation}
											</td>
											<td>
												${vo.selldelegation}
											</td>
											<td>
												<fmt:formatNumber value="${vo.f_btc}" pattern="#0.000000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${vo.f_lockbtc}" pattern="#0.000000"></fmt:formatNumber>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
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
										<th>
											平仓价格
										</th>
										<th>
											平仓数量
										</th>
										<th>
											操作
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
											<td>
												<input type="text" class="text-input" name="price" id="pcPrice_${l.index}" size="8"/>
											</td>
											<td>
												<input type="text" class="text-input" name="amount" id="pcAmount_${l.index}" size="8"/>
											</td>
											<td>
												<a href="javascript:void(0);" class="control-btn btn pc-btn" onclick="admin_pc(${l.index},${tran.holdCount},${tran.mt_id}, '<%=actionPath%>managerUserSearch/tradePC.do');">平仓</a>
												
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
										<th>
											操作
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
											<td>
												<c:choose>
													<c:when test="${pd.priority < 3}">
														<a href="javascript:void(0);" class="btn btn-blue" onclick="cancelDealAnsyc('<%=actionPath%>managerUserSearch/canclePendingAnsyc.do',${pd.delegation_id},'${pd.symbol}',${pd.state eq 'submitting'});">撤单</a>
													</c:when>
													<c:otherwise>已爆仓 </c:otherwise>
												</c:choose>
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
										<th colspan="8" align="center">
											资产详情
										</th>
									</tr>
									<tr>
										<th>
											合约名称
										</th>
										<th>
											持仓总量
										</th>
										<th>
											做多持仓
										</th>
										<th>
											做空持仓
										</th>
										<th>
											委托总量
										</th>
										<th>
											做多委托
										</th>
										<th>
											做空委托
										</th>
										<th>
											合约余额
										</th>
										<th>
											合约冻结
										</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="vo" items="${deliverylistvo}">
										<tr>
											<td>
												${vo.code}
											</td>
											<td>
												${vo.totalAmount}
											</td>
											<td>
												${vo.buyAmount}
											</td>
											<td>
												${vo.sellAmount}
											</td>
											<td>
												${vo.delegationAmount}
											</td>
											<td>
												${vo.buydelegation}
											</td>
											<td>
												${vo.selldelegation}
											</td>
											<td>
												<fmt:formatNumber value="${vo.f_btc}" pattern="#0.000000"></fmt:formatNumber>
											</td>
											<td>
												<fmt:formatNumber value="${vo.f_lockbtc}" pattern="#0.000000"></fmt:formatNumber>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
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
										<th>
											平仓价格
										</th>
										<th>
											平仓数量
										</th>
										<th>
											操作
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
											<td>
												<input type="text" class="text-input" name="price" id="pcPrice_${l.index}" size="8"/>
											</td>
											<td>
												<input type="text" class="text-input" name="amount" id="pcAmount_${l.index}" size="8"/>
											</td>
											<td>
												<a href="javascript:void(0);" class="control-btn btn pc-btn" onclick="admin_pc(${l.index},${tran.holdCount},${tran.mt_id}, '<%=actionPath%>managerUserSearch/tradePC.do');">平仓</a>
												
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
										<th>
											操作
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
											<td>
												<c:choose>
													<c:when test="${pd.priority < 3}">
														<a href="javascript:void(0);" class="btn btn-blue" onclick="cancelDealAnsyc('<%=actionPath%>managerUserSearch/canclePendingAnsyc.do',${pd.delegation_id},'${pd.symbol}',${pd.state eq 'submitting'});">撤单</a>
													</c:when>
													<c:otherwise>已爆仓 </c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:if>
						<c:if test="${selectType == 2}">
							<table id="changecolor">
								<thead>
									<tr>
										<th colspan="5" align="center">
											资金流水查询
										</th>
									</tr>
									<tr>
										<th>
											时间
										</th>
										<th>
											方式
										</th>
										<th>
											原有值
										</th>
										<th>
											增减值
										</th>
										<th>
											当前值
										</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="li" items="${pm.datas}" varStatus="st">
										<tr>
											<td>
												<span><ut:formatDate dateline="${li.dateline}"></ut:formatDate>
												</span>
											</td>
											<td>
												${li.title}
											</td>
											<td>

												<fmt:formatNumber value="${li.current_amount}" pattern="#0.00000000"></fmt:formatNumber>
											</td>
											<td>

												<fmt:formatNumber value="${li.change_amount}" pattern="#0.00000000"></fmt:formatNumber>

											</td>
											<td>
												<fmt:formatNumber value="${li.lastest_amount}" pattern="#0.00000000"></fmt:formatNumber>
											</td>
										</tr>
									</c:forEach>
								</tbody>
								<tfoot>
									<tr>
										<td colspan="5">
											<div class="pagination">
												<pg:pager url="managerUserSearch/selectUsers.do" items="${pm.total}" maxIndexPages="10"
													maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">

													<pg:param name="userId" value="${userId}" />
													<pg:param name="userEmail" value="${userEmail}" />
													<pg:param name="type" value="${selectType}" />

													<pg:first>
														<a href="${pageUrl}" mce_href="${pageUrl}">首页</a>
													</pg:first>

													<pg:prev>
														<a href="${pageUrl }" mce_href="${pageUrl }">前页</a>
													</pg:prev>
													<pg:pages>
														<c:choose>
															<c:when test="${currentPageNumber eq pageNumber }">
																<font color="red">${pageNumber }</font>
															</c:when>
															<c:otherwise>
																<a href="${pageUrl }" mce_href="${pageUrl }">${pageNumber}</a>
															</c:otherwise>
														</c:choose>
													</pg:pages>
													<pg:next>
														<a href="${pageUrl }" mce_href="${pageUrl }">后页</a>
													</pg:next>

													<pg:last>
														<a href="${pageUrl }" mce_href="${pageUrl }">尾页</a>
													</pg:last>
												</pg:pager>
											</div>
										</td>
									</tr>
								</tfoot>
							</table>
						</c:if>
						<c:if test="${selectType == 3}">
							<table id="changecolor">
								<thead>
									<tr>
										<th colspan="5" align="center">
											账单明细查询
										</th>
									</tr>
									<tr>
										<th>
											时间
										</th>
										<th>
											地址
										</th>
										<th>
											方式
										</th>
										<th>
											数量
										</th>
										<th>
											状态
										</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="recharge" items="${pm1.datas}" varStatus="st">
										<tr>
											<td>
												<span><ut:formatDate dateline="${recharge.dateline}"></ut:formatDate>
												</span>
											</td>
											<td>
												<c:out value="${recharge.receice_card_no}"></c:out>
											</td>
											<td>
												<c:choose>
													<c:when test="${recharge.operation==1}">充值</c:when>
													<c:when test="${recharge.operation==2}">提现</c:when>
													<c:when test="${recharge.operation==3}">转账</c:when>
												</c:choose>
											</td>
											<td>
												<c:out value="${recharge.amount}"></c:out>
											</td>
											<td>
												<c:choose>
													<c:when test="${recharge.state==0}">未受理</c:when>
													<c:when test="${recharge.state==1}">受理成功</c:when>
													<c:when test="${recharge.state==2}">拒绝(<c:out value="${recharge.remark}"></c:out>)</c:when>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
								<tfoot>
									<tr>
										<td colspan="5">
											<div class="pagination">
												<pg:pager url="managerUserSearch/selectUsers.do" items="${pm1.total}" maxIndexPages="10"
													maxPageItems="${pm1.maxitems}" export="currentPageNumber=pageNumber">

													<pg:param name="userId" value="${userId}" />
													<pg:param name="userEmail" value="${userEmail}" />
													<pg:param name="type" value="${selectType}" />

													<pg:first>
														<a href="${pageUrl}" mce_href="${pageUrl}">首页</a>
													</pg:first>

													<pg:prev>
														<a href="${pageUrl }" mce_href="${pageUrl }">前页</a>
													</pg:prev>
													<pg:pages>
														<c:choose>
															<c:when test="${currentPageNumber eq pageNumber }">
																<font color="red">${pageNumber }</font>
															</c:when>
															<c:otherwise>
																<a href="${pageUrl }" mce_href="${pageUrl }">${pageNumber}</a>
															</c:otherwise>
														</c:choose>
													</pg:pages>
													<pg:next>
														<a href="${pageUrl }" mce_href="${pageUrl }">后页</a>
													</pg:next>

													<pg:last>
														<a href="${pageUrl }" mce_href="${pageUrl }">尾页</a>
													</pg:last>
												</pg:pager>
											</div>
										</td>
									</tr>
								</tfoot>
							</table>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
