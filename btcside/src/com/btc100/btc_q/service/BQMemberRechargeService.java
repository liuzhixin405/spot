package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.BalanceAccountVO;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberRechargeService {

	BQMemberRecharge findByRecordId(long recordId);

	void save(BQMemberRecharge bqr, int currencyId);

	/**
	 * 确认充值
	 * 
	 * @param id
	 * @param state
	 * @param remark
	 * @return
	 * @throws ServiceException
	 */
	int rechargeCZ(long id, int state, String remark) throws ServiceException;

	/**
	 * 确认提现
	 * 
	 * @param id
	 * @param state
	 * @param remark
	 * @return
	 * @throws ServiceException
	 */
	int rechargeTX(long id, int state, String remark) throws ServiceException;

	/**
	 * 添加充值记录
	 */
	int addCZ(long uid, int currencyId, BigDecimal amount, String address, String tradeID, int isadmin, String remark) throws ServiceException;
	int addTX(long uid, int currencyId, BigDecimal amount, String address, String tradeID, int isadmin, String remark) throws ServiceException;
	
	/**
	 * 统计费用
	 * @param id 用户id
	 * @param operation 操作类型(1:充值/2:提现)
	 * @param state 0:未受理 1:受理成功 2:拒绝
	 * @return 数量
	 */
	public BigDecimal sumRecord(long id, int operation, int state, int currencyId);

	public void getBalanceAccount(long uid, int currencyId, BalanceAccountVO allBalance, BalanceAccountVO generalBalance, List<BalanceAccountVO> contractBalances, List<BalanceAccountVO> deliveryContractBalances)  throws ParseException;
	
	public BigDecimal getAllWithdrawSuccessBetweenTime(long uid, int currencyId, long beginTime, long endTime);
	
	public BigDecimal getSumBetweenTime(long uid, int currencyId, long beginTime, long endTime);
	
	public BQMemberRecharge getOne(long uid);
	
	
	/************************************ for read only **************************************/
	
	
	long readRechargeCount(long uid);
	
	PagerModel readRechargeAll(long fUserId, int currencyId);

	PagerModel readWithdrawAll(long fUserId, int currencyId);
	
	PagerModel readCzByUserID(Long uid, String uemail,String uaddr, int itype, int isadmin, int currencyId, String state);
	
	PagerModel readCzByUserID(Long uid, String uemail,String uaddr, int itype, int isadmin, int currencyId);
	
	PagerModel readRecords(int type, int isadmin, int currencyId);
	
	BQMemberRecharge readByTradeID(String tradeID, int currencyId);

	PagerModel getRecordAll(long fUserId, int currencyId);
	
	List<BQMemberRecharge> readByUserAndTime(long fUserId, int currencyId, int operation, long from,
			String direct, long startTime, long endTime, int limit);
}
