package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQCompositeIndexTypeDao;
import com.btc100.btc_q.entity.BQCompositeIndexType;
import com.btc100.btc_q.service.BQCompositeIndexTypeService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqCompositeIndexTypeService")
public class BQCompositeIndexTypeServiceImpl implements BQCompositeIndexTypeService {

	@Autowired
	private BQCompositeIndexTypeDao<BQCompositeIndexType> dao;

	@Override
	public void update(BQCompositeIndexType type) {
		if (type == null || type.getType_name() == null || type.getType_name().isEmpty()) {
			return;
		}

		if (type.getType_id() != null && type.getType_id() > 0) {
			dao.update(type);
		} else {
			type.setType_id(null);
			dao.save(type);
		}
	}

	@Override
	public BQCompositeIndexType getById(int id) {
		return dao.get(BQCompositeIndexType.class, id);
	}

	@Override
	public BQCompositeIndexType getByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return dao.get(" from BQCompositeIndexType t where t.transaction_type_code = :code ", params);
	}

	@Override
	public List<BQCompositeIndexType> findAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return dao.find(" from BQCompositeIndexType t where t.is_using = :isUsing ", params);
	}

	@Override
	public PagerModel findAll() {
		return dao.searchHql("from BQCompositeIndexType");
	}

}
