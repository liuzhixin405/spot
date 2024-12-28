package com.btc100.btc_q.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping("/adminCancelSpot")
public class AdminCancelSpot extends BaseAction {

	@Autowired
	private SpotPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private SpotPendingDelegationService pendingDelegationService;
	@Autowired
	private BQMemberService bqMemberService;

	/**
	 * 查询页面跳转
	 */
	@RequestMapping("/cancel")
	public String queryPage(HttpServletRequest request, HttpServletResponse response) {
		return super.url_JSP("adminCancelSpot");
	}

	@RequestMapping("/query")
	public String queryPending(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		String userId = request.getParameter("userId");
		String email = request.getParameter("email");
		String direction = request.getParameter("direction");
		String code = request.getParameter("code");
		
		BQMember user = this.selectUser(userId, email);

		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}
		
		int direct = ConvertUtil.toInt(direction, 0);
		if (ConvertUtil.toInt(direction, 0) <= 0) {
			return super.errorPage(model, "请输入正确的挂单方向");
		}
		
		List<SpotPendingDelegation> all = new ArrayList<SpotPendingDelegation>();
		
		List<BQSpotMarketCoinPair> transactionList = super.findCurrCoinPair();
		if (StringUtil.isEmpty(code)) {
			for (BQSpotMarketCoinPair tran : transactionList) {
				List<SpotPendingDelegation> plist = pendingDelegationService.getAllByUserId(direct, tran.getCoin_pair_name(), Long.parseLong(userId));
				all.addAll(plist);
			}
		} else {
			BQSpotMarketCoinPair transaction = null;
			for (BQSpotMarketCoinPair tran : transactionList) {
				if(tran.getCoin_pair_name().equalsIgnoreCase(code)){
					transaction = tran;
					break;
				}
			}
	
			if(transaction == null) {
				return super.errorPage(model, "请输入正确的交易对");
			}
			all = pendingDelegationService.getAllByUserId(direct, transaction.getCoin_pair_name(), Long.parseLong(userId));
			model.addAttribute("trancode", transaction.getCoin_pair_name());
		}
		
		model.addAttribute("userId", userId);
		model.addAttribute("email", user.getF_user_email());
		model.addAttribute("direct", direction);

		if(all.size() > 0) {
			model.addAttribute("plist", all);
		}
		
		return super.url_JSP("adminCancelSpot");
	}

	//取消全部
	@RequestMapping("/cancleDeal")
	public String cancleDeal(HttpServletRequest request, Model model) throws ServiceException {

		String userId = request.getParameter("userId");
		String email = request.getParameter("email");
		String direction = request.getParameter("direction"); // 指的是买/卖/全部 -- 1/2/0
		String code = request.getParameter("code");
		
		BQMember user = this.selectUser(userId, email);

		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}
		
		int idirection = ConvertUtil.toInt(direction, -1);
		if (idirection < 0) {
			return super.errorPage(model, "非法操作");
		}
		
		List<SpotPendingDelegation> all = new ArrayList<SpotPendingDelegation>();
		List<BQSpotMarketCoinPair> transactionlist = super.findCurrCoinPair();
		if (StringUtil.isEmpty(code)) {
			for (BQSpotMarketCoinPair bqt : transactionlist) {
				code = bqt.getCoin_pair_name();
				cancelPendingWithCode(code, idirection, user.getF_user_id());
				List<SpotPendingDelegation> plist = pendingDelegationService.getAllByUserId(-1, code, Long.parseLong(userId));
				all.addAll(plist);
			}
		} else {
			BQSpotMarketCoinPair transaction = null;
			for (BQSpotMarketCoinPair tran : transactionlist) {
				if (tran.getCoin_pair_name().equalsIgnoreCase(code)) {
					transaction = tran;
					break;
				}
			}
			if (transaction == null) {
				return super.errorPage(model, "请输入正确的交易对");
			}
			cancelPendingWithCode(transaction.getCoin_pair_name(), idirection, user.getF_user_id());
			all = pendingDelegationService.getAllByUserId(-1, transaction.getCoin_pair_name(), Long.parseLong(userId));
			model.addAttribute("trancode", transaction.getCoin_pair_name());
		}
		
		if(all.size() > 0) {
			model.addAttribute("plist", all);
		}
		
		model.addAttribute("userId", userId);
		model.addAttribute("email", user.getF_user_email());
		
		return super.url_JSP("adminCancelSpot");
	}

	@RequestMapping("/canclePendingOne")
	public String canclePendingDelegationAnsyc(HttpServletRequest request, Model model) throws ServiceException {
		
		String userId = request.getParameter("userId");
		String email = request.getParameter("email");
		String pdId = request.getParameter("pdId"); // 挂单号，委托ID
		String code = request.getParameter("code");
		
		BQMember user = this.selectUser(userId, email);

		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}

		if (StringUtil.isEmpty(code)) {
			return super.errorPage(model, "请输入正确的交易对");
		}
		
		code = code.toLowerCase();
		
		BQSpotMarketCoinPair bqTransaction = super.findCurrCoinPairByName(code);
		if (bqTransaction == null) {
			return super.errorPage(model, "不存在该交易对");
		}
		
		SpotPendingDelegation pd = pendingDelegationService.findPendingById(Long.valueOf(pdId), code);

		if (pd == null) {
			return super.errorPage(model, "不存在该挂单");
		}
		
		if (pd.getF_user_id() != user.getF_user_id()) {
			return super.errorPage(model, "不是该用户的单子，检查后重新填写");
		}
		
		String lk = pendingDelegationService.lock(pd.getCoin_pair_name(), pd.getDelegation_id());
		if (!StringUtil.isEmpty(lk)) {
			try {
				pendingDelegationService.cancelPending_one(pd, true, bqTransaction);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pd != null) {
					pendingDelegationService.unLock(pd.getCoin_pair_name(), pd.getDelegation_id(), lk);
				}
			}
		}
		
		List<SpotPendingDelegation> all = pendingDelegationService.getAllByUserId(-1, pd.getCoin_pair_name(), Long.parseLong(userId));
		
		if(all.size() > 0) {
			model.addAttribute("plist", all);
		}
		
		model.addAttribute("trancode", pd.getCoin_pair_name());
		model.addAttribute("userId", userId);
		model.addAttribute("email", user.getF_user_email());
		
		return super.url_JSP("adminCancelSpot");
	}

	private void cancelPendingWithCode(String code, int direction, long uid) throws ServiceException {
		BQSpotMarketCoinPair bqTransaction = super.findCurrCoinPairByName(code);
		if (bqTransaction == null) {
			return;
		}
		
		List<SpotPendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(direction, code, uid);
		for (SpotPendingDelegationCache pendingDelegationCache : plistCache) {
			String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, true, bqTransaction);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						pendingDelegationCacheService.unLock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		
		List<SpotPendingDelegation> plist = pendingDelegationService.getAllByUserId(direction, code, uid);
		for (SpotPendingDelegation pendingDelegation : plist) {
			String lock =  pendingDelegationService.lock(code, pendingDelegation.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationService.cancelPending_one(pendingDelegation, true, bqTransaction);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegation != null) {
						pendingDelegationService.unLock(code, pendingDelegation.getDelegation_id(), lock);
					}
				}
			}
		}
	}

	/**
	 * 通过用户id或者email查询用户
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
