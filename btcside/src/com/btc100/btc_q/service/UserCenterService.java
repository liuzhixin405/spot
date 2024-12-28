package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberRecharge;

public interface UserCenterService {

	public void DoWithdraw(BQMember user, BQMemberRecharge bq) throws com.btc100.btc_q.exception.ServiceException;
}
