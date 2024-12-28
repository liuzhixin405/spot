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
		<title>模拟交易-合约管理</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script lang="javascript" src="<%=actionPath %>common/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		<script type="text/javascript">
			function selectoption(obj) {
				var bqCode = document.getElementById("bqCode");
				bqCode.value=obj;
			}
			
			window.onload=function(){
				var obj = document.getElementById("tranType");
				var index = obj[obj.selectedIndex].id;
				selectoption(index);
			}
			
		</script>
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<div class="content-box">
				<div class="content-box-header">
					<h3>
						模拟交易-合约管理
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<div class="notification success png_bg">
							<br>
							<div>
								<form action="<%=actionPath%>simulation/managerTrans/addManagerTrans.do" method="post" onsubmit="return postsubmit();">
									合约类型:
									<select name="tranType" id="tranType" onchange="selectoption(this[this.selectedIndex].id);" >
										<c:forEach items="${typeList}" var="transType">
											<option value="${transType.type_id}" id="${transType.type_code}">${transType.type_name}</option>
										</c:forEach>
									</select>
									合约号：
									<input type="text" name="bqCode" id="bqCode" class="text-input " value="${bqCode}"></input>
									交易资金币种ID：
									<input type="text" name="currencyTypeId"  class="text-input " value="${currencyTypeId}"></input>
									合约状态：
									<input type="text" name="state" class="text-input " value="${state}"></input>
									杠杆：
									<input type="text" name="multiple" class="text-input " value="${multiple}"></input>
									杠杆状态：
									<input type="text" name="multipleState" class="text-input " value="${multipleState}"></input>
									交易系数：
									<input type="text" name="tradeCoefficient" id="tradeCoefficient" class="text-input " value="${tradeCoeff }"></input>
									<!-- 截至时间：
									<input type="text" name="endTime" class="Wdate" value="${endTime }" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" readonly="readonly"></input>
									 -->
									安全密码：
									<input class="text-input " name="password" type="password" value="" />
									<button type="submit" class="button">
										新 增
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
											<a href="<%=actionPath%>simulation/managerTrans/loadTrans.do?bqCode=${l.transaction_code}">编辑</a>
										</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="11">
										<div class="pagination">
											<!-- ********** 分页开始 **********	-->
											<pg:pager url="simulation/managerTrans/loadManagerTrans.do" items="${pm.total}" maxIndexPages="${pm.maxIndexPages}"
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
