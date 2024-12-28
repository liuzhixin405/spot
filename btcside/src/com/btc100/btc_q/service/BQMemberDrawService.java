package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberDraw;
import com.btc100.btc_q.vo.PagerModel;

import java.util.List;

public interface BQMemberDrawService {

    BQMemberDraw findById(long id);

    void update(BQMemberDraw bqMemberDraw);

    void save(BQMemberDraw bqMemberDraw);

    int resetAllFee();

    List<Long> findIds();

    /************************************ for read only **************************************/

    BQMemberDraw readById(long id);

    PagerModel readModel(long uid);

}
