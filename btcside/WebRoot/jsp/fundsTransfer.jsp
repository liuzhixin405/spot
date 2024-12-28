<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!-- 交易页面资金划转弹框 -->
<div class="add-windows" id="fundsTtransfer-windows">
	<div id="background_overlay" class="background_overlay"></div>
	<div class="risk-box-funer">
		<form id="tradeTransferForm" name="tradeTransferForm" method="post" autocomplete="off">
			<div class="trade-fundsTtransfer-wrap">
				<h2>
					<fmt:message key="msg.pg.trade.transfer" />
				</h2>
				<a class="close-icon-btn" href="javascript:closeFundsAlert()"><i class="iconfont icon-guanbi"></i></a>
				<div class="trade-fundsTtransfer-input">
					<span><fmt:message key="msg.pg.userCenter.transferDirection"/></span>
					<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-l" style="display: none;" name="fundsTtransferInputMsg1" id="fundsTtransferInputMsg1" placeholder="<fmt:message key="msg.pg.trade.To"/>">
					<em class="trade-fundsTtransfer-accountType">
						<fmt:message key="msg.pg.head.bibiAcount"/>
					</em>
					<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-s" name="fundsTtransferInputMsg2" id="fundsTtransferInputMsg2" placeholder="<fmt:message key="msg.pg.trade.from"/>">
					<i class="iconfont icon-huazhuan trade-changeTtransfer" onclick="changeTransferDirection('${currentTransaction.transaction_code}', '${homeName=='vtrade' ? 'vcontract' : homeName=='dtrade' ? 'delivery' : (homeName=='trade' ? 'contract' : homeName)}');"></i>
					<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-l" style="display: none;" name="fundsTtransferInputMsg3" id="fundsTtransferInputMsg3" placeholder="<fmt:message key="msg.pg.trade.from"/>">
					<em class="trade-fundsTtransfer-typeCode">
						${typeCodeName}<fmt:message key="msg.pg.index.contract"/>
					</em>
					<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-s" name="fundsTtransferInputMsg4" id="fundsTtransferInputMsg4" placeholder="<fmt:message key="msg.pg.trade.To"/>">
				</div>
				<div class="trade-fundsTtransfer-input">
					<span><fmt:message key="msg.pg.userCenter.transferNumber"/></span>
					<input type="text" class="trade-transferNumber" autocomplete="off" name="transferAmount" id="transferAmount" onkeyup="checkInputDigits(this,4);checkAmount(${btc });" onblur="checkNull()" placeholder="<fmt:message key="msg.pg.userCenter.writetransferNumber"/>"> 
					<input type="text" style="display: none;"/>
					<em class="transferUnit"><span><c:if test="${homeName=='vtrade'}">BFC</c:if><c:if test="${homeName=='trade'}">USDT</c:if><c:if test="${homeName=='usdr'}">USDR</c:if></span><span onclick="tranferAll()"><fmt:message key="msg.pg.userCenter.exchangeAll"/></span></em>
					<span class="total-num"><fmt:message key="msg.pg.userCenter.convertibleNumber"/><em id="trade-totalnum" class="totalNum"></em></span>
					<span class="transfer-record" onclick="transferRecord('${homeName=='vtrade' ? 'vcontract' : homeName=='dtrade' ? 'delivery' : (homeName=='trade' ? 'contract' : homeName)}', '${currentTransaction.transaction_code}')"><fmt:message key="msg.pg.userCenter.TransferRecord"/></span>
				</div>
				<!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
				<div class="trade-fundsTtransfer-input" style="${payPwd ? 'display: block;' : 'display: none;' }">
					<span><fmt:message key="msg.pg.trade.assetPwd"/></span>
					<input type="password" autocomplete="new-password" placeholder="<fmt:message key="msg.pg.userCenter.enterassetPwd"/>" class="form-control" id="payPwd" maxlength="20">
				</div>
				<div class="trade-fundsTtransfer-createAccount">
					<button type="submit" onclick="recharge('${homeName=='vtrade' ? 'vcontract' : homeName=='dtrade' ? 'delivery' : (homeName=='trade' ? 'contract' : homeName)}', '${currentTransaction.transaction_code}', '${currentTransaction.transfer_in_enable}', '${currentTransaction.transfer_out_enable}'); return false;" class="fundsTtransfer-btn">
					<fmt:message key="msg.pg.userCenter.affirm"/>
					</button>
					<p class="error display-none" id="amountmsg" style="color: red; margin-top: 5px;"></p>
				</div>
			</div>
		</form>
		<div class="clear"></div>
    </div>
</div>
<script type="text/javascript" src="<%=basePath%>common/js/fundsTransfer.js?<%=rjs%>"></script>
