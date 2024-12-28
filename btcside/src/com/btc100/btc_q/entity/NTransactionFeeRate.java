package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 合约设置独立手续费率
 * 
 */
@Entity
@Table(name = "n_transaction_fee_rate")
public class NTransactionFeeRate implements Serializable {

	private static final long serialVersionUID = -4557883359002084581L;

	private long id;// 'id',
	private String f_code;// '合约号',
	private long level_id;// '等级ID',
	private String level_name;// '等级名称',
	private BigDecimal pending_commission_rate;// '挂单佣金比例',
	private BigDecimal eat_commission_rate;// '吃单佣金比例',

	public NTransactionFeeRate() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String f_code) {
		this.f_code = f_code;
	}

	public long getLevel_id() {
		return level_id;
	}

	public void setLevel_id(long level_id) {
		this.level_id = level_id;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
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

}
