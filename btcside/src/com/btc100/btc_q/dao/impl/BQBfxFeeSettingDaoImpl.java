package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQBfxFeeSettingDao;

@Repository("bqBfxFeeSettingDao")
public class BQBfxFeeSettingDaoImpl<T> extends BaseDaoImpl<T> implements BQBfxFeeSettingDao<T> {

	@Override
	public T get() {
		return super.get("from BQBfxFeeSetting order by id desc");
	}

}
