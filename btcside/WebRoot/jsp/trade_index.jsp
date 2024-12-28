<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.trade.index.bitcoinPrice"/></title>
		<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
		<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script>
	</head>

	<body>
		<jsp:include page="_top.jsp"></jsp:include>
		<jsp:include page="_head.jsp"></jsp:include>

		<!-- 主体内容 -->
		<div class="container-trade-data center-cont">
			<jsp:include page="${locale}/trade_indexContent.jsp" />	
		</div>
		<div class="clear"></div>

		<jsp:include page="_footer.jsp"></jsp:include>
	</body>
	<script>
	
	setInterval(refreshIndex, 1000);
	function refreshIndex() {
		$.ajax({
	        type: "POST",
	        dataType: "json",         
	        url: basePath +"bpiAction/getIndex.do",
	        data: "code=" + "${code}",
	        success: function(data) {
	        	show(data.data);
	        	$("#price").html(data.indexPrice);
	        }
		});
	}

	function show(data) {
		if (!data) return;
		var indexHtml = "";
		for (var index = 0; index < data.length; index++) {
			var bpiVo = data[index];
			
			var wan = ""
			if (bpiVo.largeVol) {
				wan = "万";
			}
			
			var kind = "${transactionKind }";
			var otherHtml = "";
			if (kind != 3) {
				otherHtml = "<td class='text-left' >" + bpiVo.highestPrice + "</td>"
				+ "<td class='text-left' >" + bpiVo.lowestPrice + "</td>"
				+ "<td class='text-left' >" + bpiVo.volume + wan + "</td>";
			}
			
			indexHtml += "<tr> "
				+ "<td class='text-left' style='padding-left: 10px;'>" + bpiVo.siteName + "</td>"
				+ "<td class='text-left' >" + bpiVo.transPair + "</td>"
				+ "<td class='text-left' >" + bpiVo.latestPrice + "/$" + bpiVo.latestUsdPrice + "</td>"
				+ otherHtml
				+ "<td class='text-left' >" + bpiVo.dealCount + "</td>"
				+ "<td class='text-left' >" + bpiVo.updateTime + "</td>"
				+ "</tr>";
		}
		$("#indexTbody").html(indexHtml); 
	}
	</script>
</html>

