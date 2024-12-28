package com.btc100.btc_q.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.btc100.btc_q.vo.AjaxCheckVO;

public class BTCQMath {

//	public static BigDecimal SZPointToBTC = new BigDecimal(0.0001).setScale(6, RoundingMode.HALF_UP);

	/**
	 * 盈亏额
	 * 
	 * @param direction
	 *            持仓方向
	 * @param total
	 *            总价值
	 * @param lastPrice
	 *            平仓或最新价
	 * @param avgPrice
	 *            开仓均价
	 * @return 盈亏额
	 */
	public static BigDecimal YK(int direction, BigDecimal coefficient, BigDecimal lastPrice, BigDecimal avgPrice, BigDecimal totalHoldCount) {
		if (direction == Contents.DIRECTION_BUY)
			return lastPrice.subtract(avgPrice).multiply(totalHoldCount).divide(coefficient, 8, RoundingMode.HALF_UP);
		// return
		// total.divide(avgPrice,8,RoundingMode.HALF_UP).subtract(total.divide(lastPrice,8,RoundingMode.HALF_UP)).setScale(8,RoundingMode.HALF_UP);
		else
			return avgPrice.subtract(lastPrice).multiply(totalHoldCount).divide(coefficient, 8, RoundingMode.HALF_UP);
		// return
		// total.divide(lastPrice,8,RoundingMode.HALF_UP).subtract(total.divide(avgPrice,8,RoundingMode.HALF_UP)).setScale(8,RoundingMode.HALF_UP);
	}

	/**
	 * 爆仓价
	 * 
	 * @param direction
	 *            持仓方向
	 * @param rate
	 *            佣金比例
	 * @param avgPrice
	 *            开仓均价
	 * @param deposit
	 *            保证金
	 * @param amount
	 *            持仓量
	 * @return 爆仓价
	 */
	public static BigDecimal BCJ(int direction, BigDecimal avgPrice, BigDecimal deposit, BigDecimal rate, BigDecimal amount, BigDecimal coefficient) {
		if (direction == Contents.DIRECTION_BUY) {
			// (avgPrice - deposit / (amount * coefficient)) / (1 - rate)
			return avgPrice.subtract(deposit.divide(amount.multiply(coefficient), 8, RoundingMode.UP)).divide((new BigDecimal(1)).subtract(rate), 8, RoundingMode.UP);
		} else {
			// (avgPrice + deposit / (amount * coefficient)) / (1 + rate)
			return avgPrice.add(deposit.divide(amount.multiply(coefficient), 8, RoundingMode.UP)).divide((new BigDecimal(1)).add(rate), 8, RoundingMode.DOWN);
		}
	}
	
