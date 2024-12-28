package com.btc100.btc_q.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQStopLossLogDao;
import com.btc100.btc_q.entity.BQStopLossLog;
import com.btc100.btc_q.service.BQStopLossLogService;

@Service("bqStopLossLogService")
public class BQStopLossLogServiceImpl implements BQStopLossLogService {
	@Autowired
	private BQStopLossLogDao<BQStopLossLog> bqStopLossLogDao;
	
	
	public void saveBQStopLossLog(BQStopLossLog bqStopLossLog) {
		bqStopLossLogDao.insertLog(bqStopLossLog);
	}
	
	public void saveBQStopLossLogList(List<BQStopLossLog> data) {
		if (data != null) {
			for (BQStopLossLog bqs : data) {
				if (bqs != null) {
					bqStopLossLogDao.insertLog(bqs);
				}
			}
		}
	}
	
}
