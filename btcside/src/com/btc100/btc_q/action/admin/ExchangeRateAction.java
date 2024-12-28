package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.entity.BQUsdrate;
import com.btc100.btc_q.service.BQUsdrateService;

@Controller
@RequestMapping(value = "/managerExchangeRate")
public class ExchangeRateAction extends BaseAction {

	@Autowired
	private BQUsdrateService bqUsdrateService;

	/**
	 * 查询
	 * @return
	 */
	@RequestMapping(value = "/loadExchangeRate")
	public String loadExchangeRate(Model model) {
		List<BQUsdrate> lstUsdRates = bqUsdrateService.findAll();
		model.addAttribute("lstUsdRates", lstUsdRates);
		return super.url_JSP("exchangeRate");
	}

	/**
	 * 增加
	 * @param usdrate
	 * @return
	 */
	@RequestMapping(value = "/saveExchangeRate")
	public String saveExchangeRate(String ex_type, String ex_rate, String ex_use, Model model) {
		ex_type = ex_type.trim();
		ex_rate = ex_rate.trim();
		ex_use = ex_use.trim();
		if (ex_type.equals("") || ex_rate.equals("") || ex_use.equals("")) {
			return super.errorPage(model, "未填写必要参数");
		}
		try{
			BQUsdrate usdrate = new BQUsdrate();
			usdrate.setEx_type(ex_type);
			usdrate.setEx_rate(new BigDecimal(ex_rate));
			usdrate.setEx_use(Integer.parseInt(ex_use));
			usdrate.setEx_time(System.currentTimeMillis() / 1000);
			bqUsdrateService.saveUsdRate(usdrate);
		} catch (Exception e) {
			return super.errorPage(model, "填写的数值错误，请再次查看");
		}
		return super.redirect("/managerExchangeRate/loadExchangeRate.do");
	}

	/**
	 * 修改
	 * @param usdrate
	 * @return
	 */
	@RequestMapping(value = "/modifyExchangeRate")
	public String modifyExchangeRate(String id, String ex_type, String ex_rate, String ex_use, Model model) {
		id = id.trim();
		ex_type = ex_type.trim();
		ex_rate = ex_rate.trim();
		ex_use = ex_use.trim();
		
		if (id.equals("")) {
			return super.errorPage(model, "未填写主键ID");
		}

		try{
			BQUsdrate newusdrate = bqUsdrateService.findUsdRateById(Integer.parseInt(id));
			String type = ex_type.equals("") ? newusdrate.getEx_type() : ex_type;
			BigDecimal rate = ex_rate.equals("") ? newusdrate.getEx_rate() : new BigDecimal(ex_rate);
			int use = ex_use.equals("") ? newusdrate.getEx_use() : Integer.parseInt(ex_use);
			
			newusdrate.setEx_type(type);
			newusdrate.setEx_rate(rate);
			newusdrate.setEx_use(use);
			
			newusdrate.setEx_time(System.currentTimeMillis()/1000);
			bqUsdrateService.updateUsdRate(newusdrate);
		} catch (Exception e) {
			return super.errorPage(model, "填写的数值错误，请再次查看");
		}
		return super.redirect("/managerExchangeRate/loadExchangeRate.do");
	}

	/**
	 * 删除
	 * @param usdrate
	 * @return
	 */
	@RequestMapping(value = "/delExchangeRate")
	public String delExchangeRate(String id, Model model) {
		id = id.trim();
		if (id.equals("")) {
			return super.errorPage(model, "未填写主键ID");
		}
		try {
			bqUsdrateService.delUsdRate(Integer.parseInt(id));
		} catch (Exception e) {
			return super.errorPage(model, "填写的数值错误，请再次查看");
		}
		return super.redirect("/managerExchangeRate/loadExchangeRate.do");
	}

}
