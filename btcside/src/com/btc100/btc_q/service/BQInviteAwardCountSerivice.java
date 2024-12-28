package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.text.ParseException;

public interface BQInviteAwardCountSerivice {

	public void saveAwardCount(long userId, BigDecimal fee) throws ParseException;

}
