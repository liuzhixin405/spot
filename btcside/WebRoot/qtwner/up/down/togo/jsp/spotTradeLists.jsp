<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>交易信息</title>
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
						交易信息管理
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
									选择交易对:
									<c:forEach items="${lstTrans}" var="l" varStatus="status">
										<a href="<%=actionPath%>${url}/selectTrades.do?coinPairName=${l.coin_pair_name }">
											<font color="${current==(status.index+1) ? '#ff0000' : ''}">${l.coin_pair_name }</font>
										</a> &nbsp;
									</c:forEach>
	 								&nbsp; &nbsp; &nbsp;
									或者可以输入交易对:
									<input type="text" name="coinPairName" class="text-input small-input" value="${coinPairName}"></input>
									<button type="button" class="button" onclick="postform('<%=actionPath%>${url}/selectTrades.do')">
										查 询
									</button>
									<br />
									<br />
									买单用户ID
									<input type="text" name="buyuser" class="text-input small-input" value="${buyuser}"></input>
									卖单用户ID
									<input type="text" name="selluser" class="text-input small-input" value="${selluser}"></input>
									<button type="button" class="button" onclick="postform('<%=actionPath%>${url}/userTrades.do')">
										查 询
									</button>
								</form>
							</div>
							<br>
						</div>
						<div class="notification success png_bg">
							<div>
								买单量：<span style="color: #57a000;">${db eq null ? 0 : db}</span>
								<br>
								卖单量：<span style="color: #57a000;">${kb eq null ? 0 : kb }</span>
							</div>
							<br>
							<div>
							总交易量:  ${db + kb }
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
										买方
									</th>
									<th>
										卖方
									</th>
									<th>
										成交方向
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
											<c:out value="${l.coin_pair_name}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_buy_uid}"></c:out>
											<br/>
											<c:out value="${l.f_buy_user_email}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.f_sell_uid}"></c:out>
											<br/>
											<c:out value="${l.f_sell_user_email}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.trade_trans_dirction==1}">
													<font color="green">买单</font>
												</c:when>
												<c:when test="${l.trade_trans_dirction==2}">
													<font color="green">卖单</font>
												</c:when>
												<c:otherwise>
												${l.trade_trans_dirction}
												</c:otherwise>
											</c:choose>
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
										<pg:pager url="${url}/userTrades.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="buyuser" value="${buyuser}"/>
											<pg:param name="coinPairName" value="${coinPairName}"/>
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
<!--
//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
--></script>


					</div>

				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>
