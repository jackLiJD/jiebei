package com.ald.ebei.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Created by wjy on 2018/1/24.
 */

public class EbeiExtraFundModel implements Parcelable {

    private String url;

    public EbeiExtraFundModel() {
    }

    private EbeiExtraFundModel(Parcel in) {
        url = in.readString();
    }

    public static final Creator<EbeiExtraFundModel> CREATOR = new Creator<EbeiExtraFundModel>() {
        @Override
        public EbeiExtraFundModel createFromParcel(Parcel in) {
            return new EbeiExtraFundModel(in);
        }

        @Override
        public EbeiExtraFundModel[] newArray(int size) {
            return new EbeiExtraFundModel[size];
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
