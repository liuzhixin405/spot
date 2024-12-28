package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQExchangeRecordDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQExchangeRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQExchangeRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqExchangeRecordService")
public class BQExchangeRecordServiceImpl implements BQExchangeRecordService {

	@Autowired
	private BQExchangeRecordDao<BQExchangeRecord> bqExchangeRecordDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	
	@Override
	public void saveRecord(BQExchangeRecord record) throws ServiceException {
		
		record.setRecord_time(bqExchangeRecordDao.getUnix_TIMESTAMP());
		record.setOper_time(bqExchangeRecordDao.getUnix_TIMESTAMP());
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
		if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), record.getFrozen_amount().negate(), record.getFrozen_amount()) > 0) {
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getOri_currency_name() + "兑换"  + record.getTarget_amount() + "，提交，扣除可用增加冻结",
					record.getOri_currency_type_id(), 9, -1, account.getBtc(), record.getFrozen_amount(), account.getBtc().subtract(record.getFrozen_amount()), 
					account.getBtc_locked(), account.getBtc_locked().add(record.getFrozen_amount()), bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), 
					RandomUtil.getTransNo(), "提交兑换，扣除可用增加冻结", 0);
			bqAccountAmountLogDao.save(pAccountLog);
		} else {
			throw new ServiceException("兑换处理错误，用户资金异，常兑换记录ID： " + record.getId());
		}

		bqExchangeRecordDao.save(record);
	}

	@Override
	public List<BQExchangeRecord> findByState(int state, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		return bqExchangeRecordDao.find("from BQExchangeRecord where state = :state order by record_time ", params, 0, limit);
	}

	@Override
	public void update(BQExchangeRecord record) {
		bqExchangeRecordDao.update(record);
	}

	@Override
	public List<BQExchangeRecord> findSubmitAndChecked(int limit, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state1", Contents.EXCHANGE_STATE_SUBMIT);
		params.put("state2", Contents.EXCHANGE_STATE_CHECKED);
		params.put("currencyId", currencyId);
		return bqExchangeRecordDao.find("from BQExchangeRecord where (state = :state1 or state = :state2) and (ori_currency_type_id = :currencyId or target_currency_type_id = :currencyId) order by record_time", params, 0, limit);
	}

	@Override
	public void processRecord(BQExchangeRecord record) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Contents.EXCHANGE_STATE_PROCESSING);
		params.put("id", record.getId());
		params.put("state1", record.getState());
		params.put("note", "自动处理中");
		params.put("operTime", bqExchangeRecordDao.getUnix_TIMESTAMP());
		if (bqExchangeRecordDao.updateCondition("update BQExchangeRecord set state = :state,oper_time = :operTime,note = :note where id = :id and state = :state1", params) > 0) {
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), BigDecimal.ZERO, record.getFrozen_amount().negate()) > 0) {
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getOri_currency_name() + "兑换"  + record.getTarget_amount(),
						record.getOri_currency_type_id(), 9, -1, account.getBtc(), record.getFrozen_amount(), account.getBtc(), account.getBtc_locked(), account.getBtc_locked().subtract(record.getFrozen_amount()),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(), "兑换自动处理，扣除冻结", 0);
				bqAccountAmountLogDao.save(pAccountLog);
			} else {
				throw new ServiceException("兑换处理错误，用户资金异，常兑换记录ID： " + record.getId());
			}
			
		} else {
			throw new ServiceException("自动处理兑换记录出错，更改记录状态失败!!兑换记录ID： " + record.getId());
		}
	}
	
	@Override
	public void manualRecord(BQExchangeRecord record, String note) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Contents.EXCHANGE_STATE_MANUAL);
		params.put("id", record.getId());
		params.put("state1", record.getState());
		params.put("note", note);
		params.put("operTime", bqExchangeRecordDao.getUnix_TIMESTAMP());
		if (bqExchangeRecordDao.updateCondition("update BQExchangeRecord set state = :state,oper_time = :operTime,note = :note where id = :id and state = :state1", params) > 0) {
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), BigDecimal.ZERO, record.getFrozen_amount()) > 0) {
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getOri_currency_name() + "兑换"  + record.getTarget_amount() + "，转人工-恢复冻结",
						record.getOri_currency_type_id(), 9, 1, account.getBtc(), record.getFrozen_amount(), account.getBtc(), account.getBtc_locked(), account.getBtc_locked().add(record.getFrozen_amount()),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(), "转人工恢复冻结", 0);
				bqAccountAmountLogDao.save(pAccountLog);
			} else {
				throw new ServiceException("兑换处理错误，用户资金异，常兑换记录ID： " + record.getId());
			}
			
		} else {
			throw new ServiceException("自动处理兑换记录出错，更改记录状态失败!!兑换记录ID： " + record.getId());
		}
		
		final String email = UrlConfig.getValue("exchange.manualNotify.email");
		final String msg = record.getOri_currency_name() + "兑换" + record.getTarget_currency_name();
		Runnable runnable = new Runnable() {
            public void run() {
            	try {
            		SendMail themail = new SendMail();
            		String content = msg + "失败或需人工审核，请登录xkd后台管理处理";
        			themail.send(email, content);
            	} catch (Exception e) {
            	}
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
	}

	@Override
	public void changeState(long recordId, int state, int oldState, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("id", recordId);
		params.put("operTime", bqExchangeRecordDao.getUnix_TIMESTAMP());
		params.put("oldState", oldState);
		String noteSql = "";
		if (!StringUtil.isEmpty(note)) {
			noteSql = ", note = :note";
			params.put("note", note);
		}
		
		bqExchangeRecordDao.updateCondition("update BQExchangeRecord set state = :state,oper_time = :operTime" +  noteSql + " where id = :id and state = :oldState" , params);
	}

	@Override
	public void addHedgingTimesAndNote(long recordId, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("note", note);
		params.put("time", bqExchangeRecordDao.getUnix_TIMESTAMP());
		params.put("id", recordId);
		String hql = "update BQExchangeRecord set note = :note,oper_time = :time, hedging_times = hedging_times + 1 where id = :id";
		bqExchangeRecordDao.updateCondition(hql, params);
		
	}

	@Override
	public void orderSuccess(long recordId, String orderId, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", recordId);
		params.put("operTime", bqExchangeRecordDao.getUnix_TIMESTAMP());
		params.put("order_id", orderId);
		String noteSql = "";
		if (!StringUtil.isEmpty(note)) {
			noteSql = ", note = :note";
			params.put("note", note);
		}
		
		bqExchangeRecordDao.updateCondition("update BQExchangeRecord set order_id = :order_id,oper_time = :operTime" +  noteSql + " where id = :id " , params);
	}

	@Override
	public void confirmRecord(BQExchangeRecord record, BigDecimal oriDealNums, BigDecimal obtainNums, BigDecimal dealCash, String note) throws ServiceException {
		// 改变记录状态
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal changeOriAmount = BigDecimal.ZERO;
		BigDecimal changeTargetAmount = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal dealAmount = BigDecimal.ZERO;
		if (record.getDirection() == Contents.DIRECTION_BUY) {
			
			// 恢复冻结差
			price = dealCash.divide(obtainNums, 8, BigDecimal.ROUND_DOWN);
			changeOriAmount = record.getFrozen_amount().subtract(dealCash);
			
			// 增加目标币种
			fee = obtainNums.multiply(record.getFee_rate().multiply(new BigDecimal(0.01)));
			changeTargetAmount = obtainNums.subtract(fee);
			
			dealAmount = obtainNums;
		} else {
			
			// 恢复冻结差
			changeOriAmount = record.getFrozen_amount().subtract(oriDealNums);
			
			// 增加目标币种
			price = dealCash.divide(oriDealNums, 8, BigDecimal.ROUND_DOWN);
			fee = dealCash.multiply(record.getFee_rate().multiply(new BigDecimal(0.01)));
			changeTargetAmount = dealCash.subtract(fee);
			
			dealAmount = oriDealNums;
		}
		
		BigDecimal changeOriFrozen = BigDecimal.ZERO;
		if (record.getState() != Contents.EXCHANGE_STATE_PROCESSING) {
			// 扣除冻结
			changeOriFrozen = record.getFrozen_amount();
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", record.getId());
		params.put("fee", fee);
		params.put("obtain", changeTargetAmount);
		params.put("frozenBack", changeOriAmount);
		params.put("state", Contents.EXCHANGE_STATE_SUCCESS);
		params.put("state1", record.getState());
		params.put("operTime", bqExchangeRecordDao.getUnix_TIMESTAMP());
		params.put("note", note);
		params.put("price", price);
		params.put("dealAmount", dealAmount);
		params.put("dealCash", dealCash);
		
		if (bqExchangeRecordDao.updateCondition("update BQExchangeRecord set fee = :fee, obtain_amount = :obtain, frozen_amount_back = :frozenBack, state = :state,"
				+ "oper_time = :operTime, note = :note, avg_price = :price, deal_amount = :dealAmount, deal_cash = :dealCash where id = :id and state = :state1", params) > 0) {
			if (changeOriAmount.compareTo(BigDecimal.ZERO) != 0 || changeOriFrozen.compareTo(BigDecimal.ZERO) != 0) {
				BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
				if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), changeOriAmount, changeOriFrozen.negate()) > 0) {
					BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getOri_currency_name() + "兑换"  + record.getTarget_amount() + "，退回差额",
							record.getOri_currency_type_id(), 9, -1, account.getBtc(), changeOriAmount, account.getBtc().add(changeOriAmount), account.getBtc_locked(), account.getBtc_locked(),
							bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(), "兑换自动处理，成交后退回差额", 0);
					bqAccountAmountLogDao.save(pAccountLog);
				} else {
					throw new ServiceException("兑换成功后，退回差额处理错误，用户资金异常，兑换记录ID： " + record.getId());
				}
			}
			
			if (changeTargetAmount.compareTo(BigDecimal.ZERO) != 0) {
				BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getTarget_currency_type_id());
				if (bqAccountDao.updateAmount(record.getF_user_id(), record.getTarget_currency_type_id(), changeTargetAmount, BigDecimal.ZERO) > 0) {
					BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getOri_currency_name() + "兑换"  + record.getTarget_amount() + "，成交可用增加",
							record.getTarget_currency_type_id(), 9, -1, account.getBtc(), changeTargetAmount, account.getBtc().add(changeTargetAmount), account.getBtc_locked(), account.getBtc_locked(),
							bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(), "兑换自动处理，成交增加可用", 0);
					bqAccountAmountLogDao.save(pAccountLog);
				} else {
					throw new ServiceException("兑换成功后，增加可用处理错误，用户资金异常，兑换记录ID： " + record.getId());
				}
			}
		} else {
			throw new ServiceException("自动处理兑换记录，成交后处理出错，更改记录状态失败!!兑换记录ID： " + record.getId());
		}
		
	}

	@Override
	public void cancelRecord(BQExchangeRecord record, String note) throws ServiceException {
		if (record.getState() == Contents.EXCHANGE_STATE_PROCESSING 
				|| record.getState() == Contents.EXCHANGE_STATE_CANCELLED
				|| record.getState() == Contents.EXCHANGE_STATE_SUCCESS) {
				return;
			}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Contents.EXCHANGE_STATE_CANCELLED);
		params.put("id", record.getId());
		params.put("note", note);
		params.put("state1", record.getState());
		params.put("operTime", bqExchangeRecordDao.getUnix_TIMESTAMP());
		if (bqExchangeRecordDao.updateCondition("update BQExchangeRecord set state = :state,oper_time = :operTime,note = :note where id = :id and state = :state1", params) > 0) {
			if (record.getFrozen_amount().compareTo(BigDecimal.ZERO) != 0) {
				BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
				if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), record.getFrozen_amount(), record.getFrozen_amount().negate()) > 0) {
					BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getOri_currency_name() + "兑换"  + record.getTarget_amount() + "，取消兑换",
							record.getOri_currency_type_id(), 9, -1, account.getBtc(), record.getFrozen_amount(), account.getBtc().add(record.getFrozen_amount()), account.getBtc_locked(),
							account.getBtc_locked().subtract(record.getFrozen_amount()), bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(), "取消兑换，恢复可用", 0);
					bqAccountAmountLogDao.save(pAccountLog);
				} else {
					throw new ServiceException("取消兑换处理错误，用户资金异常，兑换记录ID： " + record.getId());
				}
			}
		} else {
			throw new ServiceException("自动处理兑换记录出错，更改记录状态失败!!兑换记录ID： " + record.getId());
		}
		
	}

	@Override
	public PagerModel readRecords(long uid, int currencyId, int ori, int target) {		
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "";
		if (uid > 0) {
			params.put("uid", uid);
			sql = " and f_user_id = :uid";
		}
		
		if (currencyId > 0) {
			params.put("currencyId", currencyId);
			sql += " and (ori_currency_type_id = :currencyId or target_currency_type_id = :currencyId)";
		} else {
			if (ori > 0) {
				params.put("ori", ori);
				sql += " and ori_currency_type_id = :ori";
			}
			
			if (target > 0) {
				params.put("target", target);
				sql += " and target_currency_type_id = :target";
			}
		}
		
		String hql = "From BQExchangeRecord ";
		if (!StringUtil.isEmpty(sql)) {
			hql += "where 1 = 1" + sql + " order by state, record_time desc";
		}
		
		return bqExchangeRecordDao.searchHql(hql, params);
	}
	
	@Override
	public PagerModel readRecordsOrderByTime(long uid, int currencyId, int ori, int target) {		
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "";
		if (uid > 0) {
			params.put("uid", uid);
			sql = " and f_user_id = :uid";
		}
		
		if (currencyId > 0) {
			params.put("currencyId", currencyId);
			sql += " and (ori_currency_type_id = :currencyId or target_currency_type_id = :currencyId)";
		} else {
			if (ori > 0) {
				params.put("ori", ori);
				sql += " and ori_currency_type_id = :ori";
			}
			
			if (target > 0) {
				params.put("target", target);
				sql += " and target_currency_type_id = :target";
			}
		}
		
		String hql = "From BQExchangeRecord ";
		if (!StringUtil.isEmpty(sql)) {
			hql += "where 1 = 1" + sql + " order by record_time desc";
		}
		
		return bqExchangeRecordDao.searchHql(hql, params);
	}

	@Override
	public BQExchangeRecord readById(long id) {
		return bqExchangeRecordDao.get(BQExchangeRecord.class, id);
	}

	@Override
	public BigDecimal sumFrozen(long uid, int currencyId) {
		return bqExchangeRecordDao.sumFrozen(uid, currencyId);
	}

	@Override
	public BigDecimal sumDeduction(long uid, int currencyId) {
		return bqExchangeRecordDao.sumDeduction(uid, currencyId);
	}

	@Override
	public BigDecimal sumObtain(long uid, int currencyId) {
		return bqExchangeRecordDao.sumObtain(uid, currencyId);
	}

	@Override
	public List<BQExchangeRecord> readByUidAndTime(long uid, long startTime, long endTime, long from, String direct,
			int size, int ori, int target) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "";
		if (uid > 0) {
			params.put("uid", uid);
			sql += " and f_user_id = :uid";
		}
		if (startTime > 0) {
			params.put("startTime", startTime);
			sql += " and record_time >= :startTime";
		}
		if (endTime > 0) {
			params.put("endTime", endTime);
			sql += " and record_time <= :endTime";
		}
		if (ori > 0) {
			params.put("ori", ori);
			sql += " and ori_currency_type_id = :ori";
		}
		if (target > 0) {
			params.put("target", target);
			sql += " and target_currency_type_id = :target";
		}
		if (from >= 0 && "prev".equals(direct)) {
			params.put("from", from);
			sql += " and id >= " + " :from";
		} else if (from >= 0 && "next".equals(direct)) {
			params.put("from", from);
			sql += " and id < " + " :from";
		}
		sql += " order by id desc";
		
		String hql = "From BQExchangeRecord where 1 = 1 " + sql;
		
		return bqExchangeRecordDao.find(hql, params, 0, size);
	}

	@Override
	public BQExchangeRecord readByIdAndURecordId(long uid, long recordId) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "";
		if (uid > 0) {
			params.put("uid", uid);
			sql += " and f_user_id = :uid";
		}
		if (recordId > 0) {
			params.put("recordId", recordId);
			sql += " and id = :recordId";
		}
		String hql = "From BQExchangeRecord ";
		if (!StringUtil.isEmpty(sql)) {
			hql += "where 1 = 1" + sql;
		}
		return bqExchangeRecordDao.get(hql, params);
	}

	@Override
	public void adminExchange(BQExchangeRecord record) throws ServiceException {
		record.setState(Contents.EXCHANGE_STATE_SUCCESS);
		record.setRecord_time(bqExchangeRecordDao.getUnix_TIMESTAMP());
		record.setOper_time(bqExchangeRecordDao.getUnix_TIMESTAMP());
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
		record.setOri_amount(account.getBtc());
		record.setFrozen_amount(record.getOri_amount());
		record.setFrozen_amount_back(BigDecimal.ZERO);
		record.setTarget_amount(account.getBtc().multiply(record.getPresent_price()));
		record.setObtain_amount(record.getTarget_amount());
		if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), record.getOri_amount().negate(), BigDecimal.ZERO) > 0) {
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getOri_currency_name() + "兑换"  + record.getTarget_currency_name() + "，扣除源币种可用",
					record.getOri_currency_type_id(), 9, -1, account.getBtc(), record.getOri_amount(), account.getBtc().subtract(record.getOri_amount()), 
					account.getBtc_locked(), account.getBtc_locked(), bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), 
					RandomUtil.getTransNo(), "兑换，扣除可用", 0);
			bqAccountAmountLogDao.save(pAccountLog);
		} else {
			throw new ServiceException("兑换处理错误，用户资金异，常兑换记录ID： " + record.getId());
		}
		
		BQAccount targetAccount = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getTarget_currency_type_id());
		if (bqAccountDao.updateAmount(record.getF_user_id(), record.getTarget_currency_type_id(), record.getTarget_amount(), BigDecimal.ZERO) > 0) {
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog(record.getOri_currency_name() + "兑换"  + record.getTarget_currency_name() + "，提交，增加目标可用",
					record.getTarget_currency_type_id(), 9, 1, targetAccount.getBtc(), record.getTarget_amount(), targetAccount.getBtc().add(record.getTarget_amount()), 
					targetAccount.getBtc_locked(), targetAccount.getBtc_locked(), bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), 
					RandomUtil.getTransNo(), "兑换，增加可用", 0);
			bqAccountAmountLogDao.save(pAccountLog);
		} else {
			throw new ServiceException("兑换处理错误，用户资金异，常兑换记录ID： " + record.getId());
		}

		bqExchangeRecordDao.save(record);
	}

}
