package com.btc100.btc_q.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.MatrixToImageWriter;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountContractDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQCurrencyTypeDao;
import com.btc100.btc_q.dao.BQInviteAwardCountDao;
import com.btc100.btc_q.dao.BQMemberRechargeDao;
import com.btc100.btc_q.dao.BQMemberStatusDao;
import com.btc100.btc_q.dao.BQMemberTransactionDao;
import com.btc100.btc_q.dao.BQMemberWalletDao;
import com.btc100.btc_q.dao.BQTransactionDao;
import com.btc100.btc_q.dao.BQWalletQueueBtcDao;
import com.btc100.btc_q.dao.BQWalletTransBtcDao;
import com.btc100.btc_q.dao.TradeLogDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQInviteAwardCount;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.entity.BQMemberStatus;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQMemberWallet;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQWalletQueueBtc;
import com.btc100.btc_q.entity.BQWalletTransBtc;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.WalletPlatformService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

@Service("walletPlatformService")
public class WalletPlatformServiceImpl implements WalletPlatformService {

	
	private static final Logger LOGGER = Logger.getLogger(WalletPlatformServiceImpl.class);
	
	@Autowired
	private BQWalletQueueBtcDao<BQWalletQueueBtc> bqWalletQueueBtcDao;
	
	@Autowired
	private BQMemberRechargeDao<BQMemberRecharge> bqMemberRechargeDao;
	
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	
	@Autowired
	private BQMemberWalletDao<BQMemberWallet> bqMemberWalletDao;
	
	@Autowired
	private BQWalletTransBtcDao<BQWalletTransBtc> bqWalletTransBtcDao;
	
	@Autowired
	BQAccountContractDao<BQAccountContract> bqAccountContractDao; 
	
	@Autowired
	BQMemberStatusDao<BQMemberStatus> bqMemberStatusDao;
	
	@Autowired
	BQInviteAwardCountDao<BQInviteAwardCount> bqInviteAwardCountDao;
	
	@Autowired
	BQTransactionDao<BQTransaction> bqTransactionDao;
	
	@Autowired
	TradeLogDao<TradeLog> tradeLogDao;
	
	@Autowired
	BQMemberTransactionDao<BQMemberTransaction> bqMemberTransactionDao; 
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@Autowired
	BQCurrencyTypeDao<BQCurrencyType> bqCurrencyTypeDao;
	
	@Override
	public void saveChargeRecord(int coinId, BQAccount ua, String txid, String address, BigDecimal amount, boolean erc20usdt, boolean trc20usdt) throws Exception {
		try {
			buildSaveRechargeRecord(coinId, ua, txid, address, amount, erc20usdt, trc20usdt);

			/*
			UserRechargeCoinRecordLog log = buildNewRechargeRecordLog(rechargeCoinRecord, ua);
			BigDecimal amountLog = (null == ua.getAvailable()) ? BigDecimal.ZERO : ua.getAvailable();
			if (null != ua.getFrozen()) {
				amountLog = amountLog.add(ua.getFrozen());
			}
			log.setAccountAmount(amountLog);
			log.setOldState(Code.RechargeCoinState.CREATE);
			log.setNote("[AUTO]");
			rechargeCoinRecordLogDao.save(log);
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Override
	public void confirmChargeRecord(BQAccount ua, BQMemberRecharge record, int direction, String note)  throws Exception {
		try {
			if (updateRechargeRecord(record, 1, "自动充值确认", ua) <= 0) {
				throw new Exception();
			}
			
			
			updateUserAccount(ua, record.getAmount(), BigDecimal.ZERO, record.getAmount(), 1, 1, "自动充值确认到账。");
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	@Override
	public BQMemberRecharge getProcessingRecord(String serno) {
		try {
			// 查询需要自动提币的币种
			BQMemberRecharge record = getProcessingWithdrawCoinRecord(Convert.toLong(serno, 0));
			if (null == record) {
				LOGGER.error("获取漏提数据失败：" + serno);
				return null;
			}
			
			/*
			UserWithdrawCoinRecordLog log = new UserWithdrawCoinRecordLog();
			log.setOldState(record.getState());
			log.setOperTime(System.currentTimeMillis());
			log.setRecordId(record.getId());
			log.setNewState(record.getState());
			log.setNote("钱包控制台提币记录漏单，重新获取");
			log.setProcessType(Code.ProcessType.AUTO);
			withdrawCoinRecordLogDao.save(log);
			*/
			
			return record;
		} catch (Exception e) {
			LOGGER.error("下载漏单提币数据异常，详情：", e);
			throw new RuntimeException();
		}
	}

