package com.btc100.btc_q.timerTask;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.VContractAccountLogService;
import com.btc100.btc_q.service.VMemberTradeDetailService;
import com.btc100.btc_q.service.VPendingLogService;
import com.btc100.btc_q.service.VTradeLogService;

/**
 * 备份模拟交易相关数据表(30天前的数据)
 * 
 * @author Administrator
 * 
 */
public class TimerTaskBakVTable extends BaseTask {

	@Autowired
	private VContractAccountLogService vAccountLogService;
	@Autowired
	private VMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private VPendingLogService pendingLogService;
	@Autowired
	private VTradeLogService tradeLogService;

	public void run() {

		long currHourMillis = System.currentTimeMillis() / 1000 / 3600 * 3600;
		long bakEndTime = currHourMillis - 90 * 24 * 3600L;

		doContractBak(bakEndTime);
	}

	private void doContractBak(long endTime) {

		List<VTransaction> vTransactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vTransactionList == null) {
			vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST,
					vTransactionList);
		}

		for (VTransaction vTransaction : vTransactionList) {
			String code = vTransaction.getTransaction_code();

			try {
				// 随机休眠0~2分钟
				int first = 0, second = 2 * 1000 * 60;
				Thread.sleep((first + (int) (Math.random() * (second - first))));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			vAccountLogService.createAccountLogBak(code);
			memberTradeDetailService.createMemberTradeDetailBak(code);
			pendingLogService.createPendingLogBak(code);
			tradeLogService.createTradeLogBak(code);

			vAccountLogService.insertAccountLogBak(code, endTime);
			memberTradeDetailService.insertMemberTradeDetailBak(code, endTime);
			pendingLogService.insertPendingLogBak(code, endTime);
			tradeLogService.insertTradeLogBak(code, endTime);
		}
	}
}
