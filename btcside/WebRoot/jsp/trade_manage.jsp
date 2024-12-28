<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
  <div class="main-wrap" style="overflow: visible;">
    <div class="block-title-trade">
      <div class="tab tab-trade lt" id="tab-trade">
      	 <span class="current" onclick="refreshMemberTrans(); changeTab(0);"><fmt:message key="msg.pg.trade.holdManage"/></span> 
      	 <span onclick="refreshPending(); changeTab(1);"><fmt:message key="msg.pg.trade.mandateManage"/></span> 
      	 <span onclick="refreshTradeLog(); changeTab(2);"><fmt:message key="msg.trade.record"/></span> 
      </div>
      <div class="trade-control relative">
        <c:if test="${isMarket == null }">
        	<c:if test="${buyMemeberTransCount > 0 && sellMemeberTransCount > 0}">
        	<div class="lt" id="duipingDiv"><a href="javascript:" id="duiping-btn" class="buy"><fmt:message key="msg.pg.trade.alignMent"/></a></div>
        	</c:if>
        	<c:if test="${buyMemeberTransCount == 0 || sellMemeberTransCount == 0}">
        	<div class="lt" id="duipingDiv" style="display:none"><a href="javascript:" id="duiping-btn" class="buy"><fmt:message key="msg.pg.trade.alignMent"/></a></div>
        	</c:if>
        </c:if>
      </div>
      <div class="trade-control relative" style="display: none;">
        <div class="lt"><a href="javascript:;" onclick="cancelDeal('1','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.buyBtn"/></a> </div>
        <div class="lt"><a href="javascript:;" onclick="cancelDeal('2','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.sellBtn"/></a> </div>
        <div class="lt"><a href="javascript:;" onclick="cancelDeal('0','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.alldraw"/></a> </div>
      </div>
      <div class="trade-control relative" style="display: none;">
      	<a href="tradeQuery.do?pendingcode=${currentTransaction.transaction_code}"><fmt:message key="msg.normal.more"/></a>	
      </div>
      <div class="clear"></div>
    </div>
    <div class="clear"></div>
    <div class="trade-manage-main table-bg">
      <div class="trade-cont">
        <table style="margin-bottom:25px;">
          <thead>
            <tr>
              <th style="width: 70px;"><fmt:message key="msg.pg.trade.genre"/></th>
              <th><fmt:message key="msg.pg.trade.aviablePosition"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
              <c:if test="${isMarket == null }">
              <th><fmt:message key="msg.pg.trade.freezen"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
              </c:if>
              <th><fmt:message key="msg.pg.trade.averagePrice"/></th>
              <th class="relative" style="width:80px;"><fmt:message key="msg.pg.trade.triggerPrice"/>
              <em class="que-icon icons-que th-help bcj-txt" id="bcj-help"></em>
              </th>
              <c:if test="${isMarket == null }">
              <th><fmt:message key="msg.pg.trade.depositCash"/></th>
              </c:if>
              <th class="relative" style="width:100px"><fmt:message key="msg.pg.trade.floatProfit"/>
              <em class="que-icon icons-que th-help fdyk-txt" id="dfdyk-help"></em>
              </th>
              <c:if test="${isMarket == null }">
              <th><fmt:message key="msg.pg.trade.profitRatio"/></th>
               </c:if>
              <th style="width:70px;"><fmt:message key="msg.pg.trade.exitPrice"/></th>
              <th style="width:70px;"><fmt:message key="msg.pg.trade.numberPositions"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
              <c:if test="${isMarket == null }">
              <th style="width:110px"><fmt:message key="msg.pg.trade.option"/></th>
              </c:if>
              <c:if test="${isMarket != null }">
              <th style="width:60px"><fmt:message key="msg.pg.trade.option"/></th>
              </c:if>
            </tr>
          </thead>
          <tbody id="depotbuyTbody">
            <tr id="alert_tr_pc_buy" style="display: none;">
              <td colspan="12"><div class="trade-alert" style="margin-top: 0;">
                  <div class="success" id="infomsg_d"></div>
                </div>
                <input type="hidden" value="${buyMemeberTransCount}" id="buyTransCount"/></td>
            </tr>
            <c:if test="${buyMemberTransaction == null||empty buyMemberTransaction}">
              <tr>
	            <c:if test="${sessionScope.userinfo==null}">
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
	            </c:if>
            	<c:if test="${sessionScope.userinfo!=null}">
            		<td colspan="12"> <fmt:message key="msg.pg.trade.guessBuy"/> </td>
            	</c:if>
              </tr>
            </c:if>
            <c:forEach items="${buyMemberTransaction}" var="tran" varStatus="l">
              <tr>
                <td><em class="buy"><fmt:message key="msg.pg.trade.much"/> </em> x${tran.multiple} </td>
                <td id="buyHoldCount${l.index}"><fmt:formatNumber value="${tran.holdCount}"></fmt:formatNumber></td>
                <c:if test="${isMarket == null }">
                	<td id="buyLockCount${l.index}"><fmt:formatNumber value="${tran.lockCount}"></fmt:formatNumber></td>
                </c:if>
                <td id="buyAvgPrice${l.index}"><fmt:formatNumber value="${tran.averagePrice}"></fmt:formatNumber></td>
                <td id="buyQpPrice${l.index}"><fmt:formatNumber value="${tran.qpPrice}"></fmt:formatNumber></td>
                <c:if test="${isMarket == null }">
                <td id="buyDeposit${l.index}"><fmt:formatNumber value="${tran.deposit}" pattern="#.####"></fmt:formatNumber></td>
                </c:if>
                <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }" id="floatGainLossbuy${l.index}"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
                  </fmt:formatNumber></td>
                 <c:if test="${isMarket == null }">
                <td id="floatGainLossRatebuy${l.index}"> ${tran.profitLossRatio } </td>
                 </c:if>
                <td><input type="hidden" id="buy_trade_multiple${l.index}" value="${tran.multiple}" />
                  <input type="hidden" id="buy_all_count${l.index}" value="${tran.allCount }" />
                  <input type="hidden" id="buy_hold_count${l.index}" value="${tran.holdCount }" />
                  <input type="hidden" id="buy_average_price${l.index}" value="${tran.averagePrice }"/>
                  <input type="hidden" id="buy_deposit${l.index}" value="${tran.deposit }"/>
                  <input type="hidden" id="buy_qpj${l.index}" value="${tran.qpPrice }"/>
                  <span class="td-child-control" >
                  <input type="text" class="form-control price-input td-input" id="pcPrice_d${l.index}" name="price" 
                  	value="${buytop5List[0].price}" onkeyup="checkInputDigits(this, ${digits});" autocomplete="off"/>
                  <div class="price-main" id="close_form_pc_buy${l.index}" style="display: none;">
                    <div class="tb-overlay"></div>
                    <div class="price-alert" >
                      <div class="arrow-left"></div>
                      <p> 您的报价不符合系统的限价规则，当前 <span>多仓最小可卖出</span>价为： <b id="price_pc_buy_1${l.index}">0</b> <br>
                      	  您是否确定以 <b id="price_pc_buy_2${l.index}">0</b><em class="sell">卖出</em><b id="amount_pc_buy${l.index}">0</b>${currentTransaction.transaction_code.toUpperCase()}  </p>
                      <div class="calculat-sub">
                        <button type="button" class="btn sub-btn" id="submit_but_buy${l.index}">确定</button>
                        <button type="button" class="btn sub-btn-return close-price-window" onclick="closePcBut(${l.index});">关闭</button>
                      </div>
                    </div>
                  </div>
                  </span></td>
                <td><span class="td-child-control">
                  <input type="text" class="form-control td-input" id="pcAmount_d${l.index}" name="amount" value="${tran.holdCount}" 
                  	onblur="checkPcAmount('pcAmount_d${l.index}','${tran.holdCount}');" onkeyup="checkInputDigits(this, ${amountDigits});"/>
					</span></td>
                <td>
                <c:if test="${isMarket == null }">
                	<span class="td-child-control pc">
                  		<a href="javascript:void(0);" class="control-btn btn pc-btn" onclick="pc_d('${l.index}','${tran.bqCode}',${tran.mt_id});" 
                  		<c:if test="${currentTransaction.suspend}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.closePosition"/></a> 
                		<a class="zszy-btn ${tran.stopLoss eq 0 ? 'sprites-zszy-off' : 'sprites-zszy-on' } " 
                			id="alertId_${tran.mt_id}" onclick="alertForm(${l.index}, ${tran.trade_type_id}, ${tran.mt_id});"></a> 
                  		<!--止盈止损弹窗 -->
                  		<span class="zyzs-alert-window" id="trade-alert_dt${l.index}"></span>
                  	</span>
                </c:if>
                	
                  <c:if test="${isMarket != null }">
                  	<span class="td-child-control">
                  		<a href="javascript:void(0);" class="control-btn btn pc-btn" onclick="pc_d('${l.index}','${tran.bqCode}',${tran.mt_id});" 
                  		<c:if test="${currentTransaction.suspend}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.closePosition"/></a> 
                  	</span>
                  </c:if> 
                  </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
        <table>
          <thead>
            <tr>
              <th style="width: 70px;"><fmt:message key="msg.pg.trade.genre"/></th>
              <th><fmt:message key="msg.pg.trade.aviablePosition"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
              <c:if test="${isMarket == null }">
              <th><fmt:message key="msg.pg.trade.freezen"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
              </c:if>
              <th><fmt:message key="msg.pg.trade.averagePrice"/></th>
              <th class="relative" style="width:80px"><fmt:message key="msg.pg.trade.triggerPrice"/>
              <em class="que-icon icons-que th-help bcj-txt" id="bcj-help"></em>
              </th>
              <c:if test="${isMarket == null }">
              <th><fmt:message key="msg.pg.trade.depositCash"/></th>
              </c:if>
              <th class="relative" style="width:100px"><fmt:message key="msg.pg.trade.floatProfit"/>
              <em class="que-icon icons-que th-help fdyk-txt" id="kfdyk-help"></em>
              </th>
              <c:if test="${isMarket == null }">
              <th><fmt:message key="msg.pg.trade.profitRatio"/></th>
              </c:if>
              <th style="width:70px;"><fmt:message key="msg.pg.trade.exitPrice"/></th>
              <th style="width:70px;"><fmt:message key="msg.pg.trade.numberPositions"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
              <c:if test="${isMarket == null }">
              <th style="width:110px"><fmt:message key="msg.pg.trade.option"/></th>
              </c:if>
              <c:if test="${isMarket != null }">
              <th style="width:60px"><fmt:message key="msg.pg.trade.option"/></th>
              </c:if>
            </tr>
          </thead>
          <tbody id="depotsellTbody">
            <tr id="alert_tr_pc_sell" style="display: none;">
              <td colspan="12"><div class="trade-alert" style="margin-top:0;">
                  <div class="success" id="infomsg_k"> </div>
                </div>
                <input type="hidden" value="${sellMemeberTransCount}" id="sellTransCount"/></td>
            </tr>
            <c:if test="${sellMemberTransaction == null||empty sellMemberTransaction}">
              <tr>
              	<c:if test="${sessionScope.userinfo==null}">
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
	            </c:if>
            	<c:if test="${sessionScope.userinfo!=null}">
                	<td colspan="12"> <fmt:message key="msg.pg.trade.guessSell"/> </td>
                </c:if>
              </tr>
            </c:if>
            <c:forEach items="${sellMemberTransaction}" var="tran" varStatus="l">
              <tr>
                <td><em class="sell"><fmt:message key="msg.pg.trade.guessEmpty"/> </em> x${tran.multiple} </td>
                <td id="sellHoldCount${l.index}"><fmt:formatNumber value="${tran.holdCount }">
                  </fmt:formatNumber></td>
                  <c:if test="${isMarket == null }">
                <td id="sellLockCount${l.index}"><fmt:formatNumber value="${tran.lockCount }">
                  </fmt:formatNumber></td>
                  </c:if>
                <td id="sellAvgPrice${l.index}"><fmt:formatNumber value="${tran.averagePrice }">
                  </fmt:formatNumber></td>
                <td id="sellQpPrice${l.index}"><fmt:formatNumber value="${tran.qpPrice }">
                  </fmt:formatNumber></td>
                  <c:if test="${isMarket == null }">
                <td id="sellDeposit${l.index}"><fmt:formatNumber value="${tran.deposit }" pattern="#.####">
                  </fmt:formatNumber></td>
                  </c:if>
                <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }" id="floatGainLosssell${l.index}"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
                  </fmt:formatNumber></td>
                  <c:if test="${isMarket == null }">
                <td id="floatGainLossRatesell${l.index}"> ${tran.profitLossRatio } </td>
                  </c:if>
                <td><input type="hidden" id="sell_trade_multiple${l.index}" value="${tran.multiple}" />
                  <input type="hidden" id="sell_all_count${l.index}" value="${tran.allCount }" />
                  <input type="hidden" id="sell_hold_count${l.index}" value="${tran.holdCount }" />
                  <input type="hidden" id="sell_average_price${l.index}" value="${tran.averagePrice }"/>
                  <input type="hidden" id="sell_deposit${l.index}" value="${tran.deposit }"/>
                  <input type="hidden" id="sell_qpj${l.index}" value="${tran.qpPrice }"/>
                  <span class="td-child-control" ><!-- onblur="checkPcPrice(2);" -->
                  <input type="text" class="form-control td-input" id="pcPrice_k${l.index}" name="pcPrice_k" value="${selltop5List[fn:length(selltop5List) -1].price}"
                  	onkeyup="checkInputDigits(this, ${digits});" autocomplete="off"/>
                  <div class="price-main" id="close_form_pc_sell${l.index}" style="display: none;">
                    <div class="tb-overlay"></div>
                    <div class="price-alert" >
                      <div class="arrow-left"></div>
                      <p> 您的报价不符合系统的限价规则，当前 <span>空仓最大可买入</span>价为： <b id="price_pc_sell_1${l.index}">0</b> <br>
                      	  您是否确定以 <b id="price_pc_sell_2${l.index}">0</b><em class="buy">买入</em><b id="amount_pc_sell${l.index}">0</b>${currentTransaction.transaction_code.toUpperCase()}  </p>
                      <div class="calculat-sub">
                        <button type="button" class="btn sub-btn" id="submit_but_sell${l.index}">确定</button>
                        <button type="button" class="btn sub-btn-return close-price-window" onclick="closePcBut(${l.index});">关闭</button>
                      </div>
                    </div>
                  </div>
                  </span></td>
                <td><span class="td-child-control">
                  <input type="text" class="form-control td-input" id="pcAmount_k${l.index}" name="pc_Amount_k" value="${tran.holdCount}" 
                  	onblur="checkPcAmount('pcAmount_k${l.index}','${tran.holdCount}');" onkeyup="checkInputDigits(this,${amountDigits});" />
                  </span></td>
                <td>
                <c:if test="${isMarket == null }">
                	<span class="td-child-control pc">
	                	<a href="javascript:" class="control-btn btn pc-btn" onclick="pc_k('${l.index}','${tran.bqCode}',${tran.mt_id});" 
	                	<c:if test="${currentTransaction.suspend}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.closePosition"/></a> 
	                	<a class="zszy-btn ${tran.stopLoss eq 0 ? 'sprites-zszy-off' : 'sprites-zszy-on' } " id="alertId_${tran.mt_id}" onclick="alertForm(${l.index}, ${tran.trade_type_id}, ${tran.mt_id});"></a> 
	                  	<!--止盈止损弹窗 -->
	                  	<span class="zyzs-alert-window" id="trade-alert_kt${l.index}"></span>
                  	</span>
                </c:if>
                <c:if test="${isMarket != null }">
                	<span class="td-child-control">
	                	<a href="javascript:" class="control-btn btn pc-btn" onclick="pc_k('${l.index}','${tran.bqCode}',${tran.mt_id});" 
	                	<c:if test="${currentTransaction.suspend}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.closePosition"/></a> 
	                </span>
                </c:if>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      
      <div class="trade-cont" id="trade-matter" style="display: none;">
        <table>
          <thead>
            <tr>
              <th> <fmt:message key="msg.pg.trade.entrueTime"/></th> 
              <c:if test="${isMarket == null }">
              <th> <fmt:message key="msg.pg.trade.contractNO"/> </th> 
              </c:if>
              <th> <fmt:message key="msg.pg.trade.typeOfList"/> </th> 
              <th> <fmt:message key="msg.pg.trade.leverage"/> </th> 
              <th> <fmt:message key="msg.pg.trade.entruePrice"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})</th>
              <th> <fmt:message key="msg.pg.trade.entrueNO"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> 
              <th> <fmt:message key="msg.pg.trade.doneDeal"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> 
              <th> <fmt:message key="msg.pg.trade.unsettled"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th> 
              <th> <fmt:message key="msg.pg.trade.depositCash"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'}) </th> 
              <th style="width:60px"> <fmt:message key="msg.pg.trade.option"/> </th> 
            </tr>
          </thead>
          <tbody id="pendingTbody">
           	<c:if test="${pdList == null||empty pdList}">
              <tr>
          		<c:if test="${sessionScope.userinfo==null}">
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
	            </c:if>
	          </tr>
	        </c:if>
	            
            <c:forEach var="pd" items="${pdList}" >
              <tr>
                <td><jsp:useBean id="dateValue" class="java.util.Date"/>
                	<jsp:setProperty name="dateValue" property="time" value="${pd.delegation_time * 1000}"/>
                	<fmt:formatDate value="${dateValue}" pattern="MM.dd HH:mm"/></td>
                <c:if test="${isMarket == null }">
                <td> ${fn:toUpperCase(pd.transaction_code)} </td>
                </c:if>
                <td><c:choose>
                    <c:when test="${pd.direction eq 1 && pd.priority eq 1}"><b class="buy"><fmt:message key="msg.pg.trade.manyOpen"/></b></c:when>
                    <c:when test="${pd.direction eq 1 && pd.priority eq 2}"><b class="sell"><fmt:message key="msg.pg.trade.emptyLiqu"/></b></c:when>
                    <c:when test="${pd.direction eq 1 && pd.priority eq 3}"><b class="loss-all"><fmt:message key="msg.pg.trade.manyBurst"/></b></c:when>
                    <c:when test="${pd.direction eq 2 && pd.priority eq 1}"><b class="sell"><fmt:message key="msg.pg.trade.emptyStart"/></b></c:when>
                    <c:when test="${pd.direction eq 2 && pd.priority eq 2}"><b class="buy"><fmt:message key="msg.pg.trade.manyLiqu"/></b></c:when>
                    <c:when test="${pd.direction eq 2 && pd.priority eq 3}"><b class="loss-all"><fmt:message key="msg.pg.trade.emptyBurst"/></b></c:when>
                    <c:otherwise>class="multiple" </c:otherwise>
                  </c:choose></td>
                <td> ${pd.multiple} </td>
                <td> ${pd.delegation_price.setScale(digits, 1)} </td>
                <td><fmt:formatNumber value="${pd.delegation_count}" pattern="#0.0000"/></td>
                <td><fmt:formatNumber value="${pd.deal_count}" pattern="#0.0000"/></td>
                <td><fmt:formatNumber value="${pd.undeal_count}" pattern="#0.0000"/></td>
                <td><fmt:formatNumber value="${pd.deposit}" pattern="#0.0000"/></td>
                <td><c:choose>
                    <c:when test="${pd.priority < 3}"><a href="javascript:void(0);"  class="btn btn-blue" 
                    	onclick="cancelDealAnsyc(${pd.delegation_id},'${pd.transaction_code}');"><fmt:message key="msg.pg.trade.withdrawals"/></a> </c:when>
                    <c:otherwise><fmt:message key="msg.pg.trade.hasBurst"/> </c:otherwise>
                  </c:choose></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      
      <div class="trade-cont" style="display: none;">
       <table>
          <thead>
           <tr>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealTime"/>
				</th>
				<th>
					<fmt:message key="msg.pg.trade.contractNO"/>
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealType"/>
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.holdAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.dealAmount"/>
				</th>
				<th>
					<fmt:message key="msg.pg.trade.depositCash"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.money"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
				</th>
				<th>
					<fmt:message key="msg.pg.trade.commission"/>(${homeName=='vtrade' ? 'BFC' : 'USDT'})
				</th>
				<th>
					<fmt:message key="msg.pg.tradeQuery.state"/>
				</th>
			</tr>
          </thead>
          <tbody id="tradeTbody">
          	<c:if test="${tradeLogList == null||empty tradeLogList}">
              <tr>
          		<c:if test="${sessionScope.userinfo==null}">
	            	<td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
	            </c:if>
	          </tr>
	        </c:if>
           <c:forEach var="tl" items="${tradeLogList}">
			<tr>
				<td>
					<ut:formatDate dateline="${tl.trade_time}"></ut:formatDate>
				</td>
				<td>
					${fn:toUpperCase(tl.transaction_code)}
				</td>
				<td>
					<c:choose>
						<c:when test="${tl.trade_type_id eq 1}">
							<b class="buy"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></b>
						</c:when>
						<c:when test="${tl.trade_type_id eq 2}">
							<b class="buy"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></b>
						</c:when>
						<c:when test="${tl.trade_type_id eq 3}">
							<b class="sell"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></b>
						</c:when>
						<c:when test="${tl.trade_type_id eq 4}">
							<b class="sell"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></b>
						</c:when>
						<c:when test="${tl.trade_type_id eq 5}">
							<b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.duoBaoTrade"/></b>
						</c:when>
						<c:when test="${tl.trade_type_id eq 6}">
							<b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.kongBaoTrade"/></b>
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${tl.hold_price }" pattern="#0.########" />
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${issz}">
							<fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.#" />
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.####" />
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${issz}">
							<fmt:formatNumber value="${tl.trade_count}" pattern="#0" /><fmt:message key="msg.pg.tradeEntrust.zhang"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${tl.trade_count}" pattern="#0.####" />
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${issz}">
							<fmt:formatNumber value="${tl.trade_deposit}" pattern="#0" /><fmt:message key="msg.pg.tradeEntrust.zhang"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${tl.trade_deposit}" pattern="#0.######" />
						</c:otherwise>
					</c:choose>
				</td>
				<td <c:if test="${tl.trade_income lt 0}"> class="sell"</c:if>
					<c:if test="${tl.trade_income gt 0}"> class="buy"</c:if>>
					<c:choose>
						<c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${tl.trade_income }" pattern="#0.########" />
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<fmt:formatNumber value="${tl.trade_fee}" pattern="#0.########" />
				</td>
				<td>
					<fmt:message key="msg.pg.tradeQuery.done"/>
				</td>
			</tr>
			</c:forEach>
          </tbody>
        </table>
      </div>
 
    </div>
  </div>

