package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBcexMemberDao;
import com.btc100.btc_q.entity.BQBcexMember;
import com.btc100.btc_q.service.BQBcexMemberService;

@Service("bqBcexMemberService")
public class BQBcexMemberServiceImpl implements BQBcexMemberService {

	@Autowired
	private BQBcexMemberDao<BQBcexMember> bqBcexMemberDao;

	@Override
	public BQBcexMember readByEmail(String uemail) {
		return bqBcexMemberDao.getByEmail(uemail);
	}

	@Override
	public void save(BQBcexMember member) {
		bqBcexMemberDao.save(member);
	}

	@Override
	public void update(BQBcexMember member) {
		bqBcexMemberDao.update(member);
	}

}
