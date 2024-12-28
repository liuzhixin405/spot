package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.SpotAccountLogDao;
import com.btc100.btc_q.dao.SpotPendingDelegationDao;
import com.btc100.btc_q.dao.SpotPendingLogDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotAccountLog;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.btc100.btc_q.vo.Top5VO;

@Service("spotPendingDelegationService")
public class SpotPendingDelegationServiceImpl implements SpotPendingDelegationService {

	@Autowired
	private SpotPendingDelegationDao<SpotPendingLog> pendingDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private SpotAccountLogDao<SpotAccountLog> spotAccountLogDao;
	@Autowired
	private SpotPendingLogDao<SpotPendingLog> spotPendingLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Override
	public String lock(String coinPairName, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.PENDING_LOCK + coinPairName + "_" + id);
	}

	@Override
	public void unLock(String coinPairName, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.PENDING_LOCK + coinPairName + "_" + id, value);
	}

	@Override
	public int deletePending(String coinPairName, Long id) throws HibernateException {
		return pendingDao.deletePending(coinPairName, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Top5DelegationVO> getBuyTop5(String coinPairName, long uid) {
		Session session = pendingDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT delegation_price as price ,sum(undeal_count) AS countNum from " + coinPairName
				+ "_pending_delegation where direction=:direction and f_user_id=:uid and flag < 2 GROUP BY delegation_price ORDER BY delegation_price DESC LIMIT 20 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(Top5VO.class));

		query.setParameter("direction", Contents.DIRECTION_BUY);
		query.setParameter("uid", uid);

		List<Top5VO> pdlist = query.list();
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			vo = new Top5DelegationVO("买(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), countAll);

			resList.add(vo);
		}
		return resList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Top5DelegationVO> getSellTop5(String coinPairName, long uid) {
		Session session = pendingDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT delegation_price as price ,sum(undeal_count) AS countNum from " + coinPairName
				+ "_pending_delegation where  direction=:direction and f_user_id=:uid and flag < 2 GROUP BY delegation_price ORDER BY delegation_price ASC LIMIT 20 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(Top5VO.class));

		query.setParameter("direction", Contents.DIRECTION_SELL);
		query.setParameter("uid", uid);
		List<Top5VO> pdlist = query.list();
		
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			vo = new Top5DelegationVO("买(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), countAll);

			resList.add(vo);
		}
		
//		Collections.sort(resList);
		return resList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Top5DelegationVO> getBuyTop5ExcludeUid(String coinPairName, long uid) {
		Session session = pendingDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT delegation_price as price ,sum(undeal_count) AS countNum from " + coinPairName
				+ "_pending_delegation where direction=:direction and f_user_id<>:uid and flag < 2 GROUP BY delegation_price ORDER BY delegation_price DESC LIMIT 20 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(Top5VO.class));

		query.setParameter("direction", Contents.DIRECTION_BUY);
		query.setParameter("uid", uid);

		List<Top5VO> pdlist = query.list();
		
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			vo = new Top5DelegationVO("买(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), countAll);

			resList.add(vo);
		}
		return resList;
	}

	@SuppressWarnings("unchecked")
	public List<Top5DelegationVO> getSellTop5ExcludeUid(String coinPairName, long uid) {
		Session session = pendingDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT delegation_price as price ,sum(undeal_count) AS countNum from " + coinPairName
				+ "_pending_delegation where  direction=:direction and f_user_id<>:uid and flag < 2 GROUP BY delegation_price ORDER BY delegation_price ASC LIMIT 20 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(Top5VO.class));

		query.setParameter("direction", Contents.DIRECTION_SELL);
		query.setParameter("uid", uid);
		List<Top5VO> pdlist = query.list();
		
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			vo = new Top5DelegationVO("买(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), countAll);

			resList.add(vo);
		}
		
//		Collections.sort(resList);
		return resList;
	}
	
	@Override
	public List<Top5DelegationVO> getBuyTop5(String coinPairName) {
		List<Top5VO> pdlist = pendingDao.getBuyTop5(coinPairName);
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			vo = new Top5DelegationVO("买(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), countAll);

			resList.add(vo);
		}
		return resList;
	}
	
	@Override
	public List<Top5DelegationVO> readBuyTop5(String coinPairName) {
		List<Top5VO> pdlist = pendingDao.getBuyTop5(coinPairName);
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			vo = new Top5DelegationVO("买(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), countAll);

			resList.add(vo);
		}
		return resList;
	}

	@Override
	public List<Top5DelegationVO> getSellTop5(String coinPairName) {
		List<Top5VO> pdlist = pendingDao.getSellTop5(coinPairName);
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			vo = new Top5DelegationVO("卖(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), countAll);
			resList.add(vo);
		}
		Collections.sort(resList);
		return resList;
	}
	
	@Override
	public List<Top5DelegationVO> readSellTop5(String coinPairName) {
		List<Top5VO> pdlist = pendingDao.getSellTop5(coinPairName);
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			vo = new Top5DelegationVO("卖(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), countAll);
			resList.add(vo);
		}
		Collections.sort(resList);
		return resList;
	}

	@Override
	public List<Object[]> getBuyTopN(String coinPairName, int n) {
		return pendingDao.getBuyTopN(coinPairName, n);
	}

	@Override
	public List<Object[]> getSellTopN(String coinPairName, int n) {
		return pendingDao.getSellTopN(coinPairName, n);
	}
	
	@Override
	public List<Object[]> readBuyTopN(String coinPairName, int n) {
		return pendingDao.getBuyTopN(coinPairName, n);
	}

	@Override
	public List<Object[]> readSellTopN(String coinPairName, int n) {
		return pendingDao.getSellTopN(coinPairName, n);
	}

	@Override
	public SpotPendingDelegation findPendingById(long orderId, String coinPairName) {
		return pendingDao.findPendingById(orderId, coinPairName);
	}

	@Override
	public PagerModel getPagerModel(int direct, Long uid, String semail, String coinPairName) {
		return pendingDao.findPagerModel(direct, uid, semail, coinPairName);
	}

	@Override
	public List<SpotPendingDelegation> readAllByUserId(String coinPairName, String direction, long fUserId, String orderby) {
		return pendingDao.queryByTypeUId(coinPairName, direction, fUserId, orderby);
	}

	@Override
	public List<SpotPendingDelegation> getAllByUserId(int direction, String coinPairName, long fUserId) {
		return pendingDao.queryByUserId(direction, coinPairName, fUserId);
	}

	@Override
	public int cancelPending_one(SpotPendingDelegation pd, boolean needLog, BQSpotMarketCoinPair coinPair) throws ServiceException {
		if (coinPair == null) {
			return 0;
		}
		
		if (pd == null || StringUtil.isEmpty(pd.getCoin_pair_name())) return 0;
		
		pd = pendingDao.findPendingById(pd.getDelegation_id(), pd.getCoin_pair_name());
		if (pd == null) return 0;
		
		if (pendingDao.deletePending(pd.getCoin_pair_name(), pd.getDelegation_id()) <= 0) {
			throw new ServiceException("cancelPending_one error, 删除挂单失败！");
		}
		
		int currencyId = 0;
		BigDecimal changeAmount = BigDecimal.ZERO;
		if (pd.getDirection() == 1) {
			currencyId = coinPair.getMarket_coin_id();
			changeAmount = pd.getDelegation_price().multiply(pd.getUndeal_count());
		} else {
			currencyId = coinPair.getCoin_id();
			changeAmount = pd.getUndeal_count();
		}
		
		long uid = pd.getF_user_id();
		if (changeAmount.compareTo(BigDecimal.ZERO) > 0) {
			BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
			if (bqAccountDao.updateAmount(uid, currencyId, changeAmount, changeAmount.negate()) <= 0) {
				throw new ServiceException("撤单失败，余额不足, 请重试");
			}
		
			String direction = "买入";
			if (pd.getDirection() == 2) {
				direction = "卖出";
			}
			
			if (needLog) {
				SpotAccountLog log = new SpotAccountLog();
				log.setTitle("现货交易" + pd.getCoin_pair_name() + "撤单，撤单" + direction + "，冻结转余额");
				log.setCurrency_type_id(currencyId);
				log.setOperation(3);	// 交易
				log.setDirection(1);
				log.setCurrent_amount(bqAccount.getBtc());
				log.setChange_amount(changeAmount);
				log.setLastest_amount(bqAccount.getBtc().add(changeAmount));
				log.setBefore_frozen(bqAccount.getBtc_locked());
				log.setAfter_frozen(bqAccount.getBtc_locked().add(changeAmount.negate()));
				log.setDateline(spotAccountLogDao.getUnix_TIMESTAMP());
				log.setF_user_id(uid);
				log.setTrans_no(pd.getDelegation_id() + "");
				log.setRecord_id(0);
				log.setF_coin_pair_name(pd.getCoin_pair_name());
				spotAccountLogDao.insertLog(log);
				
				spotPendingLogDao.updateActionByPendingNo(pd.getCoin_pair_name(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
		}

		return 1;
	}
	

	@Override
	public BigDecimal getSumByAction(String coinPairName, int direction) {
		Session session = pendingDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + coinPairName + "_pending_delegation WHERE direction=" + direction;
		
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getSumByUIDAction(String coinPairName, long uid, int direction) {
		Session session = pendingDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + coinPairName + "_pending_delegation WHERE direction=" + direction;
		if (uid > 0) {
			sql = sql + " and f_user_id=" + uid;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public List<SpotPendingDelegation> readPendingBetweenPrice(String coinPairName, int direction, BigDecimal minPrice,
			BigDecimal maxPrice) {
		return pendingDao.findPendingBetweenPrice(coinPairName, direction, minPrice, maxPrice);
	}

	@Override
	public List<SpotPendingDelegation> findPendingBetweenPriceByUserId(long uid, String coinPairName, int direction,
			BigDecimal minPrice, BigDecimal maxPrice) {
		return pendingDao.findPendingBetweenPriceByUserId(uid, coinPairName, direction, minPrice, maxPrice);
	}

	@Override
	public List<SpotPendingDelegation> findPendingAtPriceByUser(long uid, String coinPairName, int direction,
			BigDecimal price) {
		return pendingDao.findPendingAtPriceByUser(uid, coinPairName, direction, price);
	}

	@Override
	public SpotPendingDelegation getBuy1(String coinPairName) {
		pendingDao.getSession().flush();
		pendingDao.getSession().clear();
		return pendingDao.getBuy1(coinPairName);
	}

	@Override
	public SpotPendingDelegation getSell1(String coinPairName) {
		pendingDao.getSession().flush();
		pendingDao.getSession().clear();
		return pendingDao.getSell1(coinPairName);
	}

	@Override
	public String getDepth(String coinPairName, int n) {
		String sell = "asks", buy = "bids";
		JSONObject json = new JSONObject();

		try {
			List<Object[]> lstBuyObjs = new ArrayList<Object[]>();
			lstBuyObjs = getBuyTopN(coinPairName, n);
			
			List<JSONArray> lstBuyArray2 = new ArrayList<JSONArray>();
			for (Object[] obj : lstBuyObjs) {
				JSONArray array = new JSONArray();
				BigDecimal price = new BigDecimal(obj[0].toString());
				BigDecimal amount = new BigDecimal(obj[1].toString());

				array.put(price);
				array.put(amount);
				lstBuyArray2.add(array);
			}

			List<Object[]> lstSellObjs = new ArrayList<Object[]>();
			lstSellObjs = getSellTopN(coinPairName, n);
			List<JSONArray> lstSellArray2 = new ArrayList<JSONArray>();
			for (Object[] obj : lstSellObjs) {
				JSONArray array = new JSONArray();
				BigDecimal price = new BigDecimal(obj[0].toString());
				BigDecimal amount = new BigDecimal(obj[1].toString());

				array.put(price);
				array.put(amount);
				lstSellArray2.add(array);
			}
			json.put(buy, lstBuyArray2);
			json.put(sell, lstSellArray2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}
	
	@Override
	public String readDepth(String coinPairName, int n) {
		String sell = "asks", buy = "bids";
		JSONObject json = new JSONObject();

		try {
			List<Object[]> lstBuyObjs = new ArrayList<Object[]>();
			lstBuyObjs = readBuyTopN(coinPairName, n);
			
			List<JSONArray> lstBuyArray2 = new ArrayList<JSONArray>();
			for (Object[] obj : lstBuyObjs) {
				JSONArray array = new JSONArray();
				BigDecimal price = new BigDecimal(obj[0].toString());
				BigDecimal amount = new BigDecimal(obj[1].toString());

				array.put(price);
				array.put(amount);
				lstBuyArray2.add(array);
			}

			List<Object[]> lstSellObjs = new ArrayList<Object[]>();
			lstSellObjs = readSellTopN(coinPairName, n);
			List<JSONArray> lstSellArray2 = new ArrayList<JSONArray>();
			for (Object[] obj : lstSellObjs) {
				JSONArray array = new JSONArray();
				BigDecimal price = new BigDecimal(obj[0].toString());
				BigDecimal amount = new BigDecimal(obj[1].toString());

				array.put(price);
				array.put(amount);
				lstSellArray2.add(array);
			}
			json.put(buy, lstBuyArray2);
			json.put(sell, lstSellArray2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> readPendingByUserIdAndTime(String coinPairName, long userId, long startTime,
			long endTime, long from, String direct, int size) {
		Session session = pendingDao.getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where f_user_id = :userId";
		
		if (startTime > 0) {
			sql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time <= :endTime";
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sql += " and delegation_id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			sql += " and delegation_id < :from";
		}
		
		sql += " order by delegation_id desc";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			query.setLong("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			query.setLong("from", from);
		}
		
		query.addEntity(SpotPendingDelegation.class);
		
		return query.list(); 
	}

	@Override
	public SpotPendingDelegation readPendingByPendingNo(String pendingNo, String coinPairName, long userId) {
		return pendingDao.getPendingByPendingNo(pendingNo, coinPairName, userId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> findPendingByUserIdAndTime(String coinPairName, long userId, long startTime,
			long endTime, int start, int size, boolean sort) {
		Session session = pendingDao.getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where f_user_id = :userId";
		
		
		if (startTime > 0) {
			sql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time <= :endTime";
		}
		
		if (sort) {
			sql += " order by delegation_time desc";
		}
		
		if (size > 0) {
			if (start > 0) {
				sql += " limit " + start + "," + size;
			} else {
				sql += " limit " + size;
			}
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		query.addEntity(SpotPendingDelegation.class);
		
		return query.list(); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> readPendingByUserIdAndSymbol(String coinPairName, long userId, String orderIds) {
		Session session = pendingDao.getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where f_user_id = :userId ";
		
		sql += " and pending_no in (" + orderIds + ")";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		
		query.addEntity(SpotPendingDelegation.class);
		
		return query.list();
	}

	@Override
	public void insertPendingForDepth(List<SpotPendingDelegation> pendings, long uid, BigDecimal changeAccount,
			int direction, BQSpotMarketCoinPair bqTransaction) throws ServiceException {
		if (pendings.size() == 0) return;
		
		int remainCoinId = 0;
		if (direction == Contents.DIRECTION_BUY) {
			remainCoinId = bqTransaction.getMarket_coin_id();
		} else {
			remainCoinId = bqTransaction.getCoin_id();
		}
		
		if (bqAccountDao.updateAmount(uid, remainCoinId, changeAccount.negate(), changeAccount) <= 0) {
			throw new ServiceException("余额不足, 请重试");
		}
		
		if (!pendingDao.insertCache(pendings, bqTransaction.getCoin_pair_name())) {
			throw new ServiceException("插入错误,请重试");
		}
		
	}

	@Override
	public BigDecimal findSumBuyFrozen(String coinPairName, long userId) {
		return pendingDao.findSumBuyFrozen(coinPairName, userId);
	}

	@Override
	public BigDecimal findSumSellFrozen(String coinPairName, long userId) {
		return pendingDao.findSumSellFrozen(coinPairName, userId);
	}
	
}
