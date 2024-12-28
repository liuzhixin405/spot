package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btc100.btc_q.entity.DeliveryAutoCreateSetting;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.DeliveryAutoCreateSettingService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.TimeUtil;

/**
 * 自动创建日交割合约
 * 
 * @author Administrator
 * 
 */
@Component
public class DeliveryAutoCreateDayTransaction {

	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryTransactionTypeService transactionTypeService;
	@Autowired
	private DeliveryAutoCreateSettingService deliveryAutoCreateSettingService;

	public void run() {
		String deliveryType = "d";
		DeliveryAutoCreateSetting setting = deliveryAutoCreateSettingService.getByType(deliveryType);
		if (setting == null || !setting.getIs_open()) return;
		
		DeliveryTransactionType transactionType = transactionTypeService.getTransTypeById(setting.getTransaction_type_id());
		if (transactionType == null) return;
		
		String bqCode = transactionType.getType_code().toLowerCase();
		String prefixCode = bqCode + "_" + TimeUtil.getToday("yyyyMMdd") + setting.getDelivery_type();
		
		DeliveryTransaction trans = transactionService.findTransByCode(prefixCode);
		if (trans != null) return;
		
		long endtime = 0l;
	 	String[] endTimeStrArr = setting.getEnd_time().split(":");
		int hour = Integer.valueOf(endTimeStrArr[0]);
		int minute = Integer.valueOf(endTimeStrArr[1]);
		endtime = TimeUtil.getTimesmorning(System.currentTimeMillis()) + hour * 3600 + minute * 60;
		
		int state = 1;
		String multiple = setting.getMultiple();
		String multipleState = setting.getMultiple_state();
		BigDecimal tradeCoefficient = setting.getTrade_coefficient();
		int kind = setting.getTransaction_kind();
		String showName = prefixCode.toUpperCase();
		String enName = showName;
		String jpName = showName;
		String krName = showName;
		String hkName = showName;
		
		transactionService.addMultipleByCode(bqCode, deliveryType, state, multiple, multipleState, tradeCoefficient,
				endtime, transactionType.getType_id(), kind, showName, enName, jpName, krName, hkName);
	}
	
}
