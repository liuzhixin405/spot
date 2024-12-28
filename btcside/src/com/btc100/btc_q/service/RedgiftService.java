package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQRedgift;

public interface RedgiftService {

	public void receiveRedgift(BQRedgift redgift, BQMember user, String IP);

}
