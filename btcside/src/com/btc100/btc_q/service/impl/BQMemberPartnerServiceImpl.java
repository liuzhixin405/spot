package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberPartnerDao;
import com.btc100.btc_q.entity.BQMemberPartner;
import com.btc100.btc_q.service.BQMemberPartnerService;

@Service("bqMemberPartnerService")
public class BQMemberPartnerServiceImpl implements BQMemberPartnerService {

	@Autowired
	private BQMemberPartnerDao<BQMemberPartner> bqMemberPartnerDao;

	public BQMemberPartner findUserApiById(long userid) {
		return bqMemberPartnerDao.get(BQMemberPartner.class, userid);
	}

	public BQMemberPartner readApiById(long fUserId) {
		final String hql = "from BQMemberPartner u where u.f_user_id = :id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", fUserId);
		BQMemberPartner user = (BQMemberPartner) bqMemberPartnerDao.get(hql, paramMap);
		return user;
	}

	public void save(BQMemberPartner bqp) {
		bqMemberPartnerDao.save(bqp);
	}

}
