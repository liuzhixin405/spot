package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.*;
import com.btc100.btc_q.dao.*;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.util.CommonUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.BalanceAccountVO;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberRechargeService")
public class BQMemberRechargeServiceImpl implements BQMemberRechargeService {
	@Autowired
	private BQMemberRechargeDao<BQMemberRecharge> bqMemberRechargeDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private BQCommonCreditLogDao<BQCommonCreditLog> bqCommonCreditLogDao;
	@Autowired
	private BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;
	@Autowired
	private BQMemberInviteRebateLogDao<BQMemberInviteRebateLog> bqMemberInviteRebateLogDao;
	@Autowired
	private DeliveryMemberInviteRebateLogDao<DeliveryMemberInviteRebateLog> deliveryMemberInviteRebateLogDao;
	@Autowired
	private BQAccountContractDao<BQAccountContract> bqAccountContractDao;
	@Autowired
	private DeliveryAccountContractDao<DeliveryAccountContract> deliveryAccountContractDao;
	@Autowired
	private ContractAccountLogDao<ContractAccountLog> contractAccountLogDao;
	@Autowired
	private DeliveryContractAccountLogDao<DeliveryContractAccountLog> deliveryContractAccountLogDao;
	@Autowired
	private BQMemberTransactionDao<BQMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private DeliveryMemberTransactionDao<DeliveryMemberTransaction> deliveryMemberTransactionDao;
	@Autowired
	private TradeLogDao<TradeLog> tradeLogDao;
	@Autowired
	private DeliveryTradeLogDao<DeliveryTradeLog> deliveryTradeLogDao;
	@Autowired
	private TradeLogReconciliaDao<TradeLogReconcilia> tradeLogReconciliaDao;
	@Autowired
	private DeliveryTradeLogReconciliaDao<DeliveryTradeLogReconcilia> deliveryTradeLogReconciliaDao;
	@Autowired
	private PendingDelegationDao<PendingDelegation> pendingDelegationDao;
	@Autowired
	private DeliveryPendingDelegationDao<DeliveryPendingDelegation> deliveryPendingDelegationDao;
	@Autowired
	private PendingDelegationCacheDao<PendingDelegationCache> pendingDelegationCacheDao;
	@Autowired
	private DeliveryPendingDelegationCacheDao<DeliveryPendingDelegationCache> deliveryPendingDelegationCacheDao;
	@Autowired
	private BQExchangeRecordDao<BQExchangeRecord> bqExchangeRecordDao;
	@Autowired
	private BQMineRecordDao<BQMineRecord> bqMineRecordDao;
	@Autowired
	private BQBonusRecordDao<BQBonusRecord> bqBonusRecordDao;
	@Autowired
	private SpotTradeLogDao<SpotTradeLog> spotTradeLogDao;
	@Autowired
	private BQSpotMarketCoinPairDao<BQSpotMarketCoinPair> spotMarketCoinPairDao;
	@Autowired
	private SpotPendingDelegationDao<SpotPendingDelegation> spotPendingDelegationDao;
	@Autowired
	private SpotPendingDelegationCacheDao<SpotPendingDelegationCache> spotPendingDelegationCacheDao;
	@Autowired
	private VTransactionDao<VTransaction> vTransactionDao;
	@Autowired
	private VAccountContractDao<VAccountContract> vAccountContractDao;
	@Autowired
	private VContractAccountLogDao<VContractAccountLog> vContractAccountLogDao;
	@Autowired
	private VMemberTransactionDao<VMemberTransaction> vMemberTransactionDao;
	@Autowired
	private VTradeLogDao<VTradeLog> vTradeLogDao;
	@Autowired
	private VPendingDelegationDao<VPendingDelegation> vPendingDelegationDao;
	@Autowired
	private VPendingDelegationCacheDao<VPendingDelegationCache> vPendingDelegationCacheDao;
	@Autowired
	private BQBftRewardRecordDao<BQBftRewardRecord> bqBftRewardRecordDa;
	@Autowired
	private BQTransactionDao<BQTransaction> bqTransactionDao;
	@Autowired
	private DeliveryTransactionDao<DeliveryTransaction> deliveryTransactionDao;
	@Autowired
	private FundingFeeRecordDao<FundingFeeRecord> fundingFeeRecordDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQBfxFeeSettingDao<BQBfxFeeSetting> bqBfxFeeSettingDao;
	@Autowired
	private BQBfxFeeRecordDao<BQBfxFeeRecord> bqBfxFeeRecordDao;
	@Autowired
	private BQMemberLevelBuyAmountLogDao<BQMemberLevelBuyAmountLog> bqMemberLevelBuyAmountLogDao; 
	@Autowired
	private BQSubMemberTransferLogDao<BQSubMemberTransferLog> bqSubMemberTransferLogDao;
	@Autowired
	private BQExchangeBfcRecordDao<BQExchangeBfcRecord> bqExchangeBfcRecordDao;
	@Autowired
	private BQCurrencyTypeDao<BQCurrencyType> bqCurrencyTypeDao;
	@Autowired
	private BQTransferWithOtcRecordDao<BQTransferWithOtcRecord> bqTransferWithOtcRecordDao;
	@Autowired
	private BQMemberWalletDao<BQMemberWallet> bqMemberWalletDao;
	@Autowired
	private BQWalletTransBtcDao<BQWalletTransBtc> bqWalletTransBtcDao;
	@Autowired
	private NContractPartitionDao<NContractPartition> nContractPartitionDao;
	@Autowired
	private NAccountContractDao<NAccountContract> nAccountContractDao;
	@Autowired
	private NContractAccountLogDao<NContractAccountLog> nContractAccountLogDao;
	@Autowired
	private NTransactionDao<NTransaction> nTransactionDao;
	@Autowired
	private NTradeLogReconciliaDao<NTradeLogReconcilia> nTradeLogReconciliaDao;
	@Autowired
	private NTradeLogDao<NTradeLog> nTradeLogDao;
	@Autowired
	private NFundingFeeRecordDao<NFundingFeeRecord> nFundingFeeRecordDao;
	@Autowired
	private NMemberTransactionDao<NMemberTransaction> nMemberTransactionDao;
	@Autowired
	private NPendingDelegationDao<NPendingDelegation> nPendingDelegationDao;
	@Autowired
	private NPendingDelegationCacheDao<NPendingDelegationCache> nPendingDelegationCacheDao;
	@Autowired
	private BQLoanRecordDao<BQLoanRecord> bqLoanRecordDao;
	@Autowired
	private BQLuckObtainRecordDao<BQLuckObtainRecord> bqLuckObtainRecordDao;


	public void save(BQMemberRecharge bqr, int currencyId) {
		bqMemberRechargeDao.save(bqr);
	}

	public PagerModel readRechargeAll(long id, int currencyId) {
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();

		sbBuilder.append("from BQMemberRecharge b where b.f_user_id=:id and currency_type_id=:currencyId and b.operation=1");
		map.put("id", id);
		map.put("currencyId", currencyId);

		String hql = " order by b.record_id desc ";
		hql = sbBuilder.toString() + hql;

		return bqMemberRechargeDao.searchHql(hql, map);
	}

	public PagerModel readWithdrawAll(long id, int currencyId) {
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();

		sbBuilder.append("from BQMemberRecharge b where b.f_user_id=:id and currency_type_id=:currencyId and b.operation=2");
		map.put("id", id);
		map.put("currencyId", currencyId);
		
		String hql = " order by b.record_id desc ";
		hql = sbBuilder.toString() + hql;

		return bqMemberRechargeDao.searchHql(hql, map);
	}

	// 所有用户
	public PagerModel readRecords(int type, int isadmin, int currencyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "FROM BQMemberRecharge b WHERE b.operation=:type AND b.receice_card_type=" + isadmin
				 + " and currency_type_id=:currencyId ORDER BY b.state ASC,b.record_id DESC";
		map.put("type", type);
		map.put("currencyId", currencyId);
		return bqMemberRechargeDao.searchHql(sql, map);
	}

	// 得到某个用户的充值记录
	public PagerModel readCzByUserID(Long uid, String uemail, String uaddr, int itype, int isadmin, int currencyId, String state) {
		String swhere = "";

		if (uid == null || uid <= 0) {
			if (uemail != null && !uemail.isEmpty()) {
				List<BQMember> bqMember = bqMemberDao.findsByEmail(uemail);
				StringBuilder sids = new StringBuilder();
				for (BQMember ba : bqMember) {
					sids.append(ba.getF_user_id());
					sids.append(",");
				}

				String sid = sids.toString();
				if (sid.endsWith(",")) {
					sid = sid.substring(0, sid.length() - 1);
				}

				swhere = " AND b.f_user_id in (" + sid + ")";
			}
		} else {
			swhere = " AND b.f_user_id = " + uid;
		}
		if (uaddr != null && !uaddr.isEmpty()) {
			swhere = swhere + " AND b.receice_card_no='" + uaddr + "'";
		}

		if (itype > 0) {
			swhere = swhere + " AND b.operation=" + itype;
		}
		
		if (isadmin > 0) {
			swhere = swhere + " AND b.receice_card_type=" + isadmin;
		}

		if (!StringUtil.isEmpty(state)) {
			swhere = swhere + " AND b.state=" + state;
		}
		
		String sql = "FROM BQMemberRecharge b WHERE "
				+ " currency_type_id=" + currencyId + swhere
				+ " ORDER BY b.record_id DESC";
		PagerModel pm = bqMemberRechargeDao.searchHql(sql);
		pm.setFlag(bqMemberRechargeDao.findSUMByUserID(uid, itype).toString());

		return pm;
	}
	
	// 得到某个用户的充值记录
	public PagerModel readCzByUserID(Long uid, String uemail, String uaddr, int itype, int isadmin, int currencyId) {
		return readCzByUserID(uid, uemail, uaddr, itype, isadmin, currencyId, "");
	}

