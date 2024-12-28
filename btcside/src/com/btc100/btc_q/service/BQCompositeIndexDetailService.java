package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQCompositeIndexDetail;

public interface BQCompositeIndexDetailService {

	void save(BQCompositeIndexDetail index);

	void update(BQCompositeIndexDetail index);

	void delete(BQCompositeIndexDetail index);

	BQCompositeIndexDetail getById(long id);

	BQCompositeIndexDetail getByTypeAndCode(int type, String code);

	List<BQCompositeIndexDetail> findByType(int type);

	BigDecimal getSumRate(int type);
}
