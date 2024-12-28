<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!DOCTYPE HTML>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${lastPrice }$ Btcside区块链资产合约交易平台</title>
<link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=r %>">
<script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js?<%=rjs%>"></script> 
</head>
<body>
<jsp:include page="jisuan_sz.jsp"></jsp:include>
<!-- 上证合约用户交易须知 -->
<c:if test="${readnotice==0}">
<jsp:include page="_szfirstwindow.jsp"></jsp:include>
</c:if>
<!-- 若无合约 -->
<c:if test="${!hashy}">
<div class="add-windows" id="add-windows">
	<div class="tb-overlay" id="tb-overlay"></div>
	<div class="add-wrap" style="display: block;">
		<div class="dialogs-title">
			<h2 class="lt">
				BTC合约
			</h2>
			<a class="close-icon icons-close" id="close-alert"
				href="javascript:history.go(-1)"></a>
		</div>
		<div class="dialogs-body">
			<p
				style="text-align: center; font-size: 16px; margin-bottom: 30px;">
				对不起 暂无可交易合约，请稍候！
			</p>
			<div class="form-group-btn">
				<a class="btn sub-btn-return" href="javascript:history.go(-1)">返回</a>
			</div>
		</div>
	</div>
</div>
</c:if>

<jsp:include page="_top.jsp"></jsp:include>
<jsp:include page="_head.jsp"></jsp:include>

