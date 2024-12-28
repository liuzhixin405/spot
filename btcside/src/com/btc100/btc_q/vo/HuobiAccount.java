package com.btc100.btc_q.vo;

import java.io.Serializable;

public class HuobiAccount implements Serializable {

	private static final long serialVersionUID = -196429270748374014L;
	
	private long id = 0;
	private String type;
	private String state;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

}
