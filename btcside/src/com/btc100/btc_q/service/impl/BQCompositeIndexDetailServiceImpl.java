package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQCompositeIndexDetailDao;
import com.btc100.btc_q.entity.BQCompositeIndexDetail;
import com.btc100.btc_q.service.BQCompositeIndexDetailService;

@Service("bqTransactionCompositeIndexService")
public class BQCompositeIndexDetailServiceImpl implements BQCompositeIndexDetailService {

	@Autowired
	private BQCompositeIndexDetailDao<BQCompositeIndexDetail> dao;

	public void save(BQCompositeIndexDetail info) {
		dao.save(info);
	}

	@Override
	public void update(BQCompositeIndexDetail info) {
		dao.update(info);
	}
	
	@Override
	public void delete(BQCompositeIndexDetail index) {
		dao.delete(index);
	}

	@Override
	public BQCompositeIndexDetail getById(long id) {
		return dao.get(BQCompositeIndexDetail.class, id);
	}
	
	@Override
	public BQCompositeIndexDetail getByTypeAndCode(int type, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("code", code);
		String hql = "FROM BQCompositeIndexDetail WHERE index_type = :type and transaction_code = :code";
		return dao.get(hql, params);
	}
	
	@Override
	public List<BQCompositeIndexDetail> findByType(int type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		return dao.find("FROM BQCompositeIndexDetail WHERE index_type = :type", params);
	}

	@Override
	public BigDecimal getSumRate(int type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		return dao.sum("SELECT sum(rate) FROM BQCompositeIndexDetail WHERE index_type = :type", params);
	}

}
