package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.VAccountContractDao;

@Repository("vAccountContractDao")
public class VAccountContractDaoImpl<T> extends BaseDaoImpl<T> implements VAccountContractDao<T> {

	private static final Logger logger = Logger.getLogger(VAccountContractDaoImpl.class);

	public int updateAmount(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle) {
		
		this.getSession().flush();
		this.getSession().clear();
		
		Map<String, Object> map = new HashMap<String, Object>();
		String sqlsets = "";
		String sqlwhere = "";
		if (initBtc.signum() != 0) {
			sqlsets = sqlsets + "f_initbtc=f_initbtc+:initbtc,";
			sqlwhere = sqlwhere + " AND f_initbtc+:initbtc>=0";
			map.put("initbtc", initBtc);
		}
		if (btc.signum() != 0) {
			sqlsets = sqlsets + "f_btc=f_btc+:btc,";
			sqlwhere = sqlwhere + " AND f_btc+:btc>=0";
			map.put("btc", btc);
		}
		if (lockBtc.signum() != 0) {
			sqlsets = sqlsets + "f_lockbtc=f_lockbtc+:lockbtc,";
			sqlwhere = sqlwhere + " AND f_lockbtc+:lockbtc>=0";
			map.put("lockbtc", lockBtc);
		}
		if (settle.signum() != 0) {
			sqlsets = sqlsets + "f_settled=f_settled+:settled";
			sqlwhere = sqlwhere + " AND f_settled+:settled>=0";
			map.put("settled", settle);
		}

		if (sqlsets.endsWith(",")) {
			sqlsets = sqlsets.substring(0, sqlsets.length() - 1);
		} else {
			if (sqlsets.length() <= 0)
				return 0;
		}

		map.put("uid", userID);
		map.put("code", code);

		String sql = "update VAccountContract set " + sqlsets + " WHERE f_user_id=:uid AND f_code=:code" + sqlwhere;
		int ires = super.updateCondition(sql, map);
		return ires;
	}

	public int updateAmountCanFS(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle) {
		Map<String, Object> map = new HashMap<String, Object>();
		String sqlsets = "";
		if (initBtc.signum() != 0) {
			sqlsets = sqlsets + "f_initbtc=f_initbtc+:initbtc,";
			map.put("initbtc", initBtc);
		}
		if (btc.signum() != 0) {
			sqlsets = sqlsets + "f_btc=f_btc+:btc,";
			map.put("btc", btc);
		}
		if (lockBtc.signum() != 0) {
			sqlsets = sqlsets + "f_lockbtc=f_lockbtc+:lockbtc,";
			map.put("lockbtc", lockBtc);
		}
		if (settle.signum() != 0) {
			sqlsets = sqlsets + "f_settled=f_settled+:settled";
			map.put("settled", settle);
		}

		if (sqlsets.endsWith(",")) {
			sqlsets = sqlsets.substring(0, sqlsets.length() - 1);
		} else {
			if (sqlsets.length() <= 0)
				return 0;
		}

		map.put("uid", userID);
		map.put("code", code);

		String sql = "update VAccountContract set " + sqlsets + " WHERE f_user_id=:uid AND f_code=:code";
		return super.updateCondition(sql, map);
	}

	public T findByUserIDCode(long userID, String code) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("uid", userID);
		map.put("code", code);
		// String
		// selSQL="FROM VAccountContract WHERE f_user_id=:uid AND f_code=:code";

		String sql = "FROM VAccountContract WHERE f_user_id=:uid AND f_code=:code";
		List<T> list = super.find(sql, map, 1, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<T> findByUserID(long userID) {
		Map<String, Object> map = new HashMap<String, Object>();		
		map.put("uid", userID);		
		String sql = "FROM VAccountContract WHERE f_user_id=:uid";
		return super.find(sql, map);
	}

}
