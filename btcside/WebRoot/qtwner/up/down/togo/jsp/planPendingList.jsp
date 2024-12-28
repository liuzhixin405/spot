<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>计划委托挂单</title>
		<!-- Reset Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath %>css/reset.css" type="text/css" media="screen" />
		<!-- Main Stylesheet -->
		<link rel="stylesheet" href="<%=jspPath %>css/style.css" type="text/css" media="screen" />
		<!-- Invalid Stylesheet. This makes stuff look pretty. Remove it if you want the CSS completely valid -->
		<link rel="stylesheet" href="<%=jspPath %>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript">
		   function sure(qr,url)
		   {
			   if(qr!=null)
			   {
				   if(confirm(qr))
				   {
			            document.location.href = url;
				   }
			   }
			   else{
				   document.location.href = url;
				   }
				   
		   }
		   function postform(url) {
			   document.select.action=url;
			   document.select.submit();
		   }
		</script>

		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						计划委托挂单
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<!-- end Notifications -->
						<div class="notification success png_bg">
							<br>
							<div>
							
								<form name="select" action="#" method="post">
									选择合约: 
									<c:forEach items="${trans}" var="l">
										<a href="<%=actionPath %>managerPending/${url}.do?tcode=${l.transaction_code}" >
										<c:choose>
											<c:when test="${currtran==l.transaction_code}">
												<font color="#ff0000">${l.transaction_code}</font>
											</c:when>
											<c:otherwise>
												${l.transaction_code}
											</c:otherwise>
										</c:choose>
										</a> &nbsp;
									</c:forEach>
									
									或者可以输入合约号: 
									<input type="text" name="tcode" class="text-input small-input" value="${currtran}"></input>

									<button type="button" class="button" onclick="postform('<%=actionPath%>managerPending/${url}.do')" >查 询</button>
									<br /><br />
									选择操作方向
									<select name="direction" id="direction" >
										<c:if test="${direction eq null or direction eq '' }">
											<option value="" selected="selected">请选择</option>
											<option value="1" >买多</option>
											<option value="2" >卖空</option>
										</c:if>
										<c:if test="${direction eq '1' }">
											<option value="" >请选择</option>
											<option value="1" selected="selected">买多</option>
											<option value="2" >卖空</option>
										</c:if>
										<c:if test="${direction eq '2' }">
											<option value="" >请选择</option>
											<option value="2" selected="selected">卖空</option>
											<option value="1" >买多</option>
										</c:if>
									</select>
									&emsp;
									选择状态
									<select name="status" id="status" >
										<c:if test="${status eq null or status eq ''}">
											<option value="" selected="selected">请选择</option>
											<option value="0" >未生效</option>
											<option value="1" >已生效</option>
											<option value="2" >失败</option>
											<option value="3" >已撤销</option>
										</c:if>
										<c:if test="${status eq '0' }">
											<option value="" >请选择</option>
											<option value="0" selected="selected">未生效</option>
											<option value="1" >已生效</option>
											<option value="2" >失败</option>
											<option value="3" >已撤销</option>
										</c:if>
										<c:if test="${status eq '1' }">
											<option value="" >请选择</option>
											<option value="0" >未生效</option>
											<option value="1" selected="selected">已生效</option>
											<option value="2" >失败</option>
											<option value="3" >已撤销</option>
										</c:if>
										<c:if test="${status eq '2' }">
											<option value="" >请选择</option>
											<option value="0" >未生效</option>
											<option value="1" >已生效</option>
											<option value="2" selected="selected">失败</option>
											<option value="3" >已撤销</option>
										</c:if>
										<c:if test="${status eq '3' }">
											<option value="" >请选择</option>
											<option value="0" >未生效</option>
											<option value="1" >已生效</option>
											<option value="2" >失败</option>
											<option value="3" selected="selected">已撤销</option>
										</c:if>
									</select>
									&emsp;
									选择触发类型
									<select name="type" id="type" >
										<c:if test="${type eq null or type eq ''}">
											<option value="" selected="selected">请选择</option>
											<option value="1" >现价大于触发价</option>
											<option value="2" >现价小于触发价</option>
										</c:if>
										<c:if test="${type eq '1' }">
											<option value="" >请选择</option>
											<option value="1" selected="selected">现价大于触发价</option>
											<option value="2" >现价小于触发价</option>
										</c:if>
										<c:if test="${type eq '2' }">
											<option value="" >请选择</option>
											<option value="2" selected="selected">现价小于触发价</option>
											<option value="1" >现价大于触发价</option>
										</c:if>
									</select>
									<button type="button" class="button" onclick="postform('<%=actionPath %>managerPending/${url}.do')" >查 询</button>
									<br /><br />
									用户ID
									<input type="text" name="userid" class="text-input small-input" value="${userid}"></input>
									用户邮箱
									<input type="text" name="useremail" class="text-input small-input" value="${useremail}"></input>
									
									<button type="button" class="button" onclick="postform('<%=actionPath %>managerPending/${url}.do')" >查 询</button>
								</form>
							</div>
							<br>
						</div>
						<table id="changecolor">
							<thead>
								<tr>
									<th>
										序号
									</th>
									<th>
										合约号
									</th>
									<th>
										用户
									</th>
									<th>
										触发价格
									</th>
									<th>
										委托价格
									</th>
									<th>
										委托数量
									</th>
									<th>
										杠杆
									</th>
									<th>
										操作方向
									</th>
									<th>
										类型
									</th>
									<th>
										状态
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
											<c:out value="${l.f_code}"></c:out>
										</TD>
										<TD>
											<c:set var="u_id" value="${l.f_user_id}"></c:set>
											<c:if test="${!fn:contains(white, u_id)}">
											<font color="red">
												</c:if>
												<c:out value="${l.f_user_id}"></c:out><br/>
												<c:if test="${!fn:contains(white, u_id)}">
											</font>
											</c:if>
										</TD>
										<TD>
											<c:out value="${l.tri_price}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.delegation_price}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.delegation_count}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.multiple}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.direction==1}"><font color="green">买多</font></c:when>
												<c:when test="${l.direction==2}"><font color="red">卖空</font></c:when>
											</c:choose>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.type==1}">现价大于触发</c:when>
												<c:when test="${l.type==2}">现价低于触发</c:when>
											</c:choose>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.status==0}">未生效</c:when>
												<c:when test="${l.status==1}">已生效</c:when>
												<c:when test="${l.status==2}">失败</c:when>
												<c:when test="${l.status==3}">已撤销</c:when>
											</c:choose>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.delegation_time}"></ut:formatDate>
										</TD>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerPending/${url}.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="userid" value="${userid}"/>
											<pg:param name="tcode" value="${currtran}"/>
											<pg:param name="useremail" value="${useremail}"/>
											<pg:param name="direction" value="${direction}"/>
											<pg:param name="status" value="${status}"/>
											<pg:param name="type" value="${type}"/>
											
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
						<script language="javascript">
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
