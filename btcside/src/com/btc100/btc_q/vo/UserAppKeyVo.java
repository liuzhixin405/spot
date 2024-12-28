package com.btc100.btc_q.vo;

import java.io.Serializable;

public class UserAppKeyVo implements Serializable {

	private static final long serialVersionUID = 4486969840791525491L;

	private long id;
	private String name;
	private String password;
	private String appKey;
	private String appSecret;
	private String ip;

	public UserAppKeyVo() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
