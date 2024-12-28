package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQUserApiSetting;

public interface BQUserApiSettingService {

	BQUserApiSetting readOne();

	void update(BQUserApiSetting setting);

}
