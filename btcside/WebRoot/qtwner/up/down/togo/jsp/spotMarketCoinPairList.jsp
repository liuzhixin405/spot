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
		<title>交易对管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script lang="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript" src="<%=actionPath %>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript">
		function optionSelect(obj) {
			vas = document.getElementById("coinId").options;
			for(var i = 0; i <= vas.length-1; i++) {
				vas[i].style.display = "";
			}
			var delEles = document.getElementsByClassName(obj)[0];	
			delEles.style.display = "none";		
		}	
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						交易对管理(币币交易)
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerSpotMarket/addCoinPair.do" method="post" onsubmit="return postsubmit();">
									<div class="form-item">
										<div class="text-label">交易区:</div>
										<select name="marketId" id="marketId" onchange="optionSelect(this[this.selectedIndex].id);">
											<option>请选择</option>
											<c:forEach items="${markets}" var="market">
												<option value="${market.id}" id="${market.market_name}">${market.market_name }</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-item">
										<div class="text-label" style="width: 120px">交易对币种类型：</div>
										<select id="coinId" name="coinId" >
											<option>请选择</option>
											<c:forEach var="item" varStatus="stat" items="${coins }">
												<option value="${item.type_id}" id="${item.type_name}" class="${item.type_name}">${item.type_name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="form-item">
										<div class="text-label">交易对状态：</div>
										<select name="state" id="state">
											<option value="1">正常</option>
											<option value="2">关闭</option>
										</select>
									</div>
									<div class="form-item">
										<div class="text-label">最小交易数量：</div>
										<input type="text" name="minTradeNums" id="minTradeNums" class="text-input " value=""></input>
									</div>
									<div class="form-item">
										<div class="text-label">最小波动价格：</div>
										<input type="text" name="minFloatPrice" id="minFloatPrice" class="text-input " value=""></input>
									</div>
									<div class="form-item">
										<div class="text-label">交易量小数位数：</div>
										<input type="text" name="decimals" id="decimals" class="text-input " value=""></input>
									</div>
									<div class="form-item">
										<div class="text-label">币种买入费率：</div>
										<input class="text-input " name="coinBuyRate" type="text" value="" />
									</div>
									<div class="form-item">
										<div class="text-label">币种卖出费率：</div>
										<input class="text-input " name="coinSellRate" type="text" value="" />
									</div>
									<div class="form-item">
										<div class="text-label">安全密码：</div>
										<input class="text-input " name="password" type="password" value="" />
									</div>
									<div class="align-left">
										<button type="submit" class="button">
											新 增
										</button>
									</div>
									<div class="clear"></div>
								</form>
							</div>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										交易对
									</th>
									<th>
										交易对状态
									</th>
									<th>
										最小交易数量
									</th>
									<th>
										最小波动价格
									</th>
									<th>
										交易量小数位数
									</th>
									<th>
										币种买入费率
									</th>
									<th>
										币种卖出费率
									</th>
									<th>
										最新成交价
									</th>
									<th>
										最高报价
									</th>
									<th>
										最低报价
									</th>
									<th>
										交易区
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${pm.datas}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${status.index+1}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.coin_pair_name}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.state==1}">
													<font color="green">有效</font>
												</c:when>
												<c:when test="${l.state==2}">关闭</c:when>
												<c:when test="${l.state==3}">异常</c:when>
											</c:choose>
										</TD>
										<TD>
											<c:out value="${l.min_trade_nums}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.min_float_price}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.decimals}"></c:out>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.coin_buy_rate }" pattern="#.#####"></fmt:formatNumber>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.coin_sell_rate }" pattern="#.#####"></fmt:formatNumber>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.latest_price }" pattern="#.####"></fmt:formatNumber>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.highest_price }" pattern="#.####"></fmt:formatNumber>
										</TD>
										<TD>
											<fmt:formatNumber value="${l.lowest_price }" pattern="#.####"></fmt:formatNumber>
										</TD>
										<TD>
											<c:out value="${l.market_coin_name}">交易区</c:out>
										</TD>
										<TD>
											<a href="<%=actionPath%>managerSpotMarket/editCoinPair.do?id=${l.id}">编辑</a>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerSpotMarket/loadCoinPair.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="bqCode" value="${bqCode}" />
												<pg:param name="state" value="${state}" />
												
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
										<div class="clear"></div>
									</td>
								</tr>
							</tfoot>
						</table>
						
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
