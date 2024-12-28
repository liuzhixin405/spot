package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.entity.NTransactionPartition;

@Repository("nTransactionPartitionDao")
public class NTransactionPartitionDaoImpl extends BaseDaoImpl<NTransactionPartition> implements com.btc100.btc_q.dao.NTransactionPartitionDao<NTransactionPartition> {

	@Override
	public int insert(String partition, NTransactionPartition part) {
		Session session = getSession();
		String sql = "insert into " + partition + "_transaction_partition "
				+ "(name_cn,name_en,name_jp,name_kr,name_hk,sort,codes) "
				+ "values (?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, part.getName_cn());
		query.setString(1, part.getName_en());
		query.setString(2, part.getName_jp());
		query.setString(3, part.getName_kr());
		query.setString(4, part.getName_hk());
		query.setInteger(5, part.getSort());
		query.setString(6, part.getCodes());
		
		return query.executeUpdate();
	}

	@Override
	public int update(String partition, NTransactionPartition part) {
		Session session = getSession();
		String sql = "update " + partition + "_transaction_partition set "
				+ "name_cn=?,name_en=?,name_jp=?,name_kr=?,name_hk=?,sort=?,codes=? where id=?";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, part.getName_cn());
		query.setString(1, part.getName_en());
		query.setString(2, part.getName_jp());
		query.setString(3, part.getName_kr());
		query.setString(4, part.getName_hk());
		query.setInteger(5, part.getSort());
		query.setString(6, part.getCodes());
		query.setInteger(7, part.getId());
		
		return query.executeUpdate();
	}

}