<div class="container">
  <jsp:include page="_trade_left_sz.jsp"></jsp:include>
  <div class="main-rt-box">
    <div class="sz-item">
        <span class="sz-span">昨日收盘：<b class="buy"><fmt:formatNumber value="${bpiother}" pattern="#.#"></fmt:formatNumber>点</b></span>
        <span class="sz-span">最高：<b class="sell" id="todayHigh"><fmt:formatNumber value="${todayHigh}" pattern="#.#"></fmt:formatNumber>点</b></span>
        <span class="sz-span">最低：<b class="buy" id="todayLow"><fmt:formatNumber value="${todayLow}" pattern="#.#"></fmt:formatNumber>点</b></span>
        <span class="sz-span">成交量：<b id="tradevols"><fmt:formatNumber value="${tradevols}" pattern="#.##"></fmt:formatNumber></b></span>
        <span class="sz-span">成交额：<b id="trades"><fmt:formatNumber value="${trades}" pattern="#.##"></fmt:formatNumber>USDT</b></span>
        <span class="sz-span">持仓量：<b id="holdvols"><fmt:formatNumber value="${holdvols}" pattern="#.##"></fmt:formatNumber></b></span>
    </div>
    <div class="main-wrap">
      <div class="rt-wrap lt">
        <div class="trade-rt-table">
          <table id="selldiv">
            <thead>
              <tr>
                <th> 方向 </th>
                <th> 价格 </th>
                <th> 数量 </th>
              </tr>
            </thead>
            <tbody id="sellTbody">
              <c:forEach var="sell" items="${selltop5List}">
                <tr onclick="trade_price_market('${sell.price}', '${sell.countNum}');">
                  <td class="sell"><span>${sell.name}</span></td>
                  <td><fmt:formatNumber value="${sell.price}" pattern="#.0">
                    </fmt:formatNumber></td>
                  <td><fmt:formatNumber value="${sell.countNum}" pattern="#">
                    </fmt:formatNumber></td>
                </tr>
              </c:forEach>
            </tbody>
            <tbody id="buyTbody">
              <c:forEach var="buy" items="${buytop5List}">
                <tr onclick="trade_price_market('${buy.price}', '${buy.countNum}');">
                  <td class="buy"><span>${buy.name}</span></td>
                  <td><fmt:formatNumber value="${buy.price}" pattern="#.0">
                    </fmt:formatNumber></td>
                  <td><fmt:formatNumber value="${buy.countNum}" pattern="#">
                    </fmt:formatNumber></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <div class="trade-rt-data"> <span>最新成交价： <em id="lastPrice">
          <fmt:formatNumber value="${currentTransaction.latest_transaction_price}" pattern="#.0">
          </fmt:formatNumber>
          </em>
          <input type="hidden" name="lastPriceOneMinAgo" id="lastPriceOneMinAgo" value="${lastpriceOneMinAgo}">
          </span> <span class="zs">上证指数： <em id="szi2">${btc_index_price }</em> </span> </div>
      </div>
      <div class="lt-wrap rt"  id="lt-wrap">
      <div class="sz-box"></div>
        <form action="" name="topSearchForm" id="topSearchForm">
          <input type="hidden" name="btc_index_price" id="btc_index_price" value="${btc_index_price}">
          <input type="hidden" name="bpiother" id="bpiother" value="${bpiother}">
          <div class="trade-title  trade-tt clearfix  relative ">
            <div class="trade-title-lt lt"> <span><strong>请选择合约：</strong> </span>
              <c:forEach var="transaction" items="${transactionList}"> <a href="javascript:;" name='${transaction.transaction_code}' onclick="changeTransaction(this.name);"
											<c:if test="${transaction.transaction_code eq currentTransaction.transaction_code}" > class="current" </c:if>> ${fn:toUpperCase(transaction.transaction_code)} </a> </c:forEach>
                      <a href="<%=helpPath%>sz.do" class="link" target="_blank">了解SZ合约</a>
              <input type="hidden" id="transaction_code" name="transaction_code" value="${currentTransaction.transaction_code}" />
            </div>
            <div class="sz-hint-block rt">
                <b class="sell" id="sz-hint">交易须知<i class="icons-hint"></i></b>
                <div class="que-help" id="sz-hint-help">
                  <div class="arrow-top"></div>
                  <p>SZ合约采用 <a href="<%=helpPath%>sz.do" target="_blank">“余额爆仓制”</a>。您在SZ合约上的可用余额（即未被持仓和挂单保证金占用的自由金额）须大于您的持仓浮亏金额，否则视为您的账户已经“爆仓” 平台风控人员将电话联系您自行降低账户风险（您可选择减仓释放可用余额或在 规定时间内追加保证金），如您不能在规定时间内处理，平台风控人员有权对您 的持仓按照市价进行部分或全部<a href="<%=helpPath%>sz.do" target="_blank">强平处理</a>。
                  </p>
                </div>
              </div>
          </div>
          <div class="trade-detail tog-ht">
              <input type="hidden" id="myRemainBtc" name="myRemainBtc" value="${userAccountBtc}">
              <input type="hidden" id="avagpriceysetady" name="avagpriceysetady" value="${avagpriceysetady }">
              <input type="hidden" id="nomalTrade" name="nomalTrade" value="${nomalTrade }">
              <input type="hidden" id="myCommissionRate" name="myCommissionRate" value="${userlevel.transaction_commission_rate}">
              
                <div class="toggle-wrap"> <span class="label"> <em id="toggle-em"><i class="icon icons-show"></i>合约当前可用：</em> </span> <b id="totalamount">${userAccountBtc } USDT </b>&nbsp;&nbsp;&nbsp;&nbsp; <a href="<%=basePath %>transferAccount/transfer.do?type=1&bqCode=${currentTransaction.transaction_code}" class="a-btn">转入</a> <%--<a href="https://www.btc100.com/" target="_blank" class="a-btn">从BTC100买币</a>--%><!-- <a href="<%=basePath %>activity/zhuanpan.do" style="padding-left: 15px; color: #c2504f;" target="_blank"> <img src="<%=basePath%>common/images/activity/xiao.gif?<%=rpic%>" alt="" style="vertical-align: middle; margin-right: 5px;"> <strong>${times }</strong> 次抽奖机会</a>  --></div>
                <div class="toggle-main" id="toggle-main">
                  <p> 其中可转出： <b id="settleamount">${userAccountGainBtc } USDT </b> <a href="<%=basePath %>transferAccount/transfer.do?type=2&bqCode=${currentTransaction.transaction_code}" class="a-btn">转出</a> </p>
                  <p> 合约余额：<b id="contractamount">${userContractReamain} USDT</b></p>
                  <p> 当前可转入：<b>${bqAccount.btc} USDT</b> </p>
                </div>
          </div>
          <div class="trade-main">
            <div class="trade-box">
              
              <div class="trade-detail relative ie-bug"> <span class="label">杠杆倍数：</span>
                <p class="lever-control">
                  <c:forEach items="${multiple}" varStatus="l" var="m">
                    <c:if test="${m.state==2}"> <a class="disabled-ggbs" href="javascript:void(0);">x${m.multiple}</a> </c:if>
                    <c:if test="${m.state==1}"> <a href="javascript:void(0);" <c:choose><c:when test="${demultiple eq m.multiple}">class="current multiple" </c:when><c:otherwise>class="multiple" </c:otherwise></c:choose>
																id="multiple${m.multiple}" onclick="multipleClick(${m.multiple},this);">x${m.multiple}</a> </c:if>
                  </c:forEach>
                  <a href="https://www.coinbankotc.com/toBuyBtc?currency=CK.USDT" class="link" target="_blank">没有USDT？去购买</a>
                </p>
                <input type="hidden" name="multiple" id="multiple"  value="${demultiple}" />
                <input type="hidden" name="token" id="token"  value="${cookie._token.value}" />
                <div class="que-help que-ggbs" id="ggbs-txt">
                  <div class="arrow-top"></div>
                  <p> 由于最新成交价偏离杠杆系数过大为了有效控制风险， 系统暂时关闭了该杠杆倍数。 </p>
                </div>
              </div>
              <div class="trade-detail relative"> <span class="label">开仓数量：</span>
                <input type="text" class="form-control" id="amount" name="amount" maxlength="8" 
											onblur="sztrade_amount();" autocomplete="off" onkeyup="checkInput(this,'szamount',event);" />
                <i class="sz-icons-input icons-ge"></i> </div>
              <div class="trade-detail relative ie-bug1 "> <span class="label">开仓点位：</span>
                <input type="text" class="form-control" autocomplete="off" id="price" name="price" maxlength="8"
											onblur="sztrade_price();" autocomplete="off" onkeyup="checkInput(this,'szprice',event);" />
                <i class="sz-icons-input icons-dian"></i> 
                <!-- 判断价格是否合理 -->
                <div class="alert-money display-none">
                  <div class="arrow-top"></div>
                  <p> 您的报价不符合系统的限价规则，当前 <span id="text_alert" style="margin: 0;">多仓最大可买入</span>价为： <b id="maxBuyPrice1_alert">7777</b> <br>
                    您是否确定以 <b id="maxBuyPrice2_alert">7777</b> <b class="buy" id="direct_alert">买入</b> <b id="buyAmount_alert">12</b>张合约 </p>
                  <div class="calculat-sub">
                    <button type="button" class="btn sub-btn" id="submit_alert"> 确定 </button>
                    <button type="button" class="btn sub-btn-return" id="close_alert"> 关闭 </button>
                  </div>
                </div>
              </div>
              <!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
              <div class="trade-detail" style="${payPwd ? 'display: block;' : 'display: none;' }"> <span class="label">资金密码：</span>
                <input type="password" class="form-control" value="" name="payPwd" maxlength="20" />
              </div>
              <div class="trade-detail relative">
                <div class="lt"> <span class="label">保证金：</span><b id="baozhengjin">0</b><b>&nbsp;USDT</b> </div>
                <div class="lt pdlt" style="padding-left: 10px;"> <em> <b class="icons-count"></b><a href="javascript:" id="jisuan">计算</a> </em> </div>
              </div>
              <div class="price-item clearfix sz"> <span>最高可买入价为 <b class="buy" id="maxBuy">${maxBuy }</b></span> <span>最低可卖出价为 <b class="sell" id="minSell">${minSell }</b></span> </div>
              <div class="sz-tp"  id="sz-trade-main">
                  <div class="trade-btn-box clearfix">
                  	<input type="hidden" id="direction" name="direction" value="">
                    <div class="trade-btn-wrap sz" id="buy">
                      <button type="button" id="btnBtc" class="btn trade-btn buy-bg" onclick="return del_sure_buy('<%=basePath %>tradeAjax/tradeTrans.do', 'szi', 1);"> 买 多 </button>
                    </div>
                    <div class="trade-btn-wrap  sz" id="sell">
                      <button type="button" id="sell" class="btn trade-btn sell-bg" onclick="return del_sure_sell('<%=basePath %>tradeAjax/tradeTrans.do', 'szi', 2);"> 卖 空 </button>
                    </div>
                  </div>
                  <div class="price-item clearfix sz"> </div>
                  <!-- 开仓后提示 -->
                  <div class="trade-alert">
                     <div class="success display-none" id="infomsg_kc"></div>
                  </div>
              </div>
            </div>
          </div>
        </form>
      </div>
      
    </div>
    <div class="clear"></div>
  </div>
