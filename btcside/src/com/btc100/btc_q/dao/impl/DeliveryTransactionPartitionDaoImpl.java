package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.DeliveryTransactionPartitionDao;

@Repository("deliveryTransactionPartitionDao")
public class DeliveryTransactionPartitionDaoImpl<T> extends BaseDaoImpl<T> implements DeliveryTransactionPartitionDao<T> {

}
