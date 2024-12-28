package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.vo.MarketPendingVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5VO;

public interface SpotPendingDelegationDao<T> extends BaseDaoI<T> {

	int deletePending(String coinPairName, Long id) throws HibernateException;

	List<SpotPendingDelegation> query(String coinPairName, Map<String, Object> params) throws HibernateException;

	SpotPendingDelegation getBuy1(String coinPairName);

	SpotPendingDelegation getBuy1(String coinPairName, long fUserId);

	SpotPendingDelegation getSell1(String coinPairName);

	SpotPendingDelegation getSell1(String coinPairName, long fUserId);

	List<Top5VO> getBuyTop5(String coinPairName);

	List<Top5VO> getSellTop5(String coinPairName);

	List<SpotPendingDelegation> queryByUserId(int direction, String coinPairName, long fUserId);

	List<SpotPendingDelegation> queryByTypeUId(String coinPairName, String direction, long uId, String orderby);

	List<MarketPendingVO> getBuyList(String coinPairName);

	List<MarketPendingVO> getSellList(String coinPairName);

	List<Object[]> getBuyTopN(String coinPairName, int n);

	List<Object[]> getSellTopN(String coinPairName, int n);

	int updateNumByID(long id, String coinPairName, BigDecimal dealNum, BigDecimal undealNum);

	void moveFromCache(SpotPendingDelegationCache cache);

	// 分页
	PagerModel findPagerModel(int direction, Long uid, String uemail, String coinPairName);

//	boolean lock(String coinPairName, long id);
//
//	boolean unLock(String coinPairName, long id);

	SpotPendingDelegation findPendingById(long orderId, String coinPairName);

	List<SpotPendingDelegation> findPendingBetweenPrice(String coinPairName, int direction, BigDecimal minPrice,
			BigDecimal maxPrice);
	
	List<SpotPendingDelegation> findPendingBetweenPriceByUserId(long uid, String coinPairName, int direction,
			BigDecimal minPrice, BigDecimal maxPrice);

	List<SpotPendingDelegation> findPendingAtPriceByUser(long uid, String coinPairName, int direction,
			BigDecimal price);

	List<SpotPendingDelegation> findPendingByMemberTransInfo(String coinPairName, long uId, int direction);

	BigDecimal getDelegation(Long id, String coinPairName, int i);
	
	BigDecimal findSumBuyFrozen(String coinPairName, long userId);
	
	BigDecimal findSumSellFrozen(String coinPairName, long userId);
	
	SpotPendingDelegation getPendingByPendingNo(String pendingNo, String coinPairName, long userId);
	
	boolean insertCache(List<SpotPendingDelegation> caches, String coinPairName) throws HibernateException;
}
