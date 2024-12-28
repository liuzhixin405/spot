package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberInviteLogService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQStopLossService;
import com.btc100.btc_q.service.BQTransactionFeeRateService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryStopLossService;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.service.MemberTradeDetailService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.VIEWUsermanagerService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.BQTransVo;
import com.btc100.btc_q.vo.ContractBalanceVo;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.PendingDelegationVO;
import com.btc100.btc_q.vo.RebateRankingVo;
import com.btc100.btc_q.vo.SystemContext;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.btc100.btc_q.vo.UserInviteMemberVo;

@Controller
@RequestMapping("/managerQuantRisk")
public class QuantRiskAction extends BaseAction {
	
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private DeliveryMemberTransactionService deliveryMemberTransactionService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationService deliveryPendingDelegationService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQStopLossService bqStopLossService;
	@Autowired
	private DeliveryStopLossService deliveryStopLossService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private DeliveryAccountContractService deliveryAccountContractService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private DeliveryPendingDelegationCacheService deliveryPendingDelegationCacheService;
	@Autowired
	public BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private DeliveryTransactionTypeService deliveryTransTypeService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQMemberInviteLogService bqMemberInviteLogService;
	@Autowired
	private VIEWUsermanagerService viewUsermanagerService;
	@Autowired
	private BQTransactionFeeRateService bqTransactionFeeRateService;
	@Autowired
	private DeliveryTransactionFeeRateService deliveryTransactionFeeRateService;
	@Autowired
	private MemberTradeDetailService memberTradeDetailService;

