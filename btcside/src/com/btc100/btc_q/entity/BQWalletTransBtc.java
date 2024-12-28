package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_wallet_trans_btc")
public class BQWalletTransBtc implements Serializable {

	private static final long serialVersionUID = 6850594618567710878L;
	private long f_trans_id;
	private String f_wallet_id;
	private String f_category;
	private int f_trans_type;
	private BigDecimal f_account;
	private String f_address;
	private BigDecimal f_credit;
	private BigDecimal f_debit;
	private BigDecimal f_debit_fee;
	private BigDecimal f_amount;
	private BigDecimal f_balance;
	private int f_confirms;
	private String f_txid;
	private int f_time;
	private int f_timereceived;
	private int f_state;
	private String f_comment1;
	private String f_comment2;
	private String f_comment3;
	private String f_comment4;
	private String f_comment5;

	public BQWalletTransBtc() {
	}

	@Id
	@GeneratedValue
	public long getF_trans_id() {
		return f_trans_id;
	}

	public void setF_trans_id(long value) {
		f_trans_id = value;
	}

	public String getF_wallet_id() {
		return f_wallet_id;
	}

	public void setF_wallet_id(String value) {
		f_wallet_id = value;
	}

	public String getF_category() {
		return f_category;
	}

	public void setF_category(String value) {
		f_category = value;
	}

	public int getF_trans_type() {
		return f_trans_type;
	}

	public void setF_trans_type(int value) {
		f_trans_type = value;
	}

	public BigDecimal getF_account() {
		return f_account;
	}

	public void setF_account(BigDecimal value) {
		f_account = value;
	}

	public String getF_address() {
		return f_address;
	}

	public void setF_address(String value) {
		f_address = value;
	}

	public BigDecimal getF_credit() {
		return f_credit;
	}

	public void setF_credit(BigDecimal value) {
		f_credit = value;
	}

	public BigDecimal getF_debit() {
		return f_debit;
	}

	public void setF_debit(BigDecimal value) {
		f_debit = value;
	}

	public BigDecimal getF_debit_fee() {
		return f_debit_fee;
	}

	public void setF_debit_fee(BigDecimal value) {
		f_debit_fee = value;
	}

	public BigDecimal getF_amount() {
		return f_amount;
	}

	public void setF_amount(BigDecimal value) {
		f_amount = value;
	}

	public BigDecimal getF_balance() {
		return f_balance;
	}

	public void setF_balance(BigDecimal value) {
		f_balance = value;
	}

	public int getF_confirms() {
		return f_confirms;
	}

	public void setF_confirms(int value) {
		f_confirms = value;
	}

	public String getF_txid() {
		return f_txid;
	}

	public void setF_txid(String value) {
		f_txid = value;
	}

	public int getF_time() {
		return f_time;
	}

	public void setF_time(int value) {
		f_time = value;
	}

	public int getF_timereceived() {
		return f_timereceived;
	}

	public void setF_timereceived(int value) {
		f_timereceived = value;
	}

	public int getF_state() {
		return f_state;
	}

	public void setF_state(int value) {
		f_state = value;
	}

	public String getF_comment1() {
		return f_comment1;
	}

	public void setF_comment1(String value) {
		f_comment1 = value;
	}

	public String getF_comment2() {
		return f_comment2;
	}

	public void setF_comment2(String value) {
		f_comment2 = value;
	}

	public String getF_comment3() {
		return f_comment3;
	}

	public void setF_comment3(String value) {
		f_comment3 = value;
	}

	public String getF_comment4() {
		return f_comment4;
	}

	public void setF_comment4(String value) {
		f_comment4 = value;
	}

	public String getF_comment5() {
		return f_comment5;
	}

	public void setF_comment5(String value) {
		f_comment5 = value;
	}

}
