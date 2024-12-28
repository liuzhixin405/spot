package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSpotMarketCoinPairDao;
import com.btc100.btc_q.dao.SpotMemberTradeDetailDao;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotMemberTradeDetail;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("spotMemberTradeDetailService")
public class SpotMemberTradeDetailServiceImpl implements SpotMemberTradeDetailService {

	@Autowired
	private SpotMemberTradeDetailDao<SpotMemberTradeDetail> spotMemberTradeDetailDao;
	@Autowired
	BQSpotMarketCoinPairDao<BQSpotMarketCoinPair> bqSpotMarketCoinPairDao;
	
	public PagerModel findModel(String coinPairName) {
		PagerModel pm = new PagerModel();

		Session session = spotMemberTradeDetailDao.getSession();

		String sql = "select * from " + coinPairName + "_member_trade_detail where f_buy_uid > 1 and f_sell_uid > 1 ORDER BY id desc limit ?,? ";
		String sqlcount = "select count(1) from " + coinPairName + "_member_trade_detail where f_buy_uid > 1 and f_sell_uid > 1 limit 1";
		SQLQuery querycount = session.createSQLQuery(sqlcount);
		querycount.list().get(0);
		int total = ConvertUtil.toInt(querycount.uniqueResult(), 0);

		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, SystemContext.getOffset());
		query.setInteger(1, SystemContext.getPageSize());

