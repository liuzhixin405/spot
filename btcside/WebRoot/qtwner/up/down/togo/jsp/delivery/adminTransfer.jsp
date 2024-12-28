<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>交割合约划转</title>
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
						交割合约划转
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>delivery/managerTransfer/queryUserAccount.do" method="get">
									合约号:
									<select name="code" id="code" >
										<c:forEach items="${transactionList}" var="l">
											<c:if test="${l.transaction_code eq currentTransaction.transaction_code}">
												<option value="${l.transaction_code}" selected="selected">
													${l.transaction_code}
												</option>
											</c:if>
											<c:if test="${l.transaction_code ne currentTransaction.transaction_code}">
												<option value="${l.transaction_code}">
													${l.transaction_code}
												</option>
											</c:if>
										</c:forEach>
									</select>
									类型:
									<select name="type" id="type" >
										<c:choose>
											<c:when test="${type eq 1}">
												<option value="1" selected='selected'>转入</option>	
											</c:when>
											<c:otherwise>
												<option value="1">转入</option>	
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${type eq 2}">
												<option value="2" selected='selected'>转出</option>	
											</c:when>
											<c:otherwise>
												<option value="2">转出</option>	
											</c:otherwise>
										</c:choose>
									</select>
									用户ID：
									<input name="userId" type="text" value="${userId}" />
									用户邮箱：
									<input name="email" type="text" value="${email}" />
									<button type="submit" class="button">查 询</button>
								</form>
								<br>
							</div>
							
							<div>
								钱包账户余额：${accountAmount} &nbsp;USDT
								<br>
								合约账户余额：${contractAmount} &nbsp;USDT
							</div>
						</div>
						
						<table>
							<tr>
								<td>
									<div class="notification information png_bg">
										<br>
										<div>
											<form action="" method="post" onsubmit="return transfer(1);">
												<font style="margin-right: 4px">转入用户邮箱</font>
												<input name="uemail" id="uemail" type="text" value="${email }" />
												<br />
												<font style="margin-right: 17px">选择合约号</font>
												<select name="tcode" id="tcode" style="width: 173px;">
													<c:forEach items="${transactionList}" var="l">
														<c:if test="${l.transaction_code eq currentTransaction.transaction_code}">
															<option value="${l.transaction_code}" selected="selected">
															${l.transaction_code}
															</option>
														</c:if>
														<c:if test="${l.transaction_code ne currentTransaction.transaction_code}">
															<option value="${l.transaction_code}">
															${l.transaction_code}
															</option>
														</c:if>
													</c:forEach>
												</select>
												<br />
												<font style="margin-right: 30px">转入数量</font>
												<input type="text" name="amt" id="amt" value="" autocomplete="off" />
												<br />
												<button type="submit" class="button">
													转入
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
											<form action="" method="post" onsubmit="return transfer(2);">
												<font style="margin-right: 4px">转出用户邮箱</font>
												<input name="uemail" id="uemail2" type="text" value="${email }" />
												<br />
												<font style="margin-right: 17px">选择合约号</font>
												<select name="tcode" id="tcode2" style="width: 173px;">
													<c:forEach items="${transactionList}" var="l">
														<c:if test="${l.transaction_code eq currentTransaction.transaction_code}">
															<option value="${l.transaction_code}" selected="selected">
															${l.transaction_code}
															</option>
														</c:if>
														<c:if test="${l.transaction_code ne currentTransaction.transaction_code}">
															<option value="${l.transaction_code}">
															${l.transaction_code}
															</option>
														</c:if>
													</c:forEach>
												</select>
												<br />
												<font style="margin-right: 30px">转出数量</font>
												<input name="amt" id="amt2" type="text" value="" autocomplete="off"/>
												
												<br />
												<button type="submit" class="button">
													转出
												</button>
											</form>
										</div>
										<br>
									</div>
								</td>
							</tr>
						</table>
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
										操作时间
									</th>
									<th>
										操作类型
									</th>
									<th>
										合约号
									</th>
									<th>
										改变金额
									</th>
									<th>
										原有资金
									</th>
									<th>
										现有资金
									</th>
									
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${pm.datas}" var="log" varStatus="l">
										<tr>
											<TD>
												<c:out value="${status.index+1}"></c:out>
											</TD>
											<td>
												<c:out value="${log.f_user_id }"></c:out>
											</td>
											<td>
												<ut:formatDate dateline="${log.dateline }"></ut:formatDate>
											</td>
											<td >
												<c:choose>
													<c:when test="${log.operation==1}">
														转入
													</c:when>
													<c:when test="${log.operation==2}">转出</c:when>
													<c:otherwise>其他</c:otherwise>
												</c:choose>
											</td>
											<td >
												<c:out value="${log.f_code }"></c:out>
											</td>
											<td>
												<fmt:formatNumber value="${log.change_amount }" pattern="#.####"></fmt:formatNumber>USDT
											</td>
											<td>
												<fmt:formatNumber value="${log.current_amount }" pattern="#.####"></fmt:formatNumber>USDT
											</td>
											<td>
												<fmt:formatNumber value="${log.lastest_amount }" pattern="#.####"></fmt:formatNumber>USDT
											</td>
										</tr>
									</c:forEach>
							</tbody>

							<tfoot>
								<tr>
									<td colspan="9">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="delivery/managerTransfer/queryUserAccount.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="userId" value="${userId}"/>
												<pg:param name="email" value="${email}"/>
												<pg:param name="code" value="${code}"/>
												<pg:param name="type" value="${type}"/>
											
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
						
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
	<script type="text/javascript">
	
		function transfer(type){
			var qr = "";
			var url = "";
			var email;
			var amount;
			var code;
			if (type == 1) { // 转入
				email = document.getElementById("uemail").value;
				amount = document.getElementById("amt").value;
				code = document.getElementById("tcode").value;
				if(email == "" || amount == "" || code == "")
					return false;
				qr = "确定给" + email + "的" + code + "合约账户转入" + amount + "USDT";
				url = "<%=actionPath%>delivery/managerTransfer/transferIndo.do";
			} else if (type == 2) { // 转出
				email = document.getElementById("uemail2").value;
				amount = document.getElementById("amt2").value;
				code = document.getElementById("tcode2").value;
				if(email == "" || amount == "" || code == "")
					return false;
				qr = "确定将" + email + "的" + code + "合约账户转出" + amount + "USDT";
				url = "<%=actionPath%>delivery/managerTransfer/transferOutdo.do";
			}
			if (confirm(qr)) {
				$.ajax({
					type : "POST",
					dataType : "json",
					url : url,
					data : {
						email : email,
						amount : amount,
						code : code
					},
					success : function(data) {
						if (data.code == 'success') {
							window.location.reload();
						} else {
							alert(data.msg);
						}
					}
				});
			}
		}
	</script>
</html>
