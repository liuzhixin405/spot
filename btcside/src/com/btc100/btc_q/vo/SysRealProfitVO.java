package com.btc100.btc_q.vo;

import java.math.BigDecimal;

/**
 * 系统实时盈亏VO类
 * 
 * @author Administrator
 * 
 */
public class SysRealProfitVO {

	private String bqCode;// 合约号
	private BigDecimal totalGain;// 系统总盈利
	private BigDecimal totalLoss;// 系统总亏损
	private BigDecimal shareLoss;// 用户已分摊亏损
	private BigDecimal netLoss;// 系统净亏损
	private BigDecimal shareRatio;// 系统亏损比例（即均摊比例）

	public SysRealProfitVO() {

	}

	/**
	 * 构造函数
	 * @param bqCode 合约号
	 * @param totalGain 系统总盈利
	 * @param totalLoss 系统总亏损
	 * @param shareLoss 用户已分摊亏损
	 * @param netLoss 系统净亏损
	 * @param shareRatio 系统亏损比例（即均摊比例）
	 */
	public SysRealProfitVO(String bqCode, BigDecimal totalGain,
			BigDecimal totalLoss, BigDecimal shareLoss, BigDecimal netLoss,
			BigDecimal shareRatio) {
		this.bqCode = bqCode;
		this.totalGain = totalGain;
		this.totalLoss = totalLoss;
		this.shareLoss = shareLoss;
		this.netLoss = netLoss;
		this.shareRatio = shareRatio;
	}

	public String getBqCode() {
		return bqCode;
	}

	public void setBqCode(String bqCode) {
		this.bqCode = bqCode;
	}

	public BigDecimal getTotalGain() {
		return totalGain;
	}

	public void setTotalGain(BigDecimal totalGain) {
		this.totalGain = totalGain;
	}

	public BigDecimal getTotalLoss() {
		return totalLoss;
	}

	public void setTotalLoss(BigDecimal totalLoss) {
		this.totalLoss = totalLoss;
	}

	public BigDecimal getShareLoss() {
		return shareLoss;
	}

	public void setShareLoss(BigDecimal shareLoss) {
		this.shareLoss = shareLoss;
	}

	public BigDecimal getNetLoss() {
		return netLoss;
	}

	public void setNetLoss(BigDecimal netLoss) {
		this.netLoss = netLoss;
	}

	public BigDecimal getShareRatio() {
		return shareRatio;
	}

	public void setShareRatio(BigDecimal shareRatio) {
		this.shareRatio = shareRatio;
	}

}
