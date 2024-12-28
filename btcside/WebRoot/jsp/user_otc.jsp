<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Pragma" content="no-cache"/>
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Expires" content= "0"/>
<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.center"/></title>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript" src="<%=basePath%>common/js/telcode.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/password.js?<%=rjs%>"></script>
<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
<!-- 身份验证 -->
<div class="add-windows">
  <div id="tb-overlay-sfyz" class="tb-overlay display-none"></div>
  <div class="add-wrap" style="top:200px;" id="add-wrap-sfyz">
    <div class="dialogs-title">
      <h2 class="lt"><fmt:message key="msg.pg.userCenter.authenticate"/></h2>
      <a href="javascript:" class="close-icon icons-close close-alert" id=""></a> </div>
    <div class="dialogs-body" >
    <form action="javascript:" class="form-wrap" id="mobileform" method="post" name="mobileform" autocomplete="off">
      <div class="form-group" style="${tel!=0? '' : 'display:none' }">
        <label for=""><fmt:message key="msg.pg.userCenter.phoneVaidcode"/>
          <input type="hidden" name="mobile" id="mobile" value="${tel}" />
        </label>
        <input type="text"  class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entercellCode"/>" onblur="checkCode();" id="code" name="code" >
        <span class="error" id="error1"></span>
        <div class="send-btn-box">
          <input type="button" class="btn send-btn" onclick="send(1)" id="btnCode" name="btnCode" value="<fmt:message key="msg.pg.userCenter.availSMSCode"/>"/>
        </div>
      </div>
      <div class="form-group"  class="form-group" style="${google!=0 ? '' : 'display:none' }">
      <label for=""><fmt:message key="msg.pg.userCenter.googleCode"/></label>
      <input type="text" class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.entergoogleCode"/>" id="google" name="google">
      <span class="error" id="googleerror"></span>
      </div>
      <div class="form-group">
        <label for=""><fmt:message key="msg.pg.trade.assetPwd"/></label>
        <input type="password" class="form-control" placeholder="<fmt:message key="msg.pg.userCenter.enterassetPwd"/>" name="password" id="password" autocomplete="new-password" onblur="checkTradePwd();" />
        <span class="error" id="error3"></span> </div>
      <div class="form-group-btn">
        <button type="submit"  class="btn sub-btn" onclick="return changTradeNoInput();"><fmt:message key="msg.pg.userCenter.confirmSubmit"/></button>
      </div>
    </form>
  </div>
</div>
</div>
<jsp:include page="_top.jsp"></jsp:include>
<jsp:include page="_head.jsp"></jsp:include>
<div class="container center-cont">
  <jsp:include page="_user_left.jsp"></jsp:include>
  <div class="main-rt-box">
    <div class="main-wrap">
		<div class="block-title" style="border-bottom: 0;">
		  <h2><fmt:message key="msg.pg.userCenter.myAseets"/> </h2>
		</div>
		<div class="block-title-combine">
			<ul class="combine-menu-ul">
				<li class="${actionName=='assets'  ? 'current' : ''}">
					<a href="<%=userPath %>assets.do"> <span></span><fmt:message key="msg.pg.head.bibiAcount"/></a>
				</li>
