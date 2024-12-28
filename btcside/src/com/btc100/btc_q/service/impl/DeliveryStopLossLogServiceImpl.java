package com.btc100.btc_q.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryStopLossLogDao;
import com.btc100.btc_q.entity.DeliveryStopLossLog;
import com.btc100.btc_q.service.DeliveryStopLossLogService;

@Service("deliveryStopLossLogService")
public class DeliveryStopLossLogServiceImpl implements DeliveryStopLossLogService {
	@Autowired
	private DeliveryStopLossLogDao<DeliveryStopLossLog> deliveryStopLossLogDao;
	
	
	public void save(DeliveryStopLossLog DeliveryStopLossLog) {
		deliveryStopLossLogDao.insertLog(DeliveryStopLossLog);
	}
	
	public void saveList(List<DeliveryStopLossLog> data) {
		if (data != null) {
			for (DeliveryStopLossLog bqs : data) {
				if (bqs != null) {
					deliveryStopLossLogDao.insertLog(bqs);
				}
			}
		}
	}
	
}
