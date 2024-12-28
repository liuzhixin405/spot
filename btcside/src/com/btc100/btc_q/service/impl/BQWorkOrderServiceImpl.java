package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQWorkOrderDao;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQWorkOrder;
import com.btc100.btc_q.service.BQWorkOrderService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqBQWorkOrderService")
public class BQWorkOrderServiceImpl implements BQWorkOrderService {

	@Autowired
	private BQWorkOrderDao<BQWorkOrder> bqWorkOrderDao;

	public List<BQWorkOrder> readByUserAndSize(long userId, long from, String direct, long startTime, long endTime,
			int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "from BQWorkOrder t where 1 = 1 ";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			params.put("userId", userId);
		}
		if (startTime > 0) {
			hql += " and dateline >= :startTime";
			params.put("startTime", startTime);
		}
		if (endTime > 0) {
			hql += " and dateline < :endTime";
			params.put("endTime", endTime);
		}
		if (from >= 0 && "prev".equals(direct)) {
			params.put("from", from);
			hql += " and id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			params.put("from", from);
			hql += " and id <= :from";
		}
		
		hql += " order by f_to_order_state desc,f_update_time desc,f_create_time desc ";
		List<BQWorkOrder> list = bqWorkOrderDao.find(hql, params, 1, limit);
		return list;
	}

	public PagerModel findOrdersByState(String state) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder("from BQWorkOrder");
		params.put("state", state);
		hql.append(" where f_to_order_state = :state order by f_update_time desc,f_create_time desc ");
		return bqWorkOrderDao.searchHql(hql.toString(), params);
	}
	
	public PagerModel findOrdersByType(String type) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder("from BQWorkOrder");
		if ("ACCOUNT".equals(type) || "TRADE".equals(type) || "COMPLAIN".equals(type) || "RW".equals(type) || "COOP".equals(type)) {
			params.put("type", type);
			hql.append(" where f_order_type = :type");
		}
		hql.append(" order by f_to_order_state desc,f_update_time desc,f_create_time desc");
		return bqWorkOrderDao.searchHql(hql.toString(), params);
	}

	public PagerModel readOrderByUserId(long userId, String direction) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		if (!StringUtil.isEmpty(direction) && "manager".equals(direction)) {
			return bqWorkOrderDao.searchHql("from BQWorkOrder t where t.f_user_id =:userId order by f_to_order_state desc,f_update_time desc,f_create_time desc ", params);
		}
		return bqWorkOrderDao.searchHql("from BQWorkOrder t where t.f_user_id =:userId order by f_from_order_state desc,f_update_time desc,f_create_time desc ", params);
	}

	public long readTime() {
		return bqWorkOrderDao.getUnix_TIMESTAMP();
	}

	@Override
	public void saveWorkOrder(BQWorkOrder workOrder, BQMember user) {
		workOrder.setF_order_code("#"+Integer.toString(ThreadLocalRandom.current().nextInt(900000) + 100000));; //工单号随机码
		workOrder.setF_user_id(user.getF_user_id());
		workOrder.setF_user_email(user.getF_user_email());
		workOrder.setF_from_order_state("SEND"); //发起方已发送
		workOrder.setF_to_order_state("UNREAD"); //接收方未读
		workOrder.setF_create_time(readTime());
		workOrder.setF_update_time(readTime());
		bqWorkOrderDao.save(workOrder);
	}

	@Override
	public void updateWorkOrder(BQWorkOrder workOrder) {
		bqWorkOrderDao.update(workOrder);
	}

	@Override
	public BQWorkOrder readById(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		String hql = "from BQWorkOrder t where t.id =:id ";
		BQWorkOrder order = bqWorkOrderDao.get(hql, params);
		return order;
	}

	@Override
	public List<BQWorkOrder> readListByState(String state, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("userId", userId);
		String hql = "from BQWorkOrder where f_from_order_state = :state and f_user_id = :userId";
		return bqWorkOrderDao.find(hql, params);
	}

}
