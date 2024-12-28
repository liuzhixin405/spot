package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQSuggest;
import com.btc100.btc_q.vo.PagerModel;

public interface BQSuggestService {

	public PagerModel findAllSugg(int state);

	public PagerModel findSuggByUserId(long id);

	public void updateApplyByParas(long sid, int state, String sugReply);

	long getTime();

	void save(BQSuggest suggest);

}
