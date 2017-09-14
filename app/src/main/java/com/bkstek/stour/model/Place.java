package com.bkstek.stour.model;

/**
 * Created by acebk on 8/2/2017.
 */

public class Place {
    int Id;
    String Name;
    String LongDes;
    double Longitude;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    String Address;

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    double Latitude;

    public String getLongDes() {
        return LongDes;
    }

    public void setLongDes(String longDes) {
        LongDes = longDes;
    }


    public int getStar() {
        return Star;
    }

    public void setStar(int star) {
        Star = star;
    }

    public int getViewCount() {
        return ViewCount;
    }

    public void setViewCount(int viewCount) {
        ViewCount = viewCount;
    }


    int Star;
    int ViewCount;

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    String Avatar;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
