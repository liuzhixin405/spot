package com.btc100.btc_q.dao.impl;

import com.btc100.btc_q.dao.BQAppointLuckDao;
import com.btc100.btc_q.entity.BQAppointLuck;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository("bqAppointLuckDao")
public class BQAppointLuckDaoImpl<T> extends BaseDaoImpl<T> implements BQAppointLuckDao<T> {
    @Override
    public BQAppointLuck getByUid(long uid) {
        if (uid <= 0) {
            return  null;
        }
        String hql = "FROM BQAppointLuck where user_id = :uid";
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        return (BQAppointLuck) super.get(hql, map);
    }

    @Override
    public int updateNumByUid(long uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        String hql = "update BQAppointLuck set number = number - 1 WHERE user_id = :uid";
        return super.updateCondition(hql, params);
    }
}
