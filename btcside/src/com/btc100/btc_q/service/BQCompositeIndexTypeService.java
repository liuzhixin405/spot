package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQCompositeIndexType;
import com.btc100.btc_q.vo.PagerModel;

public interface BQCompositeIndexTypeService {

	void update(BQCompositeIndexType type);

	BQCompositeIndexType getById(int id);

	BQCompositeIndexType getByCode(String code);

	List<BQCompositeIndexType> findAllUsing();

	PagerModel findAll();

}
