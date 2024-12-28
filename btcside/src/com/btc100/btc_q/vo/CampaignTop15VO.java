package com.btc100.btc_q.vo;
public class CampaignTop15VO {
	private int num;
	private String email;
	private double rate;
	
	public CampaignTop15VO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CampaignTop15VO(int num, String email, double rate) {
		super();
		this.num = num;
		this.email = email;
		this.rate = rate;
	}

	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}

	
}
