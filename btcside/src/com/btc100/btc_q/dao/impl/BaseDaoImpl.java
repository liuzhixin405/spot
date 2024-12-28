package com.btc100.btc_q.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.dao.BaseDaoI;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

/**
 * 基本方法实现
 * 
 * @param <T>
 */
@Repository("baseDao")
public class BaseDaoImpl<T> implements BaseDaoI<T> {

	@Autowired
	private SessionFactory sessionFactory;

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public T get(Class<T> clazz, String id) {
		return (T) getSession().get(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public T get(Class<T> clazz, Long id) {
		return (T) getSession().get(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public T get(Class<T> clazz, Integer id) {
		return (T) getSession().get(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public T get(String hql) {
		List<T> l = createQuery(hql).setFirstResult(0).setMaxResults(1).list();
		if (l != null && l.size() > 0)
			return l.get(0);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public T getBySql(String sql) {
		List<T> l = createSQLQuery(sql).addEntity(getEntityClass()).setFirstResult(0).setMaxResults(1).list();
		if (l != null && l.size() > 0)
			return l.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public T get(String hql, Map<String, Object> params) {
		Query q = queryparams(hql, params);
		q.setFirstResult(0);
		q.setMaxResults(1);
		List<T> l = q.list();
		if (l != null && l.size() > 0)
			return l.get(0);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public T getBySql(String sql, Map<String, Object> params) {
		Query q = querySQLParams(sql, params).addEntity(getEntityClass());
		q.setFirstResult(0);
		q.setMaxResults(1);
		List<T> l = q.list();
		if (l != null && l.size() > 0)
			return l.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql) {
		return createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> find(String hql, Map<String, Object> params) {
		Query q = queryparams(hql, params);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql, int page, int rows) {
		Query q = createQuery(hql);
		return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql, Map<String, Object> params, int page, int rows) {
		Query q = queryparams(hql, params);
		return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findBySql(String hql, int page, int rows) {
		Query q = createSQLQuery(hql).addEntity(getEntityClass());
		return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findBySql(String hql, Map<String, Object> params, int page, int rows) {
		Query q = querySQLParams(hql, params).addEntity(getEntityClass());
		return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	public int count(String hql) {
		Query q = createQuery(hql);
		return ((Long) q.uniqueResult()).intValue();
	}

	public int count(String hql, Map<String, Object> params) {
		Query q = queryparams(hql, params);
		return ((Long) q.uniqueResult()).intValue();
	}

	public BigDecimal sum(String hql) {
		Query q = createQuery(hql);
		Object o = q.uniqueResult();
		if (o == null)
			return BigDecimal.ZERO;

		return new BigDecimal(o.toString());
	}

	public BigDecimal sum(String hql, Map<String, Object> params) {
		Query q = queryparams(hql, params);
		Object o = q.uniqueResult();
		if (o == null)
			return BigDecimal.ZERO;

		return new BigDecimal(o.toString());
	}
	
	public BigDecimal sumBySql(String hql) {
		Query q = createSQLQuery(hql);
		Object o = q.uniqueResult();
		if (o == null)
			return BigDecimal.ZERO;

		return new BigDecimal(o.toString());
	}
	
	public BigDecimal sumBySql(String hql, Map<String, Object> params) {
		Query q = querySQLParams(hql, params);
		Object o = q.uniqueResult();
		if (o == null)
			return BigDecimal.ZERO;

		return new BigDecimal(o.toString());
	}

	public Serializable save(T o) {
		return getSession().save(o);
	}

	public void update(T o) {
		getSession().update(o);
	}

	public void delete(T o) {
		getSession().delete(o);
	}

	public int updateCondition(String hql, Map<String, Object> params) {
		Query q = queryparams(hql, params);
		return q.executeUpdate();
	}
	
	public int updateConditionBySql(String sql, Map<String, Object> params) {
		Query q = querySQLParams(sql, params);
		return q.executeUpdate();
	}

	public int deleteCondition(String hql, Map<String, Object> params) {
		Query q = queryparams(hql, params);
		return q.executeUpdate();
	}
	
	public int deleteConditionBySql(String sql, Map<String, Object> params) {
		Query q = querySQLParams(sql, params);
		return q.executeUpdate();
	}

	public Query createQuery(String hql) {
		return getSession().createQuery(hql);
	}

	public SQLQuery createSQLQuery(String sql) {
		return getSession().createSQLQuery(sql);
	}

	public Query queryparams(String hql, Map<String, Object> params) {
		Query q = createQuery(hql);
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				Object obj = params.get(key);
				if (obj instanceof Collection<?>) {
					q.setParameterList(key, (Collection<?>) obj);
				} else if (obj instanceof Object[]) {
					q.setParameterList(key, (Object[]) obj);
				} else {
					q.setParameter(key, obj);
				}
			}
		}
		return q;
	}
	
	public SQLQuery querySQLParams(String sql, Map<String, Object> params) {
		SQLQuery q = createSQLQuery(sql);
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				Object obj = params.get(key);
				if (obj instanceof Collection<?>) {
					q.setParameterList(key, (Collection<?>) obj);
				} else if (obj instanceof Object[]) {
					q.setParameterList(key, (Object[]) obj);
				} else {
					q.setParameter(key, obj);
				}
			}
		}
		return q;
	}

	@SuppressWarnings("unchecked")
	public List<T> getAllList(Class clazz) {
		return getSession().createCriteria(clazz).list();
	}

	public Long getUnix_TIMESTAMP() {
		BigInteger bi = (BigInteger) getSession().createSQLQuery("select UNIX_TIMESTAMP() ").uniqueResult();
		return bi.longValue();

	}

	// /////////////////////////////下面是分页用法
	public PagerModel searchHql(String hql) {
		return searchHql(hql, null, SystemContext.getOffset(), SystemContext.getPageSize());
	}

	public PagerModel searchHql(String hql, Map<String, Object> params) {
		return searchHql(hql, params, SystemContext.getOffset(), SystemContext.getPageSize());
	}

	public PagerModel searchHql(String hql, int offset, int pageSize) {
		return searchHql(hql, null, offset, pageSize);
	}

	public PagerModel searchHql(String hql, Map<String, Object> params, int offset, int pageSize) {
		String countHql = getQuery(hql);
		Query query = getSession().createQuery(countHql);
		Query query1 = getSession().createQuery(hql);

		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				query.setParameter(key, params.get(key));
				query1.setParameter(key, params.get(key));
			}
		}

		int total = ((Long) query.uniqueResult()).intValue();
		List list = query1.setFirstResult(offset).setMaxResults(pageSize).list();
		PagerModel pm = new PagerModel();
		pm.setDatas(list);
		pm.setTotal(total);
		return pm;
	}
	
	public PagerModel searchSql(String hql) {
		return searchSql(hql, null, SystemContext.getOffset(), SystemContext.getPageSize());
	}

	public PagerModel searchSql(String hql, Map<String, Object> params) {
		return searchSql(hql, params, SystemContext.getOffset(), SystemContext.getPageSize());
	}

	public PagerModel searchSql(String hql, int offset, int pageSize) {
		return searchSql(hql, null, offset, pageSize);
	}

	public PagerModel searchSql(String hql, Map<String, Object> params, int offset, int pageSize) {
		String countHql = getQuery(hql);
		Query query = getSession().createSQLQuery(countHql);
		Query query1 = getSession().createSQLQuery(hql).addEntity(getEntityClass());

		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				query.setParameter(key, params.get(key));
				query1.setParameter(key, params.get(key));
			}
		}

		int total = Convert.toInt(query.uniqueResult() + "", 1);
		List list = query1.setFirstResult(offset).setMaxResults(pageSize).list();
		PagerModel pm = new PagerModel();
		pm.setDatas(list);
		pm.setTotal(total);
		return pm;
	}

	public String getQuery(String hql) {
		int index = hql.indexOf("from");
		if (index == -1)
			index = hql.indexOf("FROM");
		if (index == -1)
			index = hql.indexOf("From");
		if (index != -1) {
			return "select count(*) " + hql.substring(index);
		}
		return "";
		// throw new SystemException("查询语句出错！");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBySql(String sql) {
		Query query = null;
		query = getSession().createSQLQuery(sql).addEntity(getEntityClass());
		List<T> list = null;
		try {
			list = query.list();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBySql(String sql, Map<String, Object> params) {
		Query query = null;
		query = getSession().createSQLQuery(sql).addEntity(getEntityClass());
		Iterator<String> paramsKey = params.keySet().iterator();
		for (String key = ""; paramsKey.hasNext(); ) {
			key = paramsKey.next();
			if ("start".equals(key)) {
				query.setFirstResult((int) params.get(key));
			} else if ("size".equals(key)) {
				query.setMaxResults((int) params.get(key));
			} else {
				Object obj = params.get(key);
				if (obj instanceof Object[])  {
					query.setParameterList(key, (Object[])obj);
				} else {
					query.setParameter(key, obj);
				}
			}
		}
		
		return query.list();
	}
	
	@Override
	public long getlastTime(String sql) {
		BigInteger lastTime = (BigInteger) getSession().createSQLQuery(sql).uniqueResult();
		return lastTime.longValue();
	}
	
	private Class<T> entityClass = null;
	
	@SuppressWarnings("unchecked")
	public Class<T> getEntityClass() {
		if (null == entityClass) {
			entityClass = (Class<T>) getSuperClassGenricType(getClass(), 0);
		}
		return entityClass;
	}
	
	private static final Class<?> getSuperClassGenricType(final Class<?> clazz, final int index) {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		return (Class<?>) params[index];
	}

}
