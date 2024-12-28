package com.btc100.btc_q.action.admin;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQBfxMinHold;
import com.btc100.btc_q.entity.BQBfxVipSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQBfxMinHoldService;
import com.btc100.btc_q.service.BQBfxVipSettingService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQSurecomeService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

public class BaseAction {

	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private DeliveryTransactionService deliveryTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQSurecomeService bqSurecomeService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	public TradeLogService tradeLogService;
	@Autowired
	protected BQTransactionTypeService bqTransTypeService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQBfxMinHoldService bqBfxMinHoldService;
	@Autowired
	private BQBfxVipSettingService bqBfxVipSettingService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;

	/*
	 * 后台JSP根目录
	 */
	public String url_ROOT(String returnValue) {
		return "qtwner/up/down/togo/" + returnValue;
	}

	/*
	 * 后台内容页
	 */
	public String url_JSP(String returnValue) {
		return "qtwner/up/down/togo/jsp/" + returnValue;
	}

	/*
	 * 重定向
	 */
	public String redirect(String svalue) {
		return "redirect:" + svalue;
	}

	/**
	 * 重定向
	 * 
	 * @param svalue
	 * @return
	 */
	public String forward(String svalue) {
		return "forward:" + svalue;
	}

	/**
	 * 返回当前的委托
	 */
	public List<BQTransaction> findCurrTrans() {
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		return transactionList;
	}

	/**
	 * 找到某一个指定的委托
	 * 
	 * @return
	 */
	public BQTransaction findTransByCode(String tcode) {
		BQTransaction transaction = transactionService.readTransByCode(tcode);
		return transaction;
	}
	
	/**
	 * 返回当前的委托
	 */
	public List<DeliveryTransaction> findCurrDTrans() {
		List<DeliveryTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = deliveryTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		return transactionList;
	}
	
	/**
	 * 找到某一个指定的委托
	 * 
	 * @return
	 */
	public DeliveryTransaction findDTransByCode(String tcode) {
		DeliveryTransaction transaction = null;
		List<DeliveryTransaction> trans = findCurrDTrans();
		for (DeliveryTransaction bqTran : trans) {
			if (bqTran.getTransaction_code().equalsIgnoreCase(tcode)) {
				transaction = bqTran;
				break;
			}
		}
		return transaction;
	}

	/**
	 * 通过邮箱找到用户
	 * 
	 * @param uemail
	 */
	public BQMember findUsersByEmail(String uemail) {
		return bqMemberService.readUserByEmail(uemail);
	}

	public BQSurecome findCurrManager(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute("owner");
		if (obj == null) {
			return null;
		}
		
		BQSurecome manager = (BQSurecome) obj;
		return manager;
	}
	
	public boolean checkYZPwd(HttpServletRequest request, String pwd) throws Exception {
		BQSurecome ob = (BQSurecome)request.getSession().getAttribute("owner");
		if (ob == null)
			return false;
		
		return bqSurecomeService.surepwd2(ob.getUser_name(), pwd);
	}

	public String errorPage(Model mo, String msg) {
		mo.addAttribute("type", 2);
		mo.addAttribute("msg", msg);
		mo.addAttribute("ref", "javascript:history.go(-1);");

		return "/error/SystemInfo";
	}
	
