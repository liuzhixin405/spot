package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQSubMemberTransferLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQSubMemberTransferLogService {

	void save(BQSubMemberTransferLog log);
	
	boolean updateAccountForTransfer(BQSubMemberTransferLog log) throws ServiceException;
	
	
	/************************************ for read only **************************************/
	
	PagerModel readByUserId(long motherUserId);
	
	List<BQSubMemberTransferLog> readByUserId(long motherUserId, String symbol, int operation, long from, String direct, long startTime, long endTime, int limit);
}