<script type="text/javascript">
 // 点击对平弹窗
 
var currTab = 0;
function changeTab(type) {
	currTab = type;
}

function refreshTab() {
	if (currTab == 0) {
		refreshMemberTrans();
	} else if (currTab == 1) {
		refreshPending();
	}
}

//setInterval(refreshTab, 5000);

$("#duiping-btn").click(function() {
	//reset_page();
	var duiping = $("#duiping-wrap"); 
	var dpzz = $("#tb-overlay-duiping");
	var buyCount = Number($("#buyTransCount").val());
	var sellCount = Number($("#sellTransCount").val());
	if (buyCount == 0 || sellCount == 0) {
		tipAlert(MSG['unableOfFlat']);
		location.reload();
		return;
	}
	
	clickSelectedIcon(1, 0);
	clickSelectedIcon(2, 0);
	
    show(duiping);
    dpzz.show();
});
    
$(".dp-close-alert").click(function() {
	var duiping = $("#duiping-wrap"); 
	var dpzz = $("#tb-overlay-duiping");
	hide(duiping);
	dpzz.hide();
});

$(document).keyup(function(event){
	 switch(event.keyCode) {
	 	case 27: {
	 		$(".dp-close-alert").click();
	 	}
	 }
});

//爆仓价文本解释
$(".bcj-txt").hover(function() {
	$(this).parent().append('<div class="que-help bcj-help" id="ykxs-txt"><div class="arrow-top"></div>' + 
	'<p><fmt:message key="msg.pg.trade.trigglexplain"/></p></div>');
}, function() {
	$(this).next("div").remove();
});

