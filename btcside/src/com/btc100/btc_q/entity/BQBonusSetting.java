package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bonus_setting")
public class BQBonusSetting implements Serializable {
	
	private static final long serialVersionUID = -2453122621977151973L;
	
	private long id;
	private boolean running;
	private boolean first_done = false;
	private long begin_time = 1532059200L;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public boolean getRunning() {
		return this.running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean getFirst_done() {
		return first_done;
	}
	
	public void setFirst_done(boolean done) {
		this.first_done = done;
	}
	
	public long getBegin_time() {
		return this.begin_time;
	}
	
	public void setBegin_time(long time) {
		this.begin_time = time;
	}

}
