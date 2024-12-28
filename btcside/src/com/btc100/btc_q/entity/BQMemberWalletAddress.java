package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_wallet_address")
public class BQMemberWalletAddress implements Serializable {
	
	private static final long serialVersionUID = 7927497251282977480L;

	public BQMemberWalletAddress(){}
	private long id;
	
	@Id
	@GeneratedValue
	public long getId(){
		return id;
	}
	public void setId(long value){
		id=value;
	}
	
	private long f_user_id;
	
	public long getF_user_id(){
		return f_user_id;
	}
	public void setF_user_id(long value){
		f_user_id=value;
	}
	
	private int currency_type_id;
	
	public int getCurrency_type_id(){
		return currency_type_id;
	}
	public void setCurrency_type_id(int value){
		currency_type_id=value;
	}
	
	private String wallet_address;
	
	public String getWallet_address(){
		return wallet_address;
	}
	public void setWallet_address(String value){
		wallet_address=value;
	}
	
	private String name;
	
	public String getName(){
		return name;
	}
	public void setName(String value){
		name=value;
	}
	
	private int is_default;
	
	public int getIs_default(){
		return is_default;
	}
	public void setIs_default(int value){
		is_default=value;
	}
	
	private boolean erc20 = false;
	
	public boolean isErc20() {
		return erc20;
	}
	public void setErc20(boolean erc20) {
		this.erc20 = erc20;
	}
	
	private boolean trc20 = false;
	
	public boolean isTrc20() {
		return trc20;
	}
	public void setTrc20(boolean trc20) {
		this.trc20 = trc20;
	}
}
