package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQExchangeRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQExchangeRecordService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.PendingLogService;
import com.btc100.btc_q.util.HuobiApiUtil;

public class HedgingTask {
	
	@Autowired
	BQArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	BQMemberTransactionService bqMemberTransactionService; 
	@Autowired
	PendingDelegationService pendingDelegationService;
	@Autowired
	BQMemberService bqMemberService;
	@Autowired
	BQMemberLevelService bqMemberLevelService;
	@Autowired
	PendingLogService pendingLogService;
	@Autowired
	private BQExchangeRecordService bqExchangeRecordService;
	
	public void exchangeOrder(HuobiApiUtil apiUtil, String symbol, BQExchangeRecord record) {
		JSONObject retObj = null;
		try {
			BigDecimal amount = BigDecimal.ZERO;
			if (record.getDirection() == Contents.DIRECTION_BUY) {
				amount = record.getTarget_amount();
			} else {
				amount = record.getOri_amount();
			}
			retObj = apiUtil.orderUsdt(symbol, "usdt", record.getDirection(), amount, record.getOrder_price());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (retObj == null) {
			try {
				bqExchangeRecordService.manualRecord(record, "自动下单失败，调用orderUsdt出错, 下单调用次数：" + record.getHedging_times() + 1);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		} else {
			try {
				if ("ok".equals(retObj.getString("status"))) {
					bqExchangeRecordService.orderSuccess(record.getId(), retObj.getString("orderId"), "下单成功，订单号: " + retObj.getString("orderId"));
				} else if ("retry".equals(retObj.getString("status"))) {
					bqExchangeRecordService.addHedgingTimesAndNote(record.getId(), "下单失败: " + retObj.getString("error"));
				} else {
					try {
						bqExchangeRecordService.manualRecord(record, "下单失败: " + retObj.getString("error"));
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				try {
					bqExchangeRecordService.manualRecord(record, "操作失败，失败: " + e.getMessage());
				} catch (ServiceException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
