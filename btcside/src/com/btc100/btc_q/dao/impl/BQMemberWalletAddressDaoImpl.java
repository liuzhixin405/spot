package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQMemberWalletAddressDao;

@Repository("bqMemberWalletAddressDao")
public class BQMemberWalletAddressDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberWalletAddressDao<T> { }
