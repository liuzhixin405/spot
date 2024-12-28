package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.btc100.btc_q.common.Contents;

@Entity
@Table(name = "v_member_invite_rebate_log")
public class VMemberInviteRebateLog implements Serializable {

	private static final long serialVersionUID = -7239554265379348590L;

	private long id;
	private long user_id; // 用户id
	private long sub_user_id; // 下级用户id
	private String user_email; // 用户邮箱
	private String sub_user_email; // 下级用户邮箱
	private String user_level_name; // 用户等级名称
	private String sub_user_level_name; // 下级用户等级名称
	private BigDecimal commission_rate; // 用户返佣比例
	private BigDecimal sub_commission_rate; // 下级用户返佣比例
	private BigDecimal commission_rate_diff; // 返佣比例差
	private BigDecimal rebate_amount; // 返佣
	private long record_time; // 返佣时间,unix时间格式
	private long trade_id; // 交易id(trade_log:trade_id)
	private String bq_code; // 合约号(trade_log:transaction_code)
	private int currency_type_id = Contents.TRADE_CURRENCY_TYPE_ID;

	public VMemberInviteRebateLog() {

	}

	public VMemberInviteRebateLog(long userId, long subUserId, String userEmail, String subUserEmail,
			String userLevelName, String subUserLevelName, BigDecimal commissionRate, BigDecimal subCommissionRate,
			BigDecimal commissionRateDiff, BigDecimal rebateAmount, long recordTime, long tradeId, String bqCode, int currencyId) {
		user_id = userId;
		user_id = subUserId;
		user_email = userEmail;
		sub_user_email = subUserEmail;
		user_level_name = userLevelName;
		sub_user_level_name = subUserLevelName;
		commission_rate = commissionRate;
		sub_commission_rate = subCommissionRate;
		commission_rate_diff = commissionRateDiff;
		rebate_amount = rebateAmount;
		record_time = recordTime;
		trade_id = tradeId;
		bq_code = bqCode;
		currency_type_id = currencyId;
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getSub_user_id() {
		return sub_user_id;
	}

	public void setSub_user_id(long sub_user_id) {
		this.sub_user_id = sub_user_id;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getSub_user_email() {
		return sub_user_email;
	}

	public void setSub_user_email(String sub_user_email) {
		this.sub_user_email = sub_user_email;
	}

	public String getUser_level_name() {
		return user_level_name;
	}

	public void setUser_level_name(String user_level_name) {
		this.user_level_name = user_level_name;
	}

	public String getSub_user_level_name() {
		return sub_user_level_name;
	}

	public void setSub_user_level_name(String sub_user_level_name) {
		this.sub_user_level_name = sub_user_level_name;
	}

	public BigDecimal getCommission_rate() {
		return commission_rate;
	}

	public void setCommission_rate(BigDecimal commission_rate) {
		this.commission_rate = commission_rate;
	}

	public BigDecimal getSub_commission_rate() {
		return sub_commission_rate;
	}

	public void setSub_commission_rate(BigDecimal sub_commission_rate) {
		this.sub_commission_rate = sub_commission_rate;
	}

	public BigDecimal getCommission_rate_diff() {
		return commission_rate_diff;
	}

	public void setCommission_rate_diff(BigDecimal commission_rate_diff) {
		this.commission_rate_diff = commission_rate_diff;
	}

	public BigDecimal getRebate_amount() {
		return rebate_amount;
	}

	public void setRebate_amount(BigDecimal rebate_amount) {
		this.rebate_amount = rebate_amount;
	}

	public long getRecord_time() {
		return record_time;
	}

	public void setRecord_time(long record_time) {
		this.record_time = record_time;
	}

	public long getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(long trade_id) {
		this.trade_id = trade_id;
	}

	public String getBq_code() {
		return bq_code;
	}

	public void setBq_code(String bq_code) {
		this.bq_code = bq_code;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}
	
	public void setCurrency_type_id(int currencyId) {
		this.currency_type_id = currencyId;
	}
}
