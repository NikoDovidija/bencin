package com.gombiart.bencin;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

public class GasStation {

    private String company;
    private String name;
    private String address;
    private float lat, lng;
    private String phoneNum;
    private String status;

    private URL url;

    private URL pricesImg;
    private Bitmap b;

    public GasStation(URL url){
        this.url = url;
    }

    public GasStation(URL url, float lat, float lng){
        this.url = url;
        this.lat = lat;
        this.lng = lng;
    }

    public GasStation(URL url, float lat, float lng, String address, String phoneNum){
        this.url = url;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.phoneNum = phoneNum;
    }

    public GasStation(URL url, String name, float lat, float lng, String address, String phoneNum, String status){
        this.url = url;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.phoneNum = phoneNum;
        this.status = status;
    }


    public void fetchPrices(TextView v, MapsActivity c){
        AsyncTask<String, String, String> fetcher = new GetPrices(url, v, c, this).execute();
    }


    public URL getUrl(){return url;}

    public LatLng getCoordinates(){ return new LatLng(lat, lng); }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getStatus(){ return status; }

    public URL getPricesImg() {
        return pricesImg;
    }

    public Bitmap getPrices(){
        return b;
    }

    public void setPricesImg(String s) throws MalformedURLException {
        pricesImg = new URL(s);
    }

    public void setPrices(Bitmap b){
        this.b = b;
    }
}
