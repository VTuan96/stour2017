package com.bkstek.stour.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LBS implements Parcelable {
    String name;
    String address;
    double lat;
    double lon;
    String tel;
    String open_time;

    public LBS(String name, String address, double lat, double lon, String tel, String open_time) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.tel = tel;
        this.open_time = open_time;
    }

    protected LBS(Parcel in) {
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        tel = in.readString();
        open_time = in.readString();
    }

    public static final Creator<LBS> CREATOR = new Creator<LBS>() {
        @Override
        public LBS createFromParcel(Parcel in) {
            return new LBS(in);
        }

        @Override
        public LBS[] newArray(int size) {
            return new LBS[size];
        }
    };

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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(tel);
        dest.writeString(open_time);
    }
}
