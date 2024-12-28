package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.NMemberTransactionOverloadLogDao;
import com.btc100.btc_q.entity.NMemberTransactionOverloadLog;
import com.btc100.btc_q.service.NMemberTransactionOverloadLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;


@Service("nMemberTransactionOverloadLogService")
public class NMemberTransactionOverloadLogServiceImpl implements NMemberTransactionOverloadLogService { 

	@Autowired
	private NMemberTransactionOverloadLogDao<NMemberTransactionOverloadLog> nMemberTransactionOverloadLogDao;
	
	@Override
	public void savLog(String partition, NMemberTransactionOverloadLog log) {
		nMemberTransactionOverloadLogDao.insert(partition, log);
	}

	@Override
	public PagerModel readAll(String partition, long startTime, long endTime, Long uid, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select * from " + partition + "_member_transaction_overload_log where 1 = 1";
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
		
		return nMemberTransactionOverloadLogDao.searchSql(hql, params);
	}

	@Override
	public BigDecimal readSumBalance(String partition, long startTime, long endTime, Long uid, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select sum(balance) from " + partition + "_member_transaction_overload_log where 1 = 1";
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
		
		BigDecimal ret = nMemberTransactionOverloadLogDao.sumBySql(hql, params);
		if (ret == null) ret = BigDecimal.ZERO;
		return ret;
	}
}
