<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="msg.pg.systemName"/>-<fmt:message key="msg.pg.userCenter.center"/></title>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>" />
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
<jsp:include page="_common.jsp"></jsp:include>
</head>

<body>
<div class="add-windows" id="alert-windows"></div>
<jsp:include page="_top.jsp"></jsp:include>
<jsp:include page="_head.jsp"></jsp:include>
<div class="container center-cont">
  <jsp:include page="_user_left.jsp"></jsp:include>
  
  <div class="main-rt-box">
    <div class="main-wrap">
      <div class="block-title skyGray">
        <h2 class="nav-wids">
        <c:if test="${targetCoinName eq 'BFC'}">
        	<a href="<%=userPath %>exchangeBfc.do?originCoinName=${targetCoinName }&targetCoinName=${originCoinName }" class="exchange-tog">
	        	<span class="exchange-header">${targetCoinName }>><fmt:message key="msg.pg.userCenter.exchangeUp"/>>>${originCoinName }</span>
	        </a>
	        <a href="<%=userPath %>exchangeBfc.do?originCoinName=${originCoinName }&targetCoinName=${targetCoinName }" class="exchange-tog exchange-current">
	        	<span class="exchange-header">${originCoinName }>><fmt:message key="msg.pg.userCenter.exchangeUp"/>>>${targetCoinName }</span>
	        </a>
	    </c:if>
	     <c:if test="${originCoinName eq 'BFC'}">
	     	<a href="<%=userPath %>exchangeBfc.do?originCoinName=${originCoinName }&targetCoinName=${targetCoinName }" class="exchange-tog exchange-current">
	        	<span class="exchange-header">${originCoinName }>><fmt:message key="msg.pg.userCenter.exchangeUp"/>>>${targetCoinName }</span>
	        </a>
	         <a href="<%=userPath %>exchangeBfc.do?originCoinName=${targetCoinName }&targetCoinName=${originCoinName }" class="exchange-tog">
	        	<span class="exchange-header">${targetCoinName }>><fmt:message key="msg.pg.userCenter.exchangeUp"/>>>${originCoinName }</span>
	        </a>
	    </c:if>
        </h2>
      </div>
      <div class="exchange-context">
      	<div>	
	     <b>
	     	<fmt:message key="msg.pg.userCenter.currentPri"/>：&emsp;${price.doubleValue() } BFC/
	     		<c:if test="${targetCoinName eq 'BFC'}">${originCoinName }</c:if>
	     		<c:if test="${targetCoinName ne 'BFC'}">${targetCoinName }</c:if>
	     	</b>
	     <hr>
	     <form id="form-exchage" action="">
	     	<div class="margin20"><b><fmt:message key="msg.pg.userCenter.accountBlance"/>：&emsp;
	     		<span>
	     			${balance.doubleValue() }${originCoinName }
	     		</span>
	     	</b></div>
	     	<div class="exchange-state margin20">
		     	<div><b><fmt:message key="msg.pg.userCenter.exchangeNum"/>：</b></div>
		     	<div class="marginL12 pos-rela">
		     	<input type="text" class="exchange-pt" id="exchange-pt" name="quantity" onkeyup="checkInputDigits(this, 4);" onkeydown="clearPromt()" onblur="getAmount()">
		     	<span class="exchange-unit exchange-unit-us">
		     		<c:if test="${targetCoinName eq 'BFC'}">BFC</c:if>
	     			<c:if test="${targetCoinName ne 'BFC'}">${targetCoinName }</c:if>
	     			<a href="javascript:void(0)" onclick="checkExchangeable()" style="font-weight: bold"><fmt:message key="msg.pg.userCenter.exchangeAll"/></a>
				</span><br>
		     	<span style="font-size: 12px; color: #999;"><fmt:message key="msg.pg.userCenter.minexchangeNum"/>：${minAmount }
		     		<span id="exchange-kind"><c:if test="${originCoinName eq 'BFC'}">${targetCoinName }</c:if>
		     		<c:if test="${targetCoinName eq 'BFC'}">${originCoinName }</c:if></span>
		     	</span><br>
		     	<div id="inputError" style="color: red;"></div>
		     	</div>
		     	
	     	</div>
	     	<div class="margin20"><b><fmt:message key="msg.pg.userCenter.exchangeAmount"/>：&emsp;<span id="exchange-amt">0.00 </span>${originCoinName}</b></div>
	     	<div class="exchange-state margin20">
	     		<div class="exchange-outBlock"><b class="exchange-stated"><fmt:message key="msg.pg.trade.stated"/>：</b></div>
	     		<div class="exchange-details  marginL12">
	     			1. <fmt:message key="msg.pg.userCenter.statedUsdt"/><br>
					2. <fmt:message key="msg.pg.userCenter.statedTwo"/><br>
					3. <fmt:message key="msg.pg.userCenter.statedInteger"/><br>
	     		</div>
	     	</div>
	     	<div class="text-center">
	     		<!-- 成功后提示 -->
				<div class="trade-alert">
					<div class="success display-none" id="exinfomsg_kc">
						<c:if test="${not empty success }">${success }</c:if>	
					</div>
				</div>
	     		<input type="button" class="btn btn-blue" id="btn-pt" onclick="checkAgain();" value="<fmt:message key="msg.pg.userCenter.exchangeUp"/>">     		
	     	</div>
	     	<input type="hidden" class="exchange-pt" id="" name="originCoinName" value="${originCoinName }">
	     	<input type="hidden" class="exchange-pt" id="" name="targetCoinName" value="${targetCoinName }">
     	 </form>
     	</div>
      </div>
      
    </div>
    <div class="main-wrap">
      <div class="block-title">
        <h2><fmt:message key="msg.pg.userCenter.exchangeRecord"/> </h2>
      </div>
      <div class="trade-manage-main">
        <table cellpadding="0" cellspacing="0">
          <thead>
            <tr>
              <th><fmt:message key="msg.pg.index.recordTime"/></th>
              <th><fmt:message key="msg.pg.userCenter.exchangeType"/></th>
              <th><fmt:message key="msg.pg.userCenter.exchangeDealPrice"/></th>
              <th><fmt:message key="msg.pg.userCenter.exchangeNum"/></th>
              <th><fmt:message key="msg.pg.userCenter.exchangeAmount"/></th>
              <th><fmt:message key="msg.pg.tradeQuery.state"/></th>
            </tr>
          </thead>
          <tbody>
			<tr>
				<c:choose>
					<c:when test="${pm.datas != null}">
						<c:forEach var="records" items="${pm.datas}">
							<tr>
								<td>
									<span> <ut:formatDate dateline="${records.record_time }"></ut:formatDate>
									</span>
								</td>
								<td>
									<c:out value="${records.ori_currency_name }/${records.target_currency_name}" />
								</td>
								<td>
									<fmt:formatNumber value="${records.order_price }" pattern="#0.0000"></fmt:formatNumber>
								</td>
								<td>
									<fmt:formatNumber value="${records.target_amount }" pattern="#0.0000"></fmt:formatNumber>
								</td>
								<td>
									<fmt:formatNumber value="${records.ori_amount }" pattern="#0.0000"></fmt:formatNumber>
								</td>
								<td>
									<c:choose>
										<c:when test="${records.state == 4}">
											<span style="color: green;"><fmt:message key="msg.pg.userCenter.haddone"/></span>
										</c:when>
										<c:when test="${records.state == 5}">
											<span style="color: red;"><fmt:message key="msg.pg.userCenter.hadCancel"/></span>
										</c:when>
										<c:otherwise>
											<span style="color: orange;"><fmt:message key="msg.pg.userCenter.hadProess"/></span>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="7" height="30" align="center" bgcolor="#FFFFFF">
								<fmt:message key="msg.pg.userCenter.noHistory"/>。
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tr>
          </tbody>
        </table>
      </div>
	      <ul class="pagination">
			<pg:pager url="${pageN}" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
				
				<pg:param name="originCoinName" value="${originCoinName }" />
				<pg:param name="targetCoinName" value="${targetCoinName }" />
				
				<pg:prev>
				<li class="disabled">
					<a href="<%=basePath%>${pageUrl}"
						mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>
						</li>
				</pg:prev>
				
				<pg:pages>
					<c:choose>
						<c:when test="${currentPageNumber eq pageNumber }">
							<li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
						</c:when>
						<c:otherwise>
						<li>
							<a href="<%=basePath%>${pageUrl }"
								mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
						</c:otherwise>
					</c:choose>
				</pg:pages>
	
				<pg:next>
				<li>
					<a href="<%=basePath%>${pageUrl }"
						mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a>
						</li>
				</pg:next>
			</pg:pager>
		</ul>
    </div>
  </div>
  

