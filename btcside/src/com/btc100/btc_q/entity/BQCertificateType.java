package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 荣誉证书类型
 * @author win7
 *
 */
@Entity
@Table(name = "bq_certificate_type")
public class BQCertificateType implements Serializable {

	private static final long serialVersionUID = 7259028964798112966L;
	
	private long id;
	private String certificate_name; // 证书名称
	private long create_time; // 创建时间
	
	public BQCertificateType() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCertificate_name() {
		return certificate_name;
	}

	public void setCertificate_name(String certificate_name) {
		this.certificate_name = certificate_name;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

}
