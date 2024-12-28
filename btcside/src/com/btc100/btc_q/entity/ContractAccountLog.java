package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "account_log")
@Immutable
public class ContractAccountLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContractAccountLog() {
	}

	/**
	 * 增加日志变动
	 * @param title 描述标题
	 * @param currencyTypeId 币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)
	 * @param operation 操作类型(0:其它/1:转入/2:转出/3:交易/4:追加保证金)
	 * @param direction 1:加款 -1:减款
	 * @param currentAmount 当前账户余额(bq_acount:btc/bq_account:ltc)
	 * @param changeAmount 增减
	 * @param lastestAmount 变更后的金额
	 * @param dateline 操作时间,unix时间格式
	 * @param fUserId 用户ID
	 * @param transNo 交易流水号
	 * @param recordId 交易分表id
	 * @param fCode 合约号
	 */
	public ContractAccountLog(String title, int currencyTypeId, int operation, int direction, BigDecimal currentAmount, BigDecimal changeAmount,
			BigDecimal lastestAmount, BigDecimal beforeFrozen, BigDecimal afterFrozen, long dateline, long fUserId,
			String transNo, long recordId, String fCode) {
		this.title = title;
		currency_type_id = currencyTypeId;
		this.operation = operation;
		this.direction = direction;
		current_amount = currentAmount;
		change_amount = changeAmount;
		lastest_amount = lastestAmount;
		this.before_frozen = beforeFrozen;
		this.after_frozen = afterFrozen;
		this.dateline = dateline;
		f_user_id = fUserId;
		trans_no = transNo;
		record_id = recordId;
		f_code = fCode;
	}
	
	private long log_id;

	@Id
	@GeneratedValue
	public long getLog_id() {
		return log_id;
	}

	public void setLog_id(long value) {
		log_id = value;
	}

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String value) {
		title = value;
	}

	private int currency_type_id;

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int value) {
		currency_type_id = value;
	}

	private int operation;

	public int getOperation() {
		return operation;
	}

	public void setOperation(int value) {
		operation = value;
	}

	private int direction;

	public int getDirection() {
		return direction;
	}

	public void setDirection(int value) {
		direction = value;
	}

	private BigDecimal current_amount;

	public BigDecimal getCurrent_amount() {
		return current_amount;
	}

	public void setCurrent_amount(BigDecimal value) {
		current_amount = value;
	}

	private BigDecimal change_amount;

	public BigDecimal getChange_amount() {
		return change_amount;
	}

	public void setChange_amount(BigDecimal value) {
		change_amount = value;
	}

	private BigDecimal lastest_amount;

	public BigDecimal getLastest_amount() {
		return lastest_amount;
	}

	public void setLastest_amount(BigDecimal value) {
		lastest_amount = value;
	}

	private long dateline;

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long value) {
		dateline = value;
	}

	private long f_user_id;

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	private String trans_no;

	public String getTrans_no() {
		return trans_no;
	}

	public void setTrans_no(String value) {
		trans_no = value;
	}

	private long record_id;

	public long getRecord_id() {
		return record_id;
	}

	public void setRecord_id(long value) {
		record_id = value;
	}

	private String f_code;
	
	public String getF_code() {
		return f_code;
	}

	public void setF_code(String fCode) {
		f_code = fCode;
	}
	
	private BigDecimal before_frozen;	// 变更前冻结
	public BigDecimal getBefore_frozen() {
		return before_frozen;
	}
	
	public void setBefore_frozen(BigDecimal beforeFrozen) {
		this.before_frozen = beforeFrozen;
	}
	
	private BigDecimal after_frozen;	// 变更后冻结
	public BigDecimal getAfter_frozen() {
		return after_frozen;
	}
	
	public void setAfter_frozen(BigDecimal afterFrozen) {
		this.after_frozen = afterFrozen;
	}
	
}
