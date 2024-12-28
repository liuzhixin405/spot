package com.btc100.btc_q.action.admin.delivery;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.service.DeliveryTransactionRateService;

@Controller
@RequestMapping(value = "/delivery/managerFloatRate")
public class DeliveryFloatPriceRateAction extends BaseAction {
	
	@Autowired
	private DeliveryTransactionRateService bqTransactionRateService;

	/**
	 * 查询
	 * @return
	 */
	@RequestMapping(value = "/loadFloatRate")
	public String loadFloatRate(Model model) {
		List<DeliveryTransactionRate> lstTransRate = bqTransactionRateService.readAll();
		model.addAttribute("lstTransRate", lstTransRate);
		return super.url_JSP("floatRate");
	}

	/**
	 * 增加
	 * @param usdrate
	 * @return
	 */
	@RequestMapping(value = "/saveFloatRate")
	public String saveFloatRate(String bqCode, String uprate, String downrate, Model model) {

		bqCode = bqCode.trim();
		uprate = uprate.trim();
		downrate = downrate.trim();

		if (bqCode.equals("") || uprate.equals("") || downrate.equals("")) {
			return super.errorPage(model, "未填写必要参数");
		}
		
		DeliveryTransactionRate bqTransRate = new DeliveryTransactionRate();
		bqTransRate.setUprate(new BigDecimal(uprate));
		bqTransRate.setDownrate(new BigDecimal(downrate));
		bqTransRate.setTransaction_code(bqCode);
		bqTransactionRateService.saveBqTransRate(bqTransRate);
		
		return super.redirect("/delivery/managerFloatRate/loadFloatRate.do");
	}

	/**
	 * 修改
	 * @param usdrate
	 * @return
	 */
	@RequestMapping(value = "/modifyFloatRate")
	public String modifyFloatRate(String id, String bqCode, String uprate, String downrate, Model model) {
		
		id = id.trim();
		bqCode = bqCode.trim();
		uprate = uprate.trim();
		downrate = downrate.trim();
		
		if (id.equals("")) {
			return super.errorPage(model, "未填写主键ID");
		}
		
		try{
			DeliveryTransactionRate bqTransRate = bqTransactionRateService.readTransRateById(Convert.toLong(id, 0));
			bqCode = bqCode.equals("") ? bqTransRate.getTransaction_code() : bqCode;
			BigDecimal uprate_big = uprate.equals("") ? bqTransRate.getUprate() : new BigDecimal(uprate);
			BigDecimal downrate_big = downrate.equals("") ? bqTransRate.getDownrate() : new BigDecimal(downrate);
			
			bqTransRate.setTransaction_code(bqCode);
			bqTransRate.setUprate(uprate_big);
			bqTransRate.setDownrate(downrate_big);

			bqTransactionRateService.updateBqTransRate(bqTransRate);
		} catch (Exception e) {
			return super.errorPage(model, "填写的数值错误，请再次查看");
		}
		return super.redirect("/delivery/managerFloatRate/loadFloatRate.do");
	}

	/**
	 * 删除
	 * @param usdrate
	 * @return
	 */
	@RequestMapping(value = "/delFloatRate")
	public String delFloatRate(String id, Model model) {
		id = id.trim();
		
		if (id.equals("")) {
			return super.errorPage(model, "未填写主键ID");
		}
		try {
			bqTransactionRateService.delBqTransRate(Convert.toLong(id, 0));
		} catch (Exception e) {
			return super.errorPage(model, "填写的数值错误，请再次查看");
		}
		return super.redirect("/delivery/managerFloatRate/loadFloatRate.do");
	}
}
