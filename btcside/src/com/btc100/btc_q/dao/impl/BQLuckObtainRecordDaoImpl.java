package com.btc100.btc_q.dao.impl;

import com.btc100.btc_q.dao.BQLuckObtainRecordDao;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository("bqLuckObtainRecordDao")
public class BQLuckObtainRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQLuckObtainRecordDao<T> {
    @Override
    public BigDecimal findCountCoin(long uid) {
        StringBuilder sql = new StringBuilder("SELECT IFNULL(sum(b.prize_usdt),0) from bq_luck_obtain_record b ");
        if (uid > 0) {
            sql.append(" where user_id = ?");
        } else if (uid < 0){
            return BigDecimal.ZERO;
        }
        Session session = getSession();
        SQLQuery query = session.createSQLQuery(sql.toString());
        if (uid > 0) {
            query.setLong(0, uid);
        } 
        return (BigDecimal) query.uniqueResult();
    }
}
