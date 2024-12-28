package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 管理员菜单
 * 
 */
@Entity
@Table(name = "bq_manager_menu")
public class BQManagerMenu implements Serializable {

	private static final long serialVersionUID = 5792793179677208457L;

	@Id
	@GeneratedValue
	private int id; // ID

	private String menu_name; // 主菜单名称

	private String link; // URL链接（action名称）

	private Integer state = 1; // 状态：默认使用中

	private String menu_no; // 编号

	public BQManagerMenu() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMenu_name() {
		return menu_name;
	}

	public void setMenu_name(String menu_name) {
		this.menu_name = menu_name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getMenu_no() {
		return menu_no;
	}

	public void setMenu_no(String menu_no) {
		this.menu_no = menu_no;
	}

}
