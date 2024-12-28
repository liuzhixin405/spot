package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "view_usermanager")
public class VIEWUsermanager implements Serializable {
	
	private static final long serialVersionUID = -7585347715233805000L;
	private long f_user_id;
	private int is_sub_user;
	private String level_name;
	private long credits;
	private int f_user_time;
	private int f_user_state;
	private String f_user_email;
	private String f_user_tel;
	private BigDecimal btc;
	private BigDecimal btc_locked;
	private String province_name;
	private String city_name;
	private String country_name;
	private BigDecimal btc_gain;
	private long invite_uid;

	public VIEWUsermanager() {
	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	public int getIs_sub_user() {
		return is_sub_user;
	}

	public void setIs_sub_user(int is_sub_user) {
		this.is_sub_user = is_sub_user;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

	public long getCredits() {
		return credits;
	}

	public void setCredits(long value) {
		credits = value;
	}

	public int getF_user_time() {
		return f_user_time;
	}

	public void setF_user_time(int value) {
		f_user_time = value;
	}

	public int getF_user_state() {
		return f_user_state;
	}

	public void setF_user_state(int value) {
		f_user_state = value;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String value) {
		f_user_email = value;
	}

	public BigDecimal getBtc() {
		return btc;
	}

	public void setBtc(BigDecimal value) {
		btc = value;
	}

	public BigDecimal getBtc_locked() {
		return btc_locked;
	}

	public void setBtc_locked(BigDecimal value) {
		btc_locked = value;
	}

	public String getProvince_name() {
		return province_name;
	}

	public void setProvince_name(String value) {
		province_name = value;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String value) {
		city_name = value;
	}

	public String getCountry_name() {
		return country_name;
	}

	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}

	public BigDecimal getBtc_gain() {
		return btc_gain;
	}

	public void setBtc_gain(BigDecimal btcGain) {
		btc_gain = btcGain;
	}

	public long getInvite_uid() {
		return invite_uid;
	}

	public void setInvite_uid(long inviteUid) {
		invite_uid = inviteUid;
	}

	public String getF_user_tel() {
		return f_user_tel;
	}

	public void setF_user_tel(String f_user_tel) {
		this.f_user_tel = f_user_tel;
	}

}
