package com.bkstek.stour.model;

/**
 * Created by duy tho le on 10/2/2017.
 */

public class POI {
    int Id;
    String Name;
    String Image;
    double Latitude;
    double Longitude;
    String VideoDir;
    String Audio;

    public String getVideoDir() {
        return VideoDir;
    }

    public void setVideoDir(String videoDir) {
        VideoDir = videoDir;
    }

    public String getAudio() {
        return Audio;
    }

    public void setAudio(String audio) {
        Audio = audio;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double lattitude) {
        Latitude = lattitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    String Address;

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
