package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NTransactionPartitionDao;
import com.btc100.btc_q.entity.NTransactionPartition;
import com.btc100.btc_q.service.NTransactionPartitionService;
import com.btc100.btc_q.vo.PagerModel;

@Service("nTransactionPartitionService")
public class NTransactionPartitionServiceImpl implements NTransactionPartitionService {

	@Autowired
	private NTransactionPartitionDao<NTransactionPartition> bqTransactionPartitionDao;

	@Override
	public PagerModel readPager(String partition) {
		String hql = "select * from " + partition + "_transaction_partition order by sort";
		return bqTransactionPartitionDao.searchSql(hql);
	}

	@Override
	public void update(String partition, NTransactionPartition o) {
		if (o.getId() == null || o.getId() == 0) {
			o.setId(null);
			bqTransactionPartitionDao.insert(partition, o);
		} else {
			bqTransactionPartitionDao.update(partition, o);
		}
	}

	@Override
	public void delete(String partition, int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "delete from " + partition + "_transaction_partition where id = :id";
		params.put("id", id);
		
		bqTransactionPartitionDao.deleteConditionBySql(hql, params);
	}

	@Override
	public NTransactionPartition get(String partition, int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "select * from " + partition + "_transaction_partition where id = :id";
		params.put("id", id);
		
		return bqTransactionPartitionDao.getBySql(sql, params);
	}

	@Override
	public List<NTransactionPartition> readAll(String partition) {
		return bqTransactionPartitionDao.findBySql("select * from " + partition + "_transaction_partition order by sort");
	}
}
