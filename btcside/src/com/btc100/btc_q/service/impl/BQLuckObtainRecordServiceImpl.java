package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQLuckObtainRecordDao;
import com.btc100.btc_q.entity.BQLuckObtainRecord;
import com.btc100.btc_q.service.BQLuckObtainRecordService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("bqLuckObtainRecordService")
public class BQLuckObtainRecordServiceImpl implements BQLuckObtainRecordService {

    @Autowired
    private BQLuckObtainRecordDao<BQLuckObtainRecord> bqLuckObtainRecordDao;

    @Override
    public List<BQLuckObtainRecord> readLast20Draw() {
        StringBuilder sql = new StringBuilder("select * from bq_luck_obtain_record ORDER BY obtain_time DESC LIMIT 20");
        Session session = bqLuckObtainRecordDao.getSession();
        SQLQuery query = session.createSQLQuery(sql.toString());
        query.addEntity(BQLuckObtainRecord.class);
        return (List<BQLuckObtainRecord>) query.list();
    }

    @Override
    public BigDecimal readCountCoin(long uid) {
        return bqLuckObtainRecordDao.findCountCoin(uid);
    }

    @Override
    public Map<Integer, Integer> readCountGroupPrizeByUid(long uid) {
        StringBuilder sql = new StringBuilder("SELECT prize_id as id, IFNULL(count(prize_id), 0) as count FROM bq_luck_obtain_record WHERE user_id=? GROUP BY prize_id ");
        Session session = bqLuckObtainRecordDao.getSession();
        SQLQuery query = session.createSQLQuery(sql.toString());
        query.setLong(0, uid);
        List<Object[]> obtainRecords = (List<Object[]>) query.list();
        Map<Integer, Integer> integerMap = new HashMap<>();
        for (Object[] obtainRecord : obtainRecords) {
           int prize = (int)obtainRecord[0];
           BigInteger count = (BigInteger) obtainRecord[1];
           integerMap.put(prize, count.intValue());
        }

        return integerMap;
    }

    @Override
    public BQLuckObtainRecord readMatterPrizeById(long uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);

        return bqLuckObtainRecordDao.get("From BQLuckObtainRecord where user_id = :uid AND prize_matter <> '' ", params);
    }
}
