package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSuggestDao;
import com.btc100.btc_q.entity.BQSuggest;
import com.btc100.btc_q.service.BQSuggestService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqSuggestService")
public class BQSuggestServiceImpl implements BQSuggestService {

	@Autowired
	private BQSuggestDao<BQSuggest> bqSuggestDao;

	public PagerModel findAllSugg(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder("From BQSuggest");
		if (state == 0 || state == 1 || state == 2) {
			params.put("state", state);
			hql.append(" WHERE f_sug_state = :state");
		}
		hql.append(" ORDER BY f_sug_state ASC,f_sug_time DESC");
		return bqSuggestDao.searchHql(hql.toString(), params);
	}

	public PagerModel findSuggByUserId(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", id);
		return bqSuggestDao.searchHql("From BQSuggest where f_user_id =:userId order by f_sug_state ASC,f_sug_time DESC", params);
	}

	public void updateApplyByParas(long sid, int state, String sugReply) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", sid);
		params.put("state", state);
		params.put("sugReply", sugReply);
		params.put("dateTime", bqSuggestDao.getUnix_TIMESTAMP());
		bqSuggestDao.updateCondition("UPDATE BQSuggest SET f_sug_state = :state, f_sug_reply = :sugReply, f_sug_endtime = :dateTime WHERE id = :id", params);
	}

	public long getTime() {
		return bqSuggestDao.getUnix_TIMESTAMP();
	}

	public void save(BQSuggest suggest) {
		bqSuggestDao.save(suggest);
	}

}