<%--				<li class="${actionName=='otc'  ? 'current' : ''}">--%>
<%--						<a href="<%=userPath %>otc.do"> <span>OTC</span><fmt:message key="msg.pg.workerOrderAdd.account"/></a>--%>
<%--                </li>--%>
<%--                <li class="${actionName=='center'  ? 'current' : ''}">--%>
<%--                    <a href="<%=userPath %>center.do"> <span></span>USDT<fmt:message key="msg.pg.head.AccountContract"/></a>--%>
<%--                </li>--%>
<%--                <li class="${actionName=='usdr'  ? 'current' : ''}">--%>
<%--                    <a href="<%=userPath %>nCenter/usdr.do"> <span>USDR</span><fmt:message key="msg.pg.head.AccountContract"/></a>--%>
<%--                </li>--%>
				<%--<li class="${actionName=='dCenter'  ? 'current' : ''}">
					<a href="<%=userPath %>dCenter.do"> <span></span><fmt:message key="msg.pg.head.AccountDContract"/></a>
				</li>--%>
				<%--<li class="${actionName=='vCenter'  ? 'current' : ''}">
					<a href="<%=userPath %>vCenter.do"> <span></span><fmt:message key="msg.pg.userCenter.simulAseets"/></a>
				</li>--%>
				
			</ul>
		</div>
      <div class="info-box">
      	<input type="hidden" id="actionName" name="actionName" value="${actionName}">
        <ul class="info-list">
			<li  style="width: 100%;">
	          <div> 
	          	<input type="hidden" id="usdRate" value="${usdRate }">
	          	<b>OTC</span><fmt:message key="msg.pg.workerOrderAdd.account"/>总资产折合</b> <br>
	          	<span> 
		          	<em id="acountTalassetsShow">--</em> USDT
	            </span>
	            <span style="font-size: 18px;"> ≈<em id="totalCny"></em> CNY</span>
	          </div> 
          </li>
          <li>
	          <b>OTC可用折合</b><br>
	          <span><em id="available">--</em> USDT</span>
          </li>
          <li> 
	           <b>OTC冻结折合</b><br>
	          <span><em id="frozen">--</em> USDT</span>
          </li>
        </ul>
      </div>
    </div>
    <div class="main-wrap">
      <div class="trade-manage-main">
        <table cellpadding="0" cellspacing="0" id="table-details">
          <thead>
            <tr>
              <th><fmt:message key="msg.pg.userCenter.nameAssets"/></th>
              <th><fmt:message key="msg.pg.market.chartTotalAmount"/></th>
              <th><fmt:message key="msg.pg.userCenter.avaibleAssets"/></th>
              <th><fmt:message key="msg.pg.userCenter.frozeAssets"/></th>
              <th width="260px"><fmt:message key="msg.pg.trade.option"/></th>
            </tr>
          </thead>
          <tbody id="tickerTbody">
            
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
<jsp:include page="user_transfer.jsp"></jsp:include>
<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
</body>
<script type="text/javascript">
	var message = {};
	message["zh_CN"] = {
		transferin: "转入",
		transferout: "转出",
		toBuy: "去购买",
		toSell: "去出售",
	};
	message["zh_HK"] = {
		transferin : "轉入",
		transferout: "轉出",
		toBuy: "去購買",
		toSell: "去出售",
	};
	message["en_US"] = {
		transferin : "Transfer in",
		transferout: "Transfer out",
		toBuy: "To buy",
		toSell: "To sell",
	};
	message["ja_JP"] = {
		transferin : "繰り越す",
		transferout: "転出する",
		toBuy: "買います",
		toSell: "売りに出す",
	};
	message["ko_KR"] = {
		transferin : "전입 하 다.",
		transferout: "옮기다",
		toBuy: "구입 하 다.",
		toSell: "판매 하 다.",
	};
	message["ru_RU"] = {
		transferin : "перевод",
		transferout: "вывозить",
		toBuy: "покупка",
		toSell: "продавать",
	};
	var MSG = message[locale];
	
	function getData() {
		var path = basePath;
		if (basePath.indexOf("127.") >= 0) {
			path = "http://" + document.domain + ":808/";
		} else if (basePath.indexOf("47.") >= 0) {
			path = "http://" + document.domain + "/";
		}
		$.ajax({
			type : "GET",
			dataType : "json",
			url : path + "otc/otcAssetsApi/datas",
			data : {
				token: localStorage.token
			},
			success : function(data) {
				if (data.code == 0) {
					dataArr = data.data;
					showData(dataArr);
				}
			}
		});
	}
	
	function showData(data) {
		var path = basePath;
		if (basePath.indexOf("127.") >= 0) {
			path = "http://" + document.domain + ":808/";
		} else if (basePath.indexOf("47.") >= 0) {
			path = "http://" + document.domain + "/";
		}
		var tickerHtml = "";
		var totalAva = 0;
		var totalFrozen = 0;
		for (var index = 0; index < data.length; index++) {
			var obj = data[index];
			if (obj.coinName == "USDT" || obj.coinName == "USDR") {
				totalAva += parseFloat(obj.available);
				totalFrozen += parseFloat(obj.frozen);
			}
			
			var buyPath = path + "otc/toBuyBtc?currency=" + obj.coinName;
			var sellPath = path + "otc/toSellBtc?currency=" + obj.coinName;
			var inPath = path + "otc/user/asset?coinId=" + obj.coinId + "&tag=recharge";
			var outPath = path + "otc/user/asset?coinId=" + obj.coinId + "&tag=1";
			tickerHtml += "<tr>"
				+ "<td>" + obj.coinName + "</td>"
				+ "<td>" + parseFloat(obj.available + obj.frozen) + "</td>"
				+ "<td>" + parseFloat(obj.available) + "</td>"
				+ "<td>" + parseFloat(obj.frozen) + "</td>"
				+ "<td>"
				+ "<a href='" + inPath + "' class='btn btn-blue'>" + MSG['transferin'] + "</a>"
				+ "<a href='" + outPath + "' class='btn btn-blue'>" + MSG['transferout'] + "</a>"
				+ "<a href='" + buyPath + "' class='btn btn-blue'>" + MSG['toBuy'] + "</a>"
				+ "<a href='" + sellPath + "' class='btn btn-blue'>" + MSG['toSell'] + "</a>"
				+ "</td>"
				+ "</tr>";
		}
		
		var total = totalAva + totalFrozen;
		var totalCny = total * parseFloat($("#usdRate").val());
		$("#acountTalassetsShow").html(formatFloat(total, 4));
		$("#available").html(formatFloat(totalAva, 4));
		$("#frozen").html(formatFloat(totalFrozen, 4));
		$("#totalCny").html(totalCny.toFixed(2));
		
		$("#tickerTbody").html(tickerHtml);
	}
	
	function formatFloat(src, pos) {
		return Math.floor(src * Math.pow(10, pos)) / Math.pow(10, pos);
	}
	
	// $(function() {
	// 	getData();
	// });
	
</script>
</html>
