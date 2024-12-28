package com.btc100.btc_q.vo;

import java.math.BigDecimal;

public class WalletTransactionVo {
//	{"account":"","address":"1DCiuCb4peQTReoiHiJsAJSeTUPDXj6BiW","category":"receive","amount":0.5,"confirmations":3,"blockhash":"000000000000000008bd1b72d1ae4589a87526dae6ef6ea8c8c42bbc5e533f24",
//	"blockindex":"327","blocktime":"1413508983","txid":"c7836a363285225327b81e535c4ec36a61df30664801d85482b0a599400adc7a","time":"1413508655","timereceived":"1413508655"}
	private String account;
	private String address;
	private String category;
	private BigDecimal credit;
	private BigDecimal debit;
	private BigDecimal debit_fee;
	private BigDecimal amount;
	private BigDecimal balance;
	private Integer confirmations;
	private String blockhash;
	private Integer blockindex;
	private Integer blocktime;
	private String txid;
	private Integer time;
	private Integer timereceived;

	public WalletTransactionVo() {

	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public BigDecimal getDebit() {
		return debit;
	}

	public void setDebit(BigDecimal debit) {
		this.debit = debit;
	}

	public BigDecimal getDebit_fee() {
		return debit_fee;
	}

	public void setDebit_fee(BigDecimal debitFee) {
		debit_fee = debitFee;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(Integer confirmations) {
		this.confirmations = confirmations;
	}

	public String getBlockhash() {
		return blockhash;
	}

	public void setBlockhash(String blockhash) {
		this.blockhash = blockhash;
	}

	public Integer getBlockindex() {
		return blockindex;
	}

	public void setBlockindex(Integer blockindex) {
		this.blockindex = blockindex;
	}

	public Integer getBlocktime() {
		return blocktime;
	}

	public void setBlocktime(Integer blocktime) {
		this.blocktime = blocktime;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getTimereceived() {
		return timereceived;
	}

	public void setTimereceived(Integer timereceived) {
		this.timereceived = timereceived;
	}

}