</div>
<div class="clear"></div>
<div class="container-trade">
  <div class="main-wrap" style="overflow: visible;">
    <div class="block-title-trade">
      <div class="tab tab-trade lt" id="tab-trade"> <span class="current">持仓管理</span> <span>委托管理</span> </div>
      <div class="trade-time"> 距离合约：${fn:toUpperCase(currentTransaction.transaction_code)}结算还剩： <b id="transEndTime">${transactionEndTime}</b> </div>
      <div class="trade-control szhy"> <a href="javascript:;" onclick="cancelDeal('1','${currentTransaction.transaction_code}');">一键撤买</a> <a href="javascript:;" onclick="cancelDeal('2','${currentTransaction.transaction_code}');">一键撤卖</a> <a href="javascript:;" onclick="cancelDeal('0','${currentTransaction.transaction_code}');">全部撤单</a> </div>
      <div class="clear"></div>
    </div>
    <div class="clear"></div>
    <div class="trade-manage-main table-bg">
      <div class="trade-cont">
        <table cellpadding="0" cellspacing="0"
							style="margin-bottom: 25px;">
          <thead>
            <tr>
              <th> 类型 </th>
              <th> 可用持仓 </th>
              <th> 冻结 </th>
              <th> 平均价 </th>
              <th> 強平价格</th>
              <th> 保证金 </th>
              <th class="relative" width="100"> 浮动盈亏 <em class="que-icon icons-que th-help fdyk-txt" id="fdyk-help"></em> </th>
              <th> 盈亏比 </th>
              <th> 佣金 </th>
              <th width="70px;"> 平仓价格 </th>
              <th width="70px;"> 平仓数量 </th>
              <th width="110px"> 操作 </th>
            </tr>
          </thead>
          <tbody id="depotbuyTbody">
            <tr id="alert_tr_pc_buy" style="display: none;">
              <td colspan="12"><div class="trade-alert" style="margin-top: 0;">
                  <div class="success" id="infomsg_d"></div>
                </div>
                <input type="hidden" value="${buyMemeberTransCount}" id="buyTransCount" /></td>
            </tr>
            <c:if test="${buyMemberTransaction == null||empty buyMemberTransaction}">
              <tr>
                <td colspan="12"> 暂无买多持仓 </td>
              </tr>
            </c:if>
            <c:forEach items="${buyMemberTransaction}" var="tran" varStatus="l">
              <tr>
                <td> ${fn:toUpperCase(tran.bqCode)} <em class="buy">多</em>x${tran.multiple} </td>
                <td><fmt:formatNumber value="${tran.holdCount}" pattern="#">
                  </fmt:formatNumber>
                  张 </td>
                <td><fmt:formatNumber value="${tran.lockCount}" pattern="#">
                  </fmt:formatNumber>
                  张 </td>
                <td><fmt:formatNumber value="${tran.averagePrice}" pattern="#.#">
                  </fmt:formatNumber>
                  点 </td>
                <td><fmt:formatNumber value="${tran.deposit}" pattern="#.####">
                  </fmt:formatNumber>
                  USDT </td>
                <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }" id="floatGainLossbuy${l.index}"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
                  </fmt:formatNumber></td>
                <td id="floatGainLossRatebuy${l.index}"> ${tran.profitLossRatio } </td>
                <td><fmt:formatNumber value="${tran.commission }" pattern="#.######">
                  </fmt:formatNumber></td>
                <td><input type="hidden" id="buy_trade_multiple${l.index}" value="${tran.multiple}" />
                  <input type="hidden" id="buy_all_count${l.index}" value="${tran.allCount }" />
                  <input type="hidden" id="buy_hold_count${l.index}" value="${tran.holdCount }" />
                  <input type="hidden" id="buy_average_price${l.index}" value="${tran.averagePrice }" />
                  <input type="hidden" id="buy_deposit${l.index}" value="${tran.deposit }" />
                  <span class="td-child-control">
                  <input type="text" class="form-control price-input td-input" id="pcPrice_d${l.index}" name="price" value="<fmt:formatNumber value="${buytop5List[0].price}" pattern="#.#"></fmt:formatNumber>"
						onblur="checkszPcPrice('pcPrice_d${l.index}');" onkeyup="checkInput(this,'szprice',event);" autocomplete="off" />
                  <div class="price-main" id="close_form_pc_buy${l.index}" style="display: none;">
                    <div class="tb-overlay"></div>
                    <div class="price-alert">
                      <div class="arrow-left"></div>
                      <p> 您的报价不符合系统的限价规则，当前 <span>多仓最小可卖出</span>价为： <b id="price_pc_buy_1${l.index}">0</b> <br>
                        您是否确定以 <b id="price_pc_buy_2${l.index}">0</b><em class="sell">卖出</em><b
																id="amount_pc_buy${l.index}">0</b>张合约 </p>
                      <div class="calculat-sub">
                        <button type="button" class="btn sub-btn" id="submit_but_buy${l.index}"> 确定 </button>
                        <button type="button" class="btn sub-btn-return close-price-window" onclick="closePcBut(${l.index});"> 关闭 </button>
                      </div>
                    </div>
                  </div>
                  </span></td>
                <td><span class="td-child-control">
                  <input type="text" class="form-control td-input" id="pcAmount_d${l.index}" name="amount" value="<fmt:formatNumber value="${tran.holdCount }" pattern="#"></fmt:formatNumber>"
												onblur="checkPcAmount('pcAmount_d${l.index}','<fmt:formatNumber value="${tran.holdCount }" pattern="#"></fmt:formatNumber>');" onkeyup="checkInput(this, 'szamount', event);" />
                  </span></td>
                <td><span class="td-child-control pc"> <a href="javascript:void(0);" class="control-btn btn pc-btn" onclick="pc_d('${l.index}','${tran.bqCode}',${tran.mt_id});">平仓</a> <a class="zszy-btn ${tran.stopLoss eq 0 ? 'sprites-zszy-off' : 'sprites-zszy-on' } " id="alertId_${tran.mt_id}" onclick="alertForm('${tran.trade_type_id}', '${tran.mt_id}');"></a> 
                  <!--止盈止损弹窗 -->
                  <div class="zyzs-alert-window" id="trade-alert_dt"></div>
                  </span></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
        <table cellpadding="0" cellspacing="0">
          <thead>
            <tr>
              <th> 类型 </th>
              <th> 可用持仓 </th>
              <th> 冻结 </th>
              <th> 平均价 </th>
              <th> 強平价格</th>
              <th> 保证金 </th>
              <th class="relative" width="100"> 浮动盈亏 <em class="que-icon icons-que th-help fdyk-txt" id="fdyk-help"></em> </th>
              <th> 盈亏比 </th>
              <th> 佣金 </th>
              <th width="70px;"> 平仓价格 </th>
              <th width="70px;"> 平仓数量 </th>
              <th width="110px"> 操作 </th>
            </tr>
          </thead>
          <tbody id="depotsellTbody">
            <tr id="alert_tr_pc_sell" style="display: none;">
              <td colspan="12"><div class="trade-alert" style="margin-top: 0;">
                  <div class="success" id="infomsg_k"> </div>
                </div>
                <input type="hidden" value="${sellMemeberTransCount}"
											id="sellTransCount" /></td>
            </tr>
            <c:if
									test="${sellMemberTransaction == null||empty sellMemberTransaction}">
              <tr>
                <td colspan="12"> 暂无卖空持仓 </td>
              </tr>
            </c:if>
            <c:forEach items="${sellMemberTransaction}" var="tran"
									varStatus="l">
              <tr>
                <td> ${fn:toUpperCase(tran.bqCode)} <em class="sell">空</em>x${tran.multiple} </td>
                <td><fmt:formatNumber value="${tran.holdCount }" pattern="#">
                  </fmt:formatNumber>
                  张 </td>
                <td><fmt:formatNumber value="${tran.lockCount }" pattern="#">
                  </fmt:formatNumber>
                  张 </td>
                <td><fmt:formatNumber value="${tran.averagePrice }" pattern="#.####">
                  </fmt:formatNumber>
                  点 </td>
                <td><fmt:formatNumber value="${tran.deposit }" pattern="#.####">
                  </fmt:formatNumber>
                  USDT </td>
                <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }" id="floatGainLosssell${l.index}"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
                  </fmt:formatNumber></td>
                <td id="floatGainLossRatesell${l.index}"> ${tran.profitLossRatio } </td>
                <td><fmt:formatNumber value="${tran.commission }" pattern="#.######">
                  </fmt:formatNumber></td>
                <td><input type="hidden" id="sell_trade_multiple${l.index}" value="${tran.multiple}" />
                  <input type="hidden" id="sell_all_count${l.index}" value="${tran.allCount }" />
                  <input type="hidden" id="sell_hold_count${l.index}" value="${tran.holdCount }" />
                  <input type="hidden" id="sell_average_price${l.index}" value="${tran.averagePrice }" />
                  <input type="hidden" id="sell_deposit${l.index}" value="${tran.deposit }" />
                  <span class="td-child-control">
                  <input type="text" class="form-control td-input" id="pcPrice_k${l.index}" name="pcPrice_k" value="<fmt:formatNumber value="${selltop5List[fn:length(selltop5List) -1].price}" pattern="#.#"></fmt:formatNumber>" 
													onblur="checkszPcPrice('pcPrice_k${l.index}');" onkeyup="checkInput(this,'szprice',event);" autocomplete="off" />
                  <div class="price-main" id="close_form_pc_sell${l.index}" style="display: none;">
                    <div class="tb-overlay"></div>
                    <div class="price-alert">
                      <div class="arrow-left"></div>
                      <p> 您的报价不符合系统的限价规则，当前 <span>空仓最大可买入</span>价为： <b id="price_pc_sell_1${l.index}">0</b> <br>
                        您是否确定以 <b id="price_pc_sell_2${l.index}">0</b><em class="buy">买入</em><b id="amount_pc_sell${l.index}">0</b>BTC </p>
                      <div class="calculat-sub">
                        <button type="button" class="btn sub-btn" id="submit_but_sell${l.index}"> 确定 </button>
                        <button type="button" class="btn sub-btn-return close-price-window" onclick="closePcBut(${l.index});"> 关闭 </button>
                      </div>
                    </div>
                  </div>
                  </span></td>
                <td><span class="td-child-control">
                  <input type="text" class="form-control td-input" id="pcAmount_k${l.index}" name="pc_Amount_k" value="<fmt:formatNumber value="${tran.holdCount }" pattern="#"></fmt:formatNumber>"
													onblur="checkPcAmount('pcAmount_k${l.index}','<fmt:formatNumber value="${tran.holdCount }" pattern="#"></fmt:formatNumber>');" onkeyup="checkInput(this,'szamount',event);" />
                  </span></td>
                <td><span class="td-child-control pc"> <a
												href="javascript:" class="control-btn btn pc-btn"
												onclick="pc_k('${l.index}','${tran.bqCode}','${tran.holdCount}',${tran.mt_id});">平仓</a> <a
												class="zszy-btn ${tran.stopLoss eq 0 ? 'sprites-zszy-off' : 'sprites-zszy-on' } "
												id="alertId_${tran.mt_id}"
												onclick="alertForm(${tran.trade_type_id}, '${tran.mt_id}');"></a> 
                  <!--止盈止损弹窗 -->
                  <div class="zyzs-alert-window" id="trade-alert_kt"></div>
                  </span></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      <div class="trade-cont" style="display: none;">
        <table cellpadding="0" cellspacing="0">
          <thead>
            <tr>
              <th> 委托时间 </th>
              <th> 合约号 </th>
              <th> 挂单类型 </th>
              <th> 杠杆倍数 </th>
              <th> 委托价格 </th>
              <th> 委托数量 </th>
              <th> 已成交 </th>
              <th> 未成交 </th>
              <th> 保证金 </th>
              <th> 操作 </th>
            </tr>
          </thead>
          <tbody id="pendingTbody">
            <c:forEach var="pd" items="${pdList}">
              <tr>
                <td><ut:formatDate dateline="${pd.delegation_time}"></ut:formatDate></td>
                <td> ${fn:toUpperCase(pd.transaction_code)} </td>
                <td><c:choose>
                    <c:when test="${pd.direction eq 1 && pd.priority eq 1}"> <b class="buy">多单开仓</b> </c:when>
                    <c:when test="${pd.direction eq 1 && pd.priority eq 2}"> <b class="sell">空单平仓</b> </c:when>
                    <c:when test="${pd.direction eq 1 && pd.priority eq 3}"> <b class="loss-all">多单爆仓</b> </c:when>
                    <c:when test="${pd.direction eq 2 && pd.priority eq 1}"> <b class="sell">空单开仓</b> </c:when>
                    <c:when test="${pd.direction eq 2 && pd.priority eq 2}"> <b class="buy">多单平仓</b> </c:when>
                    <c:when test="${pd.direction eq 2 && pd.priority eq 3}"> <b class="loss-all">空单爆仓</b> </c:when>
                    <c:otherwise>class="multiple" </c:otherwise>
                  </c:choose></td>
                <td> ${pd.multiple} </td>
                <td> ${pd.delegation_price} </td>
                <td><fmt:formatNumber value="${pd.delegation_count}" pattern="#" />
                  张 </td>
                <td><fmt:formatNumber value="${pd.deal_count}" pattern="#" />
                  张 </td>
                <td><fmt:formatNumber value="${pd.undeal_count}" pattern="#" />
                  张 </td>
                <td><fmt:formatNumber value="${pd.undeal_count/pd.multiple}" pattern="#0.0000" />
                  USDT </td>
                <td><c:choose>
                    <c:when test="${pd.priority < 3}"> <a href="javascript:void(0);" class="btn btn-blue" onclick="cancelDealAnsyc(${pd.delegation_id},'${pd.transaction_code}');">撤单</a> </c:when>
                    <c:otherwise>已爆仓 </c:otherwise>
                  </c:choose></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
