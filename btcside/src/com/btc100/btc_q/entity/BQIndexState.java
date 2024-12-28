package com.btc100.btc_q.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * 指数设置类
 */
@Entity
@Table(name = "bq_index_state")
public class BQIndexState implements Serializable {
    private static final long serialVersionUID = 7355024778440318831L;

    private int id;
    private int okex_state = 1;     //OKEX指数状态
    private int huobi_state = 1;    //火币指数状态
    private int binance_state = 1;  //币安指数状态

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOkex_state() {
        return okex_state;
    }

    public void setOkex_state(int okex_state) {
        this.okex_state = okex_state;
    }

    public int getHuobi_state() {
        return huobi_state;
    }

    public void setHuobi_state(int huobi_state) {
        this.huobi_state = huobi_state;
    }

    public int getBinance_state() {
        return binance_state;
    }

    public void setBinance_state(int binance_state) {
        this.binance_state = binance_state;
    }
}
