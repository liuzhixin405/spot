package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_article")
public class BQArticle implements Serializable {

	private static final long serialVersionUID = -7731789266939997334L;
	private int article_id;
	private int category_id; // 1:站内公告，2：信息披露，3：帮助，4：新闻资讯
	private String title;
	private String keyword;
	private String description;
	private String username;
	private int dateline;
	private int istop;
	private int displayorder;
	private int state;
	private String language;

	public BQArticle() {

	}

	@Id
	@GeneratedValue
	public int getArticle_id() {
		return article_id;
	}

	public void setArticle_id(int value) {
		article_id = value;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int value) {
		category_id = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String value) {
		title = value;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String value) {
		keyword = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String value) {
		description = value;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String value) {
		username = value;
	}

	public int getDateline() {
		return dateline;
	}

	public void setDateline(int value) {
		dateline = value;
	}

	public int getIstop() {
		return istop;
	}

	public void setIstop(int value) {
		istop = value;
	}

	public int getDisplayorder() {
		return displayorder;
	}

	public void setDisplayorder(int value) {
		displayorder = value;
	}

	public int getState() {
		return state;
	}

	public void setState(int value) {
		state = value;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
