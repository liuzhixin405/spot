package com.btc100.btc_q.service;

import java.math.BigDecimal;
import com.btc100.btc_q.entity.BQMineStageSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMineStageSettingService {

	void save(BQMineStageSetting setting);
	
	void update(BQMineStageSetting setting);

	void updateOutNum(BQMineStageSetting setting);

	void deleteById(long id);

	BigDecimal getRateByStageAmount(BigDecimal amount);

	BQMineStageSetting getByStageAmount(BigDecimal amount);

	BQMineStageSetting getNextSetting(int num);
	
	
	/************************************ for read only **************************************/
	
	BQMineStageSetting readMineStageSettingById(long id);
	
	BQMineStageSetting readByStageAmount(BigDecimal amount);
	
	PagerModel readAllStages();

}

