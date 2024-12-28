package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQNotradetimeDao;

@Repository("bqNotradetimeDao")
public class BQNotradetimeDaoImpl<T> extends BaseDaoImpl<T> implements BQNotradetimeDao<T> { }
