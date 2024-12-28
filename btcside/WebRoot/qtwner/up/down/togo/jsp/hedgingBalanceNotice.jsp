<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<title></title>
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
						<c:if test="${notice.type == 1}">合约对冲账户资产通知</c:if>
						<c:if test="${notice.type == 2}">币币兑换账户资产通知</c:if>
						<c:if test="${notice.type == 3}">币币交易账户资产通知</c:if>
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div style="line-height: 24px;">
							<c:if test="${notice.type == 1}">
								<h5 style="float:left; margin-right: 10px;">选择合约:</h5>
								<c:forEach items="${transList}" var="l" varStatus="status">
									<a href="<%=actionPath%>managerArbitrage/hedgingBalanceNotice.do?currencyName=${l.transaction_code }&type=1">
										<font color="${current.transaction_code == l.transaction_code ? '#ff0000' : ''}">${l.transaction_code }</font>
									</a> &nbsp;
								</c:forEach>
							</c:if>
							<c:if test="${notice.type == 2}">
								<h5 style="float:left; margin-right: 10px;">选择币种:</h5>
								<c:forEach items="${coins}" var="l" varStatus="status">
									<a href="<%=actionPath%>managerArbitrage/hedgingBalanceNotice.do?currencyName=${l.type_name }&type=2">
										<font color="${currCoin.type_name == l.type_name ? '#ff0000' : ''}">${l.type_name.toUpperCase() }</font>
									</a> &nbsp;
								</c:forEach>
							</c:if>
							<c:if test="${notice.type == 3}">
								<h5 style="float:left; margin-right: 10px;">选择交易对的币种:</h5>
								<c:forEach items="${coins}" var="l" varStatus="status">
									<a href="<%=actionPath%>managerSpotArbitrage/hedgingBalanceNotice.do?currencyName=${l.type_name }&type=3">
										<font color="${currCoin.type_name == l.type_name ? '#ff0000' : ''}">${l.type_name.toUpperCase() }</font>
									</a> &nbsp;
								</c:forEach>
							</c:if>
							<div class="clear"></div>
							<form class="notification depth-form" action="${notice.type == 3 ? 'managerSpotArbitrage' : 'managerArbitrage'}/updateHedgingBalanceNotice.do" method="post">
								<input type="hidden" name="id" value="${notice.id}"></input>
								<input type="hidden" name="type" value="${notice.type}"/></input>
								<input type="hidden" name="currency_name" value="${notice.currency_name}"></input>
								
								<label for="threshold" style="width: 220px">${notice.currency_name}临界值（低于该值发邮件）：</label>
								<input name="threshold" value="${notice.threshold.doubleValue()}"></input>
								<div class="clear"></div>
							
								<label for="email" style="width: 220px">邮箱：</label>
								<input name="email" value="${notice.email}"></input>
								<div class="clear"></div>
								
								<button type="submit" id="submit" class="button">提交</button>
							</form>
						</div>
						
						
					</div>
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									币种名称
								</th>
								<th>
									临界值
								</th>
								<th>
									邮箱
								</th>
								<th>
									类型
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
										<c:out value="${l.currency_name.toUpperCase() }"></c:out>
									</td>
									<td>
										<c:out value="${l.threshold.doubleValue() }"></c:out>
									</td>
									<td>
										<c:out value="${l.email}"></c:out>
									</td>
									<td>
										<c:choose>
										<c:when test="${l.type==1}">
											合约账户
										</c:when>
										<c:when test="${l.type==2}">
											币币兑换
										</c:when>
										<c:when test="${l.type==3}">
											币币交易
										</c:when>
										<c:otherwise>
											未知
										</c:otherwise>
										</c:choose>
									</td>
									<TD>
										<a class="link" href="javascript:sure('确定删除?','<%=actionPath %>${l.type == 3 ? 'managerSpotArbitrage' : 'managerArbitrage'}/deleteHedgingBalanceNotice.do?id=${l.id}')" >删除</a>
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
									<pg:pager url="${notice.type == 3 ? 'managerSpotArbitrage' : 'managerArbitrage'}/hedgingBalanceNotice.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="currencyName" value="${notice.currency_name}"/>
										<pg:param name="type" value="${notice.type}"/>
										
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
</html>