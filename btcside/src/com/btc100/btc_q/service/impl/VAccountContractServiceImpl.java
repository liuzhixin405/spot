package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VAccountContractDao;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.util.ConvertUtil;

@Service("vAccountContractService")
public class VAccountContractServiceImpl implements VAccountContractService {

	@Autowired
	private VAccountContractDao<VAccountContract> vAccountContractDao;

	public int UpdateAmount(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle) {
		return vAccountContractDao.updateAmount(userID, code, initBtc, btc, lockBtc, settle);
	}

	public int UpdateMoney(long userID, String code, BigDecimal btc, BigDecimal lockBtc) {
		return vAccountContractDao.updateAmount(userID, code, BigDecimal.ZERO, btc, lockBtc, BigDecimal.ZERO);
	}

	public VAccountContract findByUidCode(long userID, String code) {
		VAccountContract accountContract = vAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new VAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		
		return accountContract;
	}
	
	public VAccountContract readByUidCode(long userID, String code) {
		VAccountContract accountContract = vAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new VAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		
		return accountContract;
	}
	
	public VAccountContract readWithNullByUidCode(long userID, String code) {
		VAccountContract accountContract = vAccountContractDao.findByUserIDCode(userID, code);
		return accountContract;
	}
	
	public void createByUidCode(long userID, String code) {
		vAccountContractDao.getSession().flush();
		vAccountContractDao.getSession().clear();
		VAccountContract accountContract = vAccountContractDao.findByUserIDCode(userID, code);
		if (null == accountContract) {
			accountContract = new VAccountContract(userID, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
			vAccountContractDao.save(accountContract);
		}
	}

	public BigDecimal readLockAmountByUid(long userID) {
		Session session = vAccountContractDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(f_lockbtc),0) FROM v_account_contract WHERE f_user_id = ?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userID);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0));
	}

	public BigDecimal readAmountByUid(long userID) {
		Session session = vAccountContractDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(f_btc),0) FROM v_account_contract WHERE f_user_id = ?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userID);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0));
	}

	public VAccountContract findBTCByUidCode(long userID, String code) {
		StringBuffer sql = new StringBuffer("FROM VAccountContract WHERE f_user_id = :uid and f_code=:code");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);

		List<VAccountContract> list = vAccountContractDao.find(sql.toString(), params);

		if (list == null || list.size() <= 0)
			return null;
		return list.get(0);
	}
	
	public VAccountContract getOverBalance(long userID, String code, BigDecimal amount) {
		StringBuffer sql = new StringBuffer("FROM VAccountContract WHERE f_user_id = :uid and f_code=:code and f_btc > :amount");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);
		params.put("amount", amount);

		return vAccountContractDao.get(sql.toString(), params); 
	}
	
	public VAccountContract readBTCByUidCode(long userID, String code) {
		StringBuffer sql = new StringBuffer("FROM VAccountContract WHERE f_user_id = :uid and f_code=:code");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userID);
		params.put("code", code);

		List<VAccountContract> list = vAccountContractDao.find(sql.toString(), params);

		if (list == null || list.size() <= 0)
			return null;
		return list.get(0);
	}

	public int deletetobak(String code) {
		Session session = vAccountContractDao.getSession();

		String sql1 = "INSERT INTO v_account_contract_yjg SELECT * FROM v_account_contract WHERE f_code='" + code + "'";
		int query1 = session.createSQLQuery(sql1.toString()).executeUpdate();

		String sql2 = "DELETE FROM VAccountContract WHERE f_code=:code";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		int query2 = vAccountContractDao.deleteCondition(sql2, params);

		return query1 + query2;
	}

	@Override
	public List<VAccountContract> readAllByUid(long userID) {
		return vAccountContractDao.findByUserID(userID);
	}

	@Override
	public BigDecimal findSumBalanceByUser(String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(f_btc + f_lockbtc) from VAccountContract where f_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return vAccountContractDao.sum(hql, map);
	}

}
