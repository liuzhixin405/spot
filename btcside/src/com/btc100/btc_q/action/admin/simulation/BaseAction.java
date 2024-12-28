package com.btc100.btc_q.action.admin.simulation;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQSurecomeService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;

public class BaseAction {

	@Autowired
	private VTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQSurecomeService bqSurecomeService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	protected VTransactionTypeService bqTransTypeService;

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
		return "qtwner/up/down/togo/jsp/simulation/" + returnValue;
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
	public List<VTransaction> findCurrTrans() {
		List<VTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
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
	public VTransaction findTransByCode(String tcode) {
		VTransaction transaction = transactionService.readTransByCode(tcode);
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

//	/**
//	 * 获取一分钟交易价
//	 * 
//	 * @param transactionCode
//	 * @return
//	 */
//	public BigDecimal findpriceOneminago(String transactionCode) {
//		BigDecimal lastpriceOneMinAgo = spyMemcachedClient.get(transactionCode + BTCQMemecacheKeys.LASTPRICEONEMINAGO);
//		if (lastpriceOneMinAgo == null) {
//			lastpriceOneMinAgo = tradeLogService.findLastPriceOneMinAgoByCode(transactionCode);
//		}
//		return lastpriceOneMinAgo;
//	}
	
//	/**
//	 * 查找BPI指数
//	 * 
//	 * @return
//	 */
//	public BigDecimal findBPI_BTC() {
//		BigDecimal btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.BPI);
//		if (btc_index_price == null) {
//			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(Contents.COIN_CURRENCY_BTC).getPrice();
//		}
//
//		return btc_index_price;
//	}

//	/**
//	 * 查找上证指数
//	 * 
//	 * @return
//	 */
//	public BigDecimal findBPI_SZ() {
//		BigDecimal btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.SZI);
//		if (btc_index_price == null) {
//			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(Contents.COIN_CURRENCY_SZI).getPrice();
//		}
//		return btc_index_price;
//	}
//	
//	public BigDecimal findBPI_Other() {
//		BigDecimal btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.SZI_Other);
//		if (btc_index_price == null) {
//			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(Contents.COIN_CURRENCY_SZI).getOtherprice();
//			spyMemcachedClient.set(BTCQMemecacheKeys.SZI_Other, BTCQMemecacheKeys.TIMES.ONE_HOUR, btc_index_price);
//		}
//		return btc_index_price;
//	}
	
	
	/**
	 * 根据code查找指数
	 * 
	 * @return
	 */
	public BigDecimal findBPIByCode(String code) {
		BigDecimal btc_index_price = spyMemcachedClient.get(code);
		if (btc_index_price == null) {
			VTransactionType transType = bqTransTypeService.getTransTypeByCode(code);
			if (transType == null) return btc_index_price;
			btc_index_price = bqPriceIndexCacheService.readByCode(transType.getType_code()).getPrice();
		}

		return btc_index_price;
	}
	
	public BigDecimal findBPI_Other_ByCode(String code) {
		String codeOther = code + "_other";
		BigDecimal btc_index_price = spyMemcachedClient.get(codeOther);
		if (btc_index_price == null) {
			VTransactionType transType = bqTransTypeService.getTransTypeByCode(code);
			if (transType == null) return btc_index_price;
			btc_index_price = bqPriceIndexCacheService.readByCode(transType.getType_code()).getOtherprice();
			spyMemcachedClient.set(codeOther, BTCQMemecacheKeys.TIMES.ONE_HOUR, btc_index_price);
		}
		return btc_index_price;
	}

	
	public VTransaction findCurrTransByCode(String code) {
		VTransaction bqTransaction = null;
		List<VTransaction> trans = findCurrTrans();
		for (VTransaction bqTran : trans) {
			if (bqTran.getTransaction_code().equalsIgnoreCase(code)) {
				bqTransaction = bqTran;
				break;
			}
		}
		return bqTransaction;
	}
}
