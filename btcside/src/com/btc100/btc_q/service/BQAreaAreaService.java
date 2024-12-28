package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQAreaArea;

import java.util.List;

public interface BQAreaAreaService {

    List<BQAreaArea> readAreas(String cityCode);

    BQAreaArea readAreaByName(String area);

    BQAreaArea readAreaByCityCodeAndAreaName(String areaName, String cityCode);

    BQAreaArea readAreaByCode(String code);
}
