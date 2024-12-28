package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQBonusRecordDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQBonusRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQBonusRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqBonusRecordService")
public class BQBonusRecordServiceImpl implements BQBonusRecordService {
	
	@Autowired
	BQBonusRecordDao<BQBonusRecord> bqBonusRecordDao;
	@Autowired
	BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	@Override
	public PagerModel readRecords(long uid, long startTime, long endTime) {
		String hql = "from BQBonusRecord  where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		if (startTime > 0) {
			hql += " and record_time >= :startTime";
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and record_time < :endTime";
			map.put("endTime", endTime);
		}
		
		hql += " order by record_time desc";
		
		return bqBonusRecordDao.searchHql(hql, map);
	}

	@Override
	public void bonus(BQBonusRecord record) throws ServiceException {
		if (bqAccountDao.updateAmount(record.getF_user_id(), record.getCurrency_type_id(), record.getAmount(), BigDecimal.ZERO) <= 0) {
			throw new ServiceException("平台分红 ,账户资金异常，用户：" + record.getF_user_id() + "，金额：" + record.getAmount().doubleValue());
		}
		
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getCurrency_type_id());
		BQAccountAmountLog pAccountLog = new BQAccountAmountLog("平台分红，数量：" + record.getAmount().doubleValue(), record.getCurrency_type_id(), 1, 1,
				bqAccount.getBtc().subtract(record.getAmount()), record.getAmount(), bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
				bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(), "平台分红：" + record.getAmount().doubleValue(), 0);
		bqAccountAmountLogDao.save(pAccountLog);
		
		record.setRecord_time(bqBonusRecordDao.getUnix_TIMESTAMP());
		bqBonusRecordDao.save(record);
	}

	@Override
	public BQBonusRecord readLast(long lastTime, int currencyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("currencyId", currencyId);
		map.put("lastTime", lastTime);
		return bqBonusRecordDao.get("from BQBonusRecord where currency_type_id = :currencyId and record_time >= :lastTime order by record_time desc", map);
	}

	@Override
	public BigDecimal readSumBonusRate(int currencyId) {
		Session session = bqBonusRecordDao.getSession();
		String sql = "SELECT max(rate) from bq_bonus_record where currency_type_id = :currencyId group by day";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("currencyId", currencyId);
		
		BigDecimal sum = BigDecimal.ZERO;
		@SuppressWarnings("rawtypes")
		List result = query.list();
		for (Object obj : result) {
			BigDecimal rate = new BigDecimal(obj.toString());
			sum = sum.add(rate);
		}
		
		return sum;
	}

	@Override
	public List<BQBonusRecord> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime,
			int limit) {
		String hql = "from BQBonusRecord  where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		if (startTime > 0) {
			hql += " and record_time >= :startTime";
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and record_time < :endTime";
			map.put("endTime", endTime);
		}
		if (from >= 0 && "prev".equals(direct)) {
			map.put("from", from);
			hql += " and id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			map.put("from", from);
			hql += " and id <= :from";
		}
		
		hql += " order by id desc";
		
		return bqBonusRecordDao.find(hql, map, 0, limit);
	} 

}
