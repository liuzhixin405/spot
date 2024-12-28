package com.btc100.btc_q.dao;

public interface BQIndexStateDao<T> extends BaseDaoI<T> {

    int updateStateByName(String indexName, int state);

}
