package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * 资金费用表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "v_funding_fee_record")
@Immutable
public class VFundingFeeRecord implements Serializable {

	private static final long serialVersionUID = -580109901270609686L;

	private long id;
	private String f_code; // 合约号
	private long f_user_id; // 用户id
	private int trans_type_id = 0; // 持仓方向
	private BigDecimal curr_price; // 当前价格
	private BigDecimal hold_count;// 持仓量
	private BigDecimal coefficient;// 合约乘数
	private BigDecimal position_value;// 合约价值
	private BigDecimal fee_rate;// 资金费率
	private BigDecimal funding_fee;// 资金费用
	private BigDecimal price_change_fee = BigDecimal.ZERO;// 价格变更抵扣费用
	private long dateline;// 更新时间

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

	public BigDecimal getFee_rate() {
		return fee_rate;
	}

	public void setFee_rate(BigDecimal fee_rate) {
		this.fee_rate = fee_rate;
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

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public BigDecimal getCurr_price() {
		return curr_price;
	}

	public void setCurr_price(BigDecimal curr_price) {
		this.curr_price = curr_price;
	}

	public BigDecimal getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(BigDecimal coefficient) {
		this.coefficient = coefficient;
	}

	public BigDecimal getHold_count() {
		return hold_count;
	}

	public void setHold_count(BigDecimal hold_count) {
		this.hold_count = hold_count;
	}

	public BigDecimal getPosition_value() {
		return position_value;
	}

	public void setPosition_value(BigDecimal position_value) {
		this.position_value = position_value;
	}

	public BigDecimal getFunding_fee() {
		return funding_fee;
	}

	public void setFunding_fee(BigDecimal funding_fee) {
		this.funding_fee = funding_fee;
	}

	public BigDecimal getPrice_change_fee() {
		return price_change_fee;
	}

	public void setPrice_change_fee(BigDecimal price_change_fee) {
		this.price_change_fee = price_change_fee;
	}

	public int getTrans_type_id() {
		return trans_type_id;
	}

	public void setTrans_type_id(int trans_type_id) {
		this.trans_type_id = trans_type_id;
	}

}
