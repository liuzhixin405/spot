package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQMemberApplyDao;

@Repository("bqMemberApplyDao")
public class BQMemberApplyDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberApplyDao<T> { }
