package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQMemberTransactionSettleDao;

@Repository("bqMemberTransactionSettleDao")
public class BQMemberTransactionSettleDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberTransactionSettleDao<T> {
}
