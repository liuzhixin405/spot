package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMemberRecharge;

public interface WalletPlatformService {

	BQAccount getUserAccountByAddress(String address, int currencyId);
	
	BQAccount getUserAccountByErc20Address(String address, int currencyId);
	
	BQAccount getUserAccountByTrc20Address(String address, int currencyId);
	
	BQMemberRecharge getRechargeRecord(String txid, int currencyId);
	
	List<BQMemberRecharge> getWithdrawCoinRecords(List<BQCurrencyType> list);
	
	long getUnuseAddressCount(int coinId);
	
	long getErc20AddressCount();
	
	long getTrc20AddressCount();
	
	void saveChargeRecord(int coinId, BQAccount ua, String txid, String address, BigDecimal amount, boolean erc20usdt, boolean trc20usdt) throws Exception;

	void confirmChargeRecord(BQAccount ua, BQMemberRecharge record, int direction, String note) throws Exception;
	
	boolean updateWithdrawCoinRecordDownload(BQMemberRecharge record) throws Exception;
	
	void saveCoinWalletAddress(int coinId, List<String> addresses, boolean erc20, boolean trc20) throws Exception;
	
	BQMemberRecharge getProcessingRecord(String serno) throws Exception;

	void updateWithdrawRecordAndAccount(Boolean success, String serno, String txid, String message) throws Exception;
	
	boolean isAccountAbnormal(BQMemberRecharge recode);

}
