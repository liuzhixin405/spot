package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQSuggestDao;

@Repository("bqSuggestDao")
public class BQSuggestDaoImpl<T> extends BaseDaoImpl<T> implements BQSuggestDao<T> { }
