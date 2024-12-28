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
		<title>用户证书编辑</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
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
						用户证书编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1" style="line-height: 24px;">
						<form class="notification depth-form"  action="<%=actionPath%>managerMemberCertificate/update.do" method="post" onsubmit="return postsubmit();">
							<input type="hidden" name="id" id="id" value="${memberCertificate.id}"></input>
							<label>用户id:</label>
							<input type="text" name="user_id" id="user_id"  value="${memberCertificate.user_id}" required="required"></input>
							<div class="clear"></div>
							
							<label>荣誉称号：</label>
							<select id="certificate_id" name="certificate_id" >
								<c:forEach var="item" varStatus="stat" items="${types }">
									<c:if test="${item.id == memberCertificate.certificate_id}">
										<option value="${item.id}" selected="selected">${item.certificate_name }</option>
									</c:if>
									<c:if test="${item.id != memberCertificate.certificate_id}">
										<option value="${item.id}">${item.certificate_name}</option>
									</c:if>
								</c:forEach>
							</select>
							<div class="clear"></div>
							
							<label>比赛名称(第几季)：</label>
							<input type="text" name="activity_name" id="activity_name" value="${memberCertificate.activity_name}"></input>
							eg:Btcside模拟交易排名赛第一季
							<div class="clear"></div>
							
							<button type="submit" class="button">
								提交
							</button>
							<div class="clear"></div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
