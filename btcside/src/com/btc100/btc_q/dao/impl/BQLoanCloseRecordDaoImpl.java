package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQLoanCloseRecordDao;

@Repository("bqLoanCloseRecordDao")
public class BQLoanCloseRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQLoanCloseRecordDao<T> { }
