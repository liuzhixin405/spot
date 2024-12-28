package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQAreaProvince;

public interface BQAreaProvinceService {

	List<BQAreaProvince> readProvice(String countryCode);

	BQAreaProvince readProviceByCode(String provice);
	
	BQAreaProvince readProviceByName(String provice);

}
