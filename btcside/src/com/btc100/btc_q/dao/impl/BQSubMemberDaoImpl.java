package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQSubMemberDao;

@Repository("bqSubMemberDao")
public class BQSubMemberDaoImpl<T> extends BaseDaoImpl<T> implements BQSubMemberDao<T> {

}
