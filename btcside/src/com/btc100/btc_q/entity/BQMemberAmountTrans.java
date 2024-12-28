package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 交易的第二个操作，会扣除手续费
 * @author Administrator
 *
 */
@Entity
@Table(name = "bq_member_amount_trans_fee")
public class BQMemberAmountTrans implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long record_id;
	private long f_user_id;//用户ID
	private String title;//标题说明
	private BigDecimal amount;//金额
	private long trade_id;//与其交易ID (trade_log:trade_id)
	private long dateline;//unix时间格式
	
	public BQMemberAmountTrans() {
	}
	public BQMemberAmountTrans(long fUserId, String title, BigDecimal amount, long tradeId, long dateline) {
		f_user_id = fUserId;
		this.title = title;
		this.amount = amount;
		trade_id = tradeId;
		this.dateline = dateline;
	}
	public long getRecord_id() {
		return record_id;
	}
	public void setRecord_id(long recordId) {
		record_id = recordId;
	}
	public long getF_user_id() {
		return f_user_id;
	}
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public long getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(long tradeId) {
		trade_id = tradeId;
	}
	public long getDateline() {
		return dateline;
	}
	public void setDateline(long dateline) {
		this.dateline = dateline;
	}
	
	
	
}
