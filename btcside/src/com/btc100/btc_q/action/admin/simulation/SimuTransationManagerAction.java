package com.btc100.btc_q.action.admin.simulation;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQTransactionIntroduce;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.BQTransactionIntroduceService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/simulation/managerTrans")
public class SimuTransationManagerAction extends BaseAction {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VTransactionTypeService vTransactionTypeService;
	@Autowired
	private BQTransactionIntroduceService bqTransactionIntroduceService;
	
	@RequestMapping("/loadManagerTransType")
	public String loadManagerTransType(HttpServletRequest request, Model mo) {
		PagerModel transactionList = vTransactionTypeService.findAll_pm();
		mo.addAttribute("pm", transactionList);
		return super.url_JSP("transationTypeManagerlist");
	}
	
	@RequestMapping("/editManagerTransType")
	public String editManagerTransType(HttpServletRequest request, Model mo) throws Exception {
		int typeId = Convert.toInt(request.getParameter("typeId"), 0);
		VTransactionType transType = vTransactionTypeService.getTransTypeById(typeId);
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
		Boolean isUsing = (Convert.toInt(request.getParameter("isUsing"), 0) == 1);
		String password = request.getParameter("password");
		
		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		
		if (typeName == null || typeName.isEmpty()) {
			return super.errorPage(mo, "缺少类型名称");
		}
		
		if (typeId <= 0 && vTransactionTypeService.getTransTypeByName(typeName) != null) {
			return super.errorPage(mo, "已有同名合约类型");
		}
		
		if (typeCode == null || typeCode.isEmpty()) {
			return super.errorPage(mo, "缺少类型码");
		}
		
		if (typeId <= 0 && vTransactionTypeService.getTransTypeByCode(typeCode) != null) {
			return super.errorPage(mo, "已有类型码");
		}
		
		BigDecimal coefficient = null;
		if (tradeCoefficient != null && !tradeCoefficient.isEmpty()) {
			coefficient = new BigDecimal(tradeCoefficient);
		}
		
		VTransactionType transType = new VTransactionType();
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
		
		vTransactionTypeService.updateTransactionType(transType);
		
		List<VTransactionType> types = vTransactionTypeService.readAllUsing();
		spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		
		return redirect("/simulation/managerTrans/loadManagerTransType.do");
	}

	@RequestMapping("/loadTrans")
	public String loadTrans(HttpServletRequest request, Model mo) {
		String bqCode = request.getParameter("bqCode");
		String state = request.getParameter("state");
		int istate = ConvertUtil.toInt(state, 0);
		PagerModel transactionList = vTransactionService.findTransByState_pm(bqCode, istate);
		mo.addAttribute("pm", transactionList);
		
		mo.addAttribute("state", state);
		mo.addAttribute("bqCode", bqCode);
		if (!StringUtil.isEmpty(bqCode)) {
			VTransaction trans = vTransactionService.readTransByCode(bqCode);
			mo.addAttribute("state", trans.getTransaction_state());
			mo.addAttribute("multiple", trans.getMultiple());
			mo.addAttribute("multipleState", trans.getMultiple_state());
			mo.addAttribute("trade_coefficient", trans.getTrade_coefficient());
			
			mo.addAttribute("suspend", trans.getSuspend());
			mo.addAttribute("transferIn", trans.getTransfer_in_enable());
			mo.addAttribute("transferOut", trans.getTransfer_out_enable());
		}
		

		return super.url_JSP("/transationlist");
	}
	
