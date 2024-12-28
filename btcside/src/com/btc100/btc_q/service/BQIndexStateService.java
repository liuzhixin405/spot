package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQIndexState;

public interface BQIndexStateService {

    int updateStateByName(String indexName, int state);
    
    BQIndexState get();
    
    
    /************************************ for read only **************************************/
    
    BQIndexState read();
}
