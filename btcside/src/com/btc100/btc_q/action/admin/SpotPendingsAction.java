package com.btc100.btc_q.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.btc100.btc_q.entity.BQUserTradeWhite;
import com.btc100.btc_q.service.BQUserTradeWhiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerSpotPending")
public class SpotPendingsAction extends BaseAction {

	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	@Autowired
	private BQUserTradeWhiteService bqUserTradeWhiteService;

	@RequestMapping("/loadBuy")
	public String loadBuyPending(Model mo) {
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();
		if (coinPairs != null && coinPairs.size() > 0) {

			BQSpotMarketCoinPair coinPair = coinPairs.get(0);
			PagerModel pm = spotPendingDelegationService.getPagerModel(Contents.DIRECTION_BUY, 0l, null, coinPair.getCoin_pair_name());// (currTran.getTransaction_code());

			mo.addAttribute("currtran", coinPair.getCoin_pair_name());
			mo.addAttribute("trans", coinPairs);
			mo.addAttribute("pm", pm);

			mo.addAttribute("dk", spotPendingDelegationService.getSumByAction(coinPair.getCoin_pair_name(), Contents.DIRECTION_BUY)); // 买总量
			mo.addAttribute("kp", spotPendingDelegationService.getSumByAction(coinPair.getCoin_pair_name(), Contents.DIRECTION_SELL)); // 卖总量

		}

		mo.addAttribute("page", "买入");
		mo.addAttribute("url", "selectBuy");
		mo.addAttribute("mapping", "managerSpotPending");
		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);
		return super.url_JSP("spotPendingList");
	}

	@RequestMapping("/selectBuy")
	public String selectBuyPending(String coinPairName, String userid, String useremail, HttpServletRequest request, Model mo) {
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();

		BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(coinPairName);

		if (coinPair != null) {
			long iuserID = ConvertUtil.toLong(userid, 0l);
			if (iuserID == 0) {
				BQMember bqMember = super.findUsersByEmail(useremail);
				if (bqMember != null)
					iuserID = bqMember.getF_user_id();
			}

			PagerModel pm = spotPendingDelegationService.getPagerModel(Contents.DIRECTION_BUY, iuserID, useremail, coinPair.getCoin_pair_name());
			mo.addAttribute("currtran", coinPair.getCoin_pair_name());
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);

			mo.addAttribute("dk", spotPendingDelegationService.getSumByUIDAction(coinPair.getCoin_pair_name(), iuserID, Contents.DIRECTION_BUY)); // 买总量
			mo.addAttribute("kp", spotPendingDelegationService.getSumByUIDAction(coinPair.getCoin_pair_name(), iuserID, Contents.DIRECTION_SELL)); // 卖总量

		} else {
			PagerModel pm = new PagerModel();
			pm.setTotal(0);
			mo.addAttribute("currtran", coinPairName);
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);
		}

		mo.addAttribute("trans", coinPairs);
		mo.addAttribute("page", "买入");
		mo.addAttribute("url", "selectBuy");
		mo.addAttribute("mapping", "managerSpotPending");
		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);

		return super.url_JSP("spotPendingList");
	}

	@RequestMapping("/loadSell")
	public String loadSellPending(Model mo) {
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();
		if (coinPairs != null && coinPairs.size() > 0) {

			BQSpotMarketCoinPair coinPair = coinPairs.get(0);
			PagerModel pm = spotPendingDelegationService.getPagerModel(Contents.DIRECTION_SELL, 0l, null, coinPair.getCoin_pair_name());

			mo.addAttribute("currtran", coinPair.getCoin_pair_name());
			mo.addAttribute("trans", coinPairs);
			mo.addAttribute("pm", pm);

			mo.addAttribute("dk", spotPendingDelegationService.getSumByAction(coinPair.getCoin_pair_name(), Contents.DIRECTION_BUY)); // 买总量
			mo.addAttribute("kp", spotPendingDelegationService.getSumByAction(coinPair.getCoin_pair_name(), Contents.DIRECTION_SELL)); // 卖总量

		}
		mo.addAttribute("page", "卖出");
		mo.addAttribute("url", "selectSell");
		mo.addAttribute("mapping", "managerSpotPending");
		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);

		return super.url_JSP("spotPendingList");
	}

	@RequestMapping("/selectSell")
	public String selectSellPending(String coinPairName, String userid, String useremail, HttpServletRequest request, Model mo) {
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();
		BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(coinPairName);

		if (coinPair != null) {
			long iuserID = ConvertUtil.toLong(userid, 0l);
			if (iuserID == 0) {
				BQMember bqMember = super.findUsersByEmail(useremail);
				if (bqMember != null)
					iuserID = bqMember.getF_user_id();
			}

			PagerModel pm = spotPendingDelegationService.getPagerModel(Contents.DIRECTION_SELL, iuserID, useremail, coinPair.getCoin_pair_name());
			mo.addAttribute("currtran", coinPair.getCoin_pair_name());
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);

			mo.addAttribute("dk", spotPendingDelegationService.getSumByUIDAction(coinPair.getCoin_pair_name(), iuserID, Contents.DIRECTION_BUY)); // 买总量
			mo.addAttribute("kp", spotPendingDelegationService.getSumByUIDAction(coinPair.getCoin_pair_name(), iuserID, Contents.DIRECTION_SELL)); // 卖总量

		} else {
			PagerModel pm = new PagerModel();
			pm.setTotal(0);
			mo.addAttribute("currtran", coinPairName);
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);
		}

		mo.addAttribute("trans", coinPairs);
		mo.addAttribute("page", "卖出");
		mo.addAttribute("url", "selectSell");
		mo.addAttribute("mapping", "managerSpotPending");
		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);

		return super.url_JSP("spotPendingList");
	}

}
