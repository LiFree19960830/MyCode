package com.huasu.bess4c48.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectionData implements Parcelable {
    public String value;  //主控/从控地址编码
    public String text;
    public String parentValue;//总控/主控地址编码
    public String deviceUuid;//设备唯一识别码
    public int drawableRes = -1;
    public int checkTag = 0;//选中的值

    public MyDevice myDevice;

    public SelectionData() {
    }

    public SelectionData(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public SelectionData(String value, String text, MyDevice myDevice) {
        this.value = value;
        this.text = text;
        this.myDevice = myDevice;
    }

    public SelectionData(String value, String text, String deviceUuid) {
        this.value = value;
        this.text = text;
        this.deviceUuid = deviceUuid;
    }

    public SelectionData(int value, int text) {
        this.value = String.valueOf(value);
        this.text = String.valueOf(text);
    }

    public SelectionData(String value, String text, int drawableRes) {
        this.value = value;
        this.text = text;
        this.drawableRes = drawableRes;
    }


    public SelectionData(int value, String text, int drawableRes) {
        this.value = String.valueOf(value);
        this.text = String.valueOf(text);
        this.drawableRes = drawableRes;
    }

    protected SelectionData(Parcel in) {
        value = in.readString();
        text = in.readString();
    }



    public static final Creator<SelectionData> CREATOR = new Creator<SelectionData>() {
        @Override
        public SelectionData createFromParcel(Parcel in) {
            return new SelectionData(in);
        }

        @Override
        public SelectionData[] newArray(int size) {
            return new SelectionData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
