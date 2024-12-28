package com.btc100.btc_q.timerTask;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.service.NContractAccountLogService;
import com.btc100.btc_q.service.NMemberTradeDetailService;
import com.btc100.btc_q.service.NPendingLogService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.util.StringUtil;

/**
 * 备份合约交易相关数据表(30天前的数据)
 * 
 * @author Administrator
 * 
 */
public class NTimerTaskBakContractTable {

	@Autowired
	private NContractAccountLogService contractAccountLogService;
	@Autowired
	private NMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private NPendingLogService pendingLogService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private NTransactionService nTransactionService;

	private static String[] contractArray = null;
	private static int quantuid = -1;
	
	public void run() {

		long currHourMillis = System.currentTimeMillis() / 1000 / 3600 * 3600;
		long bakEndTime = currHourMillis - 90 * 24 * 3600L;

		doContractBak(bakEndTime);
	}

	private void doContractBak(long endTime) {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (quantuid == -1) {
			String uid = UrlConfig.getValue("openPosition.quantuid");
			if (StringUtil.isEmpty(uid)) {
				quantuid = 0;
			} else {
				quantuid = Convert.toInt(uid, 0);
			}
		}

		if (contractArray == null || contractArray.length == 0)
			return;

		for (String pContract : contractArray) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}
			
			NTransaction bq = nTransactionService.findTransByCode(partition, contract);
			if (bq == null) continue;

			String code = bq.getTransaction_code(); // 获取合约号

			try {
				// 随机休眠0~2分钟
				int first = 0, second = 2 * 1000 * 60;
				Thread.sleep((first + (int) (Math.random() * (second - first))));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			if (quantuid > 0) {
				long yesterday = System.currentTimeMillis() / 1000 - 3600;
				pendingLogService.deleteByUidBeforeTime(partition, contract, quantuid, yesterday, 4);
			}

			contractAccountLogService.createAccountLogBak(partition, code);
			memberTradeDetailService.createNMemberTradeDetailBak(partition, code);
			pendingLogService.createPendingLogBak(partition, code);
			tradeLogService.createTradeLogBak(partition, code);

			contractAccountLogService.insertAccountLogBak(partition, code, endTime);
			memberTradeDetailService.insertNMemberTradeDetailBak(partition, code, endTime);
			pendingLogService.insertPendingLogBak(partition, code, endTime);
			tradeLogService.insertTradeLogBak(partition, code, endTime);
		}
	}
}
