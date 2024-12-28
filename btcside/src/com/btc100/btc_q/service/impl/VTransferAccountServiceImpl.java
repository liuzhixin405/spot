package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.VAccountContractDao;
import com.btc100.btc_q.dao.VContractAccountLogDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VContractAccountLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.VTransferAccountService;
import com.btc100.btc_q.util.RandomUtil;

@Service("vTransferAccountService")
public class VTransferAccountServiceImpl implements VTransferAccountService {

	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private VAccountContractDao<VAccountContract> vAccountContractDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private VContractAccountLogDao<VContractAccountLog> vContractAccountLogDao;
	
	private boolean updateAccountContract(long uid, int currencyId, String bqCode, BigDecimal amount, String transNo, long time) throws ServiceException {
		int equal = amount.compareTo(BigDecimal.ZERO);
		int changeType = Contents.TRANS_TYPE_TX;
		int direction = -1;
		if (equal == 0) {
			return true;
		} else if (equal < 0) {	
			changeType = Contents.TRANS_TYPE_CZ;
			direction = 1;
		} 
		
		VAccountContract accountContract = vAccountContractDao.findByUserIDCode(uid, bqCode);
		if(vAccountContractDao.updateAmount(uid, bqCode, BigDecimal.ZERO, amount.negate(), BigDecimal.ZERO, BigDecimal.ZERO)<=0){
			throw new ServiceException("余额不足！");
		}
		
		vAccountContractDao.getSession().flush();
		vAccountContractDao.getSession().clear();
		String logTitle = equal > 0 ? "提现（转出）" : "充值（转入）";
		VAccountContract afterAccountContract = vAccountContractDao.findByUserIDCode(uid, bqCode);
		VContractAccountLog accountLog = new VContractAccountLog(logTitle, currencyId, changeType, direction, 
				accountContract.getF_btc(), amount.abs(), afterAccountContract.getF_btc(), accountContract.getF_lockbtc(),
				afterAccountContract.getF_lockbtc(), time, uid, transNo, 0, bqCode);
		vContractAccountLogDao.insertLog(accountLog);
		return true;
		
	}
	
	private boolean updateAccount(long uid, int currencyId, String bqCode, BigDecimal amount, String transNo, long time) throws ServiceException {
		int equal = amount.compareTo(BigDecimal.ZERO);
		int changeType = Contents.TRANS_TYPE_OUT;
		int direction = -1;
		if (equal == 0) {
			return true;
		} else if (equal > 0) {	
			changeType = Contents.TRANS_TYPE_TO;
			direction = 1;
		} 
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
		if (bqAccountDao.updateAmount(uid, currencyId, amount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("余额不足！");
		}
		
		bqAccountDao.getSession().flush();
		bqAccountDao.getSession().clear();
		BQAccount afterAccount = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
		String logTitle = (equal > 0 ? "转入自" : "转出到") + bqCode;
		BQAccountAmountLog amountLog = new BQAccountAmountLog(logTitle, currencyId, changeType, direction, account.getBtc(), amount.abs(), afterAccount.getBtc(),
				account.getBtc_locked(), afterAccount.getBtc_locked(), time, uid, transNo, "转账", 0);
		bqAccountAmountLogDao.save(amountLog);
		return true;
	}

	public String updateAccountForZC(long uid, int currencyId, String bqCode, BigDecimal amount) throws ServiceException {
		int equal = amount.compareTo(BigDecimal.ZERO);
		long time = bqAccountDao.getUnix_TIMESTAMP();
		String transNo = RandomUtil.getTransNo();

		boolean success = false;
		if (equal == 0) {
			return "";
		} else if (equal > 0) {	// amount 大于0 合约账户减少，总账户增加（转出合约账户）
			if (updateAccountContract(uid, currencyId, bqCode, amount, transNo, time)) {
				success = updateAccount(uid, currencyId, bqCode, amount, transNo, time);
			}
		} else {
			if (updateAccount(uid, currencyId, bqCode, amount, transNo, time)) {
				success = updateAccountContract(uid, currencyId, bqCode, amount, transNo, time);
			}
		}
		
		if (success) {
			return transNo;
		}
		
		return "";
	}

}
