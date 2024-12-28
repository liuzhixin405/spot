package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_apply_agent_area")
public class BQMemberApplyAgentArea implements Serializable {

	private static final long serialVersionUID = -7690146467523947414L;
	
	private long f_user_id;
	private String province_code;
	private String province_name;
	private String country_code;
	private String country_name;
	private int state;

	public BQMemberApplyAgentArea() {
	}

	public BQMemberApplyAgentArea(long fUserId, String provinceCode,
			String provinceName, String countryCode, String countryName,
			int state) {
		f_user_id = fUserId;
		province_code = provinceCode;
		province_name = provinceName;
		country_code = countryCode;
		country_name = countryName;
		this.state = state;
	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getProvince_code() {
		return province_code;
	}

	public void setProvince_code(String provinceCode) {
		province_code = provinceCode;
	}

	public String getProvince_name() {
		return province_name;
	}

	public void setProvince_name(String provinceName) {
		province_name = provinceName;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String countryCode) {
		country_code = countryCode;
	}

	public String getCountry_name() {
		return country_name;
	}

	public void setCountry_name(String countryName) {
		country_name = countryName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
