package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQMemberStatusDao;

@Repository("bqMemberStatusDao")
public class BQMemberStatusDaoImpl<BQMemberStatus> extends BaseDaoImpl<BQMemberStatus> implements BQMemberStatusDao<BQMemberStatus>{

}
