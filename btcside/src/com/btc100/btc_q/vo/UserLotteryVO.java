package com.btc100.btc_q.vo;

import java.io.Serializable;

public class UserLotteryVO implements Serializable {

    private static final long serialVersionUID = 6449715960644730586L;

    private String userName;        //模糊用户名
    private String prize;           //奖项
    private long time;              //时间
    private String notes;           //说明
    private String photo;           //照片
    private int count;              //获奖数量

    public UserLotteryVO() {

    }

    public UserLotteryVO(String userName, String notes) {
        this.userName = userName;
        this.notes = notes;
    }

    public UserLotteryVO(long time, String notes, String prize) {
        this.time = time;
        this.notes = notes;
        this.prize = prize;
    }

    public UserLotteryVO(String prize, String notes, String photo, int count) {
        this.prize = prize;
        this.notes = notes;
        this.photo = photo;
        this.count = count;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