//浮动盈亏文本解释
$("#dfdyk-help").hover(function() {
	$(this).parent().append('<div class="que-help fdyk-help" id="ykxs-txt"><div class="arrow-top"></div><p><fmt:message key="msg.pg.trade.floatProfit"/>：<fmt:message key="msg.pg.trade.profitLoss"/>=（<fmt:message key="msg.pg.trade.newDealprice"/>-<fmt:message key="msg.pg.trade.averagePrice"/>）×（<fmt:message key="msg.pg.index.inventory"/>）</p></div>');
}, function() {
	$(this).next("div").remove();
});

$("#kfdyk-help").hover(function() {
	$(this).parent().append('<div class="que-help fdyk-help" id="ykxs-txt"><div class="arrow-top"></div><p><fmt:message key="msg.pg.trade.floatProfit"/>：<fmt:message key="msg.pg.trade.profitLoss"/>=（<fmt:message key="msg.pg.trade.averagePrice"/>-<fmt:message key="msg.pg.trade.newDealprice"/>）×（<fmt:message key="msg.pg.index.inventory"/>）</p></div>');
}, function() {
	$(this).next("div").remove();
});

// 盈亏系数解释文本
$("#ykxs-icon").hover(function() {
	$("#ykxs-txt").show();
}, function() {
	$("#ykxs-txt").fadeOut("slow");
});

