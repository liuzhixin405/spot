package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.BQMemberTransactionOverloadLogDao;
import com.btc100.btc_q.entity.BQMemberTransactionOverloadLog;
import com.btc100.btc_q.service.BQMemberTransactionOverloadLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;


@Service("bqMemberTransactionOverloadLogService")

public class BQMemberTransactionOverloadLogServiceImpl implements BQMemberTransactionOverloadLogService { 

	@Autowired
	private BQMemberTransactionOverloadLogDao<BQMemberTransactionOverloadLog> bqMemberTransactionOverloadLogDao;
	
	@Override
	public void savLog(BQMemberTransactionOverloadLog log) {
		bqMemberTransactionOverloadLogDao.save(log);
	}

	@Override
	public PagerModel readAll(long startTime, long endTime, Long uid, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "from BQMemberTransactionOverloadLog where 1 = 1";
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
		
		return bqMemberTransactionOverloadLogDao.searchHql(hql, params);
	}

	@Override
	public BigDecimal readSumBalance(long startTime, long endTime, Long uid, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select sum(balance) from BQMemberTransactionOverloadLog where 1 = 1";
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
		
		BigDecimal ret = bqMemberTransactionOverloadLogDao.sum(hql, params);
		if (ret == null) ret = BigDecimal.ZERO;
		return ret;
	}

}
