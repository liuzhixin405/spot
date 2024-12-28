package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberModifyInformationRecord;
import com.btc100.btc_q.vo.PagerModel;

import java.util.List;

public interface BQMemberModifyInformationRecordService {

    void save(BQMemberModifyInformationRecord modifyInformationRecord);

    PagerModel readPagerModel(long uid, int type, String modifyUser, String name, int opertionType);

    List<BQMemberModifyInformationRecord> readByIdAndType(long uid, int type, String modifyUser, String name);

}
