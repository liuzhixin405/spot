package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.UserDao;

@Repository("userDao")
public class UserDaoImpl<User> extends BaseDaoImpl<User> implements UserDao<User> {

}
