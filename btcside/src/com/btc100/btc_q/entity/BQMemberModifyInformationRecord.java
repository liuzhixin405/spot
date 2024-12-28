package com.btc100.btc_q.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "bq_member_modify_information_record")
public class BQMemberModifyInformationRecord implements Serializable {

    private static final long serialVersionUID = -5237620990237873144L;

    private long id;
    private long f_user_id;
    private String f_old_information;   //旧联系方式
    private String f_new_information;   //新联系方式
    private int f_information_type;     //修改类型:1-手机，2-邮箱，3-谷歌
    private long f_modify_time ;        //修改时间
    private String f_modify_ip;         //修改ip地址
    private int f_modify_server;        //修改设备:1-pc，2-api
    private String f_modify_user;       //修改人(管理员)
    private int f_operation_type;    //操作类型(1、注册；2、绑定；3、更改)

    public BQMemberModifyInformationRecord(){

    }

    public BQMemberModifyInformationRecord(long f_user_id, String f_old_information, String f_new_information, int f_information_type,
                                           long f_modify_time, String f_modify_ip, int f_modify_server, String f_modify_user, int f_operation_type){
        this.f_user_id = f_user_id;
        this.f_old_information = f_old_information;
        this.f_new_information = f_new_information;
        this.f_information_type = f_information_type;
        this.f_modify_time = f_modify_time;
        this.f_modify_ip = f_modify_ip;
        this.f_modify_server = f_modify_server;
        this.f_modify_user = f_modify_user;
        this.f_operation_type = f_operation_type;
    }


    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getF_user_id() {
        return f_user_id;
    }

    public void setF_user_id(long f_user_id) {
        this.f_user_id = f_user_id;
    }

    public String getF_old_information() {
        return f_old_information;
    }

    public void setF_old_information(String f_old_information) {
        this.f_old_information = f_old_information;
    }

    public String getF_new_information() {
        return f_new_information;
    }

    public void setF_new_information(String f_new_information) {
        this.f_new_information = f_new_information;
    }

    public int getF_information_type() {
        return f_information_type;
    }

    public void setF_information_type(int f_information_type) {
        this.f_information_type = f_information_type;
    }

    public long getF_modify_time() {
        return f_modify_time;
    }

    public void setF_modify_time(long f_modify_time) {
        this.f_modify_time = f_modify_time;
    }

    public String getF_modify_ip() {
        return f_modify_ip;
    }

    public void setF_modify_ip(String f_modify_ip) {
        this.f_modify_ip = f_modify_ip;
    }

    public int getF_modify_server() {
        return f_modify_server;
    }

    public void setF_modify_server(int f_modify_server) {
        this.f_modify_server = f_modify_server;
    }

    public String getF_modify_user() {
        return f_modify_user;
    }

    public void setF_modify_user(String f_modify_user) {
        this.f_modify_user = f_modify_user;
    }

    public int getF_operation_type() {
        return f_operation_type;
    }

    public void setF_operation_type(int f_operation_type) {
        this.f_operation_type = f_operation_type;
    }
}
