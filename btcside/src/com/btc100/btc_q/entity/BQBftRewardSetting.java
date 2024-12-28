package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bft_reward_setting")
public class BQBftRewardSetting implements Serializable {
	
	private static final long serialVersionUID = 65597508619027601L;
	
	private long id;
	private int currency_type_id;
	private BigDecimal login_reward;
	private BigDecimal register_reward;
	private BigDecimal invite_reward = BigDecimal.ZERO;
	private boolean bfx_rewarded = false;
	private boolean old_register_rewarded = false;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCurrency_type_id() {
		return this.currency_type_id;
	}
	
	public void setCurrency_type_id(int typeId) {
		this.currency_type_id = typeId;
	}
	
	public BigDecimal getLogin_reward() {
		return this.login_reward;
	}
	
	public void setLogin_reward(BigDecimal amount) {
		this.login_reward = amount;
	}
	
	public BigDecimal getInvite_reward() {
		return this.invite_reward;
	}
	
	public void setInvite_reward(BigDecimal amount) {
		this.invite_reward = amount;
	}
	
	public boolean getBfx_rewarded() {
		return this.bfx_rewarded;
	}
	
	public void setBfx_rewarded(boolean rewarded) {
		this.bfx_rewarded = rewarded;
	}
	
	public BigDecimal getRegister_reward() {
		return register_reward;
	}
	
	public void setRegister_reward(BigDecimal reward) {
		this.register_reward = reward;
	}
	
	public boolean getOld_register_rewarded() {
		return old_register_rewarded;
	}
	
	public void setOld_register_rewarded(boolean rewarded) {
		this.old_register_rewarded = rewarded;
	}
}
