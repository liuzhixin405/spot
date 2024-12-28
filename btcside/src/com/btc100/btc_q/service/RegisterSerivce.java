package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQBcexMember;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberArea;

public interface RegisterSerivce {
	int RegisterSuccess(BQMember user, String invid, String uip, String country, String province, String city,
			String countryName);

	int RegisterSuccess(BQMember user, String invid, String uip, String country, String province, String city,
						String countryName, boolean isPhone);

	int RegisterSuccess(BQMember user, String invid, String uip, String country, String province, String city,
			String countryName, String zone, boolean isPhone);

	public void click(long uid, String ip);

	int bcexRegister(BQMember user, String uip, BQBcexMember bcexMember);
	
	int registerSubUser(BQMember motherUser, BQMember subUser, String uip, BQMemberArea area);
}
