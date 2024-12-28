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
		<title>自由经纪人身份证照片查看</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						自由经纪人身份证照片查看
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerBroker/loadAudit.do" method="post">
									<button type="submit" class="button">
										返 回
									</button>
								</form>
							</div>
						</div>
						<table>
							<thead>
								<tr>
									<th>
										经纪人ID
									</th>
									<th>
										手持身份证照片
									</th>
									<th>
										身份证（正面）
									</th>
									<th>
										身份证（反面）
									</th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${applyIdCrad!= null }">
									<tr>
										<td>
											<c:out value="${applyIdCrad.f_user_id }"></c:out>
										</td>
										<td>
											<img alt="手持身份证照片" src="${applyIdCrad.photo_1 }" width="180px" height="180px">
										</td>
										<td>
											<img alt="身份证（正面）" src="${applyIdCrad.photo_2 }" width="180px" height="180px">
										</td>
										<td>
											<img alt="身份证（反面）" src="${applyIdCrad.photo_3 }" width="180px" height="180px">
										</td>
									</tr>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
