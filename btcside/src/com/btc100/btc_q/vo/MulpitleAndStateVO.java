package com.btc100.btc_q.vo;

import java.io.Serializable;

public class MulpitleAndStateVO implements Serializable {

	private static final long serialVersionUID = -1444263293592487225L;

	private int state;
	private String multiple;

	public MulpitleAndStateVO(int state, String multiple) {
		super();
		this.state = state;
		this.multiple = multiple;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

}
