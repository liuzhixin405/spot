package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQTransferWithOtcRecordDao;

@Repository("bqTransferWithOtcRecordDao")
public class BQTransferWithOtcRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQTransferWithOtcRecordDao<T> {

	@Override
	public BigDecimal sumFrozen(long uid, int currencyId, int direction) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"SELECT ifnull(SUM(frozen_amount), 0) from bq_transfer_with_otc_record where f_user_id = :uid ");
		sql.append(" and direction = :direction");
		sql.append(" and state = :state");
		sql.append(" and currency_type_id = :currencyId ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("direction", direction);
		query.setParameter("state", Contents.TRANSFER_OTC_STATE_MANUAL);
		query.setParameter("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}

}
