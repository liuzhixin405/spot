package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQMemberSettle;

public class SystemGainLossVO implements Serializable {

	private static final long serialVersionUID = -7411273990008415092L;
	private BigDecimal usergain = BigDecimal.ZERO;
	private BigDecimal userloss = BigDecimal.ZERO;
	private BigDecimal sysloss = BigDecimal.ZERO;

	private BigDecimal rate = BigDecimal.ZERO;
	private BigDecimal apportion = BigDecimal.ZERO;
	private BigDecimal shallget = BigDecimal.ZERO;

	private List<BQMemberSettle> list;

	
	public SystemGainLossVO() {
	}

	public SystemGainLossVO(BigDecimal usergain, BigDecimal rate, BigDecimal apportion, BigDecimal shallget, List<BQMemberSettle> list, BigDecimal user_loss,
			BigDecimal sys_loss) {
		this.usergain = usergain;
		this.rate = rate;
		this.apportion = apportion;
		this.shallget = shallget;
		this.list = list;
		userloss = user_loss;
		sysloss = sys_loss;
	}

	public BigDecimal getUsergain() {
		return usergain;
	}

	public void setUsergain(BigDecimal usergain) {
		this.usergain = usergain;
	}

	public BigDecimal getUserloss() {
		return userloss;
	}

	public void setUserloss(BigDecimal userloss) {
		this.userloss = userloss;
	}

	public BigDecimal getSysloss() {
		return sysloss;
	}

	public void setSysloss(BigDecimal sysloss) {
		this.sysloss = sysloss;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getApportion() {
		return apportion;
	}

	public void setApportion(BigDecimal apportion) {
		this.apportion = apportion;
	}

	public BigDecimal getShallget() {
		return shallget;
	}

	public void setShallget(BigDecimal shallget) {
		this.shallget = shallget;
	}

	public List<BQMemberSettle> getList() {
		return list;
	}

	public void setList(List<BQMemberSettle> list) {
		this.list = list;
	}

	

}
