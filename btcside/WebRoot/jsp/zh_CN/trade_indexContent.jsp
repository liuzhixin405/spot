<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
		
<div class="main-wrap">
	<div class="menu-title">
		<h3 style="color: #2b364b;">
			<span class="icons-data1"></span>${code}价格指数(${code} Price Index)
		</h3>
	</div>
	<div class="tab-wrap clearfix">
		<div class="tab lt">
			<a href="<%=basePath%>bpiAction/bpiIndex.do?code=${code}&coinType=USDT" class="${coinType eq 'USDT'? 'current':'' }">
				<c:choose>
					<c:when test="${transactionKind == 1 }">点位</c:when>
					<c:otherwise>USDT</c:otherwise>
				</c:choose>
			</a>
		</div>
	</div>
	<div class="price-box clearfix">
		<div class="price-box-detail">
			<div class="price-lt lt">
				<b>
				<c:choose>
					<c:when test="${transactionKind == 1 }"><b id="price">${price}</b>点</c:when>
					<c:otherwise>$<b id="price">${price}</b></c:otherwise>
				</c:choose>
				</b>
				<span class="trend"></span>
			</div>
		</div>
	</div>
</div>
<div class="main-wrap">
	<div class="block-title">
		<h2>
			指数来源
		</h2>
	</div>
	<div class="trade-manage-main">
		<table>
			<thead>
				<tr>
					<th class="text-left gray" style="padding-left: 10px;">
						交易所
					</th>
					<th class="text-left gray">
						交易对
					</th>
					<th class="text-left gray">
						最新成交价
					</th>
					<c:if test="${transactionKind != 3 }">
					<th class="text-left gray">
						24小时最高
					</th>
					<th class="text-left gray">
						24小时最低
					</th>
					<th class="text-left gray">
						成交量
					</th>
					</c:if>
					<th class="text-left gray">
						占比
					</th>
					<th class="text-left gray">
						更新时间
					</th>
				</tr>
			</thead>
			<tbody id="indexTbody">
				<c:forEach var="bpiVo" items="${listBpiVo}">
					<tr>
						<td class="text-left" style="padding-left: 10px;">
							${bpiVo.siteName}
						</td>
						<td class="text-left">
							${bpiVo.transPair}
						</td>
						<td class="text-left">
							<fmt:formatNumber value="${bpiVo.latestPrice}"/><span class="gray">/$<fmt:formatNumber value="${bpiVo.latestUsdPrice}"/></span>
						</td>
						<c:if test="${transactionKind != 3 }">
						<td class="text-left">
							<fmt:formatNumber value="${bpiVo.highestPrice}"/>
						</td>
						<td class="text-left">
							<fmt:formatNumber value="${bpiVo.lowestPrice}"/>
						</td>
						<td class="text-left">
							<fmt:formatNumber value="${bpiVo.volume}"/><c:if test="${bpiVo.largeVol }">万</c:if>
						</td>
						</c:if>
						<td class="text-left">
							${bpiVo.dealCount}
						</td>
						<td class="text-left">
							${bpiVo.updateTime}
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
<div class="main-wrap">
	<div class="block-title">
		<h2>
			${code}价格指数说明
		</h2>
	</div>
	<div class="box">
		<div class="freedom-broker">
			${indexDescription.zh_description }
		</div>
	</div>
</div>
			