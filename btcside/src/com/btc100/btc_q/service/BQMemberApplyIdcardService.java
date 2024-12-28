package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberApplyIdcard;

public interface BQMemberApplyIdcardService {

	public void saveApplyIdCard(BQMemberApplyIdcard applyIdcard);

	public void deleteApplyByUserid(long userId);

}
