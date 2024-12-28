package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQExchangeBfcRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQExchangeBfcRecordService {

	void saveRecord(BQExchangeBfcRecord record) throws ServiceException;

	void changeState(long recordId, int state, int oldState, String note);

	void processRecord(BQExchangeBfcRecord record) throws ServiceException;

	void confirmRecord(BQExchangeBfcRecord record, String note) throws ServiceException;
	
	void managerConfirmRecord(BQExchangeBfcRecord record, String note) throws ServiceException;

	void cancelRecord(BQExchangeBfcRecord record, String note) throws ServiceException;

	void manualRecord(BQExchangeBfcRecord record, String note) throws ServiceException;

	List<BQExchangeBfcRecord> findByState(int state, int limit);

	List<BQExchangeBfcRecord> findSubmitAndChecked(int limit, int currencyId);


	/************************************ for read only **************************************/

	BQExchangeBfcRecord readById(long id);

	PagerModel readRecords(long uid, int currencyId, int ori, int target);

	PagerModel readRecordsOrderByTime(long uid, int currencyId, int ori, int target);

	List<BQExchangeBfcRecord> readByUidAndTime(long uid, long startTime, long endTime, long from, String direct,
			int size, int ori, int target);

	BQExchangeBfcRecord readByIdAndURecordId(long uid, long recordId);
}
