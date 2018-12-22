package com.ald.ebei.auth;

/*
 * Created by liangchen on 2018/4/10.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class EbeiIDCardInfoModel implements Parcelable {

    CardInfo cardInfo;
    List<Img> list;

    public EbeiIDCardInfoModel() {

    }

    protected EbeiIDCardInfoModel(Parcel in) {
        cardInfo = in.readParcelable(CardInfo.class.getClassLoader());
        list = in.readArrayList(Img.class.getClassLoader());
    }

    public static final Creator<EbeiIDCardInfoModel> CREATOR = new Creator<EbeiIDCardInfoModel>() {
        @Override
        public EbeiIDCardInfoModel createFromParcel(Parcel in) {
            return new EbeiIDCardInfoModel(in);
        }

        @Override
        public EbeiIDCardInfoModel[] newArray(int size) {
            return new EbeiIDCardInfoModel[size];
        }
    };

    public List<Img> getList() {
        return list;
    }

    public void setList(List<Img> list) {
        this.list = list;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(cardInfo, flags);
        dest.writeList(list);
    }

    public static class CardInfo implements Parcelable {

        private String address;
        private String birthday;
        private String citizen_id;
        private String gender;
        private String name;
        private String nation;
        private String valid_date_begin;
        private String valid_date_end;

        public CardInfo() {
        }

        protected CardInfo(Parcel in) {
            address = in.readString();
            birthday = in.readString();
            citizen_id = in.readString();
            gender = in.readString();
            name = in.readString();
            nation = in.readString();
            valid_date_begin = in.readString();
            valid_date_end = in.readString();
        }

        public static final Creator<CardInfo> CREATOR = new Creator<CardInfo>() {
            @Override
            public CardInfo createFromParcel(Parcel in) {
                return new CardInfo(in);
            }

            @Override
            public CardInfo[] newArray(int size) {
                return new CardInfo[size];
            }
        };

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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getCitizen_id() {
            return citizen_id;
        }

        public void setCitizen_id(String citizen_id) {
            this.citizen_id = citizen_id;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNation() {
            return nation;
        }

        public void setNation(String nation) {
            this.nation = nation;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(address);
            dest.writeString(birthday);
            dest.writeString(citizen_id);
            dest.writeString(gender);
            dest.writeString(name);
            dest.writeString(nation);
            dest.writeString(valid_date_begin);
            dest.writeString(valid_date_end);
        }
    }

    public static class Img implements Parcelable {
        String srcFileName;
        String url;

        public Img() {

        }

        protected Img(Parcel in) {
            srcFileName = in.readString();
            url = in.readString();
        }

        public static final Creator<Img> CREATOR = new Creator<Img>() {
            @Override
            public Img createFromParcel(Parcel in) {
                return new Img(in);
            }

            @Override
            public Img[] newArray(int size) {
                return new Img[size];
            }
        };

        public String getSrcFileName() {
            return srcFileName;
        }

        public void setSrcFileName(String srcFileName) {
            this.srcFileName = srcFileName;
        }

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
            dest.writeString(srcFileName);
            dest.writeString(url);
        }
    }

}
