package com.ald.ebei.auth.model;

import android.os.Parcel;
import android.os.Parcelable;


/*
 * Created by liangchen on 2018/3/3.
 */

public class EbeiExtraGxbModel implements Parcelable {
    private String url;

    protected EbeiExtraGxbModel(Parcel in) {
        url = in.readString();
    }

    public EbeiExtraGxbModel() {
        
    }

    public static final Creator<EbeiExtraGxbModel> CREATOR = new Creator<EbeiExtraGxbModel>() {
        @Override
        public EbeiExtraGxbModel createFromParcel(Parcel in) {
            return new EbeiExtraGxbModel(in);
        }

        @Override
        public EbeiExtraGxbModel[] newArray(int size) {
            return new EbeiExtraGxbModel[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }
}
