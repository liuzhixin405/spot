package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQBuyBfxBackRecordDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQBuyBfxBackRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQBuyBfxBackRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqBuyBfxBackRecordService")
public class BQBuyBfxBackRecordServiceImpl implements BQBuyBfxBackRecordService {
	
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQBuyBfxBackRecordDao<BQBuyBfxBackRecord> bqBuyBfxBackRecordDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	@Override
	public void saveRecord(BQBuyBfxBackRecord record) throws ServiceException {
		
		record.setRecord_time(bqBuyBfxBackRecordDao.getUnix_TIMESTAMP());
		record.setConfirm_time(0);
		
		// bfx冻结
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getBfx_currency_id());
		if (bqAccountDao.updateAmount(record.getF_user_id(), record.getBfx_currency_id(), record.getAmount().negate(), record.getAmount()) <= 0) {
			throw new ServiceException("余额不足！");
		}
		
		BQAccountAmountLog amountLog = new BQAccountAmountLog("bfx回购", record.getBfx_currency_id(), 9, -1, account.getBtc(), record.getAmount(), account.getBtc().add(record.getAmount()),
				account.getBtc_locked(), account.getBtc_locked().add(record.getAmount()), record.getRecord_time(), record.getF_user_id(), RandomUtil.getTransNo(), "回购", 0);
		bqAccountAmountLogDao.save(amountLog);
		
		bqBuyBfxBackRecordDao.save(record);
	}

	@Override
	public void changeState(long recordId, int state, int oldState, String note) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BigDecimal sumAmount(long uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal sumAmountFrozen(long uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal sumObtain(long uid, int currencyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagerModel readRecords(long uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BQBuyBfxBackRecord> readByUidAndTime(long uid, long startTime, long endTime, long from, String direct,
			int size) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
