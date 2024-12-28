<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<!DOCTYPE HTML>
<html>
<head>
	<base href="<%=actionPath%>">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>积分规则</title>
	<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
	<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	<script type="text/javascript">
		function postsubmit(){
			return true;
		}
		
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
		
		function deleteLevel(type){
			/* var uid = document.getElementById("userid").value;
			var ucredit = document.getElementById("userCredit").value;
			if(uid == "" || ucredit == "")
				return false; */
			var qr = "确定要删除吗？";
			
			if (confirm(qr)) {
				window.location.href = "<%=actionPath%>managerUserLevelRule/deleteLevel.do?id=" + type;
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
						积分规则
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<form action="<%=actionPath%>managerUserLevelRule/saveLevel.do" method="post" onsubmit="return postsubmit();">
									<em>VIP等级：</em>
									<input type="text" name="level_name" id="level_name" class="text-input " value="">
									<em>条件：</em>
									<input type="text" name="level_credits" id="level_credits" class="text-input " value="">
									<em>吃单：</em>
									<input type="text" name="eat_commission_rate" id="eat_commission_rate" class="text-input " value="">
									<em>挂单：</em>
									<input type="text" name="pending_commission_rate" id="pending_commission_rate" class="text-input " value="">
									<em>提现：</em>
									<input type="text" name="withdraws_cash" id="withdraws_cash" class="text-input " value="">
									<em>经纪返佣比例：</em>
									<input type="text" name="broker_commission_rate" id="broker_commission_rate" class="text-input " value="">
									<em>合约交易佣金比例：</em>
									<input type="text" name="transaction_commission_rate" id="transaction_commission_rate" class="text-input " value="">
									<em>购买VIP金额(Btcside )：</em>
									<input type="text" name="level_buy_amount" id="level_buy_amount" class="text-input " value="">
									<em>是否隐藏(1：隐藏，0:使用)：</em>
									<input type="text" name="state" id="state" class="text-input " value="0">
									<button type="submit" class="button">
										新 增
									</button>
								</form>
							</div>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										等级ID
									</th>
									<th>
										VIP等级
									</th>
									<th>
										积分条件
									</th>
									<th>
										是否隐藏
									</th>
									<th>
										吃单
									</th>
									<th>
										挂单
									</th>
									<th>
										提现
									</th>
									<th>
										经纪返佣比例
									</th>
									<th>
										合约交易佣金比例
									</th>
									<th>
										购买VIP金额(Btcside )
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
											<c:out value="${l.level_id }"></c:out>
										</td>
										<td>
											<c:out value="${l.level_name }"></c:out>
										</td>
										<td>
											${l.level_credits }
										</td>
										<td>
											<c:choose>
												<c:when test="${l.state == 1 }">隐藏</c:when>
												<c:when test="${l.state == 0 }">使用</c:when>
												<c:otherwise>其他</c:otherwise>
											</c:choose>
										</td>
										<td>
											${l.eat_commission_rate }
										</td>
										<td>
											${l.pending_commission_rate }
										</td>
										<td>
											${l.withdraws_cash }
										</td>
										<td>
											${l.broker_commission_rate }		
										</td>
										<td>
											${l.transaction_commission_rate }		
										</td>
										<td>
											${l.level_buy_amount.stripTrailingZeros().toPlainString() }		
										</td>
										<td>
											<a href="<%=actionPath%>managerUserLevelRule/toUpdateLevel.do?id=${l.level_id}">编辑</a>
											&nbsp;<a href="javascript:void(0);" onclick="deleteLevel('${l.level_id}')" class="red">删除</a>
										</td>
									</tr>
								</c:forEach>
								
							</tbody>
						</table>
							<tfoot>
								<tr style="background-color: rgb(248, 251, 252);">
									<td colspan="11">
										<div class="pagination">
											<!--********** 分页开始 **********-->
<%--													<a href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=0" mce_href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=0">首页</a>--%>
<%--													<font color="red">1</font>--%>
<%--													<a href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=15" mce_href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=15">2</a>--%>
<%--													<a href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=30" mce_href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=30">3</a>--%>
<%--													<a href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=45" mce_href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=45">4</a>--%>
<%--													<a href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=15" mce_href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=15">后页</a>--%>
<%--													<a href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=45" mce_href="managerTrans/loadManagerTrans.do?bqCode=&amp;state=1&amp;pager.offset=45">尾页</a>--%>
											<pg:pager url="managerUserLevelRule/loadLevels.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
													  maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">

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

						<script language="javascript" type="text/javascript">
<%--						//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");--%>
							senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						</script>
					</div>
				</div>
			</div>
		</div>

</body>
</html>