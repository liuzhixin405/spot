package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.BQMemberInviteRebateLogDao;
import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteRebateLog;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberInviteRebateLogService")
public class BQMemberInviteRebateLogServiceImpl implements BQMemberInviteRebateLogService {

	@Autowired
	private BQMemberInviteRebateLogDao<BQMemberInviteRebateLog> bqMemberInviteRebateLogDao;
	@Autowired
	private BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	@Override
	public PagerModel readAll(String code, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "From BQMemberInviteRebateLog where 1 = 1";
		if (!StringUtil.isEmpty(code)) {
			params.put("code", code);
			hql += " and bq_code = :code";
		}
		
		if (userId > 0) {
			params.put("userId", userId);
			hql += " and user_id = :userId";
		}
		
		hql += " order by id desc";
		
		return bqMemberInviteRebateLogDao.searchHql(hql, params);
	}

	public long getTime() {
		return bqMemberInviteRebateLogDao.getUnix_TIMESTAMP();
	}

	@Override
	public BigDecimal getSumRebateByTime(long startTime, long endTime, long userId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("currencyId", currencyId);
		String userSql = "";
		if (userId > 0) {
			params.put("userId", userId);
			userSql = " and user_id = :userId";
		}
		
		String hql = "select sum(rebate_amount) From BQMemberInviteRebateLog where currency_type_id = :currencyId and record_time >= :startTime and record_time < :endTime" + userSql;
		return bqMemberInviteRebateLogDao.sum(hql, params);
	}
	
	@Override
	public BigDecimal readSumRebateByTime(long startTime, long endTime, long userId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("currencyId", currencyId);
		String userSql = "";
		if (userId > 0) {
			params.put("userId", userId);
			userSql = " and user_id = :userId";
		}
		
		String hql = "select sum(rebate_amount) From BQMemberInviteRebateLog where currency_type_id = :currencyId and record_time >= :startTime and record_time < :endTime" + userSql;
		return bqMemberInviteRebateLogDao.sum(hql, params);
	}

	@Override
	public BigDecimal readSumRebateByUser(long userId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("currencyId", currencyId);
		return bqMemberInviteRebateLogDao.sum("select sum(rebate_amount) From BQMemberInviteRebateLog where user_id = :userId and currency_type_id = :currencyId", params);
	}

	//所有返佣数值
	public List<Object> readAllSumRebateByTime(long userId) {
		StringBuffer sql = new StringBuffer("SELECT currency_type_id, sum(rebate_amount) FROM bq_member_invite_rebate_log where user_id = :userId GROUP BY currency_type_id");
		Session session = bqMemberInviteRebateLogDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("userId", userId);
		return query.list();
	}
	//周或月返佣数值
	public List<Object> readAllSumRebateByTimeWeekOrMonth(long userId,long startTime, long endTime) {
		StringBuffer sql = new StringBuffer("SELECT currency_type_id, sum(rebate_amount) FROM bq_member_invite_rebate_log where user_id = :userId and record_time >= :startTime and record_time < :endTime GROUP BY currency_type_id");
		Session session = bqMemberInviteRebateLogDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("userId", userId);
		query.setLong("startTime", startTime);
		query.setLong("endTime", endTime);
		return query.list();
	}