// 持仓管理tab
$('#tab-trade span').click(function() {
	$(this).addClass("current").siblings().removeClass();
	//removeClass就是删除当前其他类；只有当前对象有addClass("current")；siblings()意思就是当前对象的同级元素，removeClass就是删除； 
	$(".trade-manage-main .trade-cont").hide().eq($('#tab-trade span').index(this)).show();
	$(".block-title-trade .trade-control").hide().eq($('#tab-trade span').index(this)).show();
});

// 点击追加保证金开关
$("#toggle-contral").click(function() {
	var bzj = $("#money-wrap");
	var bzjzz = $("#tb-overlay1");
	var status = Number($("#depositOnOff_value").val());
	if (status == 0) {
		$("#openClose").html("开启");
	} else {
		$("#openClose").html("关闭");
	}
    show(bzj);
    bzjzz.show();
});
/*
	$("#yes-no").click(function() {
		var bzj = $("#money-wrap");
	var bzjzz = $("#tb-overlay1");
	var status = Number($("#depositOnOff_value").val());
       $("#toggle-contral").stop().toggleClass('sprites-money-on');
       hide(bzj);
       bzjzz.hide();
       if (status == 0) {
        $("#depositOnOff_value").val(1);
 		$.post(basePath + "tradeAjax/depositOnOff.do", {
 			status : 1,
 			token : getCookie("_token")
 		}, function(data) {
 			$("#toggle-contral").removeClass('sprites-money-off');
 			$("#toggle-contral").addClass('sprites-money-on');
 		});
       } else {
       	$("#depositOnOff_value").val(0);
   		$.post(basePath + "tradeAjax/depositOnOff.do", {
   			status : 0,
   			token : getCookie("_token")
   		}, function(data) {
   			$("#toggle-contral").removeClass('sprites-money-on');
   			$("#toggle-contral").addClass('sprites-money-off');
   		});
       }
   });
*/
$(".stpl-close-alert").click(function() {
	var bzj = $("#money-wrap");
	var bzjzz = $("#tb-overlay1");
	hide(bzj);
	bzjzz.hide();
});

    // 自动追加保证金，解释说明
    $("#money-txt").hover(function() {
        $("#money-txt-box").show();
    }, function() {
        $("#money-txt-box").fadeOut("slow");
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
     
   // 开仓单价解释文本
$("#kcdj-que").hover(
	function() {
		$("#kcdj-txt").show();
	}, function() {
		$("#kcdj-txt").fadeOut("slow");
	}
);

/** 止盈止损相关 **/
function alertForm(index, dir, tid){
	if (dir == 1) {
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
			var msg2 = '<fmt:message key="msg.pg.trade.targetProfit"/></div><p><em><fmt:message key="msg.pg.trade.reached"/>';
			var msg3 = '($)<fmt:message key="msg.pg.trade.hour"/></em> <em><fmt:message key="msg.pg.trade.iWillBe"/>';
			var msg4 = '($)<fmt:message key="msg.pg.trade.clearance"/>。</em> <span id="win_error" class="error"></span></p></div></div><div class="control-form"><div class="control-box"><div class="checkbox">';
			var msg5 = '<fmt:message key="msg.pg.trade.stopLoss"/></div><p><em><fmt:message key="msg.pg.trade.reached"/>';
			var msg6 = '($)<fmt:message key="msg.pg.trade.hour"/></em> <em><fmt:message key="msg.pg.trade.iWillBe"/>';
			var msg7 = '($)<fmt:message key="msg.pg.trade.clearance"/>。</em> <span id="loss_error" class="error"></span></p></div><div class="talk"><fmt:message key="msg.pg.trade.stated"/>：<fmt:message key="msg.pg.trade.statedCon"/>。<b class="sell">（<fmt:message key="msg.pg.trade.thisFun"/>）</b></div></div><div class="btn-sub">'
				+ '<button type="button" onclick="stopLossBtn('+index+','+dir+','+tid+');" value="" class="btn sub-btn true"><fmt:message key="msg.pg.userHon.sure"/></button> <button type="button" onclick="closeForm();"  class="btn sub-btn-return close close-windows"><fmt:message key="msg.pg.trade.close"/></button></div></form></div>';
			
			var hasWin = '';
			var stopWinprice = '';
			var pendingWinprice = '';
			var hasLoss = '';
			var stopLossprice = '';
			var pendingLossprice = '';
			
			if(data == null || data.length == 0){ // 既无止赢又无止损
				
				 hasWin = '<input type="checkbox" class="ipt" value="0" id="has_win">';
				 stopWinprice = '<input type="hidden" id="stop_win_price" value="0"  readonly="true" onkeyup="checkInputDigits(this, ${digits});"> <b id="stop_win_price_span"></b>';
				 pendingWinprice = '<input type="text" id="pending_win_price" class="form-control" value="0" onblur="check_pending_win_price('+index+','+dir+');" onkeyup="checkInputDigits(this, ${digits});">';
				 hasLoss = '<input type="checkbox" class="ipt" value="0" id="has_loss">';
				 stopLossprice = '<input type="hidden" id="stop_loss_price" value="0"  readonly="true"  onkeyup="checkInputDigits(this, ${digits});"> <b id="stop_loss_price_span"></b>';
				 pendingLossprice = '<input type="text" id="pending_loss_price" class="form-control" value="0" onblur="check_pending_loss_price('+index+','+dir+');" onkeyup="checkInputDigits(this, ${digits});">';
				
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

				 stopWinprice = '<input type="hidden" id="stop_win_price" value="' + stop_loss_price_zy + '" readonly="true" onkeyup="checkInputDigits(this, ${digits});"><b id="stop_win_price_span">'+stop_loss_price_zy+'</b>';
				 pendingWinprice = '<input type="text" id="pending_win_price" class="form-control" value="' + pending_trans_price_zy + '" onblur="check_pending_win_price('+index+','+dir+');" onkeyup="checkInputDigits(this, ${digits});">';
				 stopLossprice = '<input type="hidden" id="stop_loss_price" value="' + stop_loss_price_zs + '" readonly="true" onkeyup="checkInputDigits(this, ${digits});"><b id="stop_loss_price_span">'+stop_loss_price_zs+'</b>';
				 pendingLossprice = '<input type="text" id="pending_loss_price" class="form-control" value="' + pending_trans_price_zs + '"  onblur="check_pending_loss_price('+index+','+dir+');"  onkeyup="checkInputDigits(this, ${digits});">';
				
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
				
				stopWinprice = '<input type="hidden" id="stop_win_price" value="' + stop_loss_price_zy + '" readonly="true" onkeyup="checkInputDigits(this, ${digits});"><b id="stop_win_price_span">'+stop_loss_price_zy+'</b>';
				pendingWinprice = '<input type="text" id="pending_win_price" class="form-control" value="' + pending_trans_price_zy + '" onblur="check_pending_win_price('+index+','+dir+');" onkeyup="checkInputDigits(this,  ${digits});">';
				stopLossprice = '<input type="hidden" id="stop_loss_price" value="' + stop_loss_price_zs + '" readonly="true" onkeyup="checkInputDigits(this, ${digits});"><b id="stop_loss_price_span">'+stop_loss_price_zs+'</b>';
				pendingLossprice = '<input type="text" id="pending_loss_price" class="form-control" value="' + pending_trans_price_zs + '"  onblur="check_pending_loss_price('+index+','+dir+');" onkeyup="checkInputDigits(this, ${digits});">';
				
			}
			dataText = msg1 + hasWin + msg2 + stopWinprice + msg3 + pendingWinprice + msg4 + hasLoss + msg5 + stopLossprice + msg6 + pendingLossprice + msg7;
		}
	});
	if (dir == 1) {
		$("#trade-alert_dt"+index).html(dataText);
	}else {
		$("#trade-alert_kt"+index).html(dataText);
	}
	
}

