package com.btc100.btc_q.dao.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQWalletQueueBtcDao;
import com.btc100.btc_q.entity.BQWalletQueueBtc;

@Repository("bqWalletQueueBtcDao")
public class BQWalletQueueBtcDaoImpl<BQWalletQueueBtc> extends BaseDaoImpl<BQWalletQueueBtc> implements BQWalletQueueBtcDao<BQWalletQueueBtc> {

	private static final Logger logger = Logger.getLogger(BQWalletQueueBtcDaoImpl.class);

	public Map<String, Object> insertDatas(List<com.btc100.btc_q.entity.BQWalletQueueBtc> list) {
		Session session = getSession();
		String sql = "INSERT INTO `bq_wallet_queue_btc` (`wallet_address`, `wallet_pic`, `currency_type_id`, `erc20`, `trc20`) VALUES ";

		int icount = 0, isuccess = 0;
		StringBuilder sbBuilder = new StringBuilder();
		for (com.btc100.btc_q.entity.BQWalletQueueBtc bq : list) {
			sbBuilder.append("('" + bq.getWallet_address() + "','" + bq.getWallet_pic() + "','" + bq.getCurrency_type_id() 
			+ "'," + (bq.getErc20() ? "true" : "false") + "," + (bq.getTrc20() ? "true" : "false") +"),");
			icount++;

			if (icount % 100 == 99) {
				String str = sbBuilder.toString();
				if (str.length() > 0) {
					if (str.endsWith(",")) {
						str = str.substring(0, str.length() - 1);
					}
					sbBuilder = new StringBuilder();
					SQLQuery query = session.createSQLQuery(sql + str);
					isuccess += query.executeUpdate();
				}
			}
		}

		if (icount % 100 != 99) {
			String str = sbBuilder.toString();
			if (str.length() > 0) {
				if (str.endsWith(",")) {
					str = str.substring(0, str.length() - 1);
				}
				// sbBuilder = new StringBuilder();
				SQLQuery query = session.createSQLQuery(sql + str);
				isuccess += query.executeUpdate();
			}
		}

		logger.info("共导入了" + icount + "条, 成功了" + isuccess + "条");

		Map<String,Object> map=new HashMap<String, Object>();
		map.put("total", icount);
		map.put("success", isuccess);
		
		return map;
	}

	@Override
	public long getCount(int currencyId) {
		Session session = getSession();
		String sql = "select count(*) from bq_wallet_queue_btc where currency_type_id = :currencyId";		
		SQLQuery query = session.createSQLQuery(sql);   
		query.setInteger("currencyId", currencyId);
        return ((BigInteger) query.uniqueResult()).longValue();
	}

	@Override
	public long getErc20Count() {
		Session session = getSession();
		String sql = "select count(*) from bq_wallet_queue_btc where erc20 = true";		
		SQLQuery query = session.createSQLQuery(sql);
        return ((BigInteger) query.uniqueResult()).longValue();
	}
	
	@Override
	public long getTrc20Count() {
		Session session = getSession();
		String sql = "select count(*) from bq_wallet_queue_btc where trc20 = true";		
		SQLQuery query = session.createSQLQuery(sql);
        return ((BigInteger) query.uniqueResult()).longValue();
	}
}
