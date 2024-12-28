package com.btc100.btc_q.wallet.platform;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQMemberWallet;
import com.btc100.btc_q.service.BQAccountAmountLogService;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCommonCreditLogService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQInviteAwardCountSerivice;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberSettleService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQMemberWalletService;
import com.btc100.btc_q.service.ContractAccountLogService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.service.WalletPlatformService;
import com.btc100.btc_q.util.CommonUtil;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.SendtelcodeUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.BalanceAccountVO;
import com.wallet.platfrom.sdk.IWalletPlatfromInterface;
import com.wallet.platfrom.sdk.ProcessResult;
import com.wallet.platfrom.sdk.WalletPlatfromConfig;
import com.wallet.platfrom.sdk.beans.WithdrawDataBean;

@Component
public class WalletPlatformImpl implements IWalletPlatfromInterface {
	private static final Logger LOGGER = Logger.getLogger(WalletPlatformImpl.class);
	
	private static final WalletPlatfromConfig config = new WalletPlatfromConfig();
	static {
		config.setPublicKeyFile(UrlConfig.getValue("autoService.publicKeyFile"));
		config.setPrivateKeyFile(UrlConfig.getValue("autoService.privateKeyFile"));
	}
	
	@Autowired
	WalletPlatformService walletPlatformService;
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	BQAccountService bqAccountService;
	@Autowired
	TradeLogService tradeLogService;
	@Autowired
	BQAccountContractService bqAccountContractService;
	@Autowired
	BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	BQInviteAwardCountSerivice bqInviteAwardCountSerivice;
	@Autowired
	BQMemberSettleService bqMemberSettleService;
	@Autowired
	ContractAccountLogService contractAccountLogService;
	@Autowired
	PendingDelegationService pendingDelegationService;
	@Autowired
	PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	BQCommonCreditLogService bqCommonCreditLogService;
	@Autowired
	BQAccountAmountLogService bqAccountAmountLogService;
	@Autowired
	BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQMemberWalletService bqMemberWalletServcie;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	
	@Override
	public WalletPlatfromConfig getConfig() {
		return config;
	}

	@Override
	public synchronized Integer processAddress(String symbol, List<String> addresses) {
		try {
			if ("trc20".equals(symbol.toLowerCase())) {
				Long unusedCount = walletPlatformService.getTrc20AddressCount();
				if (null != addresses && addresses.size() > 0) {
					unusedCount = unusedCount + addresses.size();
					
					walletPlatformService.saveCoinWalletAddress(0, addresses, false, true);
					spyMemcachedClient.set("unused_trc20", BTCQMemecacheKeys.TIMES.BPI, unusedCount);
				}
				
				Long needCount = 50 - unusedCount;
				return (needCount <= 0) ? 0 : needCount.intValue();
			} else if ("erc20".equals(symbol.toLowerCase())) {
				Long unusedCount = walletPlatformService.getErc20AddressCount();
				if (null != addresses && addresses.size() > 0) {
					unusedCount = unusedCount + addresses.size();
					
					walletPlatformService.saveCoinWalletAddress(0, addresses, true, false);
					spyMemcachedClient.set("unused_erc20", BTCQMemecacheKeys.TIMES.BPI, unusedCount);
				}
				
				Long needCount = 50 - unusedCount;
				return (needCount <= 0) ? 0 : needCount.intValue();
			} else {
				BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeName(symbol.toUpperCase());
				Long unusedCount = walletPlatformService.getUnuseAddressCount(currencyType.getType_id());
				if (null != addresses && addresses.size() > 0) {
					unusedCount = unusedCount + addresses.size();
					
					walletPlatformService.saveCoinWalletAddress(currencyType.getType_id(), addresses, false, false);
					spyMemcachedClient.set("unused_" + currencyType.getType_id(), BTCQMemecacheKeys.TIMES.BPI, unusedCount);
				}
				
				Long needCount = 50 - unusedCount;
				return (needCount <= 0) ? 0 : needCount.intValue();
			}
		} catch (Exception e) {
			StringBuffer buf = new StringBuffer();
			if (null != addresses && addresses.size() > 0) {
				for (String address : addresses) {
					buf.append(address).append(",");
				}
			}
			LOGGER.error("充币地址上传处理异常，币种：" + symbol + "，地址：" + buf.toString(), e);
			return 0;
		} 
	}