</div>
<jsp:include page="_footer.jsp"></jsp:include>   
</body>
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script>
<script>
	var origin = '${originCoinName }';
	var target = '${targetCoinName }';
	var min = ${minAmount};
	var kinds = $("#exchange-kind").html();
	function checkAgain() {
		var Ams = $("#exchange-pt").val();
		if(Ams == '') {
			$("#inputError").html(MSG['enter'] + min + "&nbsp" + kinds);
			return false;	
		}
		if(Ams >= min) {
			$("#inputError").html('');	
		}
		if($("#inputError").html() == '' && Ams >= min && Ams > 0) {
			var qr = "";
			qr = MSG['enterChange'];
			qr += Ams + " " + target + "?";
			showAlert(qr);
		} else {
			return false;
		}
	}
	
	function doExchange() {
		var Ams = $("#exchange-pt").val();
		if(Ams == '' || $("#inputError").html() != '' || Ams < min || Ams < 0) {
			return false;	
		}
		closeAlert();
		$.post(basePath+'center/doExchangeBfc.do',$('#form-exchage').serialize(),function(data){
			var data = eval("(" + data + ")");
			exshowSuccInfo_kc(data.code,data.msg);
			setTimeout(function (){
				location.reload();
			}, 3000);
		});
		$("#exchange").attr("disabled","disabled");
		return false;
	}
	
	function closeAlert() {
		$("#alert-windows").hide();
	}
	
	//提示框
	function showAlert(msg) {
		$("#alert-windows").show();
		var insidehtm = "<div id='tb-overlay-duiping' class='tb-overlay'></div><div class='add-wrap alert-wrap' style='display: block;' id='invite-BFX'>"
					+"<div class='dialogs-title'>"
					+"<h2 class='lt'>"
						+MSG['warmTip']
					+"</h2>"
					+"<a class='close-icon icons-close' id='close-alert' href='javascript:closeAlert()'></a>"
					+"</div>"
					+"<div class='dialogs-body'>"
					+"<p style='text-align: center; font-size: 16px; margin-bottom: 30px;'>"+ msg +"</p>"
					+"<div class='form-group-btn text-center'>"
						+"<button class='btn sub-btn center' id='exchange' style='margin-right: 0px; border: none;' onclick='doExchange();'>"+MSG['sureTip']+"</button>"
					+"</div>"
					+"</div>"
				+"</div>";
		$("#alert-windows").html(insidehtm);
		center($('.alert-wrap'));
	}
	
	function clearPromt() {
		$("#inputError").html('');	
	}
	
	function exshowSuccInfo_kc(code,text) {
		if (code != '') {
			$("#exinfomsg_kc").css("display", "block");
			if (code == "success") {
				$("#exinfomsg_kc").removeClass('warning');
				$("#exinfomsg_kc").addClass('success');
				$("#exinfomsg_kc").html(text);
			} else {
				$("#exinfomsg_kc").addClass('warning');
				$("#exinfomsg_kc").removeClass('success');
				$("#exinfomsg_kc").html(text);
			}
		} else {
			
		}
	}
	
	function checkExchangeable() {
		$.ajax({
			url: '<%=userPath %>checkExchangeBfcAmount.do',
			type: 'post',
			cache: false,
			data: {
				originCoinName:origin,targetCoinName:target,
			},
			dataType: "JSON",
			success: function(data) {
				if (data.code == 'success') {
					$("#exchange-pt").val(data.msg);
					getAmount();
				} else {
					$("#inputError").html(data.msg);
				}
			}
			
		})
	}
	
	function getAmount() {
		var Ams = $("#exchange-pt").val();
		if(Ams == 0 || Ams == '' || Ams < min) {
			$("#inputError").html(MSG['enter'] + min + "&nbsp" + kinds);
			return false;	
		}
		if(Ams >= min) {
			$("#inputError").html('');
			$.ajax({
			url: '<%=userPath %>checkExchangeBfc.do',
			type: 'post',
			cache: false,
			data: {
				originCoinName:origin,targetCoinName:target,quantity:Ams,
			},
			dataType: "JSON",
			success: function(data) {
				if (data.code == 'success') {
					$("#exchange-amt").html(data.msg);
					$("#forzenAmount").html(data.frozen);
				} else {
					$("#inputError").html(data.msg);
					return;
				}
			}
				
			})	
		}		
	}
	
	var message = {};
	message["zh_CN"] = {
		enterChange : "确定兑换 ",
		enter : "最小兑换金额为：",
		warmTip : "温馨提示",
		sureTip : "确定",
	};
	message["zh_HK"] = {
		enterChange : "確定兌換 ",
		enter : "最小兌換金額為：",
		warmTip : "溫馨提示",
		sureTip : "確定",
	};
	message["en_US"] = {
		enterChange : "Are you sure about the exchange ",
		enter : "Minimum exchange amount：",
		warmTip : "Warm prompt",
		sureTip : "Sure",
	};
	message["ja_JP"] = {
		enterChange : "取引しますか？ ",
		enter : "最低交換額は：",
		warmTip: "ヒント",
		sureTip: "確定",
	};
	message["ko_KR"] = {
		enterChange : "교환 확인 ",
		enter : "최소 교환 금액은 다음과 같습니다：",
		warmTip : "팁",
		sureTip : "결정",
	};
	var MSG = message[locale];
</script>
</html>
