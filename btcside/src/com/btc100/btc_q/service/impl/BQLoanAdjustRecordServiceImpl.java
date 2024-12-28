package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQLoanAdjustRecordDao;
import com.btc100.btc_q.entity.BQLoanAdjustRecord;
import com.btc100.btc_q.service.BQLoanAdjustRecordService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqLoanAdjustRecordService")
public class BQLoanAdjustRecordServiceImpl implements BQLoanAdjustRecordService {

	@Autowired
	private BQLoanAdjustRecordDao<BQLoanAdjustRecord> bqLoanAdjustRecordDao;

	@Override
	public void save(BQLoanAdjustRecord setting) {
		bqLoanAdjustRecordDao.save(setting);
	}

	@Override
	public PagerModel readAllByUserId(long userId) {
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		
		return bqLoanAdjustRecordDao.searchHql("from BQLoanAdjustRecord where f_user_id = :userId order by record_time desc", params);
	}

}
