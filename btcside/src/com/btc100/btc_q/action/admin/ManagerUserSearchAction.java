package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.entity.BQTransactionRate;
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
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountAmountLogService;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQStopLossService;
import com.btc100.btc_q.service.BQTransactionFeeRateService;
import com.btc100.btc_q.service.BQTransactionRateService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryStopLossService;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.BQTransVo;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.PendingDelegationVO;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.btc100.btc_q.vo.UserAssetsVO;
import com.google.gson.Gson;

@Controller
@RequestMapping(value = "/managerUserSearch")
public class ManagerUserSearchAction extends BaseAction {

	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private DeliveryMemberTransactionService deliveryMemberTransactionService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private DeliveryTransactionService deliveryTransactionService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationService deliveryPendingDelegationService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQStopLossService bqStopLossService;
	@Autowired
	private DeliveryStopLossService deliveryStopLossService;
	@Autowired
	private BQAccountAmountLogService bqAccountAmountLogService;
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private DeliveryAccountContractService deliveryAccountContractService;
	@Autowired
	private BQTransactionRateService bqTransactionRateService;
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
	private BQTransactionFeeRateService bqTransactionFeeRateService;
	@Autowired
	private DeliveryTransactionFeeRateService deliveryTransactionFeeRateService;
	
	@RequestMapping(value = "/loadUserSearch")
	public String loadUsers(HttpServletRequest request, Model mo) {
		return super.url_JSP("userSearchlist");
	}

	@RequestMapping(value = "/selectUsers")
	public String selectUsers(String userId, String userEmail, String type, Model model) {

		BQMember user = this.selectUser(userId, userEmail);

		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}

		long id = user.getF_user_id();

