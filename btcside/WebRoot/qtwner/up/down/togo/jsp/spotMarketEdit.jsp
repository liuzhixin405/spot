<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>交易区编辑</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript">
			function postsubmit(){
				return true;
			}
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						交易区编辑(币币交易)
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerSpotMarket/updateManagerSpotMarket.do" method="post" onsubmit="return postsubmit();">
									<div>
										<input type="hidden" name="id" id="id" class="text-input " value="${market.id}"></input>
										<div class="text-label">交易区名称:</div>
										<input type="text" name="marketName" id="marketName" class="text-input " value="${market.market_name }" required="required"></input>
									</div>
									<div>
										<div class="text-label">交易币种名称：</div>
										<select id="coinId" name="coinId" >
											<c:forEach var="item" varStatus="stat" items="${coins }">
												<c:if test="${item.type_name == market.coin_name}">
													<option value="${item.type_id}" selected="selected">${item.type_name }</option>
												</c:if>
												<c:if test="${item.type_name != market.coin_name}">
													<option value="${item.type_id}">${item.type_name}</option>
												</c:if>
											</c:forEach>
										</select>
									</div>
									<div>
										<div class="text-label">是否使用：</div>
										<select name="isUsing" id="isUsing">
											<c:if test="${market.is_using}">
												<option value="1" selected="selected">使用中</option>
												<option value="0">不使用</option>
											</c:if>
											<c:if test="${!market.is_using}">
												<option value="1">使用中</option>
												<option value="0" selected="selected">不使用</option>
											</c:if>
										</select>
									</div>
									<div>
										<div class="text-label">指数地址：</div>
										<input type="text" class="text-input " name="indexAddress" id="indexAddress" value="${market.index_address}" />
									</div>
									<div>
										<div class="text-label">安全密码：</div>
										<input class="text-input " name="password" type="password" value="" />
									</div>
									<div>
										<div class="text-label"></div>
										<button type="submit" class="button">
											提交
										</button>
									</div>
									<div class="clear"></div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
