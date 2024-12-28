package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQLoanRecord;
import com.btc100.btc_q.entity.BQLoanSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQLoanRecordService;
import com.btc100.btc_q.service.BQLoanSettingService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.util.SendMail;

public class LoanPledgeRateTimerTask {
	
	@Autowired
	private BQLoanRecordService bqLoanRecordService;
	@Autowired
	private BQLoanSettingService bqLoanSettingService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQMemberService bqMemberService;
	
	public void run() {
		List<BQLoanSetting> settings = bqLoanSettingService.findAll();
		
		for (BQLoanSetting setting : settings) {
			BigDecimal lastPrice = null;
			if (setting.getId() > 0) {
				String symbol = setting.getPledge_symbol().toLowerCase() + setting.getBorrow_symbol().toLowerCase();
				lastPrice = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (lastPrice == null) {
					BQSpotMarketCoinPair currentCoinPair = bqSpotMarketCoinPairService.getByName(symbol);
					if (currentCoinPair != null) {
						lastPrice = currentCoinPair.getLatest_price();
					}
				}
			}
			
			if (lastPrice == null || lastPrice.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
			
			List<BQLoanRecord> records = bqLoanRecordService.findAllLargeRate(lastPrice, setting.getPledge_coin_id(), setting.getBorrow_coin_id(), setting.getCurr_pledge_rate());
			for (BQLoanRecord record : records) {
				sendEmail(record.getF_user_id(), record.getBorrow_symbol(), record.getPledge_symbol());
			}
		}
		
		List<BQLoanRecord> records = bqLoanRecordService.findAllOverTime();
		for (BQLoanRecord record : records) {
			sendEmail1(record.getF_user_id(), record.getBorrow_symbol(), record.getPledge_symbol());
		}
	}
	
	private void sendEmail1(long uid, String borrow, String pledge) {
		BQMember member = bqMemberService.findUserById(uid);
		if (member == null) return;
		
		StringBuffer sb = new StringBuffer("尊敬的XKD用户，您好！<br>");
	    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;您质押的" + pledge + "借入" + borrow + "订单已到期，请您及时赎回，以免质押平仓处理。<br><br>");
	    sb.append("感谢选择xkd<br>XKD官方<br><br>");
		
		final String mail = member.getF_real_email();
		final String cnt = sb.toString();
		Runnable runnable = new Runnable() {
	        public void run() {
	        	try {
	        		SendMail themail = new SendMail();
	    			themail.send(mail, cnt);
	        	} catch (Exception e) {
	        	}
	        }
	    };
	    
	    Thread thread = new Thread(runnable);
	    thread.start();
	}
	
	private void sendEmail(long uid, String borrow, String pledge) {
		BQMember member = bqMemberService.findUserById(uid);
		if (member == null) return;
		
		StringBuffer sb = new StringBuffer("尊敬的XKD用户，您好！<br>");
	    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;您质押" + pledge + "借入" + borrow + "订单，以达到补仓线，请及时补充质押金，以免被质押平仓。<br><br>");
	    sb.append("感谢选择xkd<br>XKD官方<br><br>");
		
		final String mail = member.getF_real_email();
		final String cnt = sb.toString();
		Runnable runnable = new Runnable() {
	        public void run() {
	        	try {
	        		SendMail themail = new SendMail();
	    			themail.send(mail, cnt);
	        	} catch (Exception e) {
	        	}
	        }
	    };
	    
	    Thread thread = new Thread(runnable);
	    thread.start();
	}
	
}