	@RequestMapping("/addTrans")
	public String addTrans(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String multiple = request.getParameter("multiple");
		String multipleState = request.getParameter("multipleState");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		VTransaction trans = vTransactionService.readTransByCode(bqCode);
		if (trans == null || multiple == null || multiple.isEmpty() || multipleState == null || multipleState.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}
		multiple = multiple.trim();
		multipleState = multipleState.trim();
		vTransactionService.addMultipleByCode(bqCode, multiple, trans.getMultiple(), multipleState, trans.getMultiple_state());

		return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping("/updateTrans")
	public String updateTrans(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String multiple = request.getParameter("multiple");
		String multipleState = request.getParameter("multipleState");
		String password = request.getParameter("password");
		
		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		
		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		VTransaction trans = vTransactionService.readTransByCode(bqCode);
		if (trans == null || multiple == null || multiple.isEmpty() || multipleState == null || multipleState.isEmpty()) {
			return super.errorPage(mo, "杠杆或者杠杆状态不能为空");
		}
		multiple = multiple.trim();
		multipleState = multipleState.trim();
		String[] multiples = multiple.split(",");
		String[] multipleStates = multipleState.split(",");
		if (multiples.length != multipleStates.length) {
			return super.errorPage(mo, "杠杆和杠杆状态无法一一对应");
		}
		for (int i = 0; i < multipleStates.length; i++) {
			if (!multipleStates[i].equals("0") && !multipleStates[i].equals("1") && !multipleStates[i].equals("2")) {
				return super.errorPage(mo, "杠杆状态只支持0,1,2");
			}
		}
		
		vTransactionService.updateMultipleByCode(bqCode, multiple, multipleState);
		
		return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping("/pauseTrans")
	public String pauseTrans(HttpServletRequest request, Model mo, boolean suspend, boolean transferIn, boolean transferOut) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		VTransaction trans = vTransactionService.readTransByCode(bqCode);
		if (trans == null) {
			return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}
		
		vTransactionService.changePauseAndTransfer(bqCode, suspend, transferIn, transferOut);
		return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}

	@RequestMapping("/delTrans")
	public String delTrans(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String multiple = request.getParameter("multiple");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		VTransaction trans = vTransactionService.readTransByCode(bqCode);
		if (trans == null || multiple == null || multiple.isEmpty() || trans.getMultiple() == null || trans.getMultiple().isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}
		multiple = multiple.trim();
		if (!trans.getMultiple().contains(multiple)) {
			return super.errorPage(mo, "你输入的杠杆不在该合约的杠杆内！");
		}
		vTransactionService.delMultipleByCode(bqCode, multiple, trans.getMultiple(), trans.getMultiple_state());

		return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}

	@RequestMapping("/updateEndtime")
	public String updateEndtime(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String endTime = request.getParameter("endTime");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		VTransaction trans = vTransactionService.findTransToUpdateByCode(bqCode);
		if (trans == null || StringUtil.isEmpty(endTime)) {
			return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long endtime = DateUtil.DateToUnix(formatDate.parse(endTime));

		trans.setTransaction_end_time(endtime);
		vTransactionService.update(trans);

		return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping("/closeTradeTrans")
	public String closeTradeTrans(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do");
		}
		
		VTransaction trans = vTransactionService.findTransToUpdateByCode(bqCode);
		if (trans == null || trans.getTransaction_state() == 3) {
			return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}
		
		trans.setTransaction_state(3);
		vTransactionService.update(trans);
		
		return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}

	@RequestMapping("/addTradeTrans")
	public String addTradeTrans(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String tradeCoefficient = request.getParameter("tradeCoefficient");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		VTransaction trans = vTransactionService.readTransByCode(bqCode);
		if (trans == null || tradeCoefficient == null || tradeCoefficient.isEmpty()) {
			return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}
		tradeCoefficient = tradeCoefficient.trim();

		double dtradeCoefficient = ConvertUtil.toDouble(tradeCoefficient, 0);
		if (dtradeCoefficient > 0) {
			vTransactionService.addCoefficientByCode(bqCode, new BigDecimal(dtradeCoefficient));
		}

		return redirect("/simulation/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}

	@RequestMapping("/loadManagerTrans")
	public String loadManagerTrans(HttpServletRequest request, Model mo) {
		mo.addAttribute("state", 1);
		mo.addAttribute("multiple", "1,5,10,20,50");
		mo.addAttribute("multipleState", "1,1,1,1,1");
		
		List<VTransactionType> typeList = vTransactionTypeService.readAllUsing();
		mo.addAttribute("typeList", typeList);

		PagerModel transactionList = vTransactionService.findTransByState_pm("", 0);
		mo.addAttribute("pm", transactionList);

		return super.url_JSP("transationManagerlist");
	}

	@RequestMapping("/addManagerTrans")
	public String addManagerTrans(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String currency = request.getParameter("currencyTypeId");
		String state = request.getParameter("state");
		String multiple = request.getParameter("multiple");
		String multipleState = request.getParameter("multipleState");
		String tradeCoefficient = request.getParameter("tradeCoefficient");
		//String endTime = request.getParameter("endTime");
		String password = request.getParameter("password");
		String tranType = request.getParameter("tranType");
		int itranType = ConvertUtil.toInt(tranType, 0); // 合约类型type_id
		if (itranType <= 0) {
			return super.errorPage(mo, "请选择合约类型!");
		}
		
		VTransactionType transType = vTransactionTypeService.getTransTypeById(itranType);
		if (transType == null) {
			return super.errorPage(mo, "合约类型不存在!");
		}
		
		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

//		if (TextUtils.isEmpty(endTime)) {
//			return super.errorPage(mo, "交割时间不正确");
//		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/simulation/managerTrans/loadManagerTrans.do");
		}

		bqCode = bqCode.toLowerCase();

		int istate = ConvertUtil.toInt(state, 0);

		if (istate != 1 && istate != 2 && istate != 3) {
			return super.errorPage(mo, "合约状态错误，1:正常 2:清算中 3:关闭");
		}
		
		int currencyId = ConvertUtil.toInt(currency, Contents.TRADE_CURRENCY_TYPE_ID);

		// 杠杆，杠杆状态
		String[] strMults = multiple.split(",");
		String[] strMultStats = multipleState.split(",");

		if (strMults.length <= 0 || strMults.length != strMultStats.length) {
			return super.errorPage(mo, "杠杆和杠杆状态不匹配");
		}

		for (String str : strMults) { // 判断杠杆是否是正整数
			if (ConvertUtil.toInt(str, 0) <= 0) {
				return super.errorPage(mo, "杠杆错误，杠杆为数字");
			}
		}
		for (String str : strMultStats) {
			int imultipleState = ConvertUtil.toInt(str, -1);
			if (imultipleState < 0 || (imultipleState != 0 && imultipleState != 1 && imultipleState != 2)) {
				return super.errorPage(mo, "杠杆状态错误，杠杆状态0不显示,1显示,2禁用");
			}
		}

		double dtradeCoefficient = ConvertUtil.toDouble(tradeCoefficient, 0);
		if (dtradeCoefficient <= 0) {
			return super.errorPage(mo, "杠杆系数错误，应为非负数字");
		}
		BigDecimal btradeCoefficient = new BigDecimal(dtradeCoefficient);

		VTransaction trans = vTransactionService.readTransByCode(bqCode);
		if (trans != null) {
			return super.errorPage(mo, "此合约已存在");
		}

		vTransactionService.addMultipleByCode(bqCode, istate, currencyId, multiple, multipleState, btradeCoefficient, 0, itranType);
		return redirect("/simulation/managerTrans/loadManagerTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping(value = "/loadTransIntroduce")
	public String loadTransIntroduce(Model mo) {
		PagerModel pm = bqTransactionIntroduceService.findAllpage(Contents.MARKET_TYPE_VCONTRACT);
		mo.addAttribute("pm", pm);

		return super.url_JSP("transactionIntroduceList");
	}
	
	@RequestMapping(value = "/loadAddIntroduce")
	public String loadAddIndexDesc(String id, Model mo) {
		Long tId = ConvertUtil.toLong(id, 0l);
		if (tId > 0) {
			BQTransactionIntroduce introduce = bqTransactionIntroduceService.getById(tId);
			mo.addAttribute("introduce", introduce);
		}
		mo.addAttribute("transList", super.findCurrTrans());
		return super.url_JSP("transactionIntroDetails");
	}

	@RequestMapping(value = "/addTransIntroduce", method = RequestMethod.POST)
	public String addIndexDesc(Model mo, String id, String code, String zhDesc, String enDesc, String jaDesc, String koDesc, String hkDesc) {

		if (StringUtil.isEmpty(code)) {
			return super.errorPage(mo, "合约号不能为空");
		}
		
		if (StringUtil.isEmpty(zhDesc)) {
			return super.errorPage(mo, "中文介绍不能为空");
		}
		
		if (StringUtil.isEmpty(enDesc)) {
			return super.errorPage(mo, "英文介绍不能为空");
		}
		
		if (StringUtil.isEmpty(jaDesc)) {
			return super.errorPage(mo, "日文介绍不能为空");
		}
		
		if (StringUtil.isEmpty(koDesc)) {
			return super.errorPage(mo, "韩文介绍不能为空");
		}
		
		if (StringUtil.isEmpty(hkDesc)) {
			return super.errorPage(mo, "繁体介绍不能为空");
		}
		
		int tId = ConvertUtil.toInt(id, 0);
		if (tId > 0) {
			BQTransactionIntroduce introduce = bqTransactionIntroduceService.getById(tId);
			introduce.setEn_introduce(enDesc);
			introduce.setZh_introduce(zhDesc);
			introduce.setJa_introduce(jaDesc);
			introduce.setKo_introduce(koDesc);
			introduce.setHk_introduce(hkDesc);
			bqTransactionIntroduceService.update(introduce);
		} else {
			BQTransactionIntroduce introduce = bqTransactionIntroduceService.readByCode(code, 2);
			if (null != introduce) {
				return super.errorPage(mo, "该合约介绍已存在，请进行编辑");
			}
			introduce = new BQTransactionIntroduce();
			introduce.setEn_introduce(enDesc);
			introduce.setZh_introduce(zhDesc);
			introduce.setJa_introduce(jaDesc);
			introduce.setKo_introduce(koDesc);
			introduce.setHk_introduce(hkDesc);
			introduce.setTransaction_code(code);
			introduce.setType(2);
			bqTransactionIntroduceService.add(introduce);
		}

		return redirect("/simulation/managerTrans/loadTransIntroduce.do");
	}
	
	@RequestMapping("/deleteTransIntroduce")
	public String deleteTransIntroduce(Model mo, String id){
		Long tId = ConvertUtil.toLong(id, 0l);
		BQTransactionIntroduce introduce = bqTransactionIntroduceService.getById(tId);
		if (introduce == null) return super.errorPage(mo, "该记录不存在");
		
		bqTransactionIntroduceService.delete(introduce);
		return redirect("/simulation/managerTrans/loadTransIntroduce.do");
	}
	
	@RequestMapping("/transort")
	public String transort(HttpServletRequest request, Model mo) {
		List<VTransaction> transactionList = super.findCurrTrans();
		mo.addAttribute("currtrans", transactionList);
		
		
		return super.url_JSP("transort");
	}
	
	
	@RequestMapping("/transortsave")
	public void transortsave(HttpServletRequest request, HttpServletResponse response) {
		List<VTransaction> transactionList = super.findCurrTrans();
		toWeb(response, vTransactionService.updateTrans(transactionList, request));
	}
	
}