	@Override
	public void updateWithdrawRecordAndAccount(Boolean success, String serno, String txid, String message) throws Exception {

		String hql = "from BQMemberRecharge r where r.state = :state and r.id = :serno and r.operation = :oper";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", 3);
		params.put("serno", Convert.toLong(serno, 0));
		params.put("oper", 2);
		BQMemberRecharge record = bqMemberRechargeDao.get(hql, params);
		
		if (record == null) {
			throw new RuntimeException("找不到已下载的提币记录");
		}
			
		try {
			if (success && !StringUtil.isEmpty(txid)) {	// 直接更新为成功
				updateWithdrawCoinRecord(record, 1, txid, "自动提币成功");
			} else {
				BQAccount ua = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getCurrency_type_id());
				updateUserAccount(ua, BigDecimal.ZERO, record.getAmount(), record.getAmount(), 1, 2, "自动提币失败，恢复冻结。");
				updateWithdrawCoinRecord(record, 4, "", "自动提币失败，详情：" + message);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	@Override
	public boolean updateWithdrawCoinRecordDownload(BQMemberRecharge record) throws Exception {
		BQAccount ua = bqAccountDao.getByUerAndCurrencyId(record.getF_user_id(), record.getCurrency_type_id());
		if (null == ua || ua.getBtc_locked().compareTo(record.getAmount()) < 0) {
			updateWithdrawCoinRecord(record, 4, "", "冻结金额不足。");
			return false;
		}
		
//		String hql = "from UserWithdrawCoinRecord r where r.state = :state and r.walletAddress = :walletAddress and r.userId = :userId order by r.amount desc";
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("state", Code.WithdrawCoinState.SUCCESS);
//		params.put("walletAddress", record.getWalletAddress());
//		params.put("userId", record.getUserId());
//		params.put("start", 0);
//		params.put("size", 1);
//		UserWithdrawCoinRecord maxRecord = withdrawCoinRecordDao.get(false, hql, params);
//		if (!Code.WithdrawCoinState.CHECKED.equals(record.getState()) && 
//				(null == maxRecord || maxRecord.getAmount().compareTo(record.getAmount()) < 0)) {
//			updateWithdrawCoinRecord(record, Code.WithdrawCoinState.MANUAL, "新地址或较大金额，转人工处理。");
//			return false;
//		}
		
		try {
			updateUserAccount(ua, BigDecimal.ZERO, record.getAmount().negate(), record.getAmount(), -1, 2, "自动提币扣除。");
			updateWithdrawCoinRecord(record, 3, "", "自动处理已下载");
		} catch (Exception e) {
			//updateWithdrawCoinRecord(record, Code.WithdrawCoinState.MANUAL, Code.WithdrawCoinState.MANUAL_SORT, "更新账户异常，详情：" + e.getMessage());
			throw new RuntimeException();
		}
		
		return true;
	}
	
	@Override
	public void saveCoinWalletAddress(int coinId, List<String> addresses, boolean erc20, boolean trc20) throws Exception {
		
		String sname = "/file-upload/walletaddress/btc";
		String realpath = Contents.BASE_UPLOAD_PATH + sname;
		String dbName = sname;

		File pfile = new File(realpath);
		if (!pfile.exists()) {
			pfile.mkdirs();
		}

		int width = 132;
		int height = 132;
		String format = "jpg";

		List<BQWalletQueueBtc> list = new ArrayList<BQWalletQueueBtc>();
		for (String address : addresses) {
			// 插入比特幣數據庫中
			String sDBPath = dbName + "/t_" + address + ".jpg";
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			// 内容所使用编码
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 1);
			// 生成表
			File pFile = new File(realpath, "/t_" + address + ".jpg");
			BitMatrix ma = new MultiFormatWriter().encode(address, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToFile(ma, format, pFile);

			BQWalletQueueBtc pBtc = new BQWalletQueueBtc();
			pBtc.setWallet_address(address);
			pBtc.setWallet_pic(sDBPath);
			pBtc.setCurrency_type_id(coinId);
			pBtc.setErc20(erc20);
			pBtc.setTrc20(trc20);
			list.add(pBtc);
		}
		
		bqWalletQueueBtcDao.insertDatas(list);
	}
	
	@Override
	public List<BQMemberRecharge> getWithdrawCoinRecords(List<BQCurrencyType> list) {
		String hql = "from BQMemberRecharge r where r.state = :states and r.operation = :operation" 
				+ " and r.receice_card_type = :cardType and (receice_card_no is not null or receice_card_no <> '') order by r.dateline asc";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("states", 0);
		params.put("operation", 2);
		params.put("cardType", 1);
		return bqMemberRechargeDao.find(hql, params, 1, 30);
	}
	
	@Override
	public BQAccount getUserAccountByAddress(String address, int currencyId) {
		String hql = "from BQMemberWallet a where a.wallet_address = :address and currency_type_id = :currencyId and erc20 = false";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("address", address);
		params.put("currencyId", currencyId);
		BQMemberWallet accountAddress = bqMemberWalletDao.get(hql, params);
		if (null == accountAddress) {
			LOGGER.error("充币地址：" + address + ", 未找到绑定的账户。");
			return null;
		}
		
		return bqAccountDao.getByUerAndCurrencyId(accountAddress.getF_user_id(), accountAddress.getCurrency_type_id());
	}
	
	@Override
	public BQAccount getUserAccountByErc20Address(String address, int currencyId) {
		String hql = "from BQMemberWallet a where a.wallet_address = :address and erc20 = true";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("address", address);
		BQMemberWallet accountAddress = bqMemberWalletDao.get(hql, params);
		if (null == accountAddress) {
			LOGGER.error("充币地址：" + address + ", 未找到绑定的账户。");
			return null;
		}
		
		return bqAccountDao.getByUerAndCurrencyId(accountAddress.getF_user_id(), currencyId);
	}
	
	@Override
	public BQAccount getUserAccountByTrc20Address(String address, int currencyId) {
		String hql = "from BQMemberWallet a where a.wallet_address = :address and trc20 = true";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("address", address);
		BQMemberWallet accountAddress = bqMemberWalletDao.get(hql, params);
		if (null == accountAddress) {
			LOGGER.error("充币地址：" + address + ", 未找到绑定的账户。");
			return null;
		}
		
		return bqAccountDao.getByUerAndCurrencyId(accountAddress.getF_user_id(), currencyId);
	}
	
	@Override
	public BQMemberRecharge getRechargeRecord(String txid, int currencyId) {
		String hql = "from BQMemberRecharge r where r.wallet_trade_id = :txid and r.currency_type_id = :currencyId and r.operation = 1";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("txid", txid);
		params.put("currencyId", currencyId);
		
		return bqMemberRechargeDao.get(hql, params);
	}
	
	@Override
	public long getUnuseAddressCount(int coinId) {
		Long unusedCount = spyMemcachedClient.get("unused_" + coinId);
		if (null == unusedCount) {
			unusedCount = bqWalletQueueBtcDao.getCount(coinId);
			if (null != unusedCount && unusedCount > 0) {
				spyMemcachedClient.set("unused_" + coinId, BTCQMemecacheKeys.TIMES.BPI, unusedCount);
			}
		}
		return (null == unusedCount) ? 0L : unusedCount.longValue();
	}
	
	@Override
	public long getErc20AddressCount() {
		Long unusedCount = spyMemcachedClient.get("unused_erc20");
		if (null == unusedCount) {
			unusedCount = bqWalletQueueBtcDao.getErc20Count();
			if (null != unusedCount && unusedCount > 0) {
				spyMemcachedClient.set("unused_erc20", BTCQMemecacheKeys.TIMES.BPI, unusedCount);
			}
		}
		return (null == unusedCount) ? 0L : unusedCount.longValue();
	}
	
	@Override
	public long getTrc20AddressCount() {
		Long unusedCount = spyMemcachedClient.get("unused_trc20");
		if (null == unusedCount) {
			unusedCount = bqWalletQueueBtcDao.getTrc20Count();
			if (null != unusedCount && unusedCount > 0) {
				spyMemcachedClient.set("unused_trc20", BTCQMemecacheKeys.TIMES.BPI, unusedCount);
			}
		}
		return (null == unusedCount) ? 0L : unusedCount.longValue();
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
	
	private void updateUserAccount(BQAccount ua, BigDecimal amount, BigDecimal frozen, BigDecimal changeAmount, int direction, int operation, String note) throws Exception {
		if (bqAccountDao.updateAmount(ua.getF_user_id(), ua.getCurrency_type_id(), amount, frozen) > 0) {
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog("用户" + ua.getF_user_id() + "充值金额" + amount.toString(), ua.getCurrency_type_id(),
					operation, direction, ua.getBtc(), amount, ua.getBtc().add(amount), ua.getBtc_locked(), ua.getBtc_locked().add(amount), bqAccountAmountLogDao.getUnix_TIMESTAMP(),
					ua.getF_user_id(), RandomUtil.getTransNo(), note, 0);
			bqAccountAmountLogDao.save(pAccountLog);
		} else {
			throw new ServiceException("用户资金异常");
		}
	}
	
	private void updateWithdrawCoinRecord(BQMemberRecharge record, int state, String txid, String remark) {
		if (record == null) return;
		
		String hql = "update BQMemberRecharge r set r.state = :state, r.remark = :remark, r.dateline = :dateline, r.wallet_trade_id = :wallet_trade_id where r.record_id = :id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("wallet_trade_id", txid);
		params.put("remark", remark);
		params.put("dateline", System.currentTimeMillis() / 1000);
		params.put("id", record.getRecord_id());
		
		LOGGER.info("提币记录[币种：" + record.getCurrency_type_id() + "用户："
				+ record.getF_user_id() + ", " + record.getF_user_email() + "地址："
				+ record.getReceice_card_no() + "金额：" + record.getAmount()
				+ "记录ID：" + record.getRecord_id() + "] 自动处理[状态：" + state + ", 备注："
				+ remark + "]");
		
		bqMemberRechargeDao.updateCondition(hql, params);
		
		// withdrawCoinRecordLogDao.save(getWithdrawCoinRecordLog(record, state, note));
	}
	
//	private UserWithdrawCoinRecordLog getWithdrawCoinRecordLog(UserWithdrawCoinRecord record, String newState, String note) {
//		UserWithdrawCoinRecordLog log = new UserWithdrawCoinRecordLog();
//		log.setOldState(record.getState());
//		log.setOperTime(System.currentTimeMillis());
//		log.setRecordId(record.getId());
//		log.setNewState(newState);
//		log.setNote(note);
//		log.setProcessType(Code.ProcessType.AUTO);
//		return log;
//	}
	
	private BQMemberRecharge getProcessingWithdrawCoinRecord(long serno) {
		String hql = "from BQMemberRecharge r where r.state = :state and r.id = :serno";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", 3);
		params.put("serno", serno);
		return bqMemberRechargeDao.get(hql, params);
	}
	
//	private UserRechargeCoinRecordLog buildNewRechargeRecordLog(UserRechargeCoinRecord rechargeCoinRecord, UserAccount ua) {
//		UserRechargeCoinRecordLog log = new UserRechargeCoinRecordLog();
//		log.setRecordId(rechargeCoinRecord.getId());
//		log.setAmount(rechargeCoinRecord.getAmount());
//		log.setCoinId(rechargeCoinRecord.getCoinId());
//		log.setConfirm(rechargeCoinRecord.getConfirm());
//		log.setNewState(rechargeCoinRecord.getState());
//		log.setOperTime(System.currentTimeMillis());
//		log.setPoundage(rechargeCoinRecord.getPoundage());
//		log.setProcessType(rechargeCoinRecord.getProcessType());
//		log.setRealAmount(rechargeCoinRecord.getRealAmount());
//		log.setWalletAddress(rechargeCoinRecord.getWalletAddress());
//		log.setWalletTxid(rechargeCoinRecord.getWalletTxid());
//		log.setUserId(rechargeCoinRecord.getUserId());
//		log.setUserName(rechargeCoinRecord.getUserName());
//		return log;
//	}
	
	private BQMemberRecharge buildSaveRechargeRecord(int coinId, BQAccount ua, String txid, String address, BigDecimal amount, boolean erc20usdt, boolean trc20usdt) {
		long time = bqMemberRechargeDao.getUnix_TIMESTAMP();
		BQMemberRecharge recharge = new BQMemberRecharge();
		
		BQCurrencyType currency = bqCurrencyTypeDao.get(BQCurrencyType.class, coinId);
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
	
	/**
	 * 调用对账功能，判断账户是否异常
	 * @param recode
	 * @return
	 */
	public boolean isAccountAbnormal(BQMemberRecharge recode) {
		// 人工审核通过，则不再校验地址和金额
		if ("已审核".equals(recode.getRemark())) {
			return false;
		}

		// 资金状况
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(recode.getF_user_id(), recode.getCurrency_type_id());
		BigDecimal btc = bqAccount.getBtc();
		BigDecimal lockBtc = bqAccount.getBtc_locked();
		
		List<BQAccountContract> bqAccountContract = bqAccountContractDao.findByUserID(recode.getF_user_id());
		BigDecimal hytotal = BigDecimal.ZERO;
		for (BQAccountContract bqAccountContract2 : bqAccountContract) {
			hytotal = hytotal.add(bqAccountContract2.getF_btc()).add(bqAccountContract2.getF_lockbtc());
		}

		// 充值提现状况
		BigDecimal cz = BigDecimal.ZERO, tx = BigDecimal.ZERO, zr = BigDecimal.ZERO, zc = BigDecimal.ZERO;
		List<Object[]> list = findSumByID(recode.getF_user_id());
		for (Object[] objects : list) {
			if (objects.length == 2) {
				switch ((Integer) (objects[1])) {
				case 1:
					cz = new BigDecimal(objects[0].toString());
					break;
				case 2:
					tx = new BigDecimal(objects[0].toString());
					break;
				case 3:
					zr = new BigDecimal(objects[0].toString());
					break;
				case 4:
					zc = new BigDecimal(objects[0].toString());
					break;
				default:
					break;
				}
			}
		}

		// 盈亏状况
		BQMemberStatus bqMemberStatus = bqMemberStatusDao.get(BQMemberStatus.class, recode.getF_user_id());
		BigDecimal wqyke = bqMemberStatus.getLast_comein();

		BQInviteAwardCount bqInviteAwardCount = findInviteAwardCountByUserId(recode.getF_user_id());
		BigDecimal jjr = (null == bqInviteAwardCount) ? BigDecimal.ZERO : bqInviteAwardCount.getTotal_count();

		List<BQTransaction> listTrans = findCurrentTransaction();
		Map<String, BigDecimal> yke = new HashMap<String, BigDecimal>();
		BigDecimal ykenum = BigDecimal.ZERO;
		for (BQTransaction bqTransaction : listTrans) {
			BigDecimal yk = findYkeByUserId(bqTransaction.getTransaction_code(), recode.getF_user_id());
			ykenum = ykenum.add(yk);
			yke.put(bqTransaction.getTransaction_code(), yk);
		}

		// 持仓状况
		BigDecimal ccbzj = findMemberTransactionDepositByUserId(recode.getF_user_id());
		
		BigDecimal result = cz.add(zr).subtract(tx).subtract(zc).add(ykenum).add(wqyke).add(jjr).subtract(btc).subtract(lockBtc).subtract(hytotal).subtract(ccbzj);
		if (result.setScale(8).doubleValue() < -0.01D) {
			return true;
		}
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	private List<Object[]> findSumByID(long userId) {
		String hql="SELECT SUM(t.amount) AS amount,t.operation AS operation from BQMemberRecharge t where t.f_user_id = :userId AND t.state=:state group by t.operation";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("state", 1);
		return bqMemberRechargeDao.queryparams(hql, map).list();
	}
	
	private BQInviteAwardCount findInviteAwardCountByUserId(long userId) {
		String hql = "from BQInviteAwardCount where f_user_id=:userId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		return (BQInviteAwardCount) bqInviteAwardCountDao.queryparams(hql, map).uniqueResult();
	}
	
	/*private BigDecimal findMemberSettleSumByUserId(long userId) {
		String hql = "SELECT SUM(f_settle_apportion) FROM BQMemberSettle WHERE f_user_id = :userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		return (BigDecimal) bqMemberSettleDao.queryparams(hql, params).uniqueResult();
	}*/
	
	private List<BQTransaction> findCurrentTransaction() {
		String hql = "FROM BQTransaction WHERE transaction_state = :state ORDER BY sort ASC";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Contents.TRANSACTION_STATE_ZC);
		return bqTransactionDao.find(hql, params);
	}
	
	private BigDecimal findYkeByUserId(String bqCode, long userId) {
		StringBuilder sql = new StringBuilder("select ifnull(SUM(trade_income-trade_fee), 0) as trade from " + bqCode + "_trade_log where f_user_id = :userId");
		return (BigDecimal) tradeLogDao.createSQLQuery(sql.toString()).setLong("userId", userId).uniqueResult();
	}
	
	private BigDecimal findMemberTransactionDepositByUserId(long userId) {
		String hql = "SELECT sum(deposit) from BQMemberTransaction where f_user_id = :uid ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", userId);
		return bqMemberTransactionDao.sum(hql, map);
	}
	
}
