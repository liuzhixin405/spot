<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>

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
		</script>
		
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						充值地址管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
								<a href="<%=actionPath%>managerWallet/loadRecords.do?curr=1">
									<c:choose>
										<c:when test="${curr==1}">
											<font color="red">已使用地址</font>
										</c:when>
										<c:otherwise>
											已使用地址
										</c:otherwise>
									</c:choose></a>&nbsp;&nbsp;&nbsp;
								<a href="<%=actionPath%>managerWallet/loadRecords.do?curr=2">
									<c:choose>
										<c:when test="${curr==2}">
											<font color="red">未使用地址</font>
										</c:when>
										<c:otherwise>
											未使用地址
										</c:otherwise>
									</c:choose>
								</a>

							</div>
							<br>
						</div>
						<c:if test="${curr==1}">
							<div class="notification success png_bg">
								<br>
								<div>
									<form enctype="multipart/form-data" action="<%=actionPath%>managerWallet/selectRecords.do" method="post">
										<input type="hidden" name="curr" value="${curr}" />
										用户ID<input name="uid" type="text"/>
										用户邮箱<input name="uemail" type="text" value="${uemail}"/>
										地址<input name="address" type="text" value="${address}"/>
										<button type="submit" class="button">查询</button>
									</form>
								</div>
								<br>
							</div>
						</c:if>
						<c:if test="${curr==2}">
							<div class="notification information png_bg">
								<br>
								<div>
									<form enctype="multipart/form-data" action="<%=actionPath%>managerWallet/addRecord.do" method="post">
										<input type="hidden" name="curr" value="${curr}" />
										<font style="margin-right: 4px">上传地址(使用txt或者cvs格式)</font><input name="file" type="file"/><br/>
										<button type="submit" class="button">导 入</button>
									</form>
								</div>
								<br>
							</div>
						</c:if>
								<%--<table>
								<tr><td>
							</td><td>
							<div class="notification error png_bg">
								<br>
								<div>
									<form action="<%=actionPath%>managerWallet/adminCZ.do" method="post">
										<input type="hidden" value="2" name="type">
										<input type="hidden" name="curr" value="${curr}" />
										<font style="margin-right: 4px">提现用户邮箱</font><input name="uemail" type="text" value="" /><br/>
										<font style="margin-right: 30px">提现数量</font><input name="amount" type="text" value=""/><br/>
										<font style="margin-right: 30px">安全密码</font><input name="surepwd" type="password" value=""/><br/>
										<font style="margin-right: 55px">理由</font><input name="remark" type="text" value=""/><br/>
										<button type="submit" class="button">提 现</button>
									</form>
								</div>
								<br>
							</div></td>
								</tr>
								</table>
						--%><table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										地址
									</th>
									<c:if test="${curr==1}">
									<th>
										用户ID
									</th>
									</c:if>				
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${pm.datas}" var="l" varStatus="status">
									<tr>
										<TD>
											<c:out value="${status.index+1}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.wallet_address}"></c:out>
										</TD>
										<c:if test="${curr==1}">
										<TD>
											<c:out value="${l.f_user_id}"></c:out>
										</TD>
										</c:if>			
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerWallet/selectRecords.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="curr" value="${curr}"/>
											<pg:param name="uid" value="${uid}"/>
											<pg:param name="uemail" value="${uemail}"/>
											<pg:param name="address" value="${address}"/>
										
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
