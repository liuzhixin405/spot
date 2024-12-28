package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQCommonDataDao;

@Repository("bqCommonDataDao")
public class BQCommonDataDaoImpl<T> extends BaseDaoImpl<T> implements BQCommonDataDao<T> { 
	
	public List<T> findByCode(String scode){
		String hql="FROM BQCommonData WHERE content=:code";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("code", scode);
		return find(hql, params);
	}
	
	public int UpdateData(int id,BigDecimal data){
		String hql="UPDATE BQCommonData SET data=data+:data WHERE id=:id";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("data", data);
		params.put("id", id);
		
		return updateCondition(hql, params);
	}
	
}
