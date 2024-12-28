package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NAccountContractDao;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("nccountContractService")
public class NAccountContractServiceImpl implements NAccountContractService {

	@Autowired
	private NAccountContractDao<NAccountContract> nAccountContractDao;

	public int UpdateAmount(String partition, long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle) {
		return nAccountContractDao.updateAmount(partition, userID, code, initBtc, btc, lockBtc, settle);
	}

	public int UpdateMoney(String partition, long userID, String code, BigDecimal btc, BigDecimal lockBtc) {
		return nAccountContractDao.updateAmount(partition, userID, code, BigDecimal.ZERO, btc, lockBtc, BigDecimal.ZERO);
	}

	public NAccountContract findByUidCode(String partition, long userID, String code) {
		NAccountContract accountContract = nAccountContractDao.findByUserIDCode(partition, userID, code);
		if (null == accountContract) {
			accountContract = new NAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		
		return accountContract;
	}
	
	public NAccountContract readByUidCode(String partition, long userID, String code) {
		NAccountContract accountContract = nAccountContractDao.findByUserIDCode(partition, userID, code);
		if (null == accountContract) {
			accountContract = new NAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		
		return accountContract;
	}
	
	public NAccountContract readWithNullByUidCode(String partition, long userID, String code) {
		NAccountContract accountContract = nAccountContractDao.findByUserIDCode(partition, userID, code);
		return accountContract;
	}

	@Override
	public PagerModel readRecords(String code, String partition) {
		String sql = "Select * From " + partition + "_account_contract where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		if (code != null && !"".equals(code)){
			params.put("code", code);
			sql += " and f_code = :code ";
		}
		sql += " ORDER BY (f_btc+f_lockbtc) DESC ";
		return nAccountContractDao.searchSql(sql, params);
	}

	public void createByUidCode(String partition, long userID, String code) {
		nAccountContractDao.getSession().flush();
		nAccountContractDao.getSession().clear();
		
		NAccountContract accountContract = nAccountContractDao.findByUserIDCode(partition, userID, code);
		if (null == accountContract) {
			accountContract = new NAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
			nAccountContractDao.insert(partition, accountContract);
		}
	}

	public BigDecimal readLockAmountByUid(String partition, long userID) {
		Session session = nAccountContractDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(f_lockbtc),0) FROM " + partition + "_account_contract WHERE f_user_id = ?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userID);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0));
	}

	public BigDecimal readAmountByUid(String partition, long userID) {
		Session session = nAccountContractDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(f_btc),0) FROM " + partition + "_account_contract WHERE f_user_id = ?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userID);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0));
	}
	
	public NAccountContract readBTCByUidCode(String partition, long userID, String code) {
		StringBuffer sql = new StringBuffer("select * FROM " + partition + "_account_contract WHERE f_user_id = :uid and f_code=:code");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);

		return nAccountContractDao.getBySql(sql.toString(), params);
	}

	public NAccountContract findBTCByUidCode(String partition, long userID, String code) {
		StringBuffer sql = new StringBuffer("select * FROM " + partition + "_account_contract WHERE f_user_id = :uid and f_code=:code");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);

		return nAccountContractDao.getBySql(sql.toString(), params);
	}
	
	public NAccountContract getOverBalance(String partition, long userID, String code, BigDecimal amount) {
		StringBuffer sql = new StringBuffer("select * FROM " + partition + "_account_contract WHERE f_user_id = :uid and f_code=:code and f_btc > :amount");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);
		params.put("amount", amount);

		return nAccountContractDao.getBySql(sql.toString(), params); 
	}

	public PagerModel readCountUserIds(String partition) {
		Session session = nAccountContractDao.getSession();
		String sql = "Select f_user_id From " + partition + "_account_contract Group By f_user_id Having Count(*) > 1 ";
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
	public List<NAccountContract> findGreaterThan100(String partition, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return nAccountContractDao.findBySql("select * From " + partition + "_account_contract WHERE f_code = :code and f_btc > 100", params);
	}

	@Override
	public List<NAccountContract> readAllByUid(String partition, long userID) {
		return nAccountContractDao.findByUserID(partition, userID);
	}
	
	@Override
	public BigDecimal findSumBalanceByUser(String partition, String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(f_btc + f_lockbtc) from " + partition + "_account_contract where f_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return nAccountContractDao.sumBySql(hql, map);
	}

	@Override
	public BigDecimal readSumBalanceByUser(String partition, String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(f_btc + f_lockbtc) from " + partition + "_account_contract where f_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return nAccountContractDao.sumBySql(hql, map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readBalanceAndFrozen(String partition, long userId) {
		Session session = nAccountContractDao.getSession();
		String sql = "SELECT f_user_id, IFNULL(sum(f_btc), 0) AS balance, IFNULL(sum(f_lockbtc), 0) AS frozen FROM " + partition + "_account_contract where 1 = 1";
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
