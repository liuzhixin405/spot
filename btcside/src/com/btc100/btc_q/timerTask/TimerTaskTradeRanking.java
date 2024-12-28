package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteRebateReconcilia;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.entity.TradeLogIncomeReconcilia;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMemberInviteRebateReconciliaService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.TradeLogIncomeReconciliaService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.vo.RebateRankingVo;

public class TimerTaskTradeRanking {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private BQMemberInviteRebateReconciliaService bqMemberInviteRebateReconciliaService;
	@Autowired
	private VArbitrageSettingService vArbitrageSettingService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private TradeLogIncomeReconciliaService tradeLogIncomeReconciliaService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;

	public void run() {
		doRebateRanking();

		List<VTransaction> vlstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vlstTrads == null) {
			vlstTrads = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		doIncomeRanking(vlstTrads);

		try {
			List<BQTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
			if (lstTrads == null) {
				lstTrads = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			}
			
			getOneDayTrades(lstTrads);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getOneDayTrades(List<BQTransaction> lstTrads) throws JSONException {
		BigDecimal oneDayTrades = BigDecimal.ZERO;// 24小时所有合约成交额

		for (BQTransaction bqTransaction : lstTrads) {
			String code = bqTransaction.getTransaction_code();
			String str = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + code);
			if (str == null || str.isEmpty()) {
				continue;
			}

			JSONObject obj = new JSONObject(str);
			BigDecimal trades = new BigDecimal(obj.getString("trades"));
			oneDayTrades = oneDayTrades.add(trades);
		}
		spyMemcachedClient.set(BTCQMemecacheKeys.ONE_DAY_TRADE_AMOUNT, BTCQMemecacheKeys.TIMES.ONE_DAY, oneDayTrades);
	}

	private void doRebateRanking() {
		List<RebateRankingVo> voList = new ArrayList<RebateRankingVo>();

		Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.REBATE_RANKING_LAST_TIME);
		Long currentTime = bqMemberInviteRebateReconciliaService.getTime();
		BQMemberInviteRebateReconcilia reconcilia = bqMemberInviteRebateReconciliaService.get();
		if (reconcilia == null) {
			List<Object[]> list = bqMemberInviteRebateLogService.findByTime(0, currentTime, Contents.TRADE_CURRENCY_TYPE_ID);
			for (int i = 0; i < list.size(); i++) {

				if (i > 0 && i % 100 == 0) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				Object[] obj = list.get(i);
				BQMemberInviteRebateReconcilia re = new BQMemberInviteRebateReconcilia();
				long userId = (Integer) obj[0];
				BigDecimal rebate = (BigDecimal) obj[2];
				re.setF_user_id(userId);
				re.setF_user_email(obj[1].toString());
				re.setRebate(rebate);
				re.setCut_off_time(currentTime);
				bqMemberInviteRebateReconciliaService.add(re);
			}

			spyMemcachedClient.set(BTCQMemecacheKeys.REBATE_RANKING_LAST_TIME, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY,
					currentTime);

		} else {
			if (lastTime == null) {
				lastTime = currentTime - 300L;
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.REBATE_RANKING_LAST_TIME, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY,
					currentTime);
			
			List<Object[]> objList = bqMemberInviteRebateLogService.findByTime(lastTime, currentTime, Contents.TRADE_CURRENCY_TYPE_ID);
			for (Object[] obj : objList) {
				long userId = (Integer) obj[0];
				BigDecimal rebate = (BigDecimal) obj[2];
				BQMemberInviteRebateReconcilia rec = bqMemberInviteRebateReconciliaService.getByUserId(userId);
				if (rec != null) {
					rec.setRebate(rec.getRebate().add(rebate));
					bqMemberInviteRebateReconciliaService.update(rec);
				} else {
					BQMemberInviteRebateReconcilia re = new BQMemberInviteRebateReconcilia();
					re.setF_user_id(userId);
					re.setRebate(rebate);
					re.setF_user_email(obj[1].toString());
					re.setCut_off_time(currentTime);
					bqMemberInviteRebateReconciliaService.add(re);
				}
			}

		}
		
		List<BQMemberInviteRebateReconcilia> list = bqMemberInviteRebateReconciliaService.findAll(6);
		for (BQMemberInviteRebateReconcilia log : list) {
			if (log.getF_user_id() == 6383) {
				continue;
			}
			if (voList.size() >= 5) {
				break;
			}
			RebateRankingVo vo = new RebateRankingVo();
			vo.setUserId(log.getF_user_id());
			vo.setRebate(log.getRebate().setScale(4, RoundingMode.DOWN).doubleValue());
			vo.setUserName(formatEmail(log.getF_user_email()));
			voList.add(vo);
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.REBATE_RANKING, BTCQMemecacheKeys.TIMES.ONE_DAY, voList);
	}

