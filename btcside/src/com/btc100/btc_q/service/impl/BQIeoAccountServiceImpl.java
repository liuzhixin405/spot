package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQIeoAccountDao;
import com.btc100.btc_q.dao.BQIeoRechargeRecordDao;
import com.btc100.btc_q.entity.BQIeoAccount;
import com.btc100.btc_q.entity.BQIeoRechargeRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQIeoAccountService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;


@Service("bqIeoAccountService")
public class BQIeoAccountServiceImpl implements BQIeoAccountService {

	@Autowired
	private BQIeoAccountDao<BQIeoAccount> bqIeoAccountDao;
	@Autowired
	private BQIeoRechargeRecordDao<BQIeoRechargeRecord> bqIeoRechargeRecordDao;

	@Override
	public void recharge(long uid, int currencyId, int operation, BigDecimal amount, String note) throws ServiceException {
		String hql = "from BQIeoAccount where f_user_id = :uid and currency_type_id = :currencyId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("currencyId", currencyId);
		BQIeoAccount account = bqIeoAccountDao.get(hql, params);
		if (account == null) {
			if (amount.compareTo(BigDecimal.ZERO) < 0) {
				throw new ServiceException("提现失败，无ieo账户：" + uid + "，金额：" + amount.doubleValue());
			}
			
			account = new BQIeoAccount();
			account.setF_user_id(uid);
			account.setCurrency_type_id(currencyId);
			account.setInitial(amount);
			account.setLocked(amount);
			account.setUnlocked(BigDecimal.ZERO);
			bqIeoAccountDao.save(account);
			
		} else {
			if (bqIeoAccountDao.updateAccount(uid, currencyId, amount, amount, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("更新ieo账户异常，用户：" + uid + "，金额：" + amount.doubleValue());
			}
		}
		
		BQIeoRechargeRecord record = new BQIeoRechargeRecord();
		record.setF_user_id(uid);
		record.setCurrency_type_id(currencyId);
		record.setOperation(operation);
		record.setAmount(amount.abs());
		record.setNote(note);
		record.setRecord_time(bqIeoRechargeRecordDao.getUnix_TIMESTAMP());
		record.setUnlock_amount(BigDecimal.ZERO);
		bqIeoRechargeRecordDao.save(record);
	}
	
	@Override
	public void changeRecord(long id, BigDecimal amount, String note) throws ServiceException {
		BQIeoRechargeRecord record = bqIeoRechargeRecordDao.get(BQIeoRechargeRecord.class, id);
		if (record == null || record.getOperation() != 1 || record.getAmount().add(amount).compareTo(BigDecimal.ZERO) < 0) {
			throw new ServiceException("更新ieo记录失败！！");
		}
		
		record.setAmount(record.getAmount().add(amount));
		record.setUpdate_time(System.currentTimeMillis() / 1000);
		record.setNote(note);
		
		String hql = "from BQIeoAccount where f_user_id = :uid and currency_type_id = :currencyId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", record.getF_user_id());
		params.put("currencyId", record.getCurrency_type_id());
		BQIeoAccount account = bqIeoAccountDao.get(hql, params);
		if (account == null) {
			throw new ServiceException("无ieo账户：" + record.getF_user_id() + "，金额：" + amount.doubleValue());
		} else { 
			if (bqIeoAccountDao.updateAccount(record.getF_user_id(), record.getCurrency_type_id(), BigDecimal.ZERO, amount, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("更新ieo账户异常，用户：" + record.getF_user_id() + "，金额：" + amount.doubleValue());
			}
		}
		
		bqIeoRechargeRecordDao.update(record);
	}

	@Override
	public int updateAccount(long uid, int currencyId, BigDecimal initial, BigDecimal locked, BigDecimal unlocked) {
		return bqIeoAccountDao.updateAccount(uid, currencyId, initial, locked, unlocked);
	}

	@Override
	public PagerModel findByUserId(long uid, String order) {
		String hql = "from BQIeoAccount where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		if (!StringUtil.isEmpty(order)) {
			hql += " order by :order desc";
			map.put("order", order);
		}
		
		return bqIeoAccountDao.searchHql(hql, map);
	}

	@Override
	public BQIeoAccount sumAll() {
		String sql = "select 0 id, 0 f_user_id, 0 currency_type_id, IFNULL(sum(b.initial),0) initial, IFNULL(sum(b.locked),0) locked, IFNULL(sum(b.unlocked),0) unlocked from bq_ieo_account b";
		Session session = bqIeoAccountDao.getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(BQIeoAccount.class);
		return (BQIeoAccount) query.uniqueResult();
	}

	@Override
	public List<BQIeoAccount> findAll() {
		return bqIeoAccountDao.find("from BQIeoAccount where 1=1");
	}

	@Override
	public BQIeoAccount getAccountByUserId(long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		return bqIeoAccountDao.get("from BQIeoAccount where f_user_id = :uid", map);
	}

	@Override
	public List<BQIeoAccount> findAboveZero() {
		return bqIeoAccountDao.find("from BQIeoAccount where unlocked > 0");
	}
}
