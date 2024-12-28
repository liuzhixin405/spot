package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户购买VIP等级记录
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_member_level_buy_amount_log")
public class BQMemberLevelBuyAmountLog implements Serializable {

	private static final long serialVersionUID = -4666050149128081325L;

	private long log_id;
	private int currency_type_id;		// 币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)
	private BigDecimal change_amount;	// 增减
	private long dateline;				// 操作时间,unix时间格式
	private long f_user_id;				// 用户ID

	private long before_level_name_id;	// 变更前的等级ID
	private long after_level_name_id;	// 变更后的等级ID

	private String before_level_name;	// 变更前的级别名称
	private String after_level_name;	// 变更后的级别名称
	
	private BigDecimal bfx_lock;
	private BigDecimal bfx_unlock;

	public BQMemberLevelBuyAmountLog() {

	}
	
	/**
	 * 
	 * @param currencyTypeId 币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)
	 * @param currencyTypeName 币种名称
	 * @param changeAmount 增减
	 * @param dateline 操作时间,unix时间格式
	 * @param fUserId 用户ID
	 * @param beforeLevelNameId 变更前的等级ID
	 * @param afterLevelNameId 变更后的等级ID
	 * @param beforeLevelName 变更前的级别名称
	 * @param afterLevelName 变更后的级别名称
	 */
	
	public BQMemberLevelBuyAmountLog(int currencyTypeId, BigDecimal changeAmount, long dateline, long fUserId, long beforeLevelNameId, long afterLevelNameId, String beforeLevelName, String afterLevelName) {
		currency_type_id = currencyTypeId;
		change_amount = changeAmount;
		this.dateline = dateline;
		f_user_id = fUserId;
		before_level_name_id = beforeLevelNameId;
		after_level_name_id = afterLevelNameId;
		before_level_name = beforeLevelName;
		after_level_name = afterLevelName;
		
	}

	@Id
	@GeneratedValue
	public long getLog_id() {
		return log_id;
	}

	public void setLog_id(long logId) {
		log_id = logId;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currencyTypeId) {
		currency_type_id = currencyTypeId;
	}

	public BigDecimal getChange_amount() {
		return change_amount;
	}

	public void setChange_amount(BigDecimal changeAmount) {
		change_amount = changeAmount;
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

	public long getBefore_level_name_id() {
		return before_level_name_id;
	}

	public void setBefore_level_name_id(long before_level_name_id) {
		this.before_level_name_id = before_level_name_id;
	}

	public long getAfter_level_name_id() {
		return after_level_name_id;
	}

	public void setAfter_level_name_id(long after_level_name_id) {
		this.after_level_name_id = after_level_name_id;
	}

	public String getBefore_level_name() {
		return before_level_name;
	}

	public void setBefore_level_name(String before_level_name) {
		this.before_level_name = before_level_name;
	}

	public String getAfter_level_name() {
		return after_level_name;
	}

	public void setAfter_level_name(String after_level_name) {
		this.after_level_name = after_level_name;
	}

	public BigDecimal getBfx_lock() {
		return bfx_lock;
	}

	public void setBfx_lock(BigDecimal bfx_lock) {
		this.bfx_lock = bfx_lock;
	}

	public BigDecimal getBfx_unlock() {
		return bfx_unlock;
	}

	public void setBfx_unlock(BigDecimal bfx_unlock) {
		this.bfx_unlock = bfx_unlock;
	}

}
