package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NTransactionTypeDao;
import com.btc100.btc_q.entity.NTransactionType;

@Repository("nTransactionTypeDao")
public class NTransactionTypeDaoImpl extends BaseDaoImpl<NTransactionType> implements NTransactionTypeDao<NTransactionType> {

	@Override
	public NTransactionType getTransTypeByCode(String partition, String code){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return getBySql("select * from " + partition + "_transaction_type t where t.type_code = :code ", params);
	}
	
	@Override
	public NTransactionType getTransTypeById(String partition, int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return getBySql("select * from " + partition + "_transaction_type t where t.type_id = :id ", params);
	}

	@Override
	public int insert(String partition, NTransactionType transType) {
		Session session = getSession();
		String sql = "insert into " + partition + "_transaction_type "
				+ "(type_name,type_code,default_multiple,default_multiple_state,default_coefficient,is_using,index_address,index_value,"
				+ "min_kc_nums,min_float_price) values (?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, transType.getType_name());
		query.setString(1, transType.getType_code());
		query.setString(2, transType.getDefault_multiple());
		query.setString(3, transType.getDefault_multiple_state());
		query.setBigDecimal(4, transType.getDefault_coefficient());
		query.setBoolean(5, transType.getIs_using());
		query.setString(6, transType.getIndex_address());
		query.setBigDecimal(7, transType.getIndex_value());
		query.setBigDecimal(8, transType.getMin_kc_nums());
		query.setBigDecimal(9, transType.getMin_float_price());
		
		return query.executeUpdate();
	}

	@Override
	public int update(String partition, NTransactionType transType) {
		Session session = getSession();
		String sql = "update " + partition + "_transaction_type set "
				+ "type_name=?,type_code=?,default_multiple=?,default_multiple_state=?,default_coefficient=?,is_using=?,index_address=?,index_value=?,"
				+ "min_kc_nums=?,min_float_price=?,max_kdc_nums=?,max_kkc_nums=? where type_id=?";

		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, transType.getType_name());
		query.setString(1, transType.getType_code());
		query.setString(2, transType.getDefault_multiple());
		query.setString(3, transType.getDefault_multiple_state());
		query.setBigDecimal(4, transType.getDefault_coefficient());
		query.setBoolean(5, transType.getIs_using());
		query.setString(6, transType.getIndex_address());
		query.setBigDecimal(7, transType.getIndex_value());
		query.setBigDecimal(8, transType.getMin_kc_nums());
		query.setBigDecimal(9, transType.getMin_float_price());
		query.setBigDecimal(10, transType.getMax_kdc_nums());
		query.setBigDecimal(11, transType.getMax_kkc_nums());
		query.setInteger(12, transType.getType_id());
		
		return query.executeUpdate();
	}

}