<jsp:include page="_footer.jsp"></jsp:include>
<script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script> 
<script type="text/javascript" src="<%=basePath%>common/js/trade.js?<%=rjs%>"></script> 
<script type="text/javascript" src="<%=basePath%>common/js/transSelected.js?<%=rjs%>"></script> 
<script type="text/javascript" src="<%=basePath%>common/js/animate.js?<%=rjs%>"></script> 
<script type="text/javascript">
// function tingpan(){
  
// }
    var tp = -1;
//停盘状态
	function tingpan(zt){
	   var tingpan = $("#sz-trade-main");
	   // var szbox = $(".sz-box");
	    // 如果是停盘状态，给#lt-wrap追加停盘状态代码
	    if (zt==tp) {
		    return;
	    }
	    else{
		  if(zt==1){
			  tp=1;
			  tingpan.removeClass('relative');
		    $(".sz-zhezhao,.tp-block").remove();
		    }else{
			  tp=0;
			  tingpan.addClass('relative');
		      tingpan.append('<div id="sz-zhezhao" class="sz-zhezhao"></div><div class="tp-block sprites-zs-tp"></div>');
		    }
	    }
	}

	$(function() {
		// 控制表格隔行换色
		$(".table-bg table tr:odd").css("background", "#fff");
		$(".table-bg table tr:even").css("background", "#fafafa");
		// 点击计算模态框
	        var jisuan = $("#jisuan-wrap");
	        var jszz = $("#tb-overlay-jisuan");
	        $("#jisuan").on('click', function() {
	            show(jisuan);
	            jszz.show();
	        });
	
	        $(".close-alert").on('click', function() {
	            hide(jisuan);
	            jszz.hide();
	
	        });
		 // 显示隐藏开仓待结算盈利
	    $("#toggle-em").toggle(
	      function() {
	        $("#toggle-main").show();
	        $("#toggle-em i").removeClass('icons-show').addClass('icons-hide');
	        $("#que-icon").hover(function() {
	          $("#que-help").show();
	        }, function() {
	          $("#que-help").fadeOut("slow");
	        });
	        
	      }, function() {
	        $("#toggle-main").hide();
	        $("#toggle-em i").removeClass('icons-hide').addClass('icons-show');
	        
	      });
	    
	   
	    //浮动盈亏文本解释
	    $(".fdyk-txt").hover(function() {
	      $(this).parent().append('<div class="que-help fdyk-help" id="ykxs-txt"><div class="arrow-top"></div><p>浮动盈亏：盈亏金额=价格差×持仓量×0.0001</p></div>');
	    }, function() {
	      $(this).next("div").remove();
	    });

		//停盘判断
	    // tingpan();
	    // 上证合约用户交易须知
    var szwrap = $("#sz-wrap");
    var szzz = $("#tb-overlay-sz");
    $(".close-alert").on('click', function() {
      hide(szwrap);
      szzz.hide();
    });
    // 交易须知提示
    var sz_hint_block = $("#sz-hint");
    var sz_hint_help = $("#sz-hint-help");
    sz_hint_block.mouseover(function(event) {
      sz_hint_help.show();
    });
    sz_hint_help.mouseleave(function(event) {
      sz_hint_help.fadeOut("slow");
    });
	// 盈亏系数解释文本
	$("#ykxs-icon").hover(function() {
	$("#ykxs-txt").show();
	}, function() {
	$("#ykxs-txt").fadeOut("slow");
	});
	});
	// 持仓管理tab
	$('#tab-trade span').click(function() {
	$(this).addClass("current").siblings().removeClass();
	//removeClass就是删除当前其他类；只有当前对象有addClass("current")；siblings()意思就是当前对象的同级元素，removeClass就是删除； 
	$(".trade-manage-main .trade-cont").hide().eq($('#tab-trade span').index(this)).show();
	
	reset_page("pendings",$('#transaction_code').val());
	});
	function clickSelectedIcon(type, n){
		if(type == 1){//多仓
	         $("#indexBuy").val(n);
	         $("#bqCodeBuy").val($("#bqcodebuy" + n).val());
	         $("#holdCountBuy").val($("#holdcountbuy" + n).val());
	         $("#mtIdBuy").val($("#mtidbuy" + n).val());
	         $("#baocangBuy").val($("#baocangbuy" + n).val());
		} else {//空仓
	         $("#indexSell").val(n);
	         $("#bqCodeSell").val($("#bqcodesell" + n).val());
	         $("#holdCountSell").val($("#holdcountsell" + n).val());
	         $("#mtIdSell").val($("#mtidsell" + n).val());
	         $("#baocangSell").val($("#baocangsell" + n).val());
		}
		var numBuy = Number($("#holdCountBuy").val());
		var numSell = Number($("#holdCountSell").val());
		// alert($("#holdCountBuy").val() + ", " + $("#holdCountSell").val());
		if (numBuy <= 0 || numSell <= 0) {
			return;
		}
		$("#duipingNum").val(numBuy <= numSell ? numBuy : numSell);
		
	}
	function check_pending_win_price(dir){
		if(dir == '1'){//多仓.toFixed(2)
			var avg_price = Number($('#buy_average_price').val());
			var stop_win_price = Number($('#stop_win_price').val());
			var pending_win_price = Number($('#pending_win_price').val());
			if(pending_win_price <= avg_price.toFixed(2) ){
				$('#pending_win_price').val((avg_price*1.0003).toFixed(2));
				$('#stop_win_price').val((Number($('#pending_win_price').val())*1.001).toFixed(2));
				$('#stop_win_price_span').html((Number($('#pending_win_price').val())*1.001).toFixed(2));
				return false;
			}
			$('#stop_win_price').val((Number($('#pending_win_price').val())*1.001).toFixed(2));
			$('#stop_win_price_span').html((Number($('#pending_win_price').val())*1.001).toFixed(2));
			
		}else{
			var avg_price = Number($('#sell_average_price').val());
			var stop_win_price = Number($('#stop_win_price').val());
			var pending_win_price = Number($('#pending_win_price').val());
			if(pending_win_price >= formatFloat(avg_price,2)){
				$('#pending_win_price').val(formatFloat(avg_price*0.9997,2));
				$('#stop_win_price').val((Number($('#pending_win_price').val())*0.999).toFixed(2));
				$('#stop_win_price_span').html((Number($('#pending_win_price').val())*0.999).toFixed(2));
				return false;
			}
			$('#stop_win_price').val((Number($('#pending_win_price').val())*0.999).toFixed(2));
			$('#stop_win_price_span').html((Number($('#pending_win_price').val())*0.999).toFixed(2));
		}
	}

	function check_pending_loss_price(dir){
		if(dir == '1'){//多仓.toFixed(2)
			var trade_multiple =  Number($('#buy_trade_multiple').val());
			var avg_price = Number($('#buy_average_price').val());
			var deposit = Number($('#buy_deposit').val());
			var all_count = Number($('#buy_all_count').val());
			var stop_loss_price = Number($('#stop_loss_price').val());
			var pending_loss_price = Number($('#pending_loss_price').val());
			/*要求止损价小于均价*/
			if( formatFloat(pending_loss_price*1.001,2) >= formatFloat(avg_price,2)){
				$('#pending_loss_price').val(Math.min(formatFloat(avg_price*0.9997 -0.01,2),formatFloat(avg_price/1.001-0.01,2)));
				$('#stop_loss_price').val((Number($('#pending_loss_price').val())*1.001).toFixed(2));
				$('#stop_loss_price_span').html((Number($('#pending_loss_price').val())*1.001).toFixed(2));
				return false;
			}
			/*要求止损价大于爆仓价*/
			if( pending_loss_price < formatFloat(avg_price*(1-deposit/all_count)*1.0003,2)){
				$('#pending_loss_price').val(formatFloat(avg_price*(1-deposit/all_count)*1.0003 +0.01,2));
				$('#stop_loss_price').val((Number($('#pending_loss_price').val())*1.001).toFixed(2));
				$('#stop_loss_price_span').html((Number($('#pending_loss_price').val())*1.001).toFixed(2));
				return false;
			}
			$('#stop_loss_price').val((Number($('#pending_loss_price').val())*1.001).toFixed(2));
			$('#stop_loss_price_span').html((Number($('#pending_loss_price').val())*1.001).toFixed(2));
		}else{
			var trade_multiple =  Number($('#sell_trade_multiple').val());
			var avg_price = Number($('#sell_average_price').val());
			var deposit = Number($('#sell_deposit').val());
			var all_count = Number($('#sell_all_count').val());
			var stop_loss_price = Number($('#stop_loss_price').val());
			var pending_loss_price = Number($('#pending_loss_price').val());
			/*要求止损价大于均价*/
			if(formatFloat(pending_loss_price/1.001,2) <= avg_price.toFixed(2)){
				$('#pending_loss_price').val(Math.max((avg_price*1.001+0.01).toFixed(2),(avg_price*1.0003+0.01).toFixed(2)));
				$('#stop_loss_price').val((Number($('#pending_loss_price').val())/1.001).toFixed(2));
				$('#stop_loss_price_span').html((Number($('#pending_loss_price').val())/1.001).toFixed(2));
				return false;
			}
			/*要求止损价小于爆仓价*/
			if(pending_loss_price > formatFloat(avg_price*(1+deposit/all_count)*0.9997,2)){
				$('#pending_loss_price').val(formatFloat(avg_price*(1+deposit/all_count)*0.9997 -0.01,2));
				$('#stop_loss_price').val((Number($('#pending_loss_price').val())*0.999).toFixed(2));
				$('#stop_loss_price_span').html((Number($('#pending_loss_price').val())*0.999).toFixed(2));
				return false;
			}
			$('#stop_loss_price').val((Number($('#pending_loss_price').val())*0.999).toFixed(2));
			$('#stop_loss_price_span').html((Number($('#pending_loss_price').val())*0.999).toFixed(2));
		}
	}
	
	
	function alertForm(dir, tid){
		if (dir == '1') {
			var buy_hold_count =  Number($('#buy_hold_count').val());
			if(buy_hold_count <=0){
				return false;
			}
		}else {
			var sell_hold_count =  Number($('#sell_hold_count').val());
			if(sell_hold_count <=0){
				return false;
			}
			
		}
		var dataText = "";
		$.ajax({
			cache: false,
	        async: false,   
	        dataType: 'JSON',
	        type: 'POST',
	        url: basePath +"tradeAjax/stopLossClick.do?t=" + new Date().getTime(),
			data : {transaction_member_id : tid},
			success: function (data) {
				var msg1 = '<div id="tb-overlay" class="tb-overlay"></div><div class="alert-window"><div class="arrow-top"></div><form action="" method="post"><div class="control-form bd-bt"><div class="control-box"><div class="checkbox">';
				var msg2 = '止盈</div><p><em>当最新成交价达到';
				var msg3 = '(￥)时</em> <em>我将以';
				var msg4 = '(￥)的单价挂单进行平仓。</em> <span id="win_error" class="error"></span></p></div></div><div class="control-form"><div class="control-box"><div class="checkbox">';
				var msg5 = '止损</div><p><em>当最新成交价达到';
				var msg6 = '(￥)时</em> <em>我将以';
				var msg7 = '(￥)的单价挂单进行平仓。</em> <span id="loss_error" class="error"></span></p></div><div class="talk">说明：止盈止损功能是为了降低系统风险，系统只负责挂价，不保证价格挂出后一定能成交。<b class="sell">（本功能只对当前可用持仓有效）</b></div></div><div class="btn-sub">'
					+ '<button type="button" onclick="stopLossBtn('+dir+','+tid+');" value="" class="btn sub-btn true">确定</button> <button type="button" onclick="closeForm();"  class="btn sub-btn-return close close-windows">关闭</button></div></form></div>';
				
				var hasWin = '';
				var stopWinprice = '';
				var pendingWinprice = '';
				var hasLoss = '';
				var stopLossprice = '';
				var pendingLossprice = '';
				
				if(data == null || data.length == 0){ // 既无止赢又无止损
					
					 hasWin = '<input type="checkbox" class="ipt" value="0" id="has_win">';
					 stopWinprice = '<input type="hidden" id="stop_win_price" value="0"  readonly="true" onkeyup="checkInput(this,\'bp\',event);"> <b id="stop_win_price_span"></b>';
					 pendingWinprice = '<input type="text" id="pending_win_price" class="form-control" value="0" onblur="check_pending_win_price('+dir+');" onkeyup="checkInput(this,\'bp\',event);">';
					 hasLoss = '<input type="checkbox" class="ipt" value="0" id="has_loss">';
					 stopLossprice = '<input type="hidden" id="stop_loss_price" value="0"  readonly="true"  onkeyup="checkInput(this,\'bp\',event);"> <b id="stop_loss_price_span"></b>';
					 pendingLossprice = '<input type="text" id="pending_loss_price" class="form-control" value="0" onblur="check_pending_loss_price('+dir+');" onkeyup="checkInput(this,\'bp\',event);">';
					
				} else if(data.length == 1){ // 无止赢有止损或无止损有止赢
					
					var type = data[0].stop_loss_type_id; // 类型（1止盈 2止损）
					var stop_loss_price_zy = 0, stop_loss_price_zs = 0;
					var pending_trans_price_zy = 0, pending_trans_price_zs = 0;
					if (type == 1) { // 止盈
						stop_loss_price_zy = data[0].stop_loss_price;
						pending_trans_price_zy = data[0].pending_trans_price;
						
						hasWin = '<input type="checkbox" class="ipt" checked value="1" id="has_win">';
						hasLoss = '<input type="checkbox" class="ipt" value="0" id="has_loss">';
					} else if (type == 2) { // 止损
						stop_loss_price_zs = data[0].stop_loss_price;
						pending_trans_price_zs = data[0].pending_trans_price;
						
						hasWin = '<input type="checkbox" class="ipt" value="0" id="has_win">';
						hasLoss = '<input type="checkbox" class="ipt" checked value="1" id="has_loss">';
					}
	
					 stopWinprice = '<input type="hidden" id="stop_win_price" value="' + stop_loss_price_zy + '" readonly="true" onkeyup="checkInput(this,\'bp\',event);"><b id="stop_win_price_span">'+stop_loss_price_zy+'</b>';
					 pendingWinprice = '<input type="text" id="pending_win_price" class="form-control" value="' + pending_trans_price_zy + '" onblur="check_pending_win_price('+dir+');" onkeyup="checkInput(this,\'bp\',event);">';
					 stopLossprice = '<input type="hidden" id="stop_loss_price" value="' + stop_loss_price_zs + '" readonly="true" onkeyup="checkInput(this,\'bp\',event);"><b id="stop_loss_price_span">'+stop_loss_price_zs+'</b>';
					 pendingLossprice = '<input type="text" id="pending_loss_price" class="form-control" value="' + pending_trans_price_zs + '"  onblur="check_pending_loss_price('+dir+');"  onkeyup="checkInput(this,\'bp\',event);">';
					
				} else if(data.length == 2){ // 既有止赢又有止损
					var type = data[0].stop_loss_type_id; // 类型（1止盈 2止损）
					var stop_loss_price_zy = 0, stop_loss_price_zs = 0;
					var pending_trans_price_zy = 0, pending_trans_price_zs = 0;
					if (type == 1) { // 止盈
						stop_loss_price_zy = data[0].stop_loss_price;
						stop_loss_price_zs = data[1].stop_loss_price;
						pending_trans_price_zy = data[0].pending_trans_price;
						pending_trans_price_zs = data[1].pending_trans_price;
					} else if (type == 2) { // 止损
						stop_loss_price_zy = data[1].stop_loss_price;
						stop_loss_price_zs = data[0].stop_loss_price;
						pending_trans_price_zy = data[1].pending_trans_price;
						pending_trans_price_zs = data[0].pending_trans_price;
					}
	
					hasWin = '<input type="checkbox" class="ipt" checked value="1" id="has_win">';
					hasLoss = '<input type="checkbox" class="ipt" checked value="1" id="has_loss">';
					
					stopWinprice = '<input type="hidden" id="stop_win_price" value="' + stop_loss_price_zy + '" readonly="true" onkeyup="checkInput(this,\'bp\',event);"><b id="stop_win_price_span">'+stop_loss_price_zy+'</b>';
					pendingWinprice = '<input type="hidden" id="pending_win_price" value="' + pending_trans_price_zy + '" onblur="check_pending_win_price('+dir+');" onkeyup="checkInput(this,\'bp\',event);">';
					stopLossprice = '<input type="text" id="stop_loss_price" value="' + stop_loss_price_zs + '" readonly="true" onkeyup="checkInput(this,\'bp\',event);"><b id="stop_loss_price_span">'+stop_loss_price_zs+'</b>';
					pendingLossprice = '<input type="text" id="pending_loss_price" class="form-control" value="' + pending_trans_price_zs + '"  onblur="check_pending_loss_price('+dir+');" onkeyup="checkInput(this,\'bp\',event);">';
					
				}
				dataText = msg1 + hasWin + msg2 + stopWinprice + msg3 + pendingWinprice + msg4 + hasLoss + msg5 + stopLossprice + msg6 + pendingLossprice + msg7;
			}
		});
		if (dir == '1') {
			$("#trade-alert_dt").html(dataText);
		}else {
			$("#trade-alert_kt").html(dataText);
		}
		
	}
	
	function closeForm(){
		$(".zyzs-alert-window ").empty();
	}
	
	function stopLossBtn(dir,tid){
		var hasWin = "0";
		var hasLoss = "0";
		
		if ($("#has_win").attr("checked")) {
			hasWin = 1;
			var stop_win_price = Number($('#stop_win_price').val());
			var pending_win_price = Number($('#pending_win_price').val());
			if(stop_win_price == 0 || pending_win_price == 0){
				$('#win_error').html("请填写止盈价");
				return false;
			}
	
			if(dir == '1'){//多仓止盈
				var avg_price = Number($('#buy_average_price').val());
				var stop_win_price = Number($('#stop_win_price').val());
				var pending_win_price = Number($('#pending_win_price').val());
				if(pending_win_price <= formatFloat(avg_price,2) ){
					$('#pending_win_price').val(formatFloat(avg_price*1.0003,2));
					$('#stop_win_price').val(formatFloat(Number($('#pending_win_price').val())*1.001,2));
					$('#stop_win_price_span').html(formatFloat(Number($('#pending_win_price').val())*1.001,2));
					$('#win_error').html("多仓止盈价格须大于持仓均价");
					return false;
				}
				$('#stop_win_price').val(formatFloat(Number($('#pending_win_price').val())*1.001,2));
				$('#stop_win_price_span').html(formatFloat(Number($('#pending_win_price').val())*1.001,2));
				
			}else{//空仓止盈
				var avg_price = Number($('#sell_average_price').val());
				var stop_win_price = Number($('#stop_win_price').val());
				var pending_win_price = Number($('#pending_win_price').val());
				if(pending_win_price >= formatFloat(avg_price,2)){
					$('#pending_win_price').val(formatFloat(avg_price*0.9997,2));
					$('#stop_win_price').val(formatFloat(Number($('#pending_win_price').val())*0.999,2));
					$('#stop_win_price_span').html(formatFloat(Number($('#pending_win_price').val())*0.999,2));
					$('#win_error').html("空仓止盈价格须小于持仓均价");
					return false;
				}
				$('#stop_win_price').val(formatFloat(Number($('#pending_win_price').val())*0.999,2));
				$('#stop_win_price_span').html(formatFloat(Number($('#pending_win_price').val())*0.999,2));
			}
		}
		$('#win_error').html("");
		
		if ($("#has_loss").attr("checked")) {
			hasLoss = 1;
			var stop_loss_price = Number($('#stop_loss_price').val());
			var pending_loss_price = Number($('#pending_loss_price').val());
			if(stop_loss_price == 0 || pending_loss_price == 0){
				$('#loss_error').html("请填写止损价");
				return false;
			}
	
			if(dir == '1'){//多仓止损
				var trade_multiple =  Number($('#buy_trade_multiple').val());
				var avg_price = Number($('#buy_average_price').val());
				var deposit = Number($('#buy_deposit').val());
				var all_count = Number($('#buy_all_count').val());
				var stop_loss_price = Number($('#stop_loss_price').val());
				var pending_loss_price = Number($('#pending_loss_price').val());
				/*要求止损价小于均价*/
				if( formatFloat(pending_loss_price*1.001,2) >= formatFloat(avg_price,2)){
					$('#pending_loss_price').val(Math.min(formatFloat(avg_price*0.9997 -0.01,2),formatFloat(avg_price/1.001-0.01,2)));
					$('#stop_loss_price').val((Number($('#pending_loss_price').val())*1.001).toFixed(2));
					$('#stop_loss_price_span').html((Number($('#pending_loss_price').val())*1.001).toFixed(2));
					$('#loss_error').html("多仓止损价须小于均价");
					return false;
				}
				/*要求止损价大于爆仓价*/
				if( pending_loss_price < formatFloat(avg_price*(1-deposit/all_count)*1.0003,2)){
					$('#pending_loss_price').val(formatFloat(avg_price*(1-deposit/all_count)*1.0003 +0.01,2));
					$('#stop_loss_price').val(formatFloat(Number($('#pending_loss_price').val())*1.001,2));
					$('#stop_loss_price_span').html(formatFloat(Number($('#pending_loss_price').val())*1.001,2));
					$('#loss_error').html("多仓止损价须大于爆仓价");
					return false;
				}
				$('#stop_loss_price').val(formatFloat(Number($('#pending_loss_price').val())*1.001,2));
				$('#stop_loss_price_span').html(formatFloat(Number($('#pending_loss_price').val())*1.001,2));
			}else{//空仓止损
				var trade_multiple =  Number($('#sell_trade_multiple').val());
				var avg_price = Number($('#sell_average_price').val());
				var deposit = Number($('#sell_deposit').val());
				var all_count = Number($('#sell_all_count').val());
				var stop_loss_price = Number($('#stop_loss_price').val());
				var pending_loss_price = Number($('#pending_loss_price').val());
				/*要求止损价大于均价*/
				if(formatFloat(pending_loss_price/1.001,2) <= avg_price.toFixed(2)){
					$('#pending_loss_price').val(Math.max((avg_price*1.001+0.01).toFixed(2),(avg_price*1.0003+0.01).toFixed(2)));
					$('#stop_loss_price').val((Number($('#pending_loss_price').val())/1.001).toFixed(2));
					$('#stop_loss_price_span').html((Number($('#pending_loss_price').val())/1.001).toFixed(2));
					$('#loss_error').html("空仓止损价须大于均价");
					return false;
				}
				/*要求止损价小于爆仓价*/
				if(pending_loss_price >= formatFloat(avg_price*(1+deposit/all_count)*0.9997,2)){
					$('#pending_loss_price').val(formatFloat(avg_price*(1+deposit/all_count)*0.9997 -0.01,2));
					$('#stop_loss_price').val(formatFloat(Number($('#pending_loss_price').val())*0.999,2));
					$('#stop_loss_price_span').html(formatFloat(Number($('#pending_loss_price').val())*0.999,2));
					$('#loss_error').html("空仓止损价须小于爆仓价");
					return false;
				}
				$('#stop_loss_price').val(formatFloat(Number($('#pending_loss_price').val())*0.999,2));
				$('#stop_loss_price_span').html(formatFloat(Number($('#pending_loss_price').val())*0.999,2));
			}
		}
		$('#loss_error').html("");
	
		
		
		$.ajax({
			cache: false,
	        async: false, 
	        dataType: 'JSON',
	        type: 'POST',
	        url: basePath +"tradeAjax/stopLossChange.do?t=" + new Date().getTime(),
			data : {transaction_member_id : tid, has_win : hasWin, has_loss : hasLoss,
	        	stop_win_price : $("#stop_win_price").val(), pending_win_price : $("#pending_win_price").val(),
	        	stop_loss_price : $("#stop_loss_price").val(), pending_loss_price : $("#pending_loss_price").val(),token : getCookie("_token")
				 },
			success: function (data) {
	
				 if(data.code=="success"){
					$("#alertId_"+tid).removeClass("sprites-zszy-off");
					$("#alertId_"+tid).addClass("sprites-zszy-on");
					closeForm();
				}else if(data.code=="success2"){
					$("#alertId_"+tid).removeClass("sprites-zszy-on");
					$("#alertId_"+tid).addClass("sprites-zszy-off");
					closeForm();
				}else if(data.code=="error"){
					$('#loss_error').html(""+data.msg);
				}
			}
		});
	}
	</script>
 
</body>
</html>
