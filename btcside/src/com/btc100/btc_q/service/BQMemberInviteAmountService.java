package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberInviteAmount;

public interface BQMemberInviteAmountService {

	public void updateClickByUserId(long userId);

	public void updateSuccByUserId(long uid);
	
	
	/************************************ for read only **************************************/
	
	public BQMemberInviteAmount readTodayAmount(long userId, String time);
	
	long readSumClick(long userId);
	
	long readSumSuccess(long userId);
	
	List<Object[]> readRanking(int limit);
}
