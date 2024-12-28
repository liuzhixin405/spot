package com.btc100.btc_q.action.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerSpotTrades")
public class SpotTradesAction extends BaseAction {

	@Autowired
	private BQSpotMarketCoinPairService coinPairService;
	@Autowired
	private SpotMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private SpotPendingDelegationService pendingDelegationService;
	@Autowired
	private SpotTradeLogService tradeLogService;
	
	@RequestMapping("/loadTradeLogs") 
	public String loadDetail(int id, String code, Model model) throws Exception {
		if (id > 0) {
			BQSpotMarketCoinPair coinPair = null;
			if (!StringUtil.isEmpty(code) ) {
				coinPair = coinPairService.readByName(code);
			}
			
			if (coinPair == null) {
				return super.url_JSP("tradeLogList");
			}
			
			PagerModel tradeLogList = new PagerModel();
			tradeLogList = tradeLogService.findByUserIdAndTransaction(coinPair.getCoin_pair_name(), id, 0);
			
			model.addAttribute("currentTransaction", coinPair);
			model.addAttribute("pm", tradeLogList);
		}
		
		model.addAttribute("id", id);
		model.addAttribute("code", code);
		return super.url_JSP("tradeLogList");
	}

	@RequestMapping("/loadTrades")
	public String loadTrades(Model mo) {
		List<BQSpotMarketCoinPair> coinPairs = coinPairService.readByState_new(Contents.TRANSACTION_STATE_ZC);
		if (coinPairs.size() > 0) {
			String coinPairName = coinPairs.get(0).getCoin_pair_name();
			mo.addAttribute("coinPairName", coinPairName);
			mo.addAttribute("pm", memberTradeDetailService.findModel(coinPairName));
			mo.addAttribute("db", pendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_BUY)); // 买单量
			mo.addAttribute("kb", pendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_SELL)); // 卖单量
			
		} else {
			PagerModel pm = new PagerModel();
			pm.setDatas(null);
			pm.setTotal(0);
			mo.addAttribute("pm", pm);
		}
		mo.addAttribute("lstTrans", coinPairs);
		mo.addAttribute("current", 1);
		mo.addAttribute("url","managerSpotTrades");
		return super.url_JSP("spotTradeLists");
	}

	@RequestMapping("/selectTrades")
	public String selectTrades(String coinPairName, Model mo) {
		BQSpotMarketCoinPair coinPair = coinPairService.readByName(coinPairName);
		if (coinPair == null) {
			return super.redirect("/managerSpotTrades/loadTrades.do");
		}
		List<BQSpotMarketCoinPair> coinPairs = coinPairService.readByState_new(Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("lstTrans", coinPairs);
		int icurrent=0;
		for (int i = 0; i < coinPairs.size(); i++) {
			String hy = coinPairs.get(i).getCoin_pair_name();
			if (coinPairName.equals(hy)) {
				icurrent=i+1;
			}
		}
		mo.addAttribute("current", icurrent);
		mo.addAttribute("coinPairName", coinPairName);
		mo.addAttribute("pm", memberTradeDetailService.findModel(coinPairName));
		mo.addAttribute("db", pendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_BUY)); // 买单量
		mo.addAttribute("kb", pendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_SELL)); // 卖单量
		mo.addAttribute("url","managerSpotTrades");

		return super.url_JSP("spotTradeLists");
	}

	@RequestMapping("/userTrades")
	public String userTrades(String coinPairName, String buyuser, String selluser, Model mo) {
		Integer buyuserid = ConvertUtil.toInt(buyuser, 0);
		Integer selluserid = ConvertUtil.toInt(selluser, 0);
		if (buyuserid > 0)
			mo.addAttribute("buyuser", buyuserid);
		if (selluserid > 0)
			mo.addAttribute("selluser", selluserid);
		
		BQSpotMarketCoinPair coinPair = coinPairService.readByName(coinPairName);
		if (coinPair == null) {
			return super.redirect("/managerSpotTrades/loadTrades.do");
		}
		
		List<BQSpotMarketCoinPair> coinPairs = coinPairService.readByState_new(Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("lstTrans", coinPairs);
		int icurrent=0;
		for (int i = 0; i < coinPairs.size(); i++) {
			String hy = coinPairs.get(i).getCoin_pair_name();
			if (coinPairName.equals(hy)) {
				icurrent=i+1;
			}
		}

		mo.addAttribute("current", icurrent);
		mo.addAttribute("coinPairName", coinPairName);
		mo.addAttribute("pm", memberTradeDetailService.findModel(coinPairName, buyuserid, selluserid));
		double itotal = memberTradeDetailService.findTotal(coinPairName, buyuserid, selluserid);
		mo.addAttribute("total", itotal);
		mo.addAttribute("db", pendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_BUY)); // 买单量
		mo.addAttribute("kb", pendingDelegationService.getSumByAction(coinPairName, Contents.DIRECTION_SELL)); // 卖单量
		mo.addAttribute("url","managerSpotTrades");

		return super.url_JSP("spotTradeLists");
	}
}
