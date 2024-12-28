package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQTransactionIntroduceDao;
import com.btc100.btc_q.entity.BQTransactionIntroduce;
import com.btc100.btc_q.service.BQTransactionIntroduceService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqTransactionIntroduceService")
public class BQTransactionIntroduceServiceImpl implements BQTransactionIntroduceService {

	@Autowired
	private BQTransactionIntroduceDao<BQTransactionIntroduce> bqTransactionIntroduceDao;

	@Override
	public void add(BQTransactionIntroduce intrduce) {
		bqTransactionIntroduceDao.save(intrduce);
	}

	@Override
	public void delete(BQTransactionIntroduce intrduce) {
		bqTransactionIntroduceDao.delete(intrduce);
	}

	@Override
	public void update(BQTransactionIntroduce intrduce) {
		bqTransactionIntroduceDao.update(intrduce);
	}
	
	@Override
	public BQTransactionIntroduce getById(long id) {
		return bqTransactionIntroduceDao.get(BQTransactionIntroduce.class, id);
	}

	@Override
	public PagerModel findAllpage(int type) {
		String hql = "FROM BQTransactionIntroduce WHERE type = :type and part = ''";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		return bqTransactionIntroduceDao.searchHql(hql, params);
	}
	
	@Override
	public PagerModel readAllpage(String partition, int type) {
		String hql = "FROM BQTransactionIntroduce WHERE type = :type and part = :part";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("part", partition);
		return bqTransactionIntroduceDao.searchHql(hql, params);
	}

	@Override
	public BQTransactionIntroduce readByCode(String code, int type) {
		String hql = "FROM BQTransactionIntroduce WHERE transaction_code = :code AND type = :type and part = :partition";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("type", type);
		params.put("partition", "");
		return bqTransactionIntroduceDao.get(hql, params);
	}

	@Override
	public BQTransactionIntroduce readByCode(String partition, String code) {
		String hql = "FROM BQTransactionIntroduce WHERE transaction_code = :code and part = :partition";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("partition", partition);
		return bqTransactionIntroduceDao.get(hql, params);
	}

}
