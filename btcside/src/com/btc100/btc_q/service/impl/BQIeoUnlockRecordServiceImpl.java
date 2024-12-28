package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQIeoAccountDao;
import com.btc100.btc_q.dao.BQIeoRechargeRecordDao;
import com.btc100.btc_q.dao.BQIeoUnlockRecordDao;
import com.btc100.btc_q.dao.BQMemberRechargeDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQIeoAccount;
import com.btc100.btc_q.entity.BQIeoRechargeRecord;
import com.btc100.btc_q.entity.BQIeoUnlockRecord;
import com.btc100.btc_q.entity.BQIeoUnlockSetting;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQIeoUnlockRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqIeoUnlockRecordService")

public class BQIeoUnlockRecordServiceImpl implements BQIeoUnlockRecordService {

	@Autowired
	BQIeoUnlockRecordDao<BQIeoUnlockRecord> bqIeoUnlockRecordDao;
	@Autowired
	BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	BQIeoAccountDao<BQIeoAccount> bqIeoAccountDao;
	@Autowired
	BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	BQMemberRechargeDao<BQMemberRecharge> bqMemberRechargeDao;
	@Autowired
	BQIeoRechargeRecordDao<BQIeoRechargeRecord> bqIeoRechargeRecordDao;
	
	@Override
	public PagerModel findRecords(long uid, long startTime, long endTime) {
		String hql = "from BQIeoUnlockRecord  where 1 = 1";
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
		
		return bqIeoUnlockRecordDao.searchHql(hql, map);
	}

	@Override
	public void unlockIeo(BQIeoRechargeRecord record, BQIeoUnlockSetting setting) throws ServiceException {

		if (setting.getUnlock_rate().compareTo(BigDecimal.ZERO) <= 0) return;
		
		BigDecimal unlockAmount = record.getAmount().multiply(setting.getUnlock_rate());
		if (unlockAmount.compareTo(BigDecimal.ZERO) <= 0) return;
		
		if (record.getAmount().compareTo(record.getUnlock_amount().add(unlockAmount)) < 0) return;
		
		long uid = record.getF_user_id();
		int currencyId = record.getCurrency_type_id();
		
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
		BQAccountAmountLog pAccountLog = new BQAccountAmountLog("IEO解冻，数量：" + unlockAmount.toString(), currencyId, 1, unlockAmount.compareTo(BigDecimal.ZERO),
				bqAccount.getBtc().subtract(unlockAmount), unlockAmount, bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
				bqAccountAmountLogDao.getUnix_TIMESTAMP(), uid, RandomUtil.getTransNo(), "IEO解冻：" + unlockAmount.doubleValue(), 0);
		bqAccountAmountLogDao.save(pAccountLog);
		
		long time = bqMemberRechargeDao.getUnix_TIMESTAMP();
		BQMemberRecharge recharge = new BQMemberRecharge();
		
		recharge.setCurrency_type_id(currencyId);
		recharge.setOperation(1);
		recharge.setFee(BigDecimal.ZERO);
		recharge.setAmount(unlockAmount.subtract(recharge.getFee()));
		recharge.setReceice_card_type(1);	// 1.普通充值
		recharge.setReceice_card_no("解锁释放_" + RandomUtil.getTransNo());
		recharge.setIp("");
		recharge.setDateline(time);
		recharge.setConfirm_time(time);
		recharge.setState(1);
		recharge.setRemark("解锁释放");
		recharge.setF_user_id(uid);
		recharge.setF_user_email("");
		recharge.setWallet_trade_id(recharge.getReceice_card_no());
		bqMemberRechargeDao.save(recharge);
		
		BQIeoUnlockRecord unlockRecord = new BQIeoUnlockRecord();
		unlockRecord.setF_user_id(uid);
		unlockRecord.setCurrency_type_id(currencyId);
		unlockRecord.setTotal_mine(record.getAmount());
		unlockRecord.setAmount(unlockAmount);
		unlockRecord.setUnlock_rate(setting.getUnlock_rate());
		
		unlockRecord.setDay(TimeUtil.getToday("YYYY-MM-dd"));
		unlockRecord.setRecord_time(bqIeoUnlockRecordDao.getUnix_TIMESTAMP());
		bqIeoUnlockRecordDao.save(unlockRecord);
		
		if (bqIeoRechargeRecordDao.updateRecord(record.getId(), BigDecimal.ZERO, unlockAmount) <= 0) {
			throw new ServiceException("ieo用户解冻 ,ieo充值记录更改异常，id：" + record.getId() + "，金额：" + unlockAmount.doubleValue());
		}
		
		if (bqAccountDao.updateAmount(uid, currencyId, unlockAmount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("ieo用户解冻 ,账户资金异常，用户：" + uid + "，金额：" + unlockAmount.doubleValue());
		}
		
		if (bqIeoAccountDao.updateAccount(uid, currencyId, BigDecimal.ZERO, unlockAmount.negate(), unlockAmount) <= 0) {
			throw new ServiceException("ieo用户解冻，ieo账户更新，用户：" + uid + "，金额：" + unlockAmount.doubleValue());
		}
	}

	@Override
	public List<BQIeoUnlockRecord> findRecords(long uid, int limit) {
		String hql = "from BQIeoUnlockRecord  where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		hql += " order by record_time desc";
		
		return bqIeoUnlockRecordDao.find(hql, map, 1, limit);
	} 

}
