package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.VIEWUsermanagerDao;

@Repository("viewUsermanagerDao")
public class VIEWUsermanagerDaoImpl<T> extends BaseDaoImpl<T> implements VIEWUsermanagerDao<T> { }
