package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQMemberStatus;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberStatusService {
	
	// 保存
	public void save(BQMemberStatus bqMemberStatus);
	
	/**
	 * 更新用户的登录状态
	 * @param userId 用户ID
	 * @param IP 用户IP
	 */
	public void updateStatus(long userId, String IP);
	
	public void updateComeIn(long userId, BigDecimal comin);
	
	public int addComeIn(String code);
	
	
	/************************************ for read only **************************************/
	
	/**
	 * 查询用户的ip
	 * @param userId 用户ID，如果ID为0，则查询全部
	 * @return
	 */
	public PagerModel readUserIP(long userId);
	
	/**
	 * 模糊查询用户的ip
	 * @param registIp 注册IP
	 * @param lastIp 用户ID，如果ID为0，则查询全部
	 * @return
	 */
	public PagerModel readUserIP(String registIp, String lastIp);

	
}
