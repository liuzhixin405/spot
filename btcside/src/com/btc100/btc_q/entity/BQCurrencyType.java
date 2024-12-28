package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_currency_type")
public class BQCurrencyType implements Serializable {

	private static final long serialVersionUID = 7876595979401043209L;

	private Integer type_id;
	private String type_name;
	private String description_zh;
	private String description_en;
	private String description_jp;
	private String description_kr;
	private String description_hk;
	private int state = 1;
	private BigDecimal withdraw_fee = BigDecimal.ZERO;
	private BigDecimal recharge_fee = BigDecimal.ZERO;
	private BigDecimal day_max_widthdraw = BigDecimal.ZERO;
	private BigDecimal manul_max_widthdraw = BigDecimal.ZERO;
	private BigDecimal max_widthdraw = BigDecimal.ZERO;
	private BigDecimal min_withdraw = BigDecimal.ZERO;
	private BigDecimal withdraw_fee_erc20 = BigDecimal.ZERO;
	private BigDecimal recharge_fee_erc20 = BigDecimal.ZERO;
	private BigDecimal withdraw_fee_trc20 = BigDecimal.ZERO;
	private BigDecimal recharge_fee_trc20 = BigDecimal.ZERO;
	private BigDecimal inside_withdraw_fee = BigDecimal.ZERO;
	private BigDecimal inside_recharge_fee = BigDecimal.ZERO;
	private boolean erc20 = false;
	private boolean trc20 = false;
	private boolean enable_recharge = true;
	private boolean enable_withdraw = true;

	@Id
	@GeneratedValue
	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer value) {
		type_id = value;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String value) {
		type_name = value;
	}

	public String getDescription_zh() {
		return description_zh;
	}

	public void setDescription_zh(String value) {
		description_zh = value;
	}

	public String getDescription_en() {
		return description_en;
	}

	public void setDescription_en(String value) {
		description_en = value;
	}

	public String getDescription_jp() {
		return description_jp;
	}

	public void setDescription_jp(String description_jp) {
		this.description_jp = description_jp;
	}

	public String getDescription_kr() {
		return description_kr;
	}

	public void setDescription_kr(String description_kr) {
		this.description_kr = description_kr;
	}

	public String getDescription_hk() {
		return description_hk;
	}

	public void setDescription_hk(String description_hk) {
		this.description_hk = description_hk;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public BigDecimal getWithdraw_fee() {
		return withdraw_fee;
	}

	public void setWithdraw_fee(BigDecimal fee) {
		this.withdraw_fee = fee;
	}

	public BigDecimal getRecharge_fee() {
		return recharge_fee;
	}

	public void setRecharge_fee(BigDecimal fee) {
		this.recharge_fee = fee;
	}

	public BigDecimal getDay_max_widthdraw() {
		return this.day_max_widthdraw;
	}

	public void setDay_max_widthdraw(BigDecimal dayMax) {
		this.day_max_widthdraw = dayMax;
	}

	public BigDecimal getManul_max_widthdraw() {
		return manul_max_widthdraw;
	}

	public void setManul_max_widthdraw(BigDecimal manul_max_widthdraw) {
		this.manul_max_widthdraw = manul_max_widthdraw;
	}

	public BigDecimal getMax_widthdraw() {
		return this.max_widthdraw;
	}

	public void setMax_widthdraw(BigDecimal max) {
		this.max_widthdraw = max;
	}

	public BigDecimal getMin_withdraw() {
		return this.min_withdraw;
	}

	public void setMin_withdraw(BigDecimal min) {
		this.min_withdraw = min;
	}

	public boolean getErc20() {
		return this.erc20;
	}

	public void setErc20(boolean erc20) {
		this.erc20 = erc20;
	}

	public BigDecimal getWithdraw_fee_erc20() {
		return withdraw_fee_erc20;
	}

	public void setWithdraw_fee_erc20(BigDecimal withdraw_fee_erc20) {
		this.withdraw_fee_erc20 = withdraw_fee_erc20;
	}

	public BigDecimal getRecharge_fee_erc20() {
		return recharge_fee_erc20;
	}

	public void setRecharge_fee_erc20(BigDecimal recharge_fee_erc20) {
		this.recharge_fee_erc20 = recharge_fee_erc20;
	}

	public boolean isEnable_recharge() {
		return enable_recharge;
	}

	public void setEnable_recharge(boolean enable_recharge) {
		this.enable_recharge = enable_recharge;
	}

	public boolean isEnable_withdraw() {
		return enable_withdraw;
	}

	public void setEnable_withdraw(boolean enable_withdraw) {
		this.enable_withdraw = enable_withdraw;
	}

	public BigDecimal getInside_withdraw_fee() {
		return inside_withdraw_fee;
	}

	public void setInside_withdraw_fee(BigDecimal inside_withdraw_fee) {
		this.inside_withdraw_fee = inside_withdraw_fee;
	}

	public BigDecimal getInside_recharge_fee() {
		return inside_recharge_fee;
	}

	public void setInside_recharge_fee(BigDecimal inside_recharge_fee) {
		this.inside_recharge_fee = inside_recharge_fee;
	}

	public boolean getTrc20() {
		return trc20;
	}

	public void setTrc20(boolean trc20) {
		this.trc20 = trc20;
	}

	public BigDecimal getWithdraw_fee_trc20() {
		return withdraw_fee_trc20;
	}

	public void setWithdraw_fee_trc20(BigDecimal withdraw_fee_trc20) {
		this.withdraw_fee_trc20 = withdraw_fee_trc20;
	}

	public BigDecimal getRecharge_fee_trc20() {
		return recharge_fee_trc20;
	}

	public void setRecharge_fee_trc20(BigDecimal recharge_fee_trc20) {
		this.recharge_fee_trc20 = recharge_fee_trc20;
	}
}
