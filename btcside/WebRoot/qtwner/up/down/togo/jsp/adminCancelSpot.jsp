<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>管理员撤单</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<style>
			table {
				width: 100%;
				border: 1px solid #CCC;
				border-spacing: 0;
				border-collapse: collapse;
			}
			
			th,td {
				border: 1px solid #CCC;
				text-align: center;
			}
		</style>
		
		<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script>
		<script type="text/javascript">
			function sure(qr, url) {
				if (qr != null) {
					if (confirm(qr)) {
						document.cancelform.action = url;
					}
				} else {
					document.cancelform.action = url;
				}
				document.cancelform.submit();
			}
		</script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						管理员后台撤单
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								<form id="queryform" method="post" action="<%=actionPath%>adminCancelSpot/query.do">
									用户ID：
									<input type="text" name="userId" id="userId" class="text-input " value="${userId }"></input>
									邮箱
									<input type="text" name="email" id="email" class="text-input " value="${email }"></input>
									交易对:
									<input type="text" name="code" id="code" class="text-input " value="${trancode}"></input>
									挂单方向：
									<c:if test="${direct == null || direct == 1}">
										<input type="radio" name="direction" value="1" checked="checked" />买入
										<input type="radio" name="direction" value="2" />	卖出
									</c:if>
									<c:if test="${direct == 2}">
										<input type="radio" name="direction" value="1" />买入
										<input type="radio" name="direction" value="2" checked="checked" />卖出	
									</c:if>
									<button type="submit" class="button">
										查 询
									</button>
								</form>
							</div>
						</div>
					</div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								撤销币币全部
								<form name="cancelform" action="" method="post">
									用户ID：
									<input type="text" name="userId" id="userId" class="text-input " value="${userId }"></input>
									邮箱
									<input type="text" name="email" id="email" class="text-input " value="${email }"></input>
									交易对:
									<input type="text" name="code" id="code" class="text-input " value="${trancode}"></input>
									<button type="button" class="button" onclick="sure('您确定要对${userId }执行一键撤买么？', '<%=actionPath%>adminCancelSpot/cancleDeal.do?direction=1')">
										一键撤买
									</button>
									<button type="button" class="button" onclick="sure('您确定要对${userId }执行一键撤卖么？', '<%=actionPath%>adminCancelSpot/cancleDeal.do?direction=2')">
										一键撤卖
									</button>
									<button type="button" class="button" onclick="sure('您确定要对${userId }执行全部撤单么？', '<%=actionPath%>adminCancelSpot/cancleDeal.do?direction=0');">
										全部撤单
									</button>
								</form>
							</div>
						</div>
					</div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<div>
								撤销一个
								<form id="cancelone" action="<%=actionPath%>adminCancelSpot/canclePendingOne.do" method="post">
									用户ID：
									<input type="text" name="userId" id="userId" class="text-input " value="${userId }"></input>
									邮箱
									<input type="text" name="email" id="email" class="text-input " value="${email }"></input>
									交易对:
									<input type="text" name="code" class="text-input" value="${trancode}"></input>
									挂单号：
									<input type="text" name="pdId" id="pdId" class="text-input"></input>
									<button type="submit" class="button">
										撤单
									</button>
								</form>
							</div>
						</div>
					</div>
				</div>

				<!-- 用户的挂单 -->
				<div>
					<table cellpadding="0" cellspacing="0" border="0">
						<thead>
							<tr>
								<th>
									挂单ID
								</th>
								<th>
									交易对
								</th>
								<th>
									委托时间
								</th>
								<th>
									挂单类型
								</th>
								<th>
									委托价格
								</th>
								<th>
									委托数量
								</th>
								<th>
									已成交
								</th>
								<th>
									未成交
								</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="pd" items="${plist}">
								<tr>
									<td>
										${pd.delegation_id }
									</td>
									<td>
										${fn:toUpperCase(pd.coin_pair_name)}
									</td>
									<td>
										<ut:formatDate dateline="${pd.delegation_time}"></ut:formatDate>
									</td>
									<td>
										<c:choose>
											<c:when test="${pd.direction eq 1}">
												<b class="buy">买单</b>
											</c:when>
											<c:when test="${pd.direction eq 2}">
												<b class="sell">卖单</b>
											</c:when>
											<c:otherwise>class="multiple" </c:otherwise>
										</c:choose>
									</td>
									<td>
										${pd.delegation_price}
									</td>
									<td>
										<fmt:formatNumber value="${pd.delegation_count}" pattern="#0.00" />USDT
									</td>
									<td>
										<fmt:formatNumber value="${pd.deal_count}" pattern="#0.00" />USDT
									</td>
									<td>
										<fmt:formatNumber value="${pd.undeal_count}" pattern="#0.00" />USDT
									</td>
								</tr>
							</c:forEach>

						</tbody>
					</table>
				</div>
			</div>
		</div>
	</body>
</html>
