package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQLoanAdjustRecordDao;
import com.btc100.btc_q.dao.BQLoanBackRecordDao;
import com.btc100.btc_q.dao.BQLoanCloseRecordDao;
import com.btc100.btc_q.dao.BQLoanRecordDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQLoanAdjustRecord;
import com.btc100.btc_q.entity.BQLoanBackRecord;
import com.btc100.btc_q.entity.BQLoanCloseRecord;
import com.btc100.btc_q.entity.BQLoanRecord;
import com.btc100.btc_q.entity.BQLoanSetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQLoanRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqLoanRecordService")
public class BQLoanRecordServiceImpl implements BQLoanRecordService {

	@Autowired
	private BQLoanRecordDao<BQLoanRecord> bqLoanRecordDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private BQLoanBackRecordDao<BQLoanBackRecord> bqLoanBackRecordDao;
	@Autowired
	private BQLoanAdjustRecordDao<BQLoanAdjustRecord> bqLoanAdjustRecordDao;
	@Autowired
	private BQLoanCloseRecordDao<BQLoanCloseRecord> bqLoanCloseRecordDao;
	

	@Override
	public void saveRecord(BQLoanRecord record, BQLoanSetting setting)  throws ServiceException {
		
		// 获取借出用户
		String[] uids = setting.getBorrow_uids().split(",");
		if (uids.length == 0) {
			throw new ServiceException("未设置借出用户");
		}
		
		List<String> uidList = new ArrayList<>();
		for (String s : uids) {
			uidList.add(s);
		}
		
		BQAccount account = null;
		if (setting.getBorrow_type() == 1) {
			while (uidList.size() > 0) {
				Random r = new Random();
				int index = r.nextInt(uidList.size());
				
				String uidStr = uidList.get(index);
				uidList.remove(index);
				
				long uid = Convert.toLong(uidStr, 0);
				if (uid > 0) {
					BQAccount act = bqAccountDao.getByUerAndCurrencyId(uid, setting.getBorrow_coin_id());
					if (act.getBtc().compareTo(record.getReceived_amount()) >= 0) {
						account = act;
						break;
					}
				}
			}
		} else {
			while (uidList.size() > 0) {
				int index = 0;
				String uidStr = uidList.get(index);
				uidList.remove(index);
				
				long uid = Convert.toLong(uidStr, 0);
				if (uid > 0) {
					BQAccount act = bqAccountDao.getByUerAndCurrencyId(uid, setting.getBorrow_coin_id());
					if (act.getBtc().compareTo(record.getReceived_amount()) >= 0) {
						account = act;
						break;
					}
				}
			}
		}
		
		if (account == null) {
			throw new ServiceException("无满足余额条件借出用户");
		}
		
		// 扣除借出用户资金
		long time = bqAccountDao.getUnix_TIMESTAMP();
		int ret = bqAccountDao.updateAmount(account.getF_user_id(), account.getCurrency_type_id(), record.getReceived_amount().negate(), BigDecimal.ZERO);
		if (ret <= 0) {
			throw new ServiceException("扣除借出用户余额失败，用户：" + account.getF_user_id() + ", 币种：" + record.getBorrow_symbol());
		}
		
		BQAccountAmountLog amountLog = new BQAccountAmountLog("质押借币-借出扣除", account.getCurrency_type_id(), 0, -1, account.getBtc(), record.getReceived_amount(), account.getBtc().subtract(record.getReceived_amount()), 
				account.getBtc_locked(), account.getBtc_locked(), time, account.getF_user_id(), RandomUtil.getTransNo(), "质押借币-借出" + record.getReceived_amount().doubleValue(), 0);
		bqAccountAmountLogDao.save(amountLog);
		
		// 冻结质押币种
		BQAccount pledgeAccount = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getPledge_coin_id());
		ret = bqAccountDao.updateAmount(record.getF_user_id(), record.getPledge_coin_id(), record.getPledge_amount().negate(), record.getPledge_amount());
		if (ret <= 0) {
			throw new ServiceException("质押借币冻结用户" + record.getF_user_id() + ", 质押币种" + record.getPledge_symbol() + "失败");
		}
		BQAccountAmountLog pledgeLog = new BQAccountAmountLog("质押借币-借入冻结质押", pledgeAccount.getCurrency_type_id(), 0, -1, pledgeAccount.getBtc(), record.getPledge_amount(), pledgeAccount.getBtc().subtract(record.getPledge_amount()), 
				pledgeAccount.getBtc_locked(), pledgeAccount.getBtc_locked().add(record.getPledge_amount()), time, pledgeAccount.getF_user_id(), RandomUtil.getTransNo(), "质押借币-质押" + record.getPledge_amount().doubleValue(), 0);
		bqAccountAmountLogDao.save(pledgeLog);
		
