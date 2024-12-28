package com.btc100.btc_q.action.admin.delivery;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQHedgingBalanceNotice;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryHedgingRecord;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQHedgingBalanceNoticeService;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryHedgingRecordService;
import com.btc100.btc_q.service.DeliveryMemberTradeDetailService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.CountVo;

@Controller
@RequestMapping(value = "/delivery/managerArbitrage")
public class DeliveryArbitrageAction extends BaseAction {

	@Autowired
	private DeliveryTransactionService deliveryTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryArbitrageSettingService deliveryArbitrageSettingService;
	@Autowired
	private DeliveryMemberTradeDetailService deliveryMemberTradeDetailService;
	@Autowired
	private DeliveryHedgingRecordService bqHedgingRecordService;
	@Autowired
	private BQHedgingBalanceNoticeService bqHedgingBalanceNoticeService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;

	@RequestMapping(value = "/depthManager")
	public String depthManager(String code, Model mo) {
		DeliveryTransaction currTrans = null;
		
		List<DeliveryTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transList == null) {
			transList = deliveryTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(DeliveryTransaction trans :transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
				}
			}
		}
		
		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}
		
		if (currTrans == null) {
			return super.url_JSP("depthManager");
		}
		mo.addAttribute("current", currTrans);
		
		String updateTime = "";
		Long time = spyMemcachedClient.get(BTCQMemecacheKeys.D_HUOBI_DEPTH_UPDATE + currTrans.getTransaction_code());
		if (time != null) {
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(time);
			updateTime = sdf.format(date);
		}
		mo.addAttribute("updateTime", updateTime);
		
		DeliveryArbitrageSetting setting = deliveryArbitrageSettingService.getArbitrageSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new DeliveryArbitrageSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("depthManager");
	}
	
	@RequestMapping(value = "/updateDepthSetting", method = RequestMethod.POST)
	public String updateDepthSetting(DeliveryArbitrageSetting setting, Model mo) {
		updateArbitrageSetting(setting);
		return super.redirect("/delivery/managerArbitrage/depthManager.do?code=" + setting.getF_code());
	}
	
	@RequestMapping(value = "/updateScalpingSetting", method = RequestMethod.POST)
	public String updateScalpingSetting(DeliveryArbitrageSetting setting, Model mo) {
		updateArbitrageSetting(setting);
		return super.redirect("/delivery/managerArbitrage/scalpingManager.do?code=" + setting.getF_code());
	}
	
	@RequestMapping(value = "/updateHedgingSetting", method = RequestMethod.POST)
	public String updateHedgingSetting(DeliveryArbitrageSetting setting, Model mo) {
		updateArbitrageSetting(setting);
		return super.redirect("/delivery/managerArbitrage/hedgingManager.do?code=" + setting.getF_code());
	}
	
	@RequestMapping(value = "/scalpingManager")
	public String scalpingManager(String code, String startTime, String endTime, Model mo) throws ParseException {
		DeliveryTransaction currTrans = null;
		
		List<DeliveryTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transList == null) {
			transList = deliveryTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(DeliveryTransaction trans :transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
				}
			}
		}
		
		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}
		
		if (currTrans == null) {
			return super.url_JSP("scalpingManager");
		}
		mo.addAttribute("current", currTrans);
		
		DeliveryArbitrageSetting setting = deliveryArbitrageSettingService.readArbitrageSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new DeliveryArbitrageSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		
		if (!StringUtil.isEmpty(startTime)) {
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long starttime = StringUtil.isEmpty(startTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(startTime));
			long endtime = StringUtil.isEmpty(endTime) ? deliveryTransactionService.time() : DateUtil.DateToUnix(formatDate.parse(endTime));
			CountVo vo = deliveryMemberTradeDetailService.findScalpingByTime(currTrans.getTransaction_code(), starttime, endtime);
			if (vo.getAveragePrice() != null) {
				vo.setAveragePrice(vo.getAveragePrice().setScale(4, BigDecimal.ROUND_DOWN));
			}
			mo.addAttribute("startTime", startTime);
			mo.addAttribute("endTime", endTime);
			mo.addAttribute("selectSum", vo);
		}
		
		return super.url_JSP("scalpingManager");
	}
	
	
	@RequestMapping(value = "/hedgingManager")
	public String hedgingManager(String code, String startTime, String endTime, Model mo) throws ParseException {
		DeliveryTransaction currTrans = null;
		
		List<DeliveryTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transList == null) {
			transList = deliveryTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(DeliveryTransaction trans :transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
				}
			}
		}
		
		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}
		
		if (currTrans == null) {
			return super.url_JSP("hedgingManager");
		}
		mo.addAttribute("current", currTrans);
		
		DeliveryArbitrageSetting setting = deliveryArbitrageSettingService.readArbitrageSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new DeliveryArbitrageSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		mo.addAttribute("pm", bqHedgingRecordService.readPagerModel(currTrans.getTransaction_code()));
		
		
		if (!StringUtil.isEmpty(startTime)) {
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long starttime = StringUtil.isEmpty(startTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(startTime));
			long endtime = StringUtil.isEmpty(endTime) ? deliveryTransactionService.time() : DateUtil.DateToUnix(formatDate.parse(endTime));
			DeliveryHedgingRecord buyRecord = bqHedgingRecordService.readCountFailedByTime(currTrans.getTransaction_code(), starttime, endtime, 1);
			DeliveryHedgingRecord sellRecord = bqHedgingRecordService.readCountFailedByTime(currTrans.getTransaction_code(), starttime, endtime, 2);
			buyRecord.setPrice(buyRecord.getTrade_price().multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01)))));
			sellRecord.setPrice(sellRecord.getTrade_price().multiply(BigDecimal.ONE.add(setting.getHedging_buy_range().multiply(new BigDecimal(0.01)))));
			buyRecord.setPrice(buyRecord.getPrice().setScale(8, BigDecimal.ROUND_DOWN));
			sellRecord.setPrice(sellRecord.getPrice().setScale(8, BigDecimal.ROUND_UP));
			
			mo.addAttribute("startTime", startTime);
			mo.addAttribute("endTime", endTime);
			mo.addAttribute("buyRecord", buyRecord);
			mo.addAttribute("sellRecord", sellRecord);
		}
		
		BigDecimal usdtBalance = BigDecimal.ZERO;
		if ("ft".equals(currTrans.getTransaction_code())) {
			usdtBalance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.D_HEDGING_BALANCE + "usdt");
		} else if ("elf".equals(currTrans.getTransaction_code())) {
			usdtBalance = spyMemcachedClient.get("ELF_" + BTCQMemecacheKeys.D_HEDGING_BALANCE + "usdt");
		} else {
			usdtBalance = spyMemcachedClient.get(BTCQMemecacheKeys.D_HEDGING_BALANCE + "usdt");
		}
		
		if (usdtBalance == null) {
			mo.addAttribute("usdtBalance", "获取失败");
		} else {
			mo.addAttribute("usdtBalance", usdtBalance.doubleValue());
		}
		
		BigDecimal balance = spyMemcachedClient.get(BTCQMemecacheKeys.D_HEDGING_BALANCE + currTrans.getTransaction_code());
		if (balance == null) {
			mo.addAttribute("balance", "获取失败");
		} else {
			mo.addAttribute("balance", balance.doubleValue());
		}
		
		return super.url_JSP("hedgingManager");
	}

	private void updateArbitrageSetting(DeliveryArbitrageSetting setting) {
		if (setting == null) return;
		
		if (StringUtil.isEmpty(setting.getF_code()) || setting.getDepth_uid() <= 0) {
			return;
		}
		
		if (setting.getId() == null) {
			deliveryArbitrageSettingService.save(setting);
		} else {
			deliveryArbitrageSettingService.update(setting);
		}
		
	}
	
	@RequestMapping(value = "/cancelHedging", method = RequestMethod.POST)
	public void cancelHedging(HttpServletResponse response, long id) throws JSONException {
		JSONObject retObj = new JSONObject();
		if (id <= 0) {
			retObj.put("code", "error");
			retObj.put("msg", "记录ID小于 0");
			toWeb(response, retObj);
			return;
		}
		
		DeliveryHedgingRecord record = bqHedgingRecordService.getRecordById(id);
		if (record.getState() != 0) {
			retObj.put("code", "error");
			retObj.put("msg", "记录状态不对！");
			toWeb(response, retObj);
			return;
		}
		
		bqHedgingRecordService.cancelRecord(record, "管理员取消！");
		retObj.put("code", "success");
		toWeb(response, retObj);
	}
	
	@RequestMapping(value = "/hedgingBalanceNotice")
	public String hedgingCodeNotice(int type, String currencyName, Model mo) {
		
		if (StringUtil.isEmpty(type) || type < 0) {
			type = 1;
		}
		
		if (type == 1) {
			DeliveryTransaction currTrans = null;
			
			List<DeliveryTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
			if (transList == null) {
				transList = deliveryTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			}
			
			mo.addAttribute("transList", transList);
			if (!StringUtil.isEmpty(currencyName)) {
				for	(DeliveryTransaction trans :transList) {
					if (trans.getTransaction_code().equals(currencyName)) {
						currTrans = trans;
					}
				}
			}
			
			if (currTrans == null && transList.size() > 0) {
				currTrans = transList.get(0);
			}
			
			if (currTrans == null) {
				return super.url_JSP("hedgingBalanceNotice");
			}
			currencyName = currTrans.getTransaction_code();
			mo.addAttribute("current", currTrans);
		} else if (type == 2) {
			BQCurrencyType currCoin = null;
			List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
			List<BQCurrencyType> exchangeCoins = new ArrayList<BQCurrencyType>();
			for (int i = 0; i < coins.size(); i++) {
				if (!coins.get(i).getType_name().equals("BFX") && !coins.get(i).getType_name().equals("BFC")) {
					exchangeCoins.add(coins.get(i));
				}
			}
			mo.addAttribute("coins", exchangeCoins);
			if (!StringUtil.isEmpty(currencyName)) {
				for	(BQCurrencyType coin :exchangeCoins) {
					if (coin.getType_name().equalsIgnoreCase(currencyName)) {
						currCoin = coin;
					}
				}
			}
			
			if (currCoin == null && exchangeCoins.size() > 0) {
				currCoin = exchangeCoins.get(0);
			}
			
			if (currCoin == null) {
				return super.url_JSP("hedgingBalanceNotice");
			}
			currencyName = currCoin.getType_name().toUpperCase();
			mo.addAttribute("currCoin", currCoin);
		}
		
		BQHedgingBalanceNotice notice = bqHedgingBalanceNoticeService.getByTypeAndCoinName(type, currencyName);
		if (notice == null) {
			notice = new BQHedgingBalanceNotice();
			notice.setCurrency_name(currencyName);
			notice.setType(type);
		}
		
		mo.addAttribute("notice", notice);
		mo.addAttribute("pm", bqHedgingBalanceNoticeService.readPager(type, currencyName));
		
		return super.url_JSP("hedgingBalanceNotice");
	}
	
	@RequestMapping(value = "/updateHedgingBalanceNotice", method = RequestMethod.POST)
	public String updateHedgingCodeNotice(BQHedgingBalanceNotice notice, Model mo) {
		if (notice == null) return super.errorPage(mo, "添加失败");
		
		if (StringUtil.isEmpty(notice.getCurrency_name()) || StringUtil.isEmpty(notice.getEmail())
				|| StringUtil.isEmpty(notice.getThreshold()) || StringUtil.isEmpty(notice.getType())) {
			return super.errorPage(mo, "参数不能为空");
		}
		
		if (notice.getType() < 1) return super.errorPage(mo, "类型错误");
		
		if (!StringUtil.isEmpty(notice.getId()) && notice.getId() > 0) {
			bqHedgingBalanceNoticeService.update(notice);
		} else {
			bqHedgingBalanceNoticeService.save(notice);
		}
		
		return super.redirect("/delivery/managerArbitrage/hedgingBalanceNotice.do?currencyName=" + notice.getCurrency_name() + "&type=" + notice.getType());
	}
	
	@RequestMapping(value = "/deleteHedgingBalanceNotice")
	public String deleteHedgingCodeNotice(String id, Model mo) {
		long nId = Convert.toLong(id, 0);
		BQHedgingBalanceNotice notice = bqHedgingBalanceNoticeService.get(nId);
		if (nId > 0) {
			bqHedgingBalanceNoticeService.delete(nId);
		}
		
		return super.redirect("/delivery/managerArbitrage/hedgingBalanceNotice.do?type=" + notice.getType());
	}
}
