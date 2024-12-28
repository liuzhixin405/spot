<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="_basepage.jsp"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<title>质押借币配置</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css"	type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<style type="text/css">
			.clear {
				clear: both;
			}
		</style>
		
		<script type="text/javascript">
			function sure(qr, url) {
				if (qr != null) {
					if (confirm(qr)) {
						document.location.href = url;
					}
				} else {
					document.location.href = url;
				}
		
			}
		</script>
	</head>

	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						质押借币配置
					</h3>

					<div class="clear"></div>
				</div>
				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
					
					<div style="line-height: 24px;">
						<form class="notification depth-form" action="managerLoanSetting/updateLoanSetting.do" method="post">
						
							<input name="id" type="hidden" value="${setting.id}"></input>
							<input name="pledge_symbol" type="hidden" value="${setting.pledge_symbol}"></input>
							<input name="borrow_symbol" type="hidden" value="${setting.borrow_symbol}"></input>
							
							<label for="pledge_coin_id">质押币种：</label>
							<select name="pledge_coin_id" id="pledge_coin_id">
								<c:if test="${setting.pledge_coin_id > 0}">
									<option value="${setting.pledge_coin_id }" selected="selected">${setting.pledge_symbol.toUpperCase() }</option>
								</c:if>
								<c:if test="${setting.pledge_coin_id <= 0}">
									<c:forEach items="${coins}" var="coin" varStatus="status">
										<option value="${coin.type_id}">${coin.type_name.toUpperCase() }</option>
									</c:forEach>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<label for="borrow_coin_id">借出币种：</label>
							<select name="borrow_coin_id" id="borrow_coin_id">
								<c:if test="${setting.borrow_coin_id > 0}">
									<option value="${setting.borrow_coin_id }" selected="selected">${setting.borrow_symbol.toUpperCase() }</option>
								</c:if>
								<c:if test="${setting.pledge_coin_id <= 0}">
									<c:forEach items="${coins}" var="coin" varStatus="status">
										<option value="${coin.type_id}">${coin.type_name.toUpperCase() }</option>
									</c:forEach>
								</c:if>
							</select>
							<div class="clear"></div>
						
							<label for="pledge_digits">质押数量精度：</label>
							<input name="pledge_digits" value="${setting.pledge_digits}"></input>
							<div class="clear"></div>
							
							<label for="borrow_digits">借出数量精度：</label>
							<input name="borrow_digits" value="${setting.borrow_digits}"></input>
							<div class="clear"></div>
							
							<label for="borrow_uids">可借出用户Id（逗号分隔）：</label>
							<input name="borrow_uids" value="${setting.borrow_uids}"></input>
							<div class="clear"></div>
							
							<label for="max_borrow_amount">最大可借出数量：</label>
							<input name="max_borrow_amount" value="<fmt:formatNumber pattern="#0.########" value='${setting.max_borrow_amount}'/>"></input>
							<div class="clear"></div>
							
							<label for="min_borrow_amount">最小借出数量：</label>
							<input name="min_borrow_amount" value="<fmt:formatNumber pattern="#0.########" value='${setting.min_borrow_amount}'/>"></input>
							<div class="clear"></div>

							<label for="init_pledge_rate">初始质押率(小数)：</label>
							<input name="init_pledge_rate" value="<fmt:formatNumber pattern="#0.########" value='${setting.init_pledge_rate}'/>"></input>
							<div class="clear"></div>
						
							<label for="curr_pledge_rate">补仓抵押率(小数)：</label>
							<input name="curr_pledge_rate" value="<fmt:formatNumber pattern="#0.########" value='${setting.curr_pledge_rate}'/>"></input>
							<div class="clear"></div>
							
							<label for="close_pledge_rate">平仓抵押率(小数)：</label>
							<input name="close_pledge_rate" value="<fmt:formatNumber pattern="#0.########" value='${setting.close_pledge_rate}'/>"></input>
							<div class="clear"></div>
							
							<label for="fee_rate">手续费率(小数)：</label>
							<input name="fee_rate" value="<fmt:formatNumber pattern="#0.########" value='${setting.fee_rate}'/>"></input>
							<div class="clear"></div>
							
							<label for="borrow_type">借出方式：</label>
							<select name="borrow_type" id="borrow_type">
								<option value="0" ${setting.borrow_type == 0 ? "selected='selected'" : ""}>顺序用户借出</option>
								<option value="1" ${setting.borrow_type == 1 ? "selected='selected'" : ""}>随机用户借出</option>
							</select>
							<div class="clear"></div>
							
							<label for="state">状态：</label>
							<select name="state" id="state">
								<c:if test="${setting.state == 1}">
								<option value="1" selected="selected">开启</option>
								<option value="0">关闭</option>
								</c:if>
								<c:if test="${setting.state != 1}">
								<option value="1">开启</option>
								<option value="0" selected="selected">关闭</option>
								</c:if>
							</select>
							<div class="clear"></div>
							
							<button type="submit" id="updateUid" class="button">${setting.id > 0 ? '修改' : '新增'}</button>
						</form>
					</div>
					
					<font color="red">${msg}</font>
					
					<table id="changecolor">
						<thead>
							<tr>
								<th>
									抵押币种
								</th>
								<th>
									借出币种
								</th>
								<th>
									可借出用户列表
								</th>
								<th>
									最大借出数量
								</th>
								<th>
									最少借出数量
								</th>
								<th>
									初始抵押率
								</th>
								<th>
									补仓抵押率
								</th>
								<th>
									平仓抵押率
								</th>
								<th>
									手续费率
								</th>
								<th>
									借出方式
								</th>
								<th>
									状态
								</th>
								<th>
									操作
								</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${pm.datas}" var="l" varStatus="status">
								<tr>
									<td>
										${l.pledge_symbol.toUpperCase()}
									</td>
									<td>
										${l.borrow_symbol.toUpperCase()}
									</td>
									<td>
										${l.borrow_uids}
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.max_borrow_amount}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.min_borrow_amount}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.init_pledge_rate}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.curr_pledge_rate}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.close_pledge_rate}'/>
									</td>
									<td>
										<fmt:formatNumber pattern="#0.########" value='${l.fee_rate}'/>
									</td>
									<td>
										<c:out value="${l.borrow_type == 0 ? '按顺序' : '随机'}"></c:out>
									</td>
									<td>
										<c:out value="${l.state == 1 ? '在使用' : '已关闭'}"></c:out>
									</td>
									<td>
										<a class="link" href="<%=actionPath%>managerLoanSetting/loanSettingManager.do?id=${l.id}">编辑</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<tfoot>
						<tr>
							<td colspan="9">
								<div class="pagination">
									<!-- ********** 分页开始 **********	-->
									<pg:pager url="managerLoanSetting/loanSettingManager.do" items="${pm.total}" maxIndexPages="10" 
									maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
										<pg:param name="id" value="${setting.id}"/>
										
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
				<!-- End #tab2 -->
			</div>
			<!-- End .content-box-content -->
		</div>
		<!-- End .content-box -->
	</body>
	
	<script type="text/javascript" src="<%=actionPath%>common/js/jquery.1.7.1.min.js"></script> 
	<script type="text/javascript">
	</script>
</html>