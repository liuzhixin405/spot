package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQTransactionPartitionDao;
import com.btc100.btc_q.entity.BQTransactionPartition;
import com.btc100.btc_q.service.BQTransactionPartitionService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqTransactionPartitionService")
public class BQTransactionPartitionServiceImpl implements BQTransactionPartitionService {

	@Autowired
	private BQTransactionPartitionDao<BQTransactionPartition> bqTransactionPartitionDao;

	@Override
	public PagerModel readPager() {
		String hql = "from BQTransactionPartition order by sort";
		return bqTransactionPartitionDao.searchHql(hql);
	}

	@Override
	public void update(BQTransactionPartition o) {
		if (o.getId() == null || o.getId() == 0) {
			o.setId(null);
			bqTransactionPartitionDao.save(o);
		} else {
			bqTransactionPartitionDao.update(o);
		}
	}

	@Override
	public void delete(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "delete from BQTransactionPartition where id = :id";
		params.put("id", id);
		
		bqTransactionPartitionDao.deleteCondition(hql, params);
	}

	@Override
	public BQTransactionPartition get(int id) {
		return bqTransactionPartitionDao.get(BQTransactionPartition.class, id);
	}

	@Override
	public List<BQTransactionPartition> readAll() {
		return bqTransactionPartitionDao.find("from BQTransactionPartition order by sort");
	}
}
