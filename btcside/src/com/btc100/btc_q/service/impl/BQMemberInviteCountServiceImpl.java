package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberInviteCountDao;
import com.btc100.btc_q.entity.BQMemberInviteCount;
import com.btc100.btc_q.service.BQMemberInviteCountService;


@Service("bqMemberInviteCountService")
public class BQMemberInviteCountServiceImpl implements BQMemberInviteCountService {

	@Autowired
	private BQMemberInviteCountDao<BQMemberInviteCount> bqMemberInviteCountDao;

	public void save(BQMemberInviteCount bqic) {
		bqMemberInviteCountDao.save(bqic);		
	}

}
