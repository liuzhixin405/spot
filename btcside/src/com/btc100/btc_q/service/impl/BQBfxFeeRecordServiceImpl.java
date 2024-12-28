package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBfxFeeRecordDao;
import com.btc100.btc_q.entity.BQBfxFeeRecord;
import com.btc100.btc_q.service.BQBfxFeeRecordService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqBfxFeeRecordService")
public class BQBfxFeeRecordServiceImpl implements BQBfxFeeRecordService {

	@Autowired
	private BQBfxFeeRecordDao<BQBfxFeeRecord> bqBfxFeeRecordDao;

	@Override
	public PagerModel readRecords(long uid, long startTime, long endTime) {
		String hql = "from BQBfxFeeRecord  where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		if (startTime > 0) {
			hql += " and trade_time >= :startTime";
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and trade_time < :endTime";
			map.put("endTime", endTime);
		}
		
		hql += " order by trade_time desc";
		
		return bqBfxFeeRecordDao.searchHql(hql, map);
	}
	
	@Override
	public BigDecimal sumBfxFee(String partition, long uid, String code, long startTime, long endTime) {
		String hql = "SELECT sum(amount) from BQBfxFeeRecord where part = :part";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("part", partition);
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		if (!StringUtil.isEmpty(code)) {
			hql += " and f_code = :code";
			map.put("code", code);
		}
		
		if (startTime > 0) {
			hql += " and trade_time >= :startTime";
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and trade_time < :endTime";
			map.put("endTime", endTime);
		}
		return bqBfxFeeRecordDao.sum(hql, map);
	}

	@Override
	public BigDecimal readSumBfxFee(String partition, long uid, String code, long startTime, long endTime) {
		String hql = "SELECT sum(amount) from BQBfxFeeRecord where part = :part";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("part", partition);
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		if (!StringUtil.isEmpty(code)) {
			hql += " and f_code = :code";
			map.put("code", code);
		}
		
		if (startTime > 0) {
			hql += " and trade_time >= :startTime";
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and trade_time < :endTime";
			map.put("endTime", endTime);
		}
		return bqBfxFeeRecordDao.sum(hql, map);
	}
}
