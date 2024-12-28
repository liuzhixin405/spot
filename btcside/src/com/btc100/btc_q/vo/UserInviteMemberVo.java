package com.btc100.btc_q.vo;

import java.io.Serializable;

public class UserInviteMemberVo implements Serializable {

	private static final long serialVersionUID = 622792003016825754L;

	private String userId; // 用户ID
	private String userName; // 用户名称
	private String level; // 等级
	private String points; // 积分
	private Long inviteNums; // 邀请会员数
	private Long allInviteNums; // 联盟总会员数
	private double bfxRebate; // bfx返利
	private double usdtRebate; // usdt返利

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public Long getInviteNums() {
		return inviteNums;
	}

	public void setInviteNums(Long inviteNums) {
		this.inviteNums = inviteNums;
	}

	public Long getAllInviteNums() {
		return allInviteNums;
	}

	public void setAllInviteNums(Long allInviteNums) {
		this.allInviteNums = allInviteNums;
	}

	public double getBfxRebate() {
		return bfxRebate;
	}

	public void setBfxRebate(double bfxRebate) {
		this.bfxRebate = bfxRebate;
	}

	public double getUsdtRebate() {
		return usdtRebate;
	}

	public void setUsdtRebate(double usdtRebate) {
		this.usdtRebate = usdtRebate;
	}

}
