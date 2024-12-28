package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQLuckDrawRecordDao;
import com.btc100.btc_q.entity.BQLuckDrawRecord;
import com.btc100.btc_q.service.BQLuckDrawRecordService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("bqLuckDrawRecord")
public class BQLuckDrawRecordServiceImpl implements BQLuckDrawRecordService {

    @Autowired
    private BQLuckDrawRecordDao<BQLuckDrawRecord> bqLuckDrawRecordDao;

    @Override
    public PagerModel readModelByIdAndPrize(long uid, String prize) {
        StringBuilder hql = new StringBuilder("From BQLuckDrawRecord WHERE 1=1 ");
        Map<String, Object> map = new HashMap<>();
        if (uid > 0) {
            hql.append(" AND user_id = :uid ");
            map.put("uid", uid);
        }
        if(!StringUtil.isEmpty(prize)) {
            hql.append(" AND prize = :prize ");
            map.put("prize", prize);
        }
        hql.append(" order by prize, draw_time DESC");
        return bqLuckDrawRecordDao.searchHql(hql.toString(), map);
    }

    @Override
    public PagerModel readModelRecordById(long id) {
        Map<String, Object> map = new HashMap<>();
        if (id <= 0) {
            return new PagerModel();
        }
        String hql = "from BQLuckDrawRecord b where b.user_id = :id order by b.draw_time desc";
        map.put("id", id);
        return bqLuckDrawRecordDao.searchHql(hql, map);
    }

    @Override
    public List<BQLuckDrawRecord> readDrawRecordById(long id) {
        Map<String, Object> map = new HashMap<>();
        if (id <= 0) {
            return new ArrayList<BQLuckDrawRecord>();
        }
        String hql = "from BQLuckDrawRecord b where b.user_id = :id order by b.draw_time desc";
        map.put("id", id);
        return bqLuckDrawRecordDao.find(hql, map);
    }

    @Override
    public Map<String, Integer> readCountGroupPrizeByUid(long uid) {
        StringBuilder sql = new StringBuilder("SELECT prize, IFNULL(count(prize), 0) as count FROM bq_luck_draw_record WHERE user_id=? GROUP BY prize ");
        Session session = bqLuckDrawRecordDao.getSession();
        SQLQuery query = session.createSQLQuery(sql.toString());
        query.setLong(0, uid);
        List<Object[]> obtainRecords = (List<Object[]>) query.list();
        Map<String, Integer> integerMap = new HashMap<>();
        for (Object[] obtainRecord : obtainRecords) {
            String prize = obtainRecord[0].toString();
            BigInteger count = (BigInteger) obtainRecord[1];
            integerMap.put(prize, count.intValue());
        }
        return integerMap;
    }

}
