package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * VIP
 * 
 */
@Entity
@Table(name = "bq_member_level")
public class BQMemberLevel implements Serializable {

	private static final long serialVersionUID = -2302071976467225888L;

	private long level_id;// '等级ID',
	private String level_name;//'级别名称',
	private long level_credits;// '等级所需积分',
	private BigDecimal transaction_commission_rate;//'合约交易佣金比例',
	private BigDecimal broker_commission_rate;//'经纪人返佣金比例',
	private BigDecimal pending_commission_rate;//'挂单佣金比例',
	private BigDecimal eat_commission_rate;//'吃单佣金比例',
	private BigDecimal withdraws_cash;//虚拟货币提现手续费'
	private int state = 0; // 0-使用，1-隐藏
	private int delegation_tel;//'电话委托(0:不支持 >0支持n次/每)',
	private BigDecimal level_buy_amount;// '等级所需积分',
	public BQMemberLevel() {
	}
	
	@Id
	@GeneratedValue
	public long getLevel_id() {
		return level_id;
	}
	public void setLevel_id(long levelId) {
		level_id = levelId;
	}
	public String getLevel_name() {
		return level_name;
	}
	public void setLevel_name(String levelName) {
		level_name = levelName;
	}
	public long getLevel_credits() {
		return level_credits;
	}
	public void setLevel_credits(long levelCredits) {
		level_credits = levelCredits;
	}
	
	public BigDecimal getWithdraws_cash() {
		return withdraws_cash;
	}
	public void setWithdraws_cash(BigDecimal withdrawsCash) {
		withdraws_cash = withdrawsCash;
	}
	public int getDelegation_tel() {
		return delegation_tel;
	}
	public void setDelegation_tel(int delegationTel) {
		delegation_tel = delegationTel;
	}

	public BigDecimal getTransaction_commission_rate() {
		return transaction_commission_rate;
	}

	public void setTransaction_commission_rate(BigDecimal transactionCommissionRate) {
		transaction_commission_rate = transactionCommissionRate;
	}

	public BigDecimal getBroker_commission_rate() {
		return broker_commission_rate;
	}

	public void setBroker_commission_rate(BigDecimal brokerCommissionRate) {
		broker_commission_rate = brokerCommissionRate;
	}

	public BigDecimal getPending_commission_rate() {
		return pending_commission_rate;
	}

	public void setPending_commission_rate(BigDecimal pending_commission_rate) {
		this.pending_commission_rate = pending_commission_rate;
	}

	public BigDecimal getEat_commission_rate() {
		return eat_commission_rate;
	}

	public void setEat_commission_rate(BigDecimal eat_commission_rate) {
		this.eat_commission_rate = eat_commission_rate;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}

	public BigDecimal getLevel_buy_amount() {
		return level_buy_amount;
	}

	public void setLevel_buy_amount(BigDecimal level_buy_amount) {
		this.level_buy_amount = level_buy_amount;
	}
}
