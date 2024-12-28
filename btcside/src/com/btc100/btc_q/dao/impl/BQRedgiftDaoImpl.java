package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQRedgiftDao;

@Repository("bqRedgiftDao")
public class BQRedgiftDaoImpl<T> extends BaseDaoImpl<T> implements BQRedgiftDao<T> { }
