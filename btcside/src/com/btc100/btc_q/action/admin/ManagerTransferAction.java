package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.ContractAccountLogService;
import com.btc100.btc_q.service.TransferAccountService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.PagerModel;
import com.google.gson.Gson;

@Controller
@RequestMapping("/managerTransfer")
public class ManagerTransferAction extends BaseAction {

	Logger logger = Logger.getLogger(ManagerTransferAction.class);

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private TransferAccountService transferAccountService;
	@Autowired
	private ContractAccountLogService contractAccountLogService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;


	@RequestMapping("/loadTransferRecords")
	public String loadTransferRecords(HttpServletRequest request, Model model) {
		List<BQTransaction> transactionList = super.findCurrTrans();
		model.addAttribute("transactionList", transactionList);

		model.addAttribute("type", "1");
		model.addAttribute("pm", new PagerModel());
		return super.url_JSP("adminTransfer");
	}

	@RequestMapping("/queryUserAccount")
	public String queryUserAccount(HttpServletRequest request, Model model) {
		
		String userId = request.getParameter("userId");
		String email = request.getParameter("email");
		String type = request.getParameter("type");
		
		BQMember user = this.selectUser(userId, email);

		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}
		
		int direct = ConvertUtil.toInt(type, 0);
		if (direct <= 0) {
			return super.errorPage(model, "类型错误");
		}
		
		List<BQTransaction> transactionList = super.findCurrTrans();
		model.addAttribute("transactionList", transactionList);
		String code = request.getParameter("code");
		BQTransaction bqt = null;
		if (!StringUtil.isEmpty(code)) {
			for (BQTransaction trans : transactionList) {
				if (trans.getTransaction_code().equalsIgnoreCase(code)) {
					bqt = trans;
					break;
				}
			}
		}
		if (bqt == null) {
			bqt = transactionList.get(0);
		}

		model.addAttribute("currentTransaction", bqt);
		
		BigDecimal accountAmount = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID).getBtc().setScale(4, RoundingMode.DOWN);
		BQAccountContract accountContract = bqAccountContractService.readByUidCode(user.getF_user_id(), bqt.getTransaction_code());
		BigDecimal contractAmount = accountContract.getF_btc().setScale(4, RoundingMode.DOWN);
		model.addAttribute("accountAmount", accountAmount);
		model.addAttribute("contractAmount", contractAmount);
		
		PagerModel pm = new PagerModel();
		if ("1".equals(type)) { // 转入合约账户
			pm = contractAccountLogService.readPageAccount(user.getF_user_id(), code, Contents.TRANS_TYPE_CZ, 0, 0);
		} else { // 从合约账户转出
			pm = contractAccountLogService.readPageAccount(user.getF_user_id(), code, Contents.TRANS_TYPE_TX, 0, 0);
		}
		
		model.addAttribute("userId", userId);
		model.addAttribute("email", email);
		model.addAttribute("type", type);
		model.addAttribute("pm", pm);
		
		return super.url_JSP("adminTransfer");
	}

	@RequestMapping("/transferIndo")
	public void transferAccountIn(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		String email = request.getParameter("email");
		String code = request.getParameter("code");
		
		BQMember user = this.selectUser("", email);

		if (user == null) {
			checkVO.setMsg("用户不存在！");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		List<BQTransaction> transactionList = super.findCurrTrans();
		BQTransaction bqt = null;
		if (!StringUtil.isEmpty(code)) {
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(code)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg("合约不存在！");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!bqt.getTransfer_in_enable()) {
			checkVO.setMsg("风控系统暂停合约转入功能");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0)).setScale(8, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg("转入数量错误！");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQAccount account = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		if (amount.compareTo(account.getBtc()) > 0) {
			checkVO.setMsg("转入数量超出总账户的可用！");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		// 创建账户如果不存在
		BQAccountContract accountContract = bqAccountContractService.readWithNullByUidCode(user.getF_user_id(), code);
		if (accountContract == null) {
			String lock = spyMemcachedClient.lock(code + BTCQMemecacheKeys.ACCOUNT_CONTRACT + user.getF_user_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					bqAccountContractService.createByUidCode(user.getF_user_id(), code);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					spyMemcachedClient.unlock(code + BTCQMemecacheKeys.ACCOUNT_CONTRACT + user.getF_user_id(), lock);
				}
			}
		}

		try {
			if (!StringUtil.isEmpty(transferAccountService.updateAccountForZC(user.getF_user_id(), code, amount.negate()))) {
				checkVO.setCode("success");
				checkVO.setMsg("转入成功！");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg("转入失败！");
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg("转入失败！");
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	@RequestMapping("/transferOutdo")
	public void transferAccountOut(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		String email = request.getParameter("email");
		String code = request.getParameter("code");

		BQMember user = this.selectUser("", email);

		if (user == null) {
			checkVO.setMsg("用户不存在！");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		List<BQTransaction> transactionList = super.findCurrTrans();
		BQTransaction bqt = null;
		if (!StringUtil.isEmpty(code)) {
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(code)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg("合约不存在！");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!bqt.getTransfer_out_enable()) {
			checkVO.setMsg("风控系统暂停合约转出功能");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0));
		amount = amount.setScale(9, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg("转出数量错误！");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQAccountContract accountContract = bqAccountContractService.readByUidCode(user.getF_user_id(), code);
		BigDecimal minbtc = accountContract.getF_btc();
		
		if (amount.compareTo(minbtc) > 0) {
			checkVO.setMsg("转出数量超出该合约的可用！");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		try {
			if (!StringUtil.isEmpty(transferAccountService.updateAccountForZC(user.getF_user_id(), code, amount))) {
				checkVO.setCode("success");
				checkVO.setMsg("转出成功！");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg("转出失败！");
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg("转出失败！");
		toWeb(response, gson.toJson(checkVO));
		return;
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
