package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQLoanSettingDao;

@Repository("bqLoanSettingDao")
public class BQLoanSettingDaoImpl<T> extends BaseDaoImpl<T> implements BQLoanSettingDao<T> {

	@Override
	public T getLoanSettingByCode(String borrow, String pledge) {
		Map<String, Object> params = new HashMap<>();
		params.put("pledge", pledge);
		params.put("borrow", borrow);
		
		return get("from BQLoanSetting where pledge_symbol=:pledge and borrow_symbol=:borrow", params);
	} 
	
}
