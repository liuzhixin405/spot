package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_app")
public class BQApp implements Serializable {

	private static final long serialVersionUID = -7731789266939997334L;
	private Integer id;
	private String name;
	private String platform;
	private String version;
	private int download_times;
	private long update_time;
	private String download_url;
	private String note;
	private String force_version;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getDownload_times() {
		return download_times;
	}

	public void setDownload_times(int download_times) {
		this.download_times = download_times;
	}

	public long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(long update_time) {
		this.update_time = update_time;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getForce_version() {
		return force_version;
	}

	public void setForce_version(String force_version) {
		this.force_version = force_version;
	}

}
