package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
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
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.entity.BQTransactionRate;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionFeeRateService;
import com.btc100.btc_q.service.BQTransactionRateService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.Utils;

@Controller
@RequestMapping(value = "/adminpc")
public class UserpcAction extends BaseAction {

	@Autowired
	private BQTransactionRateService bqTransactionRateService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	public BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionFeeRateService bqTransactionFeeRateService;

	/**
	 * 查询页面跳转
	 */

	@RequestMapping("/queryPage")
	public String queryPage(HttpServletRequest request, Model mo) {
		return super.url_JSP("adminPC");
	}

	/**
	 * 根据用户邮箱查找用户的合约
	 */

	@RequestMapping("/queryTrade")
	public String querytrade(HttpServletRequest request, Model model) {

		String userId = request.getParameter("userId");
		String email = request.getParameter("email");
		BQMember user = this.selectUser(userId, email);

		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}

		// 当前合约
		List<BQTransaction> transactionList = super.findCurrTrans();
		Map<String, List<BQMemberTransaction>> trans_buy_map = new HashMap<String, List<BQMemberTransaction>>();
		Map<String, List<BQMemberTransaction>> trans_sell_map = new HashMap<String, List<BQMemberTransaction>>();

		// buy持仓信息
		for (BQTransaction tran : transactionList) {
			List<BQMemberTransaction> trans = bqMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), tran.getTransaction_code(),
					Contents.DIRECTION_BUY);
			if (trans != null && trans.size() > 0) {
				trans_buy_map.put(tran.getTransaction_code(), trans);
			}
		}

		// sell持仓信息
		for (BQTransaction tran : transactionList) {
			List<BQMemberTransaction> trans = bqMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), tran.getTransaction_code(),
					Contents.DIRECTION_SELL);
			if (trans != null && trans.size() > 0) {
				trans_sell_map.put(tran.getTransaction_code(), trans);
			}
		}

		model.addAttribute("userId", userId);
		model.addAttribute("email", user.getF_user_email());
		model.addAttribute("trans_buy_map", trans_buy_map);
		model.addAttribute("trans_sell_map", trans_sell_map);

		return super.url_JSP("adminPC");
	}

	/**
	 * 进行平仓
	 * 
	 * @param request
	 * @param response
	 * @throws ParseException
	 */
	@RequestMapping("/tradePC")
	public String tradePDC(HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		// 得到用户挂单的id 然后得到用户的交易合约
		long mtid = Convert.toLong(request.getParameter("mtid").trim(), 0l);
		if (mtid <= 0) {
			return super.errorPage(model, "持仓ID参数非法!");
		}

		long userId = Convert.toLong(request.getParameter("userId").trim(), 0l);
		if (userId <= 0) {
			return super.errorPage(model, "用户ID参数非法!");
		}

		BQMember user = bqMemberService.readUserById(userId);

		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}

		BQMemberTransaction memberTrans = bqMemberTransactionService.get(mtid);
		if (memberTrans == null) {
			return super.errorPage(model, "未找到该持仓");
		}

		if (memberTrans.getF_user_id() != user.getF_user_id()) {
			return super.errorPage(model, "未找到该持仓");
		}
		
		if (memberTrans.getFlag() != 0) {
			return super.errorPage(model, "操作失败");
		}

		String transSelect = memberTrans.getTransaction_code();
		BQTransaction trans = findCurrTransByCode(transSelect);

		if (memberTrans.getHold_count().signum() <= 0) {
			return super.errorPage(model, "持仓数量须大于0");
		}

		BigDecimal amount_client = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0));
		BigDecimal price_client = new BigDecimal(ConvertUtil.toDouble(request.getParameter("price"), 0));

		BQTransactionType bqTransactionType = transTypeService.readTransactionType(trans.getTransaction_type());
		if (bqTransactionType == null) {
			return super.errorPage(model, "找不到当前委托类型");
		}
		
		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		amount_client = amount_client.setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);

		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			return super.errorPage(model, "输入的数量或者价格不正确");
		}
		
		if (amount_client.compareTo(memberTrans.getHold_count()) > 0) {
			return super.errorPage(model, "平仓数量不能大于持仓量");
		}

		BQTransactionType transType = bqTransTypeService.getTransTypeById(trans.getTransaction_type());
		if (transType == null) {
			return super.errorPage(model, "找不到当前委托类型");
		}
		
		BigDecimal btc_index_price = findBPIByCode(transType.getType_code());
		if (trans.getTransaction_kind() != null && trans.getTransaction_kind() == 1) {
			BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + trans.getTransaction_code());
			btc_index_price = preClose;
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
			return super.errorPage(model, "获取指数失败");
		}
		
		BQMemberLevel userlevel = getLevelByUserBfx(user);
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
				return super.errorPage(model, "卖出价格必须大于" + minIndexPrice);
			}

			BigDecimal multiple = memberTrans.getTrade_multiple();
			BigDecimal deposit = BTCQMath.getDeposit(amount_client, price_client, multiple, trans.getTrade_coefficient());

			PendingDelegationCache cahce = new PendingDelegationCache(trans.getTransaction_id(), trans.getTransaction_code(),
					Contents.DIRECTION_SELL, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
					feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

			if (bqMemberTransactionService.lockToTrade(mtid)) {
				try {
					String sip = IpMacUtil.getIpAddr(request);
					pendingDelegationCacheService.insertPCD(cahce, mtid, trans.getTrade_coefficient(), Contents.DEVICE_ADMIN, sip);
				} catch (Exception e) {
					e.printStackTrace();
					return super.errorPage(model, "下单失败，请稍后再试！");
				} finally {
					bqMemberTransactionService.unlockToTrade(mtid);
				}
			} else {
				return super.errorPage(model, "下单失败，请稍后再试！");
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
					BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, RoundingMode.HALF_DOWN);
//					if (oneDayMax.compareTo(maxIndexPrice)< 0) {
						maxIndexPrice = oneDayMax;
//					}
				}
				
