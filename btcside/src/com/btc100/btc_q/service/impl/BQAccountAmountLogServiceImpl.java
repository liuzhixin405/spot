package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.service.BQAccountAmountLogService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqAccountAmountLogService")
public class BQAccountAmountLogServiceImpl implements BQAccountAmountLogService {

	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	public void saveAmountlog(BQAccountAmountLog amountLog) {
		bqAccountAmountLogDao.save(amountLog);
	}

	public void save(BQAccountAmountLog bqal) {
		bqAccountAmountLogDao.save(bqal);
	}

	public PagerModel readListRechargeWithdraw(long id) {
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();

		sbBuilder.append("from BQAccountAmountLog b where b.f_user_id=:id");
		map.put("id", id);
		
		String hql = " order by b.log_id desc ";
		hql = sbBuilder.toString() + hql;

		return bqAccountAmountLogDao.searchHql(hql, map);
	}

	public List<BQAccountAmountLog> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime,
			int limit) {
		String hql = "from BQAccountAmountLog  where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		if (startTime > 0) {
			hql += " and dateline >= :startTime";
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and dateline < :endTime";
			map.put("endTime", endTime);
		}
		if (from >= 0 && "prev".equals(direct)) {
			map.put("from", from);
			hql += " and log_id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			map.put("from", from);
			hql += " and log_id <= :from";
		}
		
		hql += " order by log_id desc";
		return bqAccountAmountLogDao.find(hql, map, 0, limit);
	}

	@Override
	public void createAccountAmountLogBak() {
		bqAccountAmountLogDao.createAccountAmountLogBak();
	}

	@Override
	public void insertAccountAmountLogBak(long endTime) {
		bqAccountAmountLogDao.insertAccountAmountLogBak(endTime);
		bqAccountAmountLogDao.deleteAccountAmountLog(endTime);
	}

	@Override
	public PagerModel readOTCRecords(long uid, int currencyId, int operation) {
		String hql = "from BQAccountAmountLog  where currency_type_id = :currencyId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("currencyId", currencyId);
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		if (operation == 1) {
			hql += " and operation = :operation";
			map.put("operation", Contents.TRANS_TYPE_TOOTC);
		} else if (operation == 2) {
			hql += " and operation = :operation";
			map.put("operation", Contents.TRANS_TYPE_OUTOTC);
		} else {
			map.put("operationzrotc", Contents.TRANS_TYPE_TOOTC);
			map.put("operationzcotc", Contents.TRANS_TYPE_OUTOTC);
			hql += " and (operation = :operationzrotc or operation = :operationzcotc)";
		}
		
		hql += " order by log_id desc";
		return bqAccountAmountLogDao.searchHql(hql, map);
	}
	
}
