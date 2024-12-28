<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>交易对编辑</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript">
			function postsubmit(){
				return true;
			}
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						交易区编辑(币币交易)
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerSpotMarket/updateCoinPair.do" method="post" onsubmit="return postsubmit();">
									<div>
										<input type="hidden" name="coinPairId" id="coinPairId" class="text-input " value="${coinPair.id}"></input>
										<div class="text-label">交易区:</div>
										<input class="" readonly="readonly" value="${coinPair.market_coin_name }" />
									</div>
									<div>
										<div class="text-label">交易对币种：</div>
										<input class="" readonly="readonly" value="${coinPair.coin_name }" />
									</div>
									<div>
										<div class="text-label">交易对状态：</div>
										<select name="state" id="state">
											<c:if test="${coinPair.state == 1}">
												<option value="1" selected="selected">正常</option>
												<option value="2">关闭</option>
											</c:if>
											<c:if test="${coinPair.state == 2}">
												<option value="1">正常</option>
												<option value="2" selected="selected">关闭</option>
											</c:if>
										</select>
									</div>
									<div>
										<div class="text-label">最小交易数量：</div>
										<input type="text" name="minTradeNums" id="minTradeNums" class="text-input " value="${coinPair.min_trade_nums}"></input>
									</div>
									<div>
										<div class="text-label">最小波动：</div>
										<input type="text" name="minFloatPrice" id="minFloatPrice" class="text-input " value="${coinPair.min_float_price}"></input>
									</div>
									<div>
										<div class="text-label" style="width: 120px;">交易量小数位数：</div>
										<input type="text" name="decimals" id="decimals" class="text-input " value="${coinPair.decimals}"></input>
									</div>
									<div>
										<div class="text-label">币种买入费率:</div>
										<input class="" name="coinBuyRate" type="text" value="${coinPair.coin_buy_rate }" />
									</div>
									<div>
										<div class="text-label">币种卖出费率:</div>
										<input class="" name="coinSellRate" type="text" value="${coinPair.coin_sell_rate }" />
									</div>
									<div>
										<div class="text-label">是否暂停：</div>
										<select name=suspend id="suspend">
											<c:if test="${coinPair.suspend}">
												<option value="true" selected="selected">是</option>
												<option value="false">否</option>
												</c:if>
												<c:if test="${!coinPair.suspend}">
												<option value="true">是</option>
												<option value="false" selected="selected">否</option>
											</c:if>
										</select>
									</div>
									
									<div>
										<div class="text-label">安全密码：</div>
										<input class="text-input " name="password" type="password" value="" />
									</div>
									<div>
										<div class="text-label"></div>
										<button type="submit" class="button">
											提交
										</button>
									</div>
									<div class="clear"></div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
