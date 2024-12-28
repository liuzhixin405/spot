package com.btc100.btc_q.service;


import com.btc100.btc_q.entity.BQLuckDrawRecord;
import com.btc100.btc_q.vo.PagerModel;

import java.util.List;
import java.util.Map;

public interface BQLuckDrawRecordService {

    /************************************ for read only **************************************/

    PagerModel readModelByIdAndPrize(long uid, String prize);

    PagerModel readModelRecordById(long id);

    List<BQLuckDrawRecord> readDrawRecordById(long id);

    Map<String, Integer> readCountGroupPrizeByUid(long uid);

}
