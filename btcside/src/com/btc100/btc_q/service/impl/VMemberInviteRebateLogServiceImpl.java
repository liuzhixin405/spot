package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.VMemberInviteRebateLogDao;
import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.VMemberInviteRebateLog;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.VMemberInviteRebateLogService;
import com.btc100.btc_q.util.RandomUtil;

@Service("vMemberInviteRebateLogService")
public class VMemberInviteRebateLogServiceImpl implements VMemberInviteRebateLogService {

	@Autowired
	private VMemberInviteRebateLogDao<VMemberInviteRebateLog> vMemberInviteRebateLogDao;
	@Autowired
	private BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	public long getTime() {
		return vMemberInviteRebateLogDao.getUnix_TIMESTAMP();
	}

	@Override
	public VMemberInviteRebateLog createAndSaveRecord(long userId, long childUserId, BigDecimal tradeFee, long tradeUserId, 
			long tradeId, String vCode, long maxLevelId, int currencyId) throws ServiceException {
		if (tradeFee.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}
		
		BigDecimal childRebateRate = BigDecimal.ZERO;
		BQMemberLevel childLevel = bqMemberLevelDao.findByUserId(childUserId);
		if (childLevel != null) {
			if (childLevel.getLevel_id() >= maxLevelId) {
				return null;
			}
			
			if (tradeUserId != childUserId) {
				childRebateRate = childLevel.getBroker_commission_rate();
			}
		}
		
		VMemberInviteRebateLog record = new VMemberInviteRebateLog();
		record.setUser_id(0);
		BQMemberLevel userLevel = bqMemberLevelDao.findByUserId(userId);
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
		record.setBq_code(vCode);
		record.setCurrency_type_id(currencyId);
		
		vMemberInviteRebateLogDao.save(record);
		
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
}
