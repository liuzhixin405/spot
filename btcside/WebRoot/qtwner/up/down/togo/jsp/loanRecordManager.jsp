<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>质押借币记录</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
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
						质押借币记录
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerLoanSetting/loanRecordManager.do" method="post">
						
							<label for="userId">借入用户Id：</label>
							<input name="userId" value="${userId}"></input>
							<div class="clear"></div>
							
							<label for="email">借入用户邮箱：</label>
							<input name="email" value="${email}"></input>
							<div class="clear"></div>
							
							
							<label for="borrowId">借出用户Id：</label>
							<input name="borrowId" value="${borrowId}"></input>
							<div class="clear"></div>
							
							<button type="submit" class="button">查询</button>
						</form>
					</div>
					
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									时间
								</th>
								<th>
									用户Id
								</th>
								<th>
									抵押币种
								</th>
								<th>
									借出币种
								</th>
								<th>
									借出方
								</th>
								<th>
									借出数量
								</th>
								<th>
									抵押数量
								</th>
								<th>
									周期（天）
								</th>
								<th>
									利率
								</th>
								<th>
									手续费率
								</th>
								<th>
									已还本息
								</th>
								<th>
									已释放质押
								</th>
								<th>
									手续费
								</th>
								<th>
									已抵偿本息
								</th>
								<th>
									抵偿手续费
								</th>
								<th>
									状态
								</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${pm.datas}" var="l" varStatus="status">
								<tr>
									<td>
										<ut:formatDate dateline="${l.record_time}"></ut:formatDate>
									</td>
									<td>
										${l.f_user_id}
									</td>
									<td>
										${l.pledge_symbol}
									</td>
									<td>
										${l.borrow_symbol}
									</td>
									<td>
										${l.borrow_uid}
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.borrow_amount}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.pledge_amount}'/>
									</td>
									<td>
										${l.cycle}
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.day_rate}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.fee_rate}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.back_borrow_amount + interest}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.back_pledge_amount}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.fee}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.payment_pledge_amount + l.payment_interest}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.payment_fee}'/>
									</td>
									<td>
										<c:if test="${l.state == 0}">已提交</c:if>
										<c:if test="${l.state == 1}">计息中</c:if>
										<c:if test="${l.state == 2}">已还款</c:if>
										<c:if test="${l.state == 3}">已平仓</c:if>
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
									<pg:pager url="managerLoanSetting/loanRecordManager.do" items="${pm.total}" maxIndexPages="10" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="userId" value="${userId}"/>
										<pg:param name="email" value="${email}"/>
										
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
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
	</script>
</html>