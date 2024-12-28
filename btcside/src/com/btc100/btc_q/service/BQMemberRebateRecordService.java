package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberRebateRecord;

public interface BQMemberRebateRecordService {
	
	public void save(BQMemberRebateRecord ba);
	
	public BQMemberRebateRecord readById();
	
}
