package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotStatisticalForm;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQSpotArbitrageSettingService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.SpotStatisticalFormService;
import com.btc100.btc_q.vo.StatisticalFormSpotVo;
import com.btc100.btc_q.vo.StatisticalFormVo;

public class SpotTimerTaskStatisticalForm extends BaseTask {

	private static final Logger logger = Logger.getLogger(SpotTimerTaskStatisticalForm.class);

	@Autowired
	private SpotStatisticalFormService statisticalFormService;
	@Autowired
	private BQSpotArbitrageSettingService bqSpotArbitrageSettingService;
	@Autowired
	private SpotMemberTradeDetailService spotMemberTradeDetailService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;

	public void run() {
		try {

			Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			time.set(Calendar.HOUR_OF_DAY, 0);
			time.set(Calendar.MINUTE, 0);
			time.set(Calendar.SECOND, 0);
			time.set(Calendar.MILLISECOND, 0);

			long endTime = time.getTimeInMillis() / 1000;
			time.add(Calendar.DATE, -1);
			long startTime = time.getTimeInMillis() / 1000;

			StatisticalFormVo form = getStatisticsForm(startTime, endTime);
			List<StatisticalFormSpotVo> spotForms = form.getSpotFormVos();
			for (StatisticalFormSpotVo vo : spotForms) {
				SpotStatisticalForm sform = new SpotStatisticalForm();
				sform.setStatistical_time(startTime);
				sform.setTotal_trade_user(form.getTotalTradeUser());
				sform.setTotal_market_fee(new BigDecimal(form.getTotalFee()));
				sform.setTotal_bfx_fee(new BigDecimal(form.getTotalBfxFee()));
				sform.setTotal_coin_rebate(new BigDecimal(form.getTotalCoinRebate()));
				sform.setTotal_market_rebate(new BigDecimal(form.getTotalMarketRebate()));
				sform.setTotal_bfx_rebate(new BigDecimal(form.getTotalBfxRebate()));
				sform.setCode(vo.getCode());
				sform.setMarket(vo.getMarket());
				sform.setCoin(vo.getCoin());
				sform.setTrade_amount(new BigDecimal(vo.getTradeAmount()));
				sform.setTrade_user(vo.getTradeUser());
				sform.setBuy_trade_fee(new BigDecimal(vo.getBuyTradeFee()));
				sform.setSell_trade_fee(new BigDecimal(vo.getSellTradeFee()));
				sform.setBuy_trade_bfx_fee(new BigDecimal(vo.getBuyTradeBfxFee()));
				sform.setSell_trade_bfx_fee(new BigDecimal(vo.getSellTradeBfxFee()));
				sform.setCoin_rebate(new BigDecimal(vo.getCoinRebate()));
				sform.setMarket_rebate(new BigDecimal(vo.getMarketRebate()));
				sform.setBfx_rebate(new BigDecimal(vo.getBfxRebate()));

				statisticalFormService.save(sform);
			}

		} catch (Exception e) {
			logger.info("币币交易-每日统计报表异常。。。。");
			e.printStackTrace();
		}
	}

	private StatisticalFormVo getStatisticsForm(long startTime, long endTime) throws Exception {
		StatisticalFormVo form = new StatisticalFormVo();
		List<StatisticalFormSpotVo> spotFormVos = new ArrayList<StatisticalFormSpotVo>();

		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
        BQBfxFeeSetting bfxFeeSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxFeeSetting == null) {
			bfxFeeSetting = bqBfxFeeSettingService.get();
			if (bfxFeeSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxFeeSetting);
			}
		}

		List<Integer> users = new ArrayList<Integer>();
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			String code = coinPair.getCoin_pair_name();

			BQSpotArbitrageSetting setting = bqSpotArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new BQSpotArbitrageSetting();
				setting.setDepth_uid(0L);
			}

			StatisticalFormSpotVo spotForm = new StatisticalFormSpotVo();
			spotForm.setCode(code);
			spotForm.setMarket(coinPair.getMarket_coin_name());
			spotForm.setCoin(coinPair.getCoin_name());
			spotForm.setTradeUser(
					spotTradeLogService.getTradeUserCount(code, startTime, endTime, setting.getDepth_uid()));
			spotForm.setTradeAmount(spotMemberTradeDetailService
					.getInTimeNoScalpingAmount(code, startTime, endTime, setting.getDepth_uid()).doubleValue());
			spotForm.setBuyTradeFee(spotTradeLogService
					.getFeeInTimeNoScalping(code, Contents.TRADE_TYPE_BUY, startTime, endTime, setting.getDepth_uid())
					.doubleValue());
			spotForm.setSellTradeFee(spotTradeLogService
					.getFeeInTimeNoScalping(code, Contents.TRADE_TYPE_SELL, startTime, endTime, setting.getDepth_uid())
					.doubleValue());
			spotForm.setBuyTradeBfxFee(spotTradeLogService
					.getBfxFeeNoScalping(code, Contents.TRADE_TYPE_BUY, startTime, endTime, setting.getDepth_uid())
					.doubleValue());
			spotForm.setSellTradeBfxFee(spotTradeLogService
					.getBfxFeeNoScalping(code, Contents.TRADE_TYPE_SELL, startTime, endTime, setting.getDepth_uid())
					.doubleValue());
			spotForm.setCoinRebate(bqMemberInviteRebateLogService.getSumRebateByTimeAndUser("", code, startTime, endTime, 
					0, coinPair.getCoin_id()).doubleValue());
			spotForm.setMarketRebate(bqMemberInviteRebateLogService.getSumRebateByTimeAndUser("", code, startTime, endTime, 
					0, coinPair.getMarket_coin_id()).doubleValue());
			spotForm.setMarketRebate(bqMemberInviteRebateLogService.getSumRebateByTimeAndUser("", code, startTime, endTime, 
					0, coinPair.getMarket_coin_id()).doubleValue());

			if (bfxFeeSetting != null && bfxFeeSetting.getCoin_id() > 0) {
				spotForm.setBfxRebate(bqMemberInviteRebateLogService.getSumRebateByTimeAndUser("", code, startTime, endTime, 
						0, bfxFeeSetting.getCoin_id()).doubleValue());
			}
			
			spotFormVos.add(spotForm);

			List<Integer> ids = spotTradeLogService.getTradeUserIds(code, startTime, endTime, setting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : users) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}

				if (!have) {
					users.add(id);
				}
			}
			
			form.setTotalCoinRebate(form.getTotalCoinRebate() + spotForm.getCoinRebate());
			form.setTotalMarketRebate(form.getTotalMarketRebate() + spotForm.getMarketRebate());
			form.setTotalBfxRebate(form.getTotalBfxRebate() + spotForm.getBfxRebate());
			form.setTotalBfxFee(spotForm.getBuyTradeBfxFee() + spotForm.getSellTradeBfxFee() + form.getTotalBfxFee());
			form.setTotalFee(form.getTotalFee() + spotForm.getSellTradeFee());
		}

		form.setTotalTradeUser(users.size());
		form.setSpotFormVos(spotFormVos);

		return form;
	}

}
