package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQCommonCreditLogDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.BQMemberInviteLogDao;
import com.btc100.btc_q.dao.BQMemberLevelBuyAmountLogDao;
import com.btc100.btc_q.dao.BQMemberLevelBuyRebateReconciliaDao;
import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.dao.BQMemberRebateRecordDao;
import com.btc100.btc_q.dao.BQMemberRechargeDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteLog;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberLevelBuyAmountLog;
import com.btc100.btc_q.entity.BQMemberLevelBuyRebateReconcilia;
import com.btc100.btc_q.entity.BQMemberLevelBuySetting;
import com.btc100.btc_q.entity.BQMemberRebateRecord;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberLevelBuyAmountLogService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("bqMemberLevelBuyAmountLogService")
public class BQMemberLevelBuyAmountLogServiceImpl implements BQMemberLevelBuyAmountLogService{
	
	@Autowired
	private BQMemberLevelBuyAmountLogDao<BQMemberLevelBuyAmountLog> bqMemberLevelBuyAmountLogDao;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private BQMemberInviteLogDao<BQMemberInviteLog> bqMemberInviteLogDao;
	@Autowired
	private BQMemberRebateRecordDao<BQMemberRebateRecord> bqMemberRebateRecordDao;
	@Autowired
	private BQMemberRechargeDao<BQMemberRecharge> bqMemberRechargeDao;
	@Autowired
	private BQMemberLevelBuyRebateReconciliaDao<BQMemberLevelBuyRebateReconcilia> bqMemberLevelBuyRebateReconciliaDao;
	@Autowired
	private BQCommonCreditLogDao<BQCommonCreditLog> bqCommonCreditLogDao;
	@Autowired
	private BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;
	
	private void reconciliaBfxLock(long userId, BigDecimal buyAmount, BigDecimal lockAmount, BQMemberLevelBuyRebateReconcilia subUser, BQMemberLevelBuyRebateReconcilia subUser2, int deep) throws ServiceException {
		if (deep > 100) return;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		BQMemberInviteLog inviteLog = bqMemberInviteLogDao.get("From BQMemberInviteLog where f_user_id = :userId", params);
		if (inviteLog != null) {
			// 上级会员
			long parent = inviteLog.getInvite_uid();
			
			BQMember member = bqMemberDao.get(BQMember.class, parent);
			BQMemberLevelBuyRebateReconcilia reconcilia = new BQMemberLevelBuyRebateReconcilia();
			reconcilia.setF_user_email(member.getF_user_email());
			reconcilia.setF_user_id(parent);
			reconcilia.setSelf_bfx_lock(BigDecimal.ZERO);
//			reconcilia.setSuperior(superior);
//			reconcilia.setSuperior2(superior2);
			reconcilia.setTotal_bfx_lock(lockAmount);
			reconcilia.setTotal_bfx_unlock(BigDecimal.ZERO);
			reconcilia.setTotal_buy_amount(buyAmount);
			
			if (subUser != null) {
				subUser.setSuperior(parent);
			}
			
			if (subUser2 != null) {
				subUser2.setSuperior2(parent);
			}
			
			reconciliaBfxLock(parent, buyAmount, lockAmount, reconcilia, subUser, deep++);
			if (bqMemberLevelBuyRebateReconciliaDao.updateRecord(reconcilia) <= 0) {
				throw new ServiceException("购买vip统计bfx锁仓操作失败，用户：" + reconcilia.getF_user_id());
			}
		}
	}
	
	public void updateAccountAndLog(BQMemberLevelBuyAmountLog bqmlba, String title, BQMemberLevelBuySetting setting) throws ServiceException {
		
		int operation = 5;
		int direction = -1;
		
		long userId = bqmlba.getF_user_id();
		long time = bqAccountDao.getUnix_TIMESTAMP();
		BQAccount bqaccount = bqAccountDao.getByUerAndCurrencyId(userId, bqmlba.getCurrency_type_id());
		
		if (bqAccountDao.updateAmount(userId, bqmlba.getCurrency_type_id(), bqmlba.getChange_amount().negate(), BigDecimal.ZERO) <= 0) {
			throw new ServiceException("购买vip扣款失败，用户：" + userId + "，金额：" + bqmlba.getChange_amount().doubleValue());
		}
		
		// 增加积分记录
		long oldLevelId = bqmlba.getBefore_level_name_id();
		long newLevelId = bqmlba.getAfter_level_name_id();
		BQMemberLevel oldLevel = bqMemberLevelDao.get(BQMemberLevel.class, oldLevelId);
		BQMemberLevel newLevel = bqMemberLevelDao.get(BQMemberLevel.class, newLevelId);
		
		long changeCredit = 0;
		if (oldLevel != null && newLevel != null) {
			changeCredit = newLevel.getLevel_credits() - oldLevel.getLevel_credits();
		}
		
		BQCommonCreditLog bqcl = new BQCommonCreditLog();
		bqcl.setF_user_id(userId);
		bqcl.setCredit_type_id(Contents.CREDIT_TYPE_BUY);
		bqcl.setCredits(changeCredit);
		bqcl.setTitle("购买VIP等级");
		bqcl.setDateline(time);
		bqCommonCreditLogDao.save(bqcl);

		String hql = "UPDATE BQMember b SET b.f_user_level_id = :type, b.f_user_credits = b.f_user_credits + :credits WHERE f_user_id=:uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", bqmlba.getAfter_level_name_id());
		map.put("credits", changeCredit);
		map.put("uid", userId);
		bqMemberDao.updateCondition(hql, map);
		
