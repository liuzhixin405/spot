package com.btc100.btc_q.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NStopLossLogDao;
import com.btc100.btc_q.entity.NStopLossLog;
import com.btc100.btc_q.service.NStopLossLogService;

@Service("nStopLossLogService")
public class NStopLossLogServiceImpl implements NStopLossLogService {
	
	@Autowired
	private NStopLossLogDao<NStopLossLog> bqStopLossLogDao;
	
	
	public void saveNStopLossLog(String partition, NStopLossLog bqStopLossLog) {
		bqStopLossLogDao.insertLog(partition, bqStopLossLog);
	}
	
	public void saveNStopLossLogList(String partition, List<NStopLossLog> data) {
		if (data != null) {
			for (NStopLossLog bqs : data) {
				if (bqs != null) {
					bqStopLossLogDao.insertLog(partition, bqs);
				}
			}
		}
	}
	
}
