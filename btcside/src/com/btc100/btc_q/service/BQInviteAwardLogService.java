package com.btc100.btc_q.service;

import java.math.BigDecimal;

public interface BQInviteAwardLogService {

	/**
	 * 经纪人邀请奖励日志，每笔交易都会进行奖励
	 * @param userId 用户ID
	 * @param title 标题(奖励原因)
	 * @param tradeFee 佣金
	 * @param uid 下线用户uid
	 * @param uemail 下线用户邮箱
	 * @param code 交易ID(trade_log:trade_id)， 这里指合约号码1411，1412。。。
	 */
	public void saveInvateWardLog(long userId, String title, BigDecimal tradeFee, long uid, String uemail, long code);

}
