package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.btc100.btc_q.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.Top5DelegationVO;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 合约 API（市场深度）
 * 
 * @author Administrator
 * 
 */
public class SpotTimerTaskDepthJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	
	private static String[] marketArray = null;

	public void run() {
		
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null || marketArray.length == 0) return;
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
		for (String market : marketArray) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(market)) {
					coinPair = marketCoinPair;
					break;
				}
			}
			
			if (coinPair == null) continue;
			
			String bqCode = coinPair.getCoin_pair_name();
			
			BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + bqCode);
			boolean needAddAmount = false;
			if (setting != null && setting.getDepth_self() && setting.getDepth_uid() > 0 && setting.getPrice_digits() >= 0 && setting.getAmount_digits() >= 0) {
				needAddAmount = true;
			}
			
			List<Top5DelegationVO> buytop5List = new LinkedList<>();
			List<Top5DelegationVO> selltop5List = new LinkedList<>();
			if (needAddAmount) {
				buytop5List = spotPendingDelegationService.getBuyTop5(bqCode, setting.getDepth_uid());
				selltop5List = spotPendingDelegationService.getSellTop5(bqCode, setting.getDepth_uid());
				
				
				List<Top5DelegationVO> otherBuyList = spotPendingDelegationService.getBuyTop5ExcludeUid(bqCode, setting.getDepth_uid());
				List<Top5DelegationVO> otherSellList = spotPendingDelegationService.getSellTop5ExcludeUid(bqCode, setting.getDepth_uid());
				
				BigDecimal random = BigDecimal.ZERO;
				BigDecimal addAmount = BigDecimal.ZERO;
				for (Top5DelegationVO vo : buytop5List) {
					random = new BigDecimal(Math.random());
					BigDecimal buyAmt = setting.getDepth_buy_amount_other_min().add(random.multiply(setting.getDepth_buy_amount_other_max().subtract(setting.getDepth_buy_amount_other_min()))).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
	
					if (setting.isDepth_increasing()) {
						addAmount = addAmount.add(buyAmt).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
					} else {
						addAmount = buyAmt;
					}
					
					vo.setCountNum(vo.getCountNum().add(addAmount));
					
				}
				
				addAmount = BigDecimal.ZERO;
				for (Top5DelegationVO vo : selltop5List) {
					random = new BigDecimal(Math.random());
					BigDecimal sellAmt = setting.getDepth_sell_amount_other_min().add(random.multiply(setting.getDepth_sell_amount_other_max().subtract(setting.getDepth_sell_amount_other_min()))).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
	
					if (setting.isDepth_increasing()) {
						addAmount = addAmount.add(sellAmt).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
					} else {
						addAmount = sellAmt;
					}
					
					vo.setCountNum(vo.getCountNum().add(addAmount));
				}
				
				int index = 0;
				while(otherBuyList.size() > 0) {
					Top5DelegationVO ohterVo = otherBuyList.get(0);
					boolean have = false;
					for (int i = index; i < buytop5List.size(); i++) {
						Top5DelegationVO vo = buytop5List.get(i);
						if (vo.getPrice().compareTo(ohterVo.getPrice()) <= 0) {
							otherBuyList.remove(0);
							if (vo.getPrice().compareTo(ohterVo.getPrice()) == 0) {
								vo.setCountNum(ohterVo.getCountNum().add(vo.getCountNum()));
							} else {
								buytop5List.add(i, ohterVo);
							}
							index = i + 1;
							have = true;
							break;
						}
					}
					
					if (!have) {
						buytop5List.addAll(otherBuyList);
						otherBuyList.clear();
					}
				}
				
				index = 0;
				while(otherSellList.size() > 0) {
					Top5DelegationVO ohterVo = otherSellList.get(0);
					boolean have = false;
					for (int i = index; i < selltop5List.size(); i++) {
						Top5DelegationVO vo = selltop5List.get(i);
						if (vo.getPrice().compareTo(ohterVo.getPrice()) >= 0) {
							otherSellList.remove(0);
							if (vo.getPrice().compareTo(ohterVo.getPrice()) == 0) {
								vo.setCountNum(ohterVo.getCountNum().add(vo.getCountNum()));
							} else {
								selltop5List.add(i, ohterVo);
							}
							index = i + 1;
							have = true;
							break;
						}
					}
					
					if (!have) {
						selltop5List.addAll(otherSellList);
						otherSellList.clear();
					}
				}
				
				while (buytop5List.size() > 20) {
					buytop5List.remove(buytop5List.size() - 1);
				}
				
				if (selltop5List.size() > 20) {
					selltop5List.remove(selltop5List.size() - 1);
				}
				
				BigDecimal sumAmount = BigDecimal.ZERO;
				for (Top5DelegationVO vo : buytop5List) {
					sumAmount = sumAmount.add(vo.getCountNum());
					vo.setSumNum(sumAmount);
				}
				
				sumAmount = BigDecimal.ZERO;
				for (Top5DelegationVO vo : selltop5List) {
					sumAmount = sumAmount.add(vo.getCountNum());
					vo.setSumNum(sumAmount);
				}
				
				Collections.sort(selltop5List);
			} else {
				buytop5List = spotPendingDelegationService.getBuyTop5(bqCode);
				selltop5List = spotPendingDelegationService.getSellTop5(bqCode);
			}

			spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.BUYTOP5LIST, BTCQMemecacheKeys.TIMES.BUYTOP5LIST, buytop5List);
			spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.SELLTOP5LIST, BTCQMemecacheKeys.TIMES.SELLTOP5LIST, selltop5List);
			
			List<Object[]> lstBuyObjs = new ArrayList<Object[]>();
			for (Top5DelegationVO vo : buytop5List) {
				Object[] objs = new Object[3];
				objs[0] = vo.getPrice();
				objs[1] = vo.getCountNum();
				objs[2] = vo.getPriority();
				lstBuyObjs.add(objs);
			}
			
			List<Object[]> lstSellObjs = new ArrayList<Object[]>();
			for (Top5DelegationVO vo : selltop5List) {
				Object[] objs = new Object[3];
				objs[0] = vo.getPrice();
				objs[1] = vo.getCountNum();
				objs[2] = vo.getPriority();
				lstSellObjs.add(objs);
			}
			
			String sell = "asks", buy = "bids";
			JSONObject json = new JSONObject();

			try {
				List<JSONArray> lstBuyArray2 = new ArrayList<JSONArray>();
				for (Object[] obj : lstBuyObjs) {
					JSONArray array = new JSONArray();
					BigDecimal price = new BigDecimal(obj[0].toString());
					BigDecimal amount = new BigDecimal(obj[1].toString());

					array.put(price);
					array.put(amount);
					array.put(ConvertUtil.toInt(obj[2], 0));
					lstBuyArray2.add(array);
				}

				Collections.reverse(lstSellObjs);
				List<JSONArray> lstSellArray2 = new ArrayList<JSONArray>();
				for (Object[] obj : lstSellObjs) {
					JSONArray array = new JSONArray();
					BigDecimal price = new BigDecimal(obj[0].toString());
					BigDecimal amount = new BigDecimal(obj[1].toString());

					array.put(price);
					array.put(amount);
					array.put(ConvertUtil.toInt(obj[2], 0));
					lstSellArray2.add(array);
				}
				json.put(buy, lstBuyArray2);
				json.put(sell, lstSellArray2);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			String depthData = json.toString();
			spyMemcachedClient.set(BTCQMemecacheKeys.DEPTH_CODE + bqCode.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, depthData);
		}

	}
}
