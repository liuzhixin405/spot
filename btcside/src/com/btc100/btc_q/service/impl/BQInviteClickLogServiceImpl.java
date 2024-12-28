package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQInviteClickLogDao;
import com.btc100.btc_q.entity.BQInviteClickLog;
import com.btc100.btc_q.service.BQInviteClickLogService;



@Service("bqInviteClickLogService")

public class BQInviteClickLogServiceImpl implements BQInviteClickLogService { 

@Autowired
private BQInviteClickLogDao<BQInviteClickLog> bqInviteClickLogDao;

public void save(BQInviteClickLog bqcl) {
	bqInviteClickLogDao.save(bqcl);
	
}

}
