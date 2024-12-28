package com.btc100.btc_q.common;

public class DATA_CreditNumber {
	/*
	 * （1）累计积分
	 *
	 * 		开仓量 每开仓100个ckusd，1积分，每天00:00统计并发放积分，四舍五入。
	 * 
	 * 		净资产 每日账户留存1000个ckusd，1积分
	 * 
	 * （2）一次性积分
	 * 
	 * 		注册 +20积分
	 * 
	 * 		验证邮箱 +10积分
	 * 
	 * 		谷歌认证 +10积分
	 * 
	 * 		绑定手机 +10积分
	 * 
	 * 		首次充值 +25积分
	 * 
	 * 		首次交易 +40积分
	 * 
	 * 		建议采纳 +100积分/次
	 * 
	 * 		注意：积分取整数，四舍五入。
	 */

	/**注册、验证邮箱  +20积分 */
	public static final int CREDIT_ONE_REGISTER = 20;
	/**验证邮箱 +10积分 */
	public static final int CREDIT_ONE_SUREEMAIL = 10;
	/**谷歌认证 +10积分 */
	public static final int CREDIT_ONE_BINDGOOGLE = 10;
	/**绑定手机 +10积分 */
	public static final int CREDIT_ONE_BINDPHONE = 10;
	/**首次充值 +25积分 */
	public static final int CREDIT_ONE_FIRST_RECHARGE = 25;
	/**首次交易 +40积分 */
	public static final int CREDIT_ONE_FIRST_TRADE = 40;
	/**建议采纳 +100积分/每次 */
	public static final int CREDIT_ONE_AGGEST = 100;

	/**开仓量 每开仓A个比特币，+A×10积分 */
	public static final int CREDIT_MUCH_KCBS = 10;

}
