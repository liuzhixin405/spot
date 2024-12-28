package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户提现/充值记录,主要记录用户金额变更(个人财务流水）
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_account_amount_log")
public class BQAccountAmountLog implements Serializable {

	private static final long serialVersionUID = -4666050149128081329L;

	private long log_id;
	private String title;				// 描述标题
	private int currency_type_id;		// 币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)
	private int operation;				// 操作类型(0:其它/1:充值/2:提现/3:交易/5更多扩展)
	private int direction;				// 1:加款 -1:减款
	private BigDecimal current_amount;	// 当前账户余额(bq_acount:btc/bq_account:ltc)
	private BigDecimal change_amount;	// 增减
	private BigDecimal lastest_amount;	// 变更后的金额
	private BigDecimal before_frozen;	// 变更前冻结
	private BigDecimal after_frozen;	// 变更后冻结
	private long dateline;				// 操作时间,unix时间格式
	private long f_user_id;				// 用户ID
	private String trans_no;			// 交易流水号
	private String trans_type;			//交易类型(充值/提现|交易|其它)
	private long record_id;				//交易分表id

	public BQAccountAmountLog() {

	}
	
	/**
	 * 
	 * @param title 描述标题
	 * @param currencyTypeId 币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)
	 * @param operation 操作类型(0:其它/1:充值/2:提现/3:交易/4:追加保证金/5:购买积分/6:奖励/7:转入/8:转出/9:兑换/10:母账户转入子账号/11:子账号转出到母账户)
	 * @param direction 1:加款 -1:减款
	 * @param currentAmount 当前账户余额(bq_acount:btc/bq_account:ltc)
	 * @param changeAmount 增减
	 * @param lastestAmount 变更后的金额
	 * @param dateline 操作时间,unix时间格式
	 * @param fUserId 用户ID
	 * @param transNo 交易流水号
	 * @param transType 描述(充值/提现|交易手续费|挂单/撤单|其它|转账)
	 * @param recordId 交易分表id
	 */
	public BQAccountAmountLog(String title, int currencyTypeId, int operation,int direction, BigDecimal currentAmount, BigDecimal changeAmount,
			BigDecimal lastestAmount, BigDecimal beforeFrozen, BigDecimal afterFrozen, long dateline, long fUserId,
			String transNo, String transType, long recordId) {
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
		trans_type = transType;
		record_id = recordId;
	}

	@Id
	@GeneratedValue
	public long getLog_id() {
		return log_id;
	}

	public void setLog_id(long logId) {
		log_id = logId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public BigDecimal getCurrent_amount() {
		return current_amount;
	}

	public void setCurrent_amount(BigDecimal currentAmount) {
		current_amount = currentAmount;
	}

	public BigDecimal getChange_amount() {
		return change_amount;
	}

	public void setChange_amount(BigDecimal changeAmount) {
		change_amount = changeAmount;
	}

	public BigDecimal getLastest_amount() {
		return lastest_amount;
	}

	public void setLastest_amount(BigDecimal lastestAmount) {
		lastest_amount = lastestAmount;
	}
	
	public BigDecimal getBefore_frozen() {
		return before_frozen;
	}
	
	public void setBefore_frozen(BigDecimal beforeFrozen) {
		this.before_frozen = beforeFrozen;
	}
	
	public BigDecimal getAfter_frozen() {
		return after_frozen;
	}
	
	public void setAfter_frozen(BigDecimal afterFrozen) {
		this.after_frozen = afterFrozen;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getTrans_no() {
		return trans_no;
	}

	public void setTrans_no(String transNo) {
		trans_no = transNo;
	}

	public void setTrans_type(String transType) {
		trans_type = transType;
	}
	
	public String getTrans_type() {
		return trans_type;
	}

	public long getRecord_id() {
		return record_id;
	}

	public void setRecord_id(long recordId) {
		record_id = recordId;
	}

}
