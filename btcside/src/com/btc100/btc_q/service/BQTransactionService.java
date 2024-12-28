package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.vo.PagerModel;

public interface BQTransactionService {

	void createTable(String code);
	
	long time();
	
	public List<BQTransaction> findTransByState_new(int state);
	
	public BQTransaction findTransByCode(String code);
	
	public BQTransaction findTransToUpdateByCode(String code);

	public BQTransaction get(long transactionId);

	public void update(BQTransaction bqtTemp);

	public void updateTrans(String bqCode, String multiple, BigDecimal tradeCoefficient);
	
	
	public int updateTrans(long bqCodeid, BigDecimal highest, BigDecimal lowest);
	public int updateTrans(long bqCodeid, BigDecimal highest, BigDecimal lowest, BigDecimal last);

	/**
	 * 增加合约的杠杆和杠杆状态
	 * 描述：新杠杆和新杠杆状态非空，旧杠杆和旧杠杆状态可以为空，如果就旧杠杆包含新杠杆，则新杠杆状态替换旧杠杆状态相应位置下的杠杆状态，如果不包含，则赋值
	 * @param bqCode 合约号
	 * @param multiple 新杠杆 例如1
	 * @param multipleTrans 旧杠杆 例如1,5,10
	 * @param multipleState 新杠杆状态 例如1
	 * @param multipleStateTrans 杠杆状态 例如1,2,3
	 */
	public void addMultipleByCode(String bqCode, String multiple, String multipleTrans, String multipleState, String multipleStateTrans);
	
	/**
	 * 删除合约的杠杆和杠杆状态
	 * 描述：新杠杆和旧杠杆非空，旧杠杆包含新杠杆，删除旧杠杆里面的新杠杆，删除旧杠杆状态里的杠杆状态
	 * @param bqCode 合约号
	 * @param multiple 新杠杆 例如1
	 * @param multipleTrans 旧杠杆 例如1,5,10
	 * @param multipleStateTrans 旧杠杆状态
	 */
	public void delMultipleByCode(String bqCode, String multiple, String multipleTrans, String multipleStateTrans);
	
	void updateMultipleByCode(String bqCode, String multiple, String multipleState);

	/**
	 * 增加交易系数
	 * @param bqCode 合约号
	 * @param tradeCoefficient 交易系数
	 */
	public void addCoefficientByCode(String bqCode, BigDecimal tradeCoefficient);

	/**
	 * 新增合约
	 * @param bqCode 合约号
	 * @param state 合约状态（1:正常 2:清算中 3:关闭）
	 * @param multiple 杠杆
	 * @param multipleState 杠杆状态（0不显示,1显示,2禁用）
	 * @param btradeCoefficient 交易系数
	 * @param endtime 截至时间
	 */
	public Boolean addMultipleByCode(String bqCode, int state, String multiple, String multipleState,
			BigDecimal btradeCoefficient, long endtime, int sort, int transactionKind, String showName,
			String enName, String jpName, String krName, String hkName);

	public String updateTrans(List<BQTransaction> transactionList,
			HttpServletRequest request);
	
	public void changePauseAndTransfer(String bqCode, boolean suspend, boolean transferIn, boolean transferOut);
	
	List<BQTransaction> findTransByKind(int kind, int state);

	public PagerModel findTransByState_pm(String bqCode, int istate);
	
	
	/************************************ for read only **************************************/
	
	public List<BQTransaction> readTransByState_new(int state);
	
	public BQTransaction readTransByCode(String code);
	
	List<BQTransaction> readOrderById(int limit);
}
