package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQMemberRechargeDao;
import com.btc100.btc_q.dao.BQMemberRedgiftDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.entity.BQMemberRedgift;
import com.btc100.btc_q.entity.BQRedgift;
import com.btc100.btc_q.service.RedgiftService;
import com.btc100.btc_q.util.RandomUtil;

@Service("redgiftService")
public class RedgiftServiceImpl implements RedgiftService {

	@Autowired
	private BQMemberRedgiftDao<BQMemberRedgift> bqMemberRedgiftDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private BQMemberRechargeDao<BQMemberRecharge> bqMemberRechargeDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	
	public void receiveRedgift(BQRedgift redgift, BQMember user, String IP) {
		BQMemberRedgift memberRedgift = new BQMemberRedgift();
		memberRedgift.setF_amount(redgift.getF_amount());
		memberRedgift.setF_code(redgift.getF_code());
		memberRedgift.setF_state(1);
		memberRedgift.setF_time(redgift.getF_time());
		memberRedgift.setF_type(Contents.TRADE_CURRENCY_TYPE_ID);
		memberRedgift.setF_user_id(user.getF_user_id());
		bqMemberRedgiftDao.save(memberRedgift);
		
		BQMemberRecharge memberRecharge = new BQMemberRecharge();
		memberRecharge.setAmount(redgift.getF_amount());
		long time = bqMemberRechargeDao.getUnix_TIMESTAMP();
		memberRecharge.setConfirm_time(time);
		memberRecharge.setCurrency_type_id(Contents.TRADE_CURRENCY_TYPE_ID);
		memberRecharge.setDateline((int)time);
		memberRecharge.setF_user_email(user.getF_user_email());
		memberRecharge.setF_user_id(user.getF_user_id());
		memberRecharge.setFee(BigDecimal.ZERO);
		memberRecharge.setIp(IP);
		memberRecharge.setOperation(Contents.RECHARGE_TYPE_CZ);
		memberRecharge.setReceice_card_no(redgift.getF_code());
		memberRecharge.setReceice_card_type(Contents.RECHARGE_FLAG_NORMAL);
		memberRecharge.setRemark("红包码");
		memberRecharge.setState(Contents.RECHARGE_STATE_ED);
		memberRecharge.setWallet_trade_id("");
		bqMemberRechargeDao.save(memberRecharge);
		
		BQAccount account = bqAccountDao.getByUerAndCurrencyId(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		
		BQAccountAmountLog amountLog = new BQAccountAmountLog("红包码充值", Contents.TRADE_CURRENCY_TYPE_ID, Contents.RECHARGE_TYPE_CZ, 1, 
				account.getBtc(), redgift.getF_amount(), account.getBtc(), account.getBtc_locked(), account.getBtc_locked(), time, user.getF_user_id(), 
				RandomUtil.getTransNo(), "充值", memberRecharge.getRecord_id());
		bqAccountAmountLogDao.save(amountLog);
	}

}
