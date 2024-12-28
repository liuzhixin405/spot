package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQStopLossDao;

@Repository("bqStopLossDao")
public class BQStopLossDaoImpl<T> extends BaseDaoImpl<T> implements BQStopLossDao<T> {

}
