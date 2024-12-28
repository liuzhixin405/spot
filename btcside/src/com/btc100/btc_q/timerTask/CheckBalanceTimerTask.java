package com.btc100.btc_q.timerTask;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.vo.BalanceAccountVO;

public class CheckBalanceTimerTask {
	
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private BQMemberService bqMemberService;
	
	private long uid = 9000;

	public void run() {

		BalanceAccountVO allBalance = new BalanceAccountVO();
		BalanceAccountVO generalBalance = new BalanceAccountVO();
		List<BalanceAccountVO> contractBalances = new ArrayList<BalanceAccountVO>();
		List<BalanceAccountVO> deliveryContractBalances = new ArrayList<BalanceAccountVO>();
		
		if (uid == 0) return;
		
		BQMember user = bqMemberService.getUserAboveId(uid);
		if (user == null ) {
			if (uid != 0) {
				System.out.println("last: " + uid);
			}
			uid = 0;
			return;
		}
		
		uid = user.getF_user_id();
		
		if (uid == 1260 || uid == 8670) return;
		
		if (uid % 1000 == 0) {
			System.out.println(uid);
		}
		
		BQMemberRecharge memberRecharge = bqMemberRechargeService.getOne(uid);
		if (memberRecharge == null) return;
		
		try {
			bqMemberRechargeService.getBalanceAccount(uid, Contents.TRADE_CURRENCY_TYPE_ID, allBalance, generalBalance, contractBalances, deliveryContractBalances);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double netWorth = allBalance.getNetWorth();
		double shouldAvai = 0;
		if (generalBalance.getShouldAvailable() < 0) {
			shouldAvai = generalBalance.getShouldAvailable();
		}
		double contractShould = 0;
		for (BalanceAccountVO vo : contractBalances) {
			if (vo.getShouldAvailable() < 0) {
				contractShould += vo.getShouldAvailable();
			}
		}
		double deliveryShould = 0;
		for (BalanceAccountVO vo : deliveryContractBalances) {
			if (vo.getShouldAvailable() < 0) {
				deliveryShould += vo.getShouldAvailable();
			}
		}
		
		netWorth = shouldAvai + contractShould + deliveryShould;
		if (Math.abs(netWorth) > 0.001)  {
			System.out.println("uid: " + uid + ", should: " + netWorth);
		}
	}
}
