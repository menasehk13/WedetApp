package com.example.wedetapp;

public class HotelDetail {
    String imageurl;
    String hotelname;
    HotelDetail(){

    }
    public HotelDetail(String hotelname,String imageurl){
        this.hotelname=hotelname;
        this.imageurl=imageurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getHotelname() {
        return hotelname;
    }
}
