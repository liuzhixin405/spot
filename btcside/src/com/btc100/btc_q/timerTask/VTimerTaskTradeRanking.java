package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VTradeDayStats;
import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.entity.VTradeMonthStats;
import com.btc100.btc_q.entity.VTradeWeekStats;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VTradeDayStatsService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTradeMonthStatsService;
import com.btc100.btc_q.service.VTradeWeekStatsService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.vo.VTradeStatsVo;

public class VTimerTaskTradeRanking {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private VTradeWeekStatsService vTradeWeekStatsService;
	@Autowired
	private VTradeMonthStatsService vTradeMonthStatsService;
	@Autowired
	private VTradeDayStatsService vTradeDayStatsService;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VArbitrageSettingService vArbitrageSettingService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	
	public void run() {
		
		List<VTransaction> vTrans = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vTrans == null) {
			vTrans = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		long currentTimeMillis = System.currentTimeMillis() / 1000;
		long typeTime = 300L;
		long endTime = currentTimeMillis / typeTime * typeTime;
		long startTime = 1565334000; // 2019/8/9 15:0:0
		
		// 持仓收益
		List<Long> uids = new ArrayList<Long>();
		List<VTradeLog> allTradeLogs = new ArrayList<VTradeLog>();
//		List<VTradeLog> allTradeLogs = getMemberTransctionInome(vTrans);
//		for (VTradeLog log : allTradeLogs) {
//			uids.add(log.getF_user_id());
//		}
		
		// 所有收益
//		getTradeInome(vTrans, allTradeLogs, startTime, endTime);
		
		// 上个天时间点
//		Calendar dayCal = Calendar.getInstance();
//		dayCal.setTimeInMillis(endTime * 1000);
//		
//		// 当天15点
//		dayCal.set(Calendar.HOUR_OF_DAY, 15);
//		dayCal.set(Calendar.MINUTE, 0);
//		dayCal.set(Calendar.SECOND, 0);
//		dayCal.set(Calendar.MILLISECOND, 0);
//		
//		long dayEndTime = dayCal.getTimeInMillis() / 1000;
//		if (endTime > dayEndTime) {		// 没有超过当天15点，前一天
//			dayCal.add(Calendar.DATE, 1);
//			dayEndTime = dayCal.getTimeInMillis() / 1000;
//		}
//		doRanking(startTime, dayEndTime, 0, allTradeLogs, vTrans, uids);	// 日榜
		
		// 上个周时间点
		Calendar weekCal = Calendar.getInstance();
		weekCal.setTimeInMillis(endTime * 1000);
		weekCal.set(Calendar.HOUR_OF_DAY, 15);
		weekCal.set(Calendar.MINUTE, 0);
		weekCal.set(Calendar.SECOND, 0);
		weekCal.set(Calendar.MILLISECOND, 0);
		weekCal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		long weekEndTime = weekCal.getTimeInMillis() / 1000;
		if (endTime > weekEndTime) {
			weekCal.add(Calendar.DATE, 7);
			weekEndTime = weekCal.getTimeInMillis() / 1000; 
		}
//		doRanking(startTime, weekEndTime, 1, allTradeLogs, vTrans, uids);	// 周榜
		doRanking(weekEndTime - 7 * 24 * 3600, weekEndTime, 1, getTradeInome(vTrans, allTradeLogs, weekEndTime - 7 * 24 * 3600, weekEndTime), vTrans, uids);	// 周榜
		
		// 上个月时间点
//		Calendar monthCal = Calendar.getInstance();
//		monthCal.setTimeInMillis(endTime * 1000);
//		monthCal.set(Calendar.HOUR_OF_DAY, 15);
//		monthCal.set(Calendar.MINUTE, 0);
//		monthCal.set(Calendar.SECOND, 0);
//		monthCal.set(Calendar.MILLISECOND, 0);
//		monthCal.set(Calendar.DAY_OF_MONTH, monthCal.getActualMaximum(Calendar.DAY_OF_MONTH));
//		long monthEndTime = monthCal.getTimeInMillis() / 1000;
//		if (endTime > monthEndTime) {
//			monthCal.add(Calendar.MONTH, 1);
//			monthCal.set(Calendar.DAY_OF_MONTH, monthCal.getActualMaximum(Calendar.DAY_OF_MONTH));
//			monthEndTime = monthCal.getTimeInMillis() / 1000; 
//		}
		
		long monthEndTime = 1567753200; // 2019-9-6 15:0:0
		
		allTradeLogs = new ArrayList<VTradeLog>();
		doRanking(startTime, monthEndTime, 2, getTradeInome(vTrans, allTradeLogs, startTime, monthEndTime), vTrans, uids);	// 月榜
	}
	
