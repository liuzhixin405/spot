package com.btc100.btc_q.timerTask;

import java.util.List;

import com.btc100.btc_q.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryContractAccountLogService;
import com.btc100.btc_q.service.DeliveryMemberTradeDetailService;
import com.btc100.btc_q.service.DeliveryPendingLogService;
import com.btc100.btc_q.service.DeliveryTradeLogService;

/**
 * 备份交割合约相关数据表(30天前的数据)
 * 
 * @author Administrator
 * 
 */
public class TimerTaskBakDTable extends BaseTask {

	@Autowired
	private DeliveryContractAccountLogService dAccountLogService;
	@Autowired
	private DeliveryMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private DeliveryPendingLogService pendingLogService;
	@Autowired
	private DeliveryTradeLogService tradeLogService;

	public static String[] contractArray = null;

	public void run() {

		long currHourMillis = System.currentTimeMillis() / 1000 / 3600 * 3600;
		long bakEndTime = currHourMillis - 90 * 24 * 3600L;

		doContractBak(bakEndTime);
	}

	private void doContractBak(long endTime) {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("deliveryTrade.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}

		if (contractArray == null || contractArray.length == 0)
			return;
		
		List<DeliveryTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		if (lstTrads == null || lstTrads.size() == 0) return;
		
		for (String contract : contractArray) {
			
			String[] codeDelivery = contract.split("_");
			if (codeDelivery.length == 0) {
				continue;
			}
			String typeCode = codeDelivery[0];
			String deliveryType = codeDelivery[1];
			
			DeliveryTransaction transaction = null;
			for (DeliveryTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_type_code().equals(typeCode) && bqTransaction.getDelivery_type().equals(deliveryType)) {
					transaction = bqTransaction;
					break;
				}
			}

			if (transaction == null) {
				continue;
			}
			
			if (transaction.getTransaction_state() != 1) {
				continue;
			}
			
			String code = transaction.getTransaction_code();

			try {
				// 随机休眠0~2分钟
				int first = 0, second = 2 * 1000 * 60;
				Thread.sleep((first + (int) (Math.random() * (second - first))));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			dAccountLogService.createAccountLogBak(code);
			memberTradeDetailService.createDeliveryMemberTradeDetailBak(code);
			pendingLogService.createDeliveryPendingLogBak(code);
			tradeLogService.createDeliveryTradeLogBak(code);

			dAccountLogService.insertAccountLogBak(code, endTime);
			memberTradeDetailService.insertDeliveryMemberTradeDetailBak(code, endTime);
			pendingLogService.insertDeliveryPendingLogBak(code, endTime);
			tradeLogService.insertDeliveryTradeLogBak(code, endTime);
		}
	}
}
