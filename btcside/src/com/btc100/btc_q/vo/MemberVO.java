package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class MemberVO implements Serializable {
	
	private static final long serialVersionUID = 6576468421399455420L;

	private String id;
	
	private String userName; // 用户名
	
	private String realName; // 真实姓名
	
	private String nickName; //昵称
	
	private String img;
	
	private Boolean havePayPwd; // 支付密码
	
	private Boolean usePayPwd; // 支付密码
	
	private String state; // 用户状态
	
	private String stateName; //用户状态名称
	
	private String levelId;
	
	private String levelName;
	
	private Long points = 0L; // 用户积分
	
	private String lastLoginIp; // 上次登录IP地址
	
	private Long lastLoginTime; // 上次登录时间
	
	private Long registerTime; // 注册时间
	
	private BigDecimal available; // 可用余额
	
	private String shareSessionId;
	
	private String isSubUser;
	
	private String identity;

	private String zone;

	private String telNum = "";
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String name) {
		this.realName = name;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public Long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Long registerTime) {
		this.registerTime = registerTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getShareSessionId() {
		return shareSessionId;
	}

	public void setShareSessionId(String shareSessionId) {
		this.shareSessionId = shareSessionId;
	}

	public Boolean getHavePayPwd() {
		return havePayPwd;
	}

	public void setHavePayPwd(Boolean havePayPwd) {
		this.havePayPwd = havePayPwd;
	}

	public Boolean getUsePayPwd() {
		return usePayPwd;
	}

	public void setUsePayPwd(Boolean usePayPwd) {
		this.usePayPwd = usePayPwd;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public BigDecimal getAvailable() {
		return available;
	}

	public void setAvailable(BigDecimal available) {
		this.available = available;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getIsSubUser() {
		return isSubUser;
	}

	public void setIsSubUser(String isSubUser) {
		this.isSubUser = isSubUser;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}
}