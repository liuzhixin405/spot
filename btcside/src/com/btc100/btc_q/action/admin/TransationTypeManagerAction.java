package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerTransType")
public class TransationTypeManagerAction extends BaseAction {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionTypeService transactionTypeService;
	
	@RequestMapping("/loadManagerTransType")
	public String loadManagerTransType(HttpServletRequest request, Model mo) {
		PagerModel transactionList = transactionTypeService.findAll_pm();
		mo.addAttribute("pm", transactionList);
		return super.url_JSP("transationTypeManagerlist");
	}
	
	@RequestMapping("/editManagerTransType")
	public String editManagerTransType(HttpServletRequest request, Model mo) throws Exception {
		int typeId = Convert.toInt(request.getParameter("typeId"), 0);
		BQTransactionType transType = transactionTypeService.getTransTypeById(typeId);
		if (transType == null) {
			return super.errorPage(mo, "找不到该合约类型");
		}
		
		mo.addAttribute("transType", transType);
		return super.url_JSP("transationTypeEdit");
	}
	
	@RequestMapping("/updateManagerTransType")
	public String updateManagerTransType(HttpServletRequest request, Model mo) throws Exception {
		int typeId = Convert.toInt(request.getParameter("typeId"), 0);
		String typeName = request.getParameter("typeName");
		String typeCode = request.getParameter("typeCode");
		String multiple = request.getParameter("defaultMultiple");
		String multipleState = request.getParameter("defaultMultipleState");
		String tradeCoefficient = request.getParameter("defaultTradeCoefficient");
		String indexAddress = request.getParameter("indexAddress");
		BigDecimal minFloatPrice = new BigDecimal(Convert.toDouble(request.getParameter("minFloatPrice"), 0.01));
		BigDecimal minKcNums = new BigDecimal(Convert.toDouble(request.getParameter("minKcNums"), 0.0));
		String maxKdc = request.getParameter("max_kdc_nums");
		String maxKkc = request.getParameter("max_kkc_nums");
		BigDecimal max_kdc_nums = new BigDecimal(Convert.toDouble((StringUtil.isEmpty(maxKdc) ? "0" : maxKdc), 0.0));
		BigDecimal max_kkc_nums = new BigDecimal(Convert.toDouble((StringUtil.isEmpty(maxKkc) ? "0" : maxKkc), 0.0));		
		Boolean isUsing = (Convert.toInt(request.getParameter("isUsing"), 0) == 1);
		String password = request.getParameter("password");
		
		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		
		if (typeName == null || typeName.isEmpty()) {
			return super.errorPage(mo, "缺少类型名称");
		}
		
		if (typeId <= 0 && transactionTypeService.getTransTypeByName(typeName) != null) {
			return super.errorPage(mo, "已有同名合约类型");
		}
		
		if (typeCode == null || typeCode.isEmpty()) {
			return super.errorPage(mo, "缺少类型码");
		}
		
		if (typeId <= 0 && transactionTypeService.getTransTypeByCode(typeCode) != null) {
			return super.errorPage(mo, "已有类型码");
		}
		
		BigDecimal coefficient = null;
		if (tradeCoefficient != null && !tradeCoefficient.isEmpty()) {
			coefficient = new BigDecimal(tradeCoefficient);
		}
		
		BQTransactionType transType = new BQTransactionType();
		if (typeId > 0) {
			transType.setType_id(typeId);
		}
		
		transType.setType_name(typeName);
		transType.setType_code(typeCode);
		transType.setDefault_multiple(multiple);
		transType.setDefault_multiple_state(multipleState);
		transType.setDefault_coefficient(coefficient);
		transType.setIs_using(isUsing);
		transType.setIndex_address(indexAddress);
		transType.setMin_float_price(minFloatPrice);
		transType.setMin_kc_nums(minKcNums);
		transType.setMax_kdc_nums(max_kdc_nums);
		transType.setMax_kkc_nums(max_kkc_nums);
		
		transactionTypeService.updateTransactionType(transType);
		
		List<BQTransactionType> types = transactionTypeService.findAllUsing();
		spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		
		return redirect("/managerTransType/loadManagerTransType.do");
	}

}
