package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.btc100.btc_q.dao.BQMinePersonalAmountDao;
import com.btc100.btc_q.dao.BQMineSettingDao;
import com.btc100.btc_q.entity.BQMinePersonalAmount;
import com.btc100.btc_q.entity.BQMineSetting;
import com.btc100.btc_q.entity.SpotTradeLog;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQMineRecordDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQMineRecord;
import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMineRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMineRecordService")
public class BQMineRecordServiceImpl implements BQMineRecordService {
	
	@Autowired
	private BQMineRecordDao<BQMineRecord> bqMineRecordDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private BQMinePersonalAmountDao<BQMinePersonalAmount> bqMinePersonalAmountDao;
	@Autowired
	private BQMineSettingDao<BQMineSetting> bqMineSettingDao;

	@Override
	public PagerModel readRecords(long uid, long startTime, long endTime) {
		
		String hql = "from BQMineRecord  where 1 = 1";
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
		
		return bqMineRecordDao.searchHql(hql, map);
	}

	@Override
	public PagerModel readRecords(long uid, int tradeMode, long startTime, long endTime) {
		String hql = "from BQMineRecord  where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}

		if (tradeMode > 0) {
			hql += " and trade_mode = :tradeMode";
			map.put("tradeMode", tradeMode);
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

		return bqMineRecordDao.searchHql(hql, map);
	}

	@Override
	public void mine(TradeLog trade, int currencyId, BigDecimal amount, BigDecimal mineRate, long settingId) throws ServiceException {
		if (bqAccountDao.updateAmount(trade.getF_user_id(), currencyId, amount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("挖矿更新用户资金异常，用户：" + trade.getF_user_id() + "，金额：" + amount.doubleValue());
		}
		
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(trade.getF_user_id(), currencyId);
		BQAccountAmountLog pAccountLog = new BQAccountAmountLog("交易挖矿，数量：" + amount.toString(), currencyId, 1, amount.compareTo(BigDecimal.ZERO),
				bqAccount.getBtc().subtract(amount), amount, bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
				bqAccountAmountLogDao.getUnix_TIMESTAMP(), trade.getF_user_id(), RandomUtil.getTransNo(), "交易挖矿：" + amount.doubleValue(), trade.getTrade_id());
		bqAccountAmountLogDao.save(pAccountLog);
		
		BQMineRecord record = new BQMineRecord();
		record.setF_user_id(trade.getF_user_id());
		record.setCurrency_type_id(currencyId);
		record.setMine_amount(amount);
		record.setMine_rate(mineRate);
		record.setRecord_time(bqMineRecordDao.getUnix_TIMESTAMP());
		record.setTrade_amount(trade.getTrade_average_price().multiply(trade.getTrade_count()));
		record.setTrade_fee(trade.getTrade_fee());
		record.setTrade_id(trade.getTrade_id());
		record.setTrade_type(trade.getTransaction_code());
		record.setTrade_mode(1);
		bqMineRecordDao.save(record);
	}
	
	@Override
	public void mine(NTradeLog trade, int currencyId, BigDecimal amount, BigDecimal mineRate, long settingId) throws ServiceException {
		if (bqAccountDao.updateAmount(trade.getF_user_id(), currencyId, amount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("挖矿更新用户资金异常，用户：" + trade.getF_user_id() + "，金额：" + amount.doubleValue());
		}
		
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(trade.getF_user_id(), currencyId);
		BQAccountAmountLog pAccountLog = new BQAccountAmountLog("交易挖矿，数量：" + amount.toString(), currencyId, 1, amount.compareTo(BigDecimal.ZERO),
				bqAccount.getBtc().subtract(amount), amount, bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
				bqAccountAmountLogDao.getUnix_TIMESTAMP(), trade.getF_user_id(), RandomUtil.getTransNo(), "交易挖矿：" + amount.doubleValue(), trade.getTrade_id());
		bqAccountAmountLogDao.save(pAccountLog);
		
		BQMineRecord record = new BQMineRecord();
		record.setF_user_id(trade.getF_user_id());
		record.setCurrency_type_id(currencyId);
		record.setMine_amount(amount);
		record.setMine_rate(mineRate);
		record.setRecord_time(bqMineRecordDao.getUnix_TIMESTAMP());
		record.setTrade_amount(trade.getTrade_average_price().multiply(trade.getTrade_count()));
		record.setTrade_fee(trade.getTrade_fee());
		record.setTrade_id(trade.getTrade_id());
		record.setTrade_type(trade.getTransaction_code());
		record.setTrade_mode(1);
		bqMineRecordDao.save(record);
	}


	//现货挖矿
	@Override
	public void mine(SpotTradeLog trade, int currencyId, BigDecimal amount, BigDecimal mineRate, long settingId, BigDecimal personMax, BigDecimal fee) throws ServiceException {
		if (bqAccountDao.updateAmount(trade.getF_user_id(), currencyId, amount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("挖矿更新用户资金异常，用户：" + trade.getF_user_id() + "，金额：" + amount.doubleValue());
		}

		if (bqMinePersonalAmountDao.updateAmount(trade.getF_user_id(), amount, personMax) <= 0) {
			throw new ServiceException("挖矿更新用户个人挖矿数量异常，用户：" + trade.getF_user_id() + "，金额：" + amount.doubleValue());
		}

		if (bqMineSettingDao.addMinedAmount(settingId, amount) <= 0) {
			throw new ServiceException("挖矿更新矿池数量异常，用户：" + trade.getF_user_id() + "，金额：" + amount.doubleValue());
		}

		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(trade.getF_user_id(), currencyId);
		BQAccountAmountLog pAccountLog = new BQAccountAmountLog("交易挖矿，数量：" + amount.toString(), currencyId, 1, amount.compareTo(BigDecimal.ZERO),
				bqAccount.getBtc().subtract(amount), amount, bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(),
				bqAccountAmountLogDao.getUnix_TIMESTAMP(), trade.getF_user_id(), RandomUtil.getTransNo(), "交易挖矿：" + amount.doubleValue(), trade.getTrade_id());
		bqAccountAmountLogDao.save(pAccountLog);

		BQMineRecord record = new BQMineRecord();
		record.setF_user_id(trade.getF_user_id());
		record.setCurrency_type_id(currencyId);
		record.setMine_amount(amount);
		record.setMine_rate(mineRate);
		record.setRecord_time(bqMineRecordDao.getUnix_TIMESTAMP());
		record.setTrade_amount(trade.getTrade_average_price().multiply(trade.getTrade_count()));
		record.setTrade_fee(fee);
		record.setTrade_id(trade.getTrade_id());
		record.setTrade_type(trade.getCoin_pair_name());
		record.setTrade_mode(2);
		bqMineRecordDao.save(record);
	}

	@Override
	public BigDecimal readSumFeeBetweenTime(long uid, long startTime, long endTime) {
		String sql = "select IFNULL(sum(b.trade_fee),0) from bq_mine_record b where 1=1";
		if (uid > 0) {
			sql += " and f_user_id = :uid";
		}
		
		if (startTime > 0) {
			sql += " and record_time >= :startTime";
		}
		
		if (startTime > 0) {
			sql += " and record_time < :endTime";
		}
		
		Session session = bqMineRecordDao.getSession();
		SQLQuery query = session.createSQLQuery(sql);
		if (uid > 0) {
			query.setLong("uid", uid);
		}
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (startTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		return (BigDecimal) query.uniqueResult();
	}

}
