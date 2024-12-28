<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<c:if test="${payPwd }">
	<style type="text/css">
.full-cont .left-cont-box .canver-parent {
	padding-bottom: 390px;
}

.full-cont .trade-manager-tab {
	height: 390px;
}

.full-cont .right-cont-box .right-cont-top {
	padding-bottom: 0;
}
</style>
</c:if>

<div class="lt-wrap order-panel" style="display:none;" id="full-stop-loss">
	<form action="" method="post">
	  <div class="trade-main">
		<div class="trade-detail" style="margin-bottom: 7px;">
<%--			<p class="lever-control-select" id="stopLossLeverControl">--%>
				<div class="lt" onmouseover="showSelectOptions();" onmouseout="hideSelectOptions();">
					<div class="lever-control-input">
						<label><fmt:message key="msg.pg.trade.myPosition" />：</label>
						<input type="text" readonly="readonly"  unselectable="on" class="input-inner input-inner-new" value=""/>
						<em class="triger-arrow-select triger-arrow-header"></em>
					</div>
					<div class="select-options-item">
						<div class="select-options-box" id="selectOptionsBox">
							<c:forEach items="${buyMemberTransaction}" var="tran" varStatus="l">
								<span class="option-multiples" onclick="mtClick(${tran.mt_id}, this, ${tran.averagePrice}, ${tran.qpPrice}, ${tran.trade_type_id});"><fmt:message key="msg.pg.trade.much" />x${tran.multiple}</span>
							</c:forEach>
							<c:forEach items="${sellMemberTransaction}" var="tran" varStatus="l">
								<span class="option-multiples" onclick="mtClick(${tran.mt_id}, this, ${tran.averagePrice}, ${tran.qpPrice}, ${tran.trade_type_id});"><fmt:message key="msg.pg.trade.guessEmpty" />x${tran.multiple}</span>
							</c:forEach>
						</div>
					</div>
				</div>
				<div class="rt" onmouseover="showTypeOptions();" onmouseout="hideTypeOptions();">
					<div class="type-control-input">
						<input type="text" readonly="readonly"  unselectable="on" class="type-input-inner input-inner-new" value="<fmt:message key="msg.pg.trade.fmtFullLimitOrder"/>"/>
						<em class="triger-arrow-select triger-arrow-header"></em>
					</div>
					<div class="type-options-item">
						<div class="type-options-box">
							<span class="type-option type-option-limit" onclick="typeOptionsBtnChange(this, 0)"><fmt:message key="msg.pg.trade.fmtFullLimitOrder"/></span>
							<span class="type-option type-option-market" onclick="typeOptionsBtnChange(this, 1)"><fmt:message key="msg.pg.trade.fmtFullMarketOrder"/></span>
						</div>
					</div>
				</div>
<%--			</p>--%>
		</div>
		<input type="hidden" id="mt_avg_price" value="">
		<input type="hidden" id="mt_qp_price" value="">
		<input type="hidden" id="mt_direction" value="">
		<input type="hidden" id="mt_mtid" value="">
		<input type="hidden" name="orderType" id="mt_OrderType" value="" />
		
		<div class="trade-detail checkbox-detail" style="margin-bottom: 7px;line-height: 16px;">
			<input type="checkbox" class="ipt" id="mt_has_win" />
			<span class="label">
				<em><fmt:message key="msg.pg.trade.targetProfit" /></em>
<%--				 <input type="hidden" id="mt_stop_win_price" readonly="readonly" onkeyup="checkInputDigits(this, ${digits});" />--%>
			<span class="label sell" id="mt_win_error"></span>
			</span>
		</div>
		  <div class="trade-detail relative ie-bug1" style="margin-bottom: 7px;">
			  <%--			<span><fmt:message key="msg.trade.stopwintriprice"/>：--%>
			  <input type="text" id="mt_stop_win_price" class="validate[required,number] form-control order-input price-input-height" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.triPrice"/>"
					 onblur="mt_check_pending_win_triPrice();" onkeyup="checkInputDigits(this, ${digits});" />
			  <input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;" />
			  <div class="unit order-unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
			  <%--			</span>--%>
		  </div>

		  <div class="trade-detail relative ie-bug1 " style="margin-bottom: 7px;">
			<input type="text" class="validate[required,number] form-control order-input price-input-height" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.entruePrice"/>"
				id="mt_pending_win_price" onblur="mt_check_pending_win_price();" onkeyup="checkInputDigits(this, ${digits});" />
			<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;" />
			<div class="unit order-unit pending_loss_unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
		</div>