function closeForm(){
	$(".zyzs-alert-window ").empty();
}

function check_pending_win_price(index, dir){
	if(dir == 1) {
		var avg_price = Number($('#buy_average_price'+index).val());
		var stop_win_price = Number($('#stop_win_price').val());
		console.log(stop_win_price);
		var pending_win_price = Number($('#pending_win_price').val());
		if(pending_win_price <= avg_price) {
			pending_win_price = (avg_price * 1.0003).toFixed(${digits});
			$('#pending_win_price').val(pending_win_price);
			$('#stop_win_price').val((pending_win_price*1.001).toFixed(${digits}));
			$('#stop_win_price_span').html((pending_win_price*1.001).toFixed(${digits}));
			$('#win_error').html("<fmt:message key='msg.pg.trade.multigtAveg'/>");
			return false;
		}
		
		$('#stop_win_price').val((pending_win_price*1.001).toFixed(${digits}));
		$('#stop_win_price_span').html((pending_win_price*1.001).toFixed(${digits}));
		
	} else {
		var avg_price = Number($('#sell_average_price'+index).val());
		var stop_win_price = Number($('#stop_win_price').val());
		var pending_win_price = Number($('#pending_win_price').val());
		if(pending_win_price >= avg_price){
			pending_win_price = formatFloat(avg_price*0.9997, ${digits})
			$('#pending_win_price').val(pending_win_price);
			$('#stop_win_price').val((pending_win_price*0.999).toFixed(${digits}));
			$('#stop_win_price_span').html((pending_win_price*0.999).toFixed(${digits}));
			$('#win_error').html("<fmt:message key='msg.pg.trade.emptyltAveg'/>");
			return false;
		}
		
		$('#stop_win_price').val((pending_win_price*0.999).toFixed(${digits}));
		$('#stop_win_price_span').html((pending_win_price*0.999).toFixed(${digits}));
	}
	
	$('#win_error').html("");
	return true;
}

