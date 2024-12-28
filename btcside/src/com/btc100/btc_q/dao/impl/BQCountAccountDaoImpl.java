package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQCountAccountDao;

@Repository("bqCountAccountDao")
public class BQCountAccountDaoImpl<T> extends BaseDaoImpl<T> implements BQCountAccountDao<T> { }
