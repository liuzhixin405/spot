package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQAreaAreaDao;
import com.btc100.btc_q.entity.BQAreaArea;
import com.btc100.btc_q.service.BQAreaAreaService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("bqAreaAreaService")

public class BQAreaAreaServiceImpl implements BQAreaAreaService {
    @Autowired
    private BQAreaAreaDao<BQAreaArea> bqAreaAreaDao;

    @Override
    public List<BQAreaArea> readAreas(String cityCode) {
        Session session=bqAreaAreaDao.getSession();
        String sql = "select * from bq_area_area where citycode=?";
        SQLQuery query = session.createSQLQuery(sql);
        query.setString(0, cityCode);
        query.addEntity(BQAreaArea.class);
        return query.list();
    }

    @Override
    public BQAreaArea readAreaByName(String area) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", area);
        return bqAreaAreaDao.get("from BQAreaArea where name=:name", params);
    }

    @Override
    public BQAreaArea readAreaByCityCodeAndAreaName(String areaName, String cityCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", areaName);
        params.put("citycode", cityCode);
        return bqAreaAreaDao.get("from BQAreaArea where name=:name and citycode=:citycode ", params);
    }

    @Override
    public BQAreaArea readAreaByCode(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        return bqAreaAreaDao.get("from BQAreaArea where code=:code", params);
    }
}
