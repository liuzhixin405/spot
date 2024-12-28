package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_invite_click_count")
public class BQInviteClickCount implements Serializable {

private static final long serialVersionUID = 1L;
private long f_user_id;//'用户ID',
private int click_day_count;//'单日邀请数量',
private int click_week_count;// '周累计',
private int click_month_count;//'月累计',
private int click_total_count;//'总数量',
private int day;

public BQInviteClickCount(){}
@Id
public long getF_user_id() {
	return f_user_id;
}
public void setF_user_id(long fUserId) {
	f_user_id = fUserId;
}
public int getClick_day_count() {
	return click_day_count;
}
public void setClick_day_count(int clickDayCount) {
	click_day_count = clickDayCount;
}
public int getClick_week_count() {
	return click_week_count;
}
public void setClick_week_count(int clickWeekCount) {
	click_week_count = clickWeekCount;
}
public int getClick_month_count() {
	return click_month_count;
}
public void setClick_month_count(int clickMonthCount) {
	click_month_count = clickMonthCount;
}
public int getClick_total_count() {
	return click_total_count;
}
public void setClick_total_count(int clickTotalCount) {
	click_total_count = clickTotalCount;
}
public int getDay() {
	return day;
}
public void setDay(int day) {
	this.day = day;
}//当天时间，unix时间格式，只保存yyyy-mm-dd这部分的信息',





}
