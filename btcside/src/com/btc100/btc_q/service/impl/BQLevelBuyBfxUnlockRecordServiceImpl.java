package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQCurrencyTypeDao;
import com.btc100.btc_q.dao.BQLevelBuyBfxUnlockRecordDao;
import com.btc100.btc_q.dao.BQMemberLevelBuyAmountLogDao;
import com.btc100.btc_q.dao.BQMemberRechargeDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQLevelBuyBfxUnlockRecord;
import com.btc100.btc_q.entity.BQMemberLevelBuyAmountLog;
import com.btc100.btc_q.entity.BQMemberLevelBuySetting;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQLevelBuyBfxUnlockRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqLevelBuyBfxUnlockRecordService")
public class BQLevelBuyBfxUnlockRecordServiceImpl implements BQLevelBuyBfxUnlockRecordService {

	@Autowired
	BQLevelBuyBfxUnlockRecordDao<BQLevelBuyBfxUnlockRecord> bqIeoUnlockRecordDao;
	@Autowired
	BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	BQMemberRechargeDao<BQMemberRecharge> bqMemberRechargeDao;
	@Autowired
	BQMemberLevelBuyAmountLogDao<BQMemberLevelBuyAmountLog> bqIeoRechargeRecordDao;
	@Autowired
	BQCurrencyTypeDao<BQCurrencyType> bqCurrencyTypeDao;
	
	@Override
	public PagerModel findRecords(long uid, long startTime, long endTime) {
		String hql = "from BQLevelBuyBfxUnlockRecord  where 1 = 1";
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
	public void unlockBfx(BQMemberLevelBuyAmountLog record, BQMemberLevelBuySetting setting, int currencyId) throws ServiceException {

		if (setting.getUnlock_times() <= 0) return;
		BigDecimal unlockRate = BigDecimal.ONE.divide(new BigDecimal(setting.getUnlock_times()), 12, BigDecimal.ROUND_DOWN);
		
		if (unlockRate.compareTo(BigDecimal.ZERO) <= 0) return;
		
		BigDecimal unlockAmount = record.getBfx_lock().multiply(unlockRate);
		if (unlockAmount.compareTo(BigDecimal.ZERO) <= 0) return;
		
		if (record.getBfx_lock().compareTo(record.getBfx_unlock().add(unlockAmount)) < 0) {
			unlockAmount = record.getBfx_lock().subtract(record.getBfx_unlock());
		}
		if (unlockAmount.compareTo(BigDecimal.ZERO) <= 0) return;
		
		long uid = record.getF_user_id();
		long time = bqMemberRechargeDao.getUnix_TIMESTAMP();
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
		BQAccountAmountLog pAccountLog = new BQAccountAmountLog("Vip购买后BFX锁仓解冻，数量：" + unlockAmount.toString(), currencyId, 1, unlockAmount.compareTo(BigDecimal.ZERO),
				bqAccount.getBtc().subtract(unlockAmount), unlockAmount, bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
				time, uid, RandomUtil.getTransNo(), "BFX锁仓解冻：" + unlockAmount.doubleValue(), 0);
		bqAccountAmountLogDao.save(pAccountLog);
		
		BQMemberRecharge recharge = new BQMemberRecharge();
		recharge.setCurrency_type_id(currencyId);
		recharge.setOperation(1);
		recharge.setFee(BigDecimal.ZERO);
		recharge.setAmount(unlockAmount.subtract(recharge.getFee()));
		recharge.setReceice_card_type(1);	// 1.普通充值
		recharge.setReceice_card_no("Vip购买锁仓释放_" + RandomUtil.getTransNo());
		recharge.setIp("");
		recharge.setDateline(time);
		recharge.setConfirm_time(time);
		recharge.setState(1);
		recharge.setRemark("Vip购买锁仓释放");
		recharge.setF_user_id(uid);
		recharge.setF_user_email("");
		recharge.setWallet_trade_id(recharge.getReceice_card_no());
		bqMemberRechargeDao.save(recharge);
		
		BQLevelBuyBfxUnlockRecord unlockRecord = new BQLevelBuyBfxUnlockRecord();
		unlockRecord.setF_user_id(uid);
		unlockRecord.setCurrency_type_id(currencyId);
		unlockRecord.setTotal_lock(record.getBfx_lock());
		unlockRecord.setAmount(unlockAmount);
		unlockRecord.setUnlock_rate(unlockRate);
		unlockRecord.setLevel_buy_record_id(record.getLog_id());
		unlockRecord.setRecord_time(time);
		bqIeoUnlockRecordDao.save(unlockRecord);
		
		if (bqIeoRechargeRecordDao.updateRecord(record.getLog_id(), BigDecimal.ZERO, unlockAmount) <= 0) {
			throw new ServiceException("BFX解冻 ,Vip购买记录更新失败，log_id：" + record.getLog_id() + "，金额：" + unlockAmount.doubleValue());
		}
		
		if (bqAccountDao.updateAmount(uid, currencyId, unlockAmount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("BFX解冻 ,账户资金异常，用户：" + uid + "，金额：" + unlockAmount.doubleValue());
		}
	}

	@Override
	public List<BQLevelBuyBfxUnlockRecord> findRecords(long uid, int limit) {
		String hql = "from BQLevelBuyBfxUnlockRecord  where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		hql += " order by record_time desc";
		
		return bqIeoUnlockRecordDao.find(hql, map, 1, limit);
	} 

}
