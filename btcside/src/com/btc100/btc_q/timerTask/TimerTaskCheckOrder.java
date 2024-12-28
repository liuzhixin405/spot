package com.btc100.btc_q.timerTask;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQExchangeRecord;
import com.btc100.btc_q.entity.BQExchangeSetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQExchangeRecordService;
import com.btc100.btc_q.service.BQExchangeSettingService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.util.HuobiApiUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.BalanceAccountVO;
import com.btc100.btc_q.vo.HuobiOrderInfo;

public class TimerTaskCheckOrder extends HedgingTask {
	
	@Autowired
	BQMemberTransactionService bqMemberTransactionService; 
	@Autowired
	BQMemberService bqMemberService;
	@Autowired
	BQMemberLevelService bqMemberLevelService;
	@Autowired
	BQExchangeRecordService bqExchangeRecordService;
	@Autowired
	BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	BQExchangeSettingService bqExchangeSettingService;
	@Autowired
	BQCurrencyTypeService bqCurrencyTypeService;
	
	private static HuobiApiUtil apiUtil = null; 
	
	public void run() {
		
		if (apiUtil == null) {
			try {
				apiUtil = new HuobiApiUtil(UrlConfig.getValue("ExchangeHuobiSecretKey"), UrlConfig.getValue("ExchangeHuobiAccessKey"));
				apiUtil.getAccounts();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			}
		}
		
		// 兑换
		// 1、获取正在处理的记录，查询订单状态---
		List<BQExchangeRecord> processingRecords = bqExchangeRecordService.findByState(Contents.EXCHANGE_STATE_PROCESSING, 100);
		for (BQExchangeRecord record : processingRecords) {
			String symbol = record.getDirection() == Contents.DIRECTION_BUY ? record.getTarget_currency_name() : record.getOri_currency_name();
			if (StringUtil.isEmpty(record.getOrder_id()) && record.getHedging_times() < 3) {
				// 继续调用下单
				exchangeOrder(apiUtil, symbol, record);
			} else if (!StringUtil.isEmpty(record.getOrder_id())) {
				// 查订单状态
				HuobiOrderInfo orderInfo = null;
				String error = "";
				try {
					orderInfo = apiUtil.queryOrder(record.getOrder_id());
				} catch (Exception e) {
					error = e.getMessage();
				}
				
				if (orderInfo == null) {
					bqExchangeRecordService.changeState(record.getId(), Contents.EXCHANGE_STATE_PROCESSING, record.getState(),
							"获取订单信息失败，订单：" + record.getOrder_id() + ", " + error);
					continue;
				}
				
				if (HuobiOrderInfo.STATE_FILLED.equals(orderInfo.getState())) {	// 1、成交-确认（调整资金）
					try {
						bqExchangeRecordService.confirmRecord(record, orderInfo.getFieldAmount(), orderInfo.getFieldAmount(), orderInfo.getDealCash(), 
								"外部市场成交，成交量：" + orderInfo.getFieldAmount().doubleValue() + "，成交额：" + orderInfo.getDealCash().doubleValue());
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				} else if (HuobiOrderInfo.STATE_PARTIAL_CANCELED.equals(orderInfo.getState())) {	// 1、部分成交-确认（调整资金）
					try {
						bqExchangeRecordService.confirmRecord(record, orderInfo.getFieldAmount(), orderInfo.getFieldAmount(), orderInfo.getDealCash(), 
								"外部市场成交，成交量：" + orderInfo.getFieldAmount().doubleValue() + "，成交额：" + orderInfo.getDealCash().doubleValue());
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				} else if (HuobiOrderInfo.STATE_CANCELED.equals(orderInfo.getState())) {	// 2、撤单-转人工
					try {
						bqExchangeRecordService.manualRecord(record, "外部市场已撤单");
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				} else if (HuobiOrderInfo.STATE_PARTIAL_FILLED.equals(orderInfo.getState())) {	// 3、部分成交-确认（改变记录字段）
					// nothing to do ...
				}
			} else {
				// 转人工
				String order = record.getOrder_id();
				if (StringUtil.isEmpty(order)) {
					order = "空";
				}
				try {
					bqExchangeRecordService.manualRecord(record, record.getNote() + ", 下单调用次数：" + record.getHedging_times());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
		}
		
		// 2、处理刚提交或者审核的兑换记录
		
		List<BQExchangeSetting> settings = bqExchangeSettingService.findAll();
		for (BQExchangeSetting setting : settings) {
			if (!setting.getRunning()) continue;
			
			List<BQExchangeRecord> exchangeRecords = bqExchangeRecordService.findSubmitAndChecked(3, setting.getCurrency_type_id());
			for (BQExchangeRecord record : exchangeRecords) {
				try {
					bqExchangeRecordService.processRecord(record);
				} catch (ServiceException e) {
					// 转人工
					try {
						bqExchangeRecordService.manualRecord(record, "自动处理，改变记录状态失败！");
					} catch (ServiceException e1) {
						e1.printStackTrace();
					}
					continue;
				}
				
				// SUBMIT状态先对账-对账通过-改审核通过
				boolean checked = false;
				int oldState = record.getState();
				record.setState(Contents.EXCHANGE_STATE_PROCESSING);
				if (oldState == Contents.EXCHANGE_STATE_SUBMIT) {
					// 对账
					BalanceAccountVO allBalance = new BalanceAccountVO();
					BalanceAccountVO generalBalance = new BalanceAccountVO();
					List<BalanceAccountVO> contractBalances = new ArrayList<BalanceAccountVO>();
					List<BalanceAccountVO> deliveryContractBalances = new ArrayList<BalanceAccountVO>();
					try {
						bqMemberRechargeService.getBalanceAccount(record.getF_user_id(), record.getOri_currency_type_id(), allBalance, generalBalance, contractBalances, deliveryContractBalances);
					} catch (ParseException e1) {
						allBalance.setNetWorth(1000);
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					double amount = 0.001;
					BQCurrencyType currency = bqCurrencyTypeService.getByTypeId(record.getOri_currency_type_id());
					if (currency.getType_name().equalsIgnoreCase("usdt") || currency.getType_name().equalsIgnoreCase("usdr")) {
						amount = 1;
					}
					
					if (Math.abs(allBalance.getNetWorth()) > amount) {
						try {
							bqExchangeRecordService.manualRecord(record, "净值不为0，转人工处理。");
						} catch (ServiceException e) {
							e.printStackTrace();
						}
					} else if (generalBalance.getManualRecharge() > 0) {
						try {
							bqExchangeRecordService.manualRecord(record, "人工充值大于0，转人工处理。");
						} catch (ServiceException e) {
							e.printStackTrace();
						}
					} else {
						checked = true;
					}
				} else if (oldState == Contents.EXCHANGE_STATE_CHECKED) {
					checked = true;
				}
				
				if (checked) {
					String symbol = record.getDirection() == Contents.DIRECTION_BUY ? record.getTarget_currency_name() : record.getOri_currency_name();
					if (setting.getInside_exchange()) {
						try {
							BigDecimal dealCash = BigDecimal.ZERO;
							if (record.getDirection() == Contents.DIRECTION_BUY) {
								dealCash = record.getTarget_amount().multiply(record.getOrder_price());
							} else {
								dealCash = record.getOri_amount().multiply(record.getOrder_price());
							}
							bqExchangeRecordService.confirmRecord(record, record.getOri_amount(), record.getTarget_amount(), dealCash, 
									setting.getSymbol() + "/USDT互兑自成交，成交量：" + record.getOri_amount().doubleValue());
						} catch (ServiceException e) {
							e.printStackTrace();
						}
					} else {
						exchangeOrder(apiUtil, symbol, record);
					}
				}
			}
		}
		
	}
}
