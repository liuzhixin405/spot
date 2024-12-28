package com.btc100.btc_q.action.admin.delivery;

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
import com.btc100.btc_q.entity.BQCompositeIndexType;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQTransactionIntroduce;
import com.btc100.btc_q.entity.DeliveryAutoCreateSetting;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.entity.DeliveryTransactionPartition;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.BQCompositeIndexTypeService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQTransactionIntroduceService;
import com.btc100.btc_q.service.DeliveryAutoCreateSettingService;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.service.DeliveryTransactionPartitionService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/delivery/managerTrans")
public class DeliveryTransationManagerAction extends BaseAction {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryTransactionTypeService transactionTypeService;
	@Autowired
	private DeliveryTransactionPartitionService transactionPartitionService;
	@Autowired
	private BQTransactionIntroduceService transactionIntroduceService;
	@Autowired
	private BQCompositeIndexTypeService bqCompositeIndexTypeService;
	@Autowired
	private DeliveryAutoCreateSettingService deliveryAutoCreateSettingService;
	@Autowired
	private DeliveryTransactionFeeRateService bqTransactionFeeRateService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	
	@RequestMapping("/loadTransPartition")
	public String loadTransPartition(HttpServletRequest request, Model mo) {
		
		DeliveryTransactionPartition partition = null;
		
		String id = request.getParameter("id");
		if (!StringUtil.isEmpty(id)) {
			int lid = Convert.toInt(id, 0);
			if (lid > 0) {
				partition = transactionPartitionService.get(lid);
			}
		}
		
		if (partition == null) {
			partition = new DeliveryTransactionPartition();
		}
		
		mo.addAttribute("partition", partition);
		
		PagerModel transactionList = transactionPartitionService.readPager();
		mo.addAttribute("pm", transactionList);
		
		return super.url_JSP("transPartitionlist");
	}
	
	@RequestMapping("/updateTransPartition")
	public String updateTransPartition(DeliveryTransactionPartition partition, Model mo) throws Exception {
		transactionPartitionService.update(partition);
		
		List<DeliveryTransactionPartition> partitions = transactionPartitionService.readAll();
		spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_PARTITIONS,BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		
		return redirect("/delivery/managerTrans/loadTransPartition.do");
	}
	
	@RequestMapping("/deleteTransPartition")
	public String deleteTransPartition(String id, Model mo) throws Exception {
		if (!StringUtil.isEmpty(id)) {
			int lid = Convert.toInt(id, 0);
			if (lid > 0) {
				transactionPartitionService.delete(lid);
			}
		}
		
		List<DeliveryTransactionPartition> partitions = transactionPartitionService.readAll();
		spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_PARTITIONS,BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		
		return redirect("/delivery/managerTrans/loadTransPartition.do?id=" + id);
	}

	@RequestMapping("/loadTrans")
	public String loadTrans(HttpServletRequest request, Model mo) {
		String bqCode = request.getParameter("bqCode");
		String state = request.getParameter("state");
		int istate = ConvertUtil.toInt(state, 0);
		PagerModel transactionList = transactionService.findTransByState_pm(bqCode, istate);
		mo.addAttribute("pm", transactionList);
		
		mo.addAttribute("state", state);
		mo.addAttribute("bqCode", bqCode);
		if (!StringUtil.isEmpty(bqCode)) {
			DeliveryTransaction trans = transactionService.readTransByCode(bqCode);
			mo.addAttribute("state", trans.getTransaction_state());
			mo.addAttribute("multiple", trans.getMultiple());
			mo.addAttribute("multipleState", trans.getMultiple_state());
			mo.addAttribute("trade_coefficient", trans.getTrade_coefficient());
			
			mo.addAttribute("suspend", trans.getSuspend());
			mo.addAttribute("transferIn", trans.getTransfer_in_enable());
			mo.addAttribute("transferOut", trans.getTransfer_out_enable());
			mo.addAttribute("kind", trans.getTransaction_kind());
			mo.addAttribute("showName", trans.getTransaction_show_name());
			mo.addAttribute("enName", trans.getTransaction_show_name_en());
			mo.addAttribute("jpName", trans.getTransaction_show_name_jp());
			mo.addAttribute("krName", trans.getTransaction_show_name_kr());
			mo.addAttribute("hkName", trans.getTransaction_show_name_hk());
		}
		
		return super.url_JSP("transationlist");
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
			return redirect("/delivery/managerTrans/loadTrans.do");
		}
		
