package com.btc100.btc_q.util;

import java.io.Serializable;

/**
 * 所有属性都对应于 pagertag 分页标签来设定的
 * 
 * @author shenlin
 */
public class Pager implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -2023707508188102828L;

	/**
	 * 总记录数
	 */
	private int items;

	/**
	 * 总页数
	 */
	private int pageCount;

	/**
	 * 当前第几页
	 */
	private int pageIndex;

	/**
	 * Specifies the maximum number of items to be displayed in each page. The
	 * default value is 10. 指定每页显示的记录数，默认值是10。
	 */
	private int maxPageItems = 15;

	/**
	 * Specifies the maximum number of pages to be included in the generated
	 * index. The default value is 10. 在页面上显示的最大分页索引数，默认值是10。
	 */
	private int maxIndexPages = 10;

	/**
	 * Specifies the behavior of the index. The valid values for this attribute
	 * are "center", "forward" and "half-full". The default index behavior is
	 * "center". 指定分页索引的表现形式，有"center", "forward" 和 "half-full"三种可选值，默认的表现形式是
	 * "center"。
	 */
	private String index = "center";

	/**
	 * 分页记录偏移量,如果是第一页，则值为0*maxPageItems；如果是第二页，值为1*maxPageItems；如果是第三页，值为2*
	 * maxPageItems
	 */
	private int offset = 0;

	/**
	 * 排序条件
	 */
	private String orderBy = "";

	private int currentLastIndex = 0;

	/**
	 * @return the orderBy
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * @param orderBy
	 *            the orderBy to set
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * 分页记录偏移量,如果是第一页，则值为0*maxPageItems；如果是第二页，值为1*maxPageItems；如果是第三页，值为2*
	 * maxPageItems
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * 分页记录偏移量,如果是第一页，则值为0*maxPageItems；如果是第二页，值为1*maxPageItems；如果是第三页，值为2*
	 * maxPageItems
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getItems() {
		return items;
	}

	public void setItems(int items) {

		this.items = items;
	}

	/**
	 * Specifies the maximum number of items to be displayed in each page. The
	 * default value is 10. 获取每页显示的记录数，默认值是10。
	 */
	public int getMaxPageItems() {
		return maxPageItems;
	}

	/**
	 * Specifies the maximum number of items to be displayed in each page. The
	 * default value is 10. 指定每页显示的记录数，默认值是10。
	 */
	public void setMaxPageItems(int maxPageItems) {
		this.maxPageItems = maxPageItems;
	}

	public int getMaxIndexPages() {
		return maxIndexPages;
	}

	public void setMaxIndexPages(int maxIndexPages) {
		this.maxIndexPages = maxIndexPages;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @param pageCount
	 *            the pageCount to set
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * @return the pageCount
	 */
	public int getPageCount() {
		if (items <= 0) {
			pageCount = 0;
		} else {
			/* 总记录数 除以 每页显示记录数 */
			pageCount = (items / maxPageItems) + (items % maxPageItems == 0 ? 0 : 1);
		}
		return pageCount;
	}

	/**
	 * @param pageIndex
	 *            the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		if (offset <= 0) {
			pageIndex = 1;
		} else {
			pageIndex = offset / maxPageItems + 1;
		}
		return pageIndex;
	}

	public void setCurrentLastIndex(int currentLastIndex) {
		this.currentLastIndex = currentLastIndex;
	}

	public int getCurrentLastIndex() {
		return offset + maxPageItems;
	}

}