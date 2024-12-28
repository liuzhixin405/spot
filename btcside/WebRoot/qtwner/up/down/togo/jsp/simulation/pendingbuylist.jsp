<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>挂单信息</title>
		<!-- Reset Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath %>css/reset.css" type="text/css" media="screen" />
		<!-- Main Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath %>css/style.css" type="text/css" media="screen" />
		<!-- Invalid Stylesheet. This makes stuff look pretty. Remove it if you want the CSS completely valid -->
		<link rel="stylesheet" href="<%=jspPath %>css/invalid.css" type="text/css" media="screen" />
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
						模拟交易-挂单${page}
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
							
								<form name="select" action="#" method="post">
									选择合约: 
									<c:forEach items="${trans}" var="l">
										<a href="<%=actionPath %>simulation/managerPending/${url}.do?tcode=${l.transaction_code}" >
										<c:choose>
											<c:when test="${currtran==l.transaction_code}">
												<font color="#ff0000">${l.transaction_code}</font>
											</c:when>
											<c:otherwise>
												${l.transaction_code}
											</c:otherwise>
										</c:choose>
										</a> &nbsp;
									</c:forEach>
									
									或者可以输入合约号: 
									<input type="text" name="tcode" class="text-input small-input" value="${currtran}"></input>

									<button type="button" class="button" onclick="postform('<%=actionPath%>simulation/managerPending/${url}.do')" >查 询</button>
									<br /><br />
									用户ID
									<input type="text" name="userid" class="text-input small-input" value="${userid}"></input>
									用户邮箱
									<input type="text" name="useremail" class="text-input small-input" value="${useremail}"></input>
									
									<button type="button" class="button" onclick="postform('<%=actionPath %>simulation/managerPending/${url}.do')" >查 询</button>
								</form>
							</div>
							<br>
						</div>
						<div class="notification success png_bg">
							<div >
								${url=='selectBuy' ? '多头开仓总量' : '空头开仓总量' }: <span style="color: #57a000;">${url=='selectBuy' ? dk : kk }</span>
								<br>${url=='selectBuy' ? '空头平仓总量' : '多头平仓总量' }: <span style="color: #57a000;">${url=='selectBuy' ? kp : dp }</span>
								<br>${url=='selectBuy' ? '买单总量' : '卖单总量' }: ${url=='selectBuy' ? dk+kp : kk+dp }
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
										用户
									</th>
									<th>
										委托价格
									</th>
									<th>
										委托数量
									</th>
									<th>
										已成交数量
									</th>
									<th>
										未成交数量
									</th>
									
									<th>
										杠杆
									</th>
									<th>
										保证金
									</th>
									<th>
										类型
									</th>
									<th>
										时间
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
											<c:if test="${l.f_user_id!=1260}">
												<font color="red">
											</c:if>
											<c:out value="${l.f_user_id}"></c:out><br/>
											<c:out value="${l.f_user_email}"></c:out>
											<c:if test="${l.f_user_id!=1260}">
												</font>
											</c:if>
										</TD>
										<TD>
											<c:out value="${l.delegation_price}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.delegation_count}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.deal_count}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.undeal_count}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.multiple}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.deposit}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.priority==1}">开仓</c:when>
												<c:when test="${l.priority==2}">平仓</c:when>
												<c:when test="${l.priority==3}">爆仓</c:when>
											</c:choose>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.delegation_time}"></ut:formatDate>
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
										<pg:pager url="simulation/managerPending/${url}.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="userid" value="${userid}"/>
											<pg:param name="tcode" value="${currtran}"/>
											<pg:param name="useremail" value="${useremail}"/>
											
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
										共 ${pm.total} 条
									</div>
									<!-- End .pagination -->
									<div class="clear"></div>
								</td>
							</tr>
						</tfoot>
						<script language="javascript">
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
