<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<title>兑换记录查询</title>
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
						刷兑换记录查询
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<h5 style="float:left; margin-right: 10px;">选择币种:</h5>
							<c:forEach items="${settings}" var="l" varStatus="status">
								<a href="<%=actionPath%>managerExchange/loadRecords.do?id=${l.id }&toUsd=${toUsd}&uid=${uid}">
									<font color="${current.id == l.id ? '#ff0000' : ''}">${l.symbol.toUpperCase() }</font>
								</a> &nbsp;
							</c:forEach>
							<div class="clear"></div>
							
							<h5 style="float:left; margin-right: 10px;">选择方向:</h5>
							<a href="<%=actionPath%>managerExchange/loadRecords.do?id=${current.id }&toUsd=true&uid=${uid}">
								<font color="${toUsd ? '#ff0000' : ''}">${current.symbol}兑换USDT</font>
							</a> &nbsp;
							<a href="<%=actionPath%>managerExchange/loadRecords.do?id=${current.id }&toUsd=false&uid=${uid}">
								<font color="${toUsd ?  '' : '#ff0000'}">USDT兑换${current.symbol}</font>
							</a> &nbsp;
							<div class="clear"></div>
							
							<h5 style="float:left; margin-right: 10px;">用户ID:</h5>
							<input type="text" id="userId" value="${uid}"></input>
							<button class="button" id="searchBtn">查询</button>
							<div class="clear"></div>
						</div>
					</div>
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									用户
								</th>
								<th>
									源币种
								</th>
								<th>
									目标币种
								</th>
								<th>
									源币量
								</th>
								<th>
									源币冻结量
								</th>
								<th>
									源币退回
								</th>
								<th>
									目标币量
								</th>
								<th>
									现价
								</th>
								<th>
									下单价
								</th>
								<th>
									下单方向
								</th>
								<th>
									订单号
								</th>
								<th>
									成交量
								</th>
								<th>
									成交均价
								</th>
								<th>
									成交额
								</th>
								<th>
									手续费
								</th>
								<th>
									最终获得
								</th>
								<th>
									记录时间
								</th>
								<th>
									处理时间
								</th>
								<th>
									状态
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
										<c:out value="${l.f_user_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.ori_currency_name}"></c:out>
									</td>
									<td>
										<c:out value="${l.target_currency_name}"></c:out>
									</td>
									<td>
										<c:out value="${l.ori_amount.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.frozen_amount.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.frozen_amount_back.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.target_amount.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.present_price.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.order_price.doubleValue()}"></c:out>
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
										<c:out value="${l.order_id}"></c:out>
									</td>
									<td>
										<c:out value="${l.deal_amount.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.avg_price.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.deal_cash.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.fee.doubleValue()}"></c:out>
									</td>
									<td>
										<c:out value="${l.obtain_amount.doubleValue()}"></c:out>
									</td>
									<td>
										<ut:formatDate dateline="${l.record_time}"></ut:formatDate>
									</td>
									<td>
										<ut:formatDate dateline="${l.oper_time}"></ut:formatDate>
									</td>
									<td>
										<c:if test="${l.state==0}">
											已提交
										</c:if>
										<c:if test="${l.state==1}">
											已审核
										</c:if>
										<c:if test="${l.state==2}">
											转人工
										</c:if>
										<c:if test="${l.state==3}">
											处理中...
										</c:if>
										<c:if test="${l.state==4}">
											已处理
										</c:if>
										<c:if test="${l.state==5}">
											已取消
										</c:if>
									</td>
									<td>
										<c:out value="${l.note}"></c:out>
									</td>
									<td>
										<c:if test="${l.state==0}">
										<button class="button" style="background: #0e5f9d !important; border: 0 !important;" onclick="check(${l.id}, ${l.f_user_id},${l.order_price.doubleValue()},${l.target_amount.doubleValue()},'${l.target_currency_name}')">审核</button>
											<button class="button" onclick="confirmRecord(${l.id}, ${l.f_user_id},${l.order_price.doubleValue()},${l.target_amount.doubleValue()},'${l.target_currency_name}')">确认</button>
											<button class="button" style="color: red;" onclick="cancelRecord(${l.id}, ${l.f_user_id},${l.order_price.doubleValue()},${l.target_amount.doubleValue()},'${l.target_currency_name}')">取消</button>
										</c:if>
										<c:if test="${l.state==1}">
											<button class="button" onclick="confirmRecord(${l.id}, ${l.f_user_id},${l.order_price.doubleValue()},${l.target_amount.doubleValue()},'${l.target_currency_name}')">确认</button>
											<button class="button" style="color: red;" onclick="cancelRecord(${l.id}, ${l.f_user_id},${l.order_price.doubleValue()},${l.target_amount.doubleValue()},'${l.target_currency_name}')">取消</button>
										</c:if>
										<c:if test="${l.state==2}">
											<button class="button" style="background: #0e5f9d !important; border: 0 !important;" onclick="check(${l.id}, ${l.f_user_id},${l.order_price.doubleValue()},${l.target_amount.doubleValue()},'${l.target_currency_name}')">审核</button>
											<button class="button" onclick="confirmRecord(${l.id}, ${l.f_user_id},${l.order_price.doubleValue()},${l.target_amount.doubleValue()},'${l.target_currency_name}')">确认</button>
											<button class="button" style="background: red !important; border: 0 !important;" onclick="cancelRecord(${l.id}, ${l.f_user_id},${l.order_price.doubleValue()},${l.target_amount.doubleValue()},'${l.target_currency_name}')">取消</button>
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
									<pg:pager url="managerExchange/loadRecords.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="id" value="${current.id}"/>
										<pg:param name="toUsd" value="${toUsd}"/>
										<pg:param name="uid" value="${uid}"/>
										
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
	
	$("#searchBtn").click(function() {
		var uid = $("#userId").val();
		if (uid == '') uid = 0;
		document.location.href = "<%=actionPath%>managerExchange/loadRecords.do?id=${current.id }&toUsd=${toUsd}&uid=" + uid;
	});
	
	function check(recordid, uid, price, amount, symbol) {
		var r = confirm("审核用户" + uid + "以" + price + "的价格兑换" + amount + "个" + symbol + "?");
		if (r == true) {
		    $.ajax({
				type : "POST",
				dataType : "json",
				url : "<%=actionPath%>managerExchange/checkRecord.do",
				data : {
					recordId : recordid
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
	
	function confirmRecord(recordid, uid, price, amount, symbol) {
		var r = confirm("确定用户" + uid + "以" + price + "的价格兑换" + amount + "个" + symbol + "?");
		if (r == true) {
		    $.ajax({
				type : "POST",
				dataType : "json",
				url : "<%=actionPath%>managerExchange/confirmRecord.do",
				data : {
					recordId : recordid
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
	
	function cancelRecord(recordid, uid, price, amount, symbol) {
		var r = confirm("取消用户" + uid + "以" + price + "的价格兑换" + amount + "个" + symbol + "?");
		if (r == true) {
		    $.ajax({
				type : "POST",
				dataType : "json",
				url : "<%=actionPath%>managerExchange/cancelRecord.do",
				data : {
					recordId : recordid
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