	private List<VTradeLog> getTradeInome(List<VTransaction> vTrans, List<VTradeLog> allTradeLogs, long startTime, long endTime) {
		
		for (VTransaction vTransaction : vTrans) {
			String code = vTransaction.getTransaction_code();
			
			long depthUid = 0;
			VArbitrageSetting setting  = vArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting != null) {
				depthUid = setting.getDepth_uid();
			}
			
			List<Object[]> tradeLogs = vTradeLogService.getIncomeAndDepoistByUser(code, depthUid, startTime, endTime);
			List<VTradeLog> needAppend = new ArrayList<VTradeLog>();
			for (Object[] obj : tradeLogs) {
				boolean have = false;
				long uid = (Integer) obj[0];
				for (VTradeLog tradeLog : allTradeLogs) {
					if (uid == tradeLog.getF_user_id()) {
						tradeLog.setTrade_deposit(((BigDecimal)obj[2]).add(tradeLog.getTrade_deposit()));
						tradeLog.setTrade_income(((BigDecimal)obj[1]).add(tradeLog.getTrade_income()));
						have = true;
						break;
					}
				}
				
				if (!have) {
					VTradeLog log = new VTradeLog();
					log.setF_user_id((Integer)obj[0]);
					log.setTrade_income((BigDecimal)obj[1]);
					log.setTrade_deposit((BigDecimal)obj[2]);
					needAppend.add(log);
				}
			}
			
			allTradeLogs.addAll(needAppend);
		}
		