	@RequestMapping("/quantTradeDetail") 
	public String quantTradeDetail(Model model, String code, String startTimeStr, String endTimeStr, String userName,
			String userId, String tradeType) throws Exception {
		
		List<BQTransaction> transactionList = super.findCurrTrans();
		model.addAttribute("lstTrans", transactionList);
		
		if (transactionList.size() > 0) {
			if (StringUtil.isEmpty(code)) {
				code = transactionList.get(0).getTransaction_code();
			}
		}
		if (StringUtil.isEmpty(code)) {
			return super.errorPage(model, "合约号不能为空");
		}
		
		BQMember user = new BQMember();
		if (!StringUtil.isEmpty(userId) || !StringUtil.isEmpty(userName)) {
			user = this.selectUser(userId, userName);
			if (user == null) {
				return super.errorPage(model, "查不到该用户");
			}
		}
		long uId  = user.getF_user_id();

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (!StringUtil.isEmpty(startTimeStr)) {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (!StringUtil.isEmpty(endTimeStr)) {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		
		int czlx = ConvertUtil.toInt(tradeType, 0);
		PagerModel pm = new PagerModel();
		
		BigDecimal vol = BigDecimal.ZERO; // 数量
		BigDecimal avgPrice = BigDecimal.ZERO; // 均价
		BigDecimal income = BigDecimal.ZERO; // 盈亏额
		if (uId > 0 && !StringUtil.isEmpty(code) && !StringUtil.isEmpty(tradeType)) {
			pm = tradeLogService.findByUserIdAndTransaction(code, uId, czlx, startTime, endTime);
			income = tradeLogService.getIncome(code, 0, 0, uId, czlx);
			
			if (!StringUtil.isEmpty(startTimeStr) || !StringUtil.isEmpty(endTimeStr)) {
				vol = tradeLogService.getVol(code, startTime, endTime, uId, czlx);
				BigDecimal tradeAmount = tradeLogService.getTradeAmount(code, startTime, endTime, uId, czlx);// 交易额
				if (vol.compareTo(BigDecimal.ZERO) > 0 ) {
					avgPrice = tradeAmount.divide(vol, 4, BigDecimal.ROUND_DOWN);
				}
				income = tradeLogService.getIncome(code, startTime, endTime, uId, czlx);
			}
		}
		
		model.addAttribute("income", income.doubleValue());
		model.addAttribute("vol", vol.doubleValue());
		model.addAttribute("avgPrice", avgPrice.doubleValue());
		model.addAttribute("pm", pm);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("code", code);
		model.addAttribute("tradeType", czlx);
		model.addAttribute("startTime", startTimeStr);
		model.addAttribute("endTime", endTimeStr);
		return super.url_JSP("quantTradeDetail");
	}
	
	@RequestMapping(value = "/loadQuantHoldAndPending")
	public String loadQuantHoldAndPending() {
		return super.url_JSP("quantHoldAndPending");
	}
	
	@RequestMapping(value = "/quantHoldAndPending")
	public String quantHoldAndPending(String userId, String userName, Model model) {

		BQMember user = this.selectUser(userId, userName);
		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}
		
		long uId = user.getF_user_id();

		/***************** 基本信息 *****************/
		BQAccount bqAccount = bqAccountService.readAccount(uId, Contents.TRADE_CURRENCY_TYPE_ID);
		BigDecimal account = bqAccount.getBtc();// 币币可用余额
		BigDecimal freez = bqAccount.getBtc_locked();// 提现冻结
		model.addAttribute("account", account);
		model.addAttribute("freez", freez);
		BigDecimal total = account.add(freez);// 账户总资产
		
		/************************* 永续合约  ***********************/
		BigDecimal sumAvailable = new BigDecimal(0); // 合约可用（永续）
		BigDecimal deposit = bqMemberTransactionService.readDepositByUid(uId); // 持仓保证金（永续）
		BigDecimal sumFreeze = BigDecimal.ZERO; // 挂单冻结（永续）
		BigDecimal sumContract = BigDecimal.ZERO; // 合约总计（永续）= 合约可用 + 挂单冻结 + 持仓保证金

		/***************** 资产详情 *****************/
		
		List<BQTransaction> transactionList = super.findCurrTrans();

		for (BQTransaction bqTransaction : transactionList) {
			String code = bqTransaction.getTransaction_code();
			
			// 合约资金合计
			BQAccountContract bqag = bqAccountContractService.readBTCByUidCode(uId, code);

			if (bqag != null) {
				sumAvailable = sumAvailable.add(bqag.getF_btc());
				sumFreeze = sumFreeze.add(bqag.getF_lockbtc());
			}
			
		}
		sumContract = sumAvailable.add(sumFreeze).add(deposit); // 合约总计（永续）= 合约可用 + 挂单冻结 + 持仓保证金
		model.addAttribute("sumAvailable", sumAvailable); // 合约可用（永续）
		model.addAttribute("deposit", deposit); // 持仓保证金（永续）
		model.addAttribute("sumFreeze", sumFreeze); // 挂单冻结（永续）
		model.addAttribute("sumContract", sumContract); // 合约总计（永续）
		total = total.add(sumContract); // 账户总资产

		/***************** 持仓情况 *****************/

		BQMemberLevel userlevel = super.getLevelByUserBfx(user);

		List<BQTransVo> listTransVos = new ArrayList<BQTransVo>();

		for (BQTransaction trans : transactionList) {
			List<BQMemberTransaction> buyMemberTransaction = bqMemberTransactionService.readBQMemberTrasaction(uId, trans.getTransaction_code(),
					Contents.DIRECTION_BUY);
			List<BQMemberTransaction> sellMemberTransaction = bqMemberTransactionService.readBQMemberTrasaction(uId, trans.getTransaction_code(),
					Contents.DIRECTION_SELL);

			// 获取标记价格
			BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + trans.getTransaction_code());
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				markPrice = super.findBPIByCode(trans.getTransaction_type_code());
			}
			
			BQTransactionType tranType = transTypeService.readTransactionType(trans.getTransaction_type());
			
			int digits = Utils.getDigitsFromMinFloat(tranType.getMin_float_price());
			List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.BUYTOP5LIST);
			if (buytop5List == null) {
				buytop5List = pendingDelegationService.getBuyTop5(trans.getTransaction_code());
			}
			List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.SELLTOP5LIST);
			if (selltop5List == null) {
				selltop5List = pendingDelegationService.getSellTop5(trans.getTransaction_code());
			}
			
			BigDecimal buy1Price = BigDecimal.ZERO;
			BigDecimal sell1Price = BigDecimal.ZERO;
			if (buytop5List != null) {
				for (Top5DelegationVO v : buytop5List) {
					v.setPrice(v.getPrice().setScale(digits, RoundingMode.DOWN));
					v.setCountNum(v.getCountNum().setScale(4, RoundingMode.DOWN));
				}
				
				if (buytop5List.size() > 0)
					buy1Price = buytop5List.get(0).getPrice();
			}
			
			if (selltop5List != null) {
				for (Top5DelegationVO v : selltop5List) {
					v.setPrice(v.getPrice().setScale(digits, RoundingMode.DOWN));
					v.setCountNum(v.getCountNum().setScale(4, RoundingMode.DOWN));
				}
				
				if (selltop5List.size() > 0) {
					sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
				}
			}
			
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(trans.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}
			
			List<BQTransVo> listTransBuyVos = new ArrayList<BQTransVo>();
			if (buyMemberTransaction != null && buyMemberTransaction.size() > 0) {
				for (BQMemberTransaction bqMemberTransaction : buyMemberTransaction) {
					int buyStopLoss = bqStopLossService.readHasStopLoss(bqMemberTransaction.getMt_id());
					BQTransVo transVo = bqMemberTransactionService.readHandleData(bqMemberTransaction, 1, markPrice, feeRate, 
							buyStopLoss, digits, buy1Price, sell1Price, trans.getTrade_coefficient());
					if (transVo != null) {
						listTransBuyVos.add(transVo);
					}
				}
			}

			listTransVos.addAll(listTransBuyVos);

			List<BQTransVo> listTranssellVos = new ArrayList<BQTransVo>();
			if (sellMemberTransaction != null) {
				for (BQMemberTransaction bqMemberTransaction : sellMemberTransaction) {
					int sellStopLoss = bqStopLossService.readHasStopLoss(bqMemberTransaction.getMt_id());
					BQTransVo transVo = bqMemberTransactionService.readHandleData(bqMemberTransaction, 2, markPrice, feeRate,
							sellStopLoss, digits, buy1Price, sell1Price, trans.getTrade_coefficient());
					if (transVo != null) {
						listTranssellVos.add(transVo);
					}
				}
			}

			listTransVos.addAll(listTranssellVos);

		}
		model.addAttribute("listTransVos", listTransVos);

		/***************** 挂单情况 *****************/

		List<PendingDelegationVO> voList = new ArrayList<PendingDelegationVO>();
		
		List<PendingDelegation> pdList = pendingDelegationService.getAllByUserId(transactionList, "", "", uId, "");
		for (PendingDelegation pd : pdList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(pd.getDelegation_id());
			data.setOrderId(pd.getPending_no() + "");
			data.setSymbol(pd.getTransaction_code());
			data.setAmount(pd.getDelegation_count().doubleValue());
			data.setDealAmount(pd.getDeal_count().doubleValue());
			data.setUndealAmount(pd.getUndeal_count().doubleValue());
			data.setPrice(pd.getDelegation_price().doubleValue());
			data.setCreateTime(pd.getDelegation_time());
			data.setMultiple(pd.getMultiple());
			data.setPriority(pd.getPriority());
			data.setDirection(pd.getDirection());
			data.setDeposit(pd.getDeposit().doubleValue());
			data.setState(Contents.PENDING_STATE_SUBMITTED);
			
			voList.add(data);
		}
		
		List<PendingDelegationCache> pdCacheList = new ArrayList<PendingDelegationCache>();
		for (BQTransaction trans : transactionList) {
			List<PendingDelegationCache> pdCaches = pendingDelegationCacheService.getAllByUserId(0, trans.getTransaction_code(), uId);
			pdCacheList.addAll(pdCaches);
		}
		for (PendingDelegationCache cache : pdCacheList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(cache.getDelegation_id());
			data.setOrderId(cache.getPending_no() + "");
			data.setSymbol(cache.getTransaction_code());
			data.setAmount(cache.getDelegation_count().doubleValue());
			data.setDealAmount(cache.getDeal_count().doubleValue());
			data.setUndealAmount(cache.getUndeal_count().doubleValue());
			data.setPrice(cache.getDelegation_price().doubleValue());
			data.setCreateTime(cache.getDelegation_time());
			data.setMultiple(cache.getMultiple());
			data.setPriority(cache.getPriority());
			data.setDirection(cache.getDirection());
			data.setDeposit(cache.getDeposit().doubleValue());
			data.setState(Contents.PENDING_STATE_SUBMITTING);
			
			voList.add(data);
		}
		model.addAttribute("pdList", voList);
		
		/************************* 交割合约  ***********************/
		
		BigDecimal deliveryAvailable = new BigDecimal(0); // 合约可用交割）
		BigDecimal deliveryDeposit = deliveryMemberTransactionService.readDepositByUid(uId); // 持仓保证金（交割）
		BigDecimal deliveryFreeze = BigDecimal.ZERO; // 挂单冻结（交割）
		BigDecimal deliveryContract = BigDecimal.ZERO; // 合约总计（交割）= 合约可用 + 挂单冻结 + 持仓保证金

		/***************** 资产详情 *****************/
		List<DeliveryTransaction> deliverylist = super.findCurrDTrans();
		
		for (DeliveryTransaction bqTransaction : deliverylist) {
			String code = bqTransaction.getTransaction_code();
			// 合约资金合计
			DeliveryAccountContract bqag = deliveryAccountContractService.readBTCByUidCode(uId, code);

			if (bqag != null) {
				deliveryAvailable = deliveryAvailable.add(bqag.getF_btc());
				deliveryFreeze = deliveryFreeze.add(bqag.getF_lockbtc());
			}
		}
		
		deliveryContract = deliveryAvailable.add(deliveryFreeze).add(deliveryDeposit); // 合约总计（交割）=合约可用 + 挂单冻结 + 持仓保证金
		model.addAttribute("deliveryAvailable", deliveryAvailable); // 合约可用（交割）
		model.addAttribute("deliveryFreeze", deliveryFreeze); // 挂单冻结（交割）
		model.addAttribute("deliveryDeposit", deliveryDeposit); // 持仓保证金（交割）
		model.addAttribute("deliveryContract", deliveryContract); // 合约总计（交割）
		total = total.add(deliveryContract); // 账户总资产
		model.addAttribute("total", total);

		/***************** 持仓情况 *****************/

		List<BQTransVo> deliverylistTransVos = new ArrayList<BQTransVo>();

		for (DeliveryTransaction trans : deliverylist) {
			List<DeliveryMemberTransaction> buyMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(uId, trans.getTransaction_code(),
					Contents.DIRECTION_BUY);
			List<DeliveryMemberTransaction> sellMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(uId, trans.getTransaction_code(),
					Contents.DIRECTION_SELL);

			// 获取标记价格
			BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + trans.getTransaction_code());
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				markPrice = super.findBPIByCode(trans.getTransaction_type_code());
			}
			
			DeliveryTransactionType tranType = deliveryTransTypeService.readTransactionType(trans.getTransaction_type());
			
			int digits = Utils.getDigitsFromMinFloat(tranType.getMin_float_price());
			List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.D_BUYTOP5LIST);
			if (buytop5List == null) {
				buytop5List = deliveryPendingDelegationService.getBuyTop5(trans.getTransaction_code());
			}
			List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.D_SELLTOP5LIST);
			if (selltop5List == null) {
				selltop5List = deliveryPendingDelegationService.getSellTop5(trans.getTransaction_code());
			}
			
			BigDecimal buy1Price = BigDecimal.ZERO;
			BigDecimal sell1Price = BigDecimal.ZERO;
			if (buytop5List != null) {
				for (Top5DelegationVO v : buytop5List) {
					v.setPrice(v.getPrice().setScale(digits, RoundingMode.DOWN));
					v.setCountNum(v.getCountNum().setScale(4, RoundingMode.DOWN));
				}
				
				if (buytop5List.size() > 0)
					buy1Price = buytop5List.get(0).getPrice();
			}
			
			if (selltop5List != null) {
				for (Top5DelegationVO v : selltop5List) {
					v.setPrice(v.getPrice().setScale(digits, RoundingMode.DOWN));
					v.setCountNum(v.getCountNum().setScale(4, RoundingMode.DOWN));
				}
				
				if (selltop5List.size() > 0) {
					sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
				}
			}
			
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			DeliveryTransactionFeeRate transactionFeeRate = deliveryTransactionFeeRateService.readByCodeAndLevel(trans.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}
			
			List<BQTransVo> listTransBuyVos = new ArrayList<BQTransVo>();
			if (buyMemberTransaction != null && buyMemberTransaction.size() > 0) {
				for (DeliveryMemberTransaction bqMemberTransaction : buyMemberTransaction) {
					int buyStopLoss = deliveryStopLossService.readHasStopLoss(bqMemberTransaction.getMt_id());
					BQTransVo transVo = deliveryMemberTransactionService.readHandleData(bqMemberTransaction, 1, markPrice, feeRate, 
							buyStopLoss, digits, buy1Price, sell1Price, trans.getTrade_coefficient());
					if (transVo != null) {
						listTransBuyVos.add(transVo);
					}
				}
			}

			deliverylistTransVos.addAll(listTransBuyVos);

			List<BQTransVo> listTranssellVos = new ArrayList<BQTransVo>();
			if (sellMemberTransaction != null) {
				for (DeliveryMemberTransaction bqMemberTransaction : sellMemberTransaction) {
					int sellStopLoss = deliveryStopLossService.readHasStopLoss(bqMemberTransaction.getMt_id());
					BQTransVo transVo = deliveryMemberTransactionService.readHandleData(bqMemberTransaction, 2, markPrice, feeRate,
							sellStopLoss, digits, buy1Price, sell1Price, trans.getTrade_coefficient());
					if (transVo != null) {
						listTranssellVos.add(transVo);
					}
				}
			}

			deliverylistTransVos.addAll(listTranssellVos);

		}
		model.addAttribute("deliverylistTransVos", deliverylistTransVos);

		/***************** 挂单情况 *****************/

		List<PendingDelegationVO> deliveryvoList = new ArrayList<PendingDelegationVO>();
		
		List<DeliveryPendingDelegation> deliverypdList = deliveryPendingDelegationService.getAllByUserId(deliverylist, "", "", uId, "");
		for (DeliveryPendingDelegation pd : deliverypdList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(pd.getDelegation_id());
			data.setOrderId(pd.getPending_no() + "");
			data.setSymbol(pd.getTransaction_code());
			data.setAmount(pd.getDelegation_count().doubleValue());
			data.setDealAmount(pd.getDeal_count().doubleValue());
			data.setUndealAmount(pd.getUndeal_count().doubleValue());
			data.setPrice(pd.getDelegation_price().doubleValue());
			data.setCreateTime(pd.getDelegation_time());
			data.setMultiple(pd.getMultiple());
			data.setPriority(pd.getPriority());
			data.setDirection(pd.getDirection());
			data.setDeposit(pd.getDeposit().doubleValue());
			data.setState(Contents.PENDING_STATE_SUBMITTED);
			
			deliveryvoList.add(data);
		}
		
		List<DeliveryPendingDelegationCache> deliverypdCacheList = new ArrayList<DeliveryPendingDelegationCache>();
		for (DeliveryTransaction trans : deliverylist) {
			List<DeliveryPendingDelegationCache> pdCaches = deliveryPendingDelegationCacheService.getAllByUserId(0, trans.getTransaction_code(), uId);
			deliverypdCacheList.addAll(pdCaches);
		}
		for (DeliveryPendingDelegationCache cache : deliverypdCacheList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(cache.getDelegation_id());
			data.setOrderId(cache.getPending_no() + "");
			data.setSymbol(cache.getTransaction_code());
			data.setAmount(cache.getDelegation_count().doubleValue());
			data.setDealAmount(cache.getDeal_count().doubleValue());
			data.setUndealAmount(cache.getUndeal_count().doubleValue());
			data.setPrice(cache.getDelegation_price().doubleValue());
			data.setCreateTime(cache.getDelegation_time());
			data.setMultiple(cache.getMultiple());
			data.setPriority(cache.getPriority());
			data.setDirection(cache.getDirection());
			data.setDeposit(cache.getDeposit().doubleValue());
			data.setState(Contents.PENDING_STATE_SUBMITTING);
			
			voList.add(data);
		}
		model.addAttribute("deliverypdList", deliveryvoList);

		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		return super.url_JSP("quantHoldAndPending");
	}
	
	@RequestMapping("/quantRechargeList")
	public String quantRechargeList(Model mo, String uid, String uemail, String curr, String currencyId) {
		BQMember user = new BQMember();
		if (!StringUtil.isEmpty(uid) || !StringUtil.isEmpty(uemail)) {
			user = this.selectUser(uid, uemail);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
		}
		long userId  = user.getF_user_id();
		
		List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.readAll();
		mo.addAttribute("currencyTypes", currencyTypes);
		int type = ConvertUtil.toInt(curr, 0);
		
		int coinId = ConvertUtil.toInt(currencyId, Contents.TRADE_CURRENCY_TYPE_ID);
		
		mo.addAttribute("pm", bqMemberRechargeService.readCzByUserID(userId, uemail, "", type, Contents.RECHARGE_FLAG_NORMAL, coinId));
		mo.addAttribute("allRecharge", bqMemberRechargeService.sumRecord(userId, 1, 1, coinId).doubleValue()); // 总充值
		mo.addAttribute("allWithDraw", bqMemberRechargeService.sumRecord(userId, 2, 1, coinId).doubleValue()); // 总提现
		
		mo.addAttribute("currencyId", coinId);
		mo.addAttribute("uid", uid);
		mo.addAttribute("uemail", uemail);
		mo.addAttribute("currpage", type);

		return super.url_JSP("quantRechargeList");

	}
	
	@RequestMapping("/quantIncomeRanking")
	public String quantIncomeRanking(Model model, String code, String orderBy) {
		
		try {
			List<BQTransaction> transactionList = super.findCurrTrans();
			model.addAttribute("lstTrans", transactionList);
			if (transactionList.size() > 0) {
				if (StringUtil.isEmpty(code)) {
					code = transactionList.get(0).getTransaction_code();
				}
			}
			
			Long lastTime = spyMemcachedClient.get("quant_income_stats_last_time"+ code);
			Long currentTime = tradeLogService.getTime();
			List<TradeLog> lastTradeLogs = null;
			if (lastTime == null) {
				lastTime = 0l;
			} else {
				lastTradeLogs = spyMemcachedClient.get("quant_income_stats_last"+ code);
				if (lastTradeLogs == null) {
					lastTime = 0l;
				}
			}
			
			List<TradeLog> allTradeLogs = new ArrayList<TradeLog>();
			if (lastTradeLogs != null) {
				allTradeLogs = lastTradeLogs;
			}
			
			List<Object[]> allTrades = tradeLogService.findIncome(code, lastTime, currentTime, 0);
			getAllTradeLogs(allTradeLogs, allTrades, code);
			
			spyMemcachedClient.set("quant_income_stats_last_time"+ code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, currentTime);
			spyMemcachedClient.set("quant_income_stats_last"+ code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, allTradeLogs);
			
			List<RebateRankingVo> voList = incomeOrderBy(allTradeLogs, orderBy);
			PagerModel pm = new PagerModel();
			pm.setTotal(voList.size());
			
			List<RebateRankingVo> datas = new ArrayList<RebateRankingVo>();
			int begin = SystemContext.getOffset();
			int size = SystemContext.getPageSize();
			
			for (int i = begin; i < begin + size; i++) {
				if (voList.size() <= i) {
					break;
				}
				datas.add(voList.get(i));
			}
			
			pm.setDatas(datas);
			
			model.addAttribute("orderBy", StringUtil.isEmpty(orderBy) ? "DES" : orderBy);
			model.addAttribute("pm", pm);
			model.addAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return super.url_JSP("quantIncomeRanking");
	}

	@RequestMapping("/quantFundRanking")
	public String quantFundRanking(String userId, String userName, Model model, String orderBy) {
		
		try {
			BQMember user = new BQMember();
			if (!StringUtil.isEmpty(userId) || !StringUtil.isEmpty(userName)) {
				user = this.selectUser(userId, userName);
				if (user == null) {
					return super.errorPage(model, "查不到该用户");
				}
			}
			
			List<ContractBalanceVo> contractBalances = null;
			ContractBalanceVo allBalance = new ContractBalanceVo();
			
			if (user.getF_user_id() > 0) {
				ContractBalanceVo balance = new ContractBalanceVo();
				contractBalances = new ArrayList<ContractBalanceVo>();
				contractBalances.add(balance);
				
				balance.setUserId(user.getF_user_id());
				BQAccount accountUser = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
				if (accountUser != null) {
					balance.setUserName(accountUser.getF_user_email());
					balance.setAccountBalance(accountUser.getBtc().doubleValue());
					balance.setAccountFrozen(accountUser.getBtc_locked().doubleValue());
					balance.setAccountAll(balance.getAccountBalance() + balance.getAccountFrozen());
					balance.setAll(balance.getAccountAll());
				}
				
				// 永续合约账户资金大于0
				List<Object[]> contractAccounts = bqAccountContractService.readBalanceAndFrozen(user.getF_user_id());
				for (int i = 0; i < contractAccounts.size(); i++) {
					Object[] contractAccount = contractAccounts.get(i);
					balance.setContractBalance(((BigDecimal) contractAccount[1]).doubleValue());
					balance.setContractFrozen(((BigDecimal) contractAccount[2]).doubleValue());
					balance.setContractAll(balance.getContractBalance() + balance.getContractFrozen());
					balance.setAll(balance.getContractAll() + balance.getAll());
				}
				
				// 交割合约账户资金大于0
				List<Object[]> deliveryContractAccounts = deliveryAccountContractService.readBalanceAndFrozen(user.getF_user_id());
				for (int i = 0; i < deliveryContractAccounts.size(); i++) {
					Object[] contractAccount = deliveryContractAccounts.get(i);
					balance.setDeliveryContractBalance(((BigDecimal) contractAccount[1]).doubleValue());
					balance.setDeliveryContractFrozen(((BigDecimal) contractAccount[2]).doubleValue());
					balance.setDeliveryContractAll(balance.getDeliveryContractBalance() + balance.getDeliveryContractFrozen());
					balance.setAll(balance.getDeliveryContractAll() + balance.getAll());
				}
				
				// 永续合约持仓资金大于0
//				List<Object[]> transAccounts = bqMemberTransactionService.readSumHoldsOrderByUser(user.getF_user_id());
//				for (int i = 0; i < transAccounts.size(); i++) {
//					Object[] contractAccount = transAccounts.get(i);
//					BigDecimal bal = (BigDecimal) contractAccount[1];
//					balance.setContractDeposit(bal.doubleValue());
//					balance.setContractAll(bal.doubleValue() + balance.getContractAll());
//					balance.setAll(bal.doubleValue() + balance.getAll());
//				}
				BigDecimal floatGainLoss = BigDecimal.ZERO;
				BigDecimal depositAndRemain = BigDecimal.ZERO;	
				List<Object[]> memberTrans = bqMemberTransactionService.readDepositAndFloatingPL(user.getF_user_id());
				for (int i = 0; i < memberTrans.size(); i++) {
					Object[] contractAccount = memberTrans.get(i);
					String code = (String) contractAccount[0];
					Character type = (Character) contractAccount[2];
					int buyOrSellTradeTypeId = Integer.parseInt(String.valueOf(type));
					BigDecimal avgPrice = (BigDecimal) contractAccount[3];
					BigDecimal hold = (BigDecimal) contractAccount[4];
					BigDecimal lock = (BigDecimal) contractAccount[5];
					depositAndRemain = depositAndRemain.add((BigDecimal) contractAccount[6]);
					
					BQTransaction bqTransaction = super.findCurrTransByCode(code);
					if (bqTransaction == null) {
						break;
					}
					BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + code);
					if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
						markPrice = super.findBPIByCode(code);
					}
					if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
						BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
						ykdiff = markPrice.subtract(avgPrice).multiply(bqTransaction.getTrade_coefficient()).multiply(lock.add(hold));
						floatGainLoss = floatGainLoss.add(ykdiff);
					} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
						BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
						ykdiff = avgPrice.subtract(markPrice).multiply(bqTransaction.getTrade_coefficient())
								.multiply(lock.add(hold));
						floatGainLoss = floatGainLoss.add(ykdiff);
					}
				}
				balance.setContractDeposit(depositAndRemain.doubleValue());
				balance.setContractFloatGainLoss(floatGainLoss.doubleValue());
				balance.setContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + balance.getContractAll());
				balance.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + balance.getAll());
				
				// 交割合约持仓资金大于0
