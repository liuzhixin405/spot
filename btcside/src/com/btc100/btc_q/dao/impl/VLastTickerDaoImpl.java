package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.VLastTickerDao;

@Repository("vLastTickerDao")
public class VLastTickerDaoImpl<T> extends BaseDaoImpl<T> implements VLastTickerDao<T> { }
