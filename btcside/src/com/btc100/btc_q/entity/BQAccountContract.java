package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_account_contract")
public class BQAccountContract implements Serializable {

	private static final long serialVersionUID = -5181760767577257781L;
	
	private int f_id;
	private long f_user_id;
	private String f_code;
	private BigDecimal f_initbtc;
	private BigDecimal f_btc;
	private BigDecimal f_lockbtc;
	private BigDecimal f_settled;

	public BQAccountContract() {
	}

	/**
	 * 合约钱包
	 * @param fUserId 用户ID
	 * @param fCode 合约号
	 * @param fInitbtc 初始btc数量
	 * @param fBtc 开仓可用
	 * @param fLockbtc 合约冻结资金
	 * @param fSettled 已结算资金
	 */
	public BQAccountContract(long fUserId, String fCode, BigDecimal fInitbtc, BigDecimal fBtc, BigDecimal fLockbtc, BigDecimal fSettled) {
		f_user_id = fUserId;
		f_code = fCode.toLowerCase();
		f_initbtc = fInitbtc;
		f_btc = fBtc;
		f_lockbtc = fLockbtc;
		f_settled = fSettled;
	}

	@Id
	@GeneratedValue
	public int getF_id() {
		return f_id;
	}

	public void setF_id(int value) {
		f_id = value;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String fCode) {
		f_code = fCode;
	}

	public BigDecimal getF_initbtc() {
		return f_initbtc;
	}

	public void setF_initbtc(BigDecimal fInitbtc) {
		f_initbtc = fInitbtc;
	}

	public BigDecimal getF_btc() {
		return f_btc;
	}

	public void setF_btc(BigDecimal fBtc) {
		f_btc = fBtc;
	}

	public BigDecimal getF_lockbtc() {
		return f_lockbtc;
	}

	public void setF_lockbtc(BigDecimal fLockbtc) {
		f_lockbtc = fLockbtc;
	}

	public BigDecimal getF_settled() {
		return f_settled;
	}

	public void setF_settled(BigDecimal fSettled) {
		f_settled = fSettled;
	}

}