	@Override
	public BQMemberInviteRebateLog createAndSaveRecord(String partition, long userId, long childUserId, BigDecimal tradeFee, long tradeUserId, 
			long tradeId, String bqCode, long maxLevelId, int currencyId, BQMemberLevel childLevel, BQMemberLevel userLevel) throws ServiceException {
		if (tradeFee.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}
		
		BigDecimal childRebateRate = BigDecimal.ZERO;
		if (childLevel != null) {
			if (tradeUserId != childUserId) {
				if (childLevel.getLevel_id() >= maxLevelId) {
					return null;
				}
				
				childRebateRate = childLevel.getBroker_commission_rate();
			}
		}
		
		BQMemberInviteRebateLog record = new BQMemberInviteRebateLog();
		record.setUser_id(0);
		if (userLevel == null) {
			return record;
		}
		
		BigDecimal rebateRate = userLevel.getBroker_commission_rate();
		if (rebateRate.compareTo(childRebateRate) <= 0) {
			return record;
		}
		
		BigDecimal rebateRateDiff = rebateRate.subtract(childRebateRate);
		BigDecimal rebate = rebateRateDiff.multiply(tradeFee);
		
		record.setUser_id(userId);
		record.setPart(partition);
		record.setUser_email(bqMemberDao.get(BQMember.class, userId).getF_user_email());
		record.setTrade_id(tradeId);
		record.setUser_level_name(userLevel.getLevel_name());
		record.setSub_user_id(childUserId);
		record.setSub_user_email(bqMemberDao.get(BQMember.class, childUserId).getF_user_email());
		record.setSub_user_level_name(childLevel.getLevel_name());
		record.setCommission_rate(userLevel.getBroker_commission_rate());
		record.setSub_commission_rate(childLevel.getBroker_commission_rate());
		record.setCommission_rate_diff(rebateRateDiff);
		record.setRebate_amount(rebate);
		record.setRecord_time(bqMemberLevelDao.getUnix_TIMESTAMP());
		record.setBq_code(bqCode);
		record.setCurrency_type_id(currencyId);
		
		bqMemberInviteRebateLogDao.save(record);
		
		if (bqAccountDao.updateAmount(record.getUser_id(), currencyId, record.getRebate_amount(), BigDecimal.ZERO) > 0) {
			BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(record.getUser_id(), currencyId);
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog("获得返利，数量：" + record.getRebate_amount().doubleValue(), currencyId, 1, 1,
					bqAccount.getBtc().subtract(record.getRebate_amount()), record.getRebate_amount(), bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
					bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getUser_id(), RandomUtil.getTransNo(), "返利" + record.getRebate_amount().doubleValue(), record.getId());
			bqAccountAmountLogDao.save(pAccountLog);
		} else {
			throw new ServiceException("返利，操作资金失败！");
		}
		
		return record;
	}

	@Override
	public BigDecimal getSumRebateByTimeAndUser(String partition, String code, long startTime, long endTime, long userId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("currencyId", currencyId);
		params.put("part", partition);
		String userHql = "";
		if (userId > 0) {
			params.put("userId", userId);
			userHql = " and user_id = :userId";
		}
		
		String hql = "select sum(rebate_amount) From BQMemberInviteRebateLog where part = :part and bq_code = :code and currency_type_id = :currencyId"
				+ " and record_time >= :startTime and record_time < :endTime" + userHql;
		return bqMemberInviteRebateLogDao.sum(hql, params);
	}
	
	@Override
	public BigDecimal readSumRebateByTimeAndUser(String partition, String code, long startTime, long endTime, long userId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("currencyId", currencyId);
		params.put("part", partition);
		String userHql = "";
		if (userId > 0) {
			params.put("userId", userId);
			userHql = " and user_id = :userId";
		}
		
		String hql = "select sum(rebate_amount) From BQMemberInviteRebateLog where part = :part and bq_code = :code and currency_type_id = :currencyId"
				+ " and record_time >= :startTime and record_time < :endTime" + userHql;
		return bqMemberInviteRebateLogDao.sum(hql, params);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findByTime(long startTime, long endTime, int currencyId) {
		StringBuffer sql = new StringBuffer(
				"SELECT user_id, max(user_email) AS user_email, SUM(rebate_amount) AS rebate FROM bq_member_invite_rebate_log");
		sql.append(" where currency_type_id = " + currencyId);

		if (startTime > 0)
			sql.append(" AND record_time >= " + startTime);

		if (endTime > 0)
			sql.append(" AND record_time < " + endTime);

		sql.append(" GROUP BY user_id ");

		Session session = bqMemberInviteRebateLogDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		List<Object[]> list = query.list();
		return list;
	}

	@Override
	public BigDecimal readSumRebateByUserAndSub(long userId, long subUserId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("subUserId", subUserId);
		params.put("currencyId", currencyId);
		String hql = "select sum(rebate_amount) From BQMemberInviteRebateLog where user_id = :userId and sub_user_id = :subUserId and currency_type_id = :currencyId";
		return bqMemberInviteRebateLogDao.sum(hql, params);
	}

	@Override
	public BigDecimal readSumRebateByTimeAndSub(long startTime, long endTime, long userId, long subUserId,
			int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("currencyId", currencyId);
		params.put("userId", userId);
		params.put("subUserId", subUserId);
		
		String hql = "select sum(rebate_amount) From BQMemberInviteRebateLog where currency_type_id = :currencyId"
				+ " and record_time >= :startTime and record_time < :endTime and user_id = :userId and sub_user_id = :subUserId";
		return bqMemberInviteRebateLogDao.sum(hql, params);
	}
}
