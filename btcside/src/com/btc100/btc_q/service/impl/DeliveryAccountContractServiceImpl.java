package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryAccountContractDao;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("deliveryAccountContractService")
public class DeliveryAccountContractServiceImpl implements DeliveryAccountContractService {

	@Autowired
	private DeliveryAccountContractDao<DeliveryAccountContract> deliveryAccountContractDao;

	public int UpdateAmount(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle) {
		return deliveryAccountContractDao.updateAmount(userID, code, initBtc, btc, lockBtc, settle);
	}

	public int UpdateMoney(long userID, String code, BigDecimal btc, BigDecimal lockBtc) {
		return deliveryAccountContractDao.updateAmount(userID, code, BigDecimal.ZERO, btc, lockBtc, BigDecimal.ZERO);
	}

	public DeliveryAccountContract findByUidCode(long userID, String code) {
		DeliveryAccountContract accountContract = deliveryAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new DeliveryAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		
		return accountContract;
	}
	
	public DeliveryAccountContract readByUidCode(long userID, String code) {
		DeliveryAccountContract accountContract = deliveryAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new DeliveryAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		
		return accountContract;
	}
	
	public DeliveryAccountContract readWithNullByUidCode(long userID, String code) {
		DeliveryAccountContract accountContract = deliveryAccountContractDao.findByUserIDCode(userID, code);
		return accountContract;
	}
	
	public void createByUidCode(long userID, String code) {
		deliveryAccountContractDao.getSession().flush();
		deliveryAccountContractDao.getSession().clear();
		
		DeliveryAccountContract accountContract = deliveryAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new DeliveryAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
			deliveryAccountContractDao.save(accountContract);
		}
	}

	public BigDecimal readLockAmountByUid(long userID) {
		Session session = deliveryAccountContractDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(f_lockbtc),0) FROM delivery_account_contract WHERE f_user_id = ?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userID);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0));
	}

	public BigDecimal readAmountByUid(long userID) {
		Session session = deliveryAccountContractDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(f_btc),0) FROM delivery_account_contract WHERE f_user_id = ?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userID);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0));
	}
	
	public DeliveryAccountContract readBTCByUidCode(long userID, String code) {
		StringBuffer sql = new StringBuffer("FROM DeliveryAccountContract WHERE f_user_id = :uid and f_code=:code");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);

		List<DeliveryAccountContract> list = deliveryAccountContractDao.find(sql.toString(), params);

		if (list == null || list.size() <= 0)
			return null;
		return list.get(0);
	}

	public DeliveryAccountContract findBTCByUidCode(long userID, String code) {
		StringBuffer sql = new StringBuffer("FROM DeliveryAccountContract WHERE f_user_id = :uid and f_code=:code");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);

		List<DeliveryAccountContract> list = deliveryAccountContractDao.find(sql.toString(), params);

		if (list == null || list.size() <= 0)
			return null;
		return list.get(0);
	}

	public PagerModel readCountUserIds() {
		Session session = deliveryAccountContractDao.getSession();
		String sql = "Select f_user_id From delivery_account_contract Group By f_user_id Having Count(*) > 1 ";
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
	public List<DeliveryAccountContract> findGreaterThan100(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return deliveryAccountContractDao.find("From DeliveryAccountContract WHERE f_code = :code and f_btc > 100", params);
	}
	
	@Override
	public List<DeliveryAccountContract> findGreaterThanZero(String code, int size) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return deliveryAccountContractDao.find("From DeliveryAccountContract WHERE f_code = :code and f_btc > 0  ORDER BY f_id DESC", params, 0, size);
	}

	@Override
	public List<DeliveryAccountContract> readAllByUid(long userID) {
		return deliveryAccountContractDao.findByUserID(userID);
	}
	
	@Override
	public BigDecimal findSumBalanceByUser(String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(f_btc + f_lockbtc) from DeliveryAccountContract where f_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return deliveryAccountContractDao.sum(hql, map);
	}

	@Override
	public BigDecimal readSumBalanceByUser(String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(f_btc + f_lockbtc) from DeliveryAccountContract where f_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return deliveryAccountContractDao.sum(hql, map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readBalanceAndFrozen(long userId) {
		Session session = deliveryAccountContractDao.getSession();
		String sql = "SELECT f_user_id, IFNULL(sum(f_btc), 0) AS balance, IFNULL(sum(f_lockbtc), 0) AS frozen FROM delivery_account_contract where 1 = 1";
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
