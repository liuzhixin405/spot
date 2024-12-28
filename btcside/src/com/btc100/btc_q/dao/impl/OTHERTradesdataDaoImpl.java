package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.OTHERTradesdataDao;
import com.btc100.btc_q.entity.OTHERTradesdata;

@Repository("otherTradesdataDao")
public class OTHERTradesdataDaoImpl<T> extends BaseDaoImpl<T> implements OTHERTradesdataDao<T> {

	public List<T> findOneByFlag(int flag) {
		String sql = "FROM OTHERTradesdata WHERE d_flag='" + flag + "'";
		return super.find(sql);
	}

	public OTHERTradesdata findSumByFlag(int flag) {
		String sql = "SELECT ifnull(sum(d_amount*d_price),0) count,ifnull(sum(d_amount),0) amount FROM other_tradesdata WHERE d_flag='" + flag + "'";
		SQLQuery query = super.getSession().createSQLQuery(sql);
		List<Object[]> list = query.list();
		if(list!=null){
			Object[] objs=list.get(0);
			OTHERTradesdata data=new OTHERTradesdata();
			data.setD_amount(new BigDecimal(objs[1].toString()));
			data.setD_price(new BigDecimal(objs[0].toString()));
			return data;
		}
		
		return null;
	}

	public BigDecimal findMaxByFlag(int flag) {
		String sql = "SELECT ifnull(max(d_price),0) d_price FROM other_tradesdata WHERE d_flag='" + flag + "'";
		SQLQuery query = super.getSession().createSQLQuery(sql);
		return new BigDecimal(query.uniqueResult().toString());
	}

	public BigDecimal findMinByFlag(int flag) {
		String sql = "SELECT ifnull(min(d_price),0) d_price FROM other_tradesdata WHERE d_flag='" + flag + "'";
		SQLQuery query = super.getSession().createSQLQuery(sql);
		return new BigDecimal(query.uniqueResult().toString());
	}

	public T findLastByFlag(int flag) {
		String sql = "FROM OTHERTradesdata WHERE d_flag='" + flag + "' order by d_oid desc";
		List<T> list = super.find(sql, 1, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	public int deleteByFlagAndTime(int flag, long time) {
		String sql = "DELETE FROM OTHERTradesdata WHERE d_flag='" + flag + "' AND d_time<" + time;
		return super.deleteCondition(sql, null);
	}

}
