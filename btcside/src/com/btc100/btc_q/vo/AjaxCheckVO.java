package com.btc100.btc_q.vo;

import java.io.Serializable;

public class AjaxCheckVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;
	private String msg;
	private double frozen;

	public AjaxCheckVO() {
		super();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public double getFrozen() {
		return frozen;
	}

	public void setFrozen(double frozen) {
		this.frozen = frozen;
	}

}
