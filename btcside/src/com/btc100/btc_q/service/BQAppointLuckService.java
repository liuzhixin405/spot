package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQAppointLuck;
import com.btc100.btc_q.vo.PagerModel;

public interface BQAppointLuckService {

    int deleteById(int id);

    BQAppointLuck getByUid(long uid);

    void save(BQAppointLuck bqAppointLuck);

    ///************************************ for read only **************************************/

    PagerModel readByUid(long uid);
}