		// 增加借入用户资金
		BQAccount borrowAccount = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getBorrow_coin_id());
		ret = bqAccountDao.updateAmount(record.getF_user_id(), record.getBorrow_coin_id(), record.getReceived_amount(), BigDecimal.ZERO);
		if (ret <= 0) {
			throw new ServiceException("质押借币借出余额增加失败！用户：" + record.getF_user_id() + ", 借出币种" + record.getBorrow_symbol());
		}
		BQAccountAmountLog borrowLog = new BQAccountAmountLog("质押借币-借入增加余额", borrowAccount.getCurrency_type_id(), 0, 1, borrowAccount.getBtc(), record.getReceived_amount(), borrowAccount.getBtc().add(record.getReceived_amount()), 
				borrowAccount.getBtc_locked(), borrowAccount.getBtc_locked(), time, borrowAccount.getF_user_id(), RandomUtil.getTransNo(), "质押借币-质押" + record.getReceived_amount().doubleValue(), 0);
		bqAccountAmountLogDao.save(borrowLog);
		
		// 保存结款记录
		record.setBorrow_uid(account.getF_user_id());
		record.setRecord_time(time);
		record.setState(1);
		record.setNote("质押借币创建成功，计息中");
		bqLoanRecordDao.save(record);
		
		// 质押金调整记录
		BQLoanAdjustRecord adjustRecord = new BQLoanAdjustRecord();
		adjustRecord.setF_user_id(record.getF_user_id());
		adjustRecord.setLoan_record_id(record.getId());
		adjustRecord.setPledge_coin_id(record.getPledge_coin_id());
		adjustRecord.setPledge_symbol(record.getPledge_symbol());
		adjustRecord.setBorrow_coin_id(record.getBorrow_coin_id());
		adjustRecord.setBorrow_symbol(record.getBorrow_symbol());
		adjustRecord.setPledge_amount(record.getPledge_amount());
		adjustRecord.setAdjust_amount(record.getPledge_amount());
		adjustRecord.setOld_pledge_rate(BigDecimal.ZERO);
		adjustRecord.setNew_pledge_rate(record.getPledge_rate());
		adjustRecord.setDirection(0);
		adjustRecord.setRecord_time(time);
		adjustRecord.setNote("新增质押");

		bqLoanAdjustRecordDao.save(adjustRecord);
	}

	@Override
	public PagerModel readAllByUserId(long userId, int borrowId, int state) {
		String borrowHql = "";

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			borrowHql += " and borrow_uid = :borrowId";
		}
		
		if (state > 0) {
			params.put("state", state);
			borrowHql += " and state = :state";
		}
		
		return bqLoanRecordDao.searchHql("from BQLoanRecord where f_user_id = :userId " + borrowHql + " order by record_time desc", params);
	}

	@Override
	public PagerModel readAll(long borrowId, int state) {
		String borrowHql = "";

		Map<String, Object> params = new HashMap<>();
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			borrowHql = " and borrow_uid = :borrowId";
		}
		
		if (state > 0) {
			params.put("state", state);
			borrowHql += " and state = :state";
		}
		
		return bqLoanRecordDao.searchHql("from BQLoanRecord where 1 = 1 " + borrowHql + " order by record_time desc", params);
	}

	@Override
	public BQLoanRecord readById(long id) {
		return bqLoanRecordDao.get(BQLoanRecord.class, id);
	}

	@Override
	public void backCoin(BQLoanRecord record, BigDecimal amount) throws ServiceException {
		
		BigDecimal fee = record.getInterest().multiply(record.getFee_rate());
		BigDecimal allBack = record.getBorrow_amount();

		Map<String, Object> params = new HashMap<>();
		params.put("backBorrow", record.getReceived_amount());
		params.put("backPledge", record.getPledge_amount());
		params.put("fee", fee);
		params.put("state", 2);
		params.put("note", "用户还币");
		params.put("id", record.getId());
		
		int ret = 0;
		
		// 扣除借入用户资金
		long time = bqAccountDao.getUnix_TIMESTAMP();
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getBorrow_coin_id());
		ret = bqAccountDao.updateAmount(account.getF_user_id(), account.getCurrency_type_id(), allBack.negate(), BigDecimal.ZERO);
		if (ret <= 0) {
			throw new ServiceException("还款扣除资金失败，用户：" + record.getF_user_id() + ", 币种：" + record.getBorrow_symbol() + ", 金额：" + allBack.doubleValue());
		}
		
		BQAccountAmountLog amountLog = new BQAccountAmountLog("质押借币-还款扣除", account.getCurrency_type_id(), 0, -1, account.getBtc(), allBack, account.getBtc().subtract(allBack), 
				account.getBtc_locked(), account.getBtc_locked(), time, account.getF_user_id(), RandomUtil.getTransNo(), "质押借币-还款" + allBack.doubleValue(), 0);
		bqAccountAmountLogDao.save(amountLog);
		
		// 更新记录
		ret = bqLoanRecordDao.updateCondition("update BQLoanRecord set back_borrow_amount=:backBorrow, back_pledge_amount=:backPledge, " 
				+ "fee=:fee, state=:state where id = :id and state = 1", params);
		if (ret <= 0) {
			throw new ServiceException("还币失败，更新记录失败，记录id：" + record.getId());
		}
		
		// 释放质押
		account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getPledge_coin_id());
		ret = bqAccountDao.updateAmount(account.getF_user_id(), account.getCurrency_type_id(), record.getPledge_amount(), record.getPledge_amount().negate());
		if (ret <= 0) {
			throw new ServiceException("质押借币，还款释放质押失败，用户：" + record.getF_user_id() + ", 币种：" + record.getPledge_coin_id() + ", 金额：" + record.getPledge_amount().doubleValue());
		}
		
		BQAccountAmountLog pledgeLog = new BQAccountAmountLog("质押借币-还款释放质押", account.getCurrency_type_id(), 0, 1, account.getBtc(), record.getPledge_amount(), account.getBtc().add(record.getPledge_amount()), 
				account.getBtc_locked(), account.getBtc_locked().subtract(record.getPledge_amount()), time, account.getF_user_id(), RandomUtil.getTransNo(), "质押借币-还款释放" + record.getPledge_amount().doubleValue(), 0);
		bqAccountAmountLogDao.save(pledgeLog);
		
		// 增加借出用户资金
		BigDecimal canObtain = allBack.subtract(fee);
		account = bqAccountDao.getByUerAndCurrencyId(record.getBorrow_uid(), record.getBorrow_coin_id());
		ret = bqAccountDao.updateAmount(account.getF_user_id(), account.getCurrency_type_id(), canObtain, BigDecimal.ZERO);
		if (ret <= 0) {
			throw new ServiceException("质押借币，还款，增加借出余额失败，用户：" + account.getF_user_id() + ", 币种：" + record.getBorrow_symbol() + ", 金额：" + canObtain.doubleValue());
		}
		
		BQAccountAmountLog borrowLog = new BQAccountAmountLog("质押借币-获得还款资金", account.getCurrency_type_id(), 0, 1, account.getBtc(), canObtain, account.getBtc().add(canObtain), 
				account.getBtc_locked(), account.getBtc_locked(), time, account.getF_user_id(), RandomUtil.getTransNo(), "质押借币-获得还款" + canObtain.doubleValue(), 0);
		bqAccountAmountLogDao.save(borrowLog);
		
		// 还款记录
		BQLoanBackRecord backRecod = new BQLoanBackRecord();
		backRecod.setF_user_id(record.getF_user_id());
		backRecod.setLoan_record_id(record.getId());
		backRecod.setPledge_coin_id(record.getPledge_coin_id());
		backRecod.setPledge_symbol(record.getPledge_symbol());
		backRecod.setBorrow_coin_id(record.getBorrow_coin_id());
		backRecod.setBorrow_symbol(record.getBorrow_symbol());
		backRecod.setBorrow_uid(record.getBorrow_uid());
		backRecod.setBorrow_amount(record.getBorrow_amount());
		backRecod.setPledge_amount(record.getPledge_amount());
		backRecod.setDay_rate(record.getDay_rate());
		backRecod.setFee_rate(record.getFee_rate());
		backRecod.setBack_all_amount(allBack);
		backRecod.setBack_borrow_amount(record.getReceived_amount());
		backRecod.setBack_pledge_amount(record.getPledge_amount());
		backRecod.setInterest(record.getInterest());
		backRecod.setBack_time(time);
		backRecod.setFee(fee);
		backRecod.setType(0);
		backRecod.setNote("用户正常还款");
		bqLoanBackRecordDao.save(backRecod);
	}
	
	@Override
	public void closeCoin(BQLoanRecord record, BigDecimal lastPrice) throws ServiceException {
		
		long time = bqAccountDao.getUnix_TIMESTAMP();
		
		// 扣除借入方币种，归借出方所有（抵偿）
		BQLoanCloseRecord close = new BQLoanCloseRecord();
		close.setF_user_id(record.getF_user_id());
		close.setLoan_record_id(record.getId());
		close.setPledge_coin_id(record.getPledge_coin_id());
		close.setPledge_symbol(record.getPledge_symbol());
		close.setBorrow_coin_id(record.getBorrow_coin_id());
		close.setBorrow_symbol(record.getBorrow_symbol());
		close.setBorrow_uid(record.getBorrow_uid());
		close.setPledge_amount(record.getPledge_amount());
		close.setBack_pledge_amount(BigDecimal.ZERO);
		
		BigDecimal interest = record.getInterest();
		BigDecimal fee = interest.multiply(record.getFee_rate()).setScale(8, BigDecimal.ROUND_HALF_UP);
		
		interest = interest.divide(lastPrice, 8, BigDecimal.ROUND_HALF_UP);
		fee = fee.divide(lastPrice, 8, BigDecimal.ROUND_HALF_UP);
		
		close.setPayment_pledge_amount(record.getPledge_amount().subtract(interest));
		close.setInterest(interest);
		close.setFee(fee);
		close.setDefault_amount(BigDecimal.ZERO);
		close.setDay_rate(record.getDay_rate());
		close.setFee_rate(record.getFee_rate());
		close.setPrice_index(lastPrice);
		close.setClose_result(1);
		close.setRecord_time(time);
		close.setNote("自动平仓");
		
		// 扣除质押冻结
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getPledge_coin_id());
		int ret = bqAccountDao.updateAmount(account.getF_user_id(), account.getCurrency_type_id(), BigDecimal.ZERO, record.getPledge_amount().negate());
		if (ret <= 0) {
			throw new ServiceException("质押借币平仓，添扣除质押冻结失败，用户：" + account.getF_user_id() + ", 币种：" + record.getPledge_symbol() + ", 金额：" + record.getPledge_amount().doubleValue());
		}
		
		BQAccountAmountLog amountLog = new BQAccountAmountLog("质押借币-平仓扣除质押", account.getCurrency_type_id(), 0, -1, account.getBtc(), record.getPledge_amount(), account.getBtc(), 
				account.getBtc_locked(), account.getBtc_locked().subtract(record.getPledge_amount()), time, account.getF_user_id(), RandomUtil.getTransNo(), "质押借币-平仓扣除质押金" + record.getPledge_amount().doubleValue(), 0);
		bqAccountAmountLogDao.save(amountLog);
		
		// 更新记录状态
		Map<String, Object> params = new HashMap<>();
		params.put("payment", record.getPledge_amount().subtract(interest));
		params.put("interest", interest);
		params.put("fee", fee);
		params.put("state", 3);
		params.put("note", "自动平仓");
		params.put("id", record.getId());
		ret = bqLoanRecordDao.updateCondition("update BQLoanRecord set payment_pledge_amount=:payment,payment_interest=:interest,payment_fee=:fee,note=:note,state=:state where id=:id and state=1", params);
		if (ret <= 0) {
			throw new ServiceException("质押借款，平仓，更新记录失败，记录id：" + record.getId());
		}
		
		// 保存平仓记录
		bqLoanCloseRecordDao.save(close);
		
		// 添加借出用户余额
		BigDecimal amount = record.getPledge_amount().subtract(fee);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new ServiceException("质押借款，平仓，添加借出用户余额失败，添加值为负，记录id：" + record.getId());
		}
		
		BQAccount borrowAccount = bqAccountDao.getByUerAndCurrencyId(record.getBorrow_uid(), record.getPledge_coin_id());
		ret = bqAccountDao.updateAmount(borrowAccount.getF_user_id(), borrowAccount.getCurrency_type_id(), amount, BigDecimal.ZERO);
		if (ret <= 0) {
			throw new ServiceException("质押借币平仓，添加借出用户余额失败，用户：" + borrowAccount.getF_user_id() + ", 币种：" + record.getPledge_symbol() + ", 金额：" + amount.doubleValue());
		}
		
		BQAccountAmountLog borrowLog = new BQAccountAmountLog("质押借币-平仓添加质押", borrowAccount.getCurrency_type_id(), 0, 1, borrowAccount.getBtc(), amount, borrowAccount.getBtc().add(amount), 
				borrowAccount.getBtc_locked(), borrowAccount.getBtc_locked(), time, borrowAccount.getF_user_id(), RandomUtil.getTransNo(), "质押借币-平仓添加质押金" + amount.doubleValue(), 0);
		bqAccountAmountLogDao.save(borrowLog);
	}

	@Override
	public void adjustPledge(BQLoanRecord record, BigDecimal amount, BigDecimal closeRate, BigDecimal price) throws ServiceException {
		
		if (amount.signum() == 0) return;
		
		long time = bqAccountDao.getUnix_TIMESTAMP();
		BigDecimal realPledge = record.getBorrow_amount().divide(price, 8, BigDecimal.ROUND_HALF_UP);
		BigDecimal oldRate = realPledge.divide(record.getPledge_amount(), 8, BigDecimal.ROUND_HALF_UP);
		BigDecimal nowRate = realPledge.divide(record.getPledge_amount().add(amount), 8, BigDecimal.ROUND_HALF_UP);
		
		BQLoanAdjustRecord adjustRecord = new BQLoanAdjustRecord();
		adjustRecord.setF_user_id(record.getF_user_id());
		adjustRecord.setLoan_record_id(record.getId());
		adjustRecord.setPledge_coin_id(record.getPledge_coin_id());
		adjustRecord.setPledge_symbol(record.getPledge_symbol());
		adjustRecord.setBorrow_coin_id(record.getBorrow_coin_id());
		adjustRecord.setBorrow_symbol(record.getBorrow_symbol());
		adjustRecord.setPledge_amount(record.getPledge_amount().add(amount));
		adjustRecord.setAdjust_amount(amount.abs());
		adjustRecord.setOld_pledge_rate(oldRate);
		adjustRecord.setNew_pledge_rate(nowRate);
		adjustRecord.setDirection(amount.signum());
		adjustRecord.setRecord_time(time);
		adjustRecord.setNote("调整质押金：" + amount.doubleValue());
		
		if (amount.signum() > 0) {		// 增加质押金
			// 扣除质押币种余额
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getPledge_coin_id());
			int ret = bqAccountDao.updateAmount(account.getF_user_id(), account.getCurrency_type_id(), amount.negate(), amount);
			if (ret <= 0) {
				throw new ServiceException("质押借款，添加质押金失败，减少余额，用户：" + account.getF_user_id() + ", 币种：" + record.getPledge_symbol() + ", 金额：" + amount.doubleValue());
			}
			
			BQAccountAmountLog amountLog = new BQAccountAmountLog("质押借币-增加质押金", account.getCurrency_type_id(), 0, -1, account.getBtc(), amount, account.getBtc().subtract(amount), 
					account.getBtc_locked(), account.getBtc_locked().add(amount), time, account.getF_user_id(), RandomUtil.getTransNo(), "质押借币-增加质押金" + amount.doubleValue(), 0);
			bqAccountAmountLogDao.save(amountLog);
			

			// 更新质押订单记录
			Map<String, Object> params = new HashMap<>();
			params.put("amount", amount);
			params.put("id", record.getId());
			ret = bqLoanRecordDao.updateCondition("update BQLoanRecord set pledge_amount=pledge_amount+:amount where id = :id and state = 1", params);
			if (ret <= 0) {
				throw new ServiceException("质押借款，添加质押金失败，更新记录失败，记录id：" + record.getId());
			}
		} else {		// 减少质押金
			if (nowRate.compareTo(closeRate) > 0) {
				throw new ServiceException("质押借款，减少质押金失败，调整后质押率必须小于平仓质押率，记录id：" + record.getId());
			}
			
			// 更新质押订单记录
			Map<String, Object> params = new HashMap<>();
			params.put("amount", amount);
			params.put("id", record.getId());
			int ret = bqLoanRecordDao.updateCondition("update BQLoanRecord set pledge_amount=pledge_amount+:amount where id = :id and state = 1", params);
			if (ret <= 0) {
				throw new ServiceException("质押借款，减少质押金失败，更新记录失败，记录id：" + record.getId());
			}
			
			amount = amount.abs();
			
			// 增加质押币种余额
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getPledge_coin_id());
			ret = bqAccountDao.updateAmount(account.getF_user_id(), account.getCurrency_type_id(), amount, amount.negate());
			if (ret <= 0) {
				throw new ServiceException("质押借款，减少质押金失败，增加余额，用户：" + account.getF_user_id() + ", 币种：" + record.getPledge_symbol() + ", 金额：" + amount.abs().doubleValue());
			}
			
			BQAccountAmountLog amountLog = new BQAccountAmountLog("质押借币-减少质押金", account.getCurrency_type_id(), 0, 1, account.getBtc(), amount, account.getBtc().add(amount), 
					account.getBtc_locked(), account.getBtc_locked().subtract(amount), time, account.getF_user_id(), RandomUtil.getTransNo(), "质押借币-减少质押金" + amount.doubleValue(), 0);
			bqAccountAmountLogDao.save(amountLog);
		}
		
		// 质押金调整记录
		bqLoanAdjustRecordDao.save(adjustRecord);
	}

	@Override
	public BigDecimal readSumBorrow(long userId, int borrowId, int state) {
		return bqLoanRecordDao.getSumBorrow(userId, borrowId, state);
	}

	@Override
	public BigDecimal readSumPledge(long userId, int ledgeId, int state) {
		return bqLoanRecordDao.getSumPledge(userId, ledgeId, state);
	}

	@Override
	public BigDecimal readSumBorrowOut(long borrowUid, int borrowId, int state) {
		return bqLoanRecordDao.getSumBorrowOut(borrowUid, borrowId, state);
	}

	@Override
	public BigDecimal readSumBorrowIncome(long borrowUid, int borrowId, int state) {
		return bqLoanRecordDao.getSumBorrowIncome(borrowUid, borrowId, state);
	}

	@Override
	public List<BQLoanRecord> findAllLargeRate(BigDecimal price, int pledge, int borrow, BigDecimal rate) {
		Map<String, Object> params = new HashMap<>();
		params.put("price", price);
		params.put("pledge", pledge);
		params.put("borrow", borrow);
		params.put("rate", rate);
		
		String hql = "from BQLoanRecord where borrow_amount/:price/pledge_amount>=:rate and pledge_coin_id = :pledge and borrow_coin_id = :borrow and state=1";
		return bqLoanRecordDao.find(hql, params);
	}

	@Override
	public List<BQLoanRecord> findAllOverTime() {
		String hql = "from BQLoanRecord where UNIX_TIMESTAMP() >= record_time+cycle*24*3600 and state=1";
		return bqLoanRecordDao.find(hql);
	}

	@Override
	public List<BQLoanRecord> findAllOverCloseTime(long addTime) {
		Map<String, Object> params = new HashMap<>();
		params.put("time", addTime);
		
		String hql = "from BQLoanRecord where UNIX_TIMESTAMP() >= (record_time+cycle*24*3600+:time) and state=1";
		return bqLoanRecordDao.find(hql, params);
	}
}
