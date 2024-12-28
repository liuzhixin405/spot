package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQSubMemberTransferLogDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQSubMemberTransferLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQSubMemberTransferLogService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqSubMemberTransferLogService")
public class BQSubMemberTransferLogServiceImpl implements BQSubMemberTransferLogService {

	@Autowired
	private BQSubMemberTransferLogDao<BQSubMemberTransferLog> bqSubMemberTransferDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	public void save(BQSubMemberTransferLog log) {
		bqSubMemberTransferDao.save(log);
	}

	@Override
	public boolean updateAccountForTransfer(BQSubMemberTransferLog log) throws ServiceException {
		
		String motherTitle = "子账号转出到母账户";
		int changeType = Contents.TRANS_TYPE_FROMSUB;// 母账户加款，子账号减款
		BigDecimal motherAmount = log.getAmount();
		BigDecimal subAmount = log.getAmount().negate();
		if (log.getOperation() == Contents.SUB_TRANSFER_IN) {
			changeType = Contents.TRANS_TYPE_TOSUB;// 子账户加款，母账号减款
			motherTitle = "母账户转入子账号";
			motherAmount = motherAmount.negate();
			
			subAmount = subAmount.negate();
		}
		String motherTransNo = updateAccount(log.getMother_user_id(), log.getCurrency_type_id(), motherAmount, motherTitle, changeType);
		String subTransNo = updateAccount(log.getSub_user_id(), log.getCurrency_type_id(), subAmount, motherTitle, changeType);
		if (StringUtil.isEmpty(motherTransNo) || StringUtil.isEmpty(subTransNo)) {
			return false;
		}
		
		bqSubMemberTransferDao.save(log);
		
		return true;
	}
	
	private String updateAccount(long uid, int currencyId, BigDecimal amount, String title, int changeType) throws ServiceException {
		int equal = amount.compareTo(BigDecimal.ZERO);
		int direction = -1;
		if (equal == 0) {
			return "";
		} else if (equal > 0) {	
			direction = 1;
		} 
		String transNo = RandomUtil.getTransNo();
		long time = bqSubMemberTransferDao.getUnix_TIMESTAMP();
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
		if (bqAccountDao.updateAmount(uid, currencyId, amount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("余额不足！转入转出失败");
		}
		
		bqAccountDao.getSession().flush();
		bqAccountDao.getSession().clear();
		BQAccount afterAccount = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
		title += ",数量：" + amount.abs();
		BQAccountAmountLog amountLog = new BQAccountAmountLog(title, currencyId, changeType, direction, account.getBtc(), amount.abs(), afterAccount.getBtc(),
				account.getBtc_locked(), afterAccount.getBtc_locked(), time, uid, transNo, "母子账户划转", 0);
		bqAccountAmountLogDao.save(amountLog);
		return transNo;
	}

	@Override
	public PagerModel readByUserId(long motherUserId) {
		String hql = "FROM BQSubMemberTransferLog WHERE mother_user_id = :userId ORDER BY id DESC";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", motherUserId);
		return bqSubMemberTransferDao.searchHql(hql, params);
	}

	@Override
	public List<BQSubMemberTransferLog> readByUserId(long motherUserId, String symbol, int operation, long from,
			String direct, long startTime, long endTime, int limit) {
		String hql = "FROM BQSubMemberTransferLog WHERE mother_user_id = :userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", motherUserId);
		
		if (!StringUtil.isEmpty(symbol)) {
			hql += " and currency_name = :symbol";
			params.put("symbol", symbol);
		}
		if (operation > 0) {
			hql += " and operation = :operation";
			params.put("operation", operation);
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
			hql += " and id < :from";
		}
		
		hql += " ORDER BY id DESC";
		
		return bqSubMemberTransferDao.find(hql, params, 1, limit);
	}

}