function check_pending_loss_price(index, dir) {
	if(dir == 1) {
		var avg_price = Number($('#buy_average_price'+index).val());
		var stop_loss_price = Number($('#stop_loss_price').val());
		var pending_loss_price = Number($('#pending_loss_price').val());
		var qpPrice = Number($('#buy_qpj'+index).val());
		/*要求止损价小于均价*/
		if(pending_loss_price >= avg_price) {
			/*
			pending_loss_price = (avg_price * 0.9997).toFixed(${digits});
			$('#pending_loss_price').val(pending_loss_price);
			$('#stop_loss_price').val((pending_loss_price*1.001).toFixed(${digits}));
			$('#stop_loss_price_span').html((pending_loss_price*1.001).toFixed(${digits}));
			*/
			
			$('#loss_error').html("<fmt:message key='msg.pg.trade.multiltAveg'/>");
			return false;
		} 
		
		/*要求止损价大于爆仓价*/
		if (pending_loss_price <= qpPrice) {
			/*
			pending_loss_price = (qpPrice * 1.0003).toFixed(${digits});
			$('#pending_loss_price').val(pending_loss_price);
			$('#stop_loss_price').val((pending_loss_price*1.001).toFixed(${digits}));
			$('#stop_loss_price_span').html((pending_loss_price*1.001).toFixed(${digits}));
			*/
			
			$('#loss_error').html("<fmt:message key='msg.pg.trade.multigtStrong'/>");
			return false;
		}
		$('#stop_loss_price').val((pending_loss_price*1.001).toFixed(${digits}));
		$('#stop_loss_price_span').html((pending_loss_price*1.001).toFixed(${digits}));
	} else {
		var avg_price = Number($('#sell_average_price'+index).val());
		var stop_loss_price = Number($('#stop_loss_price').val());
		var pending_loss_price = Number($('#pending_loss_price').val());
		var qpPrice = Number($('#sell_qpj'+index).val());
		
		/*要求止损价大于均价*/
		if(pending_loss_price <= avg_price) {
			/*
			stop_loss_price = (qpPrice * 1.0003).toFixed(${digits});
			$('#pending_loss_price').val(stop_loss_price);
			$('#stop_loss_price').val((stop_loss_price*0.999).toFixed(${digits}));
			$('#stop_loss_price_span').html((stop_loss_price*0.999).toFixed(${digits}));
			*/
			$('#loss_error').html("<fmt:message key='msg.pg.trade.emptygtAveg'/>");
			return false;
		}
		/*要求止损价小于爆仓价*/
		if(pending_loss_price >= qpPrice){
			/*
			pending_loss_price = (qpPrice * 0.9997).toFixed(${digits});
			$('#pending_loss_price').val(pending_loss_price);
			$('#stop_loss_price').val((stop_loss_price*0.999).toFixed(${digits}));
			$('#stop_loss_price_span').html((stop_loss_price*0.999).toFixed(${digits}));
			*/
			$('#loss_error').html("<fmt:message key='msg.pg.trade.emptyltStrong'/>");
			return false;
		}
		$('#stop_loss_price').val((pending_loss_price*0.999).toFixed(${digits}));
		$('#stop_loss_price_span').html((pending_loss_price*0.999).toFixed(${digits}));
	}
	
	$('#loss_error').html("");
	return true;
}

