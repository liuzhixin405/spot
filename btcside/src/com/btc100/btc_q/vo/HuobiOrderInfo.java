package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class HuobiOrderInfo implements Serializable {

	private static final long serialVersionUID = 2384659677319682821L;
	
	public static final String STATE_SUBMITTING = "submitting";
	public static final String STATE_SUBMITTED = "submitted";
	public static final String STATE_PARTIAL_FILLED = "partial-filled";
	public static final String STATE_PARTIAL_CANCELED = "partial-canceled";
	public static final String STATE_FILLED = "filled";
	public static final String STATE_CANCELED = "canceled";
	
	private long id;
	private BigDecimal amount;
	private BigDecimal fieldAmount;
	private BigDecimal price;
	private BigDecimal dealCash;
	private String state;
	private String symbol;
	private String type;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public BigDecimal getFieldAmount() {
		return fieldAmount;
	}
	
	public void setFieldAmount(BigDecimal amount) {
		this.fieldAmount = amount;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public BigDecimal getDealCash() {
		return this.dealCash;
	}
	
	public void setDealCash(BigDecimal cash) {
		this.dealCash = cash;
	}
}
