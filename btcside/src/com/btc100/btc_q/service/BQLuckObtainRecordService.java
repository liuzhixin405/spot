package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQLuckObtainRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BQLuckObtainRecordService {

    /************************************ for read only **************************************/

    List<BQLuckObtainRecord> readLast20Draw();

    BigDecimal readCountCoin(long uid);

    Map<Integer, Integer> readCountGroupPrizeByUid(long uid);

    BQLuckObtainRecord readMatterPrizeById(long uid);
}
