package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.DeliveryMemberTransactionOverloadLogDao;
import com.btc100.btc_q.entity.DeliveryMemberTransactionOverloadLog;
import com.btc100.btc_q.service.DeliveryMemberTransactionOverloadLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;


@Service("deliveryMemberTransactionOverloadLogService")
public class DeliveryMemberTransactionOverloadLogServiceImpl implements DeliveryMemberTransactionOverloadLogService { 

	@Autowired
	private DeliveryMemberTransactionOverloadLogDao<DeliveryMemberTransactionOverloadLog> deliveryMemberTransactionOverloadLogDao;
	
	@Override
	public void savLog(DeliveryMemberTransactionOverloadLog log) {
		deliveryMemberTransactionOverloadLogDao.save(log);
	}

	@Override
	public PagerModel readAll(long startTime, long endTime, Long uid, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "from DeliveryMemberTransactionOverloadLog where 1 = 1";
		if (!StringUtil.isEmpty(code)) {
			hql += " and transaction_code = :code";
			params.put("code", code);
		}
		
		if (startTime > 0) {
			hql += " and dateline >= :startTime";
			params.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and dateline < :endTime";
			params.put("endTime", endTime);
		}
		
		if (uid != null && uid > 0) {
			hql += " and f_user_id = :uid";
			params.put("uid", uid);
		}
		
		hql += " order by id desc";
		
		return deliveryMemberTransactionOverloadLogDao.searchHql(hql, params);
	}

	@Override
	public BigDecimal readSumBalance(long startTime, long endTime, Long uid, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select sum(balance) from DeliveryMemberTransactionOverloadLog where 1 = 1";
		if (!StringUtil.isEmpty(code)) {
			hql += " and transaction_code = :code";
			params.put("code", code);
		}
		
		if (startTime > 0) {
			hql += " and dateline >= :startTime";
			params.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and dateline < :endTime";
			params.put("endTime", endTime);
		}
		
		if (uid != null && uid > 0) {
			hql += " and f_user_id = :uid";
			params.put("uid", uid);
		}
		
		BigDecimal ret = deliveryMemberTransactionOverloadLogDao.sum(hql, params);
		if (ret == null) ret = BigDecimal.ZERO;
		return ret;
	}

}
