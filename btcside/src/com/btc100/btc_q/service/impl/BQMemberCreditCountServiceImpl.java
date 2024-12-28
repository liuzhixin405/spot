package com.btc100.btc_q.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberCreditCountDao;
import com.btc100.btc_q.entity.BQMemberCreditCount;
import com.btc100.btc_q.service.BQMemberCreditCountService;

@Service("bqMemberCreditCountService")
public class BQMemberCreditCountServiceImpl implements BQMemberCreditCountService{
	@Autowired
	private BQMemberCreditCountDao<BQMemberCreditCount> bqMemberCreditCountDao;
	
	public void save(BQMemberCreditCount bqcco) {
		bqMemberCreditCountDao.save(bqcco);
	}
}
