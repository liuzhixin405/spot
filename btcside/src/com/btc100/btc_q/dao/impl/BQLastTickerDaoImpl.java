package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQLastTickerDao;

@Repository("bqLastTickerDao")
public class BQLastTickerDaoImpl<T> extends BaseDaoImpl<T> implements BQLastTickerDao<T> { }
