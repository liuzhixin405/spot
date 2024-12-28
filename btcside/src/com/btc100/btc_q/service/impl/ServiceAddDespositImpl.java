package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.ServiceAddDesposit;

/**
 * 追加保证金服务
 * 
 * @author Administrator
 * 
 */
@Service("serviceAddDesposit")
public class ServiceAddDespositImpl implements ServiceAddDesposit {

	/**
	 * 追加保证金
	 * 
	 * @param uid
	 *            变动用户ID
	 * @param deposit
	 *            追加的保证金
	 * @param hostid
	 *            持仓ID
	 * @param transactionID
	 *            合约ID
	 * @param transactionCode
	 *            合约号
	 * @param direction
	 *            方向
	 * @throws ServiceException 
	 */
	public void AddDesposit(Long uid, BigDecimal deposit, Long hostid, int transactionID, String transactionCode, int direction) throws ServiceException {
		
		return;
		
		// 1 查是否有余额
//		BQAccountContract bac = bqAccountContractDao.findByUserIDCode(uid, transactionCode);
//		if (bac == null) {
//			return;
//		}
//
//		if (bac.getF_btc().subtract(deposit).compareTo(BigDecimal.ZERO) < 0) {	// 账户余额不足
//			return;
//		}
//
//		Long time_now = bqAccountContractDao.getUnix_TIMESTAMP();
//
//		// 2 插入日志
//		// 2.1 保证金日志
//		BQAccountDepositLog pLog = new BQAccountDepositLog();
//		pLog.setMt_id(ConvertUtil.toInt(hostid, 0));
//		pLog.setTransaction_id(transactionID);
//		pLog.setTransaction_code(transactionCode);
//		pLog.setF_user_id(uid);
//		pLog.setDeposit(deposit);
//		pLog.setDirection(direction);
//		pLog.setDateline(time_now);
//		bqAccountDepositLogDao.save(pLog);
//
//		// 2.2 资金变动日志
//		ContractAccountLog pamountLog = new ContractAccountLog();
//		pamountLog.setChange_amount(deposit);
//		pamountLog.setCurrency_type_id(Contents.TRADE_CURRENCY_TYPE_ID);
//		pamountLog.setCurrent_amount(bac.getF_btc());
//		pamountLog.setDateline(time_now);
//		pamountLog.setDirection(-1);
//		pamountLog.setF_user_id(uid);
//		pamountLog.setLastest_amount(bac.getF_btc().subtract(deposit));
//		pamountLog.setBefore_frozen(bac.getF_lockbtc());
//		pamountLog.setAfter_frozen(bac.getF_lockbtc());
//		pamountLog.setOperation(Contents.TRANS_TYPE_APBZJ); // 追加保证金
//		pamountLog.setRecord_id(hostid);
//		pamountLog.setTrans_no(RandomUtil.getTransNo());
//		pamountLog.setTitle("自动追加保证金，扣除余额");
//		pamountLog.setF_code(transactionCode);
//		contractAccountLogDao.insertLog(pamountLog);
//	
//		// 变动账户余额
//		if(bqAccountContractDao.updateAmount(uid, transactionCode, BigDecimal.ZERO, deposit.negate(), BigDecimal.ZERO, BigDecimal.ZERO)<=0){
//			logger.error("追加保证金异常:更新账户异常---" + uid);
//			throw new ServiceException("");
//		}
//
//		// 修改持仓保证金
//		String sql_upcc = "UPDATE BQMemberTransaction b SET b.deposit2=b.deposit2+:deposit WHERE b.mt_id=:id";
//		Map<String, Object> map_upcc = new HashMap<String, Object>();
//		map_upcc.put("deposit", deposit);
//		map_upcc.put("id", hostid);
//
//		if (bqMemberTransactionDao.updateCondition(sql_upcc, map_upcc) < 0) {
//			logger.error("追加保证金异常:持仓表追加保证金错误---" + uid);
//			throw new RuntimeException();
//		}
	}
}
