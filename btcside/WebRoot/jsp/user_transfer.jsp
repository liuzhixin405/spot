<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>

<!-- user-资金划转弹框 -->
<div class="add-windows-user" id="user-fundsTtransfer-windows">
	<div id="tb-overlay-duiping" class="tb-overlay display-none"></div>
	<div class="add-wrap dp-alert-wrap invite-BFX" id="user-fundsTransfer-alert">
		<div class="risk-box" style="width: 429px;padding:0;border:none;">
			<form id="tradeTransferForm" name="tradeTransferForm" method="post" autocomplete="off">
				<div class="trade-fundsTtransfer-wrap">
					<h2>
						<fmt:message key="msg.pg.trade.transfer" />
					</h2>
					<a class="close-icon-btn" href="javascript:usercloseFundsAlert()"><i class="iconfont icon-guanbi"></i></a>
					<div class="trade-fundsTtransfer-input">
						<span><fmt:message key="msg.pg.userCenter.transferDirection"/></span>
						<input  readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-l" style="display: none;" name="fundsTtransferInputUser" id="fundsTtransferInputUser" placeholder="<fmt:message key="msg.pg.trade.To"/>">
						<em class="trade-fundsTtransfer-accountType">
							<fmt:message key="msg.pg.head.bibiAcount"/>
						</em>
						<input readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-s" name="fundsTtransferInputUser2" id="fundsTtransferInputUser2" placeholder="<fmt:message key="msg.pg.trade.from"/>">
						<i class="iconfont icon-huazhuan trade-changeTtransfer" onclick="changeTransferDirection('${actionName=='vCenter' ? 'vcontract' : actionName=='dCenter' ? 'delivery' : (actionName=='center' ? 'contract' : actionName)}');"></i>
						<input readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-l" style="display: none;" name="fundsTtransferInpuUser3" id="fundsTtransferInputUser3" placeholder="<fmt:message key="msg.pg.trade.from"/>">
						<em class="trade-fundsTtransfer-typeCode">
							<em id="showName"></em><fmt:message key="msg.pg.index.contract"/>
						</em>
						<input readonly="readonly" type="text" class="fundsTtransfer-input-msg fundsTtransfer-s" name="fundsTtransferInputUser4" id="fundsTtransferInputUser4" placeholder="<fmt:message key="msg.pg.trade.To"/>">
					</div>
					<div class="trade-fundsTtransfer-input">
						<span><fmt:message key="msg.pg.userCenter.transferNumber"/></span>
						<input type="text" class="trade-transferNumber" name="transferAmount" id="transferAmount" onkeyup="checkInputDigits(this,4);checkAmount(${btc });" onblur="checkNull()" placeholder="<fmt:message key="msg.pg.userCenter.writetransferNumber"/>"> 
						<em class="transferUnit"><span><c:if test="${actionName eq 'vCenter'}">BFC</c:if><c:if test="${actionName ne 'usdr'}">USDT</c:if><c:if test="${actionName eq 'usdr'}">USDR</c:if></span><span onclick="tranferAll()"><fmt:message key="msg.pg.userCenter.exchangeAll"/></span></em>
						<span class="total-num"><fmt:message key="msg.pg.userCenter.convertibleNumber"/><em id="trade-totalnum" class="totalNum"></em></span>
						<span class="transfer-record" onclick="transferRecord('${actionName=='vCenter' ? 'vcontract' : actionName=='dCenter' ? 'delivery' : (actionName=='center' ? 'contract' : actionName)}')"><fmt:message key="msg.pg.userCenter.TransferRecord"/></span>
					</div>
					<!-- 如果用户设置资金密码就显示，不设置就隐藏 ,默认关闭-->
					<div class="trade-fundsTtransfer-input" style="${payPwd ? 'display: block;' : 'display: none;' }">
						<span><fmt:message key="msg.pg.trade.assetPwd"/></span>
						<input type="password" autocomplete="new-password"  placeholder="<fmt:message key="msg.pg.userCenter.enterassetPwd"/>" class="form-control" id="payPwd" maxlength="20">
					</div>
					<div class="trade-fundsTtransfer-createAccount">
						<button type="submit" onclick="recharge('${actionName=='vCenter' ? 'vcontract' : actionName=='dCenter' ? 'delivery' : (actionName=='center' ? 'contract' : actionName)}'); return false;" class="fundsTtransfer-btn">
						<fmt:message key="msg.pg.userCenter.affirm"/>
						</button>
						<p class="error" id="amountmsg" style="color: red; margin-top: 5px;"></p>
						<p id="infomsg_transfer" style="margin-top: 5px;"></p>
					</div>
				</div>
			</form>
			<div class="clear"></div>
		</div>
    </div>
</div>
<script type="text/javascript" src="<%=basePath%>common/js/transfer.js?<%=rjs%>"></script>
