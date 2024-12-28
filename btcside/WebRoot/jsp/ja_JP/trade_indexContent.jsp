<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../_basepage.jsp"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
		
<div class="main-wrap">
	<div class="menu-title">
		<h3 style="color: #2b364b;">
			<span class="icons-data1"></span>${code}価格指数(${code} Price Index)
		</h3>
	</div>
	<div class="tab-wrap clearfix">
		<div class="tab lt">
			<a href="<%=basePath%>bpiAction/bpiIndex.do?code=${code}&coinType=USDT" class="${coinType eq 'USDT'? 'current':'' }">
				<c:choose>
					<c:when test="${transactionKind == 1 }">ポイント</c:when>
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
			索引ソース
		</h2>
	</div>
	<div class="trade-manage-main">
		<table>
			<thead>
				<tr>
					<th class="text-left gray" style="padding-left: 10px;">
						取引所
					</th>
					<th class="text-left gray">
						トランザクションペア
					</th>
					<th class="text-left gray">
						最新取引価格
					</th>
					<c:if test="${transactionKind != 3 }">
					<th class="text-left gray">
						24時間最大
					</th>
					<th class="text-left gray">
						24時間最小
					</th>
					<th class="text-left gray">
						取引量
					</th>
					</c:if>
					<th class="text-left gray">
						比率を占める
					</th>
					<th class="text-left gray">
						更新日時
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
			${code}価格指数の説明
		</h2>
	</div>
	<div class="box">
		<div class="freedom-broker">
			${indexDescription.ja_description }
		</div>
	</div>
</div>
			