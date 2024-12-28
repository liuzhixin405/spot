<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>IEO用户充值提现</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript">
		function postsure(qr) {
			if (confirm(qr)) {
				return true;
			}
			return false;
		}
		function gourl(qr,url) {
			if (confirm(qr)) {
				document.location.href = url;
			}
		}
		function checkFlag(type){
			var qr = "";
			if (type == 1) { // 充值
				var email = document.getElementById("email").value;
				var amount = document.getElementById("amt").value;
				if(email == "" || amount == "")
					return false;
				qr = "确定要给" + email + "充值" + amount + "${currencyName}么？";
			} else if (type == 2) { // 体现
				var email = document.getElementById("email2").value;
				var amount = document.getElementById("amt2").value;
				if(email == "" || amount == "")
					return false;
				qr = "确定要给" + email + "提现" + amount + "${currencyName}么？";
			}
			if (confirm(qr)) {
				return true;
			}
			return false;
		}
		</script>

	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						IEO用户充值提现
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<table>
							<tr>
								<td>
									<div class="notification information png_bg">
										<br>
										<div>
											<form action="<%=actionPath%>managerMine/ieoRecharge.do" method="post" onsubmit="return checkFlag(1);">
												<input type="hidden" value="1" name="type">
												<c:if test="${recordId > 0}">
												<font style="margin-right: 43px">记录ID</font>
												<input name="id" id="id" type="text" value="${recordId }" readonly />
												<br />
												</c:if>
												<font style="margin-right: 4px">充值用户邮箱</font>
												<input name="email" id="email" type="text" value="${email }" ${recordId > 0 ? 'readonly' : ''} />
												<br />
												<font style="margin-right: 30px">充值数量</font>
												<input type="text" name="amt" id="amt" value="" autocomplete="off" />
												<input name="amt" id="b" type="text" value="" style="display:none;"/>
												<br />
												<font style="margin-right: 30px">安全密码</font>
												<input name="surepwd" type="password" id="p" value="" style="display:none;"/>
												<input name="surepwd" type="password" value="" autocomplete="off"/>
												<br />
												<font style="margin-right: 55px">备注</font>
												<input name="note" type="text" value="" />
												<br />
												<button type="submit" class="button">
													<c:if test="${recordId > 0}">
													增加记录数量
													</c:if>
													<c:if test="${recordId == null || recordId <= 0}">
													充值
													</c:if>
												</button>
											</form>
										</div>
										<br>
									</div>
								</td>
								<td>
									<div class="notification error png_bg">
										<br>
										<div>
											<form action="<%=actionPath%>managerMine/ieoRecharge.do" method="post" onsubmit="return checkFlag(2);">
												<input type="hidden" value="2" name="type">
												<c:if test="${recordId > 0}">
												<font style="margin-right: 43px">记录ID</font>
												<input name="id" id="id" type="text" value="${recordId }" readonly />
												<br />
												</c:if>
												<font style="margin-right: 4px">提现用户邮箱</font>
												<input name="email" id="email2" type="text" value="${email }" ${recordId > 0 ? 'readonly' : ''} />
												<br />
												<font style="margin-right: 30px">提现数量</font>
												<input name="amt" id="amt2" type="text" value="" autocomplete="off"/>
												<input name="amt" id="b2" type="text" value="" style="display:none;"/>
												<br />
												<font style="margin-right: 30px">安全密码</font>
												<input name="surepwd" type="password" value="" autocomplete="off"/>
												<input name="surepwd" type="password" id="p2" value="" style="display:none;"/>
												<br />
												<font style="margin-right: 55px">理由</font>
												<input name="note" type="text" value="" />
												<br />
												<button type="submit" class="button">
													<c:if test="${recordId > 0}">
													减少记录数量
													</c:if>
													<c:if test="${recordId == null || recordId <= 0}">
													提现
													</c:if>
												</button>
											</form>
										</div>
										<br>
									</div>
								</td>
							</tr>
						</table>
						
						<div class="tab-content default-tab" id="tab1">
							<div style="line-height: 24px;">
								<div class="notification depth-form">
									<div>
										<form action="<%=actionPath%>managerMine/ieoRecords.do" method="get">
											用户ID
											<input name="userId" type="text" value="${userId }"/>
											&nbsp;&nbsp;&nbsp;&nbsp;用户邮箱
											<input name="email" type="text" value="${email }"/>
											<button type="submit" class="button" style="margin-left: 20px; width: 50px;">查询</button>
										</form>
									</div>
									
									<div class="clear"></div>
								</div>
							</div>
						</div>
					
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										用户
									</th>
									<th>
										操作类型
									</th>
									<th>
										币种ID
									</th>
									<th>
										数量
									</th>
									<th>
										已释放
									</th>
									<th>
										记录时间
									</th>
									<th>
										更新时间
									</th>
									<th>
										备注说明
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
											<c:out value="${l.f_user_id}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.operation==1}">
													<font color="green">充值</font>
												</c:when>
												<c:when test="${l.operation==2}">
													<font color="red">提现</font>
												</c:when>
												<c:otherwise>
												未知
												</c:otherwise>
											</c:choose>
										</TD>
										<TD>
											<c:out value="${l.currency_type_id}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.amount}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.unlock_amount}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.record_time}"></ut:formatDate>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.update_time}"></ut:formatDate>
										</TD>
										<TD>
											<c:out value="${l.note}"></c:out>
										</TD>
										<TD>
											<c:if test="${l.operation==1}">
												<a href="<%=actionPath%>managerMine/ieoRecords.do?id=${l.id}">更改数量</a>
											</c:if>
										</TD>
									</tr>
								</c:forEach>
							</tbody>

							<tfoot>
								<tr>
									<td colspan="9">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerMine/ieoRecords.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
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
											共 ${pm.total} 条
										</div>
										<div class="clear"></div>
									</td>
								</tr>
							</tfoot>
						</table>
						<script language="javascript" type="text/javascript">
							senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
							
							function hiddenUnuseInput() {
								var m = document.getElementById("b");
								m.parentNode.removeChild(m);
								var m2 = document.getElementById("b2");
								m2.parentNode.removeChild(m2);
								var p = document.getElementById("p");
								p.parentNode.removeChild(p);
								var p2 = document.getElementById("p2");
								p2.parentNode.removeChild(p2);
							}
							
							setTimeout('hiddenUnuseInput()', 1500);
						</script>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
