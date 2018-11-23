package com.bkstek.stour.model;

public class LBS{
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
}
