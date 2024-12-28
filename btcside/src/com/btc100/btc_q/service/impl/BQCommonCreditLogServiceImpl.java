package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQCommonCreditLogDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.service.BQCommonCreditLogService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqCommonCreditLogService")
public class BQCommonCreditLogServiceImpl implements BQCommonCreditLogService {
	@Autowired
	private BQCommonCreditLogDao<BQCommonCreditLog> bqCommonCreditLogDao;
	@Autowired
	BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;

	public List<BQCommonCreditLog> findById(long uid) {
		String hql = "FROM BQCommonCreditLog b WHERE f_user_id=:uid ORDER BY credit_log_id desc";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		List<BQCommonCreditLog> plist = bqCommonCreditLogDao.find(hql, map, 1, 10);
		return plist;
	}
	public List<BQCommonCreditLog> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime, int limit) {
		String hql = "FROM BQCommonCreditLog WHERE 1 = 1";
		
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
			hql += " and credit_log_id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			map.put("from", from);
			hql += " and credit_log_id < :from";
		}
		
		hql += " ORDER BY credit_log_id desc";
		List<BQCommonCreditLog> plist = bqCommonCreditLogDao.find(hql, map, 1, limit);
		return plist;
	}

	public void save(BQCommonCreditLog bqCc) {
		bqCommonCreditLogDao.save(bqCc);
	}

	public BQCommonCreditLog findByIdTime(long fUserId) {
		String hql = "FROM BQCommonCreditLog b WHERE f_user_id=:uid ORDER BY credit_log_id desc";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", fUserId);
		List<BQCommonCreditLog> plist = bqCommonCreditLogDao.find(hql, map, 1, 1);
		if (plist != null && plist.size() > 0) {
			return plist.get(0);
		}

		return null;
	}

	public PagerModel readVipRecords(long fUserId) {
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();

		sbBuilder.append("from BQCommonCreditLog b where b.f_user_id=:id");
		map.put("id", fUserId);
		
		String hql = " order by b.credit_log_id desc ";
		hql = sbBuilder.toString() + hql;

		return bqCommonCreditLogDao.searchHql(hql, map);
	}

	@Override
	public void saveCreditsAndUpdateLevel(BQCommonCreditLog bqCc, boolean withHiddenLevel, int operation) {
		bqCommonCreditLogDao.save(bqCc);
		
		// 更新用户的积分
		BQMember bqMember = bqMemberDao.get(BQMember.class, bqCc.getF_user_id());
		long ucredits = bqMember.getF_user_credits();
		long change = bqCc.getCredits();
		if (operation < 0) {
			change = -change;
		} 
		long newCredits = ucredits + change;
		long vipId = bqMemberLevelDao.getLevelByCredits(newCredits, withHiddenLevel);
		bqMemberDao.addCredits(change, vipId, bqCc.getF_user_id());
		
	}

	@Override
	public long getTime() {
		return bqCommonCreditLogDao.getUnix_TIMESTAMP();
	}
	
	@Override
	public PagerModel readAddOrSubRecords(long fUserId) {
		String swhere = "";
		if (fUserId > 0) swhere = " AND f_user_id = " + fUserId;
		String hql = "FROM BQCommonCreditLog b WHERE (credit_type_id = :creditType or credit_type_id = :creditType2)" + swhere;
		String orderHql = " ORDER BY dateline DESC";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("creditType", Contents.CREDIT_TYPE_BUY);
		map.put("creditType2", Contents.CREDIT_TYPE_SUBTRACT);
		return bqCommonCreditLogDao.searchHql(hql + orderHql, map);
	}
	
	@Override
	public int count(long uid, int type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("type", type);
		
		return bqCommonCreditLogDao.count("select count(*) from BQCommonCreditLog b WHERE b.f_user_id = :uid and b.credit_type_id = :type", map);
	}
	
	@Override
	public int readCount(long uid, int type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("type", type);
		
		return bqCommonCreditLogDao.count("select count(*) from BQCommonCreditLog b WHERE b.f_user_id = :uid and b.credit_type_id = :type", map);
	}
	
}
