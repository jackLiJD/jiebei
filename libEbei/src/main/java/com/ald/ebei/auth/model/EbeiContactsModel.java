package com.ald.ebei.auth.model;

import android.graphics.Bitmap;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/3/1 15:56
 * 描述：
 * 修订历史：
 */
public class EbeiContactsModel {

    /**
     * 名字
     */
    private String name;

    /**
     * 电话
     */
    private String phoneNumber;

    /**
     * 头像
     */
    private Bitmap photo;

    // -------------------------------------------

    public EbeiContactsModel() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "," + getName() + ":" + getPhoneNumber();
    }
}
