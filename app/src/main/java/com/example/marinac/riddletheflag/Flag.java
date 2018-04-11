package com.example.marinac.riddletheflag;

import com.google.android.gms.maps.model.LatLng;

public class Flag {
    public String name;
    public double latitude;
    public double longtitude;
    public int difficulty;
    public String hint;
    public int points;
    public String riddle;
    public String description;
    public String picture;

    public Flag(String name, LatLng location) {
        this.name = name;
        this.latitude = location.latitude;
        this.longtitude = location.longitude;
    }

    public Flag (){}
}