	// 二次操作充值
	public int rechargeCZ(long id, int state, String remark) throws ServiceException {
		if (state == Contents.RECHARGE_STATE_ING) {
			return 0;
		}
		long timenow = DateUtil.DateToUnix(new Date());
		BQMemberRecharge recharge = bqMemberRechargeDao.get(BQMemberRecharge.class, id);
		if (recharge.getState() != Contents.RECHARGE_STATE_ING)
			return 0;

		Long uid = recharge.getF_user_id();
		BigDecimal amount = recharge.getAmount();

		if (state == Contents.RECHARGE_STATE_ED) { // 确认

			String sql = "UPDATE BQMemberRecharge SET state=:state,remark=:remark,confirm_time=:time WHERE record_id=:id AND state=0";

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("state", state);
			map.put("time", timenow);
			map.put("id", id);
			map.put("remark", remark);

			int iresult = bqMemberRechargeDao.updateCondition(sql, map);
			if (iresult > 0) {
				BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, recharge.getCurrency_type_id());
				if (bqAccountDao.updateAmount(uid, recharge.getCurrency_type_id(), amount, BigDecimal.ZERO) > 0) {
					BQAccountAmountLog pAccountLog = new BQAccountAmountLog("用户" + uid + "充值金额" + amount.toString(), recharge.getCurrency_type_id(), 1, 1, bqAccount.getBtc(), amount,
							bqAccount.getBtc().add(amount), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), timenow, uid, RandomUtil.getTransNo(), "", id);
					bqAccountAmountLogDao.save(pAccountLog);
					
					// 判断用户是否是第一次充值
					BQCommonCreditLog creditLog = bqCommonCreditLogDao.getByUserIdAndType(uid, Contents.CREDIT_TYPE_FIRST_RECHARGE);
					if (creditLog == null) { // 第一次充值
						creditLog = new BQCommonCreditLog();
						creditLog.setCredit_type_id(Contents.CREDIT_TYPE_FIRST_RECHARGE);
						creditLog.setCredits(DATA_CreditNumber.CREDIT_ONE_FIRST_RECHARGE);
						creditLog.setDateline(bqMemberRechargeDao.getUnix_TIMESTAMP());
						creditLog.setF_user_id(uid);
						creditLog.setTitle("首次充值");
						bqCommonCreditLogDao.save(creditLog);
						
						BQMember user = bqMemberDao.get(BQMember.class, uid);
						if (user.getF_user_oms() == 0) {
							// 更新用户的积分
							long ucredits = user.getF_user_credits();
							long newCredits = ucredits + creditLog.getCredits();
							long vipId = bqMemberLevelDao.getLevelByCredits(newCredits, false);
							bqMemberDao.addCredits(creditLog.getCredits(), vipId, uid);
						}
					}
				} else {
					throw new ServiceException("用户资金变动失败");
				}

				return 1;
			}
		} else if (state == Contents.RECHARGE_STATE_NOT) { // 拒绝

			String sql = "UPDATE BQMemberRecharge b SET state=:state,remark=:remark,confirm_time=:time WHERE record_id=:id AND state=0";

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("state", state);
			map.put("time", timenow);
			map.put("id", id);
			map.put("remark", remark);

			int iresult = bqMemberRechargeDao.updateCondition(sql, map);
			return iresult;
		}

		return 0;
	}

	// 二次确认提现
	public int rechargeTX(long id, int state, String remark) throws ServiceException {
		if (state == Contents.RECHARGE_STATE_ING) {
			return 0;
		}

		long timenow = DateUtil.DateToUnix(new Date());
		BQMemberRecharge recharge = bqMemberRechargeDao.get(BQMemberRecharge.class, id);
		if (recharge.getState() != Contents.RECHARGE_STATE_ING 
				&& recharge.getState() != Contents.RECHARGE_STATE_MANUAL )
			return 0;

		Long uid = recharge.getF_user_id();
		BigDecimal amount = recharge.getAmount();

		if (state == Contents.RECHARGE_STATE_ED) { // 确认
			String sql = "UPDATE BQMemberRecharge b SET state=:state,remark=:remark,confirm_time=:time WHERE record_id=:id AND (state=0 or state=4)";

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("state", state);
			map.put("time", timenow);
			map.put("id", id);
			map.put("remark", remark);

			int iresult = bqMemberRechargeDao.updateCondition(sql, map);
			if (iresult > 0) {
				if (bqAccountDao.updateAmount(uid, recharge.getCurrency_type_id(), BigDecimal.ZERO, amount.negate()) <= 0) { // 扣除冻结
					throw new ServiceException("用户资金会产生负冻结");
				} else {
					BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, recharge.getCurrency_type_id());
					BQAccountAmountLog pAccountLog = new BQAccountAmountLog("二次确认提现，扣除冻结，数量：" + amount.toString(), recharge.getCurrency_type_id(), 1, -1,
							bqAccount.getBtc(), amount, bqAccount.getBtc(), bqAccount.getBtc_locked().add(amount), bqAccount.getBtc_locked(), 
							bqAccountAmountLogDao.getUnix_TIMESTAMP(), uid, RandomUtil.getTransNo(), "(Remain):" + amount + ";(Gain):0", recharge.getRecord_id());
					bqAccountAmountLogDao.save(pAccountLog);
					
					return 1;
				}
			}
		} else if (state == Contents.RECHARGE_STATE_NOT) { // 拒绝

			String sql = "UPDATE BQMemberRecharge b SET state=:state,remark=:remark,confirm_time=:time WHERE record_id=:id AND (state=0 or state=4)";

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("state", state);
			map.put("time", timenow);
			map.put("id", id);
			map.put("remark", remark);

			int iresult = bqMemberRechargeDao.updateCondition(sql, map);
			if (iresult > 0) {
				if (bqAccountDao.updateAmount(uid, recharge.getCurrency_type_id(), amount, amount.negate()) <= 0) { // 扣除冻结
					throw new ServiceException("用户资金会产生负冻结");
				} else {
					
					BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, recharge.getCurrency_type_id());
					BQAccountAmountLog pAccountLog = new BQAccountAmountLog("二次拒绝提现，恢复冻结，数量：" + amount.toString(), recharge.getCurrency_type_id(), 1, 1,
							bqAccount.getBtc().subtract(amount), amount, bqAccount.getBtc(), bqAccount.getBtc_locked().add(amount), bqAccount.getBtc_locked(), 
							bqAccountAmountLogDao.getUnix_TIMESTAMP(), uid, RandomUtil.getTransNo(), "(Remain):" + amount + ";(Gain):0", recharge.getRecord_id());
					bqAccountAmountLogDao.save(pAccountLog);
					
					return 1;
				}
			}
		} else if (state == Contents.RECHARGE_STATE_CHECKED) { // 标注为人工审核
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("address", recharge.getReceice_card_no());
			BQMemberWallet walletAddress = bqMemberWalletDao.get(" from BQMemberWallet t where t.wallet_address=:address", params);
			if (walletAddress != null) {
				BQAccount ua = bqAccountDao.getByUerAndCurrencyId(uid, recharge.getCurrency_type_id());
				updateUserAccount(ua, BigDecimal.ZERO, recharge.getAmount().negate(), recharge.getAmount(), -1, 2, "站内人工审核提币扣除。");
				
				String txid = "站内提币_" + CommonUtil.getUUID();
				if (updateWithdrawCoinRecord(recharge, 1, txid, "站内提币成功") > 0) {
					
					BQCurrencyType currency = bqCurrencyTypeDao.get(BQCurrencyType.class, recharge.getCurrency_type_id());
					if (currency == null) return 1;
					
					BigDecimal amt = recharge.getAmount().subtract(recharge.getFee());
					String symbol = currency.getType_name();
					if (walletAddress.getErc20() && recharge.getCurrency_type_id() == Contents.TRADE_CURRENCY_TYPE_ID) {
						symbol = "USDT(ERC20)";
					} else if (walletAddress.getTrc20() && recharge.getCurrency_type_id() == Contents.TRADE_CURRENCY_TYPE_ID) {
						symbol = "USDT(TRC20)";
					}
					charge(currency, txid, recharge.getReceice_card_no(), symbol, amt);
					confirmCharge(currency, txid, currency.getType_name(), 1);
				}
			} else {
			
				String sql = "UPDATE BQMemberRecharge b SET remark=:remark,state=:state WHERE record_id=:id AND (state=0 or state=4)";
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("remark", "已审核");
				map.put("id", id);
				map.put("state", 0);
				
				return bqMemberRechargeDao.updateCondition(sql, map);
			}
		}

		return 0;
	}
	
	public synchronized void charge(BQCurrencyType currencyType, String txid, String address, String symbol, BigDecimal amount) {
		try {
			boolean isErc20usdt = false;
			boolean isTrc20usdt = false;
			if (symbol.equalsIgnoreCase("USDT(ERC20)")) {
				symbol = "USDT";
				isErc20usdt = true;
			}
			
			if (symbol.equalsIgnoreCase("USDT(TRC20)")) {
				symbol = "USDT";
				isTrc20usdt = true;
			}
			
			String hql = "from BQMemberRecharge r where r.wallet_trade_id = :txid and r.currency_type_id = :currencyId and r.operation = 1";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("txid", txid);
			params.put("currencyId", currencyType.getType_id());
			
			BQMemberRecharge rechargeCoinRecord = bqMemberRechargeDao.get(hql, params);
			if (null != rechargeCoinRecord) {
				return;
			}
			
			BQAccount ua = null;
			if (isErc20usdt) {
				Map<String, Object> pms = new HashMap<String, Object>();
				pms.put("address", address);
				BQMemberWallet accountAddress = bqMemberWalletDao.get("from BQMemberWallet a where a.wallet_address = :address and erc20 = true", pms);
				if (null == accountAddress) {
					return;
				}
				
				ua = bqAccountDao.getByUerAndCurrencyId(accountAddress.getF_user_id(), currencyType.getType_id());
			} else if (currencyType.getTrc20() && isTrc20usdt) {
				Map<String, Object> pms = new HashMap<String, Object>();
				pms.put("address", address);
				BQMemberWallet accountAddress = bqMemberWalletDao.get("from BQMemberWallet a where a.wallet_address = :address and trc20 = true", pms);
				if (null == accountAddress) {
					return;
				}
				
				ua = bqAccountDao.getByUerAndCurrencyId(accountAddress.getF_user_id(), currencyType.getType_id());
			} else {
				Map<String, Object> pms = new HashMap<String, Object>();
				pms.put("address", address);
				pms.put("currencyId", currencyType.getType_id());
				BQMemberWallet accountAddress = bqMemberWalletDao.get("from BQMemberWallet a where a.wallet_address = :address and currency_type_id = :currencyId and erc20 = false", pms);
				if (null == accountAddress) {
					return;
				}
				
				ua =  bqAccountDao.getByUerAndCurrencyId(accountAddress.getF_user_id(), accountAddress.getCurrency_type_id());
			}
			
			if (null == ua || ua.getF_user_id() <= 0) {
				return;
			}
			
			buildSaveRechargeRecord(currencyType, currencyType.getType_id(), ua, txid, address, amount, isErc20usdt, isTrc20usdt);
		} catch (Exception e) {
			
		}
		
	}
	
	private BQMemberRecharge buildSaveRechargeRecord(BQCurrencyType currency, int coinId, BQAccount ua, String txid, String address, BigDecimal amount, boolean erc20usdt, boolean trc20usdt) {
		long time = bqMemberRechargeDao.getUnix_TIMESTAMP();
		BQMemberRecharge recharge = new BQMemberRecharge();
		
		if (currency == null) return recharge;
		
		BigDecimal fee = currency.getRecharge_fee();
		if (erc20usdt) {
			fee = currency.getRecharge_fee_erc20();
		} else if (trc20usdt) {
			fee = currency.getRecharge_fee_trc20();
		}
		
		recharge.setCurrency_type_id(coinId);
		recharge.setOperation(1);
		recharge.setFee(fee);
		recharge.setAmount(amount.subtract(recharge.getFee()));
		recharge.setReceice_card_type(1);	// 1.普通充值
		recharge.setReceice_card_no(address);
		recharge.setIp("");
		recharge.setDateline(time);
		recharge.setConfirm_time(time);
		recharge.setState(0);
		recharge.setRemark("自动充值");
		recharge.setF_user_id(ua.getF_user_id());
		recharge.setF_user_email(ua.getF_user_email());
		recharge.setWallet_trade_id(txid);
		bqMemberRechargeDao.save(recharge);

		BQWalletTransBtc o = new BQWalletTransBtc();
		o.setF_time((int)time);
		o.setF_address(address);
		o.setF_amount(amount);
		o.setF_txid(txid);
		o.setF_confirms(0);
		o.setF_wallet_id(recharge.getRecord_id() + "");
		bqWalletTransBtcDao.save(o);

		return recharge;
	}

	private synchronized void confirmCharge(BQCurrencyType currencyType, String txid, String symbol, Integer confirms) {
		try {
			if (0 >= confirms) {
				return;
			}
			
			if (symbol.equalsIgnoreCase("USDT(ERC20)")) {
				symbol = "USDT";
			}
			
			if (symbol.equalsIgnoreCase("USDT(TRC20)")) {
				symbol = "USDT";
			}
			
			String hql = "from BQMemberRecharge r where r.wallet_trade_id = :txid and r.currency_type_id = :currencyId and r.operation = 1";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("txid", txid);
			params.put("currencyId", currencyType.getType_id());
			
			BQMemberRecharge rechargeCoinRecord = bqMemberRechargeDao.get(hql, params);
			if (null == rechargeCoinRecord) {
				return;
			}
			
			if (rechargeCoinRecord.getState() != 0) {
				return;
			}
			
			BQAccount ua = bqAccountDao.getByUerAndCurrencyId(rechargeCoinRecord.getF_user_id(), rechargeCoinRecord.getCurrency_type_id());
			if (null == ua) {
				return;
			}
			
			try {
				confirmChargeRecord(ua, rechargeCoinRecord, 1, "站内充值确认到账。");
			} catch (Exception e) {
			}
			
		} catch (Exception e) {
		} 
	}
	
	public void confirmChargeRecord(BQAccount ua, BQMemberRecharge record, int direction, String note)  throws Exception {
		try {
			if (updateRechargeRecord(record, 1, "自动充值确认", ua) <= 0) {
				throw new Exception();
			}
			
			
			updateUserAccount(ua, record.getAmount(), BigDecimal.ZERO, record.getAmount(), 1, 1, "站内充值确认到账。");
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	private int updateRechargeRecord(BQMemberRecharge rechargeCoinRecord, int state, String note, BQAccount ua) {
		String sql = "UPDATE BQMemberRecharge SET state=:state, remark=:remark, confirm_time=:time WHERE record_id=:id AND state=0";

		long time = System.currentTimeMillis() / 1000;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", state);
		map.put("time", time);
		map.put("id", rechargeCoinRecord.getRecord_id());
		map.put("remark", note);

		return bqMemberRechargeDao.updateCondition(sql, map);
	}
	
	private void updateUserAccount(BQAccount ua, BigDecimal amount, BigDecimal frozen, BigDecimal changeAmount, int direction, int operation, String note) throws ServiceException {
		if (bqAccountDao.updateAmount(ua.getF_user_id(), ua.getCurrency_type_id(), amount, frozen) > 0) {
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog("用户" + ua.getF_user_id() + "充值金额" + amount.toString(), ua.getCurrency_type_id(),
					operation, direction, ua.getBtc(), amount, ua.getBtc().add(amount), ua.getBtc_locked(), ua.getBtc_locked().add(amount), bqAccountAmountLogDao.getUnix_TIMESTAMP(),
					ua.getF_user_id(), RandomUtil.getTransNo(), note, 0);
			bqAccountAmountLogDao.save(pAccountLog);
		} else {
			throw new ServiceException("用户资金异常");
		}
	}
	
	private int updateWithdrawCoinRecord(BQMemberRecharge record, int state, String txid, String remark) {
		if (record == null) return 0;
		
		String hql = "update BQMemberRecharge r set r.state = :state, r.remark = :remark, r.dateline = :dateline, r.wallet_trade_id = :wallet_trade_id where r.record_id = :id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("wallet_trade_id", txid);
		params.put("remark", remark);
		params.put("dateline", System.currentTimeMillis() / 1000);
		params.put("id", record.getRecord_id());
		
		
		return bqMemberRechargeDao.updateCondition(hql, params);
	}

	// 添加充值记录
	public int addCZ(long uid, int currencyId, BigDecimal amount, String address, String tradeID, int isadmin, String remark) throws ServiceException {
		int time = (int) (System.currentTimeMillis() / 1000);
		BQMember bqMember = bqMemberDao.get(BQMember.class, uid);
		
		BQMemberRecharge recharge = new BQMemberRecharge();
		recharge.setAmount(amount);
		recharge.setConfirm_time(time);
		recharge.setCurrency_type_id(currencyId);
		recharge.setDateline(time);
		recharge.setF_user_email(bqMember.getF_user_email());
		recharge.setF_user_id(uid);
		recharge.setFee(BigDecimal.ZERO);
		recharge.setIp("");
		recharge.setOperation(Contents.RECHARGE_TYPE_CZ);
		recharge.setReceice_card_no(address);
		recharge.setReceice_card_type(isadmin);
		recharge.setRemark(remark);
		BQCurrencyType currencyType = bqCurrencyTypeDao.get(BQCurrencyType.class, currencyId);
		if (currencyType != null && currencyType.getType_name().equalsIgnoreCase("usdt") && address.startsWith("0x")) {
			recharge.setUsdt_erc20(true);
		}if (currencyType != null && currencyType.getType_name().equalsIgnoreCase("usdt") && address.startsWith("T")) {
			recharge.setUsdt_trc20(true);
		}
		if (isadmin == Contents.RECHARGE_FLAG_ADMIN || isadmin == Contents.RECHARGE_FLAG_ADMIN_AWARD) {
			recharge.setState(Contents.RECHARGE_STATE_ED);
		} else
			recharge.setState(Contents.RECHARGE_STATE_ING);

		recharge.setWallet_trade_id(tradeID);

		bqMemberRechargeDao.save(recharge);

		if (isadmin == Contents.RECHARGE_FLAG_ADMIN || isadmin == Contents.RECHARGE_FLAG_ADMIN_AWARD) {
			if (bqAccountDao.updateAmount(uid, recharge.getCurrency_type_id(), amount, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("用户资金异常");
			}
			
			String title = "管理员充值，数量：";
			if (isadmin == Contents.RECHARGE_FLAG_ADMIN_AWARD) {
				title = "管理员奖励，数量：";
			}
			BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, recharge.getCurrency_type_id());
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title + amount.toString(), recharge.getCurrency_type_id(), 1, amount.compareTo(BigDecimal.ZERO),
					bqAccount.getBtc().subtract(amount), amount, bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
					bqAccountAmountLogDao.getUnix_TIMESTAMP(), uid, RandomUtil.getTransNo(), "(Remain):" + amount + ";(Gain):0", recharge.getRecord_id());
			bqAccountAmountLogDao.save(pAccountLog);
			
			// 判断用户是否是第一次充值
			BQCommonCreditLog creditLog = bqCommonCreditLogDao.getByUserIdAndType(uid, Contents.CREDIT_TYPE_FIRST_RECHARGE);
			if (creditLog == null) { // 第一次充值
				creditLog = new BQCommonCreditLog();
				creditLog.setCredit_type_id(Contents.CREDIT_TYPE_FIRST_RECHARGE);
				creditLog.setCredits(DATA_CreditNumber.CREDIT_ONE_FIRST_RECHARGE);
				creditLog.setDateline(bqMemberRechargeDao.getUnix_TIMESTAMP());
				creditLog.setF_user_id(uid);
				creditLog.setTitle("首次充值");
				bqCommonCreditLogDao.save(creditLog);
				
				BQMember user = bqMemberDao.get(BQMember.class, uid);
				if (user.getF_user_oms() == 0) {
					// 更新用户的积分
					long ucredits = user.getF_user_credits();
					long newCredits = ucredits + creditLog.getCredits();
					long vipId = bqMemberLevelDao.getLevelByCredits(newCredits, false);
					bqMemberDao.addCredits(creditLog.getCredits(), vipId, uid);
				}
			}
		}

		return 1;
	}

	// 添加提现记录
	public int addTX(long uid, int currencyId, BigDecimal amount, String address, String tradeID, int isadmin, String remark) throws ServiceException {
		int time = (int) (System.currentTimeMillis() / 1000);
		BQMember bqMember = bqMemberDao.get(BQMember.class, uid);

		BQMemberRecharge recharge = new BQMemberRecharge();
		recharge.setAmount(amount);
		recharge.setConfirm_time(time);
		recharge.setCurrency_type_id(currencyId);
		recharge.setDateline(time);
		recharge.setF_user_email(bqMember.getF_user_email());
		recharge.setF_user_id(uid);
		recharge.setFee(BigDecimal.ZERO);
		recharge.setIp("");
		recharge.setOperation(Contents.RECHARGE_TYPE_TX);
		recharge.setReceice_card_no(address);
		recharge.setReceice_card_type(isadmin);
		recharge.setRemark(remark);
		if (isadmin == Contents.RECHARGE_FLAG_ADMIN || isadmin == Contents.RECHARGE_FLAG_ADMIN_AWARD) {
			recharge.setState(Contents.RECHARGE_STATE_ED);
		} else
			recharge.setState(Contents.RECHARGE_STATE_ING);
		recharge.setWallet_trade_id(tradeID);

		bqMemberRechargeDao.save(recharge);

		if (isadmin == Contents.RECHARGE_FLAG_ADMIN || isadmin == Contents.RECHARGE_FLAG_ADMIN_AWARD) {
			if (bqAccountDao.updateAmount(uid, recharge.getCurrency_type_id(), amount.negate(), BigDecimal.ZERO) <= 0) {
				throw new ServiceException("用户资金异常");
			}
			
			String title = "管理员提现，数量：";
			if (isadmin == Contents.RECHARGE_FLAG_ADMIN_AWARD) {
				title = "管理员扣除奖励，数量：";
			}
			BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, recharge.getCurrency_type_id());
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title + amount.toString(), recharge.getCurrency_type_id(), 1, -1,
					bqAccount.getBtc().add(amount), amount, bqAccount.getBtc(), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
					bqAccountAmountLogDao.getUnix_TIMESTAMP(), uid, RandomUtil.getTransNo(), "(Remain):" + amount.negate() + ";(Gain):0", recharge.getRecord_id());
			bqAccountAmountLogDao.save(pAccountLog);
		}

		return 1;
	}

	public BQMemberRecharge readByTradeID(String tradeID, int currencyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tradeID", tradeID);
		map.put("currencyId", currencyId);
		return bqMemberRechargeDao.get("FROM BQMemberRecharge b WHERE wallet_trade_id = :tradeID and currency_type_id=:currencyId", map);
	}

	public PagerModel getRecordAll(long fUserId, int currencyId) {
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();

		sbBuilder.append("from BQMemberRecharge b where b.f_user_id=:id and b.currency_type_id=:currencyId");
		map.put("id", fUserId);
		map.put("currencyId", currencyId);
		
		String hql = " order by b.record_id desc ";
		hql = sbBuilder.toString() + hql;

		return bqMemberRechargeDao.searchHql(hql, map);
	}

	public List<BQMemberRecharge> readByUserAndTime(long fUserId, int currencyId, int operation, long from,
			String direct, long startTime, long endTime, int limit) {
		String hql = "from BQMemberRecharge where 1 = 1 ";
		
		Map<String, Object> map = new HashMap<String, Object>();
		if (fUserId > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", fUserId);
		}
		if (operation > 0) {
			hql += " and operation = :operation";
			map.put("operation", operation);
		}
		if (currencyId > 0) {
			hql += " and currency_type_id = :currencyId";
			map.put("currencyId", currencyId);
		}
		if (startTime > 0) {
			hql += " and dateline >= :startTime";
			map.put("startTime", startTime);
		}
		if (endTime > 0) {
			hql += " and dateline < :endTime";
			map.put("endTime", endTime);
		}
		if (from >= 0 && "prev".equals(direct)) {
			map.put("from", from);
			hql += " and record_id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			map.put("from", from);
			hql += " and record_id < :from";
		}
		
		hql += " order by record_id desc";
		return bqMemberRechargeDao.find(hql, map, 1, limit);
	}

	private BigDecimal findSumByIDAndTypeAndState(long fUserId, int type, int operation, int[] states, int currencyId) {
		String stateStr = "";
		if (states.length > 0) stateStr = " and (";
		for (int i = 0; i < states.length; i++) {
			if (i == 0) {
				stateStr += "t.state = :state" + i;
			} else {
				stateStr += " or t.state = :state" + i;
			}
		}
		if (states.length > 0) stateStr += ")";
		
		String sql="SELECT ifnull(SUM(t.amount), 0) from bq_member_recharge t  where 1=1 and t.f_user_id = :id"
				+ stateStr + " and t.operation = :operation and receice_card_type = :type and currency_type_id = :currencyId";
		
		Session session = bqMemberRechargeDao.getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("id", fUserId);
		query.setParameter("type", type);
		for (int i = 0; i < states.length; i++) {
			query.setParameter("state" + i, states[i]);
		}
		query.setParameter("operation", operation);
		query.setParameter("currencyId", currencyId);
		
		return (BigDecimal) query.uniqueResult();
	}

	public BigDecimal sumRecord(long id, int operation, int state, int currencyId) {
		Session session = bqMemberRechargeDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(amount), 0) from bq_member_recharge where 1=1 ");
		sql.append("AND f_user_id = :userId ");
		sql.append("AND operation = :operation ");
		sql.append("AND state = :state ");
		sql.append("AND currency_type_id = :currencyId ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", id);
		query.setParameter("operation", operation);
		query.setParameter("state", state);
		query.setParameter("currencyId", currencyId);
		return (BigDecimal)query.uniqueResult();
	}

	@Override
	public BQMemberRecharge findByRecordId(long recordId) {
		String sql = "from BQMemberRecharge where record_id=:recordId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordId", recordId);
		
		return bqMemberRechargeDao.get(sql, map);
	}
	
	@Override
	public BQMemberRecharge getOne(long uid) {
		String sql = "from BQMemberRecharge where f_user_id=:uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		return bqMemberRechargeDao.get(sql, map);
	}

	@Override
	public long readRechargeCount(long uid) {
		Session session = bqMemberRechargeDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(count(1), 0) from bq_member_recharge where f_user_id=:uid ");
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		
		BigInteger ret = (BigInteger) query.uniqueResult(); 
		return ret.longValue();
	}
	
	public List<NContractPartition> getAllPartition() {
		List<NContractPartition> partitions = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_CONTRACT_PARTITION);
		if (partitions == null) {
			partitions = nContractPartitionDao.find("from NContractPartition");
			spyMemcachedClient.set(BTCQMemecacheKeys.ALL_CONTRACT_PARTITION, BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		}
		
		return partitions;
	}

	@Override
	public void getBalanceAccount(long uid, int currencyId, BalanceAccountVO allBalance, BalanceAccountVO generalBalance,
			List<BalanceAccountVO> contractBalances, List<BalanceAccountVO> deliveryContractBalances) throws ParseException {
		bqAccountDao.getSession().flush();
		bqAccountDao.getSession().clear();
		
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
		if (bqAccount == null) return;
		
		// 充值提现状况
		int auto = 1;
		int admin = 2;
		int admin_award = 3;
		
		// 自动充
		int[] stateSuccess = { 1 };
		BigDecimal autoRecharge = findSumByIDAndTypeAndState(uid, auto, Contents.RECHARGE_TYPE_CZ, stateSuccess, currencyId);
		
		// 人工充（在线充）
		BigDecimal manualRecharge = findSumByIDAndTypeAndState(uid, admin, Contents.RECHARGE_TYPE_CZ, stateSuccess, currencyId);
		
		// 管理员人工奖励
		BigDecimal manualAward = findSumByIDAndTypeAndState(uid, admin_award, Contents.RECHARGE_TYPE_CZ, stateSuccess, currencyId);
		
		// 自动提-成功
		BigDecimal withdraw = findSumByIDAndTypeAndState(uid, auto, Contents.RECHARGE_TYPE_TX, stateSuccess, currencyId);
		
		// 自动提-冻结
		int[] stateFrozen = { 0, 4 };
		BigDecimal withdrawFrozen = findSumByIDAndTypeAndState(uid, auto, Contents.RECHARGE_TYPE_TX, stateFrozen, currencyId);
		
		// 自动提-处理中
		int[] stateProc = { 3 };
		BigDecimal withdrawProc = findSumByIDAndTypeAndState(uid, auto, Contents.RECHARGE_TYPE_TX, stateProc, currencyId);
		
		// 人工提-成功
		BigDecimal manualWithdraw = findSumByIDAndTypeAndState(uid, admin, Contents.RECHARGE_TYPE_TX, stateSuccess, currencyId);
		
		// 管理员人工扣除奖励-成功
		BigDecimal manualDeductAward = findSumByIDAndTypeAndState(uid, admin_award, Contents.RECHARGE_TYPE_TX, stateSuccess, currencyId);
		
		BigDecimal allRecharge = autoRecharge.add(manualRecharge).add(manualAward);
		
		// 兑换-冻结 
		BigDecimal exchangeFrozen = BigDecimal.ZERO;
		
		// 兑换-扣除
		BigDecimal exchangeDeduction = BigDecimal.ZERO;
		
		// 兑换-获得
		BigDecimal exchangeObtain = BigDecimal.ZERO;
		
		BQCurrencyType currencyType = bqCurrencyTypeDao.get(BQCurrencyType.class, currencyId);
		if ("BFC".equals(currencyType.getType_name()) || "BFX".equals(currencyType.getType_name())) {
			exchangeFrozen = bqExchangeBfcRecordDao.sumFrozen(uid, currencyId);
			exchangeDeduction = bqExchangeBfcRecordDao.sumDeduction(uid, currencyId);
			exchangeObtain = bqExchangeBfcRecordDao.sumObtain(uid, currencyId);
		} else {
			exchangeFrozen = bqExchangeRecordDao.sumFrozen(uid, currencyId);
			exchangeDeduction = bqExchangeRecordDao.sumDeduction(uid, currencyId);
			exchangeObtain = bqExchangeRecordDao.sumObtain(uid, currencyId);
		}
		
		
		// 币币交易
		// 买入增加
		// 卖出减少
		BigDecimal buyIn = BigDecimal.ZERO;
		BigDecimal sellOut = BigDecimal.ZERO;
		BigDecimal spotPending = BigDecimal.ZERO;
		List<BQSpotMarketCoinPair> coinPairs = spotMarketCoinPairDao.findByCoinId(currencyId);
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			buyIn = buyIn.add(spotTradeLogDao.sumBuyIn(coinPair.getCoin_pair_name(), uid, currencyId));
			buyIn = buyIn.add(spotTradeLogDao.sumBakBuyIn(coinPair.getCoin_pair_name(), uid, currencyId));
			sellOut = sellOut.add(spotTradeLogDao.sumSellOut(coinPair.getCoin_pair_name(), uid, currencyId));
			sellOut = sellOut.add(spotTradeLogDao.sumBakSellOut(coinPair.getCoin_pair_name(), uid, currencyId));
			spotPending = spotPending.add(spotPendingDelegationDao.findSumSellFrozen(coinPair.getCoin_pair_name(), uid));
			spotPending = spotPending.add(spotPendingDelegationCacheDao.findSumSellFrozen(coinPair.getCoin_pair_name(), uid));
		}
		
		// 买入花销
		// 卖出获得资金
		BigDecimal buySpend = BigDecimal.ZERO;
		BigDecimal sellObtain = BigDecimal.ZERO;
		coinPairs = spotMarketCoinPairDao.findByMarketCoinId(currencyId);
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			buySpend = buySpend.add(spotTradeLogDao.sumBuySpend(coinPair.getCoin_pair_name(), uid, currencyId));
			buySpend = buySpend.add(spotTradeLogDao.sumBakBuySpend(coinPair.getCoin_pair_name(), uid, currencyId));
			sellObtain = sellObtain.add(spotTradeLogDao.sumSellObtain(coinPair.getCoin_pair_name(), uid, currencyId));
			sellObtain = sellObtain.add(spotTradeLogDao.sumBakSellObtain(coinPair.getCoin_pair_name(), uid, currencyId));
			spotPending = spotPending.add(spotPendingDelegationDao.findSumBuyFrozen(coinPair.getCoin_pair_name(), uid));
			spotPending = spotPending.add(spotPendingDelegationCacheDao.findSumBuyFrozen(coinPair.getCoin_pair_name(), uid));
		}
		
		// 挖矿
		BigDecimal mine = BigDecimal.ZERO;
		mine = bqMineRecordDao.sumAmountBetweenTime(uid, currencyId, 0, 0);
		
		BigDecimal gChangeInto = BigDecimal.ZERO;
		BigDecimal gChagneOut = BigDecimal.ZERO;
		// OTC转入转出
		BigDecimal gChangeIntoOTC = BigDecimal.ZERO;
		BigDecimal gChagneOutOTC = BigDecimal.ZERO;
		
		// OTC转出冻结
		BigDecimal gChagneOutOTCFrozen = BigDecimal.ZERO;
		
		// 分红
		BigDecimal bonus = BigDecimal.ZERO;
		bonus = bqBonusRecordDao.sumAmountBetweenTime(uid, currencyId, 0, 0);
		
		// 奖励
		BigDecimal rewards = BigDecimal.ZERO;
		rewards = bqBftRewardRecordDa.sumAmountBetweenTime(uid, currencyId, 0, 0);
		
		boolean haveTradePartition = false;
		int vTransCurrencyId = 0;
		VTransaction vTrans = vTransactionDao.get("from VTransaction where transaction_state = 1");
		if (vTrans != null) {
			vTransCurrencyId = vTrans.getCurrency_type_id();
		}
		
		// 转入、转出
		if (currencyId == Contents.TRADE_CURRENCY_TYPE_ID || currencyId == vTransCurrencyId) {
			haveTradePartition = true;
		}
		
		NContractPartition partition = null;
		if (!haveTradePartition) {
			List<NContractPartition> partitions = getAllPartition();
			for (NContractPartition part : partitions) {
				if (part.getCurrency_type_id() == currencyId) {
					haveTradePartition = true;
					partition = part;
					break;
				}
			}
		}
		
		if (haveTradePartition) {
			List<Object[]> list = bqAccountAmountLogDao.findSumZRZCRecord(uid, currencyId);
			for (Object[] objects : list) {
				if (objects.length == 2) {
					switch ((Integer) (objects[1])) {
					case Contents.TRANS_TYPE_TO:
						gChangeInto = new BigDecimal(objects[0].toString());
						break;
					case Contents.TRANS_TYPE_OUT:
						gChagneOut = new BigDecimal(objects[0].toString());
						break;
					default:
						break;
					}
				}
			}
			
		}
		
		// OTC 转入、转出
		List<Object[]> list = bqAccountAmountLogDao.findSumZRZCOTCRecord(uid, currencyId);
		for (Object[] objects : list) {
			if (objects.length == 2) {
				switch ((Integer) (objects[1])) {
				case Contents.TRANS_TYPE_TOOTC:
					gChangeIntoOTC = new BigDecimal(objects[0].toString());
					break;
				case Contents.TRANS_TYPE_OUTOTC:
					gChagneOutOTC = new BigDecimal(objects[0].toString());
					break;
				default:
					break;
				}
			}
		}
		gChagneOutOTCFrozen = bqTransferWithOtcRecordDao.sumFrozen(uid, currencyId, 2);
		
		// 母子账户转入转出
		BigDecimal parentSubOut = bqSubMemberTransferLogDao.sumAmount(uid, currencyId, false);
		BigDecimal parentSubIn = bqSubMemberTransferLogDao.sumAmount(uid, currencyId, true);
		
		// bfx抵扣手续费
		BigDecimal deducteFee = BigDecimal.ZERO;
		BigDecimal buyVipSpend = BigDecimal.ZERO;
        BQBfxFeeSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (setting == null) {
			setting = bqBfxFeeSettingDao.get();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting != null && setting.getCoin_id() == currencyId) {
			deducteFee = bqBfxFeeRecordDao.sumAmount(uid);
		}
		buyVipSpend = bqMemberLevelBuyAmountLogDao.sumAmount(uid, currencyId);
		
		// 返利
		BigDecimal rebate = BigDecimal.ZERO;
		BigDecimal deliveryRebate = BigDecimal.ZERO;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", uid);
		params.put("currencyId", currencyId);
		rebate = bqMemberInviteRebateLogDao.sum("select sum(rebate_amount) From BQMemberInviteRebateLog where user_id = :userId and currency_type_id = :currencyId", params);
		deliveryRebate = deliveryMemberInviteRebateLogDao.sum("select sum(rebate_amount) From DeliveryMemberInviteRebateLog where user_id = :userId and currency_type_id = :currencyId", params);
		rebate = rebate.add(deliveryRebate);
		
		// 质押借币-借入
		BigDecimal borrow = bqLoanRecordDao.getSumRecived(uid, currencyId);
		
		// 质押借币-归还利息
		BigDecimal interest = bqLoanRecordDao.getSumInterest(uid, currencyId, 2);
		
		// 质押借币-抵押
		BigDecimal pledge = bqLoanRecordDao.getSumPledge(uid, currencyId, 1);
		
		// 质押借币-借出
		BigDecimal borrowOut = bqLoanRecordDao.getSumBorrowOut(uid, currencyId, 1);
		
		// 质押借币-借出获利
		BigDecimal interestObtain = bqLoanRecordDao.getSumInterestObtain(uid, currencyId, 2);
		
		// 质押借币-抵偿质押本息
		BigDecimal paymentPledge = bqLoanRecordDao.getSumPledge(uid, currencyId, 3);
		
		// 质押借币-获取抵偿版本息（除手续费）
		BigDecimal paymentPledgeObtain = bqLoanRecordDao.getSumPaymentPledgeObtain(uid, currencyId, 3);
		
		BigDecimal loanObatin = borrow.subtract(interest).subtract(borrowOut).add(interestObtain).add(paymentPledgeObtain).subtract(paymentPledge);
		
		BigDecimal loanFrozen = pledge;

		//抽奖所得
		BigDecimal luckDrawObtain = BigDecimal.ZERO;
		if ("USDT".equals(currencyType.getType_name())) {
			luckDrawObtain = bqLuckObtainRecordDao.findCountCoin(uid);
		}
		
		BigDecimal gShoudTotal = allRecharge.subtract(withdraw).subtract(withdrawProc).subtract(manualWithdraw).subtract(gChagneOut).subtract(gChagneOutOTC)
				.add(gChangeInto).add(gChangeIntoOTC).add(rebate).add(loanObatin).add(exchangeObtain).subtract(exchangeDeduction).add(mine).add(bonus).add(rewards)
				.add(buyIn).add(sellObtain).subtract(buySpend).subtract(sellOut).subtract(manualDeductAward).subtract(deducteFee).subtract(buyVipSpend)
				.add(parentSubIn.subtract(parentSubOut)).add(luckDrawObtain);
		BigDecimal gShouldFrozen = withdrawFrozen.add(exchangeFrozen).add(spotPending).add(gChagneOutOTCFrozen).add(loanFrozen);
		BigDecimal gShouldAvailable = gShoudTotal.subtract(gShouldFrozen);
		
		// 净值
		BigDecimal gNetWorth = bqAccount.getBtc().add(bqAccount.getBtc_locked()).subtract(gShoudTotal);
		
		generalBalance.setUserId(uid);
		generalBalance.setAccountName("总账户");
		generalBalance.setMine(mine.doubleValue());
		generalBalance.setRewards(rewards.doubleValue());
		generalBalance.setBonus(bonus.doubleValue());
		generalBalance.setBuyIn(buyIn.doubleValue());
		generalBalance.setSellOut(sellOut.doubleValue());
		generalBalance.setBuySpend(buySpend.doubleValue());
		generalBalance.setSellObtain(sellObtain.doubleValue());
		generalBalance.setSpotPendingFrozen(spotPending.doubleValue());
		generalBalance.setActualAvailable(bqAccount.getBtc().doubleValue());
		generalBalance.setActualFrozen(bqAccount.getBtc_locked().doubleValue());
		generalBalance.setActualTotal(bqAccount.getBtc().add(bqAccount.getBtc_locked()).doubleValue());
		generalBalance.setManualRecharge(manualRecharge.doubleValue());
		generalBalance.setManualAward(manualAward.doubleValue());
		generalBalance.setAutoRecharge(autoRecharge.doubleValue());
		generalBalance.setAllRecharge(allRecharge.doubleValue());
		generalBalance.setWithdraw(withdraw.doubleValue());
		generalBalance.setWithdrawFrozen(withdrawFrozen.doubleValue());
		generalBalance.setManualWithdraw(manualWithdraw.doubleValue());
		generalBalance.setManualDeductAward(manualDeductAward.doubleValue());
		generalBalance.setWidthdrawProc(withdrawProc.doubleValue());
		generalBalance.setChangeInto(gChangeInto.doubleValue());
		generalBalance.setChangeOut(gChagneOut.doubleValue());
		generalBalance.setChangeIntoOTC(gChangeIntoOTC.doubleValue());
		generalBalance.setChangeOutOTC(gChagneOutOTC.doubleValue());
		generalBalance.setChangeOutOTCFrozen(gChagneOutOTCFrozen.doubleValue());
		generalBalance.setRebate(rebate.doubleValue());
		generalBalance.setLoanBorrow(borrow.doubleValue());
		generalBalance.setLoanBorrowOut(borrowOut.doubleValue());
		generalBalance.setLoanInterest(interest.doubleValue());
		generalBalance.setLoanPledge(pledge.doubleValue());
		generalBalance.setLoanInterestObtain(interestObtain.doubleValue());
		generalBalance.setLoanPaymentPledge(paymentPledge.doubleValue());
		generalBalance.setLoanPaymentPledgeObtain(paymentPledgeObtain.doubleValue());
		generalBalance.setShouldTotal(gShoudTotal.doubleValue());
		generalBalance.setShouldFrozen(gShouldFrozen.doubleValue());
		generalBalance.setShouldAvailable(gShouldAvailable.doubleValue());
		generalBalance.setNetWorth(gNetWorth.doubleValue());
		generalBalance.setExchangeFrozen(exchangeFrozen.doubleValue());
		generalBalance.setExchangeDeduction(exchangeDeduction.doubleValue());
		generalBalance.setExchangeObtain(exchangeObtain.doubleValue());
		generalBalance.setDeducteFee(deducteFee.doubleValue());
		generalBalance.setBuyVipSpend(buyVipSpend.doubleValue());
		generalBalance.setTransferToSub(parentSubIn.doubleValue());
		generalBalance.setTransferFromSub(parentSubOut.doubleValue());
		generalBalance.setLuckDrawObtain(luckDrawObtain.doubleValue());

		List<BQAccountContract> bqAccountContracts = new ArrayList<BQAccountContract>();
		List<VAccountContract> vAccountContracts = new ArrayList<VAccountContract>();
		List<DeliveryAccountContract> dAccountContracts = new ArrayList<DeliveryAccountContract>();
		List<NAccountContract> nAccountContracts = new ArrayList<NAccountContract>();

		if (currencyId == Contents.TRADE_CURRENCY_TYPE_ID) {
			bqAccountContracts = bqAccountContractDao.findByUserID(uid);
			dAccountContracts = deliveryAccountContractDao.findByUserID(uid);
		} else if (currencyId == vTransCurrencyId) {
			vAccountContracts = vAccountContractDao.findByUserID(uid);
		} else if (partition != null) {
			nAccountContracts = nAccountContractDao.findByUserID(partition.getPart(), uid);
		}
		
		allBalance.setUserId(uid);
        allBalance.setAccountName("总计");
        allBalance.setActualFrozen(generalBalance.getActualFrozen());
        allBalance.setActualAvailable(generalBalance.getActualAvailable());
        allBalance.setShouldFrozen(generalBalance.getShouldFrozen());
        allBalance.setShouldAvailable(generalBalance.getShouldAvailable());
        
        long currTime = tradeLogReconciliaDao.getUnix_TIMESTAMP();
        if (currencyId == Contents.TRADE_CURRENCY_TYPE_ID) {
        	
        	// 永续合约
        	for (BQAccountContract account : bqAccountContracts) {
				BigDecimal changeInto = BigDecimal.ZERO;
				BigDecimal changeOut = BigDecimal.ZERO;
				BalanceAccountVO balanceVo = new BalanceAccountVO();
				
				String code = account.getF_code();
				balanceVo.setUserId(uid);
				balanceVo.setAccountName(code);
				balanceVo.setActualAvailable(account.getF_btc().doubleValue());
				balanceVo.setActualFrozen(account.getF_lockbtc().doubleValue());
				balanceVo.setActualTotal(balanceVo.getActualAvailable() + balanceVo.getActualFrozen());
				
				// 转入、转出
				List<Object[]> ctList = contractAccountLogDao.findSumZRZCRecord(code, uid);
				for (Object[] objects : ctList) {
					if (objects.length == 2) {
						switch (Convert.toInt(objects[1].toString(), 0)) {
						case Contents.TRANS_TYPE_CZ:
							changeInto = new BigDecimal(objects[0].toString());
							break;
						case Contents.TRANS_TYPE_TX:
							changeOut = new BigDecimal(objects[0].toString());
							break;
						default:
							break;
						}
					}
				}
				
				balanceVo.setChangeInto(changeInto.doubleValue());
				balanceVo.setChangeOut(changeOut.doubleValue());
				
				String hql = "from BQTransaction t where t.transaction_code = :code";
				Map<String, Object> pms = new HashMap<String, Object>();
				pms.put("code", account.getF_code());
				BQTransaction trans = bqTransactionDao.get(hql, pms);
				
				// 交易盈亏及手续费
				getFeeAndIncome(balanceVo, uid, trans, currTime);
//				Object[] objs = tradeLogDao.getAllIncomeAndFeeByUser(account.getF_code(), uid, 0, System.currentTimeMillis()/1000);
//				if (objs.length == 2) {
//					balanceVo.setIncome(new BigDecimal(objs[0].toString()).doubleValue());
//					balanceVo.setFee(new BigDecimal(objs[1].toString()).doubleValue());
//				}
				
				// 资金费
				BigDecimal fundingFee = fundingFeeRecordDao.sumFundingFee(account.getF_code(), uid);
				balanceVo.setFundingFee(fundingFee.doubleValue());
				
				// 持单保证金
				BigDecimal holdDeposit = bqMemberTransactionDao.findSumDepositByUser(account.getF_code(), uid);
				balanceVo.setHoldDeposit(holdDeposit.doubleValue());
				
				// 挂单冻结
				BigDecimal pendingDeposit = BigDecimal.ZERO;
				BigDecimal pendingFee = BigDecimal.ZERO;
				Object[] pending  = pendingDelegationDao.findSumDepositAndFeeByUser(code, uid, trans.getTrade_coefficient());
				if (pending.length == 2) {
					pendingDeposit = new BigDecimal(pending[0].toString());
					pendingFee = new BigDecimal(pending[1].toString());
				}
				
				Object[] pendingCache  = pendingDelegationCacheDao.findSumDepositAndFeeByUser(code, uid);
				if (pending.length == 2) {
					pendingDeposit = pendingDeposit.add(new BigDecimal(pendingCache[0].toString()));
					pendingFee = pendingFee.add(new BigDecimal(pendingCache[1].toString()));
				}
				
				balanceVo.setPendingFrozen(pendingDeposit.add(pendingFee).doubleValue());
				
				balanceVo.setShouldAvailable(balanceVo.getChangeInto() - balanceVo.getChangeOut() + balanceVo.getIncome() + balanceVo.getFundingFee()
					- balanceVo.getFee() - balanceVo.getHoldDeposit() - balanceVo.getPendingFrozen());
				balanceVo.setShouldFrozen(balanceVo.getPendingFrozen());
				balanceVo.setShouldTotal(balanceVo.getShouldAvailable() + balanceVo.getShouldFrozen());
				balanceVo.setNetWorth(balanceVo.getActualTotal() - balanceVo.getShouldTotal());
				
				allBalance.setActualAvailable(allBalance.getActualAvailable() + balanceVo.getActualAvailable());
				allBalance.setActualFrozen(allBalance.getActualFrozen() + balanceVo.getActualFrozen());
				
				allBalance.setShouldAvailable(allBalance.getShouldAvailable() + balanceVo.getShouldAvailable());
				allBalance.setShouldFrozen(allBalance.getShouldFrozen() + balanceVo.getShouldFrozen());
				
				allBalance.setAllFundingFee(allBalance.getAllFundingFee() + balanceVo.getFundingFee());
				allBalance.setAllIncome(allBalance.getAllIncome() + balanceVo.getIncome());
				allBalance.setAllFee(allBalance.getAllFee() + balanceVo.getFee());
				allBalance.setNetIncome(allBalance.getAllFundingFee() + allBalance.getAllIncome() - allBalance.getAllFee());
				
				contractBalances.add(balanceVo);
			}
        	
        	// 交割合约
        	for (DeliveryAccountContract account : dAccountContracts) {
				BigDecimal changeInto = BigDecimal.ZERO;
				BigDecimal changeOut = BigDecimal.ZERO;
				BalanceAccountVO balanceVo = new BalanceAccountVO();
				
				String code = account.getF_code();
				balanceVo.setUserId(uid);
				balanceVo.setAccountName(code);
				balanceVo.setActualAvailable(account.getF_btc().doubleValue());
				balanceVo.setActualFrozen(account.getF_lockbtc().doubleValue());
				balanceVo.setActualTotal(balanceVo.getActualAvailable() + balanceVo.getActualFrozen());
				
				// 转入、转出
				List<Object[]> ctList = deliveryContractAccountLogDao.findSumZRZCRecord(code, uid);
				for (Object[] objects : ctList) {
					if (objects.length == 2) {
						switch (Convert.toInt(objects[1].toString(), 0)) {
						case Contents.TRANS_TYPE_CZ:
							changeInto = new BigDecimal(objects[0].toString());
							break;
						case Contents.TRANS_TYPE_TX:
							changeOut = new BigDecimal(objects[0].toString());
							break;
						default:
							break;
						}
					}
				}
				
				balanceVo.setChangeInto(changeInto.doubleValue());
				balanceVo.setChangeOut(changeOut.doubleValue());
				
				String hql = "from DeliveryTransaction t where t.transaction_code = :code";
				Map<String, Object> pms = new HashMap<String, Object>();
				pms.put("code", account.getF_code());
				DeliveryTransaction trans = deliveryTransactionDao.get(hql, pms);
				
				// 交易盈亏及手续费
				getDeliveryFeeAndIncome(balanceVo, uid, trans, currTime);
//				Object[] objs = tradeLogDao.getAllIncomeAndFeeByUser(account.getF_code(), uid, 0, System.currentTimeMillis()/1000);
//				if (objs.length == 2) {
//					balanceVo.setIncome(new BigDecimal(objs[0].toString()).doubleValue());
//					balanceVo.setFee(new BigDecimal(objs[1].toString()).doubleValue());
//				}
				
				// 资金费
//				BigDecimal fundingFee = fundingFeeRecordDao.sumFundingFee(account.getF_code(), uid);
//				balanceVo.setFundingFee(fundingFee.doubleValue());
				
				// 持单保证金
				BigDecimal holdDeposit = deliveryMemberTransactionDao.findSumDepositByUser(account.getF_code(), uid);
				balanceVo.setHoldDeposit(holdDeposit.doubleValue());
				
				// 挂单冻结
				BigDecimal pendingDeposit = BigDecimal.ZERO;
				BigDecimal pendingFee = BigDecimal.ZERO;
				Object[] pending  = deliveryPendingDelegationDao.findSumDepositAndFeeByUser(code, uid, trans.getTrade_coefficient());
				if (pending.length == 2) {
					pendingDeposit = new BigDecimal(pending[0].toString());
					pendingFee = new BigDecimal(pending[1].toString());
				}
				
				Object[] pendingCache  = deliveryPendingDelegationCacheDao.findSumDepositAndFeeByUser(code, uid);
				if (pending.length == 2) {
					pendingDeposit = pendingDeposit.add(new BigDecimal(pendingCache[0].toString()));
					pendingFee = pendingFee.add(new BigDecimal(pendingCache[1].toString()));
				}
				
				balanceVo.setPendingFrozen(pendingDeposit.add(pendingFee).doubleValue());
				
				balanceVo.setShouldAvailable(balanceVo.getChangeInto() - balanceVo.getChangeOut() + balanceVo.getIncome() + balanceVo.getFundingFee()
					- balanceVo.getFee() - balanceVo.getHoldDeposit() - balanceVo.getPendingFrozen());
				balanceVo.setShouldFrozen(balanceVo.getPendingFrozen());
				balanceVo.setShouldTotal(balanceVo.getShouldAvailable() + balanceVo.getShouldFrozen());
				balanceVo.setNetWorth(balanceVo.getActualTotal() - balanceVo.getShouldTotal());
				
				allBalance.setActualAvailable(allBalance.getActualAvailable() + balanceVo.getActualAvailable());
				allBalance.setActualFrozen(allBalance.getActualFrozen() + balanceVo.getActualFrozen());
				
				allBalance.setShouldAvailable(allBalance.getShouldAvailable() + balanceVo.getShouldAvailable());
				allBalance.setShouldFrozen(allBalance.getShouldFrozen() + balanceVo.getShouldFrozen());
				
				allBalance.setAllFundingFee(allBalance.getAllFundingFee() + balanceVo.getFundingFee());
				allBalance.setAllIncome(allBalance.getAllIncome() + balanceVo.getIncome());
				allBalance.setAllFee(allBalance.getAllFee() + balanceVo.getFee());
				allBalance.setNetIncome(allBalance.getAllFundingFee() + allBalance.getAllIncome() - allBalance.getAllFee());
				
				deliveryContractBalances.add(balanceVo);
			}
        } else if (currencyId == vTransCurrencyId) {
        	for (VAccountContract account : vAccountContracts) {
				BigDecimal changeInto = BigDecimal.ZERO;
				BigDecimal changeOut = BigDecimal.ZERO;
				BalanceAccountVO balanceVo = new BalanceAccountVO();
				
				String code = account.getF_code();
				balanceVo.setUserId(uid);
				balanceVo.setAccountName(code);
				balanceVo.setActualAvailable(account.getF_btc().doubleValue());
				balanceVo.setActualFrozen(account.getF_lockbtc().doubleValue());
				balanceVo.setActualTotal(balanceVo.getActualAvailable() + balanceVo.getActualFrozen());
				
				// 转入、转出
				List<Object[]> ctList = vContractAccountLogDao.findSumZRZCRecord(code, uid);
				for (Object[] objects : ctList) {
					if (objects.length == 2) {
						switch (Convert.toInt(objects[1].toString(), 0)) {
						case Contents.TRANS_TYPE_CZ:
							changeInto = new BigDecimal(objects[0].toString());
							break;
						case Contents.TRANS_TYPE_TX:
							changeOut = new BigDecimal(objects[0].toString());
							break;
						default:
							break;
						}
					}
				}
				
				balanceVo.setChangeInto(changeInto.doubleValue());
				balanceVo.setChangeOut(changeOut.doubleValue());
				
				// 交易盈亏及手续费
				Object[] objs = vTradeLogDao.getAllIncomeAndFeeByUser(account.getF_code(), uid);
				if (objs.length == 2) {
					balanceVo.setIncome(new BigDecimal(objs[0].toString()).doubleValue());
					balanceVo.setFee(new BigDecimal(objs[1].toString()).doubleValue());
				}
				
				objs = vTradeLogDao.getAllBakIncomeAndFeeByUser(account.getF_code(), uid);
				if (objs.length == 2) {
					balanceVo.setIncome(balanceVo.getIncome() + new BigDecimal(objs[0].toString()).doubleValue());
					balanceVo.setFee(balanceVo.getFee() + new BigDecimal(objs[1].toString()).doubleValue());
				}
				
				// 持单保证金
				BigDecimal holdDeposit = vMemberTransactionDao.findSumDepositByUser(account.getF_code(), uid);
				balanceVo.setHoldDeposit(holdDeposit.doubleValue());
				
				// 挂单冻结
				BigDecimal pendingDeposit = BigDecimal.ZERO;
				BigDecimal pendingFee = BigDecimal.ZERO;
				Object[] pending  = vPendingDelegationDao.findSumDepositAndFeeByUser(code, uid);
				if (pending.length == 2) {
					pendingDeposit = new BigDecimal(pending[0].toString());
					pendingFee = new BigDecimal(pending[1].toString());
				}
				
				Object[] pendingCache  = vPendingDelegationCacheDao.findSumDepositAndFeeByUser(code, uid);
				if (pending.length == 2) {
					pendingDeposit = pendingDeposit.add(new BigDecimal(pendingCache[0].toString()));
					pendingFee = pendingFee.add(new BigDecimal(pendingCache[1].toString()));
				}
				
				balanceVo.setPendingFrozen(pendingDeposit.add(pendingFee).doubleValue());
				
				balanceVo.setShouldAvailable(balanceVo.getChangeInto() - balanceVo.getChangeOut() + balanceVo.getIncome() - balanceVo.getFee() - balanceVo.getHoldDeposit() - balanceVo.getPendingFrozen());
				balanceVo.setShouldFrozen(balanceVo.getPendingFrozen());
				balanceVo.setShouldTotal(balanceVo.getShouldAvailable() + balanceVo.getShouldFrozen());
				balanceVo.setNetWorth(balanceVo.getActualTotal() - balanceVo.getShouldTotal());
				
				allBalance.setActualAvailable(allBalance.getActualAvailable() + balanceVo.getActualAvailable());
				allBalance.setActualFrozen(allBalance.getActualFrozen() + balanceVo.getActualFrozen());
				
				allBalance.setShouldAvailable(allBalance.getShouldAvailable() + balanceVo.getShouldAvailable());
				allBalance.setShouldFrozen(allBalance.getShouldFrozen() + balanceVo.getShouldFrozen());
				
				contractBalances.add(balanceVo);
			}
        } else if (partition != null) {
        	// 永续合约
        	for (NAccountContract account : nAccountContracts) {
				BigDecimal changeInto = BigDecimal.ZERO;
				BigDecimal changeOut = BigDecimal.ZERO;
				BalanceAccountVO balanceVo = new BalanceAccountVO();
				
				String code = account.getF_code();
				balanceVo.setUserId(uid);
				balanceVo.setAccountName(code);
				balanceVo.setActualAvailable(account.getF_btc().doubleValue());
				balanceVo.setActualFrozen(account.getF_lockbtc().doubleValue());
				balanceVo.setActualTotal(balanceVo.getActualAvailable() + balanceVo.getActualFrozen());
				
				// 转入、转出
				List<Object[]> ctList = nContractAccountLogDao.findSumZRZCRecord(partition.getPart(), code, uid);
				for (Object[] objects : ctList) {
					if (objects.length == 2) {
						switch (Convert.toInt(objects[1].toString(), 0)) {
						case Contents.TRANS_TYPE_CZ:
							changeInto = new BigDecimal(objects[0].toString());
							break;
						case Contents.TRANS_TYPE_TX:
							changeOut = new BigDecimal(objects[0].toString());
							break;
						default:
							break;
						}
					}
				}
				
				balanceVo.setChangeInto(changeInto.doubleValue());
				balanceVo.setChangeOut(changeOut.doubleValue());
				
				String hql = "select * from " + partition.getPart() + "_transaction t where t.transaction_code = :code";
				Map<String, Object> pms = new HashMap<String, Object>();
				pms.put("code", account.getF_code());
				NTransaction trans = nTransactionDao.getBySql(hql, pms);
				
				// 交易盈亏及手续费
				getNFeeAndIncome(partition.getPart(), balanceVo, uid, trans, currTime);
//				Object[] objs = tradeLogDao.getAllIncomeAndFeeByUser(account.getF_code(), uid, 0, System.currentTimeMillis()/1000);
//				if (objs.length == 2) {
//					balanceVo.setIncome(new BigDecimal(objs[0].toString()).doubleValue());
//					balanceVo.setFee(new BigDecimal(objs[1].toString()).doubleValue());
//				}
				
				// 资金费
				BigDecimal fundingFee = nFundingFeeRecordDao.sumFundingFee(partition.getPart(), account.getF_code(), uid);
				balanceVo.setFundingFee(fundingFee.doubleValue());
				
				// 持单保证金
				BigDecimal holdDeposit = nMemberTransactionDao.findSumDepositByUser(partition.getPart(), account.getF_code(), uid);
				balanceVo.setHoldDeposit(holdDeposit.doubleValue());
				
				// 挂单冻结
				BigDecimal pendingDeposit = BigDecimal.ZERO;
				BigDecimal pendingFee = BigDecimal.ZERO;
				Object[] pending  = nPendingDelegationDao.findSumDepositAndFeeByUser(partition.getPart(), code, uid, trans.getTrade_coefficient());
				if (pending.length == 2) {
					pendingDeposit = new BigDecimal(pending[0].toString());
					pendingFee = new BigDecimal(pending[1].toString());
				}
				
				Object[] pendingCache  = nPendingDelegationCacheDao.findSumDepositAndFeeByUser(partition.getPart(), code, uid);
				if (pending.length == 2) {
					pendingDeposit = pendingDeposit.add(new BigDecimal(pendingCache[0].toString()));
					pendingFee = pendingFee.add(new BigDecimal(pendingCache[1].toString()));
				}
				
				balanceVo.setPendingFrozen(pendingDeposit.add(pendingFee).doubleValue());
				
				balanceVo.setShouldAvailable(balanceVo.getChangeInto() - balanceVo.getChangeOut() + balanceVo.getIncome() + balanceVo.getFundingFee()
					- balanceVo.getFee() - balanceVo.getHoldDeposit() - balanceVo.getPendingFrozen());
				balanceVo.setShouldFrozen(balanceVo.getPendingFrozen());
				balanceVo.setShouldTotal(balanceVo.getShouldAvailable() + balanceVo.getShouldFrozen());
				balanceVo.setNetWorth(balanceVo.getActualTotal() - balanceVo.getShouldTotal());
				
				allBalance.setActualAvailable(allBalance.getActualAvailable() + balanceVo.getActualAvailable());
				allBalance.setActualFrozen(allBalance.getActualFrozen() + balanceVo.getActualFrozen());
				
				allBalance.setShouldAvailable(allBalance.getShouldAvailable() + balanceVo.getShouldAvailable());
				allBalance.setShouldFrozen(allBalance.getShouldFrozen() + balanceVo.getShouldFrozen());
				
				allBalance.setAllFundingFee(allBalance.getAllFundingFee() + balanceVo.getFundingFee());
				allBalance.setAllIncome(allBalance.getAllIncome() + balanceVo.getIncome());
				allBalance.setAllFee(allBalance.getAllFee() + balanceVo.getFee());
				allBalance.setNetIncome(allBalance.getAllFundingFee() + allBalance.getAllIncome() - allBalance.getAllFee());
				
				contractBalances.add(balanceVo);
			}
        }
		
		allBalance.setActualTotal(allBalance.getActualAvailable() + allBalance.getActualFrozen());
		allBalance.setShouldTotal(allBalance.getShouldAvailable() + allBalance.getShouldFrozen());
		allBalance.setNetWorth(allBalance.getActualTotal() - allBalance.getShouldTotal());
	}
	
	private void getNFeeAndIncome(String partition, BalanceAccountVO balanceVo, long userId, NTransaction trans, long currTime) throws ParseException {
		String code = trans.getTransaction_code();
		double income = 0;
		double fee = 0;
		
		long lastTime  = 0;
		NTradeLogReconcilia logReconcilia = nTradeLogReconciliaDao.getByUser(partition, code, userId);
		if (logReconcilia != null) {
			lastTime = logReconcilia.getTime();
		}
			
		Object[] objects = nTradeLogDao.getAllIncomeAndFeeByUser(partition, code, userId, lastTime, currTime);
		BigDecimal monthIncome = new BigDecimal(objects[0].toString());
		BigDecimal monthFee = new BigDecimal(objects[1].toString());
		income += monthIncome.doubleValue();
		fee += monthFee.doubleValue();
		
		objects = nTradeLogDao.getAllBakIncomeAndFeeByUser(partition, code, userId, lastTime, currTime);
		monthIncome = new BigDecimal(objects[0].toString());
		monthFee = new BigDecimal(objects[1].toString());
		income += monthIncome.doubleValue();
		fee += monthFee.doubleValue();
		
		if (logReconcilia != null) {
			NTradeLogReconcilia log = new NTradeLogReconcilia();
			log.setTime(currTime);
			log.setTrade_fee(logReconcilia.getTrade_fee().add(new BigDecimal(fee)));
			log.setTrade_income(logReconcilia.getTrade_income().add(new BigDecimal(income)));
			log.setId(logReconcilia.getId());
			log.setF_user_id(logReconcilia.getF_user_id());
			log.setTransaction_code(logReconcilia.getTransaction_code());
			log.setTransaction_id(logReconcilia.getTransaction_id());
			
			income = log.getTrade_income().doubleValue();
			fee = log.getTrade_fee().doubleValue();
			
			nTradeLogReconciliaDao.update(partition, log);
		} else {
			logReconcilia = new NTradeLogReconcilia();
			
			logReconcilia.setF_user_id(userId);
			logReconcilia.setTime(currTime);
			logReconcilia.setTrade_fee(new BigDecimal(fee));
			logReconcilia.setTrade_income(new BigDecimal(income));
			logReconcilia.setTransaction_code(code);
			logReconcilia.setTransaction_id(trans.getTransaction_id());
			
			nTradeLogReconciliaDao.insert(partition, logReconcilia);
		}
		
		balanceVo.setIncome(income);
		balanceVo.setFee(fee);
	}
	
	private void getFeeAndIncome(BalanceAccountVO balanceVo, long userId, BQTransaction trans, long currTime) throws ParseException {
		String code = trans.getTransaction_code();
		double income = 0;
		double fee = 0;
		
		long lastTime  = 0;
		TradeLogReconcilia logReconcilia = tradeLogReconciliaDao.getByUser(code, userId);
		if (logReconcilia != null) {
			lastTime = logReconcilia.getTime();
		}
			
		Object[] objects = tradeLogDao.getAllIncomeAndFeeByUser(code, userId, lastTime, currTime);
		BigDecimal monthIncome = new BigDecimal(objects[0].toString());
		BigDecimal monthFee = new BigDecimal(objects[1].toString());
		income += monthIncome.doubleValue();
		fee += monthFee.doubleValue();
		
		objects = tradeLogDao.getAllBakIncomeAndFeeByUser(code, userId, lastTime, currTime);
		monthIncome = new BigDecimal(objects[0].toString());
		monthFee = new BigDecimal(objects[1].toString());
		income += monthIncome.doubleValue();
		fee += monthFee.doubleValue();
		
		if (logReconcilia != null) {
			logReconcilia.setTime(currTime);
			logReconcilia.setTrade_fee(logReconcilia.getTrade_fee().add(new BigDecimal(fee)));
			logReconcilia.setTrade_income(logReconcilia.getTrade_income().add(new BigDecimal(income)));
			
			income = logReconcilia.getTrade_income().doubleValue();
			fee = logReconcilia.getTrade_fee().doubleValue();
			
			tradeLogReconciliaDao.update(logReconcilia);
		} else {
			logReconcilia = new TradeLogReconcilia();
			
			logReconcilia.setF_user_id(userId);
			logReconcilia.setTime(currTime);
			logReconcilia.setTrade_fee(new BigDecimal(fee));
			logReconcilia.setTrade_income(new BigDecimal(income));
			logReconcilia.setTransaction_code(code);
			logReconcilia.setTransaction_id(trans.getTransaction_id());
			
			tradeLogReconciliaDao.save(logReconcilia);
		}
		
		balanceVo.setIncome(income);
		balanceVo.setFee(fee);
	}
	
	private void getDeliveryFeeAndIncome(BalanceAccountVO balanceVo, long userId, DeliveryTransaction trans, long currTime) throws ParseException {
		String code = trans.getTransaction_code();
		double income = 0;
		double fee = 0;
		
		long lastTime  = 0;
		DeliveryTradeLogReconcilia logReconcilia = deliveryTradeLogReconciliaDao.getByUser(code, userId);
		if (logReconcilia != null) {
			lastTime = logReconcilia.getTime();
		}
		
		Object[] objects = deliveryTradeLogDao.getAllIncomeAndFeeByUser(code, userId, lastTime, currTime);
		BigDecimal monthIncome = new BigDecimal(objects[0].toString());
		BigDecimal monthFee = new BigDecimal(objects[1].toString());
		income += monthIncome.doubleValue();
		fee += monthFee.doubleValue();
		
		objects = deliveryTradeLogDao.getAllBakIncomeAndFeeByUser(code, userId, lastTime, currTime);
		monthIncome = new BigDecimal(objects[0].toString());
		monthFee = new BigDecimal(objects[1].toString());
		income += monthIncome.doubleValue();
		fee += monthFee.doubleValue();
		
		if (logReconcilia != null) {
			logReconcilia.setTime(currTime);
			logReconcilia.setTrade_fee(logReconcilia.getTrade_fee().add(new BigDecimal(fee)));
			logReconcilia.setTrade_income(logReconcilia.getTrade_income().add(new BigDecimal(income)));
			
			income = logReconcilia.getTrade_income().doubleValue();
			fee = logReconcilia.getTrade_fee().doubleValue();
			
			deliveryTradeLogReconciliaDao.update(logReconcilia);
		} else {
			logReconcilia = new DeliveryTradeLogReconcilia();
			
			logReconcilia.setF_user_id(userId);
			logReconcilia.setTime(currTime);
			logReconcilia.setTrade_fee(new BigDecimal(fee));
			logReconcilia.setTrade_income(new BigDecimal(income));
			logReconcilia.setTransaction_code(code);
			logReconcilia.setTransaction_id(trans.getTransaction_id());
			
			deliveryTradeLogReconciliaDao.save(logReconcilia);
		}
		
		balanceVo.setIncome(income);
		balanceVo.setFee(fee);
	}
	
	@Override
	public BigDecimal getAllWithdrawSuccessBetweenTime(long uid, int currencyId, long beginTime, long endTime) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", uid);
		params.put("currencyId", currencyId);
		params.put("operation", 2);
		params.put("state", 1);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		
		return bqMemberRechargeDao.sum("select sum(amount) From BQMemberRecharge where f_user_id = :userId and currency_type_id = :currencyId"
				+ " and operation = :operation and state = :state and dateline <= :endTime and dateline >= :beginTime", params);
	}
	
	@Override
	public BigDecimal getSumBetweenTime(long uid, int currencyId, long beginTime, long endTime) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", uid);
		params.put("currencyId", currencyId);
		params.put("operation", 2);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		
		return bqMemberRechargeDao.sum("select sum(amount) From BQMemberRecharge where f_user_id = :userId and currency_type_id = :currencyId"
				+ " and operation = :operation and dateline <= :endTime and dateline >= :beginTime", params);
	}
}
