package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQAreaCountry;

import java.util.List;

public interface BQAreaCountryService {

	List<BQAreaCountry> readCountry();

	BQAreaCountry readCountyByName(String countyName);

	BQAreaCountry readCountyByCode(String countyCode);
}
