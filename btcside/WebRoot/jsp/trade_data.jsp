<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Btcside区块链资产合约交易平台-市场数据</title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>
		<div class="container">

			<jsp:include page="_trade_left.jsp"></jsp:include>

			<div class="main-rt-box">
				<div class="main-data-block1">
					<div class="block-title">
						<h2>市场挂单
						</h2>
						<div class="trade-title-lt rt">
							<span>合约：</span>
							<a href="" class="current">BTC1407</a>
							<a href="">BTC1408</a>
							<a href="">BTC1409</a>
							<a href="">BTC1502</a>
						</div>

					</div>
					<div class="trade-manage-main1">
						<table cellpadding="0" cellspacing="0" class="lt">
							<thead>
								<tr>

									<th>
										累计
									</th>
									<th>
										数量
									</th>
									<th>
										买价
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>

									<td>
										5BTC
									</td>
									<td>
										1BTC
									</td>
									<td class="buy">
										5247
									</td>
								</tr>
							</tbody>
						</table>
						<table cellpadding="0" cellspacing="0" class="rt">
							<thead>
								<tr>

									<th>
										卖价
									</th>
									<th>
										数量
									</th>
									<th>
										累计
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>

									<td class="sell">
										5247
									</td>
									<td>
										1BTC
									</td>
									<td>
										5BTC
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>

				<div class="main-data-block  mr0">
					<div class="block-title">

						<h2>成交记录
						</h2>

					</div>
					<div class="trade-manage-main">
						<table cellpadding="0" cellspacing="0">
							<thead>
								<tr>
									<th>
										时间
									</th>
									<th>
										成交价
									</th>
									<th>
										成交量
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>

									<td>
										08-01 13:25
									</td>
									<td>
										5201
									</td>
									<td>
										5247
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
