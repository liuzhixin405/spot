<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>挂单信息</title>
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
			
			var currCoinName = document.getElementById("currCoinName").innerHTML;
			
			var qr = "";
			if (type == 1) { // 充值
				var email = document.getElementById("uemail").value;
				var amount = document.getElementById("amt").value;
				if(email == "" || amount == "")
					return false;
				
				var selectType = document.getElementById("type1");
				qr = "确定要给" + email + selectType.options[selectType.selectedIndex].text + amount + currCoinName + "么？";
			} else if (type == 2) { // 体现
				var email = document.getElementById("uemail2").value;
				var amount = document.getElementById("amt2").value;
				if(email == "" || amount == "")
					return false;
				var selectType = document.getElementById("type2");
				qr = "确定要给" + email + selectType.options[selectType.selectedIndex].text + amount + currCoinName + "么？";
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
						管理员充值
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<h5 style="margin-left: 10px; float:left; margin-right: 10px;">管理员充值</h5>
							<c:forEach items="${currencyTypes}" var="l" varStatus="status">
									<a href="<%=actionPath%>managerRecharge/loadAdmin.do?currencyId=${l.type_id}&curr=${currpage}&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<font color="${currencyId == l.type_id ? '#ff0000' : ''}"  id="${currencyId == l.type_id ? 'currCoinName' : ''}">${l.type_name.toUpperCase() }</font>
									</a> &nbsp;
								</c:forEach>
							<div class="clear"></div>
							
							<h5 style="margin-left: 10px; float:left; margin-right: 10px;">选择操作类型:</h5>
									<a href="<%=actionPath%>managerRecharge/loadAdmin.do?currencyId=${currencyId}&curr=0&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}" >
										<c:choose>
											<c:when test="${currpage==0}">
												<font color="red">所有</font>
											</c:when>
											<c:otherwise>
												所有
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerRecharge/loadAdmin.do?currencyId=${currencyId}&curr=1&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==1}">
												<font color="red">充值</font>
											</c:when>
											<c:otherwise>
												充值
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerRecharge/loadAdmin.do?currencyId=${currencyId}&curr=2&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==2}">
												<font color="red">提现</font>
											</c:when>
											<c:otherwise>
												提现
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerRecharge/loadAdmin.do?currencyId=${currencyId}&curr=3&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==3}">
												<font color="red">奖励</font>
											</c:when>
											<c:otherwise>
												奖励
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerRecharge/loadAdmin.do?currencyId=${currencyId}&curr=4&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==4}">
												<font color="red">扣除奖励</font>
											</c:when>
											<c:otherwise>
												扣除奖励
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
								<div class="clear"></div>
							<div>
								<form action="<%=actionPath%>managerRecharge/loadAdmin.do" method="get">
									用户ID
									<input name="uid" type="text" value="${uid}" />
									用户邮箱
									<input name="uemail" type="text" value="${uemail}" />
									<input type="hidden" name="currencyId" value="${currencyId}" />
									<input type="hidden" name="curr" value="${currpage}" />
									<button type="submit" class="button">查 询</button>
								</form>
								<br>
								<form action="<%=actionPath%>managerRecharge/loadAdmin.do" method="get">
									地址查询
									<input name="uaddr" type="text" value="${uaddr}" />
									<input type="hidden" name="currencyId" value="${currencyId}" />
									<input type="hidden" name="curr" value="${currpage}" />
									<button type="submit" class="button">查 询</button>
								</form>
							</div>
						</div>
						<table>
							<tr>
								<td>
									<div class="notification information png_bg">
										<br>
										<div>
											<form action="<%=actionPath%>managerRecharge/adminCZ.do" method="post" onsubmit="return checkFlag(1);">
												<input type="hidden" value="${currencyId}" name="currencyid">
												<input type="hidden" name="curr" value="${currpage}" />
												<font style="margin-right: 30px">充值类型</font>
												<select name="type" id="type1" style="width: 173px;">
													<option value="1" selected="selected">充值</option>
													<option value="5">奖励</option>
												</select>
												<br />
												<font style="margin-right: 4px">充值用户邮箱</font>
												<input name="uemail" id="uemail" type="text" value="" />
												<br />
												<font style="margin-right: 30px">充值数量</font>
												<input type="text" name="amt" id="amt" value="" autocomplete="off" />
												<input name="amt" id="b" type="text" value="" style="display:none;"/>
												<br />
												<font style="margin-right: 30px">安全密码</font>
												<input name="surepwd" type="password" id="p" value="" style="display:none;"/>
												<input name="surepwd" type="password" value="" autocomplete="off"/>
												<br />
												<font style="margin-right: 55px">理由</font>
												<input name="remark" type="text" value="" />
												<br />
												<button type="submit" class="button">
													充 值
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
											<form action="<%=actionPath%>managerRecharge/adminCZ.do" method="post" onsubmit="return checkFlag(2);">
												<input type="hidden" value="${currencyId}" name="currencyid">
												<input type="hidden" name="curr" value="${currpage}" />
												<font style="margin-right: 30px">提现类型</font>
												<select name="type" id="type2" style="width: 173px;">
													<option value="2" selected="selected">提现</option>
													<option value="6">扣除奖励</option>
												</select>
												<br />
												<font style="margin-right: 4px">提现用户邮箱</font>
												<input name="uemail" id="uemail2" type="text" value="" />
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
												<input name="remark" type="text" value="" />
												<br />
												<button type="submit" class="button">
													提 现
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
										方向
									</th>
									<th>
										操作类型
									</th>
									<th>
										数量
									</th>
									<th>
										手续费
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
											<c:out value="${l.f_user_id}"></c:out>
											<br />
											<c:out value="${l.f_user_email}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.operation==1}">
													<font color="green">转入</font>
												</c:when>
												<c:when test="${l.operation==2}">
													<font color="red">转出</font>
												</c:when>
												<c:otherwise>
												未知
											</c:otherwise>
											</c:choose>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.operation==1 && l.receice_card_type==2 }">
													<font color="green">充值</font>
												</c:when>
												<c:when test="${l.operation==2 && l.receice_card_type==2 }">
													<font color="red">提现</font>
												</c:when>
												<c:when test="${l.operation==1 && l.receice_card_type==3 }">
													<font color="green">奖励</font>
												</c:when>
												<c:when test="${l.operation==2 && l.receice_card_type==3 }">
													<font color="red">扣除奖励</font>
												</c:when>
												<c:otherwise>
													未知
												</c:otherwise>
											</c:choose>
										</TD>
										<TD>
											<c:out value="${l.amount}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.fee}"></c:out>
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
											</c:choose>
										</TD>
										<TD>
											<input type="text" name="remark" value="${l.remark}" />
										</TD>
										<TD>
										</TD>
									</tr>
								</c:forEach>
							</tbody>

							<tfoot>
								<tr>
									<td colspan="9">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="managerRecharge/loadAdmin.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="uid" value="${uid}"/>
												<pg:param name="uemail" value="${uemail}"/>
												<pg:param name="curr" value="${currpage}"/>
												<pg:param name="uaddr" value="${uaddr}"/>
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