		bqCode = bqCode.trim();
		DeliveryTransaction trans = transactionService.readTransByCode(bqCode);
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
		
		transactionService.updateMultipleByCode(bqCode, multiple, multipleState);
		
		return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping("/pauseTrans")
	public String pauseTrans(HttpServletRequest request, Model mo, boolean suspend, boolean transferIn, boolean transferOut) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/delivery/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		DeliveryTransaction trans = transactionService.readTransByCode(bqCode);
		if (trans == null) {
			return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}
		
		transactionService.changePauseAndTransfer(bqCode, suspend, transferIn, transferOut);
		return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
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
			return redirect("/delivery/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		DeliveryTransaction trans = transactionService.findTransToUpdateByCode(bqCode);
		if (trans == null || StringUtil.isEmpty(endTime)) {
			return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long endtime = DateUtil.DateToUnix(formatDate.parse(endTime));

		trans.setTransaction_end_time(endtime);
		transactionService.update(trans);

		return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping("/updateKind")
	public String updateKind(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String kind = request.getParameter("kind");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/delivery/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		DeliveryTransaction trans = transactionService.findTransToUpdateByCode(bqCode);
		if (trans == null || StringUtil.isEmpty(kind)) {
			return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}

		int tranKind = ConvertUtil.toInt(kind, -1); // 合约类别：0-虚拟货币，1-股指
		if (tranKind < 0) {
			return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}

		trans.setTransaction_kind(tranKind);
		transactionService.update(trans);

		return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping("/updateShowName")
	public String updateShowName(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String showName = request.getParameter("showName");
		String enName = request.getParameter("enName");
		String jpName = request.getParameter("jpName");
		String krName = request.getParameter("krName");
		String hkName = request.getParameter("hkName");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/delivery/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		DeliveryTransaction trans = transactionService.findTransToUpdateByCode(bqCode);
		if (trans == null || StringUtil.isEmpty(showName)) {
			return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}

		trans.setTransaction_show_name(showName);
		trans.setTransaction_show_name_en(enName);
		trans.setTransaction_show_name_jp(jpName);
		trans.setTransaction_show_name_kr(krName);
		trans.setTransaction_show_name_hk(hkName);;
		transactionService.update(trans);

		return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping("/closeTradeTrans")
	public String closeTradeTrans(HttpServletRequest request, Model mo) throws Exception {
		String bqCode = request.getParameter("bqCode");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (bqCode == null || bqCode.isEmpty()) {
			return redirect("/delivery/managerTrans/loadTrans.do");
		}
		
		DeliveryTransaction trans = transactionService.findTransToUpdateByCode(bqCode);
		if (trans == null || trans.getTransaction_state() == 3) {
			return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}
		
		trans.setTransaction_state(3);
		transactionService.update(trans);
		
		return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
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
			return redirect("/delivery/managerTrans/loadTrans.do");
		}
		bqCode = bqCode.trim();
		DeliveryTransaction trans = transactionService.readTransByCode(bqCode);
		if (trans == null || tradeCoefficient == null || tradeCoefficient.isEmpty()) {
			return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
		}
		tradeCoefficient = tradeCoefficient.trim();

		double dtradeCoefficient = ConvertUtil.toDouble(tradeCoefficient, 0);
		if (dtradeCoefficient > 0) {
			transactionService.addCoefficientByCode(bqCode, new BigDecimal(dtradeCoefficient));
		}

		return redirect("/delivery/managerTrans/loadTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping("/loadManagerTransType")
	public String loadManagerTransType(HttpServletRequest request, Model mo) {
		PagerModel transactionList = transactionTypeService.findAll_pm();
		mo.addAttribute("pm", transactionList);
		return super.url_JSP("transationTypeManagerlist");
	}
	
	@RequestMapping("/editManagerTransType")
	public String editManagerTransType(HttpServletRequest request, Model mo) throws Exception {
		int typeId = Convert.toInt(request.getParameter("typeId"), 0);
		DeliveryTransactionType transType = transactionTypeService.getTransTypeById(typeId);
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
		
		DeliveryTransactionType transType = new DeliveryTransactionType();
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
		
		List<DeliveryTransactionType> types = transactionTypeService.findAllUsing();
		spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		
		return redirect("/delivery/managerTrans/loadManagerTransType.do");
	}

	@RequestMapping("/loadManagerTrans")
	public String loadManagerTrans(HttpServletRequest request, Model mo) {
		mo.addAttribute("state", 1);
		mo.addAttribute("multiple", "1,5,10,20,50");
		mo.addAttribute("multipleState", "1,1,1,1,1");
		
		List<DeliveryTransactionType> typeList = transactionTypeService.findAllUsing();
		mo.addAttribute("typeList", typeList);

		PagerModel transactionList = transactionService.findTransByState_pm("", 0);
		mo.addAttribute("pm", transactionList);

		return super.url_JSP("transationManagerlist");
	}

	@RequestMapping("/addManagerTrans")
	public String addManagerTrans(HttpServletRequest request, Model mo) throws Exception {
		
		String showName = request.getParameter("showName");
		String state = request.getParameter("state");
		String multiple = request.getParameter("multiple");
		String multipleState = request.getParameter("multipleState");
		String tradeCoefficient = request.getParameter("tradeCoefficient");
		String kind = request.getParameter("kind");
		String deliveryType = request.getParameter("deliveryType");// 交割类型：日合约，季度合约，次季合约
		String password = request.getParameter("password");
		String tranType = request.getParameter("tranType");
		String endTime = request.getParameter("endTime");
		String enName = request.getParameter("enName");
		String jpName = request.getParameter("jpName");
		String krName = request.getParameter("krName");
		String hkName = request.getParameter("hkName");
		int itranType = ConvertUtil.toInt(tranType, 0); // 合约类型type_id
		if (itranType <= 0) {
			return super.errorPage(mo, "请选择合约类型!");
		}
		if (StringUtil.isEmpty(deliveryType)) {
			return super.errorPage(mo, "请选择交割类型!");
		}
		
		DeliveryTransactionType transType = transactionTypeService.getTransTypeById(itranType);
		if (transType == null) {
			return super.errorPage(mo, "合约类型不存在!");
		}
		
		int tranKind = ConvertUtil.toInt(kind, -1); // 合约类别：0-虚拟货币，1-股指，2-试验区，3-综合指数
		if (tranKind < 0) {
			return super.errorPage(mo, "请选择合约类别!");
		}
		if (tranKind == 3) {
			BQCompositeIndexType type = bqCompositeIndexTypeService.getByCode(transType.getType_code());
			if (type == null) {
				return super.errorPage(mo, "添加综合指数合约错误!请检查合约号是否正确");
			}
		}
		
		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		
		if (StringUtil.isEmpty(endTime)) {
			return super.errorPage(mo, "请输入交割时间");
		}
		
		String bqCode = transType.getType_code().toLowerCase();
		
		long endtime = 0l;
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		endtime = DateUtil.DateToUnix(formatDate.parse(endTime));
		long now = System.currentTimeMillis() / 1000;
		if (endtime <= now) {
			return super.errorPage(mo, "交割时间错误");
		}
		
//		String dateStr = DateUtil.dateToStr(endtime, "yyyyMMdd");
//		bqCode = bqCode + "_" + dateStr;
//		if (ideliveryType == 1) { // 日交割（合约号格式：btc_20191227d/btc_20200101d）
//			bqCode = bqCode + "d";
//		} else if (ideliveryType == 2) { // 周交割（合约号格式：btc_20191227w/btc_20200103w）
//			bqCode = bqCode + "w";
//		} else if (ideliveryType == 3) { // 次周交割（合约号格式：btc_20191227w/btc_20200103w）
//			bqCode = bqCode + "nw";
//		} else if (ideliveryType == 4) { // 月交割（合约号格式：btc_20191227m/btc_20200131m）
//			bqCode = bqCode + "m";
//		} else if (ideliveryType == 5) { // 次月交割（合约号格式：btc_20191227nm/btc_20200131nm）
//			bqCode = bqCode + "nm";
//		} else if (ideliveryType == 6) { // 季度交割（合约号格式：btc_20191227q/btc_20200327q）
//			bqCode = bqCode + "q";
//		} else if (ideliveryType == 7) { // 次季度（合约号格式：btc_20200327nq/btc_20200626nq）
//			bqCode = bqCode + "nq";
//		} else if (ideliveryType == 8) { // 半年（btc_20200626hy）
//			bqCode = bqCode + "hy";
//		} else if (ideliveryType == 9) { // 年（btc_20201225y）
//			bqCode = bqCode + "y";
//		}
		
		int istate = ConvertUtil.toInt(state, 0);

		if (istate != 1 && istate != 2 && istate != 3) {
			return super.errorPage(mo, "合约状态错误，1:正常 2:清算中 3:关闭");
		}

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

		DeliveryTransaction trans = transactionService.readTransByCode(bqCode);
		if (trans != null) {
			return super.errorPage(mo, "此合约已存在");
		}
		
		transactionService.addMultipleByCode(bqCode, deliveryType, istate, multiple, multipleState, btradeCoefficient,
				endtime, itranType, tranKind, showName, enName, jpName, krName, hkName);
		return redirect("/delivery/managerTrans/loadManagerTrans.do?bqCode=" + bqCode);
	}
	
	@RequestMapping(value = "/loadTransIntroduce")
	public String loadTransIntroduce(Model mo) {
		PagerModel pm = transactionIntroduceService.findAllpage(Contents.MARKET_TYPE_DELIVERY);
		mo.addAttribute("pm", pm);

		return super.url_JSP("transactionIntroduceList");
	}
	
	@RequestMapping(value = "/loadAddIntroduce")
	public String loadAddIndexDesc(String id, Model mo) {
		Long tId = ConvertUtil.toLong(id, 0l);
		if (tId > 0) {
			BQTransactionIntroduce introduce = transactionIntroduceService.getById(tId);
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
			BQTransactionIntroduce introduce = transactionIntroduceService.getById(tId);
			introduce.setEn_introduce(enDesc);
			introduce.setZh_introduce(zhDesc);
			introduce.setJa_introduce(jaDesc);
			introduce.setKo_introduce(koDesc);
			introduce.setHk_introduce(hkDesc);
			transactionIntroduceService.update(introduce);
		} else {
			BQTransactionIntroduce introduce = transactionIntroduceService.readByCode(code, Contents.MARKET_TYPE_DELIVERY);
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
			introduce.setType(3);
			transactionIntroduceService.add(introduce);
		}

		return redirect("/delivery/managerTrans/loadTransIntroduce.do");
	}

	@RequestMapping("/deleteTransIntroduce")
	public String deleteTransIntroduce(Model mo, String id){
		Long tId = ConvertUtil.toLong(id, 0l);
		BQTransactionIntroduce introduce = transactionIntroduceService.getById(tId);
		if (introduce == null) return super.errorPage(mo, "该记录不存在");
		
		transactionIntroduceService.delete(introduce);
		return redirect("/delivery/managerTrans/loadTransIntroduce.do");
	}
	
	@RequestMapping("/transort")
	public String transort(HttpServletRequest request, Model mo) {
		List<DeliveryTransaction> transactionList = super.findCurrTrans();
		mo.addAttribute("currtrans", transactionList);
		
		return super.url_JSP("transort");
	}
	
	@RequestMapping("/transortsave")
	public void transortsave(HttpServletRequest request, HttpServletResponse response) {
		List<DeliveryTransaction> transactionList = super.findCurrTrans();
		toWeb(response, transactionService.updateTrans(transactionList, request));
	}
	
	@RequestMapping(value = "/autoCreateSetting")
	public String autoCreateSetting(String deliveryType, Model mo) {
		List<DeliveryTransactionType> transTypeList = transactionTypeService.findAllUsing();
		if (transTypeList.size() == 0) {
			return super.url_JSP("deliveryAutoCreateSetting");
		}
		mo.addAttribute("transTypeList", transTypeList);
		
		if (StringUtil.isEmpty(deliveryType)) {
			deliveryType = "d";
		}
		DeliveryAutoCreateSetting setting = deliveryAutoCreateSettingService.getByType(deliveryType);
		if (setting == null) {
			setting = new DeliveryAutoCreateSetting();
			setting.setDelivery_type("d");
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("deliveryAutoCreateSetting");
	}
	
	@RequestMapping(value = "/updateCreateSetting", method = RequestMethod.POST)
	public String updateSetting(DeliveryAutoCreateSetting setting, Model mo) {
		if (StringUtil.isEmpty(setting.getMultiple()) || StringUtil.isEmpty(setting.getMultiple_state())
				|| StringUtil.isEmpty(setting.getEnd_time()) || StringUtil.isEmpty(setting.getDelivery_type())
				|| setting.getTransaction_kind() < 0 || setting.getTransaction_type_id() <= 0) {
			return super.errorPage(mo, "请填写完整");
		}
		
		String multiple = setting.getMultiple();
		String multipleState = setting.getMultiple_state();
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
		
		String deliveryType = setting.getDelivery_type();// 交割类型：日合约，季度合约，次季合约
		if (!"d".equals(deliveryType)) {
			return super.errorPage(mo, "交割类型错误");
		}
		
		int itranType = setting.getTransaction_type_id();
		DeliveryTransactionType transType = transactionTypeService.getTransTypeById(itranType);
		if (transType == null) {
			return super.errorPage(mo, "合约类型不存在!");
		}
		
		int tranKind = setting.getTransaction_kind(); // 合约类别：0-虚拟货币，1-股指，2-试验区，3-综合指数
		if (tranKind < 0) {
			return super.errorPage(mo, "请选择合约类别!");
		}
		if (tranKind == 3) {
			BQCompositeIndexType type = bqCompositeIndexTypeService.getByCode(transType.getType_code());
			if (type == null) {
				return super.errorPage(mo, "添加综合指数合约错误!请检查合约号是否正确");
			}
		}
		
		String endTimeStr = setting.getEnd_time();
	 	String[] endTimeStrArr = endTimeStr.split(":");
		if (endTimeStrArr.length != 2) {
			return super.errorPage(mo, "时间格式为16:00");
		}
		int hour = Integer.valueOf(endTimeStrArr[0]);
		int minute = Integer.valueOf(endTimeStrArr[1]);
		if (hour < 0 || hour > 24 || minute < 0 || minute > 60) {
			return super.errorPage(mo, "时间格式为16:00");
		}
		
		if (setting.getId() == null) {
			deliveryAutoCreateSettingService.save(setting);
		} else {
			deliveryAutoCreateSettingService.update(setting);
		}
		return super.redirect("/delivery/managerTrans/autoCreateSetting.do?deliveryType=" + setting.getDelivery_type());
	}
	
	@RequestMapping("/feeRateList")
	public String feeRateList(String code, String msg, Model mo) {
		DeliveryTransaction currTrans = null;
		List<DeliveryTransaction> transList = findCurrTrans();
		mo.addAttribute("lstTrans", transList);
		if (!StringUtil.isEmpty(code)) {
			for (DeliveryTransaction trans : transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
					break;
				}
			}
		}

		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}

		if (currTrans == null) {
			PagerModel pm = new PagerModel();
			mo.addAttribute("pm", pm);
			return super.url_JSP("transFeeRateList");
		}

		code = currTrans.getTransaction_code();
		mo.addAttribute("current", code);
		
		PagerModel pm = bqTransactionFeeRateService.readByCode(code);
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("transFeeRateList");
	}
	
	@RequestMapping("/feeRateAddOrUpdate")
	public String feeRateAddOrUpdate(String code, String id, Model mo) {
		DeliveryTransactionFeeRate rate = null;
		long recordId = Convert.toLong(id, 0);
		if (recordId > 0) {
			rate = bqTransactionFeeRateService.readById(recordId);
		}
		if (rate != null) {
			mo.addAttribute("rate", rate);
			return super.url_JSP("transFeeRateEdit");
		}
		
		DeliveryTransaction currTrans = null;
		List<DeliveryTransaction> transList = findCurrTrans();
		mo.addAttribute("lstTrans", transList);
		if (!StringUtil.isEmpty(code)) {
			for (DeliveryTransaction trans : transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
				}
			}
		}

		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}

		if (currTrans == null) {
			return super.redirect("/delivery/managerTrans/feeRateList.do");
		}
		mo.addAttribute("current", currTrans.getTransaction_code());
		
		List<BQMemberLevel> levels = bqMemberLevelService.findAll(false);
		mo.addAttribute("levels", levels);

		return super.url_JSP("transFeeRateEdit");
	}
	
	@RequestMapping(value = "/feeRateAdd", method = RequestMethod.POST)
	public String feeRateAdd(HttpServletRequest request, Model mo) {
		String id = request.getParameter("id");
		String code = request.getParameter("f_code");
		String level_id = request.getParameter("level_id");
		String eatRate = request.getParameter("eat_commission_rate");
		String pendingRate = request.getParameter("pending_commission_rate");
		
		if (StringUtil.isEmpty(code) || StringUtil.isEmpty(eatRate) || StringUtil.isEmpty(pendingRate)) {
			return super.errorPage(mo, "缺少参数");
		}
		
		double dpendingRate = ConvertUtil.toDouble(pendingRate, 0);
		double deatRate = ConvertUtil.toDouble(eatRate, 0);
		
		long recordId = Convert.toLong(id, 0);
		if (recordId > 0) {
			DeliveryTransactionFeeRate rate = bqTransactionFeeRateService.readById(recordId);
			if (rate != null) {
				rate.setPending_commission_rate(new BigDecimal(dpendingRate));
				rate.setEat_commission_rate(new BigDecimal(deatRate));
				bqTransactionFeeRateService.update(rate);
			}
		} else {
			long levelId = Convert.toLong(level_id, 0);
			BQMemberLevel level = bqMemberLevelService.findByLevelId(levelId);
			if (level == null) {
				return super.errorPage(mo, "没有该用户等级");
			}
			
			DeliveryTransactionFeeRate feeRate = bqTransactionFeeRateService.readByCodeAndLevelNoCache(code, levelId);
			if (feeRate != null) {
				return super.errorPage(mo, "已有该合约等级费率设置，请到列表查找编辑");
			}
			
			DeliveryTransactionFeeRate rate = new DeliveryTransactionFeeRate();
			rate.setF_code(code);
			rate.setLevel_id(levelId);
			rate.setLevel_name(level.getLevel_name());
			rate.setPending_commission_rate(new BigDecimal(dpendingRate));
			rate.setEat_commission_rate(new BigDecimal(deatRate));
			bqTransactionFeeRateService.save(rate);
		}
		
		return super.redirect("/delivery/managerTrans/feeRateList.do?code=" + code);
	}
	
	@RequestMapping(value = "/feeRateDelete")
	public String feeRateDelete(String id, Model mo) {
		long recordId = Convert.toLong(id, 0);
		if (recordId > 0) {
			DeliveryTransactionFeeRate rate = bqTransactionFeeRateService.readById(recordId);
			if (rate != null) {
				bqTransactionFeeRateService.delete(rate);
			}
		}
		
		return super.redirect("/delivery/managerTrans/feeRateList.do");
	}
	
}
