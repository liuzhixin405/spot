package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberInviteLog;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberInviteLogService {

	void save(BQMemberInviteLog bqil);
	
	void update(BQMemberInviteLog log);

	public long getTime();

	public long findInviterByUserId(long invited);
	
	
	/************************************ for read only **************************************/

	public PagerModel readPagerByUserId(long id);
	
	BQMemberInviteLog readByUserId(long userId);
	
	List<Object[]> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime, int limit);
	
	long readCountByUserId(long userId);
	
	Long readAllCount(long userId);
	
	List<Object[]> readAllByUserId(long userId);
	
	BQMemberInviteLog readAgent(long userId);
	
	List<Integer> readAllInviteesNotAgent(long userId, int oms);
}
