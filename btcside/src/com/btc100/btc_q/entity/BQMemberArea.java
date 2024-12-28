package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_area")
public class BQMemberArea implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;	
	private long f_user_id;//'用户ID'
	private String country_code;//'国家代码',
	private String country_name;// '国家名称',
	private String province_code;// '省份代码',
	private String province_name;//省份名称',
	private String city_code;//'城市代码',
	private String city_name;//'城市名称',
	public BQMemberArea(){}
	@Id
	public long getF_user_id() {
		return f_user_id;
	}
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
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
	public String getCity_code() {
		return city_code;
	}
	public void setCity_code(String cityCode) {
		city_code = cityCode;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String cityName) {
		city_name = cityName;
	}
	

}
