package com.btc100.btc_q.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 指定用户奖品
 */
@Entity
@Table(name = "bq_appoint_luck")
public class BQAppointLuck implements Serializable {

    private static final long serialVersionUID = 2845959753564630895L;

    public BQAppointLuck(){

    }

    private int id;         //id
    private long user_id;   //用户id
    private String prize;   //奖项
    private int number;     //抽取多少次获得

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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