		query.addEntity(com.btc100.btc_q.entity.SpotMemberTradeDetail.class);
		pm.setDatas(query.list());
		pm.setTotal(total);
		return pm;
	}

	public PagerModel findModel(String coinPairName, Integer buyid, Integer sellid) {
		PagerModel pm = new PagerModel();

		Session session = spotMemberTradeDetailDao.getSession();
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append("where 1 = 1 ");
		if (buyid > 0) {
			sbBuilder.append("and f_buy_uid = :buid ");
			map.put("buid", buyid);
		}
		if (sellid > 0) {
			sbBuilder.append("and f_sell_uid = :suid ");
			map.put("suid", sellid);
		}

		String sql = "select * from " + coinPairName + "_member_trade_detail " + sbBuilder.toString() + "and f_buy_uid > 1 and f_sell_uid > 1 ORDER BY id desc limit :start,:page ";
		String sqlcount = "select count(1) from " + coinPairName + "_member_trade_detail " + sbBuilder.toString() + "and f_buy_uid > 1 and f_sell_uid > 1";
		SQLQuery querycount = session.createSQLQuery(sqlcount);
		SQLQuery query = session.createSQLQuery(sql);
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				query.setParameter(key, map.get(key));
				querycount.setParameter(key, map.get(key));
			}
		}
		int total = ConvertUtil.toInt(querycount.uniqueResult(), 0);// Integer.parseInt(querycount.uniqueResult().toString());

		query.setParameter("start", SystemContext.getOffset());
		query.setParameter("page", SystemContext.getPageSize());

		query.addEntity(com.btc100.btc_q.entity.SpotMemberTradeDetail.class);
		pm.setDatas(query.list());
		pm.setTotal(total);
		return pm;
	}

	public double findTotal(String coinPairName, Integer buyid, Integer sellid) {

		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append("where 1 = 1 ");
		if (buyid > 0) {
			sbBuilder.append("and f_buy_uid = :buid ");
			map.put("buid", buyid);
		}
		if (sellid > 0) {
			sbBuilder.append("and f_sell_uid = :suid ");
			map.put("suid", sellid);
		}

		Session session = spotMemberTradeDetailDao.getSession();

		String sql = "select sum(trade_count) from " + coinPairName + "_member_trade_detail " + sbBuilder.toString() + "and f_buy_uid > 1 and f_sell_uid > 1";
		SQLQuery querycount = session.createSQLQuery(sql);
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				querycount.setParameter(key, map.get(key));
			}
		}

		double total = ConvertUtil.toDouble(querycount.uniqueResult(), 0);// Integer.parseInt(querycount.uniqueResult().toString());

		return total;
	}

	public JSONArray getAllJsonTopN(String bqCode, int n) {
		List<Object[]> list = spotMemberTradeDetailDao.getAllTopN(bqCode, n);
		JSONArray array = new JSONArray();
		try {
			for (Object[] objs : list) {
				JSONObject json = new JSONObject();
				BigDecimal amount = new BigDecimal(objs[2].toString());
				BigDecimal price = new BigDecimal(objs[1].toString());

				json.put("date", objs[0]); 	// trade_time
				json.put("price", price); 	// trade_average_price
				json.put("amount", amount); // trade_count
				String type = "";
				if (objs[4].toString().equals("1")) {
					type = "buy";
				}
				if (objs[4].toString().equals("2")) {
					type = "sell";
				}
				BigDecimal vol = price.multiply(amount).setScale(4, BigDecimal.ROUND_HALF_UP);

				json.put("type", type); // trade_trans_dirction
				json.put("vol", vol);
				array.put(json);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}
	
	public JSONArray readAllJsonTopN(String bqCode, int n) {
		List<Object[]> list = spotMemberTradeDetailDao.getAllTopN(bqCode, n);
		JSONArray array = new JSONArray();
		try {
			for (Object[] objs : list) {
				JSONObject json = new JSONObject();
				BigDecimal amount = new BigDecimal(objs[2].toString());
				BigDecimal price = new BigDecimal(objs[1].toString());

				json.put("date", objs[0]); 	// trade_time
				json.put("price", price); 	// trade_average_price
				json.put("amount", amount); // trade_count
				String type = "";
				if (objs[4].toString().equals("1")) {
					type = "buy";
				}
				if (objs[4].toString().equals("2")) {
					type = "sell";
				}
				BigDecimal vol = price.multiply(amount).setScale(4, BigDecimal.ROUND_HALF_UP);

				json.put("type", type); // trade_trans_dirction
				json.put("vol", vol);
				array.put(json);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}

	public Integer getLatesetTradeTime(String transactionCode, long beforeTime) {
		Session session = spotMemberTradeDetailDao.getSession();
		StringBuffer sql = new StringBuffer(
				"select trade_time from " + transactionCode + "_member_trade_detail where trade_time < " + beforeTime + " order by id desc limit 1");
		Query query = session.createSQLQuery(sql.toString());
		return (Integer) query.uniqueResult();
	}

	@Override
	public void save(SpotMemberTradeDetail tradeDetail) {
		spotMemberTradeDetailDao.insertTradeDetail(tradeDetail);
	}

	@Override
	public BigDecimal readLastPriceBeforeTime(String bqCode, long endTime) {
		Session session = spotMemberTradeDetailDao.getSession();
		String sql = "SELECT trade_price from " + bqCode + "_member_trade_detail where trade_time <= " + endTime
				+ "  order by id desc limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		Object result = query.uniqueResult();

		BigDecimal price = BigDecimal.ZERO;
		if (result != null) {
			price = new BigDecimal(result.toString());
		}
		
		return price;
	}

	@Override
	public void deleteTradeScalpingDetail(String vCode, long scalpingUid, long beforeTime) {
		Session session = spotMemberTradeDetailDao.getSession();
		String sql = "delete from " + vCode + "_member_trade_detail where f_sell_uid = :uid and f_buy_uid = :uid and trade_time < :time";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", scalpingUid);
		query.setParameter("time", beforeTime);
		query.executeUpdate();
		
	}
	
	@Override
	public BigDecimal findAvgPriceBetweenTime(String bqCode, long startTime, long endTime) {
		if (bqCode == null) {
			return BigDecimal.ZERO;
		}
		
		Session session = spotMemberTradeDetailDao.getSession();
		String sql = "select ifnull(AVG(trade_price), 0) AS vol from " + bqCode + "_member_trade_detail where trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeNoScalpingAmount(String code, long startTime, long endTime, long scalUserId) {
		Session session = spotMemberTradeDetailDao.getSession();
		String sql = "SELECT IFNULL(SUM(trade_price * trade_count), 0) AS amount from " + code
				+ "_member_trade_detail where trade_time >= " + startTime + " and trade_time < " + endTime
				+ " and f_buy_uid > 1 and f_buy_uid <> " + scalUserId
				+ " and f_sell_uid > 1 and f_sell_uid <> " + scalUserId;
		SQLQuery query = session.createSQLQuery(sql);
		Object result = query.uniqueResult();

		BigDecimal price = BigDecimal.ZERO;
		if (result != null) {
			price = new BigDecimal(result.toString());
		}

		return price;
	}

	@Override
	public void createMemberTradeDetailBak(String code) {
		spotMemberTradeDetailDao.createMemberTradeDetailBak(code);
	}

	@Override
	public void insertMemberTradeDetailBak(String code, long endTime) {
		spotMemberTradeDetailDao.insertMemberTradeDetailBak(code, endTime);
		spotMemberTradeDetailDao.deleteMemberTradeDetail(code, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findNextFromId(String code, long id, int limit) {
		Session session = spotMemberTradeDetailDao.getSession();
		StringBuffer sql = new StringBuffer(
				"select trade_time AS date, trade_price AS price, trade_count AS amount,id as tid, trade_trans_dirction AS tradType from "
						+ code + "_member_trade_detail where id > :id order by id desc limit " + limit);
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("id", id);
		
		return query.list();
	}
}
