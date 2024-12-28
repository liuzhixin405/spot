package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.vo.PagerModel;

public interface BQAccountService {

	List<BQAccount> findAboveZero(int currencyId);
	
	List<Integer> findUserIdAboveZero(int currencyId);
	
	List<Object[]> findBalance(int currencyId);

	void save(BQAccount ba, int currencyId);

	public void addUserMoney(BigDecimal oneMoney, long userId, int currencyId);

	int updateAccount(long uid, int currencyId, BigDecimal btc, BigDecimal freeze);

	/**
	 * 更新用户资金（带日志）
	 * @param uid 用户ID
	 * @param btc 要改变金额
	 * @param title 日志描述标题
	 * @param type 操作类型(0:其它/1:充值/2:提现/3:交易/4:追加保证金/5:更多扩展)
	 * @param direction 方向（1:加款 -1:减款）
	 * @param transType  交易类型(充值/提现|交易手续费|挂单/撤单|其它)
	 * @return
	 */
	public int updateAccountAndLog(long uid, int currencyId, BigDecimal btc, String title, int type, int direction, String transType);

	/**
	 * 找出有余额的用户
	 * @return
	 */
	public List<BQAccount> findMoneyUser(int currencyId);

	List<BQAccount> findGreaterThan1000(int currencyId);
	
	BQAccount getAccount(long fUserId, int currencyId);
	
	
	
	/************************************ for read only **************************************/
	
	BQAccount readAccount(long fUserId, int currencyId);
	
	List<BQAccount> readAboveZero(int currencyId);
	
	List<Integer> readAllUserIds(int currencyId);
	
	List<Object[]> readBalanceAndFrozen(int currencyId);
	
	List<BQAccount> readAllByUserId(long fUserId);
	
	BQAccount readAccountStatistics(int coinId);
	
	PagerModel readStatistics(String orderBy, long userId, int coinId);
}

