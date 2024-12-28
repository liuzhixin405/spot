package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.DATA_CreditNumber;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQCommonCreditLogService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.TradeLogService;

public class TimerTaskStatisticsCredit {
	
	private static final Logger logger = Logger.getLogger(TimerTaskStatisticsCredit.class);

	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQCommonCreditLogService bqCommonCreditLogService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private BQMemberService bqMemberService;
	
	public void run() {
		try {
			List<BQTransaction> lstTrads = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			time.set(Calendar.HOUR_OF_DAY, 0);
			time.set(Calendar.MINUTE, 0);
			time.set(Calendar.SECOND, 0);
			time.set(Calendar.MILLISECOND, 0);
			
			long endTime = time.getTimeInMillis() / 1000;
			time.add(Calendar.DATE, -1);
			long beginTime = time.getTimeInMillis() / 1000;
			
			for (BQTransaction trans : lstTrads) {
				long depthUid = 0;
				BQArbitrageSetting setting = bqArbitrageSettingService.getArbitrageSettingByCode(trans.getTransaction_code());
				if (setting != null) {
					depthUid = setting.getDepth_uid();
				}
				List<TradeLog> tradeLogs = tradeLogService.findUserTurnoverBetweenTime(trans.getTransaction_code(), beginTime, endTime, depthUid, 8670); // 量化用户8670
				for (TradeLog trade : tradeLogs) {
					BQMember user = bqMemberService.findUserById(trade.getF_user_id());
					if (user == null) {
						continue;
					}
					long points = trade.getTrade_count().divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_UP).longValue();
					if (points > 0 && user.getF_user_oms() == 0) {
						BQCommonCreditLog creditLog = new BQCommonCreditLog();
						creditLog.setF_user_id(trade.getF_user_id());
						creditLog.setCredit_type_id(Contents.CREDIT_TYPE_TRADE);
						creditLog.setCredits(points);
						creditLog.setTitle("每日开仓");
						creditLog.setDateline(bqCommonCreditLogService.getTime());
						
						BQMemberLevel level = bqMemberLevelService.findByUserId(creditLog.getF_user_id());
						bqCommonCreditLogService.saveCreditsAndUpdateLevel(creditLog, level.getState() == 1, 1);
					}
					
					if (bqCommonCreditLogService.count(trade.getF_user_id(), Contents.CREDIT_TYPE_FIRST_TRADE) == 0 && user.getF_user_oms() == 0) {
						BQCommonCreditLog creditLog = new BQCommonCreditLog();
						creditLog.setF_user_id(trade.getF_user_id());
						creditLog.setCredit_type_id(Contents.CREDIT_TYPE_FIRST_TRADE);
						creditLog.setCredits(DATA_CreditNumber.CREDIT_ONE_FIRST_TRADE);
						creditLog.setTitle("首次交易");
						creditLog.setDateline(bqCommonCreditLogService.getTime());
						
						BQMemberLevel level = bqMemberLevelService.findByUserId(creditLog.getF_user_id());
						bqCommonCreditLogService.saveCreditsAndUpdateLevel(creditLog, level.getState() == 1, 1);
					}
				}
				
				List<BQAccountContract> accounts = bqAccountContractService.findGreaterThan100(trans.getTransaction_code());
				for (BQAccountContract account : accounts) {
					long points = account.getF_btc().divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_DOWN).longValue();
					
					BQCommonCreditLog creditLog = new BQCommonCreditLog();
					creditLog.setF_user_id(account.getF_user_id());
					creditLog.setCredit_type_id(Contents.CREDIT_TYPE_ACCOUNT_BALANCE);
					creditLog.setCredits(points);
					creditLog.setTitle("每日账户留存-" + trans.getTransaction_code());
					creditLog.setDateline(bqCommonCreditLogService.getTime());
					
					BQMemberLevel level = bqMemberLevelService.findByUserId(creditLog.getF_user_id());
					bqCommonCreditLogService.saveCreditsAndUpdateLevel(creditLog, level.getState() == 1, 1);
				}
			}
			
			List<BQAccount> accounts = bqAccountService.findGreaterThan1000(Contents.TRADE_CURRENCY_TYPE_ID);
			for (BQAccount account : accounts) {
				long points = account.getBtc().divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_DOWN).longValue();
				
				BQCommonCreditLog creditLog = new BQCommonCreditLog();
				creditLog.setF_user_id(account.getF_user_id());
				creditLog.setCredit_type_id(Contents.CREDIT_TYPE_ACCOUNT_BALANCE);
				creditLog.setCredits(points);
				creditLog.setTitle("每日账户留存-总账户");
				creditLog.setDateline(bqCommonCreditLogService.getTime());
				
				BQMemberLevel level = bqMemberLevelService.findByUserId(creditLog.getF_user_id());
				bqCommonCreditLogService.saveCreditsAndUpdateLevel(creditLog, level.getState() == 1, 1);
			}
		} catch (Exception e) {
			logger.info("每日统计积分异常。。。。");
			e.printStackTrace();
		}
	}
}
