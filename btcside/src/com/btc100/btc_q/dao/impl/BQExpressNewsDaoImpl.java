package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQExpressNewsDao;

@Repository("bqExpressNewsDao")
public class BQExpressNewsDaoImpl<T> extends BaseDaoImpl<T> implements BQExpressNewsDao<T> {

}
