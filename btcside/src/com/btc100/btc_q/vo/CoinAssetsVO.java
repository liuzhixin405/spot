package com.btc100.btc_q.vo;

import java.io.Serializable;

public class CoinAssetsVO implements Serializable {

	private static final long serialVersionUID = 5238197546878471975L;

	private int coinId;
	private String coinName;
	private String coinDescribleZh;
	private String coinDescribleEn;
	private String coinDescribleJp;
	private String coinDescribleKr;
	private String coinDescribleHk;
	private double total;
	private double available;
	private double freeze;
	private double lock = 0;
	private double unlock = 0;
	private boolean enableRecharge = true;
	private boolean enableWithdraw = true;

	public CoinAssetsVO() {
	}

	public int getCoinId() {
		return coinId;
	}

	public void setCoinId(int coinId) {
		this.coinId = coinId;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getAvailable() {
		return available;
	}

	public void setAvailable(double available) {
		this.available = available;
	}

	public double getFreeze() {
		return freeze;
	}

	public void setFreeze(double freeze) {
		this.freeze = freeze;
	}

	public String getCoinDescribleZh() {
		return coinDescribleZh;
	}

	public void setCoinDescribleZh(String coinDescribleZh) {
		this.coinDescribleZh = coinDescribleZh;
	}

	public String getCoinDescribleEn() {
		return coinDescribleEn;
	}

	public void setCoinDescribleEn(String coinDescribleEn) {
		this.coinDescribleEn = coinDescribleEn;
	}

	public String getCoinDescribleJp() {
		return coinDescribleJp;
	}

	public void setCoinDescribleJp(String coinDescribleJp) {
		this.coinDescribleJp = coinDescribleJp;
	}

	public String getCoinDescribleKr() {
		return coinDescribleKr;
	}

	public void setCoinDescribleKr(String coinDescribleKr) {
		this.coinDescribleKr = coinDescribleKr;
	}

	public String getCoinDescribleHk() {
		return coinDescribleHk;
	}

	public void setCoinDescribleHk(String coinDescribleHk) {
		this.coinDescribleHk = coinDescribleHk;
	}

	public double getLock() {
		return lock;
	}

	public void setLock(double lock) {
		this.lock = lock;
	}

	public double getUnlock() {
		return unlock;
	}

	public void setUnlock(double unlock) {
		this.unlock = unlock;
	}

	public boolean isEnableRecharge() {
		return enableRecharge;
	}

	public void setEnableRecharge(boolean enableRecharge) {
		this.enableRecharge = enableRecharge;
	}

	public boolean isEnableWithdraw() {
		return enableWithdraw;
	}

	public void setEnableWithdraw(boolean enableWithdraw) {
		this.enableWithdraw = enableWithdraw;
	}

}