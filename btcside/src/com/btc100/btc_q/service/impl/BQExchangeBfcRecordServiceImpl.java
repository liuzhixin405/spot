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
import com.btc100.btc_q.dao.BQExchangeBfcRecordDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQExchangeBfcRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQExchangeBfcRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqExchangeBfcRecordService")
public class BQExchangeBfcRecordServiceImpl implements BQExchangeBfcRecordService {

	@Autowired
	private BQExchangeBfcRecordDao<BQExchangeBfcRecord> bqExchangeBfcRecordDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	@Override
	public void saveRecord(BQExchangeBfcRecord record) throws ServiceException {
		record.setRecord_time(bqExchangeBfcRecordDao.getUnix_TIMESTAMP());
		record.setOper_time(bqExchangeBfcRecordDao.getUnix_TIMESTAMP());
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
		if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), record.getFrozen_amount().negate(), record.getFrozen_amount()) > 0) {
			String title = record.getOri_currency_name() + "兑换" + record.getTarget_amount().doubleValue() + record.getTarget_currency_name() + "，提交，扣除可用增加冻结";
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title, record.getOri_currency_type_id(), 9, -1,
					account.getBtc(), record.getFrozen_amount(), account.getBtc().subtract(record.getFrozen_amount()),
					account.getBtc_locked(), account.getBtc_locked().add(record.getFrozen_amount()),
					bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(),
					"提交兑换，扣除可用增加冻结", 0);
			bqAccountAmountLogDao.save(pAccountLog);
		} else {
			throw new ServiceException("兑换处理错误，用户资金异，常兑换记录ID： " + record.getId());
		}
		bqExchangeBfcRecordDao.save(record);
	}

	@Override
	public void changeState(long recordId, int state, int oldState, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("id", recordId);
		params.put("operTime", bqExchangeBfcRecordDao.getUnix_TIMESTAMP());
		params.put("oldState", oldState);
		String noteSql = "";
		if (!StringUtil.isEmpty(note)) {
			noteSql = ", note = :note";
			params.put("note", note);
		}
		
		bqExchangeBfcRecordDao.updateCondition("update BQExchangeBfcRecord set state = :state,oper_time = :operTime" +  noteSql + " where id = :id and state = :oldState" , params);
	}

	@Override
	public void processRecord(BQExchangeBfcRecord record) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Contents.EXCHANGE_STATE_PROCESSING);
		params.put("id", record.getId());
		params.put("state1", record.getState());
		params.put("note", "自动处理中");
		params.put("operTime", bqExchangeBfcRecordDao.getUnix_TIMESTAMP());
		if (bqExchangeBfcRecordDao.updateCondition("update BQExchangeBfcRecord set state = :state,oper_time = :operTime,note = :note where id = :id and state = :state1", params) > 0) {
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), BigDecimal.ZERO, record.getFrozen_amount().negate()) > 0) {
				String title = record.getOri_currency_name() + "兑换" + record.getTarget_amount().doubleValue() + record.getTarget_currency_name() + "，自动处理，扣除冻结";
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title, record.getOri_currency_type_id(), 9, -1,
						account.getBtc(), record.getFrozen_amount(), account.getBtc(), account.getBtc_locked(),
						account.getBtc_locked().subtract(record.getFrozen_amount()),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(),
						"兑换自动处理，扣除冻结", 0);
				bqAccountAmountLogDao.save(pAccountLog);
			} else {
				throw new ServiceException("兑换处理错误，用户资金异，常兑换记录ID： " + record.getId());
			}
			
		} else {
			throw new ServiceException("自动处理兑换记录出错，更改记录状态失败!!兑换记录ID： " + record.getId());
		}
	}
	
	@Override
	public void manualRecord(BQExchangeBfcRecord record, String note) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Contents.EXCHANGE_STATE_MANUAL);
		params.put("id", record.getId());
		params.put("state1", record.getState());
		params.put("note", note);
		params.put("operTime", bqExchangeBfcRecordDao.getUnix_TIMESTAMP());
		if (bqExchangeBfcRecordDao.updateCondition("update BQExchangeBfcRecord set state = :state,oper_time = :operTime,note = :note where id = :id and state = :state1", params) > 0) {
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), BigDecimal.ZERO, record.getFrozen_amount()) > 0) {
				String title = record.getOri_currency_name() + "兑换" + record.getTarget_amount().doubleValue() + record.getTarget_currency_name() + "，转人工，恢复冻结";
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title, record.getOri_currency_type_id(), 9, 1,
						account.getBtc(), record.getFrozen_amount(), account.getBtc(), account.getBtc_locked(),
						account.getBtc_locked().add(record.getFrozen_amount()),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(),
						"转人工恢复冻结", 0);
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
	public void confirmRecord(BQExchangeBfcRecord record, String note) throws ServiceException {
		BigDecimal targetAmount = record.getTarget_amount();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", record.getId());
		params.put("state", Contents.EXCHANGE_STATE_SUCCESS);
		params.put("state1", record.getState());
		params.put("operTime", bqExchangeBfcRecordDao.getUnix_TIMESTAMP());
		params.put("note", note);
		
		if (bqExchangeBfcRecordDao.updateCondition("update BQExchangeBfcRecord set state = :state,oper_time = :operTime,note = :note where id = :id and state = :state1", params) > 0) {
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getTarget_currency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getTarget_currency_type_id(), targetAmount, BigDecimal.ZERO) > 0) {
				String title = record.getOri_currency_name() + "兑换" + targetAmount.doubleValue() + record.getTarget_currency_name() + "，兑换成功，可用增加";
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title,
						record.getTarget_currency_type_id(), 9, 1, account.getBtc(), targetAmount, account.getBtc().add(targetAmount), account.getBtc_locked(), account.getBtc_locked(),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(), "兑换自动处理，成交增加可用", 0);
				bqAccountAmountLogDao.save(pAccountLog);
			} else {
				throw new ServiceException("兑换成功后，增加可用处理错误，用户资金异常，兑换记录ID： " + record.getId());
			}
		} else {
			throw new ServiceException("自动处理兑换记录，成交后处理出错，更改记录状态失败!!兑换记录ID： " + record.getId());
		}
		
	}
	
	@Override
	public void managerConfirmRecord(BQExchangeBfcRecord record, String note) throws ServiceException {
		BigDecimal targetAmount = record.getTarget_amount();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", record.getId());
		params.put("state", Contents.EXCHANGE_STATE_SUCCESS);
		params.put("state1", record.getState());
		params.put("operTime", bqExchangeBfcRecordDao.getUnix_TIMESTAMP());
		params.put("note", note);
		
		if (bqExchangeBfcRecordDao.updateCondition("update BQExchangeBfcRecord set state = :state,oper_time = :operTime,note = :note where id = :id and state = :state1", params) > 0) {
			BQAccount targetAccount = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getTarget_currency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getTarget_currency_type_id(), targetAmount,
					BigDecimal.ZERO) > 0) {
				String title = record.getOri_currency_name() + "兑换" + targetAmount.doubleValue()
						+ record.getTarget_currency_name() + "，兑换成功，可用增加";
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title, record.getTarget_currency_type_id(), 9,
						1, targetAccount.getBtc(), targetAmount, targetAccount.getBtc().add(targetAmount),
						targetAccount.getBtc_locked(), targetAccount.getBtc_locked(),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(),
						"兑换自动处理，成交增加可用", 0);
				bqAccountAmountLogDao.save(pAccountLog);
			} else {
				throw new ServiceException("兑换成功后，增加可用处理错误，用户资金异常，兑换记录ID： " + record.getId());
			}
			BQAccount oriAccount = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), BigDecimal.ZERO,
					record.getFrozen_amount().negate()) > 0) {
				String title = record.getOri_currency_name() + "兑换" + targetAmount.doubleValue()
						+ record.getTarget_currency_name() + "，兑换成功，取消冻结";
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title, record.getOri_currency_type_id(), 9, -1,
						oriAccount.getBtc(), BigDecimal.ZERO, oriAccount.getBtc(), oriAccount.getBtc_locked(),
						oriAccount.getBtc_locked().subtract(record.getFrozen_amount()),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(),
						"兑换成功，取消冻结", 0);
				bqAccountAmountLogDao.save(pAccountLog);
			} else {
				throw new ServiceException("兑换成功后，取消冻结处理错误，用户资金异常，兑换记录ID： " + record.getId());
			}
		} else {
			throw new ServiceException("自动处理兑换记录，成交后处理出错，更改记录状态失败!!兑换记录ID： " + record.getId());
		}
		
	}

	@Override
	public void cancelRecord(BQExchangeBfcRecord record, String note) throws ServiceException {
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
		params.put("operTime", bqExchangeBfcRecordDao.getUnix_TIMESTAMP());
		if (bqExchangeBfcRecordDao.updateCondition("update BQExchangeBfcRecord set state = :state,oper_time = :operTime,note = :note where id = :id and state = :state1", params) > 0) {
			if (record.getFrozen_amount().compareTo(BigDecimal.ZERO) != 0) {
				BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getOri_currency_type_id());
				if (bqAccountDao.updateAmount(record.getF_user_id(), record.getOri_currency_type_id(), record.getFrozen_amount(), record.getFrozen_amount().negate()) > 0) {
					String title = record.getOri_currency_name() + "兑换" + record.getTarget_amount().doubleValue() + record.getTarget_currency_name() + "，取消兑换，恢复可用";
					BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title, record.getOri_currency_type_id(), 9,
							-1, account.getBtc(), record.getFrozen_amount(),
							account.getBtc().add(record.getFrozen_amount()), account.getBtc_locked(),
							account.getBtc_locked().subtract(record.getFrozen_amount()),
							bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTransNo(),
							"取消兑换，恢复可用", 0);
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
	public List<BQExchangeBfcRecord> findByState(int state, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		return bqExchangeBfcRecordDao.find("from BQExchangeBfcRecord where state = :state order by record_time ", params, 0, limit);
	}
	
	@Override
	public List<BQExchangeBfcRecord> findSubmitAndChecked(int limit, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state1", Contents.EXCHANGE_STATE_SUBMIT);
		params.put("state2", Contents.EXCHANGE_STATE_CHECKED);
		params.put("currencyId", currencyId);
		return bqExchangeBfcRecordDao.find("from BQExchangeBfcRecord where (state = :state1 or state = :state2) and (ori_currency_type_id = :currencyId or target_currency_type_id = :currencyId) order by record_time", params, 0, limit);
	}

	@Override
	public BQExchangeBfcRecord readById(long id) {
		return bqExchangeBfcRecordDao.get(BQExchangeBfcRecord.class, id);
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
		
		String hql = "From BQExchangeBfcRecord ";
		if (!StringUtil.isEmpty(sql)) {
			hql += "where 1 = 1" + sql + " order by state, record_time desc";
		}
		
		return bqExchangeBfcRecordDao.searchHql(hql, params);
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
		
		String hql = "From BQExchangeBfcRecord ";
		if (!StringUtil.isEmpty(sql)) {
			hql += "where 1 = 1" + sql + " order by record_time desc";
		}
		
		return bqExchangeBfcRecordDao.searchHql(hql, params);
	}

	@Override
	public List<BQExchangeBfcRecord> readByUidAndTime(long uid, long startTime, long endTime, long from, String direct,
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
		
		String hql = "From BQExchangeBfcRecord where 1 = 1 " + sql;
		
		return bqExchangeBfcRecordDao.find(hql, params, 0, size);
	}

	@Override
	public BQExchangeBfcRecord readByIdAndURecordId(long uid, long recordId) {
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
		String hql = "From BQExchangeBfcRecord ";
		if (!StringUtil.isEmpty(sql)) {
			hql += "where 1 = 1" + sql;
		}
		return bqExchangeBfcRecordDao.get(hql, params);
	}

}
