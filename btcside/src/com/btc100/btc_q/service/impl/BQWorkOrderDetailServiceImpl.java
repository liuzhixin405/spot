package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQWorkOrderDetailDao;
import com.btc100.btc_q.entity.BQWorkOrderDetail;
import com.btc100.btc_q.service.BQWorkOrderDetailService;

@Service("bqBQWorkOrderDetailService")
public class BQWorkOrderDetailServiceImpl implements BQWorkOrderDetailService {

	@Autowired
	private BQWorkOrderDetailDao<BQWorkOrderDetail> bqWorkOrderDetailDao;

	public long readTime() {
		return bqWorkOrderDetailDao.getUnix_TIMESTAMP();
	}

	@Override
	public List<BQWorkOrderDetail> readListByOrderId(long orderId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderId", orderId);
		String hql = "from BQWorkOrderDetail t where t.f_order_id =:orderId order by f_create_time";
		List<BQWorkOrderDetail> list = bqWorkOrderDetailDao.find(hql, params);
		return list;
	}

	@Override
	public void saveWorkOrderDetail(BQWorkOrderDetail orderDetail) {
		bqWorkOrderDetailDao.save(orderDetail);
	}

}
