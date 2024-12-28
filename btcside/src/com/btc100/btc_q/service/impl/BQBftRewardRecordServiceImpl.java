package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQBftRewardRecordDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.RewardRecordVo;

@Service("bqBftRewardRecordService")
public class BQBftRewardRecordServiceImpl implements BQBftRewardRecordService {
	
	@Autowired
	BQBftRewardRecordDao<BQBftRewardRecord> bqBftRewardRecordDao;
	@Autowired
	BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	@Override
	public PagerModel findRecords(long uid, long startTime, long endTime) {
		String hql = "from BQBftRewardRecord  where 1 = 1";
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
		
		return bqBftRewardRecordDao.searchHql(hql, map);
	}

	@Override
	public void bonus(BQBftRewardRecord record) {
		if (bqAccountDao.updateAmount(record.getF_user_id(), record.getCurrency_type_id(), record.getAmount(), BigDecimal.ZERO) <= 0) {
			return;
		}
		
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getCurrency_type_id());
		BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getNote() + "奖励，数量：" + record.getAmount().doubleValue(), record.getCurrency_type_id(), 1, 1,
				bqAccount.getBtc().subtract(record.getAmount()), record.getAmount(), bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
				bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(), "奖励：" + record.getAmount().doubleValue(), 0);
		bqAccountAmountLogDao.save(pAccountLog);
		
		record.setRecord_time(bqBftRewardRecordDao.getUnix_TIMESTAMP());
		bqBftRewardRecordDao.save(record);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RewardRecordVo> readRewardAndMineRecords(long uid, int currencyId, int limit) {
		String sql = "SELECT amount as amount, note as note, record_time as record_time FROM bq_bft_reward_record where f_user_id = " + uid
				+ " union "
				+ " SELECT mine_amount as amount, '现货交易挖矿' as note, record_time as record_time FROM bq_mine_record where currency_type_id = " + currencyId + " and f_user_id = " + uid
				+ " order by record_time desc limit " + limit;
		
		Session session = bqBftRewardRecordDao.getSession();
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(com.btc100.btc_q.vo.RewardRecordVo.class));
		return query.list();
	}

}
