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
import com.btc100.btc_q.dao.BQTransferWithOtcRecordDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQTransferWithOtcRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQTransferWithOtcRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqTransferWithOtcRecordService")
public class BQTransferWithOtcRecordServiceImpl implements BQTransferWithOtcRecordService {

	@Autowired
	private BQTransferWithOtcRecordDao<BQTransferWithOtcRecord> bqRecordDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	@Override
	public String saveRecord(BQTransferWithOtcRecord record) throws ServiceException {
		String transNo = RandomUtil.getTransNo();
		record.setRecord_time(bqRecordDao.getUnix_TIMESTAMP());
		record.setOper_time(bqRecordDao.getUnix_TIMESTAMP());

		BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getCurrency_type_id());
		if (bqAccountDao.updateAmount(record.getF_user_id(), record.getCurrency_type_id(),
				record.getFrozen_amount().negate(), record.getFrozen_amount()) > 0) {
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog(
					record.getCurrency_name() + "转出到OTC：" + record.getAmount() + "，需人工审核",
					record.getCurrency_type_id(), 0, -1, account.getBtc(), record.getFrozen_amount(),
					account.getBtc().subtract(record.getFrozen_amount()), account.getBtc_locked(),
					account.getBtc_locked().add(record.getFrozen_amount()), bqAccountAmountLogDao.getUnix_TIMESTAMP(),
					record.getF_user_id(), transNo, "转出到OTC，需人工审核，扣除可用增加冻结", 0);
			bqAccountAmountLogDao.save(pAccountLog);
		} else {
			throw new ServiceException("兑换处理错误，用户资金异，常兑换记录ID： " + record.getId());
		}

		bqRecordDao.save(record);

		final String email = UrlConfig.getValue("otc.manualNotify.email");
		if (!StringUtil.isEmpty(email)) {
			final String msg = record.getCurrency_name() + "转出到OTC：" + record.getAmount();
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						SendMail themail = new SendMail();
						String content = msg + "需人工审核，请登录OTC后台管理处理";
						themail.send(email, content);
					} catch (Exception e) {
					}
				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
		}
		
		return transNo;
	}

	@Override
	public List<BQTransferWithOtcRecord> findByState(int state, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		return bqRecordDao.find("from BQTransferWithOtcRecord where state = :state order by record_time ",
				params, 0, limit);
	}

	@Override
	public void update(BQTransferWithOtcRecord record) {
		bqRecordDao.update(record);
	}

	@Override
	public void changeState(long recordId, int state, int oldState, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("id", recordId);
		params.put("operTime", bqRecordDao.getUnix_TIMESTAMP());
		params.put("oldState", oldState);
		String noteSql = "";
		if (!StringUtil.isEmpty(note)) {
			noteSql = ", note = :note";
			params.put("note", note);
		}

		bqRecordDao.updateCondition("update BQTransferWithOtcRecord set state = :state,oper_time = :operTime"
				+ noteSql + " where id = :id and state = :oldState", params);
	}

	@Override
	public String confirmRecord(BQTransferWithOtcRecord record, String note) throws ServiceException {
		String transNo = RandomUtil.getTransNo();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", record.getId());
		params.put("state", Contents.TRANSFER_OTC_STATE_SUCCESS);
		params.put("state1", record.getState());
		params.put("operTime", bqRecordDao.getUnix_TIMESTAMP());
		params.put("note", note);

		if (bqRecordDao.updateCondition(
				"update BQTransferWithOtcRecord set state = :state, oper_time = :operTime, note = :note where id = :id and state = :state1",
				params) > 0) {
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getCurrency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getCurrency_type_id(), BigDecimal.ZERO,
					record.getFrozen_amount().negate()) > 0) {
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(
						record.getCurrency_name() + "转出到OTC：" + record.getAmount() + "，人工审核通过",
						record.getCurrency_type_id(), 13, -1, account.getBtc(), record.getFrozen_amount(),
						account.getBtc(), account.getBtc_locked(),
						account.getBtc_locked().subtract(record.getFrozen_amount()),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), transNo,
						"转出到OTC，人工审核通过，扣除冻结", 0);
				bqAccountAmountLogDao.save(pAccountLog);
			} else {
				throw new ServiceException("转出到OTC，人工审核通过处理错误，用户资金异常，记录ID： " + record.getId());
			}
		} else {
			throw new ServiceException("转出到OTC，人工审核通过处理错误，更改记录状态失败!!记录ID： " + record.getId());
		}
		
		return transNo;
	}

	@Override
	public String cancelRecord(BQTransferWithOtcRecord record, String note) throws ServiceException {
		String transNo = RandomUtil.getTransNo();
		if (record.getState() != Contents.TRANSFER_OTC_STATE_MANUAL) {
			return "";
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Contents.TRANSFER_OTC_STATE_CANCELLED);
		params.put("id", record.getId());
		params.put("note", note);
		params.put("state1", record.getState());
		params.put("operTime", bqRecordDao.getUnix_TIMESTAMP());
		if (bqRecordDao.updateCondition(
				"update BQTransferWithOtcRecord set state = :state, oper_time = :operTime, note = :note where id = :id and state = :state1",
				params) > 0) {
			BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getCurrency_type_id());
			if (bqAccountDao.updateAmount(record.getF_user_id(), record.getCurrency_type_id(),
					record.getFrozen_amount(), record.getFrozen_amount().negate()) > 0) {
				BQAccountAmountLog pAccountLog = new BQAccountAmountLog(
						record.getCurrency_name() + "转出到OTC：" + record.getAmount() + "，人工审核取消",
						record.getCurrency_type_id(), 0, 1, account.getBtc(), record.getFrozen_amount(),
						account.getBtc().add(record.getFrozen_amount()), account.getBtc_locked(),
						account.getBtc_locked().subtract(record.getFrozen_amount()),
						bqAccountAmountLogDao.getUnix_TIMESTAMP(), record.getF_user_id(), transNo,
						"转出到OTC，人工审核取消，恢复可用，取消冻结", 0);
				bqAccountAmountLogDao.save(pAccountLog);
			} else {
				throw new ServiceException("转出到OTC，人工审核取消处理，用户资金异常，记录ID： " + record.getId());
			}
		} else {
			throw new ServiceException("转出到OTC，人工审核取消处理，更改记录状态失败!!记录ID： " + record.getId());
		}

		return transNo;
	}

	@Override
	public PagerModel readRecords(long uid, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "";
		if (uid > 0) {
			params.put("uid", uid);
			sql = " and f_user_id = :uid";
		}

		if (currencyId > 0) {
			params.put("currencyId", currencyId);
			sql += " and (ori_currency_type_id = :currencyId or target_currency_type_id = :currencyId)";
		}

		String hql = "From BQTransferWithOtcRecord ";
		if (!StringUtil.isEmpty(sql)) {
			hql += "where 1 = 1" + sql + " order by state, record_time desc";
		}

		return bqRecordDao.searchHql(hql, params);
	}

	@Override
	public BQTransferWithOtcRecord readById(long id) {
		return bqRecordDao.get(BQTransferWithOtcRecord.class, id);
	}

	@Override
	public BigDecimal sumFrozen(long uid, int currencyId, int direction) {
		return bqRecordDao.sumFrozen(uid, currencyId, direction);
	}

	@Override
	public BQTransferWithOtcRecord readByIdAndURecordId(long uid, long recordId) {
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
		String hql = "From BQTransferWithOtcRecord ";
		if (!StringUtil.isEmpty(sql)) {
			hql += "where 1 = 1" + sql;
		}
		return bqRecordDao.get(hql, params);
	}

}
