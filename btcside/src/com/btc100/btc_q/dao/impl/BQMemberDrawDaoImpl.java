package com.btc100.btc_q.dao.impl;

import com.btc100.btc_q.dao.BQMemberDrawDao;
import org.springframework.stereotype.Repository;

@Repository("bqMemberDrawDao")
public class BQMemberDrawDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberDrawDao<T> {
}
