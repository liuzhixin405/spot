package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.util.List;

public class PagerModel implements Serializable {

	private static final long serialVersionUID = 7492936914115484358L;

	/**
	 * 总记录数
	 */
	private int total = 0;
	/**
	 * 当前页结果集
	 */
	private List<?> datas;

	/*
	 * 最大记录数
	 */
	private int maxitems = 15;

	/*
	 * 最多显示页数
	 */
	private int maxIndexPages = 10;

	/**
	 * 备用字段
	 */
	private String flag = "";

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public List<?> getDatas() {
		return datas;
	}

	public void setDatas(List<?> datas) {
		this.datas = datas;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getMaxitems() {
		return maxitems;
	}

	public void setMaxitems(int maxitems) {
		this.maxitems = maxitems;
	}

	public int getMaxIndexPages() {
		return maxIndexPages;
	}

	public void setMaxIndexPages(int maxIndexPages) {
		this.maxIndexPages = maxIndexPages;
	}

}
