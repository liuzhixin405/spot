<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>

<c:if test="${locale == 'en_US' }">
    <style type="text/css">
        .alert-window-full {
            top: -300px;
        }
    </style>
</c:if>

<div class="main-wrap" style="overflow: visible;height: 100%;">
    <div class="block-title-trade">
        <div class="tab tab-trade lt" id="tab-trade">
            <span class="current" onclick="refreshMemberTrans(); changeTab(0);"><fmt:message key="msg.pg.trade.holdManage"/></span>
            <span onclick="refreshPending(); changeTab(1);"><fmt:message key="msg.pg.trade.fmtFullLimitOrder"/><em id="limitOrderSize"></em></span>
            <span onclick="refreshPlanEntrust(); changeTab(2);"><fmt:message key="msg.pg.trade.fmtFullPlanEntrust"/><em id="planEntrustSize"></em></span>
            <span onclick="refreshHistoryPending(); changeTab(3);"><fmt:message key="msg.pg.trade.historyOrder"/><em> (<fmt:message key="msg.pg.trade.nearly7days"/>)</em></span>
            <span onclick="refreshTradeLog(); changeTab(3);"><fmt:message key="msg.trade.record"/><em> (<fmt:message key="msg.pg.trade.nearly7days"/>)</em></span>

        </div>
        <div class="trade-control relative">
            <div class="lt"><a href="javascript:;" onclick="refreshMemberTrans()"><fmt:message key="msg.trade.position.refresh"/></a> </div>
        </div>
        <div class="trade-control relative" style="display: none;">
            <div class="lt"><a href="javascript:;" onclick="cancelDeal('1','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.buyBtn"/></a> </div>
            <div class="lt"><a href="javascript:;" onclick="cancelDeal('2','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.sellBtn"/></a> </div>
            <div class="lt"><a href="javascript:;" onclick="cancelDeal('0','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.alldraw"/></a> </div>
        </div>
        <div class="trade-control relative" style="display: none;">
            <div class="lt"><a href="javascript:;" onclick="cancelPlanDeal('1','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.buyBtn"/></a> </div>
            <div class="lt"><a href="javascript:;" onclick="cancelPlanDeal('2','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.sellBtn"/></a> </div>
            <div class="lt"><a href="javascript:;" onclick="cancelPlanDeal('0','${currentTransaction.transaction_code}');"><fmt:message key="msg.pg.trade.alldraw"/></a> </div>
        </div>
        <div class="trade-control relative" style="display: none;">
            <a href="pendingQuery.do?pendingcode=${currentTransaction.transaction_code}"><fmt:message key="msg.normal.more"/></a>
        </div>
        <div class="trade-control relative" style="display: none;">
            <a href="tradeQuery.do?pendingcode=${currentTransaction.transaction_code}"><fmt:message key="msg.normal.more"/></a>
        </div>
        <div class="clear"></div>
    </div>

    <div class="trade-manage-main table-bg" style="height: calc(100% - 34px);">
        <div class="trade-cont" >
            <!-- 持仓管理 -->
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
                    </th>
                    <c:if test="${isMarket == null }">
                        <th><fmt:message key="msg.pg.trade.depositCash"/></th>
                    </c:if>
                    <th class="relative" style="width:100px"><fmt:message key="msg.pg.trade.floatProfit"/>
                    </th>
                    <c:if test="${isMarket == null }">
                        <th><fmt:message key="msg.pg.trade.profitRatio"/></th>
                    </c:if>
                    <th style="width:70px;"><fmt:message key="msg.pg.trade.exitPrice"/></th>
                    <th style="width:80px;"><fmt:message key="msg.pg.trade.numberPositions"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                    <c:if test="${isMarket == null }">
                        <th style="width:220px" class="option-part"><fmt:message key="msg.pg.trade.option"/>
                            <c:if test="${buyMemberTransaction != null && buyMemberTransaction.size() > 0}">
                                <i class="iconfont icon-tishi relative">
                        <span class="show-option-box hide">
                            <span class="triangle"></span>
                            <span class="index-info-detail" style="text-align: left;"><fmt:message key="msg.pg.trade.optionsTips"/></span>
                        </span>
                                </i>
                            </c:if>
                        </th>
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
                            <td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do" class="full-a-link"><fmt:message key="msg.pg.top.login"/></a> </td>
                        </c:if>
                        <c:if test="${sessionScope.userinfo!=null}">
                            <td colspan="12"> <fmt:message key="msg.pg.trade.guessBuy"/> </td>
                        </c:if>
                    </tr>
                </c:if>
                <c:forEach items="${buyMemberTransaction}" var="tran" varStatus="l">
                    <tr id="tran_buy_${tran.mt_id }">
                        <td id="buyMultiple${tran.mt_id}"><em class="buy"><fmt:message key="msg.pg.trade.much"/> </em> x${tran.multiple} </td>
                        <td id="buyHoldCount${tran.mt_id}"><fmt:formatNumber value="${tran.holdCount}"></fmt:formatNumber></td>
                        <c:if test="${isMarket == null }">
                            <td id="buyLockCount${tran.mt_id}"><fmt:formatNumber value="${tran.lockCount}"></fmt:formatNumber></td>
                        </c:if>
                        <td id="buyAvgPrice${tran.mt_id}"><fmt:formatNumber value="${tran.averagePrice}" maxFractionDigits="${digits}"></fmt:formatNumber></td>
                        <td id="buyQpPrice${tran.mt_id}"><fmt:formatNumber value="${tran.qpPrice}" maxFractionDigits="${digits}"></fmt:formatNumber></td>
                        <c:if test="${isMarket == null }">
                            <td id="buyDeposit${tran.mt_id}"><fmt:formatNumber value="${tran.deposit}" pattern="#.####"></fmt:formatNumber></td>
                        </c:if>
                        <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }" id="floatGainLossbuy${tran.mt_id}"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
                        </fmt:formatNumber></td>
                        <c:if test="${isMarket == null }">
                            <td id="floatGainLossRatebuy${tran.mt_id}"> ${tran.profitLossRatio } </td>
                        </c:if>
                        <td>
                            <input type="hidden" id="buy_trade_multiple${tran.mt_id}" value="${tran.multiple}" />
                            <input type="hidden" id="buy_all_count${tran.mt_id}" value="${tran.allCount }" />
                            <input type="hidden" id="buy_hold_count${tran.mt_id}" value="${tran.holdCount }" />
                            <input type="hidden" id="buy_average_price${tran.mt_id}" value="${tran.averagePrice }"/>
                            <input type="hidden" id="buy_deposit${tran.mt_id}" value="${tran.deposit }"/>
                            <input type="hidden" id="buy_qpj${tran.mt_id}" value="${tran.qpPrice }"/>
                            <input type="hidden" id="buy_accuratePrice${tran.mt_id}" value="${tran.accuratePrice}"/>
                            <input type="hidden" id="orderType${tran.mt_id}" value=""/>
                            <span class="td-child-control" >
                           <input type="text" class="form-control price-buy-input td-input" id="pcPrice_d${tran.mt_id}" name="price"
                                  value="" onkeyup="checkInputDigits(this, ${digits});" autocomplete="off"/>
                          <div class="price-main" id="close_form_pc_buy${tran.mt_id}" style="display: none;">
                            <div class="tb-overlay"></div>
                            <div class="price-alert" >
                              <div class="arrow-left"></div>
                              <p> 您的报价不符合系统的限价规则，当前 <span>多仓最小可卖出</span>价为： <b id="price_pc_buy_1${tran.mt_id}">0</b> <br>
                                  您是否确定以 <b id="price_pc_buy_2${tran.mt_id}">0</b><em class="sell">卖出</em><b id="amount_pc_buy${tran.mt_id}">0</b>${currentTransaction.transaction_code.toUpperCase()}  </p>
                              <div class="calculat-sub">
                                <button type="button" class="btn sub-btn" id="submit_but_buy${tran.mt_id}">确定</button>
                                <button type="button" class="btn sub-btn-return close-price-window" onclick="closePcBut(${tran.mt_id});">关闭</button>
                              </div>
                            </div>
                          </div>
                      </span>
                        </td>
                        <td>
                      <span class="td-child-control">
                        <input type="text" class="form-control td-input" id="pcAmount_d${tran.mt_id}" name="amount" value="" autocomplete="off"
                               onblur="checkPcAmount('pcAmount_d${tran.mt_id}','${tran.holdCount}');" onkeyup="checkInputDigits(this, ${amountDigits});"/>
                      </span>
                        </td>
                        <td>
                            <c:if test="${isMarket == null }">
                	<span class="td-child-control pc">
                       	<a href="javascript:void(0);" class="control-btn btn pc-btn" onclick="pc_d('${tran.bqCode}',${tran.mt_id});"
                           <c:if test="${currentTransaction.suspend || !isTradeTime}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.limitedBuyMore"/></a>

                  			<a href="javascript:void(0);" class="btn-all-pc" onclick="oneClickCloseOk('${tran.bqCode}',${tran.mt_id}, 1);"
                               <c:if test="${currentTransaction.suspend || !isTradeTime}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.oneClickClose"/></a>
                		 <a class="zszy-btn ${tran.stopLoss eq 0 ? 'sprites-zszy-off' : 'sprites-zszy-on' } "
                            id="alertId_${tran.mt_id}" onclick="showFullProlitLoss(${tran.trade_type_id}, ${tran.mt_id});"></a>
                        <!--止盈止损弹窗 -->
                  		 <span class="zyzs-alert-window" id="trade-alert_dt${tran.mt_id}"></span>
                  	</span>
                            </c:if>

                            <c:if test="${isMarket != null }">
                  	<span class="td-child-control">
                  		<a href="javascript:void(0);" class="control-btn btn pc-btn" onclick="pc_d('${tran.bqCode}',${tran.mt_id});"
                           <c:if test="${currentTransaction.suspend || !isTradeTime}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.closePosition"/></a>
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
                    </th>
                    <c:if test="${isMarket == null }">
                        <th><fmt:message key="msg.pg.trade.depositCash"/></th>
                    </c:if>
                    <th class="relative" style="width:100px"><fmt:message key="msg.pg.trade.floatProfit"/>
                    </th>
                    <c:if test="${isMarket == null }">
                        <th><fmt:message key="msg.pg.trade.profitRatio"/></th>
                    </c:if>
                    <th style="width:70px;"><fmt:message key="msg.pg.trade.exitPrice"/></th>
                    <th style="width:80px;"><fmt:message key="msg.pg.trade.numberPositions"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                    <c:if test="${isMarket == null }">
                        <th style="width:220px" class="option-part"><fmt:message key="msg.pg.trade.option"/>
                            <c:if test="${sellMemberTransaction != null && sellMemberTransaction.size() > 0}">
                                <i class="iconfont icon-tishi relative">
                        <span class="show-option-box hide">
                            <span class="triangle"></span>
                            <span class="index-info-detail" style="text-align: left;"><fmt:message key="msg.pg.trade.optionsTips"/></span>
                        </span>
                                </i>
                            </c:if>
                        </th>
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
                            <td colspan="12"><fmt:message key="msg.pg.trade.please"/><a href="<%=basePath%>login/login.do" class="full-a-link"><fmt:message key="msg.pg.top.login"/></a> </td>
                        </c:if>
                        <c:if test="${sessionScope.userinfo!=null}">
                            <td colspan="12"> <fmt:message key="msg.pg.trade.guessSell"/> </td>
                        </c:if>
                    </tr>
                </c:if>
                <c:forEach items="${sellMemberTransaction}" var="tran" varStatus="l">
                    <tr id="tran_sell_${tran.mt_id }">
                        <td id="sellMultiple${tran.mt_id }"><em class="sell"><fmt:message key="msg.pg.trade.guessEmpty"/> </em> x${tran.multiple} </td>
                        <td id="sellHoldCount${tran.mt_id}"><fmt:formatNumber value="${tran.holdCount }">
                        </fmt:formatNumber></td>
                        <c:if test="${isMarket == null }">
                            <td id="sellLockCount${tran.mt_id}"><fmt:formatNumber value="${tran.lockCount }">
                            </fmt:formatNumber></td>
                        </c:if>
                        <td id="sellAvgPrice${tran.mt_id}"><fmt:formatNumber value="${tran.averagePrice }" maxFractionDigits="${digits}"></fmt:formatNumber></td>
                        <td id="sellQpPrice${tran.mt_id}"><fmt:formatNumber value="${tran.qpPrice }" maxFractionDigits="${digits}"></fmt:formatNumber></td>
                        <c:if test="${isMarket == null }">
                            <td id="sellDeposit${tran.mt_id}"><fmt:formatNumber value="${tran.deposit }" pattern="#.####">
                            </fmt:formatNumber></td>
                        </c:if>
                        <td class="${tran.floatingProfitLoss >0? 'buy':'sell' }" id="floatGainLosssell${tran.mt_id}"><fmt:formatNumber value="${tran.floatingProfitLoss }" pattern="#.########">
                        </fmt:formatNumber></td>
                        <c:if test="${isMarket == null }">
                            <td id="floatGainLossRatesell${tran.mt_id}"> ${tran.profitLossRatio } </td>
                        </c:if>
                        <td>
                            <input type="hidden" id="sell_trade_multiple${tran.mt_id}" value="${tran.multiple}" />
                            <input type="hidden" id="sell_all_count${tran.mt_id}" value="${tran.allCount }" />
                            <input type="hidden" id="sell_hold_count${tran.mt_id}" value="${tran.holdCount }" />
                            <input type="hidden" id="sell_average_price${tran.mt_id}" value="${tran.averagePrice }"/>
                            <input type="hidden" id="sell_deposit${tran.mt_id}" value="${tran.deposit }"/>
                            <input type="hidden" id="sell_qpj${tran.mt_id}" value="${tran.qpPrice }"/>
                            <input type="hidden" id="sell_accuratePrice${tran.mt_id}" value="${tran.accuratePrice}"/>
                            <input type="hidden" id="orderType${tran.mt_id}" value=""/>
                            <span class="td-child-control" >
                          <input type="text" class="form-control price-sell-input td-input" id="pcPrice_k${tran.mt_id}" name="pcPrice_k" value=""
                                 onkeyup="checkInputDigits(this, ${digits});" autocomplete="off"/>
                          <div class="price-main" id="close_form_pc_sell${tran.mt_id}" style="display: none;">
                            <div class="tb-overlay"></div>
                            <div class="price-alert" >
                              <div class="arrow-left"></div>
                              <p> 您的报价不符合系统的限价规则，当前 <span>空仓最大可买入</span>价为： <b id="price_pc_sell_1${tran.mt_id}">0</b> <br>
                                  您是否确定以 <b id="price_pc_sell_2${tran.mt_id}">0</b><em class="buy">买入</em><b id="amount_pc_sell${tran.mt_id}">0</b>${currentTransaction.transaction_code.toUpperCase()}  </p>
                              <div class="calculat-sub">
                                <button type="button" class="btn sub-btn" id="submit_but_sell${tran.mt_id}">确定</button>
                                <button type="button" class="btn sub-btn-return close-price-window" onclick="closePcBut(${tran.mt_id});">关闭</button>
                              </div>
                            </div>
                          </div>
                      </span>
                        </td>
                        <td>
                      <span class="td-child-control">
                        <input type="text" class="form-control td-input" id="pcAmount_k${tran.mt_id}" name="pc_Amount_k" value="" autocomplete="off"
                               onblur="checkPcAmount('pcAmount_k${tran.mt_id}','${tran.holdCount}');" onkeyup="checkInputDigits(this,${amountDigits});" />
                      </span>
                        </td>
                        <td>
                            <c:if test="${isMarket == null }">
                	<span class="td-child-control pc">
                       <a href="javascript:" class="control-btn btn pc-btn" onclick="pc_k('${tran.bqCode}',${tran.mt_id});"
                          <c:if test="${currentTransaction.suspend || !isTradeTime}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.limitedBuyMore"/></a>

	                	<a href="javascript:void(0);" class="btn-all-pc" onclick="oneClickCloseOk('${tran.bqCode}',${tran.mt_id}, 2);"
                           <c:if test="${currentTransaction.suspend || !isTradeTime}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.oneClickClose"/></a>
	                	 <a class="zszy-btn ${tran.stopLoss eq 0 ? 'sprites-zszy-off' : 'sprites-zszy-on' } " id="alertId_${tran.mt_id}" onclick="showFullProlitLoss(${tran.trade_type_id}, ${tran.mt_id});"></a>
                        <!--止盈止损弹窗 -->
	                  	 <span class="zyzs-alert-window" id="trade-alert_kt${tran.mt_id}"></span>
                  	</span>
                            </c:if>
                            <c:if test="${isMarket != null }">
                	<span class="td-child-control">
	                	<a href="javascript:void(0);" class="control-btn btn pc-btn" onclick="pc_k('${tran.bqCode}',${tran.mt_id});"
                           <c:if test="${currentTransaction.suspend || !isTradeTime}">disabled="true"</c:if>><fmt:message key="msg.pg.trade.closePosition"/></a>
	                </span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- 限价委托 -->
        <div class="trade-cont trade-matter" id="trade-matter" style="display: none;">
            <table>
                <thead>
                <tr>
                    <th> <fmt:message key="msg.pg.trade.entrueTime"/></th>
                    <c:if test="${isMarket == null }">
                        <th> <fmt:message key="msg.pg.trade.contractNO"/> </th>
                    </c:if>
                    <th> <fmt:message key="msg.pg.trade.typeOfList"/> </th>
                    <th> <fmt:message key="msg.pg.trade.leverage"/> </th>
                    <th> <fmt:message key="msg.pg.trade.entruePrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
                    <th> <fmt:message key="msg.pg.trade.entrueNO"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                    <th> <fmt:message key="msg.pg.trade.doneDeal"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                    <th> <fmt:message key="msg.pg.trade.unsettled"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                    <th> <fmt:message key="msg.pg.trade.depositCash"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}) </th>
                    <th style="width:60px"> <fmt:message key="msg.pg.trade.option"/> </th>
                </tr>
                </thead>
                <tbody id="pendingTbody">
                <c:if test="${pdList == null||empty pdList}">
                    <tr>
                        <c:if test="${sessionScope.userinfo==null}">
                            <td colspan="12"><fmt:message key="msg.pg.trade.please"/><a class="full-a-link" href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
                        </c:if>
                    </tr>
                </c:if>

                <c:forEach var="pd" items="${pdList}" >
                    <tr>
                        <td><jsp:useBean id="dateValue" class="java.util.Date"/>
                            <jsp:setProperty name="dateValue" property="time" value="${pd.createTime * 1000}"/>
                            <fmt:formatDate value="${dateValue}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                        <c:if test="${isMarket == null }">
                            <td> ${fn:toUpperCase(pd.symbol)} </td>
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
                        <td> ${pd.price} </td>
                        <td><fmt:formatNumber value="${pd.amount}" pattern="#0.0000"/></td>
                        <td><fmt:formatNumber value="${pd.dealAmount}" pattern="#0.0000"/></td>
                        <td><fmt:formatNumber value="${pd.undealAmount}" pattern="#0.0000"/></td>
                        <td><fmt:formatNumber value="${pd.deposit}" pattern="#0.0000"/></td>
                        <td><c:choose>
                            <c:when test="${pd.priority < 3}"><a href="javascript:void(0);"  class="btn btn-blue"
                                                                 onclick="cancelDealAnsyc('${pd.orderId}','${pd.symbol}',${pd.state eq 'submitting'});"><fmt:message key="msg.pg.trade.withdrawals"/></a> </c:when>
                            <c:otherwise><fmt:message key="msg.pg.trade.hasBurst"/> </c:otherwise>
                        </c:choose></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- 计划委托 -->
        <div class="trade-cont trade-matter" id="trade-plan" style="display: none;">
            <table>
                <thead>
                <tr>
                    <th> <fmt:message key="msg.pg.trade.entrueTime"/></th>
                    <c:if test="${isMarket == null }">
                        <th> <fmt:message key="msg.pg.trade.contractNO"/> </th>
                    </c:if>
                    <th> <fmt:message key="msg.pg.trade.typeOfList"/> </th>
                    <th> <fmt:message key="msg.pg.trade.leverage"/> </th>
                    <th> <fmt:message key="msg.pg.trade.entrueNO"/></th>
                    <th> <fmt:message key="msg.pg.trade.triPrice"/></th>
                    <th> <fmt:message key="msg.pg.trade.entruePrice"/></th>
                    <th><fmt:message key="msg.pg.tradeQuery.state"/></th>
                    <th> <fmt:message key="msg.pg.trade.option"/> </th>
                </tr>
                </thead>
                <tbody id="planEntrustTbody">
                <c:if test="${planPdVOList == null||empty planPdVOList}">
                    <tr>
                        <c:if test="${sessionScope.userinfo==null}">
                            <td colspan="12"><fmt:message key="msg.pg.trade.please"/><a class="full-a-link" href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
                        </c:if>
                    </tr>
                </c:if>
                <c:forEach var="pd" items="${planPdVOList}" >
                    <tr>
                        <td>
                            <ut:formatDate dateline="${pd.createTime * 1000}"></ut:formatDate>
                        </td>
                        <c:if test="${isMarket == null }">
                            <td> ${fn:toUpperCase(pd.symbol)} </td>
                        </c:if>
                        <td>
                            <c:choose>
                                <c:when test="${(pd.trade_type == 0 || pd.trade_type == null) && pd.direction == 1}"><b class="buy"><fmt:message key="msg.pg.tradeEntrust.buyfTrade"/></b></c:when><%--多单开仓--%>
                                <c:when test="${(pd.trade_type == 0 || pd.trade_type == null) && pd.direction == 2}"><b class="sell"><fmt:message key="msg.pg.tradeEntrust.sellfTrade"/></b></c:when><%--空单开仓--%>
                                <c:when test="${pd.trade_type == 1 && pd.direction == 1 && (pd.stop_loss_type_id <= 0 || pd.stop_loss_type_id == null)}"><b class="sell"><fmt:message key="msg.pg.tradeEntrust.buysTrade"/></b></c:when><%--空单平仓--%>
                                <c:when test="${pd.trade_type == 1 && pd.direction == 2 && (pd.stop_loss_type_id <= 0 || pd.stop_loss_type_id == null)}"><b class="buy"><fmt:message key="msg.pg.tradeEntrust.sellsTrade"/></b></c:when><%--多单平仓--%>
                                <c:when test="${pd.trade_type == 1 && pd.direction == 1 && pd.stop_loss_type_id == 1}"><b class="sell"><fmt:message key="msg.pg.tradeEntrust.sellStopProfit"/></b></c:when><%--空单止盈--%>
                                <c:when test="${pd.trade_type == 1 && pd.direction == 2 && pd.stop_loss_type_id == 1}"><b class="buy"><fmt:message key="msg.pg.tradeEntrust.buyStopProfit"/></b></c:when><%--多单止盈--%>
                                <c:when test="${pd.trade_type == 1 && pd.direction == 1 && pd.stop_loss_type_id == 2}"><b class="sell"><fmt:message key="msg.pg.tradeEntrust.sellStopLoss"/></b></c:when><%--空单止损--%>
                                <c:when test="${pd.trade_type == 1 && pd.direction == 2 && pd.stop_loss_type_id == 2}"><b class="buy"><fmt:message key="msg.pg.tradeEntrust.buyStopLoss"/></b></c:when><%--多单止损--%>
                                <c:otherwise> </c:otherwise>
                            </c:choose>
                        </td>
                        <td> ${pd.multiple} </td>
                        <td><fmt:formatNumber value="${pd.amount}" pattern="#0.0000"/></td>
                        <td><fmt:message key="msg.pg.trade.markPrice"/> <c:if test="${pd.type eq 1}">≤</c:if><c:if test="${pd.type eq 2}">≥</c:if> ${pd.triPrice} </td>
                        <td> <c:if test="${pd.price eq '0'}"><fmt:message key="msg.pg.trade.planMarketPrice"/> </c:if><c:if test="${pd.price ne '0'}">${pd.price}</c:if> </td>
                        <td>
                            <c:choose>
                                <c:when test="${pd.status eq 0}"><b><fmt:message key="msg.pg.trade.invalid"/></b></c:when>
                                <c:when test="${pd.status eq 1}"><b><fmt:message key="msg.pg.trade.executed"/></b></c:when>
                                <c:when test="${pd.status eq 2}"><b><fmt:message key="msg.pg.trade.becameInvalid"/></b></c:when>
                                <c:when test="${pd.status eq 3}"><b><fmt:message key="msg.pg.trade.allreadyCancel"/></b></c:when>
                                <c:otherwise> </c:otherwise>
                            </c:choose>
                        </td>
                        <!-- 操作 -->
                        <td>
                            <c:if test="${pd.status ne 1 and pd.status ne 3}">

                            </c:if>
                            <c:if test="${pd.status eq 0}">
                                <a href="javascript:void(0);" class="btn btn-blue"
                                   onclick="cancelPlanDealAnsyc('${pd.delegation_id}','${pd.symbol}');"><fmt:message key="msg.pg.trade.withdrawals"/></a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- 历史委托 -->
        <div class="trade-cont trade-matter" style="display: none;">
            <table>
                <thead>
                <tr>
                    <th> <fmt:message key="msg.pg.trade.entrueTime"/></th>
                    <th> <fmt:message key="msg.pg.trade.contractNO"/> </th>
                    <th> <fmt:message key="msg.pg.trade.typeOfList"/> </th>
                    <th> <fmt:message key="msg.pg.trade.entruePrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
                    <th> <fmt:message key="msg.pg.trade.entrueNO"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                    <th> <fmt:message key="msg.pg.trade.doneDeal"/><c:if test="${locale == 'zh_CN'}">(<fmt:message key="msg.pg.trade.ge"/>)</c:if></th>
                    <th> <fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
                    <th> <fmt:message key="msg.pg.tradeQuery.money"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})</th>
                    <th> <fmt:message key="msg.pg.trade.commission"/>${homeName=='vtrade' ? '(BFC)' : ''}</th>
                    <th> <fmt:message key="msg.pg.tradeQuery.state"/> </th>
                </tr>
                </thead>
                <tbody id="historyPendingTbody">
                <tr>
                    <c:if test="${sessionScope.userinfo==null}">
                        <td colspan="12"><fmt:message key="msg.pg.trade.please"/><a class="full-a-link" href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
                    </c:if>
                </tr>
                </tbody>
            </table>
        </div>
        <!-- 成交记录 -->
        <div class="trade-cont trade-matter" id="trade-history" style="display: none;">
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
                        <fmt:message key="msg.pg.tradeQuery.holdAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})
                    </th>
                    <th>
                        <fmt:message key="msg.pg.tradeQuery.dealAvgPrice"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})
                    </th>
                    <th>
                        <fmt:message key="msg.pg.tradeQuery.dealAmount"/>
                    </th>
                    <th>
                        <fmt:message key="msg.pg.trade.depositCash"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})
                    </th>
                    <th>
                        <fmt:message key="msg.pg.tradeQuery.money"/>(${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')})
                    </th>
                    <th>
                        <fmt:message key="msg.pg.trade.commission"/>${homeName=='vtrade' ? '(BFC)' : ''}
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
                            <td colspan="12"><fmt:message key="msg.pg.trade.please"/><a class="full-a-link" href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.top.login"/></a> </td>
                        </c:if>
                    </tr>
                </c:if>
                <c:forEach var="tl" items="${tradeLogList}" varStatus="l">
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
                                <c:when test="${tl.trade_type_id eq 5 }">
                                    <b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.duoBaoTrade"/></b>
                                    <div class="addTips" onmouseout="hideOverloadTip(${l.index})" onmouseover="showOverloadTip(${l.index})">
                                        <div class="addTips-arrow" id="baocangTips_arrow_${l.index}"></div>
                                        <div class="baocangTips" id="baocangTips_${l.index}" style="${l.index == 4 ? 'top:-43px;' : ''}">
                                            <fmt:message key="msg.pg.trade.bao.text.your" />${fn:toUpperCase(tl.transaction_code)}<fmt:message key="msg.pg.trade.bao.text.contract" />
                                            <fmt:message key="msg.pg.trade.bao.text.buy" />${tl.trade_multiple}<fmt:message key="msg.pg.trade.bao.text.note1" />
                                            <c:if test="${tl.force_price > 0}"><span style="color:red;">${tl.force_price.doubleValue()}USDT</span></c:if>
                                            <fmt:message key="msg.pg.trade.bao.text.note12" /><span style="color:red;">${tl.trade_average_price.doubleValue()}</span>
                                            <fmt:message key="msg.pg.trade.bao.text.note2" />
                                        </div>
                                    </div>
                                </c:when>
                                <c:when test="${tl.trade_type_id eq 6}">
                                    <b class="loss-all"><fmt:message key="msg.pg.tradeEntrust.kongBaoTrade"/></b>
                                    <div class="addTips" onmouseout="hideOverloadTip(${l.index})" onmouseover="showOverloadTip(${l.index})">
                                        <div class="addTips-arrow" id="baocangTips_arrow_${l.index}"></div>
                                        <div class="baocangTips" id="baocangTips_${l.index}" style="${l.index == 4 ? 'top:-43px;' : ''}">
                                            <fmt:message key="msg.pg.trade.bao.text.your" />${fn:toUpperCase(tl.transaction_code)}<fmt:message key="msg.pg.trade.bao.text.contract" />
                                            <fmt:message key="msg.pg.trade.bao.text.sell" />${tl.trade_multiple}<fmt:message key="msg.pg.trade.bao.text.note1" />
                                            <c:if test="${tl.force_price > 0}"><span style="color:red;">${tl.force_price.doubleValue()}USDT</span></c:if>
                                            <fmt:message key="msg.pg.trade.bao.text.note12" /><span style="color:red;">${tl.trade_average_price.doubleValue()}</span>
                                            <fmt:message key="msg.pg.trade.bao.text.note2" />
                                        </div>
                                    </div>
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
                                <c:when test="${tl.trade_type_id eq 5 or tl.trade_type_id eq 6}">
                                    --
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${issz}">
                                            <fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.#" />
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${tl.trade_average_price}" pattern="#0.########" />
                                        </c:otherwise>
                                    </c:choose>
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
                                <c:when test="${tl.trade_type_id eq 5 or tl.trade_type_id eq 6}">
                                    --
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${issz}">
                                            <fmt:formatNumber value="${tl.trade_deposit}" pattern="#0" /><fmt:message key="msg.pg.tradeEntrust.zhang"/>
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${tl.trade_deposit}" pattern="#0.####" />
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td <c:if test="${tl.trade_income lt 0}"> class="sell"</c:if>
                                <c:if test="${tl.trade_income gt 0}"> class="buy"</c:if>>
                            <c:choose>
                                <c:when test="${tl.trade_type_id eq 1 || tl.trade_type_id eq 3}">
                                </c:when>
                                <c:when test="${tl.trade_type_id eq 5 or tl.trade_type_id eq 6}">
                                    <fmt:formatNumber value="${tl.trade_income - tl.trade_fee }" pattern="#0.####" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${tl.trade_income }" pattern="#0.####" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${tl.trade_type_id eq 5 or tl.trade_type_id eq 6}">
                                    --
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${tl.trade_bfx_fee > 0 ? tl.trade_bfx_fee : tl.trade_fee}" pattern="#0.####" />${homeName == 'vtrade' ? '' : (tl.trade_bfx_fee > 0 ? '(bfx)' : (homeName=='trade' ? '(usdt)' : '('.concat(homeName).concat(')')))}
                                </c:otherwise>
                            </c:choose>
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
    var fmtTargetProfit = "<fmt:message key='msg.pg.trade.targetProfit'/>";//止盈
    var fmtReached =  '<fmt:message key="msg.pg.trade.reached"/>';//当最新成交价达到
    var fmtHour = '<fmt:message key="msg.pg.trade.hour"/>';//时
    var fmtiWillBe = '<fmt:message key="msg.pg.trade.iWillBe"/>';//我将以
    var fmtClearance = '<fmt:message key="msg.pg.trade.clearance"/>';//的单价挂单进行平仓
    var fmtStopLoss = '<fmt:message key="msg.pg.trade.stopLoss"/>';//止损
    var fmtStated = '<fmt:message key="msg.pg.trade.stated"/>';//说明
    var fmtStateCon = '<fmt:message key="msg.pg.trade.statedCon"/>';//止盈止损功能是为了降低系统风险，系统只负责挂价，不保证价格挂出后一定能成交
    var fmtThisFun = '<fmt:message key="msg.pg.trade.thisFun"/>';//本功能只对当前可用持仓有效
    var fmtLoginSure = '<fmt:message key="msg.pg.userHon.sure"/>';//确定
    var fmtClose = '<fmt:message key="msg.pg.trade.close"/>';//关闭
    var fmtFullLimitOrder = '<fmt:message key="msg.pg.trade.fmtFullLimitOrder"/>';//限价委托
    var fmtFullMarketOrder = '<fmt:message key="msg.pg.trade.fmtFullMarketOrder"/>';//市价委托
    var triPrice = '<fmt:message key="msg.pg.trade.triPrice"/>';//触发价格
    var entruePrice = '<fmt:message key="msg.pg.trade.entruePrice"/>';//委托价格
    var targetProfitStopLossTips = '<fmt:message key="msg.pg.trade.targetProfitStopLossTips"/>';//*注：触发价格采用的是标记价格，当标记价格达到强平价格之后以您委托价格去挂单，请合理设好价格。
    var closevolumn = '<fmt:message key="msg.pg.trade.closevolumn"/>';//平仓数量
    var higherThanPositionTips = '<fmt:message key="msg.pg.trade.higherThanPositionTips"/>';//平仓数量不能高于持仓量
    var exchangeAll = '<fmt:message key="msg.pg.userCenter.exchangeAll"/>';//全部
    var bestPriceOrder = '<fmt:message key="msg.pg.trade.bestPriceOrder"/>';//以当前市场最优价下单
    var targetProfitStopLossTips2 = '<fmt:message key="msg.pg.trade.targetProfitStopLossTips2"/>';//*注：剧烈行情存在止损单变成委托单的情况
    var PositionNotEnputTips = '<fmt:message key="msg.pg.trade.PositionNotEnputTips"/>';//平仓数量不能为空
    var requestCanMultiple = '<fmt:message key="msg.trade.request.canMultiple2"/>';//当前支持杠杆数：
</script>
