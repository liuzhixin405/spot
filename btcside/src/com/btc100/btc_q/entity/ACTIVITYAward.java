package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.btc100.btc_q.util.StringUtil;

@Entity
@Table(name = "activity_award")
public class ACTIVITYAward implements Serializable {

	private static final long serialVersionUID = 2469758520469548776L;

	public ACTIVITYAward() {
	}

	private int f_id;

	@Id
	@GeneratedValue
	public int getF_id() {
		return f_id;
	}

	public void setF_id(int value) {
		f_id = value;
	}

	private long f_user_id;

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	private BigDecimal f_getaward;

	public BigDecimal getF_getaward() {
		return f_getaward;
	}

	public void setF_getaward(BigDecimal value) {
		f_getaward = value;
	}

	private long f_time;

	public long getF_time() {
		return f_time;
	}

	public void setF_time(long value) {
		f_time = value;
	}

	private String f_type;

	public String getF_type() {
		return f_type;
	}

	public void setF_type(String value) {
		f_type = value;
	}

	private String f_state;

	public String getF_state() {
		return f_state;
	}

	public void setF_state(String value) {
		f_state = value;
	}

	private String f_email;

	public String getF_email() {
		if (!StringUtil.isEmpty(f_email))
			return f_email.substring(0, 2) + "******" + f_email.substring(f_email.lastIndexOf('.'), f_email.length());
		else
			return "";
	}

	public void setF_email(String fEmail) {
		f_email = fEmail;
	}

}
