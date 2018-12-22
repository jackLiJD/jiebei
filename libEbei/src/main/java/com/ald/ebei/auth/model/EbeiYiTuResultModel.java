package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/22 15:22
 * 描述：
 * 修订历史：
 */
public class EbeiYiTuResultModel extends EbeiBaseModel {
    private String name;
    private String address;
    private String citizen_id;
    private String valid_date_begin;
    private String valid_date_end;
    private String gender;
    private String nation;
    private String birthday;
    private String agency;
    private int idcard_type;

    public EbeiYiTuResultModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCitizen_id() {
        return citizen_id;
    }

    public void setCitizen_id(String citizen_id) {
        this.citizen_id = citizen_id;
    }

    public String getValid_date_begin() {
        return valid_date_begin;
    }

    public void setValid_date_begin(String valid_date_begin) {
        this.valid_date_begin = valid_date_begin;
    }

    public String getValid_date_end() {
        return valid_date_end;
    }

    public void setValid_date_end(String valid_date_end) {
        this.valid_date_end = valid_date_end;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public int getIdcard_type() {
        return idcard_type;
    }

    public void setIdcard_type(int idcard_type) {
        this.idcard_type = idcard_type;
    }
}
