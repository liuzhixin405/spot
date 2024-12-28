<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script src="<%=basePath %>common/js/jquery.1.7.1.min.js" type="text/javascript" ></script>
    <title>Btcside最新价格</title>
    <style>
    body,
    div,
    table,
    tr,
    td {
        margin: 0;
        padding: 0;
    }
    table {
        border-collapse: collapse;
        border-spacing: 0;
    }
    .market-table {
        width:378px;
        border: 1px solid #f0f0f0;
        font-size: 14px;
    }
    .table-title {
        height: 45px;
        line-height: 45px;
        border-bottom: 1px solid #f0f0f0;
        text-indent: 10px;
        color: #5f696b;
    }
    .tbody {
        height: 38px;
        line-height: 38px;
        color: #78787a;
    }
    .tbody:after {
        content: "";
        height: 0;
        clear: both;
    }
    .bdb {
        border-bottom: 1px solid #f0f0f0;
    }
    .tbody span {
        
        float: left;
        display:block;
        width:33.33%;
    }
    .tbody span.btcq{
    	 width: 49.9%;
    }
    .tbody span:first-child{
    	text-indent: 10px;
    }
    .btc-color{
    	color:#23b3d8;
    }
    .ltc-color{
    	color:#b8a034;
    }
    .dog-color{
    	color:#debf26;
    }
    .q-color{
    	color:#2387d5;
    }
   
    </style>
		<script type="text/javascript">
			var afds = setInterval(updatePrice, 2000)
			function updatePrice() {
				$.ajax( {
					type : "POST",
					dataType : "JSON",
					url : "asyticker.do",
					data : {},
					success : function(data) {
						$("#lastBtc").html(data.lastBtc);
						$("#lastLtc").html(data.lastLtc);
						$("#lastDoge").html(data.lastDoge);
						$("#bqCode").html(data.bqCode);
						$("#lastBtcq").html(data.lastBtcq);
						$("#volBtcq").html(data.volBtcq);
					}
				});
			}
		</script>
	</head>

<body>
    <div class="market-table">
        <div class="table-title"><strong>BTC100现货行情</strong>
        </div>
        <div class="tbody bdb">
            <span><b class="btc-color">USDT:</b>￥<em id="lastBtc"><fmt:formatNumber value="${lastBtc }" pattern="#.###"></fmt:formatNumber></em></span>
            <span><b class="ltc-color">Ł:</b>￥<em id="lastLtc"><fmt:formatNumber value="${lastLtc }" pattern="#.###"></fmt:formatNumber></em></span>
            <span><b class="dog-color">D:</b>￥<em id="lastDoge"><fmt:formatNumber value="${lastDoge }" pattern="#.######"></fmt:formatNumber></em></span>
        </div>
        <div class="table-title"><strong>Btcside合约行情</strong>
        </div>
        <div class="tbody">
            <span class="btcq"><b class="q-color" id="bqCode">${bqCode }</b>：$<em id="lastBtcq"><fmt:formatNumber value="${lastBtcq }" pattern="#.##"></fmt:formatNumber></em></span>
            <span class="btcq"><b class="q-color">24小时成交量：</b><em id="volBtcq"><fmt:formatNumber value="${volBtcq }" pattern="#.##"></fmt:formatNumber></em></span>
        </div>
    </div>
</body>

</html>