	/**
	 * 强平价
	 * 
	 * @param direction	持仓方向
	 * @param avgPrice 开仓均价
	 * @param deposit 保证金
	 * @param multiple 杠杆
	 * @param amount 持仓量
	 * @return 强平价
	 */
	public static BigDecimal QPJ(int direction, BigDecimal avgPrice, BigDecimal deposit, BigDecimal multiple, BigDecimal amount, BigDecimal coefficient, BigDecimal posDeposit) {
		if (amount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
		
		if (direction == Contents.DIRECTION_BUY) {
			// avg_price - deposit * (1 - multiple / 100) / (amount * coefficient)   ---- old
			// avg_price - (deposit - 0.2 * posDeposit) / (amount * coefficient)   ---- new
			return avgPrice.subtract(deposit.subtract(new BigDecimal(0.2).multiply(posDeposit)).divide(amount.multiply(coefficient), 8, RoundingMode.HALF_DOWN));
		} else {
			// avg_price + deposit * (1 - multiple / 100) / (amount * coefficient)
			// avg_price + (deposit - 0.2 * posDeposit) / (amount * coefficient)   ---- new
			return avgPrice.add(deposit.subtract(new BigDecimal(0.2).multiply(posDeposit)).divide(amount.multiply(coefficient), 8, RoundingMode.HALF_UP));
		}
	}

	/**
	 * 交易计算返回总值
	 * 
	 * @param direction
	 *            持仓方向
	 * @param total
	 *            总价值
	 * @param lastPrice
	 *            平仓或者最新价
	 * @param avgPrice
	 *            开仓均价
	 * @param despoit
	 *            保证金
	 * @return 交易计算返回总值
	 */
	public static BigDecimal gainLoss(int direction, BigDecimal coefficient, BigDecimal lastPrice, BigDecimal avgPrice, BigDecimal despoit,
			BigDecimal totalHoldCount) {
		return YK(direction, coefficient, lastPrice, avgPrice, totalHoldCount).add(despoit).setScale(8, RoundingMode.HALF_UP);
	}

	public static BigDecimal getDeposit(BigDecimal amount, BigDecimal price, int multiple, BigDecimal coefficient) {
		return getDeposit(amount, price, new BigDecimal(multiple), coefficient);
	}
	
	public static BigDecimal getFee(BigDecimal amount, BigDecimal price, BigDecimal rate, BigDecimal coefficient) {
		return amount.multiply(coefficient).multiply(price).multiply(rate).setScale(9, BigDecimal.ROUND_HALF_UP).setScale(8, BigDecimal.ROUND_HALF_DOWN);
	}

	public static BigDecimal getDeposit(BigDecimal amount, BigDecimal price, BigDecimal multiple, BigDecimal coefficient) {
		return amount.multiply(coefficient).multiply(price).divide(multiple, 9, BigDecimal.ROUND_HALF_UP).setScale(8, BigDecimal.ROUND_HALF_DOWN);
		// return amount.divide(multiple, 5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_HALF_DOWN);
	}
	
	public static BigDecimal getMaxAmount(BigDecimal remainAmount, BigDecimal price, BigDecimal multiple, BigDecimal coefficient, BigDecimal rate) {
		// maxAmount = remainAmount / (price / multiple + price * feeRate) / coeffcient;
//		return remainAmount.divide((price.divide(multiple , 5, BigDecimal.ROUND_HALF_UP).add(price.multiply(rate))) , 5, BigDecimal.ROUND_HALF_UP).divide(coefficient , 5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_HALF_DOWN);
		// maxAmount = remainAmount / [(price / multiple + price * feeRate) * coeffcient];
		return remainAmount.divide((price.divide(multiple , 4, BigDecimal.ROUND_HALF_DOWN).add(price.multiply(rate))).multiply(coefficient), 5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_HALF_DOWN);
	}

	public static BigDecimal getSZDeposit1(BigDecimal amount, BigDecimal price, int multiple) {
		return getSZDeposit1(amount, price, new BigDecimal(multiple));
	}

	public static BigDecimal getSZDeposit1(BigDecimal amount, BigDecimal price, BigDecimal multiple) {
		return (amount.multiply(price)).divide(multiple, 5, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
	}

	public static BigDecimal getSZDeposit2(BigDecimal amount, BigDecimal price, int multiple) {
		return getSZDeposit2(amount, price, new BigDecimal(multiple));
	}

	public static BigDecimal getSZDeposit2(BigDecimal amount, BigDecimal price, BigDecimal multiple) {
		return (amount.multiply(price)).divide(multiple, 5, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_DOWN);
	}

	public static boolean checkSZPriceForSell(BigDecimal lastpriceOneMinAgo, BigDecimal yesLastPrice, BigDecimal price_client, AjaxCheckVO checkVO) {

		BigDecimal min = BigDecimal.ZERO;

		if (lastpriceOneMinAgo.compareTo(BigDecimal.ZERO) > 0) {
			min = yesLastPrice.multiply(new BigDecimal(0.95)).setScale(3,RoundingMode.HALF_UP);
			BigDecimal minlast = lastpriceOneMinAgo.multiply(new BigDecimal(0.99)).setScale(3,RoundingMode.HALF_UP); // 最低可写入价为60秒前最新成交价-1%点
			BigDecimal minSell = min.compareTo(minlast) >= 0 ? min.setScale(1, RoundingMode.UP) : minlast.setScale(1, RoundingMode.UP);

			if (minSell.compareTo(price_client) > 0) {
				checkVO.setCode("error");
				checkVO.setMsg("价格不能低于" + minSell);
				return false;
			}
		} else {
			min = yesLastPrice.multiply(BigDecimal.ONE.subtract(new BigDecimal(0.1))).setScale(1, RoundingMode.UP);
			if (min.compareTo(price_client) > 0) {
				checkVO.setCode("error");
				checkVO.setMsg("价格不能低于" + min);
				return false;
			}
		}

		return true;
	}

	public static boolean checkSZPriceForBuy(BigDecimal lastpriceOneMinAgo, BigDecimal yesLastPrice, BigDecimal price_client, AjaxCheckVO checkVO) {

		BigDecimal max = BigDecimal.ZERO;

		if (lastpriceOneMinAgo.compareTo(BigDecimal.ZERO) > 0) {
			max = yesLastPrice.multiply(new BigDecimal(1.05));
			BigDecimal maxlast = lastpriceOneMinAgo.multiply(new BigDecimal(1.01)); // 最高可写入价为60秒前最新成交价+1%点

			BigDecimal maxBuy = max.compareTo(maxlast) <= 0 ? max.setScale(1, RoundingMode.DOWN) : maxlast.setScale(1, RoundingMode.DOWN);

			if (maxBuy.compareTo(price_client) < 0) {
				checkVO.setCode("error");
				checkVO.setMsg("价格不能高于" + maxBuy);
				return false;
			}
		} else {
			max = yesLastPrice.multiply(BigDecimal.ONE.add(new BigDecimal(0.1))).setScale(1, RoundingMode.DOWN);
			if (max.compareTo(price_client) < 0) {
				checkVO.setCode("error");
				checkVO.setMsg("价格不能高于" + max);
				return false;
			}
		}

		return true;
	}

	// public static void main(String[] args) {
	// Calendar calendar=Calendar.getInstance();
	// calendar.setFirstDayOfWeek(Calendar.SUNDAY);
	// calendar.setTime(new Date());
	// calendar.set(Calendar.DAY_OF_WEEK,calendar.getFirstDayOfWeek()+5);
	// SimpleDateFormat formater=new SimpleDateFormat("MM-dd");
	// int i=0;
	// List<String> list=new ArrayList<String>();
	// while(i<300){
	// String nn=formater.format(calendar.getTime());
	// if(!list.contains(nn)){
	// list.add(nn);
	// System.out.println(nn);
	// }else{
	// System.out.println("======"+nn);
	// }
	// calendar.add(Calendar.DAY_OF_YEAR, 7);
	// i++;
	// }
	//		
	// System.out.println("*****"+list.size());
	// }
}
