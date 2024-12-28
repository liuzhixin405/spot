package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberLevelBuyRebateReconciliaDao;
import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberLevelBuyRebateReconcilia;
import com.btc100.btc_q.service.BQMemberLevelBuyRebateReconciliaService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberLevelBuyRebateReconciliaService")
public class BQMemberLevelBuyRebateReconciliaServiceImpl implements BQMemberLevelBuyRebateReconciliaService {
	
	@Autowired
	private BQMemberLevelBuyRebateReconciliaDao<BQMemberLevelBuyRebateReconcilia> bqMemberLevelBuyRebateReconciliaDao;
	@Autowired
	private BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;

	@Override
	public BQMemberLevelBuyRebateReconcilia readByUserId(long uid) {
		return bqMemberLevelBuyRebateReconciliaDao.getByUerId(uid);
	}
	
	@Override
	public BQMemberLevelBuyRebateReconcilia getByUserId(long uid) {
		return bqMemberLevelBuyRebateReconciliaDao.getByUerId(uid);
	}

	@Override
	public PagerModel readSubUsers(long uid, int userType) {
		Map<String, Object> params = new HashMap<>();
		params.put("uid", uid);
		
		String hql = "from BQMemberLevelBuyRebateReconcilia where 1 = 1";
		if (userType == 1) {
			hql += " and superior = :uid";
		} else if (userType == 2) {
			hql += " and superior2 = :uid";
		} else {
			hql += " and (superior = :uid or superior2 = :uid)";
		}
		
		PagerModel pm = bqMemberLevelBuyRebateReconciliaDao.searchHql(hql, params);
		@SuppressWarnings("unchecked")
		List<BQMemberLevelBuyRebateReconcilia> dataList = (List<BQMemberLevelBuyRebateReconcilia>) pm.getDatas();
		for (BQMemberLevelBuyRebateReconcilia data : dataList) {
			long userId = data.getF_user_id();
			BQMemberLevel level = bqMemberLevelDao.findByUserId(userId);
			data.setVip_name(level.getLevel_name());
		}
		
		return pm;
	}

	@Override
	public BigDecimal readSumBfxLock(long parent, int userType) {
		String sql = "select IFNULL(sum(b.self_bfx_lock),0) from bq_member_level_buy_rebate_reconcilia b where 1 = 1";
		if (userType == 1) {
			sql += " and superior = :uid";
		} else if (userType == 2) {
			sql += " and superior2 = :uid";
		} else {
			return BigDecimal.ZERO;
		}
		
		Session session = bqMemberLevelBuyRebateReconciliaDao.getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", parent);
		
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal readSumSelfBfxLockByUser(long uid) {
		String sql = "select IFNULL(sum(b.self_bfx_lock),0) from bq_member_level_buy_rebate_reconcilia b where 1 = 1";
		if (uid > 0) {
			sql += " and f_user_id = :uid";
		} 
		
		Session session = bqMemberLevelBuyRebateReconciliaDao.getSession();
		SQLQuery query = session.createSQLQuery(sql);
		if (uid > 0) {
			query.setLong("uid", uid);
		}
		
		return (BigDecimal) query.uniqueResult();
	}
	
}
