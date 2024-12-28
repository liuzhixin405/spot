package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NMemberTradeDao;
import com.btc100.btc_q.entity.NMemberTrade;

@Repository("nMemberTradeDao")
public class NMemberTradeDaoImpl extends BaseDaoImpl<NMemberTrade> implements NMemberTradeDao<NMemberTrade>{

	@Override
	public void insertMemberTrade(String partition, NMemberTrade memberTrade, String code) {
		Session session = getSession();
        String sql = "insert into " + partition + "_" + code + "_member_trade"
            + " (f_user_id,trade_id,dateline) values (?,?,?)";

        SQLQuery query = session.createSQLQuery(sql);
        query.setLong(0, memberTrade.getF_user_id());
        query.setLong(1, memberTrade.getTrade_id());
        query.setLong(2, getUnix_TIMESTAMP());
        query.executeUpdate();
	}

}
