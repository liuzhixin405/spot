package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQSurecomeDao;

@Repository("bqSurecomeDao")
public class BQSurecomeDaoImpl<T> extends BaseDaoImpl<T> implements BQSurecomeDao<T> { }