<%--		<div class="stop-win-loss-line" style="margin-bottom: 13px"></div>--%>
				
		<div class="trade-detail checkbox-detail" style="margin-bottom: 7px;line-height: 16px;">
			<input type="checkbox" class="ipt" id="mt_has_loss" />
			<span class="label">
				<em><fmt:message key="msg.pg.trade.stopLoss" /></em>
				<%-- <input type="hidden" id="mt_stop_loss_price" readonly="readonly" onkeyup="checkInputDigits(this, ${digits});" />  --%>
			<span class="label sell" id="mt_loss_error"></span>
			</span>
		</div>

		  <div class="trade-detail relative ie-bug1 " style="margin-bottom: 7px;">
			  <%--			<span><fmt:message key="msg.trade.stoplosstriprice"/>：--%>
			  <input type="text" id="mt_stop_loss_price" class="validate[required,number] form-control order-input price-input-height" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.triPrice"/>"
					 onblur="mt_check_pending_loss_triPrice();" onkeyup="checkInputDigits(this, ${digits});" />
			  <input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;" />
			  <div class="unit order-unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
			  <%--			</span>--%>
		  </div>
		  <div class="trade-detail relative ie-bug1 " style="margin-bottom: 7px;">
			<input type="text" class="validate[required,number] form-control order-input price-input-height" autocomplete="off" placeholder="<fmt:message key="msg.pg.trade.entruePrice"/>"
				id="mt_pending_loss_price" onblur="mt_check_pending_loss_price();" onkeyup="checkInputDigits(this, ${digits});" />
			<input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;" />
			<div class="unit order-unit pending_loss_unit">${homeName=='vtrade' ? 'BFC' : (homeName=='usdr' ? homeName.toUpperCase() : 'USDT')}</div>
		</div>

		<div class="trade-detail relative ie-bug1 " style="margin-bottom: 7px;">
			<span style="font-size: 12px;color: #94ABC0;"><fmt:message key="msg.pg.trade.closevolumn"/>：
			  <input type="text" id="mt_number_positions_amount" class="validate[required,number] form-control order-input amount-input-height" autocomplete="off"
					 onblur="mt_check_number_positions_amount();" placeholder="<fmt:message key="msg.pg.trade.closevolumn"/>"
					 onkeyup="checkInputDigits(this, ${digits});" />
			  <input type="text" style="width: 0; height: 0; position: absolute; top: -100px; left: -100px;" />
			  <div class="unit all-unit" onclick="mt_allChangeAmount()"><fmt:message key="msg.pg.userCenter.exchangeAll"/></div>
				<span class="label sell" id="mt_amount_error"></span>
			</span>
		</div>
		
		<div class="btn-sub">
			<button type="button" onclick="mt_stopLossBtn();" value=""
				class="btn trade-btn active-bkg true  order-btn-style">
				<fmt:message key="msg.pg.userHon.sure" />
			</button>
		</div>
	  </div>
	</form>
</div>

<script type="text/javascript">
	var fmtChosePosition = "<fmt:message key='msg.pg.trade.chosePostion'/>";//请选择持仓或请登录
	var fmtMultigtAveg = "<fmt:message key='msg.pg.trade.multigtAveg'/>";//多仓止盈价须大于最新价
	var fmtMultiltAveg = "<fmt:message key='msg.pg.trade.multiltAveg'/>";//多仓止损价须小于最新价
	var fmtEmptyAveg = "<fmt:message key='msg.pg.trade.emptyltAveg'/>";//空仓止盈价须小于最新价
	var fmtEmptygtAveg = "<fmt:message key='msg.pg.trade.emptygtAveg'/>";//空仓止损价须大于最新价
	var fmtEmptyStrong = "<fmt:message key='msg.pg.trade.emptyltStrong'/>";//空仓止损价须小于强平价
	var fmtMultigtStrong = "<fmt:message key='msg.pg.trade.multigtStrong'/>";//多仓止损价须大于强平价
	var fmtFillProfit = "<fmt:message key='msg.pg.trade.fillProfit'/>";//请填写止盈价
	var fmtFillLoss = "<fmt:message key='msg.pg.trade.fillLoss'/>";//请填写止损价
	var fmtOptionStatusSuc = "<fmt:message key='msg.pg.trade.optionStatusSuc'/>";//操作成功
	var fmtOptionStatusFail = "<fmt:message key='msg.pg.trade.optionStatusFail'/>";//操作失败
	var multiplePriceLtAveragePrice = "<fmt:message key='msg.trade.request.multiplePriceLtAveragePrice'/>";//多仓止损价必须小于持仓均价！
	var shortPriceLtAveragePrice = "<fmt:message key='msg.trade.request.shortPriceLtAveragePrice'/>";//空仓止盈价必须小于持仓均价！
	var multiplePriceGtAveragePrice = "<fmt:message key='msg.trade.request.multiplePriceGtAveragePrice'/>";//多仓止盈价必须大于持仓均价！
	var shortPriceLtAveragePrice = "<fmt:message key='msg.trade.request.shortPriceLtAveragePrice'/>";//空仓止盈价必须小于持仓均价！
	var shortPriceGtAveragePrice = "<fmt:message key='msg.trade.request.shortPriceGtAveragePrice'/>";//空仓止损价必须大于持仓均价价！
	var entrustedPriceGtAveragePrice = "<fmt:message key='msg.trade.request.entrustedPriceGtAveragePrice'/>";//触发价必须大于持仓均价！
	var entrustedPriceLtAveragePrice = "<fmt:message key='msg.trade.request.entrustedPriceLtAveragePrice'/>";//触发价必须小于持仓均价！
</script>
