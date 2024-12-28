package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "delivery_stock_setting")
public class DeliveryStockSetting implements Serializable {

	private static final long serialVersionUID = 394973773989154756L;
	
	private Integer id;
	private int am_begin_time;
	private int am_end_time;
	private int pm_begin_time;
	private int pm_end_time;
	private String holiday_url;
	private Boolean holiday_url_using;
	private long transaction_id;
	private Boolean mon_state;
	private String mon_trade_time;
	private Boolean tue_state;
	private String tue_trade_time;
	private Boolean wed_state;
	private String wed_trade_time;
	private Boolean thu_state;
	private String thu_trade_time;
	private Boolean fri_state;
	private String fri_trade_time;
	private Boolean sat_state;
	private String sat_trade_time;
	private Boolean sun_state;
	private String sun_trade_time;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public int getAm_begin_time() {
		return am_begin_time;
	}

	public void setAm_begin_time(int am_begin_time) {
		this.am_begin_time = am_begin_time;
	}

	public int getAm_end_time() {
		return am_end_time;
	}

	public void setAm_end_time(int am_end_time) {
		this.am_end_time = am_end_time;
	}

	public int getPm_begin_time() {
		return pm_begin_time;
	}

	public void setPm_begin_time(int pm_begin_time) {
		this.pm_begin_time = pm_begin_time;
	}

	public int getPm_end_time() {
		return pm_end_time;
	}

	public void setPm_end_time(int pm_end_time) {
		this.pm_end_time = pm_end_time;
	}

	public String getHoliday_url() {
		return holiday_url;
	}

	public void setHoliday_url(String holiday_url) {
		this.holiday_url = holiday_url;
	}

	public Boolean getHoliday_url_using() {
		return holiday_url_using;
	}

	public void setHoliday_url_using(Boolean holiday_url_using) {
		this.holiday_url_using = holiday_url_using;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public Boolean getMon_state() {
		return mon_state;
	}

	public void setMon_state(Boolean mon_state) {
		this.mon_state = mon_state;
	}

	public String getMon_trade_time() {
		return mon_trade_time;
	}

	public void setMon_trade_time(String mon_trade_time) {
		this.mon_trade_time = mon_trade_time;
	}

	public Boolean getTue_state() {
		return tue_state;
	}

	public void setTue_state(Boolean tue_state) {
		this.tue_state = tue_state;
	}

	public String getTue_trade_time() {
		return tue_trade_time;
	}

	public void setTue_trade_time(String tue_trade_time) {
		this.tue_trade_time = tue_trade_time;
	}

	public Boolean getWed_state() {
		return wed_state;
	}

	public void setWed_state(Boolean wed_state) {
		this.wed_state = wed_state;
	}

	public String getWed_trade_time() {
		return wed_trade_time;
	}

	public void setWed_trade_time(String wed_trade_time) {
		this.wed_trade_time = wed_trade_time;
	}

	public Boolean getThu_state() {
		return thu_state;
	}

	public void setThu_state(Boolean thu_state) {
		this.thu_state = thu_state;
	}

	public String getThu_trade_time() {
		return thu_trade_time;
	}

	public void setThu_trade_time(String thu_trade_time) {
		this.thu_trade_time = thu_trade_time;
	}

	public Boolean getFri_state() {
		return fri_state;
	}

	public void setFri_state(Boolean fri_state) {
		this.fri_state = fri_state;
	}

	public String getFri_trade_time() {
		return fri_trade_time;
	}

	public void setFri_trade_time(String fri_trade_time) {
		this.fri_trade_time = fri_trade_time;
	}

	public Boolean getSat_state() {
		return sat_state;
	}

	public void setSat_state(Boolean sat_state) {
		this.sat_state = sat_state;
	}

	public String getSat_trade_time() {
		return sat_trade_time;
	}

	public void setSat_trade_time(String sat_trade_time) {
		this.sat_trade_time = sat_trade_time;
	}

	public Boolean getSun_state() {
		return sun_state;
	}

	public void setSun_state(Boolean sun_state) {
		this.sun_state = sun_state;
	}

	public String getSun_trade_time() {
		return sun_trade_time;
	}

	public void setSun_trade_time(String sun_trade_time) {
		this.sun_trade_time = sun_trade_time;
	}
	
}
