package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户个人挖矿数量
 */
@Entity
@Table(name = "bq_mine_personal_amount")
public class BQMinePersonalAmount implements Serializable {

    private static final long serialVersionUID = -2541158773635944891L;

    private long uid;
    private BigDecimal mined_amount;

    @Id
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public BigDecimal getMined_amount() {
        return mined_amount;
    }

    public void setMined_amount(BigDecimal mined_amount) {
        this.mined_amount = mined_amount;
    }
}