		if ("1".equals(type)) { // 基本查询

			BQAccount bqAccount = bqAccountService.readAccount(id, Contents.TRADE_CURRENCY_TYPE_ID);
			BigDecimal account = bqAccount.getBtc();// 币币可用
			BigDecimal freez = bqAccount.getBtc_locked();// 提现冻结
			model.addAttribute("account", account);
			model.addAttribute("freez", freez);
			BigDecimal total = account.add(freez);// 账户总资产
			
			/************************* 永续合约  ***********************/
			BigDecimal sumAvailable = new BigDecimal(0); // 合约可用（永续）
			BigDecimal deposit = bqMemberTransactionService.readDepositByUid(id); // 持仓保证金（永续）
			BigDecimal sumFreeze = BigDecimal.ZERO; // 挂单冻结（永续）
			BigDecimal sumContract = BigDecimal.ZERO; // 合约总计（永续）= 合约可用 + 挂单冻结 + 持仓保证金

			/***************** 资产详情 *****************/
			List<UserAssetsVO> listvo = new ArrayList<UserAssetsVO>();
			List<BQTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
			if (list == null) {
				list = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			}
			List<BQMemberTransaction> list1 = new ArrayList<BQMemberTransaction>();
			List<BQMemberTransaction> list2 = new ArrayList<BQMemberTransaction>();

			for (BQTransaction bqTransaction : list) {
				String code = bqTransaction.getTransaction_code();
				list1 = bqMemberTransactionService.readBQMemberTrasaction(id, code, 1);// 1:多单开仓,买多
				list2 = bqMemberTransactionService.readBQMemberTrasaction(id, code, 2);// 2:空头开仓,卖空

				// 合约号
				UserAssetsVO uav = new UserAssetsVO();
				uav.setShowName(bqTransaction.getTransaction_show_name());
				uav.setCode(code);
				uav.setTransactionType(bqTransaction.getTransaction_type());
				// 做多
				BigDecimal buyAccount = BigDecimal.ZERO;
				for (BQMemberTransaction bqmt1 : list1) {
					buyAccount = buyAccount.add(bqmt1.getHold_count());
				}
				uav.setBuyAmount(buyAccount);

				// 做空
				BigDecimal sellAccount = BigDecimal.ZERO;
				for (BQMemberTransaction bqmt2 : list2) {
					sellAccount = sellAccount.add(bqmt2.getHold_count());
				}
				uav.setSellAmount(sellAccount);
				// 持仓市值
				BigDecimal bgsum = buyAccount.add(sellAccount);
				uav.setTotalAmount(bgsum);

				// 做多委托
				BigDecimal deleBuyAccount = pendingDelegationService.readDelegation(id, code, 1);
				uav.setBuydelegation(deleBuyAccount);
				// 做空委托
				BigDecimal deleSellAccount = pendingDelegationService.readDelegation(id, code, 2);
				uav.setSelldelegation(deleSellAccount);
				// 委托市值
				BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
				uav.setDelegationAmount(delesum);
				// 合约资金合计
				BQAccountContract bqag = bqAccountContractService.readBTCByUidCode(id, code);

				if (bqag == null) {
					uav.setF_btc(new BigDecimal(0));
					uav.setF_lockbtc(new BigDecimal(0));
					sumAvailable = sumAvailable.add(new BigDecimal(0));
				} else {
					sumAvailable = sumAvailable.add(bqag.getF_btc());
					sumFreeze = sumFreeze.add(bqag.getF_lockbtc());
					uav.setF_btc(bqag.getF_btc());
					uav.setF_lockbtc(bqag.getF_lockbtc());
				}
				// 放到list
				listvo.add(uav);
			}
			model.addAttribute("listvo", listvo);
			
			sumContract = sumAvailable.add(sumFreeze).add(deposit); // 合约总计（永续）= 合约可用 + 挂单冻结 + 持仓保证金
			model.addAttribute("sumAvailable", sumAvailable); // 合约可用（永续）
			model.addAttribute("deposit", deposit); // 持仓保证金（永续）
			model.addAttribute("sumFreeze", sumFreeze); // 挂单冻结（永续）
			model.addAttribute("sumContract", sumContract); // 合约总计（永续）
			total = total.add(sumContract); // 账户总资产

			/***************** 持仓情况 *****************/

			BQMemberLevel userlevel = getLevelByUserBfx(user);

			List<BQTransaction> transactionList = super.findCurrTrans();

			List<BQTransVo> listTransVos = new ArrayList<BQTransVo>();

			for (BQTransaction trans : transactionList) {
				List<BQMemberTransaction> buyMemberTransaction = bqMemberTransactionService.readBQMemberTrasaction(id, trans.getTransaction_code(),
						Contents.DIRECTION_BUY);
				List<BQMemberTransaction> sellMemberTransaction = bqMemberTransactionService.readBQMemberTrasaction(id, trans.getTransaction_code(),
						Contents.DIRECTION_SELL);

				// 获取标记价格
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + trans.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(trans.getTransaction_type_code());
				}
				
				BQTransactionType tranType = transTypeService.readTransactionType(trans.getTransaction_type());
				if (type == null) {
					continue;
				}
				
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
			
			List<PendingDelegation> pdList = pendingDelegationService.getAllByUserId(transactionList, "", "", id, "");
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
				List<PendingDelegationCache> pdCaches = pendingDelegationCacheService.getAllByUserId(0, trans.getTransaction_code(), id);
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
			BigDecimal deliveryDeposit = deliveryMemberTransactionService.readDepositByUid(id); // 持仓保证金（交割）
			BigDecimal deliveryFreeze = BigDecimal.ZERO; // 挂单冻结（交割）
			BigDecimal deliveryContract = BigDecimal.ZERO; // 合约总计（交割）= 合约可用 + 挂单冻结 + 持仓保证金

			/***************** 资产详情 *****************/
			List<UserAssetsVO> deliverylistvo = new ArrayList<UserAssetsVO>();
			List<DeliveryTransaction> deliverylist = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
			if (deliverylist == null) {
				deliverylist = deliveryTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			}
			List<DeliveryMemberTransaction> deliverylist1 = new ArrayList<DeliveryMemberTransaction>();
			List<DeliveryMemberTransaction> deliverylist2 = new ArrayList<DeliveryMemberTransaction>();

			for (DeliveryTransaction bqTransaction : deliverylist) {
				String code = bqTransaction.getTransaction_code();
				deliverylist1 = deliveryMemberTransactionService.readBQMemberTrasaction(id, code, 1);// 1:多单开仓,买多
				deliverylist2 = deliveryMemberTransactionService.readBQMemberTrasaction(id, code, 2);// 2:空头开仓,卖空

				// 合约号
				UserAssetsVO uav = new UserAssetsVO();
				uav.setShowName(bqTransaction.getTransaction_show_name());
				uav.setCode(code);
				uav.setTransactionType(bqTransaction.getTransaction_type());
				// 做多
				BigDecimal buyAccount = BigDecimal.ZERO;
				for (DeliveryMemberTransaction bqmt1 : deliverylist1) {
					buyAccount = buyAccount.add(bqmt1.getHold_count());
				}
				uav.setBuyAmount(buyAccount);

				// 做空
				BigDecimal sellAccount = BigDecimal.ZERO;
				for (DeliveryMemberTransaction bqmt2 : deliverylist2) {
					sellAccount = sellAccount.add(bqmt2.getHold_count());
				}
				uav.setSellAmount(sellAccount);
				// 持仓市值
				BigDecimal bgsum = buyAccount.add(sellAccount);
				uav.setTotalAmount(bgsum);

				// 做多委托
				BigDecimal deleBuyAccount = deliveryPendingDelegationService.readDelegation(id, code, 1);
				uav.setBuydelegation(deleBuyAccount);
				// 做空委托
				BigDecimal deleSellAccount = deliveryPendingDelegationService.readDelegation(id, code, 2);
				uav.setSelldelegation(deleSellAccount);
				// 委托市值
				BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
				uav.setDelegationAmount(delesum);
				// 合约资金合计
				DeliveryAccountContract bqag = deliveryAccountContractService.readBTCByUidCode(id, code);

				if (bqag == null) {
					uav.setF_btc(new BigDecimal(0));
					uav.setF_lockbtc(new BigDecimal(0));
					deliveryAvailable = deliveryAvailable.add(new BigDecimal(0));
				} else {
					deliveryAvailable = deliveryAvailable.add(bqag.getF_btc());
					deliveryFreeze = deliveryFreeze.add(bqag.getF_lockbtc());
					uav.setF_btc(bqag.getF_btc());
					uav.setF_lockbtc(bqag.getF_lockbtc());
				}
				// 放到list
				deliverylistvo.add(uav);
			}
			model.addAttribute("deliverylistvo", deliverylistvo);
			
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
				List<DeliveryMemberTransaction> buyMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(id, trans.getTransaction_code(),
						Contents.DIRECTION_BUY);
				List<DeliveryMemberTransaction> sellMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(id, trans.getTransaction_code(),
						Contents.DIRECTION_SELL);

				// 获取标记价格
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + trans.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(trans.getTransaction_type_code());
				}
				
