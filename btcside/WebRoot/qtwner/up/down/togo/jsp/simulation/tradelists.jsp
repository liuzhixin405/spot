<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>模拟交易-交易信息</title>
		<!-- Reset Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<!-- Main Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<!-- Invalid Stylesheet. This makes stuff look pretty. Remove it if you want the CSS completely valid -->
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript">
		   function sure(qr,url)
		   {
			   if(qr!=null)
			   {
				   if(confirm(qr))
				   {
					   document.location.href = url;
				   }
			   }
			   else{
				   document.location.href = url;
				   }
				   
		   }
		   function postform(url) {
			   document.select.action=url;
			   document.select.submit();
		   }
		</script>

		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						模拟交易-交易信息管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
 								<form name="select" action="#" method="get">
									选择合约:
									<c:forEach items="${lstTrans}" var="l" varStatus="status">
										<a href="<%=actionPath%>simulation/managerTrade/loadTrades.do?tcode=${l.transaction_code }&buyuser=${buyuser}&selluser=${selluser}">
											<font color="${current eq l.transaction_code ? '#ff0000' : ''}">${l.transaction_code }</font>
										</a> &nbsp;
									</c:forEach>
	 								&nbsp; &nbsp; &nbsp;
									或者可以输入合约号:
									<input type="text" name="tcode" class="text-input small-input" value="${current}"></input>
									<button type="button" class="button" onclick="postform('<%=actionPath%>simulation/managerTrade/loadTrades.do')">
										查 询
									</button>
									<br />
									<br />
									买多用户ID
									<input type="text" name="buyuser" class="text-input small-input" value="${buyuser}"></input>
									卖空用户ID
									<input type="text" name="selluser" class="text-input small-input" value="${selluser}"></input>
									<button type="button" class="button" onclick="postform('<%=actionPath%>simulation/managerTrade/loadTrades.do')">
										查 询
									</button>
								</form>
							</div>
							<br>
						</div>
						<div class="notification success png_bg">
							<div>
								多单持仓量：
								<span style="color: #57a000;">${dc+dd+db eq null ? 0 : dc+dd+db}</span>
								&nbsp;&nbsp;多单持仓可用：
								<span style="color: #57a000;">${dc eq null ? 0 : dc}</span>
								&nbsp;&nbsp;多单持仓冻结：
								<span style="color: #57a000;">${dd eq null ? 0 : dd}</span>
								&nbsp;&nbsp;多单爆仓量：
								<span style="color: #57a000;">${db eq null ? 0 : db}</span>
								<br>空单持仓量：
								<span style="color: #57a000;">${kc+kd+kb eq null ? 0 : kc+kd+kb}</span>
								&nbsp;&nbsp;空单持仓可用：
								<span style="color: #57a000;">${kc eq null ? 0 : kc}</span>
								&nbsp;&nbsp;空单持仓冻结：
								<span style="color: #57a000;">${kd eq null ? 0 : kd}</span>
								&nbsp;&nbsp;空单爆仓量：
								<span style="color: #57a000;">${kb eq null ? 0 : kb }</span>
							</div>
							<br>
							<div>
							总交易量:  ${total }
							</div>
							<br>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										合约号
									</th>
									<th>
										买多方
									</th>
									<th>
										卖空方
									</th>
									<th>
										成交价
									</th>
									<th>
										成交数量
									</th>
									<th>
										成交时间
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
											<c:out value="${l.transaction_code}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_buy_uid}"></c:out>---
											<c:choose>
												<c:when test="${l.trade_buy_type_id==1}">
													<font color="green">多单开仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==2}">
													<font color="green">多单平仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==3}">
													<font color="red">空头开仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==4}">
													<font color="red">空头平仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==5}">
													<font color="#FF0800">多单爆仓</font>
												</c:when>
												<c:when test="${l.trade_buy_type_id==6}">
													<font color="#FF0800">空单爆仓</font>
												</c:when>
												<c:otherwise>
													${l.trade_buy_type_id}
												</c:otherwise>
											</c:choose>
											<br/>
											<c:out value="${l.f_buy_user_email}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_sell_uid}"></c:out>---
											<c:choose>
												<c:when test="${l.trade_sell_type_id==1}">
													<font color="green">多单开仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==2}">
													<font color="green">多单平仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==3}">
													<font color="red">空头开仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==4}">
													<font color="red">空头平仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==5}">
													<font color="#FF0800">多单爆仓</font>
												</c:when>
												<c:when test="${l.trade_sell_type_id==6}">
													<font color="#FF0800">空单爆仓</font>
												</c:when>
												<c:otherwise>
													${l.trade_sell_type_id}
												</c:otherwise>
											</c:choose>
											<br/>
											<c:out value="${l.f_sell_user_email}"></c:out>
										</TD>
										<TD>
										<c:out value="${l.trade_price}"></c:out>
										</TD>
										<TD>
										<c:out value="${l.trade_count}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.trade_time}"></ut:formatDate>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="simulation/managerTrade/loadTrades.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="buyuser" value="${buyuser}"/>
											<pg:param name="tcode" value="${current}"/>
											<pg:param name="selluser" value="${selluser}"/>
											
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

				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>
