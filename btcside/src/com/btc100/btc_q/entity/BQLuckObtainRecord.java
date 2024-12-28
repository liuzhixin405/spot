package com.btc100.btc_q.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 获奖记录表
 */
@Entity
@Table(name = "bq_luck_obtain_record")
public class BQLuckObtainRecord implements Serializable {

    private static final long serialVersionUID = -282186788032168058L;

    public BQLuckObtainRecord(){

    }

    private int id;
    private long user_id;           //用户id
    private String user_name;       //模糊用户名
    private int prize_id;           //奖项id
    private String prize_name_CN;   //奖项中文名称
    private String prize_name_US;   //奖项英文名称
    private String prize_matter;    //实物奖励
    private BigDecimal prize_usdt;  //usdt奖励
    private long obtain_time;       //获奖时间
    private boolean random;         //false随机抽得；true管理员指定抽得
    private String notes;           //奖品说明

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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getPrize_id() {
        return prize_id;
    }

    public void setPrize_id(int prize_id) {
        this.prize_id = prize_id;
    }

    public String getPrize_name_CN() {
        return prize_name_CN;
    }

    public void setPrize_name_CN(String prize_name_CN) {
        this.prize_name_CN = prize_name_CN;
    }

    public String getPrize_name_US() {
        return prize_name_US;
    }

    public void setPrize_name_US(String prize_name_US) {
        this.prize_name_US = prize_name_US;
    }

    public String getPrize_matter() {
        return prize_matter;
    }

    public void setPrize_matter(String prize_matter) {
        this.prize_matter = prize_matter;
    }

    public BigDecimal getPrize_usdt() {
        return prize_usdt;
    }

    public void setPrize_usdt(BigDecimal prize_usdt) {
        this.prize_usdt = prize_usdt;
    }

    public long getObtain_time() {
        return obtain_time;
    }

    public void setObtain_time(long obtain_time) {
        this.obtain_time = obtain_time;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
