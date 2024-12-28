package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NAccountContractDao;
import com.btc100.btc_q.entity.NAccountContract;

@Repository("nAccountContractDao")
public class NAccountContractDaoImpl extends BaseDaoImpl<NAccountContract> implements NAccountContractDao<NAccountContract> {

	public int updateAmount(String partition, long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle) {
		
		this.getSession().flush();
		this.getSession().clear();
		
		String sqlsets = "";
		String sqlwhere = "";
		if (initBtc.signum() != 0) {
			sqlsets = sqlsets + "f_initbtc=f_initbtc+:initbtc,";
			sqlwhere = sqlwhere + " AND f_initbtc+:initbtc>=0";
		}
		if (btc.signum() != 0) {
			sqlsets = sqlsets + "f_btc=f_btc+:btc,";
			sqlwhere = sqlwhere + " AND f_btc+:btc>=0";
		}
		if (lockBtc.signum() != 0) {
			sqlsets = sqlsets + "f_lockbtc=f_lockbtc+:lockbtc,";
			sqlwhere = sqlwhere + " AND f_lockbtc+:lockbtc>=0";
		}
		if (settle.signum() != 0) {
			sqlsets = sqlsets + "f_settled=f_settled+:settled";
			sqlwhere = sqlwhere + " AND f_settled+:settled>=0";
		}

		if (sqlsets.endsWith(",")) {
			sqlsets = sqlsets.substring(0, sqlsets.length() - 1);
		} else {
			if (sqlsets.length() <= 0)
				return 0;
		}

		String sql = "update " + partition + "_account_contract set " + sqlsets + " WHERE f_user_id=:uid AND f_code=:code" + sqlwhere;
		
		SQLQuery query = getSession().createSQLQuery(sql);
		if (initBtc.signum() != 0) {
			query.setParameter("initbtc", initBtc);
		}
		if (btc.signum() != 0) {
			query.setParameter("btc", btc);
		}
		if (lockBtc.signum() != 0) {
			query.setParameter("lockbtc", lockBtc);
		}
		if (settle.signum() != 0) {
			query.setParameter("settled", settle);
		}
		
		query.setParameter("uid", userID);
		query.setParameter("code", code);
		
		return query.executeUpdate();
	}

	public int updateAmountCanFS(String partition, long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle) {
		String sqlsets = "";
		if (initBtc.signum() != 0) {
			sqlsets = sqlsets + "f_initbtc=f_initbtc+:initbtc,";
		}
		if (btc.signum() != 0) {
			sqlsets = sqlsets + "f_btc=f_btc+:btc,";
		}
		if (lockBtc.signum() != 0) {
			sqlsets = sqlsets + "f_lockbtc=f_lockbtc+:lockbtc,";
		}
		if (settle.signum() != 0) {
			sqlsets = sqlsets + "f_settled=f_settled+:settled";
		}

		if (sqlsets.endsWith(",")) {
			sqlsets = sqlsets.substring(0, sqlsets.length() - 1);
		} else {
			if (sqlsets.length() <= 0)
				return 0;
		}

		String sql = "update " + partition + "_account_contract set " + sqlsets + " WHERE f_user_id=:uid AND f_code=:code";
		SQLQuery query = getSession().createSQLQuery(sql);
		if (initBtc.signum() != 0) {
			query.setParameter("initbtc", initBtc);
		}
		if (btc.signum() != 0) {
			query.setParameter("btc", btc);
		}
		if (lockBtc.signum() != 0) {
			query.setParameter("lockbtc", lockBtc);
		}
		if (settle.signum() != 0) {
			query.setParameter("settled", settle);
		}
		
		query.setParameter("uid", userID);
		query.setParameter("code", code);
		
		return query.executeUpdate();
	}

	public NAccountContract findByUserIDCode(String partition, long userID, String code) {
		String sql = "select * from " + partition + "_account_contract WHERE f_user_id=:uid AND f_code=:code";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("uid", userID);
		query.setParameter("code", code);
		query.addEntity(NAccountContract.class);
		
		return (NAccountContract) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<NAccountContract> findByUserID(String partition, long userID) {
		String sql = "select * from " + partition + "_account_contract WHERE f_user_id=:uid";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("uid", userID);
		query.addEntity(NAccountContract.class);
		
		return query.list();
	}

	@Override
	public int insert(String partition, NAccountContract account) {
		Session session = getSession();
		String sql = "insert into " + partition + "_account_contract "
				+ "(f_user_id,f_code,f_initbtc,f_btc,f_lockbtc,f_settled) "
				+ "values (?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setLong(0, account.getF_user_id());
		query.setString(1, account.getF_code());
		query.setBigDecimal(2, account.getF_initbtc());
		query.setBigDecimal(3, account.getF_btc());
		query.setBigDecimal(4, account.getF_lockbtc());
		query.setBigDecimal(5, account.getF_settled());
		
		return query.executeUpdate();
	}

}