		bqmlba.setDateline(time);
		bqMemberLevelBuyAmountLogDao.save(bqmlba);
		
		// 增加资金变动日志
		BQAccountAmountLog amountLog = new BQAccountAmountLog(title, bqmlba.getCurrency_type_id(), operation, direction, bqaccount.getBtc(), bqmlba.getChange_amount(), bqaccount.getBtc().subtract(bqmlba.getChange_amount()),
				bqaccount.getBtc_locked(), bqaccount.getBtc_locked(), time, userId, RandomUtil.getTransNo(), "购买VIP等级", bqmlba.getLog_id());
		bqAccountAmountLogDao.save(amountLog);
			
		// 返佣
		long superior = 0;
		long superior2 = 0;
		if (setting != null && setting.getFirst_rebate().compareTo(BigDecimal.ZERO) > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			BQMemberInviteLog inviteLog = bqMemberInviteLogDao.get("From BQMemberInviteLog where f_user_id = :userId", params);
			if (inviteLog != null) {
				// 上级会员
				long parent = inviteLog.getInvite_uid();
				superior = parent;
				{
					BigDecimal rebateAmount = bqmlba.getChange_amount().multiply(setting.getFirst_rebate()).setScale(8, BigDecimal.ROUND_DOWN);
					if (parent > 0 && rebateAmount.compareTo(BigDecimal.ZERO) > 0) {
						BQMemberRebateRecord rebateRecord = new BQMemberRebateRecord();
						rebateRecord.setCoin_name(setting.getPay_coin_name());
						rebateRecord.setCommission_rate(setting.getFirst_rebate());
						rebateRecord.setCurrency_type_id(bqmlba.getCurrency_type_id());
						rebateRecord.setRebate_amount(rebateAmount);
						rebateRecord.setRebate_type(1);
						rebateRecord.setSub_user_id(bqmlba.getF_user_id());
						rebateRecord.setUser_id(parent);
						rebateRecord.setUser_level((int)bqmlba.getAfter_level_name_id());
						rebateRecord.setRecord_time(time);
						bqMemberRebateRecordDao.save(rebateRecord);
						
						BQMemberRecharge recharge = new BQMemberRecharge();
						recharge.setCurrency_type_id(bqmlba.getCurrency_type_id());
						recharge.setOperation(1);
						recharge.setFee(BigDecimal.ZERO);
						recharge.setAmount(rebateAmount.subtract(recharge.getFee()));
						recharge.setReceice_card_type(1);	// 1.普通充值
						recharge.setReceice_card_no("会员购买Vip返利_" + RandomUtil.getTransNo());
						recharge.setIp("");
						recharge.setDateline(time);
						recharge.setConfirm_time(time);
						recharge.setState(1);
						recharge.setRemark("会员购买Vip返利");
						recharge.setF_user_id(parent);
						recharge.setF_user_email("");
						recharge.setWallet_trade_id(recharge.getReceice_card_no());
						bqMemberRechargeDao.save(recharge);
						
						BQAccount account = bqAccountDao.getByUerAndCurrencyId(parent, bqmlba.getCurrency_type_id());
						if (bqAccountDao.updateAmount(parent, bqmlba.getCurrency_type_id(), rebateAmount, BigDecimal.ZERO) > 0) {
							// 增加资金变动日志
							BQAccountAmountLog rebateLog = new BQAccountAmountLog("子会员" + userId + "购买vip，获得返利" + rebateAmount.doubleValue(), bqmlba.getCurrency_type_id(), 0, 1, account.getBtc(), rebateAmount, account.getBtc().add(rebateAmount),
									account.getBtc_locked(), account.getBtc_locked(), time, parent, RandomUtil.getTransNo(), "子会员购买vip，获得返利", rebateRecord.getId());
							bqAccountAmountLogDao.save(rebateLog);
						} else {
							throw new ServiceException("购买vip返利操作失败，用户：" + parent + "，金额：" + rebateAmount.doubleValue());
						}
					}
				}
				
				
				// 上上级会员
				if (setting.getSecond_rebate().compareTo(BigDecimal.ZERO) > 0) {
					Map<String, Object> pams = new HashMap<String, Object>();
					pams.put("userId", parent);
					BQMemberInviteLog log = bqMemberInviteLogDao.get("From BQMemberInviteLog where f_user_id = :userId", pams);
					if (log != null) {
						long uid = log.getInvite_uid();
						superior2 = uid;
						BigDecimal rebateAmount = bqmlba.getChange_amount().multiply(setting.getSecond_rebate()).setScale(8, BigDecimal.ROUND_DOWN);
						if (uid > 0 && rebateAmount.compareTo(BigDecimal.ZERO) > 0) {
							BQMemberRebateRecord rebateRecord = new BQMemberRebateRecord();
							rebateRecord.setCoin_name(setting.getPay_coin_name());
							rebateRecord.setCommission_rate(setting.getFirst_rebate());
							rebateRecord.setCurrency_type_id(bqmlba.getCurrency_type_id());
							rebateRecord.setRebate_amount(rebateAmount);
							rebateRecord.setRebate_type(1);
							rebateRecord.setSub_user_id(bqmlba.getF_user_id());
							rebateRecord.setUser_id(uid);
							rebateRecord.setUser_level((int)bqmlba.getAfter_level_name_id());
							rebateRecord.setRecord_time(time);
							bqMemberRebateRecordDao.save(rebateRecord);
							
							BQMemberRecharge recharge = new BQMemberRecharge();
							recharge.setCurrency_type_id(bqmlba.getCurrency_type_id());
							recharge.setOperation(1);
							recharge.setFee(BigDecimal.ZERO);
							recharge.setAmount(rebateAmount.subtract(recharge.getFee()));
							recharge.setReceice_card_type(1);	// 1.普通充值
							recharge.setReceice_card_no("会员购买Vip返利_" + RandomUtil.getTransNo());
							recharge.setIp("");
							recharge.setDateline(time);
							recharge.setConfirm_time(time);
							recharge.setState(1);
							recharge.setRemark("会员购买Vip返利");
							recharge.setF_user_id(uid);
							recharge.setF_user_email("");
							recharge.setWallet_trade_id(recharge.getReceice_card_no());
							bqMemberRechargeDao.save(recharge);
							
							BQAccount account = bqAccountDao.getByUerAndCurrencyId(uid, bqmlba.getCurrency_type_id());
							if (bqAccountDao.updateAmount(uid, bqmlba.getCurrency_type_id(), rebateAmount, BigDecimal.ZERO) > 0) {
								// 增加资金变动日志
								BQAccountAmountLog rebateLog = new BQAccountAmountLog("子会员" + userId + "购买vip，获得返利" + rebateAmount.toString(), bqmlba.getCurrency_type_id(), 0, 1, account.getBtc(), rebateAmount, account.getBtc().add(rebateAmount),
										account.getBtc_locked(), account.getBtc_locked(), time, uid, RandomUtil.getTransNo(), "子会员购买vip，获得返利", rebateRecord.getId());
								bqAccountAmountLogDao.save(rebateLog);
							} else {
								throw new ServiceException("购买vip返利操作失败，用户：" + uid + "，金额：" + rebateAmount.doubleValue());
							}
						}
					}
				}
			}
		}
		
