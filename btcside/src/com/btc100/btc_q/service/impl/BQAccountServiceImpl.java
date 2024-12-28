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
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqAccountService")
public class BQAccountServiceImpl implements BQAccountService {

	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	
	public BQAccount getAccount(long fUserId, int currencyId) {
		return bqAccountDao.getByUerAndCurrencyId(fUserId, currencyId);
	}

	public BQAccount readAccount(long fUserId, int currencyId) {
		return bqAccountDao.getByUerAndCurrencyId(fUserId, currencyId);
	}

	public void save(BQAccount ba, int currencyId) {
		bqAccountDao.save(ba);
	}

	public void addUserMoney(BigDecimal oneMoney, long userId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("oneMoney", oneMoney);
		params.put("userid", userId);
		params.put("currencyId", currencyId);
		bqAccountDao.updateCondition("update BQAccount SET btc = btc + :oneMoney where f_user_id = :userid and currency_type_id = :currencyId", params);
	}

	public int updateAccount(long uid, int currencyId, BigDecimal btc, BigDecimal freeze) {
		return bqAccountDao.updateAmount(uid, currencyId, btc, freeze);
	}

	public int updateAccountAndLog(long uid, int currencyId, BigDecimal btc, String title, int operation, int direction, String transType) {
		BQAccount bqaccount = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
		if (bqAccountDao.updateAmount(uid, currencyId, btc, BigDecimal.ZERO) > 0) {
			// 增加资金变动日志
			BQAccountAmountLog amountLog = new BQAccountAmountLog(title, currencyId, operation, direction, bqaccount.getBtc(), btc, btc.add(bqaccount.getBtc()),
					bqaccount.getBtc_locked(), bqaccount.getBtc_locked(), bqAccountDao.getUnix_TIMESTAMP(), uid, RandomUtil.getTransNo(), transType, 0);
			bqAccountAmountLogDao.save(amountLog);
			
			return 1;
		}
		
		return 0;
	}

	public List<BQAccount> findMoneyUser(int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currencyId", currencyId);
		return bqAccountDao.find("From BQAccount WHERE currency_type_id = :currencyId and btc > 0", params);
	}

	@Override
	public List<BQAccount> findGreaterThan1000(int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currencyId", currencyId);
		return bqAccountDao.find("From BQAccount WHERE currency_type_id = :currencyId and btc > 100", params);
	}

	@Override
	public List<BQAccount> readAllByUserId(long fUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", fUserId);
		return bqAccountDao.find("From BQAccount WHERE f_user_id = :fUserId ", params);
	}

	@Override
	public BQAccount readAccountStatistics(int coinId) {
		Session session = bqAccountDao.getSession();
		String sql = "SELECT 0 id, 0 f_user_id, ? currency_type_id,'' f_user_email,0 lastupdate,IFNULL(sum(b.btc),0) btc,IFNULL(sum(b.btc_locked),0) btc_locked from bq_account b"
				+ " where currency_type_id = ? ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, coinId);
		query.setInteger(1, coinId);
		query.addEntity(BQAccount.class);
		return (BQAccount) query.uniqueResult();
	}

	@Override
	public PagerModel readStatistics(String orderBy, long userId, int coinId) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder sbBuilder = new StringBuilder();
		PagerModel model = new PagerModel();

		params.put("coinId", coinId);
		sbBuilder.append("from BQAccount b where currency_type_id = :coinId ");
		if (userId > 0) {
			sbBuilder.append(" and f_user_id = :fUserId ");
			params.put("fUserId", userId);
		}

		String orderByStr = " order by lastupdate desc";
		if ("TOTALDES".equals(orderBy) || "TOTALASC".equals(orderBy)) {
			if ("TOTALDES".equals(orderBy)) {
				orderByStr = " order by btc + btc_locked desc";
			} else {
				orderByStr = " order by btc + btc_locked asc";
			}
		} else {
			if ("AVAILABLEDES".equals(orderBy)) {
				orderByStr = " order by btc desc";
			} else if ("AVAILABLEASC".equals(orderBy)) {
				orderByStr = " order by btc asc";
			} else if ("FROZENDES".equals(orderBy)) {
				orderByStr = " order by btc_locked desc";
			} else if ("FROZENASC".equals(orderBy)) {
				orderByStr = " order by btc_locked asc";
			}
		}

		model = bqAccountDao.searchHql(sbBuilder + orderByStr, params);
		return model;
	}

	@Override
	public List<BQAccount> findAboveZero(int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currencyId", currencyId);
		
		return bqAccountDao.find("from BQAccount where currency_type_id = :currencyId and (btc > 0 or btc_locked > 0)", params);
	}
	
	@Override
	public List<BQAccount> readAboveZero(int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currencyId", currencyId);
		
		return bqAccountDao.find("from BQAccount where currency_type_id = :currencyId and (btc > 0 or btc_locked > 0)", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findUserIdAboveZero(int currencyId) {
		Session session = bqAccountDao.getSession();
		String sql = "SELECT f_user_id from bq_account b where currency_type_id = ? and (btc > 0 or btc_locked > 0)";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, currencyId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> readAllUserIds(int currencyId) {
		Session session = bqAccountDao.getSession();
		String sql = "SELECT f_user_id from bq_account b where currency_type_id = ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, currencyId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findBalance(int currencyId) {
		Session session = bqAccountDao.getSession();
		String sql = "SELECT f_user_id, f_user_email, IFNULL(btc + btc_locked, 0) AS balance FROM bq_account where currency_type_id = ? and btc + btc_locked > 0";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, currencyId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readBalanceAndFrozen(int currencyId) {
		Session session = bqAccountDao.getSession();
		String sql = "SELECT f_user_id, f_user_email, IFNULL(btc, 0) AS balance, IFNULL(btc_locked, 0) AS frozen FROM bq_account where currency_type_id = ? and btc + btc_locked > 0";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, currencyId);
		return query.list();
	}
}
