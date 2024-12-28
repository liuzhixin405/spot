package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_wallet")
public class BQMemberWallet implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;
	
	 private int id;
	 private long f_user_id;//用户ID',
	 private int currency_type_id;//'货币类型',
	 private String wallet_address;
	 private String wallet_address_pic;//钱包二维码图片地址(路径进行hash算法存储)',
	 private boolean erc20 = false;
	 private boolean trc20 = false;
	 
	public BQMemberWallet(){}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getF_user_id() {
		return f_user_id;
	}
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}
	public int getCurrency_type_id() {
		return currency_type_id;
	}
	public void setCurrency_type_id(int currencyTypeId) {
		currency_type_id = currencyTypeId;
	}
	public String getWallet_address() {
		return wallet_address;
	}
	public void setWallet_address(String walletAddress) {
		wallet_address = walletAddress;
	}
	public String getWallet_address_pic() {
		return wallet_address_pic;
	}
	public void setWallet_address_pic(String walletAddressPic) {
		wallet_address_pic = walletAddressPic;
	}
	public boolean getErc20() {
		return this.erc20;
	}
	public void setErc20(boolean erc20) {
		this.erc20 = erc20;
	}
	public boolean getTrc20() {
		return trc20;
	}
	public void setTrc20(boolean trc20) {
		this.trc20 = trc20;
	}
}