		// bfx解锁统计
		BQMemberLevelBuyRebateReconcilia reconcilia = new BQMemberLevelBuyRebateReconcilia();
		reconcilia.setF_user_email(bqaccount.getF_user_email());
		reconcilia.setF_user_id(userId);
		reconcilia.setSelf_bfx_lock(bqmlba.getBfx_lock());
		reconcilia.setSuperior(superior);
		reconcilia.setSuperior2(superior2);
		reconcilia.setTotal_bfx_lock(bqmlba.getBfx_lock());
		reconcilia.setTotal_bfx_unlock(BigDecimal.ZERO);
		reconcilia.setTotal_buy_amount(bqmlba.getChange_amount());
		
		if (bqMemberLevelBuyRebateReconciliaDao.updateRecord(reconcilia) <= 0) {
			throw new ServiceException("购买vip统计bfx锁仓操作失败，用户：" + reconcilia.getF_user_id());
		}
		
		reconciliaBfxLock(userId, bqmlba.getChange_amount(), bqmlba.getBfx_lock(), null, null, 0);
		
	}
	
	public List<BQMemberLevelBuyAmountLog> readLevelBuyAmountLog(long uid, long from, String direct, long startTime, long endTime, int limit) {
		String hql = "FROM BQMemberLevelBuyAmountLog WHERE 1 = 1";
		
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
			hql += " and log_id < :from";
		}
		
		hql += " ORDER BY log_id desc";
		List<BQMemberLevelBuyAmountLog> plist = bqMemberLevelBuyAmountLogDao.find(hql, map, 1, limit);
		return plist;
	}
	
	
	@Override
	public PagerModel readAllForAdmin(long userId) {
	
		int offset = SystemContext.getOffset();
		int pageSize = SystemContext.getPageSize();
		
		StringBuilder sql = new StringBuilder("SELECT bqmlbal.log_id, bqmlbal.currency_type_id, bqct.type_name, bqmlbal.change_amount, bqmlbal.dateline, bqmlbal.f_user_id, bqm.f_user_email, bqmlbal.before_level_name_id, bqmlbal.after_level_name_id, bqmlbal.before_level_name, bqmlbal.after_level_name");
		sql.append(" FROM bq_member_level_buy_amount_log bqmlbal");
		sql.append(" LEFT JOIN bq_member bqm ON bqmlbal.f_user_id = bqm.f_user_id");
		sql.append(" LEFT JOIN bq_currency_type bqct ON bqmlbal.currency_type_id = bqct.type_id");
		
		if (userId > 0) {
			sql.append(" WHERE bqmlbal.f_user_id = :uid");
		}
		
		String countHql = "select count(*) from (" + sql.toString() + ") as select_count_tmp_table";

		sql.append(" ORDER BY bqmlbal.log_id DESC");
		Session session = bqMemberLevelBuyAmountLogDao.getSession();
		SQLQuery query = session.createSQLQuery(countHql.toString());
		SQLQuery query1 = session.createSQLQuery(sql.toString());
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		if (userId > 0) {
			params.put("uid", userId);
		}

		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				query.setParameter(key, params.get(key));
				query1.setParameter(key, params.get(key));
			}
		}
		System.out.println(query.uniqueResult());
		int total = ((Number) query.uniqueResult()).intValue();
		PagerModel pm = new PagerModel();
		pm.setDatas(query1.setFirstResult(offset).setMaxResults(pageSize).list());
		pm.setTotal(total);
		return pm;
	}
	
	
	@Override
	public void save(BQMemberLevelBuyAmountLog level) {
		long time = bqMemberLevelBuyAmountLogDao.getUnix_TIMESTAMP();
		level.setDateline(time);
		bqMemberLevelBuyAmountLogDao.save(level);
	}

	@Override
	public void update(BQMemberLevelBuyAmountLog level) {
		bqMemberLevelBuyAmountLogDao.update(level);
	}
	
	@Override
	public void delete(BQMemberLevelBuyAmountLog level) {
		bqMemberLevelBuyAmountLogDao.delete(level);
	}

	@Override
	public PagerModel readLevelBuyAmountLogPager(long fUserId) {
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append("from BQMemberLevelBuyAmountLog b where b.f_user_id=:id");
		map.put("id", fUserId);
		String hql = " order by b.log_id desc ";
		hql = sbBuilder.toString() + hql;
		return bqMemberLevelBuyAmountLogDao.searchHql(hql, map);
	}

	@Override
	public List<BQMemberLevelBuyAmountLog> findNeedUnlockRecordsByTime(long endTime) {
		String hql = "from BQMemberLevelBuyAmountLog where bfx_unlock < bfx_lock and dateline < :endTime";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("endTime", endTime);
		
		return bqMemberLevelBuyAmountLogDao.find(hql, map);
	}

	@Override
	public Object readSumBfxLock(long userId) {
			String sql = "select IFNULL(sum(b.bfx_lock),0) as bfxLock, IFNULL(sum(b.bfx_unlock),0) as bfxUnlock from bq_member_level_buy_amount_log b where 1 = 1";
			
			if (userId > 0) {
				sql += " and f_user_id = :userId";
			}
			
			Session session = bqMemberLevelBuyAmountLogDao.getSession();
			SQLQuery query = session.createSQLQuery(sql);
			if (userId > 0) {
				query.setLong("userId", userId);
			}
			return query.uniqueResult();
	}

}
