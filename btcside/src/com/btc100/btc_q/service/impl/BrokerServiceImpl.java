package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQMemberApplyDao;
import com.btc100.btc_q.dao.BQMemberApplyIdcardDao;
import com.btc100.btc_q.entity.BQMemberApply;
import com.btc100.btc_q.entity.BQMemberApplyIdcard;
import com.btc100.btc_q.service.BrokerService;

@Service("brokerService")
public class BrokerServiceImpl implements BrokerService {

	@Autowired
	private BQMemberApplyDao<BQMemberApply> bqMemberApplyDao;
	@Autowired
	private BQMemberApplyIdcardDao<BQMemberApplyIdcard> bqMemberApplyIdcardDao;

	public void saveBroker(long userId , String userTel, String userNo, String userAddr, String photo1, String photo2, String photo3) {
		BQMemberApplyIdcard applyIdcard = new BQMemberApplyIdcard();
		applyIdcard.setF_user_id(userId);
		applyIdcard.setPhoto_1(photo1);
		applyIdcard.setPhoto_2(photo2);
		applyIdcard.setPhoto_3(photo3);
		bqMemberApplyIdcardDao.save(applyIdcard);
		
		BQMemberApply apply = new BQMemberApply();
		apply.setF_user_id(userId);
		apply.setIdcard(userNo);
		apply.setMobile(userTel);
		apply.setAddress(userAddr);
		apply.setDateline(bqMemberApplyDao.getUnix_TIMESTAMP());
		apply.setState(Contents.BQMEMBER_APPLY_WAITE);
		apply.setUser_type(Contents.BQMEMBER_APPLY_BROKER);
		apply.setReason("");
		bqMemberApplyDao.save(apply);
	}
	
	public void delBroker(long userId) {
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("userid", userId);
		bqMemberApplyDao.deleteCondition("Delete from BQMemberApply where f_user_id = :userid", params1);
		Map<String, Object> params2 = new HashMap<String, Object>();
		params2.put("userid", userId);
		bqMemberApplyIdcardDao.deleteCondition("Delete from BQMemberApplyIdcard where f_user_id = :userid", params2);	
	}

	public BQMemberApplyIdcard searchBroker(long userId) {
		return bqMemberApplyIdcardDao.get(BQMemberApplyIdcard.class, userId);
	}

}
