package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberInviteRebateReconciliaDao;
import com.btc100.btc_q.entity.BQMemberInviteRebateReconcilia;
import com.btc100.btc_q.service.BQMemberInviteRebateReconciliaService;

@Service("bqMemberInviteRebateReconciliaService")
public class BQMemberInviteRebateReconciliaServiceImpl implements BQMemberInviteRebateReconciliaService {

	@Autowired
	private BQMemberInviteRebateReconciliaDao<BQMemberInviteRebateReconcilia> dao;
	
	@Override
	public void add(BQMemberInviteRebateReconcilia obj) {
		dao.save(obj);
	}
	
	@Override
	public void update(BQMemberInviteRebateReconcilia obj) {
		dao.update(obj);
	}
	
	@Override
	public BQMemberInviteRebateReconcilia get() {
		String hql = "from BQMemberInviteRebateReconcilia";
		List<BQMemberInviteRebateReconcilia> list = dao.find(hql, 1, 1);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public List<BQMemberInviteRebateReconcilia> findAll(int limit) {
		String hql = "from BQMemberInviteRebateReconcilia order by rebate desc ";
		return dao.find(hql, 1, limit);
	}
	
	@Override
	public Long getTime() {
		return dao.getUnix_TIMESTAMP();
	}

	
	@Override
	public BQMemberInviteRebateReconcilia getByUserId(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		BQMemberInviteRebateReconcilia re = dao.get("From BQMemberInviteRebateReconcilia where f_user_id = :userId", params);
		return re;
	}

}
