package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 现货交易（币币交易）委托挂单日志表
 * 
 * @author Administrator
 *
 */
@Entity
public class SpotPendingLog implements Serializable {

	private static final long serialVersionUID = 646262012092318927L;

	@Id
	@GeneratedValue
	private long log_id;
	private long delegation_id;// 委托编号
	private long pending_no;// 挂单流水号
	private long delegation_time;// 委托时间,unix时间格式
	private long coin_pair_id;// 合约号id
	private String coin_pair_name;// 合约号
	private int direction; // 操作方向(1:买/2:卖/)
	private BigDecimal delegation_count;// 委托数量
	private BigDecimal delegation_price;// 委托价格
	private BigDecimal deal_count;// 已成交数量
	private BigDecimal undeal_count;// 未成交数量
	private int action;// 挂单日志动作
	private long dateline;// 操作时间,unix时间格式
	private long f_user_id;// 用户ID
	private int flag = 0;// 0表示正常挂单日志，1标识交易异常时转过来的挂单
	private BigDecimal avg_price = BigDecimal.ZERO;//成交均价
	private BigDecimal income = BigDecimal.ZERO;//盈亏
	private BigDecimal fee = BigDecimal.ZERO;//手续费
	private BigDecimal bfx_fee = BigDecimal.ZERO;//bfx抵扣手续费
	private int device = 0;//下单设备（0-PC，1-App，2-Api，3-服务器[强撮单等]）
	private String ip_addr = "";//下单IP
	
	public SpotPendingLog() {
		super();
	}

	public SpotPendingLog(long delegationId, long delegationTime, long coinPairId, String coinPairName, int direction,
			BigDecimal delegationCount, BigDecimal delegationPrice, BigDecimal dealCount, BigDecimal undealCount,
			int action, long fUserId, long pendingNo, int device, String ipAddr) {
		super();
		delegation_id = delegationId;
		delegation_time = delegationTime;
		coin_pair_id = coinPairId;
		coin_pair_name = coinPairName;
		this.direction = direction;
		delegation_count = delegationCount;
		delegation_price = delegationPrice;
		deal_count = dealCount;
		undeal_count = undealCount;
		this.action = action;
		f_user_id = fUserId;
		pending_no = pendingNo;
		this.setDevice(device);
		this.setIp_addr(ipAddr);
	}

	public long getLog_id() {
		return log_id;
	}

	public void setLog_id(long log_id) {
		this.log_id = log_id;
	}

	public long getDelegation_id() {
		return delegation_id;
	}

	public void setDelegation_id(long delegation_id) {
		this.delegation_id = delegation_id;
	}

	public long getPending_no() {
		return pending_no;
	}

	public void setPending_no(long pending_no) {
		this.pending_no = pending_no;
	}

	public long getDelegation_time() {
		return delegation_time;
	}

	public void setDelegation_time(long delegation_time) {
		this.delegation_time = delegation_time;
	}

	public long getCoin_pair_id() {
		return coin_pair_id;
	}

	public void setCoin_pair_id(long coin_pair_id) {
		this.coin_pair_id = coin_pair_id;
	}

	public String getCoin_pair_name() {
		return coin_pair_name;
	}

	public void setCoin_pair_name(String coin_pair_name) {
		this.coin_pair_name = coin_pair_name;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public BigDecimal getDelegation_count() {
		return delegation_count;
	}

	public void setDelegation_count(BigDecimal delegation_count) {
		this.delegation_count = delegation_count;
	}

	public BigDecimal getDelegation_price() {
		return delegation_price;
	}

	public void setDelegation_price(BigDecimal delegation_price) {
		this.delegation_price = delegation_price;
	}

	public BigDecimal getDeal_count() {
		return deal_count;
	}

	public void setDeal_count(BigDecimal deal_count) {
		this.deal_count = deal_count;
	}

	public BigDecimal getUndeal_count() {
		return undeal_count;
	}

	public void setUndeal_count(BigDecimal undeal_count) {
		this.undeal_count = undeal_count;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public BigDecimal getAvg_price() {
		return avg_price;
	}

	public void setAvg_price(BigDecimal avg_price) {
		this.avg_price = avg_price;
	}

	public int getDevice() {
		return device;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public String getIp_addr() {
		return ip_addr;
	}

	public void setIp_addr(String ip_addr) {
		this.ip_addr = ip_addr;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getBfx_fee() {
		return bfx_fee;
	}

	public void setBfx_fee(BigDecimal bfx_fee) {
		this.bfx_fee = bfx_fee;
	}

}
