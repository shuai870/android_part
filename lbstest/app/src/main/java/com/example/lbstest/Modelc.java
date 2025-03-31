package com.example.lbstest;

import com.baidu.mapapi.model.LatLng;

public class Modelc {
    private double latitude;
    private double longitude;
    private int radius;
    // 定义圆形围栏的中心和半径
    private LatLng center= new LatLng(latitude, longitude);
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public LatLng getCenter() {
        return center;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }
}
