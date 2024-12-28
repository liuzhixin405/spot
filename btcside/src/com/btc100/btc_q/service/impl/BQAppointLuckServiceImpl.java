package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQAppointLuckDao;
import com.btc100.btc_q.entity.BQAppointLuck;
import com.btc100.btc_q.service.BQAppointLuckService;
import com.btc100.btc_q.vo.PagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("bqAppointLuckService")
public class BQAppointLuckServiceImpl implements BQAppointLuckService {

    @Autowired
    private BQAppointLuckDao<BQAppointLuck> bqAppointLuckDao;

    @Override
    public int deleteById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return bqAppointLuckDao.deleteCondition("delete from BQAppointLuck where id = :id", map);
    }

    @Override
    public PagerModel readByUid(long uid) {
        StringBuffer hql = new StringBuffer("from BQAppointLuck where 1=1 ");
        if (uid > 0) {
            hql.append("AND user_id = :uid");
        }
        Map<String, Object> map = new HashMap<>();
        if (uid > 0) map.put("uid", uid);

        return bqAppointLuckDao.searchHql(hql.toString(), map);
    }

    @Override
    public BQAppointLuck getByUid(long uid) {
        return bqAppointLuckDao.getByUid(uid);
    }

    @Override
    public void save(BQAppointLuck bqAppointLuck) {
        bqAppointLuckDao.save(bqAppointLuck);
    }
}
