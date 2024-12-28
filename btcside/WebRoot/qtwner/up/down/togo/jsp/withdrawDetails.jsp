<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="_basepage.jsp"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
	<head>
		<base href="<%=actionPath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>账面资金统计</title>
		<link rel="stylesheet" href="<%=jspPath%>css/reset.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/style.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="<%=jspPath%>css/invalid.css" type="text/css" media="screen" />
		<script type="text/javascript" charset="utf-8" src="<%=jspPath%>js/page.js"></script>
		
		<script type="text/javascript">
			function sure(qr, url) {
				if (qr != null) {
					if (confirm(qr)) {
						document.location.href = url;
					}
				} else {
					document.location.href = url;
				}
		
			}
		</script>
		
	</head>
	<body>
		<div id="main-content">
			<div class="clear"></div>
			<!-- End .clear -->
			<div class="content-box">
				<!-- Start Content Box -->
				<div class="content-box-header">
					<h3>
						用户 ${id} 账面资金统计
					</h3>
					<div class="clear"></div>
				</div>

				<div class="content-box-content">
					<div class="tab-content default-tab" id="tab1">
						<h4 style="float:left; margin-right: 10px;">总账户对账</h4>
						<c:forEach items="${currencyTypes}" var="type" varStatus="status">
							<a href="<%=actionPath%>managerCheckAccount/loadDetail.do?id=${id}&typeId=${type.type_id }">
								<font color="${current == type.type_id ? '#ff0000' : ''}">${type.type_name }</font>
							</a> &nbsp;
						</c:forEach>
						<div class="clear"></div>
						<table id="changecolor" style="margin-bottom: 20px;">
							<thead>
								<tr><th>用户</th> <th>总充值</th> <th>自动充值</th> 
								<th>人工充值</th> <th>管理员奖励</th> <th>人工提币</th> <th>管理员扣除奖励</th>
								<th>提币成功</th> <th>提币冻结</th> <th>提币处理中</th> <th>返利</th> 
								<th>兑换冻结</th> <th>兑换扣除</th> <th>兑换获得</th>
								<th>转出到合约</th> <th>从合约转入</th>
								</tr>
							</thead>

							<tbody>
								<tr><td>${generalBalance.userId}</td><td>${generalBalance.allRecharge}</td><td>${generalBalance.autoRecharge}</td>
									<td>${generalBalance.manualRecharge}</td><td>${generalBalance.manualAward}</td><td>${generalBalance.manualWithdraw}</td><td>${generalBalance.manualDeductAward}</td>
									<td>${generalBalance.withdraw}</td><td>${generalBalance.withdrawFrozen}</td><td>${generalBalance.withdrawProc}</td><td>${generalBalance.rebate}</td>
									<td>${generalBalance.exchangeFrozen}</td><td>${generalBalance.exchangeDeduction}</td><td>${generalBalance.exchangeObtain}</td>
									<td>${generalBalance.changeOut}</td><td>${generalBalance.changeInto}</td>
								</tr>
							</tbody>
						</table>
						
						<table id="changecolorr" style="margin-bottom: 20px;">
							<thead>
								<tr><th>母子转账转入</th> <th>母子转账转出</th> <th>购买vip</th> <th>抵扣手续费</th> <th>奖励</th> <th>挖矿所得</th> <th>分红</th> <th>币币买入</th> <th>币币卖出</th> <th>币币买入花费</th> <th>币币卖出获得</th>
								<th>币币挂单冻结</th> <th>转出到OTC</th> <th>转出到OTC冻结</th> <th>从OTC转入</th>
								</tr>
							</thead>
							<tbody>
								<tr>
								<td>${generalBalance.transferToSub}</td><td>${generalBalance.transferFromSub}</td><td>${generalBalance.buyVipSpend}</td><td>${generalBalance.deducteFee}</td><td>${generalBalance.rewards}</td><td>${generalBalance.mine}</td><td>${generalBalance.bonus}</td>
								<td>${generalBalance.buyIn}</td><td>${generalBalance.sellOut}</td><td>${generalBalance.buySpend}</td><td>${generalBalance.sellObtain}</td>
								<td>${generalBalance.spotPendingFrozen}</td> <td>${generalBalance.changeOutOTC}</td><td>${generalBalance.changeOutOTCFrozen}</td><td>${generalBalance.changeIntoOTC}</td>
								</tr>
							</tbody>
						</table>
						
						<table id="changecolorr" style="margin-bottom: 20px;">
							<thead>
								<tr>
								<th>质押借入</th> <th>质押利息</th> <th>质押抵押</th> <th>质押借出</th> <th>质押获利</th> <th>质押抵偿本息</th> <th>质押获取抵偿</th> <th>抽奖所得</th>
								<th>应有可用</th> <th>应有冻结</th> <th>现有可用</th> <th>现有冻结</th> <th>净值</th>
								</tr>
							</thead>
							<tbody>
								<tr>
								<td>${generalBalance.loanBorrow}</td><td>${generalBalance.loanInterest}</td><td>${generalBalance.loanPledge}</td><td>${generalBalance.loanBorrowOut}</td><td>${generalBalance.loanInterestObtain}</td><td>${generalBalance.loanPaymentPledge}</td><td>${generalBalance.loanPaymentPledgeObtain}</td><td>${generalBalance.luckDrawObtain}</td>
								<td>${generalBalance.shouldAvailable}</td><td>${generalBalance.shouldFrozen}</td><td>${generalBalance.actualAvailable}</td><td>${generalBalance.actualFrozen}</td><td>${generalBalance.netWorth}</td>
								</tr>
							</tbody>
						</table>
						
						
						
						<h4>合约账户对账</h4>
						<table id="changecolor1" style="margin-bottom: 20px;">
							<thead>
								<tr><th>用户</th> <th>合约号</th> <th>转入</th> <th>转出</th> <th>资金费</th> 
								<th>盈亏额</th> <th>手续费</th> <th>持单保证金+亏损</th> <th>挂单冻结</th> 
								<th>应有可用</th> <th>应有冻结</th> <th>现有可用</th> <th>现有冻结</th> 
								<th>净值</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${contractBalances}" var="balance" varStatus="status">
								<tr><td>${balance.userId}</td><td>${balance.accountName}</td><td>${balance.changeInto}</td><td>${balance.changeOut}</td><td>${balance.fundingFee}</td>
									<td>${balance.income} <a href="<%=actionPath%>${path}managerTrade/loadTradeLogs.do?partition=${code}&id=${balance.userId}&code=${balance.accountName}">明细</a></td>
									<td>${balance.fee}</td><td>${balance.holdDeposit}</td><td>${balance.pendingFrozen}</td>
									<td>${balance.shouldAvailable}</td><td>${balance.shouldFrozen}</td><td>${balance.actualAvailable}</td>
									<td>${balance.actualFrozen}</td><td>${balance.netWorth}</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
						
						<h4>交割合约账户对账</h4>
						<table id="changecolor1" style="margin-bottom: 20px;">
							<thead>
								<tr><th>用户</th> <th>合约号</th> <th>转入</th> <th>转出</th> <th>资金费</th> 
								<th>盈亏额</th> <th>手续费</th> <th>持单保证金+亏损</th> <th>挂单冻结</th> 
								<th>应有可用</th> <th>应有冻结</th> <th>现有可用</th> <th>现有冻结</th> 
								<th>净值</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="${deliveryContractBalances}" var="balance" varStatus="status">
								<tr><td>${balance.userId}</td><td>${balance.accountName}</td><td>${balance.changeInto}</td><td>${balance.changeOut}</td><td>${balance.fundingFee}</td>
									<td>${balance.income} <a href="<%=actionPath%>${current == 2 ? 'delivery/' : path }managerTrade/loadTradeLogs.do?partition=${code}&id=${balance.userId}&code=${balance.accountName}">明细</a></td>
									<td>${balance.fee}</td><td>${balance.holdDeposit}</td><td>${balance.pendingFrozen}</td>
									<td>${balance.shouldAvailable}</td><td>${balance.shouldFrozen}</td><td>${balance.actualAvailable}</td>
									<td>${balance.actualFrozen}</td><td>${balance.netWorth}</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
						
						<h4>总计</h4>
						<table id="changecolor2" style="margin-bottom: 20px;">
							<thead>
								<tr><th>用户</th> <th>应有可用</th> <th>应有冻结</th> <th>现有可用</th> <th>现有冻结</th> <th>净值</th>
								</tr>
							</thead>

							<tbody>
								<tr><td>${allBalance.userId}</td><td>${allBalance.shouldAvailable}</td><td>${allBalance.shouldFrozen}</td><td>${allBalance.actualAvailable}</td>
									<td>${allBalance.actualFrozen}</td><td>${allBalance.netWorth}</td>
								</tr>
							</tbody>
						</table>
						
						<table id="changecolor3" style="margin-bottom: 20px;">
							<thead>
								<tr><th>总资金费</th> <th>总盈亏</th> <th>总手续费</th> <th>净收入</th>
								</tr>
							</thead>

							<tbody>
								<tr><td>${allBalance.allFundingFee}</td><td>${allBalance.allIncome}</td>
									<td>${allBalance.allFee}</td><td>${allBalance.netIncome}</td>
								</tr>
							</tbody>
						</table>
						
						<script language="javascript" type="text/javascript">
							senfe("changecolor","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
							senfe("changecolorr","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
							senfe("changecolor1","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
							senfe("changecolor2","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
							senfe("changecolor3","#f8fbfc","#e5f1f4","#ecfbd4","#bce774");
						</script>
						
						<button onclick="javascript:history.go(-1);">返回</button>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
