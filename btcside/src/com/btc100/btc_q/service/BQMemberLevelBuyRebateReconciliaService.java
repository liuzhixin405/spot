package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQMemberLevelBuyRebateReconcilia;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberLevelBuyRebateReconciliaService {
	
	BQMemberLevelBuyRebateReconcilia readByUserId(long uid);
	
	BQMemberLevelBuyRebateReconcilia getByUserId(long uid);
	
	BigDecimal readSumBfxLock(long parent, int userType);
	
	BigDecimal readSumSelfBfxLockByUser(long uid);
	
	PagerModel readSubUsers(long uid, int userType);
	
}

