package com.btc100.btc_q.service;

import java.util.List;
import com.btc100.btc_q.entity.BQAreaCity;

public interface BQAreaCityService {
	
	List<BQAreaCity> readCitys(String proviceCode);
	
	BQAreaCity readCityByName(String name);

	BQAreaCity readCityByCode(String code);
	
}
