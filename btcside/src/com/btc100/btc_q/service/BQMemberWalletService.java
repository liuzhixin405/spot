package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberWallet;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberWalletService {
	
	void save(BQMemberWallet bqw);

	
	/************************************ for read only **************************************/
	
	List<BQMemberWallet> readErc20AddressByUser(long uid);
	
	List<BQMemberWallet> readTrc20AddressByUser(long uid);
	
	List<BQMemberWallet> readById(long fUserId,int currencyId);
	
	PagerModel readAll(int currencyId);
	
	PagerModel readByAddress(String address,int currencyId);

	PagerModel readPageById(long fUserId, int currencyId);
	
	BQMemberWallet readByAddress(String address);
}
