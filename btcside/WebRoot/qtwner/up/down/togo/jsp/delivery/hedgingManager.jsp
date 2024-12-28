<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<title>刷单管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<style type="text/css">
			.clear {
				clear: both;
			}
		</style>
		
		<script type="text/javascript">
			function sure(qr, url) {
				if (qr != null) {
					if (confirm(qr)) {
						document.location.href = url;
					}
				} else {
					document.location.href = url;
				}
		
			}
		</script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						对冲管理
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
							<c:forEach items="${transList}" var="l" varStatus="status">
								<a href="<%=actionPath%>delivery/managerArbitrage/hedgingManager.do?code=${l.transaction_code }">
									<font color="${current.transaction_code == l.transaction_code ? '#ff0000' : ''}">${l.transaction_code.toUpperCase() }</font>
								</a> &nbsp;
							</c:forEach>
							<div class="clear"></div>
							<form class="notification depth-form" action="delivery/managerArbitrage/updateHedgingSetting.do" method="post">
								<input type="hidden" name="id" value="${setting.id}"></input>
								<input type="hidden" name="f_code" value="${setting.f_code}"></input>
								<input type="hidden" name="running" value="${setting.running}"></input>
								<input type="hidden" name="depth_uid" value="${setting.depth_uid}"></input>
								<input type="hidden" name="depth_buy_range" value="${setting.depth_buy_range}"></input>
								<input type="hidden" name="depth_sell_range" value="${setting.depth_sell_range}"></input>
								<input type="hidden" name="depth_amount_min" value="${setting.depth_amount_min}"></input>
								<input type="hidden" name="depth_amount_max" value="${setting.depth_amount_max}"></input>
								<input type="hidden" name="depth_url" value="${setting.depth_url}"></input>
								<input type="hidden" name="spare_depth_url" value="${setting.spare_depth_url}"></input>
								<input type="hidden" name="scalping" value="${setting.scalping}"></input>
								<input type="hidden" name="scalping_frequency_min" value="${setting.scalping_frequency_min}"></input>
								<input type="hidden" name="scalping_frequency_max" value="${setting.scalping_frequency_max}"></input>
								<input type="hidden" name="scalping_min_each" value="${setting.scalping_min_each}"></input>
								<input type="hidden" name="scalping_max_each" value="${setting.scalping_max_each}"></input>
								<input type="hidden" name="scalping_index_range" value="${setting.scalping_index_range}"></input>
								<input type="hidden" name="scalping_depth_range" value="${setting.scalping_depth_range}"></input>
								<input type="hidden" name="curr_use_state" value="${setting.curr_use_state}"></input>
								
								<input type="hidden" name="depth_price_gap" value="${setting.depth_price_gap}"></input>
								<input type="hidden" name="depth_amount_gap_min" value="${setting.depth_amount_gap_min}"></input>
								<input type="hidden" name="depth_amount_gap_max" value="${setting.depth_amount_gap_max}"></input>
								<input type="hidden" name="depth_index_range" value="${setting.depth_index_range}"></input>
								<input type="hidden" name="depth_size" value="${setting.depth_size}"></input>
								<input type="hidden" name="price_digits" value="${setting.price_digits}"></input>
								<input type="hidden" name="amount_digits" value="${setting.amount_digits}"></input>
								<input type="hidden" name="can_trade_self" value="${setting.can_trade_self}"></input>
								<input type="hidden" name="speed_type" value="${setting.speed_type}"></input>
								
								<label for="auto_trade">自动对平：</label>
								<select name="auto_trade" id="auto_trade">
									<c:if test="${setting.auto_trade}">
									<option value="true" selected="selected">开启</option>
									<option value="false">关闭</option>
									</c:if>
									<c:if test="${!setting.auto_trade}">
									<option value="true">开启</option>
									<option value="false" selected="selected">关闭</option>
									</c:if>
								</select>
								<div class="clear"></div>
								
								<label for="hedging">外部市场对冲：</label>
								<select name="hedging" id="hedging">
									<c:if test="${setting.hedging}">
									<option value="true" selected="selected">开启</option>
									<option value="false">关闭</option>
									</c:if>
									<c:if test="${!setting.hedging}">
									<option value="true">开启</option>
									<option value="false" selected="selected">关闭</option>
									</c:if>
								</select>
								<div class="clear"></div>
								
								<label for="hedging_buy_range">火币对冲买单调整幅度(%)：</label>
								<input name="hedging_buy_range" value="${setting.hedging_buy_range.doubleValue()}"></input>
								<div class="clear"></div>
							
								<label for="hedging_sell_range">火币对冲卖单调整幅度(%)：</label>
								<input name="hedging_sell_range" value="${setting.hedging_sell_range.doubleValue()}"></input>
								<div class="clear"></div>
								
								<label for="risk_manage">风险控制（交易限制）：</label>
								<select name="risk_manage" id="risk_manage">
									<c:if test="${setting.risk_manage}">
									<option value="true" selected="selected">开启</option>
									<option value="false">关闭</option>
									</c:if>
									<c:if test="${!setting.risk_manage}">
									<option value="true">开启</option>
									<option value="false" selected="selected">关闭</option>
									</c:if>
								</select>
								<div class="clear"></div>
								
								<label for="balance">对冲账户可用余额（usdt）：</label>
								<span>${usdtBalance}</span>
								<div class="clear"></div>
								
								<label for="balance">对冲账户可用余额（${current.transaction_code}）：</label>
								<span>${balance}</span>
								<div class="clear"></div>
									
								<button type="submit" id="submit" class="button">提交</button>
							</form>
						</div>
						
						
						<div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">对冲失败统计:</h5>
							<div class="clear"></div>
							<div class="notification depth-form">
								<div>
									<form action="<%=actionPath%>delivery/managerArbitrage/hedgingManager.do" method="get">
										<input type="hidden" name="code" value="${current.transaction_code}" ></input>
										开始时间
										<input name="startTime" type="text" value="${startTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										&nbsp;&nbsp;&nbsp;&nbsp;结束时间
										<input name="endTime" type="text" value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" readonly="readonly"/>
										<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查询</button>
									</form>
									<c:if test="${buyRecord != null }">
										<p><b>查询结果</b>--对冲买入，成交量：${buyRecord.trade_amount.doubleValue()}， 成交均价：${buyRecord.trade_price.doubleValue()}，对冲均价：${buyRecord.price.doubleValue()}</p>
									</c:if>
									<c:if test="${sellRecord != null }">
										<p><b>查询结果</b>--对冲卖出，成交量：${sellRecord.trade_amount.doubleValue()}，成交均价：${sellRecord.trade_price.doubleValue()}，对冲均价：${sellRecord.price.doubleValue()}</p>
									</c:if>
									<div class="clear"></div>
								</div>
								
								<div class="clear"></div>
							</div>
						</div>
						
					</div>
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									合约号
								</th>
								<th>
									交易号
								</th>
								<th>
									成交量
								</th>
								<th>
									成交价
								</th>
								<th>
									深度交易方向
								</th>
								<th>
									成交时间
								</th>
								<th>
									深度用户
								</th>
								<th>
									对冲方向
								</th>
								<th>
									对冲价格
								</th>
								<th>
									处理时间
								</th>
								<th>
									状态
								</th>
								<th>
									对冲调用失败次数
								</th>
								<th>
									备注
								</th>
								<th>
									 操作
								</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${pm.datas}" var="l" varStatus="status">
								<tr>
									<td>
										<c:out value="${l.transaction_code}"></c:out>
									</td>
									<td>
										<c:out value="${l.trade_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.trade_amount}"></c:out>
									</td>
									<td>
										<c:out value="${l.trade_price}"></c:out>
									</td>
									<td>
										<c:choose>
										<c:when test="${l.trade_direction==1}">
											<font color="green">买入</font>
										</c:when>
										<c:otherwise>
											<font color="red">卖出</font>
										</c:otherwise>
										</c:choose>
									</td>
									<td>
										<ut:formatDate dateline="${l.trade_time}"></ut:formatDate>
									</td>
									<td>
										<c:out value="${l.f_user_id}"></c:out>
									</td>
									<td>
										<c:choose>
										<c:when test="${l.direction==1}">
											<font color="green">买入</font>
										</c:when>
										<c:otherwise>
											<font color="red">卖出</font>
										</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:out value="${l.price}"></c:out>
									</td>
									<td>
										<ut:formatDate dateline="${l.process_time}"></ut:formatDate>
									</td>
									<td>
										<c:out value="${l.state}"></c:out>
									</td>
									<td>
										<c:out value="${l.send_times}"></c:out>
									</td>
									<td>
										<c:out value="${l.note}"></c:out>
									</td>
									<td>
										<c:if test="${l.state==0}">
											<button class="button" style="background: red !important; border: 0 !important;" onclick="cancelHedging(${l.id})">取消</button>
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<tfoot>
						<tr>
							<td colspan="9">
								<div class="pagination">
									<!-- ********** 分页开始 **********	-->
									<pg:pager url="delivery/managerArbitrage/hedgingManager.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="code" value="${setting.f_code}"/>
										
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
								<!-- End .pagination -->
								<div class="clear"></div>
							</td>
						</tr>
					</tfoot>
					<script language="javascript" type="text/javascript">
						senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
					</script>
				</div>
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
	function cancelHedging(id) {
		var r = confirm("确定取消交易对冲?");
		if (r == true) {
		    $.ajax({
				type : "POST",
				dataType : "json",
				url : "<%=actionPath%>managerArbitrage/cancelHedging.do",
				data : {
					id : id
				},
				success : function(data) {
					if (data.code == 'success') {
						window.location.reload();
					} else {
						alert(data.msg);
					}
				}
			});
		}
	}
	</script>
</html>