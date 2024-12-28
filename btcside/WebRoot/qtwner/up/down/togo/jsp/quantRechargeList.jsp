<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>充提记录</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						充提记录
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="">
							<div style="line-height: 24px;">
								<h5 style="float:left; margin-right: 10px;">选择币种:</h5>
								<c:forEach items="${currencyTypes}" var="l" varStatus="status">
									<a href="<%=actionPath %>managerQuantRisk/quantRechargeList.do?currencyId=${l.type_id}&curr=${currpage}&uemail=${uemail}&uid=${uid}">
										<font color="${currencyId == l.type_id ? '#ff0000' : ''}">${l.type_name.toUpperCase() }</font>
									</a> &nbsp;
								</c:forEach>
								<div class="clear"></div>
								
								<h5 style="float:left; margin-right: 10px;">选择方向:</h5>
									<a href="<%=actionPath %>managerQuantRisk/quantRechargeList.do?currencyId=${currencyId}&curr=0&uemail=${uemail}&uid=${uid}" >
										<c:choose>
											<c:when test="${currpage==0}">
												<font color="red">所有</font>
											</c:when>
											<c:otherwise>
												所有
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath %>managerQuantRisk/quantRechargeList.do?currencyId=${currencyId}&curr=1&uemail=${uemail}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==1}">
												<font color="red">充值</font>
											</c:when>
											<c:otherwise>
												充值
											</c:otherwise>
										</c:choose></a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerQuantRisk/quantRechargeList.do?currencyId=${currencyId}&curr=2&uemail=${uemail}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==2}">
												<font color="red">提现</font>
											</c:when>
											<c:otherwise>
												提现
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
								<div class="clear"></div>
							</div>
						</div>
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath %>managerQuantRisk/quantRechargeList.do" method="get">
									用户ID
									<input name="uid" type="text" value="${uid}" />
									用户邮箱
									<input name="uemail" type="text" value="${uemail}" />
									<input type="hidden" name="currencyId" value="${currencyId}" />
									<input type="hidden" name="curr" value="${currpage}" />
									<button type="submit" class="button">查 询</button>
								</form>
								
							</div>
							<div>
								总充值：
								<span style="color: #57a000;">${allRecharge eq null ? 0 : allRecharge}</span>
								&nbsp;&nbsp;总提币：
								<span style="color: #57a000;">${allWithDraw eq null ? 0 : allWithDraw}</span>
							</div>
						</div>
						
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										用户
									</th>
									<th>
										操作类型
									</th>
									<th>
										数量 / 手续费
										<br/>实际到账
									</th>
									<th>
										地址
									</th>
									<th>
										申请时间
									</th>
									<th>
										确认时间
									</th>
									<th>
										状态
									</th>
									<th>
										备注说明
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
											<c:out value="${l.f_user_id}"></c:out><br/>
											<c:out value="${l.f_user_email}"></c:out>
										</TD>
										<TD>
											<c:choose>
											<c:when test="${l.operation==1}">
												<font color="green">充值</font>
											</c:when>
											<c:when test="${l.operation==2}">
												<font color="red">提现</font><br/>
											</c:when>
											<c:when test="${l.operation==3}">
												<font color="green">转入</font>
											</c:when>
											<c:when test="${l.operation==4}">
												<font color="red">转出</font>
											</c:when>
											<c:otherwise>
												未知
											</c:otherwise>
											</c:choose>
											<br/>
											
										</TD>
										<TD>
											<c:out value="${l.amount}"></c:out> / <c:out value="${l.fee}"></c:out>
											<br/>
											<font color="blue"><c:out value="${l.amount-l.fee}"></c:out></font>
										</TD>
										<TD>
											<c:out value="${l.receice_card_no}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.dateline}"></ut:formatDate>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.confirm_time}"></ut:formatDate>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.state==0}">
													<font color="red">未受理</font>
												</c:when>
												<c:when test="${l.state==1}">受理成功</c:when>
												<c:when test="${l.state==2}">拒绝</c:when>
												<c:when test="${l.state==3}">自动处理中</c:when>
												<c:when test="${l.state==4}"><font color="red">转人工</font></c:when>
											</c:choose>
										</TD>
										<TD>
											<input type="text" name="remark" value="${l.remark}" />
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
										<pg:pager url="managerQuantRisk/quantRechargeList.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="uid" value="${uid}"/>
											<pg:param name="uemail" value="${uemail}"/>
											<pg:param name="curr" value="${currpage}"/>
											<pg:param name="currencyId" value="${currencyId}"/>
									
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
