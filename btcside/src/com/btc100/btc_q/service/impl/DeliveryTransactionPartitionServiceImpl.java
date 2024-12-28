package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryTransactionPartitionDao;
import com.btc100.btc_q.entity.DeliveryTransactionPartition;
import com.btc100.btc_q.service.DeliveryTransactionPartitionService;
import com.btc100.btc_q.vo.PagerModel;

@Service("deliveryTransactionPartitionService")
public class DeliveryTransactionPartitionServiceImpl implements DeliveryTransactionPartitionService {

	@Autowired
	private DeliveryTransactionPartitionDao<DeliveryTransactionPartition> deliveryTransactionPartitionDao;

	@Override
	public PagerModel readPager() {
		String hql = "from DeliveryTransactionPartition order by sort";
		return deliveryTransactionPartitionDao.searchHql(hql);
	}

	@Override
	public void update(DeliveryTransactionPartition o) {
		if (o.getId() == null || o.getId() == 0) {
			o.setId(null);
			deliveryTransactionPartitionDao.save(o);
		} else {
			deliveryTransactionPartitionDao.update(o);
		}
	}

	@Override
	public void delete(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "delete from DeliveryTransactionPartition where id = :id";
		params.put("id", id);
		
		deliveryTransactionPartitionDao.deleteCondition(hql, params);
	}

	@Override
	public DeliveryTransactionPartition get(int id) {
		return deliveryTransactionPartitionDao.get(DeliveryTransactionPartition.class, id);
	}

	@Override
	public List<DeliveryTransactionPartition> readAll() {
		return deliveryTransactionPartitionDao.find("from DeliveryTransactionPartition order by sort");
	}
}
