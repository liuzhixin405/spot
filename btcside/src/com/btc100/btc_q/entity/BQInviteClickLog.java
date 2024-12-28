package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_invite_click_log")
public class BQInviteClickLog implements Serializable {
	
	private static final long serialVersionUID = -8503034598211081170L;

public BQInviteClickLog(){}
private long log_id;

@Id
@GeneratedValue
public long getLog_id(){
return log_id;
}
public void setLog_id(long value){
log_id=value;
}

private long f_user_id;

public long getF_user_id(){
return f_user_id;
}
public void setF_user_id(long value){
f_user_id=value;
}

private String referer_url;

public String getReferer_url(){
return referer_url;
}
public void setReferer_url(String value){
referer_url=value;
}

private int dateline;

public int getDateline(){
return dateline;
}
public void setDateline(int value){
dateline=value;
}

private String ip;

public String getIp(){
return ip;
}
public void setIp(String value){
ip=value;
}

}
