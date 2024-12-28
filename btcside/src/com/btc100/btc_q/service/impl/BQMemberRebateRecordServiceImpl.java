package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberRebateRecordDao;
import com.btc100.btc_q.entity.BQMemberRebateRecord;
import com.btc100.btc_q.service.BQMemberRebateRecordService;

@Service("bqMemberRebateRecordService")
public class BQMemberRebateRecordServiceImpl implements BQMemberRebateRecordService {
	
	@Autowired
	private  BQMemberRebateRecordDao<BQMemberRebateRecord> bqMemberRebateRecordDao;

	@Override
	public void save(BQMemberRebateRecord ba) {
		bqMemberRebateRecordDao.save(ba);
	}
	
	@Override
	public BQMemberRebateRecord readById() {
		return bqMemberRebateRecordDao.get("from BQMemberRebateRecord");
	}

}
