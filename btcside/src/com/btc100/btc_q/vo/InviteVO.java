package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class InviteVO implements Serializable {

	private static final long serialVersionUID = 258767936120864059L;

	private long sumClick; // 邀请链接总点击次数
	private long sumSuccess; // 总邀请成功人数
	private long todayClick; // 邀请链接今日点击次数
	private long todaySuccess; // 今日邀请成功人数
	private double weekRebate; // 本周提成
	private double monthRebate; // 本月提成
	private double totalRebate; // 总提成
	private double weekBfxRebate; // BFX本周提成
	private double monthBfxRebate; // BFX本月提成
	private double totalBfxRebate; // BFX总提成
	private double weekUsdrRebate; // USDR本周提成
	private double monthUsdrRebate; // USDR本月提成
	private double totalUsdrRebate; // USDR总提成
	private List<Map<String,Object>> allRebate; //所有币种信息

	public List<Map<String, Object>> getAllRebate() {
		return allRebate;
	}

	public void setAllRebate(List<Map<String, Object>> allRebate) {
		this.allRebate = allRebate;
	}
	public long getSumClick() {
		return sumClick;
	}

	public void setSumClick(long sumClick) {
		this.sumClick = sumClick;
	}

	public long getSumSuccess() {
		return sumSuccess;
	}

	public void setSumSuccess(long sumSuccess) {
		this.sumSuccess = sumSuccess;
	}

	public long getTodayClick() {
		return todayClick;
	}

	public void setTodayClick(long todayClick) {
		this.todayClick = todayClick;
	}

	public long getTodaySuccess() {
		return todaySuccess;
	}

	public void setTodaySuccess(long todaySuccess) {
		this.todaySuccess = todaySuccess;
	}

	public double getWeekRebate() {
		return weekRebate;
	}

	public void setWeekRebate(double weekRebate) {
		this.weekRebate = weekRebate;
	}

	public double getMonthRebate() {
		return monthRebate;
	}

	public void setMonthRebate(double monthRebate) {
		this.monthRebate = monthRebate;
	}

	public double getTotalRebate() {
		return totalRebate;
	}

	public void setTotalRebate(double totalRebate) {
		this.totalRebate = totalRebate;
	}

	public double getWeekBfxRebate() {
		return weekBfxRebate;
	}

	public void setWeekBfxRebate(double weekBfxRebate) {
		this.weekBfxRebate = weekBfxRebate;
	}

	public double getMonthBfxRebate() {
		return monthBfxRebate;
	}

	public void setMonthBfxRebate(double monthBfxRebate) {
		this.monthBfxRebate = monthBfxRebate;
	}

	public double getTotalBfxRebate() {
		return totalBfxRebate;
	}

	public void setTotalBfxRebate(double totalBfxRebate) {
		this.totalBfxRebate = totalBfxRebate;
	}

	public double getWeekUsdrRebate() {
		return weekUsdrRebate;
	}

	public void setWeekUsdrRebate(double weekUsdrRebate) {
		this.weekUsdrRebate = weekUsdrRebate;
	}

	public double getMonthUsdrRebate() {
		return monthUsdrRebate;
	}

	public void setMonthUsdrRebate(double monthUsdrRebate) {
		this.monthUsdrRebate = monthUsdrRebate;
	}

	public double getTotalUsdrRebate() {
		return totalUsdrRebate;
	}

	public void setTotalUsdrRebate(double totalUsdrRebate) {
		this.totalUsdrRebate = totalUsdrRebate;
	}
}
