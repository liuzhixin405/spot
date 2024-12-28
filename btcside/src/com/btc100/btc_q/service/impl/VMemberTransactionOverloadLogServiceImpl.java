package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.VMemberTransactionOverloadLogDao;
import com.btc100.btc_q.entity.VMemberTransactionOverloadLog;
import com.btc100.btc_q.service.VMemberTransactionOverloadLogService;


@Service("vMemberTransactionOverloadLogService")
public class VMemberTransactionOverloadLogServiceImpl implements VMemberTransactionOverloadLogService { 

	@Autowired
	private VMemberTransactionOverloadLogDao<VMemberTransactionOverloadLog> vMemberTransactionOverloadLogDao;
	
	@Override
	public void savLog(VMemberTransactionOverloadLog log) {
		vMemberTransactionOverloadLogDao.save(log);
	}

}
