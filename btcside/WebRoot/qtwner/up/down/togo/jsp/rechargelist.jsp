<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg"  uri="http://jsptags.com/tags/navigation/pager" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
				<div class="content-box-header">
					<h3>
						用户充值提现
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="">
							<div style="line-height: 24px;">
								<h5 style="float:left; margin-right: 10px;">选择币种:</h5>
								<c:forEach items="${currencyTypes}" var="l" varStatus="status">
									<a href="<%=actionPath%>managerRecharge/loadRecords.do?currencyId=${l.type_id}&curr=${currpage}&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<font color="${currencyId == l.type_id ? '#ff0000' : ''}">${l.type_name.toUpperCase() }</font>
									</a> &nbsp;
								</c:forEach>
								<div class="clear"></div>
								
								<h5 style="float:left; margin-right: 10px;">选择方向:</h5>
									<a href="<%=actionPath%>managerRecharge/loadRecords.do?currencyId=${currencyId}&curr=1&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}" >
										<c:choose>
											<c:when test="${currpage==1 || currpage==0}">
												<font color="red">所有</font>
											</c:when>
											<c:otherwise>
												所有
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerRecharge/loadRecords.do?currencyId=${currencyId}&curr=2&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==2}">
												<font color="red">充值</font>
											</c:when>
											<c:otherwise>
												充值
											</c:otherwise>
										</c:choose></a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerRecharge/loadRecords.do?currencyId=${currencyId}&curr=3&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==3}">
												<font color="red">提现</font>
											</c:when>
											<c:otherwise>
												提现
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerRecharge/loadRecords.do?currencyId=${currencyId}&curr=4&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==4}">
												<font color="red">OTC转入</font>
											</c:when>
											<c:otherwise>
												OTC转入
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
									<a href="<%=actionPath%>managerRecharge/loadRecords.do?currencyId=${currencyId}&curr=5&uemail=${uemail}&uaddr=${uaddr}&uid=${uid}">
										<c:choose>
											<c:when test="${currpage==5}">
												<font color="red">转出OTC</font>
											</c:when>
											<c:otherwise>
												转出OTC
											</c:otherwise>
										</c:choose>
									</a>&nbsp;&nbsp;&nbsp;
								<div class="clear"></div>
							</div>
						</div>
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>managerRecharge/loadRecords.do" method="get">
									用户ID
									<input name="uid" type="text" value="${uid}" />
									用户邮箱
									<input name="uemail" type="text" value="${uemail}" />
									<input type="hidden" name="currencyId" value="${currencyId}" />
									<input type="hidden" name="curr" value="${currpage}" />
									<button type="submit" class="button">查 询</button>
								</form>
								<br>
								<form action="<%=actionPath%>managerRecharge/loadRecords.do" method="get">
									地址查询
									<input name="uaddr" type="text" value="${uaddr}" />
									<input type="hidden" name="currencyId" value="${currencyId}" />
									<input type="hidden" name="curr" value="${currpage}" />
									<button type="submit" class="button">查 询</button>
								</form>
								<br>
								<form action="<%=actionPath%>managerRecharge/loadRecords.do" method="get" name="tiaojian">
									状态
									<select name="state" id="state" onchange="javascript:document.tiaojian.submit();">
										<c:choose>
											<c:when test="${state eq '0' || state eq null }">
												<option value="0" selected='selected'>未受理</option>	
												<option value="1">受理成功</option>
												<option value="2">拒绝</option>
												<option value="3">受理中</option>
												<option value="4">转人工</option>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${state eq '1' }">
												<option value="0">未受理</option>	
												<option value="1" selected='selected'>受理成功</option>
												<option value="2">拒绝</option>
												<option value="3">受理中</option>
												<option value="4">转人工</option>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${state eq '2' }">
												<option value="0">未受理</option>	
												<option value="1">受理成功</option>
												<option value="2" selected='selected'>拒绝</option>
												<option value="3">受理中</option>
												<option value="4">转人工</option>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${state eq '3' }">
												<option value="0">未受理</option>	
												<option value="1">受理成功</option>
												<option value="2">拒绝</option>
												<option value="3" selected='selected'>受理中</option>
												<option value="4">转人工</option>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${state eq '4' }">
												<option value="0">未受理</option>	
												<option value="1">受理成功</option>
												<option value="2">拒绝</option>
												<option value="3">受理中</option>
												<option value="4" selected='selected'>转人工</option>
											</c:when>
										</c:choose>
									</select>
									<input type="hidden" name="uaddr" value="${uaddr}" />
									<input type="hidden" name="currencyId" value="${currencyId}" />
									<input type="hidden" name="curr" value="${currpage}" />
									<button type="submit" class="button">查 询</button>
								</form>
							</div>
							<div>
								总量: ${pm.flag }
							</div>
						</div>
						<c:if test="${currpage==2}">
							<div class="notification information png_bg">
								<br>
								<div>
									<form action="<%=actionPath%>managerRecharge/giveCZ.do" method="post">
										<input type="hidden" name="curr" value="${currpage}" />
										<input type="hidden" name="currencyId" value="${currencyId}" />
										充值地址<input name="address" type="text" value="" style="width:260px;" />
										交易ID<input name="cid" type="text" value=""  style="width:320px;"/>
										充值数量<input name="amount" type="text" value="" autocomplete="off" />
										安全密码<input name="password" type="password" value="" autocomplete="new-password" />
										<button type="submit" class="button">充 值</button>
									</form>
								</div>
								<br>
							</div>
						</c:if>
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
										操作类型
									</th>
									<th>
										数量 / 手续费
										<br/>实际到账
									</th>
									<th>
										地址
									</th>
									<th>
										申请时间
									</th>
									<th>
										确认时间
									</th>
									<th>
										状态
									</th>
									<th>
										备注说明
									</th>
									<th>
										操作
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
											<c:out value="${l.f_user_email}"></c:out>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${currpage == 4 || currpage == 5}">
													<c:choose>
													<c:when test="${l.operation==1}">
														<font color="green">从OTC转入</font>
													</c:when>
													<c:when test="${l.operation==2}">
														<font color="red">转出到OTC</font><br/>
														<a href="<%=actionPath%>managerCheckAccount/loadDetail.do?id=${l.f_user_id}">对账</a>
													</c:when>
													<c:otherwise>
														未知
													</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<c:choose>
													<c:when test="${l.operation==1}">
														<font color="green">充值</font>
													</c:when>
													<c:when test="${l.operation==2}">
														<font color="red">提现</font><br/>
														<a href="<%=actionPath%>managerCheckAccount/loadDetail.do?id=${l.f_user_id}">对账</a>
													</c:when>
													<c:when test="${l.operation==3}">
														<font color="green">转入</font>
													</c:when>
													<c:when test="${l.operation==4}">
														<font color="red">转出</font>
														<a href="<%=actionPath%>managerCheckAccount/loadDetail.do?id=${l.f_user_id}">对账</a>
													</c:when>
													<c:otherwise>
														未知
													</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
											<br/>
											
										</TD>
										<TD>
											<c:out value="${l.amount}"></c:out> / <c:out value="${l.fee}"></c:out>
											<br/>
											<font color="blue"><c:out value="${l.amount-l.fee}"></c:out></font>
										</TD>
										<TD>
											<c:out value="${l.receice_card_no}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.dateline}"></ut:formatDate>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.confirm_time}"></ut:formatDate>
										</TD>
										<TD>
											<c:choose>
												<c:when test="${l.state==0}">
													<font color="red">未受理</font>
												</c:when>
												<c:when test="${l.state==1}">受理成功</c:when>
												<c:when test="${l.state==2}">拒绝</c:when>
												<c:when test="${l.state==3}">自动处理中</c:when>
												<c:when test="${l.state==4}"><font color="red">转人工</font></c:when>
											</c:choose>
										</TD>
										<form action="<%=actionPath%>managerRecharge/updateRecord.do" method="post">
											<TD>
												<input type="text" name="remark" value="${l.remark}" />
											</TD>
											<TD>
												<input type="hidden" name="id" value="${l.record_id}" />
												<input type="hidden" name="action" value="2" />
												<input type="hidden" name="type" value="${l.operation}" />
												<input type="hidden" name="curr" value="${currpage}" />
												<input type="hidden" name="currencyId" value="${currencyId}" />
												<c:if test="${l.state==0 || l.state==4}">
													<c:if test="${l.operation==2}">
														<input type="button" onclick="gourl('确定审核通过?','<%=actionPath%>managerRecharge/updateRecord.do?action=5&currencyId=${currencyId}&id=${l.record_id}&type=${l.operation}&curr=${currpage}');" value="审核通过"></input>
													</c:if>
													<input type="button" onclick="gourl('确定提交?','<%=actionPath%>managerRecharge/updateRecord.do?action=1&currencyId=${currencyId}&id=${l.record_id}&type=${l.operation}&curr=${currpage}');" value="确认"></input>
													<input type="submit" value="拒绝" onclick="return postsure('确定要拒绝该请求吗?该操作无法回退');"></input>
												</c:if>
												<c:if test="${l.state==3}">
													自动处理中...
												</c:if>
											</TD>
										</form>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<tfoot>
							<tr>
								<td colspan="9">
									<div class="pagination">
										<!-- ********** 分页开始 **********	-->
										<pg:pager url="managerRecharge/loadRecords.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}" 
										maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
											<pg:param name="uid" value="${uid}"/>
											<pg:param name="uemail" value="${uemail}"/>
											<pg:param name="curr" value="${currpage}"/>
											<pg:param name="uaddr" value="${uaddr}"/>
											<pg:param name="currencyId" value="${currencyId}"/>
											<pg:param name="state" value="${state}"/>
									
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
							senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						</script>
					</div>

				</div>
				<!-- End .content-box-content -->
			</div>
			<!-- End .content-box -->
		</div>
	</body>
</html>
