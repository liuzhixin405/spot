package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountContractDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.ContractAccountLogDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.ContractAccountLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.TransferAccountService;
import com.btc100.btc_q.util.RandomUtil;

@Service("transferAccountService")
public class TransferAccountServiceImpl implements TransferAccountService {

	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountContractDao<BQAccountContract> bqAccountContractDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private ContractAccountLogDao<ContractAccountLog> contractAccountLogDao;
	
	private boolean updateAccountContract(long uid, String bqCode, BigDecimal amount, String transNo, long time) throws ServiceException {
		int equal = amount.compareTo(BigDecimal.ZERO);
		int changeType = Contents.TRANS_TYPE_TX;
		int direction = -1;
		if (equal == 0) {
			return true;
		} else if (equal < 0) {	
			changeType = Contents.TRANS_TYPE_CZ;
			direction = 1;
		} 
		
		BQAccountContract accountContract = bqAccountContractDao.findByUserIDCode(uid, bqCode);
		if(bqAccountContractDao.updateAmount(uid, bqCode, BigDecimal.ZERO, amount.negate(), BigDecimal.ZERO, BigDecimal.ZERO)<=0){
			throw new ServiceException("余额不足！");
		}
		
		bqAccountContractDao.getSession().flush();
		bqAccountContractDao.getSession().clear();
		String logTitle = equal > 0 ? "提现（转出）" : "充值（转入）";
		BQAccountContract afterAccountContract = bqAccountContractDao.findByUserIDCode(uid, bqCode);
		ContractAccountLog accountLog = new ContractAccountLog(logTitle, Contents.TRADE_CURRENCY_TYPE_ID, changeType, direction, 
				accountContract.getF_btc(), amount.abs(), afterAccountContract.getF_btc(), accountContract.getF_lockbtc(),
				afterAccountContract.getF_lockbtc(), time, uid, transNo, 0, bqCode);
		contractAccountLogDao.insertLog(accountLog);
		
		return true;
	}
	
	private boolean updateAccount(long uid, String bqCode, BigDecimal amount, String transNo, long time) throws ServiceException {
		int equal = amount.compareTo(BigDecimal.ZERO);
		int changeType = Contents.TRANS_TYPE_OUT;
		int direction = -1;
		if (equal == 0) {
			return true;
		} else if (equal > 0) {	
			changeType = Contents.TRANS_TYPE_TO;
			direction = 1;
		} 
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(uid, Contents.TRADE_CURRENCY_TYPE_ID);
		if (bqAccountDao.updateAmount(uid, Contents.TRADE_CURRENCY_TYPE_ID, amount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("余额不足！");
		}
		
		bqAccountDao.getSession().flush();
		bqAccountDao.getSession().clear();
		BQAccount afterAccount = bqAccountDao.getByUerAndCurrencyId(uid, Contents.TRADE_CURRENCY_TYPE_ID);
		String logTitle = (equal > 0 ? "转入自" : "转出到") + bqCode;
		BQAccountAmountLog amountLog = new BQAccountAmountLog(logTitle, Contents.TRADE_CURRENCY_TYPE_ID, changeType, direction, account.getBtc(), amount.abs(), afterAccount.getBtc(),
				account.getBtc_locked(), afterAccount.getBtc_locked(), time, uid, transNo, "转账", 0);
		bqAccountAmountLogDao.save(amountLog);
		return true;
	}

	public String updateAccountForZC(long uid, String bqCode, BigDecimal amount) throws ServiceException {
		int equal = amount.compareTo(BigDecimal.ZERO);
		long time = bqAccountDao.getUnix_TIMESTAMP();
		String transNo = RandomUtil.getTransNo();

		boolean success = false;
		if (equal == 0) {
			return "";
		} else if (equal > 0) {	// amount 大于0 合约账户减少，总账户增加（转出合约账户）
			if (updateAccountContract(uid, bqCode, amount, transNo, time)) {
				success = updateAccount(uid, bqCode, amount, transNo, time);
			}
		} else {
			if (updateAccount(uid, bqCode, amount, transNo, time)) {
				success = updateAccountContract(uid, bqCode, amount, transNo, time);
			}
		}
		
		if (success) {
			return transNo;
		}
		
		return "";
	}
	
	@Override
	public String updateAccountToOTC(BQCurrencyType currency, long uid, BigDecimal amount) throws ServiceException {
		long time = bqAccountDao.getUnix_TIMESTAMP();
		String transNo = RandomUtil.getTransNo();

		int changeType = Contents.TRANS_TYPE_OUTOTC;
		amount = amount.negate();
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(uid, currency.getType_id());
		if (bqAccountDao.updateAmount(uid, currency.getType_id(), amount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("余额不足！");
		}
		
		bqAccountDao.getSession().flush();
		bqAccountDao.getSession().clear();
		
		BQAccount afterAccount = bqAccountDao.getByUerAndCurrencyId(uid, currency.getType_id());
		String logTitle = currency.getType_name() + "转出到OTC资产";
		BQAccountAmountLog amountLog = new BQAccountAmountLog(logTitle, currency.getType_id(), changeType, -1, account.getBtc(), amount.abs(), afterAccount.getBtc(),
				account.getBtc_locked(), afterAccount.getBtc_locked(), time, uid, transNo, "转账", 0);
		bqAccountAmountLogDao.save(amountLog);
		
		return transNo;
	}

	@Override
	public String updateAccountFromOTC(BQCurrencyType currency, long uid, BigDecimal amount) throws ServiceException {
		long time = bqAccountDao.getUnix_TIMESTAMP();
		String transNo = RandomUtil.getTransNo();

		int changeType = Contents.TRANS_TYPE_TOOTC;
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(uid, currency.getType_id());
		if (bqAccountDao.updateAmount(uid, currency.getType_id(), amount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("账户更新错误！");
		}
		
		bqAccountDao.getSession().flush();
		bqAccountDao.getSession().clear();
		
		BQAccount afterAccount = bqAccountDao.getByUerAndCurrencyId(uid, currency.getType_id());
		String logTitle = currency.getType_name() + "OTC资产转入";
		BQAccountAmountLog amountLog = new BQAccountAmountLog(logTitle, currency.getType_id(), changeType, 1, account.getBtc(), amount.abs(), afterAccount.getBtc(),
				account.getBtc_locked(), afterAccount.getBtc_locked(), time, uid, transNo, "转账", 0);
		bqAccountAmountLogDao.save(amountLog);
		
		return transNo;
	}

}
