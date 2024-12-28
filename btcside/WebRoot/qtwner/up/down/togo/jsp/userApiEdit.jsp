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
		<title>用户交易API编辑</title>
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
						用户交易API编辑
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1" style="line-height: 24px;">
						<form class="notification depth-form"  action="<%=actionPath%>managerUserApi/update.do" method="post" onsubmit="return postsubmit();">
							<input type="hidden" name="over_time" id="over_time" value="${bqUserAppKey.over_time}"></input>
							<input type="hidden" name="update_time" id="update_time" value="${bqUserAppKey.update_time}"></input>
							<label>用户id:</label>
							<input type="text" name="f_user_id" id="f_user_id" readonly="readonly" value="${bqUserAppKey.f_user_id}" required="required"></input>
							<div class="clear"></div>
							<label>公钥:</label>
							<input type="text" name="app_key" id="app_key" readonly="readonly" value="${bqUserAppKey.app_key}" required="required"></input>
							<div class="clear"></div>
							<label>私钥:</label>
							<input type="text" name="app_secret" id="app_secret" readonly="readonly" value="${bqUserAppKey.app_secret}" required="required"></input>
							<div class="clear"></div>
							<label>IP地址:</label>
							<input type="text" name="allow_ip" id="allow_ip" readonly="readonly" value="${bqUserAppKey.allow_ip}" required="required"></input>
							<div class="clear"></div>
							<label>状态：</label>
							<select name="state" id="state" >
								<c:choose>
									<c:when test="${bqUserAppKey.state eq 1}">
										<option value="1" selected='selected'>使用中</option>	
									</c:when>
									<c:otherwise>
										<option value="1">使用中</option>	
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${bqUserAppKey.state eq 0}">
										<option value="0" selected='selected'>禁用</option>	
									</c:when>
									<c:otherwise>
										<option value="0">禁用</option>	
									</c:otherwise>
								</c:choose>
							</select>
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
