package com.btc100.btc_q.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping("/adminCancel")
public class AdminCancel extends BaseAction {

	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	BQMemberTransactionService bqMemberTransactionService;

	/**
	 * 查询页面跳转
	 */

	@RequestMapping("/cancel")
	public String queryPage(HttpServletRequest request, HttpServletResponse response) {
		return super.url_JSP("adminCancel");
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
		
		List<PendingDelegation> all = new ArrayList<PendingDelegation>();
		
		List<BQTransaction> transactionList = super.findCurrTrans();
		if (StringUtil.isEmpty(code)) {
			for (BQTransaction tran : transactionList) {
				List<PendingDelegation> plist = pendingDelegationService.getAllByUserId(direct, tran.getTransaction_code(), Long.parseLong(userId));
				all.addAll(plist);
			}
		} else {
			BQTransaction transaction = null;
			for (BQTransaction tran : transactionList) {
				if(tran.getTransaction_code().equalsIgnoreCase(code)){
					transaction = tran;
					break;
				}
			}
	
			if(transaction == null) {
				return super.errorPage(model, "请输入正确的合约号");
			}
			all = pendingDelegationService.getAllByUserId(direct, transaction.getTransaction_code(), Long.parseLong(userId));
			model.addAttribute("trancode", transaction.getTransaction_code());
		}
		
		model.addAttribute("userId", userId);
		model.addAttribute("email", user.getF_user_email());
		model.addAttribute("direct", direction);

		if(all.size() > 0) {
			model.addAttribute("plist", all);
		}
		
		return super.url_JSP("adminCancel");
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
		
		List<PendingDelegation> all = new ArrayList<PendingDelegation>();
		List<BQTransaction> transactionlist = super.findCurrTrans();
		if (StringUtil.isEmpty(code)) {
			for (BQTransaction bqt : transactionlist) {
				code = bqt.getTransaction_code();
				cancelPendingWithCode(code, idirection, user.getF_user_id());
				List<PendingDelegation> plist = pendingDelegationService.getAllByUserId(-1, code, Long.parseLong(userId));
				all.addAll(plist);
			}
		} else {
			BQTransaction transaction = null;
			for (BQTransaction tran : transactionlist) {
				if (tran.getTransaction_code().equalsIgnoreCase(code)) {
					transaction = tran;
					break;
				}
			}
			if (transaction == null) {
				return super.errorPage(model, "请输入正确的合约号");
			}
			cancelPendingWithCode(transaction.getTransaction_code(), idirection, user.getF_user_id());
			all = pendingDelegationService.getAllByUserId(-1, transaction.getTransaction_code(), Long.parseLong(userId));
			model.addAttribute("trancode", transaction.getTransaction_code());
		}
		
		if(all.size() > 0) {
			model.addAttribute("plist", all);
		}
		
		model.addAttribute("userId", userId);
		model.addAttribute("email", user.getF_user_email());
		
		return super.url_JSP("adminCancel");
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
			return super.errorPage(model, "请输入正确的合约号");
		}
		
		code = code.toLowerCase();
		
		BQTransaction bqTransaction = super.findCurrTransByCode(code);
		if (bqTransaction == null) {
			return super.errorPage(model, "不存在该合约");
		}
		
		PendingDelegation pd = pendingDelegationService.findPendingById(Long.valueOf(pdId), code);

		if (pd == null) {
			return super.errorPage(model, "不存在该挂单");
		}
		
		if (pd.getF_user_id() != user.getF_user_id()) {
			return super.errorPage(model, "不是该用户的单子，检查后重新填写");
		}
		
		if (pd.getPriority() != 3) {
			String lk = pendingDelegationService.lock(pd.getTransaction_code(), pd.getDelegation_id());
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
							pendingDelegationService.cancelPending_one(pd, bqTransaction.getTrade_coefficient(), true);
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
						pendingDelegationService.unLock(pd.getTransaction_code(), pd.getDelegation_id(), lk);
					}
				}
			}
		}
		
		List<PendingDelegation> all = pendingDelegationService.getAllByUserId(-1, pd.getTransaction_code(), Long.parseLong(userId));
		
		if(all.size() > 0) {
			model.addAttribute("plist", all);
		}
		
		model.addAttribute("trancode", pd.getTransaction_code());
		model.addAttribute("userId", userId);
		model.addAttribute("email", user.getF_user_email());
		
		return super.url_JSP("adminCancel");
	}

	private void cancelPendingWithCode(String code, int direction, long uid) throws ServiceException {
		int dealCount = 0;
		
		BQTransaction bqTransaction = super.findCurrTransByCode(code);
		if (bqTransaction == null) {
			return;
		}
		
		List<BQMemberTransaction> transList = bqMemberTransactionService.readTransactionByUserId(code, uid);
		try {
			boolean locked = true;
			for (BQMemberTransaction trans : transList) {
				if (!bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id())) {
					locked = false;
					break;
				} 
				
				dealCount++;
			}
	
			if (!locked) return;
		
			List<PendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(direction, code, uid);
			for (PendingDelegationCache pendingDelegationCache : plistCache) {
				String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, bqTransaction.getTrade_coefficient(), true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegationCache != null) {
							pendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<PendingDelegation> plist = pendingDelegationService.getAllByUserId(direction, code, uid);
			for (PendingDelegation pendingDelegation : plist) {
				String lock =  pendingDelegationService.lock(code, pendingDelegation.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationService.cancelPending_one(pendingDelegation, bqTransaction.getTrade_coefficient(), true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegation != null) {
							pendingDelegationService.unLock(code, pendingDelegation.getDelegation_id(), lock);
						}
					}
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			for (BQMemberTransaction trans : transList) {
				if (dealCount == 0) {
					break;
				}
				
				if (trans != null) {
					bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
				dealCount--;
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
