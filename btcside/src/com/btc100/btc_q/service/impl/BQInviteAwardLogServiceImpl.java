package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQInviteAwardLogDao;
import com.btc100.btc_q.entity.BQInviteAwardLog;
import com.btc100.btc_q.service.BQInviteAwardLogService;

@Service("bqInviteAwardLogService")
public class BQInviteAwardLogServiceImpl implements BQInviteAwardLogService {

	@Autowired
	private BQInviteAwardLogDao<BQInviteAwardLog> bqInviteAwardLogDao;

	public void saveInvateWardLog(long userId, String title, BigDecimal tradeFee, long uid, String uemail, long code) {
		BQInviteAwardLog awardLog = new BQInviteAwardLog();
		awardLog.setF_user_id(userId);
		awardLog.setTitle(title);
		awardLog.setCommission(tradeFee);
		awardLog.setCommission_rate(new BigDecimal(0.3));
		awardLog.setAward_amount(tradeFee.multiply(new BigDecimal(0.3)));
		awardLog.setDateline(bqInviteAwardLogDao.getUnix_TIMESTAMP());
		awardLog.setUid(uid);
		awardLog.setEmail(uemail);
		awardLog.setTrade_id(code);
		bqInviteAwardLogDao.save(awardLog);
	}

}
