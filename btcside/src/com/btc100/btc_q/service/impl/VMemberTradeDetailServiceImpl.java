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

import com.btc100.btc_q.dao.VMemberTradeDetailDao;
import com.btc100.btc_q.entity.VMemberTradeDetail;
import com.btc100.btc_q.service.VMemberTradeDetailService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.CountVo;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("vMemberTradeDetailService")
public class VMemberTradeDetailServiceImpl implements VMemberTradeDetailService {

	@Autowired
	private VMemberTradeDetailDao<VMemberTradeDetail> vMemberTradeDetailDao;

	public PagerModel findModel(String transactionCode, int buyid, int sellid) {
		PagerModel pm = new PagerModel();

		Session session = vMemberTradeDetailDao.getSession();
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

		String sql = "select * from " + transactionCode + "_v_member_trade_detail " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1 ORDER BY id desc limit :start,:page ";
		String sqlcount = "select count(1) from " + transactionCode + "_v_member_trade_detail " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1";
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

		query.addEntity(com.btc100.btc_q.entity.VMemberTradeDetail.class);
		pm.setDatas(query.list());
		pm.setTotal(total);
		return pm;
	}

	public double findTotal(String transactionCode, int buyid, int sellid) {

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

		Session session = vMemberTradeDetailDao.getSession();

		String sql = "select sum(trade_count) from " + transactionCode + "_v_member_trade_detail " + sbBuilder.toString() + " and f_buy_uid > 1 and f_sell_uid > 1";
		SQLQuery querycount = session.createSQLQuery(sql);
		if (map != null && map.size() > 0)
			for (String key : map.keySet()) {
				querycount.setParameter(key, map.get(key));
			}

		double total = ConvertUtil.toDouble(querycount.uniqueResult(), 0);
		return total;
	}

	public JSONArray getAllJsonTopN(String vCode, int n) {
	
		List<Object[]> list = vMemberTradeDetailDao.getAllTopN(vCode, n);
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
	
	public JSONArray readAllJsonTopN(String vCode, int n) {
		
		List<Object[]> list = vMemberTradeDetailDao.getAllTopN(vCode, n);
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

	public CountVo findScalpingByTime(String vCode, long startTime, long endTime) {
		Session session = vMemberTradeDetailDao.getSession();
		StringBuilder sql = new StringBuilder("SELECT IFNULL(SUM(trade_count), 0) countAmount, IFNULL(SUM(trade_price * trade_count)/SUM(trade_count), 0) averagePrice ");
		sql.append(", IFNULL(transaction_code, '").append(vCode).append("') bqCode from ").append(vCode).append("_v_member_trade_detail WHERE trade_time BETWEEN ");
		sql.append(startTime).append(" AND ").append(endTime).append(" AND f_buy_uid = 1 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(com.btc100.btc_q.vo.CountVo.class));
		return (CountVo) query.uniqueResult();
	}

	@Override
	public void save(VMemberTradeDetail tradeDetail) {
		vMemberTradeDetailDao.insertTradeDetail(tradeDetail);
	}
	
	@Override
	public void deleteVTradeScalpingDetail(String vCode, long scalpingUid, long beforeTime){
		Session session = vMemberTradeDetailDao.getSession();
		String sql = "delete from " + vCode + "_v_member_trade_detail where f_sell_uid = :uid and f_buy_uid = :uid and trade_time < :time";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", scalpingUid);
		query.setParameter("time", beforeTime);
		query.executeUpdate();
	}

	@Override
	public void createMemberTradeDetailBak(String code) {
		vMemberTradeDetailDao.createMemberTradeDetailBak(code);
	}

	@Override
	public void insertMemberTradeDetailBak(String code, long endTime) {
		vMemberTradeDetailDao.insertMemberTradeDetailBak(code, endTime);
		vMemberTradeDetailDao.deleteMemberTradeDetail(code, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findNextFromId(String code, long id, int limit) {
		Session session = vMemberTradeDetailDao.getSession();
		StringBuffer sql = new StringBuffer(
				"select trade_time AS date, trade_price AS price, trade_count AS amount,id as tid, trade_trans_dirction AS tradType,trade_buy_type_id AS buyID,trade_sell_type_id AS SellID from "
						+ code + "_v_member_trade_detail where id > :id order by id desc limit " + limit);
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("id", id);
		
		return query.list();
	}
}
