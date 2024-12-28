package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountContractDao;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("bqAccountContractService")
public class BQAccountContractServiceImpl implements BQAccountContractService {

	@Autowired
	private BQAccountContractDao<BQAccountContract> bqAccountContractDao;

	public int UpdateAmount(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle) {
		return bqAccountContractDao.updateAmount(userID, code, initBtc, btc, lockBtc, settle);
	}

	public int UpdateMoney(long userID, String code, BigDecimal btc, BigDecimal lockBtc) {
		return bqAccountContractDao.updateAmount(userID, code, BigDecimal.ZERO, btc, lockBtc, BigDecimal.ZERO);
	}

	public BQAccountContract findByUidCode(long userID, String code) {
		BQAccountContract accountContract = bqAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new BQAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		
		return accountContract;
	}
	
	public BQAccountContract readByUidCode(long userID, String code) {
		BQAccountContract accountContract = bqAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new BQAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		
		return accountContract;
	}
	
	public BQAccountContract readWithNullByUidCode(long userID, String code) {
		BQAccountContract accountContract = bqAccountContractDao.findByUserIDCode(userID, code);
		return accountContract;
	}

	@Override
	public PagerModel readRecords(String code) {
		Map<String,Object> params = new HashMap<>();
		String hql = "FROM BQAccountContract where 1=1 ";
		if (code != null && !"".equals(code)){
			params.put("code", code);
			hql += " and f_code = :code";
		}
		hql += " ORDER BY (f_btc+f_lockbtc) DESC";

		return bqAccountContractDao.searchHql(hql, params);
	}

	public void createByUidCode(long userID, String code) {
		bqAccountContractDao.getSession().flush();
		bqAccountContractDao.getSession().clear();
		
		BQAccountContract accountContract = bqAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new BQAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
			bqAccountContractDao.save(accountContract);
		}
	}

	public BigDecimal readLockAmountByUid(long userID) {
		Session session = bqAccountContractDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(f_lockbtc),0) FROM bq_account_contract WHERE f_user_id = ?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userID);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0));
	}

	public BigDecimal readAmountByUid(long userID) {
		Session session = bqAccountContractDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(f_btc),0) FROM bq_account_contract WHERE f_user_id = ?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userID);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0));
	}
	
	public BQAccountContract readBTCByUidCode(long userID, String code) {
		StringBuffer sql = new StringBuffer("FROM BQAccountContract WHERE f_user_id = :uid and f_code=:code");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);

		List<BQAccountContract> list = bqAccountContractDao.find(sql.toString(), params);

		if (list == null || list.size() <= 0)
			return null;
		return list.get(0);
	}

	public BQAccountContract findBTCByUidCode(long userID, String code) {
		StringBuffer sql = new StringBuffer("FROM BQAccountContract WHERE f_user_id = :uid and f_code=:code");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);

		List<BQAccountContract> list = bqAccountContractDao.find(sql.toString(), params);

		if (list == null || list.size() <= 0)
			return null;
		return list.get(0);
	}
	
	public BQAccountContract getOverBalance(long userID, String code, BigDecimal amount) {
		StringBuffer sql = new StringBuffer("FROM BQAccountContract WHERE f_user_id = :uid and f_code=:code and f_btc > :amount");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);
		params.put("amount", amount);

		return bqAccountContractDao.get(sql.toString(), params); 
	}

	public PagerModel readCountUserIds() {
		Session session = bqAccountContractDao.getSession();
		String sql = "Select f_user_id From bq_account_contract Group By f_user_id Having Count(*) > 1 ";
		StringBuilder listsql = new StringBuilder(sql);
		listsql.append("LIMIT ").append(SystemContext.getOffset()).append(",").append(SystemContext.getPageSize());
		StringBuilder countsql = new StringBuilder("Select count(*) From (").append(sql).append(") a");
		SQLQuery listQuery = session.createSQLQuery(listsql.toString());
		SQLQuery countQuery = session.createSQLQuery(countsql.toString());
		PagerModel pm = new PagerModel();
		pm.setDatas(listQuery.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		return pm;
	}

	@Override
	public List<BQAccountContract> findGreaterThan100(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return bqAccountContractDao.find("From BQAccountContract WHERE f_code = :code and f_btc > 100", params);
	}

	@Override
	public List<BQAccountContract> readAllByUid(long userID) {
		return bqAccountContractDao.findByUserID(userID);
	}
	
	@Override
	public BigDecimal findSumBalanceByUser(String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(f_btc + f_lockbtc) from BQAccountContract where f_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return bqAccountContractDao.sum(hql, map);
	}

	@Override
	public BigDecimal readSumBalanceByUser(String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(f_btc + f_lockbtc) from BQAccountContract where f_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return bqAccountContractDao.sum(hql, map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readBalanceAndFrozen(long userId) {
		Session session = bqAccountContractDao.getSession();
		String sql = "SELECT f_user_id, IFNULL(sum(f_btc), 0) AS balance, IFNULL(sum(f_lockbtc), 0) AS frozen FROM bq_account_contract where 1 = 1";
		if (userId > 0) {
			sql += " and f_user_id = :uid";
		} else {
			sql += " and f_btc + f_lockbtc > 0 group by f_user_id";
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		if (userId > 0) {
			query.setLong("uid", userId);
		}
		
		return query.list();
	}

}
