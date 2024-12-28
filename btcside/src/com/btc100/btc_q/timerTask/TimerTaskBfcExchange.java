package com.btc100.btc_q.timerTask;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQExchangeBfcRecord;
import com.btc100.btc_q.entity.BQExchangeBfcSetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQExchangeBfcRecordService;
import com.btc100.btc_q.service.BQExchangeBfcSettingService;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.vo.BalanceAccountVO;

public class TimerTaskBfcExchange {
	@Autowired
	private BQExchangeBfcRecordService bqExchangeBfcRecordService;
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private BQExchangeBfcSettingService bqExchangeBfcSettingService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	

	public void run() {

		// 兑换
		// 1、获取正在处理的记录，查询订单状态---
		List<BQExchangeBfcRecord> processingRecords = bqExchangeBfcRecordService
				.findByState(Contents.EXCHANGE_STATE_PROCESSING, 100);
		for (BQExchangeBfcRecord record : processingRecords) {
			try {
				bqExchangeBfcRecordService.confirmRecord(record, "自动处理，兑换成功");
			} catch (ServiceException e) {
				try {
					bqExchangeBfcRecordService.manualRecord(record, "自动处理，兑换失败，转人工");
				} catch (ServiceException e1) {
					e1.printStackTrace();
				}
			}
		}

		// 2、处理刚提交或者审核的兑换记录
		List<BQExchangeBfcSetting> settings = bqExchangeBfcSettingService.findAll();
		for (BQExchangeBfcSetting setting : settings) {
			if (!setting.getRunning())
				continue;

			List<BQExchangeBfcRecord> exchangeRecords = bqExchangeBfcRecordService.findSubmitAndChecked(3,
					setting.getCurrency_type_id());
			for (BQExchangeBfcRecord record : exchangeRecords) {
				try {
					bqExchangeBfcRecordService.processRecord(record);
				} catch (ServiceException e) {
					e.printStackTrace();
					// 转人工
					try {
						bqExchangeBfcRecordService.manualRecord(record, "自动处理，改变记录状态失败！");
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
						bqMemberRechargeService.getBalanceAccount(record.getF_user_id(),
								record.getOri_currency_type_id(), allBalance, generalBalance, contractBalances, deliveryContractBalances);
					} catch (ParseException e1) {
						allBalance.setNetWorth(1000);
						e1.printStackTrace();
					}
					
					double amount = 0.001;
					BQCurrencyType currency = bqCurrencyTypeService.getByTypeId(record.getOri_currency_type_id());
					if (currency.getType_name().equalsIgnoreCase("usdt") || currency.getType_name().equalsIgnoreCase("usdr")) {
						amount = 1;
					}
					
					if (Math.abs(allBalance.getNetWorth()) > amount) {
						try {
							bqExchangeBfcRecordService.manualRecord(record, "净值不为0，转人工处理。");
						} catch (ServiceException e) {
							e.printStackTrace();
						}
					} else if (generalBalance.getManualRecharge() > 0) {
						try {
							bqExchangeBfcRecordService.manualRecord(record, "人工充值大于0，转人工处理。");
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
					try {
						bqExchangeBfcRecordService.confirmRecord(record, "自动处理，兑换成功");
					} catch (ServiceException e) {
						e.printStackTrace();
						try {
							bqExchangeBfcRecordService.manualRecord(record, "自动处理，兑换失败，转人工");
						} catch (ServiceException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}

	}
}
