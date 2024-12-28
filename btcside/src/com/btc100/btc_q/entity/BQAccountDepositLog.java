package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户所持合约保证金不足时，系统自动"追加/返还"保证金记录
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_account_deposit_log")
public class BQAccountDepositLog implements Serializable {

	private static final long serialVersionUID = -229867942186407406L;

	private long log_id;
	private int mt_id;// 持仓ID(bq_member_transaction:mt_id)
	private int transaction_id; // 合约id
	private String transaction_code;// 合约号
	private int direction; // -1:从可用金额扣除保证金 1:返还保证金到账户可用金额
	private BigDecimal deposit; // 变更金额
	private long f_user_id; // 用户ID
	private long dateline; // 操作时间

	public BQAccountDepositLog() {

	}

	@Id
	@GeneratedValue
	public long getLog_id() {
		return log_id;
	}

	public void setLog_id(long logId) {
		log_id = logId;
	}

	public int getMt_id() {
		return mt_id;
	}

	public void setMt_id(int mtId) {
		mt_id = mtId;
	}

	public int getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(int transactionId) {
		transaction_id = transactionId;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transactionCode) {
		transaction_code = transactionCode;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
