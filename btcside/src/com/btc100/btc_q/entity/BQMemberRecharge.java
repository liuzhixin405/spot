package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_recharge")
public class BQMemberRecharge implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;
	private long record_id;
	private int currency_type_id;// 币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)',
	private int operation;// 操作类型(1:充值/2:提现)',
	private BigDecimal amount;// '(充值/提现)金额',
	private BigDecimal fee;// '手续费(只在提现的时候有)',
	private int receice_card_type;// 1.普通充值, 2.在线充值, 3.奖励
	private String receice_card_no;// '接收卡号',
	private String ip;// 申请地址',
	private long dateline;// '申请时间',
	private long confirm_time;// 确认时间(充值的时候，需要人工确认后，再手动加款）',
	private int state;// '0:未受理 1:受理成功 -1:拒绝',
	private String remark;// '备注说明',
	private long f_user_id;// '用户ID',
	private String f_user_email;// '用户名',
	private String wallet_trade_id;// '钱包交易ID(确认到账)',
	private boolean usdt_erc20 = false; // usdt erc20钱包地址
	private boolean usdt_trc20 = false; // usdt trc20钱包地址

	public BQMemberRecharge() {
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String fUserEmail) {
		f_user_email = fUserEmail;
	}

	@Id
	@GeneratedValue
	public long getRecord_id() {
		return record_id;
	}

	public void setRecord_id(long recordId) {
		record_id = recordId;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currencyTypeId) {
		currency_type_id = currencyTypeId;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public int getReceice_card_type() {
		return receice_card_type;
	}

	public void setReceice_card_type(int receiceCardType) {
		receice_card_type = receiceCardType;
	}

	public String getReceice_card_no() {
		return receice_card_no;
	}

	public void setReceice_card_no(String receiceCardNo) {
		receice_card_no = receiceCardNo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public long getConfirm_time() {
		return confirm_time;
	}

	public void setConfirm_time(long confirmTime) {
		confirm_time = confirmTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getWallet_trade_id() {
		return wallet_trade_id;
	}

	public void setWallet_trade_id(String walletTradeId) {
		wallet_trade_id = walletTradeId;
	}

	public boolean getUsdt_erc20() {
		return usdt_erc20;
	}

	public void setUsdt_erc20(boolean usdt_erc20) {
		this.usdt_erc20 = usdt_erc20;
	}

	public boolean getUsdt_trc20() {
		return usdt_trc20;
	}

	public void setUsdt_trc20(boolean usdt_trc20) {
		this.usdt_trc20 = usdt_trc20;
	}

}
