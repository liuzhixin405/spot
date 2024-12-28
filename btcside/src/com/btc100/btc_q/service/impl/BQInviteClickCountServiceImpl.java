package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQInviteClickCountDao;
import com.btc100.btc_q.entity.BQInviteClickCount;
import com.btc100.btc_q.service.BQInviteClickCountService;

@Service("bqInviteClickCountService")
public class BQInviteClickCountServiceImpl implements BQInviteClickCountService {

	@Autowired
	private BQInviteClickCountDao<BQInviteClickCount> bqInviteClickCountDao;

	public void save(BQInviteClickCount bicc) {
		bqInviteClickCountDao.save(bicc);
	}

}
