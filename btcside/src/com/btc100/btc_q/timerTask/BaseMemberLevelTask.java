package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQBfxMinHold;
import com.btc100.btc_q.entity.BQBfxVipSetting;
import com.btc100.btc_q.entity.BQMarketOrderSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQBfxMinHoldService;
import com.btc100.btc_q.service.BQBfxVipSettingService;
import com.btc100.btc_q.service.BQMarketOrderSettingService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.Top5DelegationVO;

/**
 * 持bfx享对应vip待遇
 * @author Administrator
 *
 */
public class BaseMemberLevelTask {
	
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQBfxMinHoldService bqBfxMinHoldService;
	@Autowired
	private BQBfxVipSettingService bqBfxVipSettingService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMarketOrderSettingService bqMarketOrderSettingService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationService dPendingDelegationService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	@Autowired
	private NPendingDelegationService nPendingDelegationService;
	
	protected BQMemberLevel getLevelByUserBfx(BQMember user) {
		if (user == null) return null;
		
		BQMemberLevel userlevel = bqMemberLevelService.findByLevelId(user.getF_user_level_id());
		if (userlevel.getEat_commission_rate().compareTo(BigDecimal.ZERO) == 0) {
			return userlevel;
		}
		
		// 代理系统中的用户不享有持有bfx所享受的vip等级
		if (user.getF_user_oms() != 0) {
			return userlevel;
		}
		
		BQBfxFeeSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (setting == null) {
			setting = bqBfxFeeSettingService.get();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting == null) {
			return userlevel;
		}
			
		long dayTime = TimeUtil.getTimesmorning(System.currentTimeMillis() - 24 * 3600 * 1000);
		if (setting.getVip_state() != 1) {
			return userlevel;
		}
		
		BQBfxMinHold minHold = bqBfxMinHoldService.getBfxMinHoldByUserAndTime(user.getF_user_id(), dayTime);
		if (minHold != null && minHold.getMin_hold().compareTo(BigDecimal.ZERO) > 0) {
			List<BQBfxVipSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_VIP_SETTINGS);
			if (settings == null) {
				settings = bqBfxVipSettingService.findAll();
				if (settings != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.BFX_VIP_SETTINGS, BTCQMemecacheKeys.TIMES.ONE_MIN, settings);
				}
			}
			
			BQBfxVipSetting vipSetting = null;
			for (BQBfxVipSetting vipSet : settings) {
				if (minHold.getMin_hold().compareTo(vipSet.getHold_count()) >= 0) {
					vipSetting = vipSet;
					break;
				}
			}
			
			if (vipSetting != null) {
				if (vipSetting.getLevel_id() > userlevel.getLevel_id()) {
					BQMemberLevel level =  bqMemberLevelService.findByLevelId(vipSetting.getLevel_id());
					if (level != null) {
						return level;
					}
				}
			}
		}
		
		return userlevel;
	}
	
	protected BQMarketOrderSetting getMarketOrderSetting() {
		BQMarketOrderSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARKET_ORDER_SETTING);
		if (setting == null) {
			setting = bqMarketOrderSettingService.get();
			if(setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.MARKET_ORDER_SETTING, BTCQMemecacheKeys.TIMES.ONE_DAY, setting);
			}
		}
		return setting;
	}
	
	protected BigDecimal getMarketOrderPrice(String type, String code, int direction) {
		BQMarketOrderSetting setting = getMarketOrderSetting();
		if (setting != null && setting.isRunning()) {
			// 根据买卖方向， 获取买一卖一价，计算价格
			if (direction == 1) { // 买多或者平空，挂单价 = 卖一价 * （1 + price_range）
				String key = code + BTCQMemecacheKeys.SELLTOP5LIST;
				if ("delivery".equals(type)) {
					key = code + BTCQMemecacheKeys.D_SELLTOP5LIST;
				} else if ("vcontract".equals(type)) {
					key = code + BTCQMemecacheKeys.V_SELLTOP5LIST;
				} else if (type == null || "".equals(type) || "contract".equals(type)) {
					
				} else {
					key = type + "_" + code + BTCQMemecacheKeys.SELLTOP5LIST;
				}
				
				List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(key);
				if (selltop5List == null) {
					if ("contract".equals(type)) {
						selltop5List = pendingDelegationService.getSellTop5(code);
					} else if ("delivery".equals(type)) {
						selltop5List = dPendingDelegationService.getSellTop5(code);
					} else if ("vcontract".equals(type)) {
						selltop5List = vPendingDelegationService.getSellTop5(code);
					} else {
						NContractPartition part = nContractPartitionService.readByPartition(type);
						if (part != null) {
							selltop5List = nPendingDelegationService.getSellTop5(type, code);
						}
					}
				}
				
				BigDecimal sell1Price = BigDecimal.ZERO;
				if (selltop5List != null && selltop5List.size() > 0) {
					sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
				}
				if (sell1Price.compareTo(BigDecimal.ZERO) > 0) {
					sell1Price = sell1Price.multiply(BigDecimal.ONE.add(setting.getPrice_range()));
					return sell1Price;
				}
			} else if (direction == 2) { // 卖空或者平多，挂单价 = 买一价 * （1 - price_range）
				String key = code + BTCQMemecacheKeys.BUYTOP5LIST;
				if ("delivery".equals(type)) {
					key = code + BTCQMemecacheKeys.D_BUYTOP5LIST;
				} else if ("vcontract".equals(type)) {
					key = code + BTCQMemecacheKeys.V_BUYTOP5LIST;
				 }else if (type == null || "".equals(type) || "contract".equals(type)) {
					
				} else {
					key = type + "_" + code + BTCQMemecacheKeys.SELLTOP5LIST;
				}
				List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(key);
				if (buytop5List == null) {
					if ("contract".equals(type)) {
						buytop5List = pendingDelegationService.getBuyTop5(code);
					} else if ("delivery".equals(type)) {
						buytop5List = dPendingDelegationService.getBuyTop5(code);
					} else if ("vcontract".equals(type)) {
						buytop5List = vPendingDelegationService.getBuyTop5(code);
					} else {
						NContractPartition part = nContractPartitionService.readByPartition(type);
						if (part != null) {
							buytop5List = nPendingDelegationService.getBuyTop5(type, code);
						}
					}
				}
				BigDecimal buy1Price = BigDecimal.ZERO;
				if (buytop5List != null && buytop5List.size() > 0) {
					buy1Price = buytop5List.get(0).getPrice();
				}
				if (buy1Price.compareTo(BigDecimal.ZERO) > 0) {
					buy1Price = buy1Price.multiply(BigDecimal.ONE.subtract(setting.getPrice_range()));
					return buy1Price;
				}
			}
			
		}
		return BigDecimal.ZERO;
	}

}
