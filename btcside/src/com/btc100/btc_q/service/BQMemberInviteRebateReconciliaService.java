package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberInviteRebateReconcilia;

public interface BQMemberInviteRebateReconciliaService {

	void add(BQMemberInviteRebateReconcilia obj);
	
	void update(BQMemberInviteRebateReconcilia obj);
	
	BQMemberInviteRebateReconcilia get();

	List<BQMemberInviteRebateReconcilia> findAll(int limit);
	
	Long getTime();
	
	BQMemberInviteRebateReconcilia getByUserId(long userId);
	
	
	/************************************ for read only **************************************/
	
}