//				List<Object[]> deliveryMemberTrans = dMemberTransactionService.readSumHoldsOrderByUser(user.getF_user_id());
//				for (int i = 0; i < deliveryMemberTrans.size(); i++) {
//					Object[] contractAccount = deliveryMemberTrans.get(i);
//					BigDecimal bal = (BigDecimal) contractAccount[1];
//					balance.setDeliveryContractDeposit(bal.doubleValue());
//					balance.setDeliveryContractAll(bal.doubleValue() + balance.getDeliveryContractAll());
//					balance.setAll(bal.doubleValue() + balance.getAll());
//				}
				BigDecimal deliveryFloatingPL = BigDecimal.ZERO;
				BigDecimal deliveryDepositAndRemain = BigDecimal.ZERO;	
				List<Object[]> deliveryMemberTrans = deliveryMemberTransactionService.readDepositAndFloatingPL(user.getF_user_id());
				for (int i = 0; i < deliveryMemberTrans.size(); i++) {
					Object[] contractAccount = deliveryMemberTrans.get(i);
					String code = (String) contractAccount[0];
					Character type = (Character) contractAccount[2];
					int buyOrSellTradeTypeId = Integer.parseInt(String.valueOf(type));
					BigDecimal avgPrice = (BigDecimal) contractAccount[3];
					BigDecimal hold = (BigDecimal) contractAccount[4];
					BigDecimal lock = (BigDecimal) contractAccount[5];
					deliveryDepositAndRemain = deliveryDepositAndRemain.add((BigDecimal) contractAccount[6]);
					
					DeliveryTransaction bqTransaction = super.findDTransByCode(code);
					if (bqTransaction == null) {
						break;
					}
					BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + code);
					if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
						markPrice = super.findBPIByCode(code);
					}
					if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
						BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
						ykdiff = markPrice.subtract(avgPrice).multiply(bqTransaction.getTrade_coefficient()).multiply(lock.add(hold));
						deliveryFloatingPL = deliveryFloatingPL.add(ykdiff);
					} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
						BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
						ykdiff = avgPrice.subtract(markPrice).multiply(bqTransaction.getTrade_coefficient())
								.multiply(lock.add(hold));
						deliveryFloatingPL = deliveryFloatingPL.add(ykdiff);
					}
				}
				balance.setDeliveryContractDeposit(deliveryDepositAndRemain.doubleValue());
				balance.setDeliveryContractFloatGainLoss(deliveryFloatingPL.doubleValue());
				balance.setContractAll(deliveryDepositAndRemain.doubleValue() + deliveryFloatingPL.doubleValue() + balance.getContractAll());
				balance.setAll(deliveryDepositAndRemain.doubleValue() + deliveryFloatingPL.doubleValue() + balance.getAll());
			} else {
				contractBalances = spyMemcachedClient.get("contractAccountStatistics");
				allBalance = spyMemcachedClient.get("contractAccountStatistics_all");
				if (contractBalances == null) {
					contractBalances = new ArrayList<ContractBalanceVo>();
					allBalance = new ContractBalanceVo();
					
					// 总账户资金大于0
					List<Object[]> accountUsers = bqAccountService.readBalanceAndFrozen(Contents.TRADE_CURRENCY_TYPE_ID);
					for (Object[] account : accountUsers) {
						ContractBalanceVo balance = new ContractBalanceVo();
						balance.setUserId((Integer) account[0]);
						balance.setUserName((String) account[1]);
						balance.setAccountBalance(((BigDecimal) account[2]).doubleValue());
						balance.setAccountFrozen(((BigDecimal) account[3]).doubleValue());
						balance.setAccountAll(balance.getAccountBalance() + balance.getAccountFrozen());
						balance.setAll(balance.getAccountAll());
						allBalance.setAccountAll(allBalance.getAccountAll() + balance.getAccountAll());
						contractBalances.add(balance);
					}
					
					// 永续合约账户资金大于0
					List<Object[]> contractAccounts = bqAccountContractService.readBalanceAndFrozen(0);
					for (int i = 0; i < contractAccounts.size(); i++) {
						Object[] contractAccount = contractAccounts.get(i);
						int uid = (Integer) contractAccount[0];
						boolean have = false;
						BigDecimal balance = (BigDecimal) contractAccount[1];
						BigDecimal frozen = (BigDecimal) contractAccount[2];
						
						for (int j = 0; j < contractBalances.size(); j++) {
							ContractBalanceVo account = contractBalances.get(j);
							long id = account.getUserId();
							if (id == uid) {
								account.setContractBalance(balance.doubleValue());
								account.setContractFrozen(frozen.doubleValue());
								account.setContractAll(account.getContractBalance() + account.getContractFrozen());
								account.setAll(account.getContractAll() + account.getAll());
								have = true;
								break;
							}
						}
						
						if (!have) {
							ContractBalanceVo balanceVo = new ContractBalanceVo();
							balanceVo.setUserId(uid);
							balanceVo.setContractBalance(balance.doubleValue());
							balanceVo.setContractFrozen(frozen.doubleValue());
							balanceVo.setContractAll(balanceVo.getContractBalance() + balanceVo.getContractFrozen());
							balanceVo.setAll(balanceVo.getContractAll());
							contractBalances.add(balanceVo);
						}
					}
					
					// 交割合约账户资金大于0
					List<Object[]> deliveryContractAccounts = deliveryAccountContractService.readBalanceAndFrozen(0);
					for (int i = 0; i < deliveryContractAccounts.size(); i++) {
						Object[] contractAccount = deliveryContractAccounts.get(i);
						int uid = (Integer) contractAccount[0];
						boolean have = false;
						BigDecimal balance = (BigDecimal) contractAccount[1];
						BigDecimal frozen = (BigDecimal) contractAccount[2];
						
						for (int j = 0; j < contractBalances.size(); j++) {
							ContractBalanceVo account = contractBalances.get(j);
							long id = account.getUserId();
							if (id == uid) {
								account.setDeliveryContractBalance(balance.doubleValue());
								account.setDeliveryContractFrozen(frozen.doubleValue());
								account.setDeliveryContractAll(account.getDeliveryContractBalance() + account.getDeliveryContractFrozen());
								account.setAll(account.getDeliveryContractAll() + account.getAll());
								have = true;
								break;
							}
						}
						
						if (!have) {
							ContractBalanceVo balanceVo = new ContractBalanceVo();
							balanceVo.setUserId(uid);
							balanceVo.setDeliveryContractBalance(balance.doubleValue());
							balanceVo.setDeliveryContractFrozen(frozen.doubleValue());
							balanceVo.setDeliveryContractAll(balanceVo.getDeliveryContractBalance() + balanceVo.getDeliveryContractFrozen());
							balanceVo.setAll(balanceVo.getDeliveryContractAll());
							contractBalances.add(balanceVo);
						}
					}
					
					// 永续合约持仓资金大于0
//					List<Object[]> transAccounts = bqMemberTransactionService.readSumHoldsOrderByUser(0);
//					for (int i = 0; i < transAccounts.size(); i++) {
//						Object[] contractAccount = transAccounts.get(i);
//						int uid = (Integer) contractAccount[0];
//						boolean have = false;
//						BigDecimal balance = (BigDecimal) contractAccount[1];
//						
//						for (int j = 0; j < contractBalances.size(); j++) {
//							ContractBalanceVo account = contractBalances.get(j);
//							long id = account.getUserId();
//							if (id == uid) {
//								account.setContractDeposit(balance.doubleValue());
//								account.setContractAll(account.getContractAll() + account.getContractDeposit());
//								account.setAll(balance.doubleValue() + account.getAll());
//								have = true;
//								break;
//							}
//						}
//						
//						if (!have) {
//							ContractBalanceVo balanceVo = new ContractBalanceVo();
//							balanceVo.setUserId(uid);
//							balanceVo.setContractDeposit(balance.doubleValue());
//							balanceVo.setContractAll(balance.doubleValue());
//							balanceVo.setAll(balance.doubleValue());
//							contractBalances.add(balanceVo);
//						}
//						
//					}
					List<Object[]> memberTrans = bqMemberTransactionService.readDepositAndFloatingPL(0);
					for (int i = 0; i < memberTrans.size(); i++) {
						BigDecimal floatGainLoss = BigDecimal.ZERO;
						BigDecimal depositAndRemain = BigDecimal.ZERO;
						
						Object[] contractAccount = memberTrans.get(i);
						String code = (String) contractAccount[0];
						Character type = (Character) contractAccount[2];
						int buyOrSellTradeTypeId = Integer.parseInt(String.valueOf(type));
						BigDecimal avgPrice = (BigDecimal) contractAccount[3];
						BigDecimal hold = (BigDecimal) contractAccount[4];
						BigDecimal lock = (BigDecimal) contractAccount[5];
						depositAndRemain = depositAndRemain.add((BigDecimal) contractAccount[6]);
						
						BQTransaction bqTransaction = super.findCurrTransByCode(code);
						if (bqTransaction == null) {
							continue;
						}
						BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + code);
						if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
							markPrice = super.findBPIByCode(code);
						}
						if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = markPrice.subtract(avgPrice).multiply(bqTransaction.getTrade_coefficient()).multiply(lock.add(hold));
							floatGainLoss = floatGainLoss.add(ykdiff);
						} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = avgPrice.subtract(markPrice).multiply(bqTransaction.getTrade_coefficient())
									.multiply(lock.add(hold));
							floatGainLoss = floatGainLoss.add(ykdiff);
						}
						
						int uid = (Integer) contractAccount[1];
						boolean have = false;
						
						for (int j = 0; j < contractBalances.size(); j++) {
							ContractBalanceVo account = contractBalances.get(j);
							long id = account.getUserId();
							if (id == uid) {
								account.setContractDeposit(depositAndRemain.doubleValue() + account.getContractDeposit());
								account.setContractFloatGainLoss(floatGainLoss.doubleValue() + account.getContractFloatGainLoss());
								account.setContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + account.getContractAll());
								account.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + account.getAll());
								have = true;
								break;
							}
						}
						
						if (!have) {
							ContractBalanceVo balanceVo = new ContractBalanceVo();
							balanceVo.setUserId(uid);
							balanceVo.setContractDeposit(depositAndRemain.doubleValue());
							balanceVo.setContractFloatGainLoss(floatGainLoss.doubleValue());
							balanceVo.setContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue());
							balanceVo.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue());
							contractBalances.add(balanceVo);
						}
					}
					
					// 交割合约持仓资金大于0
