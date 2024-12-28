package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_deposit_onoff")
public class BQMemberDepositOnoff implements Serializable {

	private static final long serialVersionUID = -3634791325760262190L;

	public BQMemberDepositOnoff() {
	}

	private long f_user_id;

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	private int value;

	public int getValue() {
		return value;
	}

	public void setValue(int _value) {
		value = _value;
	}

}
