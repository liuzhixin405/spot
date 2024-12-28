package com.btc100.btc_q.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 抽奖设置
 */
@Entity
@Table(name = "bq_luck_draw_setting")
public class BQLuckDrawSetting implements Serializable {

    private static final long serialVersionUID = -745843520847342251L;

    public BQLuckDrawSetting(){

    }

    private int id;                 //id
    private String prize;           //奖项
    private String name_zh_CN;      //中文名称
    private String name_en_US;      //英文名称
    private double probability;     //中奖概率
    private String prize_matter;    //实物奖品
    private BigDecimal prize_usdt;  //usdt奖品
    private String notes;           //奖品说明
    private String photo_name;      //照片

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getName_zh_CN() {
        return name_zh_CN;
    }

    public void setName_zh_CN(String name_zh_CN) {
        this.name_zh_CN = name_zh_CN;
    }

    public String getName_en_US() {
        return name_en_US;
    }

    public void setName_en_US(String name_en_US) {
        this.name_en_US = name_en_US;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }
}
