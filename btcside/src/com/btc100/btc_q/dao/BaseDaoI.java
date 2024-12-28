package com.btc100.btc_q.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.btc100.btc_q.vo.PagerModel;

/**
 * 基本接口
 * 
 */
public interface BaseDaoI<T> {

	public Session getSession();

	public Long getUnix_TIMESTAMP();

	T get(Class<T> clazz, String id);

	T get(Class<T> clazz, Long id);

	T get(Class<T> clazz, Integer id);

	T get(String hql);

	T get(String hql, Map<String, Object> params);
	
	T getBySql(String sql);

	T getBySql(String sql, Map<String, Object> params);

	List<T> getAllList(Class clazz);

	List<T> find(String hql);

	List<T> find(String hql, Map<String, Object> params);

	List<T> find(String hql, int page, int rows);

	List<T> find(String hql, Map<String, Object> params, int page, int rows);

	int count(String hql);

	int count(String hql, Map<String, Object> params);

	BigDecimal sum(String hql);
	
	BigDecimal sum(String hql, Map<String, Object> params);
	
	BigDecimal sumBySql(String hql);
	
	BigDecimal sumBySql(String hql, Map<String, Object> params);
	
	Serializable save(T o);

	void update(T o);

	int updateCondition(String hql, Map<String, Object> params);

	void delete(T o);

	int deleteCondition(String hql, Map<String, Object> params);
	
	int updateConditionBySql(String sql, Map<String, Object> params);

	int deleteConditionBySql(String hql, Map<String, Object> params);

	Query createQuery(String hql);
	
	Query createSQLQuery(String hql);

	Query queryparams(String hql, Map<String, Object> params);

	// ////////////////////////////////////以下是分页
	PagerModel searchHql(String hql);

	// PagerModel searchHql(String hql,Object datas);
	PagerModel searchHql(String hql, Map<String, Object> params);

	PagerModel searchHql(String hql, int offset, int pageSize);

	// PagerModel searchHql(String hql,Object datas,int offset,int pageSize);
	PagerModel searchHql(String hql, Map<String, Object> params, int offset, int pageSize);
	
	// ////////////////////////////////////以下是分页
	PagerModel searchSql(String hql);

	// PagerModel searchHql(String hql,Object datas);
	PagerModel searchSql(String hql, Map<String, Object> params);

	PagerModel searchSql(String hql, int offset, int pageSize);

	// PagerModel searchHql(String hql,Object datas,int offset,int pageSize);
	PagerModel searchSql(String hql, Map<String, Object> params, int offset, int pageSize);

	String getQuery(String hql);
	
	List<T> findBySql(String sql);
	
	List<T> findBySql(String sql, Map<String, Object> params);
	
	List<T> findBySql(String hql, int page, int rows);
	
	List<T> findBySql(String hql, Map<String, Object> params, int page, int rows);
	
	long getlastTime(String sql);
	
	Class<T> getEntityClass();
}
