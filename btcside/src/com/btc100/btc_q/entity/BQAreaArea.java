package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_area_area")
public class BQAreaArea implements Serializable {
	
	private static final long serialVersionUID = 2665498570615273804L;

public BQAreaArea(){}
private int id;

@Id
@GeneratedValue
public int getId(){
return id;
}
public void setId(int value){
id=value;
}

private String code;

public String getCode(){
return code;
}
public void setCode(String value){
code=value;
}

private String name;

public String getName(){
return name;
}
public void setName(String value){
name=value;
}

private String citycode;

public String getCitycode(){
return citycode;
}
public void setCitycode(String value){
citycode=value;
}

}
