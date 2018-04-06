package com.example.marinac.riddletheflag;

import com.google.android.gms.maps.model.LatLng;

public class Flag {
    public String name;
    public LatLng location;
    public int difficulty;
    public String hint;
    public int points;
    public String riddle;
    public String description;
    public String picture;

    public Flag(String name, LatLng location) {
        this.name = name;
        this.location = location;
    }

    public Flag (){}
}
