package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.NMemberTradeDetailDao;
import com.btc100.btc_q.entity.NMemberTradeDetail;
import com.btc100.btc_q.service.NMemberTradeDetailService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.CountVo;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("nNMemberTradeDetailService")
public class NMemberTradeDetailServiceImpl implements NMemberTradeDetailService {

	@Autowired
	private NMemberTradeDetailDao<NMemberTradeDetail> memberTradeDetailDao;

	public PagerModel findModel(String partition, String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag) {
		PagerModel pm = new PagerModel();

		Session session = memberTradeDetailDao.getSession();
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append(" where 1 = 1 ");
		
		if (buyid > 0) {
			sbBuilder.append(" and f_buy_uid=:buid ");
			map.put("buid", buyid);
		}
		
		if (sellid > 0) {
			sbBuilder.append(" and f_sell_uid=:suid ");
			map.put("suid", sellid);
		}
		
		sbBuilder.append(" and trade_time >= :startTime ");
		map.put("startTime", startTime);
		sbBuilder.append(" and trade_time < :endTime ");
		map.put("endTime", endTime);
		
		if (!StringUtil.isEmpty(tag)) {
			String[] quantIdArr = quantitativeId.split(",");
			if (quantIdArr.length > 1) {
				sbBuilder.append(" and (f_buy_uid not in (").append(quantitativeId).append(") or f_sell_uid not in (").append(quantitativeId).append("))");
			} else {
				sbBuilder.append(" and (f_buy_uid <> ").append(quantitativeId).append(" or f_sell_uid <> ").append(quantitativeId).append(")");
			}
		}
		
		String sql = "select * from " + partition + "_" + transactionCode + "_member_trade_detail " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1 ORDER BY id desc limit :start,:page ";
		String sqlcount = "select count(1) from " + partition + "_" + transactionCode + "_member_trade_detail " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1";
		SQLQuery querycount = session.createSQLQuery(sqlcount);
		SQLQuery query = session.createSQLQuery(sql);
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				query.setParameter(key, map.get(key));
				querycount.setParameter(key, map.get(key));
			}
		}
		
		int total = ConvertUtil.toInt(querycount.uniqueResult(), 0);

		query.setParameter("start", SystemContext.getOffset());
		query.setParameter("page", SystemContext.getPageSize());

		query.addEntity(com.btc100.btc_q.entity.NMemberTradeDetail.class);
		pm.setDatas(query.list());
		pm.setTotal(total);
		return pm;
	}
	
	@SuppressWarnings("unchecked")
	public List<NMemberTradeDetail> findByTime(String partition, String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag) {
		Session session = memberTradeDetailDao.getSession();
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append(" where 1 = 1 ");
		
		if (buyid > 0) {
			sbBuilder.append(" and f_buy_uid=:buid ");
			map.put("buid", buyid);
		}
		
		if (sellid > 0) {
			sbBuilder.append(" and f_sell_uid=:suid ");
			map.put("suid", sellid);
		}
		
		sbBuilder.append(" and trade_time >= :startTime ");
		map.put("startTime", startTime);
		sbBuilder.append(" and trade_time < :endTime ");
		map.put("endTime", endTime);
		
		if (!StringUtil.isEmpty(tag) && !StringUtil.isEmpty(quantitativeId)) {
			String[] quantIdArr = quantitativeId.split(",");
			if (quantIdArr.length > 1) {
				sbBuilder.append(" and (f_buy_uid not in (").append(quantitativeId).append(") or f_sell_uid not in (").append(quantitativeId).append("))");
			} else {
				sbBuilder.append(" and (f_buy_uid <> ").append(quantitativeId).append(" or f_sell_uid <> ").append(quantitativeId).append(")");
			}
		}
		
		String sql = "select * from " + partition + "_" + transactionCode + "_member_trade_detail " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1 ORDER BY id desc";
		SQLQuery query = session.createSQLQuery(sql);
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				query.setParameter(key, map.get(key));
			}
		}

		query.addEntity(com.btc100.btc_q.entity.NMemberTradeDetail.class);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<NMemberTradeDetail> readByTime(String partition, String code, int buyid, int sellid, String quantitativeId, long startTime,
			long endTime, long from, String direct, int limit) {
		Session session = memberTradeDetailDao.getSession();
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append(" where 1 = 1");
		
		if (buyid > 0) {
			sbBuilder.append(" and f_buy_uid = :buid");
			map.put("buid", buyid);
		}
		
		if (sellid > 0) {
			sbBuilder.append(" and f_sell_uid = :suid");
			map.put("suid", sellid);
		}
		
		if (startTime > 0) {
			sbBuilder.append(" and trade_time >= :startTime");
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			sbBuilder.append(" and trade_time < :endTime");
			map.put("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sbBuilder.append(" and id >= :from");
			map.put("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			sbBuilder.append(" and id < :from");
			map.put("from", from);
		}
		
		if (!StringUtil.isEmpty(quantitativeId)) {
			String[] quantIdArr = quantitativeId.split(",");
			if (quantIdArr.length > 1) {
				sbBuilder.append(" and (f_buy_uid not in (").append(quantitativeId).append(") or f_sell_uid not in (").append(quantitativeId).append("))");
			} else {
				sbBuilder.append(" and (f_buy_uid <> ").append(quantitativeId).append(" or f_sell_uid <> ").append(quantitativeId).append(")");
			}
		}
		
		String sql = "select * from " + partition + "_" + code + "_member_trade_detail " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1 ORDER BY id desc";
		if (limit > 0) {
			sql += " limit " + limit;	
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				query.setParameter(key, map.get(key));
			}
		}
		
		query.addEntity(com.btc100.btc_q.entity.NMemberTradeDetail.class);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<NMemberTradeDetail> readFromBakByTime(String partition, String code, int buyid, int sellid, String quantitativeId, long startTime,
			long endTime, long from, String direct, int limit) {
		Session session = memberTradeDetailDao.getSession();
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append(" where 1 = 1");
		
		if (buyid > 0) {
			sbBuilder.append(" and f_buy_uid = :buid");
			map.put("buid", buyid);
		}
		
		if (sellid > 0) {
			sbBuilder.append(" and f_sell_uid = :suid");
			map.put("suid", sellid);
		}
		
		if (startTime > 0) {
			sbBuilder.append(" and trade_time >= :startTime");
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			sbBuilder.append(" and trade_time < :endTime");
			map.put("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sbBuilder.append(" and id >= :from");
			map.put("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			sbBuilder.append(" and id < :from");
			map.put("from", from);
		}
		
		if (!StringUtil.isEmpty(quantitativeId)) {
			String[] quantIdArr = quantitativeId.split(",");
			if (quantIdArr.length > 1) {
				sbBuilder.append(" and (f_buy_uid not in (").append(quantitativeId).append(") or f_sell_uid not in (").append(quantitativeId).append("))");
			} else {
				sbBuilder.append(" and (f_buy_uid <> ").append(quantitativeId).append(" or f_sell_uid <> ").append(quantitativeId).append(")");
			}
		}
		
		String sql = "select * from " + partition + "_" + code + "_member_trade_detail_bak " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1 ORDER BY id desc";
		if (limit > 0) {
			sql += " limit " + limit;	
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				query.setParameter(key, map.get(key));
			}
		}
		
		query.addEntity(com.btc100.btc_q.entity.NMemberTradeDetail.class);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NMemberTradeDetail> readByMakerTaker(String partition, String code, long quantUid, int type, long startTime, long endTime,
			long from, String direct, int limit, boolean isBak) {
		Session session = memberTradeDetailDao.getSession();
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append(" where f_buy_uid <> f_sell_uid");
		
		if (startTime > 0) {
			sbBuilder.append(" and trade_time >= :startTime");
			map.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			sbBuilder.append(" and trade_time < :endTime");
			map.put("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sbBuilder.append(" and id >= :from");
			map.put("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			sbBuilder.append(" and id < :from");
			map.put("from", from);
		}

		long moring = (long) TimeUtil.getTimesmorning();
		String tableName = partition + "_" + code + "_member_trade_detail ";
		if (isBak) {
			tableName = partition + "_" + code + "_member_trade_detail_bak ";
		} else if (startTime > 0 && startTime + 30 * 86400l < moring) {
			tableName  =partition + "_" + code + "_member_trade_detail_bak ";
		}
		
		String sql = "select * from " + tableName + sbBuilder.toString();
		if (type > 0) {
			String buyUid = "f_buy_uid = :userId";
			String sellUid = "f_sell_uid = :userId";
			sql += " AND ((trade_trans_dirction = 1 AND " + (type == 1 ? sellUid : buyUid) + ") OR"
					+ " (trade_trans_dirction = 2 AND " + (type == 1 ? buyUid : sellUid) + "))";
		} else {
			sql += " AND (f_sell_uid = :userId OR f_buy_uid = :userId)";
		}
		map.put("userId", quantUid);
		
		sql += " ORDER BY id DESC";
		if (limit > 0) {
			sql += " limit " + limit;	
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				query.setParameter(key, map.get(key));
			}
		}
		
		query.addEntity(NMemberTradeDetail.class);
		return query.list();
	}

	public double findTotal(String partition, String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag) {

		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		sbBuilder.append(" where 1 = 1 ");
		if (buyid > 0) {
			sbBuilder.append(" and f_buy_uid=:buid ");
			map.put("buid", buyid);
		}
		if (sellid > 0) {
			sbBuilder.append(" and f_sell_uid=:suid ");
			map.put("suid", sellid);
		}
		
		sbBuilder.append(" and trade_time >= :startTime ");
		map.put("startTime", startTime);
		sbBuilder.append(" and trade_time < :endTime ");
		map.put("endTime", endTime);
		
		if (!StringUtil.isEmpty(tag)) {
			String[] quantIdArr = quantitativeId.split(",");
			if (quantIdArr.length > 1) {
				sbBuilder.append(" and (f_buy_uid not in (").append(quantitativeId).append(") or f_sell_uid not in (").append(quantitativeId).append("))");
			} else {
				sbBuilder.append(" and (f_buy_uid <> ").append(quantitativeId).append(" or f_sell_uid <> ").append(quantitativeId).append(")");
			}
		}
		Session session = memberTradeDetailDao.getSession();

		String sql = "select sum(trade_count) from " + partition + "_" + transactionCode + "_member_trade_detail " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1";
		SQLQuery querycount = session.createSQLQuery(sql);
		if (map != null && map.size() > 0)
			for (String key : map.keySet()) {
				querycount.setParameter(key, map.get(key));
			}

		double total = ConvertUtil.toDouble(querycount.uniqueResult(), 0);
		return total;
	}

	public JSONArray getAllJsonTopN(String partition, String bqCode, int n) {
	
		List<Object[]> list = memberTradeDetailDao.getAllTopN(partition, bqCode, n);
		JSONArray array = new JSONArray();
		try {
			for (Object[] objs : list) {
				JSONObject json = new JSONObject();
				BigDecimal amount = new BigDecimal(objs[2].toString());
				BigDecimal price = new BigDecimal(objs[1].toString());

				json.put("date", objs[0]); 	// trade_time
				json.put("price", price); 	// trade_average_price
				json.put("amount", amount); // trade_count
				json.put("tid", objs[3]); 	// trade_id
				String type = "";
				if (objs[4].toString().equals("1"))
					type = "buy";
				if (objs[4].toString().equals("2"))
					type = "sell";

				int buyID = ConvertUtil.toInt(objs[5], 0);
				int sellID = ConvertUtil.toInt(objs[6], 0);

				BigDecimal zc = BigDecimal.ZERO;
				if (buyID == 1 || buyID == 3) { // 买者开仓
					if (sellID == 1 || sellID == 3) { // 卖者开仓
						zc = zc.add(amount.multiply(new BigDecimal(2)));
					}
				} else {
					if (sellID == 2 || sellID == 4) { // 卖者开仓
						zc = zc.subtract(amount.multiply(new BigDecimal(2)));
					}
				}

				json.put("type", type); // trade_trans_dirction
				json.put("change", zc);
				array.put(json);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}
	
	@SuppressWarnings("unchecked")
	public List<CountVo> finCountByTime(String partition, String bqCode, int type, long startTime, long endTime) {
		Session session = memberTradeDetailDao.getSession();
		StringBuilder sql = new StringBuilder("SELECT IFNULL(SUM(trade_count), 0) countAmount, IFNULL(SUM(trade_price * trade_count)/SUM(trade_count), 0) averagePrice, IFNULL(trade_trans_dirction, ");
		sql.append(type).append(") type, IFNULL(transaction_code, '").append(bqCode).append("') bqCode from ");
		sql.append(partition + "_" + bqCode).append("_member_trade_detail WHERE trade_time BETWEEN ").append(startTime).append(" AND ").append(endTime).append(" AND f_buy_uid <> f_sell_uid ");
		if (type == 1) 
			sql.append(" AND trade_trans_dirction = 1 AND f_buy_uid <> 528155 ");
		else if (type == 2) 
			sql.append(" AND trade_trans_dirction = 2 AND f_sell_uid <> 528155 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(com.btc100.btc_q.vo.CountVo.class));
		return query.list();
	}
	
	public CountVo findScalpingByTime(String partition, String bqCode, long startTime, long endTime) {
		Session session = memberTradeDetailDao.getSession();
		StringBuilder sql = new StringBuilder("SELECT IFNULL(SUM(trade_count), 0) countAmount, IFNULL(SUM(trade_price * trade_count)/SUM(trade_count), 0) averagePrice ");
		sql.append(", IFNULL(transaction_code, '").append(bqCode).append("') bqCode from ").append(partition + "_" + bqCode).append("_member_trade_detail WHERE trade_time BETWEEN ");
		sql.append(startTime).append(" AND ").append(endTime).append(" AND f_buy_uid = 1 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(com.btc100.btc_q.vo.CountVo.class));
		return (CountVo) query.uniqueResult();
	}

	public PagerModel findDetailsByPara(String partition, String bqCode, int dirType) {
		Session session = memberTradeDetailDao.getSession();
		StringBuilder sql = new StringBuilder("SELECT IFNULL(SUM(t.trade_count), 0) countAmount, IFNULL(SUM(t.trade_price * t.trade_count)/SUM(t.trade_count), 0) averagePrice, IFNULL(t.trade_trans_dirction, ");
		StringBuilder countSql = new StringBuilder("SELECT count(*) FROM (SELECT IFNULL(SUM(t.trade_count), 0) countPrice ");
		
		sql.append(dirType).append(") type, IFNULL(transaction_code, '").append(bqCode).append("') bqCode, ");
		
		if (dirType == Contents.DIRECTION_BUY) 
			sql.append("f_buy_uid userId ");
		else if (dirType == Contents.DIRECTION_SELL) 
			sql.append("f_sell_uid userId ");
		
		sql.append(",t.trade_time tradeTime FROM ").append("(SELECT * FROM ").append(partition + "_" + bqCode).append("_member_trade_detail WHERE trade_trans_dirction = ").append(dirType);
		sql.append(" AND f_buy_uid <> 528155 UNION ALL SELECT * FROM ").append(partition + "_" + bqCode).append("_member_trade_detail WHERE trade_trans_dirction = ").append(dirType);
		sql.append(" AND f_buy_uid = 528155 AND f_sell_uid <> 528155) t GROUP BY t.f_buy_uid ORDER BY t.trade_time DESC LIMIT "+SystemContext.getOffset()+","+SystemContext.getPageSize());
		
		countSql.append("FROM ").append("(SELECT * FROM ").append(partition + "_" + bqCode).append("_member_trade_detail WHERE trade_trans_dirction = ").append(dirType);
		countSql.append(" AND f_buy_uid <> 528155 UNION ALL SELECT * FROM ").append(partition + "_" + bqCode).append("_member_trade_detail WHERE trade_trans_dirction = ").append(dirType);
		countSql.append(" AND f_buy_uid = 528155 AND f_sell_uid <> 528155) t GROUP BY t.f_buy_uid ORDER BY t.trade_time DESC) m ");
		
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(com.btc100.btc_q.vo.CountVo.class));
		Query countQuery = session.createSQLQuery(countSql.toString());
		@SuppressWarnings("rawtypes")
		List list = query.list();
		int total = ConvertUtil.toInt(countQuery.uniqueResult(), 0);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(list);
		pm.setTotal(total);
		return pm;
	}

	@Override
	public void save(String partition, NMemberTradeDetail tradeDetail) {
		memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
	}
	
	@Override
	public void deleteVTradeScalpingDetail(String partition, String vCode, long scalpingUid, long beforeTime) {
		Session session = memberTradeDetailDao.getSession();
		String sql = "delete from " + partition + "_" + vCode + "_member_trade_detail where f_sell_uid = :uid and f_buy_uid = :uid and trade_time < :time";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", scalpingUid);
		query.setParameter("time", beforeTime);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PagerModel findQuantitativeModel(String partition, String code, long startTime, long endTime, long quantitativeId) {
		PagerModel pm = new PagerModel();
		Session session = memberTradeDetailDao.getSession();

		String sql = "select d.transaction_code, d.f_buy_uid, d.trade_buy_type_id, d.f_buy_user_email, d.f_sell_uid, d.trade_sell_type_id, d.f_sell_user_email, d.trade_price, d.trade_count, t.trade_fee, d.trade_time"
				+ " from " + partition + "_" + code + "_member_trade_detail d," + partition + "_" + code + "_trade_log t"
				+ " where d.trade_time >=:startTime and d.trade_time <:endTime and t.trade_fee > 0 and d.f_buy_uid <> d.f_sell_uid"
				+ " and ( d.f_buy_trade_id = t.trade_id and d.f_sell_uid = :quantitativeId"
				+ " or d.f_sell_trade_id = t.trade_id and d.f_buy_uid = :quantitativeId )"
				+ " ORDER BY d.trade_time desc limit " + SystemContext.getOffset() + ","
				+ SystemContext.getPageSize();
		String sqlcount = "select count(1) from " + partition + "_" + code + "_member_trade_detail d," + partition + "_" + code + "_trade_log t"
				+ " where d.trade_time >=:startTime and d.trade_time <:endTime and t.trade_fee > 0 and d.f_buy_uid <> d.f_sell_uid"
				+ " and ( d.f_buy_trade_id = t.trade_id and d.f_sell_uid = :quantitativeId"
				+ " or d.f_sell_trade_id = t.trade_id and d.f_buy_uid = :quantitativeId )";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("quantitativeId", quantitativeId);
		query.setParameter("startTime", startTime);
		query.setParameter("endTime", endTime);
		List<Object> list = query.list();

		SQLQuery querycount = session.createSQLQuery(sqlcount);
		querycount.setParameter("quantitativeId", quantitativeId);
		querycount.setParameter("startTime", startTime);
		querycount.setParameter("endTime", endTime);

		int total = ConvertUtil.toInt(querycount.uniqueResult(), 0);
		pm.setDatas(list);
		pm.setTotal(total);
		return pm;
	}

	@Override
	public BigDecimal getQuantitativeFee(String partition, String code, long startTime, long endTime, long quantitativeId) {
		Session session = memberTradeDetailDao.getSession();

		String sql = "select IFNULL(SUM(t.trade_fee), 0) fee from " + partition + "_" + code + "_member_trade_detail d," + partition + "_" + code + "_trade_log t"
				+ " where d.trade_time >=:startTime and d.trade_time <:endTime and t.trade_fee > 0"
				+ " and ( d.f_buy_trade_id = t.trade_id and d.f_sell_uid = :quantitativeId"
				+ " or d.f_sell_trade_id = t.trade_id and d.f_buy_uid = :quantitativeId )"
				+ " and f_buy_uid <> f_sell_uid ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("quantitativeId", quantitativeId);
		query.setParameter("startTime", startTime);
		query.setParameter("endTime", endTime);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getTradeAmountWithQuant(String partition, String code, long startTime, long endTime, long quantUid, int type) {
		Session session = memberTradeDetailDao.getSession();

		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal bakAmount = BigDecimal.ZERO;
		long moring = (long) TimeUtil.getTimesmorning();
		if (startTime + 30 * 86400l < moring) {
			String sql = "SELECT IFNULL(SUM(m.trade_price * m.trade_count), 0) AS amount FROM " + partition + "_" + code + "_member_trade_detail_bak m"
					+ " WHERE m.f_buy_uid <> m.f_sell_uid AND m.trade_time >= :startTime AND m.trade_time < :endTime";

			if (type > 0) {
				String buyUid = "m.f_buy_uid = :userId";
				String sellUid = "m.f_sell_uid = :userId";
				sql += " AND ((m.trade_trans_dirction = 1 AND " + (type == 1 ? sellUid : buyUid) + ") OR"
						+ " (m.trade_trans_dirction = 2 AND " + (type == 1 ? buyUid : sellUid) + "))";
			} else {
				sql += " AND (m.f_sell_uid = :userId OR m.f_buy_uid = :userId)";
			}

			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("startTime", startTime);
			query.setParameter("endTime", endTime);
			query.setParameter("userId", quantUid);
			bakAmount = (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
		}
		String sql = "SELECT IFNULL(SUM(m.trade_price * m.trade_count), 0) AS amount FROM " + partition + "_" + code + "_member_trade_detail m"
				+ " WHERE m.f_buy_uid <> m.f_sell_uid AND m.trade_time >= :startTime AND m.trade_time < :endTime";

		if (type > 0) {
			String buyUid = "m.f_buy_uid = :userId";
			String sellUid = "m.f_sell_uid = :userId";
			sql += " AND ((m.trade_trans_dirction = 1 AND " + (type == 1 ? sellUid : buyUid) + ") OR"
					+ " (m.trade_trans_dirction = 2 AND " + (type == 1 ? buyUid : sellUid) + "))";
		} else {
			sql += " AND (m.f_sell_uid = :userId OR m.f_buy_uid = :userId)";
		}

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("startTime", startTime);
		query.setParameter("endTime", endTime);
		query.setParameter("userId", quantUid);
		amount = (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
		amount = amount.add(bakAmount);
		return amount;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PagerModel findTradeWithQuant(String partition, String code, long startTime, long endTime, long quantUid, int type) {
		PagerModel pm = new PagerModel();
		Session session = memberTradeDetailDao.getSession();

		long moring = (long) TimeUtil.getTimesmorning();
		String tableName = partition + "_" + code + "_member_trade_detail d ";
		if (startTime + 30 * 86400l < moring) {
			tableName = partition + "_" + code + "_member_trade_detail_bak d ";
		}
		String sql = "select d.transaction_code, d.f_buy_uid, d.trade_buy_type_id, d.f_buy_user_email, d.f_sell_uid, d.trade_sell_type_id, d.f_sell_user_email, d.trade_price, d.trade_count, d.trade_time"
				+ " from " + tableName + " WHERE d.f_buy_uid <> d.f_sell_uid AND d.trade_time >= :startTime AND d.trade_time < :endTime";
		if (type > 0) {
			String buyUid = "d.f_buy_uid = :userId";
			String sellUid = "d.f_sell_uid = :userId";
			sql += " AND ((d.trade_trans_dirction = 1 AND " + (type == 1 ? sellUid : buyUid) + ") OR"
					+ " (d.trade_trans_dirction = 2 AND " + (type == 1 ? buyUid : sellUid) + "))";
		} else {
			sql += " AND (d.f_sell_uid = :userId OR d.f_buy_uid = :userId)";
		}
		sql += " ORDER BY d.trade_time DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize();
		
		String sqlcount = "select count(1) from " + tableName + " WHERE d.f_buy_uid <> d.f_sell_uid AND d.trade_time >= :startTime AND d.trade_time < :endTime";
		if (type > 0) {
			String buyUid = "d.f_buy_uid = :userId";
			String sellUid = "d.f_sell_uid = :userId";
			sqlcount += " AND ((d.trade_trans_dirction = 1 AND " + (type == 1 ? sellUid : buyUid) + ") OR"
					+ " (d.trade_trans_dirction = 2 AND " + (type == 1 ? buyUid : sellUid) + "))";
		} else {
			sqlcount += " AND (d.f_sell_uid = :userId OR d.f_buy_uid = :userId)";
		}
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", quantUid);
		query.setParameter("startTime", startTime);
		query.setParameter("endTime", endTime);
		List<Object> list = query.list();

		SQLQuery querycount = session.createSQLQuery(sqlcount);
		querycount.setParameter("userId", quantUid);
		querycount.setParameter("startTime", startTime);
		querycount.setParameter("endTime", endTime);

		int total = ConvertUtil.toInt(querycount.uniqueResult(), 0);
		pm.setDatas(list);
		pm.setTotal(total);
		return pm;
	}

	@Override
	public void createNMemberTradeDetailBak(String partition, String code) {
		memberTradeDetailDao.createMemberTradeDetailBak(partition, code);
	}

	@Override
	public void insertNMemberTradeDetailBak(String partition, String code, long endTime) {
		memberTradeDetailDao.insertMemberTradeDetailBak(partition, code, endTime);
		memberTradeDetailDao.deleteMemberTradeDetail(partition, code, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findNextFromId(String partition, String code, long id, int limit) {
		Session session = memberTradeDetailDao.getSession();
		StringBuffer sql = new StringBuffer(
				"select trade_time AS date, trade_price AS price, trade_count AS amount,id as tid, trade_trans_dirction AS tradType,trade_buy_type_id AS buyID,trade_sell_type_id AS SellID from "
						+ partition + "_" + code + "_member_trade_detail where id > :id order by id desc limit " + limit);
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("id", id);
		
		return query.list();
	}

	@Override
	public JSONArray readAllJsonTopN(String partition, String bqCode, int n) {
		List<Object[]> list = memberTradeDetailDao.getAllTopN(partition, bqCode, n);
		JSONArray array = new JSONArray();
		try {
			for (Object[] objs : list) {
				JSONObject json = new JSONObject();
				BigDecimal amount = new BigDecimal(objs[2].toString());
				BigDecimal price = new BigDecimal(objs[1].toString());

				json.put("date", objs[0]); 	// trade_time
				json.put("price", price); 	// trade_average_price
				json.put("amount", amount); // trade_count
				json.put("tid", objs[3]); 	// trade_id
				String type = "";
				if (objs[4].toString().equals("1"))
					type = "buy";
				if (objs[4].toString().equals("2"))
					type = "sell";

				int buyID = ConvertUtil.toInt(objs[5], 0);
				int sellID = ConvertUtil.toInt(objs[6], 0);

				BigDecimal zc = BigDecimal.ZERO;
				if (buyID == 1 || buyID == 3) { // 买者开仓
					if (sellID == 1 || sellID == 3) { // 卖者开仓
						zc = zc.add(amount.multiply(new BigDecimal(2)));
					}
				} else {
					if (sellID == 2 || sellID == 4) { // 卖者开仓
						zc = zc.subtract(amount.multiply(new BigDecimal(2)));
					}
				}

				json.put("type", type); // trade_trans_dirction
				json.put("change", zc);
				array.put(json);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}
}
