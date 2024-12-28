package com.btc100.btc_q.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 抽奖记录表
 */
@Entity
@Table(name = "bq_luck_draw_record")
public class BQLuckDrawRecord implements Serializable {

    private static final long serialVersionUID = -4377710068050542708L;

    public BQLuckDrawRecord(){

    }

    private int id;
    private long user_id;           //用户id
    private String prize;           //奖项
    private String prize_name;      //奖品名称
    private long draw_time;         //抽奖时间

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getPrize_name() {
        return prize_name;
    }

    public void setPrize_name(String prize_name) {
        this.prize_name = prize_name;
    }

    public long getDraw_time() {
        return draw_time;
    }

    public void setDraw_time(long draw_time) {
        this.draw_time = draw_time;
    }
}
