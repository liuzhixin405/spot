package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQLoanAdjustRecordDao;

@Repository("bqLoanAdjustRecordDao")
public class BQLoanAdjustRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQLoanAdjustRecordDao<T> { }
