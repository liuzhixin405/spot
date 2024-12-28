package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class RewardRecordVo implements Serializable {

	private static final long serialVersionUID = -6078071507560914255L;
	
	private long record_time;
	private BigDecimal amount;
	private String note;
	
	public long getRecord_time() {
		return record_time;
	}
	
	public void setRecord_time(long time) {
		record_time = time;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}

}
