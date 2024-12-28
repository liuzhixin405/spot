package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.BQMemberApplyIdcardDao;
import com.btc100.btc_q.entity.BQMemberApplyIdcard;
import com.btc100.btc_q.service.BQMemberApplyIdcardService;

@Service("bqMemberApplyIdcardService")
public class BQMemberApplyIdcardServiceImpl implements BQMemberApplyIdcardService {

	@Autowired
	private BQMemberApplyIdcardDao<BQMemberApplyIdcard> bqMemberApplyIdcardDao;

	public void saveApplyIdCard(BQMemberApplyIdcard applyIdcard) {
		bqMemberApplyIdcardDao.save(applyIdcard);
	}

	public void deleteApplyByUserid(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", userId);
		bqMemberApplyIdcardDao.deleteCondition("Delete from BQMemberApplyIdcard where f_user_id = :userid", params);
	}

}
