package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.SpotMemberTradeDao;
import com.btc100.btc_q.entity.SpotMemberTrade;
@Repository("spotMemberTradeDao")
public class SpotMemberTradeDaoImpl<MemberTrade> extends BaseDaoImpl<MemberTrade> implements SpotMemberTradeDao<MemberTrade>{

	@Override
	public void insertSpotMemberTrade(SpotMemberTrade memberTrade, String code) {
		Session session = getSession();
        String sql = "insert into "+code+"_member_trade"
            + " (f_user_id,trade_id,dateline) values (?,?,?)";

        SQLQuery query = session.createSQLQuery(sql);
        query.setLong(0, memberTrade.getF_user_id());
        query.setLong(1, memberTrade.getTrade_id());
        query.setLong(2, getUnix_TIMESTAMP());
        query.executeUpdate();
	}

}
