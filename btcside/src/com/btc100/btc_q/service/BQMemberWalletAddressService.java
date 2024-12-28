package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberWalletAddress;

public interface BQMemberWalletAddressService {

	void save(BQMemberWalletAddress bqwa);

	void deleteById(long id);

	void updateById(long id, String address, String name);

	void updateDefaultById(long id, int currencyId);

	/**
	 * 设置默认地址
	 * @param fUserId 用户ID
	 * @param lid 地址ID
	 */
	public void defaultAddr(long fUserId, long id, int currencyId);
	
	
	/************************************ for read only **************************************/
	
	List<BQMemberWalletAddress> readByUserId(long fUserId, int currencyId);

	List<BQMemberWalletAddress> readErc20ByUserId(long fUserId, int currencyId);
	
	List<BQMemberWalletAddress> readTrc20ByUserId(long fUserId, int currencyId);
	
	BQMemberWalletAddress readDefaultAddress(long fUserId, int currencyId);
	
	BQMemberWalletAddress readById(long id);
	
	int readCountByUserAndAddress(long fUserId, String address, int currencyId);
	
	int readCountByUserAndCurrency(long fUserId, int currencyId);
}