		return allTradeLogs;
	}
	
	@SuppressWarnings("unused")
	private List<VTradeLog> getMemberTransctionInome(List<VTransaction> vTrans) {
		List<VTradeLog> allTradeLogs = new ArrayList<VTradeLog>();
		for (VTransaction vTransaction : vTrans) {
			String code = vTransaction.getTransaction_code();
			
			long depthUid = 0;
			VArbitrageSetting setting  = vArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting != null) {
				depthUid = setting.getDepth_uid();
			}
			
			BigDecimal price = spyMemcachedClient.get(vTransaction.getTransaction_code() + BTCQMemecacheKeys.V_BTCLATESTPRICE);
			if (price == null) {
				price = vTransactionService.findTransByCode(vTransaction.getTransaction_code()).getLatest_transaction_price();
			}
			
			if (price == null) continue;
			
			// 持仓
			List<Object[]> tradeLogs = vMemberTransactionService.findSumIncomeAndDeposit(code, depthUid, price);
			for (Object[] obj : tradeLogs) {
				boolean have = false;
				long uid = (Integer) obj[0];
				for (VTradeLog tradeLog : allTradeLogs) {
					if (uid == tradeLog.getF_user_id()) {
						tradeLog.setTrade_deposit(((BigDecimal)obj[2]).add(tradeLog.getTrade_deposit()));
						tradeLog.setTrade_income(((BigDecimal)obj[1]).add(tradeLog.getTrade_income()));
						have = true;
						break;
					}
				}
				
				if (!have) {
					VTradeLog log = new VTradeLog();
					log.setF_user_id((Integer)obj[0]);
					log.setTrade_income((BigDecimal)obj[1]);
					log.setTrade_deposit((BigDecimal)obj[2]);
					allTradeLogs.add(log);
				}
			}
		}
		
		return allTradeLogs;
	}
	
	private void doRanking(long begin, long time, int cycleType, List<VTradeLog> allTradeLogs, List<VTransaction> vTrans, List<Long> uids) {
		
//		List<Integer> tradeUids = new ArrayList<Integer>();
//		if (cycleType != 2) {
//			Calendar cal = Calendar.getInstance();
//			cal.setTimeInMillis(time * 1000);
//			
//			long startTime = 0;
//			if (cycleType == 0) {
//				cal.add(Calendar.DATE, -1);
//				startTime = cal.getTimeInMillis() / 1000;
//			} else {
//				cal.add(Calendar.DATE, -7);
//				startTime = cal.getTimeInMillis() / 1000;
//			}
//			
//			if (startTime < begin) {
//				startTime = begin;
//			}
//			
//			for (VTransaction vTransaction : vTrans) {
//				String code = vTransaction.getTransaction_code();
//				
//				long depthUid = 0;
//				VArbitrageSetting setting  = vArbitrageSettingService.getArbitrageSettingByCode(code);
//				if (setting != null) {
//					depthUid = setting.getDepth_uid();
//				}
//				
//				tradeUids.addAll(vTradeLogService.getTradeUserIds(code, startTime, time, depthUid));
//			}
//		}
		
		List<VTradeStatsVo> allVts = new ArrayList<VTradeStatsVo>();
		for (VTradeLog tradeLog : allTradeLogs) {
//			if (cycleType != 2) {
//				boolean have = false;
//				for (Long uid : uids) {
//					if (uid.longValue() == tradeLog.getF_user_id()) {
//						have = true;
//						break;
//					}
//				}
//				
//				if (!have) {
//					for (Integer uid : tradeUids) {
//						if (uid.longValue() == tradeLog.getF_user_id()) {
//							have = true;
//							break;
//						}
//					}
//				}
//				
//				if (!have) {
//					continue;
//				}
//			}
	    	
			BQMember member = bqMemberService.findUserById(tradeLog.getF_user_id());
			if (member == null) continue;
	    	String email = member.getF_user_email();
	    	String[] emails = email.split("@");
	    	if (emails.length > 1) {
		    	String name = emails[0];
		    	if (name.length() < 4 ) {
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
	    	
	    	VTradeStatsVo statsVo = new VTradeStatsVo();
	    	statsVo.setUserId(tradeLog.getF_user_id());
	    	statsVo.setUserName(email);
	    	statsVo.setSumIncome(tradeLog.getTrade_income().doubleValue());
	    	statsVo.setSumDepoist(tradeLog.getTrade_deposit().doubleValue());
	    	
	    	BigDecimal rate = tradeLog.getTrade_income();
	    	
	    	if (begin > 0) {
		    	if (cycleType == 1) {
//		    		VTradeWeekStats lastStats = vTradeWeekStatsService.getLastBeforeTimeByUserId(tradeLog.getF_user_id(), time);
//		    		if (lastStats != null) {
//		    			rate = tradeLog.getTrade_income().subtract(lastStats.getSum_income());
//		    		}
		    		
			    	VTradeWeekStats stats = new VTradeWeekStats();
			    	stats.setUser_id(tradeLog.getF_user_id());
			    	stats.setRecord_time(time);
			    	stats.setSum_depoist(tradeLog.getTrade_deposit());
			    	stats.setSum_income(tradeLog.getTrade_income());
			    	stats.setUser_name(email);
			    	stats.setYield_rate(rate);
			    	
			    	vTradeWeekStatsService.saveOrUpdate(stats);
		    	} else if (cycleType == 2) {
		    		VTradeMonthStats stats = new VTradeMonthStats();
			    	stats.setUser_id(tradeLog.getF_user_id());
			    	stats.setRecord_time(time);
			    	stats.setSum_depoist(tradeLog.getTrade_deposit());
			    	stats.setSum_income(tradeLog.getTrade_income());
			    	stats.setUser_name(member.getF_user_email());
			    	stats.setYield_rate(rate);
			    	
			    	vTradeMonthStatsService.saveOrUpdate(stats);
		    	} else {
		    		VTradeDayStats lastStats = vTradeDayStatsService.getLastBeforeTimeByUserId(tradeLog.getF_user_id(), time);
		    		if (lastStats != null) {
		    			rate = tradeLog.getTrade_income().subtract(lastStats.getSum_income());
		    		}
		    		
		    		VTradeDayStats stats = new VTradeDayStats();
			    	stats.setUser_id(tradeLog.getF_user_id());
			    	stats.setRecord_time(time);
			    	stats.setSum_depoist(tradeLog.getTrade_deposit());
			    	stats.setSum_income(tradeLog.getTrade_income());
			    	stats.setUser_name(member.getF_user_email());
			    	stats.setYield_rate(rate);
			    	
			    	vTradeDayStatsService.saveOrUpdate(stats);
		    	}
	    	}
	    	
	    	DecimalFormat df = new DecimalFormat("0.00");
	    	statsVo.setYieldRate(Double.parseDouble(df.format(rate.doubleValue())));
	    	allVts.add(statsVo);
		}
		
		Collections.sort(allVts);
		if (cycleType == 1) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRADE_WEEK_RANKING, BTCQMemecacheKeys.TIMES.ONE_HOUR, allVts);
		} else if (cycleType == 2) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRADE_MONTH_RANKING, BTCQMemecacheKeys.TIMES.ONE_HOUR, allVts);
		} else {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRADE_DAY_RANKING, BTCQMemecacheKeys.TIMES.ONE_HOUR, allVts);
		}
	}
	
}
