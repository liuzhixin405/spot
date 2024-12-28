package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberFavoriteMarket;

public interface BQMemberFavoriteMarketService {

	void save(BQMemberFavoriteMarket market);
	
	void delete(BQMemberFavoriteMarket market);
	
	void update(BQMemberFavoriteMarket market);
	
	
	/************************************ for read only **************************************/
	
	BQMemberFavoriteMarket readByUserId(long uId, int type);
	
	BQMemberFavoriteMarket readByUserId(long uId, String partition);
	
}
