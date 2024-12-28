package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberApplyIdcard;

public interface BrokerService {

	public void saveBroker(long userId, String userTel, String userNo, String userAddr, String photo1, String photo2, String photo3);

	public void delBroker(long userId);

	public BQMemberApplyIdcard searchBroker(long userId);

}
