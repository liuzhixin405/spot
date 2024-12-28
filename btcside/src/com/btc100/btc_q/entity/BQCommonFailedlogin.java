package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户登录失败次数统计，可以根据时间字段自行设置多长时间段内用户登录失败次数并进行限制(用户+ip 限制|ip限制)
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_common_failedlogin")
public class BQCommonFailedlogin implements Serializable {

	private static final long serialVersionUID = -8417741606421101029L;

	private String ip; // 失败IP
	private String username; // 失败时的用户名
	private int count; // 尝试次数
	private long lastupdate; // 最后一次尝试时间
	private String email;
	private int google_count; // 谷歌验证错误次数
	private long google_last; // 谷歌验证错误最后一次时间

	public BQCommonFailedlogin() {
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(long lastupdate) {
		this.lastupdate = lastupdate;
	}
	@Id
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getGoogle_count() {
		return google_count;
	}

	public void setGoogle_count(int google_count) {
		this.google_count = google_count;
	}

	public long getGoogle_last() {
		return google_last;
	}

	public void setGoogle_last(long google_last) {
		this.google_last = google_last;
	}

}
