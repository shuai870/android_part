package com.example.android_recyclerview;

public class Text {
    private String name;
    private int imageID;
    public Text(String name , int imageID){
        this.name = name;
        this.imageID = imageID;
    }
    public String getName(){
        return name;
    }
    public int getImageID(){
        return imageID;
    }
}
