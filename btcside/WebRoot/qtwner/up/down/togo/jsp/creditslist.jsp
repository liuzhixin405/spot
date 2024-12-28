<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>积分信息</title>
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
		function checkFlag(){
			var uid = document.getElementById("userid").value;
			var ucredit = document.getElementById("userCredit").value;
			if(uid == "" || ucredit == "")
				return false;
			var qr = "确定要给" + uid + "增加" + ucredit + "个积分么？";
			
			if (confirm(qr)) {
				return true;
			}
			return false;
		}
		function checkSubFlag(){
			var uid = document.getElementById("uid").value;
			var ucredit = document.getElementById("uCredit").value;
			if(uid == "" || ucredit == "")
				return false;
			var qr = "确定要给" + uid + "扣除" + ucredit + "个积分么？";
			
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
						积分管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerCredit/loadRecords.do" method="post">
									用户ID
									<input name="uid" type="text" value="${uid}" />
									用户邮箱
									<input name="uemail" type="text" value="${uemail}" />
									<button type="submit" class="button">提 交</button>
								</form>
							</div>
							<div>
								<form action="<%=actionPath%>managerCredit/addRecords.do" method="post" onsubmit="return checkFlag();">
									<input name="direction" type="hidden" value="1"/>
									用户ID
									<input name="uid" id="userid" type="text" value="${uid }" />
									积分
									<input name="credit" id="userCredit" type="text" value="${direction eq '1' ? credit : '' }" />
									<button type="submit" class="button">增 加</button>
								</form>
							</div>
							<div>
								<form action="<%=actionPath%>managerCredit/addRecords.do" method="post" onsubmit="return checkSubFlag();">
									<input name="direction" type="hidden" value="-1"/>
									用户ID
									<input name="uid" id="uid" type="text" value="${uid }" />
									积分
									<input name="credit" id="uCredit" type="text" value="${direction eq '-1' ? credit : '' }" />
									<button type="submit" class="button">扣除</button>
								</form>
							</div>
							<div>
								用户当前积分: ${currCredit }
							</div>
						</div>
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
										获得积分数量
									</th>
									<th>
										时间
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
											<c:out value="${l.f_user_id}"></c:out><br/>
											<c:out value="${l.title}"></c:out>
										</TD>
										<TD>
											<c:if test="${l.credit_type_id == 11 }">
												<c:out value="${-l.credits}" />
											</c:if>
											<c:if test="${l.credit_type_id != 11 }">
												<c:out value="${l.credits}" />
											</c:if>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.dateline}"></ut:formatDate>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
						
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerCredit/loadRecords.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="uid" value="${uid}"/>
											<pg:param name="uemail" value="${uemail}"/>
											
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
									<!-- End .pagination -->
									<div class="clear"></div>
								</td>
							</tr>
						</tfoot>
						</table>
						<script language="javascript" type="text/javascript">
<!--
//senfe("表格名称","奇数行背景","偶数行背景","鼠标经过背景","点击后背景");
senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
--></script>


					</div>

				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>
