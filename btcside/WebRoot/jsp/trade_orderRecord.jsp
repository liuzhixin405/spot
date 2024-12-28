<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-结算记录</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath %>common/css/style.css?<%=rjs%>">
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<div class="container">

			<jsp:include page="_trade_left.jsp"></jsp:include>

			<div class="main-rt-box">

				<div class="main-wrap">
					<div class="block-title">
						<h2>结算记录
						</h2>
					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>

									<th>
										合约名称
									</th>
									<th>
										结算价格
									</th>
									<th>
										结算成交量
									</th>
									<th>
										系统亏损额
									</th>
									<th>
										净盈利部分分摊比例
									</th>

								</tr>
							</thead>
							<tbody>
								<tr>

									<td>
										BTC1407
									</td>
									<td>
										650
									</td>
									<td>
										145547
									</td>
									<td>
										-2BTC
									</td>
									<td>
										≈0.1%
									</td>

								</tr>
							</tbody>
						</table>

					</div>
				</div>
			</div>
		</div>

	</body>

</html>