//				BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + trans.getTransaction_code());
//				if (openPrice != null) {
//					maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, RoundingMode.HALF_DOWN);
//				}
			}
			
			if (price_client.compareTo(maxIndexPrice) > 0) {
				return super.errorPage(model, "买入价格必须小于" + maxIndexPrice);
			}

			BigDecimal multiple = memberTrans.getTrade_multiple();
			BigDecimal deposit = BTCQMath.getDeposit(amount_client, price_client, multiple, trans.getTrade_coefficient());

			PendingDelegationCache cahce = new PendingDelegationCache(trans.getTransaction_id(), trans.getTransaction_code(),
					Contents.DIRECTION_BUY, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
					feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

			if (!bqMemberTransactionService.lockToTrade(mtid)) {
				return super.errorPage(model, "下单失败，请稍后再试！");
			}
			try {
				String sip = IpMacUtil.getIpAddr(request);
				pendingDelegationCacheService.insertPCK(cahce, mtid, trans.getTrade_coefficient(), Contents.DEVICE_ADMIN, sip);
			} catch (Exception e) {
				e.printStackTrace();
				return super.errorPage(model, "下单失败，请稍后再试！");
			} finally {
				bqMemberTransactionService.unlockToTrade(mtid);
			}
		}

		model.addAttribute("userId", userId);
		model.addAttribute("email", user.getF_user_email());

		return super.redirect("/adminpc/queryTrade.do");

	}

	/**
	 * 
	 * @param userId
	 * @param userEmail
	 * @return
	 */
	private BQMember selectUser(String userId, String userEmail) {
		long uid = ConvertUtil.toLong(userId, 0l);
		BQMember user = bqMemberService.readUserById(uid);
		if (user == null) {
			user = bqMemberService.readUserByEmail(userEmail);
		}
		return user;
	}
}