	private void doIncomeRanking(List<VTransaction> lstTrads) {
		List<RebateRankingVo> voList = new ArrayList<RebateRankingVo>();

		List<TradeLog> allTradeLogs = new ArrayList<TradeLog>();
		Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.INCOME_RANKING_LAST_TIME);
		Long currentTime = tradeLogIncomeReconciliaService.getTime();
		TradeLogIncomeReconcilia reconcilia = tradeLogIncomeReconciliaService.get();
		if (reconcilia == null) {
			getIncomeList(allTradeLogs, lstTrads, 0L, currentTime);

			for (int i = 0; i < allTradeLogs.size(); i++) {
				if (i > 0 && i % 100 == 0) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				TradeLog log = allTradeLogs.get(i);
				TradeLogIncomeReconcilia obj = new TradeLogIncomeReconcilia();
				obj.setF_user_id(log.getF_user_id());
				obj.setIncome(log.getTrade_income());
				obj.setCut_off_time(currentTime);
				tradeLogIncomeReconciliaService.add(obj);
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.INCOME_RANKING_LAST_TIME, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY,
					currentTime);

		} else {
			if (lastTime == null) {
				lastTime = currentTime - 300L;
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.INCOME_RANKING_LAST_TIME, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY,
					currentTime);

			getIncomeList(allTradeLogs, lstTrads, lastTime, currentTime);
			for (TradeLog log : allTradeLogs) {
				TradeLogIncomeReconcilia rec = tradeLogIncomeReconciliaService.getByUserId(log.getF_user_id());
				if (rec != null) {
					rec.setIncome(rec.getIncome().add(log.getTrade_income()));
					tradeLogIncomeReconciliaService.update(rec);
				} else {
					TradeLogIncomeReconcilia obj = new TradeLogIncomeReconcilia();
					obj.setF_user_id(log.getF_user_id());
					obj.setIncome(log.getTrade_income());
					obj.setCut_off_time(currentTime);
					tradeLogIncomeReconciliaService.add(obj);
				}
			}
		}

		List<TradeLogIncomeReconcilia> list = tradeLogIncomeReconciliaService.findAll(5);
		for (TradeLogIncomeReconcilia log : list) {
			RebateRankingVo vo = new RebateRankingVo();
			vo.setUserId(log.getF_user_id());
			BQMember member = bqMemberService.findUserById(log.getF_user_id());
			if (member == null) continue;
			vo.setUserName(formatEmail(member.getF_user_email()));
			vo.setRebate(log.getIncome().setScale(4, RoundingMode.DOWN).doubleValue());
			voList.add(vo);
		}

		spyMemcachedClient.set(BTCQMemecacheKeys.INCOME_RANKING, BTCQMemecacheKeys.TIMES.ONE_DAY, voList);
	}

	private void getIncomeList(List<TradeLog> allTradeLogs, List<VTransaction> lstTrads, Long startTime,
			Long endTime) {
		for (VTransaction bqTransaction : lstTrads) {
			String code = bqTransaction.getTransaction_code();
			VArbitrageSetting setting = vArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new VArbitrageSetting();
				setting.setDepth_uid(0L);
			}

			List<Object[]> tradeLogs = vTradeLogService.findIncome(code, startTime, endTime, setting.getDepth_uid());
			List<TradeLog> needAppend = new ArrayList<TradeLog>();
			for (Object[] obj : tradeLogs) {
				if (obj[0] == null)
					continue;
				boolean have = false;
				long uid = (Integer) obj[0];
				for (TradeLog tradeLog : allTradeLogs) {
					if (uid == tradeLog.getF_user_id()) {
						tradeLog.setTrade_income(((BigDecimal) obj[1]).add(tradeLog.getTrade_income()));
						have = true;
						break;
					}
				}

				if (!have) {
					TradeLog log = new TradeLog();
					log.setF_user_id((Integer) obj[0]);
					log.setTrade_income((BigDecimal) obj[1]);
					needAppend.add(log);
				}
			}

			allTradeLogs.addAll(needAppend);
		}
	}

	private String formatEmail(String email) {
		String[] emails = email.split("@");
		if (emails.length > 1) {
			String name = emails[0];
			if (name.length() < 4) {
				email = name + "****@" + emails[1];
			} else {
				email = name.substring(0, 3) + "****" + name.substring(name.length() - 1, name.length()) + "@" + emails[1];
			}
		} else {
			if (email.length() < 4) {
				email = email.substring(0, 1) + "****" + email.substring(email.length() - 1, email.length());
			} else {
				email = email.substring(0, 3) + "****" + email.substring(email.length() - 1, email.length());
			}
		}

		return email;
	}

}
