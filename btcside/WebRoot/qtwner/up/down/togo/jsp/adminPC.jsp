<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>管理员平仓</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css"
			type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"
			type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css"
			type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8"
			src="<%=jspPath%>js/page.js"></script>
		<style>
table {
	width: 100%;
	border: 1px solid #CCC;
	border-spacing: 0;
	border-collapse: collapse;
}

th,td {
	border: 1px solid #CCC;
	text-align: center;
}
</style>

<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
	function admin_pc(url) {
	var mtid = Number($('#mtid').val());
	if(mtid =="" || isNaN(mtid) ) {
			alert("持仓ID不能为空且必须是数字");
			return;
		}
   var userId = Number($('#userId').val());
   if(userId =="" || isNaN(mtid) ) {
		alert("用户ID不能为空且必须是数字");
		return;
	}
	var price = Number($('#price').val());
	if(price =="" || isNaN(price) ||  price <= 0 ) {
		alert("价格不能为空且必须是合法数字");
		return;
	}

	var amount = Number($('#amount').val());
	if(amount =="" || isNaN(amount) || amount <= 0) {
		
		alert("数量不能为空且必须是合法数字");
		return;
	}
		$.ajax( {
			url : url,
			type : "GET",
			data : $('#adminpcform').serialize(),
			dataType : "JSON",
			cache : false,
			statusCode : {
				404 : function() {
					location.reload();
				}
			},
			success : function(data) {

				resultInfo_pc(data.msg, data.code);

			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		});

	}

	function resultInfo_pc(text, code) {
		if (text.length > 0) {
			alert(text);
		} else {

		}
	}
</script>


	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						管理员后台平仓
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<form action="<%=actionPath%>adminpc/queryTrade.do"
									method="post">
									用户ID：
									<input type="text" name="userId" class="text-input "
										value="${userId }"></input>
									邮箱
									<input type="text" name="email" class="text-input "
										value="${email }"></input>
									<button type="submit" class="button">
										查 询
									</button>
								</form>
							</div>
						</div>

					</div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<form id="adminpcform" action="<%=actionPath%>adminpc/tradePC.do" method="post">
									挂单号：
									<input type="text" name="mtid" id ="mtid" class="text-input"></input>
									userId：
									<input type="text" name="userId" id="userId" class="text-input"
										value=${userId}></input>
									价格:
									<input type="text" name="price" id="price" class="text-input"></input>
									数量:
									<input type="text" name="amount" id="amount" class="text-input"></input>
									<button type="submit" class="button">
										平仓
									</button>
								</form>
							</div>
						</div>

					</div>
				</div>


				<p>
					买入的持仓
				</p>
				<!-- 买入的持仓 -->
				<div>
					<table cellpadding="0" cellspacing="0" border="0">
						<c:forEach items="${trans_buy_map}" var="trans_map">
							<tr>
								<td width="50px">
									${trans_map.key}
								</td>
								<td>
									<table cellpadding="0" cellspacing="0" border="0">
										<thead>
											<tr>
												<th>
													持仓ID
												</th>
												<th>
													成交类型
												</th>
												<th>
													杠杆
												</th>
												<th>
													持仓量
												</th>
												<th>
													冻结量
												</th>
												<th>
													合约均价
												</th>

											</tr>
										</thead>
										<c:forEach items="${trans_map.value}" var="tran">
											<tr>
												<td>
													${tran.mt_id}
												</td>
												<td>
													${tran.trade_type_id == 1 ? "买入" : "卖出"}
												</td>
												<td>
													${tran.trade_multiple}
												</td>
												<td>
													${tran.hold_count}
												</td>
												<td>
													${tran.lock_count}
												</td>
												<td>
													${tran.average_price}
												</td>

											</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>
				<p>
					卖出的持仓
				</p>
				<!-- 卖出的持仓 -->
				<div>
					<table cellpadding="0" cellspacing="0" border="0">
						<c:forEach items="${trans_sell_map}" var="trans_map">
							<tr>
								<td width="50px">
									${trans_map.key}
								</td>
								<td>
									<table cellpadding="0" cellspacing="0" border="0">
										<thead>
											<tr>
												<th>
													持仓ID
												</th>
												<th>
													成交类型
												</th>
												<th>
													杠杆
												</th>
												<th>
													持仓量
												</th>
												<th>
													冻结量
												</th>
												<th>
													合约均价
												</th>

											</tr>
										</thead>
										<c:forEach items="${trans_map.value}" var="tran">
											<tr>
												<td>
													${tran.mt_id}
												</td>
												<td>
													${tran.trade_type_id == 1 ? "买入" : "卖出"}
												</td>
												<td>
													${tran.trade_multiple}
												</td>
												<td>
													${tran.hold_count}
												</td>
												<td>
													${tran.lock_count}
												</td>
												<td>
													${tran.average_price}
												</td>

											</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
		</div>


	
	</body>
</html>