				DeliveryTransactionType tranType = deliveryTransTypeService.readTransactionType(trans.getTransaction_type());
				if (type == null) {
					continue;
				}
				
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
			
			List<DeliveryPendingDelegation> deliverypdList = deliveryPendingDelegationService.getAllByUserId(deliverylist, "", "", id, "");
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
				List<DeliveryPendingDelegationCache> pdCaches = deliveryPendingDelegationCacheService.getAllByUserId(0, trans.getTransaction_code(), id);
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
			
		} else if ("2".equals(type)) { // 资金流水
			model.addAttribute("pm", bqAccountAmountLogService.readListRechargeWithdraw(id));
		} else if ("3".equals(type)) {
			model.addAttribute("pm1", bqMemberRechargeService.getRecordAll(id, Contents.TRADE_CURRENCY_TYPE_ID));
			model.addAttribute("allRecharge", bqMemberRechargeService.sumRecord(id, 1, 1, Contents.TRADE_CURRENCY_TYPE_ID)); // 总充值
			model.addAttribute("allWithDraw", bqMemberRechargeService.sumRecord(id, 2, 1, Contents.TRADE_CURRENCY_TYPE_ID)); // 总提现
			model.addAttribute("freezeWithDraw", bqMemberRechargeService.sumRecord(id, 2, 0, Contents.TRADE_CURRENCY_TYPE_ID)); // 提现冻结
		}

		model.addAttribute("selectType", type);
		model.addAttribute("userId", id);
		model.addAttribute("userEmail", user.getF_user_email());
		return super.url_JSP("userSearchlist");
	}

	@RequestMapping("/canclePendingAnsyc")
	public void canclePendingDelegationAnsyc(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		AjaxCheckVO checkVO = new AjaxCheckVO();
		Gson gson = new Gson();
		String pdId = request.getParameter("pdId");
		String code = request.getParameter("code");
		long uid = ConvertUtil.toLong(request.getParameter("uid"), 0l);
		String cache = request.getParameter("cache");
		
		BQTransaction bqTransaction = super.findCurrTransByCode(code);
		if (bqTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg("撤单失败! ");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		boolean isCache = false;
		if (!StringUtil.isEmpty(cache)) {
			isCache = Boolean.valueOf(cache);
		}
		
		if (isCache) {
			PendingDelegationCache pd = pendingDelegationCacheService.findPendingById(Long.valueOf(pdId), code);

			if (pd != null && pd.getF_user_id() == uid) {
				if (pd.getPriority() != 3) {
					String lk = pendingDelegationCacheService.lock(code, pd.getDelegation_id());
					if (!StringUtil.isEmpty(lk)) {
						try {
							boolean lock = true;
							BQMemberTransaction trans = null;
							if (pd.getPriority() == 2) {
								int tradeType = Contents.TRADE_TYPE_BUY;
								
								if (pd.getDirection() == Contents.DIRECTION_BUY) {
									tradeType = Contents.TRADE_TYPE_SELL;
								}
								
								trans = bqMemberTransactionService.readMemberTransaction(code, pd.getF_user_id(), tradeType, pd.getMultiple());
								lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
							}
							
							if (lock) {
								try {
									if (pendingDelegationCacheService.cancelPendingCache_one(pd, bqTransaction.getTrade_coefficient(), true) > 0) {
										checkVO.setCode("success");
										checkVO.setMsg("撤单成功! ");
										toWeb(response, gson.toJson(checkVO));
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (pd.getPriority() == 2 && trans != null) {
										bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd != null) {
								pendingDelegationCacheService.unLock(code, pd.getDelegation_id(), lk);
							}
						}
					}
				}
			}
		} else {
			PendingDelegation pd = pendingDelegationService.findPendingById(Long.valueOf(pdId), code);

			if (pd != null && pd.getF_user_id() == uid) {
				if (pd.getPriority() != 3) {
					String lk = pendingDelegationService.lock(code, pd.getDelegation_id());
					if (!StringUtil.isEmpty(lk)) {
						try {
							boolean lock = true;
							BQMemberTransaction trans = null;
							if (pd.getPriority() == 2) {
								int tradeType = Contents.TRADE_TYPE_BUY;
								
								if (pd.getDirection() == Contents.DIRECTION_BUY) {
									tradeType = Contents.TRADE_TYPE_SELL;
								}
								
								trans = bqMemberTransactionService.readMemberTransaction(code, pd.getF_user_id(), tradeType, pd.getMultiple());
								lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
							}
							
							if (lock) {
								try {
									if (pendingDelegationService.cancelPending_one(pd, bqTransaction.getTrade_coefficient(), true) > 0) {
										checkVO.setCode("success");
										checkVO.setMsg("撤单成功! ");
										toWeb(response, gson.toJson(checkVO));
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (pd.getPriority() == 2 && trans != null) {
										bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd != null) {
								pendingDelegationService.unLock(code, pd.getDelegation_id(), lk);
							}
						}
					}
				}
			}
		}

		checkVO.setCode("error");
		checkVO.setMsg("撤单失败! ");
		toWeb(response, gson.toJson(checkVO));
	}

	/**
	 * 进行平仓
	 * 
	 * @param request
	 * @param response
	 * @throws ParseException
	 */
	@RequestMapping("/tradePC")
	public void tradePDC(HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		// 校验
		checkVO.setCode("success");
		checkVO.setMsg("平仓成功");

		// 得到用户挂单的id 然后得到用户的交易合约
		long mtid = Convert.toLong(request.getParameter("mtid").trim(), 0l);
		if (mtid <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg("持仓ID参数非法!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		long userId = Convert.toLong(request.getParameter("userId").trim(), 0l);
		if (userId <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg("用户ID参数非法!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMember user = bqMemberService.readUserById(userId);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg("查不到该用户!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberTransaction memberTrans = bqMemberTransactionService.get(mtid);
		if (memberTrans == null) {
			checkVO.setCode("error");
			checkVO.setMsg("未找到该持仓!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (memberTrans.getF_user_id() != user.getF_user_id()) {
			checkVO.setCode("error");
			checkVO.setMsg("未找到该持仓!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (memberTrans.getFlag() != 0) {
			checkVO.setCode("error");
			checkVO.setMsg("操作失败!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String transSelect = memberTrans.getTransaction_code();
		if (memberTrans.getHold_count().signum() <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg("持仓数量须大于0!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQTransaction trans = findCurrTransByCode(transSelect);
		if (trans == null) {
			checkVO.setCode("error");
			checkVO.setMsg("找不到当前委托!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		
		BigDecimal amount_client = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0));
		BigDecimal price_client = new BigDecimal(ConvertUtil.toDouble(request.getParameter("price"), 0));
		
		BQTransactionType bqTransactionType = transTypeService.readTransactionType(trans.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg("找不到当前委托类型! ");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		amount_client = amount_client.setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);

		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg("输入的数量或者价格不正确");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (amount_client.compareTo(memberTrans.getHold_count()) > 0) {
			checkVO.setCode("error");
			checkVO.setMsg("平仓数量不能大于持仓量!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
		if (trans.getTransaction_kind() != null && trans.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + trans.getTransaction_code());
		} else if (trans.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + trans.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.readLastHourAgoByCode(trans.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}
		}
		
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg("获取指数失败!");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberLevel userlevel = super.getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(trans.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		if (memberTrans.getTrade_type_id() == Contents.DIRECTION_BUY) { // 针对多仓平仓
			BQTransactionRate index_price_rate = bqTransactionRateService.getByCode(trans.getTransaction_code());
			BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
			minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
			BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
			
			if (trans.getTransaction_kind() == 2) {
				BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + trans.getTransaction_code());
				if (oneDayAgo == null) {
					oneDayAgo = tradeLogService.readLastDayAgoByCode(trans.getTransaction_code());
					if (oneDayAgo != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
					}
				}
				
				if (oneDayAgo != null) {
					BigDecimal oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
//					if (oneDayMin.compareTo(minIndexPrice) > 0) {
						minIndexPrice = oneDayMin;
//					}
				}
				
//				BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + trans.getTransaction_code());
//				if (openPrice != null) {
//					minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
//				}
			}
			
			if (price_client.compareTo(minIndexPrice) < 0) {
				checkVO.setCode("error");
				checkVO.setMsg("卖出价格必须大于" + minIndexPrice);
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			BigDecimal multiple = memberTrans.getTrade_multiple();
			BigDecimal deposit = BTCQMath.getDeposit(amount_client, price_client, multiple, trans.getTrade_coefficient());
			
			PendingDelegationCache cahce = new PendingDelegationCache(trans.getTransaction_id(), trans.getTransaction_code(),
					Contents.DIRECTION_SELL, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
					feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
			if (checkVO.getCode().equalsIgnoreCase("success")) {
				
				if (bqMemberTransactionService.lockToTrade(mtid)) {
					try {
						String sip = IpMacUtil.getIpAddr(request);
						pendingDelegationCacheService.insertPCD(cahce, mtid, trans.getTrade_coefficient(), Contents.DEVICE_ADMIN, sip);
						toWeb(response, gson.toJson(checkVO));
						return;
					} catch (Exception e) {
						e.printStackTrace();
						checkVO.setCode("error");
						checkVO.setMsg("失败，请稍后再试!");
						toWeb(response, gson.toJson(checkVO));
						return;
					} finally {
						this.bqMemberTransactionService.unlockToTrade(mtid);
					}
				} else {
					checkVO.setCode("error");
					checkVO.setMsg("失败，请稍后再试!");
					toWeb(response, gson.toJson(checkVO));
					return;
				}
			}

		} else { // 针对卖空平仓
			BQTransactionRate index_price_rate = bqTransactionRateService.getByCode(trans.getTransaction_code());
			BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
			minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
			BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
			
			if (trans.getTransaction_kind() == 2) {
				BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + trans.getTransaction_code());
				if (oneDayAgo == null) {
					oneDayAgo = tradeLogService.readLastDayAgoByCode(trans.getTransaction_code());
					if (oneDayAgo != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
					}
				}
				
				if (oneDayAgo != null) {
					BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, BigDecimal.ROUND_DOWN);
//					if (oneDayMax.compareTo(maxIndexPrice) < 0) {
						maxIndexPrice = oneDayMax;
//					}
				}
				
//				BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + trans.getTransaction_code());
//				if (openPrice != null) {
//					maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
//				}
			}
			
			if (price_client.compareTo(maxIndexPrice) > 0) {
				checkVO.setCode("error");
				checkVO.setMsg("买入价格必须小于" + maxIndexPrice);
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			BigDecimal multiple = memberTrans.getTrade_multiple();
			BigDecimal deposit = BTCQMath.getDeposit(amount_client, price_client, multiple, trans.getTrade_coefficient());

			PendingDelegationCache cahce = new PendingDelegationCache(trans.getTransaction_id(), trans.getTransaction_code(),
					Contents.DIRECTION_BUY, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
					feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
			if (checkVO.getCode().equalsIgnoreCase("success")) {
				if (!bqMemberTransactionService.lockToTrade(mtid)) {
					checkVO.setCode("error");
					checkVO.setMsg("下单失败，请稍后再试！");
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				
				try {
					String sip = IpMacUtil.getIpAddr(request);
					pendingDelegationCacheService.insertPCK(cahce, mtid, trans.getTrade_coefficient(), Contents.DEVICE_ADMIN, sip);
					toWeb(response, gson.toJson(checkVO));
					return;
				} catch (Exception e) {
					e.printStackTrace();
					checkVO.setCode("error");
					checkVO.setMsg("下单失败，请稍后再试！");
					toWeb(response, gson.toJson(checkVO));
					return;
				} finally {
					bqMemberTransactionService.unlockToTrade(mtid);
				}
			}

		}

	}

	private BQMember selectUser(String userId, String userEmail) {
		long uid = ConvertUtil.toLong(userId, 0l);
		BQMember user = bqMemberService.readUserById(uid);
		if (user == null) {
			user = bqMemberService.readUserByEmail(userEmail);
		}
		return user;
	}

	@RequestMapping("/loadAccount")
	public String loadAccount(Model model) {
		PagerModel lstUids = bqAccountContractService.readCountUserIds();
		model.addAttribute("lstUids", lstUids);
		Map<String, List<BQAccountContract>> map = new HashMap<String, List<BQAccountContract>>();

		List<BQTransaction> lstTrans = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		model.addAttribute("lstTrans", lstTrans);
		for (Object id : lstUids.getDatas()) {
			long uid = ConvertUtil.toLong(id, 0l);
			List<BQAccountContract> lstContracts = new ArrayList<BQAccountContract>();
			for (BQTransaction tran : lstTrans) {
				String code = tran.getTransaction_code();
				BQAccountContract contract = bqAccountContractService.readBTCByUidCode(uid, code);
				if (contract == null) {
					contract = new BQAccountContract(uid, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				}
				lstContracts.add(contract);
			}
			map.put("uid" + uid, lstContracts);
		}

		model.addAttribute("map", map);
		model.addAttribute("URL", "loadAccount");
		return super.url_JSP("accountList");
	}

	@RequestMapping("/sarchAccount")
	public String sarchAccount(HttpServletRequest request, Model model) {
		String userId = request.getParameter("userId");
		String email = request.getParameter("email");
		BQMember user = this.selectUser(userId, email);

		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}

		model.addAttribute("userId", user.getF_user_id());
		model.addAttribute("email", user.getF_user_email());

		List<Long> datas = new ArrayList<Long>();
		datas.add(user.getF_user_id());
		PagerModel lstUids = new PagerModel();
		lstUids.setDatas(datas);
		lstUids.setTotal(1);
		model.addAttribute("lstUids", lstUids);
		Map<String, List<BQAccountContract>> map = new HashMap<String, List<BQAccountContract>>();

		List<BQTransaction> lstTrans = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		model.addAttribute("lstTrans", lstTrans);
		for (Object id : lstUids.getDatas()) {
			long uid = ConvertUtil.toLong(id, 0l);
			List<BQAccountContract> lstContracts = new ArrayList<BQAccountContract>();
			for (BQTransaction tran : lstTrans) {
				String code = tran.getTransaction_code();
				BQAccountContract contract = bqAccountContractService.readBTCByUidCode(uid, code);
				if (contract == null) {
					contract = new BQAccountContract(uid, code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				}
				lstContracts.add(contract);
			}
			map.put("uid" + uid, lstContracts);
		}

		model.addAttribute("map", map);
		model.addAttribute("URL", "sarchAccount");
		return super.url_JSP("accountList");
	}
}