	protected void toWeb(HttpServletResponse response, Object object) {
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter objPw = response.getWriter();
			
			objPw.print(object);
			objPw.flush();
			objPw.close();
		} catch (Exception ex) {

		}
	}

	protected String getRequestRoot(HttpServletRequest request) {
		return Utils.getWebRoot(request);
	}
	
	/**
	 * 根据code查找指数
	 * 
	 * @return
	 */
	public BigDecimal findBPIByCode(String code) {
		BigDecimal btc_index_price = spyMemcachedClient.get(code);
		if (btc_index_price == null) {
			BQTransactionType transType = bqTransTypeService.readTransTypeByCode(code);
			if (transType == null) return btc_index_price;
			btc_index_price = bqPriceIndexCacheService.readByCode(transType.getType_code()).getPrice();
		}

		return btc_index_price;
	}
	
	public BigDecimal findBPI_Other_ByCode(String code) {
		String codeOther = code + "_other";
		BigDecimal btc_index_price = spyMemcachedClient.get(codeOther);
		if (btc_index_price == null) {
			BQTransactionType transType = bqTransTypeService.readTransTypeByCode(code);
			if (transType == null) return btc_index_price;
			btc_index_price = bqPriceIndexCacheService.readByCode(transType.getType_code()).getOtherprice();
			spyMemcachedClient.set(codeOther, BTCQMemecacheKeys.TIMES.ONE_HOUR, btc_index_price);
		}
		return btc_index_price;
	}

	
	public BQTransaction findCurrTransByCode(String code) {
		BQTransaction bqTransaction = null;
		List<BQTransaction> trans = findCurrTrans();
		for (BQTransaction bqTran : trans) {
			if (bqTran.getTransaction_code().equalsIgnoreCase(code)) {
				bqTransaction = bqTran;
				break;
			}
		}
		return bqTransaction;
	}
	
	/**
	 * 当前可用的所有现货交易对
	 * @return List<BQSpotMarketCoinPair>
	 */
	public List<BQSpotMarketCoinPair> findCurrCoinPair() {
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.readByState_new(Contents.COINPAIR_STATE_NORMAL);
		}
		return coinPairs;
	}

	/**
	 * 管理员可见交易对
	 * @return List<BQSpotMarketCoinPair>
	 */
	public List<BQSpotMarketCoinPair> findCurrCoinPairManager(BQSurecome manager){
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.readByState_new(Contents.COINPAIR_STATE_NORMAL);
		}
		String coins = manager.getUser_coins();
		String[] results;
		List<BQSpotMarketCoinPair> managerCoinPairs = new ArrayList<>();
		if (null != coins && !"".equals(coins)) {
			results = coins.split(",");
			for (String result: results) {
				for (BQSpotMarketCoinPair spotMarketCoinPair:coinPairs) {
					if (result.equals(spotMarketCoinPair.getCoin_name())){
						managerCoinPairs.add(spotMarketCoinPair);
					}
				}
			}
		}else {
			return managerCoinPairs;
		}
		return managerCoinPairs;
	}

	/**
	 * 根据交易区id查找交易对列表
	 * @param marketId
	 * @return List<BQSpotMarketCoinPair>
	 */
	public List<BQSpotMarketCoinPair> findCurrCoinPairs(int marketId) {
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.readByState_new(Contents.COINPAIR_STATE_NORMAL);
		}

		List<BQSpotMarketCoinPair> bqs = new ArrayList<BQSpotMarketCoinPair>();
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			if (coinPair.getMarket_id() == marketId) {
				bqs.add(coinPair);
			}
		}

		return bqs;
	}

	/**
	 * 根据交易对名称查找交易对
	 * @param coinPairName
	 * @return BQSpotMarketCoinPair
	 */
	public BQSpotMarketCoinPair findCurrCoinPairByName(String coinPairName) {
		BQSpotMarketCoinPair markrtCoinPair = null;
		List<BQSpotMarketCoinPair> coinPairs = findCurrCoinPair();
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			if (coinPair.getCoin_pair_name().equalsIgnoreCase(coinPairName)) {
				markrtCoinPair = coinPair;
				break;
			}
		}
		return markrtCoinPair;
	}
	
	public BQMemberLevel getLevelByUserBfx(BQMember user) {
		if (user == null) return null;
		
		BQMemberLevel userlevel = bqMemberLevelService.findByLevelId(user.getF_user_level_id());
		if (userlevel.getEat_commission_rate().compareTo(BigDecimal.ZERO) == 0) {
			return userlevel;
		}
		
		if (user.getF_user_oms() != 0) {
			return userlevel;
		}
		
		BQBfxFeeSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (setting == null) {
			setting = bqBfxFeeSettingService.readOne();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting == null) {
			return userlevel;
		}
			
		long dayTime = TimeUtil.getTimesmorning(System.currentTimeMillis() - 24 * 3600 * 1000);
		if (setting.getVip_state() != 1) {
			return userlevel;
		}
		
		BQBfxMinHold minHold = bqBfxMinHoldService.readBfxMinHoldByUserAndTime(user.getF_user_id(), dayTime);
		if (minHold != null && minHold.getMin_hold().compareTo(BigDecimal.ZERO) > 0) {
			List<BQBfxVipSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_VIP_SETTINGS);
			if (settings == null) {
				settings = bqBfxVipSettingService.readAll();
				if (settings != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.BFX_VIP_SETTINGS, BTCQMemecacheKeys.TIMES.ONE_MIN, settings);
				}
			}
			
			BQBfxVipSetting vipSetting = null;
			for (BQBfxVipSetting vipSet : settings) {
				if (minHold.getMin_hold().compareTo(vipSet.getHold_count()) >= 0) {
					vipSetting = vipSet;
					break;
				}
			}
			
			if (vipSetting != null) {
				if (vipSetting.getLevel_id() > userlevel.getLevel_id()) {
					BQMemberLevel level =  bqMemberLevelService.findByLevelId(vipSetting.getLevel_id());
					if (level != null) {
						return level;
					}
				}
			}
		}
		
		return userlevel;
	}
}
