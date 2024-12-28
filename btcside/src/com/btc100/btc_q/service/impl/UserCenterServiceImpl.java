package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQMemberRechargeDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.UserCenterService;
import com.btc100.btc_q.util.RandomUtil;

@Service("userCenterService")
public class UserCenterServiceImpl implements UserCenterService {

	@Autowired
	private BQMemberRechargeDao<BQMemberRecharge> bqMemberRechargeDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;

	public void DoWithdraw(BQMember user, BQMemberRecharge recharge) throws ServiceException {

		bqMemberRechargeDao.save(recharge);

		// 更改提现后资金余额
		BQAccount ba = bqAccountDao.getByUerAndCurrencyId(user.getF_user_id(), recharge.getCurrency_type_id());
		long time = bqAccountDao.getUnix_TIMESTAMP();

		// 财务往来流水汇总表
		BQAccountAmountLog bqal = new BQAccountAmountLog();
		bqal.setTitle("提现");// '描述标题',
		bqal.setCurrency_type_id(recharge.getCurrency_type_id());// '币种（btc/ltc,gtc)1:btc 2:ltc
		// 3...(后期可添加币种)',
		bqal.setOperation(2);// 操作类型(0:其它/1:充值/2:提现/3:交易/5更多扩展)'
		bqal.setDirection(-1);// 1:加款 -1:减款
		bqal.setCurrent_amount(ba.getBtc());
		bqal.setChange_amount(recharge.getAmount());
		bqal.setLastest_amount(ba.getBtc().subtract(recharge.getAmount()));
		bqal.setDateline(time);
		bqal.setF_user_id(user.getF_user_id());
		bqal.setTrans_no(RandomUtil.getTransNo());
		bqal.setTrans_type("");
		bqal.setRecord_id(recharge.getRecord_id());
		bqal.setBefore_frozen(ba.getBtc_locked());
		bqal.setAfter_frozen(ba.getBtc_locked().add(recharge.getAmount()));
		bqAccountAmountLogDao.save(bqal);

		if(bqAccountDao.updateAmount(user.getF_user_id(), recharge.getCurrency_type_id(), recharge.getAmount().negate(),recharge.getAmount())<=0){
			throw new ServiceException("提现余额不足");
		}
	}

}
