package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQIndexStateDao;
import com.btc100.btc_q.entity.BQIndexState;
import com.btc100.btc_q.service.BQIndexStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bqIndexStateService")
public class BQIndexStateServiceImpl implements BQIndexStateService {
    @Autowired
    private BQIndexStateDao<BQIndexState> bqIndexStateDao;

    @Override
    public int updateStateByName(String indexName, int state) {
        return bqIndexStateDao.updateStateByName(indexName, state);
    }

    @Override
    public BQIndexState read() {
        return bqIndexStateDao.get("From BQIndexState");
    }

	@Override
	public BQIndexState get() {
		return bqIndexStateDao.get("From BQIndexState");
	}
}
