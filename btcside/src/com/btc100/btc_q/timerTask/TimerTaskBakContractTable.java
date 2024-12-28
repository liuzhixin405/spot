package com.btc100.btc_q.timerTask;

import java.util.List;

import com.btc100.btc_q.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.ContractAccountLogService;
import com.btc100.btc_q.service.MemberTradeDetailService;
import com.btc100.btc_q.service.PendingLogService;
import com.btc100.btc_q.service.TradeLogService;

/**
 * 备份合约交易相关数据表(30天前的数据)
 * 
 * @author Administrator
 * 
 */
public class TimerTaskBakContractTable extends BaseTask {

	@Autowired
	private ContractAccountLogService contractAccountLogService;
	@Autowired
	private MemberTradeDetailService memberTradeDetailService;
	@Autowired
	private PendingLogService pendingLogService;
	@Autowired
	private TradeLogService tradeLogService;

	private static String[] contractArray = null;

	public void run() {

		long currHourMillis = System.currentTimeMillis() / 1000 / 3600 * 3600;
		long bakEndTime = currHourMillis - 90 * 24 * 3600L;

		doContractBak(bakEndTime);
	}

	private void doContractBak(long endTime) {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}

		if (contractArray == null || contractArray.length == 0)
			return;

		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST,
					transactionList);
		}
		// 遍历在用合约，找出合约号
		for (String contract : contractArray) {
			BQTransaction bq = null;
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equals(contract)) {
					bq = bqTransaction;
					break;
				}
			}

			if (bq == null) {
				continue;
			}

			String code = bq.getTransaction_code(); // 获取合约号

			try {
				// 随机休眠0~2分钟
				int first = 0, second = 2 * 1000 * 60;
				Thread.sleep((first + (int) (Math.random() * (second - first))));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			contractAccountLogService.createAccountLogBak(code);
			memberTradeDetailService.createMemberTradeDetailBak(code);
			pendingLogService.createPendingLogBak(code);
			tradeLogService.createTradeLogBak(code);

			contractAccountLogService.insertAccountLogBak(code, endTime);
			memberTradeDetailService.insertMemberTradeDetailBak(code, endTime);
			pendingLogService.insertPendingLogBak(code, endTime);
			tradeLogService.insertTradeLogBak(code, endTime);
		}
	}
}
