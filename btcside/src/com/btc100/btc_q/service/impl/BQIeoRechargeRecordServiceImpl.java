package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQIeoRechargeRecordDao;
import com.btc100.btc_q.entity.BQIeoRechargeRecord;
import com.btc100.btc_q.service.BQIeoRechargeRecordService;
import com.btc100.btc_q.vo.PagerModel;


@Service("bqIeoRechargeRecordService")
public class BQIeoRechargeRecordServiceImpl implements BQIeoRechargeRecordService {

	@Autowired
	private BQIeoRechargeRecordDao<BQIeoRechargeRecord> bqIeoRechargeRecordDao;
	@Override
	public PagerModel findRecords(long uid) {
		String hql = "from BQIeoRechargeRecord where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		hql += " order by record_time desc";
		
		return bqIeoRechargeRecordDao.searchHql(hql, map);
	}

	@Override
	public BQIeoRechargeRecord readRecord(long id) {
		return bqIeoRechargeRecordDao.get(BQIeoRechargeRecord.class, id);
	}

	@Override
	public List<BQIeoRechargeRecord> findNeedUnlockRecordsByTime(long beginTime, long endTime) {
		String hql = "from BQIeoRechargeRecord where unlock_amount < amount and operation = 1 and record_time >= :beginTime and record_time < :endTime";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("beginTime", beginTime);
		map.put("endTime", endTime);
		
		return bqIeoRechargeRecordDao.find(hql, map);
	}

}