	@Override
	public synchronized ProcessResult charge(String txid, String address, String symbol, BigDecimal amount) {
		
		try {
			boolean isErc20usdt = false;
			boolean isTrc20usdt = false;
			if (symbol.equalsIgnoreCase("USDT(ERC20)")) {
				symbol = "USDT";
				isErc20usdt = true;
			} else if (symbol.equalsIgnoreCase("USDT(TRC20)")) {
				symbol = "USDT";
				isTrc20usdt = true;
			}
			
			if (symbol.equalsIgnoreCase("usdr") && !txid.contains("站内提币_")) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("无该币种。");
				LOGGER.info("无该币种.");
				return result;
			}
			
			BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeName(symbol.toUpperCase());
			if (currencyType == null) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("无该币种。");
				LOGGER.info("无该币种。");
				return result;
			}
			
			BQMemberRecharge rechargeCoinRecord = walletPlatformService.getRechargeRecord(txid, currencyType.getType_id());
			if (null != rechargeCoinRecord) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("重复充值订单，忽略处理。");
				LOGGER.info("重复充值订单，忽略处理。");
				return result;
			}
			
			BQAccount ua = null;
			if (currencyType.getErc20() && isErc20usdt) {
				ua = walletPlatformService.getUserAccountByErc20Address(address, currencyType.getType_id());
			} else if (currencyType.getTrc20() && isTrc20usdt) {
				ua = walletPlatformService.getUserAccountByTrc20Address(address, currencyType.getType_id());
			} else {
				ua = walletPlatformService.getUserAccountByAddress(address, currencyType.getType_id());
			}
			
			if (null == ua || ua.getF_user_id() <= 0) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("未查询到地址绑定的用户。");
				LOGGER.info("未查询到地址绑定的用户。");
				return result;
			}
			
			BQMember user = bqMemberService.findUserById(ua.getF_user_id());
			if (null == user) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("用户信息未查询到，用户ID【" + ua.getF_user_id() + ")");
				LOGGER.info("用户信息未查询到，用户ID【" + ua.getF_user_id() + ")");
				return result;
			}
			walletPlatformService.saveChargeRecord(ua.getCurrency_type_id(), ua, txid, address, amount, isErc20usdt, isTrc20usdt);
			ProcessResult result = new ProcessResult();
			result.setCode(0);
			result.setSuccess(true);
			result.setMessage("处理成功");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("上传充币记录处理异常，交易ID：" + txid + "，充币地址：" + address + "，充币金额：" + amount, e);
			ProcessResult result = new ProcessResult();
			result.setCode(0);
			result.setSuccess(false);
			result.setMessage("程序异常。" + e.getMessage());
			return result;
		}
		
	}

	@Override
	public synchronized ProcessResult confirmCharge(String txid, String symbol, Integer confirms) {
		try {
			if (0 >= confirms) {
				ProcessResult result = new ProcessResult();
				result.setCode(1);
				result.setSuccess(true);
				result.setMessage("未达到确认数。");
				return result;
			}
			
			if (symbol.equalsIgnoreCase("USDT(ERC20)")) {
				symbol = "USDT";
			}
			
			if (symbol.equalsIgnoreCase("USDT(TRC20)")) {
				symbol = "USDT";
			}
			
			if (symbol.equalsIgnoreCase("usdr") && !txid.contains("站内提币_")) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("无该币种。");
				return result;
			}
			
			BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeName(symbol.toUpperCase());
			if (currencyType == null) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("无该币种。");
				return result;
			}
			
			BQMemberRecharge rechargeCoinRecord = walletPlatformService.getRechargeRecord(txid, currencyType.getType_id());
			if (null == rechargeCoinRecord) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("未查询到该笔交易信息。 ");
				return result;
			}
			
			if (rechargeCoinRecord.getState() != 0) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("该笔交易的状态不正确。");
				return result;
			}
			
			BQAccount ua = bqAccountService.readAccount(rechargeCoinRecord.getF_user_id(), rechargeCoinRecord.getCurrency_type_id());
			if (null == ua) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("未查询到用户对应的账户。");
				return result;
			}
			
			BQMember user = bqMemberService.findUserById(ua.getF_user_id());
			if (null == user) {
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("用户信息未查询到，用户ID【" + ua.getF_user_id() + "】");
				return result;
			}
			
			try {
				walletPlatformService.confirmChargeRecord(ua, rechargeCoinRecord, 1, "自动充值确认到账。");
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				
				try {
					if (!StringUtil.isEmpty(user.getF_real_email())) {
						String content = "<div>尊敬的 XKD用户，您好！<br>&nbsp;&nbsp;&nbsp;&nbsp;您充值的" + rechargeCoinRecord.getAmount().doubleValue() + symbol.toUpperCase()
							+ "已经到账。<br><br>" + UrlConfig.getValue("webRoot") +"</div>";
						new SendMail().send(user.getF_real_email(), content);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return result;
			} catch (Exception e) {
				LOGGER.error("确认充币记录处理异常，交易ID：" + txid, e);
				ProcessResult result = new ProcessResult();
				result.setCode(1);
				result.setSuccess(false);
				result.setMessage("程序异常。详情：" + e.getMessage());
				return result;
			}
			
		} catch (Exception e) {
			LOGGER.error("确认充币记录处理异常，交易ID：" + txid, e);
			ProcessResult result = new ProcessResult();
			result.setCode(1);
			result.setSuccess(false);
			result.setMessage("程序异常。详情：" + e.getMessage());
			return result;
		} 
	}

	@Override
	public synchronized List<WithdrawDataBean> getWithdrawDatas() {
		
		try {
			List<BQCurrencyType> list = bqCurrencyTypeService.readAll();
			List<BQMemberRecharge> records = walletPlatformService.getWithdrawCoinRecords(list);
			if (null == records || records.size() == 0) {
				LOGGER.debug("暂未需要提币的数据。");
				return new ArrayList<WithdrawDataBean>();
			}
			
			List<WithdrawDataBean> datas = new ArrayList<WithdrawDataBean>();
			for (BQMemberRecharge r : records) {
				boolean isAllowed = false;
				
				try {
					BQMemberRecharge record = bqMemberRechargeService.findByRecordId(r.getRecord_id());
					if (record.getState() != 0) {
						continue;
					}
					
					BQAccount ua = bqAccountService.readAccount(r.getF_user_id(), r.getCurrency_type_id());
					if (ua == null) continue;
					
					try {
						isAllowed = walletPlatformService.updateWithdrawCoinRecordDownload(r);
					} catch (Exception e) {
						LOGGER.error("updateWithdrawCoinRecordDownload异常,r.id: " + r.getRecord_id() + ", r.UserId: " + r.getF_user_id() + ", r.UserEmail: " + r.getF_user_email(), e);
					}
					
					if (isAllowed) {
						BQCurrencyType type = bqCurrencyTypeService.readByTypeId(r.getCurrency_type_id());
						if (type == null) continue;
						
						WithdrawDataBean data = new WithdrawDataBean();
						data.setAddress(r.getReceice_card_no());
						data.setAmount(r.getAmount().subtract(r.getFee()));
						data.setSerno(r.getRecord_id() + "");
						data.setSymbol(type.getType_name());
						if ("USDT".equalsIgnoreCase(type.getType_name()) && record.getUsdt_erc20()) {
							data.setSymbol("USDT(ERC20)");
						} else if ("USDT".equalsIgnoreCase(type.getType_name()) && record.getUsdt_trc20()) {
							data.setSymbol("USDT(TRC20)");
						}
						
						// 站内提币
						BQMemberWallet walletAddress = bqMemberWalletServcie.readByAddress(r.getReceice_card_no());
						if (walletAddress != null) {
							long id = record.getF_user_id();
							
							BalanceAccountVO allBalance = new BalanceAccountVO();
							BalanceAccountVO generalBalance = new BalanceAccountVO();
							List<BalanceAccountVO> contractBalances = new ArrayList<BalanceAccountVO>();
							List<BalanceAccountVO> deliveryContractBalances = new ArrayList<BalanceAccountVO>();
							bqMemberRechargeService.getBalanceAccount(id, record.getCurrency_type_id(), allBalance, generalBalance, contractBalances, deliveryContractBalances);
							
							Long recordTime = record.getDateline();
							Date date = new Date(recordTime * 1000);
							Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
							calendar.setTime(date);
							calendar.set(Calendar.HOUR_OF_DAY, 0);
							calendar.set(Calendar.MINUTE, 0);
							calendar.set(Calendar.SECOND, 0);
							Long beginTime = calendar.getTime().getTime() / 1000;
							BigDecimal dayWithdraw = bqMemberRechargeService.getAllWithdrawSuccessBetweenTime(id, record.getCurrency_type_id(), beginTime, recordTime);
							
							double netWorth = allBalance.getNetWorth();
							double shouldAvai = 0;
							if (generalBalance.getShouldAvailable() < 0) {
								shouldAvai = generalBalance.getShouldAvailable();
							}
							double contractShould = 0;
							for (BalanceAccountVO vo : contractBalances) {
								if (vo.getShouldAvailable() < 0) {
									contractShould += vo.getShouldAvailable();
								}
							}
							double deliveryShould = 0;
							for (BalanceAccountVO vo : deliveryContractBalances) {
								if (vo.getShouldAvailable() < 0) {
									deliveryShould += vo.getShouldAvailable();
								}
							}
							
							netWorth = shouldAvai + contractShould + deliveryShould - Math.abs(netWorth);
							
							if (r.getCurrency_type_id() == Contents.TRADE_CURRENCY_TYPE_ID || type.getType_name().equalsIgnoreCase("usdr")) {
								if (Math.abs(netWorth) < 1) netWorth = 0;
							}
							
							boolean check = true;
							String message = "";
							String txid = "";
							if (BigDecimal.ZERO.compareTo(new BigDecimal(netWorth)) != 0) {
								check = false;
								message = "净值不等于0";
							} else if (generalBalance.getManualRecharge() > 0) {
								check = false;
								message = "人工充值大于0";
							} else if (type.getMax_widthdraw().compareTo(BigDecimal.ZERO) == 1 && record.getAmount().compareTo(type.getMax_widthdraw()) == 1) {
								check = false;
								message = "超过单笔最大额";
							} else if (type.getDay_max_widthdraw().compareTo(BigDecimal.ZERO) == 1 && record.getAmount().add(dayWithdraw).compareTo(type.getDay_max_widthdraw()) == 1) {
								check = false;
								message = "超过日最大提币额";
							}
							
							if (check) {
								txid = "站内提币_" + CommonUtil.getUUID();
							}
							
							ProcessResult ret = processWithdrawResult(check, txid, message, data);
							
							if (check && ret.getSuccess()) {
								charge(txid, data.getAddress(), data.getSymbol(), data.getAmount());
								confirmCharge(txid, data.getSymbol(), 1);
							}
							
							continue;
						}
						
						
						datas.add(data);
					}
					
				} catch (Exception e) {
					LOGGER.error("updateWithdrawCoinRecordDownload异常,r.id: " + r.getRecord_id() + ", r.UserId: " + r.getF_user_id() + ", r.UserEmail: " + r.getF_user_email(), e);
				}
			}
			
			LOGGER.debug("下载提币数据。datas size: " + datas.size());
			return datas;
		} catch (Exception e) {
			LOGGER.error("下载提币数据异常，详情：", e);
			return new ArrayList<WithdrawDataBean>();
		} 
	}

	@Override
	public WithdrawDataBean getProcessingWithdrawData(String serno) {
		try {
			BQMemberRecharge record = walletPlatformService.getProcessingRecord(serno);
			if (null == record) {
				return new WithdrawDataBean();
			}
			BQCurrencyType type = bqCurrencyTypeService.readByTypeId(record.getCurrency_type_id());
			if (type == null) return new WithdrawDataBean();;
			
			WithdrawDataBean data = new WithdrawDataBean();
			data.setAddress(record.getReceice_card_no());
			data.setAmount(record.getAmount());
			data.setSerno(record.getRecord_id() + "");
			data.setSymbol(type.getType_name());
			
			if ("USDT".equalsIgnoreCase(type.getType_name()) && record.getUsdt_erc20()) {
				data.setSymbol("USDT(ERC20)");
			} else if ("USDT".equalsIgnoreCase(type.getType_name()) && record.getUsdt_trc20()) {
				data.setSymbol("USDT(TRC20)");
			}
			
			return data;
		} catch (Exception e) {
			LOGGER.error("下载提币数据异常，详情：", e);
			return new WithdrawDataBean();
		}
	}

	@Override
	public synchronized ProcessResult processWithdrawResult(Boolean success, String txid, String message,
			WithdrawDataBean withdrawData) {
		try {
			BQMemberRecharge record = bqMemberRechargeService.findByRecordId(Convert.toLong(withdrawData.getSerno(),0));
			if (null == record) {
				String resultMsg = "记录ID：" + withdrawData.getSerno() + "未查询到提币记录。";
				LOGGER.error(resultMsg);
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setMessage(resultMsg);
				result.setSuccess(true);
				return result;
			}
			
			if (record.getState() != 3) {
				String resultMsg = "记录ID：" + withdrawData.getSerno() + "处理状态已经改变，不是【PROCESSING:处理中】";
				LOGGER.error(resultMsg);
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setMessage(resultMsg);
				result.setSuccess(true);
				return result;
			}
			
			try {
				walletPlatformService.updateWithdrawRecordAndAccount(success, withdrawData.getSerno(), txid, message);
				if ((!success || StringUtil.isEmpty(txid)) && !StringUtil.isEmpty(withdrawData.getEmails())) {
					final String[] emailArray = withdrawData.getEmails().split(","); 
					BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(record.getCurrency_type_id());
					final String symbol = currencyType.getType_name().toUpperCase();
					Runnable runnable = new Runnable() {
			            public void run() {
			            	for (int i = 0; i < emailArray.length; i++) {
				            	try {
				            		String email = emailArray[i];
				            		SendMail themail = new SendMail();
				            		String content = symbol + "币提币需人工审核，请登录xkd后台管理处理";
			            			themail.send(email, content);
				            	} catch (Exception e) {
				            	}
			            	}
			            }
			        };
			        
			        Thread thread = new Thread(runnable);
			        thread.start();
				}
				
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				return result;
			} catch (Exception e) {
				LOGGER.error("提币结果上传处理异常，数据：" + withdrawData.toString(), e);
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setSuccess(true);
				result.setMessage("程序异常，详情：" + e.getMessage());
				return result;
			}
		} catch (Exception e) {
			LOGGER.error("提币结果上传处理异常，数据：" + withdrawData.toString(), e);
			ProcessResult result = new ProcessResult();
			result.setCode(0);
			result.setSuccess(true);
			result.setMessage("程序异常，详情：" + e.getMessage());
			return result;
		}
	}

	@Override
	public ProcessResult processGetWithdrawCheckData(String serno) {
		try {
			BQMemberRecharge record = this.bqMemberRechargeService.findByRecordId(Convert.toLong(serno, 0));
			if (null == record) {
				String resultMsg = "记录ID：" + serno + "未查询到提币记录。";
				LOGGER.error(resultMsg);
				ProcessResult result = new ProcessResult();
				result.setCode(0);
				result.setMessage(resultMsg);
				result.setSuccess(false);
				return result;
			}

			ProcessResult result = new ProcessResult();
			result.setMessage(getBalanceData(record));
			result.setCode(0);
			result.setSuccess(true);
			return result;
		} catch (Exception e) {
			LOGGER.error("获取提笔审核数据异常，serno：" + serno, e);
			ProcessResult result = new ProcessResult();
			result.setCode(0);
			result.setSuccess(false);
			result.setMessage("程序异常，详情：" + e.getMessage());
			return result;
		}
	}
	
	@Override
	public ProcessResult processWithdrawManualOperationNotify(String emails) {
		final String[] emailArray = emails.split(","); 
		Runnable runnable = new Runnable() {
            public void run() {
            	for (int i = 0; i < emailArray.length; i++) {
	            	try {
	            	   	try {
		            		String email = emailArray[i];
		            		SendMail themail = new SendMail();
		            		String content = "xkd提币需人工审核，请登录xkd钱包控制台处理";
	            			themail.send(email, content);
		            	} catch (Exception e) {
		            	}
	            	} catch (Exception e) {
	            		
	            	}
            	}
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

		ProcessResult result = new ProcessResult();
		result.setCode(0);
		result.setSuccess(true);
		return result;
	}
	
	private String getBalanceData(BQMemberRecharge record) throws Exception {
		if (record == null) return "";
		
		BQCurrencyType currency = bqCurrencyTypeService.readByTypeId(record.getCurrency_type_id());
		if (currency == null) return "";
		
		StringBuffer buf = new StringBuffer();
		long id = record.getF_user_id();
		
		BalanceAccountVO allBalance = new BalanceAccountVO();
		BalanceAccountVO generalBalance = new BalanceAccountVO();
		List<BalanceAccountVO> contractBalances = new ArrayList<BalanceAccountVO>();
		List<BalanceAccountVO> deliveryContractBalances = new ArrayList<BalanceAccountVO>();
		bqMemberRechargeService.getBalanceAccount(id, record.getCurrency_type_id(), allBalance, generalBalance, contractBalances, deliveryContractBalances);
		
		Long recordTime = record.getDateline();
		Date date = new Date(recordTime * 1000);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Long beginTime = calendar.getTime().getTime() / 1000;
		BigDecimal dayWithdraw = bqMemberRechargeService.getAllWithdrawSuccessBetweenTime(id, record.getCurrency_type_id(), beginTime, recordTime);
		
		double netWorth = allBalance.getNetWorth();
		double shouldAvai = 0;
		if (generalBalance.getShouldAvailable() < 0) {
			shouldAvai = generalBalance.getShouldAvailable();
		}
		double contractShould = 0;
		for (BalanceAccountVO vo : contractBalances) {
			if (vo.getShouldAvailable() < 0) {
				contractShould += vo.getShouldAvailable();
			}
		}
		double deliveryShould = 0;
		for (BalanceAccountVO vo : deliveryContractBalances) {
			if (vo.getShouldAvailable() < 0) {
				deliveryShould += vo.getShouldAvailable();
			}
		}
		
		netWorth = shouldAvai + contractShould + deliveryShould - Math.abs(netWorth);
		
		if (currency.getType_id() == Contents.TRADE_CURRENCY_TYPE_ID || currency.getType_name().equalsIgnoreCase("usdr")) {
			if (Math.abs(netWorth) < 1) netWorth = 0;
		}
		
		buf.append("{");
		buf.append("\\\"manualRecharge\\\":").append(generalBalance.getManualRecharge()).append(",");
		buf.append("\\\"netWorth\\\":").append(netWorth).append(",");
		buf.append("\\\"dayWithdraw\\\":").append(dayWithdraw.doubleValue()).append(",");
		buf.append("\\\"maxWithdraw\\\":").append(currency.getMax_widthdraw().doubleValue()).append(",");
		buf.append("\\\"maxDayWithdraw\\\":").append(currency.getDay_max_widthdraw().doubleValue()).append(",");
		buf.append("}");
		
		return buf.toString();
	}
}
