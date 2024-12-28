package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberPaywordDao;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.service.BQMemberPaywordService;

@Service("bqMemberPaywordService")
public class BQMemberPaywordServiceImpl implements BQMemberPaywordService {
	@Autowired
	private BQMemberPaywordDao<BQMemberPayword> bqMemberPaywordDao;

	public BQMemberPayword readById(long id) {
		return bqMemberPaywordDao.get(BQMemberPayword.class, id);
	}

	public void save(BQMemberPayword bqmP) {
		bqMemberPaywordDao.save(bqmP);
	}

	public int UpdateSZStateById(long id) {
		if (bqMemberPaywordDao.get(BQMemberPayword.class, id) == null) {
			BQMemberPayword bqMemberPayword = new BQMemberPayword(id, "", "", 0, 1);
			bqMemberPaywordDao.save(bqMemberPayword);
			return 1;
		} else {
			String sql = "UPDATE BQMemberPayword SET f_readszms=1 WHERE f_user_id=:id";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", id);
			return bqMemberPaywordDao.updateCondition(sql, params);
		}
	}
	
	@Override
	public void resetFailedTimes(long fUserId) {
		String sql = "UPDATE BQMemberPayword SET failed_times = 0 WHERE f_user_id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", fUserId);
		
		bqMemberPaywordDao.updateCondition(sql, params);
	}

	@Override
	public void addFailedTimes(long fUserId) {
		String sql = "UPDATE BQMemberPayword SET failed_times = failed_times + 1 WHERE f_user_id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", fUserId);
		
		bqMemberPaywordDao.updateCondition(sql, params);
	}

}
