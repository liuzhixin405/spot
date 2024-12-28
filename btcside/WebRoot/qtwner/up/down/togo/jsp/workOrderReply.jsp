<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<input type="hidden" id="replyCount" value="${fn:length(details)}" />
<jsp:useBean id="createTime" class="java.util.Date" type="java.util.Date"></jsp:useBean>
<c:forEach var="detail" varStatus="status" items="${details}">
	<c:if test="${detail.f_direction  == 'USER'}">
		<div class="message-box message-left">
			<div class="clomun-tainer">
				<p class="clomun-title">用户</p>
				<div class="clomun-messages">
					<div class="message-content">
						${detail.f_reply_content}
					</div>
					<c:if test="${not empty detail.f_file}">
						<div class="message-content">
							<a href="${detail.f_file}" style="display: block;" target="_blank" >
								<img alt="" src="${detail.f_file}" style="max-width: 500px;">
							</a>
						</div>
					</c:if>
					<div class="clear"></div>
					<div class="time">
						<ut:formatDate dateline="${detail.f_create_time }"></ut:formatDate>
					</div>
				</div>
			</div>
			<img src="">	
		</div>	
	</c:if>
	
	<c:if test="${detail.f_direction == 'SYSTEM'}">
		<div class="message-box message-right">
			<div class="clomun-tainer">
				<p class="clomun-right-title">
					<c:if test="${not empty detail.f_user_name}">${detail.f_user_name }</c:if>
					<c:if test="${empty detail.f_user_name}">我</c:if>
				</p>
				<div class="clomun-messages clomun-right-messages">
					<div class="message-content">
						${detail.f_reply_content}
					</div>
					<c:if test="${not empty detail.f_file}">
						<div class="message-content">
							<a href="${detail.f_file}" style="display: block;" target="_blank" >
								<img alt="" src="${detail.f_file}" style="max-width: 500px;">
							</a>
						</div>
					</c:if>
					<div class="clear"></div>
					<div class="time">
						<ut:formatDate dateline="${detail.f_create_time }"></ut:formatDate>
					</div>
				</div>
			</div>
		</div>		
	</c:if>
	
	
	<div class="clear"></div>
</c:forEach>
<div class="clear"></div>
<c:if test="${param.scroll}">
	<script type="text/javascript">
		var lastTime = "${details[details.size()- 1].f_create_time}";
		lastTime = Number(lastTime);
		if (lastTime != lastDetailTime) {
			isScorll = false;
			var replyEl = document.getElementById("message-reply");
			var count = document.getElementById("replyCount").value;
			$(document).ready(function(){
				if (null != replyEl && "undefined" != typeof(replyEl)) {
					if (0 == count || "" == count) {
						replyEl.style.display = "none";
					} else {
						replyEl.style.display = "";
					}
					replyEl.scrollTop = replyEl.scrollHeight;
				}
			});
		}
		lastDetailTime = lastTime;
	</script>
</c:if>

