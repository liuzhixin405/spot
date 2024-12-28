package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberDepositOnoff;

public interface BQMemberDepositOnoffService {
	
	/**
	 * 得到追加保证金
	 * @return
	 */
	String findOnDeposit();

	void update(BQMemberDepositOnoff depositOnOff);
	
	
	/************************************ for read only **************************************/
	
	BQMemberDepositOnoff readByUserId(long fUserId);
}
