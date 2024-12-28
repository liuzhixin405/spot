<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.center"/></title>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
<jsp:include page="_top.jsp"></jsp:include>
<jsp:include page="_head.jsp"></jsp:include>
<div class="container center-cont">
  <jsp:include page="_user_left.jsp"></jsp:include>
  <div class="main-rt-box assets-main">
    <div class="main-wrap" style="margin-top: 20px;">
      <div class="block-title">
        <h2><fmt:message key="msg.pg.userCenter.myHonour"/></h2>
      </div>
      <div class="trade-manage-main">
        <table cellpadding="0" cellspacing="0">
          <thead>
            <tr>
              <th><fmt:message key="msg.pg.userCenter.myHonourTitle"/></th>
              <th><fmt:message key="msg.pg.userCenter.mygameName"/></th>
              <th><fmt:message key="msg.pg.userCenter.issueTm"/></th>
              <th><fmt:message key="msg.pg.userCenter.option"/></th>
            </tr>
          </thead>
          <tbody>
          	<c:forEach var="l" items="${pm.datas }" varStatus="">
		        <tr>
		        	<td>${l.certificate_name }</td>
		        	<td>${l.activity_name }</td>
		        	<td><ut:formatDate dateline="${l.issue_time}"></ut:formatDate></td>
		        	<td><a href="<%=basePath%>center/certificateDetail.do?id=${l.id}" class="gray-a">查看证书</a></td>
		        </tr>				
	        </c:forEach>
          </tbody>
        </table>
        <ul class="pagination">
			<pg:pager url="${pageN}" items="${pm.total}"
				maxIndexPages="10" maxPageItems="${pm.maxitems}"
				export="currentPageNumber=pageNumber">							
			
					<pg:prev> <li class="disabled">
						<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>	</li>
					</pg:prev>
			
			
					<pg:pages>
						<c:choose>
							<c:when test="${currentPageNumber eq pageNumber }">
								<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
							</c:when>
							<c:otherwise><li>
								<a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}">${pageNumber}</a></li>
							</c:otherwise>
						</c:choose>
					</pg:pages>
					<pg:next>
					<li><a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.next"/></a></li>
				</pg:next>
			</pg:pager>
		</ul>
      </div>
    </div>

  </div>
</div>

<jsp:include page="_footer.jsp"></jsp:include>
</body>
<script type="text/javascript">
	
</script>
</html>
