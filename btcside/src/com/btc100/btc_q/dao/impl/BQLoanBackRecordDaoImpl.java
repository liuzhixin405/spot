package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQLoanBackRecordDao;

@Repository("bqLoanBackRecordDao")
public class BQLoanBackRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQLoanBackRecordDao<T> { }