function stopLossBtn(index, dir,tid){
	var hasWin = "0";
	var hasLoss = "0";
	
	if ($("#has_win").attr("checked")) {
		hasWin = 1;
		var stop_win_price = Number($('#stop_win_price').val());
		var pending_win_price = Number($('#pending_win_price').val());
		if(stop_win_price <= 0 || pending_win_price <= 0){
			$('#win_error').html("<fmt:message key='msg.pg.trade.fillProfit'/>");
			return false;
		}
		
		if (!check_pending_win_price(index, dir)) {
			return false;
		}
	}
	
	$('#win_error').html("");
	
	if ($("#has_loss").attr("checked")) {
		hasLoss = 1;
		var stop_loss_price = Number($('#stop_loss_price').val());
		var pending_loss_price = Number($('#pending_loss_price').val());
		if(stop_loss_price == 0 || pending_loss_price == 0){
			$('#loss_error').html("<fmt:message key='msg.pg.trade.fillLoss'/>");
			return false;
		}

		if (!check_pending_loss_price(index, dir)) {
			return false;
		}
	}
	
	$('#loss_error').html("");

	$.ajax({
		cache: false,
        async: false, 
        dataType: 'JSON',
        type: 'POST',
        url: basePath +"tradeAjax/stopLossChange.do?t=" + new Date().getTime(),
		data : {
			transaction_member_id : tid, 
			has_win : hasWin, 
			has_loss : hasLoss,
        	stop_win_price : $("#stop_win_price").val(),
        	pending_win_price : $("#pending_win_price").val(),
        	stop_loss_price : $("#stop_loss_price").val(), 
        	pending_loss_price : $("#pending_loss_price").val(),
        	token : getCookie("_token")
		},
		success: function (data) {
			 if(data.code == "success") {
				$("#alertId_"+tid).removeClass("sprites-zszy-off");
				$("#alertId_"+tid).addClass("sprites-zszy-on");
				closeForm();
			} else if (data.code == "success2") {
				$("#alertId_"+tid).removeClass("sprites-zszy-on");
				$("#alertId_"+tid).addClass("sprites-zszy-off");
				closeForm();
			} else if (data.code=="error") {
				if (data.msg.indexOf("止盈") >= 0) {
					$('#win_error').html(""+data.msg);
				} else {
					$('#loss_error').html(""+data.msg);
				}
			}
		}
	});
}

</script>