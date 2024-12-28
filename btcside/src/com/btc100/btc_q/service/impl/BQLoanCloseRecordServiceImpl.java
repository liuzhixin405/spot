package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQLoanCloseRecordDao;
import com.btc100.btc_q.entity.BQLoanCloseRecord;
import com.btc100.btc_q.service.BQLoanCloseRecordService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqLoanCloseRecordService")
public class BQLoanCloseRecordServiceImpl implements BQLoanCloseRecordService {

	@Autowired
	private BQLoanCloseRecordDao<BQLoanCloseRecord> bqLoanCloseRecordDao;

	@Override
	public void save(BQLoanCloseRecord setting) {
		bqLoanCloseRecordDao.save(setting);
	}

	@Override
	public PagerModel readAllByUserId(long userId, long borrowId) {
		String borrowHql = "";

		Map<String, Object> params = new HashMap<>();
		if (userId > 0) {
			params.put("userId", userId);
			borrowHql += " and f_user_id = :userId";
		}
		
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			borrowHql += " and borrow_uid = :borrowId";
		}
		
		return bqLoanCloseRecordDao.searchHql("from BQLoanCloseRecord where 1=1 " + borrowHql + " order by record_time desc", params);
	}

}
