package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQAppointLuck;

public interface BQAppointLuckDao<T> extends BaseDaoI<T>{

    BQAppointLuck getByUid(long uid);

    int updateNumByUid(long uid);
}
