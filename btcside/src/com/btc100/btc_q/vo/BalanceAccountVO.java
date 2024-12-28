package com.btc100.btc_q.vo;

import java.io.Serializable;

public class BalanceAccountVO implements Serializable {

	private static final long serialVersionUID = -7123246666345753682L;

	private long userId;
	private String userName;
	private String accountName;
	private double allRecharge;
	private double manualRecharge;
	private double autoRecharge;
	private double manualAward;
	private double withdraw;
	private double withdrawFrozen;
	private double withdrawProc;
	private double manualWithdraw;
	private double manualDeductAward;
	private double changeInto;
	private double changeOut;
	private double changeIntoOTC;
	private double changeOutOTC;
	private double changeOutOTCFrozen;
	private double rebate;
	private double loanBorrow;				// 质押借币-借入
	private double loanBorrowOut;			// 质押借币-借出
	private double loanInterest;			// 质押借币-已还利息
	private double loanPledge;				// 质押借币-抵押
	private double loanInterestObtain;		// 质押借币-获得利息
	private double loanPaymentPledge;		// 质押借币-抵偿质押本息
	private double loanPaymentPledgeObtain;	// 质押借币-获得抵偿本息
	private double shouldTotal;
	private double shouldAvailable;
	private double shouldFrozen;
	private double actualTotal;
	private double actualAvailable;
	private double actualFrozen;
	private double netWorth;
	private double fee;
	private double fundingFee;
	private double income;
	private double holdDeposit;
	private double pendingFrozen;
	private double exchangeFrozen;
	private double exchangeDeduction;
	private double exchangeObtain;
	private double mine;
	private double rewards;
	private double bonus;
	private double buyIn;
	private double sellOut;
	private double buySpend;
	private double sellObtain;
	private double spotPendingFrozen;
	private double deducteFee;
	private double buyVipSpend;
	private double transferToSub;
	private double transferFromSub;
	private double allFundingFee;
	private double allIncome;
	private double allFee;
	private double netIncome;
	private double luckDrawObtain; 	//抽奖获得

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userid) {
		this.userId = userid;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String name) {
		this.userName = name;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public double getAllRecharge() {
		return this.allRecharge;
	}

	public void setAllRecharge(double recharge) {
		this.allRecharge = recharge;
	}

	public double getManualRecharge() {
		return this.manualRecharge;
	}

	public void setManualRecharge(double recharge) {
		this.manualRecharge = recharge;
	}

	public double getAutoRecharge() {
		return this.autoRecharge;
	}

	public void setAutoRecharge(double recharge) {
		this.autoRecharge = recharge;
	}

	public double getWithdraw() {
		return this.withdraw;
	}

	public void setWithdraw(double withdraw) {
		this.withdraw = withdraw;
	}

	public double getWithdrawFrozen() {
		return this.withdrawFrozen;
	}

	public void setWithdrawFrozen(double withdraw) {
		this.withdrawFrozen = withdraw;
	}

	public double getWithdrawProc() {
		return this.withdrawProc;
	}

	public void setWidthdrawProc(double withdraw) {
		this.withdrawProc = withdraw;
	}

	public double getManualWithdraw() {
		return this.manualWithdraw;
	}

	public void setManualWithdraw(double withdraw) {
		this.manualWithdraw = withdraw;
	}

	public double getChangeInto() {
		return this.changeInto;
	}

	public void setChangeInto(double into) {
		this.changeInto = into;
	}

	public double getChangeOut() {
		return changeOut;
	}

	public void setChangeOut(double changeOut) {
		this.changeOut = changeOut;
	}

	public double getChangeIntoOTC() {
		return changeIntoOTC;
	}

	public void setChangeIntoOTC(double changeIntoOTC) {
		this.changeIntoOTC = changeIntoOTC;
	}

	public double getChangeOutOTC() {
		return changeOutOTC;
	}

	public void setChangeOutOTC(double changeOutOTC) {
		this.changeOutOTC = changeOutOTC;
	}

	public double getChangeOutOTCFrozen() {
		return changeOutOTCFrozen;
	}

	public void setChangeOutOTCFrozen(double changeOutOTCFrozen) {
		this.changeOutOTCFrozen = changeOutOTCFrozen;
	}

	public double getRebate() {
		return this.rebate;
	}

	public void setRebate(double rebate) {
		this.rebate = rebate;
	}

	public double getShouldTotal() {
		return this.shouldTotal;
	}

	public void setShouldTotal(double shouldTotal) {
		this.shouldTotal = shouldTotal;
	}

	public double getShouldAvailable() {
		return this.shouldAvailable;
	}

	public void setShouldAvailable(double shouldAvailable) {
		this.shouldAvailable = shouldAvailable;
	}

	public double getShouldFrozen() {
		return this.shouldFrozen;
	}

	public void setShouldFrozen(double shouldFrozen) {
		this.shouldFrozen = shouldFrozen;
	}

	public double getActualTotal() {
		return this.actualTotal;
	}

	public void setActualTotal(double actualTotal) {
		this.actualTotal = actualTotal;
	}

	public double getActualAvailable() {
		return this.actualAvailable;
	}

	public void setActualAvailable(double actualAvailable) {
		this.actualAvailable = actualAvailable;
	}

	public double getActualFrozen() {
		return this.actualFrozen;
	}

	public void setActualFrozen(double actualFrozen) {
		this.actualFrozen = actualFrozen;
	}

	public double getNetWorth() {
		return this.netWorth;
	}

	public void setNetWorth(double netWorth) {
		this.netWorth = netWorth;
	}

	public double getFee() {
		return this.fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public double getIncome() {
		return this.income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public double getHoldDeposit() {
		return this.holdDeposit;
	}

	public void setHoldDeposit(double holdDeposit) {
		this.holdDeposit = holdDeposit;
	}

	public double getPendingFrozen() {
		return this.pendingFrozen;
	}

	public void setPendingFrozen(double pendingFrozen) {
		this.pendingFrozen = pendingFrozen;
	}

	public double getExchangeFrozen() {
		return this.exchangeFrozen;
	}

	public void setExchangeFrozen(double frozen) {
		this.exchangeFrozen = frozen;
	}

	public double getExchangeDeduction() {
		return this.exchangeDeduction;
	}

	public void setExchangeDeduction(double decuction) {
		this.exchangeDeduction = decuction;
	}

	public double getExchangeObtain() {
		return this.exchangeObtain;
	}

	public void setExchangeObtain(double obtain) {
		this.exchangeObtain = obtain;
	}

	public double getMine() {
		return this.mine;
	}

	public void setMine(double mine) {
		this.mine = mine;
	}

	public double getRewards() {
		return this.rewards;
	}

	public void setRewards(double rewards) {
		this.rewards = rewards;
	}

	public double getBonus() {
		return this.bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
	}

	public double getBuyIn() {
		return this.buyIn;
	}

	public void setBuyIn(double buy) {
		this.buyIn = buy;
	}

	public double getSellOut() {
		return this.sellOut;
	}

	public void setSellOut(double sell) {
		this.sellOut = sell;
	}

	public double getBuySpend() {
		return this.buySpend;
	}

	public void setBuySpend(double spend) {
		this.buySpend = spend;
	}

	public double getSellObtain() {
		return this.sellObtain;
	}

	public void setSellObtain(double obtain) {
		this.sellObtain = obtain;
	}

	public double getSpotPendingFrozen() {
		return this.spotPendingFrozen;
	}

	public void setSpotPendingFrozen(double frozen) {
		this.spotPendingFrozen = frozen;
	}

	public double getManualAward() {
		return manualAward;
	}

	public void setManualAward(double manualAward) {
		this.manualAward = manualAward;
	}

	public double getManualDeductAward() {
		return manualDeductAward;
	}

	public void setManualDeductAward(double manualDeductAward) {
		this.manualDeductAward = manualDeductAward;
	}

	public double getFundingFee() {
		return fundingFee;
	}

	public void setFundingFee(double fundingFee) {
		this.fundingFee = fundingFee;
	}

	public double getDeducteFee() {
		return deducteFee;
	}

	public void setDeducteFee(double deducteFee) {
		this.deducteFee = deducteFee;
	}

	public double getBuyVipSpend() {
		return buyVipSpend;
	}

	public void setBuyVipSpend(double buyVipSpend) {
		this.buyVipSpend = buyVipSpend;
	}

	public double getTransferToSub() {
		return transferToSub;
	}

	public void setTransferToSub(double transferToSub) {
		this.transferToSub = transferToSub;
	}

	public double getTransferFromSub() {
		return transferFromSub;
	}

	public void setTransferFromSub(double transferFromSub) {
		this.transferFromSub = transferFromSub;
	}

	public double getAllFundingFee() {
		return allFundingFee;
	}

	public void setAllFundingFee(double allFundingFee) {
		this.allFundingFee = allFundingFee;
	}

	public double getAllIncome() {
		return allIncome;
	}

	public void setAllIncome(double allIncome) {
		this.allIncome = allIncome;
	}

	public double getAllFee() {
		return allFee;
	}

	public void setAllFee(double allFee) {
		this.allFee = allFee;
	}

	public double getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(double netIncome) {
		this.netIncome = netIncome;
	}

	public double getLoanBorrow() {
		return loanBorrow;
	}

	public void setLoanBorrow(double loanBorrow) {
		this.loanBorrow = loanBorrow;
	}

	public double getLoanBorrowOut() {
		return loanBorrowOut;
	}

	public void setLoanBorrowOut(double loanBorrowOut) {
		this.loanBorrowOut = loanBorrowOut;
	}

	public double getLoanInterest() {
		return loanInterest;
	}

	public void setLoanInterest(double loanInterest) {
		this.loanInterest = loanInterest;
	}

	public double getLoanPledge() {
		return loanPledge;
	}

	public void setLoanPledge(double loanPledge) {
		this.loanPledge = loanPledge;
	}

	public double getLoanInterestObtain() {
		return loanInterestObtain;
	}

	public void setLoanInterestObtain(double loanInterestObtain) {
		this.loanInterestObtain = loanInterestObtain;
	}

	public double getLoanPaymentPledgeObtain() {
		return loanPaymentPledgeObtain;
	}

	public void setLoanPaymentPledgeObtain(double loanPaymentPledgeObtain) {
		this.loanPaymentPledgeObtain = loanPaymentPledgeObtain;
	}

	public double getLoanPaymentPledge() {
		return loanPaymentPledge;
	}

	public void setLoanPaymentPledge(double loanPaymentPledge) {
		this.loanPaymentPledge = loanPaymentPledge;
	}

	public double getLuckDrawObtain() {
		return luckDrawObtain;
	}

	public void setLuckDrawObtain(double luckDrawObtain) {
		this.luckDrawObtain = luckDrawObtain;
	}
}