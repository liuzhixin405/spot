package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_wallet_queue_btc")
public class BQWalletQueueBtc implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;
	
	
	private int wallet_id;
	private String wallet_address;//钱包地址',
	private String wallet_pic;
	private int currency_type_id;
	private boolean erc20 = false;
	private boolean trc20 = false;
	private int state;
	
	public BQWalletQueueBtc(){}
	
	@Id
	@GeneratedValue
	public int getWallet_id() {
		return wallet_id;
	}
	public void setWallet_id(int walletId) {
		wallet_id = walletId;
	}
	public String getWallet_address() {
		return wallet_address;
	}
	public void setWallet_address(String walletAddress) {
		wallet_address = walletAddress;
	}

	public String getWallet_pic() {
		return wallet_pic;
	}

	public void setWallet_pic(String walletPic) {
		wallet_pic = walletPic;
	}
	
	public int getCurrency_type_id() {
		return currency_type_id;
	}
	
	public void setCurrency_type_id(int typeId) {
		this.currency_type_id = typeId;
	}
	
	public boolean getErc20() {
		return this.erc20;
	}
	
	public void setErc20(boolean erc20) {
		this.erc20 = erc20;
	}
	
	public int getState() {
		return this.state;
	}
	
	public void setState(int state) {
		this.state = state;
	}

	public boolean getTrc20() {
		return trc20;
	}

	public void setTrc20(boolean trc20) {
		this.trc20 = trc20;
	}
}
