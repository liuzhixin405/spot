package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQLoanBackRecordDao;
import com.btc100.btc_q.entity.BQLoanBackRecord;
import com.btc100.btc_q.service.BQLoanBackRecordServcie;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqLoanBackRecordServcie")
public class BQLoanBackRecordServcieImpl implements BQLoanBackRecordServcie {

	@Autowired
	private BQLoanBackRecordDao<BQLoanBackRecord> bqLoanBackRecordDao;

	@Override
	public void save(BQLoanBackRecord setting) {
		bqLoanBackRecordDao.save(setting);
	}

	@Override
	public PagerModel readAllByUserId(long userId, long borrowId) {
		String borrowHql = "";

		Map<String, Object> params = new HashMap<>();
		if (userId > 0) {
			params.put("userId", userId);
			borrowHql += " and f_user_id = :userId ";
		}
		
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			borrowHql += " and borrow_uid = :borrowId";
		}
		
		return bqLoanBackRecordDao.searchHql("from BQLoanBackRecord where 1 = 1 " + borrowHql + " order by back_time desc", params);
	}

}
