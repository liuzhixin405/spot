<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>交割合约-合约参数管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script language="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript">
			function postform(url) {
				document.select.action=url;
				document.select.submit();
			}
			
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						交割合约-合约参数管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>delivery/managerTrans/loadTrans.do" method="post">
									合约号：
									<input type="text" name="bqCode" class="text-input " value="${bqCode}"></input>
									合约状态：
									<input type="text" name="state" class="text-input " value="${state}"></input>
									<button type="submit" class="button">
										查 询
									</button>
									<span>注释：合约号必填写(没填写则查询全部),合约状态（1:正常 2:清算中 3:关闭 其它为全部）</span>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>delivery/managerTrans/updateTrans.do" method="post" id="select" name="select">
									合约号：
									<input type="text" name="bqCode" class="text-input " value="${bqCode}"></input>
									杠杆：
									<input type="text" name="multiple" class="text-input " value="${multiple}"></input>
									(','隔开)
									杠杆状态：
									<input type="text" name="multipleState" class="text-input " value="${multipleState}"></input>
									(','隔开)
									安全密码：
									<input name="password" type="password" value="" />
									<button type="submit" class="button">
										添加
									</button>
									<span>注释：杠杆(数字),交易系数非负(默认是BPI),杠杆的状态(0不显示,1显示,2禁用)</span>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>delivery/managerTrans/addTradeTrans.do" method="post">
									合约号：
									<input type="text" name="bqCode" class="text-input " value="${bqCode}"></input>
									交易系数：
									<input type="text" name="tradeCoefficient" class="text-input " value="${trade_coefficient == null ? bpi : trade_coefficient }"></input>
									安全密码：
									<input name="password" type="password" value="" />
									<button type="submit" class="button">
										修 改
									</button>
									<span>注释：交易系数非负（默认是BPI）</span>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>delivery/managerTrans/pauseTrans.do" method="post">
									合约号：
									<input type="text" name="bqCode" class="text-input " value="${bqCode}"></input>
									
									是否暂停：
									<select name="suspend" id="suspend">
										<c:if test="${suspend}">
										<option value="true" selected="selected">是</option>
										<option value="false">否</option>
										</c:if>
										<c:if test="${!suspend}">
										<option value="true">是</option>
										<option value="false" selected="selected">否</option>
										</c:if>
									</select>
									
									合约转入状态：
									<select name="transferIn" id="transferIn">
										<c:if test="${transferIn}">
										<option value="true" selected="selected">开启</option>
										<option value="false">暂停</option>
										</c:if>
										<c:if test="${!transferIn}">
										<option value="true">开启</option>
										<option value="false" selected="selected">暂停</option>
										</c:if>
									</select>
									
									合约转出状态：
									<select name="transferOut" id="transferOut">
										<c:if test="${transferOut}">
										<option value="true" selected="selected">开启</option>
										<option value="false">暂停</option>
										</c:if>
										<c:if test="${!transferOut}">
										<option value="true">开启</option>
										<option value="false" selected="selected">暂停</option>
										</c:if>
									</select>
							
									安全密码：
									<input name="password" type="password" value="" />
									<button type="submit" class="button">
										修 改
									</button>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>managerTrans/updateKind.do" method="post">
									合约号：
									<input type="text" name="bqCode" class="text-input " value="${bqCode}"></input>
									合约类别：
									<select name="kind" id="kind" >
										<c:if test="${kind eq 0 }">
											<option value="0" selected="selected">虚拟货币</option>
											<option value="1" >股指</option>
										</c:if>
										<c:if test="${kind eq 1 }">
											<option value="0" >虚拟货币</option>
											<option value="1" selected="selected">股指</option>
										</c:if>
									</select>
									安全密码：
									<input name="password" type="password" value="" />
									<button type="submit" class="button">
										修 改
									</button>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>delivery/managerTrans/updateShowName.do" method="post">
									合约号：
									<input type="text" name="bqCode" class="text-input " value="${bqCode}"></input>
									合约显示名称（中文）：
									<input type="text" name="showName" class="text-input " value="${showName}"></input>
									合约显示名称（英文）：
									<input type="text" name="enName" class="text-input " value="${enName}"></input>
									合约显示名称（日文）：
									<input type="text" name="jpName" class="text-input " value="${jpName}"></input>
									合约显示名称（韩文）：
									<input type="text" name="krName" class="text-input " value="${krName}"></input>
									合约显示名称（繁体）：
									<input type="text" name="hkName" class="text-input " value="${hkName}"></input>
									安全密码：
									<input name="password" type="password" value="" />
									<button type="submit" class="button">
										修 改
									</button>
								</form>
							</div>
							<br>
							<div>
								<form action="<%=actionPath%>delivery/managerTrans/updateEndtime.do" method="post">
									合约号：
									<input type="text" name="bqCode" class="text-input " value="${bqCode}"></input>
									交割时间：
									<input type="text" name="endTime" class="Wdate" value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" readonly="readonly"></input>
									安全密码：
									<input name="password" type="password" value="" />
									<button type="submit" class="button">
										修 改
									</button>
								</form>
							</div>
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
										显示名称
									</th>
									<th>
										合约类别
									</th>
									<th>
										合约状态
									</th>
									<th>
										最新成交价
									</th>
									<th>
										最高报价
									</th>
									<th>
										最低报价
									</th>
									<th>
										具有的杠杆
									</th>
									<th>
										杠杆的状态
									</th>
									<th>
										交易系数（分母）
									</th>
									<th>
										开始时间
									</th>
									<th>
										结束时间
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
											<c:out value="${l.transaction_code}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.transaction_show_name}"></c:out>
										</TD>
										<td>
											<c:choose>
												<c:when test="${l.transaction_kind==0}">
													虚拟货币
												</c:when>
												<c:when test="${l.transaction_kind==1}">股指</c:when>
												<c:otherwise>其他</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${l.transaction_state==1}">
													<font color="green">有效</font>
												</c:when>
												<c:when test="${l.transaction_state==2}">交割中</c:when>
												<c:when test="${l.transaction_state==3}">关闭</c:when>
											</c:choose>
										</td>
										<TD>
											<c:out value="${l.latest_transaction_price}"></c:out>
										</TD>
										<TD>
											${l.transaction_highest}
										</TD>
										<td>
											${l.transaction_lowest}
										</td>
										<TD>
											<c:out value="${l.multiple}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.multiple_state}"></c:out>
										</TD>
										<TD>
											<c:out value="${l.trade_coefficient}"></c:out>
										</TD>
										<TD>
											<ut:formatDate dateline="${l.transaction_begin_time}"></ut:formatDate>
										</TD>
										<td>
											<ut:formatDate dateline="${l.transaction_end_time}"></ut:formatDate>
										</td>
										<td>
										<form action="<%=actionPath%>delivery/managerTrans/closeTradeTrans.do" method="post">
											<input type="hidden" name="bqCode" class="text-input " value="${l.transaction_code}"></input>
											安全密码：<input name="password" type="password" value="" style="width: 100px;"/>
											<button type="submit" class="button">关闭</button>
										</form>
										</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="delivery/managerUser/selectUsers.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
												maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
												<pg:param name="bqCode" value="${bqCode}" />
												<pg:param name="state" value="${state}" />
												
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
			</div>
		</div>
	</body>
</html>