//					List<Object[]> deliveryMemberTrans = dMemberTransactionService.readSumHoldsOrderByUser(0);
//					for (int i = 0; i < deliveryMemberTrans.size(); i++) {
//						Object[] contractAccount = deliveryMemberTrans.get(i);
//						int uid = (Integer) contractAccount[0];
//						boolean have = false;
//						BigDecimal balance = (BigDecimal) contractAccount[1];
//						
//						for (int j = 0; j < contractBalances.size(); j++) {
//							ContractBalanceVo account = contractBalances.get(j);
//							long id = account.getUserId();
//							if (id == uid) {
//								account.setDeliveryContractDeposit(balance.doubleValue());
//								account.setDeliveryContractAll(account.getDeliveryContractAll() + account.getDeliveryContractDeposit());
//								account.setAll(balance.doubleValue() + account.getAll());
//								have = true;
//								break;
//							}
//						}
//						
//						if (!have) {
//							ContractBalanceVo balanceVo = new ContractBalanceVo();
//							balanceVo.setUserId(uid);
//							balanceVo.setDeliveryContractDeposit(balance.doubleValue());
//							balanceVo.setDeliveryContractAll(balance.doubleValue());
//							balanceVo.setAll(balance.doubleValue());
//							contractBalances.add(balanceVo);
//						}
//						
//					}
					List<Object[]> deliveryMemberTrans = bqMemberTransactionService.readDepositAndFloatingPL(0);
					for (int i = 0; i < deliveryMemberTrans.size(); i++) {
						BigDecimal floatGainLoss = BigDecimal.ZERO;
						BigDecimal depositAndRemain = BigDecimal.ZERO;
						
						Object[] contractAccount = deliveryMemberTrans.get(i);
						String code = (String) contractAccount[0];
						Character type = (Character) contractAccount[2];
						int buyOrSellTradeTypeId = Integer.parseInt(String.valueOf(type));
						BigDecimal avgPrice = (BigDecimal) contractAccount[3];
						BigDecimal hold = (BigDecimal) contractAccount[4];
						BigDecimal lock = (BigDecimal) contractAccount[5];
						depositAndRemain = depositAndRemain.add((BigDecimal) contractAccount[6]);
						
						DeliveryTransaction bqTransaction = super.findDTransByCode(code);
						if (bqTransaction == null) {
							continue;
						}
						BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + code);
						if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
							markPrice = super.findBPIByCode(code);
						}
						if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = markPrice.subtract(avgPrice).multiply(bqTransaction.getTrade_coefficient()).multiply(lock.add(hold));
							floatGainLoss = floatGainLoss.add(ykdiff);
						} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = avgPrice.subtract(markPrice).multiply(bqTransaction.getTrade_coefficient())
									.multiply(lock.add(hold));
							floatGainLoss = floatGainLoss.add(ykdiff);
						}
						
						int uid = (Integer) contractAccount[1];
						boolean have = false;
						
						for (int j = 0; j < contractBalances.size(); j++) {
							ContractBalanceVo account = contractBalances.get(j);
							long id = account.getUserId();
							if (id == uid) {
								account.setDeliveryContractDeposit(depositAndRemain.doubleValue() + account.getDeliveryContractDeposit());
								account.setDeliveryContractFloatGainLoss(floatGainLoss.doubleValue() + account.getDeliveryContractFloatGainLoss());
								account.setDeliveryContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + account.getContractAll());
								account.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + account.getAll());
								have = true;
								break;
							}
						}
						
						if (!have) {
							ContractBalanceVo balanceVo = new ContractBalanceVo();
							balanceVo.setUserId(uid);
							balanceVo.setDeliveryContractDeposit(depositAndRemain.doubleValue());
							balanceVo.setDeliveryContractFloatGainLoss(floatGainLoss.doubleValue());
							balanceVo.setDeliveryContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue());
							balanceVo.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue());
							contractBalances.add(balanceVo);
						}
					}
					
					for (ContractBalanceVo balance : contractBalances) {
						allBalance.setContractAll(balance.getContractAll() + balance.getDeliveryContractAll() + allBalance.getContractAll());
						allBalance.setAll(balance.getAll() + allBalance.getAll());
					}
					
					spyMemcachedClient.set("contractAccountStatistics", BTCQMemecacheKeys.TIMES.HALF_HOUR, contractBalances);
					spyMemcachedClient.set("contractAccountStatistics_all", BTCQMemecacheKeys.TIMES.HALF_HOUR, allBalance);
				}
				
				fundOrderBy(contractBalances, orderBy);
			}
			
			PagerModel pm = new PagerModel();
			pm.setTotal(contractBalances.size());
			
			List<ContractBalanceVo> datas = new ArrayList<ContractBalanceVo>();
			int begin = SystemContext.getOffset();
			int size = SystemContext.getPageSize();
			
			for (int i = begin; i < begin + size; i++) {
				if (contractBalances.size() <= i) {
					break;
				}
				datas.add(contractBalances.get(i));
			}
			
			pm.setDatas(datas);
			
			model.addAttribute("orderBy", StringUtil.isEmpty(orderBy) ? "ALL" : orderBy);
			model.addAttribute("pm", pm);
			model.addAttribute("userId", userId);
			model.addAttribute("userName", userName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.url_JSP("quantFundRanking");
	}
	
	@RequestMapping("/quantInvite")
	public String quantInvite(String userId, String userName, Model model) {
		BQMember user = new BQMember();
		if (!StringUtil.isEmpty(userId) || !StringUtil.isEmpty(userName)) {
			user = this.selectUser(userId, userName);
			if (user == null) {
				return super.errorPage(model, "查不到该用户");
			}
		}
		
		long uId = user.getF_user_id();
		
		String level = "";
		Long inviteNums = null;
		Long allInviteNums = null;
		Long inviter = null;
		PagerModel pm = new PagerModel();
		
		if (uId > 0) {
			level = bqMemberLevelService.findByLevelId(user.getF_user_level_id()).getLevel_name();
			inviteNums = spyMemcachedClient.get(BTCQMemecacheKeys.INVITE_COUNT + uId);
			if (inviteNums == null) {
				inviteNums = bqMemberInviteLogService.readCountByUserId(uId);
				spyMemcachedClient.set(BTCQMemecacheKeys.INVITE_COUNT + uId, BTCQMemecacheKeys.TIMES.ONE_HOUR, inviteNums);
			}
			allInviteNums = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_INVITE_COUNT + uId);
			if (allInviteNums == null) {
				allInviteNums = bqMemberInviteLogService.readAllCount(uId);
				spyMemcachedClient.set(BTCQMemecacheKeys.ALL_INVITE_COUNT + uId, BTCQMemecacheKeys.TIMES.ONE_HOUR, allInviteNums);
			}
			inviter = bqMemberInviteLogService.findInviterByUserId(uId);
			
			int offset = SystemContext.getOffset();
			List<UserInviteMemberVo> allInviteVos = null;
			if (offset > 0) {
				allInviteVos = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_INVITE_VOS + uId);
			}
			
			if (allInviteVos == null) {
				allInviteVos = new ArrayList<UserInviteMemberVo>();
				List<Object[]> list = bqMemberInviteLogService.readAllByUserId(uId);
				if (list.size() > 0) {
					for (Object[] object : list) {
						UserInviteMemberVo vo = new UserInviteMemberVo();
						long useId = (Integer) object[0];
						vo.setUserId(object[0].toString());
						vo.setUserName(object[1].toString());
						vo.setLevel(object[2].toString());
						vo.setInviteNums(bqMemberInviteLogService.readCountByUserId(useId));
						vo.setAllInviteNums(bqMemberInviteLogService.readAllCount(useId));
						allInviteVos.add(vo);
					}
				}
				spyMemcachedClient.set(BTCQMemecacheKeys.ALL_INVITE_VOS + uId, BTCQMemecacheKeys.TIMES.ONE_HOUR, allInviteVos);
			}
			
			pm.setTotal(allInviteVos.size());
			
			List<UserInviteMemberVo> datas = new ArrayList<UserInviteMemberVo>();
			int begin = SystemContext.getOffset();
			int size = SystemContext.getPageSize();
			
			for (int i = begin; i < begin + size; i++) {
				if (allInviteVos.size() <= i) {
					break;
				}
				datas.add(allInviteVos.get(i));
			}
			pm.setDatas(datas);
		}
		
		
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("user", user);
		model.addAttribute("level", level);
		model.addAttribute("inviteNums", inviteNums);
		model.addAttribute("allInviteNums", allInviteNums);
		model.addAttribute("inviter", inviter);
		model.addAttribute("pm", pm);
		
		return super.url_JSP("quantInvite");
	}
	
	@RequestMapping(value = "/loadUsers")
	public String loadUsers(HttpServletRequest request, Model mo) {
		String state = request.getParameter("state");
		String userid = request.getParameter("userid");
		String useremail = request.getParameter("useremail");

		int istate = ConvertUtil.toInt(state, 0);
		long uid = ConvertUtil.toLong(userid, 0l);

		PagerModel pm = new PagerModel();

		if (istate == 1) {
			pm = viewUsermanagerService.findModel(uid, "", useremail, "", 0, -1, 1);
		} else if (istate == 2) {
			pm = viewUsermanagerService.findModel(uid, "", useremail, "", 0, -1, 2);
		} else if (istate == 3) {
			pm = viewUsermanagerService.findModel(uid, "", useremail, "", 0, -1, 3);
		} else {
			pm = viewUsermanagerService.findModel();
		}

		mo.addAttribute("pm", pm);
		mo.addAttribute("userid", userid);
		mo.addAttribute("useremail", useremail);
		mo.addAttribute("state", state);
		return super.url_JSP("quantUsersList");
	}

	@RequestMapping(value = "/selectUsers")
	public String selectUsers(HttpServletRequest request, Model mo) {
		String state = request.getParameter("state");
		String userid = request.getParameter("userid");
		String useremail = request.getParameter("useremail");

		int istate = ConvertUtil.toInt(state, 0);
		long uid = ConvertUtil.toLong(userid, 0l);
		
		PagerModel pm = viewUsermanagerService.findModel(uid, "", useremail, "", 0, -1, istate);
		mo.addAttribute("pm", pm);
		mo.addAttribute("userid", userid);
		mo.addAttribute("useremail", useremail);
		mo.addAttribute("state", state);

		return super.url_JSP("quantUsersList");
	}
	
	@RequestMapping("/quantTradeMakerTaker")
	public String quantTradeMakerTaker(Model mo, String code, String startTimeStr, String endTimeStr, String userId,
			String userName, String type) throws ParseException {
		List<BQTransaction> transactionList = super.findCurrTrans();
		mo.addAttribute("lstTrans", transactionList);
		if (transactionList.size() > 0) {
			if (StringUtil.isEmpty(code)) {
				code = transactionList.get(0).getTransaction_code();
			}
		} else {
			PagerModel pm = new PagerModel();
			pm.setDatas(null);
			pm.setTotal(0);
			mo.addAttribute("pm", pm);

			return super.url_JSP("quantTradeMakerTaker");
		}

		long uId = ConvertUtil.toLong(userId, 0l);
		BQMember user = new BQMember();
		if (uId > 0) {
			user = bqMemberService.readUserById(uId);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
		}
		if (!StringUtil.isEmpty(userName)) {
			user = bqMemberService.readUserByEmail(userName);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
			uId = user.getF_user_id();
		}

		int itype = ConvertUtil.toInt(type, 0);

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (StringUtil.isEmpty(startTimeStr)) {
			startTime = (long) TimeUtil.getTimesmorning();
		} else {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (StringUtil.isEmpty(endTimeStr)) {
			endTime = startTime + 86400L;
		} else {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}

		mo.addAttribute("startTime", startTimeStr);
		mo.addAttribute("endTime", endTimeStr);
		mo.addAttribute("userId", userId);
		mo.addAttribute("userName", userName);
		mo.addAttribute("current", code);
		mo.addAttribute("type", itype);
		mo.addAttribute("pm", memberTradeDetailService.findTradeWithQuant(code, startTime, endTime, uId, itype));

		return super.url_JSP("quantTradeMakerTaker");
	}
	
	private List<ContractBalanceVo> fundOrderBy(List<ContractBalanceVo> voList, final String order) {

		Collections.sort(voList, new Comparator<ContractBalanceVo>() {

			@Override
			public int compare(ContractBalanceVo o1, ContractBalanceVo o2) {
				// o2-o1 降序， o1-o2升序
				if ("ACCOUNTASC".equals(order)) {
					double ret = o1.getAccountAll() - o2.getAccountAll();
					return ret > 0 ? 1 : (ret < 0 ?  -1 : 0);
				} else if ("ACCOUNTDES".equals(order)) {
					double ret = o2.getAccountAll() - o1.getAccountAll();
					return ret > 0 ? 1 : (ret < 0 ?  -1 : 0);
				} else if ("CONTRACTASC".equals(order)) {
					double ret = o1.getContractAll() - o2.getContractAll();
					return ret > 0 ? 1 : (ret < 0 ?  -1 : 0);
				} else if ("CONTRACTDES".equals(order)) {
					double ret = o2.getContractAll() - o1.getContractAll();
					return ret > 0 ? 1 : (ret < 0 ?  -1 : 0);
				} else {
					double ret = o2.getAll() - o1.getAll();
					return ret > 0 ? 1 : (ret < 0 ?  -1 : 0);
				}
			}

		});

		return voList;
	}
	
	private void getAllTradeLogs(List<TradeLog> allTradeLogs, List<Object[]> allTrades, String code) {
		List<TradeLog> needAppend = new ArrayList<TradeLog>();
		for (Object[] obj : allTrades) {
			if (obj[0] == null)
				continue;
			boolean have = false;
			long uid = (Integer) obj[0];
			for (TradeLog tradeLog : allTradeLogs) {
				if (uid == tradeLog.getF_user_id()) {
					tradeLog.setTrade_income(((BigDecimal) obj[1]).add(tradeLog.getTrade_income()));
					have = true;
					break;
				}
			}

			if (!have) {
				TradeLog log = new TradeLog();
				log.setF_user_id((Integer) obj[0]);
				log.setTrade_income((BigDecimal) obj[1]);
				needAppend.add(log);
			}
		}

		allTradeLogs.addAll(needAppend);
	}

	private List<RebateRankingVo> incomeOrderBy(List<TradeLog> list, final String order) {
		List<RebateRankingVo> voList = new ArrayList<RebateRankingVo>();
		for (TradeLog tradeLog : list) {
			RebateRankingVo vo = new RebateRankingVo();
			vo.setUserId(tradeLog.getF_user_id());
			vo.setRebate(tradeLog.getTrade_income().doubleValue());
			vo.setUserName("--");
			voList.add(vo);
		}

		Collections.sort(voList, new Comparator<RebateRankingVo>() {

			@Override
			public int compare(RebateRankingVo o1, RebateRankingVo o2) {
				// o2-o1 降序， o1-o2升序
				if ("ASC".equals(order)) {
					return (int) (o1.getRebate() - o2.getRebate());// 收益升序
				} else if ("DES".equals(order)) {
					return (int) (o2.getRebate() - o1.getRebate());// 收益降序
				} else {
					return (int) (o2.getRebate() - o1.getRebate());
				}
			}

		});

		return voList;
	}
	
	private BQMember selectUser(String userId, String userName) {
		long uid = ConvertUtil.toLong(userId, 0l);
		BQMember user = bqMemberService.readUserById(uid);
		if (user == null) {
			user = bqMemberService.readUserByEmail(userName);
		}
		return user;
	}
}
