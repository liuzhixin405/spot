package com.btc100.btc_q.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户抽奖统计
 */
@Entity
@Table(name = "bq_member_draw")
public class BQMemberDraw implements Serializable {

    private static final long serialVersionUID = 9166597190844114178L;

    public  BQMemberDraw(){

    }

    private long f_user_id;
    private Integer f_draw_count;           //可抽奖次数
    private Integer f_already_draw_count;   //已抽奖次数
    private BigDecimal f_surplus_fee;       //取余手续费
    private BigDecimal f_produce_fee_usdt;  //已产生USDT手续费
    private BigDecimal f_produce_fee_usdr;  //已产生USDR手续费
    private String address;                 //收货地址


    @Id
    public long getF_user_id() {
        return f_user_id;
    }

    public void setF_user_id(long f_user_id) {
        this.f_user_id = f_user_id;
    }

    public Integer getF_draw_count() {
        return f_draw_count;
    }

    public void setF_draw_count(Integer f_draw_count) {
        this.f_draw_count = f_draw_count;
    }

    public Integer getF_already_draw_count() {
        return f_already_draw_count;
    }

    public void setF_already_draw_count(Integer f_already_draw_count) {
        this.f_already_draw_count = f_already_draw_count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getF_surplus_fee() {
        return f_surplus_fee;
    }

    public void setF_surplus_fee(BigDecimal f_surplus_fee) {
        this.f_surplus_fee = f_surplus_fee;
    }

    public BigDecimal getF_produce_fee_usdt() {
        return f_produce_fee_usdt;
    }

    public void setF_produce_fee_usdt(BigDecimal f_produce_fee_usdt) {
        this.f_produce_fee_usdt = f_produce_fee_usdt;
    }

    public BigDecimal getF_produce_fee_usdr() {
        return f_produce_fee_usdr;
    }

    public void setF_produce_fee_usdr(BigDecimal f_produce_fee_usdr) {
        this.f_produce_fee_usdr = f_produce_fee_usdr;
    }
}
